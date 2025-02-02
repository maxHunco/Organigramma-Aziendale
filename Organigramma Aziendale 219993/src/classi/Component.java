package classi;

import java.util.List;

public interface Component extends Observer{

    void add(Component component);
    void remove(Component component);
    List<Component> getChildren();
    void display(int level);
    List<Role> getRoles();
    void addRole(Role role);
    void removeRole(Role role);
    String getName();
    WorkUnit getRoot();
    void setRoot(WorkUnit root);
    void update();
    void setName(String newName);
}