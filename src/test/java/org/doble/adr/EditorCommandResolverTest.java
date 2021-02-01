package org.doble.adr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class EditorCommandResolverTest {

	@Test
	void editorCommandIsNullWhenNoVariablesSet() {
		EditorCommandResolver resolver = new EditorCommandResolver(new HashMap<>());

		String editorCommand = resolver.editorCommand();

		assertNull(editorCommand, "no variables set so the editor command must be null");
	}

	@Test
	void extractEditorCommandFromEditorVariable() {
		Map<String, String> env = new HashMap<>();
		env.put("EDITOR", "editor");
		EditorCommandResolver resolver = new EditorCommandResolver(env);

		String editorCommand = resolver.editorCommand();

		assertEquals( "editor", editorCommand, "the command must be extracted from EDITOR variable");
	}

	@Test
	void extractEditorCommandFromVisualVariable() {
		Map<String, String> env = new HashMap<>();
		env.put("VISUAL", "visual");
		EditorCommandResolver resolver = new EditorCommandResolver(env);

		String editorCommand = resolver.editorCommand();

		assertEquals( "visual", editorCommand, "the command must be extracted from VISUAL variable");
	}

	@Test
	void preferValueInEditorVariableOverVisualVariable() {
		Map<String, String> env = new HashMap<>();
		env.put("VISUAL", "visual");
		env.put("EDITOR", "editor");
		EditorCommandResolver resolver = new EditorCommandResolver(env);

		String editorCommand = resolver.editorCommand();

		assertEquals( "editor", editorCommand,
			"The EDITOR variable has higher priority than VISUAL. The command must come from the EDITOR variable");
	}

	@Test
	void preferPrefixedVisualToSystemEditors() {
		Map<String, String> env = new HashMap<>();
		env.put("VISUAL", "visual");
		env.put("EDITOR", "editor");
		env.put("ADR_VISUAL", "adr-visual");
		EditorCommandResolver resolver = new EditorCommandResolver(env);

		String editorCommand = resolver.editorCommand();

		assertEquals( "adr-visual", editorCommand,
			"The ADR-prefixed variable has higher priority than system variables");
	}

	@Test
	void preferPrefixedEditorToAllOtherEditors() {
		Map<String, String> env = new HashMap<>();
		env.put("VISUAL", "visual");
		env.put("ADR_EDITOR", "adr-editor");
		env.put("EDITOR", "editor");
		env.put("ADR_VISUAL", "adr-visual");
		EditorCommandResolver resolver = new EditorCommandResolver(env);

		String editorCommand = resolver.editorCommand();

		assertEquals( "adr-editor", editorCommand,
			"The ADR_EDITOR variable has highest priority");
	}
}
