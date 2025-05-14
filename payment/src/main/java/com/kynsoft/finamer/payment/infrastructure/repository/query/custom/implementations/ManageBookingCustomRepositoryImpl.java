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
                tuple.get(i++, UUID.class), // 0
                tuple.get(i++, Long.class), // 1
                tuple.get(i++, String.class), // 2
                tuple.get(i++, LocalDateTime.class), // 3
                tuple.get(i++, LocalDateTime.class), // 4
                tuple.get(i++, String.class), // 5
                tuple.get(i++, String.class), // 6
                tuple.get(i++, String.class), // 7
                tuple.get(i++, Double.class), // 8
                tuple.get(i++, Double.class), // 9
                tuple.get(i++, String.class), // 10
                tuple.get(i++, Integer.class), // 11
                tuple.get(i++, Integer.class), // 12
                (tuple.get(i, UUID.class) != null) ? new Invoice(
                        tuple.get(i++, UUID.class), // 13
                        tuple.get(i++, Long.class), // 14
                        tuple.get(i++, Long.class), // 15
                        tuple.get(i++, String.class), // 16
                        tuple.get(i++, Double.class), // 17
                        tuple.get(i++, LocalDateTime.class), // 18
                        tuple.get(i++, Boolean.class), // 19
                        (tuple.get(i, UUID.class) != null) ? new Invoice(
                                tuple.get(i++, UUID.class), // 20
                                tuple.get(i++, Long.class), // 21
                                tuple.get(i++, Long.class), // 22
                                tuple.get(i++, String.class), // 23
                                tuple.get(i++, Double.class), // 24
                                tuple.get(i++, LocalDateTime.class), // 25
                                tuple.get(i++, Boolean.class), // 26
                                null,
                                tuple.get(i++, EInvoiceType.class), // 27
                                null,
                                (tuple.get(i, UUID.class) != null) ? new ManageHotel(
                                        tuple.get(i++, UUID.class), // 28
                                        tuple.get(i++, String.class), // 29
                                        tuple.get(i++, Boolean.class), // 30
                                        tuple.get(i++, String.class), // 31
                                        tuple.get(i++, String.class), // 32
                                        tuple.get(i++, Boolean.class), // 33
                                        tuple.get(i++, UUID.class), // 34
                                        tuple.get(i++, Boolean.class), // 35
                                        tuple.get(i++, LocalDateTime.class), // 36
                                        tuple.get(i++, LocalDateTime.class), // 37
                                        tuple.get(i++, LocalDateTime.class) // 38
                                ) : skip( i+= 11),
                                (tuple.get(i, UUID.class) != null) ? new ManageAgency(
                                        tuple.get(i++, UUID.class), // 39
                                        tuple.get(i++, String.class), // 40
                                        tuple.get(i++, String.class), // 41
                                        tuple.get(i++, String.class), // 42
                                        (tuple.get(i, UUID.class) != null) ? new ManageAgencyType(
                                                tuple.get(i++, UUID.class), // 43
                                                tuple.get(i++, String.class), // 44
                                                tuple.get(i++, String.class), // 45
                                                tuple.get(i++, String.class) // 46
                                        ) : skip( i += 4),
                                        (tuple.get(i, UUID.class) != null) ? new ManageClient(
                                                tuple.get(i++, UUID.class), // 47
                                                tuple.get(i++, String.class), // 48
                                                tuple.get(i++, String.class), // 49
                                                tuple.get(i++, String.class) // 50
                                        ) : skip( i=+ 4),
                                        (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                                tuple.get(i++, UUID.class), // 51
                                                tuple.get(i++, String.class), // 52
                                                tuple.get(i++, String.class), // 53
                                                tuple.get(i++, String.class), // 54
                                                tuple.get(i++, Boolean.class), // 55
                                                tuple.get(i++, String.class), // 56
                                                (tuple.get(i, UUID.class) != null) ? new ManageLanguage(
                                                        tuple.get(i++, UUID.class), // 57
                                                        tuple.get(i++, String.class), // 58
                                                        tuple.get(i++, String.class), // 59
                                                        tuple.get(i++, Boolean.class), // 60
                                                        tuple.get(i++, String.class), // 61
                                                        tuple.get(i++, LocalDateTime.class), // 62
                                                        tuple.get(i++, LocalDateTime.class) // 63
                                                ) : skip( i += 7),
                                                tuple.get(i++, LocalDateTime.class), // 64
                                                tuple.get(i++, LocalDateTime.class), // 65
                                                tuple.get(i++, LocalDateTime.class), // 66
                                                tuple.get(i++, String.class) // 67
                                        ): skip( i+= 17),
                                        tuple.get(i++, LocalDateTime.class), // 68
                                        tuple.get(i++, LocalDateTime.class) // 69
                                ) : skip( i+= 31),
                                tuple.get(i++, Boolean.class) // 70
                        ) : skip( i+= 51 ),
                        tuple.get(i++, EInvoiceType.class), // 71
                        null,
                        (tuple.get(i, UUID.class) != null) ? new ManageHotel(
                                tuple.get(i++, UUID.class), // 72
                                tuple.get(i++, String.class), // 73
                                tuple.get(i++, Boolean.class), // 74
                                tuple.get(i++, String.class), // 75
                                tuple.get(i++, String.class), // 76
                                tuple.get(i++, Boolean.class), // 77
                                tuple.get(i++, UUID.class), // 78
                                tuple.get(i++, Boolean.class), // 79
                                tuple.get(i++, LocalDateTime.class), // 80
                                tuple.get(i++, LocalDateTime.class), // 81
                                tuple.get(i++, LocalDateTime.class) // 82
                        ) : skip( i += 11),
                        (tuple.get(i, UUID.class) != null) ? new ManageAgency(
                                tuple.get(i++, UUID.class), // 83
                                tuple.get(i++, String.class), // 84
                                tuple.get(i++, String.class), // 85
                                tuple.get(i++, String.class), // 86
                                (tuple.get(i, UUID.class) != null) ? new ManageAgencyType(
                                        tuple.get(i++, UUID.class), // 87
                                        tuple.get(i++, String.class), // 88
                                        tuple.get(i++, String.class), // 89
                                        tuple.get(i++, String.class) // 90
                                ) : skip( i += 4),
                                (tuple.get(i, UUID.class) != null) ? new ManageClient(
                                        tuple.get(i++, UUID.class), // 91
                                        tuple.get(i++, String.class), // 92
                                        tuple.get(i++, String.class), // 93
                                        tuple.get(i++, String.class) // 94
                                ) : skip( i += 4),
                                (tuple.get(i, UUID.class) != null) ? new ManageCountry(
                                        tuple.get(i++, UUID.class), // 95
                                        tuple.get(i++, String.class), // 96
                                        tuple.get(i++, String.class), // 97
                                        tuple.get(i++, String.class), // 98
                                        tuple.get(i++, Boolean.class), // 99
                                        tuple.get(i++, String.class), // 100
                                        (tuple.get(i, UUID.class) != null) ? new ManageLanguage(
                                                tuple.get(i++, UUID.class), // 101
                                                tuple.get(i++, String.class), // 102
                                                tuple.get(i++, String.class), // 103
                                                tuple.get(i++, Boolean.class), // 104
                                                tuple.get(i++, String.class), // 105
                                                tuple.get(i++, LocalDateTime.class), // 106
                                                tuple.get(i++, LocalDateTime.class) // 107
                                        ) : skip( i += 7),
                                        tuple.get(i++, LocalDateTime.class), // 108
                                        tuple.get(i++, LocalDateTime.class), // 109
                                        tuple.get(i++, LocalDateTime.class), // 110
                                        tuple.get(i++, String.class) // 111
                                ) : skip( i += 17),
                                tuple.get(i++, LocalDateTime.class), // 112
                                tuple.get(i++, LocalDateTime.class) // 113
                        ) : skip( i += 31),
                        tuple.get(i++, Boolean.class) // 114
                ) : skip( i+= 102),
                (tuple.get(i, UUID.class) != null) ? new Booking(
                        tuple.get(i++, UUID.class), // 115
                        tuple.get(i++, Long.class), // 116
                        tuple.get(i++, String.class), // 117
                        tuple.get(i++, LocalDateTime.class), // 118
                        tuple.get(i++, LocalDateTime.class), // 119
                        tuple.get(i++, String.class), // 120
                        tuple.get(i++, String.class), // 121
                        tuple.get(i++, String.class), // 122
                        tuple.get(i++, Double.class), // 123
                        tuple.get(i++, Double.class), // 124
                        tuple.get(i++, String.class), // 125
                        tuple.get(i++, Integer.class), // 126
                        tuple.get(i++, Integer.class), // 127
                        null,
                        null,
                        tuple.get(i++, LocalDateTime.class) // 128
                ) : skip(i += 14) ,
                tuple.get(i++, LocalDateTime.class) // 129
        );

        return booking;
    }

    @SuppressWarnings("unchecked")
    private <T> T skip(int i) {
        return null;
    }
}
