
CREATE TABLE public.batch_log (
  id UUID NOT NULL,
  type VARCHAR(255),
  status VARCHAR(255),
  started_at TIMESTAMP(6) WITHOUT TIME ZONE,
  completed_at TIMESTAMP(6) WITHOUT TIME ZONE,
  hotel VARCHAR(255),
  start_date DATE,
  end_date DATE,
  total_records_read INTEGER,
  total_records_processed INTEGER,
  process_id UUID,
  error_message TEXT,
  CONSTRAINT batch_log_pkey PRIMARY KEY (id),
  CONSTRAINT batch_log_status_check CHECK (status::text = ANY (ARRAY['START','PROCESS','END']::text[])),
  CONSTRAINT batch_log_type_check CHECK (type::text = ANY (ARRAY['MANUAL','AUTOMATIC']::text[]))
);

