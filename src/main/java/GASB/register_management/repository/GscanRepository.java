package GASB.register_management.repository;

import GASB.register_management.entity.Gscan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GscanRepository extends JpaRepository<Gscan, Long> {
}
