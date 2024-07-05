package com.kynsoft.finamer.creditcard.application.query.test.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.finamer.creditcard.application.query.objectResponse.TestResponse;
import com.kynsoft.finamer.creditcard.domain.services.ITestService;
import com.kynsoft.finamer.creditcard.domain.dto.TestDto;
import org.springframework.stereotype.Component;

@Component
public class FindTestByIdQueryHandler implements IQueryHandler<FindTestByIdQuery, TestResponse>  {

    private final ITestService service;

    public FindTestByIdQueryHandler(ITestService service) {
        this.service = service;
    }

    @Override
    public TestResponse handle(FindTestByIdQuery query) {
        TestDto response = service.findById(query.getId());

        return new TestResponse(response);
    }
}
