package org.doble.adr;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Extract editor command from the system environment.
 */
public class EditorCommandResolver {

	private final Map<String, String> env;

	public EditorCommandResolver(Map<String, String> env) {
		this.env = env;
	}

	/**
	 * Creates a default instance of resolver configured via actual environment variables.
	 */
	public EditorCommandResolver() {
		this(System.getenv());
	}

	/**
	 * Retrieve the command to launch editor.
	 *
	 * @return the command if there is a set environment variable or null, if no variable set.
	 */
	public String editorCommand() {
		return Stream.of("ADR_EDITOR", "ADR_VISUAL", "EDITOR", "VISUAL")
			.map(env::get)
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}
}
