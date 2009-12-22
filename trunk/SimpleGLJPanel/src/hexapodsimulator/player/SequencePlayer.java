/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator.player;

import de.jaret.util.date.JaretDate;
import hexapodsimulator.GLRenderer3dModel;
import hexapodsimulator.HexiSequenz;
import hexapodsimulator.SuperSeq;
import hexapodsimulator.timebar.ModelCreator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author peter
 */
public class SequencePlayer {

    private SuperSeq _superSeq;
    private Timer timer;

    public SequencePlayer(SuperSeq superSeq) {
        _superSeq = superSeq;
        timer = new Timer("SequencePlayer");
        for (int i = 0; i < 90000; i++) {
            for (int j = 0; j < 12; j++) {

                final int type = j % 2;
                final int leg = j / 2;

                double tempAngle[] = new double[3];
                for (int k = 0; k < tempAngle.length; k++) {
                    try {
                        tempAngle[k] = _superSeq.getSingleElementAtTime(j, leg, k);
                    } catch (Exception ex) {
                        tempAngle[k] = 0;
                    }
                }
                final double[] angle = tempAngle;

                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (type == 1) {
                            GLRenderer3dModel.changeAngle(leg, 0, angle[0]);
                        } else if (type == 0) {
                            GLRenderer3dModel.changeAngle(leg, 1, angle[1]);
                            GLRenderer3dModel.changeAngle(leg, 2, angle[2]);
                        }
                    }
                }, i);
            }
        }
    }
    */
}
