
import java.awt.*;
import java.io.*;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientHandler extends Thread {

    private DatagramSocket socket;
    private byte[] incoming = new byte[1024];
    private TextArea textArea;
    private String clientUsername;

    public ClientHandler(DatagramSocket socket, TextArea textArea){
        this.socket=socket;
        this.textArea=textArea;
    }

    @Override
    public void run() {
        System.out.println("Création du Thread");
        while(true){
            DatagramPacket packet = new DatagramPacket(incoming, incoming.length);
            try {
                socket.receive(packet);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
            String message = new String(packet.getData(),0, packet.getLength())+"\n";
            String current = textArea.getText();
            textArea.setText(current + message);

            }
        }
}
