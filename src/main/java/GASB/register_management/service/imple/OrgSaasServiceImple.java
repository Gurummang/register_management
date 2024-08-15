package GASB.register_management.service.imple;

import GASB.register_management.dto.OrgSaasRequest;
import GASB.register_management.dto.OrgSaasResponse;
import GASB.register_management.entity.Saas;
import GASB.register_management.repository.SaasRepository;
import GASB.register_management.service.OrgSaasService;
import GASB.register_management.entity.OrgSaas;
import GASB.register_management.entity.Workspace;
import GASB.register_management.repository.OrgSaasRepository;
import GASB.register_management.repository.WorkspaceRepository;


import GASB.register_management.util.api.StartScan;
import GASB.register_management.util.validation.SlackTeamInfo;

import org.springframework.beans.factory.annotation.Autowired;
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
public class OrgSaasServiceImple implements OrgSaasService {

    private final OrgSaasRepository orgSaasRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SaasRepository saasRepository;
    private final SlackTeamInfo slackTeamInfo;
    private final StartScan startScan;

    @Autowired
    public OrgSaasServiceImple(OrgSaasRepository orgSaasRepository, WorkspaceRepository workspaceRepository, SaasRepository saasRepository, SlackTeamInfo slackTeamInfo, StartScan startScan) {
        this.orgSaasRepository = orgSaasRepository;
        this.workspaceRepository = workspaceRepository;
        this.saasRepository = saasRepository;
        this.slackTeamInfo = slackTeamInfo;
        this.startScan = startScan;
    }

