package GASB.register_management.repository;

import GASB.register_management.entity.Activities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ActivitiesRepository extends JpaRepository<Activities, Long> {

    @Query("SELECT COUNT(av) " +
            "FROM Activities av " +
            "JOIN av.user mu " +
            "WHERE av.user.id = :userId AND av.eventType = 'file_upload'")
    int findTotalUploadCount(@Param("userId") long userId);

    @Query("SELECT MAX(a.eventTs) " +
            "FROM Activities a " +
            "WHERE a.user.id = :userId")
    LocalDateTime findLastActiveTime(@Param("userId") long userId);


    @Query("SELECT COUNT(a.id) " +
            "FROM Activities a " +
            "JOIN FileUpload fu ON a.saasFileId = fu.saasFileId AND a.eventTs = fu.timestamp " +
            "LEFT JOIN fu.storedFile sf " +
            "LEFT JOIN fu.typeScan ts " +
            "LEFT JOIN sf.scanTable gs " +
            "LEFT JOIN sf.vtReport vr " +
            "WHERE fu.deleted = false " +
            "AND (vr IS NULL AND (ts.correct = false OR gs.detected = true)) " +
            "AND a.user.id = :userId")
    int countSuspiciousActivitiesByUserId(@Param("userId") long userId);

    @Query("SELECT COUNT(a.id) " +
            "FROM Activities a " +
            "JOIN FileUpload fu ON a.saasFileId = fu.saasFileId AND a.eventTs = fu.timestamp " +
            "LEFT JOIN fu.storedFile sf " +
            "LEFT JOIN sf.vtReport vr " +
            "WHERE fu.deleted = false AND vr.threatLabel != 'none' AND a.user.id = :userId")
    int countVtMalwareByUserId(@Param("userId") long userId);

    @Query("SELECT COUNT(a.id) " +
            "FROM Activities a " +
            "JOIN FileUpload fu ON a.saasFileId = fu.saasFileId AND a.eventTs = fu.timestamp " +
            "LEFT JOIN fu.storedFile sf " +
            "LEFT JOIN sf.dlpReport dr " +
            "WHERE fu.deleted = false " +
            "AND dr.infoCnt >= 1 " +
            "AND a.user.id = :userId")
    int countSensitiveActivitiesByUserId(@Param("userId") long userId);


}
