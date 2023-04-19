/**
 *
 *  @author Wojas Kamil S23878
 *
 */

package zad1;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ChatServer {
    private final int port;
    private final String host;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private final Map<SocketChannel, String> clients = new HashMap<>();
    private final List<String> chatLog = new ArrayList<>();
    public ChatServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void startServer() {
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(host, port));
            serverSocketChannel.configureBlocking(false);
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int numKeys = selector.select();
                if (numKeys == 0) {
                    continue;
                }
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        acceptConnection(key);
                    } else if (key.isReadable()) {
                        processRequest(key);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    private void acceptConnection(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    private void processRequest(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int numRead = clientChannel.read(buffer);
        if (numRead == -1) {
            disconnectClient(clientChannel);
            return;
        }
        String request = new String(buffer.array()).trim();
        if (request.startsWith("login:")) {
            loginClient(clientChannel, request.substring(6).trim());
        } else if (request.equals("logout")) {
            disconnectClient(clientChannel);
        } else {
            broadcastMessage(clients.get(clientChannel), request);
        }
    }

    private void loginClient(SocketChannel clientChannel, String clientId) {
        clients.put(clientChannel, clientId);
        broadcastMessage(clientId, "logged in");
    }

    private void disconnectClient(SocketChannel clientChannel) throws IOException {
        String clientId = clients.get(clientChannel);
        clientChannel.close();
        clients.remove(clientChannel);
        broadcastMessage(clientId, "logged out");
    }

    private void broadcastMessage(String clientId, String message) {
        chatLog.add(clientId + ": " + message);
        for (SocketChannel channel : clients.keySet()) {
            if (channel.isOpen()) {
                try {
                    channel.write(ByteBuffer.wrap((clientId + ": " + message).getBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void stopServer() {
        try {
            selector.close();
            serverSocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerLog() {
        StringBuilder sb = new StringBuilder();
        for (String line : chatLog) {
            sb.append(line).append(System.lineSeparator());
        }
        return sb.toString();
    }


}
