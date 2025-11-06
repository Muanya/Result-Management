package mgt.result.sage.repository;


import mgt.result.sage.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    @Query("""
        SELECT s FROM CourseEnrollment e
        JOIN e.students s
        WHERE e.id = :enrollmentId
    """)
    List<Student> findStudentsByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Query("""
        SELECT s FROM Student s
        JOIN s.courses c
        WHERE c.id = :courseId
        AND s.id NOT IN (
            SELECT s2.id FROM CourseEnrollment e2
            JOIN e2.students s2
            WHERE e2.course.id = :courseId
        )
    """)
    List<Student> findStudentsWithoutEnrollmentInCourse(@Param("courseId") Long courseId);
}