package com.kynsof.share.utils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class UpdateIfNotNull {

    /**
     * Valida si T es diferente de null para actualizar el valor de la variable.
     * @param <T>
     * @param setter
     * @param value 
     */
    public static <T> void updateIfNotNull(Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    /**
     * Valida si el campo no esta nulo o vacio, para poder actualizarlo.
     * @param setter
     * @param value
     * @return 
     */
    public static boolean updateIfStringNotNull(Consumer<String> setter, String value) {
        if (value != null && !value.isEmpty()) {
            setter.accept(value);

            return true;
        }
        return false;
    }

    /**
     * Valida si el campo no esta nulo o vacio, pero ademas diferente del valor actual, para poder
     * actualizar. En caso de actualizarlo, actualiza el valor del update para sennalar que se
     * puede realizar una consulta a BD.
     * @param setter
     * @param newValue
     * @param oldValue
     * @param update
     * @return 
     */
    public static boolean updateIfStringNotNullNotEmptyAndNotEquals(Consumer<String> setter, String newValue, String oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    /**
     * Este metodo es para ser usado en los campos que se actualizar por evento de kafka.
     * Valida si el campo no esta nulo o vacio, pero ademas diferente del valor actual, para poder
     * actualizar. En caso de actualizarlo, actualiza el valor del update para sennalar que se
     * puede realizar una consulta a BD.
     * @param setter
     * @param newValue
     * @param oldValue
     * @param update
     * @param publish
     * @return 
     */
    public static boolean updateIfStringNotNullNotEmptyAndNotEqualsToPublish(Consumer<String> setter, String newValue, String oldValue, Consumer<Integer> update, Consumer<Integer> publish) {
        if (newValue != null && !newValue.isEmpty() && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
            publish.accept(1);

            return true;
        }
        return false;
    }

    public static boolean updateDoubleAndPublish(Consumer<Double> setter, Double newValue, Double oldValue, Consumer<Integer> update, Consumer<Integer> publish) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
            publish.accept(1);

            return true;
        }
        return false;
    }

    public static boolean updateDouble(Consumer<Double> setter, Double newValue, Double oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    public static boolean updateDouble(Consumer<Double> setter, Double newValue, Consumer<Integer> update) {
        if (newValue != null) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    public static boolean updateInteger(Consumer<Integer> setter, Integer newValue, Integer oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    public static boolean updateBoolean(Consumer<Boolean> setter, Boolean newValue, Boolean oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

            return true;
        }
        return false;
    }

    public static boolean updateBooleanAndPublish(Consumer<Boolean> setter, Boolean newValue, Boolean oldValue, Consumer<Integer> update, Consumer<Integer> publish) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
            publish.accept(1);

            return true;
        }
        return false;
    }

    public static void updateLocalDateTime(Consumer<LocalDateTime> setter, LocalDateTime newValue, LocalDateTime oldValue, Consumer<Integer> update){
        if(newValue != null && !newValue.equals(oldValue)){
            setter.accept(newValue);
            update.accept(1);
        }
    }

    public static <T> void updateEntity(Consumer<T> setter, UUID newValue, UUID oldValue, Consumer<Integer> update, Function<UUID, T> findByIdFunction) {
        if (newValue!= null &&!newValue.equals(oldValue)) {
            T entity = findByIdFunction.apply(newValue);
            setter.accept(entity);
            update.accept(1);
        }
    }

}
