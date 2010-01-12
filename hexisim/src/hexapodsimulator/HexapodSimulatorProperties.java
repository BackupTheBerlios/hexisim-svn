/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hexapodsimulator;

import java.io.File;
import java.io.Serializable;

/**
 *
 * @author peter
 */
public class HexapodSimulatorProperties implements Serializable {

    public File projectFile;
    public boolean normalizeInput = true;
    public boolean interpolateOutput = true;
}
