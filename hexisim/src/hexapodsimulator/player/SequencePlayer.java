/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator.player;

import hexapodsimulator.GLRenderer3dModel;
import hexapodsimulator.SuperSeq;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author peter
 */
public class SequencePlayer extends Timer {

    private SuperSeq _superSeq;
    private int time;
    /*
    private static double[] dummyValues1 = {45.0, 42.038912146036004, 38.12777877886145, 33.764192063477616, 31.72509699605868, 29.111721470764607, 26.634080637486747, 24.98600670069459, 21.492724341151515, 18.04782962797035, 12.934579782886544, 11.453685932968384, 10.570531454652802, 11.398986855581143, 10.501346717990025, 7.970648352960836, 6.67955201899084, 6.890905131963606, 7.2708417173435365, 6.621684803448218, 0.24473371184240733, -5.9084771127946, -10.994149252233548, -14.27317354327053, -14.814046344572574};
    private static double[] dummyValues2 = {45.0, 47.48112257777407, 51.45868373768785, 54.32416621141718, 55.360560941126934, 56.7692807839354, 57.95259270578592, 58.068044894899835, 60.05718815705807, 60.58505541538884, 61.876255826412674, 61.547735940830506, 59.85066115645546, 56.897540506631444, 56.52033950153165, 56.85342291173409, 55.40344401710212, 52.99911796035991, 52.222746156094104, 52.92024112954763, 60.990528817832384, 67.32174236637536, 72.67848536305797, 75.52441834326922, 75.98305772912514};
     */

    public SequencePlayer(SuperSeq superSeq) {
        _superSeq = superSeq;
        // <editor-fold defaultstate="collapsed" desc="demo">
        //timer = new Timer("SequencePlayer");
/*
        for (int i = 0; i < 6; i++) {
        final int leg = i;
        for (int j = 0; j < dummyValues1.length; j++) {
        final int idx = j;
        this.schedule(new TimerTask() {

        @Override
        public void run() {
        GLRenderer3dModel.changeAngle(leg, 1, dummyValues1[idx]);
        GLRenderer3dModel.changeAngle(leg, 2, dummyValues2[idx]);
        }
        }, leg * 200 + idx * 10);
        }
        }
        for (int i = 0; i < 6; i++) {
        final int leg = i;
        for (int j = 1; j <= dummyValues1.length; j++) {
        final int idx = j;
        this.schedule(new TimerTask() {

        @Override
        public void run() {
        GLRenderer3dModel.changeAngle(leg, 1, dummyValues1[dummyValues1.length-idx]);
        GLRenderer3dModel.changeAngle(leg, 2, dummyValues2[dummyValues1.length-idx]);
        }
        }, 1200+ leg * 200 + idx * 10);
        }
        }
         */// </editor-fold>
    }

    /**
     * Loads the next angles from the SuperSequence into the timer.
     * @param count The number of values per angle to load
     * @param interval The update interval of the values in milliseconds
     */
    public void loadNext(int count, int interval) {
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 12; j++) {

                final int type = j % 2;
                final int leg = j / 2;

                double[] tempAngle = new double[3];
                for (int k = 0; k < tempAngle.length; k++) {
                    tempAngle[k] = _superSeq.getSingleElementAtTime(time + i * interval, leg, k);
                }
                final double[] angle = tempAngle;

                schedule(new TimerTask() {

                    @Override
                    public void run() {
                        if (type == 1) {
                            GLRenderer3dModel.changeAngle(leg, 0, angle[0]);
                        } else if (type == 0) {
                            GLRenderer3dModel.changeAngle(leg, 1, angle[1]);
                            GLRenderer3dModel.changeAngle(leg, 2, angle[2]);
                        }
                    }
                }, i*interval);
            }
        }
        time += count*interval;
    }
}
