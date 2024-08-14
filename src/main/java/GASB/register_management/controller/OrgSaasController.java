package GASB.register_management.controller;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.service.OrgSaasService;

import GASB.register_management.util.GoogleUtil;
import com.google.api.services.drive.Drive;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173", "http://localhost:8080"})
@RestController
@RequestMapping("/api/v1/org-saas")
public class OrgSaasController {

    private final OrgSaasService orgSaasService;
    private final GoogleUtil googleUtil;

    public OrgSaasController(OrgSaasService orgSaasService, GoogleUtil googleUtil) {
        this.orgSaasService = orgSaasService;
        this.googleUtil = googleUtil;
    }

    @PostMapping("/slackValid")
    public OrgSaasResponse slackValid (@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.slackValid(orgSaasRequest);
    }

    @GetMapping("/{saasId}/mkUrl")
    public OrgSaasResponse mkUrl(@PathVariable Integer saasId) {
        return orgSaasService.getUrl(saasId);
    }

    @PostMapping("/register")
    public OrgSaasResponse register(@RequestBody OrgSaasRequest orgSaasRequest) throws Exception {
        if(orgSaasRequest.getSaasId() == 6) {
            System.out.println("1. 시작");
            return googleUtil.starter(orgSaasRequest);
        }
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

//    @PostMapping("/register/gd")
//    public Drive registerGoogle() throws Exception {
//        System.out.println("Call: registerGoogle");
//        return googleUtil.getDriveService();
//    }
}
