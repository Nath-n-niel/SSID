package NewCode;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManagementSystemGUI extends JFrame {
    private JPanel centerPanel;
    private JButton addButton, deleteButton, updateButton, clearButton, switchButton, exitButton, searchButton;
    private DefaultTableModel tableModel;
    private static final String STUDENTS_CSV_FILE = "C://Temp//students.csv";
    private static final String COURSES_CSV_FILE = "C://Temp//courses.csv";
    private static String currentCSVFile = STUDENTS_CSV_FILE;
    static final String CSV_SEPARATOR = ",";
    public ManagementSystemGUI() {
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

        // Load initial data
        loadInitialData();

        // Display the frame
        setVisible(true);
    }

    private void createCenterPanel() {
        centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

 
        String[] columnNames = getColumnNames();
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
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
            if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
            addStudent();
            } else if(currentCSVFile.equals(COURSES_CSV_FILE)) {
            addCourse();
            }
        }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                    deleteStudent();
                } else if(currentCSVFile.equals(COURSES_CSV_FILE)) {
                    deleteCourse();
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            if(currentCSVFile.equals(STUDENTS_CSV_FILE))
                {
                    updateDataStudent();
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearData();
            }
        });

        switchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchSystem();
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
                        // Prompt the user to enter the student ID to search
                    String studentId = JOptionPane.showInputDialog("Enter Student ID to Search:");
                    if (studentId != null && !studentId.isEmpty()) {
                        searchStudent(studentId);
        }
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
    private String[] getColumnNames() {
        if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
            return new String[]{"Name", "ID", "Year", "Gender", "Course", "Enrollment Status"};
        } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
            return new String[]{"Course Name", "Course Code"};
        }
        return new String[0];
    }


    private void loadInitialData() {
        if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
            loadStudents();
        } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
            loadCourses();
        }
    }

    private static boolean isStudentEnrolled(Student student, List<Course> courses) {
        if (!student.getCourse().isEmpty()) {
            for (Course course : courses) {
                if (student.getCourse().equals(course.getCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<Student> loadStudents() {
       tableModel.setRowCount(0); // Clear existing rows
    List<Course> courses = loadCourses(); // Load courses
    try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_CSV_FILE))) {
        String line;
        reader.readLine(); // Skip header
    
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                Student student = new Student(parts[0], parts[1], parts[2], parts[3], parts[4]);

                // Check if the student is enrolled in any course
                boolean isEnrolled = isStudentEnrolled(student, courses);
                String enrollmentStatus = isEnrolled ? "Enrolled" : "Course not Available";
                // Add the enrollment status to the CSV string
                //String csvStringWithEnrollment = parts[4] + "," + enrollmentStatus;
                String[] rowData = Arrays.copyOf(parts, parts.length + 1);
                // student.add(new Student(parts[0], parts[1], parts[2], parts[3], csvStringWithEnrollment));

                rowData[rowData.length - 1] = enrollmentStatus;
                // Add the row to the table model
                tableModel.addRow(rowData);
                
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
        
    }

    // private List<Course> loadCourses() {
    //     tableModel.setRowCount(0); // Clear existing rows
    //     try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_CSV_FILE))) {
    //         String line;
    //         reader.readLine(); // Skip header
    //         while ((line = reader.readLine()) != null) {
    //             String[] parts = line.split(",");
    //             tableModel.addRow(parts);
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    private List<Course> loadCourses() {
        tableModel.setRowCount(0);
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(CSV_SEPARATOR);
                if (parts.length >= 2) {
                    courses.add(new Course(parts[0], parts[1]));
                }
            if(currentCSVFile.equals(COURSES_CSV_FILE))
                {tableModel.addRow(parts);}
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    private void addStudent() {
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
    
            // Check if the entered ID already exists
            if (checkIdExists(id)) {
                JOptionPane.showMessageDialog(null, "ID already exists. Please enter a unique ID.");
                return; // Exit the method without adding the student
            }
    
        //    // Assign null to empty fields
        //     name = name.isEmpty() ? "none" : name;
        //     id = id.isEmpty() ? "none" : id;
        //     year = year.isEmpty() ? "none" : year;
        //     gender = gender.isEmpty() ? "none" : gender;
        //     course = course.isEmpty() ? "none" : course;
        //     updateEnrollmentStatus();
            // Construct the input string
            String input = name + "," + id + "," + year + "," + gender + "," + course;
            if (!input.isEmpty()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE, true))) {
                    writer.println(input);
                    System.out.println("Student added successfully.");
                    loadStudents(); // Refresh table after adding student
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
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
    

// Method to check if ID already exists in the CSV file
private boolean checkIdExists(String id) {
    try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_CSV_FILE))) {
        String line;
        reader.readLine(); // Skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            String existingId = parts[1].trim(); // Extract the ID from existing data
            if (existingId.equals(id)) {
                return true; // ID already exists
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return false; // ID not found
}


    private void addCourse() {
        JTextField nameField = new JTextField();
        JTextField codeField = new JTextField();
    
        Object[] fields = {
            "Course Name:", nameField,
            "Course Code:", codeField
        };
    
        int option = JOptionPane.showConfirmDialog(null, fields, "Enter Course Information", JOptionPane.OK_CANCEL_OPTION);
    
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim();
    
            String input = name + "," + code;
            if (!input.isEmpty()) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_CSV_FILE, true))) {
                    writer.println(input);
                    System.out.println("Course added successfully.");
                    loadCourses(); // Refresh table after adding course
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        updateEnrollmentStatus();
    }

    

    private void deleteCourse() {
        JTextField codeField = new JTextField();
    
        Object[] fields = {
            "Course Code:", codeField
        };
    
        int option = JOptionPane.showConfirmDialog(null, fields, "Enter Course Code to Delete", JOptionPane.OK_CANCEL_OPTION);
    
        if (option == JOptionPane.OK_OPTION) {
            String codeToDelete = codeField.getText().trim();
    
            if (!codeToDelete.isEmpty()) {
                List<Course> courses = loadCourses();
                List<Course> updatedCourses = new ArrayList<>();
    
                boolean found = false;
                for (Course course : courses) {
                    if (course.getCode().equals(codeToDelete)) {
                        found = true;
                    } else {
                        updatedCourses.add(course);
                    }
                }
    
                if (!found) {
                    JOptionPane.showMessageDialog(null, "Course with code '" + codeToDelete + "' not found.");
                    return;
                }
    
                try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_CSV_FILE))) {
                    writer.println("Course Name,Course Code");
                    for (Course updatedCourse : updatedCourses) {
                        writer.println(updatedCourse.toCSVString());
                    }
                    System.out.println("Course deleted successfully.");
                    loadCourses(); // Refresh table after deleting course
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        updateEnrollmentStatus();
    }

    private void deleteStudent() {
        // Create a new window panel for user input
        JTextField idField = new JTextField();
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Enter ID:"));
        inputPanel.add(idField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Enter ID to Delete Student", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Find and remove the row with the specified ID
            String idToDelete = idField.getText();
            deleteRow(STUDENTS_CSV_FILE, idToDelete);
            removeTableRow(idToDelete, 1);
        }
    }

    private void deleteRow(String filePath, String key) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(CSV_SEPARATOR);
                if ((currentCSVFile.equals(STUDENTS_CSV_FILE) && parts.length >= 2 && parts[1].equals(key)) ||
                        (currentCSVFile.equals(STUDENTS_CSV_FILE) && parts.length >= 2 && parts[2].equals(key))) {
                    continue; // Skip the line with the specified key
                }
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void removeTableRow(String key, int column) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, column).equals(key)) {
                tableModel.removeRow(i);
                break;
            }
        }
    }

        // private void deleteStudent() {
        //     JTextField idField = new JTextField();
        
        //     Object[] fields = {
        //         "Student ID:", idField
        //     };
        
        //     int option = JOptionPane.showConfirmDialog(null, fields, "Enter Student ID to Delete", JOptionPane.OK_CANCEL_OPTION);
        
        //     if (option == JOptionPane.OK_OPTION) {
        //         String idToDelete = idField.getText().trim();
        
        //         if (!idToDelete.isEmpty()) {
        //             List<Student> students = loadStudents();
        //             List<Student> updatedStudents = new ArrayList<>();
        
        //             boolean found = false;
        //             for (Student student : students) {
        //                 if (student.getId().equals(idToDelete)) {
        //                     found = true;
        //                 } else {
        //                     updatedStudents.add(student);
        //                 }
        //             }
        
        //             if (!found) {
        //                 JOptionPane.showMessageDialog(null, "Student with ID '" + idToDelete + "' not found.");
        //                 return;
        //             }
        
        //             try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE))) {
        //                 writer.println("Name,Id,Year,Gender,Course");
        //                 for (Student updatedStudent : updatedStudents) {
        //                     writer.println(updatedStudent.toCSVString());
        //                 }
        //                 System.out.println("Student deleted successfully.");
        //                 loadStudents(); // Refresh table after deleting student
        //             } catch (IOException e) {
        //                 e.printStackTrace();
        //             }
        //         }
        //     }
        // }

        // private void updateStudent() {
        //     // Ask which data will be edited
        //     String[] options = {"Name", "ID", "Year", "Gender", "Course"};
        //     String selectedOption = (String) JOptionPane.showInputDialog(null, "Select which data to update:", "Update Student",
        //             JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        
        //     if (selectedOption != null) {
        //         // Prompt for the new value
        //         String newValue = JOptionPane.showInputDialog("Enter new " + selectedOption + ":");
        
        //         if (newValue != null && !newValue.isEmpty()) {
        //             try {
        //                 // Read existing data from the table
        //                 int selectedRowIndex = table.getSelectedRow();
        //                 String currentData = (String) tableModel.getValueAt(selectedRowIndex, getColumnIndex(selectedOption));
        
        //                 // Update the table with the new value
        //                 tableModel.setValueAt(newValue, selectedRowIndex, getColumnIndex(selectedOption));
        
        //                 // Write the updated data to the CSV file
        //                 updateCSVFile(selectedRowIndex, getColumnIndex(selectedOption), newValue);
        
        //                 System.out.println(selectedOption + " updated successfully.");
        //             } catch (IOException e) {
        //                 e.printStackTrace();
        //             }
        //         }
        //     }
        // }
        
        // private int getColumnIndex(String columnName) {
        //     switch (columnName) {
        //         case "Name":
        //             return 0;
        //         case "ID":
        //             return 1;
        //         case "Year":
        //             return 2;
        //         case "Gender":
        //             return 3;
        //         case "Course":
        //             return 4;
        //         default:
        //             return -1; // Invalid column name
        //     }
        // }
        
        // private void updateCSVFile(int rowIndex, int columnIndex, String newValue) throws IOException {
        //     try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_CSV_FILE));
        //          PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE + ".tmp"))) {
        
        //         // Write the header
        //         writer.println("Name,Id,Year,Gender,Course");
        
        //         // Copy existing data from the original CSV file to a temporary file
        //         String line;
        //         int currentRow = 0;
        //         while ((line = reader.readLine()) != null) {
        //             if (currentRow == rowIndex) {
        //                 // Modify the line to reflect the updated value
        //                 String[] parts = line.split(",");
        //                 parts[columnIndex] = newValue;
        //                 line = String.join(",", parts);
        //             }
        //             writer.println(line);
        //             currentRow++;
        //         }
        //     }
        
        //     // Replace the original CSV file with the temporary file
        //     File originalFile = new File(STUDENTS_CSV_FILE);
        //     File tempFile = new File(STUDENTS_CSV_FILE + ".tmp");
        //     tempFile.renameTo(originalFile);
        // }

        public void updateDataStudent(){
            // Create a new window panel for user input
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        JTextField idField = new JTextField();
        inputPanel.add(new JLabel("Enter ID of the student to update:"));
        inputPanel.add(idField);
    
        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Enter Student ID to Update Information", JOptionPane.OK_CANCEL_OPTION);
    
        if (result == JOptionPane.OK_OPTION) {
            // Find the student with the specified ID
            String idToUpdate = idField.getText();
            int rowIndex = -1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 1).equals(idToUpdate)) {
                    rowIndex = i;
                    break;
                }
            }
    
            if (rowIndex != -1) {
                // Display the current information of the student
                String name = (String) tableModel.getValueAt(rowIndex, 0);
                String year = (String) tableModel.getValueAt(rowIndex, 2);
                String gender = (String) tableModel.getValueAt(rowIndex, 3);
                String course = (String) tableModel.getValueAt(rowIndex, 4);
    
                // Create a new window panel for editing
                JPanel editPanel = new JPanel(new GridLayout(5, 2));
                JTextField nameField = new JTextField(name);
                JTextField yearField = new JTextField(year);
                JTextField genderField = new JTextField(gender);
                JTextField courseField = new JTextField(course);
    
                editPanel.add(new JLabel("Name:"));
                editPanel.add(nameField);
                editPanel.add(new JLabel("Year:"));
                editPanel.add(yearField);
                editPanel.add(new JLabel("Gender:"));
                editPanel.add(genderField);
                editPanel.add(new JLabel("Course:"));
                editPanel.add(courseField);
    
                int editResult = JOptionPane.showConfirmDialog(null, editPanel,
                        "Edit Student Information", JOptionPane.OK_CANCEL_OPTION);
    
                if (editResult == JOptionPane.OK_OPTION) {
                    // Update the data in the table
                    tableModel.setValueAt(nameField.getText(), rowIndex, 0);
                    tableModel.setValueAt(yearField.getText(), rowIndex, 2);
                    tableModel.setValueAt(genderField.getText(), rowIndex, 3);
                    tableModel.setValueAt(courseField.getText(), rowIndex, 4);
    
                    // Update the data in the CSV file by overwriting the existing row
                    String[] rowData = {
                            nameField.getText(),
                            idToUpdate, // ID remains unchanged
                            yearField.getText(),
                            genderField.getText(),
                            courseField.getText()
                    };
                    updateDataInCSV(STUDENTS_CSV_FILE, rowData, rowIndex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Student with ID " + idToUpdate + " not found.");
            }
        }
        updateEnrollmentStatus();
        }
        
        private void updateDataInCSV(String filePath, String[] data, int rowIndex) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
                // Update the corresponding line in the list
                lines.set(rowIndex + 1, String.join(CSV_SEPARATOR, data));
                // Write the updated lines back to the file
                try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                    for (String updatedLine : lines) {
                        writer.println(updatedLine);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        

        private void clearData() {
            int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear all data?", "Clear Data", JOptionPane.YES_NO_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                try (PrintWriter writer = new PrintWriter(new FileWriter(currentCSVFile))) {
                    // Write only the header to the file
                    if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                        writer.println("Name,Id,Year,Gender,Course,Enrollment Status");
                        System.out.println("Student data cleared successfully.");
                    } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                        writer.println("Course Name,Course Code");
                        System.out.println("Course data cleared successfully.");
                    }
                    loadStudents(); // Refresh table after clearing data
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

   private void switchSystem() {
        currentCSVFile = currentCSVFile.equals(STUDENTS_CSV_FILE) ? COURSES_CSV_FILE : STUDENTS_CSV_FILE;
        updateColumnNames(); // Update column names
        loadInitialData(); // Reload
        
    }

    //a method to search for the student ID
