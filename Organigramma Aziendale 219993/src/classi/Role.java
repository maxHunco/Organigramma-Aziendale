package classi;

import java.io.Serializable;

public class Role implements Serializable {
    private String name;
    private WorkUnit root;

    public Role(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public WorkUnit getRoot() {
        return root;
    }

    public void setRoot(WorkUnit root) {
        this.root = root;
    }
}
