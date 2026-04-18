import java.io.*;

public class FilePayload implements Serializable
{
    public String fileName;
    public String mimeType;
    public byte[] content;

    FilePayload(String fileName, String mimeType, byte[] content)
    {
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.content = content;
    }
}
