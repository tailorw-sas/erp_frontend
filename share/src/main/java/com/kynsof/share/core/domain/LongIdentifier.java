package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class LongIdentifier implements Serializable {

    protected final Long value;

    protected LongIdentifier() {
        this.value = 0L;
    }

    protected LongIdentifier(Long value) {
        this.value = value;
    }

    public Long value() {
        return value;
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof LongIdentifier instance) {
            return Objects.equals(value, instance.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
