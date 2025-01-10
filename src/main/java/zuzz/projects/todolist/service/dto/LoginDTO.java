package zuzz.projects.todolist.service.dto;

public class LoginDTO {

    private String password;
    private String email;

    public LoginDTO() {
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserRequestDTO [password=" + password + ", email=" + email + "]";
    }
}
