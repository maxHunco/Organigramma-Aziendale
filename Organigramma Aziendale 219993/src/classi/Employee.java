package classi;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Employee implements Component, Serializable{

    private String name;
    private LinkedList<Role> roles;
    private WorkUnit root;

    public Employee(String name) {
        this.name = name;
        this.roles = new LinkedList<Role>();
    }

    @Override
    public void add(Component component) {
        throw new UnsupportedOperationException("Impossibile aggiungere Componenti ad un Dipendente");
    }

    @Override
    public void remove(Component component) {
        throw new UnsupportedOperationException("Impossibile rimuovere un Componente da un Dipendente");
    }

    @Override
    public List<Component> getChildren() {
        return null; // No children for employees
    }

    @Override
    public void display(int level) {
        String indent = " ".repeat(level * 2);
        System.out.println(indent + "Dipendente: " + getName() + " (" + this.roles + ")");
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public void addRole(Role role) {
        if(roles.contains(role)){ return; }
        roles.add(role);
    }

    @Override
    public void removeRole(Role role) {
        roles.remove(role);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public WorkUnit getRoot() {
        return root;
    }

    @Override
    public void setRoot(WorkUnit root) { this.root = root; }

    @Override
    public void update() {}

    public void setRoles(LinkedList<Role> roles) {
        this.roles = roles;
    }
}
