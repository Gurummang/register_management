package GASB.register_management.service.user;

import GASB.register_management.dto.user.DlpTop5;
import GASB.register_management.dto.user.LastActivities;
import GASB.register_management.dto.user.MalwareTop5;
import GASB.register_management.dto.user.UserStatisticsDto;
import GASB.register_management.entity.MonitoredUsers;
import GASB.register_management.repository.ActivitiesRepository;
import GASB.register_management.repository.MonitoredUsersRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

    public LastActivities getLastActivities(long orgId) {
        // 모든 사용자를 가져옵니다.
        List<MonitoredUsers> users = monitoredUsersRepo.getUserListByOrgId(orgId);

        // 휴면 상태를 계산할 변수 초기화
        int dormantCount = 0;
        int dormantingCount = 0;
        int undormantCount = 0;

        // 현재 날짜
        LocalDateTime currentDate = LocalDateTime.now();

        // 각 사용자에 대해 마지막 활동 시간을 확인하고 휴면 상태를 계산합니다.
        for (MonitoredUsers user : users) {
            LocalDateTime lastActiveDate = getLastDate(user.getId());

            if (lastActiveDate == null) {
                // 마지막 활동 시간이 없으면 휴면 상태로 간주
                dormantCount++;
            } else {
                long monthsSinceLastActive = ChronoUnit.MONTHS.between(lastActiveDate, currentDate);

                if (monthsSinceLastActive >= 12) {
                    dormantCount++;
                } else if (monthsSinceLastActive >= 6) {
                    dormantingCount++;
                } else {
                    undormantCount++;
                }
            }
        }

        // 결과를 LastActivities 객체로 반환합니다.
        return LastActivities.builder()
                .dormant(dormantCount)
                .dormanting(dormantingCount)
                .undormant(undormantCount)
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

    private LocalDateTime getLastDate(long userId){
        return activitiesRepository.findLastActiveTime(userId);
    }

}
