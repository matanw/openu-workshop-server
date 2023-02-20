package openu.workshop.webservice;

import openu.workshop.webservice.auth.AuthManager;
import openu.workshop.webservice.auth.LoginInformation;
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
import openu.workshop.webservice.auth.LoginType;
import openu.workshop.webservice.datatransferobjects.CourseDTO;
import openu.workshop.webservice.datatransferobjects.TaskDTO;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.FileObject;
import openu.workshop.webservice.model.Professor;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class CourseController {

  @Autowired
  private AuthManager authManager;

  @Autowired
  private ControllersService controllersService;


  @GetMapping("/courses")
  public List<CourseDTO> GetCourses(@RequestHeader Map<String, String> headers) throws Exception {
   LoginInformation loginInformation=authManager.GetLoginInformationOrThrows401(headers);
   if (loginInformation.loginType== LoginType.PROFESSOR) {
      Professor professor = controllersService.getProfessor(loginInformation.username,
          loginInformation.password);
      if (professor == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      List<Course> courses = controllersService.listCourses(professor);
      return courses.stream().map(CourseDTO::FromModel).toList();
    }else{
     Student student = controllersService.getStudent(loginInformation.username,
         loginInformation.password);
     if (student == null) {
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
     }
     List<Course> courses = controllersService.listCourses(student);
     return courses.stream().map(CourseDTO::FromModel).toList();
   }
  }

  @GetMapping("/courses/{id}")
  public CourseDTO GetCourse(@PathVariable int id, @RequestHeader Map<String, String> headers) throws Exception{
    LoginInformation loginInformation=authManager.GetLoginInformationOrThrows401(headers);
    if (loginInformation.loginType==LoginType.PROFESSOR) {
      Professor professor = controllersService.getProfessor(loginInformation.username,
          loginInformation.password);
      if (professor == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      Course course = controllersService.getCourse(id);
      if (!course.getProfessor().getId().equals(professor.getId())) {
        //todo: 403
        throw new Exception();
      }
      return CourseDTO.FromModel(course);
    }else{
      Student student = controllersService.getStudent(loginInformation.username,
          loginInformation.password);
      if (student == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      Course course = controllersService.getCourse(id);
      if (!controllersService.isStudentRegisteredToCourse(student,course)) {
        //todo: error
        throw new Exception();
      }
      return CourseDTO.FromModel(course);
    }
  }

  @GetMapping("/courses/{id}/tasks")
  public List<TaskDTO> GetCourseTasks(@PathVariable int id,
      @RequestHeader Map<String, String> headers) throws Exception {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
   if (loginInformation.loginType==LoginType.PROFESSOR) {
     Professor professor = controllersService.getProfessor(loginInformation.username,
         loginInformation.password);
     if (professor == null) {
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
     }
     Course course = controllersService.getCourse(id);
     if (!course.getProfessor().getId().equals(professor.getId())) {
       //todo: 403
       throw new Exception();
     }
     List<Task> tasks = controllersService.getTasksByCourse(id);
     return tasks.stream().map(TaskDTO::FromModel).toList();
   }else{
     Student student = controllersService.getStudent(loginInformation.username,
         loginInformation.password);
     if (student == null) {
       throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
     }
     Course course = controllersService.getCourse(id);
     if (!controllersService.isStudentRegisteredToCourse(student,course)) {
       //todo: error
       throw new Exception();
     }
     List<Task> tasks = controllersService.getTasksByCourse(id);
     return tasks.stream().map(TaskDTO::FromModel).toList();
   }
  }

  @PostMapping("/courses/{id}/tasks")
  public ResponseEntity<Resource> CreateCourseTasks(@PathVariable int id,
      @RequestBody List<TaskDTO> taskDTOs,
      @RequestHeader Map<String, String> headers) throws  Exception{
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(id);
    if (!course.getProfessor().getId().equals(professor.getId())){
      //todo: 403
      throw new Exception();
    }
    List<Task> tasks = taskDTOs.stream().map(t-> t.ToModel(id)).toList();
    controllersService.saveTasks(tasks);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/file")
  public ResponseEntity<Resource> GetCourseTaskFile(@PathVariable int courseId, @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws Exception {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    if ( loginInformation.loginType==LoginType.PROFESSOR) {
      Professor professor = controllersService.getProfessor(loginInformation.username,
          loginInformation.password);
      if (professor == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      Course course = controllersService.getCourse(courseId);
      if (!course.getProfessor().getId().equals(professor.getId())) {
        //todo: 403
        throw new Exception();
      }
      FileObject fileObject = controllersService.getFile(courseId, taskId);
      return fileResponse(fileObject);
    }else{
      Student student = controllersService.getStudent(loginInformation.username,
          loginInformation.password);
      if (student == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      Course course = controllersService.getCourse(courseId);
      if (!controllersService.isStudentRegisteredToCourse(student,course)) {
        //todo: error
        throw new Exception();
      }
      FileObject fileObject = controllersService.getFile(courseId, taskId);
      return fileResponse(fileObject);
    }
  }

  @PostMapping("/courses/{courseId}/tasks/{taskId}/file")
  public ResponseEntity<Resource> CreateCourseTaskFile(@PathVariable int courseId, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws Exception {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(courseId);
    if (!course.getProfessor().getId().equals(professor.getId())){
      //todo: 403
      throw new Exception();
    }
    FileObject fileObject =fileToFileObject(file);
    controllersService.saveFile(courseId, taskId, fileObject);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/courses/{id}/tasks/{taskId}/file")
  public ResponseEntity<Resource> ReplaceCourseTaskFile(@PathVariable int id, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /// submission (p)
  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions")
  public List<Submission> GetCourseTasksSubmissions(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) {
   
    return Arrays.asList(
    );
  }


  @GetMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}")
  public Submission GetCourseTasksSubmission(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) {
   
    return new Submission();// new Date(),null);
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/file")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return fileResponse();
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/mysubmission")
  public Submission GetCourseTasksMySubmission(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) {
   
    return new Submission();// new Date(),null);
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return fileResponse();
  }

  @PostMapping("/courses/{id}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> CreateCourseTasksMySubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws Exception {
    LoginInformation loginInformation=authManager.GetLoginInformationOrThrows401(headers);
    if (loginInformation.loginType==LoginType.PROFESSOR) {
      //trhow
    }
      Student student = controllersService.getStudent(loginInformation.username,
          loginInformation.password);
      if (student == null) {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
      }
      Course course = controllersService.getCourse(id);
      if (!controllersService.isStudentRegisteredToCourse(student,course)) {
        //todo: error
        throw new Exception();
      }
      Task task = controllersService.getTask(id,taskId);
FileObject fileObject=fileToFileObject(file);
      //tdo replae?
    //todo: check date
controllersService.addSubmission(id, task, student.getId(),fileObject);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  private FileObject fileToFileObject( MultipartFile file) throws IOException {
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    return new FileObject(fileName, file.getBytes());
  }


  @PutMapping("/courses/{id}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> ReplaceCourseTasksMySubmissionFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @GetMapping("/courses/{id}/tasks/{taskId}/mysubmission/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return fileResponse();
  }

  @GetMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return fileResponse();
  }

  @PostMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> CreateCourseTasksSubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PutMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> ReplaceCourseTasksSubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PostMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/grade")
  public ResponseEntity<Resource> CreateGrade(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestBody int grade,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/grade")
  public ResponseEntity<Resource> UpdateGrade(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestBody int grade,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  //todo: delete
  @PostMapping("/realUpload")
  public ResponseEntity<Resource> RealUpload( @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    Files.copy(file.getInputStream(), Paths.get("files").resolve(
        Objects.requireNonNull(file.getOriginalFilename())));
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  //todo delete this signature
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
  private ResponseEntity<Resource> fileResponse(FileObject fileObject){
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileObject.Name);
    return ResponseEntity.ok()
        .headers(responseHeaders)
        .contentLength(fileObject.Data.length)
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(new ByteArrayResource(fileObject.Data));
  }


}
