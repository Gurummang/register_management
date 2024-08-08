package GASB.register_management.controller;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.service.OrgSaasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@CrossOrigin(origins = {"http://localhost:5173","http://127.0.0.1:5173"})
@RestController
@RequestMapping("/api/v1/org-saas")
public class OrgSaasController {

    @Autowired
    private OrgSaasService orgSaasService;

    @PostMapping("/slackValid")
    public OrgSaasResponse slackValid (@RequestBody OrgSaasRequest orgSaasRequest) {
        return orgSaasService.slackValid(orgSaasRequest);
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
}
