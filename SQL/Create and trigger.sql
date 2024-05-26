USE testcode;

CREATE TABLE IF NOT EXISTS
Courses(
	courseName VARCHAR (50),
    courseCode VARCHAR (30) PRIMARY KEY
);
CREATE TABLE IF NOT EXISTS
Students(
	studentID VARCHAR(9) PRIMARY KEY,
    Name VARCHAR(30),
    YearLevel INT,
    Gender VARCHAR(9),
    Course VARCHAR(30),
    EnrollmentStatus VARCHAR(30)
    );
    
    -- Create trigger to update EnrollmentStatus
DELIMITER $$
CREATE TRIGGER UpdateEnrollmentStatus
AFTER INSERT ON Students
FOR EACH ROW
BEGIN
    DECLARE course_exists INT;
    SELECT COUNT(*) INTO course_exists FROM Courses WHERE courseName = NEW.Course AND courseCode = NEW.CourseCode;
    
    IF course_exists = 1 THEN
        UPDATE Students SET EnrollmentStatus = 'Enrolled' WHERE studentID = NEW.studentID;
    ELSE
        UPDATE Students SET EnrollmentStatus = 'Not Enrolled' WHERE studentID = NEW.studentID;
    END IF;
END$$
DELIMITER ;


