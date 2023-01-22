package openu.workshop.webservice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import openu.workshop.webservice.db.JPAWrapper;
import openu.workshop.webservice.model.Course;
import openu.workshop.webservice.model.Professor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebserviceApplication {

	private static final boolean INIT_DB_MODE = true;

	private static void initDB() throws Exception {
			try(JPAWrapper jpaWrapper=new JPAWrapper()){
				EntityManager entityManager = jpaWrapper.getEntityManager();
				entityManager.getTransaction().begin();
				entityManager.createQuery("DELETE FROM Course").executeUpdate();
				entityManager.createQuery("DELETE FROM Professor").executeUpdate();
				entityManager.createQuery("DELETE FROM Task").executeUpdate();

				Professor p1 = new Professor("123", "123pass");
				Professor p2 = new Professor("456", "456pass");
				entityManager.persist(p1);
				entityManager.persist(p2);
				entityManager.persist(new Course(1, "java", p1));
			  entityManager.persist(new Course(2, "python", p1));
				entityManager.persist(new Course(3, "history", p2));
				entityManager.persist(new Course(4, "theology", p2));
				entityManager.getTransaction().commit();
			}
	}

	public static void main(String[] args) throws Exception {
		if (INIT_DB_MODE){
			initDB();
			System.out.println("init DB passed!");
		} else {
			SpringApplication.run(WebserviceApplication.class, args);
		}
	}
}
