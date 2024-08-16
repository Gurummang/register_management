package GASB.register_management.controller;

import GASB.register_management.dto.OrgRequest;
import GASB.register_management.dto.OrgResponse;
import GASB.register_management.service.OrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orgs")
public class OrgController {

    private final OrgService orgService;

    @Autowired
    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }


    @PostMapping("/register")
    public OrgResponse registerOrg(@RequestBody OrgRequest orgRequest) {
        return orgService.registerOrg(orgRequest);
    }

    @PostMapping("/modify")
    public OrgResponse modifyOrg(@RequestBody OrgRequest orgRequest) {
        return orgService.modifyOrg(orgRequest);
    }

    @PostMapping("/delete")
    public OrgResponse deleteOrg(@RequestBody OrgRequest orgRequest) {
        return orgService.deleteOrg(orgRequest.getId());
    }

    @GetMapping
    public List<OrgResponse> getOrgList() {
        return orgService.getOrgList();
    }
}