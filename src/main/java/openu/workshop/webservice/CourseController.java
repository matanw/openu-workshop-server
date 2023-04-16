package openu.workshop.webservice;

import openu.workshop.webservice.auth.AuthManager;
import openu.workshop.webservice.auth.LoginInformation;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import openu.workshop.webservice.auth.LoginType;
import openu.workshop.webservice.datatransferobjects.CourseDTO;
import openu.workshop.webservice.datatransferobjects.SubmissionDTO;
import openu.workshop.webservice.datatransferobjects.TaskDTO;
import openu.workshop.webservice.errors.ApiError;
import openu.workshop.webservice.errors.CourseNotFoundOrPermissionDenied;
import openu.workshop.webservice.errors.FileAlreadyExistsException;
import openu.workshop.webservice.errors.FileNotExistException;
import openu.workshop.webservice.errors.GradeAlreayExistsException;
import openu.workshop.webservice.errors.GradeNotExistException;
import openu.workshop.webservice.errors.MethodAllowedOnlyForProfessorsException;
import openu.workshop.webservice.errors.MethodAllowedOnlyForStudentsException;
import openu.workshop.webservice.errors.SubmissionAlreadyExistExeption;
import openu.workshop.webservice.errors.SubmissionNotFoundExeption;
import openu.workshop.webservice.errors.TaskAlreadyExists;
import openu.workshop.webservice.errors.TaskIDMismatchException;
import openu.workshop.webservice.errors.TaskNotFoundError;
import openu.workshop.webservice.errors.UnauthorizedException;
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
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @Autowired
  private DataLayerService controllersService;


  @GetMapping("/courses")
  public List<CourseDTO> GetCourses(@RequestHeader Map<String, String> headers) throws ApiError {
   LoginInformation loginInformation=authManager.GetLoginInformationOrThrows401(headers);
   if (loginInformation.loginType== LoginType.PROFESSOR) {
      Professor professor = controllersService.getProfessor(loginInformation.username,
          loginInformation.password);
      if (professor == null) {
        throw new UnauthorizedException();
      }
      List<Course> courses = controllersService.listCourses(professor);
      return courses.stream().map(CourseDTO::FromModel).toList();
    }
     Student student = controllersService.getStudent(loginInformation.username,
         loginInformation.password);
     if (student == null) {
       throw new UnauthorizedException();
     }
     List<Course> courses = controllersService.listCourses(student);
     return courses.stream().map(CourseDTO::FromModel).toList();
  }

  @GetMapping("/courses/{courseId}")
  public CourseDTO GetCourse(@PathVariable int courseId, @RequestHeader Map<String, String> headers) throws ApiError{
    LoginInformation loginInformation=authManager.GetLoginInformationOrThrows401(headers);
    verifyCourseExistAndMatchPerson(loginInformation, courseId);
    Course course = controllersService.getCourse(courseId);
    return CourseDTO.FromModel(course);
  }

  @GetMapping("/courses/{courseId}/tasks")
  public List<TaskDTO> GetCourseTasks(@PathVariable int courseId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    verifyCourseExistAndMatchPerson(loginInformation, courseId);
    List<Task> tasks = controllersService.getTasksByCourse(courseId);
    return tasks.stream().map(TaskDTO::FromModel).toList();
  }

  @PostMapping("/courses/{courseId}/tasks")
  public ResponseEntity<Resource> CreateCourseTask(@PathVariable int courseId,
      @RequestBody TaskDTO taskDTO,
      @RequestHeader Map<String, String> headers) throws ApiError{
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    Task task = taskDTO.ToModel(courseId);
    if  (controllersService.getTask(courseId,task.getId().taskId)!=null){
      throw new TaskAlreadyExists(courseId, task.getId().taskId);
    }
    controllersService.saveTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/courses/{courseId}/tasks/{taskId}")
  public ResponseEntity<Resource> ReplaceCourseTask(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestBody TaskDTO taskDTO,
      @RequestHeader Map<String, String> headers) throws ApiError{
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    if (taskDTO.getId()!=taskId){
      throw new TaskIDMismatchException();
    }
    Task task = taskDTO.ToModel(courseId);
    if  (controllersService.getTask(courseId,task.getId().taskId)==null){
      throw new TaskNotFoundError(courseId, task.getId().taskId);
    }
    controllersService.updateTaskWithoutFile(courseId, taskId,task);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @DeleteMapping("/courses/{courseId}/tasks/{taskId}")
  public ResponseEntity<Resource> DeleteCourseTask(@PathVariable int courseId,
      @PathVariable  int taskId,
      @RequestHeader Map<String, String> headers) throws ApiError{
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    if  (controllersService.getTask(courseId,taskId)==null){
      throw new TaskNotFoundError(courseId, taskId);
    }
    controllersService.deleteTask(courseId, taskId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/file")
  public ResponseEntity<Resource> GetCourseTaskFile(@PathVariable int courseId, @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    verifyCourseExistAndMatchPerson(loginInformation, courseId);
    assertTaskExists(courseId, taskId);
    FileObject fileObject = controllersService.getFile(courseId, taskId);
    if (fileObject == null){
      throw new FileNotExistException();
    }
    return fileResponse(fileObject);
  }

  @PostMapping("/courses/{courseId}/tasks/{taskId}/file")
  public ResponseEntity<Resource> CreateCourseTaskFile(@PathVariable int courseId, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws ApiError ,IOException{
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation, courseId);
    assertTaskExists(courseId, taskId);
    if (controllersService.getFile(courseId,taskId)!=null){
      throw new FileAlreadyExistsException();
    }
    FileObject fileObject = fileToFileObject(file);
    controllersService.saveFile(courseId, taskId, fileObject);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/courses/{courseId}/tasks/{taskId}/file")
  public ResponseEntity<Resource> ReplaceCourseTaskFile(@PathVariable int courseId, @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws ApiError, IOException {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation, courseId);
    assertTaskExists(courseId, taskId);
    if (controllersService.getFile(courseId,taskId)==null){
      throw new FileNotExistException();
    }
    FileObject fileObject = fileToFileObject(file);
    controllersService.saveFile(courseId, taskId, fileObject);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions")
  public List<SubmissionDTO> GetCourseTasksSubmissions(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    List<Submission> submissions=controllersService.getSubmissions(courseId, taskId);
    return submissions.stream().map(SubmissionDTO::FromModel).toList();
  }


  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}")
  public SubmissionDTO GetCourseTasksSubmission(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    return SubmissionDTO.FromModel(submission);
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/file")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    //submission is created with file, so no need to check on file
    return fileResponse(submission.getFile());
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/mysubmission")
  public SubmissionDTO GetCourseTasksMySubmission(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsStudent(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    String studentId= loginInformation.username;
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    return SubmissionDTO.FromModel(submission);
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsStudent(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    String studentId= loginInformation.username;
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    // submission always has a file
    return fileResponse(submission.getFile());
  }

  @PostMapping("/courses/{courseId}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> CreateCourseTasksMySubmissionFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws ApiError, IOException {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsStudent(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    String studentId= loginInformation.username;
    if (controllersService.getSubmission(courseId, taskId,studentId) != null){
      throw  new SubmissionAlreadyExistExeption(courseId, taskId, studentId);
    }
    FileObject fileObject=fileToFileObject(file);
    controllersService.addSubmission(courseId, taskId, studentId,fileObject);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PutMapping("/courses/{courseId}/tasks/{taskId}/mysubmission/file")
  public ResponseEntity<Resource> ReplaceCourseTasksMySubmissionFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws ApiError, IOException {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsStudent(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    String studentId= loginInformation.username;
    if (controllersService.getSubmission(courseId, taskId,studentId) == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    FileObject fileObject=fileToFileObject(file);
    controllersService.replaceSubmissionFile(courseId, taskId, studentId,fileObject);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/mysubmission/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksMySubmissionFeedbackFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsStudent(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    String studentId= loginInformation.username;
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    if (submission.getFeedbackFile()==null){
      throw new FileNotExistException();
    }
    return fileResponse(submission.getFeedbackFile());
  }

  @GetMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> GetCourseTasksSubmissionFeedbackFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    if (submission.getFeedbackFile()==null){
      throw new FileNotExistException();
    }
    return fileResponse(submission.getFeedbackFile());
  }

  @PostMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> CreateCourseTasksSubmissionFeedbackFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws ApiError, IOException {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    if (submission.getFeedbackFile()!=null){
      throw new FileAlreadyExistsException();
    }
    FileObject  fileObject=fileToFileObject(file);
    controllersService.setSubmissionFeedBackFile(courseId, taskId,studentId,fileObject);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }


  @PutMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/feedbackFile")
  public ResponseEntity<Resource> ReplaceCourseTasksSubmissionFeedbackFile(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestParam("file") MultipartFile file,
      @RequestHeader Map<String, String> headers) throws ApiError, IOException {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    if (submission.getFeedbackFile()==null){
      throw new FileNotExistException();
    }
    FileObject  fileObject=fileToFileObject(file);
    controllersService.setSubmissionFeedBackFile(courseId, taskId,studentId,fileObject);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }


  @PostMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/grade")
  public ResponseEntity<Resource> CreateGrade(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestBody int grade,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    if (submission.getGrade()!=null){
      throw new GradeAlreayExistsException();
    }
    controllersService.setSubmissionGrade(courseId, taskId,studentId,grade);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PutMapping("/courses/{courseId}/tasks/{taskId}/submissions/{studentId}/grade")
  public ResponseEntity<Resource> UpdateGrade(@PathVariable int courseId,
      @PathVariable int taskId,
      @PathVariable String studentId,
      @RequestBody int grade,
      @RequestHeader Map<String, String> headers) throws ApiError {
    LoginInformation loginInformation = authManager.GetLoginInformationOrThrows401(headers);
    assertCallerIsProfessor(loginInformation);
    verifyCourseExistAndMatchPerson(loginInformation,courseId);
    assertTaskExists(courseId, taskId);
    Submission submission=controllersService.getSubmission(courseId, taskId,studentId);
    if (submission == null){
      throw  new SubmissionNotFoundExeption(courseId, taskId, studentId);
    }
    if (submission.getGrade()==null){
      throw new GradeNotExistException();
    }
    controllersService.setSubmissionGrade(courseId, taskId,studentId,grade);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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


  private void verifyCourseExistAndMatchPerson(LoginInformation loginInformation, int courseId) throws ApiError{
    if (loginInformation.loginType==LoginType.PROFESSOR) {
      Professor professor = controllersService.getProfessor(loginInformation.username,
          loginInformation.password);
      if (professor == null) {
        throw new UnauthorizedException();
      }
      Course course = controllersService.getCourse(courseId);
      if (course ==null || !course.getProfessor().getId().equals(professor.getId())) {
        throw new CourseNotFoundOrPermissionDenied(courseId);
      }
      return;
    }
    Student student = controllersService.getStudent(loginInformation.username,
          loginInformation.password);
      if (student == null) {
        throw new UnauthorizedException();
      }
      Course course = controllersService.getCourse(courseId);
      if (course== null || !controllersService.isStudentRegisteredToCourse(student,course)) {
        throw new CourseNotFoundOrPermissionDenied(courseId);
      }
  }

  private void assertCallerIsStudent(LoginInformation loginInformation) throws ApiError{
    if (loginInformation.loginType ==LoginType.PROFESSOR){
      throw new MethodAllowedOnlyForStudentsException();
    }
  }
  private void assertCallerIsProfessor(LoginInformation loginInformation) throws ApiError{
    if (loginInformation.loginType ==LoginType.STUDENT){
      throw new MethodAllowedOnlyForProfessorsException();
    }
  }

  private void assertTaskExists(int courseId, int taskId) throws  ApiError{
    Task task = controllersService.getTask(courseId, taskId);
    if (task == null){
      throw new TaskNotFoundError(courseId, taskId);
    }
  }

  private FileObject fileToFileObject( MultipartFile file) throws IOException {
    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    return new FileObject(fileName, file.getBytes());
  }

}
