/*
 * Super Sequenz wo alle Sequenzen unterkommen von allen Füßen
 */
package hexapodsimulator;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gerhard
 */
public class SuperSeq implements Serializable {

    /**
     * Beinhaltet eine Sequenz, genannt hexiSequenz, und eine Position (zeitlich), pos
     */
    class SeqAtPos implements Serializable {

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
    public int interpolate = 1;

    public SuperSeq() {
        angles = new Vector<Vector<SeqAtPos>>(12);
        for (int i = 0; i < angles.capacity(); i++) {
            angles.add(new Vector<SeqAtPos>());
        }
    }

    /**
     *
     * @param hexiSequenz eine Sequenz
     * @param pos Zeit in ms
     * @param leg Bein 0-5
     * @param ebene Ebene0 oder 1 - 0 oder 1
     */
    public void addSeq(HexiSequenz hexiSequenz, int pos, int leg, int ebene) {
        int N = (leg * 2) + ebene;    //von 0-5 und 0/1 auf 0-11
        for (int i = 0; i < angles.elementAt(N).size(); i++) {
            if (pos < angles.elementAt(N).elementAt(i).pos) {
                angles.elementAt(N).add(i, new SeqAtPos(hexiSequenz, pos));
                angles2 = null;
                return;
            }
        }
        angles.elementAt(N).add(new SeqAtPos(hexiSequenz, pos));
        angles2 = null;
    }

    public void delSeq(String name) throws Exception {
        boolean deleted = false;
        for (int N = 0; N < 11; N++) {
            Vector elementsToDelete = new Vector();
            for (int i = 0; i < angles.elementAt(N).size(); i++) {
                if (name.equals(angles.elementAt(N).elementAt(i).hexiSequenz.getName())) {
                    elementsToDelete.add(angles.elementAt(N).elementAt(i));
                    deleted = true;
                }
            }
            angles.elementAt(N).removeAll(elementsToDelete);
        }
        angles2 = null;
        if (!deleted) {
            throw new Exception("No exisiting leg with specified name.");
        }
    }

