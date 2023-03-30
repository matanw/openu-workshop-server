package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;

public class SubmissionNotFoundExeption extends ApiError {

  public SubmissionNotFoundExeption(int courseId, int taskId, String studentId) {
    super(String.format("courese %d task %d has no submission for %s", courseId, taskId, studentId),
        HttpStatus.NOT_FOUND);
  }
}
