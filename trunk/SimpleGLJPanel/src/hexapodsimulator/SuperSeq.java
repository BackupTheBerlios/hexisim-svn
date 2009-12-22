/*
 * Super Sequenz wo alle Sequenzen unterkommen von allen Füßen
 */
package hexapodsimulator;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerhard
 */
public class SuperSeq {

    /**
     * Beinhaltet eine Sequenz, genannt hexiSequenz, und eine Position (zeitlich), pos
     */
    class SeqAtPos {

        public SeqAtPos(HexiSequenz hexiSequenz) {
            this.hexiSequenz = hexiSequenz;
        }

        public SeqAtPos(HexiSequenz hexiSequenz, int pos) {
            this.hexiSequenz = hexiSequenz;
            this.pos = pos;
        }
        public HexiSequenz hexiSequenz;
        public int pos;
    }
    private Vector<Vector<SeqAtPos>> angles;
    private Vector<Vector<SeqAtPos>> angles2;

    public SuperSeq() {
        angles = new Vector<Vector<SeqAtPos>>(12);
        for (int i = 0; i < angles.capacity(); i++) {
            angles.add(new Vector<SeqAtPos>());
        }

        angles2 = new Vector<Vector<SeqAtPos>>(12);
        for (int i = 0; i < angles2.capacity(); i++) {
            angles2.add(new Vector<SeqAtPos>());
        }
    }

    /**
     *
     * @param hexiSequenz eine Sequenz
     * @param pos Zeit in ms
     * @param leg Bein 0-5
     * @param SeqAtPos Ebene0 oder 1 - 0 oder 1
     */
    public void addSeq(HexiSequenz hexiSequenz, int pos, int leg, int hallo) {
        int N = (leg * 2) + hallo;    //von 0-5 und 0/1 auf 0-11
        for (int i = 0; i < angles.elementAt(N).size(); i++) {
            if (pos > angles.elementAt(N).elementAt(i).pos) {
                angles.elementAt(N).add(i, new SeqAtPos(hexiSequenz, pos));
                return;
            }
        }
        angles.elementAt(N).add(new SeqAtPos(hexiSequenz, pos));
    }

    public void delSeq(String name) throws Exception {
        boolean deleted = false;
        for (int N = 0; N < 11; N++) {
            for (int i = 0; i < angles.elementAt(N).size(); i++) {
                if (name.equals(angles.elementAt(N).elementAt(i).hexiSequenz.getName())) {
                    angles.elementAt(N).remove(i);
                    deleted = true;
                }
            }
        }
        if (!deleted) {
            throw new Exception("No exisiting leg with specified name.");
        }
    }

    /**
     * @param pos Zeit in ms
     * @param leg Bein 0-5
     * @param SeqAtPos Ebene0 oder 1 - 0 oder 1
     */
    public void delSeq(int pos, int leg, int hallo) throws Exception {
        int N = (leg * 2) + hallo;    //von 0-5 und 0/1 auf 0-11
        for (int i = 0; i < angles.elementAt(N).size(); i++) {
            if (pos == angles.elementAt(N).elementAt(i).pos) {
                angles.elementAt(N).remove(i);
                return;
            }
        }
        throw new Exception("No exisiting leg at specified position.");
    }

    public void toSteps(int stepTime) throws Exception {
        int gesLen = 0;     //Gesamtzeit
        int gesCnt = 0;     //Gesamtanzahl der (errechneten) Schritte

        double[] tmpAngles = new double[3];
        double[] tmpCoords = new double[3];
        int tmpTime = 0;


        for (int i = 0; i < 12; i++) {  //Gesamtzeit ermitteln, für nachherige StepAnzahlermittlung
            if (gesLen < angles.elementAt(i).lastElement().pos + angles.elementAt(i).lastElement().hexiSequenz.getTime()) {
                gesLen = angles.elementAt(i).lastElement().pos + angles.elementAt(i).lastElement().hexiSequenz.getTime();
            }
        }
        gesCnt = gesLen / stepTime;     //Gesamtanzahl der Schritte
        //Gesamtzeit bekannt, jetzt kommt die Hauptschleife, welche jeden Punkt einzeln durchläuft


        angles2.clear();
        for (int i = 0; i < 12; i++) {
            angles2.add(new Vector<SeqAtPos>());
            angles2.elementAt(i).add(new SeqAtPos(new HexiSequenz(), 0));
            angles2.elementAt(i).elementAt(0).hexiSequenz.setName(
                    "gesteppte Gesamtsequenz der Ebene " + i % 2 + ", des Beins " + i / 2);
            angles2.elementAt(i).elementAt(0).hexiSequenz.setTime(gesLen);
        }

        for (int i = 0; i < gesCnt; i++) {

            tmpTime = i * stepTime;

            //Innerhalb dieser Hauptschleife, je eine Schleife für jedes der Beine
            for (int j = 0; j < 6; j++) {
                try {
                    tmpAngles[0] = getSingleElementAtTime(tmpTime, j, 0);
                    tmpAngles[1] = getSingleElementAtTime(tmpTime, j, 1);
                    tmpAngles[2] = getSingleElementAtTime(tmpTime, j, 2);
                    angles2.elementAt(j * 2).elementAt(0).hexiSequenz.addContent(tmpAngles[0]);
                    angles2.elementAt(j * 2 + 1).elementAt(0).hexiSequenz.addContent(new double[]{tmpAngles[1], tmpAngles[2]});
                } catch (Exception ex) {
                    Logger.getLogger(SuperSeq.class.getName()).log(Level.SEVERE, null, ex);
                    throw new Exception("Es trat ein Problem mit der Funktion getSingleElementAtTime() auf.");
                }
            }

        }
    }

