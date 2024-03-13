import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManagementSystemGUI extends JFrame {
    private JPanel centerPanel;
    private JButton addButton, deleteButton, updateButton, clearButton, switchButton, exitButton;
    private boolean isStudentSystem = true;
    private DefaultTableModel tableModel;

    private static String currentCSVFile = "";
    private static final String STUDENTS_CSV_FILE = "C://Temp//students.csv";
    private static final String COURSES_CSV_FILE = "C://Temp//courses.csv";
    private static final String CSV_SEPARATOR = ",";

    public ManagementSystemGUI() {
        // Initialize frame
        setTitle("Management System GUI");
        setSize(800, 600);
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

        // Create table
        String[] studentColumnNames = {"Name", "ID", "Year", "Gender", "Course", "Enrollment Status"};
        String[] courseColumnNames = {"Course Name", "Course Code"};
        tableModel = new DefaultTableModel(studentColumnNames, 0);
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
        clearButton = new JButton("Clear");
        switchButton = new JButton("Switch");
        exitButton = new JButton("Exit");

        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStudentSystem) {
                    addStudent();
                } else {
                    addCourse();
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isStudentSystem) {
                    deleteStudent();
                } else {
                    deleteCourse();
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
            if(isStudentSystem)
                {updateDataStudent();}
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

        // Add buttons to west panel
        westPanel.add(addButton);
        westPanel.add(deleteButton);
        westPanel.add(updateButton);
        westPanel.add(clearButton);
        westPanel.add(switchButton);
        westPanel.add(exitButton);

        return westPanel;
    }

    private void loadInitialData() {
        // Load initial data based on the current system
        if (isStudentSystem) {
            loadCSVData(STUDENTS_CSV_FILE);
        } else if(!isStudentSystem) {
            loadCSVData(COURSES_CSV_FILE);
        }
    }

    private void loadCSVData(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(CSV_SEPARATOR);
                tableModel.addRow(parts);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // window panel for add student
    private void addStudent() {
        // Create a new window panel for user input
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        JTextField nameField = new JTextField();
        JTextField idField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField courseField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Year:"));
        inputPanel.add(yearField);
        inputPanel.add(new JLabel("Gender:"));
        inputPanel.add(genderField);
        inputPanel.add(new JLabel("Course:"));
        inputPanel.add(courseField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Enter Student Information", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Add the entered data to the table and CSV file
            String[] rowData = {nameField.getText(), idField.getText(),
                    yearField.getText(), genderField.getText(), courseField.getText()};
            tableModel.addRow(rowData);
            writeDataToCSV(STUDENTS_CSV_FILE, rowData);
        }
    }

    private void addCourse() {
        // Create a new window panel for user input
        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        JTextField courseNameField = new JTextField();
        JTextField courseCodeField = new JTextField();

        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(courseNameField);
        inputPanel.add(new JLabel("Course Code:"));
        inputPanel.add(courseCodeField);

        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Enter Course Information", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Add the entered data to the table and CSV file
            String[] rowData = {courseNameField.getText(), courseCodeField.getText()};
            tableModel.addRow(rowData);
            writeDataToCSV(COURSES_CSV_FILE, rowData);
        }
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

    private void deleteCourse() {
        // Create a new window panel for user input
        JTextField courseCodeField = new JTextField();
        JPanel inputPanel = new JPanel(new GridLayout(1, 2));
        inputPanel.add(new JLabel("Enter Course Code:"));
        inputPanel.add(courseCodeField);
    
        int result = JOptionPane.showConfirmDialog(null, inputPanel,
                "Enter Course Code to Delete Course", JOptionPane.OK_CANCEL_OPTION);
    
        if (result == JOptionPane.OK_OPTION) {
            // Find and remove the row with the specified Course Code
            String courseCodeToDelete = courseCodeField.getText();
            deleteRow(COURSES_CSV_FILE, courseCodeToDelete);
            removeTableRow(courseCodeToDelete, 1);
        }
    }
    
    

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

    public void clearData(){
        // clear data in the CSV
        isStudentSystem = !isStudentSystem;
    if(!isStudentSystem){
        currentCSVFile = STUDENTS_CSV_FILE;
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentCSVFile))) 
        {
            // Write only the header to the file
            if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                writer.println("Name,Id,Year,Gender,Course,Enrollment Status");
            }    
        }   catch (IOException e) {
                    e.printStackTrace();
                }
    }
    
    else 
        currentCSVFile = COURSES_CSV_FILE;
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentCSVFile))){
         if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                writer.println("Course Name,Course Code");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        // Clear all data in the table (only in the table not in the CSV)
        tableModel.setRowCount(0);
    }

    private void switchSystem() {
        // Switch between Student and Course systems
        isStudentSystem = !isStudentSystem;

        // Retain column names based on the system
        if (isStudentSystem) {
            String[] studentColumnNames = {"Name", "ID", "Year", "Gender", "Course", "Enrollment Status"};
            tableModel.setColumnIdentifiers(studentColumnNames);
        } else {
            String[] courseColumnNames = {"Course Name", "Course Code"};
            tableModel.setColumnIdentifiers(courseColumnNames);
        }

        // Load data for the switched system
        if (isStudentSystem) {
            clear();
            loadCSVData(STUDENTS_CSV_FILE);
        } else {
            clear();
            loadCSVData(COURSES_CSV_FILE);
        }
    }

    private void writeDataToCSV(String filePath, String[] data) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath, true))) {
            writer.println(String.join(CSV_SEPARATOR, data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRow(String filePath, String key) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(CSV_SEPARATOR);
                if ((isStudentSystem && parts.length >= 2 && parts[1].equals(key)) ||
                        (!isStudentSystem && parts.length >= 2 && parts[2].equals(key))) {
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManagementSystemGUI();
            }
        });
    }
}
