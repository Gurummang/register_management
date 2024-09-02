package GASB.register_management.controller.user;

import GASB.register_management.dto.user.*;
import GASB.register_management.service.user.UserInfoService;
import GASB.register_management.service.user.UserStatisticsService;
import GASB.register_management.service.user.UserTotalService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserTotalService userTotalService;
    private final UserStatisticsService userStatisticsService;
    private final UserInfoService userInfoService;

    public UserController(UserTotalService userTotalService, UserStatisticsService userStatisticsService, UserInfoService userInfoService){
        this.userTotalService = userTotalService;
        this.userStatisticsService = userStatisticsService;
        this.userInfoService = userInfoService;
    }

    @PostMapping("/total")
    public ResponseDto<UserTotalDto> getUserTotal(@RequestBody OrgIdRequest orgIdRequest){
        long orgId = orgIdRequest.getOrgId();
        UserTotalDto userTotal = userTotalService.getTotal(orgId);
        return ResponseDto.ofSuccess(userTotal);
    }

    @GetMapping("/statistics")
    public ResponseDto<UserStatisticsDto> getUserStatistics(){
        UserStatisticsDto userStatistics = userStatisticsService.getStatistics();
        return ResponseDto.ofSuccess(userStatistics);
    }

    @PostMapping("/info")
    public ResponseDto<List<UserInfo>> getUserInfo(@RequestBody OrgIdRequest orgIdRequest){
        long orgId = orgIdRequest.getOrgId();
        List<UserInfo> userinfo = userInfoService.fetchUserInfoList(orgId);
        return ResponseDto.ofSuccess(userinfo);
    }
}
