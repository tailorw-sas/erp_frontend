package com.kynsoft.report.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.report.applications.query.dbconection.getById.DBConectionResponse;
import com.kynsoft.report.domain.dto.DBConectionDto;
import com.kynsoft.report.domain.services.IDBConectionService;
import com.kynsoft.report.infrastructure.entity.DBConection;
import com.kynsoft.report.infrastructure.repository.command.DBConectionWriteDataJPARepository;
import com.kynsoft.report.infrastructure.repository.query.DBConectionReadDataJPARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DBConectionServiceImpl implements IDBConectionService {

    private final DBConectionWriteDataJPARepository repositoryCommand;

    private final DBConectionReadDataJPARepository repositoryQuery;

    public DBConectionServiceImpl(DBConectionWriteDataJPARepository repositoryCommand, DBConectionReadDataJPARepository repositoryQuery) {
        this.repositoryCommand = repositoryCommand;
        this.repositoryQuery = repositoryQuery;
    }
    @Override
    public void create(DBConectionDto object) {
        DBConection dbConection = new DBConection(object);
        this.repositoryCommand.save(dbConection);
    }

    @Override
    public void update(DBConectionDto object) {
        DBConection dbConection = new DBConection(object);
        this.repositoryCommand.save(dbConection);
    }

    @Override
    public void delete(DBConectionDto object) {
        try {
            this.repositoryCommand.deleteById(object.getId());
        } catch (Exception e) {
            throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_DELETE, new ErrorField("id", DomainErrorMessage.NOT_DELETE.getReasonPhrase())));
        }
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<DBConection> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<DBConection> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    @Override
    public DBConectionDto findById(UUID id) {
        Optional<DBConection> dbConection = this.repositoryQuery.findById(id);
        if (dbConection.isPresent()) {
            return dbConection.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.NOT_FOUND,
                new ErrorField("id",  DomainErrorMessage.NOT_FOUND.getReasonPhrase())));
    }

    @Override
    public Long countByCodeAndNotId(String code, UUID id) {
        return this.repositoryQuery.countByCodeAndNotId(code, id);
    }

    private PaginatedResponse getPaginatedResponse(Page<DBConection> data) {
        List<DBConectionResponse> responses = new ArrayList<>();
        for (DBConection p : data.getContent()) {
            responses.add(new DBConectionResponse(p.toAggregate()));
        }
        return new PaginatedResponse(responses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }
}
