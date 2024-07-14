package GASB.register_management.service.imple;

import GASB.register_management.dto.SaasRequest;
import GASB.register_management.dto.SaasResponse;
import GASB.register_management.entity.Saas;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.service.SaasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaasServiceImple implements SaasService {

    @Autowired
    private SaasRepository saasRepository;

    @Override
    public SaasResponse registerSaas(SaasRequest saasRequest) {
        Saas saas = new Saas();
        saas.setSaas_name(saasRequest.getSaas_name());
        Saas saveSaas = saasRepository.save(saas);

        return new SaasResponse("success", "Register Success: " + saveSaas.getSaas_name(), saveSaas.getId());
//        {
//            "status": "success",
//            "messege": "Register Success: Slack",
//            "id": 1
//        }
    }

    @Override
    public SaasResponse modifySaas(SaasRequest saasRequest) {
        Saas saas = new Saas();
        saas.setSaas_name(saasRequest.getSaas_name());
        Saas saveSaas = saasRepository.save(saas);

        return new SaasResponse("success", "Modify Success: " + saveSaas.getSaas_name(), saveSaas.getId());
    }

    @Override
    public SaasResponse deleteSaas(SaasRequest saasRequest) {
        Saas saas = new Saas();
        saas.setSaas_name(saasRequest.getSaas_name());
        Saas saveSaas = saasRepository.save(saas);

        return new SaasResponse("success", "Delete Success: " + saveSaas.getSaas_name(), saveSaas.getId());
    }

    @Override
    public List<SaasResponse> getSaasList(){
        return saasRepository.findAll().stream().map(saas -> new SaasResponse(saas.getId(), saas.getSaas_name())).collect(Collectors.toList());
    }
}
