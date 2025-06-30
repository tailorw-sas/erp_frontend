CREATE SCHEMA IF NOT EXISTS batch;
ALTER SCHEMA batch OWNER TO admin;

CREATE SCHEMA IF NOT EXISTS public;
ALTER SCHEMA public OWNER TO pg_database_owner;
COMMENT ON SCHEMA public IS 'standard public schema';


CREATE TABLE batch.log (
    id uuid NOT NULL,
    completedat timestamp(6) without time zone,
    enddate date,
    hotel character varying(255),
    startdate date,
    startedat timestamp(6) without time zone,
    status character varying(255),
    totalrecordsprocessed integer NOT NULL,
    totalrecordsread integer NOT NULL,
    type character varying(255),
    processid uuid,
    CONSTRAINT log_status_check CHECK (((status)::text = ANY ((ARRAY['START'::character varying, 'PROCESS'::character varying, 'END'::character varying])::text[]))),
    CONSTRAINT log_type_check CHECK (((type)::text = ANY ((ARRAY['MANUAL'::character varying, 'AUTOMATIC'::character varying])::text[])))
);

CREATE TABLE public.booking (
    id uuid NOT NULL,
    adults integer NOT NULL,
    agencycode character varying(255),
    amount double precision,
    amountpaymentapplied double precision,
    checkindate date,
    checkoutdate date,
    childrens integer NOT NULL,
    couponnumber character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    firstname character varying(255),
    guestname character varying(255),
    hash character varying(255),
    hotelcreationdate date,
    hotelinvoiceamount double precision,
    hotelinvoicenumber character varying(255),
    invoicefolionumber character varying(255),
    invoicingdate date,
    lastname character varying(255),
    originalamount double precision,
    quote double precision,
    ratebyadult double precision,
    ratebychild double precision,
    rateplancode character varying(255),
    remarks character varying(255),
    renewalnumber character varying(255),
    reservationcode character varying(255),
    roomcategorycode character varying(255),
    roomnumber character varying(255),
    roomtypecode character varying(255),
    status character varying(255),
    staydays integer NOT NULL,
    totalnumberofguest integer NOT NULL,
    updatedat timestamp(6) without time zone,
    agency_id uuid,
    hotel_id uuid,
    rateplan_id uuid,
    roomcategory_id uuid,
    roomtype_id uuid,
    CONSTRAINT booking_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'IN_PROCESS'::character varying, 'PROCESSED'::character varying, 'FAILED'::character varying, 'DELETED'::character varying])::text[])))
);

CREATE TABLE public.import_booking (
    id uuid NOT NULL,
    createdat timestamp(6) without time zone NOT NULL,
    errormessage character varying(255),
    updatedat timestamp(6) without time zone,
    booking_id uuid,
    import_process_id uuid
);

CREATE TABLE public.import_process (
    id uuid NOT NULL,
    completedat timestamp(6) without time zone,
    createdat timestamp(6) without time zone NOT NULL,
    importdate date,
    status character varying(255),
    totalbooking integer NOT NULL,
    totalfailed integer,
    totalsuccessful integer,
    userid uuid,
    CONSTRAINT import_process_status_check CHECK (((status)::text = ANY ((ARRAY['CREATED'::character varying, 'IN_PROCESS'::character varying, 'COMPLETED'::character varying])::text[])))
);

CREATE TABLE public.import_roomrate (
    id uuid NOT NULL,
    createdat timestamp(6) without time zone NOT NULL,
    errormessage character varying(255),
    updatedat timestamp(6) without time zone,
    import_process_id uuid,
    roomrate_id uuid
);

CREATE TABLE public.innsist_connection_params (
    id uuid NOT NULL,
    createdat timestamp(6) without time zone NOT NULL,
    databasename character varying(255),
    deleted boolean NOT NULL,
    description character varying(255),
    hostname character varying(255),
    password character varying(255),
    portnumber integer NOT NULL,
    status character varying(255),
    updatedat timestamp(6) without time zone,
    username character varying(255)
);

CREATE TABLE public.innsist_hotel_room_type (
    id uuid NOT NULL,
    createdat timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    roomtypeprefix character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone,
    hotel_id uuid
);

CREATE TABLE public.manage_agency (
    id uuid NOT NULL,
    agencyalias character varying(255),
    code character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    name character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone
);

CREATE TABLE public.manage_b2b_partner_type (
    id uuid NOT NULL,
    code character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    name character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone
);

CREATE TABLE public.manage_employee (
    id uuid NOT NULL,
    createdat timestamp(6) without time zone NOT NULL,
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    updatedat timestamp(6) without time zone
);

CREATE TABLE public.manage_employee_agencies_relations (
    parent_id uuid NOT NULL,
    child_id uuid NOT NULL
);

