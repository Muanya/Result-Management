package mgt.result.sage.repository;

import mgt.result.sage.entity.Course;
import mgt.result.sage.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT DISTINCT s FROM Student s " +
            "JOIN Result r ON r.student = s " +
            "WHERE r.course.id = :courseId " +
            "OR s IN (SELECT st FROM Course c " +
            "JOIN c.enrollments e " +
            "JOIN e.students st " +
            "WHERE c.id = :courseId)")
    List<Student> findAllStudentsByCourseId(@Param("courseId") Long courseId);
}
