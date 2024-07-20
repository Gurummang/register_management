package GASB.register_management.service.imple;

import GASB.register_management.dto.SaasRequest;
import GASB.register_management.dto.SaasResponse;
import GASB.register_management.entity.Saas;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.service.SaasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    }

    @Override
    public SaasResponse modifySaas(SaasRequest saasRequest) {
        Optional<Saas> optionalSaas = saasRepository.findById(saasRequest.getId());
        if (optionalSaas.isPresent()) {
            Saas saas = optionalSaas.get();
            saas.setSaas_name(saasRequest.getSaas_name());
            Saas updatedSaas = saasRepository.save(saas);
            return new SaasResponse("success", "Modify Success: " + updatedSaas.getSaas_name(), updatedSaas.getId());
        } else {
            return new SaasResponse("failure", "SaaS not found for ID: " + saasRequest.getId(), null);
        }
    }

    @Override
    public SaasResponse deleteSaas(Integer id) {
        Optional<Saas> optionalSaas = saasRepository.findById(id);
        if (optionalSaas.isPresent()) {
            saasRepository.deleteById(id);
            return new SaasResponse("success", "Delete Success for ID: " + id, id);
        } else {
            return new SaasResponse("failure", "SaaS not found for ID: " + id, null);
        }
    }

    @Override
    public List<SaasResponse> getSaasList() {
        return saasRepository.findAll().stream()
                .map(saas -> new SaasResponse(saas.getId(), saas.getSaas_name()))
                .collect(Collectors.toList());
    }
}
