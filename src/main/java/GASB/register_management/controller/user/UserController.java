package GASB.register_management.controller.user;

import GASB.register_management.annotation.jwt.ValidateJWT;
import GASB.register_management.dto.user.*;
import GASB.register_management.entity.Admin;
import GASB.register_management.repository.AdminRepository;
import GASB.register_management.service.user.UserInfoService;
import GASB.register_management.service.user.UserStatisticsService;
import GASB.register_management.service.user.UserTotalService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserTotalService userTotalService;
    private final UserStatisticsService userStatisticsService;
    private final UserInfoService userInfoService;
    private final AdminRepository adminRepository;
    private static final String INVALID_JWT_MSG = "Invalid JWT: email attribute is missing.";
    private static final String ERROR = "error";
    private static final String EMAIL = "email";
    private static final String EMAIL_NOT_FOUND = "Admin not found with email: ";


    public UserController(UserTotalService userTotalService, UserStatisticsService userStatisticsService, UserInfoService userInfoService, AdminRepository adminRepository){
        this.userTotalService = userTotalService;
        this.userStatisticsService = userStatisticsService;
        this.userInfoService = userInfoService;
        this.adminRepository = adminRepository;
    }

    @GetMapping("/total")
    @ValidateJWT
    public ResponseDto<UserTotalDto> getUserTotal(HttpServletRequest servletRequest){
        try {
            if (servletRequest.getAttribute(ERROR) != null) {
                String errorMessage = (String) servletRequest.getAttribute(ERROR);
                return ResponseDto.ofFail(errorMessage);
            }
            String email = (String) servletRequest.getAttribute(EMAIL);

            if (email == null) {
                return ResponseDto.ofFail(INVALID_JWT_MSG);
            }

            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isEmpty()) {
                return ResponseDto.ofFail(EMAIL_NOT_FOUND + email);
            }

            long orgId = adminOptional.get().getOrg_id();
            UserTotalDto userTotal = userTotalService.getTotal(orgId);
            return ResponseDto.ofSuccess(userTotal);
        } catch (IllegalArgumentException e) {
            return ResponseDto.ofFail("Invalid argument: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseDto.ofFail("No such element: " + e.getMessage());
        } catch (Exception e) {
            return ResponseDto.ofFail("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    @ValidateJWT
    public ResponseDto<UserStatisticsDto> getUserStatistics(HttpServletRequest servletRequest){
        try {
            if (servletRequest.getAttribute(ERROR) != null) {
                String errorMessage = (String) servletRequest.getAttribute(ERROR);
                return ResponseDto.ofFail(errorMessage);
            }
            String email = (String) servletRequest.getAttribute(EMAIL);

            if (email == null) {
                return ResponseDto.ofFail(INVALID_JWT_MSG);
            }

            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isEmpty()) {
                return ResponseDto.ofFail(EMAIL_NOT_FOUND + email);
            }

            long orgId = adminOptional.get().getOrg_id();
            UserStatisticsDto userStatistics = userStatisticsService.getStatistics(orgId);
            return ResponseDto.ofSuccess(userStatistics);
        } catch (IllegalArgumentException e) {
            return ResponseDto.ofFail("Invalid argument: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseDto.ofFail("No such element: " + e.getMessage());
        } catch (Exception e) {
            return ResponseDto.ofFail("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    @ValidateJWT
    public ResponseDto<List<UserInfo>> getUserInfo(HttpServletRequest servletRequest){
        try {
            if (servletRequest.getAttribute(ERROR) != null) {
                String errorMessage = (String) servletRequest.getAttribute(ERROR);
                return ResponseDto.ofFail(errorMessage);
            }
            String email = (String) servletRequest.getAttribute(EMAIL);

            if (email == null) {
                return ResponseDto.ofFail(INVALID_JWT_MSG);
            }

            Optional<Admin> adminOptional = adminRepository.findByEmail(email);
            if (adminOptional.isEmpty()) {
                return ResponseDto.ofFail(EMAIL_NOT_FOUND + email);
            }

            long orgId = adminOptional.get().getOrg_id();
            List<UserInfo> userinfo = userInfoService.fetchUserInfoList(orgId);
            return ResponseDto.ofSuccess(userinfo);
        } catch (IllegalArgumentException e) {
            return ResponseDto.ofFail("Invalid argument: " + e.getMessage());
        } catch (NoSuchElementException e) {
            return ResponseDto.ofFail("No such element: " + e.getMessage());
        } catch (Exception e) {
            return ResponseDto.ofFail("An unexpected error occurred: " + e.getMessage());
        }
    }
}