    /**
     *
     * @param time Zeit in Millisekunden
     * @param leg 0-5
     * @param angle 0,1,2 - Welcher Winkel soll gesucht werden
     * @return einen bestimmten Winkel
     */
    public double getSingleElementAtTime(int time, int leg, int angle) throws Exception {
        if (time == 0) {     //Wenn Zeit 0, also allererstes Element
            return angles.elementAt(leg + ((angle == 2) ? 1 : angle)).firstElement().hexiSequenz.getAngle(0)[(angle != 0) ? angle - 1 : 0];
        }
        for (int i = 0; i < angles.elementAt(leg + ((angle == 2) ? 1 : angle)).size(); i++) {

            if (time </*=*/ angles.elementAt(leg + ((angle == 2) ? 1 : angle)).elementAt(i).pos) {
                //Hiermit steht fest dass das benötigte Element folgendes ist:
                //    angles.elementAt(leg+((angle==2)?1:angle)).elementAt(i-1)
                //Hierzu eine Abkürzung, sonst sehen die Zeilen schrecklich aus:
                SeqAtPos benElem = angles.elementAt(leg + ((angle == 2) ? 1 : angle)).elementAt(i - 1);

                if (time >= benElem.pos +
                        benElem.hexiSequenz.getTime()) {
                    //Wenn außerhalb des bestimmten Elements (Freiraum zwischen den Elementen)
                    return benElem.hexiSequenz.getAngle(benElem.hexiSequenz.getLength() - 1)[(angle != 0) ? angle - 1 : 0];
                    //returne das letzte Element, das du findest
                } else {
                    return benElem.hexiSequenz.getAngle((int) (((double) time - benElem.pos / (double) benElem.hexiSequenz.getTime()) *
                            benElem.hexiSequenz.getLength()))[(angle != 0) ? angle - 1 : 0];
                }
            }
        }
        //return -1.0;    //Eigentlich sollte er hier nie herkommen, wenn doch: ungültig
        //Deshalb kommt hier eine Exception her
        throw new Exception("Anscheinend wurde kein passendes Element gefunden.");
    }

    private double[] toCoords(double[] angles, int leg,
            double r, double b1, double b2) {
        if (angles.length == 3) {
            double[] Coords = new double[3];
            Coords[0] =
                    r * Math.cos((double) (((leg + 2) % 6 * 2 * Math.PI) / 6)) + (/*r*/b1 * Math.cos(Math.toRadians(angles[1]))) * Math.cos((((leg + 2) % 6 * 2 * Math.PI) / 6) + Math.toRadians(angles[0] - 45)) + (/*r*/b2 * Math.cos(Math.toRadians(angles[1] + angles[2])) /*/r*/) * Math.cos((((leg + 2) % 6 * 2 * Math.PI) / 6) + Math.toRadians(angles[0] - 45));
            Coords[1] =
                    r * Math.sin((double) (((leg + 2) % 6 * 2 * Math.PI) / 6)) + (/*r*/b1 * Math.cos(Math.toRadians(angles[1]))) * Math.sin((((leg + 2) % 6 * 2 * Math.PI) / 6) + Math.toRadians(angles[0] - 45)) + (/*r*/b2 * Math.cos(Math.toRadians(angles[1] + angles[2])) /*/r*/) * Math.sin((((leg + 2) % 6 * 2 * Math.PI) / 6) + Math.toRadians(angles[0] - 45));
            Coords[2] =
                    0.0 + b1 * java.lang.Math.sin(Math.toRadians(angles[1])) + b2 * Math.sin(Math.toRadians(angles[1] + angles[2]));
            return Coords;
        } else {
            return null;
        }
    }

    /**
     *
     * @param filename Filename
     * @throws Exception Problems ie with File-Access, Memory-Allocation, ...
     */
    public void angletofile(String filename) throws Exception {
        if (this.angles2 == null) {
            try {
                toSteps(20);
            } catch (Exception ex) {
                System.out.println(ex);
                throw ex;
            }
        }
        FileOutputStream fs = new FileOutputStream(filename);
        DataOutputStream ds = new DataOutputStream(fs);

        ds.writeByte((byte) 0);
        for (int i = 0; i <
                angles2.firstElement().firstElement().hexiSequenz.getLength(); i++) {
            for (int j = 0; j < 6; j++) {
                ds.writeShort((short) Math.toRadians(angles2.get(j * 2).firstElement().hexiSequenz.getAngle(i)[0]) * 1024);
                ds.writeShort((short) Math.toRadians(angles2.get(j * 2 + 1).firstElement().hexiSequenz.getAngle(i)[0]) * 1024);
                ds.writeShort((short) Math.toRadians(angles2.get(j * 2 + 1).firstElement().hexiSequenz.getAngle(i)[1]) * 1024);
            }
        }
        ds.writeByte(0xff);
        fs.close();
    }
}