    @Override
    public OrgSaasResponse slackValid(OrgSaasRequest orgSaasRequest) {
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
        Optional<Saas> saasOptional = saasRepository.findById(saasId);

        if(saasOptional.isPresent()) {
            Saas saas = saasOptional.get();

            System.out.println(saas.getSaasName());
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

        if(orgSaasRequest.getSaasId() == 6) {
            orgSaas.setOrgId(orgSaasRequest.getOrgId());    // workspace_config.id
            orgSaas.setSaasId(orgSaasRequest.getSaasId());
            orgSaas.setSpaceId("TEMP");
            OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);

            // workspace_config
            workspace.setId(regiOrgSaas.getId());
            workspace.setSpaceName("TEMP");
            workspace.setAlias(orgSaasRequest.getAlias());
            workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
            workspace.setApiToken("TEMP");
            workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
            workspaceRepository.save(workspace);
            return new OrgSaasResponse(200, "Waiting Google Drive", null, null);
        }

        try {
            List<String> slackInfo = slackTeamInfo.getTeamInfo(orgSaasRequest.getApiToken());

            // org_saas
            orgSaas.setOrgId(orgSaasRequest.getOrgId());    // workspace_config.id
            orgSaas.setSaasId(orgSaasRequest.getSaasId());
            orgSaas.setSpaceId(slackInfo.get(1));
            OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);

            // workspace_config
            workspace.setId(regiOrgSaas.getId());
            workspace.setSpaceName(slackInfo.get(0));
            workspace.setAlias(orgSaasRequest.getAlias());
            workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
            workspace.setApiToken(orgSaasRequest.getApiToken());
            workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
            workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
            Workspace registeredWorkspace = workspaceRepository.save(workspace);

            //saasId -> saasName
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

                // org_saas
                orgSaas.setSpaceId(slackInfo.get(1));
                OrgSaas regiOrgSaas = orgSaasRepository.save(orgSaas);

                // workspace_config
                workspace.setSpaceName(slackInfo.get(0));
                workspace.setAlias(orgSaasRequest.getAlias());
                workspace.setAdminEmail(orgSaasRequest.getAdminEmail());
                workspace.setApiToken(orgSaasRequest.getApiToken());
                workspace.setWebhookUrl(orgSaasRequest.getWebhookUrl());
                workspace.setRegisterDate(Timestamp.valueOf(LocalDateTime.now()));
                Workspace registeredWorkspace = workspaceRepository.save(workspace);

                //saasId -> saasName
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

            // orgSaas의 튜플을 삭제하면
            // 자식 튜플(config, monitored_users 등)들 모두 CASCADE로 삭제
            orgSaasRepository.delete(orgSaas);

            return new OrgSaasResponse( 200, null, null,null);
        } else {
            return new OrgSaasResponse( 199, "Not found for ID", null,null);
        }
    }

    @Override
    public List<OrgSaasResponse> getOrgSaasList(Integer orgId) {
        // 1. orgId로 org_saas 테이블에서 튜플 조회
        List<OrgSaas> orgSaasList = orgSaasRepository.findByOrgId(orgId);

        // 2. 조회된 orgSaas 데이터에서 config 값을 추출
        List<Integer> configIds = orgSaasList.stream()
                .map(OrgSaas::getId)
                .distinct()  // 중복 제거
                .collect(Collectors.toList());

        // 3. config 값을 사용하여 workspace_config 테이블에서 데이터 조회
        List<Workspace> workspaceList = workspaceRepository.findByIdIn(configIds);

        // 4. configId를 기준으로 Workspace 객체를 맵으로 변환
        Map<Integer, Workspace> workspaceMap = workspaceList.stream()
                .collect(Collectors.toMap(Workspace::getId, workspace -> workspace));

        // 5. 결과 리스트 생성
        return orgSaasList.stream().map(orgSaas -> {
            // Lookup workspace by configId
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
        // spaceId가 "TEMP"인 튜플을 찾음
        List<OrgSaas> tempOrgSaasList = orgSaasRepository.findBySpaceId("TEMP");

        if (tempOrgSaasList.isEmpty()) {
            System.out.println("No entries found with spaceId 'TEMP'");
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
                    optionalWorkspace.ifPresent(workspaceRepository::delete);  // 워크스페이스도 삭제
                }

                System.out.println("Deleted all entries with spaceId 'TEMP' due to DELETE status.");
                return;  // DELETE 처리가 완료되었으므로 함수 종료
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
                orgSaas.setOrgId(originalOrgSaas.getOrgId());
                orgSaas.setSaasId(originalOrgSaas.getSaasId());
                orgSaas.setSpaceId("TEMP");  // 나중에 업데이트될 것이므로 우선 TEMP로 설정
                orgSaas = orgSaasRepository.save(orgSaas);  // 복제된 튜플 저장

                // Workspace도 복제
                Optional<Workspace> originalWorkspaceOpt = workspaceRepository.findById(originalOrgSaas.getId());
                if (originalWorkspaceOpt.isPresent()) {
                    Workspace originalWorkspace = originalWorkspaceOpt.get();
                    workspace = new Workspace();
                    workspace.setId(orgSaas.getId());
                    workspace.setAlias(originalWorkspace.getAlias());
                    workspace.setAdminEmail(originalWorkspace.getAdminEmail());
                    workspace.setApiToken(originalWorkspace.getApiToken());
                    workspace.setWebhookUrl(originalWorkspace.getWebhookUrl());
                    workspace.setRegisterDate(originalWorkspace.getRegisterDate());
                    workspace = workspaceRepository.save(workspace);  // 복제된 워크스페이스 저장
                } else {
                    System.out.println("Workspace for TEMP OrgSaas not found.");
                    continue;
                }
            }

            // org_saas 정보 업데이트
            orgSaas.setSpaceId(driveInfo[0]);  // 드라이브 ID로 업데이트
            orgSaasRepository.save(orgSaas);

            // workspace_config 정보 업데이트
            Optional<Workspace> optionalWorkspace = workspaceRepository.findById(orgSaas.getId());
            if (optionalWorkspace.isPresent()) {
                workspace = optionalWorkspace.get();
                workspace.setSpaceName(driveInfo[1]);  // 드라이브 이름으로 업데이트
                workspace.setApiToken(accessToken);    // 토큰 업데이트
                workspaceRepository.save(workspace);
            }

            System.out.println("Updated OrgSaas and Workspace for Drive ID: " + driveInfo[0]);
        }
    }


}
