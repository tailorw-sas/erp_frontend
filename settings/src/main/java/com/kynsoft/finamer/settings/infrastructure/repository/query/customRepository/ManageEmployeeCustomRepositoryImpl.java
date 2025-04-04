package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.domain.dto.PermissionStatusEnm;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import com.kynsoft.finamer.settings.infrastructure.identity.*;
import com.kynsoft.finamer.settings.infrastructure.projections.*;
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

import java.util.*;

@Repository
public class ManageEmployeeCustomRepositoryImpl implements ManageEmployeeCustomRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManageEmployeeProjection> findAllCustom(Specification<ManageEmployee> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<UUID> queryIds = cb.createQuery(UUID.class);
        Root<ManageEmployee> root = queryIds.from(ManageEmployee.class);
        queryIds.select(root.get("id"));
        if(specification != null){
            Predicate predicate = specification.toPredicate(root, queryIds, cb);
            queryIds.where(predicate);
        }
        queryIds.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));

        TypedQuery<UUID> typedQuery = entityManager.createQuery(queryIds);
        typedQuery.setFirstResult((int)pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<UUID> employeeIds = typedQuery.getResultList();

        if (employeeIds.isEmpty()) {
            return null; // Retorna vacío si no hay empleados seleccionados
        }

        CriteriaQuery<Tuple> queryEmployee = cb.createTupleQuery();
        Root<ManageEmployee> rootEmployee = queryEmployee.from(ManageEmployee.class);
        Join<ManageEmployee, ManageDepartmentGroup> departmentGroupJoin = rootEmployee.join("departmentGroup", JoinType.LEFT);
        Join<ManageEmployee, ManagePermission> permissionListJoin = rootEmployee.join("managePermissionList", JoinType.LEFT);
        Join<ManageEmployee, ManageAgency> manageAgencyListJoin = rootEmployee.join("manageAgencyList", JoinType.LEFT);
        Join<ManageEmployee, ManageHotel> manageHotelListJoin = rootEmployee.join("manageHotelList", JoinType.LEFT);
        Join<ManageEmployee, ManageTradingCompanies> manageTradingCompaniesListJoin = rootEmployee.join("manageTradingCompaniesList", JoinType.LEFT);
        //Join<ManageEmployee, ManageReport> manageReportListJoin = rootEmployee.join("manageReportList", JoinType.LEFT);

        queryEmployee.where(rootEmployee.get("id").in(employeeIds));

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(rootEmployee.get("id"));

        //Department Group
        selections.add(departmentGroupJoin.get("id"));
        selections.add(departmentGroupJoin.get("code"));
        selections.add(departmentGroupJoin.get("name"));
        selections.add(departmentGroupJoin.get("description"));
        selections.add(departmentGroupJoin.get("status"));
//
//
//        //ManageTradingCompany List
//        selections.add(manageReportListJoin.get("id"));
//        selections.add(manageReportListJoin.get("code"));
//        selections.add(manageReportListJoin.get("description"));
//        selections.add(manageReportListJoin.get("status"));
//        selections.add(manageReportListJoin.get("name"));
//        selections.add(manageReportListJoin.get("moduleId"));
//        selections.add(manageReportListJoin.get("moduleName"));

        selections.add(rootEmployee.get("status"));
        selections.add(rootEmployee.get("firstName"));
        selections.add(rootEmployee.get("lastName"));
        selections.add(rootEmployee.get("loginName"));
        selections.add(rootEmployee.get("email"));
        selections.add(rootEmployee.get("innsistCode"));
        selections.add(rootEmployee.get("phoneExtension"));
        selections.add(rootEmployee.get("userType"));

        //ManageAgency List
        selections.add(manageAgencyListJoin.get("id"));
        selections.add(manageAgencyListJoin.get("code"));
        selections.add(manageAgencyListJoin.get("status"));
        selections.add(manageAgencyListJoin.get("name"));

        //Permission List
        selections.add(permissionListJoin.get("id"));
        selections.add(permissionListJoin.get("code"));
        selections.add(permissionListJoin.get("status"));
        selections.add(permissionListJoin.get("description"));
        selections.add(permissionListJoin.get("name"));
        selections.add(permissionListJoin.get("isHighRisk"));

        //ManageHotel List
        selections.add(manageHotelListJoin.get("id"));
        selections.add(manageHotelListJoin.get("code"));
        selections.add(manageHotelListJoin.get("status"));
        selections.add(manageHotelListJoin.get("name"));

        //ManageTradingCompany List
        selections.add(manageTradingCompaniesListJoin.get("id"));
        selections.add(manageTradingCompaniesListJoin.get("code"));
        selections.add(manageTradingCompaniesListJoin.get("status"));
        selections.add(manageTradingCompaniesListJoin.get("company"));

        queryEmployee.multiselect(selections.toArray(new Selection[0]));

        queryEmployee.orderBy(QueryUtils.toOrders(pageable.getSort(), rootEmployee, cb));
        queryEmployee.distinct(true);

        TypedQuery<Tuple> typedQueryEmployee = entityManager.createQuery(queryEmployee);
        //typedQuery.setFirstResult((int) pageable.getOffset());
        //typedQuery.setMaxResults(pageable.getPageSize());

        List<Tuple> tuples = typedQueryEmployee.getResultList();

        Map<UUID, ManageEmployeeProjection> employeeMap = new HashMap<>();

        for (Tuple tuple : tuples) {
            UUID employeeId = tuple.get(0, UUID.class); // ID del empleado

            // Si el empleado no existe en el mapa, crearlo
            ManageEmployeeProjection employee = employeeMap.computeIfAbsent(employeeId, id -> {
                return new ManageEmployeeProjection(
                        id,
                        new ManageDepartmentGroupProjection(
                                tuple.get(1, UUID.class), // ID del grupo
                                tuple.get(2, String.class), // Código del grupo
                                tuple.get(3, String.class), // Nombre del grupo
                                tuple.get(4, String.class), // Descripción del grupo
                                tuple.get(5, Status.class) // Estado del grupo
                        ),
                        tuple.get(6, Status.class), // Status del empleado
                        tuple.get(7, String.class), // First Name
                        tuple.get(8, String.class), // Last Name
                        tuple.get(9, String.class), // Login Name
                        tuple.get(10, String.class), // Email
                        tuple.get(11, String.class), // Innsist Code
                        tuple.get(12, String.class), // Phone Extension
                        new ArrayList<>(), // Agency List
                        new ArrayList<>(), //Permission List
                        new ArrayList<>(), //Hotel List
                        new ArrayList<>(),
                        tuple.get(13, UserType.class) // User Type
                );
            });

            if (tuple.get(14, UUID.class) != null) {
                ManageAgencyProjection agency = new ManageAgencyProjection(
                        tuple.get(14, UUID.class), // Agency ID
                        tuple.get(15, String.class), // Code
                        tuple.get(16, Status.class), // Status
                        tuple.get(17, String.class) // Name
                );

                employee.getManageAgencyList().add(agency);
            }

            if(tuple.get(18, UUID.class) != null){
                ManagePermissionProjection permission = new ManagePermissionProjection(
                        tuple.get(18, UUID.class), // ID
                        tuple.get(19, String.class), //Code
                        tuple.get(20, PermissionStatusEnm.class), //status
                        tuple.get(21, String.class), //description
                        tuple.get(22, String.class), //name
                        tuple.get(23, Boolean.class) //isHighRisk
                );

                employee.getManagePermissionList().add(permission);
            }

            if(tuple.get(24, UUID.class) != null){
                ManageHotelProjection hotel = new ManageHotelProjection(
                        tuple.get(24, UUID.class), // ID
                        tuple.get(25, String.class), //Code
                        tuple.get(26, String.class), //name
                        tuple.get(27, Status.class) //status
                );

                employee.getManageHotelList().add(hotel);
            }

            if(tuple.get(28, UUID.class) != null){
                ManageTradingCompanyProjection tradingCompany = new ManageTradingCompanyProjection(
                        tuple.get(28, UUID.class), // ID
                        tuple.get(29, String.class), //Code
                        tuple.get(30, Status.class), //status
                        tuple.get(31, String.class) //company
                );

                employee.getManageTradingCompaniesList().add(tradingCompany);
            }
        }

        List<ManageEmployeeProjection> results = new ArrayList<>(employeeMap.values());

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
