import java.util.*;

public class FileHandler
{
	// Singleton instance.
	private static FileHandler instance;

	private FileHandler()
	{

	}

	public static synchronized FileHandler getInstance()
	{
		return instance;
	}

	public String readFileAsString(String path)
	{
		return new String();
	}

	public List<String> readFileAsLines(String path)
	{
		return null;
	}

	public byte[] readFileAsBytes(String path)
	{
		return new byte[0];
	}

	public void writeStringToFile(String path, String content)
	{

	}

	public void writeLinesToFile(String path, List<String> lines)
	{

	}

	public void writeBytesToFile(String path, byte[] bytes)
	{

	}

	public boolean fileExists(String path)
	{
		return false;
	}

	public void createFile(String path)
	{

	}

	public void deleteFile(String path)
	{

	}

	public void createDirectory(String path)
	{

	}
}