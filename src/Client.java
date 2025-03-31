import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    private static DatagramSocket socket;
    private static String identifier;  // Nom de l'utilisateur
    private static final int SERVER_PORT = 50000;
    private static Scanner scanner = new Scanner(System.in);

    static {
        try {
            socket = new DatagramSocket();  // Le système va choisir un port disponible

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        // Envoie un message d'initialisation pour s'enregistrer auprès du serveur
        System.out.println("Entrez votre nom d'utilisateur");
        String identifier = scanner.nextLine();
        byte[] initMessage = ("init : " + identifier).getBytes();
        DatagramPacket initPacket = new DatagramPacket(initMessage, initMessage.length, InetAddress.getByName("localhost"), SERVER_PORT);
        socket.send(initPacket);

        // Démarrer un thread pour recevoir les messages du serveur
        ClientHandler clientHandler = new ClientHandler(socket);
        Thread handlerThread = new Thread(clientHandler);
        handlerThread.start();

        // Permet à l'utilisateur d'envoyer un message à un autre utilisateur
        while (true) {
            System.out.println("Destinataire (All pour Broadcast | 'exit' pour quitter) :");
            String recipient = scanner.nextLine();
            if (recipient.equals("exit")) {
                break;
            }

            System.out.println("Entrez votre message :");
            String message = scanner.nextLine();

            String fullMessage = recipient + ":" + message;
            byte[] messageBytes = fullMessage.getBytes();
            DatagramPacket messagePacket = new DatagramPacket(messageBytes, messageBytes.length, InetAddress.getByName("localhost"), SERVER_PORT);
            socket.send(messagePacket);
        }
    }
}
