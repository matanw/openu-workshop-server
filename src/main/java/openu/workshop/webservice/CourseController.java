package openu.workshop.webservice;

import openu.workshop.webservice.auth.AuthManager;
import openu.workshop.webservice.auth.LoginInformation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import openu.workshop.webservice.auth.LoginType;
import openu.workshop.webservice.datatransferobjects.CourseDTO;
import openu.workshop.webservice.datatransferobjects.SubmissionDTO;
import openu.workshop.webservice.datatransferobjects.TaskDTO;
import openu.workshop.webservice.errors.ApiError;
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
import org.springframework.http.HttpStatusCode;
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
  public List<SubmissionDTO> GetCourseTasksSubmissions(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws Exception {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(courseId);
    if (!course.getProfessor().getId().equals(professor.getId())) {
      //todo: 403
      throw new Exception();
    }
    List<Submission> submissions=controllersService.getSubmissions(courseId, taskId);//todo non exists task

    return submissions.stream().map(SubmissionDTO::FromModel).toList();
  }


  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}")
  public SubmissionDTO GetCourseTasksSubmission(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws Exception {

    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(courseId);
    if (!course.getProfessor().getId().equals(professor.getId())) {
      //todo: 403
      throw new Exception();
    }
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);

    return SubmissionDTO.FromModel(submission);
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/file")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws Exception {

    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(courseId);
    if (!course.getProfessor().getId().equals(professor.getId())) {
      //todo: 403
      throw new Exception();
    }
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    return fileResponse(submission.getFile());
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/mysubmission")
  public SubmissionDTO GetCourseTasksMySubmission(@PathVariable int courseId,
      @PathVariable int taskId,
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
    Course course = controllersService.getCourse(courseId);
    if (!controllersService.isStudentRegisteredToCourse(student,course)) {
      //todo: error
      throw new Exception();
    }
    Task task = controllersService.getTask(courseId,taskId);
    //handle not found task
    Submission submission=controllersService.getSubmission(courseId,taskId,student.getId());
    return SubmissionDTO.FromModel(submission);
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFile(@PathVariable int courseId,
      @PathVariable int taskId,
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
    Course course = controllersService.getCourse(courseId);
    if (!controllersService.isStudentRegisteredToCourse(student,course)) {
      //todo: error
      throw new Exception();
    }
    Task task = controllersService.getTask(courseId,taskId);
    //handle not found task
    Submission submission=controllersService.getSubmission(courseId,taskId,student.getId());
    return fileResponse(submission.getFile());
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


  @GetMapping("/courses/{courseId}/tasks/{taskId}/mysubmission/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFeedbackFile(@PathVariable int courseId,
      @PathVariable int taskId,
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
    Course course = controllersService.getCourse(courseId);
    if (!controllersService.isStudentRegisteredToCourse(student,course)) {
      //todo: error
      throw new Exception();
    }
    Task task = controllersService.getTask(courseId,taskId);
    //handle not found task
    Submission submission=controllersService.getSubmission(courseId,taskId,student.getId());
    return fileResponse(submission.getFeedbackFile());
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFeedbackFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws Exception {

    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(courseId);
    if (!course.getProfessor().getId().equals(professor.getId())) {
      //todo: 403
      throw new Exception();
    }
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);

    return fileResponse(submission.getFeedbackFile());
  }

  @PostMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> CreateCourseTasksSubmissionFeedbackFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws Exception {

    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(courseId);
    if (!course.getProfessor().getId().equals(professor.getId())) {
      //todo: 403
      throw new Exception();
    }
    FileObject  fileObject=fileToFileObject(file);
    controllersService.addFeedBackFileToSubmission(courseId, taskId,studentId,fileObject);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PutMapping("/courses/{id}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> ReplaceCourseTasksSubmissionFeedbackFile(@PathVariable int id,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws IOException {
   
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PostMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/grade")
  public ResponseEntity<Resource> CreateGrade(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestBody int grade,
      @RequestHeader Map<String, String> headers) throws Exception {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    //todo: if it is student
    Professor professor = controllersService.getProfessor(loginInformation.username, loginInformation.password);
    if (professor == null){
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
    Course course = controllersService.getCourse(courseId);
    if (!course.getProfessor().getId().equals(professor.getId())) {
      //todo: 403
      throw new Exception();
    }
    controllersService.addGradeToSubmission(courseId, taskId,studentId,grade);
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
  @GetMapping("/apierr")
  public SubmissionDTO getApiError( ) throws Exception {
    throw new ApiError("ggg", HttpStatus.BAD_REQUEST);
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
