package com.kynsoft.report.applications.command.jasperReportTemplate.update;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsof.share.core.domain.rules.ValidateObjectNotNullRule;
import com.kynsof.share.utils.ConsumerUpdate;
import com.kynsof.share.utils.UpdateIfNotNull;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateType;
import com.kynsoft.report.domain.dto.status.Status;
import com.kynsoft.report.domain.services.IDBConectionService;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;

import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Component;

@Component
public class UpdateJasperReportTemplateCommandHandler implements ICommandHandler<UpdateJasperReportTemplateCommand> {

    private final IJasperReportTemplateService service;

    private final IDBConectionService conectionService;

    public UpdateJasperReportTemplateCommandHandler(IJasperReportTemplateService service, IDBConectionService conectionService) {
        this.service = service;
        this.conectionService = conectionService;
    }

    @Override
    public void handle(UpdateJasperReportTemplateCommand command) {
        RulesChecker.checkRule(new ValidateObjectNotNullRule<>(command.getId(), "id", "JasperReportTemplate ID cannot be null."));
        JasperReportTemplateDto updateDto = this.service.findById(command.getId());

        ConsumerUpdate update = new ConsumerUpdate();
        updateStatus(updateDto::setStatus, command.getStatus(), updateDto.getStatus(), update::setUpdate);
        updateType(updateDto::setType, command.getType(), updateDto.getType(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(updateDto::setDescription, command.getDescription(), updateDto.getDescription(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(updateDto::setFile, command.getFile(), updateDto.getFile(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(updateDto::setName, command.getName(), updateDto.getName(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(updateDto::setParameters, command.getParameters(), updateDto.getParameters(), update::setUpdate);

        UpdateIfNotNull.updateDouble(updateDto::setParentIndex, command.getParentIndex(), updateDto.getParentIndex(), update::setUpdate);
        UpdateIfNotNull.updateDouble(updateDto::setMenuPosition, command.getMenuPosition(), updateDto.getMenuPosition(), update::setUpdate);

        UpdateIfNotNull.updateBoolean(updateDto::setWeb, command.getWeb(), updateDto.getWeb(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(updateDto::setSubMenu, command.getSubMenu(), updateDto.getSubMenu(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(updateDto::setSendEmail, command.getSendEmail(), updateDto.getSendEmail(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(updateDto::setInternal, command.getInternal(), updateDto.getInternal(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(updateDto::setHighRisk, command.getHighRisk(), updateDto.getHighRisk(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(updateDto::setVisible, command.getVisible(), updateDto.getVisible(), update::setUpdate);
        UpdateIfNotNull.updateBoolean(updateDto::setCancel, command.getCancel(), updateDto.getCancel(), update::setUpdate);

        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(updateDto::setRootIndex, command.getRootIndex(), updateDto.getRootIndex(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(updateDto::setLanguage, command.getLanguage(), updateDto.getLanguage(), update::setUpdate);
        UpdateIfNotNull.updateIfStringNotNullNotEmptyAndNotEquals(updateDto::setQuery, command.getQuery(), updateDto.getQuery(), update::setUpdate);

        this.updateConection(updateDto::setDbConection, command.getDbConection(), updateDto.getDbConection() != null ? updateDto.getDbConection().getId() : null);

        if (update.getUpdate() > 0) {
            this.service.update(updateDto);
        }
    }

    private void updateStatus(Consumer<Status> setter, Status newValue, Status oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

    private void updateType(Consumer<JasperReportTemplateType> setter, JasperReportTemplateType newValue, JasperReportTemplateType oldValue, Consumer<Integer> update) {
        if (newValue != null && !newValue.equals(oldValue)) {
            setter.accept(newValue);
            update.accept(1);
        }
    }

    private void updateConection(Consumer<DBConectionDto> setter, UUID newValue, UUID oldValue) {
        if (newValue != null && !newValue.equals(oldValue)) {
            DBConectionDto conectionDto = this.conectionService.findById(newValue);
            setter.accept(conectionDto);
        }
    }

}
