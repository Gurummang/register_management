package GASB.register_management.repository;

import GASB.register_management.entity.MonitoredUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MonitoredUsersRepo extends JpaRepository<MonitoredUsers, Long> {

    @Query("SELECT m FROM MonitoredUsers m JOIN m.orgSaaS os WHERE os.org.id = :orgId")
    List<MonitoredUsers> getUserListByOrgId(@Param("orgId") long orgId);

    @Query("SELECT COUNT(m.id) FROM MonitoredUsers m JOIN m.orgSaaS os WHERE os.org.id = :orgId")
    int getTotalUserCount(@Param("orgId") long orgId);

    @Query("SELECT COUNT(DISTINCT m.id) " +
            "FROM MonitoredUsers m " +
            "JOIN m.activities a " +
            "JOIN FileUpload fu ON a.saasFileId = fu.saasFileId AND a.eventTs = fu.timestamp " +
            "JOIN fu.orgSaaS os " +
            "LEFT JOIN fu.storedFile sf " +
            "LEFT JOIN fu.typeScan ts " +
            "LEFT JOIN sf.scanTable gs " +
            "LEFT JOIN sf.vtReport vr " +
            "WHERE fu.deleted = false " +
            "AND (vr IS NULL AND (ts.correct = false OR gs.detected = true)) " +
            "     OR (vr IS NOT NULL AND vr.threatLabel != 'none') " +
            "AND os.org.id = :orgId")
    int countDistinctUsersWithSuspiciousActivity(@Param("orgId") long orgId);

    @Query("SELECT COUNT(DISTINCT m.id) " +
            "FROM MonitoredUsers m " +
            "JOIN m.activities a " +
            "JOIN FileUpload fu ON a.saasFileId = fu.saasFileId AND a.eventTs = fu.timestamp " +
            "JOIN fu.orgSaaS os " +
            "LEFT JOIN fu.storedFile sf " +
            "LEFT JOIN sf.dlpReport dr " +
            "WHERE fu.deleted = false " +
            "AND dr.infoCnt >= 1 " +
            "AND os.org.id = :orgId")
    int countDistinctUsersWithSensitiveActivity(@Param("orgId") long orgId);

}
