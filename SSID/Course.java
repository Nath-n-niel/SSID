package SSID;

public class Course {

    private String name;
    private String code;

    //course constructor
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
        return name + ManagementSystem.CSV_SEPARATOR + code;
    }
}
