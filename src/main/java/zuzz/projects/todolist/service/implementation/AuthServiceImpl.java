package zuzz.projects.todolist.service.implementation;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JOSEException;

import zuzz.projects.todolist.persistence.entity.UserEntity;
import zuzz.projects.todolist.persistence.repository.UserRepository;
import zuzz.projects.todolist.service.dto.LoginDTO;
import zuzz.projects.todolist.service.dto.ResponseDTO;
import zuzz.projects.todolist.service.interfaces.AuthService;
import zuzz.projects.todolist.service.interfaces.JwtService;
import zuzz.projects.todolist.util.validation.UserValidation;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserValidation userValidation;

    private Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    public Map<String, String> login(LoginDTO login) throws Exception {
        Map<String, String> jwt = new HashMap<>();
        logger.info("Logging in");

        try {
            Optional<UserEntity> user = userRepository.findByEmail(login.getEmail());

            if (user.isEmpty()) {
                logger.error("User not registered.");
                jwt.put("error", "user not registered");
                return jwt;
            }

            if (verifyPassword(login.getPassword(), user.get().getPassword())) {
                jwt.put("jwt", jwtService.generateJwt(user.get().getId()));
                logger.info("Authentication successful");
            } else {
                jwt.put("error", "Authentication failed");
                logger.error("Authentication failed");
            }

        } catch(JOSEException | IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error("Error logging in");
            ex.printStackTrace(System.out);
        }

        return jwt;
    }

    public ResponseDTO register(UserEntity user) throws Exception {
        logger.info("Registering user");
        ResponseDTO response = userValidation.validate(user);

        try {
            if (response.getNumberOfErrors() > 3) {
                logger.error("Validation failed.");
                return response;
            }

            List<UserEntity> users = userRepository.findAll();

            for (UserEntity existingUser : users) {
                if (existingUser.getEmail().equals(user.getEmail())) {
                    logger.error("Email already exists");
                    response.setNumberOfErrors(response.getNumberOfErrors() + 1);
                    logger.error("errors: " + response.getNumberOfErrors());
                    response.setMessage("Email already exists");
                    return response;
                }
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            user.setPassword(encoder.encode(user.getPassword()));
            userRepository.save(user);
            response.setMessage("user created successfully");
            logger.info("User created successfully");
            
        }catch(Exception ex) {
            logger.error("Error registering user");
            ex.printStackTrace(System.out);
        }

        return response;
    }

    private boolean verifyPassword(String enteredPassword, String storedPassword) {
        logger.info("Verifying password");
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(enteredPassword, storedPassword);
    }
}
