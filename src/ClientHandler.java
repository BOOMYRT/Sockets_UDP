import java.net.*;

public class ClientHandler implements Runnable {
    private DatagramSocket socket;

    public ClientHandler(DatagramSocket socket) {
        this.socket = socket;
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
}
