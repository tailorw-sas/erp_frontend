package com.kynsoft.finamer.creditcard.infrastructure.services;

import com.kynsof.share.core.domain.exception.BusinessNotFoundException;
import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.exception.GlobalBusinessException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.specifications.GenericSpecificationsBuilder;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TestResponse;
import com.kynsoft.finamer.creditcard.domain.services.ITestService;
import com.kynsoft.finamer.creditcard.infrastructure.repository.command.TestWriteDataJPARepository;
import com.kynsoft.finamer.creditcard.infrastructure.repository.query.TestReadDataJPARepository;
import com.kynsoft.finamer.creditcard.domain.dto.TestDto;
import com.kynsoft.finamer.creditcard.infrastructure.identity.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TestServiceImpl implements ITestService {

    @Autowired
    private TestWriteDataJPARepository repositoryCommand;

    @Autowired
    private TestReadDataJPARepository repositoryQuery;

    @Override
    public UUID create(TestDto dto) {
        Test data = new Test(dto);
        Test test = this.repositoryCommand.save(data);
        return test.getId();
    }

    @Override
    public void update(TestDto dto) {
        this.repositoryCommand.save(new Test(dto));
    }

    @Override
    public void delete(TestDto dto) {
        Test delete = new Test(dto);
        delete.setDeleted(Boolean.TRUE);
        delete.setUserName(delete.getUserName() + "-" + UUID.randomUUID());

        this.repositoryCommand.save(delete);
    }

    @Override
    public TestDto findById(UUID id) {
        Optional<Test> userSystem = this.repositoryQuery.findById(id);
        if (userSystem.isPresent()) {
            return userSystem.get().toAggregate();
        }
        throw new BusinessNotFoundException(new GlobalBusinessException(DomainErrorMessage.TEST_NOT_FOUND, new ErrorField("id", "Test not found.")));
    }

    @Override
    public PaginatedResponse search(Pageable pageable, List<FilterCriteria> filterCriteria) {
        GenericSpecificationsBuilder<Test> specifications = new GenericSpecificationsBuilder<>(filterCriteria);
        Page<Test> data = this.repositoryQuery.findAll(specifications, pageable);

        return getPaginatedResponse(data);
    }

    private PaginatedResponse getPaginatedResponse(Page<Test> data) {
        List<TestResponse> userSystemsResponses = new ArrayList<>();
        for (Test p : data.getContent()) {
            userSystemsResponses.add(new TestResponse(p.toAggregate()));
        }
        return new PaginatedResponse(userSystemsResponses, data.getTotalPages(), data.getNumberOfElements(),
                data.getTotalElements(), data.getSize(), data.getNumber());
    }

    @Override
    public Long countByNameAndNotId(String userName, UUID id) {
        return this.repositoryQuery.countByUserNameAndNotId(userName, id);
    }

}