private void searchStudent(String studentId) {
    // Loop through the rows in the table
    for (int i = 0; i < tableModel.getRowCount(); i++) {
        String id = (String) tableModel.getValueAt(i, 1); // Assuming student ID is in the second column (index 1)
        if (id.equals(studentId)) {
            // Retrieve student data
            String name = (String) tableModel.getValueAt(i, 0);
            String year = (String) tableModel.getValueAt(i, 2);
            String gender = (String) tableModel.getValueAt(i, 3);
            String course = (String) tableModel.getValueAt(i, 4);
            
            // Display student data in a new dialog
            StringBuilder message = new StringBuilder();
            message.append("Name: ").append(name).append("\n");
            message.append("ID: ").append(id).append("\n");
            message.append("Year: ").append(year).append("\n");
            message.append("Gender: ").append(gender).append("\n");
            message.append("Course: ").append(course);
            
            JOptionPane.showMessageDialog(null, message.toString(), "Student Information", JOptionPane.INFORMATION_MESSAGE);
            return; // Exit the method once the student data is displayed
        }
    }
    // If the student ID is not found, display a message
    JOptionPane.showMessageDialog(null, "Student ID '" + studentId + "' not found.");
}

    private void updateColumnNames() {
        String[] columnNames = getColumnNames();
        tableModel.setColumnIdentifiers(columnNames);
    }

    private void updateEnrollmentStatus() {
    List<Student> students = loadStudents();
    List<Course> courses = loadCourses();

    // Update enrollment status for each student
    for (Student student : students) {
        boolean isEnrolled = isStudentEnrolled(student, courses);
        String enrollmentStatus = isEnrolled ? "Enrolled" : "Not Enrolled";
        student.setEnrollmentStatus(enrollmentStatus);
    }

    // Save updated enrollment status in the CSV file
    try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE))) {
        writer.println("Name,Id,Year,Gender,Course,Enrollment Status");
        for (Student student : students) {
            writer.println(student.toCSVString());
        }
        System.out.println("Enrollment status updated and saved successfully.");
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManagementSystemGUI();
            }
        });
    }
}
