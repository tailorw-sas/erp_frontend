package com.kynsof.share.core.infrastructure.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class GenericSpecification<T> implements Specification<T> {
    private final SearchCriteria criteria;

    public GenericSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String[] keys = criteria.getKey().split("\\.");
        Path<Object> path = root.get(keys[0]);

        if (criteria.getKey().contains(".")) {
            path = root.get(keys[0]);
            for (int i = 1; i < keys.length; i++) {
                path = path.get(keys[i]);
            }
        } else {
            path = root.get(criteria.getKey());
        }

        // Verificar si el valor es un UUID y convertirlo a UUID si es necesario.
        Object value = criteria.getValue();

        if (value instanceof String && isValidUUID((String) value)) {
            value = UUID.fromString((String) value);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE; // Define el formato esperado

        try {
            value = LocalDate.parse(value.toString(), formatter);
        } catch (DateTimeParseException ignored) {

        }

        return switch (criteria.getOperation()) {
            case LIKE -> builder.like(builder.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%");
            case EQUALS -> builder.equal(path, value);
            case GREATER_THAN -> builder.greaterThan(path.as(String.class), value.toString());
            case LESS_THAN -> builder.lessThan(path.as(String.class), value.toString());
            case GREATER_THAN_OR_EQUAL_TO -> builder.greaterThanOrEqualTo(path.as(String.class), value.toString());
            case LESS_THAN_OR_EQUAL_TO -> builder.lessThanOrEqualTo(path.as(String.class), value.toString());
            case NOT_EQUALS -> builder.notEqual(path, value);
//            case IN -> {
//                CriteriaBuilder.In<Object> inClause = builder.in(path);
//                if (value instanceof List) {
//                    ((List<?>) value).forEach(item -> {
//                        UUID uuid = convertToUUID(item.toString());
//                        if (uuid != null) {
//                            inClause.value(uuid);
//                        }
//                    });
//                } else {
//                    UUID uuid = convertToUUID(value.toString());
//                    if (uuid != null) {
//                        inClause.value(uuid);
//                    }
//                }
//                yield inClause;
//            }
            case IN -> {
                CriteriaBuilder.In<Object> inClause = builder.in(path);
                if (value instanceof List) {
                    for (Object item : (List<?>) value) {
                        // Intenta convertir cada elemento a UUID, si es posible. Si no, usa el valor tal cual.
                        Object finalValue = convertToUUID(item.toString());
                        if (finalValue == null) {
                            // Si el valor no es un UUID válido, lo usamos como String.
                            finalValue = item.toString();
                        }
                        inClause.value(finalValue);
                    }
                } else {
                    // Trata de convertir el valor individual a UUID, si es posible.
                    Object finalValue = convertToUUID(value.toString());
                    if (finalValue == null) {
                        // Si el valor no es un UUID válido, lo usamos como String.
                        finalValue = value.toString();
                    }
                    inClause.value(finalValue);
                }
                yield inClause;
            }
            case NOT_IN -> {
                CriteriaBuilder.In<Object> inClause = builder.in(path);
                if (value instanceof List) {
                    ((List<?>) value).forEach(item -> {
                        UUID uuid = convertToUUID(item.toString());
                        if (uuid != null) {
                            inClause.value(uuid);
                        }
                    });
                } else {
                    UUID uuid = convertToUUID(value.toString());
                    if (uuid != null) {
                        inClause.value(uuid);
                    }
                }
                yield builder.not(inClause);
            }

            case IS_NULL -> builder.isNull(path);
            case IS_NOT_NULL -> builder.isNotNull(path);
            case IS_TRUE -> builder.isTrue(path.as(Boolean.class));
            case IS_FALSE -> builder.isFalse(path.as(Boolean.class));
            default -> throw new IllegalArgumentException("Operación no soportada: " + criteria.getOperation());
        };
    }

    private UUID convertToUUID(String str) {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private boolean isValidUUID(String str) {
        try {
            UUID uuid = UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
