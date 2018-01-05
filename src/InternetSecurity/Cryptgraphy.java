package InternetSecurity;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.crypto.*;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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
public class Cryptgraphy {
    private String name;
    private String mainPath;
    private String privateKeyExt = ".sk";
    private String publicKeyExt = ".pk";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String asymmetricAlgorithem = "RSA";
    private String asymmetricKeyAlgorithem = "RSA";
    private String signitureAlgorithem = "SHA512withRSA";
    private int asymmetricKeyLen = 2048;
    private PublicKey reciverPublicKey;
    
    private String symmetricAlgorithem = "AES/CBC/PKCS5Padding";
    private String symmetricKeyAlgorithem = "AES";
    private int symmetricKeyLen = 128; //withouth extensoin 128
    private byte[] symmetricKey;
    private byte[] symmetricIV;
    private Cipher symmetricEnc;
    private Cipher symmetricDec;
    
    public Cryptgraphy(String name, boolean certified){
        try {
            this.mainPath = new java.io.File( "." ).getCanonicalPath() + "\\" + name + "\\";
            Path path = Paths.get(this.mainPath);
            if (!Files.exists(path))
                Files.createDirectories(path);
        } catch (Exception e) {
            System.out.println("CertificatePath error");
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        this.name = name;
        
        if(certified && !this.loadMyCertificates()){
            System.out.println("Certificate missing");
            System.exit(-1);
    }
            }

    public boolean loadMyCertificates(){
        File fPrivat = new File(this.getMyPrivateKeyPath());
        File fPublic = new File(this.getMyPublicKeyPath());
        
        if(fPrivat.exists() && fPublic.exists()) {
            this.privateKey = (PrivateKey) FileManagment.readObject(this.getMyPrivateKeyPath());
            this.publicKey = (PublicKey) FileManagment.readObject(this.getMyPublicKeyPath());
            return true;
        }
        return false;
    }
    
    public String getMainFolderPath(){
        return this.mainPath;
    }
    
    public byte[] getRandomBytes(int len){
        SecureRandom sRnd = new SecureRandom();
        byte bytes[] = new byte[len];
        sRnd.nextBytes(bytes);
        return bytes;
    }
    
    public boolean publicKeyExists(String name){
        File keyPath = new File(this.getPublicKeyPath(name));
        return keyPath.exists();
    }
    
    public String getMyPublicKeyPath(){
        return this.getPublicKeyPath(this.name);
    }
    public String getMyPrivateKeyPath(){
        return this.getPrivateKeyPath(this.name);
    }
    
    public String getPublicKeyPath(String name){
        return this.mainPath + name + this.publicKeyExt;
    }
    
    public String getPrivateKeyPath(String name){
        return this.mainPath + name + this.privateKeyExt;
    }
    
    public void generateAsymKeyPair(String name){
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(this.asymmetricKeyAlgorithem);
            keyPairGen.initialize(this.asymmetricKeyLen);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            PrivateKey sKey = keyPair.getPrivate();
            PublicKey pKey = keyPair.getPublic();
            
            FileManagment.writeObject(this.getPrivateKeyPath(name), sKey);
            FileManagment.writeObject(this.getPublicKeyPath(name), pKey);
        } catch (Exception e) {
            System.out.println("KeyPairGeneration error");
            System.out.println(e.getMessage());
        }
    }
    
