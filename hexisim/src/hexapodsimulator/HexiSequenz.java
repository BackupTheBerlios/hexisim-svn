package hexapodsimulator;

import java.io.*;
import java.util.Vector;
//import javax.vecmath.Point3d;

/**
 * Beschreibt eine Bewegungssequenz f�r den Hexapodsimulator.<br />
 * <b>time:</b> Zeit in ms<br />
 * <b>angle[2][]:</b> Array beliebiger L�nge von 2 Winkeln
 *
 * @author gerhard
 */
public class HexiSequenz implements Serializable {

    static final long serialVersionUID = 280051512980290226L;

    /**
     * Array beliebiger L�nge von 2 Winkeln<br />
     * [die beiden Winkel][wieviele]
     */
    //private double[][] angle;    //[Winkel1, 2][�ber Zeit]
    private Vector<double[]> angle;
    /**
     * Zeit in Millisekunden
     */
    private int time;
    /**
     * Name of the sequence
     */
    private String name;
    /**
     * Anzahl Winkel pro Position
     */
    private int ValueCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(int time) {
        this.time = time;
    }

    /**
     *
     * @return the amount of Values
     */
    public int getLength() {
        return angle.size();
    }

    public double[][] getAngle() {
        return (double[][]) angle.toArray();
    }

    public double[] getAngle(int position) {
        //double[] tmp = new double[2];
        //tmp[0] = angle[0][position];
        //tmp[1] = angle[1][position];
        //return tmp;
        return angle.elementAt(position);
    }

    /**
     *
     * @return die Zeitdauer der Sequenz
     */
    public int getTime() {
        return time;
    }

    public HexiSequenz(int time, double angle1, double angle2) {
        this.time = time;
        angle = new Vector();
        //angle = new double[2][];
        //angle[0] = angle1.clone();
        //angle[1] = angle2.clone();
        angle.addElement(new double[]{angle1, angle2});
        ValueCount = angle.firstElement().length;
    }

    public HexiSequenz() {
        angle = new Vector();
        //angle = new double[2][];
        //angle[0] = new double[0];
        //angle[0] = new double[0];
        ValueCount = 0;
    }

    public HexiSequenz(Vector<double[]> angle) {
        this.angle = (Vector<double[]>) angle.clone();
        ValueCount = angle.firstElement().length;
    }

    public void addContent(double angle1, double angle2) {
        if (angle.size() == 0) {
            ValueCount = 2;
        }
        if (ValueCount == 2) {
            angle.addElement((double[]) new double[]{angle1, angle2});
        } else {
            throw new IllegalStateException("Object is initialised to " + ValueCount
                    + " Values per position.");
        }
    }

    /**
     *
     * @param angles Nach Möglichkeit 2 Winkel eines "Zeitpunktes"
     * andere Werte gehen technisch auch, machen aber z.B. bei toString() Probleme
     */
    public void addContent(double[] angles) {
        if (angle.size() == 0) {
            ValueCount = angles.length;
        }
        if (ValueCount == angles.length) {
            angle.add(angles);
        } else {
            throw new IllegalStateException("Object is initialised to " + ValueCount
                    + " Values per position.");
        }
    }

    /**
     * Für die Verwendung mit nur einem Winkel
     * @param anglee
     */
    public void addContent(double anglee) {
        if (angle.size() == 0) {
            ValueCount = 1;
        }
        if (ValueCount == 1) {
            angle.add((double[]) (new double[]{anglee}));
        } else {
            throw new IllegalStateException("Object is initialised to " + ValueCount
                    + " Values per position.");
        }
    }

    /**
     * Removes all values of the vector.
     */
    public void clear() {
        angle.clear();
    }

    /**
     * Removes repeated values from the vector
     * and trims the capacity of the vector to its size.
     */
    public void clean() {
        Vector<double[]> cleanedAngles = new Vector<double[]>();
        if (ValueCount == 1) {
            for (int i = 0; i < (angle.size() - 1); i++) {
                if (angle.elementAt(i)[0] != angle.elementAt(i + 1)[0]) {
                    cleanedAngles.add(angle.elementAt(i));
                }
            }
        } else if (ValueCount == 2) {
            for (int i = 0; i < (angle.size() - 1); i++) {
                if (angle.elementAt(i)[0] != angle.elementAt(i + 1)[0]
                        || angle.elementAt(i)[1] != angle.elementAt(i + 1)[1]) {
                    cleanedAngles.add(angle.elementAt(i));
                }
            }
        }
        angle = cleanedAngles;
        angle.trimToSize();
    }

