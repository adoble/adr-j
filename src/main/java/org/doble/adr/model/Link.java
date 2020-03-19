package org.doble.adr.model;

import java.nio.file.Path;
import java.util.Optional;

import org.doble.adr.ADR;
import org.doble.adr.LinkSpecificationException;

public class Link {
	Integer id;
	String comment = "";
	String reverseComment = "";
	private Path docsPath;
	
	/**
	 * Create a (forward)link using the filed values directly.
	 * @param id The id of the ADR being linked to.
	 * @param comment The link comment in this ADR.
	 * @param reverseComment The comment added to a referenced (reverse link) ADR with the specified id 
	 * @param docsPath The path containing the ADRs.
	 */
	Link(Integer id, String comment, String adrFileName, String reverseComment, Path docsPath) {
		this.id = id;
		this.comment = comment;
		this.reverseComment = reverseComment;
		this.docsPath = docsPath;
	}
	
	/**
	 * Based on a link specification of the form "LinkID:LinkComment:ReverseLinkComment" create a (forward) link.
	 * LinkId             - The id of the ADR being linked to.
	 * LinkComment        - The link comment in this ADR  (optional)
	 * ReverseLinkComment - The comment added to a referenced (linked) ADR with the specified id (optional).
	 *
	 * @param linkSpec      The link specification as string
	 * @param docsPath The path containing the ADRs.
	 * @throws              LinkSpecificationException Thrown if the link specification is incorrect
	 */
	Link(String linkSpec, Path docsPath) throws LinkSpecificationException {

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
		this.docsPath = docsPath;

	}

	public String getFragment(Optional<String> templateLinkFragment, Optional<String> templateCommentFragment) {

		
		String linkFragment = templateLinkFragment.get().replace("{{{link.comment}}}", capitalizeFirstCharacter(this.comment))
				.replace("{{{link.id}}}", this.id.toString())
				.replace("{{{link.file}}}", ADR.getADRFileName(this.id, this.docsPath));
		// If the template has specified comments for meta-data then extend to link
		// fragment with the comment
		String expandedCommentFragment = "";
		if (templateCommentFragment.isPresent()) {
			// Surround with newlines as this is required by some markdown processors to
			// render the comment invisible.
			expandedCommentFragment = "\n" + templateCommentFragment.get() + "\n";
			// Replace the fields with their meta-data equivalents
			expandedCommentFragment = expandedCommentFragment.replace("{{template.comment}}", templateLinkFragment.get())
					.replace("{{{link.comment}}}",
							"{{{link.comment=\"" + capitalizeFirstCharacter(this.comment) + "\"}}}")
					.replace("{{{link.id}}}", "{{{link.id=\"" + this.id.toString() + "\"}}}")
					.replace("{{{link.file}}}",
							"{{{link.file=\"" + ADR.getADRFileName(this.id, this.docsPath) + "\"}}}");
		}
		
		return linkFragment + "\n" + expandedCommentFragment;

	}	
	
	private String capitalizeFirstCharacter(String s) {
		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}
}
