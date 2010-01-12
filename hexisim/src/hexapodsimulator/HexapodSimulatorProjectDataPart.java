/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator;

import hexapodsimulator.timebar.EventInterval;
import java.io.Serializable;
import java.util.Vector;

/**
 * This class represents the data part of an hexapod simulator project file
 * It holds the sequences, the super sequence and the timebar intervals
 * @author peter
 */
public class HexapodSimulatorProjectDataPart implements Serializable {

    private String _name;
    private SuperSeq _superSeq;
    private Vector<HexiSequenz> _sequences;
    private Vector<Vector<EventInterval>> _intervals;

    public HexapodSimulatorProjectDataPart(String name, SuperSeq superSeq, Vector<HexiSequenz> sequences, Vector<Vector<EventInterval>> intervals) {
        _name = name;
        _superSeq = superSeq;
        _sequences = sequences;
        _intervals = intervals;
    }

    public HexapodSimulatorProjectDataPart(SuperSeq superSeq, Vector<HexiSequenz> sequences, Vector<Vector<EventInterval>> intervals) {
        this(null, superSeq, sequences, intervals);
    }

    public HexapodSimulatorProjectDataPart() {
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        _name = name;
    }

    public SuperSeq getSuperSeq() {
        return _superSeq;
    }

    public void setSuperSeq(SuperSeq superSeq) {
        _superSeq = superSeq;
    }

    public Vector<HexiSequenz> getSequences() {
        return _sequences;
    }

    public void setSequences(Vector<HexiSequenz> sequences) {
        _sequences = sequences;
    }

    public Vector<Vector<EventInterval>> getIntervals() {
        return _intervals;
    }

    public void setIntervals(Vector<Vector<EventInterval>> intervals) {
        _intervals = intervals;
    }
}
