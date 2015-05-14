package org.sef4j.core.helpers.tasks;

import org.sef4j.core.api.def.ioevenchain.InputEventChainDef;

public class PeriodicTaskInputEventChainDef extends InputEventChainDef {
	
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
	
	public TaskDef getTaskDef() {
		return taskDef;
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