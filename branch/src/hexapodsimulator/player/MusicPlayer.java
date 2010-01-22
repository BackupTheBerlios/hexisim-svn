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

    /**
     * 
     * @param player
     * @deprecated
     */
    public MusicPlayer(AdvancedPlayer player) {
        super("MusicPlayer");
        _player = player;
    }

    public MusicPlayer(InputStream inputStream) throws JavaLayerException{
        super("MusicPlayer");
        if(running)
            throw new IllegalStateException("Player is already running");
        _player = new AdvancedPlayer(inputStream);
        running = true;
    }

    public MusicPlayer(File file) throws FileNotFoundException, JavaLayerException {
        /*super("MusicPlayer");
        if(running)
            throw new IllegalStateException("Player is already running");
        FileInputStream fileInputStream = new FileInputStream(file);
        _player = new AdvancedPlayer(fileInputStream);
        running = true;*/
        this(new FileInputStream(file));
    }

    @Override
    public void run() {
        try {
            _player.play();
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
