package mgt.result.sage.controller;

import mgt.result.sage.dto.ResultDetail;
import mgt.result.sage.dto.ResultEnrollmentRequest;
import mgt.result.sage.service.ResultService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping
    public ResultDetail saveResult(@RequestBody ResultDetail result) {
        return resultService.saveResult(result);
    }

    @PostMapping("/bulk")
    public List<ResultDetail> saveAllResults(@RequestBody List<ResultDetail> results) {
        return resultService.saveAllResults(results);
    }

    @GetMapping("/enrollment/{enrollmentId}")
    public List<ResultDetail> getResultsByEnrollment(@PathVariable Long enrollmentId) {
        return resultService.getResultsByEnrollment(enrollmentId);
    }

    @PostMapping("/enrollment")
    public List<ResultDetail> getResultsByEnrollment(@RequestBody ResultEnrollmentRequest req) {
        if (req == null) {
            throw new RuntimeException("Missing request body");
        }

        List<ResultDetail> resultDetails = resultService.getResultsByEnrollment(req.getEnrollmentId());
        return resultService.filterResultByStudent(req.getStudentIds(), resultDetails);
    }

    @GetMapping("/student/{studentId}")
    public List<ResultDetail> getResultsByStudent(@PathVariable Long studentId) {
        return resultService.getResultsByStudent(studentId);
    }
}