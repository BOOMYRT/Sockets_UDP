
import org.w3c.dom.Text;

import java.awt.*;
import java.io.*;
import java.net.*;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Client extends Application {

//    private Socket socket;
//    private BufferedReader bufferedReader;
//    private BufferedWriter bufferedWriter;
//    private String username;

    private static DatagramSocket socket;

    static {
        try{
            socket = new DatagramSocket(); //init to any available port
            InetSocketAddress adresse = new InetSocketAddress("localhost", 50000);
            socket.bind(adresse);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static final String identifier = "Marcel";

    private static final int SERVER_PORT = 50000;

    private static  final TextArea messageArea = new TextArea();

    private static final TextField input = new TextField();

    public static void main(String[] args) throws IOException {
        ClientHandler clientHandler = new ClientHandler(socket, messageArea);
        clientHandler.start();

        //send initialization message to the server
        byte[] initMessage = ("init : " + identifier).getBytes();
        DatagramPacket init = new DatagramPacket(initMessage, initMessage.length, socket.getInetAddress(), SERVER_PORT);
        socket.send(init);

        launch();

    }
    @Override
    public void start(Stage primaryStage) {

        messageArea.setMaxWidth(500);
        messageArea.setEditable(false);


        input.setMaxWidth(500);
        input.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String temp = identifier + ";" + input.getText(); // message to send
                messageArea.setText(messageArea.getText() + input.getText() + "\n"); // update messages on screen
                byte[] msg = temp.getBytes(); // convert to bytes
                input.setText(""); // remove text from input box

                // create a packet & send
                DatagramPacket send = new DatagramPacket(msg, msg.length, socket.getInetAddress(), SERVER_PORT);
                try {
                    socket.send(send);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // put everything on screen
        Scene scene = new Scene(new VBox(35, messageArea, input), 550, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
