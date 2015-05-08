package org.sef4j.core.api.ioeventchain;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sef4j.core.helpers.tasks.PeriodicityDef;
import org.sef4j.core.helpers.tasks.TaskDef;


/**
 * default class implementations for describing different Output even chain types
 * 
 * <ul>
 * <li>FilteredOutputEventChainDef</li>
 * <li>TransformedOutputEventChainDef</li>
 * <li>UnionOutputEventChainDef</li>
 * <li>MultiplexedOutputEventChainDef</li>
 * <li>NamedDelegateOutputEventChainDef</li>
 * <li>NamedOutputEventChainDef</li>
 * <li>PeriodicTaskOutputEventChainDef</li>
 * <li>SubSamplingPeriodicTaskOutputEventChainDef</li>
 * <li>PropTreePathOutputEventChainDef</li>
 * </ul>
 * 
 */
public final class DefaultOutputEventChainDefs {

	/* private to force all static */
	private DefaultOutputEventChainDefs() {}
	
	// ------------------------------------------------------------------------
	
	/**
	 * value-object for describing <code>FilteredEventSender(EventSender(..))<code>
	 */
	public static class FilteredOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;

		private final EventFilterDef filterDef;

		private final OutputEventChainDef underlying;

		public FilteredOutputEventChainDef(EventFilterDef filterDef, OutputEventChainDef underlying) {
			this.filterDef = filterDef;
			this.underlying = underlying;
		}

		public EventFilterDef getFilterDef() {
			return filterDef;
		}

		public OutputEventChainDef getUnderlying() {
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
			FilteredOutputEventChainDef other = (FilteredOutputEventChainDef) obj;
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
			return "FitleredOutputEventChainDef [filterDef=" + filterDef + ", underlying=" + underlying + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class TransformedOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;

		private final EventTransformerDef eventTransformerDef;

		private final OutputEventChainDef underlying;

		public TransformedOutputEventChainDef(EventTransformerDef eventTransformerDef, OutputEventChainDef underlying) {
			this.eventTransformerDef = eventTransformerDef;
			this.underlying = underlying;
		}

		public EventTransformerDef getEventTransformerDef() {
			return eventTransformerDef;
		}

		public OutputEventChainDef getUnderlying() {
			return underlying;
		}

		@Override
		public String toString() {
			return "TransformedOutputEventChainDef [eventTransformerDef=" + eventTransformerDef + ", underlying=" + underlying + "]";
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
			TransformedOutputEventChainDef other = (TransformedOutputEventChainDef) obj;
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
	
	public static class UnionOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final OutputEventChainDef[] elements;

		public UnionOutputEventChainDef(Collection<OutputEventChainDef> elements) {
			this.elements = elements.toArray(new OutputEventChainDef[elements.size()]);
		}

		public OutputEventChainDef[] getElements() {
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
			UnionOutputEventChainDef other = (UnionOutputEventChainDef) obj;
			if (!Arrays.equals(elements, other.elements))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "UnionOutputEventChainDef [elements=" + Arrays.toString(elements) + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class MultiplexedOutputEventChainDef<K> extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
	
		private final Map<K,OutputEventChainDef> Outputs;

		public MultiplexedOutputEventChainDef(Map<K, OutputEventChainDef> Outputs) {
			this.Outputs = Collections.unmodifiableMap(new HashMap<K, OutputEventChainDef>(Outputs));
		}

		public Map<K, OutputEventChainDef> getOutputs() {
			return Outputs;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((Outputs == null) ? 0 : Outputs.hashCode());
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
			MultiplexedOutputEventChainDef<K> other = (MultiplexedOutputEventChainDef<K>) obj;
			if (Outputs == null) {
				if (other.Outputs != null)
					return false;
			} else if (!Outputs.equals(other.Outputs))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "MultiplexedOutputEventChainDef [Outputs=" + Outputs + "]";
		}
		
		
	}
	
	
	// ------------------------------------------------------------------------
	
	public static class NamedDelegateOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String externalSystem;
		private final OutputEventChainDef underlying;

		public NamedDelegateOutputEventChainDef(String externalSystem, OutputEventChainDef underlying) {
			this.externalSystem = externalSystem;
			this.underlying = underlying;
		}
		
		public String getExternalSystem() {
			return externalSystem;
		}

		public OutputEventChainDef getUnderlying() {
			return underlying;
		}

		@Override
		public String toString() {
			return "ExternalOutputEventChainDef[" + externalSystem + " : " + underlying + "]";
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
			NamedDelegateOutputEventChainDef other = (NamedDelegateOutputEventChainDef) obj;
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
	
	public static class NamedOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String name;

		public NamedOutputEventChainDef(String name) {
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
			NamedOutputEventChainDef other = (NamedOutputEventChainDef) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "NamedOutputEventChainDef[" + name + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class PeriodicTaskOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final PeriodicityDef periodicity;
		private final TaskDef taskDef;

		public PeriodicTaskOutputEventChainDef(PeriodicityDef periodicity, TaskDef taskDef) {
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
			PeriodicTaskOutputEventChainDef other = (PeriodicTaskOutputEventChainDef) obj;
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
			return "PeriodicTaskOutputEventChainDef [" + taskDef + ", periodicity=" + periodicity + "]";
		}
		
	}	
	
	// ------------------------------------------------------------------------
	
	public static class SubSamplingPeriodicTaskOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final PeriodicTaskOutputEventChainDef underlying;
		private final int subSampingFrequency;
		
		public SubSamplingPeriodicTaskOutputEventChainDef(PeriodicTaskOutputEventChainDef underlying, int subSampingFrequency) {
			this.underlying = underlying;
			this.subSampingFrequency = subSampingFrequency;
		}

		public PeriodicTaskOutputEventChainDef getUnderlying() {
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
			SubSamplingPeriodicTaskOutputEventChainDef other = (SubSamplingPeriodicTaskOutputEventChainDef) obj;
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
			return "SubSamplingPeriodicTaskOutputEventChainDef [underlying=" + underlying + ", subSampingFrequency=" + subSampingFrequency + "]";
		}
		
	}

	// ------------------------------------------------------------------------
	
	public static class PropTreePathOutputEventChainDef extends OutputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String path;
		private final String propName;
		private final String markAndCompareAccessorName;
		
		public PropTreePathOutputEventChainDef(String path, String propName,
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
			PropTreePathOutputEventChainDef other = (PropTreePathOutputEventChainDef) obj;
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
			return "PropTreePathOutputEventChainDef [" 
					+ "path=" + path 
					+ ", propName=" + propName
					+ ", markAndCompareAccessorName=" + markAndCompareAccessorName 
					+ "]";
		}
		
	}
	
	// ------------------------------------------------------------------------

}
