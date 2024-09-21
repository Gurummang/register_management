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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        if(validateDto.getErrorMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getErrorMessage(), (Boolean) null);
        }
        if(validateDto.getExceptionMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getExceptionMessage(), (Boolean) null);
        }
        return orgSaasService.slackValid(orgSaasRequest, validateDto);
    }

    @GetMapping("/{saasId}/mkUrl")
    @ValidateJWT
    public OrgSaasResponse mkUrl(@PathVariable Integer saasId, HttpServletRequest servletRequest) {
        ValidateDto validateDto = validateJwt(servletRequest);
        if(validateDto.getErrorMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getErrorMessage(), (Boolean) null);
        }
        if(validateDto.getExceptionMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getExceptionMessage(), (Boolean) null);
        }
        return orgSaasService.getUrl(saasId);
    }

    @PostMapping("/register")
    @ValidateJWT
    public OrgSaasResponse register(@RequestBody OrgSaasRequest orgSaasRequest, HttpServletRequest servletRequest) {
        ValidateDto validateDto = validateJwt(servletRequest);
        if(validateDto.getErrorMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getErrorMessage(), (Boolean) null);
        }
        if(validateDto.getExceptionMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getExceptionMessage(), (Boolean) null);
        }
        // orgId를 JWT에서 추출하여 요청 객체에 설정
        try {
            orgSaasRequest.setOrgId(Math.toIntExact(validateDto.getOrgId()));
        } catch (ArithmeticException e) {
            return new OrgSaasResponse(400, "Org ID exceeds allowable range.", (Boolean) null);
        }

        // 서비스 호출
        return orgSaasService.registerOrgSaas(orgSaasRequest);
    }

    @PostMapping("/modify")
    @ValidateJWT
    public OrgSaasResponse modify(@RequestBody OrgSaasRequest orgSaasRequest, HttpServletRequest servletRequest) {
        ValidateDto validateDto = validateJwt(servletRequest);
        if(validateDto.getErrorMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getErrorMessage(), (Boolean) null);
        }
        if(validateDto.getExceptionMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getExceptionMessage(), (Boolean) null);
        }
        return orgSaasService.modifyOrgSaas(orgSaasRequest);
    }

    @PostMapping("/delete")
    @ValidateJWT
    public OrgSaasResponse delete(@RequestBody OrgSaasRequest orgSaasRequest, HttpServletRequest servletRequest) {
        ValidateDto validateDto = validateJwt(servletRequest);
        if(validateDto.getErrorMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getErrorMessage(), (Boolean) null);
        }
        if(validateDto.getExceptionMessage() != null) {
            return new OrgSaasResponse(400, validateDto.getExceptionMessage(), (Boolean) null);
        }
        return orgSaasService.deleteOrgSaas(orgSaasRequest);
    }

    @GetMapping("/list")
    @ValidateJWT
    public List<OrgSaasResponse> getOrgSaasList(HttpServletRequest servletRequest) {
        List<OrgSaasResponse> exceptList = new ArrayList<>();
        ValidateDto validateDto = validateJwt(servletRequest);
        if(validateDto.getErrorMessage() != null) {
            OrgSaasResponse errorResponse = new OrgSaasResponse(400, validateDto.getErrorMessage(), (Boolean) null);
            exceptList.add(errorResponse);
            return exceptList;
        }
        if(validateDto.getExceptionMessage() != null) {
            OrgSaasResponse errorResponse = new OrgSaasResponse(400, validateDto.getExceptionMessage(), (Boolean) null);
            exceptList.add(errorResponse);
            return exceptList;
        }
        Integer orgId = Math.toIntExact(validateDto.getOrgId());
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
