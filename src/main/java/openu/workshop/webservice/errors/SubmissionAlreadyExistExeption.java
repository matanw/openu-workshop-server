package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class SubmissionAlreadyExistExeption extends
    ApiError {

  public SubmissionAlreadyExistExeption(int courseId, int taskId, String studentId) {
    super(String.format("course %d task %d already have submission for student %s",
        courseId,taskId,studentId), HttpStatus.BAD_REQUEST);
  }
}
