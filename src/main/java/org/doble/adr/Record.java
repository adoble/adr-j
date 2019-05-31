package org.doble.adr;

import org.doble.adr.template.TemplateEngine;

import java.io.*;
import java.nio.file.*;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class Record {
	private final Path docsPath; // Where the adr files are stored
	private final int id;
	private final String idFormatted;
	private final String name;
	private final Date date;
	;
	private final String status;
	private final String context;
	private final String decision;
	private final String consequences;
	private final TemplateEngine templateEngine;

	private ArrayList<Integer> supersedes = new ArrayList<Integer>();

	private class Link {

		Link(Integer id, String comment, String reverseComment) {
			this.id = id;
			this.comment = comment;
			this.reverseComment = reverseComment;
		}

		Integer id;
		String comment = "";
		String reverseComment = "";
	}

	private ArrayList<Link> links = new ArrayList<Link>();


	/**
	 * Constructor for an ADR record. It has private scope so that only
	 * the builder can be used to construct it.
	 */
	private Record(Record.Builder builder) {
		this.docsPath = builder.docsPath;
		this.id = builder.id;
		this.idFormatted = builder.idFormatted;
		this.name = builder.name;
		this.date = builder.date;
		this.status = builder.status;
		this.context = builder.context;
		this.decision = builder.decision;
		this.consequences = builder.consequences;
		this.templateEngine = builder.templateEngine;
	}

	/**
	 * Generate a string with the specified ADR sections (in this case from Michael Nygard.)
	 *
	 * @return String The generated string.
	 */
	private String generate() {
		String s;

		// Integer idInt = new Integer(id);

		//s = template.replace("@ID",  idInt.toString());
		s = templateEngine.getRecordTemplate().replace("@ID", idFormatted);
		s = s.replace("@Name", capitalizeFirstCharacter(this.name));  // First character of title is always upper case
		s = s.replace("@Date", DateFormat.getDateInstance().format(date));
		s = s.replace("@Context", context);
		s = s.replace("@Decision", decision);
		s = s.replace("@Consequences", consequences);

		String statusMsg = status + "\n\n";

		for (Integer adrID : supersedes) {
			statusMsg += "\nSupersedes the [architecture decision record " + adrID + "](" + getADRFileName(adrID) + ")\n";
		}

		for (Link link : links) {
			//statusMsg += capitalizeFirstCharacter(link.comment) + " ADR " + link.id.toString() +  "\n";
			statusMsg += capitalizeFirstCharacter(link.comment) + " [ADR " + link.id.toString() + "](" + getADRFileName(link.id) + ")\n";
		}

		s = s.replace("@Status", statusMsg);

		return s;
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
	 * Store this ADR
	 */
	public Path store() throws ADRException {

		// Create a file name for the ADR
		String fileName = this.name.toLowerCase();
		fileName = fileName.replace(' ', '-');    // Replace blanks with hyphens
		//String idFormatted = String.format("%04d", this.id);
		fileName = idFormatted + '-' + fileName + templateEngine.getFileExtension();  // Compose full file name
		//Path p = env.fileSystem.getPath(docsPath.toString(), fileName);
		Path p = docsPath.resolve(fileName); // Full path of the ADR file in the document path

		// Now write the ADR

		try (PrintWriter adrWriter = new PrintWriter(Files.newBufferedWriter(p))) {

			adrWriter.print(this.generate());
			adrWriter.close();

			// If there are (reverse) links to other ADR files then add them. 
			for (Link link : links) {
				addReverseLink(docsPath, link);
			}

			// If the  ADR supersedes another, then add the link to the record that supersedes it
			for (Integer adrID : supersedes) {
				this.supersede(docsPath, adrID, this.id);
			}
		} catch (Exception e) {
			throw new ADRException("FATAL: Unable to store ADR" + this.id, e);
		}

		return p;
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
	 * Adds a reverse link reference to an ADR at the end of the Status section with the form
	 * "Linked to from ADR {id}"
	 *
	 * @param docsPath The path where the ADRs are..
	 * @param link     The ADR record file where the reverse link reference is to be added.
	 */
	private void addReverseLink(Path docsPath, Link link) throws ADRException {

		try (Stream<Path> stream = Files.list(docsPath)) {

			Path[] paths = stream.filter(ADRFilter.filter(link.id)).toArray(Path[]::new);
			if (paths.length == 1) {
				// Read in the file
				List<String> lines = Files.readAllLines(paths[0]);

				// Find the Status section (before the context station) and add the reverse link comment
				String line;
				for (int index = 0; index < lines.size(); index++) {
					line = lines.get(index);
					if (line.startsWith("## Context")) {  // TODO Need to have use constants for the titles
						lines.add(index, link.reverseComment + " [ADR " + id + "](" + getADRFileName(this.id) + ")\n");
						lines.add(index + 1, "");
						break;
					}
				}

				// Write out the file
				Files.write(paths[0], lines);
			} else {
				throw new ADRException("FATAL: More than one matching ADR file found for the reverse link.");
			}
		} catch (IOException e) {
			throw new ADRException("FATAL: Unable to add reverse link", e);
		}
	}

	/**
	 * Adds a link the the record
	 *
	 * @param id             the id of the ADR being linked to.
	 * @param comment        The link comment in this ADR
	 * @param reverseComment The comment added to the ADR with the specified id.
	 */
	public void addLink(Integer id, String comment, String reverseComment) {
		links.add(new Link(id, comment, reverseComment));
	}

	/**
	 * Based on a link specification of the form "LinkID:LinkComment:ReverseLinkComment" create a link
	 * and add it to the record.
	 * LinkId             - The id of the ADR being linked to.
	 * LinkComment        - The link comment in this ADR
	 * ReverseLinkComment - The comment added to the ADR with the specified id.
	 *
	 * @param linkSpec The link specification as string
	 * @throws LinkSpecificationException Thrown if the link specification is incorrect
	 */
	public void addLink(String linkSpec) throws LinkSpecificationException {
		try {
			if (linkSpec.length() > 0) {
				String[] linkSpecs = linkSpec.split(":");
				if (linkSpecs.length == 3) {
					links.add(new Link(new Integer(linkSpecs[0]), linkSpecs[1], linkSpecs[2]));
				} else {
					//throw new LinkSpecificationException();
					throw new LinkSpecificationException();
				}
			}
		} catch (NumberFormatException e) {
			throw new LinkSpecificationException();
		}
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

	private String lowercaseFirstCharacter(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}

	public static class Builder {
		private Path docsPath;
		private TemplateEngine templateEngine;
		private int id;
		private String idFormatted;
		private String name;
		private Date date = new Date();
		private String status = "Accepted";
		private String context = "Record the architectural decisions made on this project.";
		private String decision = "We will use Architecture Decision Records, as described by Michael Nygard in this article: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions";
		private String consequences = "See Michael Nygard's article, linked above.";

		public Builder(Path docsPath, TemplateEngine templateEngine) {
			this.docsPath = docsPath;
			this.templateEngine = templateEngine;
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

		public Builder context(String context) {
			this.context = context;
			return this;
		}

		public Builder decision(String decision) {
			this.decision = decision;
			return this;
		}

		public Builder consequences(String consequences) {
			this.consequences = consequences;
			return this;
		}

		public Record build() {
			return new Record(this);
		}
	}
}
