
import java.net.*;

public class Client {
    public Client() {
    }

    public void run(){
        try {
            // Création du canal
            DatagramSocket socketClient = new DatagramSocket();
            InetAddress adresseClient = InetAddress.getByName("localhost");
            byte[] envoyees;
            byte[] recues = new byte[1024];
            // Émettre
            String message = "Bonjour !";
            envoyees = message.getBytes();
            DatagramPacket messageEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseClient, 6666);
            socketClient.send(messageEnvoye);
            // Recevoir
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketClient.receive(paquetRecu);
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Depuis le serveur: " + reponse);
            // Libérer le canal
            socketClient.close();
        }catch (Exception e){
            System.err.println(e);
        }
    }
}
