package openu.workshop.webservice.db;

import javax.persistence.EntityManager;

public interface FuncWithEntityManager<T> {
  T call(EntityManager em);
}
