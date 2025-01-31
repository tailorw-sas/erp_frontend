package com.kynsoft.finamer.payment.infrastructure.repository.query.payments;

import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.infrastructure.identity.*;
import com.kynsoft.finamer.payment.infrastructure.identity.projection.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<PaymentSearchProjection> findAllProjected(Specification<Payment> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Payment> root = query.from(Payment.class);
        Join<Payment, ManagePaymentStatus> statusJoin = root.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> sourceJoin = root.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManageAgency> agencyJoin = root.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> agencyTypeJoin = agencyJoin.join("agencyType", JoinType.LEFT);
        Join<Payment, ManageBankAccount> bankAccountJoin = root.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManageClient> clientJoin = root.join("client", JoinType.LEFT);
        Join<Payment, ManageHotel> hotelJoin = root.join("hotel", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> attachmentStatusJoin = root.join("attachmentStatus", JoinType.LEFT);

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.add(root.get("paymentId"));
        selections.add(root.get("transactionDate"));
        selections.add(root.get("reference"));

        // Payment Status
        selections.add(statusJoin.get("id"));
        selections.add(statusJoin.get("code"));
        selections.add(statusJoin.get("name"));
        selections.add(statusJoin.get("confirmed"));
        selections.add(statusJoin.get("applied"));
        selections.add(statusJoin.get("cancelled"));
        selections.add(statusJoin.get("transit"));
        selections.add(statusJoin.get("status"));

        // Payment Source
        selections.add(sourceJoin.get("id"));
        selections.add(sourceJoin.get("code"));
        selections.add(sourceJoin.get("name"));
        selections.add(sourceJoin.get("status"));

        // Agency
        selections.add(agencyJoin.get("id"));
        selections.add(agencyJoin.get("code"));
        selections.add(agencyJoin.get("name"));
        selections.add(agencyJoin.get("status"));

        // Agency Type
        selections.add(agencyTypeJoin.get("id"));
        selections.add(agencyTypeJoin.get("code"));
        selections.add(agencyTypeJoin.get("name"));
        selections.add(agencyTypeJoin.get("status"));

        // Bank Account
        selections.add(bankAccountJoin.get("id"));
        selections.add(bankAccountJoin.get("accountNumber"));
        selections.add(bankAccountJoin.get("nameOfBank"));
        selections.add(bankAccountJoin.get("status"));

        // Client
        selections.add(clientJoin.get("id"));
        selections.add(clientJoin.get("code"));
        selections.add(clientJoin.get("name"));
        selections.add(clientJoin.get("status"));

        // Hotel
        selections.add(hotelJoin.get("id"));
        selections.add(hotelJoin.get("code"));
        selections.add(hotelJoin.get("name"));
        selections.add(hotelJoin.get("status"));

        // Payment Attachment Status
        selections.add(attachmentStatusJoin.get("id"));
        selections.add(attachmentStatusJoin.get("code"));
        selections.add(attachmentStatusJoin.get("name"));
        selections.add(attachmentStatusJoin.get("status"));
        selections.add(attachmentStatusJoin.get("defaults"));
        selections.add(attachmentStatusJoin.get("nonNone"));
        selections.add(attachmentStatusJoin.get("patWithAttachment"));
        selections.add(attachmentStatusJoin.get("pwaWithOutAttachment"));
        selections.add(attachmentStatusJoin.get("supported"));
        selections.add(attachmentStatusJoin.get("otherSupport"));

        // Payment details
        selections.add(root.get("paymentAmount"));
        selections.add(root.get("paymentBalance"));
        selections.add(root.get("depositAmount"));
        selections.add(root.get("depositBalance"));
        selections.add(root.get("otherDeductions"));
        selections.add(root.get("identified"));
        selections.add(root.get("notIdentified"));
        selections.add(root.get("notApplied"));
        selections.add(root.get("applied"));
        selections.add(root.get("remark"));
        selections.add(root.get("eAttachment"));
        selections.add(root.get("applyPayment"));
        selections.add(root.get("paymentSupport"));
        selections.add(root.get("createByCredit"));
        selections.add(root.get("hasAttachment"));
        selections.add(root.get("hasDetailTypeDeposit"));

        query.multiselect(selections.toArray(new Selection[0]));

        if (specification != null) {
            Predicate predicate = specification.toPredicate(root, query, cb);
            query.where(predicate);
        }

        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        TypedQuery<Tuple> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<Tuple> tuples = typedQuery.getResultList();

        List<PaymentSearchProjection> results = tuples.stream().map(tuple -> {
            return new PaymentSearchProjection(
                    tuple.get(0, UUID.class),   // id
                    tuple.get(1, Long.class),   // paymentId
                    tuple.get(2, java.time.LocalDate.class),   // transactionDate
                    tuple.get(3, String.class),   // reference
                    new PaymentStatusProjection(
                            tuple.get(4, UUID.class),
                            tuple.get(5, String.class),
                            tuple.get(6, String.class),
                            tuple.get(7, Boolean.class),
                            tuple.get(8, Boolean.class),
                            tuple.get(9, Boolean.class),
                            tuple.get(10, Boolean.class),
                            tuple.get(11, String.class)
                    ),
                    new PaymentSourceProjection(
                            tuple.get(12, UUID.class),
                            tuple.get(13, String.class),
                            tuple.get(14, String.class),
                            tuple.get(15, String.class)
                    ),
                    new AgencyProjection(
                            tuple.get(16, UUID.class),
                            tuple.get(17, String.class),
                            tuple.get(18, String.class),
                            tuple.get(19, String.class),
                            new ManageAgencyTypeProjection(
                                    tuple.get(20, UUID.class),
                                    tuple.get(21, String.class),
                                    tuple.get(22, String.class),
                                    tuple.get(23, String.class)
                            )
                    ),
                    new BankAccountProjection(
                            tuple.get(24, UUID.class),
                            tuple.get(25, String.class),
                            tuple.get(26, String.class),
                            tuple.get(27, String.class)
                    ),
                    new ClientProjection(
                            tuple.get(28, UUID.class),
                            tuple.get(29, String.class),
                            tuple.get(30, String.class),
                            tuple.get(31, String.class)
                    ),
                    new HotelProjection(
                            tuple.get(32, UUID.class),
                            tuple.get(33, String.class),
                            tuple.get(34, String.class),
                            tuple.get(35, String.class)
                    ),
                    new PaymentAttachmentStatusProjection(
                            tuple.get(36, UUID.class),
                            tuple.get(37, String.class),
                            tuple.get(38, String.class),
                            tuple.get(39, String.class),
                            tuple.get(40, Boolean.class), // defaults
                            tuple.get(41, Boolean.class), // nonNone
                            tuple.get(42, Boolean.class), // patWithAttachment
                            tuple.get(43, Boolean.class), // pwaWithOutAttachment
                            tuple.get(44, Boolean.class), // supported
                            tuple.get(45, Boolean.class)  // otherSupport
                    ),
                    tuple.get(46, Double.class), // paymentAmount
                    tuple.get(47, Double.class), // paymentBalance
                    tuple.get(48, Double.class), // depositAmount
                    tuple.get(49, Double.class), // depositBalance
                    tuple.get(50, Double.class), // otherDeductions
                    tuple.get(51, Double.class), // identified
                    tuple.get(52, Double.class), // notIdentified
                    tuple.get(53, Double.class), // notApplied
                    tuple.get(54, Double.class), // applied
                    tuple.get(55, String.class), // remark
                    tuple.get(56, EAttachment.class), // eAttachment
                    tuple.get(57, Boolean.class), // applyPayment
                    tuple.get(58, Boolean.class), // paymentSupport
                    tuple.get(59, Boolean.class) , // createByCredit
                    tuple.get(60, Boolean.class) , // hasAttachment
                    tuple.get(61, Boolean.class)  // hasDetailTypeDeposit
            );
        }).collect(Collectors.toList());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Payment> countRoot = countQuery.from(Payment.class);
        countQuery.select(cb.count(countRoot));

        if (specification != null) {
            Predicate countPredicate = specification.toPredicate(countRoot, countQuery, cb);
            countQuery.where(countPredicate);
        }
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }
}