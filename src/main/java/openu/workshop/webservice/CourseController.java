package openu.workshop.webservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Student;
import openu.workshop.webservice.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CourseController {

  @Autowired
  private AuthManager authManager;


  @GetMapping("/courses")
  public List<Course> GetCourses(@RequestHeader("Authorization") String authorization) {
    authManager.ValidateAuth(authorization);
    return Arrays.asList(
        new Course(1, "c#"),
        new Course(2, "politics")
    );
  }

  @GetMapping("/courses/{id}")
  public Course GetCourse(@PathVariable int id, @RequestHeader("Authorization") String authorization) {
    authManager.ValidateAuth(authorization);
    return new Course(id, "name of "+id);
  }

  @GetMapping("/courses/{id}/tasks")
  public List<Task> GetCourseTasks(@PathVariable int id, @RequestHeader("Authorization") String authorization) {
    authManager.ValidateAuth(authorization);
    return Arrays.asList(
        new Task(1, new Date()),
        new Task(2, new Date())
    );
  }
  @GetMapping("/courses/{id}/tasks/{taskId}/file")
  public  ResponseEntity<Resource>  GetCourseTaskFile(@PathVariable int id,@PathVariable int taskId,
      @RequestHeader("Authorization") String authorization) throws IOException {
    authManager.ValidateAuth(authorization);

    File file = new File("/usr/local/google/home/matanwiesner/personal/webservice/call_example.sh");

    Path path = Paths.get(file.getAbsolutePath());
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=myDoc.docx");
    return ResponseEntity.ok()
        .headers(headers)
        .contentLength(file.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

}
