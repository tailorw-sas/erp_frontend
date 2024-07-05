package com.kynsof.share.core.infrastructure.specifications;

import com.kynsof.share.core.domain.request.FilterCriteria;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericSpecificationsBuilder<T> implements Specification<T> {

    private final List<SearchCriteria> params;

    public GenericSpecificationsBuilder(List<FilterCriteria> filterCriteria) {
        this.params = filterCriteria.stream()
                .map(filterCriteriaItem -> new SearchCriteria(
                        filterCriteriaItem.getKey(),
                        filterCriteriaItem.getOperator(),
                        filterCriteriaItem.getValue(),
                        filterCriteriaItem.getLogicalOperation()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> andPredicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        // Verifica si el campo 'deleted' existe en la entidad
//        if (doesClassContainField(root.getJavaType(), "deleted")) {
//            andPredicates.add(cb.isFalse(root.get("deleted")));
//        }

        for (SearchCriteria criteria : params) {
            Predicate predicate = new GenericSpecification<T>(criteria).toPredicate(root, query, cb);
            if (predicate != null) {
                if (criteria.getLogicalOperation() == LogicalOperation.AND) {
                    andPredicates.add(predicate);
                } else {
                    orPredicates.add(predicate);
                }
            }
        }

        Predicate andPredicate = cb.and(andPredicates.toArray(Predicate[]::new));
        Predicate orPredicate = cb.or(orPredicates.toArray(Predicate[]::new));

        if (!orPredicates.isEmpty()) {
            return cb.and(andPredicate, orPredicate);
        } else {
            return andPredicate;
        }
    }

    private boolean doesClassContainField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

    private Path<Object> getPath(Root<T> root, String fieldName) {
        String[] keys = fieldName.split("\\.");
        Path<Object> path = root.get(keys[0]);
        for (int i = 1; i < keys.length; i++) {
            path = path.get(keys[i]);
        }
        return path;
    }
}