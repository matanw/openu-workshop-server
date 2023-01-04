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
import java.util.Map;
import java.util.Objects;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Student;
import openu.workshop.webservice.model.Submission;
import openu.workshop.webservice.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CourseController {

  @Autowired
  private AuthManager authManager;


  @GetMapping("/courses")
  public List<Course> GetCourses(@RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return Arrays.asList(
        new Course(1, "c#"),
        new Course(2, "politics")
    );
  }

  @GetMapping("/courses/{id}")
  public Course GetCourse(@PathVariable int id, @RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return new Course(id, "name of " + id);
  }

  @GetMapping("/courses/{id}/tasks")
  public List<Task> GetCourseTasks(@PathVariable int id,
      @RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return Arrays.asList(
        new Task(1, new Date()),
        new Task(2, new Date())
    );
  }


  @GetMapping("/courses/{id}/tasks/{taskId}/file")
  public ResponseEntity<Resource> GetCourseTaskFile(@PathVariable int id, @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);

    File file = new File("files/task1.txt");

    Path path = Paths.get(file.getAbsolutePath());
    ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=myDoc.docx");
    return ResponseEntity.ok()
        .headers(responseHeaders)
        .contentLength(file.length())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(resource);
  }

  @PostMapping("/courses/{id}/tasks")
  public ResponseEntity<Resource> CreateCourseTasks(@PathVariable int id,
      @RequestBody List<Task> tasks,
      @RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PostMapping("/courses/{id}/tasks/{taskId}/file")
  public ResponseEntity<Resource> CreateCourseTaskFile(@PathVariable int id, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
    Files.copy(file.getInputStream(), Paths.get("files").resolve(
        Objects.requireNonNull(file.getOriginalFilename())));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/courses/{id}/tasks/{taskId}/mysubmissionfile")
  public ResponseEntity<Resource> CreateCourseTaskSubmissionFile(@PathVariable int id, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
    Files.copy(file.getInputStream(), Paths.get("files").resolve(
        Objects.requireNonNull(file.getOriginalFilename())));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @GetMapping("/courses/{id}/tasks/{taskId}/submissions")
  public List<Submission> GetCourseTasksSubmission(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return Arrays.asList(
        new Submission( new Date(),null),
        new Submission( new Date(), 96)
    );
  }


  @PostMapping("/courses/{id}/tasks/{taskId}/submission/{submissionId}/feedbeckFile")
  public ResponseEntity<Resource> CreateCourseTaskSubmissionFeedbackFile(@PathVariable int id, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
    Files.copy(file.getInputStream(), Paths.get("files").resolve(
        Objects.requireNonNull(file.getOriginalFilename())));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

}
