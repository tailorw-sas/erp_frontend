package com.kynsoft.finamer.payment.application.command.payment.applyPayment;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.http.entity.InvoiceHttp;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentDetailsKafka;
import com.kynsof.share.core.domain.kafka.entity.ReplicatePaymentKafka;
import com.kynsof.share.core.domain.kafka.entity.update.UpdateBookingBalanceKafka;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.core.infrastructure.util.DateUtil;
import com.kynsof.share.utils.BankerRounding;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
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
import com.kynsoft.finamer.payment.infrastructure.identity.*;
import com.kynsoft.finamer.payment.infrastructure.services.http.InvoiceHttpUUIDService;
import com.kynsoft.finamer.payment.infrastructure.services.http.helper.InvoiceImportAutomaticeHelperServiceImpl;
import com.kynsoft.finamer.payment.infrastructure.services.kafka.producer.updateBooking.ProducerUpdateListBookingService;
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
    private final IPaymentStatusHistoryService paymentStatusHistoryService;

    private final IPaymentCloseOperationService paymentCloseOperationService;
    private final IManageBookingService manageBookingService;

    private final ProducerUpdateListBookingService producerUpdateListBookingService;
    private final IManagePaymentStatusService statusService;
    private final IManageEmployeeService manageEmployeeService;

    public ApplyPaymentCommandHandler(IPaymentService paymentService,
            IManageInvoiceService manageInvoiceService,
            IPaymentDetailService paymentDetailService,
            InvoiceHttpUUIDService invoiceHttpUUIDService,
            InvoiceImportAutomaticeHelperServiceImpl invoiceImportAutomaticeHelperServiceImpl,
            IManagePaymentTransactionTypeService paymentTransactionTypeService,
            IPaymentStatusHistoryService paymentStatusHistoryService,
            IPaymentCloseOperationService paymentCloseOperationService,
            IManageBookingService manageBookingService,
            IManagePaymentStatusService statusService,
            IManageEmployeeService manageEmployeeService,
            ProducerUpdateListBookingService producerUpdateListBookingService) {
        this.invoiceHttpUUIDService = invoiceHttpUUIDService;
        this.invoiceImportAutomaticeHelperServiceImpl = invoiceImportAutomaticeHelperServiceImpl;
        this.paymentService = paymentService;
        this.manageInvoiceService = manageInvoiceService;
        this.paymentDetailService = paymentDetailService;
        this.paymentTransactionTypeService = paymentTransactionTypeService;
        this.paymentStatusHistoryService = paymentStatusHistoryService;
        this.paymentCloseOperationService = paymentCloseOperationService;
        this.manageBookingService = manageBookingService;
        this.statusService = statusService;
        this.manageEmployeeService = manageEmployeeService;
        this.producerUpdateListBookingService = producerUpdateListBookingService;
    }

    @Override
    public void handle(ApplyPaymentCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getPayment(), "id", "Payment ID cannot be null."));

        List<UpdateBookingBalanceKafka> kafkaList = new ArrayList<>();
        List<Booking> bookingsList = new ArrayList<>();
        List<PaymentDetail> detailTypeDeposits = new ArrayList<>();
        List<PaymentDetail> createPaymentDetails = new ArrayList<>();

        Payment updatePayment = this.paymentService.findByIdWithBalancesOnly(command.getPayment());
        PaymentDto payment = this.paymentService.findByIdCustom(command.getPayment());
        ManagePaymentTransactionType cachedApplyDepositTransactionType = this.paymentTransactionTypeService.findByApplyDepositEntityGraph();//AANT
        ManagePaymentTransactionType cachedPaymentInvoiceTransactionType = this.paymentTransactionTypeService.findByPaymentInvoiceEntityGraph();//PAGO

        List<Invoice> invoiceQueue = createInvoiceQueue(command, bookingsList);
        List<PaymentDetailDto> deposits = this.createPaymentDetailsTypeDepositQueue(command.getDeposits(), detailTypeDeposits);

        List<UUID> hotelIds = invoiceQueue.stream().map(invoice -> invoice.getHotel().getId()).collect(Collectors.toList());
        Map<UUID, PaymentCloseOperationDto> paymentCloseOperationByHotelMap = this.getTransactionDateMapByHotel(hotelIds);

        double paymentBalance = updatePayment.getPaymentBalance();
        double notApplied = updatePayment.getNotApplied();
        double depositBalance = updatePayment.getDepositBalance();
        boolean applyPaymentBalance =  (updatePayment.getPaymentBalance() == 0) ? false : true;
        for (Invoice manageInvoiceDto : invoiceQueue) {
            List<Booking> bookingDtoList = getSortedBookings(manageInvoiceDto);
            for (Booking bookingDto : bookingDtoList) {
                //TODO: almaceno el valor de Balance del Booking porque puede que no llegue a cero cuando el Payment Balance si lo haga. Y todavia
                // tenga valor el notApplied
                double amountBalance = bookingDto.getAmountBalance();
                if (notApplied > 0 && paymentBalance > 0 && command.isApplyPaymentBalance() && amountBalance > 0) {
                    double amountToApply = Math.min(notApplied, amountBalance);
                    OffsetDateTime transactionDate = this.getTransactionDate(paymentCloseOperationByHotelMap.get(payment.getHotel().getId()));

                    PaymentDetail message = createDetailsTypeCash(amountToApply, cachedPaymentInvoiceTransactionType, updatePayment, transactionDate);
                    this.applyPayment(command.getEmployee(), bookingDto, message, kafkaList, bookingsList, createPaymentDetails, updatePayment, transactionDate);
                    notApplied =  BankerRounding.round(notApplied - amountToApply);
                    paymentBalance = BankerRounding.round(paymentBalance - amountToApply);
                    amountBalance = BankerRounding.round(amountBalance - amountToApply);
                }
                if ((notApplied == 0 && paymentBalance == 0 && command.isApplyDeposit() && amountBalance > 0 && depositBalance > 0)
                        || (command.isApplyDeposit() && !command.isApplyPaymentBalance() && amountBalance > 0 && depositBalance > 0)) {
                    //TODO: este aplica para cuando se quiere aplicar solo a los deposit
                    if (command.getDeposits() != null && !command.getDeposits().isEmpty()) {
                        for (PaymentDetailDto paymentDetailTypeDeposit : deposits) {
                            PaymentDetail parentDetail = detailTypeDeposits.stream().filter(detail ->
                                    detail.getId().equals(paymentDetailTypeDeposit.getId())).findFirst().get();

                            double depositAmount = parentDetail.getApplyDepositValue();
                            if (depositAmount == 0) {
                                continue;
                            }
                            while (depositAmount > 0) {
                                double amountToApply = Math.min(depositAmount, amountBalance);
                                OffsetDateTime transactionDate = getTransactionDate(paymentCloseOperationByHotelMap.get(payment.getHotel().getId()));

                                this.applyPayment(command.getEmployee(), bookingDto,
                                        this.createDetailsTypeApplyDeposit(parentDetail, amountToApply, cachedApplyDepositTransactionType,
                                                detailTypeDeposits, updatePayment, transactionDate),
                                        kafkaList, bookingsList, createPaymentDetails, updatePayment, transactionDate);
                                depositAmount = BankerRounding.round(depositAmount - amountToApply);
                                amountBalance = BankerRounding.round(amountBalance - amountToApply);
                                depositBalance = BankerRounding.round(depositBalance - amountToApply);
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

        //Se crean los Payment Details que genera el flujo de aplicacion de pago.
        this.paymentDetailService.createAll(createPaymentDetails);
        //Se actualizan los booking balance.
        this.manageBookingService.updateAll(bookingsList);

        //Se actualiza el payment
        this.paymentService.updateBalances(
                updatePayment.getPaymentBalance(),
                updatePayment.getDepositBalance(),
                updatePayment.getIdentified(),
                updatePayment.getNotIdentified(),
                updatePayment.getNotApplied(),
                updatePayment.getApplied(),
                updatePayment.isApplyPayment(),
                updatePayment.getId()
        );
        //Se actualizan los deposit
        this.paymentDetailService.createAll(detailTypeDeposits);
        this.paymentCloseOperationService.clearCache();

        this.producerUpdateListBookingService.update(kafkaList);
    }

    /**
     *
     * @param deposits
     * @return
     */
    private List<PaymentDetailDto> createPaymentDetailsTypeDepositQueue(List<UUID> deposits, List<PaymentDetail> detailTypeDeposits) {
        try {
            detailTypeDeposits.addAll(this.paymentDetailService.findByPaymentDetailsApplyIdIn(deposits));
            List<PaymentDetailDto> queue = this.paymentDetailService.change(detailTypeDeposits);

            queue.sort(Comparator.comparingDouble(PaymentDetailDto::getApplyDepositValue));
            return queue;
        } catch (Exception e) {
            return List.of();
        }
    }

    /**
     * Ordena los Booking de menor a mayor por su AmountBalance.
     *
     * @param manageInvoiceDto
     * @return
     */
    private List<Booking> getSortedBookings(Invoice manageInvoiceDto) {
        List<Booking> bookingDtoList = new ArrayList<>();
        if (manageInvoiceDto.getBookings() != null && !manageInvoiceDto.getBookings().isEmpty()) {
            bookingDtoList.addAll(manageInvoiceDto.getBookings());
            bookingDtoList.sort(Comparator.comparingDouble(Booking::getAmountBalance));
        }
        return bookingDtoList;
    }

    /**
     * Ordena las Invoice de menor a mayor por su InvoiceAmount.
     *
     * @param command
     * @return
     */
    private List<Invoice> createInvoiceQueue(ApplyPaymentCommand command, List<Booking> bookingsList) {
        List<Invoice> queue = new ArrayList<>();
        //List<ManageInvoiceDto> queue = new ArrayList<>();
        try {
            queue.addAll(this.manageInvoiceService.findInvoiceWithEntityGraphByIdIn(command.getInvoices()));
        } catch (Exception e) {
            for (UUID invoice : command.getInvoices()) {
                /**
                 * *
                 * TODO: Aqui se define un flujo alternativo por HTTP si en
                 * algun momento kafka falla y las invoice no se replicaron,
                 * para evitar que el flujo de aplicacion de pago falle.
                 */
                InvoiceHttp response = invoiceHttpUUIDService.sendGetBookingHttpRequest(invoice);
                this.invoiceImportAutomaticeHelperServiceImpl.createInvoice(response);
            }
            //FLUJO PARA ESPERAR MIENTRAS LAS BD SE SINCRONIZAN.
            int maxAttempts = 3;
            int delay = 1;
            while (maxAttempts > 0) {
                try {
                    queue.addAll(manageInvoiceService.findInvoiceWithEntityGraphByIdIn(command.getInvoices()));
                    break;
                } catch (Exception exp) {
                    //log.warn("Retrying invoice fetch. Attempts left: {}. Error: {}", maxAttempts, exp.getMessage());
                }
                maxAttempts--;
                try {
                    TimeUnit.SECONDS.sleep(delay);
                    delay *= 2;
                } catch (InterruptedException exp) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        queue.sort(Comparator.comparingDouble(Invoice::getInvoiceAmount));
        for (Invoice invoice : queue) {
            bookingsList.addAll(invoice.getBookings());
        }
        return queue;
    }

    private PaymentDetail createDetailsTypeCash(double invoiceAmount, ManagePaymentTransactionType transactionTypeDto, Payment updatePayment, OffsetDateTime transactionDate) {
        PaymentDetail newDetailDto = new PaymentDetail();
        newDetailDto.setId(UUID.randomUUID());
        newDetailDto.setStatus(Status.ACTIVE);
        newDetailDto.setPayment(updatePayment);
        newDetailDto.setTransactionType(transactionTypeDto);
        newDetailDto.setAmount(invoiceAmount);
        newDetailDto.setRemark(transactionTypeDto.getDefaultRemark());
        newDetailDto.setApplyPayment(Boolean.FALSE);
        newDetailDto.setCreateByCredit(false);
        newDetailDto.setTransactionDate(transactionDate);

        this.calculateCash(invoiceAmount, updatePayment);

        return newDetailDto;
    }

    private void createPaymentStatusHistory(ManageEmployeeDto employeeDto, PaymentDto payment) {

        PaymentStatusHistoryDto paymentStatusHistoryDto = new PaymentStatusHistoryDto();
        paymentStatusHistoryDto.setId(UUID.randomUUID());
        paymentStatusHistoryDto.setDescription("Update Payment.");
        paymentStatusHistoryDto.setEmployee(employeeDto);
        paymentStatusHistoryDto.setPayment(payment);
        paymentStatusHistoryDto.setStatus(payment.getPaymentStatus().getCode() + "-" + payment.getPaymentStatus().getName());

        this.paymentStatusHistoryService.create(paymentStatusHistoryDto);
    }

    public PaymentDetail createDetailsTypeApplyDeposit(PaymentDetail parentDetail, double amount, ManagePaymentTransactionType transactionTypeDto,
            List<PaymentDetail> detailTypeDeposits, Payment updatePayment, OffsetDateTime transactionDate) {

        this.calculateApplyDeposit(amount, updatePayment);

        //Create detail Apply Deposit.
        PaymentDetail children = new PaymentDetail();
        children.setId(UUID.randomUUID());
        children.setStatus(Status.ACTIVE);
        children.setPayment(parentDetail.getPayment());
        children.setTransactionType(transactionTypeDto);
        children.setAmount(amount);
        children.setRemark(transactionTypeDto.getDefaultRemark());
        children.setTransactionDate(transactionDate);

        //Set Parent
        children.setParentId(parentDetail.getPaymentDetailId());

        //Add los Apply Deposit.
        List<PaymentDetail> updateChildren = new ArrayList<>(parentDetail.getPaymentDetails());
        updateChildren.add(children);
        parentDetail.setPaymentDetails(updateChildren);
        parentDetail.setApplyDepositValue(BankerRounding.round(parentDetail.getApplyDepositValue() - amount));

        parentDetail.setUpdatedAt(OffsetDateTime.now());
        parentDetail.setAppliedAt(transactionDate);//TODO Preguntar si es factible setear el campo appliedAt al deposito del cual se esta creando el AANT.

        //Update Detail Deposit
        this.updatePaymentDetails(parentDetail, detailTypeDeposits);
        return children;
    }

    private void calculateCash(double amount, Payment updatePayment) {
        updatePayment.setIdentified(BankerRounding.round(updatePayment.getIdentified() + amount));
        updatePayment.setNotIdentified(BankerRounding.round(updatePayment.getNotIdentified() - amount));

        updatePayment.setApplied(BankerRounding.round(updatePayment.getApplied() + amount));
        updatePayment.setNotApplied(BankerRounding.round(updatePayment.getNotApplied() - amount));
        updatePayment.setPaymentBalance(BankerRounding.round(updatePayment.getPaymentBalance() - amount));
    }

    private void calculateApplyDeposit(double amount, Payment updatePayment) {
        updatePayment.setDepositBalance(BankerRounding.round(updatePayment.getDepositBalance() - amount));
        updatePayment.setApplied(BankerRounding.round(updatePayment.getApplied() + amount)); // TODO: Suma de trx tipo check Cash + Check Apply

        updatePayment.setIdentified(BankerRounding.round(updatePayment.getIdentified() + amount));
        updatePayment.setNotIdentified(BankerRounding.round(updatePayment.getNotIdentified() - amount));

    }

    public void applyPayment(UUID empoyee, Booking bookingDto, PaymentDetail paymentDetailDto,
                            List<UpdateBookingBalanceKafka> kafkaList, List<Booking> bookingsList,
                            List<PaymentDetail> createPaymentdetails,
                            Payment updatePayment, OffsetDateTime effectiveDate) {
        Booking booking = bookingsList.stream().filter(d -> d.getId().equals(bookingDto.getId())).findFirst().get();
        booking.setAmountBalance(BankerRounding.round(booking.getAmountBalance() - paymentDetailDto.getAmount()));
        this.updateBooking(booking, bookingsList);

        paymentDetailDto.setManageBooking(booking);
        paymentDetailDto.setApplyPayment(Boolean.TRUE);
        paymentDetailDto.setAppliedAt(OffsetDateTime.now(ZoneId.of("UTC")));
        paymentDetailDto.setEffectiveDate(effectiveDate);
        paymentDetailDto.setUpdatedAt(OffsetDateTime.now());

        createPaymentdetails.add(paymentDetailDto);

        try {
            ReplicatePaymentKafka paymentKafka = new ReplicatePaymentKafka(
                    updatePayment.getId(),
                    updatePayment.getPaymentId(),
                    new ReplicatePaymentDetailsKafka(paymentDetailDto.getId(), paymentDetailDto.getPaymentDetailId()
                    ));
            kafkaList.add(new UpdateBookingBalanceKafka(booking.getId(), booking.getAmountBalance(), paymentKafka, false, OffsetDateTime.now()));
        } catch (Exception e) {
            System.err.println("Error al enviar el evento de integracion: " + e.getMessage());
        }

        if (updatePayment.getPaymentBalance() == 0 && updatePayment.getDepositBalance() == 0) {
            updatePayment.setPaymentStatus(this.statusService.findPaymentStatusByApplied());
            ManageEmployeeDto employeeDto = empoyee != null ? this.manageEmployeeService.findById(empoyee) : null;
            this.updatePaymentStatus(updatePayment);
            this.createPaymentStatusHistory(employeeDto, updatePayment.toAggregateBasic());
        }
        updatePayment.setApplyPayment(true);
        updatePayment.setUpdatedAt(OffsetDateTime.now());
    }

    private void updateBooking(Booking update, List<Booking> bookingsList) {
        int index = bookingsList.indexOf(update);
        if (index != -1) {
            bookingsList.set(index, update);
        }
    }

    private void updatePaymentDetails(PaymentDetail update, List<PaymentDetail> detailTypeDeposits) {
        int index = detailTypeDeposits.indexOf(update);
        if (index != -1) {
            detailTypeDeposits.set(index, update);
        }
    }

    private void updatePaymentStatus(Payment updatePayment){
        if (Objects.nonNull(updatePayment) && Objects.nonNull(updatePayment.getPaymentStatus())){
            this.paymentService.updateStatus(updatePayment.getId(), updatePayment.getPaymentStatus().getId());
        }
    }

    private Map<UUID, PaymentCloseOperationDto> getTransactionDateMapByHotel(List<UUID> hotelIds){
        return this.paymentCloseOperationService.findByHotelIds(hotelIds).stream()
                .collect(Collectors.toMap(paymentCloseOperationDto -> paymentCloseOperationDto.getHotel().getId(),
                        paymentCloseOperationDto -> paymentCloseOperationDto));
    }

    private OffsetDateTime getTransactionDate(PaymentCloseOperationDto closeOperationDto) {
        if (DateUtil.getDateForCloseOperation(closeOperationDto.getBeginDate(), closeOperationDto.getEndDate())) {
            return OffsetDateTime.now(ZoneId.of("UTC"));
        }
        return OffsetDateTime.of(closeOperationDto.getEndDate(), LocalTime.now(ZoneId.of("UTC")), ZoneOffset.UTC);
    }

}
