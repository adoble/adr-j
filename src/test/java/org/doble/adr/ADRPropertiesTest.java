package org.doble.adr;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.opentest4j.MultipleFailuresError;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

@TestMethodOrder(OrderAnnotation.class)
class ADRPropertiesTest {
	final static private String rootPathName = "/project";
	private FileSystem fileSystem;
	private Environment env;
	
	@BeforeEach
	void setUp() throws Exception {
		fileSystem = Jimfs.newFileSystem(Configuration.unix());

		Path projectPath = fileSystem.getPath(rootPathName);
		Files.createDirectory(projectPath);

		// Set up the environment
		env = new Environment.Builder(fileSystem)
				.out(System.out)
				.err(System.err)
				.in(System.in)
				.userDir(rootPathName)
				.editorCommand("dummyEditor")
				.build();
	
	}

	@AfterEach
	void tearDown() throws Exception {
		fileSystem.close();
	}

	@Test
	@Order(1)
	void testStoreNewPopertiesFile() {
		
		ADRProperties properties = new ADRProperties(env);
		properties.setProperty("author", "shakespeare");
		properties.setProperty("docPath", "doc/adr");
		
		
		try {
			properties.store();
			
			// Now check if they have been stored
			Path adrPropertiesPath =  env.fileSystem.getPath(env.dir + "/.adr/adr.properties");
			List<String> lines = Files.readAllLines(adrPropertiesPath);
			assertTrue((lines.contains("author=shakespeare")));
			assertTrue((lines.contains("docPath=doc/adr")));
		} catch (ADRException | IOException e) {
			e.printStackTrace();
			fail();
		}
	
		
	}
	
	@Test
	@Order(2)
	void testStoreExistingPropertiesFile() {
		ADRProperties properties = new ADRProperties(env);
		properties.setProperty("author", "shakespeare");
		properties.setProperty("docPath", "doc/adr");
		
		
		try {
			properties.store();
		} catch (ADRException e) {
			e.printStackTrace();
			fail();
		}
		
		// Check that the properties file exists
		assertTrue(Files.exists(env.fileSystem.getPath(env.dir + "/.adr/adr.properties")));
		
		// Change the properties, store them and check that they have been changed.
		properties.setProperty("author", "marlow");
		properties.setProperty("newProperty", "true");
		
		try {
			properties.store();
			// Now check if they have been stored
			Path adrPropertiesPath =  env.fileSystem.getPath(env.dir + "/.adr/adr.properties");
			List<String> lines = Files.readAllLines(adrPropertiesPath);
			assertTrue((lines.contains("author=marlow")));
			assertTrue((lines.contains("newProperty=true")));
		} catch (ADRException | IOException e) {
			e.printStackTrace();
			fail();
		}
	
	}
	
	@Test
	@Order(3)
	void testLoad() {
		
		ADRProperties properties = new ADRProperties(env);
		properties.setProperty("author", "shakespeare");
		properties.setProperty("docPath", "doc/adr");
		
		
		try {
			properties.store();
			
			ADRProperties loadedProperties = new ADRProperties(env);
			loadedProperties.load();
			assertAll("Properties loaded", 
				    	() -> assertEquals("shakespeare", loadedProperties.getProperty("author")),
					    () -> assertEquals("doc/adr", loadedProperties.getProperty("docPath"))
			        );
		} catch (MultipleFailuresError | ADRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		

		
		
	}

}
