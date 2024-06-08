package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.time.MonthDay;
import java.util.Objects;

public class MonthDayValueObject implements Serializable {

    protected final MonthDay value;

    protected MonthDayValueObject() {
        this.value = null;
    }

    protected MonthDayValueObject(MonthDay value) {
        this.value = value;
    }

    public MonthDay value() {
        return value;
    }

    public MonthDay getValue() {
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
        if (obj instanceof MonthDayValueObject instance) {
            return Objects.equals(value, instance.value);
        }
        return false;
    }
}
