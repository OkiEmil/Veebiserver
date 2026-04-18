import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class WebrootHandler {
    private final File WEBROOTDIR;

    public WebrootHandler(String webrootLoc) {
        File webrootFolder = new File(webrootLoc);


        if (webrootFolder.exists() && webrootFolder.isDirectory()) {
            WEBROOTDIR = webrootFolder;
        } else {
            throw new RuntimeException("Webroot folder not found"); // maybe make seperate class for this?
        }

    }

    public String getCorrectPath(String path) throws FileNotFoundException {
        File file = new File(WEBROOTDIR, path);
        if (doesFileOrFolderExist(file) && file.isDirectory()) {
            path += "index.html";
        }

        if (!doesFileOrFolderExist(file)) {
            throw new FileNotFoundException("No file found at path");
        }
        return WEBROOTDIR + "\\" + path;
    }

    public boolean doesFileOrFolderExist (File fileToCheck) {
        if (!fileToCheck.exists()) {
            return false;
        }

        try {
            return fileToCheck.getCanonicalPath().startsWith(WEBROOTDIR.getCanonicalPath());
        } catch (Exception e) {
            return false;
        }
    }

    public byte[] getByteArray(String path) throws FileNotFoundException {

        File file = new File(WEBROOTDIR, path);
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

    // unsure, whether this should be used instead in the request handlers
/*
    public String getMimeType(String path) throws IOException {
        File file = new File(WEBROOTDIR, path);
        if (doesFileOrFolderExist(file) && file.isDirectory()) {
            file = new File(file, "index.html");
            path += "index.html";
        }

        if (!doesFileOrFolderExist(file)) {
            throw new FileNotFoundException("No file found at path");
        }

        return FileHandler.getInstance().getMimeType(path);
    }*/


}
