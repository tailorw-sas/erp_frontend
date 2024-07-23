package com.kynsoft.finamer.invoicing.application.command.income.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAgencyDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceStatusDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import com.kynsoft.finamer.invoicing.domain.services.IIncomeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageAgencyService;
import com.kynsoft.finamer.invoicing.domain.services.IManageHotelService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceStatusService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceTypeService;
import java.time.LocalDate;
import java.util.UUID;

import java.util.function.Consumer;

import org.springframework.stereotype.Component;

@Component
public class UpdateIncomeCommandHandler implements ICommandHandler<UpdateIncomeCommand> {

    private final IManageAgencyService agencyService;
    private final IManageHotelService hotelService;
    private final IIncomeService incomeService;
    private final IManageInvoiceTypeService invoiceTypeService;
    private final IManageInvoiceStatusService invoiceStatusService;

    public UpdateIncomeCommandHandler(IManageAgencyService agencyService,
                                      IIncomeService incomeService,
                                      IManageHotelService hotelService,
                                      IManageInvoiceTypeService invoiceTypeService,
                                      IManageInvoiceStatusService invoiceStatusService) {
        this.agencyService = agencyService;
        this.incomeService = incomeService;
        this.hotelService = hotelService;
        this.invoiceTypeService = invoiceTypeService;
        this.invoiceStatusService = invoiceStatusService;
    }

    @Override
    public void handle(UpdateIncomeCommand command) {

        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "Payment ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getAgency(), "agency", "Agency ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getHotel(), "hotel", "Hotel ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getInvoiceType(), "invoiceType", "Invoince Type ID cannot be null."));
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getInvoiceStatus(), "invoiceStatus", "Invoince Status ID cannot be null."));

        IncomeDto incomeDto = this.incomeService.findById(command.getId());
        ManageInvoiceTypeDto invoiceTypeDto = null;
        try {
            invoiceTypeDto = this.invoiceTypeService.findById(command.getInvoiceType());
        } catch (Exception e) {
        }
        incomeDto.setInvoiceType(invoiceTypeDto);

        ManageInvoiceStatusDto invoiceStatusDto = null;
        try {
            invoiceStatusDto = this.invoiceStatusService.findById(command.getInvoiceStatus());
        } catch (Exception e) {
        }
        incomeDto.setInvoiceStatus(invoiceStatusDto);

        ConsumerUpdate update = new ConsumerUpdate();
        UpdateIfNotNull.updateDouble(incomeDto::setIncomeAmount, command.getIncomeAmount(), incomeDto.getIncomeAmount(), update::setUpdate);
        this.updateStatus(incomeDto::setStatus, command.getStatus(), incomeDto.getStatus(), update::setUpdate);
        this.updateBooleam(incomeDto::setManual, command.getManual(), incomeDto.getManual(), update::setUpdate);
        this.updateBooleam(incomeDto::setReSend, command.getReSend(), incomeDto.getReSend(), update::setUpdate);
        this.updateDate(incomeDto::setInvoiceDate, command.getInvoiceDate(), incomeDto.getInvoiceDate(), update::setUpdate);
        this.updateDate(incomeDto::setDueDate, command.getDueDate(), incomeDto.getDueDate(), update::setUpdate);
        this.updateDate(incomeDto::setReSendDate, command.getReSendDate(), incomeDto.getReSendDate(), update::setUpdate);
        this.updateManageAgency(incomeDto::setAgency, command.getAgency(), incomeDto.getAgency().getId(), update::setUpdate);
        this.updateManageHotel(incomeDto::setHotel, command.getHotel(), incomeDto.getHotel().getId(), update::setUpdate);

        this.incomeService.update(incomeDto);

    }

    private boolean updateManageAgency(Consumer<ManageAgencyDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageAgencyDto agencyDto = this.agencyService.findById(newValue);
            setter.accept(agencyDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private boolean updateManageHotel(Consumer<ManageHotelDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            ManageHotelDto hotelDto = this.hotelService.findById(newValue);
            setter.accept(hotelDto);
            update.accept(1);

            return true;
        }
        return false;
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

    private void updateBooleam(Consumer<Boolean> setter, Boolean newValue, Boolean oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

    private void updateDate(Consumer<LocalDate> setter, LocalDate newValue, LocalDate oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);

        }
    }

}
