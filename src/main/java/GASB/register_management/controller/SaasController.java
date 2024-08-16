package GASB.register_management.controller;

import GASB.register_management.dto.SaasRequest;
import GASB.register_management.dto.SaasResponse;
import GASB.register_management.service.SaasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saas")
public class SaasController {

    private final SaasService saasService;

    @Autowired
    public SaasController(SaasService saasService) {
        this.saasService = saasService;
    }

    @PostMapping("/register")
    public SaasResponse registerSaas(@RequestBody SaasRequest saasRequest) {
        return saasService.registerSaas(saasRequest);
    }

    @PostMapping("/modify")
    public SaasResponse modifySaas(@RequestBody SaasRequest saasRequest) {
        return saasService.modifySaas(saasRequest);
    }

    @PostMapping("/delete")
    public SaasResponse deleteSaas(@RequestBody SaasRequest saasRequest) {
        return saasService.deleteSaas(saasRequest.getId());
    }

    @GetMapping
    public List<SaasResponse> getSaasList() {
        return saasService.getSaasList();
    }
}
