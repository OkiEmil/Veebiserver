import UserManagement.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            Users users=Users.getInstance();
            Runtime.getRuntime().addShutdownHook(new Thread(users::saveUsersToFile));
            SessionManager sessionManager = new SessionManager();
            while (true) { //just runs forever
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket,sessionManager);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
