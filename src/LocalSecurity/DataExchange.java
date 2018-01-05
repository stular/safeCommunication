/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocalSecurity;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author stula
 */
public class DataExchange {
    Socket socket;
    Cryptography crypto = new Cryptography();
    
    public DataExchange(Socket socket){
        this.socket = socket;    
    }
    
    public void disconnect(){
        try{
            this.socket.close();
            System.out.println("socket closed");
        }
        catch (Exception e)
        {
                System.out.println("Cant disconect, don't know about host");
                System.out.println(e.getMessage());
                System.exit(1);
        }
    }

    public void writeBytes(byte[] dataBytes){
        try {
            OutputStream outputStream = socket.getOutputStream();
            int dataLen = dataBytes.length;
            //send len
            outputStream.write(intToBuff(dataLen));
            System.out.println("sending " + dataLen + " bytes len");
            //send data
            if(dataLen > 0)
                outputStream.write(dataBytes);
            System.out.println("sending data");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        
    }
    
    public int readInt(){
        return buffToInt(readBytes());
    }
    public void writeInt(int n){
        writeBytes(intToBuff(n));
    }
    
    public int readIntEnc(){
        return buffToInt(readBytesEnc());
    }
    public void writeIntEnc(int n){
        writeBytesEnc(intToBuff(n));
    }
    
    public byte[] readBytes(){
         try {
             byte[] lenBytes = new byte[4];
            InputStream inputStream = socket.getInputStream();
            //recive len
            inputStream.read(lenBytes);
            int len=buffToInt(lenBytes);

            System.out.println("reciving " + len + " bytes len");
            byte[] dataBytes = new byte[len];
            //recive data

            if(len > 0){
                int readed = 0;
                while(readed < len){
                    readed += inputStream.read(dataBytes,readed,len-readed); //return number of readed bytes
                    System.out.println("Readed: " + readed);
                }
            }
            System.out.println("data recived");
            return dataBytes;
             
         } catch (Exception e) {
             System.out.println(e.getMessage());
         }
         return null;
    }
    
    public int buffToInt(byte[] buffer)
    {   
            return ByteBuffer.wrap(buffer).getInt();
    }

    public byte[] intToBuff(int n) {
            return  ByteBuffer.allocate(4).putInt(n).array();
    }
    
    public boolean keyExchange(){
        int g = this.readInt();
        int p = this.readInt();
        int SKey = this.readInt();
        
        int cKey = this.crypto.getPrime(3, p-1);
        int CKey = this.crypto.sqrAndMul(g, cKey, p);

	this.writeInt(CKey);
        int sharedSecret = this.crypto.sqrAndMul(SKey, cKey, p);
        System.out.printf("DH DATA: g %d, p %d, cKey %d, CKey %d, SKey %d, shareSec %d\n", g, p, cKey, CKey, SKey, sharedSecret);
        this.crypto.setSharedSecret(sharedSecret);
        
        this.writeIntEnc(0);
        return this.readIntEnc() == 0;
    }
    
    public void writeBytesEnc(byte[] dataBytes){
        writeBytes(crypto.encryptXOR(dataBytes));
    }
    public byte[] readBytesEnc(){
        return crypto.decryptXOR(readBytes());
    }
}