    /**
     * @param pos Zeit in ms
     * @param leg Bein 0-5
     * @param hallo Ebene0 oder 1 - 0 oder 1
     */
    public void delSeq(int pos, int leg, int hallo) throws Exception {
        int N = (leg * 2) + hallo;    //von 0-5 und 0/1 auf 0-11
        for (int i = 0; i < angles.elementAt(N).size(); i++) {
            if (pos == angles.elementAt(N).elementAt(i).pos) {
                angles.elementAt(N).remove(i);
                angles2 = null;
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
            if (angles.elementAt(i).size() > 0 && gesLen < angles.elementAt(i).lastElement().pos + angles.elementAt(i).lastElement().hexiSequenz.getTime()) {
                gesLen = angles.elementAt(i).lastElement().pos + angles.elementAt(i).lastElement().hexiSequenz.getTime();
            }
        }
        System.out.println("..," + gesLen + ",..");
        gesCnt = gesLen / stepTime;     //Gesamtanzahl der Schritte
        //Gesamtzeit bekannt, jetzt kommt die Hauptschleife, welche jeden Punkt einzeln durchläuft

        angles2 = new Vector<Vector<SeqAtPos>>(12);
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
                    System.out.println("--" + tmpAngles[0] + "--" + tmpAngles[1] + "--" + tmpAngles[2] + "--");
                    angles2.elementAt(j * 2 + 1).elementAt(0).hexiSequenz.addContent(tmpAngles[0]);
                    angles2.elementAt(j * 2).elementAt(0).hexiSequenz.addContent(new double[]{tmpAngles[1], tmpAngles[2]});
                } catch (Exception ex) {
                    Logger.getLogger(SuperSeq.class.getName()).log(Level.SEVERE, null, ex);
                    throw new Exception("Es trat ein Problem mit der Funktion getSingleElementAtTime() auf.");
                }
            }

        }
    }

    /**
     * Gibt einen (interpolierten) Winkel an einem bestimmten Zeitpunkt zurück<br>
     * die Objektvariable <b>interpolate</b> bestimmt ob der Wert interpoliert werden soll, oder nicht
     * @param time Zeit in Millisekunden
     * @param leg 0-5
     * @param angle 0,1,2 - Welcher Winkel soll gesucht werden
     * @return einen bestimmten Winkel
     */
    public double getSingleElementAtTime(int time, int leg, int angle) {
        if (getSeqCnt(leg, (angle == 0) ? 1 : 0) < 1) {
            return angle == 0 ? 0. : 45.;//�0
        }
        if (time < getTimePosOfSeq(leg, (angle == 0) ? 1 : 0, 0)) {
            if (interpolate == 0) {
                return angle == 0 ? 0. : 45.;//�0
            } else if (interpolate == 1) {
                //TO-DO interpolieren // Update - sollte gehen, testen
                return interpolate(0.,
                        getLegAngle(leg, 0, 0, angle),
                        (double) time / (double) angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).elementAt(0).pos);
            }
        }
        //} else if(time == angles.elementAt(leg*2 + ((angle == 0) ? 1 : 0)).firstElement().pos) {
        //    return angles.elementAt(leg*2 + ((angle == 0) ? 1 : 0)).firstElement().hexiSequenz.getAngle(0)[(angle != 0) ? angle - 1 : 0];
        //}
        for (int i = 1; i <= getSeqCnt(leg, (angle == 0) ? 1 : 0); i++) {

            if (i == getSeqCnt(leg, (angle == 0) ? 1 : 0) || time </*=*/ getTimePosOfSeq(leg, (angle == 0) ? 1 : 0, i)) { //Achtung: Short-Circuit-Evaluation
                //Hiermit steht fest dass das benötigte Element folgendes ist:
                //    angles.elementAt(leg+((angle==2)?1:angle)).elementAt(i-1)
                //Hierzu eine Abkürzung, sonst sehen die Zeilen schrecklich aus: //UPDATE: ohne Abk^^
                HexiSequenz benSeq = getSeq(leg, (angle == 0) ? 1 : 0, i - 1);
                int benPos = angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).elementAt(i - 1).pos;
                //bei obiger Zeile kann ausnahmsweise keine Fehlerbehandlung gemacht werden, da diese bereits int getSeq erledigt wird

                if (time >= benPos + benSeq.getTime()) {
                    //Wenn außerhalb des bestimmten Elements (Freiraum zwischen den Elementen)
                    if (interpolate == 0) {  //returne das letzte Element, das du findest
                        return benSeq.getAngle(benSeq.getLength() - 1)[(angle == 0) ? 0 : angle - 1];
                    } else if (interpolate == 1) {
                        return (i >= getSeqCnt(leg, (angle == 0) ? 1 : 0))
                                ? benSeq.getAngle(benSeq.getLength() - 1)[(angle == 0) ? 0 : angle - 1]
                                : interpolate(
                                benSeq.getAngle(benSeq.getLength() - 1)[(angle == 0) ? 0 : angle - 1],
                                getLegAngle(leg, i, 0, angle),
                                //(((time - benPos) / benSeq.getTime()) * benSeq.getLength())
                                ((double) time - (double) (benPos + benSeq.getTime()))
                                / (double) (angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).elementAt(i).pos
                                - (benPos + benSeq.getTime())));
                    }
                } else {    //gesuchte Zeit befindet sich innerhalb der Sequenz
                    //return benSeq.getAngle((int) (((double) time - benPos / (double) benSeq.getTime()) *
                    //        benSeq.getLength()))[(angle != 0) ? angle - 1 : 0];
                    try {
                        if (interpolate == 0) {
                            return getLegAngle(leg, i - 1,
                                    ((int) ((((double) time - (double) benPos) / (double) benSeq.getTime()) * (double) benSeq.getLength())), angle);
                        } else if (interpolate == 1) {
                            try {
                                return interpolate(
                                        getLegAngle(leg, i - 1,
                                        ((int) Math.floor(((double) (time - benPos) / (double) benSeq.getTime()) * benSeq.getLength())), angle),
                                        getLegAngle(leg, i - 1,
                                        ((int) Math.ceil(((double) (time - benPos) / (double) benSeq.getTime()) * benSeq.getLength())) < benSeq.getLength() /*TODO*/ ? ((int) Math.ceil(((double) (time - benPos) / (double) benSeq.getTime()) * benSeq.getLength())) : benSeq.getLength() - 1/*TODO*/, angle),
                                        (((double) (time - benPos) / (double) benSeq.getTime()) * (double) benSeq.getLength())
                                        - Math.floor((((double) (time - benPos) / benSeq.getTime()) * benSeq.getLength())));
                            } catch (Exception e) {
                                System.out.println("µµµµµµµµµµµµµµ\n" + e);
                                System.out.println(Math.floor(((double) (time - benPos) / (double) benSeq.getTime()) * benSeq.getLength()));
                                System.out.println(((int) Math.ceil(((double) (time - benPos) / (double) benSeq.getTime()) * benSeq.getLength())) < benSeq.getLength() /*TODO*/ ? ((int) Math.ceil(((double) (time - benPos) / (double) benSeq.getTime()) * benSeq.getLength())) : benSeq.getLength()/*TODO*/);
                                System.out.println("µµµµµµµµµµµµµµ");
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println(time + " - " + benPos + " - " + benSeq.getTime() + " - " + benSeq.getLength());
                        throw e;
                    }
                }
            }
        }
        //return -1.0;    //Eigentlich sollte er hier nie herkommen, wenn doch: ungültig
        //Deshalb kommt hier eine Exception her
        throw new UnknownError("Anscheinend wurde kein passendes Element gefunden.");
    }

    /**
     * Interpolates linear point between 2 1-dimensional values
     * @param d1 First Interpolation value
     * @param d2 Second Interpolation value
     * @param verh intended percentage
     * @return d1 + (d2 - d1) * verh
     */
    public static double interpolate(double d1, double d2, double verh) {
        return d1 + (d2 - d1) * verh;
        //Besser?: d2 + d1 (1 - verh) – egal, ergibt das selbe
    }

    /**
     * Gibt eine Zahl zurück die einen bestimmten durch Positionen bestimmten Winkel repräsentiert
     * @param leg 0-5, das Bein
     * @param seq Eine Sequenz (muss existieren)
     * @param seqPos welcher Wert innerhalb der Sequenz
     * @param angle 0,1,2 - Welcher Winkel soll gesucht werden
     * @return
     * @throws IllegalArgumentException Wenn einer der Übergabeparameter auf einen nichtexistenten Wert verweist
     */
    public double getLegAngle(int leg, int seq, int seqPos, int angle) throws IllegalArgumentException {
        if (leg < 0 || leg > 5) {
            throw new IllegalArgumentException("No leg " + leg + " available.");
        }
        if (angle < 0 || angle > 2) {
            throw new IllegalArgumentException("No Angle " + angle + " available (0/1/2).");
        }
        if (seq < 0 || seq >= angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).size()) {
            throw new IllegalArgumentException(
                    "No Sequence " + seq + " at leg/angle " + leg + "/" + angle
                    + " – must be < " + angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).size());
        }
        if (seqPos < 0
                || seqPos >= angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).
                elementAt(seq).hexiSequenz.getLength()) {
            throw new IllegalArgumentException(
                    "No seqPos " + seqPos + " at Sequence " + seq + " at leg/angle " + leg + "/" + angle
                    + " – must be < " + angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).
                    elementAt(seq).hexiSequenz.getLength());
        }
        return angles.elementAt(leg * 2 + ((angle == 0) ? 1 : 0)).
                elementAt(seq).hexiSequenz.getAngle(seqPos)[angle == 0 ? 0 : angle - 1];
    }

    /**
     * Gibt die Anzahl an einzelSequenzen einer Spur (bestehend aus Bein und Dimension)
     * @param leg 0-5, das Bein
     * @param dim 0/1 – Dimension
     * @return Anzahl der Sequenzen
     */
    public int getSeqCnt(int leg, int dim) throws IllegalArgumentException {
        if (leg < 0 || leg > 5) {
            throw new IllegalArgumentException("No leg " + leg + " available.");
        }
        if (dim < 0 || dim > 1) {
            throw new IllegalArgumentException("No dimension " + leg + " (0/1) existing.");
        }
        return angles.elementAt(leg * 2 + dim).size();
    }

    /**
     * Gibt eine Referenz auf eine bestimmte HexiSequenz, welche durch die
     * Übergabeparameter definiert wird, zurück
     * @param leg Bein – 0-5
     * @param dim Dimension (0/1)
     * @param seq Sequenz - muss vorhanden Sein
     * @return eine HexiSequenz
     * @throws IllegalArgumentException
     */
    public HexiSequenz getSeq(int leg, int dim, int seq) throws IllegalArgumentException {
        if (seq < 0 || seq >= getSeqCnt(leg, dim)) {
            throw new IllegalArgumentException(
                    "No Sequence " + seq + " at leg/dim " + leg + "/" + dim
                    + " – there are " + getSeqCnt(leg, dim) + " Seqs.");
        }
        return angles.elementAt(leg * 2 + dim).elementAt(seq).hexiSequenz;
    }

    /**
     * Returns the amount of values in a Sequence
     * @param leg Bein 0-5
     * @param dim Dimension (0/1)
     * @param seq welche Sequenz - muss existieren
     * @return Anzahl von Werten (seqPos) in einer Sequenz
     * @throws IllegalArgumentException
     */
    public int getPosAtSeqCnt(int leg, int dim, int seq) throws IllegalArgumentException {
        return getSeq(leg, dim, seq).getLength();
    }

    /**
     * Gibt die Startzeit einer bestimmten Sequenz zurück, d.h. die Zeit-Position
     * an der die Sequenz liegt, wo sie beginnt
     * @param leg Bein 0-5
     * @param dim Dimension (0/1)
     * @param seq welche Sequenz – muss existieren
     * @return Zeit
     * @throws IllegalArgumentException
     */
    public int getTimePosOfSeq(int leg, int dim, int seq) throws IllegalArgumentException {
        getPosAtSeqCnt(leg, dim, seq);
        return angles.elementAt(leg * 2 + dim).elementAt(seq).pos;
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
    public void angletofile(File file) throws Exception {
        if (angles2 == null) {
            toSteps(20);
        }
        FileOutputStream fs = new FileOutputStream(file);
        DataOutputStream ds = new DataOutputStream(fs);

        //ds.writeByte((byte) 0);
        for (int i = 0; i
                < angles2.firstElement().firstElement().hexiSequenz.getLength(); i++) {
            for (int j = 0; j < 6; j++) {
                ds.writeShort((int) (Math.toRadians(angles2.get(j * 2 + 1).firstElement().hexiSequenz.getAngle(i)[0]) * 1024 + 1609));
                ds.writeShort((int) (Math.toRadians(-angles2.get(j * 2).firstElement().hexiSequenz.getAngle(i)[0]) * 1024 + 2145));
                ds.writeShort((int) (Math.toRadians(-angles2.get(j * 2).firstElement().hexiSequenz.getAngle(i)[1]) * 1024 + 2680));
                if (j == 1 || j == 3) {
                    ds.write(new byte[6]);
                }
            }
        }
        ds.writeByte(0xff); // end byte
        ds.writeByte(0xff);
        fs.close();
    }

    public void angletofile(String filename) throws Exception {
        angletofile(new File(filename));
    }
}
