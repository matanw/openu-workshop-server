package openu.workshop.webservice.model;

import java.io.Serializable;
import javax.persistence.Embeddable;

@Embeddable
public class SubmissionID implements Serializable {
  public int courseId;
  public int taskId;
  public String studentId;
}
