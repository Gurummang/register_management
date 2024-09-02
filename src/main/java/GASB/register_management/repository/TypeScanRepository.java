package GASB.register_management.repository;

import GASB.register_management.entity.TypeScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeScanRepository extends JpaRepository<TypeScan, Long> {
}
