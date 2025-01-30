package com.kynsoft.finamer.payment.infrastructure.identity.projection;

import lombok.Getter;

import java.util.UUID;

@Getter
public class PaymentStatusProjection {
    // Métodos Getter
    private UUID id;
    private String code;
    private String name;
    private boolean confirmed;
    private Boolean applied;
    private boolean cancelled;
    private boolean transit;
    private String status;

    // Constructor
    public PaymentStatusProjection(
            UUID id,
            String code,
            String name,
            boolean confirmed,
            Boolean applied,
            boolean cancelled,
            boolean transit,
            String status
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.confirmed = confirmed;
        this.applied = applied;
        this.cancelled = cancelled;
        this.transit = transit;
        this.status = status;
    }

    // Métodos Setter
    public void setId(UUID id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setApplied(Boolean applied) {
        this.applied = applied;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setTransit(boolean transit) {
        this.transit = transit;
    }

    private void setStatus(String status) {
        this.status = status;
    }
}