package com.kynsof.share.core.application.excel;

import lombok.Data;
import java.io.InputStream;

@Data
public class ReaderConfiguration {

    private InputStream inputStream;

    private boolean readLastActiveSheet;

    private boolean ignoreHeaders;

    private String sheetNameToRead;

    private boolean strictHeaderOrder;

}
