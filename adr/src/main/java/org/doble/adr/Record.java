package org.doble.adr;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Record  {
	public int id;
	public String name = "";
	public String date = "";
	public String status = "Accepted";
	public String context = "Record the architectural decisions made on this project.";
	public String decision = "We will use Architecture Decision Records, as described by Michael Nygard in this article: http://thinkrelevance.com/blog/2011/11/15/documenting-architecture-decisions";
	public String consequences = "See Michael Nygard's article, linked above.";
	private ArrayList<Integer> supercedes  = new ArrayList<Integer>();
	
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
	
	private ArrayList<Link> links  = new ArrayList<Link>();

	
	
	// Mark down template 
	String template = "# @ID. @Name\n\n" + 
			           "Date: @Date\n\n" +
			      	  "## Status\n\n" +
			      	  "@Status\n\n" + 
			      	  "## Context\n\n" + 
			      	  "@Context\n\n" + 
			      	  "## Decision\n\n" +
			      	  "@Decision\n\n" + 
			      	  "## Consequences\n\n" + 
			      	  "@Consequences\n";
	
	
	
	public Record() {
		
	}
	
	public String generate() {
		 String s;
		 
		 Integer idInt = new Integer(id);
		 
		 s = template.replace("@ID",  idInt.toString());
		 s = s.replace("@Name",  capitalizeFirstCharacter(this.name));  // First character of title is always upper case
		 s = s.replace("@Date",  date);
		 s = s.replace("@Context",  context);
		 s = s.replace("@Decision",  decision);
		 s = s.replace("@Consequences",  consequences);

		String statusMsg = status + "\n\n";

		for (Integer sc : supercedes) {
			statusMsg += "\nSupersedes the architecture decision record " + sc + "\n";
			
		}
		
		for (Link link: links) {
			statusMsg += capitalizeFirstCharacter(link.comment) + " ADR " + link.id.toString() +  "\n";
		}

		s = s.replace("@Status", statusMsg);
		
		return s;
		 
	}
	
	/** Store this ADR
	 * 
	 * @param docsPath The directory where the ADRs are.
	 */
	public Path store(Path docsPath) throws FileNotFoundException, UnsupportedEncodingException, ADRNotFoundException {
		// Create a file name for the ADR
		String fileName = lowercaseFirstCharacter(this.name);
		fileName = fileName.replace(' ', '-');    // Replace blanks with hyphens
		String idFormatted = String.format("%04d", this.id);
		fileName = idFormatted + '-' + fileName + ".md";  // Compose full file name
		Path p = FileSystems.getDefault().getPath(docsPath.toString(), fileName);
				
		// Now write the ADR
		PrintWriter adrWriter = new PrintWriter(p.toFile() , "UTF-8");
		adrWriter.print(this.generate());		
		adrWriter.close();
		
		// If there are (reverse) links to other ADR files then add them. 
		for (Link link: links) {
			addReverseLink(docsPath, link);
		}
		
		
		return p;
	}
	
	
	
	/** Adds a reverse link reference to an ADR at the end of the Status section with the form
	 * "Linked to from ADR {id}"
	 * 
	 * @param docsPath The path where the ADRs are..
	 * @param link The ADR record file where the reverse link reference is to be added.
	 */
	private void addReverseLink(Path docsPath, Link link) {
			
		// Find and open the file where the reverse link comment should be placed.
		Path adrPath = FileSystems.getDefault().getPath(docsPath.toString());
        try (Stream<Path> stream = Files.list(adrPath)) {
			
			Path[] paths = stream.filter(ADRFilter.filter(link.id)).toArray(Path[]::new);
			if (paths.length == 1 ) {
				// Read in the file
				List<String> lines = Files.readAllLines(paths[0]);
				 
				
				// Find the Status section (before the context station) and add the reverse link comment
				String line; 
				for (int index = 0; index < lines.size(); index++) {
					line = lines.get(index); 
					if (line.startsWith("## Context")) {  // TODO Need to have use constants for the titles
						lines.add(index, link.reverseComment + " ADR " + id );
						lines.add(index+1, "");
						break;
					}
				}
			    				
				// Write out the file
				Files.write(paths[0],lines);
				
			}
			else {
				System.err.print("FATAL: More than one matching ADR file found for the reverse link.");
				System.exit(1);
			}
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	/**
	 * @return the supercedes records
	 */
	public List<Integer> getSupercedes() {
		ArrayList<Integer> listSupercedes = new ArrayList<Integer>(supercedes);
			
		return listSupercedes;
	}

	/**
	 * @param supercede the supercede to set
	 */
	public void addSupercede(Integer supercede) {
		supercedes.add(supercede);
	}
	
	
	/**
	 * Adds a link the the record
	 * @param id  the id of the ADR being linked to.
	 * @param comment The link comment in this ADR 
	 * @param reverseComment The comment added to the ADR with the specified id. 
	 */
	public void addLink(Integer id, String comment, String reverseComment)  {
		links.add(new Link(id, comment, reverseComment));
	}

    /** 
     * Based on a link specification of the form "LinkID:LinkComment:ReverseLinkComment" create a link 
     * and add it to the record. 
     * 			LinkId             - The id of the ADR being linked to.
	 *          LinkComment        - The link comment in this ADR 
	 *          ReverseLinkComment - The comment added to the ADR with the specified id. 
     * 
     * 
     * @param linkSpec  The link specification as string
     * @throws LinkSpecificationException  Thrown if the link specification is incorrect
     */
	public void addLink(String linkSpec) throws LinkSpecificationException {
    	try {
			if (linkSpec.length() > 0  ) { 
				String[] linkSpecs =  linkSpec.split(":");
			    if (linkSpecs.length == 3) {
			    	links.add(new Link(new Integer(linkSpecs[0]), linkSpecs[1], linkSpecs[2]));
			    }
			    else { 
			    	throw new LinkSpecificationException();
			    }
			
			}
		} catch (NumberFormatException e) {
			throw new LinkSpecificationException();
	   }	
    	
    }
	
	private String capitalizeFirstCharacter(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
	
	private String lowercaseFirstCharacter(String s) {
		return s.substring(0, 1).toLowerCase() + s.substring(1);
	}



}
