package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ManageHotelCustomRepositoryImpl implements ManageHotelCustomRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManageHotel> findAllCustom(Specification<ManageHotel> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageHotel> root = query.from(ManageHotel.class);
        Join<ManageHotel, ManageCountry> manageCountryJoin = root.join("manageCountry", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> managerLanguageJoin = manageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageHotel, ManageCityState> manageCityStateJoin = root.join("manageCityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> manageCityStateCountryJoin = manageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> manageCityStateCountryLanguageJoin = manageCityStateCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> manageCityStateTimeZoneJoin = manageCityStateJoin.join("timeZone", JoinType.LEFT);
        Join<ManageHotel, ManagerCurrency> managerCurrencyJoin = root.join("manageCurrency", JoinType.LEFT);
        Join<ManageHotel, ManageRegion> manageRegionJoin = root.join("manageRegion", JoinType.LEFT);
        Join<ManageHotel, ManageTradingCompanies> manageTradingCompaniesJoin = root.join("manageTradingCompanies", JoinType.LEFT);

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.add(root.get("code"));
        selections.add(root.get("status"));
        selections.add(root.get("description"));
        selections.add(root.get("name"));
        selections.add(root.get("createdAt"));
        selections.add(root.get("updatedAt"));
        selections.add(root.get("babelCode"));

        selections.add(manageCountryJoin.get("id"));
        selections.add(manageCountryJoin.get("code"));
        selections.add(manageCountryJoin.get("name"));
        selections.add(manageCountryJoin.get("description"));
        selections.add(manageCountryJoin.get("dialCode"));
        selections.add(manageCountryJoin.get("iso3"));
        selections.add(manageCountryJoin.get("isDefault"));

        selections.add(managerLanguageJoin.get("id"));
        selections.add(managerLanguageJoin.get("code"));
        selections.add(managerLanguageJoin.get("status"));
        selections.add(managerLanguageJoin.get("description"));
        selections.add(managerLanguageJoin.get("name"));
        selections.add(managerLanguageJoin.get("isEnabled"));
        selections.add(managerLanguageJoin.get("defaults"));
        selections.add(managerLanguageJoin.get("createdAt"));
        selections.add(managerLanguageJoin.get("updatedAt"));

        selections.add(manageCountryJoin.get("status"));
        selections.add(manageCountryJoin.get("createdAt"));
        selections.add(manageCountryJoin.get("updateAt"));
        selections.add(manageCountryJoin.get("deleteAt"));

        selections.add(manageCityStateJoin.get("id"));
        selections.add(manageCityStateJoin.get("code"));
        selections.add(manageCityStateJoin.get("name"));
        selections.add(manageCityStateJoin.get("description"));

        selections.add(manageCityStateCountryJoin.get("id"));
        selections.add(manageCityStateCountryJoin.get("code"));
        selections.add(manageCityStateCountryJoin.get("name"));
        selections.add(manageCityStateCountryJoin.get("description"));
        selections.add(manageCityStateCountryJoin.get("dialCode"));
        selections.add(manageCityStateCountryJoin.get("iso3"));
        selections.add(manageCityStateCountryJoin.get("isDefault"));

        selections.add(manageCityStateCountryLanguageJoin.get("id"));
        selections.add(manageCityStateCountryLanguageJoin.get("code"));
        selections.add(manageCityStateCountryLanguageJoin.get("status"));
        selections.add(manageCityStateCountryLanguageJoin.get("description"));
        selections.add(manageCityStateCountryLanguageJoin.get("name"));
        selections.add(manageCityStateCountryLanguageJoin.get("isEnabled"));
        selections.add(manageCityStateCountryLanguageJoin.get("defaults"));
        selections.add(manageCityStateCountryLanguageJoin.get("createdAt"));
        selections.add(manageCityStateCountryLanguageJoin.get("updatedAt"));

        selections.add(manageCityStateCountryJoin.get("status"));
        selections.add(manageCityStateCountryJoin.get("createdAt"));
        selections.add(manageCityStateCountryJoin.get("updateAt"));
        selections.add(manageCityStateCountryJoin.get("deleteAt"));

        selections.add(manageCityStateTimeZoneJoin.get("id"));
        selections.add(manageCityStateTimeZoneJoin.get("code"));
        selections.add(manageCityStateTimeZoneJoin.get("name"));
        selections.add(manageCityStateTimeZoneJoin.get("description"));
        selections.add(manageCityStateTimeZoneJoin.get("elapse"));
        selections.add(manageCityStateTimeZoneJoin.get("status"));
        selections.add(manageCityStateTimeZoneJoin.get("createdAt"));
        selections.add(manageCityStateTimeZoneJoin.get("updateAt"));

        selections.add(manageCityStateJoin.get("status"));
        selections.add(manageCityStateJoin.get("createdAt"));
        selections.add(manageCityStateJoin.get("updateAt"));

        selections.add(root.get("city"));
        selections.add(root.get("address"));

        selections.add(managerCurrencyJoin.get("id"));
        selections.add(managerCurrencyJoin.get("code"));
        selections.add(managerCurrencyJoin.get("name"));
        selections.add(managerCurrencyJoin.get("description"));
        selections.add(managerCurrencyJoin.get("status"));
        selections.add(managerCurrencyJoin.get("createdAt"));
        selections.add(managerCurrencyJoin.get("updateAt"));

        selections.add(manageRegionJoin.get("id"));
        selections.add(manageRegionJoin.get("code"));
        selections.add(manageRegionJoin.get("status"));
        selections.add(manageRegionJoin.get("description"));
        selections.add(manageRegionJoin.get("name"));
        selections.add(manageRegionJoin.get("createdAt"));
        selections.add(manageRegionJoin.get("updatedAt"));

        selections.add(manageTradingCompaniesJoin.get("id"));
        selections.add(manageTradingCompaniesJoin.get("code"));
        selections.add(manageTradingCompaniesJoin.get("status"));
        selections.add(manageTradingCompaniesJoin.get("description"));
        selections.add(manageTradingCompaniesJoin.get("company"));
        selections.add(manageTradingCompaniesJoin.get("createdAt"));
        selections.add(manageTradingCompaniesJoin.get("updatedAt"));
        selections.add(manageTradingCompaniesJoin.get("cif"));
        selections.add(manageTradingCompaniesJoin.get("address"));
        selections.add(manageTradingCompaniesJoin.get("city"));
        selections.add(manageTradingCompaniesJoin.get("zipCode"));
        selections.add(manageTradingCompaniesJoin.get("innsistCode"));
        selections.add(manageTradingCompaniesJoin.get("isApplyInvoice"));

        selections.add(root.get("applyByTradingCompany"));
        selections.add(root.get("prefixToInvoice"));
        selections.add(root.get("isVirtual"));
        selections.add(root.get("requiresFlatRate"));
        selections.add(root.get("isApplyByVCC"));
        selections.add(root.get("autoApplyCredit"));

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

        List<ManageHotel> results = tuples.stream()
                .map(tuple -> {
                   return new ManageHotel(
                           tuple.get(0, UUID.class),
                           tuple.get(1, String.class),
                           tuple.get(2, Status.class),
                           tuple.get(3, String.class),
                           tuple.get(4, String.class),
                           tuple.get(5, LocalDateTime.class),
                           tuple.get(6, LocalDateTime.class),
                           tuple.get(7, String.class),
                            new ManageCountry(
                                    tuple.get(8, UUID.class),
                                    tuple.get(9, String.class),
                                    tuple.get(10, String.class),
                                    tuple.get(11, String.class),
                                    tuple.get(12, String.class),
                                    tuple.get(13, String.class),
                                    tuple.get(14, Boolean.class),
                                    new ManagerLanguage(
                                            tuple.get(15, UUID.class),
                                            tuple.get(16, String.class),
                                            tuple.get(17, Status.class),
                                            tuple.get(18, String.class),
                                            tuple.get(19, String.class),
                                            tuple.get(20, Boolean.class),
                                            tuple.get(21, Boolean.class),
                                            tuple.get(22, LocalDateTime.class),
                                            tuple.get(23, LocalDateTime.class)
                                    ),
                                    tuple.get(24, Status.class),
                                    tuple.get(25, LocalDateTime.class),
                                    tuple.get(26, LocalDateTime.class),
                                    tuple.get(27, LocalDateTime.class),
                                    null
                            ),
                           new ManageCityState(
                                   tuple.get(28, UUID.class),
                                   tuple.get(29, String.class),
                                   tuple.get(30, String.class),
                                   tuple.get(31, String.class),
                                   new ManageCountry(
                                           tuple.get(32, UUID.class),
                                           tuple.get(33, String.class),
                                           tuple.get(34, String.class),
                                           tuple.get(35, String.class),
                                           tuple.get(36, String.class),
                                           tuple.get(37, String.class),
                                           tuple.get(38, Boolean.class),
                                           new ManagerLanguage(
                                                   tuple.get(39, UUID.class),
                                                   tuple.get(40, String.class),
                                                   tuple.get(41, Status.class),
                                                   tuple.get(42, String.class),
                                                   tuple.get(43, String.class),
                                                   tuple.get(44, Boolean.class),
                                                   tuple.get(45, Boolean.class),
                                                   tuple.get(46, LocalDateTime.class),
                                                   tuple.get(47, LocalDateTime.class)
                                           ),
                                           tuple.get(48, Status.class),
                                           tuple.get(49, LocalDateTime.class),
                                           tuple.get(50, LocalDateTime.class),
                                           tuple.get(51, LocalDateTime.class),
                                           null
                                   ),
                                   new ManagerTimeZone(
                                           tuple.get(52, UUID.class),
                                           tuple.get(53, String.class),
                                           tuple.get(54, String.class),
                                           tuple.get(55, String.class),
                                           tuple.get(56, Double.class),
                                           tuple.get(57, Status.class),
                                           tuple.get(58, LocalDateTime.class),
                                           tuple.get(59, LocalDateTime.class)
                                   ),
                                   tuple.get(60, Status.class),
                                   tuple.get(61, LocalDateTime.class),
                                   tuple.get(62, LocalDateTime.class)
                           ),
                           tuple.get(63, String.class),
                           tuple.get(64, String.class),
                           new ManagerCurrency(
                                   tuple.get(65, UUID.class),
                                   tuple.get(66, String.class),
                                   tuple.get(67, String.class),
                                   tuple.get(68, String.class),
                                   tuple.get(69, Status.class),
                                   tuple.get(70, LocalDateTime.class),
                                   tuple.get(71, LocalDateTime.class)
                           ),
                           new ManageRegion(
                                   tuple.get(72, UUID.class),
                                   tuple.get(73, String.class),
                                   tuple.get(74, Status.class),
                                   tuple.get(75, String.class),
                                   tuple.get(76, String.class),
                                   tuple.get(77, LocalDateTime.class),
                                   tuple.get(78, LocalDateTime.class)
                           ),
                           new ManageTradingCompanies(
                                   tuple.get(79, UUID.class),
                                   tuple.get(80, String.class),
                                   tuple.get(81, Status.class),
                                   tuple.get(82, String.class),
                                   tuple.get(83, String.class),
                                   tuple.get(84, LocalDateTime.class),
                                   tuple.get(85, LocalDateTime.class),
                                   tuple.get(86, String.class),
                                   tuple.get(87, String.class),
                                   null,
                                   null,
                                   tuple.get(88, String.class),
                                   tuple.get(89, String.class),
                                   tuple.get(90, String.class),
                                   tuple.get(91, Boolean.class)
                           ),
                           tuple.get(92, Boolean.class),
                           tuple.get(93, String.class),
                           tuple.get(94, Boolean.class),
                           tuple.get(95, Boolean.class),
                           tuple.get(96, Boolean.class),
                           tuple.get(97, Boolean.class)
                   );
                }).collect(Collectors.toList());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ManageHotel> countRoot = countQuery.from(ManageHotel.class);
        countQuery.select(cb.count(countRoot));

        if (specification != null) {
            Predicate countPredicate = specification.toPredicate(countRoot, countQuery, cb);
            countQuery.where(countPredicate);
        }
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
