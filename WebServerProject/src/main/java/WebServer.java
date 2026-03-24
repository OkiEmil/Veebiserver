import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {
    static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {

            while (true) { //just runs forever
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
