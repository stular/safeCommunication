/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Infrastructure;

import LocalSecurity.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.time.Instant;

/**
 *
 * @author stula
 */
public class ServerMotionThread extends Thread {

    private DataExchange dataExchange;

    public ServerMotionThread(Socket socket) {
        this.dataExchange = new DataExchange(socket);
        System.out.println("socket connected");
    }

    public void run() {
        try {
            
            this.dataExchange.keyExchange();
            while(true) {
            byte[] dataBytes = this.dataExchange.readBytesEnc();
            
            String time = Instant.now().toString();
            time = time.substring(0,time.length()-5);
            time = time.replace("-", "").replace(":", "");
            
            if (dataBytes.length == 0) {
                break;
            }
            
            if (dataBytes.length > 0) {
                
            OutputStream os = new FileOutputStream("E:\\SaiMalmo\\ConnectedSystems\\Project\\New Folder\\" + time + ".jpeg");
            os.write(dataBytes);
            os.flush();
            os.close();
            System.out.println("Client disconnected");
            System.out.println("File Received at " + time);
            }
            }
            //this.dataExchange.disconnect();
            
        } catch (Exception e) {
        }

    }

}