CREATE TABLE public.manage_employee_hotels_relations (
    parent_id uuid NOT NULL,
    child_id uuid NOT NULL
);

CREATE TABLE public.manage_hotel (
    id uuid NOT NULL,
    code character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    name character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone,
    trading_company_id uuid
);

CREATE TABLE public.manage_rate_plan (
    id uuid NOT NULL,
    code character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    name character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone,
    hotel_id uuid
);

CREATE TABLE public.manage_room_category (
    id uuid NOT NULL,
    code character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    name character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone
);

CREATE TABLE public.manage_room_type (
    id uuid NOT NULL,
    code character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    deleted boolean NOT NULL,
    name character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone,
    hotel_id uuid
);

CREATE TABLE public.manage_trading_company (
    id uuid NOT NULL,
    code character varying(255),
    company character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    has_connection boolean DEFAULT false NOT NULL,
    innsistcode character varying(255),
    status character varying(255),
    updatedat timestamp(6) without time zone,
    manage_innsist_connection_id uuid
);

CREATE TABLE public.roomrate (
    id uuid NOT NULL,
    adults integer NOT NULL,
    amount double precision,
    amountpaymentapplied double precision,
    checkindate date,
    checkoutdate date,
    childrens integer NOT NULL,
    couponnumber character varying(255),
    createdat timestamp(6) without time zone NOT NULL,
    firstname character varying(255),
    guestname character varying(255),
    hash character varying(255),
    hotelcreationdate date,
    hotelinvoiceamount double precision,
    hotelinvoicenumber character varying(255),
    invoicefolionumber character varying(255),
    invoicingdate date,
    lastname character varying(255),
    originalamount double precision,
    quote double precision,
    ratebyadult double precision,
    ratebychild double precision,
    remarks character varying(255),
    renewalnumber character varying(255),
    reservationcode character varying(255),
    roomnumber character varying(255),
    status character varying(255),
    staydays integer NOT NULL,
    totalnumberofguest integer NOT NULL,
    updatedat timestamp(6) without time zone,
    booking_id uuid,
    hotel_id uuid,
    agencycode character varying(255),
    rateplancode character varying(255),
    roomcategorycode character varying(255),
    roomtypecode character varying(255),
    agency_id uuid,
    rateplan_id uuid,
    roomcategory_id uuid,
    roomtype_id uuid,
    invoice_id uuid,
    agency character varying(255),
    rateplan character varying(255),
    roomtype character varying(255),
    CONSTRAINT roomrate_status_check CHECK (((status)::text = ANY (ARRAY[('PENDING'::character varying)::text, ('IN_PROCESS'::character varying)::text, ('PROCESSED'::character varying)::text, ('FAILED'::character varying)::text, ('DELETED'::character varying)::text])))
);



ALTER TABLE ONLY batch.log ADD CONSTRAINT log_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.booking ADD CONSTRAINT booking_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.import_booking ADD CONSTRAINT import_booking_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.import_process ADD CONSTRAINT import_process_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.import_roomrate ADD CONSTRAINT import_roomrate_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.innsist_connection_params ADD CONSTRAINT innsist_connection_params_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.innsist_hotel_room_type ADD CONSTRAINT innsist_hotel_room_type_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_agency ADD CONSTRAINT manage_agency_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_b2b_partner_type ADD CONSTRAINT manage_b2b_partner_type_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_employee ADD CONSTRAINT manage_employee_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_hotel ADD CONSTRAINT manage_hotel_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_rate_plan ADD CONSTRAINT manage_rate_plan_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_room_category ADD CONSTRAINT manage_room_category_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_room_type ADD CONSTRAINT manage_room_type_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_trading_company ADD CONSTRAINT manage_trading_company_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.roomrate ADD CONSTRAINT roomrate_pkey PRIMARY KEY (id);
ALTER TABLE ONLY public.manage_trading_company ADD CONSTRAINT uk_932ppd36gynpeusrnauyp6nmd UNIQUE (manage_innsist_connection_id);



CREATE INDEX idx_booking ON public.import_booking USING btree (booking_id);
CREATE INDEX idx_import_process ON public.import_roomrate USING btree (import_process_id);
CREATE INDEX idx_import_process_booking ON public.import_booking USING btree (import_process_id, booking_id);
CREATE INDEX idx_import_process_roomrate ON public.import_roomrate USING btree (import_process_id, roomrate_id);
CREATE INDEX idx_roomrate ON public.import_roomrate USING btree (roomrate_id);


