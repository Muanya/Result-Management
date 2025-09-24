package mgt.result.sage.controller;

import mgt.result.sage.dto.UserDetail;
import mgt.result.sage.service.MagisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/magisters")
public class MagisterController {

    private static final Logger log = LoggerFactory.getLogger(MagisterController.class);

    @Autowired
    private MagisterService magisterService;


    @GetMapping
    public ResponseEntity<List<UserDetail>> getStudents() {
        return ResponseEntity.ok(magisterService.getAllStudents());

    }
}
