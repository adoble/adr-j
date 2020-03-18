package org.doble.adr.model;

import java.nio.file.Path;

import org.doble.adr.ADR;
import org.doble.adr.LinkSpecificationException;

public class Link {
	Integer id;
	String comment = "";
	String reverseComment = "";
	
	/**
	 * Create a (forward)link using the filed values directly.
	 * @param id The id of the ADR being linked to.
	 * @param comment The link comment in this ADR.
	 * @param reverseComment The comment added to a referenced (reverse link) ADR with the specified id 
	 */
	Link(Integer id, String comment, String adrFileName, String reverseComment) {
		this.id = id;
		this.comment = comment;
		this.reverseComment = reverseComment;
	}
	
	/**
	 * Based on a link specification of the form "LinkID:LinkComment:ReverseLinkComment" create a (forward) link.
	 * LinkId             - The id of the ADR being linked to.
	 * LinkComment        - The link comment in this ADR  (optional)
	 * ReverseLinkComment - The comment added to a referenced (linked) ADR with the specified id (optional).
	 *
	 * @param linkSpec      The link specification as string
	 * @throws              LinkSpecificationException Thrown if the link specification is incorrect
	 */
	Link(String linkSpec) throws LinkSpecificationException {

		try {
			if (linkSpec.length() > 0) {
				String[] linkSpecs = linkSpec.split(":");
				if (linkSpecs.length >= 1) id = new Integer(linkSpecs[0]);
				if (linkSpecs.length >= 2) comment = linkSpecs[1];
				if (linkSpecs.length == 3) reverseComment = linkSpecs[2];
				if (linkSpecs.length > 3 || linkSpecs.length == 0) {
					throw new LinkSpecificationException();
				}
			}
		} catch (NumberFormatException e) {
			throw new LinkSpecificationException();
		}

	}

	
}
