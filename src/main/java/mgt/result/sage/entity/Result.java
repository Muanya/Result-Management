package mgt.result.sage.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double score;
    private String grade;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(optional = true)
    @JoinColumn(name = "enrollment_id", nullable = true)
    private CourseEnrollment enrollment;

    @ManyToOne(optional = true)
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;
}
