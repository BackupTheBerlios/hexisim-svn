package hexapodsimulator;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

/**
 * GLRenderer3dModel.java <BR>
 * author: Brian Paul (converted to Java by Ron Cemer and Sven Goethel) <P>
 *
 * This version is equal to Brian Paul's version 1.2 1999/10/21
 */
public class GLRenderer3dModel implements GLEventListener {

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
        double[][][] joint = new double[6][3][3];
        updateLocs(joint);

        GL gl = drawable.getGL();

        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Reset the current matrix to the "identity"
        gl.glLoadIdentity();

        gl.glRotatef(-60, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotation, 0.0f, 0.0f, 1.0f);

        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1.0f,1.0f,1.0f);
        for(int i=0; i<6; i++)
        {
            for(int j=0; j<2; j++)
            {
                gl.glVertex3d(joint[i][j][0]/60,joint[i][j][1]/60,-joint[i][j][2]/60);
                gl.glVertex3d(joint[i][j+1][0]/60,joint[i][j+1][1]/60,-joint[i][j+1][2]/60);
            }
            gl.glVertex3d(joint[i][0][0]/60,joint[i][0][1]/60,-joint[i][0][2]/60);
            gl.glVertex3d(joint[(i+1)%6][0][0]/60,joint[(i+1)%6][0][1]/60,-joint[(i+1)%6][0][2]/60);
        }
        gl.glEnd();
        
        //drawFemur(gl, new double[] {0,0,0}, new double[] {1,2,3});

        // Flush all drawing operations to the graphics card
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public static double[][] angle = new double[6][3];
    private static int rotation;

    public static void setAngle(double[][] a) {
        for (int i = 0; i < 6; i++) {
            angle[i][0] = a[i][0];
            angle[i][1] = a[i][1];
            angle[i][2] = a[i][2];
        }
    }

    public static void getAngle(double[][] a) {
        for (int i = 0; i < 6; i++) {
            a[i][0] = angle[i][0];
            a[i][1] = angle[i][1];
            a[i][2] = angle[i][2];
        }
    }

    public static void changeAngle(int leg, int joint, double ang) {
        angle[leg][joint] = ang;
    }

    public static void setRotation(int rot) {
        rotation = rot;
    }

    public void updateLocs(double[][][] joint/*, double[][] angle*/) {
        double r = 30;
        double b1 = 10.394, b2 = 19.606;    // "CaptainAhab Mechanikhandbuch" page 42, scale factor = 0.24205
        for (int i = 0; i < 6; i++) {
            joint[i][0][0] = r * Math.cos((double) ((i * 2 * Math.PI) / 6));	//	System.out.printf("x: %f",joint[i][0][0]);
            joint[i][0][1] = r * Math.sin((double) ((i * 2 * Math.PI) / 6));	//	System.out.printf("\ty: %f\n",joint[i][0][1]);
            joint[i][0][2] = 0.0;
        }

        for (int i = 0; i < 6; i++) {
            /*angle[i][0] = 0;
            angle[i][1] = 45;
            angle[i][2] = 45;*/
            joint[i][1][0] = joint[i][0][0] + (/*r*/b1 * Math.cos(Math.toRadians(angle[i][1]))) * Math.cos(((i * 2 * Math.PI) / 6) + Math.toRadians(angle[i][0]-45));
            joint[i][1][1] = joint[i][0][1] + (/*r*/b1 * Math.cos(Math.toRadians(angle[i][1]))) * Math.sin(((i * 2 * Math.PI) / 6) + Math.toRadians(angle[i][0]-45));
            joint[i][1][2] = joint[i][0][2] + b1 * java.lang.Math.sin(Math.toRadians(angle[i][1]));
        }

        for (int i = 0; i < 6; i++) //unfertig: es fehlen die neuen Winkel; UPDATE: fertig
        {
            joint[i][2][0] = joint[i][1][0] + (/*r*/b2 * Math.cos(Math.toRadians(angle[i][1] + angle[i][2])) /*/r*/) * Math.cos(((i * 2 * Math.PI) / 6) + Math.toRadians(angle[i][0]-45));
            joint[i][2][1] = joint[i][1][1] + (/*r*/b2 * Math.cos(Math.toRadians(angle[i][1] + angle[i][2])) /*/r*/) * Math.sin(((i * 2 * Math.PI) / 6) + Math.toRadians(angle[i][0]-45));
            joint[i][2][2] = joint[i][1][2] + b2 * Math.sin(Math.toRadians(angle[i][1] + angle[i][2])); //erl.
        }
    }

    private void drawCoxa(GL gl, double[] startPos, double[] endPos) {
        
    }

    private void drawFemur(GL gl, double[] startPos, double[] endPos) {
        double l = 12.78, w = 4.841, h = 10.432;    // dimensions of one servo, see HS311 datasheet, scale factor = 0.24205

        // draw a cuboid, consisting of 6 quads
        // one quad is given by 4 points (counter-clockwise)
        gl.glBegin(GL.GL_QUADS);

        gl.glColor3d(1, 1, 1);

        //front
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] + w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] - w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] - w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] + w/2.0, startPos[2] + h/2.0);

        //back
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] + w/2.0, startPos[2] - h/2.0);
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] - w/2.0, startPos[2] - h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] - w/2.0, startPos[2] - h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] + w/2.0, startPos[2] - h/2.0);

        //top
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] + w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] + w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] + w/2.0, startPos[2] - h/2.0);
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] + w/2.0, startPos[2] - h/2.0);

        //bottom
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] - w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] - w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] - w/2.0, startPos[2] - h/2.0);
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] - w/2.0, startPos[2] - h/2.0);

        //left
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] + w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] - w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] - w/2.0, startPos[2] - h/2.0);
        gl.glVertex3d(startPos[0] - l/6.0, startPos[1] + w/2.0, startPos[2] - h/2.0);

        //right
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] + w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] - w/2.0, startPos[2] + h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] - w/2.0, startPos[2] - h/2.0);
        gl.glVertex3d(startPos[0] + 5*l/6.0, startPos[1] + w/2.0, startPos[2] - h/2.0);

        gl.glEnd();
    }

    private void drawTibia(GL gl) {

    }
}

