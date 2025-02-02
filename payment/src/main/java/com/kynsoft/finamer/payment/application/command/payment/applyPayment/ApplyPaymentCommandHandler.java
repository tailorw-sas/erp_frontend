package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
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
import com.kynsoft.finamer.payment.infrastructure.identity.Booking;
import com.kynsoft.finamer.payment.infrastructure.identity.ManagePaymentTransactionType;
import com.kynsoft.finamer.payment.infrastructure.identity.Payment;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentDetail;
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
import java.util.stream.Collectors;

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

    private List<Booking> bookings;
    private Payment updatePayment;
    private List<PaymentDetail> detailTypeDeposits;

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
        this.bookings = new ArrayList<>();
        this.updatePayment = new Payment();
        this.detailTypeDeposits = new ArrayList<>();
    }

    @Override
    public void handle(ApplyPaymentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPayment(), "id", "Payment ID cannot be null."));
        System.err.println("Inicia proceso: " + LocalTime.now());
        this.updatePayment = this.paymentService.findPaymentById(command.getPayment());

        List<ManageInvoiceDto> invoiceQueue = createInvoiceQueue(command);// Ordenar las Invoice

        double paymentBalance = this.updatePayment.getPaymentBalance();// PaymentBalance
        double notApplied = this.updatePayment.getNotApplied();// notApplied
        double depositBalance = this.updatePayment.getDepositBalance();
        for (ManageInvoiceDto manageInvoiceDto : invoiceQueue) {
            List<ManageBookingDto> bookingDtos = getSortedBookings(manageInvoiceDto);// Ordelar los Booking de la Invoice seleccionada.
            for (ManageBookingDto bookingDto : bookingDtos) {
                //TODO: almaceno el valor de Balance del Booking porque puede que no llegue a cero cuando el Payment Balance si lo haga. Y todavia
                //tenga valor el notApplied
                double amountBalance = bookingDto.getAmountBalance();
                if (notApplied > 0 && paymentBalance > 0 && command.isApplyPaymentBalance() && amountBalance > 0) {
                    double amountToApply = Math.min(notApplied, amountBalance);
                    //CreatePaymentDetailTypeCashMessage message = command.getMediator().send(new CreatePaymentDetailTypeCashCommand(paymentDto, bookingDto.getId(), amountToApply, true, manageInvoiceDto.getInvoiceDate(), false));
                    PaymentDetailDto message = createDetailsTypeCash(amountToApply);
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
                                //CreatePaymentDetailTypeApplyDepositMessage message = command.getMediator().send(new CreatePaymentDetailTypeApplyDepositCommand(this.updatePayment.toAggregate(), amountToApply, paymentDetailTypeDeposit, true, manageInvoiceDto.getInvoiceDate(), false));// quite *-1
                                this.applyPayment(command.getEmployee(), bookingDto, this.createDetailsTypeApplyDeposit(paymentDetailTypeDeposit, amountToApply));
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
        this.manageBookingService.updateAll(bookings);
        this.paymentService.update(updatePayment);
        this.paymentDetailService.createAll(detailTypeDeposits);
        this.paymentCloseOperationService.clearCache();
        System.err.println("Finaliza proceso: " + LocalTime.now());
    }

    /**
     * Ordena los Deposit por su Apply Deposit Value.
     *
     * @param manageInvoiceDto
     * @return
     */
    private List<PaymentDetailDto> createPaymentDetailsTypeDepositQueue(List<UUID> deposits) {
        this.detailTypeDeposits = this.paymentDetailService.findByPaymentDetailsApplyIdIn(deposits);
        List<PaymentDetailDto> queue = this.paymentDetailService.change(detailTypeDeposits);

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
        List<Long> ids = queue.stream().flatMap(dto -> dto.getBookings().stream()).map(ManageBookingDto::getBookingId).collect(Collectors.toList());
        this.bookings = this.manageBookingService.findAllByBookingIdIn(ids);
        return queue;
    }

    private PaymentDetailDto createDetailsTypeCash(double invoiceAmount) {
        ManagePaymentTransactionTypeDto transactionTypeDto = this.paymentTransactionTypeService.findByPaymentInvoiceCacheable();
        PaymentDetailDto newDetailDto = new PaymentDetailDto();
        newDetailDto.setId(UUID.randomUUID());
        newDetailDto.setStatus(Status.ACTIVE);
        newDetailDto.setPayment(this.updatePayment.toAggregate());
        newDetailDto.setTransactionType(transactionTypeDto);
        newDetailDto.setAmount(invoiceAmount);
        newDetailDto.setRemark(transactionTypeDto.getDefaultRemark());
        newDetailDto.setApplayPayment(Boolean.FALSE);
        newDetailDto.setCreateByCredit(false);

//        this.paymentDetailService.create(newDetailDto);
        this.calculateCash(invoiceAmount);

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
        PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIdsCacheable(hotel);
        //PaymentCloseOperationDto closeOperationDto = this.paymentCloseOperationService.findByHotelIds(hotel);

        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

    public PaymentDetailDto createDetailsTypeApplyDeposit(PaymentDetailDto paymentDetailDto, double amount) {

        PaymentDetail parentDetail = this.detailTypeDeposits.stream().filter(detail -> detail.getId().equals(paymentDetailDto.getId())).findFirst().get();
        this.calculateApplyDeposit(amount);
        //TODO: Se debe de validar esta variable para que cumpla con el Close Operation
        OffsetDateTime transactionDate = OffsetDateTime.now(ZoneId.of("UTC"));
        ManagePaymentTransactionTypeDto transactionTypeDto = this.paymentTransactionTypeService.findByApplyDeposit();

        //Se crea el Apply Deposit.
        PaymentDetail children = new PaymentDetail();
        children.setId(UUID.randomUUID());
        children.setStatus(Status.ACTIVE);
        children.setPayment(this.updatePayment);
        children.setTransactionType(new ManagePaymentTransactionType(transactionTypeDto));
        children.setAmount(amount);
        children.setRemark(transactionTypeDto.getDefaultRemark());
        children.setTransactionDate(transactionDate);

        //Se asigna el padre.
        children.setParentId(parentDetail.getPaymentDetailId());

        //Agregando los Apply Deposit.
        List<PaymentDetail> updateChildrens = new ArrayList<>();
        updateChildrens.addAll(parentDetail.getChildren());
        updateChildrens.add(children);
        parentDetail.setChildren(updateChildrens);
        parentDetail.setApplyDepositValue(paymentDetailDto.getApplyDepositValue() - amount);

        //Actualizando el Deposit
        this.updatePaymentDetails(parentDetail);
        //paymentDetailService.update(paymentDetailDto);
        return children.toAggregate();
    }

    private void calculateCash(double amount) {
        this.updatePayment.setIdentified(this.updatePayment.getIdentified() + amount);
        this.updatePayment.setNotIdentified(this.updatePayment.getNotIdentified() - amount);

        this.updatePayment.setApplied(this.updatePayment.getApplied() + amount);
        this.updatePayment.setNotApplied(this.updatePayment.getNotApplied() - amount);
        this.updatePayment.setPaymentBalance(this.updatePayment.getPaymentBalance() - amount);

        //this.paymentService.update(paymentDto);
    }

    private void calculateApplyDeposit(double amount) {
        this.updatePayment.setDepositBalance(this.updatePayment.getDepositBalance() - amount);
        //paymentDto.setNotApplied(paymentDto.getNotApplied() + newDetailDto.getAmount()); // TODO: al hacer un applied deposit el notApplied aumenta.
        this.updatePayment.setApplied(this.updatePayment.getApplied() + amount); // TODO: Suma de trx tipo check Cash + Check Apply Deposit  en el Manage Payment Transaction Type
        this.updatePayment.setIdentified(this.updatePayment.getIdentified() + amount);
        this.updatePayment.setNotIdentified(this.updatePayment.getNotIdentified() - amount);

    }

    public void applyPayment(UUID empoyee, ManageBookingDto bookingDto, PaymentDetailDto paymentDetailDto) {
        Booking booking = this.bookings.stream().filter(d -> d.getId().equals(bookingDto.getId())).findFirst().get();
        booking.setAmountBalance(booking.getAmountBalance() - paymentDetailDto.getAmount());
        this.updateBooking(booking);

        paymentDetailDto.setManageBooking(booking.toAggregate());
        paymentDetailDto.setApplayPayment(Boolean.TRUE);
        paymentDetailDto.setTransactionDate(transactionDate(paymentDetailDto.getPayment().getHotel().getId()));
        this.paymentDetailService.create(paymentDetailDto);

        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    this.updatePayment.getId(),
                    this.updatePayment.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            this.producerUpdateBookingService.update(new UpdateBookingBalanceKafka(booking.getId(), paymentDetailDto.getAmount(), paymentKafka, false));
        } catch (Exception e) {
            System.err.println("Error al enviar el evento de integracion: " + e.getMessage());
        }

        if (this.updatePayment.getPaymentBalance() == 0 && this.updatePayment.getDepositBalance() == 0) {
            this.updatePayment.setPaymentStatus(this.statusService.findPaymentStatusByApplied());
            ManageEmployeeDto employeeDto = empoyee != null ? this.manageEmployeeService.findById(empoyee) : null;
            this.createPaymentAttachmentStatusHistory(employeeDto, this.updatePayment.toAggregate());
        }
        this.updatePayment.setApplyPayment(true);
    }

    private void updateBooking(Booking update) {
        int index = this.bookings.indexOf(update);
        if (index != -1) {
            this.bookings.set(index, update);
        }
    }

    private void updatePaymentDetails(PaymentDetail update) {
        int index = this.detailTypeDeposits.indexOf(update);
        if (index != -1) {
            this.detailTypeDeposits.set(index, update);
        }
    }

}
