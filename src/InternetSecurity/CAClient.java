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
public class CAClient {
    private String name;
    private String serverName;
    private boolean connected = false;
    private DataExchange dataExchange;
    private AboutServer server;
    private Cryptgraphy crypto;

    public CAClient(String name, AboutServer server) {
        this.name = name;
        this.serverName = server.name;
        this.server = server;
        this.crypto = new Cryptgraphy(this.name, false);
    }
    
    private void connect()
    {
        if(!this.connected){
            try{
                this.dataExchange = new DataExchange(new Socket(server.ip, server.portNumber), this.name, false);
                System.out.println("Client connected");
            }
            catch(Exception e){
                System.out.println("CA CONNECTION ERROR");
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
    
    public byte[] requestPublicKey(String name){
        this.connect();
        this.certified();
        
        this.dataExchange.writeString("get public key");
        this.dataExchange.writeString(name);
        byte[] keyBytes = this.dataExchange.read();
        byte[] keyBytesSign = this.dataExchange.read();
        
        crypto.loadReciverPublicKey(this.serverName);
        
        if(crypto.verSign(keyBytes, keyBytesSign)){
            this.disconnect();
            return keyBytes;
        }
        
        if(keyBytes == null){
                System.out.println("CA key delivery error");
                this.disconnect();
                System.exit(-1);            
        }
        
        this.disconnect();
        return null;
    }
    
    public String revokeKey(){
        this.connect();
        this.certified();
        
        this.dataExchange.writeString("revoke key");
        this.dataExchange.writeString(this.name);
        byte[] challange = this.dataExchange.read();
        
        if(new String(challange).equals("unknown user")){
            System.out.println("UNKNOWN USER ERROR");
            this.disconnect();
            System.exit(-1);
        }
        
        byte[] challangeSign = this.crypto.sign(challange);
        this.dataExchange.write(challangeSign);
        this.disconnect();
        return this.dataExchange.readString();
    }
    
    public String requestKeyPair(){
        this.connect();
        this.dataExchange.writeString("generate keyPair");
        this.dataExchange.writeString(this.name);
        this.disconnect();
        return this.dataExchange.readString();
    }
    
    public void certified(){
        if(!this.crypto.loadMyCertificates()){
            System.out.println("NON CERTIFIED USER ERROR");
            this.disconnect();
            System.exit(-1);            
        }
    }
    
    public static void main(String[] args) {
        AboutServer caSrv = new AboutServer("CA1", "127.0.0.1", 10040);
        CAClient client = new CAClient("ClientJanez",caSrv);
//        byte[] key = client.requestPublicKey("SafeServer");
//        for (int i = 0; i < key.length; i++)
//            System.out.print(key[i]);
//        System.out.println("");
        
        System.out.println(client.requestKeyPair());
//        System.out.println(client.revokeKey());
        
        
    }
    
}

