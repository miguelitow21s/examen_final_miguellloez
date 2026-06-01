package com.teamarrow.mirakuru.application.port.in.command;

/**
 * Immutable command carrying the raw input needed to register a new affected
 * individual.
 *
 * <p>Commands are the contract of the inbound ports. They deliberately use
 * primitive/raw types instead of domain Value Objects: this keeps the
 * application boundary independent of how the domain models its internals, and
 * the application service is responsible for turning these primitives into valid
 * Value Objects (and surfacing any validation error as a domain exception).</p>
 */
public record RegisterAffectedCommand(
        String codeName,
        int mirakuruSaturation,
        int aggressionIndex,
        String locationSector,
        double latitude,
        double longitude) {
}
