package Infrastructure;


import InternetSecurity.SafeClient;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stula
 */
public class ClientJanez {
    public static void main(String[] args) {
        
        AboutServer ca = new AboutServer("CA1","127.0.0.1", 10040);
        AboutServer home = new AboutServer(null,"127.0.0.1", 10041);
        AboutServer police = new AboutServer(null,"127.0.0.1", 10042);
        
        System.out.println("HOME SERVER CONNECTION: ");
        SafeClient homeClient = new SafeClient("ClientJanez",home,ca);
        if(homeClient.isConnected()){
            String message = "Test";
            System.out.println(message);
            homeClient.writeEnc(message.getBytes());
            
            System.out.println(new String(homeClient.readEnc()));
            message = "JANEZ to server 34";
            System.out.println(message);
            homeClient.writeEnc(message.getBytes());
            
            homeClient.writeEnc("EXIT".getBytes());   
        }
        homeClient.disconnect();
        
        
        System.out.println("\nPOLICE SERVER CONNECTION: ");
        SafeClient policeClient = new SafeClient("ClientJanez",police,ca);
        if(policeClient.isConnected()){
            String message = "Test";
            System.out.println(message);
            policeClient.writeEnc(message.getBytes());
            
            System.out.println(new String(policeClient.readEnc()));
            message = "JANEZ to server 34";
            System.out.println(message);
            policeClient.writeEnc(message.getBytes());
            
            policeClient.writeEnc("EXIT".getBytes());   
        }
        policeClient.disconnect();
        
        System.out.println("\nEND OF CONNECTIONS");
    }
}
