package GASB.register_management.repository;

import GASB.register_management.entity.VtReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VtReportRepository extends JpaRepository<VtReport, Long> {
}
