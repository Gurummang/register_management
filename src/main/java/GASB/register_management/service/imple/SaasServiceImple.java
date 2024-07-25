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

        try {
            saas.setSaasName(saasRequest.getSaasName());
            Saas regiSaas = saasRepository.save(saas);

            return new SaasResponse(200, null, regiSaas.getId(), null);
        } catch (Exception e) {
            return new SaasResponse(199, e.getMessage(), null, null);
        }
    }

    @Override
    public SaasResponse modifySaas(SaasRequest saasRequest) {
        Optional<Saas> optionalSaas = saasRepository.findById(saasRequest.getId());
        if (optionalSaas.isPresent()) {
            Saas saas = optionalSaas.get();
            saas.setSaasName(saasRequest.getSaasName());
            Saas updatedSaas = saasRepository.save(saas);

            return new SaasResponse(200, null, updatedSaas.getId(), updatedSaas.getSaasName());
        } else {
            return new SaasResponse(199, "Not found for ID", null, null);
        }
    }

    @Override
    public SaasResponse deleteSaas(Integer id) {
        Optional<Saas> optionalSaas = saasRepository.findById(id);
        if (optionalSaas.isPresent()) {
            saasRepository.deleteById(id);

            return new SaasResponse(200, null, id, null);
        } else {
            return new SaasResponse(199, "Not found for ID", null, null);
        }
    }

    @Override
    public List<SaasResponse> getSaasList() {
        return saasRepository.findAll().stream()
                .map(saas -> new SaasResponse(null, null, saas.getId(), saas.getSaasName()))
                .collect(Collectors.toList());
    }
}
