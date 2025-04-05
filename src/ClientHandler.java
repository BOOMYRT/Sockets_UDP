import java.net.*;

public class ClientHandler implements Runnable {
    private DatagramSocket socket;
    private String username;
    private Serveur serveur;
    private InetAddress clientAddress;
    private int clientPort;

    public ClientHandler(DatagramSocket socket, String username, Serveur serveur, InetAddress clientAddress,
            int clientPort) {
        try {
            this.socket = socket;
            this.username = username;
            this.serveur = serveur;
            this.clientAddress = clientAddress;
            this.clientPort = clientPort;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public DatagramSocket getSocket() {
        return this.socket;
    }

    public String getUsername() {
        return this.username;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Mémoriser l’adresse et le port du client
                if (clientAddress == null) {
                    clientAddress = packet.getAddress();
                    clientPort = packet.getPort();
                }

                String message = new String(packet.getData(), 0, packet.getLength());

                if (message.startsWith("all")) { // Message public
                    String[] parts = message.split(":", 2);
                    if (parts.length > 1) {
                        serveur.broadcast(username, parts[1]);
                    }

                } else if (message.startsWith("exit")) {
                    System.out.println(username + " s'est déconnecté(e).");
                    serveur.removeClient(username);
                    socket.close(); // libère le port dédié
                    break;
                } else { // Message privé
                    String[] parts = message.split(":", 2);
                    if (parts.length > 1) {
                        String recipient = parts[0]; // Récupérer le pseudo
                        serveur.sendPrivateMessage(username, recipient, parts[1]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            if (clientAddress != null) {
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
