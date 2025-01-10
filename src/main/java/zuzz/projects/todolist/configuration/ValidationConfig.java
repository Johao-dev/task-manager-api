package zuzz.projects.todolist.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import zuzz.projects.todolist.util.validation.TaskValidation;
import zuzz.projects.todolist.util.validation.UserValidation;

@Configuration
public class ValidationConfig {

    @Bean
    public UserValidation userValidation() {
        return new UserValidation();
    }

    @Bean
    public TaskValidation taskValidation() {
        return new TaskValidation();
    }
}
