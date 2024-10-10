package com.kynsoft.finamer.audit.domain.elastic;

import co.elastic.clients.elasticsearch._types.analysis.Analyzer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Document(indexName = "audit")
public class Audit {
    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String entityName;

    @Field(type = FieldType.Keyword)
    private String action;

    @Field(type = FieldType.Keyword)
    private String username;

    @Field(type=FieldType.Text)
    private String data;

    @Field(type = FieldType.Keyword)
    private LocalDateTime created;

    @Field(type= FieldType.Keyword)
    private String tag;
}
