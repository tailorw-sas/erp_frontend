package com.kynsof.share.core.infrastructure.specifications;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        // Intentar convertir el valor a LocalDate o LocalDateTime si es necesario.
        try {
            value = LocalDate.parse(value.toString(), dateFormatter);
        } catch (DateTimeParseException ignored) {
            try {
                value = LocalDateTime.parse(value.toString(), dateTimeFormatter);
            } catch (DateTimeParseException ignored2) {
            }
        }

        return switch (criteria.getOperation()) {
            case LIKE -> builder.like(builder.lower(path.as(String.class)), "%" + value.toString().toLowerCase() + "%");
            case EQUALS -> {
                if (value instanceof LocalDate) {
                    yield builder.equal(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.equal(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else {
                    yield builder.equal(path, value);
                }
            }
            case GREATER_THAN -> {
                if (value instanceof LocalDate) {
                    yield builder.greaterThan(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.greaterThan(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else {
                    yield builder.greaterThan(path.as(String.class), value.toString());
                }
            }
            case LESS_THAN -> {
                if (value instanceof LocalDate) {
                    yield builder.lessThan(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.lessThan(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else {
                    yield builder.lessThan(path.as(String.class), value.toString());
                }
            }
            case GREATER_THAN_OR_EQUAL_TO -> {
                if (value instanceof LocalDate) {
                    yield builder.greaterThanOrEqualTo(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.greaterThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else {
                    yield builder.greaterThanOrEqualTo(path.as(String.class), value.toString());
                }
            }
            case LESS_THAN_OR_EQUAL_TO -> {
                if (value instanceof LocalDate) {
                    yield builder.lessThanOrEqualTo(path.as(LocalDate.class), (LocalDate) value);
                } else if (value instanceof LocalDateTime) {
                    yield builder.lessThanOrEqualTo(path.as(LocalDateTime.class), (LocalDateTime) value);
                } else {
                    yield builder.lessThanOrEqualTo(path.as(String.class), value.toString());
                }
            }
            case NOT_EQUALS -> builder.notEqual(path, value);
            case IN -> {
                CriteriaBuilder.In<Object> inClause = builder.in(path);
                if (value instanceof List) {
                    for (Object item : (List<?>) value) {
                        Object finalValue = convertToUUID(item.toString());
                        if (finalValue == null) {
                            finalValue = item.toString();
                        }
                        inClause.value(finalValue);
                    }
                } else {
                    Object finalValue = convertToUUID(value.toString());
                    if (finalValue == null) {
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

    // Método auxiliar para verificar si un String es un UUID válido
    private boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // Método auxiliar para convertir un String a UUID
    private UUID convertToUUID(String str) {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
