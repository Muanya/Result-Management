package mgt.result.sage.controller;

import mgt.result.sage.entity.Student;
import mgt.result.sage.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        log.info(student.toString());
        return studentService.addStudent(student);
    }

    @GetMapping
    public List<Student> getStudents() {
        log.info("Reached here");
        return studentService.getAllStudents();

    }

}