    public void normalize() {
        Vector<double[]> normalizedAngles = new Vector<double[]>();
        if (ValueCount == 1) {
            boolean rising = false;
            double[] prevAngle = new double[1];
            for (int i = 0; i < (angle.size() - 1); i++) {
                prevAngle = angle.elementAt(i);
                if (i == 0
                        || rising && angle.elementAt(i + 1)[0] < (prevAngle[0] - 3)
                        || !rising && angle.elementAt(i + 1)[0] > (prevAngle[0] + 3)) {
                    normalizedAngles.add(prevAngle);
                }
                if(rising && angle.elementAt(i+1)[0] < (prevAngle[0]-3))
                    rising = false;
                else if(!rising && angle.elementAt(i+1)[0] > (prevAngle[0]+3))
                    rising = true;
            }
            normalizedAngles.add(angle.lastElement());
        } else if (ValueCount == 2) {
            boolean[] rising = new boolean[2];
            double[] prevAngle = new double[2];
            for (int i = 0; i < (angle.size() - 1); i++) {
                prevAngle = angle.elementAt(i);
                if (i == 0
                        || rising[0] && angle.elementAt(i + 1)[0] < (prevAngle[0] - 3)
                        || !rising[0] && angle.elementAt(i + 1)[0] > (prevAngle[0] + 3)
                        || rising[1] && angle.elementAt(i + 1)[1] < (prevAngle[1] - 3)
                        || !rising[1] && angle.elementAt(i + 1)[1] > (prevAngle[1] + 3)) {
                    normalizedAngles.add(prevAngle);
                }
                if(rising[0] && angle.elementAt(i+1)[0] < (prevAngle[0]-3))
                    rising[0] = false;
                else if(!rising[0] && angle.elementAt(i+1)[0] > (prevAngle[0]+3)
                        || !rising[0] && i==1 && angle.elementAt(i+1)[0] > prevAngle[0])
                    rising[0] = true;
                if(rising[1] && angle.elementAt(i+1)[1] < (prevAngle[1]-3))
                    rising[1] = false;
                else if(!rising[1] && angle.elementAt(i+1)[1] > (prevAngle[1]+3)
                        || !rising[1] && i==1 && angle.elementAt(i+1)[1] > prevAngle[1])
                    rising[1] = true;
            }
            normalizedAngles.add(angle.lastElement());
        }
        angle = normalizedAngles;
        angle.trimToSize();
    }

    @Override
    public String toString() {
        String s = getClass().getName();
        for (int i = 0; i < ValueCount; i++) {
            s += "\nWinkel" + Integer.toString(i + 1) + ": ";
            for (int j = 0; j < angle.size(); j++) {
                s += angle.elementAt(j)[i] + " ";
            }
        }

        s += "\nZeit: " + time + " ms";
        return s;
    }

    /**
     *get
     * @param stepTime Üblicherweise 10ms, die Zeit f
     * @return
     */
    private Vector<double[]> toSteps(int stepTime) {
        /*Vector<double []> V = new Vector<double[]>( (int) time / stepTime );
        double[] gesLen = new double[ValueCount];
        for (int i = 0; i < angle.size() - 1; i++) {
        for (int j = 0; j < gesLen.length; j++) {
        gesLen[j] = ;
        
        }
        
        }
        for (int i = 0; i < V.size(); i++) {
        //V.addElement(new double[angle.firstElement().length]);
        double[] temp = new double[angle.firstElement().length];
        for (int j = 0; j < angle.firstElement().length; j++) {
        //temp[j] =
        }
        //V.addElement(new double[angle]);
        V.addElement(temp);
        }
        return V;*/
        return null;
    }

    private Vector<double[]> toAngleSteps(int stepTime) {
        Vector<double[]> V = new Vector<double[]>((int) time / stepTime);
        double[] gesLen = new double[ValueCount];

        for (int j = 0; j < gesLen.length; j++) {
            for (int i = 0; i < angle.size() - 1; i++) {
                gesLen[j] += Math.abs(angle.elementAt(i)[j] - angle.elementAt(i + 1)[j]);
            }

        }

        double[] temp;
        double tmp;
        for (int i = 0; i < V.capacity(); i++) {

            tmp = 0;
            //V.addElement(new double[angle.firstElement().length]);
            temp = new double[ValueCount];
            for (int j = 0; j < angle.firstElement().length; j++) {
                tmp = gesLen[j] * ((double) i / (double) V.capacity());
                for (int k = 0; k < angle.size() - 1; k++) {
                    tmp -= Math.abs(angle.elementAt(k)[j] - angle.elementAt(k + 1)[j]);
                    /*if (temp[j] > 0) {
                    temp[j] -= Math.abs(angle.elementAt(k)[j] - angle.elementAt(k + 1)[j]);
                    } else {
                    if (tmp < 0) {
                    temp[j] *= -1;
                    }
                    temp[j] += angle.elementAt(k)[j];
                    break;
                    }
                     */
                    if (tmp > 0) {
                        temp[j] += angle.elementAt(k)[j] - angle.elementAt(k + 1)[j];
                    } else {
                        //temp[j] += gesLen[j] * ((double) i / (double) V.capacity());
                    }

                }
            }
            V.addElement(temp);
        }
        System.out.println("--- " + V.capacity() + " --- " + V.size() + " ---");
        return V;
    }

    private Vector<double[]> toCoords() {
        return null;
    }

    /**
     *
     * @param angles [3]:
     * @param leg 0-5
     * @param r Radius Board
     * @param b1 length of first leg-part
     * @param b2 length of second leg-part
     * @return
     */
    static public double[] toCoords(double[] angles, int leg,
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
     * @param angles [3]:
     * @param leg 0-5, like in Sandbox Documentation
     * @return Endpunkt (Fuß)
     */
    static public double[] toCoords(double[] angles, int leg) {
        return toCoords(angles, leg, 30, 4, 4);
    }
    /*
    public static void main(String args[]) {
    HexiSequenz a = new HexiSequenz();
    a.setTime(50);
    a.addContent(123, 13);
    a.addContent(53, 4521);
    a.addContent(55, 6423);
    //System.out.println(a);
    HexiSequenz b = new HexiSequenz();
    b.addContent(123);
    b.addContent(345);
    b.addContent(4);
    b.addContent(32);
    b.addContent(45);
    b.addContent(78);
    b.setTime(100);
    HexiSequenz c = new HexiSequenz(b.toAngleSteps(10));

    //Test der Point3d (einfach) -- ok, wohl doch ned
    Point3d p = new Point3d();

    System.out.println(b);
    System.out.println(c);
    }*/
}
