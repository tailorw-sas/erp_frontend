--RENAME TABLES
ALTER TABLE roomrate RENAME TO room_rate;
ALTER TABLE IF EXISTS import_roomrate RENAME TO import_room_rate;

CREATE TABLE IF NOT EXISTS import_room_rate
(
    id UUID NOT NULL,
    created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
    error_message VARCHAR(255),
    updated_at TIMESTAMP(6) WITHOUT TIME ZONE,
    import_process_id UUID,
    room_rate_id UUID,
    CONSTRAINT pk_import_room_rate PRIMARY KEY (id),
    CONSTRAINT fk_import_room_rate_import_process_id FOREIGN KEY (import_process_id)
        REFERENCES import_process (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fk_import_room_rate_room_rate_id FOREIGN KEY (room_rate_id)
        REFERENCES room_rate (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

ALTER TABLE IF EXISTS manage_employee_agencies_relations RENAME TO manage_employee_agency;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'manage_employee_agency') THEN
        EXECUTE $create$
            CREATE TABLE manage_employee_agency (
                employee_id UUID NOT NULL,
                agency_id UUID NOT NULL,
                PRIMARY KEY (employee_id, agency_id),
                FOREIGN KEY (employee_id) REFERENCES manage_employee(id) ON DELETE CASCADE,
                FOREIGN KEY (agency_id) REFERENCES manage_agency(id) ON DELETE CASCADE
            );
            $create$;
    END IF;
END$$;

ALTER TABLE IF EXISTS manage_employee_hotels_relations RENAME TO manage_employee_hotel;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'manage_employee_hotel') THEN
        EXECUTE $create$
            CREATE TABLE manage_employee_hotel (
                employee_id UUID NOT NULL,
                hotel_id UUID NOT NULL,
                PRIMARY KEY (employee_id, hotel_id),
                FOREIGN KEY (employee_id) REFERENCES manage_employee(id) ON DELETE CASCADE,
                FOREIGN KEY (hotel_id) REFERENCES manage_hotel(id) ON DELETE CASCADE
            );
            $create$;
    END IF;
END$$;

--RENAME COLUMNS

ALTER TABLE room_rate RENAME createdat TO created_at;
ALTER TABLE room_rate RENAME updatedat TO updated_at;
ALTER TABLE room_rate RENAME agency TO agency_code;
ALTER TABLE room_rate RENAME checkindate TO check_in_date;
ALTER TABLE room_rate RENAME checkoutdate TO check_out_date;
ALTER TABLE room_rate RENAME staydays TO stay_days;
ALTER TABLE room_rate RENAME reservationcode TO reservation_code;
ALTER TABLE room_rate RENAME guestname TO guest_name;
ALTER TABLE room_rate RENAME firstname TO first_name;
ALTER TABLE room_rate RENAME lastname TO last_name;
ALTER TABLE room_rate RENAME roomtype TO room_type_code;
ALTER TABLE room_rate RENAME couponnumber TO coupon_number;
ALTER TABLE room_rate RENAME totalnumberofguest TO total_number_of_guest;
ALTER TABLE room_rate RENAME childrens TO children;
ALTER TABLE room_rate RENAME rateplan TO rate_plan_code;
ALTER TABLE room_rate RENAME invoicingdate TO invoice_date;
ALTER TABLE room_rate RENAME hotelcreationdate TO hotel_creation_date;
ALTER TABLE room_rate RENAME originalamount TO original_amount;
ALTER TABLE room_rate RENAME amountpaymentapplied TO amount_payment_applied;
ALTER TABLE room_rate RENAME ratebyadult TO rate_by_adult;
ALTER TABLE room_rate RENAME ratebychild TO rate_by_child;
ALTER TABLE room_rate RENAME roomnumber TO room_number;
ALTER TABLE room_rate RENAME hotelinvoiceamount TO hotel_invoice_amount;
ALTER TABLE room_rate RENAME hotelinvoicenumber TO hotel_invoice_number;
ALTER TABLE room_rate RENAME invoicefolionumber TO invoice_folio_number;
ALTER TABLE room_rate RENAME renewalnumber TO renewal_number;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'import_room_rate' AND column_name = 'roomrate_id') THEN
        EXECUTE 'ALTER TABLE import_room_rate RENAME COLUMN roomrate_id TO room_rate_id';
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'import_room_rate' AND column_name = 'errormessage') THEN
        EXECUTE 'ALTER TABLE import_room_rate RENAME errormessage TO error_message';
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'import_room_rate' AND column_name = 'createdat') THEN
        EXECUTE 'ALTER TABLE import_room_rate RENAME createdat TO created_at';
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'import_room_rate' AND column_name = 'updatedat') THEN
        EXECUTE 'ALTER TABLE import_room_rate RENAME updatedat TO updated_at';
    END IF;
