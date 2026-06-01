package com.teamarrow.mirakuru.infrastructure.adapter.in.web;

import com.teamarrow.mirakuru.application.port.in.AssignOperativeUseCase;
import com.teamarrow.mirakuru.application.port.in.GetMissionUseCase;
import com.teamarrow.mirakuru.application.port.in.LaunchMissionUseCase;
import com.teamarrow.mirakuru.application.port.in.ListMissionsUseCase;
import com.teamarrow.mirakuru.application.port.in.PlanMissionUseCase;
import com.teamarrow.mirakuru.domain.model.mission.Mission;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.AssignOperativeRequest;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.MissionResponse;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.PlanMissionRequest;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.mapper.MissionDtoMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving adapter that exposes mission coordination over HTTP. Like the affected
 * controller, it depends only on the inbound ports and holds no business logic —
 * a second driving adapter onto the same hexagon, proving the core is reusable.
 */
@RestController
@RequestMapping("/api/misiones")
public class MissionController {

    private final PlanMissionUseCase planMissionUseCase;
    private final AssignOperativeUseCase assignOperativeUseCase;
    private final LaunchMissionUseCase launchMissionUseCase;
    private final GetMissionUseCase getMissionUseCase;
    private final ListMissionsUseCase listMissionsUseCase;
    private final MissionDtoMapper mapper;

    public MissionController(PlanMissionUseCase planMissionUseCase,
                            AssignOperativeUseCase assignOperativeUseCase,
                            LaunchMissionUseCase launchMissionUseCase,
                            GetMissionUseCase getMissionUseCase,
                            ListMissionsUseCase listMissionsUseCase,
                            MissionDtoMapper mapper) {
        this.planMissionUseCase = planMissionUseCase;
        this.assignOperativeUseCase = assignOperativeUseCase;
        this.launchMissionUseCase = launchMissionUseCase;
        this.getMissionUseCase = getMissionUseCase;
        this.listMissionsUseCase = listMissionsUseCase;
        this.mapper = mapper;
    }

    /** Open a new mission against a target. Operatives required are derived from the threat. */
    @PostMapping
    public ResponseEntity<MissionResponse> plan(@Valid @RequestBody PlanMissionRequest request) {
        Mission mission = planMissionUseCase.plan(mapper.toCommand(request));
        return ResponseEntity
                .created(URI.create("/api/misiones/" + mission.getId()))
                .body(mapper.toResponse(mission));
    }

    /** Assign a team member (by code name) to a mission. */
    @PostMapping("/{id}/operativos")
    public MissionResponse assign(@PathVariable String id,
                                  @Valid @RequestBody AssignOperativeRequest request) {
        Mission mission = assignOperativeUseCase.assign(id, request.operativeCodeName());
        return mapper.toResponse(mission);
    }

    /** Launch a fully-staffed mission. */
    @PostMapping("/{id}/lanzar")
    public MissionResponse launch(@PathVariable String id) {
        return mapper.toResponse(launchMissionUseCase.launch(id));
    }

    /** Read a single mission. */
    @GetMapping("/{id}")
    public MissionResponse getById(@PathVariable String id) {
        return mapper.toResponse(getMissionUseCase.getById(id));
    }

    /** List every mission. */
    @GetMapping
    public List<MissionResponse> listAll() {
        return listMissionsUseCase.listAll().stream()
                .map(mapper::toResponse)
                .toList();
    }
}
