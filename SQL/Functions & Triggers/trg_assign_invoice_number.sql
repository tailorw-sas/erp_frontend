-- Trigger: trg_assign_invoice_number

-- DROP TRIGGER IF EXISTS trg_assign_invoice_number ON public.invoice;

CREATE OR REPLACE TRIGGER trg_assign_invoice_number
    BEFORE INSERT
    ON public.invoice
    FOR EACH ROW
    WHEN (new.invoiceno IS NULL OR new.invoiceno = 0)
    EXECUTE FUNCTION public.fn_assign_invoice_number();