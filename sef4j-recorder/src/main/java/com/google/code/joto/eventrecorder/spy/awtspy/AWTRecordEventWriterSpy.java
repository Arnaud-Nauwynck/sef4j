package com.google.code.joto.eventrecorder.spy.awtspy;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.AWTEventListener;
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
import java.awt.event.TextEvent;
import java.awt.event.WindowEvent;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.joto.eventrecorder.RecordEventSummary;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTEventInfos.AWTEventGroupInfo;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTEventInfos.AWTEventInfo;
import com.google.code.joto.eventrecorder.spy.awtspy.AWTEventInfos.AWTEventMaskInfo;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 * a spy to listen AWT event, and write them as RecordEventSummary 
 * into a RecordEventWriter
 * 
 */
public class AWTRecordEventWriterSpy {

	private static Logger log = LoggerFactory.getLogger(AWTRecordEventWriterSpy.class);

	public static final String MARKER_KEY_IGNORE_AWTEVENT_SPY = "MARKER_KEY_IGNORE_AWTEVENT_SPY"; 

	
	protected RecordEventWriter eventWriter;

	protected boolean enable = false;

	protected AWTEventListener innerAWTEventListener;

	protected long awtEventMask = 0; 

	// sub-events enable/disable flags, per group of eventTypes (or per eventIDs)
	
	private static class AWTEventGroupFlag {
		private boolean enable = true;
		
	}
	private Map<AWTEventGroupInfo,AWTEventGroupFlag> eventGroupFlags = new HashMap<AWTEventGroupInfo,AWTEventGroupFlag>();
	
	
	// ------------------------------------------------------------------------

	public AWTRecordEventWriterSpy(RecordEventWriter eventWriter) {
		this.eventWriter = eventWriter;
		this.innerAWTEventListener = new AWTEventListener() {
			@Override
			public void eventDispatched(AWTEvent event) {
				onAWTEventDispatched(event);
			}
		};
		
		enable = false;
		for (AWTEventMaskInfo maskInfo : AWTEventMaskInfo.values()) {
			setAwtEventMaskFlag(maskInfo, maskInfo.getDefaultSelected());
			
			for (AWTEventGroupInfo eventGroup : maskInfo.getEventGroups()) {
				setEventGroupFlag(eventGroup, eventGroup.getDefaultSelected());
			}
		}
	}

	// ------------------------------------------------------------------------

	public static void setIgnoreComponentAwtEventSpy(JComponent comp) {
		comp.putClientProperty(MARKER_KEY_IGNORE_AWTEVENT_SPY, Boolean.TRUE);
	}

	public static void clearIgnoreComponentAwtEventSpy(JComponent comp) {
		comp.putClientProperty(MARKER_KEY_IGNORE_AWTEVENT_SPY, null);
	}

	public static boolean isSetIgnoreComponentAwtEventSpy(Component comp) {
		if (comp instanceof JComponent) {
			return ((JComponent) comp).getClientProperty(MARKER_KEY_IGNORE_AWTEVENT_SPY) != null;
		} else {
			return false; //?? no marker on old AWT comp?
		}
	}
	
