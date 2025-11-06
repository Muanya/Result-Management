package mgt.result.sage.service;

import lombok.extern.slf4j.Slf4j;
import mgt.result.sage.dto.ResultDetail;
import mgt.result.sage.entity.Course;
import mgt.result.sage.entity.CourseEnrollment;
import mgt.result.sage.entity.Result;
import mgt.result.sage.repository.CourseEnrollmentRepository;
import mgt.result.sage.repository.CourseRepository;
import mgt.result.sage.repository.ResultRepository;
import mgt.result.sage.repository.StudentRepository;
import mgt.result.sage.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepo;

    @Autowired
    private CourseEnrollmentRepository courseEnrollmentRepo;

    @Autowired
    private StudentRepository studentRepo;

    @Autowired
    private CourseRepository courseRepo;

    @Autowired
    private Util util;

    @Transactional
    public ResultDetail saveResult(ResultDetail dto) {
        var student = studentRepo.findById(dto.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.getStudentId()));

        // Handle enrollment (optional)
        CourseEnrollment enrollment = null;
        Course course = null;


        if (dto.getEnrollmentId() != null) {
            enrollment = courseEnrollmentRepo.findById(dto.getEnrollmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Enrollment not found: " + dto.getEnrollmentId()));
            course = enrollment.getCourse(); // derive course from enrollment
        } else {
            // No enrollment → must attach to a course directly
            if (dto.getCourseId() == null) {
                throw new IllegalArgumentException("Course ID is required when enrollment is null.");
            }
            course = courseRepo.findById(dto.getCourseId())
                    .orElseThrow(() -> new IllegalArgumentException("Course not found: " + dto.getCourseId()));
        }

        // If a result already exists for this student+enrollment, update instead of creating duplicate
        Result result = null;

        if (dto.getId() != null) {
            result = resultRepo.findById(dto.getId()).orElse(new Result());
        } else if (enrollment != null) {
            result = resultRepo.findByStudentIdAndEnrollmentId(student.getId(), enrollment.getId())
                    .orElse(new Result());
        } else {
            result = resultRepo.findByStudentIdAndCourseIdAndEnrollmentIsNull(student.getId(), course.getId())
                    .orElse(new Result());
        }


        result.setGrade(dto.getGrade());
        result.setScore(dto.getScore());
        result.setStudent(student);
        result.setEnrollment(enrollment);

        var saved = resultRepo.save(result);

        dto.setId(saved.getId());

        return dto;
    }

    @Transactional
    public List<ResultDetail> saveAllResults(List<ResultDetail> results) {
        return results.stream().map(this::saveResult).toList();
    }

    public List<ResultDetail> getResultsByEnrollment(Long enrollmentId, Long courseId) {
        List<Result> results;
        if (courseId == null && enrollmentId == null) {
            throw new RuntimeException("One of EnrollmentId or CourseId should be passed");
        } else if (enrollmentId != null) {
            results = resultRepo.findByEnrollmentId(enrollmentId);
        } else {
            results = resultRepo.findByCourseIdAndEnrollmentIsNull(courseId);
        }
        return results.stream().map(this::buildResultDetails).toList();


    }

    public List<ResultDetail> getResultsByStudent(Long studentId) {
        var results = resultRepo.findByStudentId(studentId);
        if (results.isEmpty()) {
            throw new IllegalArgumentException("No results found for student " + studentId);
        }

        return results.stream().map(this::buildResultDetails).toList();
    }

    private ResultDetail buildResultDetails(Result result) {
        return ResultDetail.builder()
                .id(result.getId())
                .enrollmentId(result.getEnrollment().getId())
                .grade(result.getGrade())
                .score(result.getScore())
                .studentId(result.getStudent().getId())
                .build();

    }

    public List<ResultDetail> filterResultByStudent(List<Long> studentIds, List<ResultDetail> resultDetails) {
        Map<Long, ResultDetail> resultMap = resultDetails.stream()
                .collect(Collectors.toMap(ResultDetail::getStudentId, r -> r, (a, b) -> a));

        List<ResultDetail> resultDetailList = new ArrayList<>();

        for (Long studentId : studentIds) {
            ResultDetail result = resultMap.get(studentId);
            if (result != null) {
                resultDetailList.add(result);
            }
        }

        return resultDetailList;
    }

}