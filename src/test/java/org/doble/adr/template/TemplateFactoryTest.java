package org.doble.adr.template;

import org.doble.adr.ADRException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemplateFactoryTest {

	@Test
	public void shouldReturnAsciidocTemplateEngineWhenAsciidocRequested() throws ADRException {
		assertEquals(AsciidocTemplateEngine.class, Template.ASCIIDOC.templateEngine().getClass());
	}

	@Test
	public void shouldReturnMarkdownTemplateEngineWhenMarkdownRequested() throws ADRException {
		assertEquals(MarkdownTemplateEngine.class, Template.MARKDOWN.templateEngine().getClass());
	}


}
