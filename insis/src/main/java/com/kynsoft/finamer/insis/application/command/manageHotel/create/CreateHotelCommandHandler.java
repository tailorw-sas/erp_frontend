package com.kynsoft.finamer.insis.application.command.manageHotel.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.finamer.insis.domain.dto.InnsistHotelRoomTypeDto;
import com.kynsoft.finamer.insis.domain.dto.ManageHotelDto;
import com.kynsoft.finamer.insis.domain.dto.ManageTradingCompanyDto;
import com.kynsoft.finamer.insis.domain.services.IInnsistHotelRoomTypeService;
import com.kynsoft.finamer.insis.domain.services.IManageHotelService;
import com.kynsoft.finamer.insis.domain.services.IManageTradingCompanyService;
import com.kynsoft.finamer.insis.infrastructure.model.kafka.ReplicateManageHotelKafka;
import com.kynsoft.finamer.insis.infrastructure.services.kafka.producer.manageHotel.ProducerReplicateManageHotelService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

@Component
public class CreateHotelCommandHandler implements ICommandHandler<CreateHotelCommand> {

    private final IManageHotelService service;
    private final IManageTradingCompanyService tradingCompanyService;
    private final ProducerReplicateManageHotelService producerReplicateManageHotelService;
    private final IInnsistHotelRoomTypeService tradingCompanyHotelService;

    public CreateHotelCommandHandler(IManageHotelService service,
                                     IManageTradingCompanyService tradingCompanyService,
                                     ProducerReplicateManageHotelService producerReplicateManageHotelService,
                                     IInnsistHotelRoomTypeService tradingCompanyHotelService){
        this.service = service;
        this.tradingCompanyService = tradingCompanyService;
        this.producerReplicateManageHotelService = producerReplicateManageHotelService;
        this.tradingCompanyHotelService = tradingCompanyHotelService;
    }

    @Override
    public void handle(CreateHotelCommand command) {
        ManageHotelDto dto = getHotelIfExits(command.getId());
        if(Objects.nonNull(getHotelIfExits(command.getId()))){
            ManageTradingCompanyDto tradingCompanyDto = tradingCompanyService.findById(command.getTradingCompany());

            ConsumerUpdate update = new ConsumerUpdate();

            UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setName, command.getName(), dto.getName(), update::setUpdate);
            UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(dto::setStatus, command.getStatus(), dto.getStatus(), update::setUpdate);

            ConsumerUpdate updateTradingCompany = new ConsumerUpdate();
            updateTradingCompanies(dto::setManageTradingCompany, tradingCompanyDto.getId(), dto.getManageTradingCompany().getId(), updateTradingCompany::setUpdate);

            service.update(dto);
        }else{
            ManageTradingCompanyDto tradingCompanyDto = tradingCompanyService.findById(command.getTradingCompany());
            ManageHotelDto hotelDto = new ManageHotelDto(
                    command.getId(),
                    command.getCode(),
                    command.getName(),
                    command.getStatus(),
                    false,
                    null,
                    tradingCompanyDto
            );
        }

        InnsistHotelRoomTypeDto tradingCompanyHotelDto = tradingCompanyHotelService.findByHotelAndActive(dto.getId(), "ACTIVE");

        ReplicateManageHotelKafka replicateManageHotelKafka = new ReplicateManageHotelKafka(
                dto.getId(),
                dto.getCode(),
                dto.getName(),
                Objects.nonNull(tradingCompanyHotelDto) ? tradingCompanyHotelDto.getRoomTypePrefix() : "",
                dto.getManageTradingCompany().getId()
        );
        producerReplicateManageHotelService.create(replicateManageHotelKafka);

    }

    private ManageHotelDto getHotelIfExits(UUID id){
        try{
            return service.findById(id);
        }catch (BusinessNotFoundException ex){
            return null;
        }catch (Exception ex){
            throw new RuntimeException(ex.toString());
        }
    }

    private boolean updateTradingCompanies(Consumer<ManageTradingCompanyDto> setter, UUID newValue, UUID oldValue, Consumer<Integer> update) {
        if (newValue != null) {
            if (!newValue.equals(oldValue)) {
                ManageTradingCompanyDto tradingCompaniesDto = tradingCompanyService.findById(newValue);
                setter.accept(tradingCompaniesDto);
                update.accept(1);

                return true;
            }
        } else {
            setter.accept(null);
            update.accept(1);
            return true;
        }
        return false;
    }
}
