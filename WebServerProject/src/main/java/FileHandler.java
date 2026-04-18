import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;

public class FileHandler
{


	public static synchronized FileHandler getInstance()
	{
		if (instance == null) {
			instance = new FileHandler();
		}
		return instance;
	}

	public String readFileAsString(String path)
	{
		return readFileAsStringImpl(path);
		//return new String();
	}

	public List<String> readFileAsLines(String path)
	{
		return readFileAsLinesImpl(path);
		//return null;
	}

	public byte[] readFileAsBytes(String path)
	{
		return readFileAsBytesImpl(path);
		//return new byte[0];
	}

	public boolean writeStringToFile(String path, String content)
	{
		return writeStringToFileImpl(path, content);
	}

	public boolean writeLinesToFile(String path, List<String> lines)
	{
		return writeLinesToFileImpl(path, lines);
	}

	public boolean writeBytesToFile(String path, byte[] bytes)
	{
		return writeBytesToFileImpl(path, bytes);
		//return false;
	}

	public boolean fileExists(String path)
	{
		Path fileName = Path.of(path);

		// TODO: add check for user allowed paths.
		return Files.exists(fileName);
	}

	public boolean createFile(String path)
	{
		try
		{
			Path fileName = Path.of(path);
			Files.createFile(fileName);
			return true;
		}
		catch(IOException exception)
		{
			//TODO: add exception to log.
			logger.log("Failed to create file: " + path + " with exception: " + exception.getLocalizedMessage(), true);
			return false;
		}
		
	}

	public boolean deleteFile(String path)
	{
		try
		{
			Path fileName = Path.of(path);
			Files.delete(fileName);
			return true;
		}
		catch(IOException exception)
		{
			//TODO: add exception to log.
			logger.log("Failed to delete file: " + path + " with exception: " + exception.getLocalizedMessage(), true);
			return false;
		}
	}

	public boolean createDirectory(String path)
	{
		try
		{
			Path directoryName = Path.of(path);
			Files.createDirectory(directoryName);
			return true;
		}
		catch(IOException exception)
		{
			//TODO: add exception to log.
			logger.log("Failed to create directory: " + path + " witht exception:" + exception.getLocalizedMessage(), true);
			return false;
		}
	}

	public String getMimeType(String path) throws IOException{
		return getMimeTypeImpl(path);
	}

	public long getFileSize(String path) throws IOException {
		return getFileSizeImpl(path);
	}


	// DO NOT EXPOSE IT TO OUTER CLASSES!
	private HashSet<Path> userAllowedPaths = new HashSet<Path>();

	private HashSet<Path> systemAllowedDirectories = new HashSet<Path>();

	private Path baseUserPath = null;//Path.of("").toRealPath();

	//private Charset defaultEncoding;
	
	// Singleton instance.
	private static FileHandler instance;

	private static Logger logger;

	private FileHandler()
	{
		logger = new Logger("FileHandler.log_" + LocalDate.now().toString() + ".txt");

		try
		{
			baseUserPath = Path.of("").toRealPath();
		}
		catch(Exception exception)
		{
			//TODO: add exception to log and terminate program.
			logger.log("Failed to get real path of current directory: " + exception.getLocalizedMessage(), true);
		}
	}

	private boolean isUserPathAllowed(String path)
	{
		Path normalized = baseUserPath.resolve(path).normalize();

		for(Path allowed : userAllowedPaths)
		{
			if(normalized.startsWith(allowed))
				return true;
		}

		return false;
	}

	private String readFileAsStringImpl(String path)
	{
		try
		{
			Path fileName = Path.of(path);

			String out = Files.readString(fileName);
			
			return out;
		}
		catch(Exception exception)
		{
			// TODO: Add exception to log.
			logger.log("Failed to read string from file:" + path + " with exception:" + exception.getLocalizedMessage(), true); 
			return new String();
		}
	}

	private List<String> readFileAsLinesImpl(String path)
	{
		BufferedReader reader;

		try
		{
			List<String> out = new ArrayList<>();

			reader = new BufferedReader(new FileReader(path));
			String line = reader.readLine();

			while(line != null)
			{
				out.add(line);
				line = reader.readLine();
			}

			reader.close();
			return out;
		}
		catch(Exception exception)
		{
			//TODO: Add to log.
			logger.log("Failed to read lines from file:" + path + " with exception:" + exception.getLocalizedMessage(), true); 
			return null;
		}
	}

	private boolean writeStringToFileImpl(String path, String content)
	{
		try
		{
			Path fileName = Path.of(path);
			Files.writeString(fileName, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

			return true;
		}
		catch(Exception exception)
		{
			// TODO: Add exception to log.
			logger.log("Failed to write content to file:" + path + " with exception:" + exception.getLocalizedMessage(), true); 
			return false;
		}
	}

	private boolean writeLinesToFileImpl(String path, List<String> lines)
	{
		try
		{
			Path fileName = Path.of(path);

			Files.write(fileName, lines);

			return true;
		}
		catch(Exception exception)
		{
			//TODO: add exception to log.
			logger.log("Failed to write lines to file:" + path + " with exception:" + exception.getLocalizedMessage(), true); 
			return false;
		}
	}

	private String getMimeTypeImpl(String path) throws IOException
	{
		try {
			Path fileName = Path.of(path);
			String mimeType = Files.probeContentType(fileName);

			if (mimeType == null) return "application/octet-stream"; // undetermined filetype
			return mimeType;
		} catch (IOException exception){
			logger.log("Failed to fetch MIME type for " + path + " with exception:" + exception.getLocalizedMessage(), true);
			throw new IOException();
		}
	}

	private long getFileSizeImpl(String path) throws IOException
	{
		try {
			Path fileName = Path.of(path);
			return Files.size(fileName);
		} catch (IOException exception) {
			logger.log("Failed to fetch FileSize for " + path + " with exception: " + exception.getLocalizedMessage(), true);
			throw new IOException();
		}
	}

	private byte[] readFileAsBytesImpl(String path)
	{
		try {
			Path fileName = Path.of(path);
			return Files.readAllBytes(fileName);
		}
		catch(Exception exception)
		{
			logger.log("Failed to read bytes from file: " + path + " with exception: " + exception.getLocalizedMessage(), true);
			return new byte[0];
		}
	}

	private boolean writeBytesToFileImpl(String path, byte[] bytes)
	{
		try
		{
			Path fileName = Path.of(path);
			Files.write(fileName, bytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			return true;
		}
		catch(Exception exception)
		{
			logger.log("Failed to write bytes to file: " + path + " with exception: " + exception.getLocalizedMessage(), true);
			return false;
		}
	}
}