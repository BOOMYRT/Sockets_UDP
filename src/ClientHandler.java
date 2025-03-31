import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler implements Runnable {
    private DatagramSocket socket;
    public static Map<String, ClientInfo> clients = new HashMap<>();  // Mapping des clients par identifiant
    

    public ClientHandler(DatagramSocket socket) {
        
        this.socket = socket;
    }

    public void addClient(String clientId, DatagramPacket packetRecu) {
        clients.put(clientId, new ClientInfo(packetRecu.getAddress(), packetRecu.getPort()));
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Message reçu : " + message);  // Afficher dans la console
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ClientInfo {
        private InetAddress address;
        private int port;

        public ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        public InetAddress getAddress() {
            return address;
        }

        public int getPort() {
            return port;
        }
    }
}
