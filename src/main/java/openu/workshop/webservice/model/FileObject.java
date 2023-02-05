package openu.workshop.webservice.model;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class FileObject {
  public String Name;

  @Lob
  public byte[] Data;
}
