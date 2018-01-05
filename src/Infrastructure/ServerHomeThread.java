package Infrastructure;


import InternetSecurity.SafeServerThread;
import LocalSecurity.Client;
import java.net.Socket;
import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stula
 */
public class ServerHomeThread extends SafeServerThread{
    
    public ServerHomeThread(Socket socket, String name, AboutServer ca) {
        super(socket, name, ca);
    }
    
    public boolean cameraConnect(Client cameraClient){
        String ipAddress = this.readEncString();
        int portNumber = this.readEncInt();
        
        cameraClient.connect(ipAddress, portNumber);
        
        String response;
        if(!cameraClient.keyExchange()){
            response = "Key exchange failed";
            System.out.println(response);
            this.writeEncString(response);
            return false;
        }
        response = "Cammera connected";
        System.out.println(response);
        this.writeEncString(response);
        return true;
    }
    
    public void cameraDisconnect(Client cameraClient){
        cameraClient.disconnect();
    }
    
    public void stream(){
        Client cameraClient = new Client();
        
        if(!this.cameraConnect(cameraClient)){
            String message = "CAMERA CONNECTION ERROR";
            System.out.println(message);
            this.writeEncString(message);
            return;
        }
        
        
        cameraClient.writeIntEnc(1); //command for stream
        
        int width = this.readEncInt();
        int height = this.readEncInt();
        int fps = this.readEncInt();
        
        String command = String.format("resolution=%dx%d&fps=%d", width, height, fps);
        cameraClient.writeBytesEnc(command.getBytes());
        cameraClient.writeIntEnc(fps);
        
        String request = this.readEncString();
        while(request.equals("continue")){
            cameraClient.writeIntEnc(1);
            byte[] image = cameraClient.readBytesEnc();
            this.writeEnc(image);
            request = this.readEncString();
        }
        
        cameraClient.writeIntEnc(0);
        this.cameraDisconnect(cameraClient);
    }
    
    @Override
    public void run() {
        if(!this.isConnected()){
            this.writeEncString("connection refused");
            return;
        }
        System.out.println("NEW CLIENT CONNECTED");

        //ADD YOUR PARTS
        String command;
        while(true){
            command = this.readEncString();
            switch(command){
                case "Test":
                    String message = "HOME to client 12";
                    System.out.println(message);
                    this.writeEncString(message);
                    System.out.println(this.readEncString());
                    System.out.println();
                    return;
                case "stream":
                    this.stream();
                    break;                
                default:
                    String resposne = "CLIENT DISSCONECTED";
                    this.writeEncString(resposne);
                    this.disconnect();
                    System.out.println(resposne);
                    return;
            }
        }
    }
}
