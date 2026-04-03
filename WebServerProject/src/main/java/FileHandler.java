import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.BufferedReader;
import java.io.FileReader;

public class FileHandler
{


	public static synchronized FileHandler getInstance()
	{
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

	/*public byte[] readFileAsBytes(String path)
	{
		return new byte[0];
	}*/

	public boolean writeStringToFile(String path, String content)
	{
		return writeStringToFileImpl(path, content);
	}

	public boolean writeLinesToFile(String path, List<String> lines)
	{
		return writeLinesToFileImpl(path, lines);
	}

	/*public boolean writeBytesToFile(String path, byte[] bytes)
	{
		return false;
	}*/

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
			return false;
		}
	}


	// DO NOT EXPOSE IT TO OUTER CLASSES!
	private HashSet<Path> userAllowedPaths = new HashSet<Path>();

	private HashSet<Path> systemAllowedDirectories = new HashSet<Path>();

	private Path baseUserPath = null;//Path.of("").toRealPath();

	//private Charset defaultEncoding;
	
	// Singleton instance.
	private static FileHandler instance;

	private FileHandler()
	{
		try
		{
			baseUserPath = Path.of("").toRealPath();
		}
		catch(Exception exception)
		{
			//TODO: add exception to log and terminate program.
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
			return false;
		}
	}
}