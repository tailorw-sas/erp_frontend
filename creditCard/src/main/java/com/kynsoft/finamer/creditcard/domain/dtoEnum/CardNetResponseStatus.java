package com.kynsoft.finamer.creditcard.domain.dtoEnum;

import jakarta.annotation.Nullable;
import lombok.Getter;

public enum CardNetResponseStatus implements ICardNetResponseStatus {
    SUCCESS_00("00", ETransactionResultStatus.SUCCESS, "Aprobada"),
    CALL_BANK_01("01", ETransactionResultStatus.DECLINED, "Llamar al Banco"),
    CALL_BANK_02("02", ETransactionResultStatus.DECLINED, "Llamar al Banco"),
    INVALID_MERCHANT_03("03", ETransactionResultStatus.DECLINED, "Comercio Invalido"),
    DECLINED_04("04", ETransactionResultStatus.DECLINED, "Rechazada"),
    DECLINED_05("05", ETransactionResultStatus.DECLINED, "Rechazada"),
    MESSAGE_ERROR_06("06", ETransactionResultStatus.DECLINED, "Error en Mensaje"),
    CARD_DECLINED_07("07", ETransactionResultStatus.DECLINED, "Tarjeta Rechazada"),
    CALL_BANK_08("08", ETransactionResultStatus.DECLINED, "Llamar al Banco"),
    REQUEST_IN_PROGRESS_09("09", ETransactionResultStatus.DECLINED, "Request in progress"),
    PARTIAL_APPROVAL_10("10", ETransactionResultStatus.SUCCESS, "Aprobación Parcial"),
    VIP_APPROVED_11("11", ETransactionResultStatus.SUCCESS, "Approved VIP"),
    INVALID_TRANSACTION_12("12", ETransactionResultStatus.DECLINED, "Transaccion Invalida"),
    INVALID_AMOUNT_13("13", ETransactionResultStatus.DECLINED, "Monto Invalido"),
    INVALID_ACCOUNT_14("14", ETransactionResultStatus.DECLINED, "Cuenta Invalida"),
    NO_ISSUER_15("15", ETransactionResultStatus.DECLINED, "No such issuer"),
    UPDATE_TRACK_3_16("16", ETransactionResultStatus.SUCCESS, "Approved update track 3"),
    CUSTOMER_CANCELLATION_17("17", ETransactionResultStatus.CANCELLED, "Customer cancellation"),
    CUSTOMER_DISPUTE_18("18", ETransactionResultStatus.CANCELLED, "Customer dispute"),
    RETRY_TRANSACTION_19("19", ETransactionResultStatus.DECLINED, "Reintentar Transaccion"),
    NO_ACTION_TAKEN_20("20", ETransactionResultStatus.DECLINED, "No tomo accion"),
    NO_ACTION_TAKEN_21("21", ETransactionResultStatus.DECLINED, "No tomo acción"),
    NOT_APPROVED_22("22", ETransactionResultStatus.DECLINED, "Transaccion No Aprobada"),
    NOT_ACCEPTED_23("23", ETransactionResultStatus.DECLINED, "Transaccion No Aceptada"),
    FILE_UPDATE_NOT_SUPPORTED_24("24", ETransactionResultStatus.DECLINED, "File update not supported"),
    RECORD_NOT_FOUND_25("25", ETransactionResultStatus.DECLINED, "Unable to locate record"),
    DUPLICATE_RECORD_26("26", ETransactionResultStatus.DECLINED, "Duplicate record"),
    FILE_UPDATE_ERROR_27("27", ETransactionResultStatus.DECLINED, "File update edit error"),
    FILE_LOCKED_28("28", ETransactionResultStatus.DECLINED, "File update file locked"),
    FILE_UPDATE_FAILED_30("30", ETransactionResultStatus.DECLINED, "File update failed"),
    BIN_NOT_SUPPORTED_31("31", ETransactionResultStatus.DECLINED, "Bin no soportado"),
    PARTIAL_COMPLETION_32("32", ETransactionResultStatus.SUCCESS, "Tx. Completada Parcialmente"),
    CARD_EXPIRED_33("33", ETransactionResultStatus.DECLINED, "Tarjeta Expirada"),
    NOT_APPROVED_34("34", ETransactionResultStatus.DECLINED, "Transacción No Aprobada"),
    NOT_APPROVED_35("35", ETransactionResultStatus.DECLINED, "Transaccion No Aprobada"),
    NOT_APPROVED_36("36", ETransactionResultStatus.DECLINED, "Transaccion No Aprobada"),
    NOT_APPROVED_37("37", ETransactionResultStatus.DECLINED, "Transaccion No Aprobada"),
    NOT_APPROVED_38("38", ETransactionResultStatus.DECLINED, "Transaccion No Aprobada"),
    INVALID_CARD_39("39", ETransactionResultStatus.DECLINED, "Tarjeta Invalida"),
    FUNCTION_NOT_SUPPORTED_40("40", ETransactionResultStatus.DECLINED, "Función no Soportada"),
    NOT_APPROVED_41("41", ETransactionResultStatus.DECLINED, "Transacción No Aprobada"),
    INVALID_ACCOUNT_42("42", ETransactionResultStatus.DECLINED, "Cuenta Invalida"),
    NOT_APPROVED_43("43", ETransactionResultStatus.DECLINED, "Transacción No Aprobada"),
    NO_INVESTMENT_ACCOUNT_44("44", ETransactionResultStatus.DECLINED, "No investment account"),
    INSUFFICIENT_FUNDS_51("51", ETransactionResultStatus.DECLINED, "Fondos insuficientes"),
    INVALID_ACCOUNT_52("52", ETransactionResultStatus.DECLINED, "Cuenta Invalida"),
    INVALID_ACCOUNT_53("53", ETransactionResultStatus.DECLINED, "Cuenta Invalida"),
    CARD_EXPIRED_54("54", ETransactionResultStatus.DECLINED, "Tarjeta vencida"),
    INVALID_ACCOUNT_56("56", ETransactionResultStatus.DECLINED, "Cuenta Invalida"),
    TRANSACTION_NOT_ALLOWED_57("57", ETransactionResultStatus.DECLINED, "Transaccion no permitida"),
    TRANSACTION_NOT_ALLOWED_TERMINAL_58("58", ETransactionResultStatus.DECLINED, "Transaccion no permitida en terminal"),
    CONTACT_ACQUIRER_60("60", ETransactionResultStatus.DECLINED, "Contactar Adquirente"),
    EXCEEDED_WITHDRAWAL_LIMIT_61("61", ETransactionResultStatus.DECLINED, "Excedió Limte de Retiro"),
    RESTRICTED_CARD_62("62", ETransactionResultStatus.DECLINED, "Tarjeta Restringida"),
    EXCEEDED_ATTEMPTS_LIMIT_65("65", ETransactionResultStatus.DECLINED, "Excedió Cantidad de Intento"),
    CONTACT_ACQUIRER_66("66", ETransactionResultStatus.DECLINED, "Contactar Adquirente"),
    HARD_CAPTURE_67("67", ETransactionResultStatus.DECLINED, "Hard capture"),
    LATE_RESPONSE_68("68", ETransactionResultStatus.DECLINED, "Response received too late"),
    PIN_ATTEMPTS_EXCEEDED_75("75", ETransactionResultStatus.DECLINED, "Pin excedio Limte de Intentos"),
    INVALID_BATCH_CAPTURE_77("77", ETransactionResultStatus.DECLINED, "Captura de Lote Invalida"),
    BANK_INTERVENTION_REQUIRED_78("78", ETransactionResultStatus.DECLINED, "Intervención del Banco Requerida"),
    DECLINED_79("79", ETransactionResultStatus.DECLINED, "Rechazada"),
    INVALID_PIN_81("81", ETransactionResultStatus.DECLINED, "Pin invalido"),
    PIN_REQUIRED_82("82", ETransactionResultStatus.DECLINED, "PIN Required"),
    KEYS_NOT_AVAILABLE_85("85", ETransactionResultStatus.DECLINED, "Llaves no disponibles"),
    INVALID_TERMINAL_89("89", ETransactionResultStatus.DECLINED, "Terminal Invalida"),
    CLOSURE_IN_PROGRESS_90("90", ETransactionResultStatus.DECLINED, "Cierre en proceso"),
    HOST_NOT_AVAILABLE_91("91", ETransactionResultStatus.DECLINED, "Host No Disponible"),
    ROUTING_ERROR_92("92", ETransactionResultStatus.DECLINED, "Error de Ruteo"),
    DUPLICATE_TRANSACTION_94("94", ETransactionResultStatus.DECLINED, "Duplicate Transaction"),
    RECONCILIATION_ERROR_95("95", ETransactionResultStatus.DECLINED, "Error de Reconciliación"),
    SYSTEM_ERROR_96("96", ETransactionResultStatus.DECLINED, "Error de Sistema"),
    ISSUER_NOT_AVAILABLE_97("97", ETransactionResultStatus.DECLINED, "Emisor no Disponible"),
    CASH_LIMIT_EXCEEDED_98("98", ETransactionResultStatus.DECLINED, "Excede Limite de Efectivo"),
    CVV_CVC_ERROR_99("99", ETransactionResultStatus.DECLINED, "CVV or CVC Error response"),
    AUTHENTICATION_REJECTED_TF("TF", ETransactionResultStatus.DECLINED, "Solicitud de autenticación rechazada o no completada.");

