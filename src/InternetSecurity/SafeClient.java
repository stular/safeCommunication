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
public class SafeClient {
    private String name;
    private boolean connected = false;
    private DataExchange dataExchange;

    public SafeClient(String name, AboutServer server, AboutServer ca) {
        this.name = name;
        this.connect(name, server,ca);
    }
    
    public void connect(String name, AboutServer server, AboutServer ca)
    {
        if(!this.connected){
            try{
                this.dataExchange = new DataExchange(new Socket(server.ip, server.portNumber), name, true);
                this.securityHandshake(ca);
                System.out.println("Client connected");
            }
            catch(Exception e){
                System.out.println("Connecting error");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
    
    public void securityHandshake(AboutServer ca){
        try {
            
            this.dataExchange.writeString(this.name);
            String serverName = this.dataExchange.readString();
            System.out.println("ServerName: " + serverName);
            
            if(!this.dataExchange.hasCertifikate(ca.name)){
                System.out.println("MISSING CA CRETIFICATE ERROR");
                System.exit(-1);
            }
            
            CAClient caClient = new CAClient(this.name, ca);
            byte[] keyBytes = caClient.requestPublicKey(serverName);
            

            if(!this.dataExchange.setReciverPublicKeyBytes(keyBytes)){
                System.out.println("SERVER CRETIFICATE ERROR");
                System.exit(-1);            
            }
            
            if(!this.dataExchange.dhKeyExchange(false)){
                System.out.println("KEY EXCHANGE FAILED");
                System.exit(-1);
            }
            
            this.connected = true;
            System.out.println("Secutiry Handshake Complete\n");
        } catch (Exception e) {
            System.out.println("SecurityHandshake error");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    
    public boolean isConnected(){
        return this.connected;
    }
    
    //PASSING FUNCTIONS
    public void disconnect() {
        this.dataExchange.disconnect();
    }

    public void writeEnc(byte[] bytes) {
        this.dataExchange.writeEnc(bytes);
    }
    
    public byte[] readEnc() {
        return this.dataExchange.readEnc();
    }
    public void writeEncString(String str) {
        this.dataExchange.writeEncString(str);
    }

    public void writeEncInt(int n) {
        this.dataExchange.writeEncInt(n);
    }
    
    public int readEncInt() {
        return this.dataExchange.readEncInt();
    }

    public String readEncString() {
        return this.dataExchange.readEncString();
    }
    
}

