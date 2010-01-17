/*
 * HexapodSimulator.java
 *
 * Created on 30. Juli 2008, 16:18
 */
package hexapodsimulator;

import com.sun.opengl.util.Animator;
import de.jaret.util.date.JaretDate;
import de.jaret.util.ui.timebars.TimeBarMarker;
import de.jaret.util.ui.timebars.swing.TimeBarViewer;
import hexapodsimulator.player.MusicPlayer;
import hexapodsimulator.player.SequencePlayer;
import hexapodsimulator.timebar.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javazoom.jl.decoder.JavaLayerException;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import sun.org.mozilla.javascript.internal.ObjArray;

/**
 * Main class for the Hexapod Simulator
 * @author peter
 * @author cylab
 * @author mbien
 */
public class HexapodSimulator extends JFrame {

    private HexapodSimulatorProperties properties;
    private Animator animator;
    private ActionListener updater;
    private Timer timer;
    private HexiSequenz ftSequence, cSequence;
    private Vector<HexiSequenz> sequenceVector;
    private SuperSeq superSeq;
    private File musicFile;
    private boolean musicFileIsTempFile;
    private InputStream musicInputStream;
    private MusicPlayer musicPlayer;
    private SequencePlayer sequencePlayer;
    private java.util.Timer timeBarMarkerTimer;
    private java.util.Timer previewTimer;
    private boolean projectChanged;

