package com.kynsof.share.core.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RowErrorField {

    private Integer rowNumber;
    private List<ErrorField> errorFieldList;
}
