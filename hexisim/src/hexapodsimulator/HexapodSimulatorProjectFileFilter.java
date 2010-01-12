package hexapodsimulator;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * File filter for Hexapod Simulator Project files (*.hexisimproj)
 * @author peter
 */
public class HexapodSimulatorProjectFileFilter extends FileFilter {

    public static String description = "Hexapod Simulator Project Files";
    public static String extension = ".hexisimproj";

    @Override
    public boolean accept(File f) {
        // Returns true when f ends with the specified extension and is not a directory
        return (f.getName().endsWith(extension) /*&& !f.isDirectory()*/ || f.isDirectory());
    }

    @Override
    public String getDescription() {
        return description;
    }
}
