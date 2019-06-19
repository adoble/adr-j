package org.doble.adr;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Record {
	private final Path docsPath; // Where the adr files are stored
	private final Optional<Path> template;
	private final String templateExtension;
	private final Integer id;
	private final String idFormatted;
	private final String name;
	private final Date date;
	private final String status;
	//private final String context;
	//private final String decision;
	//private final String consequences;

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

//	// Mark down template
//	private String template = "# @ID. @Name\n\n" +
//			"Date: @Date\n\n" +
//			"## Status\n\n" +
//			"@Status\n\n" +
//			"## Context\n\n" +
//			"@Context\n\n" +
//			"## Decision\n\n" +
//			"@Decision\n\n" +
//			"## Consequences\n\n" +
//			"@Consequences\n";

	/**
	 * Constructor for an ADR record. It has private scope so that only
	 * the builder can be used to construct it.
	 */
	private Record(Record.Builder builder) throws URISyntaxException {
		final String defaultTemplateName = "default_template.md";
		this.docsPath = builder.docsPath;
		//this.template = builder.template;
		this.id = builder.id;
		this.idFormatted = builder.idFormatted;
		this.name = builder.name;
		this.date = builder.date;
		this.status = builder.status;
		
		if (builder.template.isPresent()) {
			this.template = builder.template;
			this.templateExtension = builder.templateExtension;
		} else {
			// Use the default Nygard template (in markdown format)
			this.template = Optional.of(Paths.get(getClass()
					.getClassLoader()
					.getResource(defaultTemplateName)  
					.toURI()));
			this.templateExtension = "md";
		}
			
		//this.context = builder.context;
		//this.decision = builder.decision;
		//this.consequences = builder.consequences;
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
		Path sourceFile = this.template.get();
		String templateLinkFragment = getFragment(sourceFile, "{{{link.id}}}");
		
		//Now generate link fragments (i.e. the markdown and the template field) for each of the links
		ArrayList<String> linkFragments = new ArrayList<String>();
		for (Link link: links) {
			String linkFragment = templateLinkFragment;
			linkFragments.add(linkFragment.replace("{{{link.comment}}}", 
					                               capitalizeFirstCharacter(link.comment))
					                      .replace("{{{link.id}}}", link.id.toString())
					                      .replace("{{{link.file}}}", getADRFileName(link.id))
					         );
		}
		String linkSectionString = linkFragments.stream().collect(Collectors.joining("\n"));
		
		// Create the superceded fragment using the line in the template file
		String templateSupercededFragment = getFragment(sourceFile, "{{{superceded.id}}}");
		// Now generate superceded string fragments 
		ArrayList<String> supercededFragments = new ArrayList<String>();
		for (Integer supercededId: supersedes) {
			String supercededFragment = templateSupercededFragment;
			supercededFragments.add(supercededFragment.replace("{{{superceded.id}}}", supercededId.toString())
												        .replace("{{{superceded.file}}}", getADRFileName(supercededId))
					                );
		}
		String supercededSectionString = supercededFragments.stream().collect(Collectors.joining("\n"));
		
		// Now substitute the fields in the template and write to the ADR
		List<String> targetContent = new ArrayList<String>();
		try (Stream<String> lines = Files.lines(sourceFile)) {
			targetContent = lines
					.map(line -> line.replaceAll("\\{\\{id\\}\\}", id.toString()))
					.map(line -> line.replaceAll("\\{\\{name\\}\\}", name))
					.map(line -> line.replaceAll("\\{\\{status\\}\\}", status))
					.map(line -> line.replaceAll("\\{\\{date\\}\\}", DateFormat.getDateInstance().format(date)))
					.filter(line -> !(line.contains("{{{link.id}}}") && linkFragments.size() == 0))        // Remove lines which will be blank
					.filter(line -> !(line.contains("{{{superceded.id}}}") && supercededFragments.size() == 0)) // Remove lines which will be blank
					.map(line -> line.contains("{{{link.id}}}")?linkSectionString: line)   
					.map(line -> line.contains("{{{superceded.id}}}")?supercededSectionString: line)
					.collect(Collectors.toList());   
			//targetContent.removeIf(item -> item.isEmpty());  // Remove double empty lines
			Files.write(targetFile, targetContent);  
		} catch (IOException e) {
			// TODO Auto-generated catch block
		   throw new ADRException("Cannot write ADR", e.getCause());
		}
		
		// If there are (reverse) links to other ADR files then add them. 
		// REMOVED. Using user defined templates means that there is no reliable way to 
		// insert a reverse link at predetermined location in the other ADR
		//
		//TODO: Change the documentation around the link specification 
		//
//		for (Link link : links) {
//			addReverseLink(docsPath, link);
//		}

		// If the  ADR supersedes another, then add the link to the record that supersedes it
		// REMOVED. Using user defined templates means that there is no reliable way to 
		// insert a superceded message at predetermined location in the other ADR
		//
		//TODO: Change the documentation around the superceded flag 
		//
//		for (Integer adrID : supersedes) {
//			this.supersede(docsPath, adrID, this.id);
//		}
		
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
	
	/*
	 * Finds and returns the line in the template that contains the specified substitution field.
	 * Assumes that all the other substitution field for the link are on the same line.
	 * TODO make sure that the above is in the documentation
	 */
	private static String getFragment(Path sourceFile, String substitutionField) {
		String templateFragment = "";
		try (Stream<String> templateLines = Files.lines(sourceFile)) {
			templateFragment = templateLines.filter(line-> line.contains(substitutionField)).findAny().orElse(null);
			templateLines.close();

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return templateFragment;
	}
	
	

//	private String lowercaseFirstCharacter(String s) {
//		return s.substring(0, 1).toLowerCase() + s.substring(1);
//	}

	public static class Builder {
		private Path docsPath;
		private Optional<Path> template = Optional.empty();
		private String templateExtension = "md";  //Default to markdown
		private int id;
		private String idFormatted;
		private String name;
		private Date date = new Date();
		private String status = "Proposed";
//TODO remove commented out code
//		private String context = "Record the architectural decisions made on this project.";
//		private String decision = "We will use Architecture Decision Records, as described by Michael Nygard in this article: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions";
//		private String consequences = "See Michael Nygard's article, linked above.";

		public Builder(Path docsPath) {
			this.docsPath = docsPath;
		}
      

		public Builder template(Path template) {
			this.template = Optional.ofNullable(template);
			if (this.template.isPresent()) {
				// Get the file extension
				String fileName = this.template.get().toString();
				this.templateExtension = fileName.substring(fileName.lastIndexOf('.') + 1);
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

		public Builder date(Date date) {
			this.date = date;
			return this;
		}

		public Builder status(String status) {
			this.status = status;
			return this;
		}

		/*
		 * public Builder context(String context) { this.context = context; return this;
		 * }
		 * 
		 * public Builder decision(String decision) { this.decision = decision; return
		 * this; }
		 * 
		 * public Builder consequences(String consequences) { this.consequences =
		 * consequences; return this; }
		 */

		public Record build() throws URISyntaxException {
			return new Record(this);
		}
	}
}
