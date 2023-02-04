package openu.workshop.webservice.db;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public interface FuncWithEntityManagerAndTransaction<T> {
  T call(EntityManager em, EntityTransaction t);
}
