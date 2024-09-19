package GASB.register_management.controller.register;

import GASB.register_management.annotation.jwt.ValidateJWT;
import GASB.register_management.dto.register.OrgSaasRequest;
import GASB.register_management.dto.register.OrgSaasResponse;
import GASB.register_management.dto.register.ValidateDto;
import GASB.register_management.repository.AdminRepository;
import GASB.register_management.service.register.OrgSaasService;
import GASB.register_management.util.GoogleUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173, http://localhost:8080"})
@RestController
@Slf4j
@RequestMapping("/api/v1/org-saas")
public class OrgSaasController {

    private final OrgSaasService orgSaasService;
    private final GoogleUtil googleUtil;
    private final AdminRepository adminRepository;

    @Autowired
    public OrgSaasController(OrgSaasService orgSaasService, GoogleUtil googleUtil, AdminRepository adminRepository) {
        this.orgSaasService = orgSaasService;
        this.googleUtil = googleUtil;
        this.adminRepository = adminRepository;
    }


    @PostMapping("/slackValid")
    @ValidateJWT
    public OrgSaasResponse slackValid (@RequestBody OrgSaasRequest orgSaasRequest, HttpServletRequest servletRequest) {
        ValidateDto validateDto = validateJwt(servletRequest);
        return orgSaasService.slackValid(orgSaasRequest, validateDto);
    }

    @GetMapping("/{saasId}/mkUrl")
    public OrgSaasResponse mkUrl(@PathVariable Integer saasId) {
        return orgSaasService.getUrl(saasId);
    }

    @PostMapping("/register")
    public OrgSaasResponse register(@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.registerOrgSaas(orgSaasRequest);
    }

    @PostMapping("/modify")
    public OrgSaasResponse modify(@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.modifyOrgSaas(orgSaasRequest);
    }

    @PostMapping("/delete")
    public OrgSaasResponse delete(@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.deleteOrgSaas(orgSaasRequest);
    }

    @GetMapping("/{orgId}")
    public List<OrgSaasResponse> getOrgSaasList(@PathVariable Integer orgId) {
        return orgSaasService.getOrgSaasList(orgId);
    }

    @GetMapping("/token")
    public void token(@RequestParam("code") String code){
        googleUtil.func(code);
    }

    private ValidateDto validateJwt(HttpServletRequest servletRequest) {
        ValidateDto validateDto = new ValidateDto();

        // JWT 검증 실패 시 바로 반환
        String errorMessage = (String) servletRequest.getAttribute("error");
        if (errorMessage != null) {
            validateDto.setErrorMessage(errorMessage);
            return validateDto;  // 더 이상 진행하지 않음
        }

        // JWT 검증 성공 후 orgId 조회
        try {
            String email = (String) servletRequest.getAttribute("email");
            long orgId = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new NoSuchElementException("Admin not found with email: " + email))
                    .getOrg_id();
            validateDto.setOrgId(orgId);
        } catch (Exception e) {
            validateDto.setExceptionMessage(e.getMessage());
        }

        return validateDto;
    }
}
