package openu.workshop.webservice;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import openu.workshop.webservice.db.JPAWrapper;
import openu.workshop.webservice.model.Course;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebserviceApplication {

	private static final  Boolean INIT_DB_MODE = Boolean.FALSE;

	private static void initDB() throws Exception {
			Course cource = new Course(2, "java");
			try(JPAWrapper jpaWrapper=new JPAWrapper()){
				EntityManager entityManager = jpaWrapper.getEntityManager();
				entityManager.getTransaction().begin();
				entityManager.persist(cource);
				entityManager.getTransaction().commit();
			}
	}

	public static void main(String[] args) throws Exception {
		if (INIT_DB_MODE){
			initDB();
		} else {
			SpringApplication.run(WebserviceApplication.class, args);
		}
	}
}
