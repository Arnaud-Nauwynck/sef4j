package org.sef4j.core;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class MockEvent {

    public static final MockEvent E1 = new MockEvent("e1");
    public static final MockEvent E2 = new MockEvent("e2");
    public static final MockEvent E1a = new MockEvent("e1a");
    public static final List<MockEvent> List_E1_E2_E1a = Arrays.asList(E1, E2, E1a);

    private final String value;

    public MockEvent(String value) {
	this.value = value;
    }

    public String getValue() {
	return value;
    }

    public static class MockEventValueContainsPredicate implements Predicate<MockEvent> {

	public static final MockEventValueContainsPredicate CONTAINS_1 = new MockEventValueContainsPredicate("1");
	public static final MockEventValueContainsPredicate CONTAINS_2 = new MockEventValueContainsPredicate("2");

	private final String text;

	public MockEventValueContainsPredicate(String text) {
	    this.text = text;
	}

	@Override
	public boolean test(MockEvent event) {
	    return event.getValue().contains(text);
	}

    }
}