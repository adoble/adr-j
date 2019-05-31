package org.doble.adr.template;

public class MarkdownTemplateEngine implements TemplateEngine {


	@Override
	public String getRecordTemplate() {
		return "# @ID. @Name\n\n" +
				"Date: @Date\n\n" +
				"## Status\n\n" +
				"@Status\n\n" +
				"## Context\n\n" +
				"@Context\n\n" +
				"## Decision\n\n" +
				"@Decision\n\n" +
				"## Consequences\n\n" +
				"@Consequences\n";
	}

	@Override
	public String getFileExtension() {
		return ".md";
	}
}
