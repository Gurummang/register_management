package GASB.register_management.service.user;

import GASB.register_management.dto.user.DlpTop5;
import GASB.register_management.dto.user.LastActivities;
import GASB.register_management.dto.user.MalwareTop5;
import GASB.register_management.dto.user.UserStatisticsDto;
import GASB.register_management.entity.MonitoredUsers;
import GASB.register_management.repository.ActivitiesRepository;
import GASB.register_management.repository.MonitoredUsersRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserStatisticsService {

    private final MonitoredUsersRepo monitoredUsersRepo;
    private final ActivitiesRepository activitiesRepository;

    public UserStatisticsService(MonitoredUsersRepo monitoredUsersRepo, ActivitiesRepository activitiesRepository){
        this.monitoredUsersRepo = monitoredUsersRepo;
        this.activitiesRepository = activitiesRepository;
    }

    public UserStatisticsDto getStatistics(long orgId){
        return UserStatisticsDto.builder()
                .lastActivities(getLastActivities(orgId))
                .topSensitive(getDlpTop5(orgId))
                .topMalware(malwareTop5(orgId))
                .build();
    }

    public LastActivities getLastActivities(long orgId){
        return LastActivities.builder()
                .dormant(20)
                .domanting(50)
                .undormant(11)
                .build();
    }

    public List<DlpTop5> getDlpTop5(long orgId){
        List<MonitoredUsers> users = monitoredUsersRepo.getUserListByOrgId(orgId);

        return users.stream()
                .map(user -> new DlpTop5(
                        user.getUserName(),  // 사용자 이름
                        getSensitive(user.getId())  // 악성 파일 수
                ))
                .sorted((u1, u2) -> Integer.compare(u2.getSensitive(), u1.getSensitive()))  // 악성 파일 수에 따라 내림차순 정렬
                .limit(5)  // 상위 5명만 선택
                .collect(Collectors.toList());
    }

    public List<MalwareTop5> malwareTop5(long orgId) {
        List<MonitoredUsers> users = monitoredUsersRepo.getUserListByOrgId(orgId);

        return users.stream()
                .map(user -> new MalwareTop5(
                        user.getUserName(),  // 사용자 이름
                        getMalware(user.getId())  // 악성 파일 수
                ))
                .sorted((u1, u2) -> Integer.compare(u2.getMalware(), u1.getMalware()))  // 악성 파일 수에 따라 내림차순 정렬
                .limit(5)  // 상위 5명만 선택
                .collect(Collectors.toList());
    }

    private int getSensitive(long userId){
        return activitiesRepository.countSensitiveActivitiesByUserId(userId);
    }

    private int getMalware(long userId){
        return activitiesRepository.countSuspiciousActivitiesByUserId(userId) + activitiesRepository.countVtMalwareByUserId(userId);
    }

}
