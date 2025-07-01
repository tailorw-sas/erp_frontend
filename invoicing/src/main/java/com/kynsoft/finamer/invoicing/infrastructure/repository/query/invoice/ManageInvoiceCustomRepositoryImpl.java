package com.kynsoft.finamer.invoicing.infrastructure.repository.query.invoice;

import com.kynsoft.finamer.invoicing.infrastructure.identity.*;
import com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ManageInvoiceCustomRepositoryImpl implements ManageInvoiceCustomRepository {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ManageInvoiceCustomRepositoryImpl.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManageInvoiceSearchProjection> findAllProjected(Specification<Invoice> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ManageInvoiceSearchProjection> query = cb.createQuery(ManageInvoiceSearchProjection.class);
        Root<Invoice> root = query.from(Invoice.class);

        Join<Invoice, ManageHotel> hotelJoin = root.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> agencyJoin = root.join("agency", JoinType.LEFT);
        Join<Invoice, ManageInvoiceStatus> statusJoin = root.join("manageInvoiceStatus", JoinType.LEFT);
        Join<Invoice, ManageInvoiceType> typeJoin = root.join("manageInvoiceType", JoinType.LEFT);

        query.select(cb.construct(
                ManageInvoiceSearchProjection.class,
                root.get("id"),
                root.get("invoiceId"),
                root.get("isManual"),
                root.get("invoiceNo"),
                root.get("invoiceAmount"),
                root.get("dueAmount"),
                root.get("invoiceDate"),
                root.get("hasAttachments"),
                root.get("invoiceType"),
                root.get("invoiceStatus"),
                root.get("invoiceNumber"),
                root.get("sendStatusError"),
                root.get("parent").get("id"),
                root.get("autoRec"),
                root.get("originalAmount"),
                root.get("importType"),
                root.get("cloneParent"),
                root.get("dueDate"),
                root.get("aging"),
                cb.construct(
                        ManageInvoiceHotelProjection.class,
                        hotelJoin.get("id"),
                        hotelJoin.get("code"),
                        hotelJoin.get("name"),
                        hotelJoin.get("isVirtual")),
                cb.construct(
                        ManageInvoiceAgencyProjection.class,
                        agencyJoin.get("id"),
                        agencyJoin.get("code"),
                        agencyJoin.get("name")),
                cb.construct(
                        ManageInvoiceStatusProjection.class,
                        statusJoin.get("id"),
                        statusJoin.get("name"),
                        statusJoin.get("code"),
                        statusJoin.get("showClone"),
                        statusJoin.get("enabledToApply"),
                        statusJoin.get("processStatus"),
                        statusJoin.get("sentStatus"),
                        statusJoin.get("reconciledStatus"),
                        statusJoin.get("canceledStatus")
                ),
                cb.construct(
                        ManageInvoiceTypeProjection.class,
                        typeJoin.get("id"),
                        typeJoin.get("name"),
                        typeJoin.get("code")),
                cb.selectCase()
                        .when(cb.and(
                                cb.isNotNull(hotelJoin.get("closeOperation")),
                                cb.greaterThanOrEqualTo(root.get("invoiceDate"), hotelJoin.get("closeOperation").get("beginDate")),
                                cb.lessThanOrEqualTo(root.get("invoiceDate"), hotelJoin.get("closeOperation").get("endDate"))
                        ), true)
                        .otherwise(false)
        ));

        if (specification != null) {
            Predicate predicate = specification.toPredicate(root, query, cb);
            query.where(predicate);
        }

        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        TypedQuery<ManageInvoiceSearchProjection> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Invoice> countRoot = countQuery.from(Invoice.class);
        countQuery.select(cb.count(countRoot));

        if (specification != null) {
            Predicate countPredicate = specification.toPredicate(countRoot, countQuery, cb);
            countQuery.where(countPredicate);
        }
        long total = entityManager.createQuery(countQuery).getSingleResult();
        org.hibernate.query.Query<?> hibernateQuery = typedQuery.unwrap(org.hibernate.query.Query.class);
        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }
}