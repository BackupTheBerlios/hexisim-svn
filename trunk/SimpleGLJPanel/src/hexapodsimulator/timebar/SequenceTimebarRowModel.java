/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator.timebar;

import de.jaret.util.ui.timebars.model.DefaultTimeBarRowModel;
import de.jaret.util.ui.timebars.model.TimeBarRowHeader;

/**
 * Extension of DefaultTimeBarRowModel with id and type-String
 * @author peter
 */
public class SequenceTimebarRowModel extends DefaultTimeBarRowModel {

    private int _id;
    private int _secId;
    private String _type;

    /**
     * Constructor for SequenceTimeBarRowModel
     * @param header The TimeBarRowHeader for the Row
     * @param id The primary id
     * @param secId The secondary id (e.g. leg number)
     * @param type Type of the row
     */
    public SequenceTimebarRowModel(TimeBarRowHeader header, int id, int secId, String type) {
        super(header);
        _id = id;
        _secId = secId;
        _type = type;
    }

    public SequenceTimebarRowModel(TimeBarRowHeader header, int id, String type) {
        this(header, id, 0, type);
    }

    public SequenceTimebarRowModel(int id, String type) {
        _id = id;
        _type = type;
    }

    /**
     * Returns the id of the timebar row
     * @return id
     */
    public int getID() {
        return _id;
    }

    /**
     * Returns the secondary id of the timebar row
     * @return secId
     */
    public int getSecID() {
        return _secId;
    }

    /**
     * Returns the type of the timebar row
     * @return type
     */
    public String getType() {
        return _type;
    }
}
