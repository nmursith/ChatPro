package Model;

/**
 * Created by mmursith on 12/12/2015.
 */
public class Variable {
    private String ID;
    private String Name;

    public Variable(String ID, String name) {
        this.ID = ID;
        this.Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
