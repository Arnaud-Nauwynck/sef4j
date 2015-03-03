package com.google.code.joto.eventrecorder.spy.awtspy;

import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.FocusEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InvocationEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.TextEvent;
import java.awt.event.WindowEvent;

/**
 * infos about AWTEvent, and AWT_EVENT_MASKS, with event sub-groups  
 */
public class AWTEventInfos {

	public static final long ALL_AWTEVENTS_MASK =
			AWTEvent.COMPONENT_EVENT_MASK
			| AWTEvent.CONTAINER_EVENT_MASK // cf ContainerEvent
			| AWTEvent.FOCUS_EVENT_MASK // cf FocusEvent
			| AWTEvent.KEY_EVENT_MASK
			| AWTEvent.MOUSE_EVENT_MASK
			| AWTEvent.MOUSE_MOTION_EVENT_MASK
			| AWTEvent.WINDOW_EVENT_MASK
			| AWTEvent.ACTION_EVENT_MASK
			| AWTEvent.ADJUSTMENT_EVENT_MASK
			| AWTEvent.ITEM_EVENT_MASK
			| AWTEvent.TEXT_EVENT_MASK
			| AWTEvent.INPUT_METHOD_EVENT_MASK
			// | AWTEvent.INPUT_METHODS_ENABLED_MASK
			| AWTEvent.PAINT_EVENT_MASK 
			| AWTEvent.INVOCATION_EVENT_MASK
			| AWTEvent.HIERARCHY_EVENT_MASK
			| AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK
			| AWTEvent.MOUSE_WHEEL_EVENT_MASK
			| AWTEvent.WINDOW_STATE_EVENT_MASK
			| AWTEvent.WINDOW_FOCUS_EVENT_MASK;

	
	/**
	 * info for AWTEvent
	 */
	public static enum AWTEventInfo {
		
		// ComponentEvent
		COMPONENT_SHOWN(ComponentEvent.COMPONENT_SHOWN, "COMPONENT_SHOWN", "ComponentEvent", "Shown"),
		COMPONENT_HIDDEN(ComponentEvent.COMPONENT_HIDDEN, "COMPONENT_HIDDEN", "ComponentEvent", "Hidden"),
		COMPONENT_MOVED(ComponentEvent.COMPONENT_MOVED, "COMPONENT_MOVED", "ComponentEvent", "Moved"),
		COMPONENT_RESIZED(ComponentEvent.COMPONENT_RESIZED, "COMPONENT_RESIZED", "ComponentEvent", "Resized"),

		// ContainerEvent
		COMPONENT_ADDED(ContainerEvent.COMPONENT_ADDED, "COMPONENT_ADDED", "ContainerEvent", "Added"),
		COMPONENT_REMOVED(ContainerEvent.COMPONENT_REMOVED, "COMPONENT_REMOVED", "ContainerEvent", "Removed"),

		// FocusEvent
		FOCUS_GAINED(FocusEvent.FOCUS_GAINED, "FOCUS_GAINED", "FocusEvent", "FocusGained"),
		FOCUS_LOST(FocusEvent.FOCUS_LOST, "FOCUS_LOST", "FocusEvent", "FocusLost"),

		// KeyEvent
		KEY_PRESSED(KeyEvent.KEY_PRESSED, "KEY_PRESSED", "KeyEvent", "Pressed"),
		KEY_RELEASED(KeyEvent.KEY_RELEASED, "KEY_RELEASED", "KeyEvent", "Released"),
		KEY_TYPED(KeyEvent.KEY_TYPED, "KEY_TYPED", "KeyEvent", "Typed"),

		// MouseEvent
		MOUSE_PRESSED(MouseEvent.MOUSE_PRESSED, "MOUSE_PRESSED", "MouseEvent", "Pressed"),
		MOUSE_RELEASED(MouseEvent.MOUSE_RELEASED, "MOUSE_RELEASED", "MouseEvent", "Released"),
		MOUSE_CLICKED(MouseEvent.MOUSE_CLICKED, "MOUSE_CLICKED", "MouseEvent", "Clicked"),
		MOUSE_MOVED(MouseEvent.MOUSE_MOVED, "MOUSE_MOVED", "MouseEvent", "Moved"),
		MOUSE_DRAGGED(MouseEvent.MOUSE_DRAGGED, "MOUSE_DRAGGED", "MouseEvent", "Dragged"),
		MOUSE_ENTERED(MouseEvent.MOUSE_ENTERED, "MOUSE_ENTERED", "MouseEvent", "Eventered"),
		MOUSE_EXITED(MouseEvent.MOUSE_EXITED, "MOUSE_EXITED", "MouseEvent", "Exited"),
		MOUSE_WHEEL(MouseEvent.MOUSE_WHEEL, "MOUSE_WHEEL", "MouseEvent", "Wheel"),

