package InternetSecurity;


import Infrastructure.AboutServer;
import java.net.*;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stula
 */
public class Client {
    private boolean connected = false;
    private DataExchange dataExchange;
    
    public Client(String name, String serverName, int portNumber) {
        this.connect(name, serverName,portNumber);
    }
    
    private void connect(String name, String serverName, int portNumber)
    {
        if(!this.connected){
            try{
                this.dataExchange = new DataExchange(new Socket(serverName, portNumber), name, false);
                System.out.println("Client connected");
            }
            catch(Exception e){
                System.out.println("CONNECTION ERROR");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
    public void disconnect(){
        if(this.connected){
            this.dataExchange.disconnect();
        }
    }
    
    
    public static void main(String[] args) {
        Client client = new Client("Client","127.0.0.1", 10040);
        client.dataExchange.writeString("hello");
        System.out.println(client.dataExchange.readString());

        
        
    }
    
}

