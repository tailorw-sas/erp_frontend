package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositCommand;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeApplyDeposit.CreatePaymentDetailTypeApplyDepositMessage;
import com.kynsoft.finamer.payment.application.command.paymentDetail.createPaymentDetailsTypeCash.CreatePaymentDetailTypeCashCommand;
import com.kynsoft.finamer.payment.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.payment.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentCloseOperationDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDetailDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentStatusHistoryDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageBookingService;
import com.kynsoft.finamer.payment.domain.services.IManageEmployeeService;
import com.kynsoft.finamer.payment.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentStatusService;
import com.kynsoft.finamer.payment.domain.services.IManagePaymentTransactionTypeService;
import com.kynsoft.finamer.payment.domain.services.IPaymentCloseOperationService;
import com.kynsoft.finamer.payment.domain.services.IPaymentDetailService;
import com.kynsoft.finamer.payment.domain.services.IPaymentService;
import com.kynsoft.finamer.payment.domain.services.IPaymentStatusHistoryService;
import com.kynsoft.finamer.payment.infrastructure.services.http.InvoiceHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.InvoiceImportAutomaticeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateBookingService;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class ApplyPaymentCommandHandler implements ICommandHandler<ApplyPaymentCommand> {

    private final IManageInvoiceService manageInvoiceService;
    private final IPaymentService paymentService;
    private final IPaymentDetailService paymentDetailService;

    private final InvoiceHttpUUIDService invoiceHttpUUIDService;
    private final InvoiceImportAutomaticeHelperServiceImpl invoiceImportAutomaticeHelperServiceImpl;

    private final IManagePaymentTransactionTypeService paymentTransactionTypeService;
    private final IPaymentStatusHistoryService paymentAttachmentStatusHistoryService;

    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBookingService manageBookingService;

    private final ProducerUpdateBookingService producerUpdateBookingService;
    private final IManagePaymentStatusService statusService;
    private final IManageEmployeeService manageEmployeeService;

    public ApplyPaymentCommandHandler(IPaymentService paymentService,
            IManageInvoiceService manageInvoiceService,
            IPaymentDetailService paymentDetailService,
            InvoiceHttpUUIDService invoiceHttpUUIDService,
            InvoiceImportAutomaticeHelperServiceImpl invoiceImportAutomaticeHelperServiceImpl,
            IManagePaymentTransactionTypeService paymentTransactionTypeService,
            IPaymentStatusHistoryService paymentAttachmentStatusHistoryService,
            IPaymentCloseOperationService paymentCloseOperationService,
            IManageBookingService manageBookingService,
            ProducerUpdateBookingService producerUpdateBookingService,
            IManagePaymentStatusService statusService,
            IManageEmployeeService manageEmployeeService) {
        this.invoiceHttpUUIDService = invoiceHttpUUIDService;
        this.invoiceImportAutomaticeHelperServiceImpl = invoiceImportAutomaticeHelperServiceImpl;
        this.paymentService = paymentService;
        this.manageInvoiceService = manageInvoiceService;
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentAttachmentStatusHistoryService = paymentAttachmentStatusHistoryService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.manageBookingService = manageBookingService;
        this.producerUpdateBookingService = producerUpdateBookingService;
        this.statusService = statusService;
        this.manageEmployeeService = manageEmployeeService;
    }

    @Override
    public void handle(ApplyPaymentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPayment(), "id", "Payment ID cannot be null."));
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("Inicia: " + LocalTime.now());
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        PaymentDto paymentDto = this.paymentService.findById(command.getPayment());

        List<ManageInvoiceDto> invoiceQueue = createInvoiceQueue(command);// Ordenar las Invoice

        double paymentBalance = paymentDto.getPaymentBalance();// PaymentBalance
        double notApplied = paymentDto.getNotApplied();// notApplied
        double depositBalance = paymentDto.getDepositBalance();
        for (ManageInvoiceDto manageInvoiceDto : invoiceQueue) {
            List<ManageBookingDto> bookingDtos = getSortedBookings(manageInvoiceDto);// Ordelar los Booking de la Invoice seleccionada.
            for (ManageBookingDto bookingDto : bookingDtos) {
                //TODO: almaceno el valor de Balance del Booking porque puede que no llegue a cero cuando el Payment Balance si lo haga. Y todavia
                //tenga valor el notApplied
                double amountBalance = bookingDto.getAmountBalance();
                if (notApplied > 0 && paymentBalance > 0 && command.isApplyPaymentBalance() && amountBalance > 0) {
                    double amountToApply = Math.min(notApplied, amountBalance);
                    //CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentDto, bookingDto.getId(), amountToApply, true, manageInvoiceDto.getInvoiceDate(), false));
                    PaymentDetailDto message = createDetailsTypeCash(new CreatePaymentDetailTypeCashCommand(paymentDto, bookingDto.getId(), amountToApply, true, manageInvoiceDto.getInvoiceDate(), false));
                    //command.getMediator().send(new ApplyPaymentDetailCommand(message.getId(), bookingDto.getId(), command.getEmployee()));
                    this.applyPayment(command.getEmployee(), bookingDto, message);
                    //command.getMediator().send(new ApplyPaymentDetailCommand(message.getId(), bookingDto.getId(), command.getEmployee()));
                    notApplied = notApplied - amountToApply;
                    paymentBalance = paymentBalance - amountToApply;
                    amountBalance = amountBalance - amountToApply;
                }
                if ((notApplied == 0 && paymentBalance == 0 && command.isApplyDeposit() && amountBalance > 0 && depositBalance > 0)
                        || //Aqui aplica para cuando dentro del flujo se usa payment balance y deposit.
                        (command.isApplyDeposit() && !command.isApplyPaymentBalance() && amountBalance > 0 && depositBalance > 0)) {//TODO: este aplica para cuando se quiere aplicar solo a los deposit
                    if (command.getDeposits() != null && !command.getDeposits().isEmpty()) {
                        List<PaymentDetailDto> deposits = this.createPaymentDetailsTypeDepositQueue(command.getDeposits());
                        for (PaymentDetailDto paymentDetailTypeDeposit : deposits) {
                            double depositAmount = paymentDetailTypeDeposit.getApplyDepositValue();
                            if (depositAmount == 0) {
                                continue;
                            }
                            while (depositAmount > 0) {
                                double amountToApply = Math.min(depositAmount, amountBalance);// Debe de compararse con el amountBalance, porque puede venir de haber sido rebajado en el flujo anterior.
                                CreatePaymentDetailTypeApplyDepositMessage message = command.getMediator().send(new CreatePaymentDetailTypeApplyDepositCommand(paymentDto, amountToApply, paymentDetailTypeDeposit, true, manageInvoiceDto.getInvoiceDate(), false));// quite *-1
                                this.applyPayment(command.getEmployee(), bookingDto, message.getNewDetailDto());
                                //command.getMediator().send(new ApplyPaymentDetailCommand(message.getNewDetailDto().getId(), bookingDto.getId(), command.getEmployee()));
                                depositAmount = depositAmount - amountToApply;
                                amountBalance = amountBalance - amountToApply;
                                depositBalance = depositBalance - amountToApply;
                                if (amountBalance == 0 || depositBalance == 0) {
                                    break;
                                }
                            }
                            if (amountBalance == 0 || depositBalance == 0) {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            if (notApplied == 0 && paymentBalance == 0 && depositBalance == 0) {
                break;
            }
        }
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("Finaliza: " + LocalTime.now());
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    /**
     * Ordena los Deposit por su Apply Deposit Value.
     *
     * @param manageInvoiceDto
     * @return
     */
    private List<PaymentDetailDto> createPaymentDetailsTypeDepositQueue(List<UUID> deposits) {

        List<PaymentDetailDto> queue = this.paymentDetailService.findByIdIn(deposits);
//        List<PaymentDetailDto> queue = new ArrayList<>();
//        for (UUID d : deposits) {
//            queue.add(this.paymentDetailService.findById(d));
//        }

        Collections.sort(queue, Comparator.comparingDouble(m -> m.getApplyDepositValue()));
        return queue;
    }

    /**
     * Ordena los Booking de menor a mayor por su AmountBalance.
     *
     * @param manageInvoiceDto
     * @return
     */
    private List<ManageBookingDto> getSortedBookings(ManageInvoiceDto manageInvoiceDto) {
        List<ManageBookingDto> bookingDtos = new ArrayList<>();
        if (manageInvoiceDto.getBookings() != null && !manageInvoiceDto.getBookings().isEmpty()) {
            bookingDtos.addAll(manageInvoiceDto.getBookings());
            Collections.sort(bookingDtos, Comparator.comparingDouble(m -> m.getAmountBalance()));
        }
        return bookingDtos;
    }

    /**
     * Ordena las Invoice de menor a mayor por su InvoiceAmount.
     *
     * @param command
     * @return
     */
    private List<ManageInvoiceDto> createInvoiceQueue(ApplyPaymentCommand command) {
        List<ManageInvoiceDto> queue = new ArrayList<>();
        try {
            queue.addAll(this.manageInvoiceService.findByIdIn(command.getInvoices()));
        } catch (Exception e) {
            for (UUID invoice : command.getInvoices()) {
                try {
                    queue.add(this.manageInvoiceService.findById(invoice));
                } catch (Exception ex) {
                    /**
                     * *
                     * TODO: Aqui se define un flujo alternativo por HTTP si en
                     * algun momento kafka falla y las invoice no se replicaron,
                     * para evitar que el flujo de aplicacion de pago falle.
                     */
                    InvoiceHttp response = invoiceHttpUUIDService.sendGetBookingHttpRequest(invoice);
                    this.invoiceImportAutomaticeHelperServiceImpl.createInvoice(response);
                    //FLUJO PARA ESPERAR MIENTRAS LAS BD SE SINCRONIZAN.
                    int maxAttempts = 3;
                    while (maxAttempts > 0) {
                        try {
                            queue.add(this.manageInvoiceService.findById(invoice));
                            break;
                        } catch (Exception exp) {
                        }
                        maxAttempts--;
                        try {
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException exp) {
                        }
                    }
                }
            }
        }

        Collections.sort(queue, Comparator.comparingDouble(m -> m.getInvoiceAmount()));
        return queue;
    }

    private void calculate(PaymentDto paymentDto, double amount) {
        paymentDto.setIdentified(paymentDto.getIdentified() + amount);
        paymentDto.setNotIdentified(paymentDto.getNotIdentified() - amount);

        paymentDto.setApplied(paymentDto.getApplied() + amount);
        paymentDto.setNotApplied(paymentDto.getNotApplied() - amount);
        paymentDto.setPaymentBalance(paymentDto.getPaymentBalance() - amount);

        this.paymentService.update(paymentDto);
    }

    private PaymentDetailDto createDetailsTypeCash(CreatePaymentDetailTypeCashCommand command) {
        ManagePaymentTransactionTypeDto transactionTypeDto = this.paymentTransactionTypeService.findByPaymentInvoice();
        PaymentDetailDto newDetailDto = new PaymentDetailDto(
                command.getId(),
                Status.ACTIVE,
                command.getPaymentCash(),
                transactionTypeDto,
                command.getInvoiceAmount(),
                transactionTypeDto.getDefaultRemark(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false
        );
        newDetailDto.setCreateByCredit(command.isCreateByCredit());
        this.paymentDetailService.create(newDetailDto);
        if (command.isApplyPayment()) {
            this.calculate(command.getPaymentCash(), command.getInvoiceAmount());
        }
        return newDetailDto;
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

    private OffsetDateTime transactionDate(UUID hotel) {
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    public void applyPayment(UUID empoyee, ManageBookingDto bookingDto, PaymentDetailDto paymentDetailDto) {
        ManageBookingDto booking = this.manageBookingService.findById(bookingDto.getId());

        booking.setAmountBalance(booking.getAmountBalance() - paymentDetailDto.getAmount());
        paymentDetailDto.setManageBooking(booking);
        paymentDetailDto.setApplayPayment(Boolean.TRUE);
        //paymentDetailDto.setTransactionDate(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetailDto.setTransactionDate(transactionDate(paymentDetailDto.getPayment().getHotel().getId()));
        this.manageBookingService.update(booking);
        this.paymentDetailService.update(paymentDetailDto);

        PaymentDto paymentDto = this.paymentService.findById(paymentDetailDto.getPayment().getId());
        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    paymentDto.getId(),
                    paymentDto.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(booking.getId(), paymentDetailDto.getAmount(), paymentKafka, false));
        } catch (Exception e) {
            System.err.println("Error al enviar el evento de integracion: " + e.getMessage());
        }

        if (paymentDto.getPaymentBalance() == 0 && paymentDto.getDepositBalance() == 0) {
            paymentDto.setPaymentStatus(this.statusService.findByApplied());
            ManageEmployeeDto employeeDto = empoyee != null ? this.manageEmployeeService.findById(empoyee) : null;
            this.createPaymentAttachmentStatusHistory(employeeDto, paymentDto);
        }
        paymentDto.setApplyPayment(true);
        this.paymentService.update(paymentDto);
    }
}
