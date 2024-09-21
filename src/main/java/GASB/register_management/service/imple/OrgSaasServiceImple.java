package GASB.register_management.service.imple;

import GASB.register_management.config.RabbitMQConfig;
import GASB.register_management.dto.register.OrgSaasRequest;
import GASB.register_management.dto.register.OrgSaasResponse;
import GASB.register_management.dto.register.ValidateDto;
import GASB.register_management.entity.Org;
import GASB.register_management.entity.Saas;
import GASB.register_management.repository.OrgRepository;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.service.register.OrgSaasService;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.Workspace;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.WorkspaceRepository;
import GASB.register_management.util.api.StartScan;
import GASB.register_management.util.validation.SlackTeamInfo;
import GASB.register_management.util.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrgSaasServiceImple implements OrgSaasService {

    private final OrgSaasRepository orgSaasRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SaasRepository saasRepository;
    private final SlackTeamInfo slackTeamInfo;
    private final StartScan startScan;
    private final RabbitMQConfig rabbitMQConfig;
    private final RabbitTemplate rabbitTemplate;
    private final OrgRepository orgRepository;

    @Value("${aes.key}")
    private String aesKey;

    @Autowired
    public OrgSaasServiceImple(OrgSaasRepository orgSaasRepository, WorkspaceRepository workspaceRepository, SaasRepository saasRepository, SlackTeamInfo slackTeamInfo, StartScan startScan, RabbitMQConfig rabbitMQConfig, RabbitTemplate rabbitTemplate, OrgRepository orgRepository) {
        this.orgSaasRepository = orgSaasRepository;
        this.workspaceRepository = workspaceRepository;
        this.saasRepository = saasRepository;
        this.slackTeamInfo = slackTeamInfo;
        this.startScan = startScan;
        this.rabbitMQConfig = rabbitMQConfig;
        this.rabbitTemplate = rabbitTemplate;
        this.orgRepository = orgRepository;
    }

    @Override
    public OrgSaasResponse slackValid(OrgSaasRequest orgSaasRequest, ValidateDto validateDto) {
        if (validateDto.getErrorMessage() != null) {
            return new OrgSaasResponse(198, validateDto.getErrorMessage(), false);
        }
        if (validateDto.getExceptionMessage() != null) {
            return new OrgSaasResponse(198, validateDto.getExceptionMessage(), false);
        }
        String token = orgSaasRequest.getApiToken();
        try {
            slackTeamInfo.getTeamInfo(token);

            return new OrgSaasResponse(200, null, true);
        } catch (IOException | InterruptedException e) {
            return new OrgSaasResponse(199, e.getMessage(), false);
        }
    }

    @Override
    public OrgSaasResponse getUrl(Integer saasId) {
//        if (validateDto.getErrorMessage() != null) {
//            return new OrgSaasResponse(198, validateDto.getErrorMessage(), false);
//        }
//        if (validateDto.getExceptionMessage() != null) {
//            return new OrgSaasResponse(198, validateDto.getExceptionMessage(), false);
//        }
        Optional<Saas> saasOptional = saasRepository.findById(saasId);

        if(saasOptional.isPresent()) {
            Saas saas = saasOptional.get();

            return new OrgSaasResponse( 200, null,
                    "https://back.grummang.com/webhook/"+saas.getSaasName()+ "/" + UUID.randomUUID());
        }else {
            return new OrgSaasResponse( 199, "Not found for ID", "");
        }
    }

    @Override
    public OrgSaasResponse registerOrgSaas(OrgSaasRequest orgSaasRequest) {
        OrgSaas orgSaas = new OrgSaas();
        Workspace workspace = new Workspace();

        Optional<Org> orgOpt = orgRepository.findById(orgSaasRequest.getOrgId());
        Optional<Saas> saasOpt = saasRepository.findById(orgSaasRequest.getSaasId());

        // Org와 Saas가 존재하지 않으면 에러 처리
        if (orgOpt.isEmpty()) {
            return new OrgSaasResponse(404, "Org not found", (Boolean) null);
        }

        if (saasOpt.isEmpty()) {
            return new OrgSaasResponse(404, "Saas not found", (Boolean) null);
        }
        // Org와 Saas가 존재하는 경우 orgSaas 객체에 설정
        Org org = orgOpt.get();
        Saas saas = saasOpt.get();

        if(orgSaasRequest.getSaasId() == 6) {
//            orgSaas.setOrgId(orgSaasRequest.getOrgId());
//            orgSaas.setSaasId(orgSaasRequest.getSaasId());
            orgSaas.setOrg(org);
            orgSaas.setSaas(saas);
            orgSaas.setSpaceId("TEMP");
            OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);

//            workspace.setId(regiOrgSaas.getId());
            workspace.setOrgSaas(regiOrgSaas);
            workspace.setSpaceName("TEMP");
            workspace.setAlias(orgSaasRequest.getAlias());
            workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
            workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
            workspace.setApiToken("TEMP");
            workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
            Workspace regiWorksapce = workspaceRepository.save(workspace);

            return new OrgSaasResponse(200, "Waiting Google Drive", regiWorksapce.getId(), regiWorksapce.getRegisterDate());
        }

        try {
            List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());

//            orgSaas.setOrgId(orgSaasRequest.getOrgId());
//            orgSaas.setSaasId(orgSaasRequest.getSaasId());
            orgSaas.setOrg(org);
            orgSaas.setSaas(saas);
            orgSaas.setSpaceId(slackInfo.get(1));
            OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);

            workspace.setOrgSaas(regiOrgSaas);
            workspace.setSpaceName(slackInfo.get(0));
            workspace.setAlias(orgSaasRequest.getAlias());
            workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
            workspace.setApiToken(AESUtil.encrypt(orgSaasRequest.getApiToken(), aesKey));

            System.out.println(AESUtil.decrypt(AESUtil.encrypt(orgSaasRequest.getApiToken(), aesKey), aesKey));
            workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
            workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
            Workspace registeredWorkspace = workspaceRepository.save(workspace);

            String saasName = saasRepository.findById(orgSaasRequest.getSaasId()).get().getSaasName();

            try{
                startScan.postToScan(registeredWorkspace.getId(), saasName);

                return new OrgSaasResponse( 200, null, registeredWorkspace.getId(), registeredWorkspace.getRegisterDate());
            } catch (Exception e) {
                return new OrgSaasResponse(198, e.getMessage(), null, null);
            }
        } catch (IOException | InterruptedException e) {
            return new OrgSaasResponse( 199, e.getMessage(),null, null);
        }
    }

    @Override
    public OrgSaasResponse modifyOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<OrgSaas> optionalOrgSaas = orgSaasRepository.findById(orgSaasRequest.getId());
        Optional<Workspace> optionalWorkspace = workspaceRepository.findById(orgSaasRequest.getId());

        if(optionalOrgSaas.isPresent() && optionalWorkspace.isPresent()) {
            OrgSaas orgSaas = optionalOrgSaas.get();
            Workspace workspace = optionalWorkspace.get();

            try{
                List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());

                orgSaas.setSpaceId(slackInfo.get(1));
                orgSaasRepository.save(orgSaas);

                workspace.setSpaceName(slackInfo.get(0));
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setApiToken(AESUtil.encrypt(orgSaasRequest.getApiToken(), aesKey));
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace registeredWorkspace = workspaceRepository.save(workspace);

                Integer saasId = orgSaasRepository.findById(orgSaasRequest.getId()).get().getSaasId();
                String saasName = saasRepository.findById(saasId).get().getSaasName();

                try{
                    startScan.postToScan(registeredWorkspace.getId(), saasName);

                    return new OrgSaasResponse( 200, null, registeredWorkspace.getId(), registeredWorkspace.getRegisterDate());
                } catch (Exception e) {
                    return new OrgSaasResponse(198, e.getMessage(), null, null);
                }

            } catch (IOException | InterruptedException e) {
                return new OrgSaasResponse( 199, e.getMessage(),null, null);
            }
        } else {
            return new OrgSaasResponse( 199, "Not found for ID", "");
        }
    }

    @Override
    public OrgSaasResponse deleteOrgSaas(OrgSaasRequest orgSaasRequest) {
        Optional<OrgSaas> optionalOrgSaas = orgSaasRepository.findById(orgSaasRequest.getId());

        if (optionalOrgSaas.isPresent()) {
            OrgSaas orgSaas = optionalOrgSaas.get();

            orgSaasRepository.delete(orgSaas);

            return new OrgSaasResponse( 200, null, null,null);
        } else {
            return new OrgSaasResponse( 199, "Not found for ID", null,null);
        }
    }

    @Override
    public List<OrgSaasResponse> getOrgSaasList(Integer orgId) {
        List<OrgSaas> orgSaasList = orgSaasRepository.findByOrgId(orgId);

        List<Integer> configIds = orgSaasList.stream()
                .map(OrgSaas::getId)
                .distinct()  // 중복 제거
                .collect(Collectors.toList());

        List<Workspace> workspaceList = workspaceRepository.findByIdIn(configIds);
        Map<Integer, Workspace> workspaceMap = workspaceList.stream()
                .collect(Collectors.toMap(Workspace::getId, workspace -> workspace));

        return orgSaasList.stream().map(orgSaas -> {
            Workspace workspace = workspaceMap.get(orgSaas.getId());

            Optional<Saas> saasOptional = saasRepository.findById(orgSaas.getSaasId());
            String saasName = saasOptional.map(Saas::getSaasName).orElse("Unknown");

            return new OrgSaasResponse(
                    workspace != null ? workspace.getId() : null,
                    saasName,
                    workspace != null ? workspace.getAlias() : null,
                    orgSaas.getStatus(),
                    workspace != null ? workspace.getAdminEmail() : null,
                    workspace != null ? workspace.getApiToken() : null,
                    workspace != null ? workspace.getWebhookUrl() : null,
                    workspace != null ? workspace.getRegisterDate() : null
            );
        }).collect(Collectors.toList());
    }

    public void updateOrgSaasGD(List<String[]> drives, String accessToken) {
        List<OrgSaas> tempOrgSaasList = orgSaasRepository.findBySpaceId("TEMP");

        if (tempOrgSaasList.isEmpty()) {
            return;
        }

        // 드라이브 리스트 순회
        for (int i = 0; i < drives.size(); i++) {
            String[] driveInfo = drives.get(i);  // [0]: 드라이브 ID, [1]: 드라이브 이름

            // DELETE 상태인 드라이브 처리
            if ("DELETE".equals(driveInfo[0])) {
                // spaceId가 TEMP인 튜플 모두 삭제
                for (OrgSaas orgSaas : tempOrgSaasList) {
                    orgSaasRepository.delete(orgSaas);

                    Optional<Workspace> optionalWorkspace = workspaceRepository.findById(orgSaas.getId());
                    optionalWorkspace.ifPresent(workspaceRepository::delete);
                }

                return;
            }

            OrgSaas orgSaas;
            Workspace workspace;

            if (i < tempOrgSaasList.size()) {
                // 기존 TEMP 튜플 업데이트
                orgSaas = tempOrgSaasList.get(i);
            } else {
                // TEMP 튜플을 복제
                OrgSaas originalOrgSaas = tempOrgSaasList.get(0);  // 첫 번째 TEMP 튜플을 기준으로 복사
                orgSaas = new OrgSaas();
                Optional<Org> orgOpt = orgRepository.findById(originalOrgSaas.getOrgId());
                Optional<Saas> saasOpt = saasRepository.findById(originalOrgSaas.getSaasId());
                // Org와 Saas가 존재하는 경우 orgSaas 객체에 설정
                if (orgOpt.isEmpty()) {
                    log.error("org is empty()");
                }

                if (saasOpt.isEmpty()) {
                    log.error("saas is empty()");
                }
                Org org = orgOpt.get();
                Saas saas = saasOpt.get();
                orgSaas.setOrg(org);
                orgSaas.setSaas(saas);
//                orgSaas.setOrgId(originalOrgSaas.getOrgId());
//                orgSaas.setSaasId(originalOrgSaas.getSaasId());
                orgSaas.setSpaceId("TEMP");  // 나중에 업데이트될 것이므로 우선 TEMP로 설정
                orgSaas = orgSaasRepository.save(orgSaas);  // 복제된 튜플 저장

                Optional<Workspace> originalWorkspaceOpt = workspaceRepository.findById(originalOrgSaas.getId());
                if (originalWorkspaceOpt.isPresent()) {
                    Workspace originalWorkspace = originalWorkspaceOpt.get();
                    workspace = new Workspace();
//                    workspace.setId(orgSaas.getId());
                    workspace.setOrgSaas(orgSaas);
                    workspace.setAlias(originalWorkspace.getAlias());
                    workspace.setAdminEmail(originalWorkspace.getAdminEmail());
                    workspace.setApiToken(originalWorkspace.getApiToken());
                    workspace.setWebhookUrl(originalWorkspace.getWebhookUrl());
                    workspace.setRegisterDate(originalWorkspace.getRegisterDate());
                    workspaceRepository.save(workspace);
                } else {
                    continue;
                }
            }

            orgSaas.setSpaceId(driveInfo[0]);
            OrgSaas saveOrgSaas = orgSaasRepository.save(orgSaas);

            Optional<Workspace> optionalWorkspace = workspaceRepository.findById(orgSaas.getId());
            if (optionalWorkspace.isPresent()) {
                workspace = optionalWorkspace.get();
                workspace.setSpaceName(driveInfo[1]);
                workspace.setApiToken(AESUtil.encrypt(accessToken, aesKey));
                workspaceRepository.save(workspace);
            }
            log.info("Send orgSaasId (={}) to gd_init_queue",saveOrgSaas.getOrgId());
            rabbitTemplate.convertAndSend(rabbitMQConfig.getExchangeName(), rabbitMQConfig.getRoutingKey(), saveOrgSaas.getId());
        }
    }
}
