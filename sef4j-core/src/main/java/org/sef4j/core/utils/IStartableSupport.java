package org.sef4j.core.utils;

import java.util.function.Supplier;

public interface IStartableSupport {

    public boolean isStarted();

    public void start();

    public void stop();

    /**
     * Functional helper class implementation for IStartableSupport delegate to
     * start/stop runnable functions
     */
    public static class StartStopMethods implements IStartableSupport {
	private final Supplier<Boolean> isStartedSupplier;
	private final Runnable startRunnable;
	private final Runnable stopRunnable;

	public StartStopMethods(Supplier<Boolean> isStartedSupplier, Runnable startRunnable, Runnable stopRunnable) {
	    this.isStartedSupplier = isStartedSupplier;
	    this.startRunnable = startRunnable;
	    this.stopRunnable = stopRunnable;
	}

	@Override
	public boolean isStarted() {
	    return isStartedSupplier.get();
	}

	@Override
	public void start() {
	    startRunnable.run();
	}

	@Override
	public void stop() {
	    stopRunnable.run();
	}

    }
}
