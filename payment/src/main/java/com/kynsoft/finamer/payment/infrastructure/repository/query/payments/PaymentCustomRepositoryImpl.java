package com.kynsoft.finamer.payment.infrastructure.repository.query.payments;

import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Payment> findByIdCustom(UUID id){
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<Payment> root = query.from(Payment.class);
        Join<Payment, ManagePaymentStatus> statusJoin = root.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> sourceJoin = root.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManageAgency> agencyJoin = root.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> agencyTypeJoin = agencyJoin.join("agencyType", JoinType.LEFT);
        Join<Payment, ManageBankAccount> bankAccountJoin = root.join("bankAccount", JoinType.LEFT);
        Join<ManageBankAccount, ManageHotel> bankAccountHotelJoin = bankAccountJoin.join("manageHotel", JoinType.LEFT);
        Join<Payment, ManageClient> clientJoin = root.join("client", JoinType.LEFT);
        Join<Payment, ManageHotel> hotelJoin = root.join("hotel", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> attachmentStatusJoin = root.join("attachmentStatus", JoinType.LEFT);
        Join<Payment, MasterPaymentAttachment> masterPaymentAttachmentJoin = root.join("attachments", JoinType.LEFT);

        query.where(cb.equal(root.get("id"), id));

        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.add(root.get("paymentId"));
        selections.add(root.get("status"));
        selections.add(root.get("eAttachment"));

        // Payment Source
        selections.add(sourceJoin.get("id"));
        selections.add(sourceJoin.get("code"));
        selections.add(sourceJoin.get("name"));
        selections.add(sourceJoin.get("status"));
        selections.add(sourceJoin.get("expense"));
        selections.add(sourceJoin.get("isBank"));

        selections.add(root.get("reference"));
        selections.add(root.get("transactionDate"));
        selections.add(root.get("dateTime"));

        // Payment Status
        selections.add(statusJoin.get("id"));
        selections.add(statusJoin.get("code"));
        selections.add(statusJoin.get("name"));
        selections.add(statusJoin.get("status"));
        selections.add(statusJoin.get("applied"));
        selections.add(statusJoin.get("confirmed"));
        selections.add(statusJoin.get("cancelled"));
        selections.add(statusJoin.get("transit"));

        // Client
        selections.add(clientJoin.get("id"));
        selections.add(clientJoin.get("code"));
        selections.add(clientJoin.get("name"));
        selections.add(clientJoin.get("status"));

        // Agency
        selections.add(agencyJoin.get("id"));
        selections.add(agencyJoin.get("code"));
        selections.add(agencyJoin.get("name"));
        selections.add(agencyJoin.get("status"));

        //Agency Type
        selections.add(agencyTypeJoin.get("id"));
        selections.add(agencyTypeJoin.get("code"));
        selections.add(agencyTypeJoin.get("status"));
        selections.add(agencyTypeJoin.get("name"));

        // Hotel
        selections.add(hotelJoin.get("id"));
        selections.add(hotelJoin.get("code"));
        selections.add(hotelJoin.get("name"));
        selections.add(hotelJoin.get("status"));
        selections.add(hotelJoin.get("applyByTradingCompany"));
        selections.add(hotelJoin.get("manageTradingCompany"));
        selections.add(hotelJoin.get("autoApplyCredit"));

        // Bank Account
        selections.add(bankAccountJoin.get("id"));
        selections.add(bankAccountJoin.get("accountNumber"));
        selections.add(bankAccountJoin.get("status"));
        selections.add(bankAccountJoin.get("nameOfBank"));

        selections.add(bankAccountHotelJoin.get("id"));
        selections.add(bankAccountHotelJoin.get("code"));
        selections.add(bankAccountHotelJoin.get("name"));
        selections.add(bankAccountHotelJoin.get("status"));
        selections.add(bankAccountHotelJoin.get("applyByTradingCompany"));
        selections.add(bankAccountHotelJoin.get("manageTradingCompany"));
        selections.add(bankAccountHotelJoin.get("autoApplyCredit"));

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
        selections.add(root.get("applyPayment"));
        selections.add(root.get("hasAttachment"));
        selections.add(root.get("hasDetailTypeDeposit"));
        selections.add(root.get("paymentSupport"));
        selections.add(root.get("createByCredit"));
        selections.add(root.get("createdAt"));
        selections.add(root.get("updatedAt"));
        selections.add(root.get("importType"));

        query.multiselect(selections.toArray(new Selection[0]));

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        Payment payment = new Payment(
                tuple.get(0, UUID.class),
                tuple.get(1, Long.class),
                tuple.get(2, Status.class),
                tuple.get(3, EAttachment.class),
                new ManagePaymentSource(
                        tuple.get(4, UUID.class),
                        tuple.get(5, String.class),
                        tuple.get(6, String.class),
                        tuple.get(7, String.class),
                        tuple.get(8, Boolean.class),
                        tuple.get(9, Boolean.class)
                ),
                tuple.get(10, String.class),
                tuple.get(11, LocalDate.class),
                tuple.get(12, LocalTime.class),
                new ManagePaymentStatus(
                        tuple.get(13, UUID.class),
                        tuple.get(14, String.class),
                        tuple.get(15, String.class),
                        tuple.get(16, String.class),
                        tuple.get(17, Boolean.class),
                        tuple.get(18, Boolean.class),
                        tuple.get(19, Boolean.class),
                        tuple.get(20, Boolean.class)
                ),
                new ManageClient(
                        tuple.get(21, UUID.class),
                        tuple.get(22, String.class),
                        tuple.get(23, String.class),
                        tuple.get(24, String.class)
                ),
                new ManageAgency(
                        tuple.get(25, UUID.class),
                        tuple.get(26, String.class),
                        tuple.get(27, String.class),
                        tuple.get(28, String.class),
                        new ManageAgencyType(
                                tuple.get(29, UUID.class),
                                tuple.get(30, String.class),
                                tuple.get(31, String.class),
                                tuple.get(32, String.class)
                        )
                ),
                new ManageHotel(
                        tuple.get(33, UUID.class),
                        tuple.get(34, String.class),
                        tuple.get(35, String.class),
                        tuple.get(36, String.class),
                        tuple.get(37, Boolean.class),
                        tuple.get(38, UUID.class),
                        tuple.get(39, Boolean.class)
                ),
                null,
                new ManageBankAccount(
                        tuple.get(40, UUID.class),
                        tuple.get(41, String.class),
                        tuple.get(42, String.class),
                        tuple.get(43, String.class),
                        new ManageHotel(
                                tuple.get(44, UUID.class),
                                tuple.get(45, String.class),
                                tuple.get(46, String.class),
                                tuple.get(47, String.class),
                                tuple.get(48, Boolean.class),
                                tuple.get(49, UUID.class),
                                tuple.get(50, Boolean.class)
                        )
                ),
                new ManagePaymentAttachmentStatus(
                        tuple.get(51, UUID.class),
                        tuple.get(52, String.class),
                        tuple.get(53, String.class),
                        tuple.get(54, String.class),
                        tuple.get(55, Boolean.class),
                        tuple.get(56, Boolean.class),
                        tuple.get(57, Boolean.class),
                        tuple.get(58, Boolean.class),
                        tuple.get(59, Boolean.class),
                        tuple.get(60, Boolean.class)
                ),
                Collections.emptyList(),
                Collections.emptyList(),
                tuple.get(61, Double.class),
                tuple.get(62, Double.class),
                tuple.get(63, Double.class),
                tuple.get(64, Double.class),
                tuple.get(65, Double.class),
                tuple.get(66, Double.class),
                tuple.get(67, Double.class),
                tuple.get(68, Double.class),
                tuple.get(69, Double.class),
                tuple.get(70, String.class),
                tuple.get(71, Boolean.class),
                tuple.get(72, Boolean.class),
                tuple.get(73, Boolean.class),
                tuple.get(74, Boolean.class),
                tuple.get(75, Boolean.class),
                tuple.get(76, OffsetDateTime.class),
                tuple.get(77, OffsetDateTime.class),
                tuple.get(78, ImportType.class)
        );

        return Optional.of(payment);
    }

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
        selections.add(sourceJoin.get("expense"));
        selections.add(sourceJoin.get("isBank"));

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
        selections.add(hotelJoin.get("applyByTradingCompany"));
        selections.add(hotelJoin.get("manageTradingCompany"));

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
        selections.add(root.get("importType"));

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
                            tuple.get(15, String.class),
                            tuple.get(16, Boolean.class),
                            tuple.get(17, Boolean.class)
                    ),
                    new AgencyProjection(
                            tuple.get(18, UUID.class),
                            tuple.get(19, String.class),
                            tuple.get(20, String.class),
                            tuple.get(21, String.class),
                            new ManageAgencyTypeProjection(
                                    tuple.get(22, UUID.class),
                                    tuple.get(23, String.class),
                                    tuple.get(24, String.class),
                                    tuple.get(25, String.class)
                            )
                    ),
                    new BankAccountProjection(
                            tuple.get(26, UUID.class),
                            tuple.get(27, String.class),
                            tuple.get(28, String.class),
                            tuple.get(29, String.class)
                    ),
                    new ClientProjection(
                            tuple.get(30, UUID.class),
                            tuple.get(31, String.class),
                            tuple.get(32, String.class),
                            tuple.get(33, String.class)
                    ),
                    new HotelProjection(
                            tuple.get(34, UUID.class),
                            tuple.get(35, String.class),
                            tuple.get(36, String.class),
                            tuple.get(37, String.class),
                            tuple.get(38, Boolean.class),
                            tuple.get(39, UUID.class)
                    ),
                    new PaymentAttachmentStatusProjection(
                            tuple.get(40, UUID.class),
                            tuple.get(41, String.class),
                            tuple.get(42, String.class),
                            tuple.get(43, String.class),
                            tuple.get(44, Boolean.class), // defaults
                            tuple.get(45, Boolean.class), // nonNone
                            tuple.get(46, Boolean.class), // patWithAttachment
                            tuple.get(47, Boolean.class), // pwaWithOutAttachment
                            tuple.get(48, Boolean.class), // supported
                            tuple.get(49, Boolean.class)  // otherSupport
                    ),
                    tuple.get(50, Double.class), // paymentAmount
                    tuple.get(51, Double.class), // paymentBalance
                    tuple.get(52, Double.class), // depositAmount
                    tuple.get(53, Double.class), // depositBalance
                    tuple.get(54, Double.class), // otherDeductions
                    tuple.get(55, Double.class), // identified
                    tuple.get(56, Double.class), // notIdentified
                    tuple.get(57, Double.class), // notApplied
                    tuple.get(58, Double.class), // applied
                    tuple.get(59, String.class), // remark
                    tuple.get(60, EAttachment.class), // eAttachment
                    tuple.get(61, Boolean.class), // applyPayment
                    tuple.get(62, Boolean.class), // paymentSupport
                    tuple.get(63, Boolean.class) , // createByCredit
                    tuple.get(64, Boolean.class) , // hasAttachment
                    tuple.get(65, Boolean.class),  // hasDetailTypeDeposit
                    tuple.get(66, ImportType.class) // ImportType
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