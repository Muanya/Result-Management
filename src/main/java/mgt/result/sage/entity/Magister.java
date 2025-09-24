package mgt.result.sage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name = "magister")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Magister extends User {
    private String specialization;

    @ManyToMany(mappedBy = "magisters")
    private List<CourseEnrollment> enrollments;

}