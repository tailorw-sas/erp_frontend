package com.kynsof.share.core.domain.http.entity.income;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateAntiToIncomeRequest {

    private List<CreateIncomeRequest> createIncomeRequests;
}
