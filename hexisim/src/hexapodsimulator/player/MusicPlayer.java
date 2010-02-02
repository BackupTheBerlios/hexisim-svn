/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

/**
 *
 * @author peter
 */
public class MusicPlayer extends Thread {

    private AdvancedPlayer _player;
    public static boolean running;  // make this static to allow only one player
    private int frame;

    /**
     * 
     * @param player
     * @deprecated
     */
    public MusicPlayer(AdvancedPlayer player) {
        super("MusicPlayer");
        _player = player;
    }

    public MusicPlayer(InputStream inputStream) throws JavaLayerException {
        super("MusicPlayer");
        if(running)
            throw new IllegalStateException("Player is already running");
        frame = 0;
        _player = new AdvancedPlayer(inputStream);
        running = true;
    }

    public MusicPlayer(InputStream inputStream, int startFrame) throws JavaLayerException {
        this(inputStream);
        frame = startFrame;
    }

    public MusicPlayer(File file) throws FileNotFoundException, JavaLayerException {
        this(new FileInputStream(file));
    }

    /*public void jumpToFrame(int frame) {
        if(running) {
            AdvancedPlayer tempPlayer = null;
            _player.close();
            _player = tempPlayer;
            _frame = frame;
            start();
        }
    }*/

    @Override
    public void run() {
        try {
            _player.play(frame, Integer.MAX_VALUE);
        } catch (JavaLayerException ex) {
            //System.out.println("JavaLayerException while starting player.");
        }
    }

    public void closePlayer() {
        if (running) {
            _player.close();
        }
        running = false;
    }
}
