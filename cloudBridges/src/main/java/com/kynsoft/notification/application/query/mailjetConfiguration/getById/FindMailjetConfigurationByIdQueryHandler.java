package com.kynsoft.notification.application.query.mailjetConfiguration.getById;

import com.kynsof.share.core.domain.bus.query.IQueryHandler;
import com.kynsoft.notification.domain.dto.MailjetConfigurationDto;
import com.kynsoft.notification.domain.service.IMailjetConfigurationService;
import org.springframework.stereotype.Component;

@Component
public class FindMailjetConfigurationByIdQueryHandler implements IQueryHandler<FindMailjetConfigurationByIdQuery, MailjetConfigurationResponse>  {

    private final IMailjetConfigurationService serviceImpl;

    public FindMailjetConfigurationByIdQueryHandler(IMailjetConfigurationService serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    @Override
    public MailjetConfigurationResponse handle(FindMailjetConfigurationByIdQuery query) {
        MailjetConfigurationDto mailjetConfigurationDto = serviceImpl.findById(query.getId());
        return new MailjetConfigurationResponse(mailjetConfigurationDto);
    }
}
