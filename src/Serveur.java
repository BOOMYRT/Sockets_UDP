import java.net.*;
import java.util.*;

public class Serveur {
    public static void main(String[] args) {
        try {
            // Création du canal pour le serveur
            DatagramSocket socketServeur = new DatagramSocket(null);

            // Réservation du port
            InetSocketAddress adresse = new InetSocketAddress("localhost", 50000);
            socketServeur.bind(adresse);

            byte[] recues = new byte[1024]; // Tampon pour recevoir des messages
            List<InetSocketAddress> clients = new ArrayList<>(); // Liste des clients connectés

            System.out.println("Serveur en écoute...");

            while (true) {
                // Recevoir un message du client
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());

                // Afficher le message du client
                System.out.println("Reçu de " + paquetRecu.getAddress() + ":" + paquetRecu.getPort() + " -> " + message);

                // Ajouter le client à la liste s'il n'y est pas déjà
                InetSocketAddress clientAddr = new InetSocketAddress(paquetRecu.getAddress(), paquetRecu.getPort());
                if (!clients.contains(clientAddr)) {
                    clients.add(clientAddr);
                }

                // Transmettre le message à tous les clients connectés
                for (InetSocketAddress client : clients) {
                    if (!client.equals(clientAddr)) { // Ne pas envoyer le message au client émetteur
                        byte[] envoyees = message.getBytes();
                        DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, client.getAddress(), client.getPort());
                        socketServeur.send(paquetEnvoye);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