END $$;

ALTER TABLE import_process RENAME importdate TO import_date;
ALTER TABLE import_process RENAME createdat TO created_at;
ALTER TABLE import_process RENAME completedat TO completed_at;
ALTER TABLE import_process RENAME totalbooking TO total_booking;
ALTER TABLE import_process RENAME userid TO user_id;
ALTER TABLE import_process RENAME totalsuccessful TO total_successful;
ALTER TABLE import_process RENAME totalfailed TO total_failed;

ALTER TABLE innsist_connection_params RENAME hostname TO host_name;
ALTER TABLE innsist_connection_params RENAME portnumber TO port_number;
ALTER TABLE innsist_connection_params RENAME databasename TO database_name;
ALTER TABLE innsist_connection_params RENAME username TO user_name;
ALTER TABLE innsist_connection_params RENAME createdat TO created_at;
ALTER TABLE innsist_connection_params RENAME updatedat TO updated_at;

ALTER TABLE innsist_hotel_room_type RENAME roomtypeprefix TO room_type_prefix;
ALTER TABLE innsist_hotel_room_type RENAME createdat TO created_at;
ALTER TABLE innsist_hotel_room_type RENAME updatedat TO updated_at;

ALTER TABLE manage_agency RENAME agencyalias TO agency_alias;
ALTER TABLE manage_agency RENAME createdat TO created_at;
ALTER TABLE manage_agency RENAME updatedat TO updated_at;

ALTER TABLE manage_b2b_partner_type RENAME createdat TO created_at;
ALTER TABLE manage_b2b_partner_type RENAME updatedat TO updated_at;

ALTER TABLE manage_employee RENAME firstname TO first_name;
ALTER TABLE manage_employee RENAME lastname TO last_name;
ALTER TABLE manage_employee RENAME createdat TO created_at;
ALTER TABLE manage_employee RENAME updatedat TO updated_at;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'manage_employee_agency' AND column_name = 'parent_id') THEN
        EXECUTE 'ALTER TABLE manage_employee_agency RENAME COLUMN parent_id TO employee_id';
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'manage_employee_agency' AND column_name = 'child_id') THEN
        EXECUTE 'ALTER TABLE manage_employee_agency RENAME COLUMN child_id TO agency_id';
    END IF;
END$$;


DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'manage_employee_hotel' AND column_name = 'parent_id') THEN
        EXECUTE 'ALTER TABLE manage_employee_hotel RENAME COLUMN parent_id TO employee_id';
    END IF;

    IF EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema = 'public'
                AND table_name = 'manage_employee_hotel' AND column_name = 'child_id') THEN
        EXECUTE 'ALTER TABLE manage_employee_hotel RENAME COLUMN child_id TO hotel_id';
    END IF;
END$$;

ALTER TABLE manage_hotel RENAME createdat TO created_at;
ALTER TABLE manage_hotel RENAME updatedat TO updated_at;

ALTER TABLE manage_rate_plan RENAME createdat TO created_at;
ALTER TABLE manage_rate_plan RENAME updatedat TO updated_at;

ALTER TABLE manage_room_category RENAME createdat TO created_at;
ALTER TABLE manage_room_category RENAME updatedat TO updated_at;

ALTER TABLE manage_room_type RENAME createdat TO created_at;
ALTER TABLE manage_room_type RENAME updatedat TO updated_at;

ALTER TABLE manage_trading_company RENAME innsistcode TO innsist_code;
ALTER TABLE manage_trading_company RENAME createdat TO created_at;
ALTER TABLE manage_trading_company RENAME updatedat TO updated_at;

--CREATE INDEX

CREATE INDEX IF NOT EXISTS idx_employee_agency_employee_id ON manage_employee_agency(employee_id);
CREATE INDEX IF NOT EXISTS idx_employee_agency_agency_id ON manage_employee_agency(agency_id);

CREATE INDEX IF NOT EXISTS idx_employee_hotel_employee_id ON manage_employee_hotel(employee_id);
CREATE INDEX IF NOT EXISTS idx_employee_hotel_agency_id ON manage_employee_hotel(hotel_id);

CREATE INDEX IF NOT EXISTS idx_import_process_roomrate ON import_room_rate(import_process_id, room_rate_id);
CREATE INDEX IF NOT EXISTS idx_roomrate ON import_room_rate(room_rate_id);