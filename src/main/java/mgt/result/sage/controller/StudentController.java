package mgt.result.sage.controller;

import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;


    @GetMapping
    public ResponseEntity<List<UserDetail>> getStudents(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Long enrollmentId
    ) {
        return ResponseEntity.ok(studentService.getAllStudents(courseId, enrollmentId));

    }

    @GetMapping("/{studentId}")
    public ResponseEntity<UserDetail> getStudentsById(@PathVariable Long studentId) {
        return ResponseEntity.ok(studentService.getStudentById(studentId));

    }


}
