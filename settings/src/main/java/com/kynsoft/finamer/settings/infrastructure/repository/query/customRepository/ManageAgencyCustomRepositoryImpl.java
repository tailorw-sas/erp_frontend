package com.kynsoft.finamer.settings.infrastructure.repository.query.customRepository;

import com.kynsoft.finamer.settings.domain.dtoEnum.EGenerationType;
import com.kynsoft.finamer.settings.domain.dtoEnum.ESentFileFormat;
import com.kynsoft.finamer.settings.domain.dtoEnum.Status;
import com.kynsoft.finamer.settings.infrastructure.identity.*;
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

@Repository
public class ManageAgencyCustomRepositoryImpl implements ManageAgencyCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<ManageAgency> getAgencyByIdWithAllRelations(UUID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<ManageAgency> root = query.from(ManageAgency.class);
        Join<ManageAgency, ManageAgencyType> agencyTypeJoin = root.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> clientJoin = root.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageB2BPartner> sentB2BPartnerJoin = root.join("sentB2BPartner", JoinType.LEFT);
        Join<ManageB2BPartner, ManageB2BPartnerType> b2bPartnerTypeJoin = sentB2BPartnerJoin.join("b2bPartnerType", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> countryJoin = root.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> countryManagerLanguageJoin = countryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageAgency, ManageCityState> cityStateJoin = root.join("cityState", JoinType.LEFT);
        Join<ManageCityState, ManageCountry> cityStateManageCountryJoin = cityStateJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManagerLanguage> cityStateCountryLanguageJoin = cityStateManageCountryJoin.join("managerLanguage", JoinType.LEFT);
        Join<ManageCityState, ManagerTimeZone> cityStateManagerTimeZoneJoin = cityStateJoin.join("timeZone", JoinType.LEFT);

        query.where(cb.equal(root.get("id"), id));

        List<Selection<?>> selections = new ArrayList<>();

        selections.add(root.get("id"));
        selections.add(root.get("code"));
        selections.add(root.get("cif"));
        selections.add(root.get("agencyAlias"));
        selections.add(root.get("audit"));
        selections.add(root.get("zipCode"));
        selections.add(root.get("address"));
        selections.add(root.get("mailingAddress"));
        selections.add(root.get("phone"));
        selections.add(root.get("alternativePhone"));
        selections.add(root.get("email"));
        selections.add(root.get("alternativeEmail"));
        selections.add(root.get("contactName"));
        selections.add(root.get("autoReconcile"));
        selections.add(root.get("creditDay"));
        selections.add(root.get("rfc"));
        selections.add(root.get("validateCheckout"));
        selections.add(root.get("bookingCouponFormat"));
        selections.add(root.get("description"));
        selections.add(root.get("city"));
        selections.add(root.get("isDefault"));
        selections.add(root.get("generationType"));
        selections.add(root.get("sentFileFormat"));

        selections.add(agencyTypeJoin.get("id"));
        selections.add(agencyTypeJoin.get("code"));
        selections.add(agencyTypeJoin.get("status"));
        selections.add(agencyTypeJoin.get("name"));
        selections.add(agencyTypeJoin.get("description"));

        selections.add(clientJoin.get("id"));
        selections.add(clientJoin.get("code"));
        selections.add(clientJoin.get("name"));
        selections.add(clientJoin.get("description"));
        selections.add(clientJoin.get("status"));
        selections.add(clientJoin.get("isNightType"));

        selections.add(sentB2BPartnerJoin.get("id"));
        selections.add(sentB2BPartnerJoin.get("code"));
        selections.add(sentB2BPartnerJoin.get("name"));
        selections.add(sentB2BPartnerJoin.get("description"));
        selections.add(sentB2BPartnerJoin.get("status"));
        selections.add(sentB2BPartnerJoin.get("url"));
        selections.add(sentB2BPartnerJoin.get("ip"));
        selections.add(sentB2BPartnerJoin.get("userName"));
        selections.add(sentB2BPartnerJoin.get("password"));
        selections.add(sentB2BPartnerJoin.get("token"));

        selections.add(b2bPartnerTypeJoin.get("id"));
        selections.add(b2bPartnerTypeJoin.get("code"));
        selections.add(b2bPartnerTypeJoin.get("name"));
        selections.add(b2bPartnerTypeJoin.get("description"));
        selections.add(b2bPartnerTypeJoin.get("status"));

        selections.add(countryJoin.get("id"));
        selections.add(countryJoin.get("code"));
        selections.add(countryJoin.get("name"));
        selections.add(countryJoin.get("description"));
        selections.add(countryJoin.get("dialCode"));
        selections.add(countryJoin.get("iso3"));
        selections.add(countryJoin.get("isDefault"));

        selections.add(countryManagerLanguageJoin.get("id"));
        selections.add(countryManagerLanguageJoin.get("code"));
        selections.add(countryManagerLanguageJoin.get("status"));
        selections.add(countryManagerLanguageJoin.get("description"));
        selections.add(countryManagerLanguageJoin.get("name"));
        selections.add(countryManagerLanguageJoin.get("isEnabled"));
        selections.add(countryManagerLanguageJoin.get("defaults"));

        selections.add(countryJoin.get("status"));

        selections.add(cityStateJoin.get("id"));
        selections.add(cityStateJoin.get("code"));
        selections.add(cityStateJoin.get("name"));
        selections.add(cityStateJoin.get("description"));

        selections.add(cityStateManageCountryJoin.get("id"));
        selections.add(cityStateManageCountryJoin.get("code"));
        selections.add(cityStateManageCountryJoin.get("name"));
        selections.add(cityStateManageCountryJoin.get("description"));
        selections.add(cityStateManageCountryJoin.get("dialCode"));
        selections.add(cityStateManageCountryJoin.get("iso3"));
        selections.add(cityStateManageCountryJoin.get("isDefault"));

        selections.add(cityStateCountryLanguageJoin.get("id"));
        selections.add(cityStateCountryLanguageJoin.get("code"));
        selections.add(cityStateCountryLanguageJoin.get("status"));
        selections.add(cityStateCountryLanguageJoin.get("description"));
        selections.add(cityStateCountryLanguageJoin.get("name"));
        selections.add(cityStateCountryLanguageJoin.get("isEnabled"));
        selections.add(cityStateCountryLanguageJoin.get("defaults"));

        selections.add(cityStateManageCountryJoin.get("status"));

        selections.add(cityStateManagerTimeZoneJoin.get("id"));
        selections.add(cityStateManagerTimeZoneJoin.get("code"));
        selections.add(cityStateManagerTimeZoneJoin.get("description"));
        selections.add(cityStateManagerTimeZoneJoin.get("name"));
        selections.add(cityStateManagerTimeZoneJoin.get("elapse"));
        selections.add(cityStateManagerTimeZoneJoin.get("status"));

        selections.add(cityStateJoin.get("status"));

        selections.add(root.get("status"));
        selections.add(root.get("name"));
        selections.add(root.get("createdAt"));
        selections.add(root.get("updatedAt"));

        query.multiselect(selections.toArray(new Selection[0]));

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        ManageAgency agency = new ManageAgency(
                tuple.get(0, UUID.class),
                tuple.get(1, String.class),
                tuple.get(2, String.class),
                tuple.get(3, String.class),
                tuple.get(4, Boolean.class),
                tuple.get(5, String.class),
                tuple.get(6, String.class),
                tuple.get(7, String.class),
                tuple.get(8, String.class),
                tuple.get(9, String.class),
                tuple.get(10, String.class),
                tuple.get(11, String.class),
                tuple.get(12, String.class),
                tuple.get(13, Boolean.class),
                tuple.get(14, Integer.class),
                tuple.get(15, String.class),
                tuple.get(16, Boolean.class),
                tuple.get(17, String.class),
                tuple.get(18, String.class),
                tuple.get(19, String.class),
                tuple.get(20, Boolean.class),
                tuple.get(21, EGenerationType.class),
                tuple.get(22, ESentFileFormat.class),
                new ManageAgencyType(
                        tuple.get(23, UUID.class),
                        tuple.get(24, String.class),
                        tuple.get(25, Status.class),
                        tuple.get(26, String.class),
                        tuple.get(27, String.class)
                ),
                new ManageClient(
                        tuple.get(28, UUID.class),
                        tuple.get(29, String.class),
                        tuple.get(30, String.class),
                        tuple.get(31, String.class),
                        tuple.get(32, Status.class),
                        tuple.get(33, Boolean.class)
                ),
                new ManageB2BPartner(
                        tuple.get(34, UUID.class),
                        tuple.get(35, String.class),
                        tuple.get(36, String.class),
                        tuple.get(37, String.class),
                        tuple.get(38, Status.class),
                        tuple.get(39, String.class),
                        tuple.get(40, String.class),
                        tuple.get(41, String.class),
                        tuple.get(42, String.class),
                        tuple.get(43, String.class),
                        new ManageB2BPartnerType(
                                tuple.get(44, UUID.class),
                                tuple.get(45, String.class),
                                tuple.get(46, String.class),
                                tuple.get(47, String.class),
                                tuple.get(48, Status.class)
                        )
                ),
                new ManageCountry(
                        tuple.get(49, UUID.class),
                        tuple.get(50, String.class),
                        tuple.get(51, String.class),
                        tuple.get(52, String.class),
                        tuple.get(53, String.class),
                        tuple.get(54, String.class),
                        tuple.get(55, Boolean.class),
                        new ManagerLanguage(
                                tuple.get(56, UUID.class),
                                tuple.get(57, String.class),
                                tuple.get(58, Status.class),
                                tuple.get(59, String.class),
                                tuple.get(60, String.class),
                                tuple.get(61, Boolean.class),
                                tuple.get(62, Boolean.class)
                        ),
                        tuple.get(63, Status.class)
                ),
                new ManageCityState(
                        tuple.get(64, UUID.class),
                        tuple.get(65, String.class),
                        tuple.get(66, String.class),
                        tuple.get(67, String.class),
                        new ManageCountry(
                                tuple.get(68, UUID.class),
                                tuple.get(69, String.class),
                                tuple.get(70, String.class),
                                tuple.get(71, String.class),
                                tuple.get(72, String.class),
                                tuple.get(73, String.class),
                                tuple.get(74, Boolean.class),
                                new ManagerLanguage(
                                        tuple.get(75, UUID.class),
                                        tuple.get(76, String.class),
                                        tuple.get(77, Status.class),
                                        tuple.get(78, String.class),
                                        tuple.get(79, String.class),
                                        tuple.get(80, Boolean.class),
                                        tuple.get(81, Boolean.class)
                                ),
                                tuple.get(82, Status.class)
                        ),
                        new ManagerTimeZone(
                                tuple.get(83, UUID.class),
                                tuple.get(84, String.class),
                                tuple.get(85, String.class),
                                tuple.get(86, String.class),
                                tuple.get(87, Double.class),
                                tuple.get(88, Status.class)
                        ),
                        tuple.get(89, Status.class)
                ),
                tuple.get(90, Status.class),
                tuple.get(91, String.class),
                tuple.get(92, LocalDateTime.class),
                tuple.get(93, LocalDateTime.class)
        );

        return Optional.of(agency);
    }

}
