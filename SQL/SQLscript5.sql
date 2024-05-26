use testcode;
select *from students;
SET SQL_SAFE_UPDATES = 0;
UPDATE Students s
LEFT JOIN Courses c ON s.Course = c.courseCode
SET s.EnrollmentStatus = 
    CASE
        WHEN s.Course IS NULL THEN NULL
        WHEN c.courseCode IS NOT NULL THEN 'Enrolled'
        ELSE 'Course not available'
    END;