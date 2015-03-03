package com.google.code.joto.ui.spy.awtspy;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.spy.awtspy.AWTEventInfos;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTEventInfos.AWTEventGroupInfo;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTEventInfos.AWTEventMaskInfo;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTRecordEventWriterSpy;
import com.google.code.joto.ui.JotoContext;
import com.google.code.joto.ui.capture.RecordEventsCaptureCategoryPanel;
import com.google.code.joto.util.ui.GridBagLayoutFormBuilder;
import com.google.code.joto.util.ui.JButtonUtils;
import com.google.code.joto.util.ui.JCheckBoxUtils;

/**
 *
 */
public class AWTEventCaptureCategoryPanel extends RecordEventsCaptureCategoryPanel {

	public static final String AWTSPY_CAPTURE_CATEGORY = "AWTSpy";
	
	private static Logger log = LoggerFactory.getLogger(AWTEventCaptureCategoryPanel.class);
	
	private AWTRecordEventWriterSpy awtSpy;

	private JCheckBox activateAWTToolkitListenerCheckBox;

	private Map<AWTEventMaskInfo,JCheckBox> eventMaskCheckBoxes = new HashMap<AWTEventMaskInfo,JCheckBox>();
	private Map<AWTEventGroupInfo,JCheckBox> eventGroupFlagCheckBoxes = new HashMap<AWTEventGroupInfo,JCheckBox>();
	
	
	
	// ------------------------------------------------------------------------
	
	public AWTEventCaptureCategoryPanel(JotoContext context) {
		super(context, AWTSPY_CAPTURE_CATEGORY);
		
		specificPanel.setLayout(new GridBagLayout());
		GridBagLayoutFormBuilder b = new GridBagLayoutFormBuilder(specificPanel);
		
		awtSpy = new AWTRecordEventWriterSpy(filterCategoryModel.getResultFilteringEventWriter());
		
		activateAWTToolkitListenerCheckBox = JCheckBoxUtils.snew("Activate AWT Toolkit Listener", false, this, "onActivateAWTToolkitListenerCheckBox");
		b.addCompRow(activateAWTToolkitListenerCheckBox);
		
		{ // toolbar
			JToolBar toolbar = new JToolBar();
			toolbar.setFloatable(false);
			
			JButton selectAllButton = JButtonUtils.snew("Select All", this, "onButtonSelectAll");
			toolbar.add(selectAllButton);

			JButton selectDefaultButton = JButtonUtils.snew("Select Default", this, "onButtonSelectDefault");
			toolbar.add(selectDefaultButton);

			JButton deselectAllButton = JButtonUtils.snew("Deselect All", this, "onButtonDeselectAll");
			toolbar.add(deselectAllButton);

			b.addCompRow(toolbar);
		}
		
		{ // checkbox Masks Panel (several columns)
			JPanel masksPanel = new JPanel(new GridBagLayout());
			JScrollPane masksScrollPanel = new JScrollPane(masksPanel);
			// masksScrollPanel.setPreferredSize(new Dimension(200, 500));
			b.addCompFillRow(masksScrollPanel);
			
			GridBagLayoutFormBuilder mb = new GridBagLayoutFormBuilder(masksPanel);
			
			for (AWTEventMaskInfo awtEventMaskInfo : AWTEventMaskInfo.values()) {
				JPanel eventMaskPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				mb.addCompFillRow(eventMaskPanel);
				
				{ // checkbox for AWTEvent MASK
					final JCheckBox awkMaskCheckBox = new JCheckBox(awtEventMaskInfo.getFlagName());
					eventMaskPanel.add(awkMaskCheckBox);
					awkMaskCheckBox.setSelected(awtSpy.getAwtEventMaskFlag(awtEventMaskInfo));
					final AWTEventMaskInfo fAwtEventMaskInfo = awtEventMaskInfo;
					awkMaskCheckBox.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							onCheckBoxAWTEventMask(fAwtEventMaskInfo, awkMaskCheckBox.isSelected());
						}
					});
					eventMaskCheckBoxes.put(awtEventMaskInfo, awkMaskCheckBox);
				}
				
