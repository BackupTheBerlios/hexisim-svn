package hexapodsimulator.timebar;

import de.jaret.util.date.IntervalImpl;
import de.jaret.util.date.JaretDate;

/**
 * Interval Extension with title
 * 
 * @author peter
 * @version 1
 */
public class EventInterval extends IntervalImpl {

    private String _title;

    public EventInterval(JaretDate from, JaretDate to) {
        super(from, to);
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

    public long getMillis() {
        return getEnd().diffMilliSeconds(getBegin());
    }

    @Override
    public String toString() {
        return _title + ": " + getBegin().toDisplayStringTime(true) + "." + getBegin().getMillis() +
                " - " + getEnd().toDisplayStringTime(true) + "." + getEnd().getMillis();
    }
}
