package edu.hartnell.iris.utility;

import edu.hartnell.iris.Iris;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceUtils {

    public static String ExportResource(String resourceName, String extendedFolder)
            throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        String jarFolder;
        try {

            stream = Iris.class.getResourceAsStream(resourceName);
            if(stream == null) {
                throw new Exception
                        ("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];

            jarFolder = new File(Iris.class.getProtectionDomain().getCodeSource()
                    .getLocation().toURI().getPath()).getParentFile().getPath()
                    .replace('\\', '/');

            resStreamOut =
                    new FileOutputStream(jarFolder + (!extendedFolder.equalsIgnoreCase("")
                            ? ( "/" + extendedFolder ):("")) + resourceName);

            Iris.warn("Output: " + (!extendedFolder.equalsIgnoreCase("")
                    ? ( "/" + extendedFolder ):("")) + resourceName);

            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            stream.close();
            resStreamOut.close();
        }
        return jarFolder + (!extendedFolder.equalsIgnoreCase("")
                ? ( "/" + extendedFolder ):("")) + resourceName;
    }

}
