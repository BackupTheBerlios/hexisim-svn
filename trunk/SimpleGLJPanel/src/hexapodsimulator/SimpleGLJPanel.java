/*
 * SimpleGLJPanel.java
 *
 * Created on 30. Juli 2008, 16:18
 */
//javax.swing.Grouplayout
package hexapodsimulator;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.swing.GroupLayout;
import com.sun.opengl.util.Animator;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLJPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import /*javax.swing.GroupLayout; /*/org.jdesktop.layout.GroupLayout;
import org.jdesktop.layout.LayoutStyle;
import javax.swing.UIManager;

import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author cylab
 * @author mbien
 */
public class SimpleGLJPanel extends JFrame implements ActionListener
 {

    private Animator animator, animator2;

    /** Creates new form MainFrame */
    public SimpleGLJPanel() {
        initComponents();
        setTitle("Hexapod Simulator");

        panel.addGLEventListener(new GLRenderer());
        animator = new Animator(panel);

        panel2.addGLEventListener(new GLRenderer2());
        panel2.addMouseMotionListener(new GLRenderer2());
        animator2 = new Animator(panel2);

        /*panel2.addMouseMotionListener(new MouseMotionListener() {

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {

        DoubleBuffer objCoord = DoubleBuffer.allocate(3);
        javax.media.opengl.GL gl = panel2.getGL();
        javax.media.opengl.glu.GLU glu = new javax.media.opengl.glu.GLU();
        //double[] modelview = new double[16];
        DoubleBuffer modelview = DoubleBuffer.allocate(16);
        //double[] projection = new double[16];
        DoubleBuffer projection = DoubleBuffer.allocate(16);
        //int[] viewport = new int[4];
        IntBuffer viewport = IntBuffer.allocate(4);

        gl.glGetDoublev(javax.media.opengl.GL.GL_MODELVIEW_MATRIX, modelview);
        gl.glGetDoublev(javax.media.opengl.GL.GL_PROJECTION_MATRIX, projection);
        gl.glGetIntegerv(javax.media.opengl.GL.GL_VIEWPORT, viewport);
        glu.gluUnProject(e.getX(), e.getY(), 0.0, modelview, projection, viewport, objCoord);
        System.out.printf("%d - %d\n",e.getX(),e.getY());
        //System.out.printf("%f - %f - %f\n", objCoord.get(0), objCoord.get(1), objCoord.get(2));
        }
        });*/

        double[][] angle = {{45, 45, 45}, {45, 45, 45}, {45, 45, 45}, {45, 45, 45}, {45, 45, 45}, {45, 45, 45}};
        GLRenderer.setAngle(angle);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        animator2.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });

        jSlider1.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                double[][] Angle = new double[6][3];
                GLRenderer.getAngle(Angle);
                for (int i = 0; i < 6; i++) {
                    Angle[i][0] = jSlider1.getValue();
                }
                GLRenderer.setAngle(Angle);

                panel.repaint();
            }
        });

        jSlider2.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                double[][] Angle = new double[6][3];
                GLRenderer.getAngle(Angle);
                for (int i = 0; i < 6; i++) {
                    Angle[i][1] = jSlider2.getValue();
                }
                GLRenderer.setAngle(Angle);

                panel.repaint();
            }
        });

        jSlider3.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                double[][] Angle = new double[6][3];
                GLRenderer.getAngle(Angle);
                for (int i = 0; i < 6; i++) {
                    Angle[i][2] = jSlider3.getValue();
                }
                GLRenderer.setAngle(Angle);

                panel.repaint();
            }
        });

    }

    @Override
    public void setVisible(boolean show) {
        if (!show) {
            animator.stop();
            animator2.stop();
        }
        super.setVisible(show);
        if (!show) {
            animator.start();
            animator2.stop();
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
        panel = new GLJPanel();
        jSlider1 = new JSlider();
        rotationSlider = new JSlider();
        jSlider2 = new JSlider();
        jSlider3 = new JSlider();
        panel2 = new GLJPanel();
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

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GroupLayout panelLayout = new GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 453, Short.MAX_VALUE)
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 380, Short.MAX_VALUE)
        );

        jSlider1.setMaximum(90);
        jSlider1.setOrientation(JSlider.VERTICAL);
        jSlider1.setValue(45);
        jSlider1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        rotationSlider.setMaximum(360);
        rotationSlider.setValue(0);
        rotationSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                rotationSliderStateChanged(evt);
            }
        });

        jSlider2.setMaximum(90);
        jSlider2.setOrientation(JSlider.VERTICAL);
        jSlider2.setValue(45);
        jSlider2.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jSlider3.setMaximum(90);
        jSlider3.setOrientation(JSlider.VERTICAL);
        jSlider3.setValue(45);
        jSlider3.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent evt) {
                jSlider3StateChanged(evt);
            }
        });

        panel2.setPreferredSize(new Dimension(307, 307));

        GroupLayout panel2Layout = new GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 307, Short.MAX_VALUE)
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(GroupLayout.LEADING)
            .add(0, 307, Short.MAX_VALUE)
        );

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
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("2");
        jButton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("3");
        jButton3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("4");
        jButton4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("5");
        jButton5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("6");
        jButton6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(GroupLayout.TRAILING, false)
                    .add(GroupLayout.LEADING, rotationSlider, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(GroupLayout.LEADING, panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.RELATED)
                .add(jSlider1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(jSlider2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED)
                .add(jSlider3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.RELATED, 9, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                    .add(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.TRAILING, false)
                            .add(GroupLayout.LEADING, jButton1, 0, 0, Short.MAX_VALUE)
                            .add(GroupLayout.LEADING, jCheckBox1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(jButton2, 0, 0, Short.MAX_VALUE)
                            .add(jCheckBox2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(jButton3, 0, 0, Short.MAX_VALUE)
                            .add(jCheckBox3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(jButton4, 0, 0, Short.MAX_VALUE)
                            .add(jCheckBox4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(jButton5, 0, 0, Short.MAX_VALUE)
                            .add(jCheckBox5, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING, false)
                            .add(jButton6, 0, 0, Short.MAX_VALUE)
                            .add(jCheckBox6, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(panel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(jCheckBox1)
                            .add(jCheckBox2)
                            .add(jCheckBox3)
                            .add(jCheckBox4)
                            .add(jCheckBox5)
                            .add(jCheckBox6))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.BASELINE)
                            .add(jButton1)
                            .add(jButton2)
                            .add(jButton3)
                            .add(jButton4)
                            .add(jButton5)
                            .add(jButton6)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jPanel1Layout.createParallelGroup(GroupLayout.LEADING)
                            .add(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jSlider1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(jSlider2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .add(jSlider3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.RELATED)
                        .add(rotationSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        getContentPane().add(jPanel1, BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        GLRenderer2.angle[0] = GLRenderer.angle[0][1];
        GLRenderer2.angle[1] = GLRenderer.angle[0][2];
        panel2.repaint();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        GLRenderer2.angle[0] = GLRenderer.angle[1][1];
        GLRenderer2.angle[1] = GLRenderer.angle[1][2];
        panel2.repaint();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        GLRenderer2.angle[0] = GLRenderer.angle[2][1];
        GLRenderer2.angle[1] = GLRenderer.angle[2][2];
        panel2.repaint();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        GLRenderer2.angle[0] = GLRenderer.angle[3][1];
        GLRenderer2.angle[1] = GLRenderer.angle[3][2];
        panel2.repaint();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        GLRenderer2.angle[0] = GLRenderer.angle[4][1];
        GLRenderer2.angle[1] = GLRenderer.angle[4][2];
        panel2.repaint();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        GLRenderer2.angle[0] = GLRenderer.angle[5][1];
        GLRenderer2.angle[1] = GLRenderer.angle[5][2];
        panel2.repaint();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void rotationSliderStateChanged(ChangeEvent evt) {//GEN-FIRST:event_rotationSliderStateChanged
        GLRenderer.setRotation(rotationSlider.getValue());
        panel.repaint();
    }//GEN-LAST:event_rotationSliderStateChanged

    private void jSlider1StateChanged(ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        double[][] Angle = new double[6][3];
        GLRenderer.getAngle(Angle);
        for (int i = 0; i < 6; i++) {
            Angle[i][0] = jSlider1.getValue();
        }
        GLRenderer.setAngle(Angle);

        panel.repaint();
    }//GEN-LAST:event_jSlider1StateChanged

    private void jSlider2StateChanged(ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        double[][] Angle = new double[6][3];
        GLRenderer.getAngle(Angle);
        for (int i = 0; i < 6; i++) {
            Angle[i][1] = jSlider2.getValue();
        }
        GLRenderer.setAngle(Angle);

        panel.repaint();
    }//GEN-LAST:event_jSlider2StateChanged

    private void jSlider3StateChanged(ChangeEvent evt) {//GEN-FIRST:event_jSlider3StateChanged
        double[][] Angle = new double[6][3];
        GLRenderer.getAngle(Angle);
        for (int i = 0; i < 6; i++) {
            Angle[i][2] = jSlider3.getValue();
        }
        GLRenderer.setAngle(Angle);

        panel.repaint();
    }//GEN-LAST:event_jSlider3StateChanged

    private void jCheckBoxActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jCheckBoxActionPerformed
        int id = Integer.parseInt(((JCheckBox) evt.getSource()).getName());
        if (((JCheckBox) evt.getSource()).isSelected()) {
            GLRenderer.changeAngle(id - 1, 1, GLRenderer2.angle[0]);
            GLRenderer.changeAngle(id - 1, 2, GLRenderer2.angle[1]);
            panel.repaint();
        }
    }//GEN-LAST:event_jCheckBoxActionPerformed

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

                SimpleGLJPanel frame = new SimpleGLJPanel();
                frame.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    private JSlider jSlider1;
    private JSlider jSlider2;
    private JSlider jSlider3;
    private GLJPanel panel;
    private GLJPanel panel2;
    private JSlider rotationSlider;
    // End of variables declaration//GEN-END:variables

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
