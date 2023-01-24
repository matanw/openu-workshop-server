package openu.workshop.webservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.TypedQuery;
import openu.workshop.webservice.datatransferobjects.CourseDTO;
import openu.workshop.webservice.datatransferobjects.TaskDTO;
import openu.workshop.webservice.db.JPAWrapper;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;
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
import org.springframework.web.bind.annotation.PutMapping;
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
  public List<CourseDTO> GetCourses(@RequestHeader Map<String, String> headers) throws Exception {
   //tbd: think on this design
    Professor professor = authManager.getAuthenticatedProfessor(headers);
    List<Course> courses;
    try(JPAWrapper jpaWrapper=new JPAWrapper()){
      TypedQuery<Course> q = jpaWrapper.getEntityManager().
          createQuery("select c from Course c where c.professor.id = :professorId", Course.class).setParameter("professorId",professor.getId());
      courses = q.getResultList();
    }
    return courses.stream().map(c-> CourseDTO.FromModel(c,professor)).toList();
  }

  @GetMapping("/courses/{id}")
  public CourseDTO GetCourse(@PathVariable int id, @RequestHeader Map<String, String> headers) throws Exception{
    Professor professor = authManager.getAuthenticatedProfessor(headers);
    Course course;
    try(JPAWrapper jpaWrapper=new JPAWrapper()){
      course = jpaWrapper.getEntityManager().find(Course.class, id);
    }
    if (!course.getProfessor().getId().equals(professor.getId())){
      //todo: 403
      throw new Exception();
    }
    return CourseDTO.FromModel(course,professor);
  }

  @GetMapping("/courses/{id}/tasks")
  public List<TaskDTO> GetCourseTasks(@PathVariable int id,
      @RequestHeader Map<String, String> headers) throws Exception {
    Professor professor = authManager.getAuthenticatedProfessor(headers);
    Course course;
    List<Task> tasks;
    try(JPAWrapper jpaWrapper=new JPAWrapper()){
      course = jpaWrapper.getEntityManager().find(Course.class, id);
      if (!course.getProfessor().getId().equals(professor.getId())){
        //todo: 403
        throw new Exception();
      }
      TypedQuery<Task> q = jpaWrapper.getEntityManager().
          createQuery("select t from Task t where t.id.courseId = :courseId", Task.class)
          .setParameter("courseId",id);
      tasks = q.getResultList();
    }

    return tasks.stream().map(TaskDTO::FromModel).toList();
  }

  @PostMapping("/courses/{id}/tasks")
  public ResponseEntity<Resource> CreateCourseTasks(@PathVariable int id,
      @RequestBody List<TaskDTO> taskDTOs,
      @RequestHeader Map<String, String> headers) throws  Exception{
    Professor professor = authManager.getAuthenticatedProfessor(headers);
    Course course;
    try(JPAWrapper jpaWrapper=new JPAWrapper()){
      course = jpaWrapper.getEntityManager().find(Course.class, id);
      if (!course.getProfessor().getId().equals(professor.getId())){
        //todo: 403
        throw new Exception();
      }
      //todo>: validate no entity wet
      jpaWrapper.getEntityManager().getTransaction().begin();
      for (TaskDTO taskDTO: taskDTOs){
        Task task = taskDTO.ToModel(id);
        jpaWrapper.getEntityManager().persist(task);
      }
      jpaWrapper.getEntityManager().getTransaction().commit();
    }

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/file")
  public ResponseEntity<Resource> GetCourseTaskFile(@PathVariable int id, @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return fileResponse();
  }

  @PostMapping("/courses/{id}/tasks/{taskId}/file")
  public ResponseEntity<Resource> CreateCourseTaskFile(@PathVariable int id, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/courses/{id}/tasks/{taskId}/file")
  public ResponseEntity<Resource> ReplaceCourseTaskFile(@PathVariable int id, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /// submission (p)
  @GetMapping("/courses/{id}/tasks/{taskId}/submissions")
  public List<Submission> GetCourseTasksSubmissions(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return Arrays.asList(
        new Submission( new Date(),null),
        new Submission( new Date(), 96)
    );
  }


  @GetMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}")
  public Submission GetCourseTasksSubmission(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return new Submission( new Date(),null);
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/file")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return fileResponse();
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/mysubmission")
  public Submission GetCourseTasksMySubmission(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) {
    authManager.ValidateAuth(headers);
    return new Submission( new Date(),null);
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return fileResponse();
  }

  @PostMapping("/courses/{id}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> CreateCourseTasksMySubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PutMapping("/courses/{id}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> ReplaceCourseTasksMySubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @GetMapping("/courses/{id}/tasks/{taskId}/mysubmission/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return fileResponse();
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return fileResponse();
  }

  @PostMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> CreateCourseTasksSubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PutMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> ReplaceCourseTasksSubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PostMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/grade")
  public ResponseEntity<Resource> CreateGrade(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestBody int grade,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/grade")
  public ResponseEntity<Resource> UpdateGrade(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestBody int grade,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  //todo: delete
  @PostMapping("/realUpload")
  public ResponseEntity<Resource> RealUpload( @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
    authManager.ValidateAuth(headers);
    Files.copy(file.getInputStream(), Paths.get("files").resolve(
        Objects.requireNonNull(file.getOriginalFilename())));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  private ResponseEntity<Resource> fileResponse() throws IOException {

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


}
