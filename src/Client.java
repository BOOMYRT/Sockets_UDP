import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            // Création du canal
            DatagramSocket socketClient = new DatagramSocket();
            InetAddress adresseServeur = InetAddress.getByName("localhost");
            byte[] envoyees;
            byte[] recues = new byte[1024];

            // Scanner pour lire l'entrée utilisateur
            Scanner scanner = new Scanner(System.in);

            // Demander à l'utilisateur de saisir son nom
            System.out.print("Entrez votre nom : ");
            String nomClient = scanner.nextLine();

            // Lancer un thread pour recevoir les messages du serveur
            Thread recepteur = new Thread(() -> {
                try {
                    while (true) {
                        // Recevoir les messages du serveur
                        DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                        socketClient.receive(paquetRecu);
                        String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                        System.out.println("Message reçu : " + message);
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            });
            recepteur.start();  // Démarrer le thread de réception des messages

            while (true) {
                // Demander à l'utilisateur de saisir un message
                System.out.print("Message : \n");
                String messageUtilisateur = scanner.nextLine();

                // Si l'utilisateur entre "exit", on quitte la boucle
                if (messageUtilisateur.equalsIgnoreCase("exit")) {
                    System.out.println("Déconnexion...");
                    break;
                }

                // Ajouter un préfixe pour personnaliser le message avec le nom du client
                String message = nomClient + " dit : " + messageUtilisateur + "\n";
                envoyees = message.getBytes();

                // Envoi du message au serveur
                DatagramPacket messageEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseServeur, 50000);
                socketClient.send(messageEnvoye);
            }

            // Fermeture du socket après la fin de la boucle
            socketClient.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
