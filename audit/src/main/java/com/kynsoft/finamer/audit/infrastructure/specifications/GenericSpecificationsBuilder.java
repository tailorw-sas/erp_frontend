package com.kynsoft.finamer.audit.infrastructure.specifications;


import com.kynsoft.finamer.audit.domain.request.FilterCriteria;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

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
}