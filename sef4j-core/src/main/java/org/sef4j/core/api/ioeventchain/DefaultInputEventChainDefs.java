package org.sef4j.core.api.ioeventchain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.helpers.tasks.PeriodicityDef;
import org.sef4j.core.helpers.tasks.TaskDef;


/**
 * default class implementations for describing different input even chain types
 * 
 * <ul>
 * <li>FilteredInputEventChainDef</li>
 * <li>TransformedInputEventChainDef</li>
 * <li>UnionInputEventChainDef</li>
 * <li>MultiplexedInputEventChainDef</li>
 * <li>NamedDelegateInputEventChainDef</li>
 * <li>NamedInputEventChainDef</li>
 * <li>PeriodicTaskInputEventChainDef</li>
 * <li>SubSamplingPeriodicTaskInputEventChainDef</li>
 * <li>PropTreePathInputEventChainDef</li>
 * </ul>
 * 
 */
public final class DefaultInputEventChainDefs {

	/* private to force all static */
	private DefaultInputEventChainDefs() {}
	
	// ------------------------------------------------------------------------
	
	/**
	 * value-object for describing <code>FilteredEventSender(EventSender(..))<code>
	 */
	public static class FilteredInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;

		private final EventFilterDef filterDef;

		private final InputEventChainDef underlying;

		public FilteredInputEventChainDef(EventFilterDef filterDef, InputEventChainDef underlying) {
			this.filterDef = filterDef;
			this.underlying = underlying;
		}

		public EventFilterDef getFilterDef() {
			return filterDef;
		}

