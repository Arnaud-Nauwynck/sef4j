package org.sef4j.core.helpers.files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;

import org.junit.Assert;
import org.junit.Test;


public class ContentEnricherFileChangeEventTransformerTest {

	ContentEnricherFileChangeEventTransformer sut = ContentEnricherFileChangeEventTransformer.INSTANCE;
	
	@Test
	public void testApply() {
		// Prepare
		Path path = Paths.get("pom.xml");
		FileChangeEvent event = new FileChangeEvent(path.toString(), StandardWatchEventKinds.ENTRY_MODIFY);
		// Perform
		ContentFileChangeEvent res = sut.apply(event);
		// Post-check
		byte[] content = res.getContent();
		Assert.assertNotNull(content);
		String contentStr = new String(content);
		contentStr.startsWith("<?xml version=\"1.0\"");
	}
}
