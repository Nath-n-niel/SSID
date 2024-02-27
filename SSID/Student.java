package SSID;

public class Student {

    private String name;
    private String id;
    private String year;
    private String gender;
    private String course;

    // student constructor
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
        return name + ManagementSystem.CSV_SEPARATOR + id + ManagementSystem.CSV_SEPARATOR +
               year + ManagementSystem.CSV_SEPARATOR + gender + ManagementSystem.CSV_SEPARATOR + course;
    }

    
}
