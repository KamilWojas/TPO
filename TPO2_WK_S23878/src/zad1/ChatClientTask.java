/**
 *
 *  @author Wojas Kamil S23878
 *
 */

package zad1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

public class ChatClientTask {
    private final Socket socket;
    private final String username;

    public ChatClientTask(Socket socket, String username) {
        this.socket = socket;
        this.username = username;
    }

    public static ChatClientTask create(ChatClient c, List<String> msgs, int wait) {
        Socket socket = new Socket();
        InetSocketAddress address = new InetSocketAddress(c.getHost(), c.getPort());
        try {
            socket.connect(address);
            return new ChatClientTask(socket, c.getUsername());
        } catch (IOException e) {
            throw new RuntimeException("Unable to create ChatClientTask", e);
        }
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send the user's username to the server
            out.println(username);

            // Read messages from the server and print them to the console
            while (true) {
                String message = in.readLine();
                if (message == null) {
                    break;
                }
                System.out.println(message);
            }

            // Clean up resources
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        }
    }

    public ChatClient getClient() {
        return new ChatClient(socket.getInetAddress().getHostName(), socket.getPort(), username);
    }

    public void get() {
    }
}