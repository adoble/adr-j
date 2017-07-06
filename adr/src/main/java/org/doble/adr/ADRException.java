/**
 * 
 */
package org.doble.adr;

/**
 * Exception is raised from the adr command handlers if they have encountered an error. 
 * This is usually raised after the command has printed a user error/warning
 * message 
 * @author adoble
 *
 */
public class ADRException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//TODO check that this constructor for ADRException is used throughout
	public ADRException(String msg) {
		super(msg);
	
	}

}
