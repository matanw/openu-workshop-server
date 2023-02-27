package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;

public class TaskNotFoundError extends ApiError {

  public TaskNotFoundError(int courseId, int taskId) {
   super(String.format("course %d doesn't have task with id %s",
        courseId,taskId), HttpStatus.NOT_FOUND);
  }
}
