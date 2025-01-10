package zuzz.projects.todolist.service.implementation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import zuzz.projects.todolist.service.interfaces.JwtService;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("classpath:jwtKeys/private_key.pem")
    private Resource privateKeyResource;

    @Value("classpath:jwtKeys/public_key.pem")
    private Resource publicKeyResource;

    private Logger logger = LoggerFactory.getLogger(JwtServiceImpl.class);

    @Override
    public String generateJwt(Long id)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
        JOSEException {

        logger.info("generating jwt");
        PrivateKey privateKey = loadPrivateKey(privateKeyResource);
        JWSSigner signer = new RSASSASigner(privateKey);
        Date now = new Date();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(id.toString())
            .issueTime(now)
            .expirationTime(new Date(now.getTime() + 14_400_000))
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
        signedJWT.sign(signer);
        logger.info("jwt generated");
        return signedJWT.serialize();
    }

    @Override
    public JWTClaimsSet parseJwt(String token)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
        JOSEException, ParseException {

        logger.info("parsing token");
        PublicKey publicKey = loadPublicKey(publicKeyResource);

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);

        if (!signedJWT.verify(verifier)) {
            logger.error("jwt verification failed");
            throw new JOSEException("JWT verification failed");
        }
        
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        if (claimsSet.getExpirationTime().before(new Date())) {
            logger.error("token expired");
            throw new JOSEException("Expired token");
        }

        logger.info("token parsed");
        return claimsSet;
    }

    private PrivateKey loadPrivateKey(Resource resource)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));

        String privateKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        byte[] decodeKey = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodeKey));
    }

    private PublicKey loadPublicKey(Resource resource)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        
        String publicKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
    }
}
