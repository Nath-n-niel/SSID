import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class GUI_SQL extends JFrame {
    private JPanel centerPanel;
    private JButton addButton, deleteButton, updateButton, clearButton, switchButton, exitButton, searchButton;
    private DefaultTableModel tableModel1, tableModel2;
    private JTable table1, table2;
    private Connection connection;
    private boolean isTable1Visible = true;
    

    public GUI_SQL() {
        // Initialize frame
        setTitle("Management System GUI");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create components
        createCenterPanel();
        createWestPanel();
        
        // Set layout
        setLayout(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);
        add(createWestPanel(), BorderLayout.WEST);

        // Initialize database connection
        initializeDBConnection();

        // Load initial data
        loadInitialData();

        // Display the frame
        setVisible(true);
    }

    private void initializeDBConnection() {
        try {
            // Change these values according to your database configuration
            String url = "jdbc:mysql://127.0.0.1:3306/testcode";
            String user = "root";
            String password = "hostpassword";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadInitialData() {
        loadTableData("students", tableModel1);
        loadTableData("courses", tableModel2);
    }

    private void loadTableData(String tableName, DefaultTableModel tableModel) {
        try (Statement stmt = connection.createStatement()) {
            String query = "SELECT * FROM " + tableName;
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Set column names
            String[] columnNames = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columnNames[i - 1] = rsmd.getColumnName(i);
            }
            tableModel.setColumnIdentifiers(columnNames);

            // Populate table with data
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new CardLayout());

        tableModel1 = new DefaultTableModel();
        table1 = new JTable(tableModel1);
        JScrollPane scrollPane1 = new JScrollPane(table1);

        tableModel2 = new DefaultTableModel();
        table2 = new JTable(tableModel2);
        JScrollPane scrollPane2 = new JScrollPane(table2);

        centerPanel.add(scrollPane1, "Table1");
        centerPanel.add(scrollPane2, "Table2");

        // Show the first table initially
        ((CardLayout) centerPanel.getLayout()).show(centerPanel, "Table1");
    }

    private JPanel createWestPanel() {
        JPanel westPanel = new JPanel();
        westPanel.setLayout(new GridLayout(6, 1));

        // Create buttons
        addButton = new JButton("Add");
        deleteButton = new JButton("Delete");
        updateButton = new JButton("Update");
        searchButton = new JButton("Search");
        switchButton = new JButton("Switch");
        clearButton = new JButton("Clear");
        exitButton = new JButton("Exit");
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {   
                if (isTable1Visible) {
                    addRecordStudent();
                } else {
                    addRecordCourse();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRecord();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRecord();
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearTable();
            }
        });

        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createUpdateEnrollmentStatusTrigger();
                switchOperation();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchRecord();
            }
        });

        // Add buttons to west panel
        westPanel.add(addButton);
        westPanel.add(deleteButton);
        westPanel.add(updateButton);
        westPanel.add(searchButton);
        westPanel.add(switchButton);
        westPanel.add(clearButton);
        westPanel.add(exitButton);

        return westPanel;
    }

    private void addRecordStudent() {
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField courseField = new JTextField();
    
        Object[] fields = {
            "Name:", nameField,
            "ID:", idField,
            "Year (1-4):", yearField,
            "Gender (Male/Female):", genderField,
            "Course:", courseField
        };
    
        int option = JOptionPane.showConfirmDialog(null, fields, "Enter Student Information", JOptionPane.OK_CANCEL_OPTION);
    
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String id = idField.getText().trim();
            String year = yearField.getText().trim();
            String gender = genderField.getText().trim();
            String course = courseField.getText().trim();
            // Validate year input
            if (!isValidYear(year)) {
               JOptionPane.showMessageDialog(null, "Invalid year. Please enter a value between 1 and 4.");
               return; // Exit the method without adding the student
           }
   
           // Validate gender input
           if (!isValidGender(gender)) {
               JOptionPane.showMessageDialog(null, "Invalid gender. Please enter 'Male' or 'Female'.");
               return; // Exit the method without adding the student
           }
   
           if(!id.matches("\\d{4}-\\d{4}")){
           
               JOptionPane.showMessageDialog(null, "Invalid IDNumver. Please enter ####-#### format");
               return;
           }

           // Assign null to empty fields
           name = name.isEmpty() ? "null" : name;
           id = id.isEmpty() ? "null" : id;
           year = year.isEmpty() ? "null" : year;
           gender = gender.isEmpty() ? "null" : gender;
           course = course.isEmpty() ? "null" : course;
           
           updateEnrollmentStatus(id);
           insertStudentRecord(id, name, year, gender, course);
           clearTable();
           loadInitialData();
        }

    }

    private void insertStudentRecord(String studentID, String Name, String YearLevel, String Gender, String Course) {
        // Check if the student ID already exists
        if (isStudentIDExists(studentID)) {
            JOptionPane.showMessageDialog(null, "ID already exists. Please enter a unique ID.");
            return; // Exit without inserting the record
        }
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO Students (studentID, Name, YearLevel, Gender, Course) VALUES (?, ?, ?, ?, ?)")) {
            pstmt.setString(1, studentID);
            pstmt.setString(2, Name);
            pstmt.setString(3, YearLevel);
            pstmt.setString(4, Gender);
            pstmt.setString(5, Course);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Student Added Successfully");
                // After adding the student, execute the SQL script to create the trigger
                // createUpdateEnrollmentStatusTrigger();
            } else {
                JOptionPane.showMessageDialog(null, "Adding Student Failed Try Again");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private boolean isStudentIDExists(String studentID) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM Students WHERE studentID = ?")) {
            pstmt.setString(1, studentID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0; // If count > 0, student ID exists
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Default to false if an exception occurs
    }

    private void addRecordCourse(){
        JTextField nameField = new JTextField();
        JTextField codeField = new JTextField();
    
        Object[] fields = {
            "Course Name:", nameField,
            "Course Code:", codeField
        };
    
        int option = JOptionPane.showConfirmDialog(null, fields, "Enter Course Information", JOptionPane.OK_CANCEL_OPTION);
    
        if (option == JOptionPane.OK_OPTION) {
            String courseName = nameField.getText().trim();
            String courseCode = codeField.getText().trim();

            // Check if the entered ID already exists
            if (checkCourseIdExists(courseCode)) {
                JOptionPane.showMessageDialog(null, "Course already exists. Please enter a unique Course Code.");
                return; // Exit the method without adding the course
            }
        insertCourseRecord(courseName,courseCode);
        clearTable();
        loadInitialData();
        
        JOptionPane.showMessageDialog(null, "Course Added Successfully");

        }
        
    }

    private void insertCourseRecord(String courseName, String courseCode) {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT INTO Courses (courseName, courseCode) VALUES (?, ?)")) {
            pstmt.setString(1, courseName);
            pstmt.setString(2, courseCode);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Course added successfully.");
                // After adding the student, execute the SQL script to create the trigger
                // createUpdateEnrollmentStatusTrigger();
            } else {
                System.out.println("Failed to add course.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to check if ID already exists in the CSV file
private boolean checkCourseIdExists(String courseCode) {
    try (PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM Students WHERE courseCode = ?")) {
        pstmt.setString(1, courseCode);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; // If count > 0, student ID exists
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false; // Default to false if an exception occurs
}

    private void deleteRecord() {
        // Implement logic to delete a record from the database
        // Example: Execute a DELETE SQL statement
        String deleteQuery = JOptionPane.showInputDialog("Enter Student ID or Course Code to delete:");
    
    if (deleteQuery != null && !deleteQuery.trim().isEmpty()) {
        deleteQuery = deleteQuery.trim();

        if (isTable1Visible) {
            deleteStudentRecord(deleteQuery);
        } else {
            deleteCourseRecord(deleteQuery);
        }
    }
    }

    private void deleteStudentRecord(String studentID) {
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Students WHERE studentID = ?")) {
            pstmt.setString(1, studentID);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Student deleted successfully.");
                clearTable();
                loadInitialData();
            } else {
                JOptionPane.showMessageDialog(null, "No student found with ID: " + studentID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void deleteCourseRecord(String courseCode) {
        try (PreparedStatement pstmt = connection.prepareStatement("DELETE FROM Courses WHERE courseCode = ?")) {
            pstmt.setString(1, courseCode);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Course deleted successfully.");
                clearTable();
                loadInitialData();
            } else {
                JOptionPane.showMessageDialog(null, "No course found with code: " + courseCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecord() {
        if (isTable1Visible) {
            // Updating student record
            String studentID = JOptionPane.showInputDialog(null, "Enter Student ID to update:");
            if (studentID != null && !studentID.trim().isEmpty()) {
                updateStudentRecord(studentID.trim());
            }
        } else {
            // Updating course record
            String courseCode = JOptionPane.showInputDialog(null, "Enter Course Code to update:");
            if (courseCode != null && !courseCode.trim().isEmpty()) {
                updateCourseRecord(courseCode.trim());
            }
        }
    }

    private void updateStudentRecord(String studentID) {
        JTextField nameField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField courseField = new JTextField();

        Object[] fields = {
            "Name:", nameField,
            "Year (1-4):", yearField,
            "Gender (Male/Female):", genderField,
            "Course:", courseField
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Update Student Information", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String year = yearField.getText().trim();
            String gender = genderField.getText().trim();
            String course = courseField.getText().trim();

            if (!isValidYear(year)) {
                JOptionPane.showMessageDialog(null, "Invalid year. Please enter a value between 1 and 4.");
                return;
            }

            if (!isValidGender(gender)) {
                JOptionPane.showMessageDialog(null, "Invalid gender. Please enter 'Male' or 'Female'.");
                return;
            }

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE Students SET Name = ?, YearLevel = ?, Gender = ?, Course = ? WHERE studentID = ?")) {
                pstmt.setString(1, name);
                pstmt.setString(2, year);
                pstmt.setString(3, gender);
                pstmt.setString(4, course);
                pstmt.setString(5, studentID);

                updateEnrollmentStatus(studentID);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(null, "Student Updated Successfully");
                    clearTable();
                    loadInitialData();
                } else {
                    JOptionPane.showMessageDialog(null, "Updating Student Failed. Try Again");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateCourseRecord(String courseCode) {
        JTextField nameField = new JTextField();

        Object[] fields = {
            "Course Name:", nameField,
        };

        int option = JOptionPane.showConfirmDialog(null, fields, "Update Course Information", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String courseName = nameField.getText().trim();

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE Courses SET courseName = ? WHERE courseCode = ?")) {
                pstmt.setString(1, courseName);
                pstmt.setString(2, courseCode);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    createUpdateEnrollmentStatusTrigger();
                    JOptionPane.showMessageDialog(null, "Course Updated Successfully");
                    clearTable();
                    loadInitialData();
                } else {
                    JOptionPane.showMessageDialog(null, "Updating Course Failed. Try Again");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void searchRecord() {
        // Implement logic to search records in the database
        // Example: Execute a SELECT SQL statement with a WHERE clause
        String searchQuery = JOptionPane.showInputDialog("Enter Student ID or Course Code:");
    
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            searchQuery = searchQuery.trim();
    
            if (isTable1Visible) {
                searchStudentRecord(searchQuery);
            } else {
                searchCourseRecord(searchQuery);
            }
        }
    }

    private void searchStudentRecord(String studentID) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Students WHERE studentID = ?")) {
            pstmt.setString(1, studentID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    displaySearchResults(rs);
                } else {
                    JOptionPane.showMessageDialog(null, "No student found with ID: " + studentID);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void searchCourseRecord(String courseCode) {
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Courses WHERE courseCode = ?")) {
            pstmt.setString(1, courseCode);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    displaySearchResults(rs);
                } else {
                    JOptionPane.showMessageDialog(null, "No course found with code: " + courseCode);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void displaySearchResults(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        StringBuilder result = new StringBuilder();
    
        for (int i = 1; i <= columnCount; i++) {
            result.append(rsmd.getColumnName(i)).append(": ").append(rs.getString(i)).append("\n");
        }
    
        JTextArea textArea = new JTextArea(result.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
    
        JOptionPane.showMessageDialog(null, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }



    private void clearTable() {
        if (isTable1Visible) {
            tableModel1.setRowCount(0);
        } else {
            tableModel2.setRowCount(0);
        }
    }

    private void switchOperation() {
        CardLayout cl = (CardLayout) (centerPanel.getLayout());
        if (isTable1Visible) {
            cl.show(centerPanel, "Table2");
        } else {
            cl.show(centerPanel, "Table1");
        }
        isTable1Visible = !isTable1Visible;
        clearTable();
        loadInitialData();
    }

  // Method to validate year input
  private boolean isValidYear(String year) {
    try {
        int yearValue = Integer.parseInt(year);
        return yearValue >= 1 && yearValue <= 4;
    } catch (NumberFormatException e) {
        return false;
    }
}

// Method to validate gender input
private boolean isValidGender(String gender) {
    return gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female");
}

private void createUpdateEnrollmentStatusTrigger() {
   try (PreparedStatement pstmt = connection.prepareStatement(
    "UPDATE Students s " +
    "LEFT JOIN Courses c ON s.Course = c.courseCode " +
    "SET s.EnrollmentStatus = CASE " +
    "WHEN s.Course IS NULL THEN NULL " +
    "WHEN c.courseCode IS NOT NULL THEN 'Enrolled' " +
    "ELSE 'Course not available' " +
    "END")) {
int rowsAffected = pstmt.executeUpdate();
System.out.println("Enrollment status updated for " + rowsAffected + " students.");
} catch (SQLException e) {
e.printStackTrace();
}
}

private void updateEnrollmentStatus(String studentID) {
    try (PreparedStatement pstmt = connection.prepareStatement(
            "SELECT s.Course, c.courseCode FROM Students s LEFT JOIN Courses c ON s.Course = c.courseName WHERE s.studentID = ?")) {
        pstmt.setString(1, studentID);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String studentCourse = rs.getString("Course");
                String courseCode = rs.getString("courseCode");
                String enrollmentStatus;
                
                if (studentCourse == null) {
                    enrollmentStatus = null;
                } else if (courseCode != null)
                enrollmentStatus = "Enrolled";
             else {
                enrollmentStatus = "Course not available";
            }

            try (PreparedStatement updateStmt = connection.prepareStatement(
                    "UPDATE Students SET EnrollmentStatus = ? WHERE studentID = ?")) {
                updateStmt.setString(1, enrollmentStatus);
                updateStmt.setString(2, studentID);
                updateStmt.executeUpdate();
            }
        }
    }
} catch (SQLException e) {
    e.printStackTrace();
}
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GUI_SQL();
            }
        });
    }
}
