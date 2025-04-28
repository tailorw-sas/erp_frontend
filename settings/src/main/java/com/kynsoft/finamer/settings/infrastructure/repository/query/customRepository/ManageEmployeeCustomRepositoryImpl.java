package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.domain.dto.ModuleStatus;
import com.kynsoft.finamer.settings.domain.dto.PermissionStatusEnm;
import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.domain.dtoEnum.UserType;
import com.kynsoft.finamer.settings.infrastructure.identity.*;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.query.QueryUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ManageEmployeeCustomRepositoryImpl implements ManageEmployeeCustomRepository{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManageEmployee> findAllCustom(Specification<ManageEmployee> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageDepartmentGroup> departmentGroupJoin = root.join("departmentGroup", JoinType.LEFT);

        List<Selection<?>> selections = this.getEmployeeSelection(root, departmentGroupJoin);

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

        List<ManageEmployee> results = tuples.stream()
                        .map(this::convertTupleToManageEmployee)
                .collect(Collectors.toList());

        List<UUID> employeeIds = results.stream()
                .map(ManageEmployee::getId)
                .collect(Collectors.toList());

        Map<UUID, List<ManagePermission>> mapPermissionsByEmployeeId = this.getPermissionsByEmployeIdMap(employeeIds);
        Map<UUID, List<ManageAgency>> mapAgencyProjectionsByEmployeeId = this.getAgenciesByEmployeeIdMap(employeeIds);
        Map<UUID, List<ManageHotel>> mapHotelsProjectionByEmployeeId = this.getHotelsByEmployeeIdMap(employeeIds);
        Map<UUID, List<ManageTradingCompanies>> mapTradingCompaniesByEmployeeId = this.getTradingCompaniesByEmployeeMap(employeeIds);
        Map<UUID, List<ManageReport>> mapReportsByEmployeeId = this.getManageReportByEmployeeMap(employeeIds);

        results.forEach(employee -> {
            employee.getManagePermissionList().addAll(mapPermissionsByEmployeeId.get(employee.getId()));
            employee.getManageAgencyList().addAll(mapAgencyProjectionsByEmployeeId.get(employee.getId()));
            employee.getManageHotelList().addAll(mapHotelsProjectionByEmployeeId.get(employee.getId()));
            employee.getManageTradingCompaniesList().addAll(mapTradingCompaniesByEmployeeId.get(employee.getId()));
            employee.getManageReportList().addAll(mapReportsByEmployeeId.get(employee.getId()));
        });

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

    @Override
    public Optional<ManageEmployee> findByIdCustom(UUID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageDepartmentGroup> departmentGroupJoin = root.join("departmentGroup", JoinType.LEFT);

        List<Selection<?>> selections = this.getEmployeeSelection(root, departmentGroupJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        try{
            Tuple tuple = entityManager.createQuery(query).getSingleResult();
            ManageEmployee result = this.convertTupleToManageEmployee(tuple);

            result.getManagePermissionList().addAll(this.getPermissionsByEmployeId(result.getId()));
            result.getManageAgencyList().addAll(this.getAgenciesByEmployeeId(result.getId()));
            result.getManageHotelList().addAll(this.getHotelsByEmployeeId(result.getId()));
            result.getManageTradingCompaniesList().addAll(this.getTradingCompaniesByEmployeeId(result.getId()));
            result.getManageReportList().addAll(this.getManageReportByEmployeeId(result.getId()));

            return Optional.of(result);
        }catch (NoResultException e){
            return Optional.empty();
        }
    }

    @Override
    public List<ManageEmployee> findAllCustom() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageDepartmentGroup> departmentGroupJoin = root.join("departmentGroup", JoinType.LEFT);

        List<Selection<?>> selections = this.getEmployeeSelection(root, departmentGroupJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);

        List<Tuple> tuples = typedQuery.getResultList();

        List<ManageEmployee> results = tuples.stream()
                .map(this::convertTupleToManageEmployee)
                .collect(Collectors.toList());

        List<UUID> employeeIds = results.stream()
                .map(ManageEmployee::getId)
                .collect(Collectors.toList());

        Map<UUID, List<ManagePermission>> mapPermissionsByEmployeeId = this.getPermissionsByEmployeIdMap(employeeIds);
        Map<UUID, List<ManageAgency>> mapAgencyProjectionsByEmployeeId = this.getAgenciesByEmployeeIdMap(employeeIds);
        Map<UUID, List<ManageHotel>> mapHotelsProjectionByEmployeeId = this.getHotelsByEmployeeIdMap(employeeIds);
        Map<UUID, List<ManageTradingCompanies>> mapTradingCompaniesByEmployeeId = this.getTradingCompaniesByEmployeeMap(employeeIds);
        Map<UUID, List<ManageReport>> mapReportsByEmployeeId = this.getManageReportByEmployeeMap(employeeIds);

        results.forEach(employee -> {
            employee.getManagePermissionList().addAll(mapPermissionsByEmployeeId.get(employee.getId()));
            employee.getManageAgencyList().addAll(mapAgencyProjectionsByEmployeeId.get(employee.getId()));
            employee.getManageHotelList().addAll(mapHotelsProjectionByEmployeeId.get(employee.getId()));
            employee.getManageTradingCompaniesList().addAll(mapTradingCompaniesByEmployeeId.get(employee.getId()));
            employee.getManageReportList().addAll(mapReportsByEmployeeId.get(employee.getId()));
        });

        return results;
    }

    @Override
    public List<ManageEmployee> findAllByIdCustom(List<UUID> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageDepartmentGroup> departmentGroupJoin = root.join("departmentGroup", JoinType.LEFT);

        List<Selection<?>> selections = this.getEmployeeSelection(root, departmentGroupJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("id").in(ids));

        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);

        List<Tuple> tuples = typedQuery.getResultList();

        List<ManageEmployee> results = tuples.stream()
                .map(this::convertTupleToManageEmployee)
                .collect(Collectors.toList());

        List<UUID> employeeIds = results.stream()
                .map(ManageEmployee::getId)
                .collect(Collectors.toList());

        Map<UUID, List<ManagePermission>> mapPermissionsByEmployeeId = this.getPermissionsByEmployeIdMap(employeeIds);
        Map<UUID, List<ManageAgency>> mapAgencyProjectionsByEmployeeId = this.getAgenciesByEmployeeIdMap(employeeIds);
        Map<UUID, List<ManageHotel>> mapHotelsProjectionByEmployeeId = this.getHotelsByEmployeeIdMap(employeeIds);
        Map<UUID, List<ManageTradingCompanies>> mapTradingCompaniesByEmployeeId = this.getTradingCompaniesByEmployeeMap(employeeIds);
        Map<UUID, List<ManageReport>> mapReportsByEmployeeId = this.getManageReportByEmployeeMap(employeeIds);

        results.forEach(employee -> {
            employee.getManagePermissionList().addAll(mapPermissionsByEmployeeId.get(employee.getId()));
            employee.getManageAgencyList().addAll(mapAgencyProjectionsByEmployeeId.get(employee.getId()));
            employee.getManageHotelList().addAll(mapHotelsProjectionByEmployeeId.get(employee.getId()));
            employee.getManageTradingCompaniesList().addAll(mapTradingCompaniesByEmployeeId.get(employee.getId()));
            employee.getManageReportList().addAll(mapReportsByEmployeeId.get(employee.getId()));
        });

        return results;
    }

    private List<Selection<?>> getEmployeeSelection(Root<ManageEmployee> root,
                                                    Join<ManageEmployee, ManageDepartmentGroup> departmentGroupJoin){
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));

        //Department Group
        selections.add(departmentGroupJoin.get("id"));
        selections.add(departmentGroupJoin.get("code"));
        selections.add(departmentGroupJoin.get("name"));
        selections.add(departmentGroupJoin.get("description"));
        selections.add(departmentGroupJoin.get("status"));
        selections.add(departmentGroupJoin.get("createdAt"));
        selections.add(departmentGroupJoin.get("updateAt"));

        //Manage Employee resto
        selections.add(root.get("status"));
        selections.add(root.get("createdAt"));
        selections.add(root.get("updateAt"));
        selections.add(root.get("firstName"));
        selections.add(root.get("lastName"));
        selections.add(root.get("loginName"));
        selections.add(root.get("email"));
        selections.add(root.get("innsistCode"));
        selections.add(root.get("phoneExtension"));
        selections.add(root.get("userType"));

        return selections;
    }

    private ManageEmployee convertTupleToManageEmployee(Tuple tuple){
        ManageEmployee employee = new ManageEmployee(
                tuple.get(0, UUID.class),
                new ManageDepartmentGroup(
                        tuple.get(1, UUID.class),
                        tuple.get(2, String.class),
                        tuple.get(3, String.class),
                        tuple.get(4, String.class),
                        tuple.get(5, Status.class),
                        tuple.get(6, LocalDateTime.class),
                        tuple.get(7, LocalDateTime.class)
                ),
                tuple.get(8, Status.class),
                tuple.get(9, LocalDateTime.class),
                tuple.get(10, LocalDateTime.class),
                tuple.get(11, String.class),
                tuple.get(12, String.class),
                tuple.get(13, String.class),
                tuple.get(14, String.class),
                tuple.get(15, String.class),
                tuple.get(16, String.class),
                new ArrayList<>(), //Permission List
                new ArrayList<>(), // Agency List
                new ArrayList<>(), //Hotel List
                new ArrayList<>(), //Trading company List
                new ArrayList<>(),//Report List
                tuple.get(17, UserType.class) // User Type
        );

        return employee;
    }

    private Map<UUID, List<ManagePermission>> getPermissionsByEmployeIdMap(List<UUID> ids){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManagePermission> permissionListJoin = root.join("managePermissionList", JoinType.LEFT);
        Join<ManagePermission, ManageModule> permissionManageModuleJoin = permissionListJoin.join("module", JoinType.LEFT);

        List<Selection<?>> selections = new ArrayList<>();

        selections.add(root.get("id"));
        selections.addAll(this.getPermissionSelection(permissionListJoin, permissionManageModuleJoin));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("id").in(ids));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        Map<UUID, List<ManagePermission>> mapPermissionsByEmployeeId = tuples.stream()
                .collect(Collectors.groupingBy(
                                tuple -> tuple.get(0, UUID.class),
                                Collectors.mapping(tuple -> {return this.convertTupleToManagePermission(tuple, 1);}, Collectors.toList())));
        return mapPermissionsByEmployeeId;
    }

    private List<ManagePermission> getPermissionsByEmployeId(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManagePermission> permissionListJoin = root.join("managePermissionList", JoinType.LEFT);
        Join<ManagePermission, ManageModule> permissionManageModuleJoin = permissionListJoin.join("module", JoinType.LEFT);

        List<Selection<?>> selections = this.getPermissionSelection(permissionListJoin, permissionManageModuleJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<ManagePermission> permissions = tuples.stream()
                .map(tuple -> {
                    return this.convertTupleToManagePermission(tuple, 0);
                })
                .collect(Collectors.toList());

        return permissions;
    }

    private List<Selection<?>> getPermissionSelection(Join<ManageEmployee, ManagePermission> permissionListJoin,
                                                      Join<ManagePermission, ManageModule> permissionManageModuleJoin){
        List<Selection<?>> selections = new ArrayList<>();

        //Permission List
        selections.add(permissionListJoin.get("id"));
        selections.add(permissionListJoin.get("code"));
        selections.add(permissionListJoin.get("description"));
        selections.add(permissionListJoin.get("action"));
        selections.add(permissionListJoin.get("status"));

        //Manage Module
        selections.add(permissionManageModuleJoin.get("id"));
        selections.add(permissionManageModuleJoin.get("name"));
        selections.add(permissionManageModuleJoin.get("image"));
        selections.add(permissionManageModuleJoin.get("description"));
        selections.add(permissionManageModuleJoin.get("status"));
        selections.add(permissionManageModuleJoin.get("code"));
        selections.add(permissionManageModuleJoin.get("createdAt"));

        //Permission List resto
        selections.add(permissionListJoin.get("createdAt"));
        selections.add(permissionListJoin.get("isHighRisk"));
        selections.add(permissionListJoin.get("isIT"));
        selections.add(permissionListJoin.get("name"));

        return selections;
    }

    private ManagePermission convertTupleToManagePermission(Tuple tuple, int i) {
        return new ManagePermission(
                tuple.get(i++, UUID.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, PermissionStatusEnm.class),
                new ManageModule(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, ModuleStatus.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, LocalDateTime.class),
                        Collections.emptySet()
                ),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, String.class)
        );
    }


    private Map<UUID, List<ManageAgency>> getAgenciesByEmployeeIdMap(List<UUID> ids){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageAgency> agencyListJoin = root.join("manageAgencyList", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> agencyManageAgencyTypeJoin = agencyListJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> agencyManageClientJoin = agencyListJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageB2BPartner> agencyManageB2BPartnerJoin = agencyListJoin.join("sentB2BPartner", JoinType.LEFT);
        Join<ManageB2BPartner, ManageB2BPartnerType> manageB2BPartnerManageB2BPartnerTypeJoin = agencyManageB2BPartnerJoin.join("b2bPartnerType", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> agencyManageCountryJoin = agencyListJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> manageCountryManagerLanguageJoin = agencyManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageAgency, ManageCityState> agencyManageCityStateJoin = agencyListJoin.join("cityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> cityStateManageCountryJoin = agencyManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> cityStateCountryManagerLanguageJoin = cityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> cityStateManageTimeZoneJoin = agencyManageCityStateJoin.join("timeZone", JoinType.LEFT);

        List<Selection<?>> selections = new ArrayList<>();

        selections.add(root.get("id"));
        selections.addAll(this.getAgencySelection(agencyListJoin,
                agencyManageAgencyTypeJoin,
                agencyManageClientJoin,
                agencyManageB2BPartnerJoin,
                manageB2BPartnerManageB2BPartnerTypeJoin,
                agencyManageCountryJoin,
                manageCountryManagerLanguageJoin,
                agencyManageCityStateJoin,
                cityStateManageCountryJoin,
                cityStateCountryManagerLanguageJoin,
                cityStateManageTimeZoneJoin));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("id").in(ids));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        Map<UUID, List<ManageAgency>> agenciesByEmployeeMap = tuples.stream()
                .collect(Collectors.groupingBy(tuple -> tuple.get(0, UUID.class),
                        Collectors.mapping(tuple -> { return convertTupleToManageAgency(tuple, 1);}, Collectors.toList())
                ));

        return agenciesByEmployeeMap;
    }

    private List<ManageAgency> getAgenciesByEmployeeId(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageAgency> agencyListJoin = root.join("manageAgencyList", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> agencyManageAgencyTypeJoin = agencyListJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> agencyManageClientJoin = agencyListJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageB2BPartner> agencyManageB2BPartnerJoin = agencyListJoin.join("sentB2BPartner", JoinType.LEFT);
        Join<ManageB2BPartner, ManageB2BPartnerType> manageB2BPartnerManageB2BPartnerTypeJoin = agencyManageB2BPartnerJoin.join("b2bPartnerType", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> agencyManageCountryJoin = agencyListJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> manageCountryManagerLanguageJoin = agencyManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageAgency, ManageCityState> agencyManageCityStateJoin = agencyListJoin.join("cityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> cityStateManageCountryJoin = agencyManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> cityStateCountryManagerLanguageJoin = cityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> cityStateManageTimeZoneJoin = agencyManageCityStateJoin.join("timeZone", JoinType.LEFT);

        List<Selection<?>> selections = this.getAgencySelection(agencyListJoin,
                agencyManageAgencyTypeJoin,
                agencyManageClientJoin,
                agencyManageB2BPartnerJoin,
                manageB2BPartnerManageB2BPartnerTypeJoin,
                agencyManageCountryJoin,
                manageCountryManagerLanguageJoin,
                agencyManageCityStateJoin,
                cityStateManageCountryJoin,
                cityStateCountryManagerLanguageJoin,
                cityStateManageTimeZoneJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<ManageAgency> agencies = tuples.stream()
                .map(tuple -> {
                    return convertTupleToManageAgency(tuple, 0);
                    }
                )
                .collect(Collectors.toList());

        return agencies;
    }

    private List<Selection<?>> getAgencySelection(Join<ManageEmployee, ManageAgency> agencyListJoin,
                                                  Join<ManageAgency, ManageAgencyType> agencyManageAgencyTypeJoin,
                                                  Join<ManageAgency, ManageClient> agencyManageClientJoin,
                                                  Join<ManageAgency, ManageB2BPartner> agencyManageB2BPartnerJoin,
                                                  Join<ManageB2BPartner, ManageB2BPartnerType> manageB2BPartnerManageB2BPartnerTypeJoin, Join<ManageAgency, ManageCountry> agencyManageCountryJoin,
                                                  Join<ManageCountry, ManagerLanguage> manageCountryManagerLanguageJoin,
                                                  Join<ManageAgency, ManageCityState> agencyManageCityStateJoin,
                                                  Join<ManageCityState, ManageCountry> cityStateManageCountryJoin,
                                                  Join<ManageCountry, ManagerLanguage> cityStateCountryManagerLanguageJoin,
                                                  Join<ManageCityState, ManagerTimeZone> cityStateManageTimeZoneJoin){
        List<Selection<?>> selections = new ArrayList<>();

        selections.add(agencyListJoin.get("id"));
        selections.add(agencyListJoin.get("code"));
        selections.add(agencyListJoin.get("cif"));
        selections.add(agencyListJoin.get("agencyAlias"));
        selections.add(agencyListJoin.get("audit"));
        selections.add(agencyListJoin.get("zipCode"));
        selections.add(agencyListJoin.get("address"));
        selections.add(agencyListJoin.get("mailingAddress"));
        selections.add(agencyListJoin.get("phone"));
        selections.add(agencyListJoin.get("alternativePhone"));
        selections.add(agencyListJoin.get("email"));
        selections.add(agencyListJoin.get("alternativeEmail"));
        selections.add(agencyListJoin.get("contactName"));
        selections.add(agencyListJoin.get("autoReconcile"));
        selections.add(agencyListJoin.get("creditDay"));
        selections.add(agencyListJoin.get("rfc"));
        selections.add(agencyListJoin.get("validateCheckout"));
        selections.add(agencyListJoin.get("bookingCouponFormat"));
        selections.add(agencyListJoin.get("description"));
        selections.add(agencyListJoin.get("city"));
        selections.add(agencyListJoin.get("isDefault"));
        selections.add(agencyListJoin.get("generationType"));
        selections.add(agencyListJoin.get("sentFileFormat"));

        selections.add(agencyManageAgencyTypeJoin.get("id"));
        selections.add(agencyManageAgencyTypeJoin.get("code"));
        selections.add(agencyManageAgencyTypeJoin.get("status"));
        selections.add(agencyManageAgencyTypeJoin.get("name"));
        selections.add(agencyManageAgencyTypeJoin.get("description"));
        selections.add(agencyManageAgencyTypeJoin.get("createdAt"));
        selections.add(agencyManageAgencyTypeJoin.get("updatedAt"));

        selections.add(agencyManageClientJoin.get("id"));
        selections.add(agencyManageClientJoin.get("code"));
        selections.add(agencyManageClientJoin.get("name"));
        selections.add(agencyManageClientJoin.get("description"));
        selections.add(agencyManageClientJoin.get("status"));
        selections.add(agencyManageClientJoin.get("isNightType"));
        selections.add(agencyManageClientJoin.get("createdAt"));
        selections.add(agencyManageClientJoin.get("updateAt"));

        selections.add(agencyManageB2BPartnerJoin.get("id"));
        selections.add(agencyManageB2BPartnerJoin.get("code"));
        selections.add(agencyManageB2BPartnerJoin.get("name"));
        selections.add(agencyManageB2BPartnerJoin.get("description"));
        selections.add(agencyManageB2BPartnerJoin.get("url"));
        selections.add(agencyManageB2BPartnerJoin.get("ip"));
        selections.add(agencyManageB2BPartnerJoin.get("userName"));
        selections.add(agencyManageB2BPartnerJoin.get("password"));
        selections.add(agencyManageB2BPartnerJoin.get("token"));
        selections.add(agencyManageB2BPartnerJoin.get("status"));

        selections.add(manageB2BPartnerManageB2BPartnerTypeJoin.get("id"));
        selections.add(manageB2BPartnerManageB2BPartnerTypeJoin.get("code"));
        selections.add(manageB2BPartnerManageB2BPartnerTypeJoin.get("name"));
        selections.add(manageB2BPartnerManageB2BPartnerTypeJoin.get("description"));
        selections.add(manageB2BPartnerManageB2BPartnerTypeJoin.get("status"));
        selections.add(manageB2BPartnerManageB2BPartnerTypeJoin.get("createdAt"));
        selections.add(manageB2BPartnerManageB2BPartnerTypeJoin.get("updateAt"));

        selections.add(agencyManageB2BPartnerJoin.get("createdAt"));
        selections.add(agencyManageB2BPartnerJoin.get("updateAt"));

        selections.add(agencyManageCountryJoin.get("id"));
        selections.add(agencyManageCountryJoin.get("code"));
        selections.add(agencyManageCountryJoin.get("name"));
        selections.add(agencyManageCountryJoin.get("description"));
        selections.add(agencyManageCountryJoin.get("dialCode"));
        selections.add(agencyManageCountryJoin.get("iso3"));
        selections.add(agencyManageCountryJoin.get("isDefault"));

        selections.add(manageCountryManagerLanguageJoin.get("id"));
        selections.add(manageCountryManagerLanguageJoin.get("code"));
        selections.add(manageCountryManagerLanguageJoin.get("status"));
        selections.add(manageCountryManagerLanguageJoin.get("description"));
        selections.add(manageCountryManagerLanguageJoin.get("name"));
        selections.add(manageCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(manageCountryManagerLanguageJoin.get("defaults"));
        selections.add(manageCountryManagerLanguageJoin.get("createdAt"));
        selections.add(manageCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(agencyManageCountryJoin.get("status"));
        selections.add(agencyManageCountryJoin.get("createdAt"));
        selections.add(agencyManageCountryJoin.get("updateAt"));
        selections.add(agencyManageCountryJoin.get("deleteAt"));

        selections.add(agencyManageCityStateJoin.get("id"));
        selections.add(agencyManageCityStateJoin.get("code"));
        selections.add(agencyManageCityStateJoin.get("name"));
        selections.add(agencyManageCityStateJoin.get("description"));

        selections.add(cityStateManageCountryJoin.get("id"));
        selections.add(cityStateManageCountryJoin.get("code"));
        selections.add(cityStateManageCountryJoin.get("name"));
        selections.add(cityStateManageCountryJoin.get("description"));
        selections.add(cityStateManageCountryJoin.get("dialCode"));
        selections.add(cityStateManageCountryJoin.get("iso3"));
        selections.add(cityStateManageCountryJoin.get("isDefault"));

        selections.add(cityStateCountryManagerLanguageJoin.get("id"));
        selections.add(cityStateCountryManagerLanguageJoin.get("code"));
        selections.add(cityStateCountryManagerLanguageJoin.get("status"));
        selections.add(cityStateCountryManagerLanguageJoin.get("description"));
        selections.add(cityStateCountryManagerLanguageJoin.get("name"));
        selections.add(cityStateCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(cityStateCountryManagerLanguageJoin.get("defaults"));
        selections.add(cityStateCountryManagerLanguageJoin.get("createdAt"));
        selections.add(cityStateCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(cityStateManageCountryJoin.get("status"));
        selections.add(cityStateManageCountryJoin.get("createdAt"));
        selections.add(cityStateManageCountryJoin.get("updateAt"));
        selections.add(cityStateManageCountryJoin.get("deleteAt"));

        selections.add(cityStateManageTimeZoneJoin.get("id"));
        selections.add(cityStateManageTimeZoneJoin.get("code"));
        selections.add(cityStateManageTimeZoneJoin.get("description"));
        selections.add(cityStateManageTimeZoneJoin.get("name"));
        selections.add(cityStateManageTimeZoneJoin.get("elapse"));
        selections.add(cityStateManageTimeZoneJoin.get("status"));
        selections.add(cityStateManageTimeZoneJoin.get("createdAt"));
        selections.add(cityStateManageTimeZoneJoin.get("updateAt"));

        selections.add(agencyManageCityStateJoin.get("status"));
        selections.add(agencyManageCityStateJoin.get("createdAt"));
        selections.add(agencyManageCityStateJoin.get("updateAt"));

        selections.add(agencyListJoin.get("status"));
        selections.add(agencyListJoin.get("name"));
        selections.add(agencyListJoin.get("createdAt"));
        selections.add(agencyListJoin.get("updatedAt"));

        return selections;
    }

    public ManageAgency convertTupleToManageAgency(Tuple tuple, int i) {
        return new ManageAgency(
                tuple.get(i++, UUID.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, Integer.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, EGenerationType.class),
                tuple.get(i++, ESentFileFormat.class),
                (tuple.get(i, UUID.class) != null) ? new ManageAgencyType(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 7),
                (tuple.get(i, UUID.class) != null) ? new ManageClient(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Status.class),
                        null,
                        tuple.get(i++, Boolean.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 8),
                (tuple.get(i, UUID.class) != null) ? new ManageB2BPartner(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Status.class),
                        (tuple.get(i, UUID.class) != null) ? new ManageB2BPartnerType(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 7),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        null
                ) : skip(i += 19),
                (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Boolean.class),
                        (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 9),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        null
                ) : skip(i += 20),
                (tuple.get(i, UUID.class) != null) ? new ManageCityState(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Status.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class)
                                ) : skip(i += 9),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                null
                        ) : skip(i += 20),
                        (tuple.get(i, UUID.class) != null) ? new ManagerTimeZone(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Double.class),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 8),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 35),
                tuple.get(i++, Status.class),
                tuple.get(i++, String.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class)
        );
    }

    private Map<UUID, List<ManageHotel>> getHotelsByEmployeeIdMap(List<UUID> ids){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageHotel> hotelJoin = root.join("manageHotelList", JoinType.LEFT);
        Join<ManageHotel, ManageCountry> hotelManageCountryJoin = hotelJoin.join("manageCountry", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelCountryManagerLanguageJoin = hotelManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageHotel, ManageCityState> hotelManageCityStateJoin = hotelJoin.join("manageCityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> hotelCityStateManageCountryJoin = hotelManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelCityStateCountryManagerLanguageJoin = hotelCityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> hotelCityStateTimeZoneJoin = hotelManageCityStateJoin.join("timeZone", JoinType.LEFT);
        Join<ManageHotel, ManagerCurrency> hotelManagerCurrencyJoin = hotelJoin.join("manageCurrency", JoinType.LEFT);
        Join<ManageHotel, ManageRegion> hotelManageRegionJoin = hotelJoin.join("manageRegion", JoinType.LEFT);
        Join<ManageHotel, ManageTradingCompanies> hotelManageTradingCompaniesJoin = hotelJoin.join("manageTradingCompanies", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCountry> hotelTradingCompaniesManageCountryJoin = hotelManageTradingCompaniesJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelTradingCompaniesCountryManagerLanguageJoin = hotelTradingCompaniesManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCityState> hotelTradingCompaniesManageCityStateJoin = hotelManageTradingCompaniesJoin.join("cityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> hotelTradingCompaniesCityStateManageCountryJoin = hotelTradingCompaniesManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelTradingCompaniesCityStateCountryManagerLanguageJoin = hotelTradingCompaniesCityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> hotelCityStateManageTimeZone = hotelTradingCompaniesManageCityStateJoin.join("timeZone", JoinType.LEFT);

        List<Selection<?>> selections = new ArrayList<>();

        selections.add(root.get("id"));
        selections.addAll(this.getHotelSelection(hotelJoin,
                hotelManageCountryJoin,
                hotelCountryManagerLanguageJoin,
                hotelManageCityStateJoin,
                hotelCityStateManageCountryJoin,
                hotelCityStateCountryManagerLanguageJoin,
                hotelCityStateTimeZoneJoin,
                hotelManagerCurrencyJoin,
                hotelManageRegionJoin,
                hotelManageTradingCompaniesJoin,
                hotelTradingCompaniesManageCountryJoin,
                hotelTradingCompaniesCountryManagerLanguageJoin,
                hotelTradingCompaniesManageCityStateJoin,
                hotelTradingCompaniesCityStateManageCountryJoin,
                hotelTradingCompaniesCityStateCountryManagerLanguageJoin,
                hotelCityStateManageTimeZone));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("id").in(ids));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        Map<UUID, List<ManageHotel>> hotelsByEmployeeMap = tuples.stream()
                .collect(Collectors.groupingBy(tuple -> tuple.get(0, UUID.class),
                        Collectors.mapping(tuple -> { return convertTupleToHotel(tuple, 1);}, Collectors.toList())
                ));

        return hotelsByEmployeeMap;
    }

    private List<ManageHotel> getHotelsByEmployeeId(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageHotel> hotelJoin = root.join("manageHotelList", JoinType.LEFT);
        Join<ManageHotel, ManageCountry> hotelManageCountryJoin = hotelJoin.join("manageCountry", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelCountryManagerLanguageJoin = hotelManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageHotel, ManageCityState> hotelManageCityStateJoin = hotelJoin.join("manageCityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> hotelCityStateManageCountryJoin = hotelManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelCityStateCountryManagerLanguageJoin = hotelCityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> hotelCityStateTimeZoneJoin = hotelManageCityStateJoin.join("timeZone", JoinType.LEFT);
        Join<ManageHotel, ManagerCurrency> hotelManagerCurrencyJoin = hotelJoin.join("manageCurrency", JoinType.LEFT);
        Join<ManageHotel, ManageRegion> hotelManageRegionJoin = hotelJoin.join("manageRegion", JoinType.LEFT);
        Join<ManageHotel, ManageTradingCompanies> hotelManageTradingCompaniesJoin = hotelJoin.join("manageTradingCompanies", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCountry> hotelTradingCompaniesManageCountryJoin = hotelManageTradingCompaniesJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelTradingCompaniesCountryManagerLanguageJoin = hotelTradingCompaniesManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCityState> hotelTradingCompaniesManageCityStateJoin = hotelManageTradingCompaniesJoin.join("cityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> hotelTradingCompaniesCityStateManageCountryJoin = hotelTradingCompaniesManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> hotelTradingCompaniesCityStateCountryManagerLanguageJoin = hotelTradingCompaniesCityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> hotelCityStateManageTimeZone = hotelTradingCompaniesManageCityStateJoin.join("timeZone", JoinType.LEFT);

        List<Selection<?>> selections = this.getHotelSelection(hotelJoin,
                hotelManageCountryJoin,
                hotelCountryManagerLanguageJoin,
                hotelManageCityStateJoin,
                hotelCityStateManageCountryJoin,
                hotelCityStateCountryManagerLanguageJoin,
                hotelCityStateTimeZoneJoin,
                hotelManagerCurrencyJoin,
                hotelManageRegionJoin,
                hotelManageTradingCompaniesJoin,
                hotelTradingCompaniesManageCountryJoin,
                hotelTradingCompaniesCountryManagerLanguageJoin,
                hotelTradingCompaniesManageCityStateJoin,
                hotelTradingCompaniesCityStateManageCountryJoin,
                hotelTradingCompaniesCityStateCountryManagerLanguageJoin,
                hotelCityStateManageTimeZone);

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<ManageHotel> hotels = tuples.stream()
                .map(tuple -> {
                    return convertTupleToHotel(tuple, 0);
                    }
                )
                .collect(Collectors.toList());

        return hotels;
    }

    private List<Selection<?>> getHotelSelection(Join<ManageEmployee, ManageHotel> hotelJoin,
                                                 Join<ManageHotel, ManageCountry> hotelManageCountryJoin,
                                                 Join<ManageCountry, ManagerLanguage> hotelCountryManagerLanguageJoin,
                                                 Join<ManageHotel, ManageCityState> hotelManageCityStateJoin,
                                                 Join<ManageCityState, ManageCountry> hotelCityStateManageCountryJoin,
                                                 Join<ManageCountry, ManagerLanguage> hotelCityStateCountryManagerLanguageJoin,
                                                 Join<ManageCityState, ManagerTimeZone> hotelCityStateTimeZoneJoin,
                                                 Join<ManageHotel, ManagerCurrency> hotelManagerCurrencyJoin,
                                                 Join<ManageHotel, ManageRegion> hotelManageRegionJoin,
                                                 Join<ManageHotel, ManageTradingCompanies> hotelManageTradingCompaniesJoin,
                                                 Join<ManageTradingCompanies, ManageCountry> hotelTradingCompaniesManageCountryJoin,
                                                 Join<ManageCountry, ManagerLanguage> hotelTradingCompaniesCountryManagerLanguageJoin,
                                                 Join<ManageTradingCompanies, ManageCityState> hotelTradingCompaniesManageCityStateJoin,
                                                 Join<ManageCityState, ManageCountry> hotelTradingCompaniesCityStateManageCountryJoin,
                                                 Join<ManageCountry, ManagerLanguage> hotelTradingCompaniesCityStateCountryManagerLanguageJoin,
                                                 Join<ManageCityState, ManagerTimeZone> hotelCityStateManageTimeZone){
        List<Selection<?>> selections = new ArrayList<>();

        selections.add(hotelJoin.get("id"));
        selections.add(hotelJoin.get("code"));
        selections.add(hotelJoin.get("status"));
        selections.add(hotelJoin.get("description"));
        selections.add(hotelJoin.get("name"));
        selections.add(hotelJoin.get("createdAt"));
        selections.add(hotelJoin.get("updatedAt"));
        selections.add(hotelJoin.get("babelCode"));

        selections.add(hotelManageCountryJoin.get("id"));
        selections.add(hotelManageCountryJoin.get("code"));
        selections.add(hotelManageCountryJoin.get("name"));
        selections.add(hotelManageCountryJoin.get("description"));
        selections.add(hotelManageCountryJoin.get("dialCode"));
        selections.add(hotelManageCountryJoin.get("iso3"));
        selections.add(hotelManageCountryJoin.get("isDefault"));

        selections.add(hotelCountryManagerLanguageJoin.get("id"));
        selections.add(hotelCountryManagerLanguageJoin.get("code"));
        selections.add(hotelCountryManagerLanguageJoin.get("status"));
        selections.add(hotelCountryManagerLanguageJoin.get("description"));
        selections.add(hotelCountryManagerLanguageJoin.get("name"));
        selections.add(hotelCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(hotelCountryManagerLanguageJoin.get("defaults"));
        selections.add(hotelCountryManagerLanguageJoin.get("createdAt"));
        selections.add(hotelCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(hotelManageCountryJoin.get("status"));
        selections.add(hotelManageCountryJoin.get("createdAt"));
        selections.add(hotelManageCountryJoin.get("updateAt"));
        selections.add(hotelManageCountryJoin.get("deleteAt"));

        selections.add(hotelManageCityStateJoin.get("id"));
        selections.add(hotelManageCityStateJoin.get("code"));
        selections.add(hotelManageCityStateJoin.get("name"));
        selections.add(hotelManageCityStateJoin.get("description"));

        selections.add(hotelCityStateManageCountryJoin.get("id"));
        selections.add(hotelCityStateManageCountryJoin.get("code"));
        selections.add(hotelCityStateManageCountryJoin.get("name"));
        selections.add(hotelCityStateManageCountryJoin.get("description"));
        selections.add(hotelCityStateManageCountryJoin.get("dialCode"));
        selections.add(hotelCityStateManageCountryJoin.get("iso3"));
        selections.add(hotelCityStateManageCountryJoin.get("isDefault"));

        selections.add(hotelCityStateCountryManagerLanguageJoin.get("id"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("code"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("status"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("description"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("name"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("defaults"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("createdAt"));
        selections.add(hotelCityStateCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(hotelCityStateManageCountryJoin.get("status"));
        selections.add(hotelCityStateManageCountryJoin.get("createdAt"));
        selections.add(hotelCityStateManageCountryJoin.get("updateAt"));
        selections.add(hotelCityStateManageCountryJoin.get("deleteAt"));

        selections.add(hotelCityStateTimeZoneJoin.get("id"));
        selections.add(hotelCityStateTimeZoneJoin.get("code"));
        selections.add(hotelCityStateTimeZoneJoin.get("name"));
        selections.add(hotelCityStateTimeZoneJoin.get("description"));
        selections.add(hotelCityStateTimeZoneJoin.get("elapse"));
        selections.add(hotelCityStateTimeZoneJoin.get("status"));
        selections.add(hotelCityStateTimeZoneJoin.get("createdAt"));
        selections.add(hotelCityStateTimeZoneJoin.get("updateAt"));

        selections.add(hotelManageCityStateJoin.get("status"));
        selections.add(hotelManageCityStateJoin.get("createdAt"));
        selections.add(hotelManageCityStateJoin.get("updateAt"));

        selections.add(hotelJoin.get("city"));
        selections.add(hotelJoin.get("address"));

        selections.add(hotelManagerCurrencyJoin.get("id"));
        selections.add(hotelManagerCurrencyJoin.get("code"));
        selections.add(hotelManagerCurrencyJoin.get("name"));
        selections.add(hotelManagerCurrencyJoin.get("description"));
        selections.add(hotelManagerCurrencyJoin.get("status"));
        selections.add(hotelManagerCurrencyJoin.get("createdAt"));
        selections.add(hotelManagerCurrencyJoin.get("updateAt"));

        selections.add(hotelManageRegionJoin.get("id"));
        selections.add(hotelManageRegionJoin.get("code"));
        selections.add(hotelManageRegionJoin.get("status"));
        selections.add(hotelManageRegionJoin.get("description"));
        selections.add(hotelManageRegionJoin.get("name"));
        selections.add(hotelManageRegionJoin.get("createdAt"));
        selections.add(hotelManageRegionJoin.get("updatedAt"));

        selections.add(hotelManageTradingCompaniesJoin.get("id"));
        selections.add(hotelManageTradingCompaniesJoin.get("code"));
        selections.add(hotelManageTradingCompaniesJoin.get("status"));
        selections.add(hotelManageTradingCompaniesJoin.get("description"));
        selections.add(hotelManageTradingCompaniesJoin.get("company"));
        selections.add(hotelManageTradingCompaniesJoin.get("createdAt"));
        selections.add(hotelManageTradingCompaniesJoin.get("updatedAt"));
        selections.add(hotelManageTradingCompaniesJoin.get("cif"));
        selections.add(hotelManageTradingCompaniesJoin.get("address"));

        selections.add(hotelTradingCompaniesManageCountryJoin.get("id"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("code"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("name"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("description"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("dialCode"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("iso3"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("isDefault"));

        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("id"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("code"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("status"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("description"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("name"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("defaults"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("createdAt"));
        selections.add(hotelTradingCompaniesCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(hotelTradingCompaniesManageCountryJoin.get("status"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("createdAt"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("updateAt"));
        selections.add(hotelTradingCompaniesManageCountryJoin.get("deleteAt"));

        selections.add(hotelTradingCompaniesManageCityStateJoin.get("id"));
        selections.add(hotelTradingCompaniesManageCityStateJoin.get("code"));
        selections.add(hotelTradingCompaniesManageCityStateJoin.get("name"));
        selections.add(hotelTradingCompaniesManageCityStateJoin.get("description"));

        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("id"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("code"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("name"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("description"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("dialCode"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("iso3"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("isDefault"));

        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("id"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("code"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("status"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("description"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("name"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("defaults"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("createdAt"));
        selections.add(hotelTradingCompaniesCityStateCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("status"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("createdAt"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("updateAt"));
        selections.add(hotelTradingCompaniesCityStateManageCountryJoin.get("deleteAt"));

        selections.add(hotelCityStateManageTimeZone.get("id"));
        selections.add(hotelCityStateManageTimeZone.get("code"));
        selections.add(hotelCityStateManageTimeZone.get("description"));
        selections.add(hotelCityStateManageTimeZone.get("name"));
        selections.add(hotelCityStateManageTimeZone.get("elapse"));
        selections.add(hotelCityStateManageTimeZone.get("status"));
        selections.add(hotelCityStateManageTimeZone.get("createdAt"));
        selections.add(hotelCityStateManageTimeZone.get("updateAt"));

        selections.add(hotelTradingCompaniesManageCityStateJoin.get("status"));
        selections.add(hotelTradingCompaniesManageCityStateJoin.get("createdAt"));
        selections.add(hotelTradingCompaniesManageCityStateJoin.get("updateAt"));

        selections.add(hotelManageTradingCompaniesJoin.get("city"));
        selections.add(hotelManageTradingCompaniesJoin.get("zipCode"));
        selections.add(hotelManageTradingCompaniesJoin.get("innsistCode"));
        selections.add(hotelManageTradingCompaniesJoin.get("isApplyInvoice"));

        selections.add(hotelJoin.get("applyByTradingCompany"));
        selections.add(hotelJoin.get("prefixToInvoice"));
        selections.add(hotelJoin.get("isVirtual"));
        selections.add(hotelJoin.get("requiresFlatRate"));
        selections.add(hotelJoin.get("isApplyByVCC"));
        selections.add(hotelJoin.get("autoApplyCredit"));

        return selections;
    }

    private ManageHotel convertTupleToHotel(Tuple tuple, int i){
        return new ManageHotel(
                tuple.get(i++, UUID.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Status.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, String.class),
                (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Boolean.class),
                        (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 9),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        null
                ) : skip( i += 20),
                (tuple.get(i, UUID.class) != null) ? new ManageCityState(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Status.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class)
                                ) : skip(i += 9),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                null
                        ) : skip(i += 20),
                        (tuple.get(i, UUID.class) != null) ? new ManagerTimeZone(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Double.class),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 8),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 28),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                (tuple.get(i, UUID.class) != null) ? new ManagerCurrency(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 7),
                (tuple.get(i, UUID.class) != null) ? new ManageRegion(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 7),
                (tuple.get(i, UUID.class) != null) ? new ManageTradingCompanies(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Status.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class)
                                ) : skip(i += 9),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                null
                        ) : skip(i += 20),
                        (tuple.get(i, UUID.class) != null) ? new ManageCityState(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                                tuple.get(i++, UUID.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, Status.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, Boolean.class),
                                                tuple.get(i++, Boolean.class),
                                                tuple.get(i++, LocalDateTime.class),
                                                tuple.get(i++, LocalDateTime.class)
                                        ) : skip(i += 9),
                                        tuple.get(i++, Status.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        null
                                ) : skip(i += 20),
                                (tuple.get(i, UUID.class) != null) ? new ManagerTimeZone(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Double.class),
                                        tuple.get(i++, Status.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class)
                                ) : skip(i += 8),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 35),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Boolean.class)
                ) : skip(i += 68),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, Boolean.class),
                tuple.get(i++, Boolean.class)
        );
    }

    private Map<UUID, List<ManageTradingCompanies>> getTradingCompaniesByEmployeeMap(List<UUID> ids){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageTradingCompanies> tradingCompaniesJoin = root.join("manageTradingCompaniesList", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCountry> tradingCompaniesManageCountryJoin = tradingCompaniesJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> tradingCompaniesCountryManagerLanguageJoin = tradingCompaniesManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCityState> tradingCompaniesManageCityStateJoin = tradingCompaniesJoin.join("cityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> tradingCompaniesCityStateManageCountryJoin = tradingCompaniesManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> tradingCompaniesCityStateCountryManagerLanguageJoin = tradingCompaniesCityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> hotelCityStateTimeZoneJoin = tradingCompaniesManageCityStateJoin.join("timeZone", JoinType.LEFT);

        List<Selection<?>> selections = new ArrayList<>();

        selections.add(root.get("id"));
        selections.addAll(this.getTradingCompanySelection(tradingCompaniesJoin,
                tradingCompaniesManageCountryJoin,
                tradingCompaniesCountryManagerLanguageJoin,
                tradingCompaniesManageCityStateJoin,
                tradingCompaniesCityStateManageCountryJoin,
                tradingCompaniesCityStateCountryManagerLanguageJoin,
                hotelCityStateTimeZoneJoin));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("id").in(ids));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        Map<UUID, List<ManageTradingCompanies>> tradingCompaniesByEmployeeMap = tuples.stream()
                .collect(Collectors.groupingBy(tuple -> tuple.get(0, UUID.class),
                        Collectors.mapping(tuple -> { return convertTupleToManageTradingCompany(tuple, 1);}, Collectors.toList())
                ));

        return tradingCompaniesByEmployeeMap;
    }

    private List<ManageTradingCompanies> getTradingCompaniesByEmployeeId(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageTradingCompanies> tradingCompaniesJoin = root.join("manageTradingCompaniesList", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCountry> tradingCompaniesManageCountryJoin = tradingCompaniesJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> tradingCompaniesCountryManagerLanguageJoin = tradingCompaniesManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageTradingCompanies, ManageCityState> tradingCompaniesManageCityStateJoin = tradingCompaniesJoin.join("cityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> tradingCompaniesCityStateManageCountryJoin = tradingCompaniesManageCityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> tradingCompaniesCityStateCountryManagerLanguageJoin = tradingCompaniesCityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> hotelCityStateTimeZoneJoin = tradingCompaniesManageCityStateJoin.join("timeZone", JoinType.LEFT);

        List<Selection<?>> selections = this.getTradingCompanySelection(tradingCompaniesJoin,
                tradingCompaniesManageCountryJoin,
                tradingCompaniesCountryManagerLanguageJoin,
                tradingCompaniesManageCityStateJoin,
                tradingCompaniesCityStateManageCountryJoin,
                tradingCompaniesCityStateCountryManagerLanguageJoin,
                hotelCityStateTimeZoneJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<ManageTradingCompanies> tradingCompanies = tuples.stream()
                .map(tuple -> {
                    return convertTupleToManageTradingCompany(tuple, 0);
                    }
                )
                .collect(Collectors.toList());

        return tradingCompanies;
    }

    private List<Selection<?>> getTradingCompanySelection(Join<ManageEmployee, ManageTradingCompanies> tradingCompaniesJoin,
                                                          Join<ManageTradingCompanies, ManageCountry> tradingCompaniesManageCountryJoin,
                                                          Join<ManageCountry, ManagerLanguage> tradingCompaniesCountryManagerLanguageJoin,
                                                          Join<ManageTradingCompanies, ManageCityState> tradingCompaniesManageCityStateJoin,
                                                          Join<ManageCityState, ManageCountry> tradingCompaniesCityStateManageCountryJoin,
                                                          Join<ManageCountry, ManagerLanguage> tradingCompaniesCityStateCountryManagerLanguageJoin,
                                                          Join<ManageCityState, ManagerTimeZone> hotelCityStateTimeZoneJoin){
        List<Selection<?>> selections = new ArrayList<>();

        selections.add(tradingCompaniesJoin.get("id"));
        selections.add(tradingCompaniesJoin.get("code"));
        selections.add(tradingCompaniesJoin.get("status"));
        selections.add(tradingCompaniesJoin.get("description"));
        selections.add(tradingCompaniesJoin.get("company"));
        selections.add(tradingCompaniesJoin.get("createdAt"));
        selections.add(tradingCompaniesJoin.get("updatedAt"));
        selections.add(tradingCompaniesJoin.get("cif"));
        selections.add(tradingCompaniesJoin.get("address"));

        selections.add(tradingCompaniesManageCountryJoin.get("id"));
        selections.add(tradingCompaniesManageCountryJoin.get("code"));
        selections.add(tradingCompaniesManageCountryJoin.get("name"));
        selections.add(tradingCompaniesManageCountryJoin.get("description"));
        selections.add(tradingCompaniesManageCountryJoin.get("dialCode"));
        selections.add(tradingCompaniesManageCountryJoin.get("iso3"));
        selections.add(tradingCompaniesManageCountryJoin.get("isDefault"));

        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("id"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("code"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("status"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("description"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("name"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("defaults"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("createdAt"));
        selections.add(tradingCompaniesCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(tradingCompaniesManageCountryJoin.get("status"));
        selections.add(tradingCompaniesManageCountryJoin.get("createdAt"));
        selections.add(tradingCompaniesManageCountryJoin.get("updateAt"));
        selections.add(tradingCompaniesManageCountryJoin.get("deleteAt"));

        selections.add(tradingCompaniesManageCityStateJoin.get("id"));
        selections.add(tradingCompaniesManageCityStateJoin.get("code"));
        selections.add(tradingCompaniesManageCityStateJoin.get("name"));
        selections.add(tradingCompaniesManageCityStateJoin.get("description"));

        selections.add(tradingCompaniesCityStateManageCountryJoin.get("id"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("code"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("name"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("description"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("dialCode"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("iso3"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("isDefault"));

        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("id"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("code"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("status"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("description"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("name"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("isEnabled"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("defaults"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("createdAt"));
        selections.add(tradingCompaniesCityStateCountryManagerLanguageJoin.get("updatedAt"));

        selections.add(tradingCompaniesCityStateManageCountryJoin.get("status"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("createdAt"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("updateAt"));
        selections.add(tradingCompaniesCityStateManageCountryJoin.get("deleteAt"));

        selections.add(hotelCityStateTimeZoneJoin.get("id"));
        selections.add(hotelCityStateTimeZoneJoin.get("code"));
        selections.add(hotelCityStateTimeZoneJoin.get("description"));
        selections.add(hotelCityStateTimeZoneJoin.get("name"));
        selections.add(hotelCityStateTimeZoneJoin.get("elapse"));
        selections.add(hotelCityStateTimeZoneJoin.get("status"));
        selections.add(hotelCityStateTimeZoneJoin.get("createdAt"));
        selections.add(hotelCityStateTimeZoneJoin.get("updateAt"));

        selections.add(tradingCompaniesManageCityStateJoin.get("status"));
        selections.add(tradingCompaniesManageCityStateJoin.get("createdAt"));
        selections.add(tradingCompaniesManageCityStateJoin.get("updateAt"));

        selections.add(tradingCompaniesJoin.get("city"));
        selections.add(tradingCompaniesJoin.get("zipCode"));
        selections.add(tradingCompaniesJoin.get("innsistCode"));
        selections.add(tradingCompaniesJoin.get("isApplyInvoice"));

        return selections;
    }

    private ManageTradingCompanies convertTupleToManageTradingCompany(Tuple tuple, int i){
        return new ManageTradingCompanies(
                tuple.get(i++, UUID.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Status.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Boolean.class),
                        (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 9),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        null
                ) : skip(i += 20),
                (tuple.get(i, UUID.class) != null) ? new ManageCityState(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                (tuple.get(i, UUID.class) != null) ? new ManagerLanguage(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Status.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class)
                                ) : skip(i += 9),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                null
                        ) : skip(i += 20),
                        (tuple.get(i, UUID.class) != null) ? new ManagerTimeZone(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Double.class),
                                tuple.get(i++, Status.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip(i += 8),
                        tuple.get(i++, Status.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 35),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Boolean.class)
        );
    }

    private Map<UUID, List<ManageReport>> getManageReportByEmployeeMap(List<UUID> ids){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageReport> employeeManageReportJoin = root.join("manageReportList", JoinType.LEFT);

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.addAll(this.getManageReportSelection(employeeManageReportJoin));

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("id").in(ids));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        Map<UUID, List<ManageReport>> reportsByEmployeeMap = tuples.stream()
                .collect(Collectors.groupingBy(tuple -> tuple.get(0, UUID.class),
                        Collectors.mapping(tuple -> { return convertTupleToManageReport(tuple, 1);}, Collectors.toList())
                ));

        return reportsByEmployeeMap;
    }

    private List<ManageReport> getManageReportByEmployeeId(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageEmployee> root = query.from(ManageEmployee.class);
        Join<ManageEmployee, ManageReport> employeeManageReportJoin = root.join("manageReportList", JoinType.LEFT);

        List<Selection<?>> selections = this.getManageReportSelection(employeeManageReportJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(cb.equal(root.get("id"), id));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<ManageReport> reports = tuples.stream()
                .map(tuple -> {
                    return convertTupleToManageReport(tuple, 0);
                    }
                )
                .collect(Collectors.toList());

        return reports;
    }

    private List<Selection<?>> getManageReportSelection(Join<ManageEmployee, ManageReport> employeeManageReportJoin){
        List<Selection<?>> selections = new ArrayList<>();

        selections.add(employeeManageReportJoin.get("id"));
        selections.add(employeeManageReportJoin.get("code"));
        selections.add(employeeManageReportJoin.get("status"));
        selections.add(employeeManageReportJoin.get("description"));
        selections.add(employeeManageReportJoin.get("name"));
        selections.add(employeeManageReportJoin.get("moduleId"));
        selections.add(employeeManageReportJoin.get("moduleName"));
        selections.add(employeeManageReportJoin.get("createdAt"));
        selections.add(employeeManageReportJoin.get("updatedAt"));

        return selections;
    }

    private ManageReport convertTupleToManageReport(Tuple tuple, int i){
        return new ManageReport(
                tuple.get(i++, UUID.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Status.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class)
        );
    }

    @SuppressWarnings("unchecked")
    private <T> T skip(int i) {
        return null;
    }
}
