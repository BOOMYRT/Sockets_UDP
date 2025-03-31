
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Serveur {
    private static byte[] recues = new byte[1024];
    private static final int PORT = 50000;

    private static DatagramSocket socket;

    static {
        try{
            socket = new DatagramSocket(PORT);
            InetSocketAddress adresse = new InetSocketAddress("localhost", 50000);
            socket.bind(adresse);
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private static ArrayList<Integer> clients = new ArrayList<>();


//    public void startServer() {
//        try {
//
//            byte[] envoyees; //tampon de réception
//
//            while(!serverSocket.isClosed()) {
//                //recevoir
//                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
//                serverSocket.receive(paquetRecu);
//                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
//
//                System.out.println("Un nouveau client s'est connecté");
//                ClientHandler clientHandler = new ClientHandler(serverSocket);
//                Thread thread = new Thread(clientHandler);
//                thread.start();
//            }
//
//
//        }
//        catch (Exception e) {
//            System.err.println(e);
//        }
//    }

//    public void closeServerSocket(){
//        try{
//            if(serverSocket!=null){
//                serverSocket.close();
//            }
//
//        }catch (Exception e){
//            System.err.println(e);
//        }
//    }

    public static void main(String[] args) throws IOException {


//        DatagramSocket datagramSocket = new DatagramSocket(null);
//        InetSocketAddress adresse = new InetSocketAddress("localhost", 50000);
//        datagramSocket.bind(adresse);
//        Serveur serveur = new Serveur(datagramSocket);
//        serveur.startServer();

        System.out.println("Le serveur a démarré sur le port : " + PORT);

        while(true){
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            try{
                socket.receive(paquetRecu);
            }catch (IOException e){
                throw new RuntimeException(e);
            }

            String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Le serveur a reçu le message : " + message);

            if(message.contains("init")){
                clients.add(paquetRecu.getPort());
                //envoyer un message à tous les autres clients deja connectés

            } else {
                int clientPort = paquetRecu.getPort();
                byte[] byteMessage = message.getBytes();

                for (int forwardPort : clients) {
                    if(forwardPort != clientPort){
                        DatagramPacket forward = new DatagramPacket(byteMessage, byteMessage.length, socket.getInetAddress(), forwardPort );
                        try{
                            socket.send(forward);
                        } catch (IOException e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

        }
    }
}
