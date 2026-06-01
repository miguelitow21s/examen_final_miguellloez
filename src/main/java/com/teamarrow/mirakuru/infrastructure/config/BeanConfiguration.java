package com.teamarrow.mirakuru.infrastructure.config;

import com.teamarrow.mirakuru.application.port.out.AffectedRepository;
import com.teamarrow.mirakuru.application.port.out.MissionRepository;
import com.teamarrow.mirakuru.application.port.out.TeamMemberRepository;
import com.teamarrow.mirakuru.application.port.out.ThreatIntelligenceSource;
import com.teamarrow.mirakuru.application.service.AffectedService;
import com.teamarrow.mirakuru.application.service.MissionService;
import com.teamarrow.mirakuru.application.service.TeamService;
import com.teamarrow.mirakuru.application.service.ThreatIntelligenceService;
import com.teamarrow.mirakuru.domain.service.MissionCoordinationService;
import com.teamarrow.mirakuru.domain.service.StandardMissionCoordinationService;
import com.teamarrow.mirakuru.domain.service.ThreatAssessmentService;
import com.teamarrow.mirakuru.domain.service.WeightedThreatAssessmentService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root that wires the framework-agnostic core (domain + application)
 * into the Spring container.
 *
 * <p>The domain services and the application services carry no Spring annotations
 * on purpose; this configuration is the single place where the infrastructure
 * "plugs them in". Because each application service is exposed by its concrete
 * type but implements several inbound use-case interfaces, Spring injects the
 * same instance wherever any of those ports is required. This keeps the
 * dependency arrows pointing inward: infrastructure knows about the core, never
 * the reverse.</p>
 *
 * <p>Note how {@link #threatIntelligenceService} receives a {@code List} of every
 * {@link ThreatIntelligenceSource} adapter on the classpath: adding a new
 * external feed needs no change here (Open/Closed).</p>
 */
@Configuration(proxyBeanMethods = false)
public class BeanConfiguration {

    // ----- Domain services -----

    @Bean
    public ThreatAssessmentService threatAssessmentService() {
        return new WeightedThreatAssessmentService();
    }

    @Bean
    public MissionCoordinationService missionCoordinationService() {
        return new StandardMissionCoordinationService();
    }

    // ----- Application services (inbound use cases) -----

    @Bean
    public AffectedService affectedService(AffectedRepository repository,
                                           ThreatAssessmentService assessmentService) {
        return new AffectedService(repository, assessmentService);
    }

    @Bean
    public MissionService missionService(MissionRepository missionRepository,
                                         AffectedRepository affectedRepository,
                                         TeamMemberRepository teamMemberRepository,
                                         MissionCoordinationService coordinationService) {
        return new MissionService(missionRepository, affectedRepository,
                teamMemberRepository, coordinationService);
    }

    @Bean
    public TeamService teamService(TeamMemberRepository teamMemberRepository) {
        return new TeamService(teamMemberRepository);
    }

    @Bean
    public ThreatIntelligenceService threatIntelligenceService(List<ThreatIntelligenceSource> sources,
                                                               AffectedRepository affectedRepository) {
        return new ThreatIntelligenceService(sources, affectedRepository);
    }
}
