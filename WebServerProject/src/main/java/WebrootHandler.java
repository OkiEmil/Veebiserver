import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            if (doesFileOrFolderExist(new File(WEBROOTDIR, path + "index.html"))) {

                path += "index.html";
            }

        } else if (!doesFileOrFolderExist(file)) {
            System.out.println(path + " no file found here");
            throw new FileNotFoundException("No file found at path");
        }
        System.out.println("New path: " + WEBROOTDIR + path);
        return WEBROOTDIR + path;
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
        System.out.println("HERE!");
        File file = new File(WEBROOTDIR, path);
        if (doesFileOrFolderExist(file) && file.isDirectory()) {
            file = new File(file, "index.html");
            if (!doesFileOrFolderExist(file)) {
                return getDirectoryListing(new File(WEBROOTDIR, path));
            }

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

    private byte[] getDirectoryListing(File folder) {

        Path path = folder.toPath();

        StringBuilder html = new StringBuilder();
        try {
            html.append("<html>\n\t<body>\n\t\t<h1>Contents of ").append(path)
                    .append(":</h1>\n\t\t<ul>\n");
            Files.list(path)
                    .forEach( p -> {
                        String name = p.getFileName().toString();
                        System.out.println(name);
                        if (Files.isDirectory(p)) {
                            name += "/";
                        }
                        html.append("\t\t\t<li> <a href=")
                                .append(name)
                                .append(">")
                                .append(name)
                                .append("</a> </li>\n");
                    });
            html.append("\t\t</ul>\n\t</body>\n</html>");
            return html.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
