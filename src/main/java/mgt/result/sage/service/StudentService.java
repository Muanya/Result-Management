package mgt.result.sage.service;

import mgt.result.sage.entity.Student;
import mgt.result.sage.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepo;

    public Student addStudent(Student s) {
        return studentRepo.save(s);
    }

    public List<Student> getAllStudents() {
        return studentRepo.findAll();
    }
}
