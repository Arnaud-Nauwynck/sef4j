package com.google.code.joto.ui.filter;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.apache.commons.collections.Predicate;

import com.google.code.joto.eventrecorder.writer.FilteringRecordEventWriter;
import com.google.code.joto.eventrecorder.writer.RecordEventWriter;

/**
 * a swing wrapper model for FilteringRecordEventWriter
 * 
 * internally use a RecordEventFilterItemTableModel to edit list of filter to apply
 * 
 */
public class RecordEventFilterCategoryModel {

    protected RecordEventFilterFileTableModel filterItemTableModel;
    
    protected FilteringRecordEventWriter resultFilteringEventWriter;

    // implicit from resultFilteringEventWriter.getTarget()
    // protected RecordEventWriter underlyingEventWriter;
    
    private String name;
    private Object owner;
    
	private RecordEventFilterFileExternalFrameHolder filterFrameHolder;

    // ------------------------------------------------------------------------
    
    public RecordEventFilterCategoryModel(RecordEventWriter underlyingEventWriter) {
        this.resultFilteringEventWriter = new FilteringRecordEventWriter(underlyingEventWriter);
        this.filterItemTableModel = new RecordEventFilterFileTableModel();
        
        filterItemTableModel.addTableModelListener(
                new FilteringRecordEventWriterSyncFromTableModelListener(filterItemTableModel, resultFilteringEventWriter));
        
        filterFrameHolder = new RecordEventFilterFileExternalFrameHolder(filterItemTableModel);
    }
    
    // ------------------------------------------------------------------------
    
    public FilteringRecordEventWriter getResultFilteringEventWriter() {
        return resultFilteringEventWriter;
    }
    
    public RecordEventFilterFileTableModel getFilterItemTableModel() {
		return filterItemTableModel;
	}

	public String getName() {
        return name;
    }

    public void setName(String p) {
        this.name = p;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object p) {
        this.owner = p;
    }

    public void addFilterRow(RecordEventFilterFile filter) {
    	filterItemTableModel.addRow(filter);
    }
	
    public RecordEventFilterFileExternalFrameHolder getFilterFrameHolder() {
		return filterFrameHolder;
	}
	
	// ------------------------------------------------------------------------

	/**
     * inner TableModelListener adapter class 
     * to update FilteringRecordEventWriter when a RecordEventFilterItemTableModel change
     * 
     * transform List<RecordEventFilterItem> to List<Predicate>  (+ filter only active filters)
     */
    public static class FilteringRecordEventWriterSyncFromTableModelListener implements TableModelListener {

        protected RecordEventFilterFileTableModel sourceModel;
        protected FilteringRecordEventWriter target;
        
        protected List<RecordEventFilterFile> currSourceRowsListened = new ArrayList<RecordEventFilterFile>();  
        protected PropertyChangeListener innerPropertyChangeListener = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				onSourceRowPropertyChanged(evt);
			}
		};
        
        public FilteringRecordEventWriterSyncFromTableModelListener(RecordEventFilterFileTableModel sourceModel, FilteringRecordEventWriter target) {
            super();
            this.sourceModel = sourceModel;
            this.target = target;

            updateSourceToTarget();
        }

        @Override
        public void tableChanged(TableModelEvent event) {
			// handle add/remove rows... check all rows are synchronized with PropertyChangeListener
        	// brute force remove+add all.. 
            updatePropertyChangeListeners();

            updateSourceToTarget();
        }

		private void updatePropertyChangeListeners() {
            List<RecordEventFilterFile> newRows = sourceModel.getRows();
            for(RecordEventFilterFile oldRow : currSourceRowsListened) {
            	oldRow.removePropertyChangeSupport(innerPropertyChangeListener);
            }
            for(RecordEventFilterFile newRow : newRows) {
            	newRow.addPropertyChangeSupport(innerPropertyChangeListener);
            }
            currSourceRowsListened.clear();
            currSourceRowsListened.addAll(newRows);
		}

		private void onSourceRowPropertyChanged(PropertyChangeEvent evt) {
			updateSourceToTarget();
		}

		private void updateSourceToTarget() {
			List<RecordEventFilterFile> sourceRows = sourceModel.getRows();
			List<Predicate> targetElts = filterItemsToPredicates(sourceRows);
            target.setEventPredicateElts(targetElts);
		}

        public static List<Predicate> filterItemsToPredicates(List<RecordEventFilterFile> srcElts) {
            List<Predicate> targetElts = new ArrayList<Predicate>();
            for(RecordEventFilterFile srcElt : srcElts) {
                if (srcElt.isActive()) {
                    Predicate predicate = srcElt.getEventPredicate();
                    if (predicate != null) {
                        targetElts.add(predicate);
                    }
                }
            }
            return targetElts;
        }
    }
    
    
}