				{ // checkbox panel for AWTEvent sub-groups 
					int subGroupCount = awtEventMaskInfo.getEventGroups().length;
					if (subGroupCount != 1) {
						JPanel eventGroupsPanel = new JPanel(new GridLayout(subGroupCount, 1));
						eventMaskPanel.add(eventGroupsPanel);
						for (AWTEventGroupInfo awtEventGroupInfo : awtEventMaskInfo.getEventGroups()) {
							awtEventGroupInfo.getGroupName();
							final JCheckBox awkEventGroupCheckBox = new JCheckBox(awtEventGroupInfo.getGroupName());
							eventGroupsPanel.add(awkEventGroupCheckBox);
							awkEventGroupCheckBox.setSelected(awtSpy.getEventGroupFlag(awtEventGroupInfo));
							final AWTEventGroupInfo fAwtEventGroupInfo = awtEventGroupInfo;
							awkEventGroupCheckBox.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									onCheckBoxAWTEventGroup(fAwtEventGroupInfo, awkEventGroupCheckBox.isSelected());
								}
							});
							eventGroupFlagCheckBoxes.put(fAwtEventGroupInfo, awkEventGroupCheckBox);
							
						}
					}
				}
			}
		}
		
	}

	
	// ------------------------------------------------------------------------
	
	/** called by introspection, GUI callback */
	public void onActivateAWTToolkitListenerCheckBox(ActionEvent event) {
		awtSpy.setEnable(activateAWTToolkitListenerCheckBox.isSelected());
	}


	/** called by introspection, GUI callback */
	public void onButtonSelectAll(ActionEvent event) {
		boolean prevEnable = awtSpy.isEnable();
		awtSpy.setEnable(false); //tmp set
		
		awtSpy.setAwtEventMask(AWTEventInfos.ALL_AWTEVENTS_MASK);
		for (JCheckBox cb : eventMaskCheckBoxes.values()) {
			cb.setSelected(true);
			// awtSpy.setAwtEventMaskFlag(eventMaskInfo, true);... cf above: set ALL_AWTEVENTS_MASK 
		} 
		for (Map.Entry<AWTEventGroupInfo,JCheckBox> e : eventGroupFlagCheckBoxes.entrySet()) {
			AWTEventGroupInfo eventGroupInfo = e.getKey();
			awtSpy.setEventGroupFlag(eventGroupInfo, true);
			e.getValue().setSelected(true);
		}
		
		awtSpy.setEnable(prevEnable);
	}
	
	/** called by introspection, GUI callback */
	public void onButtonSelectDefault(ActionEvent event) {
		boolean prevEnable = awtSpy.isEnable();
		awtSpy.setEnable(false); //tmp set
		
		for(Map.Entry<AWTEventMaskInfo,JCheckBox> e : eventMaskCheckBoxes.entrySet()) {
			AWTEventMaskInfo eventMaskInfo = e.getKey();
			JCheckBox cb = e.getValue();
			boolean def = eventMaskInfo.getDefaultSelected();
			awtSpy.setAwtEventMaskFlag(eventMaskInfo, def);
			cb.setSelected(def);
		}
		
		for (Map.Entry<AWTEventGroupInfo,JCheckBox> e : eventGroupFlagCheckBoxes.entrySet()) {
			AWTEventGroupInfo eventGroupInfo = e.getKey();
			boolean def = eventGroupInfo.getDefaultSelected();
			awtSpy.setEventGroupFlag(eventGroupInfo, def);
			e.getValue().setSelected(def);
		}
		
		awtSpy.setEnable(prevEnable);
	}
	
	/** called by introspection, GUI callback */
	public void onButtonDeselectAll(ActionEvent event) {
		awtSpy.setAwtEventMask(0);
		for (JCheckBox cb : eventMaskCheckBoxes.values()) {
			cb.setSelected(false);
			// awtSpy.setAwtEventMaskFlag(eventMaskInfo, true);... cf above: set 0 
		}
		for (Map.Entry<AWTEventGroupInfo,JCheckBox> e : eventGroupFlagCheckBoxes.entrySet()) {
			AWTEventGroupInfo eventGroupInfo = e.getKey();
			awtSpy.setEventGroupFlag(eventGroupInfo, false);
			e.getValue().setSelected(false);
		}

	}
	

	/** GUI callback */
	private void onCheckBoxAWTEventMask(AWTEventMaskInfo awtEventMaskInfo, boolean selected) {
		long oldMask = awtSpy.getAwtEventMask();
		long flag = awtEventMaskInfo.getFlag();
		long newMask = AWTRecordEventWriterSpy.clearOrSetFlag(oldMask, flag, selected);
		log.debug("toggle AWTSpy event mask:" + (selected? "set" : "clear") + " " + awtEventMaskInfo.getFlagName()
				+ " => " + AWTRecordEventWriterSpy.maskToString(newMask)
				// + " (old:" + AWTRecordEventWriterSpy.maskToString(oldMask) + ")"
				);
		awtSpy.setAwtEventMask(newMask);
	}
	
	/** GUI callback */
	private void onCheckBoxAWTEventGroup(AWTEventGroupInfo awtEventGroupInfo, boolean selected) {
		log.info("set AWTSpy EventGroup " + awtEventGroupInfo.getGroupName() + " " + (selected? "enabled" : "disabled"));
		awtSpy.setEventGroupFlag(awtEventGroupInfo, selected);
	}
	
}
