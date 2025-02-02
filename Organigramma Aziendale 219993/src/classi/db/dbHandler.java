package classi.db;

import classi.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class dbHandler {
    static List<WorkUnit> workUnits;
    static OrgController controller;
    static File file;
    static Connection conn;

    public dbHandler() {
        try {
            conn = dbConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadFromDatabase() {
        workUnits = new ArrayList<>();
        HashMap<String, WorkUnit> workUnitMap = new HashMap<>();

        workUnitMap.put("root", controller.getRoot());

        try (Connection conn = dbConnection.getConnection()) {
            // Carica le unità lavorative
            String workUnitQuery = "SELECT name, parent_name FROM work_units";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(workUnitQuery)) {

                while (rs.next()) {
                    String name = rs.getString("name");

                    WorkUnit workUnit = new WorkUnit(name);
                    workUnits.add(workUnit);
                    workUnitMap.put(name,workUnit);
//                    if(parentName.equals("root")){
//                        workUnit.setRoot(controller.getRoot());
//                        controller.getRoot().add(workUnit);
//                        workUnits.add(workUnit);
//                    }
//                    else {
//                        WorkUnit parent = workUnitMap.get(parentName);
//                        workUnit.setRoot(parent);
//                        parent.add(workUnit);
//                        workUnits.add(workUnit);
//                    }
//                    workUnitMap.put(name, workUnit);
                }
            }
            System.out.println(workUnitMap);
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(workUnitQuery)) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    String parentName = rs.getString("parent_name");
                    //System.out.println(name + " " + parentName);

                    WorkUnit current = workUnitMap.get(name);
                    WorkUnit parent = workUnitMap.get(parentName);
                    current.setRoot(parent);
                    parent.add(current);
                }
            }

            // Carica i ruoli
            String roleQuery = "SELECT name, workunit_name FROM roles";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(roleQuery)) {

                while (rs.next()) {
                    String name = rs.getString("name");
                    String workUnitName = rs.getString("workunit_name");
                    Role role = new Role(name);
                    WorkUnit workUnit = workUnitMap.get(workUnitName);
                    if (workUnit != null) {
                        role.setRoot(workUnit);
                        workUnit.addRole(role);
                    }
                }
            }

            // Carica i dipendenti
            String employeeQuery = "SELECT name, workunit_name FROM employees";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(employeeQuery)) {

                while (rs.next()) {
                    String name = rs.getString("name");
                    String workUnitName = rs.getString("workunit_name");

                    Employee employee = new Employee(name);
                    WorkUnit workUnit = workUnitMap.get(workUnitName);
                    if (workUnit != null) {
                        employee.setRoot(workUnit);
                        workUnit.add(employee);
                    }
                }
            }

            // Carica le associazioni dipendente-ruolo
            String employeeRoleQuery = "SELECT employee_name, role_name FROM employee_roles";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(employeeRoleQuery)) {

                while (rs.next()) {
                    String employeeName = rs.getString("employee_name");
                    String roleName = rs.getString("role_name");

                    Employee employee = findEmployeeByName(employeeName);
                    Role role = findRoleByName(roleName);

                    if (employee != null && role != null) {
                        employee.addRole(role);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadFromSQLDump(File sqlFile) {
        try (Connection connection = dbConnection.getConnection();
             BufferedReader reader = new BufferedReader(new FileReader(sqlFile))) {

            StringBuilder sqlStatement = new StringBuilder();
            String line;
            try (Statement stmt = connection.createStatement()) {
                while ((line = reader.readLine()) != null) {
                    // Ignora commenti e linee vuote
                    line = line.trim();
                    if (line.startsWith("--") || line.isEmpty()) {
                        continue;
                    }

                    // Accumula le query fino a trovare un punto e virgola
                    sqlStatement.append(line);
                    if (line.endsWith(";")) {
                        // Esegui l'istruzione SQL completa
                        stmt.execute(sqlStatement.toString());
                        sqlStatement.setLength(0); // Resetta il builder
                    }
                }
            }
            System.out.println("SQL Dump executed successfully.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.err.println("Error executing SQL Dump: " + e.getMessage());
        }
    }

    public OrgController inizializzaDB(File selectedFile) {
        file = selectedFile;
        try {
            dbHandler.loadFromSQLDump(file);
            controller = new OrgController();
            dbHandler.loadFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return controller;
    }


    public void saveOrganigramma() throws IOException, InterruptedException {
        List<String> command = new ArrayList<>();
        command.add("mysqldump");
        command.add("-h" + dbConnection.HOST);
        command.add("-p" + dbConnection.PORT);
        command.add("-u" + dbConnection.USER);
        command.add("-p" + dbConnection.PASSWORD);
        command.add(dbConnection.dbName);

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(new File(dbConnection.SAVEPATH));

        Process p = pb.start();
        int exitCode = p.waitFor();
        if (exitCode != 0) {
            System.err.println("Errore salvataggio organigramma : " + exitCode);
        }
        else{
            System.out.println("Salvataggio organigramma completato.");
        }
    }

    public static void saveWorkUnit(WorkUnit workUnit) throws SQLException {
        String insertWorkUnitSQL = "INSERT INTO work_units (name, parent_name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertWorkUnitSQL)) {
            stmt.setString(1, workUnit.getName());
            stmt.setString(2, workUnit.getRoot().getName());
            stmt.executeUpdate();
        }
    }

    public static void updateWorkUnit(String unit, String newName) {
        String query = "UPDATE work_units SET name = ? WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setString(2, unit);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteWorkUnit(WorkUnit unit) {
        String query = "DELETE FROM work_units WHERE name = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, unit.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveEmployee(Employee employee, WorkUnit root) throws SQLException {
        String insertEmployeeSQL = "INSERT INTO employees (name, workunit_name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertEmployeeSQL, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, employee.getName());
            stmt.setString(2, root.getName());
            stmt.executeUpdate();
        }
        if(!employee.getRoles().isEmpty()){
            saveEmployeeRoles(employee.getRoles(), employee.getName());
        }
    }

    public static void updateEmployee(String employeeName, String newEmployeeName, String root){
        String query = "UPDATE employees SET name = ? WHERE name = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newEmployeeName);
            stmt.setString(2, employeeName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteEmployee(Employee employee) {
        String query = "DELETE FROM employees WHERE name = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, employee.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void saveRole(List<Role> roles, WorkUnit root) throws SQLException {
        String insertEmployeeSQL = "INSERT INTO roles (name, workunit_name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertEmployeeSQL, Statement.RETURN_GENERATED_KEYS)) {
            for (Role role : roles) {
                stmt.setString(1, role.getName());
                stmt.setString(2, root.getName());
                stmt.addBatch();
            }
            stmt.executeUpdate();
        }
    }

    public static void updateRole(String current, String newName, String root) {
        String query = "UPDATE roles SET name = ? WHERE name = ? and workunit_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setString(2, current);
            stmt.setString(3, root);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteRole(Role role, WorkUnit parent) {
        String query = "DELETE FROM roles WHERE name = ? and workunit_name = ?";
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, role.getName());
            stmt.setString(2, parent.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveEmployeeRoles(List<Role> roles, String employeeName) throws SQLException {
        String insertEmployeeRoleSQL = "INSERT INTO employee_roles (employee_name, role_name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertEmployeeRoleSQL)) {
            for (Role role : roles) {
                stmt.setString(1, employeeName);
                stmt.setString(2, role.getName());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }


    private static Employee findEmployeeByName(String name) {
        for (WorkUnit workUnit : workUnits) {
            for (Employee employee : workUnit.getEmployees()) {
                if (employee.getName().equals(name)) {
                    return employee;
                }
            }
        }
        return null;
    }

    private static Role findRoleByName(String name) {
        for (WorkUnit workUnit : workUnits) {
            for (Role role : workUnit.getRoles()) {
                if (role.getName().equals(name)) {
                    return role;
                }
            }
        }
        return null;
    }

}
