package GASB.register_management.controller;

import GASB.register_management.dto.SaasRequest;
import GASB.register_management.dto.SaasResponse;
import GASB.register_management.service.SaasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import java.util.List;

@RestController
@RequestMapping("/api/v1/saas")
public class SaasController {

    @Autowired
    private SaasService saasService;
    @Autowired
    private InternalResourceViewResolver defaultViewResolver;

    @PostMapping
    public SaasResponse manageSaas(@RequestBody SaasRequest saasRequest) {
        return switch (saasRequest.getAction()) {
            case "register" -> saasService.registerSaas(saasRequest);
            case "modify" -> saasService.modifySaas(saasRequest);
            case "delete" -> saasService.deleteSaas(saasRequest);
            // 예외처리 부분
            // 다음 task에서 진행, 우선은 CRUD 구현 먼저
            case null, default -> throw new IllegalArgumentException("Invalid action: " + saasRequest.getAction());
        };
    }

    @GetMapping
    public List<SaasResponse> getSaasList(){
        return saasService.getSaasList();
    }
}
