package mgt.result.sage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "magister")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Magister extends User {
    private String specialization;

}