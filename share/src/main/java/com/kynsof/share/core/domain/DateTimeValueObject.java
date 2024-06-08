package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class DateTimeValueObject implements Serializable {

    protected final LocalDateTime value;

    protected DateTimeValueObject() {
        this.value = null;
    }

    protected DateTimeValueObject(LocalDateTime value) {
        this.value = value;
    }

    public LocalDateTime value() {
        return value;
    }

    public LocalDateTime getValue() {
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
        if (obj instanceof DateTimeValueObject instance) {
            return Objects.equals(value, instance.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
