import UserManagement.Users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class WebServer {
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(80)) {
            Users users=Users.getInstance();
            Runtime.getRuntime().addShutdownHook(new Thread(users::saveUsersToFile));
            ServerRouter serverrouter = new ServerRouter();
            long last_saved = System.currentTimeMillis();
            while (true) { //just runs forever
                if (System.currentTimeMillis()-last_saved>600000) { // periodically saves the userdata (every 10 mins)
                    new Thread(users::saveUsersToFile);
                    last_saved=System.currentTimeMillis();
                }
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket,serverrouter);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
