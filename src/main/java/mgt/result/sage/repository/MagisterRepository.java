package mgt.result.sage.repository;


import mgt.result.sage.entity.Magister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MagisterRepository  extends JpaRepository<Magister, Long> {
}
