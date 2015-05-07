package org.sef4j.core.api.iochain;

import org.sef4j.core.helpers.PeriodicityDef;


/**
 * default class implementations for describing different EventSource channels
 */
public final class DefaultEventSourceDefs {

	/* private to force all static */
	private DefaultEventSourceDefs() {}
	
	// ------------------------------------------------------------------------
	
	public static class FitleredEventSourceDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;

		private final EventFilterDef filterDef;

		private final InputEventChainDef underlying;

		public FitleredEventSourceDef(EventFilterDef filterDef, InputEventChainDef underlying) {
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
			FitleredEventSourceDef other = (FitleredEventSourceDef) obj;
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
			return "FitleredEventSourceDef [filterDef=" + filterDef + ", underlying=" + underlying + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class TransformedEventSourceDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;

		private final EventTransformerDef eventTransformerDef;

		private final InputEventChainDef underlying;

		public TransformedEventSourceDef(EventTransformerDef eventTransformerDef, InputEventChainDef underlying) {
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
			return "TransformedEventSourceDef [eventTransformerDef=" + eventTransformerDef + ", underlying=" + underlying + "]";
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
			TransformedEventSourceDef other = (TransformedEventSourceDef) obj;
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
	
	public static class ExternalNamedEventSourceDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String externalSystem;
		private final String name;

		public ExternalNamedEventSourceDef(String externalSystem, String name) {
			this.externalSystem = externalSystem;
			this.name = name;
		}
		
		public String getExternalSystem() {
			return externalSystem;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "ExternalNamedEventSourceDef[" + externalSystem + " : " + name + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((externalSystem == null) ? 0 : externalSystem.hashCode());
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
			ExternalNamedEventSourceDef other = (ExternalNamedEventSourceDef) obj;
			if (externalSystem == null) {
				if (other.externalSystem != null)
					return false;
			} else if (!externalSystem.equals(other.externalSystem))
				return false;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}

		
	}
	
	// ------------------------------------------------------------------------
	
	public static class InternalNamedTopicEventSourceDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String name;

		public InternalNamedTopicEventSourceDef(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "InternalNamedTopicEventSourceDef[" + name + "]";
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
			InternalNamedTopicEventSourceDef other = (InternalNamedTopicEventSourceDef) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class NamedPeriodicTaskEventSourceDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final String name;
		private final PeriodicityDef periodicity;

		public NamedPeriodicTaskEventSourceDef(String name, PeriodicityDef periodicity) {
			this.name = name;
			this.periodicity = periodicity;
		}

		public String getName() {
			return name;
		}

		public PeriodicityDef getPeriodicity() {
			return periodicity;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			NamedPeriodicTaskEventSourceDef other = (NamedPeriodicTaskEventSourceDef) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
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
			return "NamedPeriodicTaskEventSourceDef [name=" + name + ", periodicity=" + periodicity + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	public static class PropTreePathPeriodicTaskEventSourceDef extends InputEventChainDef {
		
		/** */
		private static final long serialVersionUID = 1L;
		
		private final PeriodicityDef periodicity;
		private final String path;
		private final String propName;
		private final String markAndCompareAccessorName;
		
		public PropTreePathPeriodicTaskEventSourceDef(PeriodicityDef periodicity, String path, String propName,
				String markAndCompareAccessorName) {
			this.periodicity = periodicity;
			this.path = path;
			this.propName = propName;
			this.markAndCompareAccessorName = markAndCompareAccessorName;
		}
		
		public PeriodicityDef getPeriodicity() {
			return periodicity;
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
			result = prime * result + ((periodicity == null) ? 0 : periodicity.hashCode());
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
			PropTreePathPeriodicTaskEventSourceDef other = (PropTreePathPeriodicTaskEventSourceDef) obj;
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
			if (periodicity == null) {
				if (other.periodicity != null)
					return false;
			} else if (!periodicity.equals(other.periodicity))
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
			return "PropTreePathPeriodicTaskEventSourceDef [periodicity=" + periodicity + ", path=" + path + ", propName=" + propName
					+ ", markAndCompareAccessorName=" + markAndCompareAccessorName + "]";
		}
		
	}
	
	// ------------------------------------------------------------------------

}
