package Infrastructure;


import InternetSecurity.SafeServerThread;
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
public class ServerPoliceThread  extends SafeServerThread{
    public ServerPoliceThread(Socket socket, String name, AboutServer ca) {
        super(socket, name, ca);
    }
    
    @Override
    public void run() {
        if(!this.isConnected()){
            this.writeEncString("connection refused");
            return;
        }

        //ADD YOUR PARTS
        String command;
        while(true){
            command = this.readEncString();
            switch(command){
                case "Test":
                    String message = "POLICE to client 12";
                    System.out.println(message);
                    this.writeEncString(message);
                    System.out.println(this.readEncString());
                    System.out.println();
                default:
                    this.disconnect();
                    System.out.println("Client disconnected");
                    return;
            }
        }
    }
    
}
