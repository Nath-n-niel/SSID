package SSID;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManagementSystem {

    private static String currentCSVFile = "students.csv";
    private static final String STUDENTS_CSV_FILE = "C://Temp//students.csv";
    private static final String COURSES_CSV_FILE = "C://Temp//courses.csv";
    static final String CSV_SEPARATOR = ",";

    // listing of student data
    private static void listStudents() {
        List<Student> students = loadStudents();
        for (Student student : students) {
            System.out.println(student.toCSVString());
        }
    }
   
    // listing of course data
    private static void listCourses() {
        List<Course> courses = loadCourses();
        for (Course course : courses) {
            System.out.println(course.toCSVString());
        }
    }

    //switching to which data will be edited
    private static void switchSystem() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose which system to manage (1. Students, 2. Courses): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                currentCSVFile = STUDENTS_CSV_FILE;
                System.out.println("Now managing Student Information.");
                break;
            case 2:
                currentCSVFile = COURSES_CSV_FILE;
                System.out.println("Now managing Course Information.");
                break;
            default:
                System.out.println("Invalid choice. Defaulting to Student Information.");
                currentCSVFile = STUDENTS_CSV_FILE;
                break;
        }
    }

    //Initializing which data will be edited
    private static void initialize() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Choose which system to manage first (1. Students, 2. Courses): ");
        int initialChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (initialChoice) {
            case 1:
                currentCSVFile = STUDENTS_CSV_FILE;
                break;
            case 2:
                currentCSVFile = COURSES_CSV_FILE;
                break;
            default:
                System.out.println("Invalid choice. Exiting program.");
                System.exit(0);
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

                    //Checking for enrollment status
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
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter student information (Name, Id, Year, Gender, Course): ");
        String input = scanner.nextLine();
    
        String[] parts = input.split(CSV_SEPARATOR);
        if (parts.length >= 2) {
            String studentId = parts[1];
    
            // Check if the ID is already present
            List<Student> students = loadStudents();
            for (Student student : students) {
                if (student.getId().equals(studentId)) {
                    System.out.println("Error: Student with ID '" + studentId + "' already exists. Please enter unique ID.");
                    return; // Exit the function without adding the student
                }
            }
    
            // If the ID is not repeated, add the student
            try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE, true))) {
                writer.println(input);
                System.out.println("Student added successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Invalid input format. Please enter the data in the correct format.");
        }
    }

    private static void addCourse() {
        Scanner scanner = new Scanner(System.in);
    System.out.print("Enter course information (Name, Code): ");
    String input = scanner.nextLine();

    String[] parts = input.split(CSV_SEPARATOR);
    if (parts.length >= 2) {
        String courseCode = parts[1];

        // Check if the course code is already present
        List<Course> courses = loadCourses();
        for (Course course : courses) {
            if (course.getCode().equals(courseCode)) {
                System.out.println("Error: Course with code '" + courseCode + "' already exists. Please enter a unique code.");
                return; // Exit the function without adding the course
            }
        }

        // If the course code is not repeated, add the course
        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_CSV_FILE, true))) {
            writer.println(input);
            System.out.println("Course added successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        System.out.println("Invalid input format. Please enter the data in the correct format.");
    }
    }

    private static void updateStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the student ID to update: ");
        String idToUpdate = scanner.nextLine();

        List<Student> students = loadStudents();
        List<Student> updatedStudents = new ArrayList<>();

        for (Student student : students) {
            if (student.getId().equals(idToUpdate)) {
                System.out.print("Enter updated student information (Name, Id, Year, Gender, Course): ");
                String updatedInfo = scanner.nextLine();
                String[] parts = updatedInfo.split(CSV_SEPARATOR);
                if (parts.length >= 5) {
                    updatedStudents.add(new Student(parts[0], parts[1], parts[2], parts[3], parts[4]));
                } else {
                    System.out.println("Invalid input. No updates made.");
                    return;
                }
            } else {
                updatedStudents.add(student);
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE))) {
            writer.println("Name,Id,Year,Gender,Course");
            for (Student updatedStudent : updatedStudents) {
                writer.println(updatedStudent.toCSVString());
            }
            System.out.println("Student updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateCourse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the course code to update: ");
        String codeToUpdate = scanner.nextLine();

        List<Course> courses = loadCourses();
        List<Course> updatedCourses = new ArrayList<>();

        for (Course course : courses) {
            if (course.getCode().equals(codeToUpdate)) {
                System.out.print("Enter updated course information (Name, Code): ");
                String updatedInfo = scanner.nextLine();
                String[] parts = updatedInfo.split(CSV_SEPARATOR);
                if (parts.length >= 2) {
                    updatedCourses.add(new Course(parts[0], parts[1]));
                } else {
                    System.out.println("Invalid input. No updates made.");
                    return;
                }
            } else {
                updatedCourses.add(course);
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_CSV_FILE))) {
            writer.println("Course Name,Course Code");
            for (Course updatedCourse : updatedCourses) {
                writer.println(updatedCourse.toCSVString());
            }
            System.out.println("Course updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteStudent() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the student ID to delete: ");
        String idToDelete = scanner.nextLine();

        List<Student> students = loadStudents();
        List<Student> updatedStudents = new ArrayList<>();

        for (Student student : students) {
            if (!student.getId().equals(idToDelete)) {
                updatedStudents.add(student);
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(STUDENTS_CSV_FILE))) {
            writer.println("Name,Id,Year,Gender,Course");
            for (Student updatedStudent : updatedStudents) {
                writer.println(updatedStudent.toCSVString());
            }
            System.out.println("Student deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteCourse() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the course code to delete: ");
        String codeToDelete = scanner.nextLine();

        List<Course> courses = loadCourses();
        List<Course> updatedCourses = new ArrayList<>();

        for (Course course : courses) {
            if (!course.getCode().equals(codeToDelete)) {
                updatedCourses.add(course);
            }
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(COURSES_CSV_FILE))) {
            writer.println("Course Name,Course Code");
            for (Course updatedCourse : updatedCourses) {
                writer.println(updatedCourse.toCSVString());
            }
            System.out.println("Course deleted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void clearData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(currentCSVFile))) {
            // Write only the header to the file
            if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                writer.println("Name,Id,Year,Gender,Course");
            } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                writer.println("Course Name,Course Code");
            }
            System.out.println("Data in the CSV file has been cleared.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     public static void main(String[] args) {
        // Initializing
        initialize();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nManagement System");
            System.out.println("1. List Data");
            System.out.println("2. Add Data");
            System.out.println("3. Update Data");
            System.out.println("4. Delete Data");
            System.out.println("5. Clear Data");
            System.out.println("6. Switch System");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                        listStudents();
                    } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                        listCourses();
                    }
                    break;
                case 2:
                    if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                        addStudent();
                    } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                        addCourse();
                    }
                    break;
                case 3:
                    if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                        updateStudent();
                    } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                        updateCourse();
                    }
                    break;
                case 4:
                    if (currentCSVFile.equals(STUDENTS_CSV_FILE)) {
                        deleteStudent();
                    } else if (currentCSVFile.equals(COURSES_CSV_FILE)) {
                        deleteCourse();
                    }
                    break;
                case 5:
                    clearData();
                    break;
                case 6:
                    switchSystem();
                    break;
                case 7:
                    System.out.println("Exiting program. Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }
}
