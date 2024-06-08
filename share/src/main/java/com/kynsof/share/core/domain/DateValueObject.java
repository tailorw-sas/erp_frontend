package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public abstract class DateValueObject implements Serializable {

    protected final LocalDate value;

    protected DateValueObject() {
        this.value = null;
    }

    protected DateValueObject(LocalDate value) {
        this.value = value;
    }

    public LocalDate value() {
        return value;
    }

    public LocalDate getValue() {
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
        if (obj instanceof DateValueObject instance) {
            return Objects.equals(value, instance.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
