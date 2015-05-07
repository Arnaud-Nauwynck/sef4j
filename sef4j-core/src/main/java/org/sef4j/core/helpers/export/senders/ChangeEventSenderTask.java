package org.sef4j.core.helpers.export.senders;

import org.sef4j.core.helpers.PeriodicTask;

/**
 * 
 * @param <T>
 * @param <E>
 */
public class ChangeEventSenderTask<T,E> {

	private PeriodicTask sendAllPeriodicTask;
	private PeriodicTask sendChangesPeriodicTask;

	private EventSenderFragmentsExporter<T,E> exporter;
	
	// ------------------------------------------------------------------------

	public ChangeEventSenderTask(
			PeriodicTask.Builder sendAllPeriodicTask,
			PeriodicTask.Builder sendChangesPeriodicTask,
			EventSenderFragmentsExporter<T,E> exporter
			) {
		this.exporter = exporter;
		// if (sendAllPeriodicTask == null) sendAllPeriodicTask = new PeriodicTask.Builder();
		sendAllPeriodicTask.withTask(() -> exporter.sendEventsForCollectedFragments());
		sendChangesPeriodicTask.withTask(() -> exporter.sendEventsForMarkedAndCollectedChangedFragments());
		this.sendAllPeriodicTask = sendAllPeriodicTask.build();
		this.sendChangesPeriodicTask = sendChangesPeriodicTask.build();
	}

	// ------------------------------------------------------------------------
	
	public PeriodicTask getSendAllPeriodicTask() {
		return sendAllPeriodicTask;
	}

	public PeriodicTask getSendChangesPeriodicTask() {
		return sendChangesPeriodicTask;
	}

	public EventSenderFragmentsExporter<T, E> getExporter() {
		return exporter;
	}

	// ------------------------------------------------------------------------

	public static class Builder<T,E> {
		
		public PeriodicTask.Builder sendAllPeriodicTask = new PeriodicTask.Builder();
		public PeriodicTask.Builder sendChangesPeriodicTask = new PeriodicTask.Builder();
		public EventSenderFragmentsExporter.Builder<T,E> exporter = new EventSenderFragmentsExporter.Builder<T,E>();
		
		public ChangeEventSenderTask<T,E> build() {
			EventSenderFragmentsExporter<T,E> export = exporter.build();
			return new ChangeEventSenderTask<T,E>(sendAllPeriodicTask, sendChangesPeriodicTask, export);
		}
		
	}
	
}
