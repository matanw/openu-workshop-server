package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;

public class TaskAlreadyExists extends ApiError {

  public TaskAlreadyExists(int courseId, int taskId) {
    super(String.format("course %d already has task with id %s",
        courseId,taskId), HttpStatus.BAD_REQUEST);
  }
}
