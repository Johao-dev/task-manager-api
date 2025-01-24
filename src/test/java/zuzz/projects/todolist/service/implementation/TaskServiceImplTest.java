package zuzz.projects.todolist.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.List;
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
import zuzz.projects.todolist.service.exception.task.TaskNotFoundException;
import zuzz.projects.todolist.service.exception.task.TaskValidationException;
import zuzz.projects.todolist.service.exception.task.UserIsNotOwnerOfTaskException;
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
    public void createTask_invalidTask_throwsException()
        throws TaskValidationException {

        TaskEntity task = new TaskEntity();
        task.setTitle(null);
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        assertThrows(TaskValidationException.class,
            () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Check that only the authenticated user can create the task")
    public void createTask_unauthenticatedUser_throwsException()
        throws TaskValidationException {

        TaskEntity task = new TaskEntity();
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        assertThrows(UserNotFoundException.class,
            () -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Verify that the fields of an existing task are updated correctly")
    public void updateTask_validTask_savesSuccessfully()
    throws TaskNotFoundException, UserNotFoundException,
    UserIsNotOwnerOfTaskException, TaskValidationException {
        
        TaskEntity updatedTask = new TaskEntity();
        updatedTask.setId(1L);
        updatedTask.setTitle("test update Title");
        updatedTask.setDescription("test update Description");
        updatedTask.setStatus(TaskEntity.Status.IN_PROGRESS);
        updatedTask.setUser(mockUser);
        
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(updatedTask);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(updatedTask));
        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));
        
        taskService.updateTask(1L, updatedTask);
        
        verify(taskRepository).save(updatedTask);
        verify(taskRepository).findById(1L);
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Validate that an exception is thrown if the user\r\n" +
                "tries to update a task that does not belong to him")
    public void updateTask_unownedTask_throwsException()
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException, TaskValidationException {
        
        UserEntity failUser = new UserEntity();
        failUser.setId(2L);
        failUser.setUsername("failUser");
        failUser.setEmail("test@test.com");
        failUser.setPassword("testPassword2");

        TaskEntity updatedTask = new TaskEntity();
        updatedTask.setId(1L);
        updatedTask.setTitle("test update Title");
        updatedTask.setDescription("test update Description");
        updatedTask.setStatus(TaskEntity.Status.IN_PROGRESS);
        updatedTask.setUser(failUser);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(updatedTask));
        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));

        assertThrows(UserIsNotOwnerOfTaskException.class,
            () -> taskService.updateTask(1L, updatedTask));
    }

    @Test
    @DisplayName("Confirm that an existing task has been successfully deleted")
    public void deleteTask_validTask_deletesSuccessfully()
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException {

        Long taskId = 1L;

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));

        taskService.deleteTask(taskId);

        verify(userRepository).findById(anyLong());
        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Verify that an exception is thrown if an attempt is made to delete a non-existent task")
    public void deleteTask_nonExistentTask_throwsException()
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException {

        Long taskId = 1L;

        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
            () -> taskService.deleteTask(taskId));

        verify(userRepository).findById(anyLong());
        verify(taskRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Test that a user cannot delete tasks from another user.")
    public void deleteTask_unownedTask_throwsException()
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException {

        Long taskId = 1L;

        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);
        anotherUser.setUsername("failUser");
        anotherUser.setEmail("test@test.com");
        anotherUser.setPassword("testPassword2");

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(anotherUser);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));

        assertThrows(UserIsNotOwnerOfTaskException.class,
            () -> taskService.deleteTask(taskId));
        
        verify(userRepository).findById(anyLong());
        verify(taskRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Check that the correct task is returned for the provided ID")
    public void findById_validTask_returnsSuccessfully()
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException {

        Long taskId = 1L;

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));

        TaskEntity returnedTask = taskService.findById(taskId);

        assertEquals(task, returnedTask);
        verify(taskRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Verify that an exception is thrown if the task does not belong to the authenticated user.")
    public void findById_unownedTask_throwsException()
        throws TaskNotFoundException, UserNotFoundException,
        UserIsNotOwnerOfTaskException {

        Long taskId = 1L;

        UserEntity anotherUser = new UserEntity();
        anotherUser.setId(2L);
        anotherUser.setUsername("failUser");
        anotherUser.setEmail("test@test.com");
        anotherUser.setPassword("testPassword2");

        TaskEntity task = new TaskEntity();
        task.setId(taskId);
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(anotherUser);

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));

        assertThrows(UserIsNotOwnerOfTaskException.class,
            () -> taskService.findById(taskId));
        
        verify(taskRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Ensure that only tasks from the authenticated user are returned")
    public void findAll_validTasks_returnsSuccessfully()
        throws UserNotFoundException {

        TaskEntity task = new TaskEntity();
        task.setId(1L);
        task.setTitle("testTitle");
        task.setDescription("testDescription");
        task.setStatus(TaskEntity.Status.PENDING);
        task.setUser(mockUser);

        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));
        when(taskRepository.findAllByUserId(anyLong())).thenReturn(List.of(task));

        List<TaskEntity> tasks = taskService.findAll();

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));

        verify(taskRepository).findAllByUserId(anyLong());
        verify(userRepository).findById(anyLong());
    }

    @Test
    @DisplayName("Validate that the list is empty if the user does not have any tasks")
    public void findAll_unownedTasks_returnsEmptyList()
        throws UserNotFoundException {

        when(userRepository.findById(anyLong())).thenReturn((Optional.of(mockUser)));
        when(taskRepository.findAllByUserId(anyLong())).thenReturn(List.of());

        List<TaskEntity> tasks = taskService.findAll();

        assertEquals(0, tasks.size());

        verify(taskRepository).findAllByUserId(anyLong());
        verify(userRepository).findById(anyLong());
    }
}