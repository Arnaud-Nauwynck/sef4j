package org.sef4j.core.helpers.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentEnricherFileChangeEventTransformer implements Function<FileChangeEvent,ContentFileChangeEvent>{

	private static final Logger LOG = LoggerFactory.getLogger(ContentEnricherFileChangeEventTransformer.class);
	
	public static final ContentEnricherFileChangeEventTransformer INSTANCE = new ContentEnricherFileChangeEventTransformer();
	
	// ------------------------------------------------------------------------

	public ContentEnricherFileChangeEventTransformer() {
	}

	// ------------------------------------------------------------------------

	@Override
	public ContentFileChangeEvent apply(FileChangeEvent event) {
		String filePathStr = event.getFilePath();
		Path path = Paths.get(filePathStr);
		byte[] content = safeLoadFileContent(path);
		return new ContentFileChangeEvent(filePathStr, event.getEventKind(), content);
	}

	protected byte[] safeLoadFileContent(Path fileName) {
		byte[] fileContent;
		try {
			fileContent = Files.readAllBytes(fileName);
		} catch (IOException ex) {
			LOG.warn("failed to read file " + fileName + ".. ignore, no rethrow!");
			fileContent = null;
		}
		return fileContent;
	}
	
}
