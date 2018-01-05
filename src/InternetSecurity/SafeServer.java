package InternetSecurity;

import Infrastructure.AboutServer;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stula
 */
public class SafeServer {
    public SafeServer(AboutServer server, AboutServer ca){
        this.connect(server, ca);
    }

    public void connect(AboutServer server, AboutServer ca) {
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(server.portNumber)) {
            while (listening) {
                System.out.println(server.name + " server is running");
                new SafeServerThread(serverSocket.accept(), server.name, ca).start();
            }
        } catch (Exception e) {
            System.out.println("Could not listen on port " + server.portNumber);
            System.out.println(e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        AboutServer server = new AboutServer("SafeServer",null, 10042);
        AboutServer ca = new AboutServer("CA1","127.0.0.1", 10040);
        SafeServer safeServer = new SafeServer(server, ca);
    }

}
