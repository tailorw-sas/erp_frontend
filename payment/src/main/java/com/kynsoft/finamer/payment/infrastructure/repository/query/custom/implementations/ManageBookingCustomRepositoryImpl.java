package com.kynsoft.finamer.payment.infrastructure.repository.query.custom.implementations;

import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.infrastructure.identity.*;
import com.kynsoft.finamer.payment.infrastructure.repository.query.custom.ManageBookingCustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class ManageBookingCustomRepositoryImpl implements ManageBookingCustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Booking> findAllByBookingId(List<Long> ids) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<Booking> root = query.from(Booking.class);
        Join<Booking, Invoice> bookingInvoiceJoin = root.join("invoice", JoinType.LEFT);

        Join<Invoice, Invoice> bookingInvoiceInvoiceJoin = bookingInvoiceJoin.join("parent", JoinType.LEFT);
        Join<Invoice, ManageHotel> bookingInvoiceInvoiceHotelJoin = bookingInvoiceInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> bookingInvoiceInvoiceAgencyJoin = bookingInvoiceInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> bookingInvoiceInvoiceAgencyAgencyTypeJoin = bookingInvoiceInvoiceAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> bookingInvoiceInvoiceAgencyClientJoin = bookingInvoiceInvoiceAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> bookingInvoiceInvoiceAgencyCountryJoin = bookingInvoiceInvoiceAgencyJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManageLanguage> bookingInvoiceInvoiceAgencyCountryLanguageJoin = bookingInvoiceInvoiceAgencyCountryJoin.join("managerLanguage", JoinType.LEFT);

        Join<Invoice, ManageHotel> bookingInvoiceHotelJoin = bookingInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> bookingInvoiceAgencyJoin = bookingInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> bookingInvoiceAgencyAgencyTypeJoin = bookingInvoiceAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> bookingInvoiceAgencyClientJoin = bookingInvoiceAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> bookingInvoiceAgencyCountryJoin = bookingInvoiceAgencyJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManageLanguage> bookingInvoiceAgencyCountryLanguageJoin = bookingInvoiceAgencyCountryJoin.join("managerLanguage", JoinType.LEFT);

        Join<Booking, Booking> bookingBookingJoin = root.join("parent", JoinType.LEFT);

        List<Selection<?>> selections = this.getBookingSelections(root,
                bookingInvoiceJoin,
                bookingInvoiceInvoiceJoin,
                bookingInvoiceInvoiceHotelJoin,
                bookingInvoiceInvoiceAgencyJoin,
                bookingInvoiceInvoiceAgencyAgencyTypeJoin,
                bookingInvoiceInvoiceAgencyClientJoin,
                bookingInvoiceInvoiceAgencyCountryJoin,
                bookingInvoiceInvoiceAgencyCountryLanguageJoin,
                bookingInvoiceHotelJoin,
                bookingInvoiceAgencyJoin,
                bookingInvoiceAgencyAgencyTypeJoin,
                bookingInvoiceAgencyClientJoin,
                bookingInvoiceAgencyCountryJoin,
                bookingInvoiceAgencyCountryLanguageJoin,
                bookingBookingJoin
                );

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("bookingId").in(ids));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<Booking> results = tuples.stream()
                .map(this::convertTupleToBooking)
                .collect(Collectors.toList());

        return results;
    }

    @Override
    public List<Booking> findAllByCouponIn(List<String> coupons) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();

        Root<Booking> root = query.from(Booking.class);
        Join<Booking, Invoice> bookingInvoiceJoin = root.join("invoice", JoinType.LEFT);

        Join<Invoice, Invoice> bookingInvoiceInvoiceJoin = bookingInvoiceJoin.join("parent", JoinType.LEFT);
        Join<Invoice, ManageHotel> bookingInvoiceInvoiceHotelJoin = bookingInvoiceInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> bookingInvoiceInvoiceAgencyJoin = bookingInvoiceInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> bookingInvoiceInvoiceAgencyAgencyTypeJoin = bookingInvoiceInvoiceAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> bookingInvoiceInvoiceAgencyClientJoin = bookingInvoiceInvoiceAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> bookingInvoiceInvoiceAgencyCountryJoin = bookingInvoiceInvoiceAgencyJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManageLanguage> bookingInvoiceInvoiceAgencyCountryLanguageJoin = bookingInvoiceInvoiceAgencyCountryJoin.join("managerLanguage", JoinType.LEFT);

        Join<Invoice, ManageHotel> bookingInvoiceHotelJoin = bookingInvoiceJoin.join("hotel", JoinType.LEFT);
        Join<Invoice, ManageAgency> bookingInvoiceAgencyJoin = bookingInvoiceJoin.join("agency", JoinType.LEFT);
        Join<ManageAgency, ManageAgencyType> bookingInvoiceAgencyAgencyTypeJoin = bookingInvoiceAgencyJoin.join("agencyType", JoinType.LEFT);
        Join<ManageAgency, ManageClient> bookingInvoiceAgencyClientJoin = bookingInvoiceAgencyJoin.join("client", JoinType.LEFT);
        Join<ManageAgency, ManageCountry> bookingInvoiceAgencyCountryJoin = bookingInvoiceAgencyJoin.join("country", JoinType.LEFT);
        Join<ManageCountry, ManageLanguage> bookingInvoiceAgencyCountryLanguageJoin = bookingInvoiceAgencyCountryJoin.join("managerLanguage", JoinType.LEFT);

        Join<Booking, Booking> bookingBookingJoin = root.join("parent", JoinType.LEFT);

        List<Selection<?>> selections = this.getBookingSelections(root,
                bookingInvoiceJoin,
                bookingInvoiceInvoiceJoin,
                bookingInvoiceInvoiceHotelJoin,
                bookingInvoiceInvoiceAgencyJoin,
                bookingInvoiceInvoiceAgencyAgencyTypeJoin,
                bookingInvoiceInvoiceAgencyClientJoin,
                bookingInvoiceInvoiceAgencyCountryJoin,
                bookingInvoiceInvoiceAgencyCountryLanguageJoin,
                bookingInvoiceHotelJoin,
                bookingInvoiceAgencyJoin,
                bookingInvoiceAgencyAgencyTypeJoin,
                bookingInvoiceAgencyClientJoin,
                bookingInvoiceAgencyCountryJoin,
                bookingInvoiceAgencyCountryLanguageJoin,
                bookingBookingJoin
        );

        query.multiselect(selections.toArray(new Selection[0]));

        query.where(root.get("couponNumber").in(coupons));

        List<Tuple> tuples = entityManager.createQuery(query).getResultList();

        List<Booking> results = tuples.stream()
                .map(this::convertTupleToBooking)
                .collect(Collectors.toList());

        return results;
    }

    private List<Selection<?>> getBookingSelections(Root<Booking> root,
                                                    Join<Booking, Invoice> bookingInvoiceJoin,
                                                    Join<Invoice, Invoice> bookingInvoiceInvoiceJoin,
                                                    Join<Invoice, ManageHotel> bookingInvoiceInvoiceHotelJoin,
                                                    Join<Invoice, ManageAgency> bookingInvoiceInvoiceAgencyJoin,
                                                    Join<ManageAgency, ManageAgencyType> bookingInvoiceInvoiceAgencyAgencyTypeJoin,
                                                    Join<ManageAgency, ManageClient> bookingInvoiceInvoiceAgencyClientJoin,
                                                    Join<ManageAgency, ManageCountry> bookingInvoiceInvoiceAgencyCountryJoin,
                                                    Join<ManageCountry, ManageLanguage> bookingInvoiceInvoiceAgencyCountryLanguageJoin,
                                                    Join<Invoice, ManageHotel> bookingInvoiceHotelJoin,
                                                    Join<Invoice, ManageAgency> bookingInvoiceAgencyJoin,
                                                    Join<ManageAgency, ManageAgencyType> bookingInvoiceAgencyAgencyTypeJoin,
                                                    Join<ManageAgency, ManageClient> bookingInvoiceAgencyClientJoin,
                                                    Join<ManageAgency, ManageCountry> bookingInvoiceAgencyCountryJoin,
                                                    Join<ManageCountry, ManageLanguage> bookingInvoiceAgencyCountryLanguageJoin,
                                                    Join<Booking, Booking> bookingBookingJoin){
        List<Selection<?>> selections = new ArrayList<>();

        selections.add(root.get("id"));
        selections.add(root.get("bookingId"));
        selections.add(root.get("reservationNumber"));
        selections.add(root.get("checkIn"));
        selections.add(root.get("checkOut"));
        selections.add(root.get("fullName"));
        selections.add(root.get("firstName"));
        selections.add(root.get("lastName"));
        selections.add(root.get("invoiceAmount"));
        selections.add(root.get("amountBalance"));
        selections.add(root.get("couponNumber"));
        selections.add(root.get("adults"));
        selections.add(root.get("children"));

        selections.add(bookingInvoiceJoin.get("id"));
        selections.add(bookingInvoiceJoin.get("invoiceId"));
        selections.add(bookingInvoiceJoin.get("invoiceNo"));
        selections.add(bookingInvoiceJoin.get("invoiceNumber"));
        selections.add(bookingInvoiceJoin.get("invoiceAmount"));
        selections.add(bookingInvoiceJoin.get("invoiceDate"));
        selections.add(bookingInvoiceJoin.get("hasAttachment"));

        //Invoice - Invoice
        selections.add(bookingInvoiceInvoiceJoin.get("id"));
        selections.add(bookingInvoiceInvoiceJoin.get("invoiceId"));
        selections.add(bookingInvoiceInvoiceJoin.get("invoiceNo"));
        selections.add(bookingInvoiceInvoiceJoin.get("invoiceNumber"));
        selections.add(bookingInvoiceInvoiceJoin.get("invoiceAmount"));
        selections.add(bookingInvoiceInvoiceJoin.get("invoiceDate"));
        selections.add(bookingInvoiceInvoiceJoin.get("hasAttachment"));
        selections.add(bookingInvoiceInvoiceJoin.get("invoiceType"));

        selections.add(bookingInvoiceInvoiceHotelJoin.get("id"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("code"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("deleted"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("name"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("status"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("applyByTradingCompany"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("manageTradingCompany"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("autoApplyCredit"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("createdAt"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("updatedAt"));
        selections.add(bookingInvoiceInvoiceHotelJoin.get("deletedAt"));

        selections.add(bookingInvoiceInvoiceAgencyJoin.get("id"));
        selections.add(bookingInvoiceInvoiceAgencyJoin.get("code"));
        selections.add(bookingInvoiceInvoiceAgencyJoin.get("name"));
        selections.add(bookingInvoiceInvoiceAgencyJoin.get("status"));

        selections.add(bookingInvoiceInvoiceAgencyAgencyTypeJoin.get("id"));
        selections.add(bookingInvoiceInvoiceAgencyAgencyTypeJoin.get("code"));
        selections.add(bookingInvoiceInvoiceAgencyAgencyTypeJoin.get("status"));
        selections.add(bookingInvoiceInvoiceAgencyAgencyTypeJoin.get("name"));

        selections.add(bookingInvoiceInvoiceAgencyClientJoin.get("id"));
        selections.add(bookingInvoiceInvoiceAgencyClientJoin.get("code"));
        selections.add(bookingInvoiceInvoiceAgencyClientJoin.get("name"));
        selections.add(bookingInvoiceInvoiceAgencyClientJoin.get("status"));

        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("id"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("code"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("name"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("description"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("isDefault"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("status"));

        selections.add(bookingInvoiceInvoiceAgencyCountryLanguageJoin.get("id"));
        selections.add(bookingInvoiceInvoiceAgencyCountryLanguageJoin.get("code"));
        selections.add(bookingInvoiceInvoiceAgencyCountryLanguageJoin.get("name"));
        selections.add(bookingInvoiceInvoiceAgencyCountryLanguageJoin.get("defaults"));
        selections.add(bookingInvoiceInvoiceAgencyCountryLanguageJoin.get("status"));
        selections.add(bookingInvoiceInvoiceAgencyCountryLanguageJoin.get("createdAt"));
        selections.add(bookingInvoiceInvoiceAgencyCountryLanguageJoin.get("updatedAt"));

        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("createdAt"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("updateAt"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("deleteAt"));
        selections.add(bookingInvoiceInvoiceAgencyCountryJoin.get("iso3"));

        selections.add(bookingInvoiceInvoiceAgencyJoin.get("createdAt"));
        selections.add(bookingInvoiceInvoiceAgencyJoin.get("updatedAt"));

        selections.add(bookingInvoiceInvoiceJoin.get("autoRec"));
        //Invoice - Invoice Fin

        //Invoice - resto
        selections.add(bookingInvoiceJoin.get("invoiceType"));

        //Invoice - Hotel
        selections.add(bookingInvoiceHotelJoin.get("id"));
        selections.add(bookingInvoiceHotelJoin.get("code"));
        selections.add(bookingInvoiceHotelJoin.get("deleted"));
        selections.add(bookingInvoiceHotelJoin.get("name"));
        selections.add(bookingInvoiceHotelJoin.get("status"));
        selections.add(bookingInvoiceHotelJoin.get("applyByTradingCompany"));
        selections.add(bookingInvoiceHotelJoin.get("manageTradingCompany"));
        selections.add(bookingInvoiceHotelJoin.get("autoApplyCredit"));
        selections.add(bookingInvoiceHotelJoin.get("createdAt"));
        selections.add(bookingInvoiceHotelJoin.get("updatedAt"));
        selections.add(bookingInvoiceHotelJoin.get("deletedAt"));

        //Invoice - Agency
        selections.add(bookingInvoiceAgencyJoin.get("id"));
        selections.add(bookingInvoiceAgencyJoin.get("code"));
        selections.add(bookingInvoiceAgencyJoin.get("name"));
        selections.add(bookingInvoiceAgencyJoin.get("status"));

        selections.add(bookingInvoiceAgencyAgencyTypeJoin.get("id"));
        selections.add(bookingInvoiceAgencyAgencyTypeJoin.get("code"));
        selections.add(bookingInvoiceAgencyAgencyTypeJoin.get("status"));
        selections.add(bookingInvoiceAgencyAgencyTypeJoin.get("name"));

        selections.add(bookingInvoiceAgencyClientJoin.get("id"));
        selections.add(bookingInvoiceAgencyClientJoin.get("code"));
        selections.add(bookingInvoiceAgencyClientJoin.get("name"));
        selections.add(bookingInvoiceAgencyClientJoin.get("status"));

        selections.add(bookingInvoiceAgencyCountryJoin.get("id"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("code"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("name"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("description"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("isDefault"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("status"));

        selections.add(bookingInvoiceAgencyCountryLanguageJoin.get("id"));
        selections.add(bookingInvoiceAgencyCountryLanguageJoin.get("code"));
        selections.add(bookingInvoiceAgencyCountryLanguageJoin.get("name"));
        selections.add(bookingInvoiceAgencyCountryLanguageJoin.get("defaults"));
        selections.add(bookingInvoiceAgencyCountryLanguageJoin.get("status"));
        selections.add(bookingInvoiceAgencyCountryLanguageJoin.get("createdAt"));
        selections.add(bookingInvoiceAgencyCountryLanguageJoin.get("updatedAt"));

        selections.add(bookingInvoiceAgencyCountryJoin.get("createdAt"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("updateAt"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("deleteAt"));
        selections.add(bookingInvoiceAgencyCountryJoin.get("iso3"));

        selections.add(bookingInvoiceAgencyJoin.get("createdAt"));
        selections.add(bookingInvoiceAgencyJoin.get("updatedAt"));
        //Invoice  Agency fin

        selections.add(bookingInvoiceJoin.get("autoRec"));

        //Booking - Booking
        selections.add(bookingBookingJoin.get("id"));
        selections.add(bookingBookingJoin.get("bookingId"));
        selections.add(bookingBookingJoin.get("reservationNumber"));
        selections.add(bookingBookingJoin.get("checkIn"));
        selections.add(bookingBookingJoin.get("checkOut"));
        selections.add(bookingBookingJoin.get("fullName"));
        selections.add(bookingBookingJoin.get("firstName"));
        selections.add(bookingBookingJoin.get("lastName"));
        selections.add(bookingBookingJoin.get("invoiceAmount"));
        selections.add(bookingBookingJoin.get("amountBalance"));
        selections.add(bookingBookingJoin.get("couponNumber"));
        selections.add(bookingBookingJoin.get("adults"));
        selections.add(bookingBookingJoin.get("children"));
        selections.add(bookingBookingJoin.get("bookingDate"));

        //Booking - resto
        selections.add(root.get("bookingDate"));

        return selections;
    }

    private Booking convertTupleToBooking(Tuple tuple){
        int i = 0;
        Booking booking = new Booking(
                tuple.get(i++, UUID.class),
                tuple.get(i++, Long.class),
                tuple.get(i++, String.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, LocalDateTime.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Double.class),
                tuple.get(i++, Double.class),
                tuple.get(i++, String.class),
                tuple.get(i++, Integer.class),
                tuple.get(i++, Integer.class),
                (tuple.get(i, UUID.class) != null) ? new Invoice(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, Long.class),
                        tuple.get(i++, Long.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Double.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, Boolean.class),
                        (tuple.get(i, UUID.class) != null) ? new Invoice(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, Long.class),
                                tuple.get(i++, Long.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Double.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, Boolean.class),
                                null,
                                tuple.get(i++, EInvoiceType.class),
                                null,
                                (tuple.get(i, UUID.class) != null) ? new ManageHotel(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class)
                                ) : skip( i+= 11),
                                (tuple.get(i, UUID.class) != null) ? new ManageAgency(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        (tuple.get(i, UUID.class) != null) ? new ManageAgencyType(
                                                tuple.get(i++, UUID.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class)
                                        ) : skip( i += 4),
                                        (tuple.get(i, UUID.class) != null) ? new ManageClient(
                                                tuple.get(i++, UUID.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class)
                                        ) : skip( i=+ 4),
                                        (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                                tuple.get(i++, UUID.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, Boolean.class),
                                                tuple.get(i++, String.class),
                                                (tuple.get(i, UUID.class) != null) ? new ManageLanguage(
                                                        tuple.get(i++, UUID.class),
                                                        tuple.get(i++, String.class),
                                                        tuple.get(i++, String.class),
                                                        tuple.get(i++, Boolean.class),
                                                        tuple.get(i++, String.class),
                                                        tuple.get(i++, LocalDateTime.class),
                                                        tuple.get(i++, LocalDateTime.class)
                                                ) : skip( i += 7),
                                                tuple.get(i++, LocalDateTime.class),
                                                tuple.get(i++, LocalDateTime.class),
                                                tuple.get(i++, LocalDateTime.class),
                                                tuple.get(i++, String.class)
                                        ): skip( i+= 17),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class)
                                ) : skip( i+= 31),
                                tuple.get(i++, Boolean.class)
                        ) : skip( i+= 51 ),
                        tuple.get(i++, EInvoiceType.class),
                        null,
                        (tuple.get(i, UUID.class) != null) ? new ManageHotel(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, Boolean.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip( i += 11),
                        (tuple.get(i, UUID.class) != null) ? new ManageAgency(
                                tuple.get(i++, UUID.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                tuple.get(i++, String.class),
                                (tuple.get(i, UUID.class) != null) ? new ManageAgencyType(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class)
                                ) : skip( i += 4),
                                (tuple.get(i, UUID.class) != null) ? new ManageClient(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class)
                                ) : skip( i += 4),
                                (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                        tuple.get(i++, UUID.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, String.class),
                                        tuple.get(i++, Boolean.class),
                                        tuple.get(i++, String.class),
                                        (tuple.get(i, UUID.class) != null) ? new ManageLanguage(
                                                tuple.get(i++, UUID.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, Boolean.class),
                                                tuple.get(i++, String.class),
                                                tuple.get(i++, LocalDateTime.class),
                                                tuple.get(i++, LocalDateTime.class)
                                        ) : skip( i += 7),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, LocalDateTime.class),
                                        tuple.get(i++, String.class)
                                ) : skip( i += 17),
                                tuple.get(i++, LocalDateTime.class),
                                tuple.get(i++, LocalDateTime.class)
                        ) : skip( i += 31),
                        tuple.get(i++, Boolean.class)
                ) : skip( i+= 103),
                (tuple.get(i, UUID.class) != null) ? new Booking(
                        tuple.get(i++, UUID.class),
                        tuple.get(i++, Long.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, LocalDateTime.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Double.class),
                        tuple.get(i++, Double.class),
                        tuple.get(i++, String.class),
                        tuple.get(i++, Integer.class),
                        tuple.get(i++, Integer.class),
                        null,
                        null,
                        tuple.get(i++, LocalDateTime.class)
                ) : skip(i += 14) ,
                tuple.get(i++, LocalDateTime.class)
        );

        return booking;
    }

    @SuppressWarnings("unchecked")
    private <T> T skip(int i) {
        return null;
    }
}
