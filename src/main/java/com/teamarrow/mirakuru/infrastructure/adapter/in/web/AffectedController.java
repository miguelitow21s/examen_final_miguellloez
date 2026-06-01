package com.teamarrow.mirakuru.infrastructure.adapter.in.web;

import com.teamarrow.mirakuru.application.port.in.GetAffectedUseCase;
import com.teamarrow.mirakuru.application.port.in.ListAffectedUseCase;
import com.teamarrow.mirakuru.application.port.in.RegisterAffectedUseCase;
import com.teamarrow.mirakuru.application.port.in.UpdateAffectedUseCase;
import com.teamarrow.mirakuru.domain.model.AffectedIndividual;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.AffectedResponse;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.RegisterAffectedRequest;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto.UpdateAffectedRequest;
import com.teamarrow.mirakuru.infrastructure.adapter.in.web.mapper.AffectedDtoMapper;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Driving adapter that exposes the affected-individual use cases over HTTP.
 *
 * <p>The controller depends only on the inbound ports (the use-case interfaces),
 * never on the application service implementation. It contains no business logic:
 * it converts HTTP into commands, delegates to the core, and converts the result
 * back into a DTO. This thinness is what lets the same domain be exposed through
 * other adapters (a message listener, a CLI, gRPC) without change.</p>
 */
@RestController
@RequestMapping("/api/afectados")
public class AffectedController {

    private final RegisterAffectedUseCase registerAffectedUseCase;
    private final GetAffectedUseCase getAffectedUseCase;
    private final ListAffectedUseCase listAffectedUseCase;
    private final UpdateAffectedUseCase updateAffectedUseCase;
    private final AffectedDtoMapper mapper;

    public AffectedController(RegisterAffectedUseCase registerAffectedUseCase,
                             GetAffectedUseCase getAffectedUseCase,
                             ListAffectedUseCase listAffectedUseCase,
                             UpdateAffectedUseCase updateAffectedUseCase,
                             AffectedDtoMapper mapper) {
        this.registerAffectedUseCase = registerAffectedUseCase;
        this.getAffectedUseCase = getAffectedUseCase;
        this.listAffectedUseCase = listAffectedUseCase;
        this.updateAffectedUseCase = updateAffectedUseCase;
        this.mapper = mapper;
    }

    /** Register a new affected individual. */
    @PostMapping
    public ResponseEntity<AffectedResponse> register(@Valid @RequestBody RegisterAffectedRequest request) {
        AffectedIndividual created = registerAffectedUseCase.register(mapper.toCommand(request));
        return ResponseEntity
                .created(URI.create("/api/afectados/" + created.getId()))
                .body(mapper.toResponse(created));
    }

    /** Look up a single affected individual by id. */
    @GetMapping("/{id}")
    public AffectedResponse getById(@PathVariable String id) {
        return mapper.toResponse(getAffectedUseCase.getById(id));
    }

    /** List every affected individual, ordered by engagement priority. */
    @GetMapping
    public List<AffectedResponse> listAll() {
        return listAffectedUseCase.listAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    /** Update an existing affected individual. */
    @PutMapping("/{id}")
    public AffectedResponse update(@PathVariable String id,
                                   @Valid @RequestBody UpdateAffectedRequest request) {
        return mapper.toResponse(updateAffectedUseCase.update(id, mapper.toCommand(request)));
    }
}
