package InternetSecurity;


import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stula
 */
public class Server {
    public Server(int portNum,String name){
        this.connect(portNum, name);
    }

    public void connect(int portNumber, String name) {
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                System.out.println(name + " Server is running");
                new ServerThread(serverSocket.accept(), name).start();
            }
        } catch (Exception e) {
            System.out.println("Could not listen on port " + portNumber);
            System.out.println(e.getMessage());
        }
    }

    public class ServerThread extends Thread {
        private String name;
        private DataExchange dataExchange;

        public ServerThread(Socket socket, String name) {
            this.name = name;
            this.dataExchange = new DataExchange(socket, name, false);
        }
        
        
        
        public void run() {
            System.out.println("Reading request");
            String command = this.dataExchange.readString();
            System.out.println(command);
            this.dataExchange.writeString("hello beack");
        }

    }
    public static void main(String[] args) {
        Server server = new Server(10040,"Server1");
    }
}

