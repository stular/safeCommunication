/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package InternetSecurity;

import Infrastructure.AboutServer;
import java.net.Socket;

/**
 *
 * @author stula
 */
 public class SafeServerThread extends Thread {
        private String name;
        private boolean connected = false;        
        private DataExchange dataExchange;

        public SafeServerThread(Socket socket, String name, AboutServer ca) {
            this.name = name;
            this.dataExchange = new DataExchange(socket, name, true);
            this.securityHandshake(ca);
        }
        
        public void securityHandshake(AboutServer ca){
            try {
                String clientName = new String(this.dataExchange.read());
                this.dataExchange.writeString(this.name);
                System.out.println("Client name: " + clientName);

                CAClient caClient = new CAClient(this.name, ca);
                byte[] keyBytes = caClient.requestPublicKey(clientName);

                if(!this.dataExchange.hasCertifikate(ca.name)){
                    System.out.println("MISSING CA CRETIFICATE ERROR");
                    return;
                }

                if(!this.dataExchange.setReciverPublicKeyBytes(keyBytes)){
                    System.out.println("CLIENT CRETIFICATE ERROR");
                    return;
                }

                if(!this.dataExchange.dhKeyExchange(true)){
                    System.out.println("KEY EXCHANGE FAILED");
                    return;
                }

                this.connected = true;
                System.out.println("Secutiry Handshake Complete\n");
            } catch (Exception e) {
                System.out.println("SecurityHandshake error");
                System.out.println(e.getMessage());
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
