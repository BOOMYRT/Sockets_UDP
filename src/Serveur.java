import java.net.*;
import java.util.*;

public class Serveur {
    private static Map<Integer, DatagramPacket> clients = new HashMap<>();  // Map pour stocker les paquets des clients

    public static void main(String[] args) {
        try {
            // Création du canal
            DatagramSocket socketServeur = new DatagramSocket(50000);  // Serveur écoute sur le port 50000
            byte[] recues = new byte[1024];  // Tampon pour la réception des messages
            byte[] envoyees;  // Tampon pour l'envoi des messages

            System.out.println("Serveur en écoute...");

            while (true) {
                // Recevoir le paquet du client
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                InetAddress adrClient = paquetRecu.getAddress();
                int portClient = paquetRecu.getPort();

                System.out.println("Message reçu de " + adrClient + ":" + portClient + " - " + message);

                // Enregistrer ou mettre à jour les informations du client
                clients.put(portClient, paquetRecu);

                // Si un autre client est déjà enregistré, retransmettre le message
                for (Map.Entry<Integer, DatagramPacket> entry : clients.entrySet()) {
                    if (entry.getKey() != portClient) {
                        // L'autre client a déjà envoyé un message, on lui envoie le message
                        InetAddress adrAutreClient = entry.getValue().getAddress();
                        int portAutreClient = entry.getValue().getPort();
                        envoyees = message.getBytes();
                        DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adrAutreClient, portAutreClient);
                        socketServeur.send(paquetEnvoye);
                        System.out.println("Message envoyé à " + adrAutreClient + ":" + portAutreClient);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
