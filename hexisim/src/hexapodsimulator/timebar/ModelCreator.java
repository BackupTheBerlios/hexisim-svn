package hexapodsimulator.timebar;

import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.TimeBarModel;
import hexapodsimulator.DeepObjectCopy;
import java.util.Vector;

/**
 * Simple model creator creating a timebar model with dynamic interval management
 * 
 * @author peter
 * @version 1
 */
public class ModelCreator {

    private static Vector<Vector<EventInterval>> intervals = new Vector<Vector<EventInterval>>(13); // 2x6 leg, 1 music
    private static Vector<Vector<int[]>> intervalCombinations = new Vector<Vector<int[]>>();
    // commited:
    private static Vector<Vector<EventInterval>> savedIntervals = new Vector<Vector<EventInterval>>(13);
    private static Vector<Vector<int[]>> savedIntervalCombinations = new Vector<Vector<int[]>>();

    private static void initializeVector() {
        for (int i = 0; i < intervals.capacity(); i++) {
            intervals.add(new Vector<EventInterval>());
        }
    }

    /**
     * Adds an interval to the Vector<br>
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
        intervals.elementAt(row).add(interval);
        return 0;
    }

    /**
     * Creates a combination between the two defined intervals.
     * If the first interval is already part of a combination,
     * the second interval will be added to this combination.
     * @param row1 Row of the first interval
     * @param index1 Index of the first interval
     * @param row2 Row of the second interval
     * @param index2 Index of the second interval
     */
    public static void combineIntervals(int row1, int index1, int row2, int index2) {
        if (getCombinationIndex(row2, index2) != -1) {
            return;
        }
        boolean elementAdded = false;
        for (int i = 0; i < intervalCombinations.size(); i++) {
            for (int j = 0; j < intervalCombinations.elementAt(i).size(); j++) {
                int[] intervalIndex = intervalCombinations.elementAt(i).elementAt(j);
                if (intervalIndex[0] == row1 && intervalIndex[1] == index1) {
                    int[] newIntervalIndex = new int[2];
                    newIntervalIndex[0] = row2;
                    newIntervalIndex[1] = index2;
                    intervalCombinations.elementAt(i).add(newIntervalIndex);
                    elementAdded = true;
                    break;
                }
            }
        }
        if (!elementAdded) {
            int[] newIntervalIndex = new int[2];
            newIntervalIndex[0] = row1;
            newIntervalIndex[1] = index1;
            intervalCombinations.add(new Vector<int[]>());
            intervalCombinations.lastElement().add(newIntervalIndex);
            combineIntervals(row1, index1, row2, index2);
        }
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
     * Returns a vector containing all intervals that are combined
     * with the specified interval.
     * @param row The row to search for the given interval
     * @param index The index of the given interval
     * @return A vector with EventInterval objects or null if the specified interval
     * is not part of a combination
     */
    public static Vector<EventInterval> getCombinedIntervals(int row, int index) {
        Vector<EventInterval> combinedIntervals = new Vector<EventInterval>();
        int combinationIndex = getCombinationIndex(row, index);
        if (combinationIndex == -1) {
            return null;
        }
        for (int i = 0; i < intervalCombinations.elementAt(combinationIndex).size(); i++) {
            int[] intervalIndex = intervalCombinations.elementAt(combinationIndex).elementAt(i);
            combinedIntervals.add(getInterval(intervalIndex[0], intervalIndex[1]));
        }
        return combinedIntervals;
    }

    /**
     * Returns a vector containing the row indices of the intervals
     * that are combined with the specified interval.
     * @param row The row to search for the given interval
     * @param index The index of the given interval
     * @return A vector of Integers containing one row id per interval
     * or null if the specified interval is not part of a combination.
     */
    public static Vector<Integer> getCombinedIntervalRows(int row, int index) {
        Vector<Integer> combinedIntervalRows = new Vector<Integer>();
        int combinationIndex = getCombinationIndex(row, index);
        if (combinationIndex == -1) {
            return null;
        }
        for (int i = 0; i < intervalCombinations.elementAt(combinationIndex).size(); i++) {
            int[] intervalIndex = intervalCombinations.elementAt(combinationIndex).elementAt(i);
            combinedIntervalRows.add(intervalIndex[0]);
        }
        return combinedIntervalRows;
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
            if (intervals.elementAt(row).elementAt(i).intersects(searchInterval)) {
                return intervals.elementAt(row).elementAt(i);
            }
        }
        return null;
    }

    /**
     * Returns a Vector containing all intervals
     * @return Interval vector: consists of 13 vectors that contain the intervals of each row
     */
    public static Vector<Vector<EventInterval>> getAllIntervals() {
        return intervals;
    }

    /**
     * Returns the vector that represents combinations of intervals in the interval vector
     * @return A vector consisting of one vector per combination which contains the indices of the interval vector
     * @see getAllIntervals(), setIntervals()
     */
    public static Vector<Vector<int[]>> getAllIntervallCombinations() {
        return intervalCombinations;
    }