	public static boolean isIgnoreComponentAwtEventSpyHierarchy(Component comp) {
		boolean res = false;
		if (isSetIgnoreComponentAwtEventSpy(comp)) {
			res = true;
		} else { // also find marker in ancestor hierarchy
			for(Component p = comp; p != null; p = p.getParent()) {
				if (isSetIgnoreComponentAwtEventSpy(p)) {
					res = true;
					break;
				}
			}
		}
		return res;
	}
	
	
	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean p) {
		if (p != enable) {
			if (enable && (awtEventMask != 0)) {
				uninstallAWTEventListener();
			}
			this.enable = p;
			if (enable && (awtEventMask != 0)) {
				installAWTEventListener();
			}
		}
	}
	
	public long getAwtEventMask() {
		return awtEventMask;
	}

	public void setAwtEventMask(long p) {
		if (p != awtEventMask) {
			if (enable && (awtEventMask != 0)) {
				uninstallAWTEventListener();
			}
			this.awtEventMask = p;
			if (enable && (awtEventMask != 0)) {
				installAWTEventListener();
			}
		}
	}

	public boolean getAwtEventMaskFlag(AWTEventMaskInfo awtEventMaskInfo) {
		return 0 != (awtEventMask & awtEventMaskInfo.getFlag());
	}

	public void setAwtEventMaskFlag(AWTEventMaskInfo awtEventMaskInfo, boolean p) {
		long newMask = clearOrSetFlag(awtEventMask, awtEventMaskInfo.getFlag(), p);
		setAwtEventMask(newMask);
	}

	public static long clearOrSetFlag(long mask, long flag, boolean isSetFlag) {
		long res = (isSetFlag)? (mask | flag) : (mask & (~flag));
		return res;
	}
	
	
	public static String maskToString(long mask) {
		StringBuilder sb = new StringBuilder();
		for (AWTEventMaskInfo flagInfo : AWTEventMaskInfo.values()) {
			if (0 != (mask & flagInfo.getFlag())) {
				sb.append(flagInfo.getFlagName() + " |");
			}
		}
		if (sb.length() != 0) {
			sb.delete(sb.length()-2, sb.length());
		}
		return sb.toString();
	}
	
	public boolean getEventGroupFlag(AWTEventGroupInfo key) {
		AWTEventGroupFlag tmp = getOrCreateEventGroupFlag(key);
		return tmp.enable;
	}

	public void setEventGroupFlag(AWTEventGroupInfo key, boolean enable) {
		AWTEventGroupFlag tmp = getOrCreateEventGroupFlag(key);
		tmp.enable = enable;
	}

	private AWTEventGroupFlag getOrCreateEventGroupFlag(AWTEventGroupInfo key) {
		AWTEventGroupFlag tmp = eventGroupFlags.get(key);
		if (tmp == null) {
			tmp = new AWTEventGroupFlag();
			tmp.enable = key.getDefaultSelected();
			eventGroupFlags.put(key, tmp);
		}
		return tmp;
	}

	private void installAWTEventListener() {
		log.info("add AWTEventListener for joto event writer: mask=" + awtEventMask + " (" + maskToString(awtEventMask) + ")");
		try {
			java.awt.Toolkit.getDefaultToolkit().addAWTEventListener(
					innerAWTEventListener, awtEventMask);
		} catch (SecurityException ex) {
			log.error("Failed to add AWTEventListener ... ignore!", ex);
		}
	}

	private void uninstallAWTEventListener() {
		log.info("remove AWTEventListener for joto event writer");
		java.awt.Toolkit.getDefaultToolkit().removeAWTEventListener(
				innerAWTEventListener);
	}

	private void onAWTEventDispatched(AWTEvent event) {
		if (!enable) {
			return;
		}
		Object eventSource = event.getSource();
		if (eventSource != null && (eventSource instanceof Component)) {
			if (isIgnoreComponentAwtEventSpyHierarchy((Component) eventSource)) {
				return;
			}
		}
		
		AWTEventInfo eventInfo;
		String eventMethodDetail = null;
		Serializable eventData = null;
		
		switch (event.getID()) {

		// ComponentEvent
		case ComponentEvent.COMPONENT_SHOWN:
			eventInfo = AWTEventInfo.COMPONENT_SHOWN;
			break;
		case ComponentEvent.COMPONENT_HIDDEN:
			eventInfo = AWTEventInfo.COMPONENT_HIDDEN;
			break;
		case ComponentEvent.COMPONENT_MOVED:
			eventInfo = AWTEventInfo.COMPONENT_MOVED;
			break;
		case ComponentEvent.COMPONENT_RESIZED:
			eventInfo = AWTEventInfo.COMPONENT_RESIZED;
			break;

		// ContainerEvent
        case ContainerEvent.COMPONENT_ADDED:
			eventInfo = AWTEventInfo.COMPONENT_ADDED;
            break;
        case ContainerEvent.COMPONENT_REMOVED:
			eventInfo = AWTEventInfo.COMPONENT_REMOVED;
            break;

        // FocusEvent 
        case FocusEvent.FOCUS_GAINED:
			eventInfo = AWTEventInfo.FOCUS_GAINED;
    		// eventMethodDetail = (event.temporary ? ",temporary" : ",permanent") + ",opposite=" + event.getOppositeComponent();
            break;
        case FocusEvent.FOCUS_LOST:
        	eventInfo = AWTEventInfo.FOCUS_LOST;
    		// eventMethodDetail = (event.temporary ? ",temporary" : ",permanent") + ",opposite=" + event.getOppositeComponent();
            break;
	
		// KeyEvent
        case KeyEvent.KEY_PRESSED:
        case KeyEvent.KEY_RELEASED:
        case KeyEvent.KEY_TYPED: {
        	KeyEvent e = (KeyEvent) event;
        	
        	switch(event.getID()) {
	        case KeyEvent.KEY_PRESSED: {
	        	eventInfo = AWTEventInfo.KEY_PRESSED;
			} break;
			case KeyEvent.KEY_RELEASED: {
	        	eventInfo = AWTEventInfo.KEY_RELEASED;
			} break;
			case KeyEvent.KEY_TYPED: {
	        	eventInfo = AWTEventInfo.KEY_TYPED;
			} break;
			default:
				eventInfo = null; // for compiler, can not occur
        	}

        	eventMethodDetail = "";
            if (e.getModifiers() != 0) {
            	eventMethodDetail += KeyEvent.getKeyModifiersText(e.getModifiers()) + " ";
            }
            if (e.getModifiersEx() != 0) {
            	eventMethodDetail += KeyEvent.getModifiersExText(e.getModifiers()) + " ";
            }
            String keyCharStr = keyCharToString(e.getKeyChar());
            if (keyCharStr != null) {
            	eventMethodDetail += keyCharStr;
            }
            if (e.getKeyCode() != 0) {
            	eventMethodDetail += " (keyCode=" + Integer.toString(e.getKeyCode()) + ")";
            }
        	String keyText = KeyEvent.getKeyText(e.getKeyCode());
        	if (keyText.indexOf("unknown") != -1) {
        		eventMethodDetail += " " + keyText; 
        	}
        	
		} break;

		// MouseEvent 
		case MouseEvent.MOUSE_PRESSED:
        	eventInfo = AWTEventInfo.MOUSE_PRESSED;
			break;
		case MouseEvent.MOUSE_RELEASED:
        	eventInfo = AWTEventInfo.MOUSE_RELEASED;
			break;
		case MouseEvent.MOUSE_CLICKED:
        	eventInfo = AWTEventInfo.MOUSE_CLICKED;
			break;
		case MouseEvent.MOUSE_MOVED:
        	eventInfo = AWTEventInfo.MOUSE_MOVED;
			break;
		case MouseEvent.MOUSE_DRAGGED:
        	eventInfo = AWTEventInfo.MOUSE_DRAGGED;
        	break;
		case MouseEvent.MOUSE_ENTERED:
        	eventInfo = AWTEventInfo.MOUSE_ENTERED;
			break;
		case MouseEvent.MOUSE_EXITED:
        	eventInfo = AWTEventInfo.MOUSE_EXITED;
			break;
		case MouseEvent.MOUSE_WHEEL: // cf also MouseWheelEvent
        	eventInfo = AWTEventInfo.MOUSE_WHEEL;
			break;

		// WindowEvent
        case WindowEvent.WINDOW_OPENED:
        	eventInfo = AWTEventInfo.WINDOW_OPENED;
            break;
        case WindowEvent.WINDOW_CLOSING:
        	eventInfo = AWTEventInfo.WINDOW_CLOSING;
            break;
        case WindowEvent.WINDOW_CLOSED:
        	eventInfo = AWTEventInfo.WINDOW_CLOSED;
            break;
        case WindowEvent.WINDOW_ICONIFIED:
        	eventInfo = AWTEventInfo.WINDOW_ICONIFIED;
            break;
        case WindowEvent.WINDOW_DEICONIFIED:
        	eventInfo = AWTEventInfo.WINDOW_DEICONIFIED;
            break;
        case WindowEvent.WINDOW_ACTIVATED:
        	eventInfo = AWTEventInfo.WINDOW_ACTIVATED;
            break;
        case WindowEvent.WINDOW_DEACTIVATED:
        	eventInfo = AWTEventInfo.WINDOW_DEACTIVATED;
            break;
        case WindowEvent.WINDOW_GAINED_FOCUS:
        	eventInfo = AWTEventInfo.WINDOW_GAINED_FOCUS;
        	break;
        case WindowEvent.WINDOW_LOST_FOCUS:
        	eventInfo = AWTEventInfo.WINDOW_LOST_FOCUS;
        	break;
        case WindowEvent.WINDOW_STATE_CHANGED:
        	eventInfo = AWTEventInfo.WINDOW_STATE_CHANGED;
        	break;

        // ActionEvent
        case ActionEvent.ACTION_PERFORMED:
        	eventInfo = AWTEventInfo.ACTION_PERFORMED;
            break;

        // AdjustmentEvent
        case AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED: {
        	eventInfo = AWTEventInfo.ADJUSTMENT_VALUE_CHANGED;
			AdjustmentEvent e = (AdjustmentEvent) event;
		    String adjTypeStr;
		    switch(e.getAdjustmentType()) {
		    case AdjustmentEvent.UNIT_INCREMENT:
		    	adjTypeStr = "UNIT_INCREMENT";
		    	break;
		    case AdjustmentEvent.UNIT_DECREMENT:
		    	adjTypeStr = "UNIT_DECREMENT";
		    	break;
		    case AdjustmentEvent.BLOCK_INCREMENT:
		    	adjTypeStr = "BLOCK_INCREMENT";
		    	break;
		    case AdjustmentEvent.BLOCK_DECREMENT:
		    	adjTypeStr = "BLOCK_DECREMENT";
		    	break;
		    case AdjustmentEvent.TRACK:
		    	adjTypeStr = "TRACK";
		    	break;
		    default:
		    	adjTypeStr = "unknown type";
		    }
		    eventMethodDetail = "adjType="+adjTypeStr
			    + ",value=" + e.getValue()
			    + ",isAdjusting="+e.getValueIsAdjusting();
        } break;
      
        // ItemEvent
        case ItemEvent.ITEM_STATE_CHANGED: {
        	eventInfo = AWTEventInfo.ITEM_STATE_CHANGED;
			ItemEvent e = (ItemEvent) event;
	        String stateStr;
	        switch(e.getStateChange()) {
	          case ItemEvent.SELECTED:
	              stateStr = "SELECTED";
	              break;
	          case ItemEvent.DESELECTED:
	              stateStr = "DESELECTED";
	              break;
	          default:
	              stateStr = "unknown type";
	        }
	        eventMethodDetail = "item="+e.getItem() + ",stateChange="+stateStr;
        } break;

        // TextEvent
        case TextEvent.TEXT_VALUE_CHANGED: {
        	eventInfo = AWTEventInfo.TEXT_VALUE_CHANGED;
        	// TextEvent e = (TextEvent) event;
        	// eventMethodDetail = e.paramString();
        } break;
            
		// InputMethodEvent
		case InputMethodEvent.INPUT_METHOD_TEXT_CHANGED: {
			eventInfo = AWTEventInfo.INPUT_METHOD_TEXT_CHANGED;
			InputMethodEvent e = (InputMethodEvent) event;
        	eventMethodDetail = e.paramString();
		} break;
		case InputMethodEvent.CARET_POSITION_CHANGED: {
			eventInfo = AWTEventInfo.CARET_POSITION_CHANGED;
			InputMethodEvent e = (InputMethodEvent) event;
        	eventMethodDetail = e.paramString();
		} break;

		// PaintEvent ?? 
		
		// InvocationEvent, INVOCATION_EVENT_MASK 
        case InvocationEvent.INVOCATION_DEFAULT: {
        	eventInfo = AWTEventInfo.INVOCATION_DEFAULT;
        	InvocationEvent e = (InvocationEvent) event;
        	eventMethodDetail = e.paramString();
        		// = typeStr + ",runnable=" + runnable + ",notifier=" + notifier + ",catchExceptions=" + catchExceptions + ",when=" + when;

        } break;
	        
		// HierarchyEvent, HIERARCHY_EVENT_MASK
   	  	case HierarchyEvent.HIERARCHY_CHANGED:
   		case HierarchyEvent.ANCESTOR_MOVED:
   		case HierarchyEvent.ANCESTOR_RESIZED:
  	   {
	  		HierarchyEvent e = (HierarchyEvent) event;
	  		Container changedParent = e.getChangedParent();
	  		if (isIgnoreComponentAwtEventSpyHierarchy(changedParent)) {
	  			return;
	  		}
	  		Component changedComp = e.getComponent();
	  		if (isIgnoreComponentAwtEventSpyHierarchy(changedComp)) {
	  			return;
	  		}

	  		switch(event.getID()) {
	  		case HierarchyEvent.ANCESTOR_MOVED:
	  			eventInfo = AWTEventInfo.ANCESTOR_MOVED;
	  			// "("+changed+","+changedParent+")";
	  			break;
	  		case HierarchyEvent.HIERARCHY_CHANGED:
	  			eventInfo = AWTEventInfo.HIERARCHY_CHANGED;
	  			break;
	  		case HierarchyEvent.ANCESTOR_RESIZED:
	  			eventInfo = AWTEventInfo.ANCESTOR_RESIZED;
	  			// ("+changed+","+changedParent+")";
	  			break;
	  		default:
	  			eventInfo = null; // can not occur
	  		}
  	  } break;
  	  
  	  // HIERARCHY_BOUNDS_EVENT_MASK ??
  	  
  	  // MouseWheelEvent, MOUSE_WHEEL_EVENT_MASK ... cf MouseEvent
  	  
  	  // WINDOW_STATE_EVENT_MASK ... cf WindowEvent
  	  
  	  // WINDOW_FOCUS_EVENT_MASK .. cf WindowEvent
  	  
  	  
  	  default:
  		  eventInfo = AWTEventInfo.UNKNOWN_EVENT;
		}

		AWTEventGroupInfo eventGroupInfo = (eventInfo != null)? eventInfo.getEventGroup() : AWTEventGroupInfo.UNKNOWN;
		if (eventGroupInfo == null) { // SHOULD NOT OCCUR
			eventGroupInfo = AWTEventGroupInfo.UNKNOWN;
		}
		boolean enableEvent = getEventGroupFlag(eventGroupInfo);
		if (!enableEvent) {
			return;
		}
		
		if (enableEvent) {
			String typeStr;
			String eventClassName;
			String eventMethodName;
			
			if (eventInfo != AWTEventInfo.UNKNOWN_EVENT) {
				typeStr = eventInfo.getEventName();
				eventClassName = eventInfo.getEventClassName();
				eventMethodName = eventInfo.getEventMethodName();
			} else {
	  		  	enableEvent = true;
				typeStr = "ID:" + event.getID();
				eventClassName = null;
				eventMethodName = null;
			}

			if (eventMethodDetail == null) {
				eventMethodDetail = event.paramString(); // default built-in <<toString>> from AWT 
			}
			
			RecordEventSummary recordEvent = new RecordEventSummary();
			recordEvent.setEventType("AWTSpy");
			recordEvent.setEventSubType(typeStr);
			recordEvent.setEventDate(new Date());
			recordEvent.setThreadName(Thread.currentThread().getName()); // should be the EDT ...
			recordEvent.setEventClassName(eventClassName);
			recordEvent.setEventMethodName(eventMethodName);
			recordEvent.setEventMethodDetail(eventMethodDetail);
			
			try {
				eventWriter.addEvent(recordEvent, eventData, null);
			} catch(Exception ex) {
				log.warn("should not occur.. ignore", ex);
			}
		}
	}

	
	private static String keyCharToString(char keyChar) {
        String res;
		switch (keyChar) {
		case '\b': res = KeyEvent.getKeyText(KeyEvent.VK_BACK_SPACE); break;
		case '\t': res = KeyEvent.getKeyText(KeyEvent.VK_TAB); break;
		case '\n': res = KeyEvent.getKeyText(KeyEvent.VK_ENTER); break;
		case '\u0018': res = KeyEvent.getKeyText(KeyEvent.VK_CANCEL); break;
		case '\u001b': res = KeyEvent.getKeyText(KeyEvent.VK_ESCAPE); break;
		case '\u007f': res = KeyEvent.getKeyText(KeyEvent.VK_DELETE); break;
		case KeyEvent.CHAR_UNDEFINED: res = null; break; // undefined...
		default:
			res = Character.toString(keyChar);
			break;
		}
		return res;
	}
}
