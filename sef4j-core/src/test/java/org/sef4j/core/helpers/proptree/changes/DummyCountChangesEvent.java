package org.sef4j.core.helpers.proptree.changes;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.core.helpers.proptree.DummyCount;

/**
 * event class for holding DummyCount changes 
 */
public class DummyCountChangesEvent implements Serializable {

    /** internal for java.io.Serializable */
    private static final long serialVersionUID = 1L;

    public static final Function<Map<String,DummyCount>,DummyCountChangesEvent> FACTORY =
            new Function<Map<String,DummyCount>,DummyCountChangesEvent>() {
        @Override
        public DummyCountChangesEvent apply(Map<String, DummyCount> t) {
            return new DummyCountChangesEvent(t);
        }
    };
    
    private final Map<String,DummyCount> changes;

    // ------------------------------------------------------------------------
    
    public DummyCountChangesEvent(Map<String, DummyCount> changes) {
        if (changes == null) throw new IllegalArgumentException();
        this.changes = changes;
    }

    // ------------------------------------------------------------------------
    
    public Map<String,DummyCount> getChanges() {
        return changes;
    }

    @Override
    public String toString() {
        return "DummyCountChangesEvent[" 
                + changes.size()+ " change(s)"
                + "]";
    }
    
}
