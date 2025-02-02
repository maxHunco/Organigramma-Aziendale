package classi;

import java.io.ObjectStreamException;
import java.sql.SQLException;
import java.util.*;
import classi.db.dbHandler;

public class OrgController {
    private static OrgController organigramma;
    private WorkUnit root;
    private List<Observer> obs;

    public OrgController() {
        root = new WorkUnit("root");
        root.setRoot(new WorkUnit(""));
        obs = new LinkedList<>();
    }

    //Gestione della creazione oggetto Singleton
    public synchronized static OrgController getInstance() {
        if (organigramma == null) {
            synchronized (OrgController.class) {
                if (organigramma == null) {
                    organigramma = new OrgController();
                }
            }
        }
        return organigramma;
    }

    private Object readResolve() throws ObjectStreamException {
        return getInstance(); // Restituisce l'istanza esistente dell'oggetto singleton
    }

    public WorkUnit getRoot() {
        return root;
    }

    private void addObserver(Observer observer) {
        obs.add(observer);
    }

    private void removeObserver(Observer observer) {
        obs.remove(observer);
    }

    private void notifyObservers() {
        for (Observer observer : obs) {
            observer.update();
        }
    }

    public void addWorkUnit(String parentName, String unitName) {
        WorkUnit parent = (WorkUnit) findByName(root, parentName);
        if (parent != null) {
            WorkUnit child = new WorkUnit(unitName);
            child.setRoot(parent);
            parent.add(child);
            addObserver((Observer) child);
            notifyObservers();
            try {
                dbHandler.saveWorkUnit(child);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Parent unit not found.");
        }
    }

    public void addEmployee(String workUnitName, String employeeName, String roleName) {
        WorkUnit workUnit = (WorkUnit) findByName(root, workUnitName);
        if (workUnit == null) {
            System.out.println("Work Unit not found.");
            return;
        }
        boolean roleOk = false;
        for(Role r : workUnit.getRoles()){
            if(r.getName().equals(roleName)){
                roleOk = true;
            }
        }
        if (!roleOk) {
            System.out.println("Role not found in the specified Work Unit.");
            return;
        }
        Role role = workUnit.getRole(roleName);
        Employee employee = new Employee(employeeName);
        employee.setRoot(workUnit);
        employee.addRole(role);
        workUnit.add(employee);
        addObserver((Observer) employee);
        notifyObservers();
        try {
            dbHandler.saveEmployee(employee, employee.getRoot());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void addRoleToWorkUnit(String workUnitName, String roleName) {
        WorkUnit workUnit = (WorkUnit) findByName(root, workUnitName);
        if (workUnit != null) {
            Role role = new Role(roleName);
            role.setRoot(workUnit);
            workUnit.addRole(role);
            try {
                LinkedList<Role> roles = new LinkedList<>();
                roles.add(role);
                dbHandler.saveRole(roles, workUnit);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Work Unit not found.");
        }
    }

    public void addRoleToEmployee(String employeeName, Role role, String root) {
        WorkUnit parent = (WorkUnit) findByName(this.root,root);
        Employee employee = findEmployee(parent,employeeName);
        System.out.println(employee.toString());
        if (employee != null) {
            employee.addRole(role);
            try {
                LinkedList<Role> roles = new LinkedList<>();
                roles.add(role);
                dbHandler.saveEmployeeRoles(roles, employeeName);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Role> getRolesByWorkUnit(String workUnitName) {
        WorkUnit workUnit = (WorkUnit) findByName(root, workUnitName);
        return workUnit != null ? workUnit.getRoles() : new ArrayList<>();
    }

    public Component findByName(Component component, String name) {
        if (component.getName().equals(name)) {
            return component;
        }
        if (component instanceof WorkUnit) {
            for (Component child : component.getChildren()) {
                Component found = findByName(child, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public void removeEmployee(WorkUnit unit, String employeeName, String root) {
        WorkUnit parent = (WorkUnit) findByName(this.root,root);
        Employee employee = findEmployee(parent,employeeName);
        unit.remove(employee);
        removeObserver((Observer) employee);
        notifyObservers();
        dbHandler.deleteEmployee(employee);
    }

    public void removeWorkUnit(String workUnitName) {
        WorkUnit child = (WorkUnit) findByName(root, workUnitName);
        if (child != null) {
            child.getRoot().getChildren().remove(child);
            removeObserver((Observer) child);
            notifyObservers();
            dbHandler.deleteWorkUnit(child);
        }
    }

    public void updateWorkUnit(String currentName, String newName) {
        WorkUnit unit = (WorkUnit) findByName(root, currentName);
        if (unit != null && unit instanceof WorkUnit) {
            unit.setName(newName);
            dbHandler.updateWorkUnit(currentName, newName);
        } else {
            System.out.println("Work Unit not found.");
        }
        notifyObservers();
    }

    public void updateEmployee(String employeeName, String newEmployeeName, String root) {
        WorkUnit parent = (WorkUnit) findByName(this.root,root);
        Employee employee = findEmployee(parent, employeeName);
        if (employee != null && employee instanceof Employee) {
            employee.setName(newEmployeeName);
            dbHandler.updateEmployee(employeeName, newEmployeeName, root);
        } else {
            System.out.println("Employee or Role not found.");
        }
        notifyObservers();
    }

    public void updateRole(String currentRoleName, String newRoleName) {
        Role role = findRoleByName(root, currentRoleName);
        if (role != null){
            role.setName(newRoleName);
            notifyObservers();
            dbHandler.updateRole(currentRoleName, newRoleName, role.getRoot().getName());
        }
        else {
            System.out.println("Role not found.");
        }

    }

    public Role findRoleByName(WorkUnit root, String currentRoleName) {
        // Ottieni la lista di figli della WorkUnit corrente
        List<Component> children = root.getChildren();
        for (Component child : children) {
            // Se il figlio è una WorkUnit, cerca nei suoi ruoli e figli
            if (child instanceof WorkUnit) {
                WorkUnit workUnit = (WorkUnit) child;

                // Cerca nei ruoli della WorkUnit corrente
                List<Role> roles = workUnit.getRoles();
                for (Role role : roles) {
                    if (role.getName().equals(currentRoleName)) {
                        return role; // Ruolo trovato
                    }
                }
                // Cerca ricorsivamente nei figli della WorkUnit
                Role foundRole = findRoleByName(workUnit, currentRoleName);
                if (foundRole != null) {
                    return foundRole; // Ruolo trovato nella ricorsione
                }
            }
        }
        // Nessun ruolo trovato nella WorkUnit corrente o nei suoi figli
        return null;
    }



    public static List<WorkUnit> getAllWorkUnits(Component component) {
        List<WorkUnit> workUnits = new ArrayList<>();

        // Se il componente è una WorkUnit, aggiungila alla lista
        if (component instanceof WorkUnit) {
            workUnits.add((WorkUnit) component);

            // Ora attraversa i figli di questa WorkUnit
            for (Component child : component.getChildren()) {
                workUnits.addAll((Collection<? extends WorkUnit>) getAllWorkUnits(child));  // Chiamata ricorsiva per i figli
            }
        }

        return workUnits;
    }

    public static Employee findEmployee(WorkUnit root, String name) {
        for(Employee employee : root.getEmployees()){
            if(employee.getName().equals(name)){
                return employee;
            }
        }
        return null;
    }
}
