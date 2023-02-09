package openu.workshop.webservice.model;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

@Embeddable
public class FileObject {
  public String Name;

  @Lob
  public byte[] Data;

  public FileObject(){}

  public FileObject(String name, byte[] data) {
    Name = name;
    Data = data;
  }
}
