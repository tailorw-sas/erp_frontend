CREATE MATERIALIZED VIEW IF NOT EXISTS public.mv_invoice_booking_summary
TABLESPACE pg_default
AS
SELECT
    i.id invoice_uuid,
    i.invoicedate::date invoice_date,
    i.invoice_gen_id AS invoice_id,
    i.invoicenumberprefix AS invoice_number,
    h.autogen_code AS hotel_id,
    h.code AS hotel_code,
    h.name AS hotel_name,
    i.duedate AS due_date,
    i.originalamount AS total_amount,
    b.nights,
    b.fullname AS guest,
    rr.checkin,
    rr.checkout,
    b.hotelbookingnumber,
    b.couponnumber,
    rr.adults,
    rr.children,
    rr.rateadult,
    b.invoiceAmount,
    b.booking_gen_id AS booking_id,
    a.code AS agency_code,
    a.name AS agency_name,
    c.name AS country_name
FROM invoice i
    INNER JOIN booking b on i.id=b.manage_invoice
    INNER JOIN room_rate rr on b.id=rr.booking
    INNER JOIN manage_hotel h on h.id=i.manage_hotel
    INNER JOIN manage_agency a on a.id=i.manage_agency
    INNER JOIN manage_country c on c.id = a.manage_country_id
WITH DATA;


CREATE INDEX CONCURRENTLY idx_invoice_booking_summary_invoice_uuid
    ON mv_invoice_booking_summary (invoice_uuid);