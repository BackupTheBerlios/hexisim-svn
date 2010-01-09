package hexapodsimulator;

import java.io.*;

/**
 * Beschreibt eine Bewegungssequenz für den Hexapodsimulator.<br />
 * <b>time:</b> Zeit in ms<br />
 * <b>angle[2][]:</b> Array beliebiger Länge von 2 Winkeln
 *
 * @author gerhard
 */
public class HexiSequenz2 implements Serializable {

    /**
     * Array beliebiger Länge von 2 Winkeln<br />
     * [die beiden Winkel][wieviele]
     */
    private double[][] angle;    //[Winkel1, 2][über Zeit]
    /**
     * Zeit in Millisekunden
     */
    private int time;
    /**
     * Name of the sequence
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLength() {
        return angle[0].length;
    }

    public double[][] getAngle() {
        return angle;
    }

    public double[] getAngle(int position) {
        double[] tmp = new double[2];
        tmp[0] = angle[0][position];
        tmp[1] = angle[1][position];
        return tmp;
    }

    /**
     * stub
     */
    public double[] getAngleAtTime(int time) {
        return getAngle(time/this.time * getLength());
    }

    public int getTime() {
        return time;
    }

    public HexiSequenz2(int time, double angle1[], double angle2[]) {
        this.time = time;
        angle = new double[2][];
        angle[0] = angle1.clone();
        angle[1] = angle2.clone();
    }

    public HexiSequenz2() {
        angle = new double[2][];
        angle[0] = new double[0];
        angle[0] = new double[0];
    }

    /**
     * Füge neue Werte hinzu, Übergabe: beliebig große Arrays
     * @param angle1 Winkelfeld
     * @param angle2 Winkelfeld
     * @exception IllegalArgumentExeption Übergabe falscher Referenzen,
     * insbesondere null
     */
    public void addContent(double angle1[], double angle2[]) {
        try {
            int i = angle1.length;
            i = angle2.length;
        } catch (java.lang.NullPointerException e) {
            throw new IllegalArgumentException("Einer der beiden Winkel referenziert auf ein ungültiges Objekt (null?)");
        }
        double[] temp;
        try {
            temp = new double[angle[0].length + angle1.length];
            System.arraycopy(angle[0], 0, temp, 0, angle[0].length);
            System.arraycopy(angle1, 0, temp, angle[0].length, angle1.length);
            angle[0] = temp;
        } catch (java.lang.NullPointerException e) {
            angle[0] = angle1.clone();
        }
        try {
            temp = new double[angle[1].length + angle2.length];
            System.arraycopy(angle[1], 0, temp, 0, angle[1].length);
            System.arraycopy(angle2, 0, temp, angle[1].length, angle2.length);
            angle[1] = temp;
        } catch (java.lang.NullPointerException e) {
            angle[1] = angle2.clone();
        }
    }

    public void addContent(double angle1, double angle2) {
        addContent(new double[]{angle1}, new double[]{angle2});
    }

    @Override
    public String toString() {
        String s = getClass().getName() + " " + name +  "\nWinkel1: ";
        for (int i = 0; i < angle[0].length; i++) {
            s += angle[0][i] + " ";
        }
        s += "\nWinkel2: ";
        for (int i = 0; i < angle[1].length; i++) {
            s += angle[1][i] + " ";
        }
        s += "\nZeit: " + time + " ms";
        return s;
    }
}
