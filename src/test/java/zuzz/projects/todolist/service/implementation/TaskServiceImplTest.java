package zuzz.projects.todolist.service.implementation;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import zuzz.projects.todolist.persistence.entity.TaskEntity;
import zuzz.projects.todolist.persistence.entity.UserEntity;
import zuzz.projects.todolist.persistence.repository.TaskRepository;
import zuzz.projects.todolist.persistence.repository.UserRepository;
import zuzz.projects.todolist.service.exception.TaskException;
import zuzz.projects.todolist.util.validation.TaskValidation;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private TaskValidation taskValidator;

    @Mock
    private UserRepository userRepository;
    
    private UserEntity mockUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("1");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void createTask_validTask_savesSuccessfully() throws TaskException {
        TaskEntity task = new TaskEntity();
        task.setTitle("valid task");
        task.setDescription("task description");

        when(taskValidator.validateTaskCreation(any(TaskEntity.class))).thenReturn(task);
        when(taskValidator.getAuthenticatedUser()).thenReturn(mockUser);

        taskService.createTask(task);

        verify(taskRepository).save(task);
        assertEquals(mockUser, task.getUser());
    }
}