		public InputEventChainDef getUnderlying() {
			return underlying;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((filterDef == null) ? 0 : filterDef.hashCode());
			result = prime * result + ((underlying == null) ? 0 : underlying.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FilteredInputEventChainDef other = (FilteredInputEventChainDef) obj;
			if (filterDef == null) {
				if (other.filterDef != null)
					return false;
			} else if (!filterDef.equals(other.filterDef))
				return false;
			if (underlying == null) {
				if (other.underlying != null)
					return false;
			} else if (!underlying.equals(other.underlying))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "FitleredInputEventChainDef [filterDef=" + filterDef + ", underlying=" + underlying + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class TransformedInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;

		private final EventTransformerDef eventTransformerDef;

		private final InputEventChainDef underlying;

		public TransformedInputEventChainDef(EventTransformerDef eventTransformerDef, InputEventChainDef underlying) {
			this.eventTransformerDef = eventTransformerDef;
			this.underlying = underlying;
		}

		public EventTransformerDef getEventTransformerDef() {
			return eventTransformerDef;
		}

		public InputEventChainDef getUnderlying() {
			return underlying;
		}

		@Override
		public String toString() {
			return "TransformedInputEventChainDef [eventTransformerDef=" + eventTransformerDef + ", underlying=" + underlying + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((eventTransformerDef == null) ? 0 : eventTransformerDef.hashCode());
			result = prime * result + ((underlying == null) ? 0 : underlying.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TransformedInputEventChainDef other = (TransformedInputEventChainDef) obj;
			if (eventTransformerDef == null) {
				if (other.eventTransformerDef != null)
					return false;
			} else if (!eventTransformerDef.equals(other.eventTransformerDef))
				return false;
			if (underlying == null) {
				if (other.underlying != null)
					return false;
			} else if (!underlying.equals(other.underlying))
				return false;
			return true;
		}

		
	}

	
	// ------------------------------------------------------------------------
	
	public static class UnionInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final InputEventChainDef[] elements;

		public UnionInputEventChainDef(Collection<InputEventChainDef> elements) {
			this.elements = elements.toArray(new InputEventChainDef[elements.size()]);
		}

		public InputEventChainDef[] getElements() {
			return elements;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(elements);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			UnionInputEventChainDef other = (UnionInputEventChainDef) obj;
			if (!Arrays.equals(elements, other.elements))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "UnionInputEventChainDef [elements=" + Arrays.toString(elements) + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class MultiplexedInputEventChainDef<K> extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
	
		private final Map<K,InputEventChainDef> inputs;

		public MultiplexedInputEventChainDef(Map<K, InputEventChainDef> inputs) {
			this.inputs = Collections.unmodifiableMap(new HashMap<K, InputEventChainDef>(inputs));
		}

		public Map<K, InputEventChainDef> getInputs() {
			return inputs;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((inputs == null) ? 0 : inputs.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			MultiplexedInputEventChainDef<K> other = (MultiplexedInputEventChainDef<K>) obj;
			if (inputs == null) {
				if (other.inputs != null)
					return false;
			} else if (!inputs.equals(other.inputs))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MultiplexedInputEventChainDef [inputs=" + inputs + "]";
		}
		
		
	}
	
	
	// ------------------------------------------------------------------------
	
	public static class NamedDelegateInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String externalSystem;
		private final InputEventChainDef underlying;

		public NamedDelegateInputEventChainDef(String externalSystem, InputEventChainDef underlying) {
			this.externalSystem = externalSystem;
			this.underlying = underlying;
		}
		
		public String getExternalSystem() {
			return externalSystem;
		}

		public InputEventChainDef getUnderlying() {
			return underlying;
		}

		@Override
		public String toString() {
			return "ExternalInputEventChainDef[" + externalSystem + " : " + underlying + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((externalSystem == null) ? 0 : externalSystem.hashCode());
			result = prime * result + ((underlying == null) ? 0 : underlying.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NamedDelegateInputEventChainDef other = (NamedDelegateInputEventChainDef) obj;
			if (externalSystem == null) {
				if (other.externalSystem != null)
					return false;
			} else if (!externalSystem.equals(other.externalSystem))
				return false;
			if (underlying == null) {
				if (other.underlying != null)
					return false;
			} else if (!underlying.equals(other.underlying))
				return false;
			return true;
		}

		
	}
	
	// ------------------------------------------------------------------------
	
	public static class NamedInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String name;

		public NamedInputEventChainDef(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NamedInputEventChainDef other = (NamedInputEventChainDef) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "NamedInputEventChainDef[" + name + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class PeriodicTaskInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final PeriodicityDef periodicity;
		private final TaskDef taskDef;

		public PeriodicTaskInputEventChainDef(PeriodicityDef periodicity, TaskDef taskDef) {
			this.taskDef = taskDef;
			this.periodicity = periodicity;
		}

		public PeriodicityDef getPeriodicity() {
			return periodicity;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((taskDef == null) ? 0 : taskDef.hashCode());
			result = prime * result + ((periodicity == null) ? 0 : periodicity.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PeriodicTaskInputEventChainDef other = (PeriodicTaskInputEventChainDef) obj;
			if (taskDef == null) {
				if (other.taskDef != null)
					return false;
			} else if (!taskDef.equals(other.taskDef))
				return false;
			if (periodicity == null) {
				if (other.periodicity != null)
					return false;
			} else if (!periodicity.equals(other.periodicity))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "PeriodicTaskInputEventChainDef [" + taskDef + ", periodicity=" + periodicity + "]";
		}
		
	}	
	
	// ------------------------------------------------------------------------
	
	public static class SubSamplingPeriodicTaskInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final PeriodicTaskInputEventChainDef underlying;
		private final int subSampingFrequency;
		
		public SubSamplingPeriodicTaskInputEventChainDef(PeriodicTaskInputEventChainDef underlying, int subSampingFrequency) {
			this.underlying = underlying;
			this.subSampingFrequency = subSampingFrequency;
		}

		public PeriodicTaskInputEventChainDef getUnderlying() {
			return underlying;
		}

		public int getSubSampingFrequency() {
			return subSampingFrequency;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + subSampingFrequency;
			result = prime * result + ((underlying == null) ? 0 : underlying.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SubSamplingPeriodicTaskInputEventChainDef other = (SubSamplingPeriodicTaskInputEventChainDef) obj;
			if (subSampingFrequency != other.subSampingFrequency)
				return false;
			if (underlying == null) {
				if (other.underlying != null)
					return false;
			} else if (!underlying.equals(other.underlying))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "SubSamplingPeriodicTaskInputEventChainDef [underlying=" + underlying + ", subSampingFrequency=" + subSampingFrequency + "]";
		}
		
	}

	// ------------------------------------------------------------------------
	
	public static class PropTreePathInputEventChainDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String path;
		private final String propName;
		private final String markAndCompareAccessorName;
		
		public PropTreePathInputEventChainDef(String path, String propName,
				String markAndCompareAccessorName) {
			this.path = path;
			this.propName = propName;
			this.markAndCompareAccessorName = markAndCompareAccessorName;
		}
		
		public String getPath() {
			return path;
		}
		public String getPropName() {
			return propName;
		}
		public String getMarkAndCompareAccessorName() {
			return markAndCompareAccessorName;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((markAndCompareAccessorName == null) ? 0 : markAndCompareAccessorName.hashCode());
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			result = prime * result + ((propName == null) ? 0 : propName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PropTreePathInputEventChainDef other = (PropTreePathInputEventChainDef) obj;
			if (markAndCompareAccessorName == null) {
				if (other.markAndCompareAccessorName != null)
					return false;
			} else if (!markAndCompareAccessorName.equals(other.markAndCompareAccessorName))
				return false;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			if (propName == null) {
				if (other.propName != null)
					return false;
			} else if (!propName.equals(other.propName))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "PropTreePathInputEventChainDef [" 
					+ "path=" + path 
					+ ", propName=" + propName
					+ ", markAndCompareAccessorName=" + markAndCompareAccessorName 
					+ "]";
		}
		
	}
	
	// ------------------------------------------------------------------------

}
