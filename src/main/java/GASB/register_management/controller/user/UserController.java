package GASB.register_management.controller.user;

import GASB.register_management.dto.user.ResponseDto;
import GASB.register_management.dto.user.UserStatisticsDto;
import GASB.register_management.dto.user.UserTotalDto;
import GASB.register_management.service.user.UserStatisticsService;
import GASB.register_management.service.user.UserTotalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserTotalService userTotalService;
    private final UserStatisticsService userStatisticsService;

    public UserController(UserTotalService userTotalService, UserStatisticsService userStatisticsService){
        this.userTotalService = userTotalService;
        this.userStatisticsService = userStatisticsService;
    }

    @GetMapping("/total")
    public ResponseDto<UserTotalDto> getUserTotal(){
        UserTotalDto userTotal = userTotalService.getTotal();
        return ResponseDto.ofSuccess(userTotal);
    }

    @GetMapping("/statistics")
    public ResponseDto<UserStatisticsDto> getUserStatistics(){
        UserStatisticsDto userStatistics = userStatisticsService.getStatistics();
        return ResponseDto.ofSuccess(userStatistics);
    }
}
