import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Serveur {

    private static final int PORT = 50000;
    private static DatagramSocket socket;
    private static ClientHandler Gestionnaire = new ClientHandler(socket);

    static {
        try {
            socket = new DatagramSocket(PORT);
        } catch (Exception e) {
            System.out.println("Erreur lors de l'initialisation du serveur : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println("Serveur démarré sur le port " + PORT);

        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packetRecu = new DatagramPacket(buffer, buffer.length);
                socket.receive(packetRecu);
                String message = new String(packetRecu.getData(), 0, packetRecu.getLength());

                System.out.println("Message reçu du client : " + message);

                // Si le message contient "init", il s'agit d'une initialisation
                if (message.contains("init")) {
                    String clientId = message.split(":")[1].trim();  // Identifier l'utilisateur
                    Gestionnaire.addClient(clientId, packetRecu);
                    System.out.println("Client " + clientId + " ajouté avec le port : " + packetRecu.getPort());
                } else {
                    // Exemple : format attendu -> "destinataire:message"
                    String[] parts = message.split(":");
                    if (parts.length == 2) {
                        String recipientId = parts[0].trim();
                        String messageToSend = parts[1].trim();

                        System.out.println(recipientId);

                        // Si le destinataire existe, on lui envoie le message
                        
                        System.out.println(Gestionnaire.clients.get(recipientId));

                        if (Gestionnaire.clients.get(recipientId) != null) {

                            System.out.println("first for");

                            byte[] byteMessage = messageToSend.getBytes();
                            DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, Gestionnaire.clients.get(recipientId).getAddress(), Gestionnaire.clients.get(recipientId).getPort());
                            socket.send(forward);
                            System.out.println("Message envoyé à " + recipientId);
                            
                        } 
                        
                        // Si le message commence par All, on envoie à tout le monde

                        else if (recipientId.equals("all")) {
                            System.out.println("if all");

                                byte[] byteMessage = messageToSend.getBytes();
                                for (Map.Entry<String, ClientHandler.ClientInfo> entry : Gestionnaire.clients.entrySet()) {
                                    // Utilisez 'entry.getValue()' pour obtenir l'objet ClientInfo de chaque client
                                    InetAddress clientAddress = entry.getValue().getAddress();  // L'adresse du client
                                    int clientPort = entry.getValue().getPort();  // Le port du client
                                
                                    // Créez un DatagramPacket pour envoyer le message
                                    DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, clientAddress, clientPort);
                                    socket.send(forward);  // Envoie du message au client
                                    System.out.println("Message envoyé à " + entry.getKey());  // Affiche le client qui a reçu le message
                                }
                            }
                        }


                        

                        
                    }
                }
             catch (Exception e) {
                e.printStackTrace();
            }
        }}} 

    // Classe interne pour stocker les informations d'un client (adresse et port)
   

