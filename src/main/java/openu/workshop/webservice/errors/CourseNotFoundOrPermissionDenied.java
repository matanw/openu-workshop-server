package openu.workshop.webservice.errors;

import org.springframework.http.HttpStatus;

public class CourseNotFoundOrPermissionDenied extends
    ApiError {

  public CourseNotFoundOrPermissionDenied(int courseId) {
    super(String.format("course %d does not exist, "+
        "or you don't have permission to accsees it", courseId),
        HttpStatus.NOT_FOUND);
  }
}