    /**
     * Sets the interval vector
     * @param intervals Interval vector: consists of 13 vectors that contain the intervals of each row
     */
    public static void setIntervals(Vector<Vector<EventInterval>> intervals) {
        ModelCreator.intervals = intervals;
    }

    /**
     * Sets the vector that represents combinations of intervals in the interval vector
     * @param intervalCombinations A vector consisting of one vector per combination which contains the indices of the interval vector
     * @see getAllIntervals, setIntervals
     */
    public static void setIntervalCombinations(Vector<Vector<int[]>> intervalCombinations) {
        ModelCreator.intervalCombinations = intervalCombinations;
    }

    /**
     * Removes an interval from the Vector of a row.<br>
     * If the selected interval is part of a combination,
     * all other intervals of the combination are also deleted.<br>
     * After the interval had been removed, the model has to be recreated with the createModel Method
     * @param row The row where an interval should be removed
     * @param index The index of the interval to remove
     */
    public static void remInterval(int row, int index) {
        int combinationIndex = getCombinationIndex(row, index);
        if (combinationIndex == -1) {
            intervals.elementAt(row).remove(index);
            for (int i = 0; i < intervalCombinations.size(); i++) {
                for (int j = 0; j < intervalCombinations.elementAt(i).size(); j++) {
                    if (intervalCombinations.elementAt(i).elementAt(j)[0] > index) {
                        intervalCombinations.elementAt(i).elementAt(j)[0]--;
                    }
                    if (intervalCombinations.elementAt(i).elementAt(j)[1] > index) {
                        intervalCombinations.elementAt(i).elementAt(j)[1]--;
                    }
                }
            }
        } else {
            for (int i = 0; i < intervalCombinations.elementAt(combinationIndex).size(); i++) {
                intervals.elementAt(intervalCombinations.elementAt(combinationIndex).elementAt(i)[0]).remove(intervalCombinations.elementAt(combinationIndex).elementAt(i)[1]);
                for (int j = 0; j < intervalCombinations.size(); j++) {
                    for (int k = 0; k < intervalCombinations.elementAt(j).size(); k++) {
                        int[] intervalIndex = intervalCombinations.elementAt(j).elementAt(k);
                        if (intervalIndex[0] == intervalCombinations.elementAt(combinationIndex).elementAt(i)[0]
                                && intervalIndex[1] > intervalCombinations.elementAt(combinationIndex).elementAt(i)[1]) {
                            intervalIndex[1]--;
                        }
                    }
                }
            }
            intervalCombinations.remove(combinationIndex);
        }
    }

    /**
     * Removes all intervals with the specified name
     * and all intervals that are combined with one of these intervals.
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
            for (int j = 0; j < elementsToDelete.size(); j++) {
                remInterval(i, getIntervalIndexAtDate(i, elementsToDelete.elementAt(j).getBegin()));
            }
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

    /**
     * Returns the row model index for the specified row
     * @param index The index of the row
     * @return A SequenceTimebarRowModel object, describing the row model
     * of the specified row
     */
    public static SequenceTimebarRowModel getRowModelByIndex(int index) {
        return (SequenceTimebarRowModel) createModel().getRow(index);
    }

    private static int getCombinationIndex(int row, int index) {
        for (int i = 0; i < intervalCombinations.size(); i++) {
            for (int j = 0; j < intervalCombinations.elementAt(i).size(); j++) {
                int[] intervalIndex = intervalCombinations.elementAt(i).elementAt(j);
                if (intervalIndex[0] == row && intervalIndex[1] == index) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Saves the changes made to the interval vector.
     * This allows to revert this state later.
     */
    public static void saveIntervalChanges() {
        savedIntervals = (Vector<Vector<EventInterval>>) DeepObjectCopy.getDeepCopy(intervals);
    }

    /**
     * Saves the changes made to the interval combinations.
     * This allows to revert this state later.
     */
    public static void saveCombinationChanges() {
        savedIntervalCombinations = (Vector<Vector<int[]>>) DeepObjectCopy.getDeepCopy(intervalCombinations);
    }

    /**
     * Saves the changes made to the interval vector and to the interval combinations.
     * This allows to revert this state later.
     */
    public static void saveChanges() {
        saveIntervalChanges();
        saveCombinationChanges();
    }

    /**
     * Reverts the changes made to the interval vector
     * since the last call of saveChanges or saveIntervalChanges.
     */
    public static void revertIntervalChanges() {
        intervals = (Vector<Vector<EventInterval>>) DeepObjectCopy.getDeepCopy(savedIntervals);
    }

    /**
     * Reverts the changes made to the interval combinations
     * since the last call of saveChanges or saveCombinationChanges.
     */
    public static void revertCombinationChanges() {
        intervalCombinations = (Vector<Vector<int[]>>) DeepObjectCopy.getDeepCopy(savedIntervalCombinations);
    }

    /**
     * Reverts the changes made to the interval vector or to the interval combinations
     * since the last call of saveChanges, saveIntervalChanges or saveCombinationChanges.
     */
    public static void revertChanges() {
        revertIntervalChanges();
        revertCombinationChanges();
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
