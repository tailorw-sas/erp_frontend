package com.kynsoft.finamer.invoicing.infrastructure.repository.query.invoice;

import com.kynsoft.finamer.invoicing.infrastructure.identity.*;
import com.kynsoft.finamer.invoicing.infrastructure.interfacesEntity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class ManageInvoiceCustomRepositoryImpl implements ManageInvoiceCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<ManageInvoiceSearchProjection> findAllProjected(Specification<Invoice> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ManageInvoiceSearchProjection> query = cb.createQuery(ManageInvoiceSearchProjection.class);
        Root<Invoice> root = query.from(Invoice.class);

        // Joins optimizados
        Join<Invoice, ManageHotel> hotelJoin = root.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> agencyJoin = root.join("agency", JoinType.LEFT);
        Join<Invoice, ManageInvoiceStatus> statusJoin = root.join("manageInvoiceStatus", JoinType.LEFT);
        Join<Invoice, ManageInvoiceType> typeJoin = root.join("manageInvoiceType", JoinType.LEFT);

        // Construcción de proyección
        query.select(cb.construct(
                ManageInvoiceSearchProjection.class,
                root.get("id"),
                root.get("invoiceId"),
                root.get("isManual"),
                root.get("invoiceNo"),
                root.get("invoiceAmount"),
                root.get("dueAmount"),
                root.get("invoiceDate"),
                cb.construct(ManageInvoiceHotelProjection.class,
                        hotelJoin.get("id"),
                        hotelJoin.get("code"),
                        hotelJoin.get("name"),
                        hotelJoin.get("isVirtual")
                ),
                cb.construct(ManageInvoiceAgencyProjection.class,
                        agencyJoin.get("id"),
                        agencyJoin.get("code"),
                        agencyJoin.get("name")
                ),

        cb.construct(ManageInvoiceStatusProjection.class,
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
                root.get("hasAttachments"),
                root.get("invoiceType"),
                root.get("invoiceStatus"),
                root.get("invoiceNumber"),
                cb.construct(ManageInvoiceTypeProjection.class,
                        typeJoin.get("id"),
                        typeJoin.get("name"),
                        typeJoin.get("code")
                ),
                root.get("sendStatusError"),
                root.get("parent").get("id"),
                root.get("autoRec"),
                root.get("originalAmount"),
                root.get("importType"),
                root.get("cloneParent"),
                root.get("aging"),
                cb.selectCase()
                        .when(cb.and(
                                cb.isNotNull(hotelJoin.get("closeOperation")),
                                cb.greaterThanOrEqualTo(root.get("invoiceDate"), hotelJoin.get("closeOperation").get("beginDate")),
                                cb.lessThanOrEqualTo(root.get("invoiceDate"), hotelJoin.get("closeOperation").get("endDate"))
                        ), true)
                        .otherwise(false),
                root.get("dueDate")
        ));

        // Aplicar especificaciones
        if (specification != null) {
            Predicate predicate = specification.toPredicate(root, query, cb);
            query.where(predicate);
        }

        // Orden y paginación
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        TypedQuery<ManageInvoiceSearchProjection> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        // Contar el total de resultados
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Invoice> countRoot = countQuery.from(Invoice.class);
        countQuery.select(cb.count(countRoot));

        if (specification != null) {
            Predicate countPredicate = specification.toPredicate(countRoot, countQuery, cb);
            countQuery.where(countPredicate);
        }

        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(typedQuery.getResultList(), pageable, total);
    }
}