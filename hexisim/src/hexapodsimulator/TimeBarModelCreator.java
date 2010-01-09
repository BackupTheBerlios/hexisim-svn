/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator;

import de.jaret.util.date.Interval;
import de.jaret.util.date.JaretDate;
import de.jaret.util.date.IntervalImpl;
import de.jaret.util.ui.timebars.model.DefaultRowHeader;
import de.jaret.util.ui.timebars.model.DefaultTimeBarModel;
import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarRow;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;
import java.util.List;

/**
 *@deprecated
 * 
 */
public class TimeBarModelCreator {

    public static DefaultTimeBarModel model;

    public static DefaultTimeBarModel createModel() {
        model = new DefaultTimeBarModel();

        DefaultRowHeader header;
        DefaultTimeBarRowModel tbr;
        IntervalImpl interval;
        JaretDate date;
        for (int i = 0; i < 6; i++) {
            header = new DefaultRowHeader(Integer.toString(i + 1));
            tbr = new DefaultTimeBarRowModel(header);
            model.addRow(tbr);
        }
/*
        header = new DefaultRowHeader("h");
        tbr = new DefaultTimeBarRowModel(header);
        interval = new IntervalImpl();
        date = new JaretDate(/*1, 1, 1970, 0, 0, 0);
        interval.setEnd(date);
        //date = new JaretDate();
        interval.setBegin(date);
        tbr.addInterval(interval);
        model.addRow(tbr);
*/
        return model;
    }

    public static void addEvent(TimeBarRow row, IntervalImpl event) {
        int rowIndex = model.getIndexForRow(row);
        List<Interval> intervalList = row.getIntervals();
        TimeBarRowHeader rowHeader = row.getRowHeader();

        model.remRow(row);
        rowHeader = new DefaultRowHeader("h");

        DefaultTimeBarRowModel newRow = new DefaultTimeBarRowModel(rowHeader);
        if (!intervalList.isEmpty()) {
            newRow.addIntervals(intervalList);
        }
        System.out.println(event.getBegin().toDisplayStringTime()+"-"+event.getEnd().toDisplayStringTime());
        newRow.addInterval(event);
        model.addRow(rowIndex, row);
    }
}
