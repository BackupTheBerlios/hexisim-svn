/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 *
 * @author peter
 */
public class MusicPlayer extends Thread {

    private AdvancedPlayer _player;
    private boolean _running;

    /**
     * 
     * @param player
     * @deprecated
     */
    public MusicPlayer(AdvancedPlayer player) {
        super("MusicPlayer");
        _player = player;
    }

    public MusicPlayer(File file) throws FileNotFoundException, JavaLayerException {
        super("MusicPlayer");
        FileInputStream fileInputStream = new FileInputStream(file);
        _player = new AdvancedPlayer(fileInputStream);
    }

    @Override
    public void run() {
        try {
            _player.play();
        } catch (JavaLayerException ex) {
            //System.out.println("JavaLayerException while starting player.");
        }
    }

    public void setRunning(boolean running) {
        if (_running = true && running == false) {
            _player.stop();
        }
        _running = running;
    }
}
