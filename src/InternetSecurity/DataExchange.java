package InternetSecurity;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import Processing.FileManagment;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author stula
 */
public class DataExchange {
    private Socket socket = null;
    private OutputStream out;
    private InputStream in;
    private Cryptgraphy cryptography;
    
    public DataExchange(Socket socket, String name, boolean certified){
        this.setSocket(socket);
        this.cryptography = new Cryptgraphy(name, certified);
    }
    
    private void setSocket(Socket socket){
        try {
            this.socket = socket;
            this.out = socket.getOutputStream();
            this.in = socket.getInputStream();
        } catch (Exception e) {
            System.out.println("Communication establihment error");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public void disconnect() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Dissconecting error");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }
    
    public boolean hasCertifikate(String name){
        return this.cryptography.publicKeyExists(name);
    }

    public int buffToInt(byte[] buffer) {
        return ByteBuffer.wrap(buffer).getInt();
    }

    public byte[] intToBuff(int n) {
        return ByteBuffer.allocate(4).putInt(n).array();
    }
    
    public void writeString(String data){
        this.write(data.getBytes());
    }

    public void write(byte[] data) {
        try {

            out.write(intToBuff(data.length));
            out.write(data);
        } catch (Exception e) {
            System.out.println("Write data error");
            System.out.println(e.getMessage());
        }

    }
    
    public String readString(){
        byte[] data = this.read();
        if(data != null)
            return new String(data);
        return null;
    }
    
    public byte[] read() {
        try {
            //get data length
            byte[] lenData = readBytes(4);
            if (lenData == null) {
                return null;
            }
            int len = buffToInt(lenData);

            //get data
            byte[] dataBytes = readBytes(len);
            return dataBytes;
        } catch (Exception e) {
            System.out.println("Read data error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    public boolean loadReciverPublicKey(String name){
        return this.cryptography.loadReciverPublicKey(name);
    }
    
    public boolean setReciverPublicKeyBytes(byte[] keyData){
        if(keyData == null)
            return false;
        return this.cryptography.setReciverPublicKeyBytes(keyData);
    }
    
    public boolean dhKeyExchange(boolean first){
        KeyPair keyPair;
        byte[] recKeyBytes;
        
        if(first){
            keyPair = this.cryptography.dhKeyGeneration(null);
            this.writeSigned(keyPair.getPublic().getEncoded());
            recKeyBytes = this.readAndVer();
        }else{
            recKeyBytes = this.readAndVer();
            keyPair = this.cryptography.dhKeyGeneration(recKeyBytes);
            this.writeSigned(keyPair.getPublic().getEncoded());
        }
        byte[] sharedSecret = this.cryptography.dhSecretGeneration(keyPair, recKeyBytes);
        //System.out.println("SECRET:" + javax.xml.bind.DatatypeConverter.printHexBinary(sharedSecret));
        
        if(sharedSecret == null)
            return false;
        
        byte[] iv;
        
        if(first){
            iv = this.cryptography.initSymEnc(sharedSecret, null);
            this.write(iv);
        }else{
            iv = this.read();
            this.cryptography.initSymEnc(sharedSecret, iv);
        }
        this.cryptography.initSymDec(sharedSecret, iv);
        return true;
    }
    
    public void writeSigned(byte[] data){
        this.write(data);
        this.write(this.cryptography.sign(data));
    }
    
    public byte[] readAndVer(){
        byte[] data = this.read();
        byte[] sign = this.read();
        if(this.cryptography.verSign(data, sign))
            return data;
        return null;
    }
    
    

    
    public void writeEnc(byte[] data) {
        try {
            byte[] cipherText = cryptography.encSym(data);
            byte[] cipherLen = cryptography.encSym(intToBuff(cipherText.length)); 
            out.write(cipherLen);
            out.write(cipherText);
        } catch (Exception e) {
            System.out.println("Write data error");
            System.out.println(e.getMessage());
        }

    }
    public String readEncString(){
        byte[] data = this.readEnc();
        if(data != null)
            return new String(data);
        return null;
    }
    
    public int readEncInt(){
        return this.buffToInt(this.readEnc());
    }

    public byte[] readEnc() {
        try {
            //get data length
            byte[] lenData = readBytes(16); //AES block size
            if (lenData == null) {
                return null;
            }
            lenData = cryptography.decSym(lenData);
            int len = buffToInt(lenData);

            //get data
            byte[] dataBytes = readBytes(len);
            return cryptography.decSym(dataBytes);
        } catch (Exception e) {
            System.out.println("Read data error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public void writeEncString(String data){
        this.writeEnc(data.getBytes());
    }
    
    public void writeEncInt(int n){
        this.writeEnc(this.intToBuff(n));
    }
    
    private byte[] readBytes(int len) {
        try {
            byte[] dataBytes = new byte[len];
            int readed = 0;
            while (readed < dataBytes.length) {
                readed += in.read(dataBytes, readed, dataBytes.length - readed); //return number of readed bytes
                //System.out.println("Readed: " + readed);
            }
            return dataBytes;
        } catch (Exception e) {
            System.out.println("ReadBytes data error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public String getMainFolderPath(){
        return this.cryptography.getMainFolderPath();
    }
}

