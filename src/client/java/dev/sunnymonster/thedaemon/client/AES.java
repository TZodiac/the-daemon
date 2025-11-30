package dev.sunnymonster.thedaemon.client;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

// This class abstracts away the complicated calls to decrypt our encrypted redirect IP.
public class AES {
    // The private key has to be exactly 32 bytes long. This method makes sure it is the case.
    private static String padding(String ip) {
        // If the IP is longer than 32 bytes, we truncate it.
        if (ip.length() > 32) {
            return ip.substring(0, 32);
        }

        // Otherwise we fill it with `=` characters.
        return ip + new String(new char[32 - ip.length()]).replace("\0", "=");
    }

    // I mean, it literally says decrypt here. Isn't that obvious?
    public static String decrypt(String strToDecrypt, String ip)
    {
        try {
            // This "invalid pointer!" thing just happens to be 16 bytes, which is what an IV needs to be.
            // The IV is something we use in the AES algorithm to slightly mess up the encryption.
            // I'm not exactly sure how it works yet.
            byte[] iv = "invalid pointer!".getBytes();
            IvParameterSpec ivspec
                    = new IvParameterSpec(iv);

            // We pad the IP so that it's exactly 32 bytes long.
            String paddedIP = padding(ip);

            // We make a secret key out of the padded IP.
            SecretKeySpec secretKey = new SecretKeySpec(
                    paddedIP.getBytes(), "AES");

            // The cipher does it's thing and decrypts the encrypted data, which should give us our
            // ugly IP we redirect to.
            Cipher cipher = Cipher.getInstance(
                    "AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey,
                    ivspec);

            return new String(cipher.doFinal(
                    Base64.getDecoder().decode(strToDecrypt)));
        }
        catch (Exception e) {
            System.out.println("Error while decrypting: "
                    + e);
        }
        // `null`! It makes me shiver.
        return null;
    }
}