package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.jasperReportParameter.getById.JasperReportParameterResponse;
import com.kynsoft.report.domain.dto.JasperReportParameterDto;
import com.kynsoft.report.domain.services.IReportParameterService;
import com.kynsoft.report.infrastructure.entity.JasperReportParameter;
import com.kynsoft.report.infrastructure.repository.command.JasperReportParameterWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.JasperReportTemplateParameterReadDataJPARepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JasperReportParameterServiceImpl implements IReportParameterService {

    private final JasperReportParameterWriteDataJPARepository repositoryCommand;

    private final JasperReportTemplateParameterReadDataJPARepository repositoryQuery;

    public JasperReportParameterServiceImpl(JasperReportParameterWriteDataJPARepository repositoryCommand,
                                            JasperReportTemplateParameterReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }
    @Override
    public void create(JasperReportParameterDto object) {
        JasperReportParameter dbConection = new JasperReportParameter(object);
        this.repositoryCommand.save(dbConection);
    }

    @Override
    public void update(JasperReportParameterDto object) {
        JasperReportParameter dbConection = new JasperReportParameter(object);
        this.repositoryCommand.save(dbConection);
    }

    @Override
    public void delete(JasperReportParameterDto object) {
        try {
            this.repositoryCommand.deleteById(object.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<JasperReportParameter> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<JasperReportParameter> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public JasperReportParameterDto findById(UUID id) {
        Optional<JasperReportParameter> parameter = this.repositoryQuery.findById(id);
        if (parameter.isPresent()) {
            return parameter.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND,
                new ErrorField("id",  DomainErrorMessage.NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public List<JasperReportParameterDto> findByTemplateId(UUID id) {
        List<JasperReportParameter> parameterList = this.repositoryQuery.findByTemplateId(id);
        List<JasperReportParameterDto> dtoList = new ArrayList<>();

        if (parameterList != null) {
            for (JasperReportParameter parameter : parameterList) {
                dtoList.add(parameter.toAggregate());
            }
        }
        return dtoList;
    }


    private PaginatedResponse getPaginatedResponse(Page<JasperReportParameter> data) {
        List<JasperReportParameterResponse> responses = new ArrayList<>();
        for (JasperReportParameter p : data.getContent()) {
            responses.add(new JasperReportParameterResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