    /** Creates new form MainFrame */
    public HexapodSimulator() {
        initComponents();
        setTitle("Hexapod Simulator");

        properties = new HexapodSimulatorProperties();
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("properties.dat"));
            properties = (HexapodSimulatorProperties) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException ex) {
            ObjectOutputStream objectOutputStream;
            try {
                objectOutputStream = new ObjectOutputStream(new FileOutputStream("properties.dat"));
                objectOutputStream.writeObject(properties);
            } catch (FileNotFoundException ex1) {
            } catch (IOException ex1) {
            }
        } catch (IOException ex) {
            System.out.println("IO Exception while reading properties file.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class HexapodSimulatorProperties not found.");
        }
        normalizeInputCheckBoxMenuItem.setState(properties.normalizeInput);
        interpolateOutputCheckBoxMenuItem.setState(properties.interpolateOutput);

        panel3dModel.addGLEventListener(new GLRenderer3dModel());
        animator = new Animator(panel3dModel);

        panelFemurTibia.addGLEventListener(new GLRendererFemurTibia());
        panelFemurTibia.addMouseListener(new GLRendererFemurTibia());
        panelFemurTibia.addMouseMotionListener(new GLRendererFemurTibia());

        panelCoxa.addGLEventListener(new GLRendererCoxa());
        panelCoxa.addMouseMotionListener(new GLRendererCoxa());

        /*addKeyListener(new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent ke) {
        System.out.println("a");
        }
        });*/

        updater = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                panel3dModel.repaint();
                panelFemurTibia.repaint();
                panelCoxa.repaint();

                if (ftCaptureButton.isSelected()) {
                    /*double[] prevAngle = new double[2];
                    if (ftSequence.getLength() > 0) {
                        prevAngle = ftSequence.getAngle(ftSequence.getLength() - 1).clone();
                    }*/

                    /*if (ftSequence.getLength() > 0
                            && prevAngle[0] != GLRendererFemurTibia.angle[0]
                            && prevAngle[1] != GLRendererFemurTibia.angle[1]
                            || ftSequence.getLength() == 0) {*/
                        ftSequence.addContent(GLRendererFemurTibia.angle[0], GLRendererFemurTibia.angle[1]);
                        //System.out.println(GLRendererFemurTibia.angle[0] + ", " + GLRendererFemurTibia.angle[1]);
                    //}
                    //System.out.println(ftSequence);
                }

                if (cCaptureButton.isSelected()) {
                    /*double prevAngle[] = new double[2];
                    if (cSequence.getLength() > 0) {
                        prevAngle = cSequence.getAngle(cSequence.getLength() - 1).clone();
                    }

                    if (cSequence.getLength() > 0
                            && prevAngle[0] != GLRendererCoxa.angle
                            || cSequence.getLength() == 0) {*/
                        cSequence.addContent(GLRendererCoxa.angle, 0);
                    //}
                }
            }
        };
        timer = new Timer(50, updater);

        ftSequence = new HexiSequenz();
        cSequence = new HexiSequenz();
        sequenceVector = new Vector<HexiSequenz>();
        superSeq = new SuperSeq();

        HexapodSimulatorProjectDataPart project = new HexapodSimulatorProjectDataPart();

        if (properties != null && properties.projectFile != null) {
            try {
                openProjectFile(properties.projectFile);
            } catch (FileNotFoundException ex) {
                System.out.println("Project file not found.");
            } catch (IOException ex) {
                System.out.println("IO Exception while reading the project file");
                System.out.println(ex);
            } catch (ClassNotFoundException ex) {
                System.out.println("Class not found.");
                System.out.println(ex);
            } catch (JavaLayerException ex) {
                System.out.println("JavaLayer exception while opening project file");
            }
        }
        superSeq.interpolate = properties.interpolateOutput == true ? 1 : 0;
        System.out.println(sequenceVector);

        new DropTarget(timeBarViewer1, new DropTargetListener() {

            public void dragEnter(DropTargetDragEvent dtde) {
            }

            public void dragOver(DropTargetDragEvent dtde) {
                Point loc = dtde.getLocation();
                try {
                    timeBarViewer1.highlightRow(timeBarViewer1.getRowForXY(loc.x, loc.y));
                } catch (RuntimeException ex) {
                }
            }

            public void dropActionChanged(DropTargetDragEvent dtde) {
            }

            public void dragExit(DropTargetEvent dte) {
                timeBarViewer1.repaint();   // clear row highlighting
            }

            public void drop(DropTargetDropEvent dtde) {
                Point loc = dtde.getLocation();
                String name;
                try {
                    name = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    SequenceTimebarRowModel row = ((SequenceTimebarRowModel) timeBarViewer1.getRowForXY(loc.x, loc.y));
                    int seconds = timeBarViewer1.dateForXY(loc.x, loc.y).diffSeconds(new JaretDate(1, 1, 1970, 1, 0, 0));
                    int milliseconds = timeBarViewer1.dateForXY(loc.x, loc.y).getMillis();
                    if (!row.getType().equals(name.split("_")[0])) {    //right type? (ft/c)
                        timeBarViewer1.deHighlightRow();
                        dtde.rejectDrop();
                    } else {
                        seconds = (seconds < 0) ? 0 : seconds;
                        JaretDate begin = new JaretDate(0);
                        begin.advanceSeconds(seconds).advanceMillis(milliseconds);
                        EventInterval interval = new EventInterval(begin.copy(), begin.copy().advanceMillis(getSequenceByName(name).getTime()));
                        interval.setTitle(name);
                        if (ModelCreator.addInterval(row.getID(), interval) == -1) {
                            timeBarViewer1.deHighlightRow();
                            dtde.rejectDrop();
                        } else {
                            superSeq.addSeq(getSequenceByName(name), seconds * 1000 + milliseconds, row.getSecID(), (row.getType().equals("ft") ? 0 : 1));
                            /*for (int abc = 0; abc < 90000; abc++) {
                            try {
                            System.out.print(superSeq.getSingleElementAtTime(abc, 0, 1) + ", ");
                            } catch (Exception ex) {
                            System.out.println(ex);
                            }
                            }*/
                            /*
                            try {
                            superSeq.angletofile("angles.txt");
                            } catch (Exception ex) {
                            System.out.println("error");
                            }*/
                            timeBarViewer1.setModel(ModelCreator.createModel());
                            dtde.acceptDrop(dtde.getDropAction());
                            dtde.dropComplete(true);
                            projectChanged = true;
                        }
                    }
                } catch (UnsupportedFlavorException ex) {
                    System.out.println("Unsupported Flavor");
                } catch (IOException ex) {
                    System.out.println("IO Exception while getting data from clipboard.");
                } catch (NullPointerException ex) {
                    dtde.rejectDrop();
                    System.out.println("Drop position is out of range.");
                } finally {
                    timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                }
            }
        });

        new DropTarget(panelFemurTibia, new DropTargetAdapter() {

            @Override
            public void dragExit(DropTargetEvent dte) {
                hintFemurTibia.setVisible(false);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                try {
                    if (((String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor)).split("_")[0].equals("ft")) {
                        hintFemurTibia.setVisible(true);
                    }
                } catch (UnsupportedFlavorException ex) {
                    System.out.println("Unsupported Flavor");
                } catch (IOException ex) {
                    System.out.println("IO Exception while getting data from clipboard.");
                }
            }

            public void drop(DropTargetDropEvent dtde) {
                try {
                    String name = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    if (!name.split("_")[0].equals("ft")) {
                        dtde.rejectDrop();
                        return;
                    }
                    SuperSeq seq = new SuperSeq();
                    seq.addSeq(getSequenceByName(name), 0, 0, 0);
                    final SuperSeq fSeq = seq;
                    if (previewTimer != null) {
                        previewTimer.cancel();
                    }
                    previewTimer = new java.util.Timer();
                    for (int i = 0; i < seq.getSeq(0, 0, 0).getTime(); i++) {
                        final int time = i;
                        previewTimer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                GLRendererFemurTibia.angle[0] = fSeq.getSingleElementAtTime(time, 0, 1);
                                GLRendererFemurTibia.angle[1] = fSeq.getSingleElementAtTime(time, 0, 2);
                            }
                        }, i);
                    }
                    dtde.acceptDrop(dtde.getDropAction());
                    dtde.dropComplete(true);
                } catch (UnsupportedFlavorException ex) {
                    System.out.println("Unsupported Flavor");
                } catch (IOException ex) {
                    System.out.println("IO Exception while getting data from clipboard.");
                } finally {
                    hintFemurTibia.setVisible(false);
                }
            }
        });

        new DropTarget(panelCoxa, new DropTargetAdapter() {

            @Override
            public void dragExit(DropTargetEvent dte) {
                hintCoxa.setVisible(false);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                try {
                    if (((String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor)).split("_")[0].equals("c")) {
                        hintCoxa.setVisible(true);
                    }
                } catch (UnsupportedFlavorException ex) {
                    System.out.println("Unsupported Flavor");
                } catch (IOException ex) {
                    System.out.println("IO Exception while getting data from clipboard.");
                }
            }

            public void drop(DropTargetDropEvent dtde) {
                try {
                    String name = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    if (!name.split("_")[0].equals("c")) {
                        dtde.rejectDrop();
                        return;
                    }
                    SuperSeq seq = new SuperSeq();
                    seq.addSeq(getSequenceByName(name), 0, 0, 1);
                    final SuperSeq fSeq = seq;
                    if (previewTimer != null) {
                        previewTimer.cancel();
                    }
                    previewTimer = new java.util.Timer();
                    for (int i = 0; i < seq.getSeq(0, 1, 0).getTime(); i++) {
                        final int time = i;
                        previewTimer.schedule(new TimerTask() {

                            @Override
                            public void run() {
                                GLRendererCoxa.angle = fSeq.getSingleElementAtTime(time, 0, 0);
                            }
                        }, i);
                    }
                    dtde.acceptDrop(dtde.getDropAction());
                    dtde.dropComplete(true);
                } catch (UnsupportedFlavorException ex) {
                    System.out.println("Unsupported Flavor");
                } catch (IOException ex) {
                    System.out.println("IO Exception while getting data from clipboard.");
                } finally {
                    hintCoxa.setVisible(false);
                }
            }
        });

        timeBarViewer1.addMouseListener(new MouseListener() {

            private EventInterval tempInterval = null;
            private SequenceTimebarRowModel tempRow = null;

            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                JaretDate date = timeBarViewer1.dateForXY(e.getX(), e.getY());
                SequenceTimebarRowModel row = ((SequenceTimebarRowModel) timeBarViewer1.getRowForXY(e.getX(), e.getY()));
                int rowIndex = row.getID();
                int intervalIndex = ModelCreator.getIntervalIndexAtDate(rowIndex, date);

                if (intervalIndex < 0 && row.getType().equals("music")) {
                    try {
                        JFileChooser fc = new JFileChooser();
                        fc.showOpenDialog(panel3dModel);
                        File f = fc.getSelectedFile();
                        musicFile = f;
                        musicFileIsTempFile = false;
                        musicInputStream = new FileInputStream(f);

                        MP3File mp3file = new MP3File(f);
                        String songTitle, artist;
                        if (mp3file.hasID3v2Tag()) {
                            AbstractID3v2 id3v2Tag = mp3file.getID3v2Tag();
                            songTitle = id3v2Tag.getSongTitle();
                            artist = id3v2Tag.getLeadArtist();
                            //String songLength = (String) id3v2Tag.getFrame("TLEN").getBody().getObject("Text");
                        } else if (mp3file.hasID3v1Tag()) {
                            ID3v1 id3v1Tag = mp3file.getID3v1Tag();
                            songTitle = id3v1Tag.getSongTitle();
                            artist = id3v1Tag.getArtist();
                        } else {
                            songTitle = "";
                            artist = "";
                        }
                        long songLength = (f.length() - mp3file.getMp3StartByte()) * 8 / mp3file.getBitRate();  // millis

                        JaretDate begin = new JaretDate(0);
                        EventInterval musicInterval = new EventInterval(begin.copy(), begin.copy().advanceMillis(songLength));
                        musicInterval.setTitle(artist + " - " + songTitle);
                        ModelCreator.addInterval(rowIndex, musicInterval);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                        projectChanged = true;
                    } catch (FileNotFoundException ex) {
                        System.out.println("File not found.");
                    } catch (TagException ex) {
                        System.out.println("Tag Exception while reading ID3 Tags.");
                    } catch (IOException ex) {
                        System.out.println("IO Exception while calculating Song Length.");
                    }
                    return;
                }

                if (e.getButton() == MouseEvent.BUTTON1) {  // leftclick
                    if(row.getType().equals("music")) {
                        return;
                    }
                    tempInterval = ModelCreator.getInterval(rowIndex, intervalIndex);
                    tempRow = row;
                    try {
                        superSeq.delSeq(tempInterval.getBegin().getMinutes() * 60000 + tempInterval.getBegin().getSeconds() * 1000 + tempInterval.getBegin().getMillis(), row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                    ModelCreator.remInterval(rowIndex, intervalIndex);
                    timeBarViewer1.setModel(ModelCreator.createModel());
                    timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                } else if (e.getButton() == MouseEvent.BUTTON3) {   // rightclick
                    SequenceTimebarDialog dialog = new SequenceTimebarDialog((Frame) jPanel1.getRootPane().getParent(), row.getType().equals("music") ? false : true);
                    JaretDate oldBeginDate = ModelCreator.getInterval(rowIndex, intervalIndex).getBegin();
                    dialog.setDate(oldBeginDate.getDate());
                    dialog.setLocation(100, 100);
                    dialog.setVisible(true);
                    if (dialog.getCancelled() || dialog.getDate() == null) {
                        return;
                    }
                    if (dialog.getDeleted()) {
                        ModelCreator.remInterval(rowIndex, intervalIndex);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                        if(row.getType().equals("music")) {
                            if(musicFileIsTempFile) {
                                musicFile.delete();
                            }
                            musicFile = null;
                            musicInputStream = null;
                            return;
                        }
                        try {
                            superSeq.delSeq(oldBeginDate.getMinutes() * 60000 + oldBeginDate.getSeconds() * 1000 + oldBeginDate.getMillis(), row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                            projectChanged = true;
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }
                        return;
                    }
                    JaretDate newBeginDate = new JaretDate(dialog.getDate());

                    tempInterval = ModelCreator.getInterval(rowIndex, intervalIndex);
                    ModelCreator.remInterval(rowIndex, intervalIndex);
                    try {
                        superSeq.delSeq(oldBeginDate.getMinutes() * 60000 + oldBeginDate.getSeconds() * 1000 + oldBeginDate.getMillis(), row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                    long duration = tempInterval.getMillis();
                    tempInterval.setBegin(newBeginDate);
                    tempInterval.setEnd(newBeginDate.copy().advanceMillis(duration));
                    if (ModelCreator.addInterval(rowIndex, tempInterval) != -1) {
                        superSeq.addSeq(getSequenceByName(tempInterval.getTitle()), newBeginDate.getMinutes() * 60000 + newBeginDate.getSeconds() * 1000 + newBeginDate.getMillis(), row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                        projectChanged = true;
                    } else {
                        tempInterval.setBegin(oldBeginDate);
                        tempInterval.setEnd(oldBeginDate.copy().advanceMillis(duration));
                        ModelCreator.addInterval(rowIndex, tempInterval);
                        superSeq.addSeq(getSequenceByName(tempInterval.getTitle()), oldBeginDate.getMinutes() * 60000 + oldBeginDate.getSeconds() * 1000 + oldBeginDate.getMillis(), row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                    }

                    tempInterval = null;
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (tempInterval != null) {
                    SequenceTimebarRowModel row = (SequenceTimebarRowModel) timeBarViewer1.rowForY(e.getY());
                    if(row.getType().equals("music")) {
                        return;
                    }
                    int rowIndex = row.getID();
                    JaretDate oldBeginDate = tempInterval.getBegin();
                    long duration = tempInterval.getMillis();
                    int seconds = timeBarViewer1.dateForXY(e.getX(), e.getY()).diffSeconds(new JaretDate(1, 1, 1970, 1, 0, 0));
                    int milliseconds = timeBarViewer1.dateForXY(e.getX(), e.getY()).getMillis();
                    seconds = (seconds < 0) ? 0 : seconds;
                    JaretDate begin = new JaretDate(0);
                    begin.advanceSeconds(seconds).advanceMillis(milliseconds);

                    tempInterval.setBegin(begin);
                    tempInterval.setEnd(begin.copy().advanceMillis(duration));

                    if (ModelCreator.addInterval(rowIndex, tempInterval) != -1
                            && row.getType().equals(tempRow.getType())) {
                        superSeq.addSeq(getSequenceByName(tempInterval.getTitle()), seconds * 1000 + milliseconds, row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                        projectChanged = true;
                    } else {
                        tempInterval.setBegin(oldBeginDate);
                        tempInterval.setEnd(oldBeginDate.copy().advanceMillis(duration));
                        ModelCreator.addInterval(tempRow.getID(), tempInterval);
                        superSeq.addSeq(getSequenceByName(tempInterval.getTitle()), seconds * 1000 + milliseconds, tempRow.getSecID(), tempRow.getType().equals("ft") ? 0 : 1);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                    }

                    tempRow = null;
                    tempInterval = null;
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        double[][] angle = {{0, 45, 45}, {0, 45, 45}, {0, 45, 45}, {0, 45, 45}, {0, 45, 45}, {0, 45, 45}};
        GLRenderer3dModel.setAngle(angle);

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (!ensureProjectSaving()) {
                        return;
                    }
                    ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream("properties.dat"));
                    os.writeObject(properties);
                    os.close();
                } catch (FileNotFoundException ex) {
                    System.out.println("Properties File not found.");
                } catch (IOException ex) {
                    System.out.println("IO Exception while writing properties file");
                    System.out.println(ex);
                }
                
                if(musicFileIsTempFile) {
                    musicFile.delete();
                }

                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        /*jSlider1.addChangeListener(new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
        double[][] Angle = new double[6][3];
        GLRenderer3dModel.getAngle(Angle);
        for (int i = 0; i < 6; i++) {
        Angle[i][0] = jSlider1.getValue();
        }
        GLRenderer3dModel.setAngle(Angle);

        panel.repaint();
        }
        });

        jSlider2.addChangeListener(new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
        double[][] Angle = new double[6][3];
        GLRenderer3dModel.getAngle(Angle);
        for (int i = 0; i < 6; i++) {
        Angle[i][1] = jSlider2.getValue();
        }
        GLRenderer3dModel.setAngle(Angle);

        panel.repaint();
        }
        });

        jSlider3.addChangeListener(new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
        double[][] Angle = new double[6][3];
        GLRenderer3dModel.getAngle(Angle);
        for (int i = 0; i < 6; i++) {
        Angle[i][2] = jSlider3.getValue();
        }
        GLRenderer3dModel.setAngle(Angle);

        panel.repaint();
        }
        });*/

    }

    @Override
    public void setVisible(boolean show) {
        if (!show) {
            animator.stop();
        }
        super.setVisible(show);
        if (!show) {
            animator.start();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new JPanel();
        panel3dModel = new GLJPanel();
        panelFemurTibia = new GLJPanel();
        hintFemurTibia = new JLabel();
        hintFemurTibia.setVisible(false);
        kneeModeLabel = new JLabel();
        panelCoxa = new GLJPanel();
        hintCoxa = new JLabel();
        hintCoxa.setVisible(false);
        rotationSlider = new JSlider();
        jCheckBox1 = new JCheckBox();
        jCheckBox2 = new JCheckBox();
        jCheckBox3 = new JCheckBox();
        jCheckBox4 = new JCheckBox();
        jCheckBox5 = new JCheckBox();
        jCheckBox6 = new JCheckBox();
        jButton1 = new JButton();
        jButton2 = new JButton();
        jButton3 = new JButton();
        jButton4 = new JButton();
        jButton5 = new JButton();
        jButton6 = new JButton();
        jScrollPane1 = new JScrollPane();
        sequenceList = new JList();
        ftCaptureButton = new JToggleButton();
        timeBarViewer1 = new TimeBarViewer(null, false, true);
        cCaptureButton = new JToggleButton();
        deleteButton = new JButton();
        playButton = new JButton();
        stopButton = new JButton();
        jMenuBar1 = new JMenuBar();
        fileMenu = new JMenu();
        openProjectMenuItem = new JMenuItem();
        closeProjectMenuItem = new JMenuItem();
        jSeparator1 = new JSeparator();
        saveProjectMenuItem = new JMenuItem();
        saveProjectAsMenuItem = new JMenuItem();
        jSeparator2 = new JSeparator();
        importSequencesMenuItem = new JMenuItem();
        jSeparator3 = new JSeparator();
        exportMenuItem = new JMenuItem();
        propertiesMenu = new JMenu();
        normalizeInputCheckBoxMenuItem = new JCheckBoxMenuItem();
        interpolateOutputCheckBoxMenuItem = new JCheckBoxMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GroupLayout panel3dModelLayout = new GroupLayout(panel3dModel);
        panel3dModel.setLayout(panel3dModelLayout);
        panel3dModelLayout.setHorizontalGroup(
            panel3dModelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 468, Short.MAX_VALUE)
        );
        panel3dModelLayout.setVerticalGroup(
            panel3dModelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 369, Short.MAX_VALUE)
        );

        panelFemurTibia.setPreferredSize(new Dimension(307, 307));

        hintFemurTibia.setForeground(Color.orange);
        hintFemurTibia.setHorizontalAlignment(SwingConstants.CENTER);
        hintFemurTibia.setText("Drop here to get a preview");

        kneeModeLabel.setForeground(new Color(255, 255, 255));
        kneeModeLabel.setText("Knee up");
        kneeModeLabel.setName("kneeMode"); // NOI18N

        GroupLayout panelFemurTibiaLayout = new GroupLayout(panelFemurTibia);
        panelFemurTibia.setLayout(panelFemurTibiaLayout);
        panelFemurTibiaLayout.setHorizontalGroup(
            panelFemurTibiaLayout.createParallelGroup(GroupLayout.LEADING)
            .add(panelFemurTibiaLayout.createSequentialGroup()
                .add(panelFemurTibiaLayout.createParallelGroup(GroupLayout.LEADING)
                    .add(kneeModeLabel)
                    .add(panelFemurTibiaLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(hintFemurTibia, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panelFemurTibiaLayout.setVerticalGroup(
            panelFemurTibiaLayout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, panelFemurTibiaLayout.createSequentialGroup()
                .addContainerGap(148, Short.MAX_VALUE)
                .add(hintFemurTibia)
                .add(127, 127, 127)
                .add(kneeModeLabel))
        );

        panelCoxa.setPreferredSize(new Dimension(307, 307));

        hintCoxa.setForeground(Color.orange);
        hintCoxa.setHorizontalAlignment(SwingConstants.CENTER);
        hintCoxa.setText("Drop here to get a preview");

        GroupLayout panelCoxaLayout = new GroupLayout(panelCoxa);
        panelCoxa.setLayout(panelCoxaLayout);
        panelCoxaLayout.setHorizontalGroup(
            panelCoxaLayout.createParallelGroup(GroupLayout.LEADING)
            .add(panelCoxaLayout.createSequentialGroup()
                .addContainerGap()
                .add(hintCoxa, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelCoxaLayout.setVerticalGroup(
            panelCoxaLayout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, panelCoxaLayout.createSequentialGroup()
                .addContainerGap(147, Short.MAX_VALUE)
                .add(hintCoxa)
                .add(144, 144, 144))
        );

        rotationSlider.setMaximum(360);
        rotationSlider.setValue(0);
        rotationSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                rotationSliderStateChanged(evt);
            }
        });

        jCheckBox1.setName("1"); // NOI18N
        jCheckBox1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jCheckBoxActionPerformed(evt);
            }
        });

        jCheckBox2.setName("2"); // NOI18N
        jCheckBox2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jCheckBoxActionPerformed(evt);
            }
        });

        jCheckBox3.setName("3"); // NOI18N
        jCheckBox3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jCheckBoxActionPerformed(evt);
            }
        });

        jCheckBox4.setName("4"); // NOI18N
        jCheckBox4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jCheckBoxActionPerformed(evt);
            }
        });

        jCheckBox5.setName("5"); // NOI18N
        jCheckBox5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jCheckBoxActionPerformed(evt);
            }
        });

        jCheckBox6.setName("6"); // NOI18N
        jCheckBox6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jCheckBoxActionPerformed(evt);
            }
        });

        jButton1.setText("1");
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        jButton2.setText("2");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        jButton3.setText("3");
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        jButton4.setText("4");
        jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        jButton5.setText("5");
        jButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        jButton6.setText("6");
        jButton6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButtonActionPerformed(evt);
            }
        });

        sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sequenceList.setDragEnabled(true);
        jScrollPane1.setViewportView(sequenceList);

        ftCaptureButton.setText("Capture");
        ftCaptureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                ftCaptureButtonActionPerformed(evt);
            }
        });

        timeBarViewer1.setXAxisHeight(35);
        timeBarViewer1.setDrawRowGrid(true);
        timeBarViewer1.setRowHeight(23);
        timeBarViewer1.setTimeScalePosition(0);
        timeBarViewer1.setModel(ModelCreator.createModel());
        JaretDate date = new JaretDate(1, 1, 1970, 1, 0, 0);
        timeBarViewer1.setInitialDisplayRange(date, 90);

        cCaptureButton.setText("Capture");
        cCaptureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                cCaptureButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        playButton.setIcon(new ImageIcon(getClass().getResource("/hexapodsimulator/play.png"))); // NOI18N
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                playButtonActionPerformed(evt);
            }
        });

        stopButton.setIcon(new ImageIcon(getClass().getResource("/hexapodsimulator/stop.png"))); // NOI18N
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                    .add(timeBarViewer1, GroupLayout.DEFAULT_SIZE, 1207, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(playButton, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(stopButton, GroupLayout.PREFERRED_SIZE, 34, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(rotationSlider, GroupLayout.DEFAULT_SIZE, 393, Short.MAX_VALUE))
                            .add(panel3dModel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                            .add(panelFemurTibia, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.TRAILING, false)
                                    .add(GroupLayout.LEADING, jButton1, 0, 0, Short.MAX_VALUE)
                                    .add(GroupLayout.LEADING, jCheckBox1))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                                    .add(jButton2, 0, 0, Short.MAX_VALUE)
                                    .add(jCheckBox2))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                                    .add(jButton3, 0, 0, Short.MAX_VALUE)
                                    .add(jCheckBox3))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                                    .add(jButton4, 0, 0, Short.MAX_VALUE)
                                    .add(jCheckBox4))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                                    .add(jButton5, 0, 0, Short.MAX_VALUE)
                                    .add(jCheckBox5))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                                    .add(jButton6, 0, 0, Short.MAX_VALUE)
                                    .add(jCheckBox6))
                                .addPreferredGap(LayoutStyle.UNRELATED)
                                .add(ftCaptureButton)))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                            .add(panelCoxa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(cCaptureButton)
                                .add(24, 24, 24)
                                .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(deleteButton)
                                .add(28, 28, 28)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(panel3dModel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                            .add(rotationSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
                                .add(playButton)
                                .add(stopButton))))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.TRAILING)
                            .add(panelCoxa, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(panelFemurTibia, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                            .add(deleteButton)
                            .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
                                    .add(jCheckBox1)
                                    .add(jCheckBox2)
                                    .add(jCheckBox3)
                                    .add(jCheckBox4)
                                    .add(jCheckBox5)
                                    .add(jCheckBox6)
                                    .add(ftCaptureButton)
                                    .add(cCaptureButton))
                                .addPreferredGap(LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
                                    .add(jButton1)
                                    .add(jButton2)
                                    .add(jButton3)
                                    .add(jButton4)
                                    .add(jButton5)
                                    .add(jButton6))))))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(timeBarViewer1, GroupLayout.PREFERRED_SIZE, 252, GroupLayout.PREFERRED_SIZE)
                .add(122, 122, 122))
        );

        getContentPane().add(jPanel1, BorderLayout.CENTER);

        fileMenu.setText("File");

        openProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        openProjectMenuItem.setText("Open Project");
        openProjectMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                openProjectMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openProjectMenuItem);

        closeProjectMenuItem.setText("Close Project");
        closeProjectMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                closeProjectMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeProjectMenuItem);
        fileMenu.add(jSeparator1);

        saveProjectMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        saveProjectMenuItem.setText("Save Project");
        saveProjectMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveProjectMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveProjectMenuItem);

        saveProjectAsMenuItem.setText("Save Project As");
        saveProjectAsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                saveProjectAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveProjectAsMenuItem);
        fileMenu.add(jSeparator2);

        importSequencesMenuItem.setText("Import sequences from existing project");
        importSequencesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                importSequencesMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(importSequencesMenuItem);
        fileMenu.add(jSeparator3);

        exportMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        exportMenuItem.setText("Export for Hexapod");
        exportMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exportMenuItem);

        jMenuBar1.add(fileMenu);

        propertiesMenu.setText("Properties");

        normalizeInputCheckBoxMenuItem.setSelected(true);
        normalizeInputCheckBoxMenuItem.setText("Normalize Input");
        normalizeInputCheckBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                normalizeInputCheckBoxMenuItemActionPerformed(evt);
            }
        });
        propertiesMenu.add(normalizeInputCheckBoxMenuItem);

        interpolateOutputCheckBoxMenuItem.setSelected(true);
        interpolateOutputCheckBoxMenuItem.setText("Interpolate Output");
        interpolateOutputCheckBoxMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                interpolateOutputCheckBoxMenuItemActionPerformed(evt);
            }
        });
        propertiesMenu.add(interpolateOutputCheckBoxMenuItem);

        jMenuBar1.add(propertiesMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rotationSliderStateChanged(ChangeEvent evt) {//GEN-FIRST:event_rotationSliderStateChanged
        GLRenderer3dModel.setRotation(rotationSlider.getValue());
        panel3dModel.repaint();
    }//GEN-LAST:event_rotationSliderStateChanged

    private void jCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jCheckBoxActionPerformed
        int id = Integer.parseInt(((JCheckBox) evt.getSource()).getName());
        if (((JCheckBox) evt.getSource()).isSelected()) {
            GLRenderer3dModel.changeAngle(id - 1, 0, GLRendererCoxa.angle);
            GLRenderer3dModel.changeAngle(id - 1, 1, GLRendererFemurTibia.angle[0]);
            GLRenderer3dModel.changeAngle(id - 1, 2, GLRendererFemurTibia.angle[1]);
            panel3dModel.repaint();
        }
    }//GEN-LAST:event_jCheckBoxActionPerformed

    private void jButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButtonActionPerformed
        int id = Integer.parseInt(((JButton) evt.getSource()).getText());
        GLRendererCoxa.angle = GLRenderer3dModel.angle[id - 1][0];
        GLRendererFemurTibia.angle[0] = GLRenderer3dModel.angle[id - 1][1];
        GLRendererFemurTibia.angle[1] = GLRenderer3dModel.angle[id - 1][2];
        panelFemurTibia.repaint();
        panelCoxa.repaint();
    }//GEN-LAST:event_jButtonActionPerformed

    private void ftCaptureButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_ftCaptureButtonActionPerformed
        if (!((JToggleButton) evt.getSource()).isSelected()) {
            ftSequence.clean();
            System.out.println("cleaned:");
            System.out.println(ftSequence);
            if (properties.normalizeInput) {
                ftSequence.normalize();
                ftSequence.addContent(GLRendererFemurTibia.angle[0], GLRendererFemurTibia.angle[1]); // add the last value
                ftSequence.clean();
            }
            System.out.println("normalized:");
            System.out.println(ftSequence);
            String name;
            boolean validName = false;
            do {
                int validNameCounter = 0;
                name = "ft_" + JOptionPane.showInputDialog("Name?");
                for (int i = 0; i < sequenceVector.size(); i++) {
                    if (!sequenceVector.elementAt(i).getName().equals(name)) {
                        validNameCounter++;
                    }
                }
                if (validNameCounter == sequenceVector.size()) {
                    validName = true;
                }
            } while (!validName);
            String time = JOptionPane.showInputDialog("Time (milliseconds)?");

            ftSequence.setTime(Integer.parseInt(time));
            ftSequence.setName(name);

            ListModel items = sequenceList.getModel();
            Object[] listentries = new Object[items.getSize()];
            for (int i = 0; i < listentries.length; i++) {
                listentries[i] = items.getElementAt(i);
            }
            Object[] newlistentries = new Object[listentries.length + 1];
            System.arraycopy(listentries, 0, newlistentries, 0, listentries.length);
            newlistentries[listentries.length] = name;
            sequenceList = new JList(newlistentries);
            sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            sequenceList.setDragEnabled(true);
            jScrollPane1.setViewportView(sequenceList);

            sequenceVector.addElement(ftSequence);
            ftSequence = new HexiSequenz();   // !
            projectChanged = true;
        }
    }//GEN-LAST:event_ftCaptureButtonActionPerformed

    private void cCaptureButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cCaptureButtonActionPerformed
        if (!((JToggleButton) evt.getSource()).isSelected()) {
            cSequence.clean();
            System.out.println("cleaned:");
            System.out.println(cSequence);
            if (properties.normalizeInput) {
                cSequence.normalize();
                cSequence.addContent(GLRendererCoxa.angle, 0);
                ftSequence.clean();
            }
            System.out.println("normalized:");
            System.out.println(cSequence);
            String name;
            boolean validName = false;
            do {
                int validNameCounter = 0;
                name = "c_" + JOptionPane.showInputDialog("Name?");
                for (int i = 0; i < sequenceVector.size(); i++) {
                    if (!sequenceVector.elementAt(i).getName().equals(name)) {
                        validNameCounter++;
                    }
                }
                if (validNameCounter == sequenceVector.size()) {
                    validName = true;
                }
            } while (!validName);
            String time = JOptionPane.showInputDialog("Time (milliseconds)?");

            cSequence.setTime(Integer.parseInt(time));
            cSequence.setName(name);

            ListModel items = sequenceList.getModel();
            Object[] listentries = new Object[items.getSize()];
            for (int i = 0; i < listentries.length; i++) {
                listentries[i] = items.getElementAt(i);
            }
            Object[] newlistentries = new Object[listentries.length + 1];
            System.arraycopy(listentries, 0, newlistentries, 0, listentries.length);
            newlistentries[listentries.length] = name;
            sequenceList = new JList(newlistentries);
            sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            sequenceList.setDragEnabled(true);
            jScrollPane1.setViewportView(sequenceList);

            sequenceVector.addElement(cSequence);
            cSequence = new HexiSequenz();   // !
            projectChanged = true;
        }
    }//GEN-LAST:event_cCaptureButtonActionPerformed

    private void deleteButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int index = sequenceList.getSelectedIndex();

        // delete this sequence in the super sequence, ...
        try {
            superSeq.delSeq(sequenceVector.get(index).getName());
        } catch (Exception ex) {
            System.out.println(ex);
        }

        // ... in the timebar model, ...
        ModelCreator.remInterval(sequenceVector.get(index).getName());
        timeBarViewer1.setModel(ModelCreator.createModel());
        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);

        // ... in the sequence vector and in the sequence list
        sequenceVector.removeElementAt(index);
        ListModel items = sequenceList.getModel();
        Vector listentries = new Vector(items.getSize());
        for (int i = 0; i < listentries.capacity(); i++) {
            listentries.add(items.getElementAt(i));
        }
        listentries.removeElementAt(index);
        sequenceList = new JList(listentries);
        sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sequenceList.setDragEnabled(true);
        jScrollPane1.setViewportView(sequenceList);

        projectChanged = true;
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void playButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        if (musicInputStream != null) {
            try {
                musicPlayer = new MusicPlayer(musicInputStream);
                musicPlayer.start();
                //musicPlayer.setRunning(true);
            } catch (JavaLayerException ex) {
                System.out.println("JavaLayerException while opening file.");
            }
        }

        sequencePlayer = new SequencePlayer(superSeq);

        final SequenceTimebarMarker timeBarMarker = new SequenceTimebarMarker(new JaretDate(1, 1, 1970, 1, 0, 0));
        timeBarViewer1.addMarker(timeBarMarker);
        timeBarMarkerTimer = new java.util.Timer("TimeBarMarkerTimer");
        timeBarMarkerTimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                timeBarMarker.advanceDateMillis(100);
                sequencePlayer.loadNext(100, 1);
                timeBarViewer1.repaint();
            }
        }, 0, 100);
    }//GEN-LAST:event_playButtonActionPerformed

    private void stopButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_stopButtonActionPerformed
        if (musicPlayer != null) {
            musicPlayer.closePlayer();
            musicPlayer = null;
            try {
                musicInputStream = new FileInputStream(musicFile);
            } catch (FileNotFoundException ex) {
                System.out.println("Reopening: Music file not found!");
            }
        }

        sequencePlayer.cancel();
        timeBarMarkerTimer.cancel();
        List<TimeBarMarker> markers = timeBarViewer1.getMarkers();
        timeBarViewer1.remMarker(markers.get(0));
        timeBarViewer1.repaint();
    }//GEN-LAST:event_stopButtonActionPerformed

    private void exportMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_exportMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(jPanel1) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();
        try {
            superSeq.angletofile(f);
        } catch (Exception ex) {
            //System.out.println("An error occured while exporting the Project");
            ex.printStackTrace();
        }
    }//GEN-LAST:event_exportMenuItemActionPerformed

    private void saveProjectAsMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveProjectAsMenuItemActionPerformed
        int option;
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new HexapodSimulatorProjectFileFilter());
        File f;
        do {
            if (fc.showSaveDialog(jPanel1) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            f = fc.getSelectedFile();
            if (f.exists()) {
                option = JOptionPane.showConfirmDialog(null, "This file already exists. Do you want to replace it?");
                if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            } else {
                option = JOptionPane.YES_OPTION;
            }
        } while (option != JOptionPane.YES_OPTION);
        if (!f.getName().toLowerCase().endsWith(HexapodSimulatorProjectFileFilter.extension)) {
            f = new File(fc.getSelectedFile().getPath().concat(HexapodSimulatorProjectFileFilter.extension));
        }

        try {
            saveProjectFile(f);
        } catch (IOException ex) {
            System.out.println("An error occured while writing the Project File");
            System.out.println(ex);
        }
    }//GEN-LAST:event_saveProjectAsMenuItemActionPerformed

    private void closeProjectMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_closeProjectMenuItemActionPerformed
        try {
            closeProjectFile();
        } catch (IOException ex) {
            System.out.println("An error occured while saving the Project File");
            System.out.println(ex);
        }
    }//GEN-LAST:event_closeProjectMenuItemActionPerformed

    private void openProjectMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_openProjectMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new HexapodSimulatorProjectFileFilter());
        if (fc.showOpenDialog(jPanel1) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = fc.getSelectedFile();

        try {
            openProjectFile(f);
        } catch (IOException ex) {
            System.out.println("An error occured while opening the Project File");
            System.out.println(ex);
        } catch (NullPointerException ex) {
            System.out.println("An error occured while opening the Project File");
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("An error occured while opening the Project File: A class was not found");
        } catch (JavaLayerException ex) {
            System.out.println("JavaLayer exception while loading music from project file");
        }
    }//GEN-LAST:event_openProjectMenuItemActionPerformed

    private void saveProjectMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_saveProjectMenuItemActionPerformed
        if (properties.projectFile == null) {
            saveProjectAsMenuItem.doClick();
        } else {
            try {
                saveProjectFile(properties.projectFile);
            } catch (IOException ex) {
                System.out.println("An error occured while writing the Project File");
                System.out.println(ex);
            }
        }
    }//GEN-LAST:event_saveProjectMenuItemActionPerformed

    private void importSequencesMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_importSequencesMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new HexapodSimulatorProjectFileFilter());
        if (fc.showOpenDialog(jPanel1) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            ZipFile zipFile = new ZipFile(fc.getSelectedFile());
            ObjectInputStream dataInputStream = new ObjectInputStream(zipFile.getInputStream(zipFile.getEntry("data")));
            Vector<HexiSequenz> sequences = ((HexapodSimulatorProjectDataPart) dataInputStream.readObject()).getSequences();
            for (int i = 0; i < sequences.size(); i++) {
                if (getSequenceByName(sequences.elementAt(i).getName()) != null) {
                    String nameWithoutSuffix = sequences.elementAt(i).getName();
                    int nameSuffix = 1;
                    do {
                        sequences.elementAt(i).setName(nameWithoutSuffix + "_" + nameSuffix);
                        nameSuffix++;
                    } while (getSequenceByName(sequences.elementAt(i).getName()) != null);
                }
                sequenceVector.add(sequences.elementAt(i));
            }
            HexiSequenz[] readSequences = new HexiSequenz[sequenceVector.size()];
            sequenceVector.toArray(readSequences);
            String[] names = new String[readSequences.length];
            for (int i = 0; i < readSequences.length; i++) {
                names[i] = readSequences[i].getName();
            }
            sequenceList = new JList(names);
            sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            sequenceList.setDragEnabled(true);
            jScrollPane1.setViewportView(sequenceList);
        } catch (IOException ex) {
            System.out.println("An error occured while opening the Project file to import the sequences");
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("An error occured while opening the Project File to import the sequences: A class was not found");
        }
        projectChanged = true;
    }//GEN-LAST:event_importSequencesMenuItemActionPerformed

    private void normalizeInputCheckBoxMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_normalizeInputCheckBoxMenuItemActionPerformed
        properties.normalizeInput = normalizeInputCheckBoxMenuItem.getState();
    }//GEN-LAST:event_normalizeInputCheckBoxMenuItemActionPerformed

    private void interpolateOutputCheckBoxMenuItemActionPerformed(ActionEvent evt) {//GEN-FIRST:event_interpolateOutputCheckBoxMenuItemActionPerformed
        properties.interpolateOutput = interpolateOutputCheckBoxMenuItem.getState();
        superSeq.interpolate = interpolateOutputCheckBoxMenuItem.getState() == true ? 1 : 0;
    }//GEN-LAST:event_interpolateOutputCheckBoxMenuItemActionPerformed

    private HexiSequenz getSequenceByName(String name) {
        HexiSequenz seq = null;
        for (int i = 0; i < sequenceVector.size(); i++) {
            if (sequenceVector.elementAt(i).getName().equals(name)) {
                seq = sequenceVector.elementAt(i);
                break;
            }
        }
        return seq;
    }

    private void openProjectFile(File file) throws IOException, NullPointerException, ClassNotFoundException, JavaLayerException {
        if (!ensureProjectSaving()) {
            return;
        }

        ZipFile zipFile = new ZipFile(file);
        ObjectInputStream dataInputStream = new ObjectInputStream(zipFile.getInputStream(zipFile.getEntry("data")));
        HexapodSimulatorProjectDataPart projectDataPart = (HexapodSimulatorProjectDataPart) dataInputStream.readObject();
        superSeq = projectDataPart.getSuperSeq();
        ModelCreator.setIntervals(projectDataPart.getIntervals());
        timeBarViewer1.setModel(ModelCreator.createModel());
        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
        sequenceVector = projectDataPart.getSequences();
        HexiSequenz[] readSequences = new HexiSequenz[sequenceVector.size()];
        sequenceVector.toArray(readSequences);
        String[] names = new String[readSequences.length];
        for (int i = 0; i < readSequences.length; i++) {
            names[i] = readSequences[i].getName();
        }
        sequenceList = new JList(names);
        sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sequenceList.setDragEnabled(true);
        jScrollPane1.setViewportView(sequenceList);

        if (zipFile.getEntry("music") != null) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(zipFile.getInputStream(zipFile.getEntry("music")));
            musicFile = File.createTempFile("hexisimTempMusicFile", null);
            musicFileIsTempFile = true;
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(musicFile));
            int buffer;
            while((buffer = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(buffer);
            }
            bufferedOutputStream.close();
            musicInputStream = new FileInputStream(musicFile);
        }
        zipFile.close();

        setTitle("Hexapod Simulator - " + file.getName());
        properties.projectFile = file;
        projectChanged = false;
    }

    private void closeProjectFile() throws IOException {
        if (!ensureProjectSaving()) {
            return;
        }
        superSeq = new SuperSeq();
        ModelCreator.clear();
        timeBarViewer1.setModel(ModelCreator.createModel());
        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
        sequenceVector.clear();
        sequenceList = new JList();
        sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sequenceList.setDragEnabled(true);
        jScrollPane1.setViewportView(sequenceList);
        musicInputStream = null;
        if(musicFileIsTempFile) {
            musicFile.delete();
        }
        musicFile = null;
        setTitle("Hexapod Simulator");
        properties.projectFile = null;
        projectChanged = false;
    }

    private void saveProjectFile(File file) throws IOException {
        ZipEntry data = new ZipEntry("data");
        ZipEntry music = new ZipEntry("music");
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file));
        zipOutputStream.setMethod(ZipOutputStream.DEFLATED);    // compress zip file
        HexapodSimulatorProjectDataPart projectDataPart = new HexapodSimulatorProjectDataPart(null, superSeq, sequenceVector, ModelCreator.getIntervals());

        zipOutputStream.putNextEntry(data);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(zipOutputStream);
        objectOutputStream.writeObject(projectDataPart);

        if (musicInputStream != null) {
            zipOutputStream.putNextEntry(music);

            int buffer;
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(zipOutputStream);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(musicInputStream);
            while ((buffer = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(buffer);
            }
            zipOutputStream.closeEntry();
        }

        zipOutputStream.finish();
        zipOutputStream.close();

        setTitle("Hexapod Simulator - " + file.getName());
        properties.projectFile = file;
        projectChanged = false;
    }

    /**
     * Asks the user to save the project if there are unsaved changes.
     * @return true if the changes were saved, false if the user has cancelled the operation
     */
    private boolean ensureProjectSaving() throws IOException {
        if (projectChanged) {
            int option = JOptionPane.showConfirmDialog(null, "The project has unsaved changes. Do you want to save the file?");
            if (option == JOptionPane.YES_OPTION) {
                if (properties.projectFile == null) {
                    saveProjectAsMenuItem.doClick();
                } else {
                    saveProjectFile(properties.projectFile);
                }
            }
            if (option == JOptionPane.CANCEL_OPTION) {
                return false;
            }
        }
        return true;
    }

    /**
     * Called from within initComponents().
     * hint: to customize the generated code choose 'Customize Code' in the contextmenu
     * of the selected UI Component you wish to cutomize in design mode.
     * @return Returns customized GLCapabilities.
     */
    private GLCapabilities createGLCapabilites() {

        GLCapabilities capabilities = new GLCapabilities();
        capabilities.setHardwareAccelerated(true);

        // try to enable 2x anti aliasing - should be supported on most hardware
        capabilities.setNumSamples(2);
        capabilities.setSampleBuffers(true);

        return capabilities;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Run this in the AWT event thread to prevent deadlocks and race conditions
        EventQueue.invokeLater(new Runnable() {

            public void run() {

                // switch to system l&f for native font rendering etc.
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.INFO, "can not enable system look and feel", ex);
                }

                HexapodSimulator frame = new HexapodSimulator();
                frame.setVisible(true);

                frame.timer.start();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JToggleButton cCaptureButton;
    private JMenuItem closeProjectMenuItem;
    private JButton deleteButton;
    private JMenuItem exportMenuItem;
    private JMenu fileMenu;
    private JToggleButton ftCaptureButton;
    private JLabel hintCoxa;
    private JLabel hintFemurTibia;
    private JMenuItem importSequencesMenuItem;
    private JCheckBoxMenuItem interpolateOutputCheckBoxMenuItem;
    private JButton jButton1;
    private JButton jButton2;
    private JButton jButton3;
    private JButton jButton4;
    private JButton jButton5;
    private JButton jButton6;
    private JCheckBox jCheckBox1;
    private JCheckBox jCheckBox2;
    private JCheckBox jCheckBox3;
    private JCheckBox jCheckBox4;
    private JCheckBox jCheckBox5;
    private JCheckBox jCheckBox6;
    private JMenuBar jMenuBar1;
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private JSeparator jSeparator1;
    private JSeparator jSeparator2;
    private JSeparator jSeparator3;
    private JLabel kneeModeLabel;
    private JCheckBoxMenuItem normalizeInputCheckBoxMenuItem;
    private JMenuItem openProjectMenuItem;
    private GLJPanel panel3dModel;
    private GLJPanel panelCoxa;
    private GLJPanel panelFemurTibia;
    private JButton playButton;
    private JMenu propertiesMenu;
    private JSlider rotationSlider;
    private JMenuItem saveProjectAsMenuItem;
    private JMenuItem saveProjectMenuItem;
    private JList sequenceList;
    private JButton stopButton;
    private TimeBarViewer timeBarViewer1;
    // End of variables declaration//GEN-END:variables
}
