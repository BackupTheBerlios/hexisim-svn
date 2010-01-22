/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator.timebar;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.TimeBarMarkerListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of TimeBarMarker with functions to advance the marker position
 * @author peter
 */
public class SequenceTimebarMarker implements TimeBarMarker {

    private JaretDate _date;
    private boolean _draggable;
    private String _description;
    private String _tooltip;
    List<TimeBarMarkerListener> _listenerList;

    public SequenceTimebarMarker(JaretDate date) {
        _date = date;
        _draggable = false;
    }

    public void addTimeBarMarkerListener(TimeBarMarkerListener tbml) {
        if (_listenerList == null) {
            _listenerList = new ArrayList<TimeBarMarkerListener>();
        }
        _listenerList.add(tbml);
    }

    public void remTimeBarMarkerListener(TimeBarMarkerListener tbml) {
        if (_listenerList != null) {
            _listenerList.remove(tbml);
        }
    }

    public JaretDate getDate() {
        return _date;
    }

    public void setDate(JaretDate date) {
        JaretDate oldDate = _date;
        _date = date;
        fireMarkerChanged(oldDate, _date);
    }

    public String getDescription() {
        return _description;
    }

    public void setDescription(String description) {
        String oldDescription = _description;
        _description = description;
        fireMarkerDescriptionChanged(oldDescription, _description);
    }

    public String getTooltipText() {
        return _tooltip;
    }

    public void setTooltipText(String tooltip) {
        _tooltip = tooltip;
    }

    public boolean isDraggable() {
        return _draggable;
    }

    public void setDraggable(boolean draggable) {
        _draggable = draggable;
        fireMarkerDescriptionChanged(_description, _description);
    }

    /**
     * Advance date of the cursor by some milliseconds
     * @param milliseconds number of milliseconds
     */
    public void advanceDateMillis(long milliseconds) {
        JaretDate oldDate = _date;
        _date.advanceMillis(milliseconds);
        fireMarkerChanged(oldDate, _date);
    }

    /**
     * Advance date of the cursor by some seconds
     * @param seconds number of seconds
     */
    public void advanceDateSeconds(double seconds) {
        JaretDate oldDate = _date;
        _date.advanceSeconds(seconds);
        fireMarkerChanged(oldDate, _date);
    }

    private void fireMarkerChanged(JaretDate oldDate, JaretDate newDate) {
        if (_listenerList != null) {
            for (TimeBarMarkerListener listener : _listenerList) {
                listener.markerMoved(this, oldDate, newDate);
            }
        }
    }

    private void fireMarkerDescriptionChanged(String oldValue, String newValue) {
        if (_listenerList != null) {
            for (TimeBarMarkerListener listener : _listenerList) {
                listener.markerDescriptionChanged(this, oldValue, newValue);
            }
        }
    }
}
