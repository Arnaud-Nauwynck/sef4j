package com.google.code.joto.ui.spy.logs;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import com.google.code.joto.eventrecorder.spy.log.EventStoreWriterLogbackAppender;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.capture.RecordEventsCaptureCategoryPanel;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;

/**
 *
 */
public class LogCallCaptureCategoryPanel extends RecordEventsCaptureCategoryPanel {

	public static final String LOGS_CAPTURE_CATEGORY = "logs";

	private EventStoreWriterLogbackAppender logAppenderSpy;
	
	// ------------------------------------------------------------------------
	
	public LogCallCaptureCategoryPanel(JotoContext jotoContext) {
		super(jotoContext, LOGS_CAPTURE_CATEGORY);

		// TODO ... code to move in model / outside ctor
		// create + install appender to root logback Logger
		logAppenderSpy = new EventStoreWriterLogbackAppender(filterCategoryModel.getResultFilteringEventWriter(), LOGS_CAPTURE_CATEGORY);
		ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		rootLogger.addAppender(logAppenderSpy);
		logAppenderSpy.start();
		
		specificPanel.setLayout(new GridBagLayout());
		GridBagLayoutFormBuilder b = new GridBagLayoutFormBuilder(specificPanel);

		b.addLabelFillRow("Minimum Level");
		ButtonGroup levelGroup = new ButtonGroup();  
		// b.addCompRow(createMinimumLevelRadioButton(levelGroup, Level.ALL));
		b.addCompRow(createMinimumLevelRadioButton(levelGroup, Level.TRACE));
		b.addCompRow(createMinimumLevelRadioButton(levelGroup, Level.DEBUG));
		JRadioButton infoRadioButton = createMinimumLevelRadioButton(levelGroup, Level.INFO);
		b.addCompRow(infoRadioButton);
		infoRadioButton.setSelected(true);
		b.addCompRow(createMinimumLevelRadioButton(levelGroup, Level.WARN));
		b.addCompRow(createMinimumLevelRadioButton(levelGroup, Level.ERROR));
		b.addCompRow(createMinimumLevelRadioButton(levelGroup, Level.OFF));
	}

	// ------------------------------------------------------------------------
	
	private JRadioButton createMinimumLevelRadioButton(ButtonGroup levelGroup, final Level level) {
		JRadioButton res = new JRadioButton(level.toString());
		res.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				logAppenderSpy.setMinimumLevel(level);
			}
		});
		return res;
	}
	
}
