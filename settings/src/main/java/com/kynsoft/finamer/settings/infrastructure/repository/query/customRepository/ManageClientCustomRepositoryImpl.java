package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageAgency;
import com.kynsoft.finamer.settings.infrastructure.identity.ManageClient;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ManageClientCustomRepositoryImpl implements ManageClientCustomRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManageClient> findAllCustom(Specification<ManageClient> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageClient> root = query.from(ManageClient.class);

        List<Selection<?>> selections = this.getSelections(root);

        query.multiselect(selections.toArray(new Selection[0]));

        if(specification != null){
            Predicate predicate = specification.toPredicate(root, query, cb);
            query.where(predicate);
        }

        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int)pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Tuple> tuples = typedQuery.getResultList();

        List<ManageClient> results = tuples.stream()
                .map(this::convertTupleToManageClient)
                .collect(Collectors.toList());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ManageClient> countRoot = countQuery.from(ManageClient.class);
        countQuery.select(cb.count(countRoot));

        if (specification != null) {
            Predicate countPredicate = specification.toPredicate(countRoot, countQuery, cb);
            countQuery.where(countPredicate);
        }
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Optional<ManageClient> findByIdCustom(UUID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageClient> root = query.from(ManageClient.class);

        List<Selection<?>> selections = this.getSelections(root);

        query.where(cb.equal(root.get("id"), id));

        query.multiselect(selections.toArray(new Selection[0]));

        try {
            Tuple tuple = entityManager.createQuery(query).getSingleResult();
            ManageClient client = this.convertTupleToManageClient(tuple);
            return Optional.of(client);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private List<Selection<?>> getSelections(Root<ManageClient> root){
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.add(root.get("code"));
        selections.add(root.get("name"));
        selections.add(root.get("description"));
        selections.add(root.get("status"));
        selections.add(root.get("isNightType"));
        selections.add(root.get("createdAt"));
        selections.add(root.get("updateAt"));

        return selections;
    }

    private ManageClient convertTupleToManageClient(Tuple tuple){
        return new ManageClient(
                tuple.get(0, UUID.class),
                tuple.get(1, String.class),
                tuple.get(2, String.class),
                tuple.get(3, String.class),
                tuple.get(4, Status.class),
                tuple.get(5, Boolean.class)
        );
    }
}
