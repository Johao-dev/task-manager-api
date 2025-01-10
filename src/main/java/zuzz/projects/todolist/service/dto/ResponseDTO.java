package zuzz.projects.todolist.service.dto;

public class ResponseDTO {

    private int numberOfErrors;
    private String message;

    public ResponseDTO() {
    }

    public ResponseDTO(int numberOfErrors, String message) {
        this.numberOfErrors = numberOfErrors;
        this.message = message;
    }

    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" + "numberOfErrors=" + numberOfErrors + ", message=" + message + '}';
    }
}
