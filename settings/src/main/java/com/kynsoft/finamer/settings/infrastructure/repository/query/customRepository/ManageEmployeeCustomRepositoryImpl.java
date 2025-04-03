package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.*;
import com.kynsoft.finamer.settings.infrastructure.projections.ManageDepartmentGroupProjection;
import com.kynsoft.finamer.settings.infrastructure.projections.ManageEmployeeProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.query.QueryUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ManageEmployeeCustomRepositoryImpl implements ManageEmployeeCustomRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManageEmployeeProjection> findAllCustom(Specification<ManageEmployee> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageDepartmentGroupProjection> departmentGroupJoin = root.join("departmentGroup", JoinType.LEFT);
        //Join<ManageEmployee, ManagePermission> permissionListJoin = root.join("managePermissionList", JoinType.LEFT);
        //Join<ManageEmployee, ManageAgency> manageAgencyListJoin = root.join("manageAgencyList", JoinType.LEFT);
        //Join<ManageEmployee, ManageHotel> manageHotelListJoin = root.join("manageHotelList", JoinType.LEFT);
        //Join<ManageEmployee, ManageTradingCompanies> manageTradingCompaniesListJoin = root.join("manageTradingCompaniesList", JoinType.LEFT);
        //Join<ManageEmployee, ManageReport> manageReportListJoin = root.join("manageReportList", JoinType.LEFT);


        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));

        //Department Group
        selections.add(departmentGroupJoin.get("id"));
        selections.add(departmentGroupJoin.get("code"));
        selections.add(departmentGroupJoin.get("name"));
        selections.add(departmentGroupJoin.get("description"));
        selections.add(departmentGroupJoin.get("status"));

        //Permission List
//        selections.add(permissionListJoin.get("id"));
//        selections.add(permissionListJoin.get("code"));
//        selections.add(permissionListJoin.get("status"));
//        selections.add(permissionListJoin.get("description"));
//        selections.add(permissionListJoin.get("name"));
//        selections.add(permissionListJoin.get("isHighRisk"));
//
//        //ManageAgency List
//        selections.add(manageAgencyListJoin.get("id"));
//        selections.add(manageAgencyListJoin.get("code"));
//        selections.add(manageAgencyListJoin.get("status"));
//        selections.add(manageAgencyListJoin.get("name"));
//
//        //ManageHotel List
//        selections.add(manageHotelListJoin.get("id"));
//        selections.add(manageHotelListJoin.get("code"));
//        selections.add(manageHotelListJoin.get("status"));
//        selections.add(manageHotelListJoin.get("name"));
//
//        //ManageTradingCompany List
//        selections.add(manageHotelListJoin.get("id"));
//        selections.add(manageHotelListJoin.get("code"));
//        selections.add(manageHotelListJoin.get("status"));
//        selections.add(manageHotelListJoin.get("company"));
//
//        //ManageTradingCompany List
//        selections.add(manageReportListJoin.get("id"));
//        selections.add(manageReportListJoin.get("code"));
//        selections.add(manageReportListJoin.get("description"));
//        selections.add(manageReportListJoin.get("status"));
//        selections.add(manageReportListJoin.get("name"));
//        selections.add(manageReportListJoin.get("moduleId"));
//        selections.add(manageReportListJoin.get("moduleName"));

        selections.add(root.get("status"));
        selections.add(root.get("firstName"));
        selections.add(root.get("lastName"));
        selections.add(root.get("loginName"));
        selections.add(root.get("email"));
        selections.add(root.get("innsistCode"));
        selections.add(root.get("phoneExtension"));
        selections.add(root.get("userType"));

        query.multiselect(selections.toArray(new Selection[0]));

        if(specification != null){
            Predicate predicate = specification.toPredicate(root, query, cb);
            query.where(predicate);
        }

        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Tuple> tuples = typedQuery.getResultList();

        List<ManageEmployeeProjection> results = tuples.stream().map(tuple -> {
            return new ManageEmployeeProjection(
                    tuple.get(0, UUID.class),
                    new ManageDepartmentGroupProjection(
                            tuple.get(1, UUID.class),
                            tuple.get(2, String.class),
                            tuple.get(3, String.class),
                            tuple.get(4, String.class),
                            tuple.get(5, String.class)
                    ),
                    tuple.get(8, String.class),
                    tuple.get(9, String.class),
                    tuple.get(10, String.class),
                    tuple.get(11, String.class),
                    tuple.get(12, String.class),
                    tuple.get(13, String.class),
                    tuple.get(14, String.class),
                    tuple.get(15, String.class)
            );
        }).collect(Collectors.toList());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ManageEmployee> countRoot = countQuery.from(ManageEmployee.class);
        countQuery.select(cb.count(countRoot));

        if(specification != null){
            Predicate countPredicate = specification.toPredicate(countRoot, countQuery, cb);
            countQuery.where(countPredicate);
        }
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}
