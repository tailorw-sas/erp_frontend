package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.applications.query.jasperreporttemplate.getbyid.JasperReportTemplateResponse;
import com.kynsoft.report.applications.query.menu.ReportMenuResponse;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.dto.status.JasperReportTemplateWithParamsDto;
import com.kynsoft.report.domain.dto.status.ModuleSystems;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
import com.kynsoft.report.domain.services.IReportParameterService;
import com.kynsoft.report.infrastructure.entity.JasperReportTemplate;
import com.kynsoft.report.infrastructure.repository.command.JasperReportTemplateWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.JasperReportTemplateReadDataJPARepository;
import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JasperReportTemplateServiceImpl implements IJasperReportTemplateService {

    private final JasperReportTemplateWriteDataJPARepository commandRepository;
    private final JasperReportTemplateReadDataJPARepository queryRepository;
    private final IReportParameterService reportParameterService;

    public JasperReportTemplateServiceImpl(JasperReportTemplateWriteDataJPARepository commandRepository,
                                           JasperReportTemplateReadDataJPARepository queryRepository, IReportParameterService reportParameterService) {
        this.commandRepository = commandRepository;
        this.queryRepository = queryRepository;
        this.reportParameterService = reportParameterService;
    }

    @Override
    public UUID create(JasperReportTemplateDto object) {
        return this.commandRepository.save(new JasperReportTemplate(object)).getId();
    }

    @Override
    public void update(JasperReportTemplateDto object) {
        JasperReportTemplate update = new JasperReportTemplate(object);
        update.setUpdateAt(LocalDateTime.now());
        this.commandRepository.save(update);
    }

    @Override
    public void delete(JasperReportTemplateDto object) {
        try {
            this.commandRepository.deleteById(object.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE,
                    new ErrorField("id", "Element cannot be deleted has a related element.")));
        }
    }

    @Override
    public JasperReportTemplateDto findById(UUID id) {
        Optional<JasperReportTemplate> object = this.queryRepository.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "JasperReportTemplate not found.")));
    }

    @Override
    public JasperReportTemplateWithParamsDto getReportTemplateWithParams(UUID id) {
        JasperReportTemplate baseTemplate = this.queryRepository.findById(id)
                .orElseThrow(() -> new BusinessNotFoundException(new GlobalBusinessException(
                        DomainErrorMessage.BUSINESS_NOT_FOUND,
                        new ErrorField("id", "JasperReportTemplate not found.")
                )));

        JasperReportTemplateWithParamsDto dto = new JasperReportTemplateWithParamsDto();
        BeanUtils.copyProperties(baseTemplate.toAggregate(), dto);

        List<JasperReportParameterDto> parameters = reportParameterService.findByTemplateId(id);
        dto.setParameters(parameters);

        return dto;
    }

    @Override
    public JasperReportTemplateDto findByTemplateCode(String templateCode) {
        Optional<JasperReportTemplate> object = this.queryRepository.findByCode(templateCode);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND,
                new ErrorField("id", "JasperReportTemplate not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<JasperReportTemplate> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<JasperReportTemplate> data = this.queryRepository.findAll(specifications, pageable);
        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<JasperReportTemplate> data) {
        List<JasperReportTemplateResponse> patients = new ArrayList<>();
        for (JasperReportTemplate o : data.getContent()) {
            patients.add(new JasperReportTemplateResponse(o.toAggregate()));
        }
        return new PaginatedResponse(patients, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByCodeAndNotId(String templateCode, UUID id) {
        return this.queryRepository.countByCodeAndNotId(templateCode, id);
    }

    @Override
    public Map<ModuleSystems, List<ReportMenuResponse>> getGroupedAndOrderedReports() {
        // Obtener todos los reportes
        List<JasperReportTemplate> templates = queryRepository.findAll();

        // Agrupar por módulo y ordenar por `menuPosition`
        return templates.stream()
                .map(JasperReportTemplate::toAggregate) // Convertir a DTO
                .map(ReportMenuResponse::new) // Crear Response
                .collect(Collectors.groupingBy(
                        ReportMenuResponse::getModuleSystems, // Agrupar por módulo
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                list -> list.stream()
                                        .sorted((a, b) -> Double.compare(a.getMenuPosition(), b.getMenuPosition())) // Ordenar
                                        .collect(Collectors.toList())
                        )
                ));
    }
}