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

	//todo: fix "throw Exception" All over code
	//todo: rethink on JPAWrapper

	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebserviceApplication.class, args);
	}
}
