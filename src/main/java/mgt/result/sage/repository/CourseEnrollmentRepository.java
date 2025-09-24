package mgt.result.sage.repository;

import mgt.result.sage.entity.CourseEnrollment;
import mgt.result.sage.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    List<CourseEnrollment> findByCourseId(Long studentId);
}