    private SecretKeySpec generateSymKey(byte[] keyBytes){
        try {
            return new SecretKeySpec(keyBytes, 0, this.symmetricKeyLen / Byte.SIZE , this.symmetricKeyAlgorithem);
        } catch (Exception e) {
            System.out.println("KeyGeneration error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public byte[] getPublicKeyBytes(String name){
        return ((PublicKey)FileManagment.readObject(this.getPublicKeyPath(name))).getEncoded();
    }
    
    public PublicKey getMyPublicKey(){
        return this.publicKey;
    }
    public PrivateKey getMyPrivateKey(){
        return this.privateKey;
    }
    
    
    public boolean loadReciverPublicKey(String name){
        this.reciverPublicKey = (PublicKey)FileManagment.readObject(this.getPublicKeyPath(name));
        return this.reciverPublicKey != null;
    }
    
    public boolean setReciverPublicKeyBytes(byte[] keyBytes){
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(this.asymmetricKeyAlgorithem);
            this.reciverPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
            return this.reciverPublicKey != null;
        } catch (Exception e) {
            System.out.println("ReciverPublicKeyBytesLoading error");
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public byte[] sign(byte[] data){
        try {
            Signature signing = Signature.getInstance(this.signitureAlgorithem);
            signing.initSign(this.privateKey);
            signing.update(data);
            return signing.sign();
        } catch (Exception e) {
            System.out.println("Signing error");
            System.out.println(e.getMessage());
            return null;
        }
        
    }
    
    public boolean verSign(byte[] data, byte[] sign){
        try {
            Signature signing = Signature.getInstance(this.signitureAlgorithem);
            signing.initVerify(this.reciverPublicKey);

            signing.update(data);
            if (signing.verify(sign))
                return true;     
        } catch (Exception e) {
            System.out.println("SignitureVerification error");
            System.out.println(e.getMessage());
        }
        return false;
    }
    
    
    public byte[] encAsym(byte[] data){
        try {
            Cipher encion = Cipher.getInstance(this.asymmetricAlgorithem);
            encion.init(Cipher.ENCRYPT_MODE, this.reciverPublicKey);
            return encion.doFinal(data);
        } catch (Exception e) {
            System.out.println("AsymEnc error");
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public byte[] decAsym(byte[] data){
        try {
            Cipher decion = Cipher.getInstance(this.asymmetricAlgorithem);
            decion.init(Cipher.DECRYPT_MODE, this.privateKey);
            return decion.doFinal(data);
        } catch (Exception e) {
            System.out.println("AsymDec error");
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public byte[] initSymEnc(byte[] sharedSecret, byte[] iv){
        try {
            SecretKeySpec secretKey = this.generateSymKey(sharedSecret);
            this.symmetricEnc = Cipher.getInstance(this.symmetricAlgorithem);
            
            if(iv == null)
               this.symmetricEnc.init(Cipher.ENCRYPT_MODE, secretKey);
            else
               this.symmetricEnc.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            
            this.symmetricKey = secretKey.getEncoded();
            this.symmetricIV = this.symmetricEnc.getIV();
            return this.symmetricIV;
            
        } catch (Exception e) {
            System.out.println("SymEncInitalization error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    public byte[] getSymEncKey(){
        return this.symmetricKey;
    }
    public byte[] getSymEncIV(){
        return this.symmetricIV;
    }

    public void initSymDec(byte[] sharedSecret, byte[] iv){
        try {
            SecretKeySpec secretKey = generateSymKey(sharedSecret);
            this.symmetricDec = Cipher.getInstance(this.symmetricAlgorithem);            
            this.symmetricDec.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            this.symmetricIV = this.symmetricDec.getIV();
        } catch (Exception e) {
            System.out.println("SymEncInitalization error");
            System.out.println(e.getMessage());
        }
    }
    
    public byte[] encSym(byte[] data){
        try {
            return this.symmetricEnc.doFinal(data);
        } catch (Exception e) {
            System.out.println("SymEnc error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    public byte[] decSym(byte[] data){
        try {
            return this.symmetricDec.doFinal(data);
        } catch (Exception e) {
            System.out.println("SymDec error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    private DHPublicKey dhDecodePublicKey(byte[] keyBytes){
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            return (DHPublicKey) KeyFactory.getInstance("DH").generatePublic(keySpec);
        } catch (Exception e) {
            System.out.println("dhPublicKeyDecoding error");
            System.out.println(e.getMessage());
        }
        return null;
    }

    
    public KeyPair dhKeyGeneration(byte[] reciverKey){
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            
            if(reciverKey != null)
                kpg.initialize(dhDecodePublicKey(reciverKey).getParams());
            else
                kpg.initialize(2048);
            
            return kpg.generateKeyPair();
        } catch (Exception e) {
            System.out.println("dhKeyGeneration error");
            System.out.println(e.getMessage());
        }
        return null;
    }
    
    
    public byte[] dhSecretGeneration(KeyPair myKey, byte[] reciverKey){
        try {
            KeyAgreement dh = KeyAgreement.getInstance("DH");
            dh.init(myKey.getPrivate());
            dh.doPhase(dhDecodePublicKey(reciverKey), true);
            return dh.generateSecret();
        } catch (Exception e) {
            System.out.println("dhSecretGeneration error");
            System.out.println(e.getMessage());
        }
        return null;
    }

    
}
