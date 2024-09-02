package GASB.register_management.service.user;

import GASB.register_management.dto.user.UserInfo;
import GASB.register_management.entity.*;
import GASB.register_management.repository.ActivitiesRepository;
import GASB.register_management.repository.MonitoredUsersRepo;
import GASB.register_management.repository.StoredFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserInfoService {

    private final MonitoredUsersRepo monitoredUsersRepo;
    private final ActivitiesRepository activitiesRepository;

    public UserInfoService(MonitoredUsersRepo monitoredUsersRepo, ActivitiesRepository activitiesRepository){
        this.monitoredUsersRepo = monitoredUsersRepo;
        this.activitiesRepository = activitiesRepository;
    }
    public List<UserInfo> fetchUserInfoList(long orgId) {
        return monitoredUsersRepo.getUserListByOrgId(orgId)
                .stream()
                .map(this::createUserInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private UserInfo createUserInfo(MonitoredUsers user) {
        LocalDateTime lastDate = getLastDate(user.getId());
        return UserInfo.builder()
                .id(user.getId())
                .saas(user.getOrgSaaS().getSaas().getSaasName())
                .user(user.getUserName())
                .account(user.getEmail() != null ? user.getEmail() : "none")
                .totalUpload(getTotalUpload(user.getId()))
                .sensitive(getSensitive(user.getId()))
                .malware(getMalware(user.getId()))
                .lastDate(lastDate != null ? lastDate : LocalDateTime.MIN)
                .build();
    }

    private int getTotalUpload(long userId){
        return activitiesRepository.findTotalUploadCount(userId);
    }

    private int getSensitive(long userId){
        return activitiesRepository.countSensitiveActivitiesByUserId(userId);
    }

    private int getMalware(long userId){
        return activitiesRepository.countSuspiciousActivitiesByUserId(userId) + activitiesRepository.countVtMalwareByUserId(userId);
    }

    private LocalDateTime getLastDate(long userId){
        return activitiesRepository.findLastActiveTime(userId);
    }


}
