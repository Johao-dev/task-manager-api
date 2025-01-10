package zuzz.projects.todolist.service.interfaces;

import java.util.Map;

import zuzz.projects.todolist.persistence.entity.UserEntity;
import zuzz.projects.todolist.service.dto.LoginDTO;
import zuzz.projects.todolist.service.dto.ResponseDTO;

public interface AuthService {

    public Map<String, String> login(LoginDTO login) throws Exception;
    
    public ResponseDTO register(UserEntity user) throws Exception;
}
