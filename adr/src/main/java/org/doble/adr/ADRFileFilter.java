/**
 * Filters the file that is a specific ADR as specified by the ADR id. 
 */
package org.doble.adr;

import java.io.File;
import java.io.FileFilter;

/**
 * @author adoble
 *
 */
public class ADRFileFilter implements FileFilter {
    private Integer linkId; 
	/**
	 * 
	 */
	public ADRFileFilter(Integer linkId) {
		this.linkId = linkId;
	}

	/* 
	 * Filter the files that are ADR files
	 * (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File f) {
		boolean endOK = false;
		boolean startOK = false;
		Integer id; 
		String filename = f.getName(); 
		
		
		// Accept if the file ends in .md and starts with an integer (i.e. the adr id); 
		endOK = filename.endsWith(".md");
		
		String start = filename.substring(0, ADR.MAX_ID_LENGTH);
		
		try {
			id = Integer.parseInt(start);
			
			startOK = (id.equals(linkId)); 
		}
		catch (NumberFormatException e) {
			startOK = false;
		}
		
				
		return (endOK && startOK);
	}

}