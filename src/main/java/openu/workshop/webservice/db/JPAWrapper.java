package openu.workshop.webservice.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JPAWrapper implements AutoCloseable {
  private final EntityManagerFactory entityManagerFactory;
  private final EntityManager entityManager;

  public JPAWrapper(){
    entityManagerFactory = Persistence.
        createEntityManagerFactory("default");
    entityManager = entityManagerFactory.createEntityManager();
  }

  public EntityManager getEntityManager() {
    return entityManager;
  }

  @Override
  public void close() throws Exception {
    entityManager.close();
    entityManagerFactory.close();
  }
}