    private static final CardNetResponseStatus[] VALUES;

    static {
        VALUES = values();
    }

    private final String code;
    private final ETransactionResultStatus transactionResultStatus;
    @Getter
    private final String message;

    CardNetResponseStatus(String code, ETransactionResultStatus transactionResultStatus, String message) {
        this.code = code;
        this.transactionResultStatus = transactionResultStatus;
        this.message = message;
    }

    @Override
    public String value() {
        return this.code;
    }

    @Override
    public ETransactionResultStatus transactionStatus() {
        return this.transactionResultStatus;
    }

    @Override
    public String toString() {
        return this.code + " " + name() + ": " + this.message;
    }

    /**
     * Return the {@code CardNetResponseStatus} enum constant with the specified
     * string value.
     *
     * @param statusCode the string value of the enum to be returned
     * @return the enum constant with the specified string value
     * @throws IllegalArgumentException if this enum has no constant for the
     * specified string value
     */
    public static CardNetResponseStatus valueOfCode(String statusCode) {
        CardNetResponseStatus status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    /**
     * Resolve the given status code to an {@code CardNetResponseStatus}, if possible.
     *
     * @param statusCode the status code (string)
     * @return the corresponding {@code CardNetResponseStatus}, or {@code null} if not found
     */
    @Nullable
    public static CardNetResponseStatus resolve(String statusCode) {
        for (CardNetResponseStatus status : VALUES) {
            if (status.code.equals(statusCode)) {
                return status;
            }
        }
        return null;
    }
}
