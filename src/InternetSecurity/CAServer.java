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
public class CAServer {
    public CAServer(int portNum,String name){
        this.connect(portNum, name);
    }

    public void connect(int portNumber, String name) {
        boolean listening = true;

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (listening) {
                System.out.println(name + " Server is running");
                new CAThread(serverSocket.accept(), name).start();
            }
        } catch (Exception e) {
            System.out.println("Could not listen on port " + portNumber);
            System.out.println(e.getMessage());
        }
    }

    public class CAThread extends Thread {
        private String name;
        private DataExchange dataExchange;
        private Cryptgraphy cryptgraphy;

        public CAThread(Socket socket, String name) {
            this.name = name;
            this.cryptgraphy = new Cryptgraphy(name, true);
            if(!this.cryptgraphy.loadMyCertificates()){
                this.cryptgraphy.generateAsymKeyPair(this.name);
                this.cryptgraphy.loadMyCertificates();
            }
            this.dataExchange = new DataExchange(socket, name, false);
        }
        
        private void publicKeyRequest(){
            String reqName = this.dataExchange.readString();
            System.out.println(reqName + " key request");
            byte[] reqKey = this.cryptgraphy.getPublicKeyBytes(reqName);
            byte[] reqKeySign = this.cryptgraphy.sign(reqKey);
            this.dataExchange.write(reqKey);
            this.dataExchange.write(reqKeySign);
            System.out.println(reqName + " key sent\n");
        }
        
        private void privateKeyRevoke(){
            String name = this.dataExchange.readString();
            
            if(!this.cryptgraphy.publicKeyExists(name)){
                this.dataExchange.writeString("unknown user");
                return;
            }
            
            this.cryptgraphy.loadReciverPublicKey(name);
            byte[] challange = this.cryptgraphy.getRandomBytes(128);
            
            this.dataExchange.write(challange);
            byte[] challangeSign = this.dataExchange.read();
            
            if(this.cryptgraphy.verSign(challange, challangeSign)){
                File pKey = new File(this.cryptgraphy.getPublicKeyPath(name));
                pKey.delete();
                File sKey = new File(this.cryptgraphy.getPrivateKeyPath(name));
                if(sKey.exists())
                    sKey.delete();
                this.dataExchange.writeString("key revoked");
            }else
                this.dataExchange.writeString("authorization failed");
            
        }
        
        public void generateKeyPair(){
            String reqName = this.dataExchange.readString();
            System.out.println(reqName + " keyPair request");
            
            File pKey = new File(this.cryptgraphy.getPublicKeyPath(reqName));    
            if(pKey.exists()){
                this.dataExchange.writeString("keyPair already exists");
                return;
            }
            this.cryptgraphy.generateAsymKeyPair(reqName);
            this.dataExchange.writeString("keyPair generated");
        }
        
        public void run() {
            System.out.println("Reading request");
            String command = this.dataExchange.readString();
            
            switch(command){
                case "get public key":
                    this.publicKeyRequest();
                    break;
                    
                case "revoke key":
                    this.privateKeyRevoke();
                    break;
                    
                case "generate keyPair":
                    this.generateKeyPair();
                    break;
            
                    
                default:
                    this.dataExchange.writeString("undefined request");
                
            }
        }

    }
}

