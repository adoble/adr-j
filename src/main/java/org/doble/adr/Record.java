package org.doble.adr;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Record {
	private final Path docsPath;              // Where the adr files are stored
	private final Optional<String> template;  //Using String type instead of Path as the default template is a resource. 
	                                          //Resources are not correctly supported by Path. 
	private final String templateExtension;;
	private final Integer id;
	private final String idFormatted;
	private final String name;
	private final LocalDate date;
    private final String author;
	private final String status;
	private final DateTimeFormatter dateFormatter;

	private ArrayList<Integer> supersedes = new ArrayList<Integer>();

	private class Link {

		Link(Integer id, String comment) {
			this.id = id;
			this.comment = comment;
		}

		Integer id;
		String comment = "";
	}
	
	private ArrayList<Link> links = new ArrayList<Link>();


	/**
	 * Constructor for an ADR record. It has private scope so that only
	 * the builder can be used to construct it.
	 */
	private Record(Record.Builder builder) throws URISyntaxException {
		//final String defaultTemplateName = ADRProperties.defaultTemplateName;
		this.docsPath = builder.docsPath;
		//this.template = builder.template;
		this.id = builder.id;
		this.idFormatted = builder.idFormatted;
		this.name = builder.name;
		this.date = builder.date;
		this.author = builder.author;
		this.status = builder.status;
		this.dateFormatter = builder.dateFormatter;
		
		if (builder.template.isPresent()) {
			this.template = builder.template;
			this.templateExtension = builder.templateExtension;
		} else {
			this.template = Optional.empty();
			this.templateExtension = "md";
		}
	
	}

	/**
	 * Generate and store an ADR using the data stored in this record. 
	 * Generate a file with a name of the form: 
	 *    (adr id)-(adr name, lower case separated with hyphens).(the extension of the template file used)   //TODO extension
	 *
	 * @return Path The generated ADR file.
	 */
	public Path store() throws ADRException {
	
		// Create a file name for the ADR
		String targetFileName = this.name.toLowerCase();
		targetFileName = targetFileName.replace(' ', '-');    // Replace blanks with hyphens
		//String idFormatted = String.format("%04d", this.id);
		targetFileName = idFormatted + '-' + targetFileName + "." + templateExtension;  // Compose full file name
		//Path p = env.fileSystem.getPath(docsPath.toString(), fileName);
		Path targetFile = docsPath.resolve(targetFileName); // Full path of the ADR file in the document path
		
		
		// Create the link fragment using the line in the template file
		Optional<String> templateLinkFragment = getFragment("{{{link.id}}}");

		//Now generate link fragments (i.e. the markdown and the template field) for each of the links
		ArrayList<String> linkFragments = new ArrayList<String>();
		String linkSectionString;
		if (templateLinkFragment.isPresent()) {
			for (Link link: links) {
				String linkFragment = templateLinkFragment.get();
				linkFragments.add(linkFragment.replace("{{{link.comment}}}", 
						capitalizeFirstCharacter(link.comment))
						.replace("{{{link.id}}}", link.id.toString())
						.replace("{{{link.file}}}", getADRFileName(link.id))
						);
			}
			linkSectionString = linkFragments.stream().collect(Collectors.joining("\n"));
		} else {
			linkSectionString = "";
		}

		
		// Create the superseded fragment using the line in the template file
		Optional<String> templateSupersededFragment;
		String supersededSectionString;
		ArrayList<String> supersededFragments = new ArrayList<String>();

		templateSupersededFragment = getFragment("{{{superseded.id}}}");
       
		// Now generate superseded string fragments 
		if (templateSupersededFragment.isPresent()) {
			for (Integer supersededId: supersedes) {
				String supersededFragment = templateSupersededFragment.get();
				supersededFragments.add(supersededFragment.replace("{{{superseded.id}}}", supersededId.toString())
						.replace("{{{superseded.file}}}", getADRFileName(supersededId))
						);
			}
			supersededSectionString = supersededFragments.stream().collect(Collectors.joining("\n"));
		} else {
			supersededSectionString = "";
		}
		
		// Now substitute the fields in the template and write to the ADR
		TemplateProvider templateProvider = new TemplateProvider(docsPath.getFileSystem(), ADRProperties.defaultTemplateName);
		List<String> targetContent = new ArrayList<String>();
		
		try (Stream<String> lines = Files.lines(templateProvider.getPath(this.template))) {
			targetContent = lines
					.map(line -> line.replaceAll("\\{\\{id\\}\\}", id.toString()))
					.map(line -> line.replaceAll("\\{\\{name\\}\\}", name))
					.map(line -> line.replaceAll("\\{\\{status\\}\\}", status))
					.map(line -> line.replaceAll("\\{\\{author\\}\\}", author))
					.map(line -> line.replaceAll("\\{\\{date\\}\\}", dateFormatter.format(date)))
					.filter(line -> !(line.contains("{{{link.id}}}") && linkFragments.size() == 0))        // Remove lines which will be blank
					.filter(line -> !(line.contains("{{{superseded.id}}}") && supersededFragments.size() == 0)) // Remove lines which will be blank
					.map(line -> line.contains("{{{link.id}}}")?linkSectionString: line)   
					.map(line -> line.contains("{{{superseded.id}}}")?supersededSectionString: line)
					.collect(Collectors.toList());   
			//targetContent.removeIf(item -> item.isEmpty());  // Remove double empty lines
			Files.write(targetFile, targetContent);  
		} catch (Exception e) {
			// TODO Auto-generated catch block
		   throw new ADRException("Cannot write ADR", e.getCause());
		}

        return targetFile;
	}

	private String getADRFileName(int adrId) {
		String fileName;

		try {
			Path[] paths = Files.list(docsPath).filter(ADRFilter.filter(adrId)).toArray(Path[]::new);

			if (paths.length == 1) {
				fileName = paths[0].getFileName().toString();
			} else {
				// Gracefully fail and return an empty string
				fileName = "";
			}
		} catch (IOException e) {
			// Gracefully fail and return an empty string
			fileName = "";
		}

		return fileName;
	}

	

	/**
	 * Writes the ADR (status section) with id supersededID that it has been
	 * superseded by the ADR with the id supersedesID.
	 *
	 * The message written in the superseded ADR has the form
	 * Superseded by the architecture decision record [supersedesID]
	 *
	 * @param supersededID The id of the superseded ADR.
	 * @param supersedesID The id of the ADR that supersedes it.
	 * TODO REMOVE as this is now not used. 
	 */
	private void supersede(Path docsPath, int supersededID, int supersedesID) throws ADRException {
		Path supersededADRFile;
		Path[] paths;

		// Get the ADR file that is to be superseded
		try (Stream<Path> stream = Files.list(docsPath)) {
			paths = stream.filter(ADRFilter.filter((int) supersededID)).toArray(Path[]::new);

			if (paths.length == 1) {
				supersededADRFile = paths[0];

				// Read in the file
				List<String> lines = Files.readAllLines(supersededADRFile);

				// Find the Status section (before the context station) and add the reverse link comment
				String line;
				for (int index = 0; index < lines.size(); index++) {
					line = lines.get(index);
					if (line.startsWith("## Context")) {  // TODO Need to have use constants for the titles
						lines.add(index, "Superseded by the [architecture decision record " + supersedesID + "](" + getADRFileName(supersedesID) + ")");
						lines.add(index + 1, "");
						break;
					}
				}

				// Write out the file
				Files.write(supersededADRFile, lines);  //TODO use a temporary file when making such changes
			} else {
				throw new ADRException("FATAL: No matching ADR file found or more than one matching ADR file found with the id " + supersededID);
			}
		} catch (Exception e) {
			throw new ADRException("FATAL: Problem with the superseding of ADR: " + supersededID, e);
		}
	}


	/**
	 * Adds a link the the record
	 *
	 * @param id             the id of the ADR being linked to.
	 * @param comment        The link comment in this ADR
	 **/
	public void addLink(Integer id, String comment) {
		links.add(new Link(id, comment));
	}

	/**
	 * Based on a link specification of the form "LinkID:LinkComment:ReverseLinkComment" create a link
	 * and add it to the record.
	 * LinkId             - The id of the ADR being linked to.
	 * LinkComment        - The link comment in this ADR
	 * ReverseLinkComment - The comment added to the ADR with the specified id.
	 *
	 * @param linkSpec The link specification as string
	 * @return The id of the ADR linked to
	 * @throws LinkSpecificationException Thrown if the link specification is incorrect
	 */
	public int addLink(String linkSpec) throws LinkSpecificationException {
		String linkComment;
		int linkID = -1;
		try {
			if (linkSpec.length() > 0) {
				String[] linkSpecs = linkSpec.split(":");
				if (linkSpecs.length == 2) {
					linkID = new Integer(linkSpecs[0]);
					linkComment = linkSpecs[1];
					links.add(new Link(linkID, linkComment));
				} else {
					throw new LinkSpecificationException();
				}
			}
		} catch (NumberFormatException e) {
			throw new LinkSpecificationException();
		}

		return linkID;
	}

	/**
	 * Adds a supersedes ADR identifier, i.e. specifies what ADRs are superseded by this ADR.
	 *
	 * @param adrId The id of the ADR superseded by this ADR.
	 */
	public void addSupersedes(int adrId) {
		supersedes.add(new Integer(adrId));
	}

	private String capitalizeFirstCharacter(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	/**
	 * Finds and returns the line in the template that contains the specified substitution field.
	 * Assumes that all the other substitution field for the link are on the same line.
	 * TODO make sure that the above is in the documentation
	 * @param substitutionField The substitutionField being looked for
	 * @returns Optional<String) The fragment found. Optional.empty if no fragment found 
	 * (e.g. if the substitution field has not been specified in the template).
	 */
	private Optional<String> getFragment(String substitutionField) throws ADRException {
		String templateFragment; 
		
		TemplateProvider templateProvider = new TemplateProvider(docsPath.getFileSystem(), ADRProperties.defaultTemplateName);
		
		//BufferedReader reader = getTemplateReader();
		try {
			Path templatePath = templateProvider.getPath(this.template);
			Stream<String> templateLines = Files.lines(templatePath);
			templateFragment = templateLines.filter(line-> line.contains(substitutionField)).findAny().orElse(null);
			templateLines.close();
		} 
		catch (Exception e) {
			String msg = "Cannot get the template containing " + substitutionField;
			throw new ADRException(msg, e);
		}
		
		return Optional.ofNullable(templateFragment);
	}
	
	private Optional<String> getTemplate() {
		return template;
	}



	public static class Builder {
		private Path docsPath;
		private Optional<String> template = Optional.empty();
		private String templateExtension = "md";  //Default to markdown
		private int id;
		private String idFormatted;
		private String name;

		private String author = System.getProperty("user.name");
		private LocalDate date = LocalDate.now();
		private String status = "Proposed";
		private DateTimeFormatter dateFormatter;

		public Builder(Path docsPath, DateTimeFormatter dateFormatter) {
			this.docsPath = docsPath;
			this.dateFormatter = dateFormatter;
		}
      

		/** 
		 * Sets up the path name of the template. Templates can be 
		 * a) normal files.  In which case the string is a normal path specification
		 * b) resources. In which case the string is preceded with "rsrc:"
		 * @param template Path name of the template
		 * @return Builder
		 */
		public Builder template(String template) {
			this.template = Optional.ofNullable(template);
			if (this.template.isPresent()) {
				// Get the file extension
				String fileName = this.template.get().toString();
				this.templateExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
			} else {
				this.templateExtension = "md";
			}
			return this;
		}

		public Builder id(int id) {
			this.id = id;
			idFormatted = String.format("%04d", id);
			return this;
		}

		public Builder name(String name) {
			this.name = name.trim();
			return this;
		}

		public Builder date(LocalDate date) {
			this.date = date;
			return this;
		}

		public Builder author(String author) {
			this.author = author;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		public Record build() throws URISyntaxException {
			return new Record(this);
		}
	}
}
