package com.kynsof.share.utils;

import java.util.function.Consumer;

public class UpdateFields {

    public static boolean updateString(Consumer<String> setter, String newValue, String oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
            return true;
        }
        return false;
    }

}
