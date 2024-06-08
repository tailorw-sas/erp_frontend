package com.kynsof.share.core.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public abstract class Base64ValueObject implements Serializable {

    protected final byte[] value;

    protected Base64ValueObject() {
        this.value = null;
    }

    protected Base64ValueObject(byte[] value) {
        this.value = value;
    }

    public byte[] value() {
        return value;
    }

    public byte[] getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.value());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Base64ValueObject instance) {
            return Arrays.equals(value, instance.value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
