package zuzz.projects.todolist.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import zuzz.projects.todolist.service.exception.task.TaskValidationException;
import zuzz.projects.todolist.service.exception.user.UserNotFoundException;
import zuzz.projects.todolist.util.validation.TaskValidation;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskService;

    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private UserRepository userRepository;

    private TaskValidation taskValidator;
    
    private UserEntity mockUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@test.com");
        mockUser.setPassword("testPassword");

        taskValidator = new TaskValidation(userRepository, taskRepository);
        taskService = new TaskServiceImpl(taskRepository, taskValidator);
        
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("1");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
    
    @Test
    @DisplayName("Validate that a task with valid data has been saved correctly")
    public void createTask_validTask_savesSuccessfully()
        throws TaskValidationException, UserNotFoundException {
            
        TaskEntity task = new TaskEntity();
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);
        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));

        taskService.createTask(task);

        verify(taskRepository).save(task);
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("ensure that an exception is thrown if the task fails validation")
    public void createTask_invalidTask_throwsException() throws TaskValidationException {
        TaskEntity task = new TaskEntity();
        task.setTitle(null);
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        assertThrows(TaskValidationException.class, () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Check that only the authenticated user can create the task")
    public void createTask_unauthenticatedUser_throwsException() throws TaskValidationException {
        TaskEntity task = new TaskEntity();
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        assertThrows(UserNotFoundException.class, () -> taskService.createTask(task));
    }
}