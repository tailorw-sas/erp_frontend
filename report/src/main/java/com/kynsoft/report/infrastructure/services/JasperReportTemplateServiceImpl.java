package com.kynsoft.report.infrastructure.services;

import com.kynsoft.report.applications.query.jasperreporttemplate.getbyid.JasperReportTemplateResponse;
import com.kynsoft.report.domain.dto.JasperReportTemplateDto;
import com.kynsoft.report.domain.services.IJasperReportTemplateService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JasperReportTemplateServiceImpl implements IJasperReportTemplateService {

    @Autowired
    private JasperReportTemplateWriteDataJPARepository commandRepository;

    @Autowired
    private JasperReportTemplateReadDataJPARepository queryRepository;

    @Override
    public void create(JasperReportTemplateDto object) {
        this.commandRepository.save(new JasperReportTemplate(object));
    }

    @Override
    public void update(JasperReportTemplateDto object) {
        this.commandRepository.save(new JasperReportTemplate(object));
    }

    @Override
    public void delete(JasperReportTemplateDto object) {
        JasperReportTemplate delete = new JasperReportTemplate(object);
        delete.setDeleted(Boolean.TRUE);
        delete.setTemplateCode(delete.getTemplateCode() + " + " + UUID.randomUUID());
        delete.setTemplateName(delete.getTemplateName()+ " + " + UUID.randomUUID());

        this.commandRepository.save(delete);
    }

    @Override
    public JasperReportTemplateDto findById(UUID id) {
        Optional<JasperReportTemplate> object = this.queryRepository.findById(id);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("id", "JasperReportTemplate not found.")));
    }

    @Override
    public JasperReportTemplateDto findByTemplateCode(String templateCode) {
        Optional<JasperReportTemplate> object = this.queryRepository.findByTemplateCode(templateCode);
        if (object.isPresent()) {
            return object.get().toAggregate();
        }

        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.BUSINESS_NOT_FOUND, new ErrorField("id", "JasperReportTemplate not found.")));
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

}
