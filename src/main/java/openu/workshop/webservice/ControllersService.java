package openu.workshop.webservice;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import openu.workshop.webservice.model.FileObject;
import openu.workshop.webservice.db.FuncWithEntityManager;
import openu.workshop.webservice.db.FuncWithEntityManagerAndTransaction;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;
import openu.workshop.webservice.model.Registration;
import openu.workshop.webservice.model.Student;
import openu.workshop.webservice.model.Submission;
import openu.workshop.webservice.model.SubmissionID;
import openu.workshop.webservice.model.Task;
import org.springframework.stereotype.Component;

@Component
public class ControllersService {

  public Professor getProfessor(String id, String password){
    return executeInDB(em->{
      Professor professor=em.find(Professor.class, id);
      if (professor ==null){
        return null;
      }
      if (!professor.getPassword().equals(password)){
        return null;
      }
      return professor;
    });
  }
  public Student getStudent(String id, String password){
    return executeInDB(em->{
      Student student=em.find(Student.class, id);
      if (student ==null){
        return null;
      }
      if (!student.getPassword().equals(password)){
        return null;
      }
      return student;
    });
  }

  private <T> T executeInDB(FuncWithEntityManager<T> func){
    EntityManagerFactory entityManagerFactory = Persistence.
        createEntityManagerFactory("default");
    EntityManager entityManager = entityManagerFactory.createEntityManager();
    T value =func.call(entityManager);
    entityManager.close();
    entityManagerFactory.close();
    return value;
  }

  private <T> T executeInTransaction(FuncWithEntityManagerAndTransaction<T> func){
    return executeInDB(em-> {
          EntityTransaction t = em.getTransaction();
          t.begin();
          T value = func.call(em,t);
          t.commit();
          return value;
        }
    );
  }

  public List<Course> listCourses(Professor professor) {
    return executeInDB(em->
        em.createQuery("select c from Course c where c.professor.id = :professorId", Course.class)
            .setParameter("professorId",professor.getId()).getResultList());
  }

  public List<Course> listCourses(Student student) {
    return executeInDB(em->
        em.createQuery("select r.course from Registration r where r.student.id = :studentId", Course.class)
            .setParameter("studentId",student.getId()).getResultList());
  }

  public boolean isStudentRegisteredToCourse(Student student, Course cousre){
    return executeInDB(em->
        em.createQuery(
            "select r from Registration r where r.student.id = :studentId and"+
                " r.course.id = :courseId", Registration.class)
            .setParameter("studentId",student.getId())
            .setParameter("courseId",cousre.getId())
            .getResultList().size()>0);
  }

  public Course getCourse(int id) {
    return executeInDB(em->em.find(Course.class, id));
  }



  public List<Task> getTasksByCourse(int courseId) {
    return executeInDB(em->em.
        createQuery("select t from Task t where t.id.courseId = :courseId", Task.class)
        .setParameter("courseId",courseId).getResultList());
  }

  public void saveTasks(List<Task> tasks) {
    executeInTransaction((em, t)->{
      for (Task task: tasks){
        em.persist(task);
      }
      return 0;
    });
  }

  public boolean saveFile(int courseId, int taskId, FileObject file) {
    return executeInTransaction((em,t)->
        {
          List<Task> tasks=em.
              createQuery("select t from Task t where t.id.courseId = :courseId and t.id.taskId = :taskId", Task.class)
              .setParameter("courseId",courseId)
              .setParameter("taskId",taskId).getResultList();
          Task task = singleOrNull(tasks);
          if (task == null){
            return false;
          }
          task.setFile(file);
          em.persist(task);
          return true;
        }
    );
  }
  public FileObject getFile(int courseId, int taskId) {
    return executeInDB(em->
        {
          List<Task> tasks=em.
              createQuery("select t from Task t where t.id.courseId = :courseId and t.id.taskId = :taskId", Task.class)
              .setParameter("courseId",courseId)
              .setParameter("taskId",taskId).getResultList();
          //todo: handle non 1
          Task task = tasks.get(0);
          return task.getFile();
        }
    );
  }

  public void addSubmission(int courseId,Task task, String studentId, FileObject fileObject) {
    SubmissionID submissionID=new SubmissionID();
    submissionID.courseId=courseId;
    submissionID.taskId=task.getId().taskId;
    submissionID.studentId=studentId;
    Submission submission=new Submission();
    submission.setId(submissionID);
    submission.setSubmitDate(new Date());
    submission.setFile(fileObject);
    submission.setTask(task);
    executeInTransaction((em,t)->{
      em.persist(submission);
      return 0;
    });
  }

  public Task getTask(int courseId, int taskId) {
    return executeInDB(em->
        {
          List<Task> tasks=em.
              createQuery("select t from Task t where t.id.courseId = :courseId and t.id.taskId = :taskId", Task.class)
              .setParameter("courseId",courseId)
              .setParameter("taskId",taskId).getResultList();
          //todo handle non 1
          return tasks.get(0);
        }
    );
  }
  public List<Submission> getSubmissions(int courseId, int taskId) {
    return executeInDB(em->em.
        createQuery("select s from Submission s where s.id.courseId = :courseId and s.id.taskId = :taskId", Submission.class)
        .setParameter("courseId",courseId)
        .setParameter("taskId",taskId).getResultList());
  }
  private Submission getSubmission(int courseId, int taskId, String studentId, EntityManager em) {
    return em.
        createQuery("select s from Submission s where s.id.courseId = :courseId and"+
            " s.id.taskId = :taskId and s.id.studentId = :studentId", Submission.class)
        .setParameter("courseId",courseId)
        .setParameter("taskId",taskId)
        .setParameter("studentId",studentId).getResultList().get(0);//handle non exist?
  }

  public Submission getSubmission(int courseId, int taskId, String studentId) {
    return executeInDB(em->getSubmission(courseId, taskId, studentId,em));
  }

  public void addFeedBackFileToSubmission(int courseId, int taskId, String studentId,FileObject feedbackFile) {
    executeInTransaction((em,t)->
        {
          Submission submission=getSubmission(courseId, taskId, studentId,em);
          submission.setFeedbackFile(feedbackFile);
          em.persist(submission);
          return 0;
        }
    );
  }
  public void addGradeToSubmission(int courseId, int taskId, String studentId,int grade) {
    executeInTransaction((em,t)->
        {
          Submission submission=getSubmission(courseId, taskId, studentId,em);
          submission.setGrade(grade);
          em.persist(submission);
          return 0;
        }
    );
  }

  private <T> T singleOrNull(List<T> list){
    if (list.size() ==0 ){
      return null;
    }
    if (list.size()==1){
      return list.get(0);
    }
    throw new RuntimeException("list contains more than one item, "+
        "expected to have maximum one item by db constraints");
  }
}
