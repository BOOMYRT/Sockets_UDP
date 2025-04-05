import java.net.*;
import java.util.ArrayList;

public class Serveur {

    private static final int MAIN_PORT = 50000;
    private static int nextPort = 55000;
    private static DatagramSocket socket;

    public static ArrayList<ClientHandler> clients = new ArrayList<>(); // Liste des client handlers

    static {
        try {
            socket = new DatagramSocket(MAIN_PORT);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'initialisation du serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Serveur().startServer();
    }

    public void startServer() {
        try {
            System.out.println("Serveur démarré sur le port " + MAIN_PORT);
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packetRecu = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetRecu);
                String message = new String(packetRecu.getData(), 0, packetRecu.getLength());

                // Si le message contient "init", il s'agit d'une initialisation
                if (message.startsWith("init")) {
                    String clientId = message.split(":")[1].trim(); // Identifier l'utilisateur

                    System.out.println("Tentative d'initialisation avec le username : [" + clientId + "]");

                    boolean alreadyExists = false;

                    // vérifier si le username est déjà utilisé par un autre client
                    for (ClientHandler client : clients) {
                        if (client.getUsername().equals(clientId)) {
                            alreadyExists = true;
                            break;
                        }
                    }

                    if (alreadyExists) {
                        System.out.println("nom deja pris");
                        String errorMsg = "Nom déjà utilisé. Choisissez un autre.";
                        byte[] errData = errorMsg.getBytes();
                        DatagramPacket errorPacket = new DatagramPacket(errData, errData.length,
                                packetRecu.getAddress(), packetRecu.getPort());
                        socket.send(errorPacket);
                        continue;
                    }

                    int clientPort = nextPort++;
                    DatagramSocket clientSocket = new DatagramSocket(clientPort);

                    // Envoyer au client son port dédié
                    String confirmationMessage = "OK:" + String.valueOf(clientPort);
                    byte[] responseData = confirmationMessage.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                            responseData, responseData.length, packetRecu.getAddress(), packetRecu.getPort());
                    socket.send(responsePacket);

                    ClientHandler clientHandler = new ClientHandler(clientSocket, clientId, this,
                            packetRecu.getAddress(), packetRecu.getPort());
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                    clients.add(clientHandler);
                    System.out.println("Client " + clientId + " ajouté avec le port dédié : " +
                            clientPort);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void broadcast(String sender, String message) {
        for (ClientHandler handler : clients) {
            if (!handler.getUsername().equals(sender)) {
                System.out.println("envoi message public de " + sender + " à " + handler.getUsername());
                handler.sendMessage("[" + sender + "] : " + message);
            }
        }
    }

    public void sendPrivateMessage(String sender, String recipient, String message) {
        for (ClientHandler handler : clients) {
            if (handler.getUsername().equals(recipient)) {
                System.out.println("envoi message privé de " + sender + " à " + recipient);
                handler.sendMessage("[Message Privé de " + sender + "] : " + message);
                break;
            }
        }

    }

    public void removeClient(String username) {
        for (ClientHandler handler : clients) {
            if (handler.getUsername().equals(username)) {
                clients.remove(handler);
                System.out.println("Client '" + username + "' retiré du serveur.");
                alertClientsWhenClientExit(username);
                break;
            }
        }
    }

    public void alertClientsWhenClientExit(String username) {
        for (ClientHandler client : clients) {
            client.sendMessage("[info] : " + username + " a quitté le chat");
        }
    }
}
