import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDate;

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

			if(Files.exists(Path.of(logPath), null))
				Files.createFile(logFilePath);
		}
		catch(Exception exception)
		{
			System.out.println("Failed to create log file: " + exception.getLocalizedMessage());
		}
	}

	public void log(String message, boolean includeDate)
	{
		try
		{
			if(includeDate)
			{
				message = "[" + LocalDate.now().toString() +  "]: " + message;
			}
			Files.writeString(logFilePath, message + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		catch(Exception exception)
		{
			System.out.println("Failed to write log to file:" + exception.getLocalizedMessage());
		}
	}


}