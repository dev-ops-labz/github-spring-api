package org.git.api.utils;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;


public class JwtUtils {



    public static String getGitAccessJwt(String clientId, String pemFilePath) {
        String jwt =null;
        try {
            String pem = readPemFile(pemFilePath);
            jwt = generateJwt(pem, clientId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jwt;
    }

    private static String readPemFile(String filePath) throws Exception {
        String pem = Files.readString(Paths.get(filePath));
        return pem.replaceAll("-----[A-Z ]+-----", "").replaceAll("\\s+", "");
    }

    private static String generateJwt(String pem, String clientId) throws Exception {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

        long now = System.currentTimeMillis();
        Date iat = new Date(now - 60000); // 60 seconds in the past
        Date exp = new Date(now + 600000); // 10 minutes in the future

        return Jwts.builder()
                .setIssuedAt(iat)
                .setExpiration(exp)
                .setIssuer(clientId)
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }



}