		// WindowEvent
		WINDOW_OPENED(WindowEvent.WINDOW_OPENED, "WINDOW_OPENED", "WindowEvent", "Opened"),
		WINDOW_CLOSING(WindowEvent.WINDOW_CLOSING, "WINDOW_CLOSING", "WindowEvent", "Closing"),
		WINDOW_CLOSED(WindowEvent.WINDOW_CLOSED, "WINDOW_CLOSED", "WindowEvent", "Closed"),
		WINDOW_ICONIFIED(WindowEvent.WINDOW_ICONIFIED, "WINDOW_ICONIFIED", "WindowEvent", "Iconified"),
		WINDOW_DEICONIFIED(WindowEvent.WINDOW_DEICONIFIED, "WINDOW_DEICONIFIED", "WindowEvent", "Deiconified"),
		WINDOW_ACTIVATED(WindowEvent.WINDOW_ACTIVATED, "WINDOW_ACTIVATED", "WindowEvent", "Activated"),
		WINDOW_DEACTIVATED(WindowEvent.WINDOW_DEACTIVATED, "WINDOW_DEACTIVATED", "WindowEvent", "Deactivated"),
		WINDOW_GAINED_FOCUS(WindowEvent.WINDOW_GAINED_FOCUS, "WINDOW_GAINED_FOCUS", "WindowEvent", "GainedFocus"),
		WINDOW_LOST_FOCUS(WindowEvent.WINDOW_LOST_FOCUS, "WINDOW_LOST_FOCUS", "WindowEvent", "LostFocus"),
		WINDOW_STATE_CHANGED(WindowEvent.WINDOW_STATE_CHANGED, "WINDOW_STATE_CHANGED", "WindowEvent", "StateChanged"),

		// ActionEvent
		ACTION_PERFORMED(ActionEvent.ACTION_PERFORMED, "ACTION_PERFORMED", "ActionEvent", "Performed"),

		// AdjustmentEvent
		ADJUSTMENT_VALUE_CHANGED(AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED, "ADJUSTMENT_VALUE_CHANGED", "AdjustmentEvent", "ValueChanged"),

		// ItemEvent
		ITEM_STATE_CHANGED(ItemEvent.ITEM_STATE_CHANGED, "ITEM_STATE_CHANGED", "ItemEvent", "StateChanged"),

		// TextEvent
		TEXT_VALUE_CHANGED(TextEvent.TEXT_VALUE_CHANGED, "TEXT_VALUE_CHANGED", "TextEvent", "ValueChanged"),

		// InputMethodEvent
		INPUT_METHOD_TEXT_CHANGED(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, "INPUT_METHOD_TEXT_CHANGED", "InputMethodEvent", "TextChanged"),
		CARET_POSITION_CHANGED(InputMethodEvent.CARET_POSITION_CHANGED, "CARET_POSITION_CHANGED", "InputMethodEvent", "CaretPosChanged"),

		// PaintEvent ?? 
		PAINT_FIRST(PaintEvent.PAINT_FIRST, "PAINT_FIRST", "PaintEvent", "PaintFirst"),
		PAINT_LAST(PaintEvent.PAINT_LAST, "PAINT_LAST", "PaintEvent", "PaintLast"),
		
		// InvocationEvent, INVOCATION_EVENT_MASK 
		INVOCATION_DEFAULT(InvocationEvent.INVOCATION_DEFAULT, "INVOCATION_DEFAULT", "InvocationEvent", "Default"),

		// HierarchyEvent, HIERARCHY_EVENT_MASK
		ANCESTOR_MOVED(HierarchyEvent.ANCESTOR_MOVED, "ANCESTOR_MOVED", "HierarchyEvent", "Moved"),
		ANCESTOR_RESIZED(HierarchyEvent.ANCESTOR_RESIZED, "ANCESTOR_RESIZED", "HierarchyEvent", "Resized"),
		HIERARCHY_CHANGED(HierarchyEvent.HIERARCHY_CHANGED, "HIERARCHY_CHANGED", "HierarchyEvent", "Changed"),
		
		UNKNOWN_EVENT(-1,"UNKNOWN", "", "");
		
		private final long id;
		private final String eventName;
		private final String eventClassName;
		private final String eventMethodName;
		private AWTEventGroupInfo eventGroup; // cf inverse relationship, initialization next in AWTEventGroupInfo enum ctor 
		
		private AWTEventInfo(long id, String eventName, String eventClassName, String eventMethodName) {
			this.id = id;
			this.eventName = eventName;
			this.eventClassName = eventClassName;
			this.eventMethodName = eventMethodName;
		}

		public long getId() {
			return id;
		}

		public String getEventName() {
			return eventName;
		}

		public String getEventClassName() {
			return eventClassName;
		}

