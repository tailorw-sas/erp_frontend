package com.kynsof.share.utils;

import org.apache.commons.io.FilenameUtils;

public class FileNameGetExtension {

    public static String getExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }
}
