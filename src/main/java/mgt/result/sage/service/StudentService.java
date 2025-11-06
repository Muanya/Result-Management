package mgt.result.sage.service;

import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.entity.Student;
import mgt.result.sage.repository.StudentRepository;
import mgt.result.sage.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private Util util;

    public Student addStudent(Student s) {
        return studentRepo.save(s);
    }

    public List<UserDetail> getAllStudents(Long courseId, Long enrollmentId) {
        List<Student> students;
        if (courseId == null && enrollmentId == null) {
            students = studentRepo.findAll();
        } else {
            students = getStudentsByCourseAndEnrollment(courseId, enrollmentId);
        }
        return util.getUserDetails(students);

    }


    public UserDetail getStudentById(Long id) {
        Student student = studentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
        return util.getUserDetailFromUser(student);
    }


    public List<Student> getStudentsByCourseAndEnrollment(Long courseId, Long enrollmentId) {
        if (enrollmentId != null) {
            return studentRepo.findStudentsByEnrollmentId(enrollmentId);
        } else {
            return studentRepo.findStudentsWithoutEnrollmentInCourse(courseId);
        }
    }


}
