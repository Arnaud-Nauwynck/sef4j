package org.sef4j.core.sources.files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;

import org.junit.Assert;
import org.junit.Test;
import org.sef4j.core.sources.files.ContentEnricherFileChangeEventTransformer;
import org.sef4j.core.sources.files.ContentFileChangeEvent;
import org.sef4j.core.sources.files.FileChangeEvent;

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
