package com.kynsof.share.core.domain.exception;

import jakarta.annotation.Nullable;

public enum DomainErrorMessage implements IDomainErrorMessage {
    PATIENTS_NOT_FOUND(601, Series.DOMAIN_ERROR, "Patients not found."),
    QUALIFICATION_NOT_FOUND(602, Series.DOMAIN_ERROR, "Qualification not found."),
    QUALIFICATION_OR_ID_NULL(603, Series.DOMAIN_ERROR, "Qualification DTO or ID cannot be null."),
    BUSINESS_NOT_FOUND(604, Series.DOMAIN_ERROR, "Business not found."),
    BUSINESS_OR_ID_NULL(605, Series.DOMAIN_ERROR, "Business DTO or ID cannot be null."),
    EXISTS_SCHEDULE_SOME_DATE_WHOSE_TIME_RANGE(606, Series.DOMAIN_ERROR,"There exists a schedule on the same date, whose time range coincides at some moment with what you want to create."),
    EXISTS_SCHEDULE_WITH_DATE_STARTTIME_ENDTIME(607, Series.DOMAIN_ERROR,"There exists a schedule with the same date, start time, and end time."),
    SCHEDULE_NOT_FOUND(608, Series.DOMAIN_ERROR, "Schedule not found."),
    SCHEDULE_CANNOT_BE_EQUALS_STARTTIME_ENDTIME(609, Series.DOMAIN_ERROR,"The start time and end time cannot be equal."),
    SCHEDULE_DATE_LESS_THAN_CURRENT_DATE(610, Series.DOMAIN_ERROR, "The provided date is less than the current date."),
    SCHEDULE_INITIAL_TIME_IS_PASSED(611, Series.DOMAIN_ERROR, "The initial time has passed."),
    SCHEDULE_END_TIME_IS_LESS_THAN(612, Series.DOMAIN_ERROR, "The provided end time is less than the start time."),
    SCHEDULE_EXISTS_SOME_TIME_STARTTIME_EDNTIME(613, Series.DOMAIN_ERROR,"There exists a schedule with the same date, start time, and end time."),
    RESOURCE_NOT_FOUND(614, Series.DOMAIN_ERROR, "Resource not found."),
    RECEIPT_NOT_FOUND(615, Series.DOMAIN_ERROR, "Receipt not found."),
    STATUS_NOT_ACCEPTED(616, Series.DOMAIN_ERROR, "Status not accepted, the appointment was attended."),
    SCHEDULE_IS_NOT_AVAIBLE(617, Series.DOMAIN_ERROR, "The selected schedule is not available."),
    COLUMN_UNIQUE(618, Series.DOMAIN_ERROR, "Duplicate key value violates unique constraint."),
    QUALIFICATION_DESCRIPTION_NOT_NULL(619, Series.DOMAIN_ERROR, "Qualification description not null!"),
    QUALIFICATION_DESCRIPTION_UNIQUE(620, Series.DOMAIN_ERROR, "Qualification description unique!"),
    PERMISSION_NOT_FOUND(621, Series.DOMAIN_ERROR, "Permission not found."),
    PERMISSION_OR_ID_NULL(622, Series.DOMAIN_ERROR, "Permission DTO or ID cannot be null."),
    ROLE_PERMISSION_NOT_FOUND(623, Series.DOMAIN_ERROR, "RolPermission not found."),
    RELATIONSHIP_MUST_BE_UNIQUE(624, Series.DOMAIN_ERROR, "Existing relationship."),
    OBJECT_NOT_NULL(625, Series.DOMAIN_ERROR, "Object not null."),
    USER_ROLE_BUSINESS_NOT_FOUND(626, Series.DOMAIN_ERROR, "UserRoleBusiness not found."),
    ROLE_NOT_FOUND(627, Series.DOMAIN_ERROR, "Role not found."),
    ROLE_EXIT(628, Series.DOMAIN_ERROR, "Role not found."),
    BUSINESS_MODULE_NOT_FOUND(629, Series.DOMAIN_ERROR, "BusinessModule not found."),
    MODULE_PERMISSION_NOT_FOUND(630, Series.DOMAIN_ERROR, "ModulepPermission not found."),
    BUSINESS_RUC(631, Series.DOMAIN_ERROR, "The business's RUC must have thirteen characters."),
    BUSINESS_RUC_MUST_BY_UNIQUE(632, Series.DOMAIN_ERROR, "The business ruc must be unique."),
    BUSINESS_NAME_MUST_BY_UNIQUE(633, Series.DOMAIN_ERROR, "The business name must be unique."),
    SCHEDULED_TASK_ALREADY_EXISTS(634, Series.DOMAIN_ERROR, "A scheduled task for this service already exists."),
    MODULE_NAME_CANNOT_BE_EMPTY(635, Series.DOMAIN_ERROR, "The name of the module cannot be empty."),
    MODULE_DESCRIPTION_CANNOT_BE_EMPTY(636, Series.DOMAIN_ERROR, "The description of the module cannot be empty."),
    MODULE_NAME_MUST_BY_UNIQUE(637, Series.DOMAIN_ERROR, "The module name must be unique."),
    MODULE_NOT_FOUND(638, Series.DOMAIN_ERROR, "The module not found."),
    GEOGRAPHIC_LOCATION_NOT_FOUND(639, Series.DOMAIN_ERROR, "GeographicLocation not found."),
    USER_NOT_FOUND(640, Series.DOMAIN_ERROR, "User not found."),
    USER_PERMISSION_BUSINESS_NOT_FOUND(641, Series.DOMAIN_ERROR, "UserPermissionBusiness not found."),
    PERMISSION_CODE_MUST_BY_UNIQUE(642, Series.DOMAIN_ERROR, "The permission code must be unique."),
    PERMISSION_CODE_CANNOT_BE_EMPTY(643, Series.DOMAIN_ERROR, "The code of the permission cannot be empty."),
    DEVICE_NOT_FOUND(644, Series.DOMAIN_ERROR, "Device not found."),
    DEVICE_IP_VALIDATE(645, Series.DOMAIN_ERROR, "La direccion ip no es correcta."),
    DEVICE_SERIAL_CANNOT_BE_EMPTY(646, Series.DOMAIN_ERROR, "The serial of the device cannot be empty."),
    DEVICE_EMAIL_VALIDATE(647, Series.DOMAIN_ERROR, "Direccion de correo incorrecta."),
    CUSTOMER_NOT_FOUND(648, Series.DOMAIN_ERROR, "Customer not found."),
    PATIENT_IDENTIFICATION_MUST_BY_UNIQUE(649, Series.DOMAIN_ERROR, "The patient identification must be unique."),
    SERVICE_TYPE_NAME_MUST_BY_UNIQUE(650, Series.DOMAIN_ERROR, "The service type name must be unique."),
    SERVICE_NAME_MUST_BY_UNIQUE(651, Series.DOMAIN_ERROR, "The service name must be unique."),
    VACCINE_MUST_BY_UNIQUE(652, Series.DOMAIN_ERROR, "The vaccine name must be unique."),
    PROCEDURE_NOT_FOUND(653, Series.DOMAIN_ERROR, "Procedure not found."),
    PROCEDURE_CODE_MUST_BY_UNIQUE(654, Series.DOMAIN_ERROR, "The procedure code must be unique."),
    TREATMENT_NOT_FOUND(654, Series.DOMAIN_ERROR, "Procedure not found."),
    DIAGNOSIS_NOT_FOUND(655, Series.DOMAIN_ERROR, "Diagnosis not found."),
    EXAM_NOT_FOUND(656, Series.DOMAIN_ERROR, "Exam not found."),
    INSURANCE_NOT_FOUND(657, Series.DOMAIN_ERROR, "Insurance not found."),
    SERVICE_TYPE_NOT_FOUND(658, Series.DOMAIN_ERROR, "Service Type not found."),
    SERVICE_NOT_FOUND(659, Series.DOMAIN_ERROR, "Service not found."),
    SCHEDULED_DATE_IS_NOT_PRESENT(660, Series.DOMAIN_ERROR, "The date must be present."),
    CONTACT_INFO_NOT_FOUND(661, Series.DOMAIN_ERROR, "Contact Info not found."),
    MEDICAL_INFO_NOT_FOUND(662, Series.DOMAIN_ERROR, "Medical Info not found."),
    MEDICINES_NOT_FOUND(663, Series.DOMAIN_ERROR, "Medicines not found."),
    EXAM_ORDER_NOT_FOUND(664, Series.DOMAIN_ERROR, "Exam Order not found."),
    VACCINE_NOT_FOUND(665, Series.DOMAIN_ERROR, "Vaccine not found."),
    PATIENT_VACCINE_NOT_FOUND(666, Series.DOMAIN_ERROR, "Relationship not found."),
    EXTERNAL_CONSULTATION_NOT_FOUND(667, Series.DOMAIN_ERROR, "External Consultation not found."),
    DOCTOR_NOT_FOUND(668, Series.DOMAIN_ERROR, "Doctor not found."),
    CIE10_NOT_FOUND(669, Series.DOMAIN_ERROR, "Cie10 not found."),
    TEST_NOT_FOUND(670, Series.DOMAIN_ERROR, "Test not found."),
    MANAGER_BANK_NOT_FOUND(671, Series.DOMAIN_ERROR, "Manager Bank not found."),
    MANAGER_BANK_CODE_SIZE(672, Series.DOMAIN_ERROR, "The manager bank code is not accepted."),
    MANAGER_BANK_CODE_MUST_BY_UNIQUE(673, Series.DOMAIN_ERROR, "The manager bank code must be unique."),
    MANAGER_B2BPARTNER_CODE_SIZE(674, Series.DOMAIN_ERROR, "The manager B2BPartener code is not accepted."),
    MANAGER_B2BPARTNER_CODE_MUST_BY_UNIQUE(675, Series.DOMAIN_ERROR, "The manager B2BPartener code must be unique."),
    MANAGER_B2BPARTNER_NAME_CANNOT_BE_EMPTY(676, Series.DOMAIN_ERROR, "The name of the B2B Partner cannot be empty."),
    MANAGER_BANK_NAME_CANNOT_BE_EMPTY(677, Series.DOMAIN_ERROR, "The name of the Bank cannot be empty."),
    MANAGER_CURRENCY_NOT_FOUND(678, Series.DOMAIN_ERROR, "Manager Currency not found."),
    MANAGER_CURRENCY_CODE_MUST_BY_UNIQUE(679, Series.DOMAIN_ERROR, "The manager currency code must be unique."),
    MANAGER_CURRENCY_CODE_SIZE(680, Series.DOMAIN_ERROR, "The manager currency code is not accepted."),
    MANAGER_CURRENCY_NAME_CANNOT_BE_EMPTY(681, Series.DOMAIN_ERROR, "The name of the currency cannot be empty."),
    MANAGER_B2BPARTNER_NOT_FOUND(682, Series.DOMAIN_ERROR, "Manager B2BPartner not found."),
    MANAGER_MERCHANT_NOT_FOUND(683, Series.DOMAIN_ERROR, "Manager Merchant not found."),
    MANAGE_PAYMENT_SOURCE_NOT_FOUND(684, Series.DOMAIN_ERROR, "The manage payment source not found."),
    MANAGE_PAYMENT_SOURCE_CODE_MUST_BY_UNIQUE(685, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_PAYMENT_SOURCE_CODE_SIZE(686, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_PAYMENT_SOURCE_NAME_CANNOT_BE_EMPTY(687, Series.DOMAIN_ERROR,"The name of the payment source cannot be empty."),
    MANAGER_LANGUAGE_CODE_MUST_BY_UNIQUE(688, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGER_LANGUAGE_CODE_SIZE(689, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_LANGUAGE_NAME_CANNOT_BE_EMPTY(690, Series.DOMAIN_ERROR, "The name of the language cannot be empty."),
    MANAGER_LANGUAGE_NOT_FOUND(691, Series.DOMAIN_ERROR, "Manager language not found."),
    MANAGER_MERCHANT_CURRENCY_NOT_FOUND(687, Series.DOMAIN_ERROR, "Manager Merchant Currency not found."),
    MANAGER_MERCHANT_CURRENCY_MUST_BY_UNIQUE(688, Series.DOMAIN_ERROR,"Data entered overlaps with others, please check."),
    ALERT_CODE_CANNOT_BE_EMPTY(690, Series.DOMAIN_ERROR, "Alert code cannot be empty"),
    ALERT_NAME_CANNOT_BE_EMPTY(691, Series.DOMAIN_ERROR, "Alert name cannot be empty"),
    ALERT_NOT_FOUND(692, Series.DOMAIN_ERROR, "Alert code not found"),
    ALERT_CODE_MUST_BE_UNIQUE(693, Series.DOMAIN_ERROR, "Alert code must be unique"),
    MANAGER_ACCOUNT_TYPE_NAME_CANNOT_BE_EMPTY(694, Series.DOMAIN_ERROR,"The name of the Manager Account Type cannot be empty."),
    MANAGER_ACCOUNT_TYPE_CODE_SIZE(695, Series.DOMAIN_ERROR, "The Manager Account Type code is not accepted."),
    MANAGER_ACCOUNT_TYPE_CODE_MUST_BY_UNIQUE(696, Series.DOMAIN_ERROR, "The Manager Account Type code must be unique."),
    MANAGER_ACCOUNT_TYPE_NOT_FOUND(697, Series.DOMAIN_ERROR, "Manager Account Type not found."),
    MANAGER_COUNTRY_NOT_FOUND(698, Series.DOMAIN_ERROR, "Manager Country not found."),
    MANAGER_COUNTRY_CODE_SIZE(699, Series.DOMAIN_ERROR, "The manager Country code is not accepted."),
    MANAGER_COUNTRY_NAME_CANNOT_BE_EMPTY(700, Series.DOMAIN_ERROR, "The name of the country cannot be empty."),
    MANAGER_COUNTRY_CODE_MUST_BY_UNIQUE(701, Series.DOMAIN_ERROR, "The manager country code must be unique."),
    MANAGER_COUNTRY_DIAL_CODE_SIZE(702, Series.DOMAIN_ERROR, "The manager country dial code is not accepted."),
    MANAGER_TIME_ZONE_NOT_FOUND(703, Series.DOMAIN_ERROR, "Manager Time Zone not found."),
    MANAGER_TIME_ZONE_CODE_SIZE(704, Series.DOMAIN_ERROR, "The manager time zone code is not accepted."),
    MANAGER_TIME_ZONE_NAME_CANNOT_BE_EMPTY(705, Series.DOMAIN_ERROR, "The name of the time zone cannot be empty."),
    MANAGER_TIME_ZONE_CODE_MUST_BY_UNIQUE(706, Series.DOMAIN_ERROR, "The manager bank code must be unique."),
    MANAGER_CREDIT_CARD_TYPE_CODE_SIZE(707, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_CREDIT_CARD_TYPE_NAME_CANNOT_BE_EMPTY(708, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGER_CREDIT_CARD_TYPE_CODE_MUST_BY_UNIQUE(709, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_INVOICE_TYPE_CODE_MUST_BY_UNIQUE(710, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_INVOICE_TYPE_CODE_SIZE(711, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_INVOICE_TYPE_NAME_CANNOT_BE_EMPTY(712, Series.DOMAIN_ERROR, "The name of the invoice type cannot be empty."),
    MANAGE_INVOICE_TYPE_NOT_FOUND(713, Series.DOMAIN_ERROR, "The source not found."),
    MANAGER_TRANSACTION_STATUS_CODE_SIZE(714, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_TRANSACTION_STATUS_NAME_CANNOT_BE_EMPTY(715, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGER_TRANSACTION_STATUS_NAVIGATE_CANNOT_BE_EMPTY(716, Series.DOMAIN_ERROR, "The navigate cannot be empty."),
    MANAGER_TRANSACTION_STATUS_CODE_MUST_BY_UNIQUE(717, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_INVOICE_STATUS_NOT_FOUND(718, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_INVOICE_STATUS_CODE_MUST_BY_UNIQUE(719, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_INVOICE_STATUS_CODE_SIZE(720, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_INVOICE_STATUS_NAME_CANNOT_BE_EMPTY(721, Series.DOMAIN_ERROR, "The source not found."),
    MANAGER_CHARGE_TYPE_CODE_SIZE(722, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_CHARGE_TYPE_NAME_CANNOT_BE_EMPTY(723, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGER_CHARGE_TYPE_CODE_MUST_BY_UNIQUE(724, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_INVOICE_TRANSACTION_TYPE_NOT_FOUND(725, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_INVOICE_TRANSACTION_TYPE_CODE_MUST_BY_UNIQUE(726, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_INVOICE_TRANSACTION_TYPE_CODE_SIZE(727, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_INVOICE_TRANSACTION_TYPE_NAME_CANNOT_BE_EMPTY(728, Series.DOMAIN_ERROR, "The source not found."),
    MANAGER_PAYMENT_STATUS_CODE_SIZE(729, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_PAYMENT_STATUS_MUST_BE_UNIQUE(730, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGER_PAYMENT_STATUS_NAME_CANT_BE_NULL(731, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_CITY_STATE_NOT_FOUND(732, Series.DOMAIN_ERROR, "Manage City State not found."),
    MANAGER_CITY_STATE_CODE_SIZE(733, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_CITY_STATE_NAME_CANNOT_BE_EMPTY(734, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGER_CITY_STATE_CODE_MUST_BY_UNIQUE(735, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_REPORT_PARAM_TYPE_NOT_FOUND(736, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_REPORT_PARAM_TYPE_NAME_MUST_BY_UNIQUE(737, Series.DOMAIN_ERROR, "The name must be unique."),
    MANAGE_REPORT_PARAM_TYPE_NAME_CANNOT_BE_EMPTY(738, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGER_CLIENT_NOT_FOUND(739, Series.DOMAIN_ERROR, "Manage Client not found."),
    MANAGER_CLIENT_CODE_MUST_BY_UNIQUE(740, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGER_CLIENT_CODE_SIZE(741, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_CLIENT_NAME_CANNOT_BE_EMPTY(742, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_ACTION_LOG_NOT_FOUND(743, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_ACTION_LOG_NAME_CANNOT_BE_EMPTY(744, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_ACTION_LOG_CODE_SIZE(745, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_ACTION_LOG_CODE_MUST_BY_UNIQUE(746, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_RATE_PLAN_NOT_FOUND(747, Series.DOMAIN_ERROR, "Manage Rate Plan not found."),
    MANAGE_RATE_PLAN_CODE_SIZE(748, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_RATE_PLAN_NAME_CANNOT_BE_EMPTY(749, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_RATE_PLAN_CODE_MUST_BY_UNIQUE(750, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGER_PERMISSION_MODULE_NOT_FOUND(751, Series.DOMAIN_ERROR, "Manage Permission Group not found."),
    MANAGER_PERMISSION_MODULE_CODE_MUST_BY_UNIQUE(752, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGER_PERMISSION_MODULE_CODE_SIZE(753, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGER_PERMISSION_MODULE_NAME_CANNOT_BE_EMPTY(754, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_AGENCY_TYPE_NOT_FOUND(755, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_AGENCY_TYPE_NAME_CANNOT_BE_EMPTY(756, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_AGENCY_TYPE_CODE_SIZE(757, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_AGENCY_TYPE_CODE_MUST_BY_UNIQUE(758, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_EMPLOYEE_GROUP_NOT_FOUND(759, Series.DOMAIN_ERROR, "The employee group is not found."),
    MANAGE_EMPLOYEE_GROUP_CODE_MUST_BY_UNIQUE(760, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_EMPLOYEE_GROUP_CODE_SIZE(761, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_EMPLOYEE_GROUP_NAME_CANNOT_BE_EMPTY(762, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_DEPARTMENT_GROUP_NOT_FOUND(763, Series.DOMAIN_ERROR, "The department group was not found."),
    MANAGE_DEPARTMENT_GROUP_NAME_CANNOT_BE_EMPTY(764, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_DEPARTMENT_GROUP_CODE_SIZE(765, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_DEPARTMENT_GROUP_CODE_MUST_BY_UNIQUE(766, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGER_MERCHANT_COMMISSION_NOT_FOUND(767, Series.DOMAIN_ERROR, "Manage Merchant Commission not found."),
    MANAGER_MERCHANT_COMMISSION_FROM_DATE_CANNOT_BE_EMPTY(768, Series.DOMAIN_ERROR, "The fromDate cannot be empty."),
    MANAGER_MERCHANT_COMMISSION_COMMISSION_CANNOT_BE_EMPTY(769, Series.DOMAIN_ERROR, "The commission cannot be empty."),
    MANAGE_ATTACHMENT_TYPE_NOT_FOUND(770, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_ATTACHMENT_TYPE_NAME_CANNOT_BE_EMPTY(771, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_ATTACHMENT_TYPE_CODE_SIZE(772, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_ATTACHMENT_TYPE_CODE_MUST_BY_UNIQUE(773, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_TRADING_COMPANIES_TYPE_NOT_FOUND(774, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_TRADING_COMPANIES_NAME_CANNOT_BE_EMPTY(775, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_TRADING_COMPANIES_CODE_SIZE(776, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_TRADING_COMPANIES_CODE_MUST_BY_UNIQUE(777, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_EMPLOYEE_NOT_FOUND(778, Series.DOMAIN_ERROR, "Manage Employee not found."),
    MANAGE_EMPLOYEE_EMAIL_MUST_BY_UNIQUE(779, Series.DOMAIN_ERROR, "The email must be unique."),
    MANAGE_EMPLOYEE_LOGIN_NAME_MUST_BY_UNIQUE(780, Series.DOMAIN_ERROR, "The loginName must be unique."),
    MANAGE_EMPLOYEE_EMAIL_SIZE(781, Series.DOMAIN_ERROR, "The email is not accepted."),
    MANAGER_PAYMENT_ATTACHMENT_STATUS_NOT_FOUND(782, Series.DOMAIN_ERROR, "Payment Attachment Status not found."),
    MANAGER_PAYMENT_STATUS_NOT_FOUND(783, Series.DOMAIN_ERROR, "Payment Status code not found."),
    MANAGE_PAYMENT_ATTACHMENT_STATUS_MUST_BE_UNIQUE(784, Series.DOMAIN_ERROR, "Payment Status code must be unique."),
    MANAGE_PAYMENT_ATTACHMENT_STATUS_CODE_SIZE(785, Series.DOMAIN_ERROR, "Payment Status code is not accepted."),
    MANAGE_PAYMENT_ATTACHMENT_STATUS_NAME_CANT_BE_NULL(786, Series.DOMAIN_ERROR, "Name can't be empty."),
    MANAGER_MERCHANT_BANK_ACCOUNT_NOT_FOUND(787, Series.DOMAIN_ERROR, "Manage Merchant Bank Account not found."),
    MANAGER_COUNTRY_NAME_MUST_BY_UNIQUE(788, Series.DOMAIN_ERROR, "The manager country name must be unique."),
    MANAGE_CONTACT_NOT_FOUND(789, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_CONTACT_NAME_CANNOT_BE_EMPTY(790, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_CONTACT_CODE_SIZE(791, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_CONTACT_CODE_MUST_BY_UNIQUE(792, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_CONTACT_EMAIL_INVALID(793, Series.DOMAIN_ERROR, "The email is not accepted."),
    MANAGE_CONTACT_PHONE_INVALID(794, Series.DOMAIN_ERROR, "The phone is not accepted."),
    MANAGE_HOTEL_NOT_FOUND(795, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_HOTEL_NAME_CANNOT_BE_EMPTY(796, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_HOTEL_CODE_SIZE(797, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_HOTEL_CODE_MUST_BY_UNIQUE(798, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_REGION_NOT_FOUND(799, Series.DOMAIN_ERROR, "The source not found."),
    MANAGE_REGION_NAME_CANNOT_BE_EMPTY(800, Series.DOMAIN_ERROR, "The name cannot be empty."),
    MANAGE_REGION_CODE_SIZE(801, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_REGION_CODE_MUST_BY_UNIQUE(802, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_NOT_FOUND(802, Series.DOMAIN_ERROR, "The payment transaction type is not found."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CODE_MUST_BY_UNIQUE(803, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CODE_SIZE(804, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_NAME_CANNOT_BE_EMPTY(805, Series.DOMAIN_ERROR,"The name of the payment transaction type cannot be empty."),
    NAME_CANNOT_BE_EMPTY(1000, Series.DOMAIN_ERROR, "The name cannot be empty."),
    ITEM_ALREADY_EXITS(1001, Series.DOMAIN_ERROR, "Item already exists."),
    NOT_FOUND(1002, Series.DOMAIN_ERROR, "Not found."),
    FIELD_IS_REQUIRED(1003, Series.DOMAIN_ERROR, "The field is required."),
    CODE_SIZE(1003, Series.DOMAIN_ERROR, "The code is not accepted."),
    ACCOUNT_NUMBER(1004, Series.DOMAIN_ERROR, "The account number is not accepted."),
    MANAGE_AGENCY_NOT_FOUND(1005, Series.DOMAIN_ERROR, "The agency is not found."),
    MANAGE_AGENCY_CODE_MUST_BY_UNIQUE(1006, Series.DOMAIN_ERROR, "The code must be unique."),
    MANAGE_AGENCY_CODE_SIZE(1007, Series.DOMAIN_ERROR, "The code is not accepted."),
    MANAGE_AGENCY_NAME_CANNOT_BE_EMPTY(1008, Series.DOMAIN_ERROR, "The name of the agency cannot be empty."),
    MANAGE_AGENCY_CIF_CANNOT_BE_EMPTY(1009, Series.DOMAIN_ERROR, "The name of the agency cannot be empty."),
    NOT_DELETE(1010, Series.DOMAIN_ERROR, "Element cannot be deleted has a related element."),
    EMAIL_ALREADY_EXISTS(1011, Series.DOMAIN_ERROR, "Email already exists."),
    PHONE_EXTENSION(1012, Series.DOMAIN_ERROR, "Phone extension is not accepted."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEFAULT(1013, Series.DOMAIN_ERROR,"Another item already exists with default."),
    CHECK_AMOUNT_GREATER_THAN_ZERO_STRICTLY(1014, Series.DOMAIN_ERROR,"The amount must be greater than zero and less or equal than Payment Balance."),
    CHECK_MINIMUM_CHARACTER_REQUIREMENT(1015, Series.DOMAIN_ERROR,"The comment does not meet the minimum character requirement."),
    CHECK_DATE_IS_BEFORE_CURRENT_DATE(1016, Series.DOMAIN_ERROR, "The provided date must be before the current date."),
    CHECK_PAYMENT_AMOUNT_GREATER_THAN_ZERO(1017, Series.DOMAIN_ERROR,"The entered payment amount is invalid. Please enter an amount strictly greater than zero."),
    CHECK_AMOUNT_IF_DEPOSIT_BALANCE_GREATER_THAN_ZERO(1018, Series.DOMAIN_ERROR,"Cannot create Apply Deposit payment details because the Deposit check transaction has insufficient balance."),
    AMOUNT_GREATER_THAN_ZERO(1019, Series.DOMAIN_ERROR, "The amount must be greater than 0."),
    RESERVATION_NUMBER(1020, Series.DOMAIN_ERROR, "The reservation number is not accepted."),
    RESOURCE_TYPE_NOT_FOUND(1021, Series.DOMAIN_ERROR, "Resource Type not found."),
    PAYMENT_NOT_FOUND(1022, Series.DOMAIN_ERROR, "Payment not found."),
    PAYMENT_DETAIL_NOT_FOUND(1023, Series.DOMAIN_ERROR, "Payment Detail not found."),
    MASTER_PAYMENT_ATTACHMENT_NOT_FOUND(1024, Series.DOMAIN_ERROR, "Master Payment Attachment not found."),
    MANAGE_BANK_ACCOUNT_NOT_FOUND(1025, Series.DOMAIN_ERROR, "Manage Bank Account not found."),
    ATTACHMENT_TYPE_NOT_FOUND(1026, Series.DOMAIN_ERROR, "Attachment Type not found."),
    ATTACHMENT_STATUS_HISTORY_NOT_FOUND(1027, Series.DOMAIN_ERROR, "Attachment Status History not found."),
    ATTACHMENT_TYPE_CHECK_DEFAULT(1028, Series.DOMAIN_ERROR, "Only a payment support by payment is allowed."),
    PAYMENT_CLOSE_OPERATION_NOT_FOUND(1029, Series.DOMAIN_ERROR, "Payment Close Operation not found."),
    BOOKING_HOTEL_NUMBER_INVALID(1029, Series.DOMAIN_ERROR, "The field Hotel Booking No. is repeated"),
    RESERVATION_NUMBER_MUST_BE_UNIQUE(1030, Series.DOMAIN_ERROR, "There is already a transaction with that reservation number in this hotel."),
    CHECK_DEPOSIT_TO_APPLY(1031, Series.DOMAIN_ERROR, "Only deposits can be applied to transactions of type Check Deposit."),
    CHECK_APPLY_DEPOSIT(1032, Series.DOMAIN_ERROR, "Please make sure that the 'Apply Deposit' option is selected for the type of transaction chosen."),
    CHECK_PAYMENT_DETAILS_AMOUNT_GREATER_THAN_ZERO(1033, Series.DOMAIN_ERROR, "The amount must be greater than zero and less or equal than Payment Balance."),
    CHECK_AMOUNT_LESS_OR_EQUAL_TRANSACTION_AMOUNT(1034, Series.DOMAIN_ERROR, "Deposit Amount must be greather than zero and less or equal than the selected transaction amount."),
    CHECK_PAYMENT_AMOUNT_AND_PAYMENT_BALANCE(1035, Series.DOMAIN_ERROR, "Transaction failed due to exceeding the payment balance limit."),
    MANAGE_CREDIT_CARD_TYPE_NOT_FOUND(1036, Series.DOMAIN_ERROR, "Credit Card Type not found."),
    VCC_MANAGE_HOTEL_NOT_FOUND(1037, Series.DOMAIN_ERROR, "Hotel not found."),
    VCC_MANAGE_LANGUAGE_NOT_FOUND(1038, Series.DOMAIN_ERROR, "Language not found."),
    VCC_MANAGE_MERCHANT_COMMISSION_NOT_FOUND(1039, Series.DOMAIN_ERROR, "Merchant Commission not found."),
    VCC_MANAGE_MERCHANT_NOT_FOUND(1040, Series.DOMAIN_ERROR, "Merchant not found."),
    VCC_MANAGE_TRANSACTION_STATUS_NOT_FOUND(1041, Series.DOMAIN_ERROR, "Transaction Status not found."),
    VCC_MANAGE_TRANSACTION_TYPE_NOT_FOUND(1042, Series.DOMAIN_ERROR, "VCC Transaction Type not found."),
    VCC_PARAMETERIZATION_NOT_FOUND(1043, Series.DOMAIN_ERROR, "Parameterization not found."),
    VCC_TRANSACTION_NOT_FOUND(1044, Series.DOMAIN_ERROR, "Transaction not found."),
    VCC_REFUND_NOT_ACCEPTED(1045, Series.DOMAIN_ERROR, "Refunds cannot exceed the original amount."),
    VCC_REFUND_CANNOT_BE_ADJUSTMENT(1046, Series.DOMAIN_ERROR, "Refund cannot be made to an adjustment transaction."),
    MANAGE_AGENCY_CHECK_DEFAULT(1047, Series.DOMAIN_ERROR, "Another item already exists with default."),
    VCC_INVALID_BOOKING_COUPON_FORMAT(1048, Series.DOMAIN_ERROR, "Invalid Agency booking coupon format."),
    CHECK_SPLIT_DEPOSIT_BALANCE_GREATER_THAN_ZERO(1049, Series.DOMAIN_ERROR,"The amount entered must be greater zero and less than the selected transaction amount"),
    CHECK_SPLIT_DEPOSIT(1050, Series.DOMAIN_ERROR, "Only transactions of type deposit can be divided."),
    CHECK_SPLIT_TRANSACTION_TYPE(1051, Series.DOMAIN_ERROR, "The transaction to be applied can only be of type Check Deposit."),
    CHECK_IF_NEW_PAYMENT_DETAIL_IS_APPLY_DEPOSIT(1052, Series.DOMAIN_ERROR, "Transaction of type Apply Deposit is not applicable to Payment."),
    CHECK_DATES(1053, Series.DOMAIN_ERROR, "The initial date must be less than the final date."),
    VCC_CHECKIN_DATE_IS_BEFORE_CURRENT_DATE(1054, Series.DOMAIN_ERROR, "The check in date must be before or equal to the current date."),
    VCC_CLOSE_OPERATION_OUT_OF_RANGE(1055, Series.DOMAIN_ERROR, "Transaction date out of schedule range"),
    VCC_CLOSE_OPERATION_NOT_FOUND(1056, Series.DOMAIN_ERROR, "Close Operation not found."),
    INCOME_ADJUSTMENT_NOT_FOUND(1057, Series.DOMAIN_ERROR, "Adjustment not found."),
    INCOME_ADJUSTMENT_AMOUNT_NOT_ZERO(1058, Series.DOMAIN_ERROR,"The amount must be different from zero."),
    INCOME_DATE_IS_BEFORE_CURRENT_DATE(1059, Series.DOMAIN_ERROR, "The provided date must be before the current date."),
    DATE_VALIDATE_CLOSE_OPERATION(1060, Series.DOMAIN_ERROR, "The selected date does not comply with the Close Operation."),
    PAYMENT_TRANSACTION_DATE_IS_BEFORE_CURRENT_DATE(1061, Series.DOMAIN_ERROR, "The provided transaction date must be before the current date."),
    PAYMENT_TRANSACTION_DATE_VALIDATE_CLOSE_OPERATION(1062, Series.DOMAIN_ERROR, "The selected date does not comply with the Close Operation."),
    EXCEL_IMPORT_FORMAT_ERROR(1061,Series.DOMAIN_ERROR,"Invalid excel content."),
    EXCEL_SHEET_EMPTY_FORMAT_ERROR(1062,Series.DOMAIN_ERROR,"There is no data to import."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_CASH(1063, Series.DOMAIN_ERROR,"Payment Transaction Type marked with cash is not acceptable: neither deposit nor applies to deposit should be marked."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEPOSIT(1064, Series.DOMAIN_ERROR,"Payment Transaction Type marked with deposit is not acceptable: neither cash nor applies to deposit should be marked."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_APPLY_DEPOSIT(1065, Series.DOMAIN_ERROR, "Payment Transaction Type marked with apply deposit is not acceptable: neither cash nor deposit should be marked."),
    CHECK_PAYMENT_CREATEAT_VALIDATE_HOUR_FOR_DELETE(1066, Series.DOMAIN_ERROR, "The Payment Detail cannot be deleted because 24 hours have already passed since its creation."),
    INVOICE_CLOSE_OPERATION_NOT_FOUND(1067, Series.DOMAIN_ERROR, "Close Operation not found."),
    INVOICE_CLOSE_OPERATION_OUT_OF_RANGE(1068, Series.DOMAIN_ERROR, "Invoice date out of schedule range"),
    CHECK_INVOICE_OF_TYPE_INCOME(1069, Series.DOMAIN_ERROR, "Invoice of type income. It is not possible to create a new booking."),
    VCC_WRONG_CALCULATION_TYPE(1070, Series.DOMAIN_ERROR, "The calculation type is wrong."),
    VCC_TRANSACTION_CANNOT_BE_REFUNDED(1071, Series.DOMAIN_ERROR, "This transaction cannot be refunded."),
    PAYMENT_ATTACHMENT_STATUS_HISTORY_NOT_FOUND(1072, Series.DOMAIN_ERROR, "Payment Attachment Status History not found."),
    CHECK_APPLY_DEPOSIT_TO_APPLIED_PAYMENT(1073, Series.DOMAIN_ERROR, "The selected transaction has an applied payment."),
    INCOME_ATTACHMENT_TYPE_CHECK_DEFAULT(1074, Series.DOMAIN_ERROR, "Only one default attachment type is allowed."),
    INVOICE_ATTACHMENT_TYPE_CHECK_DEFAULT(1075, Series.DOMAIN_ERROR, "Only a income support by income is allowed."),
    AMOUNT_MISMATCH(1076, Series.DOMAIN_ERROR, "The total amount isn't equals to the imported total."),
    ATTACHMENT_TYPE_CHECK_ANTI(1077, Series.DOMAIN_ERROR, "Only a anti to income import property checked is allowed."),
    PASSWORD_MISMATCH(1078, Series.DOMAIN_ERROR, "Password does not match."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_INCOME_DEFAULT(1079, Series.DOMAIN_ERROR,"Another item already exists with income default."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_APPLY_DEPOSIT_(1080, Series.DOMAIN_ERROR,"Another item already exists with default."),
    MANAGE_PAYMENT_TRANSACTION_TYPE_CHECK_DEPOSIT_(1081, Series.DOMAIN_ERROR,"Another item already exists with default."),
    CREDITS_CANNOT_EXCEED_INVOICE_AMOUNT(1082, Series.DOMAIN_ERROR,"The amount of credits cannot exceed the invoice amount.");

    private static final DomainErrorMessage[] VALUES;

    static {
        VALUES = values();
    }

    private final int value;

    private final Series series;

    private final String reasonPhrase;

    DomainErrorMessage(int value, Series series, String reasonPhrase) {
        this.value = value;
        this.series = series;
        this.reasonPhrase = reasonPhrase;
    }

    @Override
    public int value() {
        return this.value;
    }

    /**
     * Return the status series of this status code.
     */
    public Series series() {
        return this.series;
    }

    /**
     * Return the reason phrase of this status code.
     */
    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    /**
     * Return a string representation of this status code.
     */
    @Override
    public String toString() {
        return this.value + " " + name();
    }

    /**
     * Return the {@code ApiErrorStatus} enum constant with the specified
     * numeric value.
     *
     * @param statusCode the numeric value of the enum to be returned
     * @return the enum constant with the specified numeric value
     * @throws IllegalArgumentException if this enum has no constant for the
     * specified numeric value
     */
    public static DomainErrorMessage valueOf(int statusCode) {
        DomainErrorMessage status = resolve(statusCode);
        if (status == null) {
            throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
        }
        return status;
    }

    /**
     * Resolve the given status code to an {@code ApiErrorStatus}, if possible.
     *
     * @param statusCode the ApiError status code (potentially non-standard)
     * @return the corresponding {@code ApiErrorStatus}, or {@code null} if not
     * found
     */
    @Nullable
    public static DomainErrorMessage resolve(int statusCode) {
        // Use cached VALUES instead of values() to prevent array allocation.
        for (DomainErrorMessage status : VALUES) {
            if (status.value == statusCode) {
                return status;
            }
        }
        return null;
    }

    /**
     * Enumeration of ApiError status series.
     * <p>
     * Retrievable via {@link DomainErrorMessage#series()}.
     */
    public enum Series {
        DOMAIN_ERROR
    }

}
