package classi;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class WorkUnit implements Component, Serializable {

    private String name;
    private WorkUnit root;
    private List<Component> children;
    private List<Role> roles; // Ruoli specifici di questa unità

    public WorkUnit(String name) {
        this.name = name;
        this.children = new LinkedList<Component>();
        this.roles = new LinkedList<Role>();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    @Override
    public WorkUnit getRoot() {
        return root;
    }

    @Override
    public void setRoot(WorkUnit root) {
        this.root = root;
    }

    @Override
    public void update() {

    }

    @Override
    public void add(Component component) {
        if(component instanceof Employee employee){
            children.add(employee);
        }
        else {
            WorkUnit workUnit = (WorkUnit) component;
            children.add(workUnit);
        }
    }

    @Override
    public void remove(Component component) {
        children.remove(component);
    }

    @Override
    public List<Component> getChildren() {
        return children;
    }

    @Override
    public List<Role> getRoles() {
        return roles;
    }

    public Role getRole(String roleName){
        for(Role role : roles){
            if(role.getName().equals(roleName)){
                return role;
            }
        }
        return null;
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
    public void display(int level) {
        String indent = " ".repeat(level * 2);
        System.out.println(indent + "Work Unit: " + getName());
        System.out.println(indent + "Roles: " + roles);
        for (Component child : children) {
            child.display(level + 1);
        }
    }

    public List<WorkUnit> getSubUnits() {
        List<WorkUnit> subUnits = new LinkedList<>();
        List<Component> child = getChildren();
        for(Component c : child){
            if(c instanceof WorkUnit){ subUnits.add((WorkUnit) c); }
        }
        return subUnits;
    }

    public List<Employee> getEmployees() {
        List<Employee> emp = new LinkedList<>();
        List<Component> child = getChildren();
        for(Component c : child){
            if(c instanceof Employee){ emp.add((Employee) c); }
        }
        return emp;
    }
}

