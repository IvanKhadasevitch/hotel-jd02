package services.auth.impl;

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoder {
    public static String encode(String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(pwd.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            return encoder.encode(digest);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

//    public static void main(String[] args) {
//        System.out.printf("password=[%s] equals=[%s]\n", "1", Encoder.encode("1"));
//        System.out.printf("password=[%s] equals=[%s]\n", "2", Encoder.encode("2"));
//        System.out.printf("password=[%s] equals=[%s]\n", "3", Encoder.encode("3"));
//        System.out.printf("password=[%s] equals=[%s]\n", "cool admin", Encoder.encode("cool admin"));
//    }
}
