import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger
{
	private Path logFilePath;

	public Logger(String logPath)
	{
		try
		{
			String dateTime = LocalDate.now().toString();
			logPath = logPath + "_" + dateTime + ".log";
			logFilePath = Path.of(logPath);

			Path parentDir = logFilePath.getParent();
			if (parentDir != null && !Files.exists(parentDir)) {
    			Files.createDirectories(parentDir);
			}

			if(!Files.exists(Path.of(logPath)))
				Files.createFile(logFilePath);
		}
		catch(IOException  exception)
		{
			System.out.println("Failed to create log file: " + exception.getLocalizedMessage());
			exception.printStackTrace();
		}
	}

	public void log(String message, boolean includeDate)
	{
		try
		{
			if(includeDate)
			{
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				//message = "[" + LocalDate.now().toString() +  "]: " + message;
				message = "[" + LocalDateTime.now().format(formatter) + "]: " + message;
			}
			Files.writeString(logFilePath, message + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		catch(IOException  exception)
		{
			System.out.println("Failed to write log to file:" + exception.getLocalizedMessage());
			exception.printStackTrace();
		}
	}

	public static void logStatic(ENamedStaticLogger context, String message, boolean includeDate)
	{
		Path localPath;
		
		switch(context)
		{
			default:
				return;
			
			case REQUEST_GET:
				localPath = Path.of("Logs/GetRequests.log");
				break;
			case REQUEST_POST:
				localPath = Path.of("Logs/PostRequests.log");
				break;
			case REQUEST_DOWNLOAD:
				localPath = Path.of("Logs/DownloadRequests.log");
				break;
		}

		try
		{
			

			Path parentDir = localPath.getParent();
			if (parentDir != null && !Files.exists(parentDir)) {
    			Files.createDirectories(parentDir);
			}

			if(!Files.exists(localPath))
				Files.createFile(localPath);

			if(includeDate)
			{
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				//message = "[" + LocalDate.now().toString() +  "]: " + message;
				message = "[" + LocalDateTime.now().format(formatter) + "]: " + message;
			}

			Files.writeString(localPath, message + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		catch(IOException  exception)
		{
			System.out.println("Failed to create log file: " + exception.getLocalizedMessage());
			exception.printStackTrace();
		}
	}

}