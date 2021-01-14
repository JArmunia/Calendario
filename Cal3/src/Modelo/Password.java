package Modelo;

/**
 * Password.java
 * ccatalan (02/2018)
 * 
 */ 


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Clase Password
 *
 */ 
public class Password {
  public static String ALGORITMO = "SHA-256";
  private byte[] hash;

  /**
   * Construye una password
   *
   */    
  public Password(String pass) throws NoSuchAlgorithmException {
    MessageDigest messageDigest = MessageDigest.getInstance(ALGORITMO);
    messageDigest.reset();
    messageDigest.update(pass.getBytes());
    hash = messageDigest.digest();  
  }

  /**
   * Sobreescribe equals 
   *
   */ 
  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(!(obj instanceof Password)) {
      return false;
    }
    Password tmp = (Password)obj;
    return Arrays.equals(hash, tmp.hash);
  }   
  
  /**
   * Devuelve hash en forma de cadena caracteres hexadecimal
   *
   */   
  public String toHexStringHash() {
    StringBuffer sb = new StringBuffer(hash.length * 2);
    for (int i = 0; i < hash.length; i++){
       int v = hash[i] & 0xff;
       if (v < 16) {
         sb.append('0');
       }
       sb.append(Integer.toHexString(v));
     }
     return sb.toString().toUpperCase();       
  }
  
  /**
   * Sobreescribe toString
   *
   */  
  @Override
  public String toString() {
    return toHexStringHash();
  }
}

