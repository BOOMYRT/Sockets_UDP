
import java.net.*;

public class Serveur {
    public static void main(String[] args) {
        try {
            //création du canal
            DatagramSocket socketServeur = new DatagramSocket(null);

            //Réservation du port
            InetSocketAddress adresse = new InetSocketAddress("localhost", 50000);
            socketServeur.bind(adresse);
            byte[] recues = new byte[1024]; //tampon d'émission
            byte[] envoyees; //tampon de réception

            while(true) {
                //Recevoir
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("Reçu: " + message);

                //Émettre
                InetAddress adrClient = paquetRecu.getAddress();
                int prtClient = paquetRecu.getPort();
                String reponse = "Accusé de reception";
                envoyees = reponse.getBytes();
                DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adrClient, prtClient);
                socketServeur.send(paquetEnvoye);
            }
            //libérer le canal
//            socketServeur.close();

        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
}
