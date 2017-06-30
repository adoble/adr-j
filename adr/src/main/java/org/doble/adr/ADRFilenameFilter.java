/**
 * Filters the files that are ADR files, i.e. with the pattern 
 *    dddd*.md
 */
package org.doble.adr;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author adoble
 *
 */
public class ADRFilenameFilter implements FilenameFilter {

	/**
	 * 
	 */
	public ADRFilenameFilter() {
		// TODO Auto-generated constructor stub
	}

	/* 
	 * Filter the files that are ADR files
	 * (non-Javadoc)
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name) {
		boolean endOK = false;
		boolean startOK = false;
		
		
		// Accept if the file ends in .md and starts with an integer (i.e. the adr id); 
		endOK = name.endsWith(".md");
		
		String start = name.substring(0, ADR.MAX_ID_LENGTH);
		
		try {
			Integer.parseInt(start);
			startOK = true; 
		}
		catch (NumberFormatException e) {
			startOK = false;
		}
		
				
		return (endOK && startOK);
	}

}
