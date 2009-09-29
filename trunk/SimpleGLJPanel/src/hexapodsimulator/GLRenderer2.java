package hexapodsimulator;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;

/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer2 implements GLEventListener, MouseMotionListener {

    public void mouseDragged(MouseEvent e) {
        GLJPanel glpanel = (GLJPanel) e.getSource();
        double[] objCoord = new double[2];
        //double[] angle = new double[2];
        double x1, x2, x3, y1, y2, y3;
        double b = 0.9;

        x3 = objCoord[0] = (double) e.getX() / glpanel.getWidth() * 2;
        y3 = objCoord[1] = (double) e.getY() / glpanel.getHeight() * 2;

        x1 = 0.1;
        y1 = 0.1;
/*
        x2 = x1 + (x3 - x1) / 2 + Math.sqrt((b * b - Math.pow((x1 + (x3 - x1) / 2) - x1, 2.0) - Math.pow((y3 - y1) / 2, 2.0)) / 2.0);
        y2 = y1 + Math.sqrt(b * b - Math.pow(x2 - x1, 2.0));

        angle[0] = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
        angle[1] = Math.toDegrees(Math.atan((y3 - y2) / (x3 - x2))) - angle[0];
*/
        angle[0] = Math.toDegrees( 90 - Math.atan( (x3-x1) / (y3-y1) ) - Math.acos( Math.sqrt( ( Math.pow(x3-x1,2) + Math.pow(y3-y1,2) ) / (2 * b) ) ) );
        angle[1] = Math.toDegrees( 2 *  Math.acos( Math.sqrt( ( Math.pow(x3-x1,2) + Math.pow(y3-y1,2) ) / (2 * b) ) ) );

        System.out.print(angle[0]);
        System.out.print(" - ");
        System.out.println(angle[1]);

        glpanel.repaint();
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void init(GLAutoDrawable drawable) {
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));

        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.

        for (int i = 0; i < 2; i++) {
            angle[i] = 45;
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        //glu.gluPerspective(45.0f, h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void display(GLAutoDrawable drawable) {

        double[][] joint = new double[3][2];
        update2dLocs(joint);

        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        gl.glTranslated(-1.0, 1.0, 0.0);

        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 2; i++) {
            gl.glVertex2d(joint[i][0], joint[i][1]);
            gl.glVertex2d(joint[i + 1][0], joint[i + 1][1]);
        }
        gl.glEnd();

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    public static double[] angle = new double[2];

    public void update2dLocs(double[][] joint) {
        double b1 = 0.9, b2 = 0.9;
        joint[0][0] = 0.1;
        joint[0][1] = -0.1;
        joint[1][0] = joint[0][0] + b1 * Math.cos(Math.toRadians(angle[0]));
        joint[1][1] = joint[0][1] - b1 * Math.sin(Math.toRadians(angle[0]));
        joint[2][0] = joint[1][0] + b2 * Math.cos(Math.toRadians(angle[0] + angle[1]));
        joint[2][1] = joint[1][1] - b2 * Math.sin(Math.toRadians(angle[0] + angle[1]));
    }
}

