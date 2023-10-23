package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Enter server port:");
        int port = ConsoleHelper.readInt();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            ConsoleHelper.writeMessage("Chat server is running");
            while(true) {
                Socket socket = serverSocket.accept();
                new Handler(socket).start();
            }
        } catch (IOException e) {
            ConsoleHelper.writeMessage("An error occured while starting or running the server.");
        }
    }

    public static void sendBroadcastMessage(Message message) {
        for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
            try {
                entry.getValue().send(message);
            } catch (IOException e) {
                System.out.println("Message couldn't be sent.");
            }
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }


        public void run() {
            ConsoleHelper.writeMessage("Connection has been established with the remote address " + socket.getRemoteSocketAddress());
            try {
                Connection connection = new Connection(socket);
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
                for (String user : connectionMap.keySet()) {
                    if (user.equals(userName)) {
                        connectionMap.remove(user);
                    }
                }
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            } catch (IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("An error occurred while communicating with the remote address");
            }
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));

                Message message = connection.receive();
                if (message.getType() != MessageType.USER_NAME) {
                    ConsoleHelper.writeMessage("Message received from " + socket.getRemoteSocketAddress() + ". The message type does not match the protocol.");
                    continue;
                }

                String userName = message.getData();

                if (userName.isEmpty()) {
                    ConsoleHelper.writeMessage("There was an attempt to connect to the server using an empty name from " + socket.getRemoteSocketAddress());
                    continue;
                }

                if (connectionMap.containsKey(userName)) {
                    ConsoleHelper.writeMessage("There was an attempt to connect to the server using a name that is already being used from " + socket.getRemoteSocketAddress());
                    continue;
                }
                connectionMap.put(userName, connection);

                connection.send(new Message(MessageType.NAME_ACCEPTED));
                return userName;
            }
        }

        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (String name : connectionMap.keySet()) {
                if (name.equals(userName))
                    continue;
                connection.send(new Message(MessageType.USER_ADDED, name));
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Wrong type of message.");
                }
            }
        }
    }
}
