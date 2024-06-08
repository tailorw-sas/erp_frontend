package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.util.Objects;

public abstract class IntegerValueObject implements Serializable {

    protected Integer value;

    protected IntegerValueObject(Integer value) {
        this.value = value;
    }

    protected IntegerValueObject() {
    }

    public Integer value() {
        return Objects.requireNonNullElse(this.value, 0);
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(this.value());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof IntegerValueObject instance) {
            return Objects.equals(value, instance.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
