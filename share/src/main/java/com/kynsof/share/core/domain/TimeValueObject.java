package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

public abstract class TimeValueObject implements Serializable {

    protected final LocalTime value;

    protected TimeValueObject() {
        this.value = null;
    }

    protected TimeValueObject(LocalTime value) {
        this.value = value;
    }

    public LocalTime value() {
        return value;
    }

    public LocalTime getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Objects.toString(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TimeValueObject instance) {
            return Objects.equals(value, instance.value);
        }
        return false;
    }
}
