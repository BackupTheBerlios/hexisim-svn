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
public class GLRendererCoxa implements GLEventListener, MouseMotionListener {

    public void mouseDragged(MouseEvent e) {
        GLJPanel glpanel = (GLJPanel) e.getSource();
        double mx = (double) e.getX() / glpanel.getWidth() * 2 - 1;
        double my = (double) e.getY() / glpanel.getHeight() * 2;

        double b = GLRendererFemurTibia.b1 * Math.cos(Math.toRadians(GLRendererFemurTibia.angle[0]));
        b += GLRendererFemurTibia.b2 * Math.cos(Math.toRadians(GLRendererFemurTibia.angle[0] + GLRendererFemurTibia.angle[1]));

        double bx = b * Math.cos(Math.toRadians(90 - angle));
        double by = 0.1 + b * Math.sin(Math.toRadians(90 - angle));

        if (Math.abs(mx - bx) < 0.2 && Math.abs(my - by) < 0.2 && !Double.isNaN(Math.acos(mx / b))) {
            angle = 90 - Math.toDegrees(Math.acos(mx / b));
            glpanel.repaint();
        }
        System.out.println(angle);

        if (angle < -45 || angle > 45) {    // angle is out of range
            angle = angle > 0 ? 45 : -45;
        }

        if(checkBox){
            double l = 0.8+Math.hypot(mx, my - 0.1);
            GLRendererFemurTibia.moveAnglesToXY(l, GLRendererFemurTibia.getY() + 1);
        }
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

        angle = 0;
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

        double[][] joint = new double[2][2];
        update2dLocs(joint);

        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        gl.glTranslated(-1.0, 1.0, 0.0);

        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glVertex2d(joint[0][0], joint[0][1]);
        gl.glVertex2d(joint[1][0], joint[1][1]);
        gl.glEnd();

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    public static double angle;
    public static boolean checkBox;

    public void update2dLocs(double[][] joint) {
        double b = GLRendererFemurTibia.b1 * Math.cos(Math.toRadians(GLRendererFemurTibia.angle[0]));
        b += GLRendererFemurTibia.b2 * Math.cos(Math.toRadians(GLRendererFemurTibia.angle[0] + GLRendererFemurTibia.angle[1]));
        joint[0][0] = 1.0;
        joint[0][1] = -0.1;
        joint[1][0] = joint[0][0] + b * Math.cos(Math.toRadians(90 - angle));
        joint[1][1] = joint[0][1] - b * Math.sin(Math.toRadians(90 - angle));
    }
}

