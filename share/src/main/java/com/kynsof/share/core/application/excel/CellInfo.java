package com.kynsof.share.core.application.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellType;
@Getter
@AllArgsConstructor
public class CellInfo {
    private int position;
    private CustomCellType cellType;
    private String headerName;
}
