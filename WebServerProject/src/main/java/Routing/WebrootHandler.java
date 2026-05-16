package Routing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class WebrootHandler {
    private final File WEBROOTDIR;
    private boolean isDirectoryListing = true;
    private final String routePrefix;

    public WebrootHandler(String webrootLoc,String routePrefix) {
        File webrootFolder = new File(webrootLoc);
        this.routePrefix=routePrefix;

        if (webrootFolder.exists() && webrootFolder.isDirectory()) {
            WEBROOTDIR = webrootFolder;
        } else {
            throw new RuntimeException("Webroot folder not found"); // maybe make seperate class for this?
        }

    }

    public String getCorrectPath(String path) {
        System.out.println(path + "HEREE");
        File file = new File(WEBROOTDIR, path);
        if (file.isDirectory()) {
            if (doesFileOrFolderExist(new File(WEBROOTDIR, path + "index.html"))) {
                path += "index.html";
            }

        }
        System.out.println("New path: " + WEBROOTDIR + "\\" +  path);
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
        //System.out.println("HERE!");
        File file = new File(WEBROOTDIR, path);
        if (doesFileOrFolderExist(file) && file.isDirectory()) {
            file = new File(file, "index.html");
            if (!doesFileOrFolderExist(file) && isDirectoryListing) {
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
        System.out.println("path: " + path.toString());
        // https://codereview.stackexchange.com/questions/117451/scanning-a-directory-and-listing-contents-in-an-html-file
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
                        String requestPathFinal = routePrefix;
                        if (!requestPathFinal.endsWith("/")) {
                            requestPathFinal+="/";
                        }
                        requestPathFinal+=name;
                        try {
                            html.append("\t\t\t<li> <a href=\"")
                                    .append(requestPathFinal)
                                    .append("\">")
                                    .append(name)
                                    .append("</a>")
                                    .append(" (")
                                    .append(humanReadableByteCountSI(Files.size(p)))
                                    .append(") Last modified ")
                                    .append(Files.getLastModifiedTime(p))
                                    .append("</li>\n");
                        } catch (IOException e) {
                            throw new RuntimeException("Error while getting file sizes or last modified time");
                        }
                    });
            html.append("\t\t</ul>\n\t</body>\n</html>");
            return html.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error while making directory listing");
        }

    }
    //https://stackoverflow.com/questions/3758606/how-can-i-convert-byte-size-into-a-human-readable-format-in-java
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
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
