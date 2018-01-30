package com.around.technician;

import android.util.Base64;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

public class JwtBuilder {

    static final String HEXES = "0123456789abcdef";
    static JwtClaims claims = new JwtClaims();

    public static String generateJWTToken(String requestUrl, String canonicalUrl, String key, String sharedSecret)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {


        claims.setIss(key);
        claims.setIat(System.currentTimeMillis() / 1000L);
        claims.setExp(claims.getIat() + 180L);

        claims.setQsh(canonicalUrl);
        String jwtToken = sign(claims, sharedSecret);
        return jwtToken;
    }

    private static String sign(JwtClaims claims, String sharedSecret) throws InvalidKeyException, NoSuchAlgorithmException {

        String signingInput = getSigningInput(claims, sharedSecret);
        String signed256 = signHmac256(signingInput, sharedSecret);
        return signingInput + "." + signed256;
    }

    private static String getSigningInput(JwtClaims claims, String sharedSecret) throws InvalidKeyException, NoSuchAlgorithmException {

        JwtHeader header = new JwtHeader();
        header.alg = "sha256";
        header.typ = "JWT";

        Gson gson = new Gson();
        String headerJsonString = gson.toJson(header);
        String claimsJsonString = gson.toJson(claims);
        String signingInput = new String(encodeBase64(headerJsonString.getBytes())) + "." + new String(encodeBase64(claimsJsonString.getBytes()));
        return signingInput;
    }

    public static String signHmac256(String signingInput, String sharedSecret) throws NoSuchAlgorithmException, InvalidKeyException {

        String hash = null;
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(sharedSecret.getBytes("UTF-8"), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] res = sha256_HMAC.doFinal(signingInput.getBytes("UTF-8"));
            hash = getHex(res);
            hash = Base64.encodeToString(hash.getBytes("UTF-8"), Base64.NO_WRAP);
        } catch (Exception e) {

        }
        return hash;
    }

    public static String signMd5(String password) throws Exception {

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        //System.out.println("Digest(in hex format):: " + sb.toString());

        //convert the byte to hex format method 2
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static String getHex(byte[] raw) {

        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4))
                    .append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }

    private static String getQueryStringHash(String canonicalUrl) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        byte[] digest = canonicalUrl.getBytes("UTF-8");
        return new String(Hex.encodeHex(digest));
        //return encodeHexString(digest);
    }
}
