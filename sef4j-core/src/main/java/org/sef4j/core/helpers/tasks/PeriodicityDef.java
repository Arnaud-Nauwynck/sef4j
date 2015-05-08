package org.sef4j.core.helpers.tasks;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class PeriodicityDef implements Serializable {

	/** */
	private static final long serialVersionUID = 1L;

	private final int period;
	
	private final TimeUnit periodUnit;
	
	private final String schedulerExecutorName;

	// ------------------------------------------------------------------------
	
	public PeriodicityDef(int period, TimeUnit periodUnit, String schedulerExecutorName) {
		this.period = period;
		this.periodUnit = periodUnit;
		this.schedulerExecutorName = schedulerExecutorName;
	}

	public int getPeriod() {
		return period;
	}

	public TimeUnit getPeriodUnit() {
		return periodUnit;
	}

	public String getSchedulerExecutorName() {
		return schedulerExecutorName;
	}

	// ------------------------------------------------------------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + period;
		result = prime * result + ((periodUnit == null) ? 0 : periodUnit.hashCode());
		result = prime * result + ((schedulerExecutorName == null) ? 0 : schedulerExecutorName.hashCode());
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
		PeriodicityDef other = (PeriodicityDef) obj;
		if (period != other.period)
			return false;
		if (periodUnit != other.periodUnit)
			return false;
		if (schedulerExecutorName == null) {
			if (other.schedulerExecutorName != null)
				return false;
		} else if (!schedulerExecutorName.equals(other.schedulerExecutorName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "PeriodicityDef [period=" + period + ", periodUnit=" + periodUnit + ", schedulerExecutorName=" + schedulerExecutorName + "]";
	}
	
}
