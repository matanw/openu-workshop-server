package openu.workshop.webservice.model;

import java.util.Collection;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Student {

  @Id
  private String id;
  private String password; // todo: salt

  @OneToMany(mappedBy = "student")
  private Collection<Registration> registrations;

  public Student(){}

  public Student(String id, String password) {
    this.id = id;
    this.password = password;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Collection<Registration> getRegistrations() {
    return registrations;
  }

  public void setRegistrations(
      Collection<Registration> registrations) {
    this.registrations = registrations;
  }
}