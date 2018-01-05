/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LocalSecurity;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 *
 * @author axis
 */
public class Cryptography {
    private int sharedSecret;
    
    public Cryptography(){}
    
    int getPrime(int min, int max){
	int prime = 0;
	boolean notPrime = true;
        Random rnd = new Random();
	while(notPrime){
		prime = min + rnd.nextInt(max-min);
		notPrime = false;
		int trashold = (int)Math.sqrt((double) prime) + 1; // +1 only for safety
		//printf("Number %d, trashold %d\n",prime,trashold);
		int i;
		for(i=2; i<trashold; i++){
			if(prime%i==0){
				notPrime=true;
				break;
			}
		}
	}
	return prime;
    }

    int sqrAndMul(int x, int y, int n){
	if(y == 0)
		return  1;
	if(y == 1)
		return  x % n;
	
	if(y%2 == 0)
		return sqrAndMul( (x*x)%n,  y/2, n) %n;
	else
		return (x * (sqrAndMul( (x*x)%n, (y-1)/2, n)%n) ) % n;
    }
    
    byte[] encryptXOR(byte[] clear){
	int len = clear.length*2;
	byte[] cipher = new byte[len];
        byte[] t1 =  ByteBuffer.allocate(4).putInt(this.sharedSecret).array();
	byte kPrev = ByteBuffer.allocate(4).putInt(this.sharedSecret).array()[3];
        byte k;
	int i;
        Random rnd = new Random();
	for(i=0; i < len; i=i+2){
		k = ByteBuffer.allocate(4).putInt(rnd.nextInt(256)).array()[3];
		cipher[i]=(byte) (kPrev ^ k);
		cipher[i+1]=(byte) (clear[i/2] ^ k);
		kPrev = k;		
	}
	return cipher;	
    }

    byte[] decryptXOR(byte[] cipher){
        int len = cipher.length;
	byte[] clear = new byte[len/2];
	byte kPrev = ByteBuffer.allocate(4).putInt(this.sharedSecret).array()[3];
        byte k;
	int i;
	for(i=0; i < len; i=i+2){
		k = (byte) (kPrev ^ cipher[i]);
		clear[i/2] = (byte) (cipher[i+1] ^ k);
		kPrev = k;		
	}
	return clear;		
    }
    
    public void setSharedSecret(int sharedSecret){
        this.sharedSecret = sharedSecret;
    }
}
