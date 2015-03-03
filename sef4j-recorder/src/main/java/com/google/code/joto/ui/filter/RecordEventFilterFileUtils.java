package com.google.code.joto.ui.filter;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.predicate.RecordEventSummaryPredicateUtils;
import com.google.code.joto.util.io.XStreamUtils;
import com.thoughtworks.xstream.XStream;

/**
 *
 */
public class RecordEventFilterFileUtils {
	
	private static Logger log = LoggerFactory.getLogger(RecordEventFilterFileUtils.class);
	
	public static XStream getXStream() {
		XStream xstream = new XStream();
		registerDefaultXStreamAlias(xstream);
		return xstream;
	}
	
	public static void registerDefaultXStreamAlias(XStream res) {
		res.alias("eventFilter", RecordEventFilterFile.class);
		RecordEventSummaryPredicateUtils.registerDefaultXStreamAlias(res);
	}
	
	public static void saveFilterFile(RecordEventFilterFile filterFile) {
		XStream xstream = RecordEventFilterFileUtils.getXStream();
		File file = filterFile.getPersistentFile();
		if (file == null) {
			// "save as..." instead of "save" => prompt filename or generate unique file name? 
			// file path attribute should be set for storing, generate a unique name...
			for(int i = 1; ; i++) {
				String testFileName = "tmp-filter-" + i + ".xml";
				File testFile = new File(testFileName);
				if (!testFile.exists()) {
					file = testFile;
					break;
				}
			}
			filterFile.setPersistentFile(file);
		}
		XStreamUtils.toFile(xstream, filterFile, file);
	}
	

	public static void loadFilterFile(RecordEventFilterFile filterFile) {
		File file = filterFile.getPersistentFile();
		if (file == null) {
			log.error("file is not set, can not reload");
			return;
		}
		if (!file.exists()) {
			log.error("file not found, can not reload");
			return;
		}
		XStream xstream = RecordEventFilterFileUtils.getXStream();
		RecordEventFilterFile newModelData = (RecordEventFilterFile) XStreamUtils.fromFile(xstream, file);
		filterFile.set(newModelData);
	}

}
