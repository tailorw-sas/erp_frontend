package com.kynsof.share.core.domain;

import com.kynsof.share.core.domain.bus.query.IResponse;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessagePaginatedResponse implements IResponse, Serializable {

    private String timestamp;

    private final int status = 200;

    private String message;

    private String errors;

    private List<?> data;

    private Integer totalPages;

    private Long totalElements;

    private Integer pageElements;

    private Integer pageLimit;

    private Integer pageNumber;

    public MessagePaginatedResponse(String message) {
        this.message = message;
        this.data = null;
    }

    public MessagePaginatedResponse(String message, List<?> data) {
        this.message = message;
        this.data = data;
    }

    public MessagePaginatedResponse(String message, List<?> data, Integer totalPages,
                                    Integer pageElements, Long totalElements, Integer pageLimit, Integer pageNumber) {

        this.timestamp = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss '['z']'")
                .format(ZonedDateTime.now(ZoneId.of("America/Guayaquil")));
        this.message = message;
        this.errors = null;
        this.data = data;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageElements = pageElements;
        this.pageLimit = pageLimit;
        this.pageNumber = pageNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String error) {
        this.errors = error;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getPageElements() {
        return pageElements;
    }

    public void setPageElements(Integer pageElements) {
        this.pageElements = pageElements;
    }

    public Integer getPageLimit() {
        return pageLimit;
    }

    public void setPageLimit(Integer pageLimit) {
        this.pageLimit = pageLimit;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getStatus() {
        return status;
    }
}
