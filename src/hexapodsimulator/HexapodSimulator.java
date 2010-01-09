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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
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

/**
 * Main class for the Hexapod Simulator
 * @author peter
 * @author cylab
 * @author mbien
 */
public class HexapodSimulator extends JFrame {

    private Animator animator;
    private ActionListener updater;
    private Timer timer;
    private HexiSequenz ftSequence, cSequence;
    private Vector<HexiSequenz> sequenceVector;
    private SuperSeq superSeq;
    //private AdvancedPlayer advancedPlayer;
    private File musicFile;
    private MusicPlayer musicPlayer;
    private SequencePlayer sequencePlayer;
    private java.util.Timer timeBarMarkerTimer;
    private java.util.Timer previewTimer;

    /** Creates new form MainFrame */
    public HexapodSimulator() {
        initComponents();
        setTitle("Hexapod Simulator");

        panel3dModel.addGLEventListener(new GLRenderer3dModel());
        animator = new Animator(panel3dModel);

        panelFemurTibia.addGLEventListener(new GLRendererFemurTibia());
        panelFemurTibia.addMouseMotionListener(new GLRendererFemurTibia());

        panelCoxa.addGLEventListener(new GLRendererCoxa());
        panelCoxa.addMouseMotionListener(new GLRendererCoxa());

        updater = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                panel3dModel.repaint();
                panelFemurTibia.repaint();
                panelCoxa.repaint();

                if (ftCaptureButton.isSelected()) {
                    //System.out.println(ftSequence.getLength());
                    double[] angle = new double[2];
                    if (ftSequence.getLength() > 0) {
                        angle = ftSequence.getAngle(ftSequence.getLength() - 1).clone();
                    }

                    if (ftSequence.getLength() > 0 &&
                            angle[0] != GLRendererFemurTibia.angle[0] &&
                            angle[1] != GLRendererFemurTibia.angle[1] ||
                            ftSequence.getLength() == 0) {
                        ftSequence.addContent(GLRendererFemurTibia.angle[0], GLRendererFemurTibia.angle[1]);
                    }
                    //System.out.println(ftSequence);
                }

                if (cCaptureButton.isSelected()) {
                    double angle[] = new double[2];
                    if (cSequence.getLength() > 0) {
                        angle = cSequence.getAngle(cSequence.getLength() - 1).clone();
                    }

                    if (cSequence.getLength() > 0 &&
                            angle[0] != GLRendererCoxa.angle ||
                            cSequence.getLength() == 0) {
                        cSequence.addContent(GLRendererCoxa.angle, 0);
                    }
                }
            }
        };
        timer = new Timer(50, updater);

        ftSequence = new HexiSequenz();
        cSequence = new HexiSequenz();
        sequenceVector = new Vector<HexiSequenz>();
        superSeq = new SuperSeq();
        try {
            FileInputStream file = new FileInputStream("sequences.dat");
            ObjectInputStream in = new ObjectInputStream(file);
            sequenceVector = (Vector<HexiSequenz>) in.readObject();
            in.close();
        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println("IO Exception (read)");
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found.");
        }
        HexiSequenz[] readSequences = new HexiSequenz[sequenceVector.size()];
        System.out.println(sequenceVector);
        sequenceVector.toArray(readSequences);
        String[] names = new String[readSequences.length];
        for (int i = 0; i < readSequences.length; i++) {
            names[i] = readSequences[i].getName();
        }
        sequenceList = new JList(names);
        sequenceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sequenceList.setDragEnabled(true);
        jScrollPane1.setViewportView(sequenceList);

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
                        FileInputStream fis = new FileInputStream(f);

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

                        /*advancedPlayer = new AdvancedPlayer(fis);
                        advancedPlayer.setPlayBackListener(new PlaybackListener() {
                        });*/
                        //musicPlayer = new MusicPlayer(f);
                        /*musicPlayer = new MusicPlayer(advancedPlayer);
                        musicPlayer.start();*/
                    } catch (FileNotFoundException ex) {
                        System.out.println("File not found.");
                    } catch (TagException ex) {
                        System.out.println("Tag Exception while reading ID3 Tags.");
                    } catch (IOException ex) {
                        System.out.println("IO Exception while calculating Song Length.");
                    }/* catch (JavaLayerException ex) {
                        System.out.println("JavaLayerException while opening file.");
                    }*/
                    return;
                }

                if (e.getButton() == MouseEvent.BUTTON1) {  // leftclick
                    tempInterval = ModelCreator.getInterval(rowIndex, intervalIndex);
                    tempRow = row;
                    ModelCreator.remInterval(rowIndex, intervalIndex);
                    try {
                        superSeq.delSeq(date.getMinutes() * 60000 + date.getSeconds() * 1000 + date.getMillis(), row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                    timeBarViewer1.setModel(ModelCreator.createModel());
                    timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
                } else if (e.getButton() == MouseEvent.BUTTON3) {   // rightclick
                    JaretDate oldBeginDate = ModelCreator.getInterval(rowIndex, intervalIndex).getBegin();
                    JaretDate newBeginDate = new JaretDate(1, 1, 1970, 1, 0, 0);
                    String oldBeginDateString = oldBeginDate.getMinutes() + ":" + oldBeginDate.getSeconds() + "." + oldBeginDate.getMillis();
                    String newBeginDateString = JOptionPane.showInputDialog("Start time? (minutes:seconds.millis)", oldBeginDateString);
                    String[] newBeginDateSplittedString = newBeginDateString.split(":", 2);
                    newBeginDate.setMinutes(Integer.parseInt(newBeginDateSplittedString[0]));
                    newBeginDateSplittedString = newBeginDateSplittedString[1].split("\\.", 2);
                    newBeginDate.setSeconds(Integer.parseInt(newBeginDateSplittedString[0]));
                    newBeginDate.setMilliseconds(Integer.parseInt(newBeginDateSplittedString[1]));

                    tempInterval = ModelCreator.getInterval(rowIndex, intervalIndex);
                    ModelCreator.remInterval(rowIndex, intervalIndex);
                    long duration = tempInterval.getMillis();
                    tempInterval.setBegin(newBeginDate);
                    tempInterval.setEnd(newBeginDate.copy().advanceMillis(duration));
                    if (ModelCreator.addInterval(rowIndex, tempInterval) != -1) {
                        superSeq.addSeq(getSequenceByName(tempInterval.getTitle()), newBeginDate.getMinutes() * 60000 + newBeginDate.getSeconds() * 1000 + newBeginDate.getMillis(), row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
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

                    if (ModelCreator.addInterval(rowIndex, tempInterval) != -1 &&
                            row.getType().equals(tempRow.getType())) {
                        superSeq.addSeq(getSequenceByName(tempInterval.getTitle()), seconds * 1000 + milliseconds, row.getSecID(), row.getType().equals("ft") ? 0 : 1);
                        timeBarViewer1.setModel(ModelCreator.createModel());
                        timeBarViewer1.setInitialDisplayRange(new JaretDate(1, 1, 1970, 1, 0, 0), 90);
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

        double[][] angle = {{45, 45, 45}, {45, 45, 45}, {45, 45, 45}, {45, 45, 45}, {45, 45, 45}, {45, 45, 45}};
        GLRenderer3dModel.setAngle(angle);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    FileOutputStream file = new FileOutputStream("sequences.dat", false);
                    ObjectOutputStream os = new ObjectOutputStream(file);
                    os.writeObject(sequenceVector);
                    os.close();
                } catch (FileNotFoundException ex) {
                    System.out.println("File not found.");
                } catch (IOException ex) {
                    System.out.println("IO Exception (write)");
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

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GroupLayout panel3dModelLayout = new GroupLayout(panel3dModel);
        panel3dModel.setLayout(panel3dModelLayout);
        panel3dModelLayout.setHorizontalGroup(
            panel3dModelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 467, Short.MAX_VALUE)
        );
        panel3dModelLayout.setVerticalGroup(
            panel3dModelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 369, Short.MAX_VALUE)
        );

        panelFemurTibia.setPreferredSize(new Dimension(307, 307));

        hintFemurTibia.setForeground(Color.orange);
        hintFemurTibia.setHorizontalAlignment(SwingConstants.CENTER);
        hintFemurTibia.setText("Drop here to get a preview");

        GroupLayout panelFemurTibiaLayout = new GroupLayout(panelFemurTibia);
        panelFemurTibia.setLayout(panelFemurTibiaLayout);
        panelFemurTibiaLayout.setHorizontalGroup(
            panelFemurTibiaLayout.createParallelGroup(GroupLayout.LEADING)
            .add(panelFemurTibiaLayout.createSequentialGroup()
                .addContainerGap()
                .add(hintFemurTibia, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelFemurTibiaLayout.setVerticalGroup(
            panelFemurTibiaLayout.createParallelGroup(GroupLayout.LEADING)
            .add(GroupLayout.TRAILING, panelFemurTibiaLayout.createSequentialGroup()
                .addContainerGap(147, Short.MAX_VALUE)
                .add(hintFemurTibia)
                .add(144, 144, 144))
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
                                .add(rotationSlider, GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
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
                                .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
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
                            .add(jScrollPane1, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
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
            String time = JOptionPane.showInputDialog("Zeit (ms)?");

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
        }
    }//GEN-LAST:event_ftCaptureButtonActionPerformed

    private void cCaptureButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_cCaptureButtonActionPerformed
        if (!((JToggleButton) evt.getSource()).isSelected()) {
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
            String time = JOptionPane.showInputDialog("Zeit (ms)?");

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
        }
    }//GEN-LAST:event_cCaptureButtonActionPerformed

    private void deleteButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        int index = sequenceList.getSelectedIndex();
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
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void playButtonActionPerformed(ActionEvent evt) {//GEN-FIRST:event_playButtonActionPerformed
        try {
            musicPlayer = new MusicPlayer(musicFile);
            musicPlayer.start();
            musicPlayer.setRunning(true);
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found");
        } catch (JavaLayerException javaLayerException) {
            System.out.println("JavaLayerException while opening file.");
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
        musicPlayer.setRunning(false);
        musicPlayer = null;

        sequencePlayer.cancel();
        timeBarMarkerTimer.cancel();
        List<TimeBarMarker> markers = timeBarViewer1.getMarkers();
        timeBarViewer1.remMarker(markers.get(0));
        timeBarViewer1.repaint();
    }//GEN-LAST:event_stopButtonActionPerformed

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
    private JButton deleteButton;
    private JToggleButton ftCaptureButton;
    private JLabel hintCoxa;
    private JLabel hintFemurTibia;
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
    private JPanel jPanel1;
    private JScrollPane jScrollPane1;
    private GLJPanel panel3dModel;
    private GLJPanel panelCoxa;
    private GLJPanel panelFemurTibia;
    private JButton playButton;
    private JSlider rotationSlider;
    private JList sequenceList;
    private JButton stopButton;
    private TimeBarViewer timeBarViewer1;
    // End of variables declaration//GEN-END:variables
}
