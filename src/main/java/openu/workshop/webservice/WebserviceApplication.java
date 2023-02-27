package openu.workshop.webservice;

import java.util.Arrays;
import javax.persistence.Persistence;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;
import openu.workshop.webservice.model.Registration;
import openu.workshop.webservice.model.Student;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebserviceApplication {
	private static void initDB() throws Exception {

		var entityManagerFactory = Persistence.
				createEntityManagerFactory("default");
		var entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();
		entityManager.createQuery("DELETE FROM Submission ").executeUpdate();
		entityManager.createQuery("DELETE FROM Registration ").executeUpdate();
		entityManager.createQuery("DELETE FROM Course").executeUpdate();
		entityManager.createQuery("DELETE FROM Professor").executeUpdate();
		entityManager.createQuery("DELETE FROM Task").executeUpdate();
		entityManager.createQuery("DELETE FROM Student").executeUpdate();

		Professor p1 = new Professor("1", "1p");
		Professor p2 = new Professor("2", "2p");
		Course c1=new Course(1, "java", p1);
		Course c2=new Course(2, "python", p1);
		Course c3=new Course(3, "history", p2);
		Course c4=new Course(4, "theology", p2);
		Student s1 = new Student("3","3p");
		Registration r1=new Registration(s1,c1);
		for (Object o: Arrays.asList(p1,p2,c1,c2,c3,c4,r1,s1)){
			entityManager.persist(o);
		}
		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
		System.out.println("updated ok!");
	}
	//todo: fix "throw Exception" All over code
	public static void main(String[] args) throws Exception {
		initDB();//todo:delete
		SpringApplication.run(WebserviceApplication.class, args);
	}
}
