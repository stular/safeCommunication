/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocalSecurity;

import java.io.*;
import java.io.InputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Random;
import sun.misc.IOUtils;

/**
 *
 * @author axis
 */
public class Client{
    private DataExchange dataExchange;

    
    public void connect(String ipAddress, int portNumber){
        try{
            this.dataExchange = new DataExchange(new Socket());
            this.dataExchange.socket.connect(new InetSocketAddress(ipAddress,portNumber));
            System.out.println("socket connected");
        }
        catch (Exception e)
        {
                System.out.println("don't know about host:" +ipAddress);
                System.out.println(e.getMessage());
                System.exit(1);
        }
        
    }
    public void disconnect(){
        this.dataExchange.disconnect();
    }

    //passing functions
    public void writeIntEnc(int n) {
        this.dataExchange.writeIntEnc(n);
    }

    public void readIntEnc(int n) {
        this.dataExchange.readIntEnc();
    }
    
    public byte[] readBytesEnc() {
        return this.dataExchange.readBytesEnc();
    }

    public void writeBytesEnc(byte[] bytes) {
        this.dataExchange.writeBytesEnc(bytes);
    }

    public boolean keyExchange() {
        return this.dataExchange.keyExchange();
    }
    
    public static void main(String[] args) {
        Client client = new Client();
        client.connect("127.0.0.1", 10042);
        
        int command = 1;
        System.out.println("Writing: " + command);
        client.dataExchange.writeInt(command);
        
        command = client.dataExchange.readInt();
        System.out.println("Reading: " + command);
        
    }
    
}
