package hexapodsimulator.timebar;

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;
import java.io.Serializable;
import java.util.Date;

/**
 * Interval Extension with title
 * 
 * @author peter
 * @version 1
 */
public class EventInterval extends IntervalImpl implements Serializable{

    private String _title;
    // for serialization:
    private Date _beginI;
    private Date _endI;

    public EventInterval(JaretDate from, JaretDate to) {
        super(from, to);
        _beginI = _begin.getDate();
        _endI = _end.getDate();
    }

    /**
     * Returns the title of the interval.
     * @return title
     */
    public String getTitle() {
        return _title;
    }

    /**
     * Sets the title (display name) of the interval.
     * @param title The title of the interval
     */
    public void setTitle(String title) {
        _title = title;
    }

    @Override
    public void setBegin(JaretDate begin) {
        super.setBegin(begin);
        _beginI = _begin.getDate();
    }

    @Override
    public void setEnd(JaretDate end) {
        super.setEnd(end);
        _endI = _end.getDate();
    }
    
    @Override
    public JaretDate getBegin() {
        return new JaretDate(_beginI);
    }

    @Override
    public JaretDate getEnd() {
        return new JaretDate(_endI);
    }

    public long getMillis() {
        return getEnd().diffMilliSeconds(getBegin());
    }

    @Override
    public String toString() {
        return _title + ": " + getBegin().toDisplayStringTime(true) + "." + getBegin().getMillis() +
                " - " + getEnd().toDisplayStringTime(true) + "." + getEnd().getMillis();
    }
}
