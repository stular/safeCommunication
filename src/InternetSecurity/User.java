package InternetSecurity;


import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.xml.bind.DatatypeConverter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author stula
 */
public class User {
    private String name;
    private byte[] passwordDigest;
    
    private String digestAlgorithm = "SHA-512";
    
    
    public User(String name, byte[] password){
        this.name = name;
        this.passwordDigest = password;
        
    }
    
    public User(String name, String password, boolean clear){
        this.name = name;
        if(clear)
            this.passwordDigest = this.passwordDigest(password);
        else
            this.passwordDigest = this.hexStringToByte(password);
        
    }
    
    
    public byte[] getPasswordDigest(){
        return this.passwordDigest;
    }
    
    public String getPasswordDigestHexString(){
        return this.byteToHexString(this.passwordDigest);
    }
    
    public String getUserName(){
        return this.name;
    }
        
    private byte[] passwordDigest(String password){
        return this.messageDigest(password.getBytes());
    }
    
    private byte[] messageDigest(byte[] data){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(this.digestAlgorithm);
            return messageDigest.digest(data);
        } catch (Exception e) {
            System.out.println("message digest error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    private byte[] getLoginBytes(long time){
        //name + pass + time
        byte[] ret = this.combineArrays(this.name.getBytes(), this.passwordDigest);
        ret = this.combineArrays(ret, this.longToBytes(time));
        return ret;
    }
    
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    
    private byte[] combineArrays(byte[] one, byte[] two){
        byte[] combined = new byte[one.length + two.length];
        
        for (int i = 0; i < combined.length; ++i)
        {
            combined[i] = i < one.length ? one[i] : two[i - one.length];
        }
        return combined;
    }
    
    public byte[] getLoginDigest(){
        return this.messageDigest(this.getLoginBytes(System.currentTimeMillis()));
    }
    
    public boolean verLoginDigest(byte[] loginData){
        //authentication is valid +-valid time
        long validyTime = 500; // +- 500ms
        long start = System.currentTimeMillis() - validyTime;
        long end = System.currentTimeMillis() + validyTime;
        for(long i=start; i<end; i++){
            byte[] verData = this.messageDigest(this.getLoginBytes(i));
            if(Arrays.equals(loginData,verData))
                return true;
        }
        return false;
    }
    
    public String byteToHexString(byte[] data){
        return DatatypeConverter.printHexBinary(data);
    }
    
    public byte[] hexStringToByte(String hex){
        return DatatypeConverter.parseHexBinary(hex);
    }
    

    
    public static void main(String[] args) {
        User janez = new User("Janez", "Password", true); 
        
        //username and password
        System.out.println(janez.getUserName());
        System.out.println(janez.getPasswordDigestHexString());
        
        //login
        byte[] loginDigest = janez.getLoginDigest();
        System.out.println(janez.verLoginDigest(loginDigest));
        
        
        System.out.println("\nTEST 2");
        String hexPass = janez.getPasswordDigestHexString();
        String name = janez.getUserName();
        User newJanez = new User(name, hexPass, false);
        
        System.out.println(newJanez.getUserName());
        System.out.println(newJanez.getPasswordDigestHexString());
        
        //login test2
        byte[] newLoginDigest = newJanez.getLoginDigest();
        System.out.println(janez.verLoginDigest(newLoginDigest));
        
        System.out.println(janez.getUserName() + ";" + janez.getPasswordDigestHexString());
        
        
        
       
    }
    
}
