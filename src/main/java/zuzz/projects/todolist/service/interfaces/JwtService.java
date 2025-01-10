package zuzz.projects.todolist.service.interfaces;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

public interface JwtService {

    public String generateJwt(Long id)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
        InvalidKeySpecException, JOSEException;
    
    public JWTClaimsSet parseJwt(String token)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException,
        JOSEException, ParseException;
}