ALTER TABLE ONLY public.booking ADD CONSTRAINT fk1rll650hjd1iblhttbca3yepo FOREIGN KEY (roomtype_id) REFERENCES public.manage_room_type(id);
ALTER TABLE ONLY public.roomrate ADD CONSTRAINT fk1xj52vp8fku0nsntjrxplb983 FOREIGN KEY (rateplan_id) REFERENCES public.manage_rate_plan(id);
ALTER TABLE ONLY public.manage_employee_hotels_relations ADD CONSTRAINT fk3v0ewx0yhpsele0c9mbf53i50 FOREIGN KEY (parent_id) REFERENCES public.manage_employee(id);
ALTER TABLE ONLY public.booking ADD CONSTRAINT fk6edwel86tfttkajkvpgoebqh4 FOREIGN KEY (roomcategory_id) REFERENCES public.manage_room_category(id);
ALTER TABLE ONLY public.roomrate ADD CONSTRAINT fk92aapp5k2t5fdi5s6l0tl0l08 FOREIGN KEY (roomcategory_id) REFERENCES public.manage_room_category(id);
ALTER TABLE ONLY public.roomrate ADD CONSTRAINT fk9mf6co5g0jbb6vjr37rs1hsyx FOREIGN KEY (roomtype_id) REFERENCES public.manage_room_type(id);
ALTER TABLE ONLY public.import_roomrate ADD CONSTRAINT fka6gikae4pg9e31h25ew57t7w6 FOREIGN KEY (import_process_id) REFERENCES public.import_process(id);
ALTER TABLE ONLY public.booking ADD CONSTRAINT fkahypg3sv0rc24b88upobgbo1q FOREIGN KEY (rateplan_id) REFERENCES public.manage_rate_plan(id);
ALTER TABLE ONLY public.roomrate ADD CONSTRAINT fkavj518y6wadrhd8p54cntely3 FOREIGN KEY (booking_id) REFERENCES public.booking(id);
ALTER TABLE ONLY public.roomrate ADD CONSTRAINT fkcpu6l3xe518tlx1p3uodjc0im FOREIGN KEY (agency_id) REFERENCES public.manage_agency(id);
ALTER TABLE ONLY public.manage_employee_agencies_relations ADD CONSTRAINT fkekdp26jv97shypeggrq53aa9d FOREIGN KEY (child_id) REFERENCES public.manage_agency(id);
ALTER TABLE ONLY public.innsist_hotel_room_type ADD CONSTRAINT fkfj3gacm3gmu134kwn1uqawk2m FOREIGN KEY (hotel_id) REFERENCES public.manage_hotel(id);
ALTER TABLE ONLY public.import_booking ADD CONSTRAINT fkg1d7h14e77dnq4uohw8oxaspp FOREIGN KEY (import_process_id) REFERENCES public.import_process(id);
ALTER TABLE ONLY public.manage_room_type ADD CONSTRAINT fkhsg81hr00cr2hukfngdho4ea1 FOREIGN KEY (hotel_id) REFERENCES public.manage_hotel(id);
ALTER TABLE ONLY public.manage_trading_company ADD CONSTRAINT fki6e0osst5up2vhm7qcux2a7pc FOREIGN KEY (manage_innsist_connection_id) REFERENCES public.innsist_connection_params(id);
ALTER TABLE ONLY public.import_booking ADD CONSTRAINT fkkjcc4lbtk9ruyli6p73xr79l6 FOREIGN KEY (booking_id) REFERENCES public.booking(id);
ALTER TABLE ONLY public.manage_hotel ADD CONSTRAINT fknfw1lvrulileqhmgctft4ysu5 FOREIGN KEY (trading_company_id) REFERENCES public.manage_trading_company(id);
ALTER TABLE ONLY public.import_roomrate ADD CONSTRAINT fknmkshfi11ds5k8gbnao17nd4f FOREIGN KEY (roomrate_id) REFERENCES public.roomrate(id);
ALTER TABLE ONLY public.roomrate ADD CONSTRAINT fko35k6mbnoqofxgmvdoq538cbv FOREIGN KEY (hotel_id) REFERENCES public.manage_hotel(id);
ALTER TABLE ONLY public.booking ADD CONSTRAINT fko3ci6m7rb78iswvrgk71wfqxt FOREIGN KEY (agency_id) REFERENCES public.manage_agency(id);
ALTER TABLE ONLY public.manage_rate_plan ADD CONSTRAINT fko863rerkspik89wkdfq8prfhl FOREIGN KEY (hotel_id) REFERENCES public.manage_hotel(id);
ALTER TABLE ONLY public.manage_employee_hotels_relations ADD CONSTRAINT fkraj9ow5202ij801hqf9vb4rd5 FOREIGN KEY (child_id) REFERENCES public.manage_hotel(id);
ALTER TABLE ONLY public.manage_employee_agencies_relations ADD CONSTRAINT fktqx5r1d0eg0aao2giu1uafd2u FOREIGN KEY (parent_id) REFERENCES public.manage_employee(id);
ALTER TABLE ONLY public.booking ADD CONSTRAINT fkxhkhq50rdbkj36y889n3spw1 FOREIGN KEY (hotel_id) REFERENCES public.manage_hotel(id);