package mgt.result.sage.service;


import lombok.extern.slf4j.Slf4j;
import mgt.result.sage.dto.ClassEnrollRequest;
import mgt.result.sage.dto.CourseData;
import mgt.result.sage.dto.EnrollmentDetail;
import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.entity.Course;
import mgt.result.sage.entity.CourseEnrollment;
import mgt.result.sage.entity.Student;
import mgt.result.sage.repository.CourseEnrollmentRepository;
import mgt.result.sage.repository.CourseRepository;
import mgt.result.sage.repository.MagisterRepository;
import mgt.result.sage.repository.StudentRepository;
import mgt.result.sage.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;
    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private MagisterRepository magisterRepo;

    @Autowired
    private CourseEnrollmentRepository enrollmentRepo;

    @Autowired
    private Util util;

    public List<CourseData> getAllCourses() {
        List<Course> courses = courseRepo.findAll();

        return courses.stream().map(util::getCourseData).toList();
    }

    @Transactional
    public String saveEnrollment(ClassEnrollRequest req) {
        var course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        var students = studentRepo.findAllById(req.getStudentIds());
        var magisters = magisterRepo.findAllById(req.getMagisterIds());

        var enrollment = CourseEnrollment.builder()
                .className(req.getEnrollmentName())
                .course(course)
                .students(students)
                .magisters(magisters)
                .startDate(req.getStartDate())
                .build();

        CourseEnrollment courseEnrollment = enrollmentRepo.save(enrollment);

        // save students under corresponding course
        if (course.getStudents() == null) {
            course.setStudents(new ArrayList<>());
        }

        for (var student : students) {
            if (!course.getStudents().contains(student)) {
                course.getStudents().add(student);
            }
        }

        courseRepo.save(course);

        return courseEnrollment.getClassName();
    }

    public EnrollmentDetail getCourseEnrollmentById(Long id) {
        CourseEnrollment enrollment = enrollmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        return getEnrollmentDetail(enrollment);

    }

    public List<UserDetail> getStudentsByCourseId(Long courseId) {
        List<Student> students = courseRepo.findAllStudentsByCourseId(courseId);
        return util.getUserDetails(students);

    }

    public List<EnrollmentDetail> getCourseEnrollmentByCourseId(Long courseId) {
        List<CourseEnrollment> enrollment = enrollmentRepo.findByCourseId(courseId);
        return enrollment.stream().map(this::getEnrollmentDetail).toList();

    }

    private EnrollmentDetail getEnrollmentDetail(CourseEnrollment enrollment) {

        List<UserDetail> magisters = util.getUserDetails(enrollment.getMagisters());
        List<UserDetail> students = util.getUserDetails(enrollment.getStudents());
        CourseData courseData = util.getCourseData(enrollment.getCourse());

        return EnrollmentDetail.builder()
                .id(enrollment.getId())
                .enrollmentName(enrollment.getClassName())
                .course(courseData)
                .magisters(magisters)
                .students(students)
                .startDate(enrollment.getStartDate())
                .build();
    }

    public CourseData getCourseById(Long id) {
        Course course = courseRepo.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
        return util.getCourseData(course);

    }
}
