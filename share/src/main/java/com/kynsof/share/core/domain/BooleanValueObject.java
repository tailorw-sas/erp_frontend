package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class BooleanValueObject implements Serializable {

    protected final Boolean value;

    protected BooleanValueObject() {
        this.value = null;
    }

    protected BooleanValueObject(Boolean value) {
        this.value = value;
    }

    public Boolean value() {
        return value;
    }

    public Boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BooleanValueObject instance) {
            return Objects.equals(value, instance.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