		public String getEventMethodName() {
			return eventMethodName;
		}

		public AWTEventGroupInfo getEventGroup() {
			return eventGroup;
		}
		
	}
	
	/**
	 * info for group of AWTEvent, to be enabled/disabled together
	 */
	public static class AWTEventGroupInfo {
		public static final AWTEventGroupInfo UNKNOWN = new AWTEventGroupInfo("unknown", false);
		
		private final String groupName;
		private boolean defaultSelected;
		private final AWTEventInfo[] eventInfos;
		
		AWTEventGroupInfo(String groupName, boolean defaultSelected, AWTEventInfo... eventInfos) {
			 this.groupName = groupName;
			 this.defaultSelected = defaultSelected;
			 this.eventInfos = eventInfos;
			 if (eventInfos != null) {
				 for (AWTEventInfo e : eventInfos) {
					 e.eventGroup = this;
				 }
			 }
		}

		
		public String getGroupName() {
			return groupName;
		}
		public boolean getDefaultSelected() {
			return defaultSelected;
		}
		public AWTEventInfo[] getEventInfos() {
			return eventInfos;
		}
	}
	
	/**
	 * info for AWTEvent MASK
	 */
	public static enum AWTEventMaskInfo {

		COMPONENT_EVENT_MASK(AWTEvent.COMPONENT_EVENT_MASK, "COMPONENT_EVENT_MASK", "ComponentEvent", //
				new AWTEventGroupInfo("Shown-Hiden", true, AWTEventInfo.COMPONENT_SHOWN, AWTEventInfo.COMPONENT_HIDDEN),
				new AWTEventGroupInfo("Moved-Resized", false, AWTEventInfo.COMPONENT_MOVED, AWTEventInfo.COMPONENT_RESIZED)),
		CONTAINER_EVENT_MASK(AWTEvent.CONTAINER_EVENT_MASK, "CONTAINER_EVENT_MASK", "ContainerEvent", //
				new AWTEventGroupInfo("Added-Removed", false, AWTEventInfo.COMPONENT_ADDED, AWTEventInfo.COMPONENT_REMOVED)),
		FOCUS_EVENT_MASK(AWTEvent.FOCUS_EVENT_MASK, "FOCUS_EVENT_MASK", "FocusEvent", //
				new AWTEventGroupInfo("Focus Gained-Lost", false, AWTEventInfo.FOCUS_GAINED, AWTEventInfo.FOCUS_LOST)),
		KEY_EVENT_MASK(AWTEvent.KEY_EVENT_MASK, "KEY_EVENT_MASK", "KeyEvent", //
				new AWTEventGroupInfo("Pressed-Released", false, AWTEventInfo.KEY_PRESSED, AWTEventInfo.KEY_RELEASED),
				new AWTEventGroupInfo("Typed", true, AWTEventInfo.KEY_TYPED)),
		MOUSE_EVENT_MASK(AWTEvent.MOUSE_EVENT_MASK, "MOUSE_EVENT_MASK", "MouseEvent", //
				new AWTEventGroupInfo("Pressed-Released", false, AWTEventInfo.MOUSE_PRESSED, AWTEventInfo.MOUSE_RELEASED),
				new AWTEventGroupInfo("Clicked", true, AWTEventInfo.MOUSE_CLICKED),
				new AWTEventGroupInfo("Moved", false, AWTEventInfo.MOUSE_MOVED),
				new AWTEventGroupInfo("Dragged", true, AWTEventInfo.MOUSE_DRAGGED),
				new AWTEventGroupInfo("Wheel", false, AWTEventInfo.MOUSE_WHEEL),
				new AWTEventGroupInfo("Entered-Leaved", false, AWTEventInfo.MOUSE_ENTERED, AWTEventInfo.MOUSE_EXITED)
		),
		MOUSE_MOTION_EVENT_MASK(AWTEvent.MOUSE_MOTION_EVENT_MASK, "MOUSE_MOTION_EVENT_MASK", "MouseEvent" //
				),
		WINDOW_EVENT_MASK(AWTEvent.WINDOW_EVENT_MASK, "WINDOW_EVENT_MASK", "WindowEvent", //
				new AWTEventGroupInfo("Opened-Closed", true, AWTEventInfo.WINDOW_OPENED, AWTEventInfo.WINDOW_CLOSING, AWTEventInfo.WINDOW_CLOSED),
				new AWTEventGroupInfo("Iconified-Deiconified", false, AWTEventInfo.WINDOW_ICONIFIED, AWTEventInfo.WINDOW_DEICONIFIED), 
				new AWTEventGroupInfo("Activated-Deiconified", false, AWTEventInfo.WINDOW_ACTIVATED, AWTEventInfo.WINDOW_DEACTIVATED),
				new AWTEventGroupInfo("Focus Gained-Lost", false, AWTEventInfo.WINDOW_GAINED_FOCUS, AWTEventInfo.WINDOW_LOST_FOCUS), 
				new AWTEventGroupInfo("State Changed", false, AWTEventInfo.WINDOW_STATE_CHANGED)
		),
		ACTION_EVENT_MASK(AWTEvent.ACTION_EVENT_MASK, "ACTION_EVENT_MASK", "ActionEvent", //
				new AWTEventGroupInfo("ActionPerformed", true, AWTEventInfo.ACTION_PERFORMED)
		),
		ADJUSTMENT_EVENT_MASK(AWTEvent.ADJUSTMENT_EVENT_MASK, "ADJUSTMENT_EVENT_MASK", "AdjustmentEvent", //
				new AWTEventGroupInfo("ValuedChanged", true, AWTEventInfo.ADJUSTMENT_VALUE_CHANGED)
		),
		ITEM_EVENT_MASK(AWTEvent.ITEM_EVENT_MASK, "ITEM_EVENT_MASK", "ItemEvent", //
				new AWTEventGroupInfo("ItemStateChanged", true, AWTEventInfo.ITEM_STATE_CHANGED)),
		TEXT_EVENT_MASK(AWTEvent.TEXT_EVENT_MASK, "TEXT_EVENT_MASK", "TextEvent", //
				new AWTEventGroupInfo("TextValueChanged", true, AWTEventInfo.TEXT_VALUE_CHANGED)),
		INPUT_METHOD_EVENT_MASK(AWTEvent.INPUT_METHOD_EVENT_MASK, "INPUT_METHOD_EVENT_MASK", "InputMethodEvent", //
				new AWTEventGroupInfo("TextChanged", true, AWTEventInfo.INPUT_METHOD_TEXT_CHANGED, AWTEventInfo.CARET_POSITION_CHANGED)),
		PAINT_EVENT_MASK(AWTEvent.PAINT_EVENT_MASK, "PAINT_EVENT_MASK", "PainEvent", //
				new AWTEventGroupInfo("PaintEvent", false, AWTEventInfo.PAINT_FIRST, AWTEventInfo.PAINT_LAST)), 
		INVOCATION_EVENT_MASK(AWTEvent.INVOCATION_EVENT_MASK, "INVOCATION_EVENT_MASK", "InvocationEvent", //
				new AWTEventGroupInfo("Invocation", true, AWTEventInfo.INVOCATION_DEFAULT)),
		HIERARCHY_EVENT_MASK(AWTEvent.HIERARCHY_EVENT_MASK, "HIERARCHY_EVENT_MASK", "HierarchyEvent",
				new AWTEventGroupInfo("Ancestor Moved-Resized", false, AWTEventInfo.ANCESTOR_MOVED, AWTEventInfo.ANCESTOR_RESIZED, AWTEventInfo.HIERARCHY_CHANGED)),
		HIERARCHY_BOUNDS_EVENT_MASK(AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK, "HIERARCHY_BOUNDS_EVENT_MASK", "HierarchyEvent", //
				new AWTEventGroupInfo("Hierarchy Changed", false, AWTEventInfo.HIERARCHY_CHANGED)),
		MOUSE_WHEEL_EVENT_MASK(AWTEvent.MOUSE_WHEEL_EVENT_MASK, "MOUSE_WHEEL_EVENT_MASK", "MouseEvent" //
				),
		WINDOW_STATE_EVENT_MASK(AWTEvent.WINDOW_STATE_EVENT_MASK, "WINDOW_STATE_EVENT_MASK", "WindowEvent" //
				),
		WINDOW_FOCUS_EVENT_MASK(AWTEvent.WINDOW_FOCUS_EVENT_MASK, "WINDOW_FOCUS_EVENT_MASK", "WindowEvent" // 
				);


		private final long flag;
		private final String flagName;
		private final String eventClassName;
		private final AWTEventGroupInfo[] eventGroups;
		
		AWTEventMaskInfo(long flag, String flagName, String eventClassName, AWTEventGroupInfo... eventGroups) {
			 this.flag = flag;
			 this.flagName = flagName;
			 this.eventClassName = eventClassName;
			 this.eventGroups = eventGroups;
		}
		
		public long getFlag() {
			return flag;
		}
		public String getFlagName() {
			return flagName;
		}
		public String getEventClassName() {
			return eventClassName;
		}
		public AWTEventGroupInfo[] getEventGroups() {
			return eventGroups;
		}
		
		public boolean getDefaultSelected() {
			boolean res = false;
			if (eventGroups != null) {
				for (AWTEventGroupInfo g : eventGroups) {
					if (g.getDefaultSelected()) {
						res = true;
						break;
					}
				}
			}
			return res;
		}
	}
	
	
	
}
