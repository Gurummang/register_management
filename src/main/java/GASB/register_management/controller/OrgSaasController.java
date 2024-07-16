package GASB.register_management.controller;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.service.OrgSaasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/org-saas")
public class OrgSaasController {

    @Autowired
    private OrgSaasService orgSaasService;

    @GetMapping("/{saasId}/mkurl")
    public OrgSaasResponse mkUrl(@PathVariable Integer saasId) {
        return orgSaasService.getUrl(saasId);
    }
}
