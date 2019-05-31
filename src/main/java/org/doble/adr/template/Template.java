package org.doble.adr.template;

public enum Template {


	MARKDOWN(new MarkdownTemplateEngine()),
	ASCIIDOC(new AsciidocTemplateEngine());

	private final TemplateEngine templateEngine;

	Template(final TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}

	public TemplateEngine templateEngine(){
		return this.templateEngine;
	}

}
