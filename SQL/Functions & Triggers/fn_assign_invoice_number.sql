CREATE OR REPLACE FUNCTION fn_assign_invoice_number()
    RETURNS TRIGGER AS $$
DECLARE
    new_invoice_no BIGINT;
    invoice_type_code TEXT;
    v_apply_by_trading_company BOOLEAN;
    v_trading_company_id UUID;
BEGIN
    -- Obtener los valores desde el hotel
    SELECT
        mh.applybytradingcompany,
        mh.trading_company_id
    INTO
        v_apply_by_trading_company,
        v_trading_company_id
    FROM manage_hotel mh
    WHERE mh.id = NEW.manage_hotel;

    -- Mapear el tipo de invoice a código corto (para la numeración)
    SELECT CASE
               WHEN NEW.invoicetype = 'OLD_CREDIT' THEN 'CRE'
               WHEN NEW.invoicetype = 'CREDIT' THEN 'CRE'
               WHEN NEW.invoicetype = 'INVOICE' THEN 'INV'
               WHEN NEW.invoicetype = 'INCOME' THEN 'INC'
               ELSE 'UNK' -- Desconocido (por seguridad)
               END
    INTO invoice_type_code;

    -- Ahora evaluamos de acuerdo a lo recuperado
    IF v_apply_by_trading_company = TRUE AND v_trading_company_id IS NOT NULL THEN
        -- Aplica por trading company
        UPDATE hotel_invoice_number_sequence
        SET invoiceno = invoiceno + 1
        WHERE trading_company_id = v_trading_company_id
          AND invoicetype = NEW.invoicetype
        RETURNING invoiceno INTO new_invoice_no;

        NEW.invoiceno := new_invoice_no;
        NEW.invoicenumber := invoice_type_code || '-' || v_trading_company_id || '-' || new_invoice_no;
        NEW.invoicenumberprefix := invoice_type_code || '-' || new_invoice_no;
    ELSE
        -- Aplica por hotel directamente
        UPDATE hotel_invoice_number_sequence
        SET invoiceno = invoiceno + 1
        WHERE manage_hotel = NEW.manage_hotel
          AND invoicetype = NEW.invoicetype
        RETURNING invoiceno INTO new_invoice_no;

        NEW.invoiceno := new_invoice_no;
        NEW.invoicenumber := invoice_type_code || '-' || NEW.manage_hotel || '-' || new_invoice_no;
        NEW.invoicenumberprefix := invoice_type_code || '-' || new_invoice_no;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;