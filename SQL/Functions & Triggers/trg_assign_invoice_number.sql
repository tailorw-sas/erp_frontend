CREATE OR REPLACE TRIGGER trg_assign_invoice_number
    BEFORE INSERT ON invoice
    FOR EACH ROW
    WHEN (NEW.invoiceno IS NULL) -- Solo si no viene seteado ya
EXECUTE FUNCTION fn_assign_invoice_number();