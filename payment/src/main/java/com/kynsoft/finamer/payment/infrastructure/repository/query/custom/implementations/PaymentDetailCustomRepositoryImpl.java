package com.kynsoft.finamer.payment.infrastructure.repository.query.custom.implementations;

import com.kynsoft.finamer.payment.domain.dtoEnum.EAttachment;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.dtoEnum.ImportType;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.infrastructure.identity.*;
import com.kynsoft.finamer.payment.infrastructure.repository.query.custom.PaymentDetailCustomRepository;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PaymentDetailCustomRepositoryImpl implements PaymentDetailCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<PaymentDetail> findByIdCustom(UUID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> root = query.from(PaymentDetail.class);
        Join<PaymentDetail, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = root.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = root.join("manageBooking", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        query.where(cb.equal(root.get("id"), id ));

        List<Selection<?>> selections = getPaymentDetailSelections(root,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        PaymentDetail paymentDetail = this.convertTupleToEntity(tuple);
        List<PaymentDetail> childrens = findChildrenWithDetailsByParentId(paymentDetail.getId());
        if(Objects.nonNull(childrens) && !childrens.isEmpty()){
            paymentDetail.setPaymentDetails(childrens);
        }

        return Optional.of(paymentDetail);
    }

    @Override
    public Optional<PaymentDetail> findByPaymentDetailIdCustom(int id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> root = query.from(PaymentDetail.class);
        Join<PaymentDetail, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = root.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = root.join("manageBooking", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        query.where(cb.equal(root.get("paymentDetailId"), id ));

        List<Selection<?>> selections = getPaymentDetailSelections(root,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        Tuple tuple = entityManager.createQuery(query).getSingleResult();

        PaymentDetail paymentDetail = this.convertTupleToEntity(tuple);
        List<PaymentDetail> childrens = findChildrenWithDetailsByParentId(paymentDetail.getId());
        if(Objects.nonNull(childrens) && !childrens.isEmpty()){
            paymentDetail.setPaymentDetails(childrens);
        }

        return Optional.of(paymentDetail);
    }

    @Override
    public List<PaymentDetail> findAllByPaymentIdCustom(UUID id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> root = query.from(PaymentDetail.class);
        Join<PaymentDetail, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = root.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = root.join("manageBooking", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        query.where(cb.equal(paymentJoin.get("id"), id ));

        List<Selection<?>> selections = getPaymentDetailSelections(root,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<PaymentDetail> results = tuples.stream()
                .map(this::convertTupleToEntity).collect(Collectors.toList());

        return results;
    }

    @Override
    public List<PaymentDetail> findChildrensByParentId(Long parentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> root = query.from(PaymentDetail.class);
        Join<PaymentDetail, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = root.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = root.join("manageBooking", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        query.where(cb.equal(root.get("parentId"), parentId ));

        List<Selection<?>> selections = getPaymentDetailSelections(root,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<PaymentDetail> results = tuples.stream()
                .map(this::convertTupleToEntity).collect(Collectors.toList());

        return results;
    }

    public List<PaymentDetail> findChildrenWithDetailsByParentId(UUID parentId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> parent = query.from(PaymentDetail.class);
        Join<PaymentDetail, PaymentDetail> children = parent.join("paymentDetails", JoinType.INNER);

        // Joins sobre el hijo
        Join<PaymentDetail, Payment> paymentJoin = children.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = children.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = children.join("manageBooking", JoinType.LEFT);

        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        query.where(cb.equal(parent.get("id"), parentId));

        List<Selection<?>> selections = getChildrensPaymentDetailSelections(
                children,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin
        );

        query.multiselect(selections.toArray(new Selection[0]));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        return tuples.stream().map(this::convertTupleToEntity).collect(Collectors.toList());
    }


    @Override
    public List<PaymentDetail> findAllByPaymentGenIdIn(List<Long> genIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> root = query.from(PaymentDetail.class);
        Join<PaymentDetail, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = root.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = root.join("manageBooking", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        query.where(root.get("paymentDetailId").in(genIds));

        List<Selection<?>> selections = getPaymentDetailSelections(root,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<PaymentDetail> results = tuples.stream()
                .map(this::convertTupleToEntity).collect(Collectors.toList());

        return results;
    }

    @Override
    public Page<PaymentDetail> findAllCustom(Specification<PaymentDetail> specification, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> root = query.from(PaymentDetail.class);
        Join<PaymentDetail, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = root.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = root.join("manageBooking", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        List<Selection<?>> selections = getPaymentDetailSelections(root,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin);

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

        List<PaymentDetail> results = tuples.stream()
                .map(this::convertTupleToEntity).collect(Collectors.toList());

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PaymentDetail> countRoot = countQuery.from(PaymentDetail.class);
        countQuery.select(cb.count(countRoot));

        if (specification != null) {
            Predicate countPredicate = specification.toPredicate(countRoot, countQuery, cb);
            countQuery.where(countPredicate);
        }
        long total = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public List<PaymentDetail> findAllByPaymentGenIdInCustom(List<Long> paymentGenIds) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<PaymentDetail> root = query.from(PaymentDetail.class);
        Join<PaymentDetail, Payment> paymentJoin = root.join("payment", JoinType.LEFT);
        Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin = root.join("transactionType", JoinType.LEFT);
        Join<PaymentDetail, Booking> bookingJoin = root.join("manageBooking", JoinType.LEFT);
        Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin = paymentJoin.join("paymentSource", JoinType.LEFT);
        Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin = paymentJoin.join("paymentStatus", JoinType.LEFT);
        Join<Payment, ManageClient> paymentManageClientJoin = paymentJoin.join("client", JoinType.LEFT);
        Join<Payment, ManageAgency> paymentManageAgencyJoin = paymentJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType = paymentManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentManageAgencyManageClient = paymentManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry = paymentManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageHotel> paymentManageHotelJoin = paymentJoin.join("hotel", JoinType.LEFT);
        Join<Payment, Invoice> paymentInvoiceJoin = paymentJoin.join("invoice", JoinType.LEFT);
        Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin = paymentInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin = paymentInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType = paymentInvoiceManageAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient = paymentInvoiceManageAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry = paymentInvoiceManageAgencyJoin.join("country", JoinType.LEFT);
        Join<Payment, ManageBankAccount> paymentManageBankAccountJoin = paymentJoin.join("bankAccount", JoinType.LEFT);
        Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin = paymentJoin.join("attachmentStatus", JoinType.LEFT);
        Join<Booking, Invoice> bookingInvoiceJoin = bookingJoin.join("invoice", JoinType.LEFT);

        query.where(paymentJoin.get("paymentId").in(paymentGenIds));

        List<Selection<?>> selections = getPaymentDetailSelections(root,
                paymentJoin,
                transactionTypeJoin,
                bookingJoin,
                paymentManagePaymentSourceJoin,
                paymentManagePaymentStatusJoin,
                paymentManageClientJoin,
                paymentManageAgencyJoin,
                paymentManageAgencyManageAgencyType,
                paymentManageAgencyManageClient,
                paymentManageAgencyManageCountry,
                paymentManageHotelJoin,
                paymentInvoiceJoin,
                paymentInvoiceManageHotelJoin,
                paymentInvoiceManageAgencyJoin,
                paymentInvoiceManageAgencyManageAgencyType,
                paymentInvoiceManageAgencyManageClient,
                paymentInvoiceManageAgencyManageCountry,
                paymentManageBankAccountJoin,
                paymentManagePaymentAttachmentStatusJoin,
                bookingInvoiceJoin);

        query.multiselect(selections.toArray(new Selection[0]));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<PaymentDetail> results = tuples.stream()
                .map(this::convertTupleToEntity).collect(Collectors.toList());

        return results;
    }

    private List<Selection<?>> getPaymentDetailSelections(Root<PaymentDetail> root,
                                                          Join<PaymentDetail, Payment> paymentJoin,
                                                          Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin,
                                                          Join<PaymentDetail, Booking> bookingJoin,
                                                          Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin,
                                                          Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin,
                                                          Join<Payment, ManageClient> paymentManageClientJoin,
                                                          Join<Payment, ManageAgency> paymentManageAgencyJoin,
                                                          Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType,
                                                          Join<ManageAgency, ManageClient> paymentManageAgencyManageClient,
                                                          Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry,
                                                          Join<Payment, ManageHotel> paymentManageHotelJoin,
                                                          Join<Payment, Invoice> paymentInvoiceJoin,
                                                          Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin,
                                                          Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin,
                                                          Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType,
                                                          Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient,
                                                          Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry,
                                                          Join<Payment, ManageBankAccount> paymentManageBankAccountJoin,
                                                          Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin,
                                                          Join<Booking, Invoice> bookingInvoiceJoin){
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(root.get("id"));
        selections.add(root.get("status"));
        selections.add(root.get("paymentDetailId"));
        selections.add(root.get("parentId"));

        //Payment
        selections.add(paymentJoin.get("id"));
        selections.add(paymentJoin.get("paymentId"));
        selections.add(paymentJoin.get("status"));
        selections.add(paymentJoin.get("eAttachment"));

        // Payment - Payment Source
        selections.add(paymentManagePaymentSourceJoin.get("id"));
        selections.add(paymentManagePaymentSourceJoin.get("code"));
        selections.add(paymentManagePaymentSourceJoin.get("name"));
        selections.add(paymentManagePaymentSourceJoin.get("status"));
        selections.add(paymentManagePaymentSourceJoin.get("expense"));
        selections.add(paymentManagePaymentSourceJoin.get("isBank"));

        selections.add(paymentJoin.get("reference"));
        selections.add(paymentJoin.get("transactionDate"));
        selections.add(paymentJoin.get("dateTime"));

        // Payment - Payment Status
        selections.add(paymentManagePaymentStatusJoin.get("id"));
        selections.add(paymentManagePaymentStatusJoin.get("code"));
        selections.add(paymentManagePaymentStatusJoin.get("name"));
        selections.add(paymentManagePaymentStatusJoin.get("status"));
        selections.add(paymentManagePaymentStatusJoin.get("applied"));
        selections.add(paymentManagePaymentStatusJoin.get("confirmed"));
        selections.add(paymentManagePaymentStatusJoin.get("cancelled"));
        selections.add(paymentManagePaymentStatusJoin.get("transit"));

        // Payment - Payment Client
        selections.add(paymentManageClientJoin.get("id"));
        selections.add(paymentManageClientJoin.get("code"));
        selections.add(paymentManageClientJoin.get("name"));
        selections.add(paymentManageClientJoin.get("status"));

        // Payment - Payment Agency
        selections.add(paymentManageAgencyJoin.get("id"));
        selections.add(paymentManageAgencyJoin.get("code"));
        selections.add(paymentManageAgencyJoin.get("name"));
        selections.add(paymentManageAgencyJoin.get("status"));

        // Payment - Payment Agency - Agency Type
        selections.add(paymentManageAgencyManageAgencyType.get("id"));
        selections.add(paymentManageAgencyManageAgencyType.get("code"));
        selections.add(paymentManageAgencyManageAgencyType.get("status"));
        selections.add(paymentManageAgencyManageAgencyType.get("name"));

        // Payment - Payment Agency - Client
        selections.add(paymentManageAgencyManageClient.get("id"));
        selections.add(paymentManageAgencyManageClient.get("code"));
        selections.add(paymentManageAgencyManageClient.get("name"));
        selections.add(paymentManageAgencyManageClient.get("status"));

        // Payment - Payment Agency - Country
        selections.add(paymentManageAgencyManageCountry.get("id"));
        selections.add(paymentManageAgencyManageCountry.get("code"));
        selections.add(paymentManageAgencyManageCountry.get("name"));
        selections.add(paymentManageAgencyManageCountry.get("description"));
        selections.add(paymentManageAgencyManageCountry.get("isDefault"));
        selections.add(paymentManageAgencyManageCountry.get("status"));
        selections.add(paymentManageAgencyManageCountry.get("createdAt"));
        selections.add(paymentManageAgencyManageCountry.get("updateAt"));
        selections.add(paymentManageAgencyManageCountry.get("deleteAt"));
        selections.add(paymentManageAgencyManageCountry.get("iso3"));

        selections.add(paymentManageAgencyJoin.get("createdAt"));
        selections.add(paymentManageAgencyJoin.get("updatedAt"));

        // Payment - Payment Hotel
        selections.add(paymentManageHotelJoin.get("id"));
        selections.add(paymentManageHotelJoin.get("code"));
        selections.add(paymentManageHotelJoin.get("deleted"));
        selections.add(paymentManageHotelJoin.get("name"));
        selections.add(paymentManageHotelJoin.get("status"));
        selections.add(paymentManageHotelJoin.get("applyByTradingCompany"));
        selections.add(paymentManageHotelJoin.get("manageTradingCompany"));
        selections.add(paymentManageHotelJoin.get("autoApplyCredit"));
        selections.add(paymentManageHotelJoin.get("createdAt"));
        selections.add(paymentManageHotelJoin.get("updatedAt"));
        selections.add(paymentManageHotelJoin.get("deletedAt"));

        //Payment - Payment - Invoice
        selections.add(paymentInvoiceJoin.get("id"));
        selections.add(paymentInvoiceJoin.get("invoiceId"));
        selections.add(paymentInvoiceJoin.get("invoiceNo"));
        selections.add(paymentInvoiceJoin.get("invoiceNumber"));
        selections.add(paymentInvoiceJoin.get("invoiceAmount"));
        selections.add(paymentInvoiceJoin.get("invoiceDate"));
        selections.add(paymentInvoiceJoin.get("hasAttachment"));
        selections.add(paymentInvoiceJoin.get("invoiceType"));

        //Payment - Payment - Invoice Hotel
        selections.add(paymentInvoiceManageHotelJoin.get("id"));
        selections.add(paymentInvoiceManageHotelJoin.get("code"));
        selections.add(paymentInvoiceManageHotelJoin.get("deleted"));
        selections.add(paymentInvoiceManageHotelJoin.get("name"));
        selections.add(paymentInvoiceManageHotelJoin.get("status"));
        selections.add(paymentInvoiceManageHotelJoin.get("applyByTradingCompany"));
        selections.add(paymentInvoiceManageHotelJoin.get("manageTradingCompany"));
        selections.add(paymentInvoiceManageHotelJoin.get("autoApplyCredit"));
        selections.add(paymentInvoiceManageHotelJoin.get("createdAt"));
        selections.add(paymentInvoiceManageHotelJoin.get("updatedAt"));
        selections.add(paymentInvoiceManageHotelJoin.get("deletedAt"));

        //Payment - Payment - Invoice Agency
        selections.add(paymentInvoiceManageAgencyJoin.get("id"));
        selections.add(paymentInvoiceManageAgencyJoin.get("code"));
        selections.add(paymentInvoiceManageAgencyJoin.get("name"));
        selections.add(paymentInvoiceManageAgencyJoin.get("status"));

        //Payment - Payment - Invoice Agency Type
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("id"));
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("code"));
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("status"));
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("name"));

        // Payment - Payment Agency - Client
        selections.add(paymentInvoiceManageAgencyManageClient.get("id"));
        selections.add(paymentInvoiceManageAgencyManageClient.get("code"));
        selections.add(paymentInvoiceManageAgencyManageClient.get("name"));
        selections.add(paymentInvoiceManageAgencyManageClient.get("status"));

        // Payment - Payment Agency - Country
        selections.add(paymentInvoiceManageAgencyManageCountry.get("id"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("code"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("name"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("description"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("isDefault"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("status"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("createdAt"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("updateAt"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("deleteAt"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("iso3"));

        selections.add(paymentInvoiceManageAgencyJoin.get("createdAt"));
        selections.add(paymentInvoiceManageAgencyJoin.get("updatedAt"));

        //Payment - resto
        selections.add(paymentInvoiceJoin.get("autoRec"));

        // Payment - Payment Bank Account
        selections.add(paymentManageBankAccountJoin.get("id"));
        selections.add(paymentManageBankAccountJoin.get("accountNumber"));
        selections.add(paymentManageBankAccountJoin.get("status"));
        selections.add(paymentManageBankAccountJoin.get("nameOfBank"));

        // Payment - Payment Attachement Status
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("id"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("code"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("name"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("status"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("defaults"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("nonNone"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("patWithAttachment"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("pwaWithOutAttachment"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("supported"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("otherSupport"));

        //Payment
        selections.add(paymentJoin.get("paymentAmount"));
        selections.add(paymentJoin.get("paymentBalance"));
        selections.add(paymentJoin.get("depositAmount"));
        selections.add(paymentJoin.get("depositBalance"));
        selections.add(paymentJoin.get("otherDeductions"));
        selections.add(paymentJoin.get("identified"));
        selections.add(paymentJoin.get("notIdentified"));
        selections.add(paymentJoin.get("notApplied"));
        selections.add(paymentJoin.get("applied"));
        selections.add(paymentJoin.get("remark"));
        selections.add(paymentJoin.get("applyPayment"));
        selections.add(paymentJoin.get("hasAttachment"));
        selections.add(paymentJoin.get("hasDetailTypeDeposit"));
        selections.add(paymentJoin.get("paymentSupport"));
        selections.add(paymentJoin.get("createByCredit"));
        selections.add(paymentJoin.get("createdAt"));
        selections.add(paymentJoin.get("updatedAt"));
        selections.add(paymentJoin.get("importType"));

        //ManagePaymentTransactionType
        selections.add(transactionTypeJoin.get("id"));
        selections.add(transactionTypeJoin.get("code"));
        selections.add(transactionTypeJoin.get("name"));
        selections.add(transactionTypeJoin.get("status"));
        selections.add(transactionTypeJoin.get("cash"));
        selections.add(transactionTypeJoin.get("deposit"));
        selections.add(transactionTypeJoin.get("applyDeposit"));
        selections.add(transactionTypeJoin.get("remarkRequired"));
        selections.add(transactionTypeJoin.get("minNumberOfCharacter"));
        selections.add(transactionTypeJoin.get("defaultRemark"));
        selections.add(transactionTypeJoin.get("defaults"));
        selections.add(transactionTypeJoin.get("paymentInvoice"));
        selections.add(transactionTypeJoin.get("debit"));
        selections.add(transactionTypeJoin.get("expenseToBooking"));
        selections.add(transactionTypeJoin.get("negative"));

        //Booking
        selections.add(bookingJoin.get("id"));
        selections.add(bookingJoin.get("bookingId"));
        selections.add(bookingJoin.get("reservationNumber"));
        selections.add(bookingJoin.get("checkIn"));
        selections.add(bookingJoin.get("checkOut"));
        selections.add(bookingJoin.get("fullName"));
        selections.add(bookingJoin.get("firstName"));
        selections.add(bookingJoin.get("lastName"));
        selections.add(bookingJoin.get("invoiceAmount"));
        selections.add(bookingJoin.get("amountBalance"));
        selections.add(bookingJoin.get("couponNumber"));
        selections.add(bookingJoin.get("adults"));
        selections.add(bookingJoin.get("children"));

        //Booking - Invoice
        selections.add(bookingInvoiceJoin.get("id"));
        selections.add(bookingInvoiceJoin.get("invoiceId"));
        selections.add(bookingInvoiceJoin.get("invoiceNo"));
        selections.add(bookingInvoiceJoin.get("invoiceNumber"));
        selections.add(bookingInvoiceJoin.get("invoiceAmount"));
        selections.add(bookingInvoiceJoin.get("invoiceDate"));
        selections.add(bookingInvoiceJoin.get("hasAttachment"));
        selections.add(bookingInvoiceJoin.get("invoiceType"));
        selections.add(bookingInvoiceJoin.get("autoRec"));

        //Booking - resto
        selections.add(bookingJoin.get("bookingDate"));

        selections.add(root.get("reverseFrom"));
        selections.add(root.get("reverseFromParentId"));
        selections.add(root.get("amount"));
        selections.add(root.get("applyDepositValue"));
        selections.add(root.get("remark"));
        selections.add(root.get("reverseTransaction"));
        selections.add(root.get("canceledTransaction"));
        selections.add(root.get("createByCredit"));
        selections.add(root.get("bookingId"));
        selections.add(root.get("invoiceId"));
        selections.add(root.get("transactionDate"));
        selections.add(root.get("firstName"));
        selections.add(root.get("lastName"));
        selections.add(root.get("reservation"));
        selections.add(root.get("couponNo"));
        selections.add(root.get("adults"));
        selections.add(root.get("children"));
        selections.add(root.get("createdAt"));
        selections.add(root.get("updatedAt"));
        selections.add(root.get("applyPayment"));
        selections.add(root.get("appliedAt"));
        selections.add(root.get("effectiveDate"));

        return selections;
    }

    private List<Selection<?>> getChildrensPaymentDetailSelections(Join<PaymentDetail, PaymentDetail> children,
                                                          Join<PaymentDetail, Payment> paymentJoin,
                                                          Join<PaymentDetail, ManagePaymentTransactionType> transactionTypeJoin,
                                                          Join<PaymentDetail, Booking> bookingJoin,
                                                          Join<Payment, ManagePaymentSource> paymentManagePaymentSourceJoin,
                                                          Join<Payment, ManagePaymentStatus> paymentManagePaymentStatusJoin,
                                                          Join<Payment, ManageClient> paymentManageClientJoin,
                                                          Join<Payment, ManageAgency> paymentManageAgencyJoin,
                                                          Join<ManageAgency, ManageAgencyType> paymentManageAgencyManageAgencyType,
                                                          Join<ManageAgency, ManageClient> paymentManageAgencyManageClient,
                                                          Join<ManageAgency, ManageCountry> paymentManageAgencyManageCountry,
                                                          Join<Payment, ManageHotel> paymentManageHotelJoin,
                                                          Join<Payment, Invoice> paymentInvoiceJoin,
                                                          Join<Invoice, ManageHotel> paymentInvoiceManageHotelJoin,
                                                          Join<Invoice, ManageAgency> paymentInvoiceManageAgencyJoin,
                                                          Join<ManageAgency, ManageAgencyType> paymentInvoiceManageAgencyManageAgencyType,
                                                          Join<ManageAgency, ManageClient> paymentInvoiceManageAgencyManageClient,
                                                          Join<ManageAgency, ManageCountry> paymentInvoiceManageAgencyManageCountry,
                                                          Join<Payment, ManageBankAccount> paymentManageBankAccountJoin,
                                                          Join<Payment, ManagePaymentAttachmentStatus> paymentManagePaymentAttachmentStatusJoin,
                                                          Join<Booking, Invoice> bookingInvoiceJoin){
        List<Selection<?>> selections = new ArrayList<>();
        selections.add(children.get("id"));
        selections.add(children.get("status"));
        selections.add(children.get("paymentDetailId"));
        selections.add(children.get("parentId"));

        //Payment
        selections.add(paymentJoin.get("id"));
        selections.add(paymentJoin.get("paymentId"));
        selections.add(paymentJoin.get("status"));
        selections.add(paymentJoin.get("eAttachment"));

        // Payment - Payment Source
        selections.add(paymentManagePaymentSourceJoin.get("id"));
        selections.add(paymentManagePaymentSourceJoin.get("code"));
        selections.add(paymentManagePaymentSourceJoin.get("name"));
        selections.add(paymentManagePaymentSourceJoin.get("status"));
        selections.add(paymentManagePaymentSourceJoin.get("expense"));
        selections.add(paymentManagePaymentSourceJoin.get("isBank"));

        selections.add(paymentJoin.get("reference"));
        selections.add(paymentJoin.get("transactionDate"));
        selections.add(paymentJoin.get("dateTime"));

        // Payment - Payment Status
        selections.add(paymentManagePaymentStatusJoin.get("id"));
        selections.add(paymentManagePaymentStatusJoin.get("code"));
        selections.add(paymentManagePaymentStatusJoin.get("name"));
        selections.add(paymentManagePaymentStatusJoin.get("status"));
        selections.add(paymentManagePaymentStatusJoin.get("applied"));
        selections.add(paymentManagePaymentStatusJoin.get("confirmed"));
        selections.add(paymentManagePaymentStatusJoin.get("cancelled"));
        selections.add(paymentManagePaymentStatusJoin.get("transit"));

        // Payment - Payment Client
        selections.add(paymentManageClientJoin.get("id"));
        selections.add(paymentManageClientJoin.get("code"));
        selections.add(paymentManageClientJoin.get("name"));
        selections.add(paymentManageClientJoin.get("status"));

        // Payment - Payment Agency
        selections.add(paymentManageAgencyJoin.get("id"));
        selections.add(paymentManageAgencyJoin.get("code"));
        selections.add(paymentManageAgencyJoin.get("name"));
        selections.add(paymentManageAgencyJoin.get("status"));

        // Payment - Payment Agency - Agency Type
        selections.add(paymentManageAgencyManageAgencyType.get("id"));
        selections.add(paymentManageAgencyManageAgencyType.get("code"));
        selections.add(paymentManageAgencyManageAgencyType.get("status"));
        selections.add(paymentManageAgencyManageAgencyType.get("name"));

        // Payment - Payment Agency - Client
        selections.add(paymentManageAgencyManageClient.get("id"));
        selections.add(paymentManageAgencyManageClient.get("code"));
        selections.add(paymentManageAgencyManageClient.get("name"));
        selections.add(paymentManageAgencyManageClient.get("status"));

        // Payment - Payment Agency - Country
        selections.add(paymentManageAgencyManageCountry.get("id"));
        selections.add(paymentManageAgencyManageCountry.get("code"));
        selections.add(paymentManageAgencyManageCountry.get("name"));
        selections.add(paymentManageAgencyManageCountry.get("description"));
        selections.add(paymentManageAgencyManageCountry.get("isDefault"));
        selections.add(paymentManageAgencyManageCountry.get("status"));
        selections.add(paymentManageAgencyManageCountry.get("createdAt"));
        selections.add(paymentManageAgencyManageCountry.get("updateAt"));
        selections.add(paymentManageAgencyManageCountry.get("deleteAt"));
        selections.add(paymentManageAgencyManageCountry.get("iso3"));

        selections.add(paymentManageAgencyJoin.get("createdAt"));
        selections.add(paymentManageAgencyJoin.get("updatedAt"));

        // Payment - Payment Hotel
        selections.add(paymentManageHotelJoin.get("id"));
        selections.add(paymentManageHotelJoin.get("code"));
        selections.add(paymentManageHotelJoin.get("deleted"));
        selections.add(paymentManageHotelJoin.get("name"));
        selections.add(paymentManageHotelJoin.get("status"));
        selections.add(paymentManageHotelJoin.get("applyByTradingCompany"));
        selections.add(paymentManageHotelJoin.get("manageTradingCompany"));
        selections.add(paymentManageHotelJoin.get("autoApplyCredit"));
        selections.add(paymentManageHotelJoin.get("createdAt"));
        selections.add(paymentManageHotelJoin.get("updatedAt"));
        selections.add(paymentManageHotelJoin.get("deletedAt"));

        //Payment - Payment - Invoice
        selections.add(paymentInvoiceJoin.get("id"));
        selections.add(paymentInvoiceJoin.get("invoiceId"));
        selections.add(paymentInvoiceJoin.get("invoiceNo"));
        selections.add(paymentInvoiceJoin.get("invoiceNumber"));
        selections.add(paymentInvoiceJoin.get("invoiceAmount"));
        selections.add(paymentInvoiceJoin.get("invoiceDate"));
        selections.add(paymentInvoiceJoin.get("hasAttachment"));
        selections.add(paymentInvoiceJoin.get("invoiceType"));

        //Payment - Payment - Invoice Hotel
        selections.add(paymentInvoiceManageHotelJoin.get("id"));
        selections.add(paymentInvoiceManageHotelJoin.get("code"));
        selections.add(paymentInvoiceManageHotelJoin.get("deleted"));
        selections.add(paymentInvoiceManageHotelJoin.get("name"));
        selections.add(paymentInvoiceManageHotelJoin.get("status"));
        selections.add(paymentInvoiceManageHotelJoin.get("applyByTradingCompany"));
        selections.add(paymentInvoiceManageHotelJoin.get("manageTradingCompany"));
        selections.add(paymentInvoiceManageHotelJoin.get("autoApplyCredit"));
        selections.add(paymentInvoiceManageHotelJoin.get("createdAt"));
        selections.add(paymentInvoiceManageHotelJoin.get("updatedAt"));
        selections.add(paymentInvoiceManageHotelJoin.get("deletedAt"));

        //Payment - Payment - Invoice Agency
        selections.add(paymentInvoiceManageAgencyJoin.get("id"));
        selections.add(paymentInvoiceManageAgencyJoin.get("code"));
        selections.add(paymentInvoiceManageAgencyJoin.get("name"));
        selections.add(paymentInvoiceManageAgencyJoin.get("status"));

        //Payment - Payment - Invoice Agency Type
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("id"));
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("code"));
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("status"));
        selections.add(paymentInvoiceManageAgencyManageAgencyType.get("name"));

        // Payment - Payment Agency - Client
        selections.add(paymentInvoiceManageAgencyManageClient.get("id"));
        selections.add(paymentInvoiceManageAgencyManageClient.get("code"));
        selections.add(paymentInvoiceManageAgencyManageClient.get("name"));
        selections.add(paymentInvoiceManageAgencyManageClient.get("status"));

        // Payment - Payment Agency - Country
        selections.add(paymentInvoiceManageAgencyManageCountry.get("id"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("code"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("name"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("description"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("isDefault"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("status"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("createdAt"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("updateAt"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("deleteAt"));
        selections.add(paymentInvoiceManageAgencyManageCountry.get("iso3"));

        selections.add(paymentInvoiceManageAgencyJoin.get("createdAt"));
        selections.add(paymentInvoiceManageAgencyJoin.get("updatedAt"));

        //Payment - resto
        selections.add(paymentInvoiceJoin.get("autoRec"));

        // Payment - Payment Bank Account
        selections.add(paymentManageBankAccountJoin.get("id"));
        selections.add(paymentManageBankAccountJoin.get("accountNumber"));
        selections.add(paymentManageBankAccountJoin.get("status"));
        selections.add(paymentManageBankAccountJoin.get("nameOfBank"));

        // Payment - Payment Attachement Status
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("id"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("code"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("name"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("status"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("defaults"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("nonNone"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("patWithAttachment"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("pwaWithOutAttachment"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("supported"));
        selections.add(paymentManagePaymentAttachmentStatusJoin.get("otherSupport"));

        //Payment
        selections.add(paymentJoin.get("paymentAmount"));
        selections.add(paymentJoin.get("paymentBalance"));
        selections.add(paymentJoin.get("depositAmount"));
        selections.add(paymentJoin.get("depositBalance"));
        selections.add(paymentJoin.get("otherDeductions"));
        selections.add(paymentJoin.get("identified"));
        selections.add(paymentJoin.get("notIdentified"));
        selections.add(paymentJoin.get("notApplied"));
        selections.add(paymentJoin.get("applied"));
        selections.add(paymentJoin.get("remark"));
        selections.add(paymentJoin.get("applyPayment"));
        selections.add(paymentJoin.get("hasAttachment"));
        selections.add(paymentJoin.get("hasDetailTypeDeposit"));
        selections.add(paymentJoin.get("paymentSupport"));
        selections.add(paymentJoin.get("createByCredit"));
        selections.add(paymentJoin.get("createdAt"));
        selections.add(paymentJoin.get("updatedAt"));
        selections.add(paymentJoin.get("importType"));

        //ManagePaymentTransactionType
        selections.add(transactionTypeJoin.get("id"));
        selections.add(transactionTypeJoin.get("code"));
        selections.add(transactionTypeJoin.get("name"));
        selections.add(transactionTypeJoin.get("status"));
        selections.add(transactionTypeJoin.get("cash"));
        selections.add(transactionTypeJoin.get("deposit"));
        selections.add(transactionTypeJoin.get("applyDeposit"));
        selections.add(transactionTypeJoin.get("remarkRequired"));
        selections.add(transactionTypeJoin.get("minNumberOfCharacter"));
        selections.add(transactionTypeJoin.get("defaultRemark"));
        selections.add(transactionTypeJoin.get("defaults"));
        selections.add(transactionTypeJoin.get("paymentInvoice"));
        selections.add(transactionTypeJoin.get("debit"));
        selections.add(transactionTypeJoin.get("expenseToBooking"));
        selections.add(transactionTypeJoin.get("negative"));

        //Booking
        selections.add(bookingJoin.get("id"));
        selections.add(bookingJoin.get("bookingId"));
        selections.add(bookingJoin.get("reservationNumber"));
        selections.add(bookingJoin.get("checkIn"));
        selections.add(bookingJoin.get("checkOut"));
        selections.add(bookingJoin.get("fullName"));
        selections.add(bookingJoin.get("firstName"));
        selections.add(bookingJoin.get("lastName"));
        selections.add(bookingJoin.get("invoiceAmount"));
        selections.add(bookingJoin.get("amountBalance"));
        selections.add(bookingJoin.get("couponNumber"));
        selections.add(bookingJoin.get("adults"));
        selections.add(bookingJoin.get("children"));

        //Booking - Invoice
        selections.add(bookingInvoiceJoin.get("id"));
        selections.add(bookingInvoiceJoin.get("invoiceId"));
        selections.add(bookingInvoiceJoin.get("invoiceNo"));
        selections.add(bookingInvoiceJoin.get("invoiceNumber"));
        selections.add(bookingInvoiceJoin.get("invoiceAmount"));
        selections.add(bookingInvoiceJoin.get("invoiceDate"));
        selections.add(bookingInvoiceJoin.get("hasAttachment"));
        selections.add(bookingInvoiceJoin.get("invoiceType"));
        selections.add(bookingInvoiceJoin.get("autoRec"));

        //Booking - resto
        selections.add(bookingJoin.get("bookingDate"));

        selections.add(children.get("reverseFrom"));
        selections.add(children.get("reverseFromParentId"));
        selections.add(children.get("amount"));
        selections.add(children.get("applyDepositValue"));
        selections.add(children.get("remark"));
        selections.add(children.get("reverseTransaction"));
        selections.add(children.get("canceledTransaction"));
        selections.add(children.get("createByCredit"));
        selections.add(children.get("bookingId"));
        selections.add(children.get("invoiceId"));
        selections.add(children.get("transactionDate"));
        selections.add(children.get("firstName"));
        selections.add(children.get("lastName"));
        selections.add(children.get("reservation"));
        selections.add(children.get("couponNo"));
        selections.add(children.get("adults"));
        selections.add(children.get("children"));
        selections.add(children.get("createdAt"));
        selections.add(children.get("updatedAt"));
        selections.add(children.get("applyPayment"));
        selections.add(children.get("appliedAt"));
        selections.add(children.get("effectiveDate"));

        return selections;
    }

    private PaymentDetail convertTupleToEntity(Tuple tuple){
        return new PaymentDetail(
                tuple.get(0, UUID.class),
                tuple.get(1, Status.class),
                tuple.get(2, Long.class),
                tuple.get(3, Long.class),
                (tuple.get(4, UUID.class) != null) ? new Payment(
                        tuple.get(4, UUID.class),
                        tuple.get(5, Long.class),
                        tuple.get(6, Status.class),
                        tuple.get(7, EAttachment.class),
                        (tuple.get(8, UUID.class) != null) ? new ManagePaymentSource(
                                tuple.get(8, UUID.class),
                                tuple.get(9, String.class),
                                tuple.get(10, String.class),
                                tuple.get(11, String.class),
                                tuple.get(12, Boolean.class),
                                tuple.get(13, Boolean.class)
                        ) : null,
                        tuple.get(14, String.class),
                        tuple.get(15, LocalDate.class),
                        tuple.get(16, LocalTime.class),
                        (tuple.get(17, UUID.class) != null) ? new ManagePaymentStatus(
                                tuple.get(17, UUID.class),
                                tuple.get(18, String.class),
                                tuple.get(19, String.class),
                                tuple.get(20, String.class),
                                tuple.get(21, Boolean.class),
                                tuple.get(22, Boolean.class),
                                tuple.get(23, Boolean.class),
                                tuple.get(24, Boolean.class)
                        ) : null,
                        (tuple.get(25, UUID.class) != null) ? new ManageClient(
                                tuple.get(25, UUID.class),
                                tuple.get(26, String.class),
                                tuple.get(27, String.class),
                                tuple.get(28, String.class)
                        ) : null,
                        (tuple.get(29, UUID.class) != null) ? new ManageAgency(
                                tuple.get(29, UUID.class),
                                tuple.get(30, String.class),
                                tuple.get(31, String.class),
                                tuple.get(32, String.class),
                                (tuple.get(33, UUID.class) != null) ? new ManageAgencyType(
                                        tuple.get(33, UUID.class),
                                        tuple.get(34, String.class),
                                        tuple.get(35, String.class),
                                        tuple.get(35, String.class)
                                ) : null,
                                (tuple.get(37, UUID.class) != null) ? new ManageClient(
                                        tuple.get(37, UUID.class),
                                        tuple.get(38, String.class),
                                        tuple.get(39, String.class),
                                        tuple.get(40, String.class)
                                ) : null,
                                (tuple.get(41, UUID.class) != null) ? new ManageCountry(
                                        tuple.get(41, UUID.class),
                                        tuple.get(42, String.class),
                                        tuple.get(43, String.class),
                                        tuple.get(44, String.class),
                                        tuple.get(45, Boolean.class),
                                        tuple.get(46, String.class),
                                        null,
                                        tuple.get(47, LocalDateTime.class),
                                        tuple.get(48, LocalDateTime.class),
                                        tuple.get(49, LocalDateTime.class),
                                        tuple.get(50, String.class)
                                ) : null,
                                tuple.get(51, LocalDateTime.class),
                                tuple.get(52, LocalDateTime.class)
                        ) : null,
                        (tuple.get(53, UUID.class) != null) ? new ManageHotel(
                                tuple.get(53, UUID.class),
                                tuple.get(54, String.class),
                                tuple.get(55, Boolean.class),
                                tuple.get(56, String.class),
                                tuple.get(57, String.class),
                                tuple.get(58, Boolean.class),
                                tuple.get(59, UUID.class),
                                tuple.get(60, Boolean.class),
                                tuple.get(61, LocalDateTime.class),
                                tuple.get(62, LocalDateTime.class),
                                tuple.get(63, LocalDateTime.class)
                        ) : null,
                        (tuple.get(64, UUID.class) != null) ? new Invoice(
                                tuple.get(64, UUID.class),
                                tuple.get(65, Long.class),
                                tuple.get(66, Long.class),
                                tuple.get(67, String.class),
                                tuple.get(68, Double.class),
                                tuple.get(69, LocalDateTime.class),
                                tuple.get(70, Boolean.class),
                                null,
                                tuple.get(71, EInvoiceType.class),
                                null,
                                (tuple.get(72, UUID.class) != null) ? new ManageHotel(
                                        tuple.get(72, UUID.class),
                                        tuple.get(73, String.class),
                                        tuple.get(74, Boolean.class),
                                        tuple.get(75, String.class),
                                        tuple.get(76, String.class),
                                        tuple.get(77, Boolean.class),
                                        tuple.get(78, UUID.class),
                                        tuple.get(79, Boolean.class),
                                        tuple.get(80, LocalDateTime.class),
                                        tuple.get(81, LocalDateTime.class),
                                        tuple.get(82, LocalDateTime.class)
                                ) : null,
                                (tuple.get(83, UUID.class) != null) ? new ManageAgency(
                                        tuple.get(83, UUID.class),
                                        tuple.get(84, String.class),
                                        tuple.get(85, String.class),
                                        tuple.get(86, String.class),
                                        (tuple.get(87, UUID.class) != null) ? new ManageAgencyType(
                                                tuple.get(87, UUID.class),
                                                tuple.get(88, String.class),
                                                tuple.get(89, String.class),
                                                tuple.get(90, String.class)
                                        ) : null,
                                        (tuple.get(91, UUID.class) != null) ? new ManageClient(
                                                tuple.get(91, UUID.class),
                                                tuple.get(92, String.class),
                                                tuple.get(93, String.class),
                                                tuple.get(94, String.class)
                                        ) : null,
                                        (tuple.get(95, UUID.class) != null) ? new ManageCountry(
                                                tuple.get(95, UUID.class),
                                                tuple.get(96, String.class),
                                                tuple.get(97, String.class),
                                                tuple.get(98, String.class),
                                                tuple.get(99, Boolean.class),
                                                tuple.get(100, String.class),
                                                null,
                                                tuple.get(101, LocalDateTime.class),
                                                tuple.get(102, LocalDateTime.class),
                                                tuple.get(103, LocalDateTime.class),
                                                tuple.get(104, String.class)
                                        ) : null,
                                        tuple.get(105, LocalDateTime.class),
                                        tuple.get(106, LocalDateTime.class)
                                ) : null,
                                tuple.get(107, Boolean.class)
                        ) : null,
                        (tuple.get(108, UUID.class) != null) ? new ManageBankAccount(
                                tuple.get(108, UUID.class),
                                tuple.get(109, String.class),
                                tuple.get(110, String.class),
                                tuple.get(111, String.class),
                                null
                        ) : null,
                        (tuple.get(112, UUID.class) != null) ? new ManagePaymentAttachmentStatus(
                                tuple.get(112, UUID.class),
                                tuple.get(113, String.class),
                                tuple.get(114, String.class),
                                tuple.get(115, String.class),
                                tuple.get(116, Boolean.class),
                                tuple.get(117, Boolean.class),
                                tuple.get(118, Boolean.class),
                                tuple.get(119, Boolean.class),
                                tuple.get(120, Boolean.class),
                                tuple.get(121, Boolean.class)
                        ) : null,
                        Collections.emptyList(),
                        Collections.emptyList(),
                        tuple.get(122, Double.class),
                        tuple.get(123, Double.class),
                        tuple.get(124, Double.class),
                        tuple.get(125, Double.class),
                        tuple.get(126, Double.class),
                        tuple.get(127, Double.class),
                        tuple.get(128, Double.class),
                        tuple.get(129, Double.class),
                        tuple.get(130, Double.class),
                        tuple.get(131, String.class),
                        tuple.get(132, Boolean.class),
                        tuple.get(133, Boolean.class),
                        tuple.get(134, Boolean.class),
                        tuple.get(135, Boolean.class),
                        tuple.get(136, Boolean.class),
                        tuple.get(137, OffsetDateTime.class),
                        tuple.get(138, OffsetDateTime.class),
                        tuple.get(139, ImportType.class)
                ) : null,
                (tuple.get(140, UUID.class) != null) ? new ManagePaymentTransactionType(
                        tuple.get(140, UUID.class),
                        tuple.get(141, String.class),
                        tuple.get(142, String.class),
                        tuple.get(143, Status.class),
                        tuple.get(144, Boolean.class),
                        tuple.get(145, Boolean.class),
                        tuple.get(146, Boolean.class),
                        tuple.get(147, Boolean.class),
                        tuple.get(148, Integer.class),
                        tuple.get(149, String.class),
                        tuple.get(150, Boolean.class),
                        tuple.get(151, Boolean.class),
                        tuple.get(152, Boolean.class),
                        tuple.get(153, Boolean.class),
                        tuple.get(154, Boolean.class)
                ) : null,
                (tuple.get(155, UUID.class) != null) ? new Booking(
                        tuple.get(155, UUID.class),
                        tuple.get(156, Long.class),
                        tuple.get(157, String.class),
                        tuple.get(158, LocalDateTime.class),
                        tuple.get(159, LocalDateTime.class),
                        tuple.get(160, String.class),
                        tuple.get(161, String.class),
                        tuple.get(162, String.class),
                        tuple.get(163, Double.class),
                        tuple.get(164, Double.class),
                        tuple.get(165, String.class),
                        tuple.get(166, Integer.class),
                        tuple.get(167, Integer.class),
                        (tuple.get(168, UUID.class) != null) ? new Invoice(
                                tuple.get(168, UUID.class),
                                tuple.get(169, Long.class),
                                tuple.get(170, Long.class),
                                tuple.get(171, String.class),
                                tuple.get(172, Double.class),
                                tuple.get(173, LocalDateTime.class),
                                tuple.get(174, Boolean.class),
                                null,
                                tuple.get(175, EInvoiceType.class),
                                null,
                                null,
                                null,
                                tuple.get(176, Boolean.class)
                        ) : null,
                        null,
                        tuple.get(177, LocalDateTime.class)
                ) : null,
                tuple.get(178, Long.class),
                tuple.get(179, Long.class),
                tuple.get(180, Double.class),
                tuple.get(181, Double.class),
                tuple.get(182, String.class),
                tuple.get(183, Boolean.class),
                tuple.get(184, Boolean.class),
                tuple.get(185, Boolean.class),
                tuple.get(186, Double.class),
                tuple.get(187, String.class),
                tuple.get(188, OffsetDateTime.class),
                tuple.get(189, String.class),
                tuple.get(190, String.class),
                tuple.get(191, String.class),
                tuple.get(192, String.class),
                tuple.get(193, Integer.class),
                tuple.get(194, Integer.class),
                null,
                tuple.get(195, OffsetDateTime.class),
                tuple.get(196, OffsetDateTime.class),
                tuple.get(197, Boolean.class),
                tuple.get(198, OffsetDateTime.class),
                tuple.get(199, OffsetDateTime.class)
        );
    }
}
