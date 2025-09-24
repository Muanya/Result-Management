package mgt.result.sage.controller;

import mgt.result.sage.dto.ClassEnrollRequest;
import mgt.result.sage.dto.CourseData;
import mgt.result.sage.dto.EnrollmentDetail;
import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.entity.Course;
import mgt.result.sage.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;


    @GetMapping
    public ResponseEntity<List<CourseData>> getCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());

    }

    @GetMapping("key/{id}")
    public ResponseEntity<CourseData> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));

    }



    @PostMapping("/enroll")
    public ResponseEntity<?> enrollClass(@RequestBody ClassEnrollRequest request) {
        String name = courseService.saveEnrollment(request);
        return ResponseEntity.ok(Map.of("message", name + " saved successfully!"));

    }

    @GetMapping("/enrollment/{id}")
    public EnrollmentDetail getEnrollmentById(@PathVariable Long id) {
        return courseService.getCourseEnrollmentById(id);
    }

    @GetMapping("/enrollmentsByCourse/{id}")
    public List<EnrollmentDetail> getCourseEnrollmentByCourseId(@PathVariable Long id) {
        // get enrollment by course id
        return courseService.getCourseEnrollmentByCourseId(id);
    }

    @GetMapping("/studentsByCourse/{id}")
    public List<UserDetail> getStudentsByCourseId(@PathVariable Long id) {
        // get students by course id
        return courseService.getStudentsByCourseId(id);
    }

}
