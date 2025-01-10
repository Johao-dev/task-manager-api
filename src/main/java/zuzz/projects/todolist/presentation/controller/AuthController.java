package zuzz.projects.todolist.presentation.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import zuzz.projects.todolist.persistence.entity.UserEntity;
import zuzz.projects.todolist.service.dto.LoginDTO;
import zuzz.projects.todolist.service.dto.ResponseDTO;
import zuzz.projects.todolist.service.interfaces.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> register(@RequestBody UserEntity user) throws Exception {
        logger.info("/api/auth/register is called.");
        return new ResponseEntity<>(authService.register(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginRequest) throws Exception {
        logger.info("/api/auth/login is called.");
        Map<String, String> login = authService.login(loginRequest);
        
        if(login.containsKey("jwt")) {
            logger.info("Authentication successful.");
            return new ResponseEntity<>(login, HttpStatus.OK);
        }
        
        logger.info("Authentication failed.");
        return new ResponseEntity<>(login, HttpStatus.UNAUTHORIZED);
    }
}
