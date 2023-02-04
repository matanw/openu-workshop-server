package openu.workshop.webservice;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import openu.workshop.webservice.db.FuncWithEntityManager;
import openu.workshop.webservice.db.FuncWithEntityManagerAndTransaction;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;
import openu.workshop.webservice.model.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import javax.persistence.TypedQuery;

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
}
