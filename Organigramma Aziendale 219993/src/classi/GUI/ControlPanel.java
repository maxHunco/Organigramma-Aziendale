package classi.GUI;

import classi.*;
import classi.Component;
import classi.db.dbHandler;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class ControlPanel extends JPanel {
    private dbHandler db = new dbHandler();
    private OrgController controller;
    private OrgTreeView treeView;

    public ControlPanel(OrgController controller, OrgTreeView treeView) {
        this.controller = controller;
        this.treeView = treeView;
        initializePanel();
    }

    private void initializePanel() {
        setLayout(new GridLayout(1, 4)); // Modificato il layout per aggiungere un'altra colonna

        // Bottone principale per Unit
        JButton unitButton = new JButton("Unità");
        JPopupMenu unitMenu = createPopupMenu(
                new String[]{"Aggiungi Unità", "Modifica Unità", "Rimuovi Unità"},
                new Runnable[]{this::addWorkUnit, this::editWorkUnit, this::removeWorkUnit}
        );
        unitButton.addActionListener(e -> showPopup(unitMenu, unitButton));
        add(unitButton);

        // Bottone principale per Employee
        JButton employeeButton = new JButton("Dipendente");
        JPopupMenu employeeMenu = createPopupMenu(
                new String[]{"Aggiungi Dipendente", "Modifica dipendente", "Rimuovi dipendente"},
                new Runnable[]{this::addEmployee, this::editEmployee, this::removeEmployee}
        );
        employeeButton.addActionListener(e -> showPopup(employeeMenu, employeeButton));
        add(employeeButton);

        // Bottone principale per Role
        JButton roleButton = new JButton("Ruoli");
        JPopupMenu roleMenu = createPopupMenu(
                new String[]{"Aggiungi Ruolo", "Modifica Ruolo", "Rimuovi Ruolo", "Aggiungi ruolo ad un Dipendente"},
                new Runnable[]{this::addRole, this::editRole, this::removeRole, this::addRoleToEmployee}
        );
        roleButton.addActionListener(e -> showPopup(roleMenu, roleButton));
        add(roleButton);

        // Bottone per Salvare l'Organigramma
        JButton saveButton = new JButton("Salva Organigramma");
        saveButton.addActionListener(e -> saveOrganigramma());
        add(saveButton);  // Aggiungi il pulsante al pannello
    }

    // Metodo che salva l'organigramma (lo stesso che abbiamo discusso prima)
    private void saveOrganigramma() {
        try {
            db.saveOrganigramma();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        JOptionPane.showMessageDialog(this, "Organigramma salvato con successo!");
    }

    // Metodo per creare un popup menu
    private JPopupMenu createPopupMenu(String[] items, Runnable[] actions) {
        JPopupMenu popupMenu = new JPopupMenu();
        for (int i = 0; i < items.length; i++) {
            String itemName = items[i];
            Runnable action = actions[i];
            JMenuItem menuItem = new JMenuItem(itemName);
            menuItem.addActionListener(e -> action.run());
            popupMenu.add(menuItem);
        }
        return popupMenu;
    }

    // Metodo per mostrare il popup
    private void showPopup(JPopupMenu popupMenu, JButton button) {
        int x = 0; // Posizione orizzontale
        int y = -popupMenu.getPreferredSize().height; // Posizione verticale
        popupMenu.show(button, x, y);
    }


    private void addWorkUnit() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        String unitName = JOptionPane.showInputDialog("Nome dell'unità lavorativa:");
        if (unitName == null || unitName.isEmpty()) {
            JOptionPane.showMessageDialog(this,"Nome unità richiesto");
            return;
        }
        if (root != null && unitName != null) {
            controller.addWorkUnit(root, unitName);
            treeView.refreshTree();
        }
    }

    private void addEmployee() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        List<Role> roles = controller.getRolesByWorkUnit(root);
        if (roles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Non ci sono ruoli per questa unità");
            return;
        }
        String employeeName = JOptionPane.showInputDialog("Nome Dipendente:");
        if (employeeName == null || employeeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome richiesto.");
            return;
        }

        String[] roleNames = roles.stream().map(Role::getName).toArray(String[]::new);
        String selectedRole = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Ruolo:",
                "Role Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                roleNames,
                roleNames[0]
        );
        if (selectedRole == null || selectedRole.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ruolo richiesto.");
            return;
        }
        controller.addEmployee(root, employeeName, selectedRole);
        treeView.refreshTree();
    }


    private void addRole() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        String roleName = JOptionPane.showInputDialog("Nome Ruolo:");
        if (roleName == null || roleName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome Ruolo richiesto.");
        }
        if (root != null && roleName != null) {
            controller.addRoleToWorkUnit(root, roleName);
            treeView.refreshTree();
        }
    }

    private void addRoleToEmployee() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        String employeeName = JOptionPane.showInputDialog("Nome Dipendente :");
        if (employeeName == null || employeeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome Dipendente richiesto.");
            return;
        }
        WorkUnit parent = (WorkUnit) controller.findByName(controller.getRoot(),root);
        Employee employee = controller.findEmployee(parent,employeeName);
        List<Role> roles = controller.getRolesByWorkUnit(root);
        String[] roleNames = roles.stream().map(Role::getName).toArray(String[]::new);
        String selectedRole = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Ruolo:",
                "Role Selection",
                JOptionPane.QUESTION_MESSAGE,
                null,
                roleNames,
                roleNames[0]
        );
        if (selectedRole == null || selectedRole.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ruolo richiesto.");
            return;
        }
        WorkUnit father = employee.getRoot();
        for(Role r : father.getRoles()){
            if (r.getName().equals(selectedRole)) {
                controller.addRoleToEmployee(employee.getName(), new Role(selectedRole), root);
                treeView.refreshTree();
            }
        }
    }

    private void editWorkUnit() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String currentName = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità:",
                "Scelta Unità",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (currentName == null || currentName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità richiesto.");
            return;
        }
        String newName = JOptionPane.showInputDialog("Nuovo nome Unità:");
        if (currentName != null && newName != null) {
            controller.updateWorkUnit(currentName, newName);
            treeView.refreshTree();
        }
    }

    private void editEmployee() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        String employeeName = JOptionPane.showInputDialog("Nome Dipendente :");
        if (employeeName == null || employeeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome dipendente richiesto:");
            return;
        }
        String newEmployeeName = JOptionPane.showInputDialog("Nuovo nome Dipendente :");
        if (employeeName != null && newEmployeeName != null) {
            controller.updateEmployee(employeeName, newEmployeeName, root);
            treeView.refreshTree();
        }
    }

    private void editRole() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        String currentRoleName = JOptionPane.showInputDialog("Nome attuale Ruolo:");
        if (currentRoleName == null || currentRoleName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome ruolo richiesto.");
            return;
        }
        String newRoleName = JOptionPane.showInputDialog("Nuovo nome Ruolo:");
        if (currentRoleName != null && newRoleName != null) {
            controller.updateRole(currentRoleName, newRoleName);
            dbHandler.updateRole(currentRoleName, newRoleName,root);
            treeView.refreshTree();
        }
    }

    private void removeEmployee() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        String employeeName = JOptionPane.showInputDialog("Nome Dipendente :");
        if (employeeName == null || employeeName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Dipendente da eliminare richiesto.");
            return;
        }
        for (WorkUnit workUnit : controller.getAllWorkUnits(controller.getRoot())) {
            for (Component component : workUnit.getChildren()) {
                if (component instanceof Employee && ((Employee) component).getName().equals(employeeName)) {
                    controller.removeEmployee(workUnit,employeeName, root);
                    treeView.refreshTree();
                }
            }
        }
    }

    private void removeWorkUnit() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String workUnitName = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (workUnitName == null || workUnitName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        WorkUnit parent = (WorkUnit) controller.findByName(controller.getRoot(), workUnitName);
        if (parent != null) {
            controller.removeWorkUnit(workUnitName);
            // Refresh dell'interfaccia grafica
            treeView.refreshTree();
        }
    }

    private void removeRole() {
        List<WorkUnit> units = controller.getAllWorkUnits(controller.getRoot());
        String[] unitNames = units.stream().map(WorkUnit::getName).toArray(String[]::new);
        String root = (String) JOptionPane.showInputDialog(
                null,
                "Seleziona Unità Padre:",
                "Scelta Unità Padre",
                JOptionPane.QUESTION_MESSAGE,
                null,
                unitNames,
                unitNames[0]
        );
        if (root == null || root.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome unità padre richiesto.");
            return;
        }
        String roleName = JOptionPane.showInputDialog("Nome Ruolo:");
        WorkUnit parent = (WorkUnit) controller.findByName(controller.getRoot(), root);
        Role role = (Role) controller.findByName(controller.getRoot(), roleName);
        if (role != null) {
            parent.removeRole(role);
            dbHandler.deleteRole(role, parent);
            treeView.refreshTree();
        }
    }
}
