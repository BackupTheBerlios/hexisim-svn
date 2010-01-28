package hexapodsimulator;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JLabel;

/**
 * GLRenderer.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRendererFemurTibia extends MouseAdapter implements GLEventListener {

    public static boolean kneeUp;
    private static boolean currentKneeUp;
    public static boolean holdX;
    public static boolean holdY;

    @Override
    public void mouseDragged(MouseEvent e) {
        GLJPanel glPanel = (GLJPanel) e.getSource();
        double xStart, x3, yStart, y3;

        x3 = (double) e.getX() / glPanel.getWidth() * 2;    //Mouse position X, calculated
        y3 = (double) e.getY() / glPanel.getHeight() * 2;   //Mouse position Y, calculated

        if(holdX)
            moveAnglesToXY(getX()+0.8, y3);
        else if (holdY)
            moveAnglesToXY(x3, getY()+1);
        else
            moveAnglesToXY(x3, y3);

}

    public static void moveAnglesToXY(double x3, double y3){
        double xStart = 0.8;
        double yStart = 1;

        double[] oldAngle = angle.clone();

        double angleIntermediateResult1 = Math.acos((b1 * b1 - b2 * b2 + (Math.pow(x3 - xStart, 2) + Math.pow(y3 - yStart, 2))) / (2 * b1 * Math.hypot(x3 - xStart, y3 - yStart)));
        double angleIntermediateResult2 = Math.acos((b2 * b2 - b1 * b1 + (Math.pow(x3 - xStart, 2) + Math.pow(y3 - yStart, 2))) / (2 * b2 * Math.hypot(x3 - xStart, y3 - yStart)));
        if (currentKneeUp && !Double.isNaN(angleIntermediateResult1) && !Double.isNaN(angleIntermediateResult2)) {
            angle[0] = 90 - Math.toDegrees(Math.atan((x3 - xStart) / (y3 - yStart))) - Math.toDegrees(angleIntermediateResult1);
            angle[1] = Math.toDegrees(angleIntermediateResult1) + Math.toDegrees(angleIntermediateResult2);

        //TO-DO: calculate the right angles for knee down - mode - should work
        } else if (!currentKneeUp && !Double.isNaN(angleIntermediateResult1) && !Double.isNaN(angleIntermediateResult2)) {
            angle[0] = - 90 - Math.toDegrees(Math.atan((x3 - xStart) / (y3 - yStart))) - Math.toDegrees(angleIntermediateResult1);
            angle[1] = - Math.toDegrees(angleIntermediateResult1) + Math.toDegrees(angleIntermediateResult2);
        }

        if (Math.hypot(x3 - xStart, y3 - yStart) >= (b1 + b2)) { // Mouse position is out of range
            angle[0] = 90 - Math.toDegrees(Math.atan((x3 - xStart) / (y3 - yStart)));
            angle[1] = 0;
            //if (currentKneeUp != kneeUp) {    //lol - n1
                currentKneeUp = kneeUp;
            //}
        }

        //Maybe it's better to treat both angles eqally (?)
        double[] anglePwmValue = new double[2];
        anglePwmValue[0] = Math.toRadians(-angle[0])*1024 + 2145;
        anglePwmValue[1] = Math.toRadians(-angle[1])*1024 + 2680;
        /*if (angle[0] < (-72.9 + 30) || angle[0] > (93.3 + 30)) {*/ if(anglePwmValue[0] > 3277 || anglePwmValue[0] < 306) {
            angle[0] = oldAngle[0];
        }
        /*if (angle[1] < (-90 + 59.9) || angle[1] > (83.9 + 59.9)) {*/ if(anglePwmValue[1] > 3109 || anglePwmValue[1] < 0 /*|| anglePwmValue[0] > 3277 || anglePwmValue[0] < 306*/) {
            //angle[0] = oldAngle[0];
            angle[1] = oldAngle[1];
            //angle[0] = 90 - Math.toDegrees(Math.atan((x3 - xStart) / (y3 - yStart))) - (180 - angle[1]) / 2;
        }

        //System.out.println((Math.toRadians(angle[0]) * 1024 + 2145) + ", " + (Math.toRadians(angle[1]) * 1024 + 2680));
    }

    public static double getX(){
        return b1 * Math.cos(Math.toRadians(angle[0])) +  b2 * Math.cos(Math.toRadians(angle[0] + angle[1]));
    }

    public static double getY(){
        return b1 * Math.sin(Math.toRadians(angle[0])) +  b2 * Math.sin(Math.toRadians(angle[0] + angle[1]));
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
        b1 = 0.624 / 1.8;
        b2 = 1.176 / 1.8;
        currentKneeUp = kneeUp = true;
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

        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        for (int i = 0; i < 3; i++) {
            gl.glVertex2d(joint[i][0], joint[i][1]);
        }
        gl.glEnd();

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }
    public static double[] angle = new double[2];
    public static double b1, b2;

    public void update2dLocs(double[][] joint) {
        joint[0][0] = 0.8;
        joint[0][1] = -1;
        joint[1][0] = joint[0][0] + b1 * Math.cos(Math.toRadians(angle[0]));
        joint[1][1] = joint[0][1] - b1 * Math.sin(Math.toRadians(angle[0]));
        joint[2][0] = joint[1][0] + b2 * Math.cos(Math.toRadians(angle[0] + angle[1]));
        joint[2][1] = joint[1][1] - b2 * Math.sin(Math.toRadians(angle[0] + angle[1]));
    }
}
