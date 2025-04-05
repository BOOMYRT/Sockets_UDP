import java.net.*;
import java.util.Scanner;

public class Client {

    private static DatagramSocket socket;
    private static final int SERVER_PORT = 50000;
    private static Scanner scanner = new Scanner(System.in);
    private static int serverDedicatedPort;

    static {
        try {
            socket = new DatagramSocket(); // Le système va choisir un port disponible

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        try {

            boolean isUsernameTaken = true;

            while (isUsernameTaken) {
                // Envoie un message d'initialisation pour s'enregistrer auprès du serveur
                System.out.println("Entrez votre nom d'utilisateur");
                String identifier = scanner.nextLine();

                // envoyer le pseudo du client au serveur
                byte[] initMessage = ("init : " + identifier).getBytes();
                DatagramPacket initPacket = new DatagramPacket(initMessage, initMessage.length,
                        InetAddress.getByName("localhost"), SERVER_PORT);
                socket.send(initPacket);

                // Recevoir le port attribué du serveur
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);

                String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());

                if (serverResponse.startsWith("Nom déjà utilisé")) {
                    System.out.println(serverResponse);
                } else if (serverResponse.startsWith("OK:")) {
                    serverDedicatedPort = Integer
                            .parseInt(serverResponse.split(":")[1]);
                    System.out.println("Connecté avec succès. Port dédié : " + serverDedicatedPort);
                    isUsernameTaken = false;

                    // Vider les anciens paquets éventuels du socket
                    socket.setSoTimeout(100); // Timeout court
                    try {
                        while (true) {
                            DatagramPacket flushPacket = new DatagramPacket(new byte[1024], 1024);
                            socket.receive(flushPacket); // Essaie de vider tout ce qui traîne
                        }
                    } catch (SocketTimeoutException ignored) {
                        // Aucun paquet en attente : on peut continuer
                    }
                    socket.setSoTimeout(0);
                }

            }

            // Thread pour recevoir les messages
            new Thread(() -> {
                byte[] buffer = new byte[1024];
                while (true) {
                    try {
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);
                        String message = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("\n" + message + "\n");
                        System.out.println("Destinataire ('all' pour Broadcast | 'exit' pour quitter) :");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // Permet à l'utilisateur d'envoyer un message à un autre utilisateur via port
            // dédié du serveur
            while (true) {
                System.out.println("Destinataire ('all' pour Broadcast | 'exit' pour quitter) :");
                String recipient = scanner.nextLine();
                if (recipient.equals("exit")) {
                    byte[] bufferQuit = recipient.getBytes();
                    DatagramPacket quitPacket = new DatagramPacket(bufferQuit, bufferQuit.length,
                            InetAddress.getByName("localhost"), serverDedicatedPort);
                    socket.send(quitPacket);
                    System.out.println("Déconnexion...");
                    socket.close();
                    System.exit(0);
                    break;
                }

                System.out.println("Entrez votre message :");
                String message = scanner.nextLine();

                String fullMessage = recipient + ":" + message;
                byte[] messageBytes = fullMessage.getBytes();
                DatagramPacket messagePacket = new DatagramPacket(messageBytes, messageBytes.length,
                        InetAddress.getByName("localhost"), serverDedicatedPort);
                socket.send(messagePacket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
