package zuzz.projects.todolist.util.validation;

import zuzz.projects.todolist.service.dto.ResponseDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import zuzz.projects.todolist.persistence.entity.UserEntity;

@Component
public class UserValidation {

    private Logger logger = LoggerFactory.getLogger(UserValidation.class);
    
    public ResponseDTO validate(UserEntity user) {
        ResponseDTO response = new ResponseDTO();
        
        response.setNumberOfErrors(0);
        logger.info("Validating user");
        
        if(user.getUsername() == null ||
                user.getUsername().length() < 3 ||
                user.getUsername().length() > 15) {
            
            logger.warn("username is null or less than 3 or greater than 15.");
            response.setNumberOfErrors(response.getNumberOfErrors() + 1);
            response.setMessage(
                    "The username field shouldn't be null "
                            + "and also shouldn't less than 3 and "
                            + "greater than 15.");
        }
        
        if(user.getEmail() == null ||
                !user.getEmail()
                .matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            
            logger.warn("email is null or not valid.");
            response.setNumberOfErrors(response.getNumberOfErrors() + 1);
            response.setMessage(
                    "The email field is not valid.");
        }
        
        if(user.getPassword() == null ||
                !user.getPassword()
                .matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,16}$")) {
            
            logger.info("password is null or not valid.");
            response.setNumberOfErrors(response.getNumberOfErrors() + 1);
            response.setMessage(
                    "The password must be between 8 and 16 characters, "
                            + "at least one number, one uppercase letter,"
                            + "and one lowercase letter.");
        }
        
        return response;
    }
}
