/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator.player;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 *
 * @author peter
 */
public class MusicPlayer extends Thread {

    private AdvancedPlayer _player;
    private boolean _running;

    public MusicPlayer(AdvancedPlayer player) {
        _player = player;
    }

    @Override
    public void run() {
        while (true) {
            if (_running) {
                try {
                    _player.play();
                } catch (JavaLayerException ex) {
                    //System.out.println("JavaLayerException while starting player.");
                }
            } else {
            }
        }
    }

    public void setRunning(boolean running) {
        if (_running = true && running == false) {
            _player.stop();
        }
        _running = running;
    }
}
