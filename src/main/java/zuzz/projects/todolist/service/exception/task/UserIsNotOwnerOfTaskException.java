package zuzz.projects.todolist.service.exception.task;

public class UserIsNotOwnerOfTaskException extends TaskException {

    public UserIsNotOwnerOfTaskException(String message) {
        super(message);
    }
}