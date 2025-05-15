package com.kynsoft.finamer.payment.infrastructure.repository.query.custom.implementations;

import com.kynsoft.finamer.payment.infrastructure.identity.*;
import com.kynsoft.finamer.payment.infrastructure.repository.query.custom.ManageEmployeeCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ManageEmployeeCustomRepositoryImpl implements ManageEmployeeCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ManageEmployee> findByIdCustom(UUID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.add(root.get("firstName"));
        selections.add(root.get("lastName"));
        selections.add(root.get("email"));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        ManageEmployee result = new ManageEmployee(
                tuple.get(0, UUID.class),
                tuple.get(1, String.class),
                tuple.get(1, String.class),
                tuple.get(1, String.class),
                null,
                null
        );

        List<ManageAgency> agencies = this.getAgenciesByEmployeeId(id);
        List<ManageHotel> hotels = this.getHotelsByEmployeeId(id);

        result.setManageAgencyList(agencies);
        result.setManageHotelList(hotels);

        return Optional.of(result);
    }

    private List<ManageAgency> getAgenciesByEmployeeId(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageAgency> agencyJoin = root.join("manageAgencyList", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> agencyTypeJoin = agencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> clientJoin = agencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> countryJoin = agencyJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManageLanguage> countryManageLanguageJoin = countryJoin.join("managerLanguage", JoinType.LEFT);


        List<Selection<?>> selections = new ArrayList<>();
        selections.add(agencyJoin.get("id"));
        selections.add(agencyJoin.get("code"));
        selections.add(agencyJoin.get("name"));
        selections.add(agencyJoin.get("status"));

        selections.add(agencyTypeJoin.get("id"));
        selections.add(agencyTypeJoin.get("code"));
        selections.add(agencyTypeJoin.get("status"));
        selections.add(agencyTypeJoin.get("name"));

        selections.add(clientJoin.get("id"));
        selections.add(clientJoin.get("code"));
        selections.add(clientJoin.get("status"));
        selections.add(clientJoin.get("name"));

        selections.add(countryJoin.get("id"));
        selections.add(countryJoin.get("code"));
        selections.add(countryJoin.get("name"));
        selections.add(countryJoin.get("description"));
        selections.add(countryJoin.get("isDefault"));
        selections.add(countryJoin.get("status"));

        selections.add(countryManageLanguageJoin.get("id"));
        selections.add(countryManageLanguageJoin.get("code"));
        selections.add(countryManageLanguageJoin.get("name"));
        selections.add(countryManageLanguageJoin.get("defaults"));
        selections.add(countryManageLanguageJoin.get("status"));
        selections.add(countryManageLanguageJoin.get("createdAt"));
        selections.add(countryManageLanguageJoin.get("updatedAt"));

        selections.add(countryJoin.get("createdAt"));
        selections.add(countryJoin.get("updateAt"));
        selections.add(countryJoin.get("deleteAt"));
        selections.add(countryJoin.get("iso3"));

        selections.add(agencyJoin.get("createdAt"));
        selections.add(agencyJoin.get("updatedAt"));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<ManageAgency> results = tuples.stream()
                .map(this::convertTupleToManageAgency)
                .collect(Collectors.toList());

        return results;
    }

    private ManageAgency convertTupleToManageAgency(Tuple tuple){
        int i = 0;
        return new ManageAgency(
                tuple.get(i++, UUID.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                (tuple.get(i, UUID.class) != null) ? new ManageAgencyType(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class)
                ) : skip(i += 4),
                (tuple.get(i, UUID.class) != null) ? new ManageClient(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class)
                ) : skip( i+= 4),
                (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Boolean.class),
                        tuple.get(i++, String.class),
                        (tuple.get(i, UUID.class) != null) ? new ManageLanguage(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip( i+= 7),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, String.class)
                ) : skip( i+= 17 ),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class)
        );
    }

    private List<ManageHotel> getHotelsByEmployeeId(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageHotel> hotelJoin = root.join("manageHotelList");

        List<Selection<?>> selections = new ArrayList<>();

        selections.add(hotelJoin.get("id"));
        selections.add(hotelJoin.get("code"));
        selections.add(hotelJoin.get("deleted"));
        selections.add(hotelJoin.get("name"));
        selections.add(hotelJoin.get("status"));
        selections.add(hotelJoin.get("applyByTradingCompany"));
        selections.add(hotelJoin.get("manageTradingCompany"));
        selections.add(hotelJoin.get("autoApplyCredit"));
        selections.add(hotelJoin.get("createdAt"));
        selections.add(hotelJoin.get("updatedAt"));
        selections.add(hotelJoin.get("deletedAt"));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<ManageHotel> results = tuples.stream()
                .map(this::convertTupleToManageHotel)
                .collect(Collectors.toList());

        return results;
    }

    private ManageHotel convertTupleToManageHotel(Tuple tuple){
        int i = 0;
        return new ManageHotel(
                tuple.get(i++, UUID.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, UUID.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class)
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T skip(int i) {
        return null;
    }
}
