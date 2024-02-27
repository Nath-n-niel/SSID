package SSID;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ManagementSystemGUI extends JFrame {

    private JTextArea outputArea;
    private JButton addButton;
    private JComboBox<String> systemSwitch;

    // CSV file paths
    private static String currentCSVFile = "students.csv";
    private static final String STUDENTS_CSV_FILE = "C://Temp//students.csv";
    private static final String COURSES_CSV_FILE = "C://Temp//courses.csv";
    private static final String CSV_SEPARATOR = ",";

    public static class Student {
        private String name;
        private String id;
        private String year;
        private String gender;
        private String course;

        public Student(String name, String id, String year, String gender, String course) {
            this.name = name;
            this.id = id;
            this.year = year;
            this.gender = gender;
            this.course = course;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        public String getYear() {
            return year;
        }

        public String getGender() {
            return gender;
        }

        public String getCourse() {
            return course;
        }

        public String toCSVString() {
            return name + CSV_SEPARATOR + id + CSV_SEPARATOR + year + CSV_SEPARATOR + gender + CSV_SEPARATOR + course;
        }
    }

    public static class Course {
        private String name;
        private String code;

        public Course(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }

        public String toCSVString() {
            return name + CSV_SEPARATOR + code;
        }
    }

    public ManagementSystemGUI() {
        super("Management System");

        // Panel for the center part (Output Table)
        outputArea = new JTextArea(20, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(new JLabel("Output Table"), BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel for the west part (Input Menu)
        addButton = new JButton("Add");
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.add(new JLabel("Input Menu"), BorderLayout.NORTH);
        westPanel.add(addButton, BorderLayout.CENTER);

        // Panel for the south part (Switch System)
        String[] systemOptions = {"Students", "Courses"};
        systemSwitch = new JComboBox<>(systemOptions);
        JPanel southPanel = new JPanel();
        southPanel.add(new JLabel("Switch System"));
        southPanel.add(systemSwitch);

        // Add panels to the frame
        add(centerPanel, BorderLayout.CENTER);
        add(westPanel, BorderLayout.WEST);
        add(southPanel, BorderLayout.SOUTH);

        // Set up the "Add" button action listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show a blank text box for user input
                String input = JOptionPane.showInputDialog("Enter information:");
        
                // Process the input as needed
                if (input != null && !input.isEmpty()) {
                    if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                        // Process student input
                        addStudent(input);
                    } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                        // Process course input
                        addCourse(input);
                    }
                }
        
                // Update the output
                updateOutput();
            }
        });

        

        // Set up the "Switch System" combo box action listener
        systemSwitch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch the system based on the selected item
                int selectedIndex = systemSwitch.getSelectedIndex();
                currentCSVFile = (selectedIndex == 0) ? STUDENTS_CSV_FILE : COURSES_CSV_FILE;
                updateOutput();
            }
        });

        // Initialize the system
        initialize();
        updateOutput(); // Update the initial output

        // Set up the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // Center the frame on the screen
        setVisible(true);
    }

    private void updateOutput() {
        // Implement the logic to update the output area based on the current system (students or courses)
        if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
            // Update the output for students
            List<Student> students = loadStudents();
            StringBuilder outputText = new StringBuilder();
            for (Student student : students) {
                outputText.append(student.toCSVString()).append("\n");
            }
            outputArea.setText(outputText.toString());
        } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
            // Update the output for courses
            List<Course> courses = loadCourses();
            StringBuilder outputText = new StringBuilder();
            for (Course course : courses) {
                outputText.append(course.toCSVString()).append("\n");
            }
            outputArea.setText(outputText.toString());
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

    private static List<Student> loadStudents() {
        List<Student> students = new ArrayList<>();
        List<Course> courses = loadCourses();
        try (BufferedReader reader = new BufferedReader(new FileReader(STUDENTS_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(CSV_SEPARATOR);
                if (parts.length >= 5) {
                    Student student = new Student(parts[0], parts[1], parts[2], parts[3], parts[4]);

                    // Checking for enrollment status
                    boolean isEnrolled = isStudentEnrolled(student, courses);
                    String enrollmentStatus = isEnrolled ? "Enrolled" : "Not Enrolled";

                    String csvStringWithEnrollment = parts[4] + CSV_SEPARATOR + enrollmentStatus;
                    students.add(new Student(parts[0], parts[1], parts[2], parts[3], csvStringWithEnrollment));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return students;
    }

    private static List<Course> loadCourses() {
        List<Course> courses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(COURSES_CSV_FILE))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(CSV_SEPARATOR);
                if (parts.length >= 2) {
                    courses.add(new Course(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    private static void addStudent() {
        String input = JOptionPane.showInputDialog("Enter student information (Name, Id, Year, Gender, Course):");
        addData(input, STUDENTS_CSV_FILE);
    }

    private static void addCourse() {
        String input = JOptionPane.showInputDialog("Enter course information (Name, Code):");
        addData(input, COURSES_CSV_FILE);
    }

    private static void addData(String input, String csvFile) {
        String[] parts = input.split(CSV_SEPARATOR);
        if (parts.length >= 2) {
            String code = (csvFile.equals(STUDENTS_CSV_FILE)) ? parts[1] : parts[0];

            // Check if the ID or code is already present
            List<String> existingCodes = loadCodes(csvFile);
            if (existingCodes.contains(code)) {
                JOptionPane.showMessageDialog(null, "Error: " + (csvFile.equals(STUDENTS_CSV_FILE) ?
                        "Student with ID '" + code + "' already exists." :
                        "Course with code '" + code + "' already exists.") +
                        " Please enter a unique ID or code.");
                return; // Exit the function without adding the data
            }

            // If the ID or code is not repeated, add the data
            try (PrintWriter writer = new PrintWriter(new FileWriter(csvFile, true))) {
                writer.println(input);
                JOptionPane.showMessageDialog(null, (csvFile.equals(STUDENTS_CSV_FILE) ? "Student" : "Course") +
                        " added successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter the data in the correct format.");
        }
    }

    private static List<String> loadCodes(String csvFile) {
        List<String> codes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(CSV_SEPARATOR);
                if (parts.length >= 2) {
                    codes.add(parts[(csvFile.equals(STUDENTS_CSV_FILE)) ? 1 : 0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return codes;
    }

    private void initialize() {
        String message = "Choose which system to manage first (1. Students, 2. Courses):";
        int initialChoice = getUserChoice(message, 2);

        switch (initialChoice) {
            case 1:
                currentCSVFile = STUDENTS_CSV_FILE;
                break;
            case 2:
                currentCSVFile = COURSES_CSV_FILE;
                break;
            default:
                JOptionPane.showMessageDialog(null, "Invalid choice. Exiting program.");
                System.exit(0);
        }
    }

    private int getUserChoice(String message, int maxChoice) {
        int choice;
        do {
            String choiceString = JOptionPane.showInputDialog(message);
            try {
                choice = Integer.parseInt(choiceString);
            } catch (NumberFormatException e) {
                choice = 0;
            }

            if (choice < 1 || choice > maxChoice) {
                JOptionPane.showMessageDialog(null, "Invalid choice. Please enter a number between 1 and " + maxChoice + ".");
            }
        } while (choice < 1 || choice > maxChoice);
        return choice;
    }
    private void addStudent(String input) {
        String[] parts = input.split(CSV_SEPARATOR);
        if (parts.length >= 5) {
            String studentId = parts[1];
    
            // Check if the ID is already present
            List<Student> students = loadStudents();
            for (Student student : students) {
                if (student.getId().equals(studentId)) {
                    JOptionPane.showMessageDialog(null, "Error: Student with ID '" + studentId + "' already exists. Please enter a unique ID.");
                    return; // Exit the function without adding the student
                }
            }
    
            // If the ID is not repeated, add the student
            try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE, true))) {
                writer.println(input);
                JOptionPane.showMessageDialog(null, "Student added successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter the data in the correct format.");
        }
    }
    private void addCourse(String input) {
        String[] parts = input.split(CSV_SEPARATOR);
        if (parts.length >= 2) {
            String courseCode = parts[1];
    
            // Check if the course code is already present
            List<Course> courses = loadCourses();
            for (Course course : courses) {
                if (course.getCode().equals(courseCode)) {
                    JOptionPane.showMessageDialog(null, "Error: Course with code '" + courseCode + "' already exists. Please enter a unique code.");
                    return; // Exit the function without adding the course
                }
            }
    
            // If the course code is not repeated, add the course
            try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_CSV_FILE, true))) {
                writer.println(input);
                JOptionPane.showMessageDialog(null, "Course added successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid input format. Please enter the data in the correct format.");
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
