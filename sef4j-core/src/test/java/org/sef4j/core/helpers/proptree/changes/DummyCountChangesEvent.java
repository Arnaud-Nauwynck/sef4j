package org.sef4j.core.helpers.proptree.changes;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.sef4j.core.helpers.export.ExportFragmentList;
import org.sef4j.core.helpers.proptree.DummyCount;

/**
 * event class for holding DummyCount changes 
 */
public class DummyCountChangesEvent implements Serializable {

    /** internal for java.io.Serializable */
    private static final long serialVersionUID = 1L;

    public static final Function<ExportFragmentList<DummyCount>,List<DummyCountChangesEvent>> FACTORY =
            new Function<ExportFragmentList<DummyCount>,List<DummyCountChangesEvent>>() {
        @Override
        public List<DummyCountChangesEvent> apply(ExportFragmentList<DummyCount> changes) {
            return Collections.singletonList(new DummyCountChangesEvent(changes.identifiableFragmentsToValuesMap()));
        }
    };
    
    private final Map<?,DummyCount> changes;

    // ------------------------------------------------------------------------
    
    public DummyCountChangesEvent(Map<?,DummyCount> changes) {
        if (changes == null) throw new IllegalArgumentException();
        this.changes = changes;
    }

    // ------------------------------------------------------------------------
    
    public Map<?,DummyCount> getChanges() {
        return changes;
    }

    @Override
    public String toString() {
        return "DummyCountChangesEvent[" 
                + changes.size()+ " change(s)"
                + "]";
    }
    
}
