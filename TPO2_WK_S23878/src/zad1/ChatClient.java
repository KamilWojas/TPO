/**
 *
 *  @author Wojas Kamil S23878
 *
 */

package zad1;


import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClient {
    private final String host;
    private final int port;
    private final String id;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private final BlockingQueue<String> chatView = new LinkedBlockingQueue<>();

    public ChatClient(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public void login() throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        out.println("LOGIN " + id);
        chatView.add("Logged in as " + id);
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    chatView.add(line);
                }
            } catch (IOException e) {
                chatView.add("Error receiving message: " + e.getMessage());
            }
        }).start();
    }

    public void logout() {
        out.println("LOGOUT " + id);
        try {
            socket.close();
        } catch (IOException e) {
            chatView.add("Error closing socket: " + e.getMessage());
        }
        chatView.add("Logged out");
    }

    public void send(String req) {
        out.println(req);
    }

    public String getChatView() {
        StringBuilder sb = new StringBuilder();
        chatView.drainTo(sb);
        return sb.toString();
    }

    public String getHost() {
        return null;
    }

    public String getUsername() {
    }

    public int getPort() {
        return 0;
    }
}

