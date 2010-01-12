package hexapodsimulator.timebar;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import java.util.Vector;

/**
 * Simple model creator creating a timebar model with dynamic interval management
 * 
 * @author peter
 * @version 1
 */
public class ModelCreator {

    private static Vector<Vector<EventInterval>> intervals = new Vector<Vector<EventInterval>>(13); // 2x6 leg, 1 music

    private static void initializeVector() {
        for (int i = 0; i < intervals.capacity(); i++) {
            intervals.add(new Vector<EventInterval>());
        }
    }

    /**
     * Adds an interval to the Vector
     * After the interval had been added, the model has to be recreated with the createModel Method
     * @param row The row to add the interval to
     * @param interval The interval to add
     * @return 0 if the operation was successful<br>or -1 if there is already an interval at the specified position
     */
    public static int addInterval(int row, EventInterval interval) {
        if (intervals.isEmpty()) {
            initializeVector();
        }

        for (int i = 0; i < interval.getMillis(); i++) {
            if (getIntervalIndexAtDate(row, interval.getBegin().copy().advanceMillis(i)) != -1) {
                return -1;
            }
        }
        /*for (int i = 0; i < intervals.elementAt(row).size(); i++) {
        if(intervals.elementAt(row).elementAt(i).getBegin().getDate().after(interval.getBegin().getDate())
        && intervals.elementAt(row).elementAt(i).getEnd().getDate().before(interval.getEnd().getDate()))
        return;
        }*/
        intervals.elementAt(row).add(interval);
        return 0;
    }

    /**
     * Returns the interval with the chosen row and index from the vector
     * @param row The row to search for the interval
     * @param index The index of the interval
     * @return The selected interval
     */
    public static EventInterval getInterval(int row, int index) {
        return intervals.elementAt(row).elementAt(index);
    }

    /**
     * Returns the interval that is active at the specified date.
     * This means that the specified date needs not to be the start date of the required interval.
     * @param row The row to search for the interval
     * @param date The date that the interval should contain
     * @return The required interval or null if no interval matches
     */
    public static EventInterval getIntervalAtDate(int row, JaretDate date) {
        EventInterval searchInterval = new EventInterval(date, date);
        for (int i = 0; i < intervals.elementAt(row).size(); i++) {
            if(intervals.elementAt(row).elementAt(i).intersects(searchInterval))
                return intervals.elementAt(row).elementAt(i);
        }
        return null;
    }

    /**
     * Returns a Vector containing all intervals
     * @return intervals Interval vector: consists of 13 vectors that contain the intervals of each row
     */
    public static Vector<Vector<EventInterval>> getIntervals() {
        return intervals;
    }

    /**
     * Sets the interval vector
     * @param intervals Interval vector: consists of 13 vectors that contain the intervals of each row
     */
    public static void setIntervals(Vector<Vector<EventInterval>> intervals) {
        ModelCreator.intervals = intervals;
    }

    /**
     * Removes an interval from the Vector of a row
     * After the interval had been removed, the model has to be recreated with the createModel Method
     * @param row The row where an interval should be removed
     * @param index The index of the interval to remove
     */
    public static void remInterval(int row, int index) {
        intervals.elementAt(row).remove(index);
    }

    /**
     * Removes all intervals with the specified name
     * @param name The name of the intervals that should be removed
     */
    public static void remInterval(String name) {
        for (int i = 0; i < intervals.size(); i++) {
            Vector<EventInterval> elementsToDelete = new Vector<EventInterval>();
            for (int j = 0; j < intervals.elementAt(i).size(); j++) {
                if (intervals.elementAt(i).elementAt(j).getTitle().equals(name)) {
                    elementsToDelete.add(intervals.elementAt(i).elementAt(j));
                }
            }
            intervals.elementAt(i).removeAll(elementsToDelete);
        }
    }

    /**
     * Removes all intervals
     */
    public static void clear() {
        for (int i = 0; i < intervals.size(); i++) {
            intervals.elementAt(i).clear();
        }
    }

    /**
     * Returns the index of the first occurence of the given name in the interval Vector of one row
     * @param row The row where the interval should be searched
     * @param s The string to search for
     * @return The index of first occurence in the interval Vector,
     * or -1 if the element was not found
     */
    public static int getIntervalIndexByName(int row, String s) {
        for (int i = 0; i < intervals.elementAt(row).size(); i++) {
            if (intervals.elementAt(row).elementAt(i).getTitle().equals(s)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the interval at the specified time.
     * Overlapping intervals are not supported.
     * @param row The row that contains the interval
     * @param date The date of the interval. Values between the start date and the end date are valid.
     * @return The index of the interval, or -1 if no interval is at the specified position
     */
    public static int getIntervalIndexAtDate(int row, JaretDate date) {
        if (intervals.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < intervals.elementAt(row).size(); i++) {
            if (intervals.elementAt(row).elementAt(i).getBegin().getDate().getTime() <= date.getDate().getTime()
                    && intervals.elementAt(row).elementAt(i).getEnd().getDate().getTime() >= date.getDate().getTime()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the row with the specified name
     * @param s The string to search for
     * @return 0
     * @deprecated
     */
    public static int getRowIndexByName(String s) {
        return 0;
    }

    public static SequenceTimebarRowModel getRowModelByIndex(int index) {
        return (SequenceTimebarRowModel) createModel().getRow(index);
    }

    /**
     * Creates a TimeBarModel with the intervals from the interval vector
     * Intervals can be added with the <a>addInterval</a> method
     * @return The model
     */
    public static TimeBarModel createModel() {

        DefaultTimeBarModel model = new DefaultTimeBarModel();
        DefaultRowHeader header;
        SequenceTimebarRowModel rowModel;
        for (int i = 0; i < intervals.capacity(); i++) {
            if (i < (intervals.capacity() - 1)) {
                header = new DefaultRowHeader(Integer.toString(i / 2 + 1) + ((i % 2) == 0 ? "ft" : "c"));
                rowModel = new SequenceTimebarRowModel(header, i, i / 2, (i % 2) == 0 ? "ft" : "c");
            } else {
                header = new DefaultRowHeader("M");
                rowModel = new SequenceTimebarRowModel(header, i, "music");
            }
            if (!intervals.isEmpty() && !intervals.elementAt(i).isEmpty()) {
                for (int j = 0; j < intervals.elementAt(i).size(); j++) {
                    rowModel.addInterval(intervals.elementAt(i).elementAt(j));
                }
            }
            model.addRow(rowModel);
        }
        return model;

    }
}
