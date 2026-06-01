package com.teamarrow.mirakuru.infrastructure.config;

import com.teamarrow.mirakuru.application.port.out.AffectedRepository;
import com.teamarrow.mirakuru.application.service.AffectedService;
import com.teamarrow.mirakuru.domain.service.ThreatAssessmentService;
import com.teamarrow.mirakuru.domain.service.WeightedThreatAssessmentService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Composition root that wires the framework-agnostic core (domain + application)
 * into the Spring container.
 *
 * <p>The domain service and the application service carry no Spring annotations
 * on purpose; this configuration is the single place where the infrastructure
 * "plugs them in". Because the {@link AffectedService} bean is exposed by its
 * concrete type but implements all four inbound use-case interfaces, Spring can
 * inject it wherever any of those ports is required. This keeps the dependency
 * arrows pointing inward: infrastructure knows about the core, never the
 * reverse.</p>
 */
@Configuration(proxyBeanMethods = false)
public class BeanConfiguration {

    @Bean
    public ThreatAssessmentService threatAssessmentService() {
        return new WeightedThreatAssessmentService();
    }

    @Bean
    public AffectedService affectedService(AffectedRepository repository,
                                           ThreatAssessmentService assessmentService) {
        return new AffectedService(repository, assessmentService);
    }
}
