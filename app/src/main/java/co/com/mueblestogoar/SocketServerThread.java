package co.com.mueblestogoar;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by ojmalagon on 11/04/2017.
 */

public class SocketServerThread extends Thread {

    static final int SOCKET_SERVER_PORT = 43770;
    private ViewAR viewAR;
    private String jsonInput;

    public SocketServerThread(ViewAR callingActivity) {
        viewAR = callingActivity;
    }
    private void getJSON() {
        jsonInput = viewAR.getJSON();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(SOCKET_SERVER_PORT);

            while(true) {
                getJSON();
                Socket socket = serverSocket.accept();
                SocketServerReplyThread socketServerReplyThread = new SocketServerReplyThread(socket, jsonInput);
                socketServerReplyThread.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
