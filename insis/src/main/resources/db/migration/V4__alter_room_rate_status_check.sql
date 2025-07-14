ALTER TABLE IF EXISTS public.room_rate DROP CONSTRAINT IF EXISTS roomrate_status_check;

ALTER TABLE IF EXISTS public.room_rate
    ADD CONSTRAINT roomrate_status_check CHECK (status::text = ANY (ARRAY['PENDING'::character varying::text,
    'PROCESSED'::character varying::text,
    'DELETED'::character varying::text,
    'IN_PROCESS'::character varying::text,
    'FAILED'::character varying::text,
    'ANNULLED'::character varying::text]));