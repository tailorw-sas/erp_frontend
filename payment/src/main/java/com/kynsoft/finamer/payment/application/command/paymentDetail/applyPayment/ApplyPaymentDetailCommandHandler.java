package com.kynsoft.finamer.payment.application.command.paymentDetail.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.http.entity.BookingHttp;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EInvoiceType;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import com.kynsoft.finamer.payment.infrastructure.services.http.BookingHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.BookingImportAutomaticeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class ApplyPaymentDetailCommandHandler implements ICommandHandler<ApplyPaymentDetailCommand> {

    private final IPaymentDetailService paymentDetailService;
    private final IManageBookingService manageBookingService;
    private final IPaymentService paymentService;
    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IManagePaymentStatusService statusService;

    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;
    private final IManageEmployeeService manageEmployeeService;

    private final IPaymentCloseOperationService paymentCloseOperationService;

    private final BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl;
    private final BookingHttpUUIDService bookingHttpUUIDService;

    public ApplyPaymentDetailCommandHandler(IPaymentDetailService paymentDetailService,
                                            IManageBookingService manageBookingService,
                                            IPaymentService paymentService,
                                            ProducerUpdateBookingService producerUpdateBookingService,
                                            IManagePaymentStatusService statusService,
                                            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
                                            IManageEmployeeService manageEmployeeService,
                                            IPaymentCloseOperationService paymentCloseOperationService,
                                            BookingImportAutomaticeHelperServiceImpl bookingImportAutomaticeHelperServiceImpl,
                                            BookingHttpUUIDService bookingHttpUUIDService) {
        this.paymentDetailService = paymentDetailService;
        this.manageBookingService = manageBookingService;
        this.paymentService = paymentService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.statusService = statusService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.manageEmployeeService = manageEmployeeService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.bookingImportAutomaticeHelperServiceImpl = bookingImportAutomaticeHelperServiceImpl;
        this.bookingHttpUUIDService = bookingHttpUUIDService;
    }

    @Override
    public void handle(ApplyPaymentDetailCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getBooking(), "id", "Booking ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPaymentDetail(), "id", "Payment Detail ID cannot be null."));

        ManageBookingDto bookingDto = this.getBookingDto(command.getBooking());

        PaymentDetailDto paymentDetailDto = this.paymentDetailService.findById(command.getPaymentDetail());

        bookingDto.setAmountBalance(bookingDto.getAmountBalance() - paymentDetailDto.getAmount());
        paymentDetailDto.setManageBooking(bookingDto);
        paymentDetailDto.setApplyPayment(Boolean.TRUE);
        paymentDetailDto.setAppliedAt(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetailDto.setEffectiveDate(transactionDate(paymentDetailDto.getPayment().getHotel().getId()));

        this.manageBookingService.update(bookingDto);
        this.paymentDetailService.update(paymentDetailDto);

        PaymentDto paymentDto = this.paymentService.findById(paymentDetailDto.getPayment().getId());
        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            if (bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT) || bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.OLD_CREDIT)) {
                this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingDto.getId(), paymentDetailDto.getAmount(), paymentKafka, false));
            } else {
                this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(bookingDto.getId(), paymentDetailDto.getAmount(), paymentKafka, false));
            }
        } catch (Exception e) {
        }

        if (paymentDto.getNotApplied() == 0 && paymentDto.getDepositBalance() == 0 && !bookingDto.getInvoice().getInvoiceType().equals(EInvoiceType.CREDIT)) {
            paymentDto.setPaymentStatus(this.statusService.findByApplied());
            ManageEmployeeDto employeeDto = command.getEmployee() != null ? this.manageEmployeeService.findById(command.getEmployee()) : null;
            this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
        }
        paymentDto.setApplyPayment(true);//TODO APF No se porque se hace esto si se esta aplicando detalles
        this.paymentService.update(paymentDto);

        command.setPaymentResponse(paymentDto);
    }

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    //Este es para agregar el History del Payment. Aqui el estado es el del nomenclador Manage Payment Status
    private void createPaymentAttachmentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto attachmentStatusHistoryDto = new PaymentStatusHistoryDto();
        attachmentStatusHistoryDto.setId(UUID.randomUUID());
        attachmentStatusHistoryDto.setDescription("Update Payment.");
        attachmentStatusHistoryDto.setEmployee(employeeDto);
        attachmentStatusHistoryDto.setPayment(payment);
        attachmentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentAttachmentStatusHistoryService.create(attachmentStatusHistoryDto);
    }

    private ManageBookingDto getBookingDto(UUID bookingId) {
        try {
            return this.manageBookingService.findById(bookingId);
        } catch (Exception e) {
            try {
                /***
                 * TODO: Aqui se define un flujo alternativo por HTTP si en algun momento kafka falla y las invoice no se replicaron, para
                 * evitar que el flujo de aplicacion de pago falle.
                 */
                BookingHttp bookingHttp = this.bookingHttpUUIDService.sendGetBookingHttpRequest(bookingId);
                this.bookingImportAutomaticeHelperServiceImpl.createInvoice(bookingHttp);
                return this.manageBookingService.findById(bookingId);
            } catch (Exception ex) {
                //FLUJO PARA ESPERAR MIENTRAS LAS BD SE SINCRONIZAN.
                int maxAttempts = 3;
                while (maxAttempts > 0) {
                    try {
                        return this.manageBookingService.findById(bookingId);
                    } catch (Exception exc) {
                    }
                    maxAttempts--;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException excp) {
                    }
                }
                throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BOOKING_NOT_FOUND, new ErrorField("booking Id", DomainErrorMessage.BOOKING_NOT_FOUND.getReasonPhrase())));
            }
        }
    }

}
