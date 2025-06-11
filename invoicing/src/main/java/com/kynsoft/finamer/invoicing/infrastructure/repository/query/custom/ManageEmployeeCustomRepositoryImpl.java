package com.kynsoft.finamer.invoicing.infrastructure.repository.query.custom;

import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageEmployee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ManageEmployeeCustomRepositoryImpl implements ManageEmployeeCustomRepository{
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Optional<ManageEmployee> findByIdWithoutRelations(UUID employeeId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.add(root.get("firstName"));
        selections.add(root.get("lastName"));
        selections.add(root.get("email"));
        selections.add(root.get("phoneExtension"));

        query.multiselect(selections.toArray(new Selection[0]));
        query.where(cb.equal(root.get("id"), employeeId));

        try{
            Tuple tuple = entityManager.createQuery(query).getSingleResult();

            ManageEmployee employee = new ManageEmployee(
                    tuple.get(0, UUID.class),
                    tuple.get(1, String.class),
                    tuple.get(2, String.class),
                    tuple.get(3, String.class),
                    tuple.get(4, String.class),
                    null,
                    null);

            return Optional.of(employee);
        }catch (Exception ex){
            return Optional.empty();
        }
    }
}
