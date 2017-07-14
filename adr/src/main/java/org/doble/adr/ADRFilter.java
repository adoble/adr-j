package org.doble.adr;
import java.nio.file.*;
import java.util.function.Predicate;

public class ADRFilter {
	

	public static Predicate<Path> filter() {

		return p -> { 
			String glob = "[0-9][0-9][0-9][0-9]*.md";
			return p.getFileSystem().getPathMatcher("glob:" + glob).matches(p.getName(p.getNameCount()-1));
		};

	}
	
	public static Predicate<Path> filter(String adrID) {

		return p -> { 
			String glob = adrID + "*.md";
			return p.getFileSystem().getPathMatcher("glob:" + glob).matches(p.getName(p.getNameCount()-1));
			
		};

	}
	
	public static Predicate<Path> filter(int adrID) {
				return p -> { 
			String formattedID = String.format("%04d", adrID);
			String glob = formattedID + "*.md";
			return p.getFileSystem().getPathMatcher("glob:" + glob).matches(p.getName(p.getNameCount()-1));
			
		};

	}

	
}
