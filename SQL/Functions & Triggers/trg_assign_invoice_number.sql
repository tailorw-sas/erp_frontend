CREATE OR REPLACE TRIGGER trg_assign_invoice_number
    BEFORE INSERT ON invoice
    FOR EACH ROW
    WHEN (NEW.invoiceno IS NULL OR NEW.invoiceno = 0) -- Solo si no viene seteado ya
EXECUTE FUNCTION fn_assign_invoice_number();