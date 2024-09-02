package GASB.register_management.repository;

import GASB.register_management.entity.DlpReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DlpReportRepository extends JpaRepository<DlpReport, Long> {
}
