import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

public class WebrootHandler {
    private File webrootDir;

    public WebrootHandler(String webrootLoc) {
        File webrootFolder = new File(webrootLoc);

        if (webrootFolder.exists() && webrootFolder.isDirectory()) {
            webrootDir = webrootFolder;
        } else {
            throw new RuntimeException("Webroot folder not found"); // maybe make seperate class for this?
        }

    }

    public boolean doesFileOrFolderExist (File fileToCheck) {
        if (!fileToCheck.exists()) {
            return false;
        }

        try {
            return fileToCheck.getCanonicalPath().startsWith(webrootDir.getCanonicalPath());
        } catch (Exception e) {
            return false;
        }
    }

    public byte[] getByteArray(String path) throws FileNotFoundException {

        File file = new File(webrootDir, path);
        if (doesFileOrFolderExist(file) && file.isDirectory()) {
            file = new File(file, "index.html");
        }

        if (!doesFileOrFolderExist(file)) {
            throw new FileNotFoundException("No file found at path");
        }

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error while reading bytes from file");
        }

    }

    public String getMimeType(String path) throws IOException {
        File file = new File(webrootDir, path);
        if (doesFileOrFolderExist(file) && file.isDirectory()) {
            file = new File(file, "index.html");
        }

        if (!doesFileOrFolderExist(file)) {
            throw new FileNotFoundException("No file found at path");
        }

        return Files.probeContentType(file.toPath());
    }


}
