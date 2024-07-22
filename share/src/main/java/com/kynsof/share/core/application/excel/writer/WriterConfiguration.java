package com.kynsof.share.core.application.excel.writer;

import com.kynsof.share.core.domain.EWorkbookFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class WriterConfiguration<T> {
    private List<String> sheetsNames;
    private List<T> rows;
    private EWorkbookFormat eWorkbookFormat;
    private Class<T> type;


    public static class WriterConfigurationBuilder<T>{
        private List<String> sheetsNames;
        private List<T> rows;
        private EWorkbookFormat eWorkbookFormat;
        private Class<T> type;

        public WriterConfigurationBuilder<T> withType(Class<T> type){
            this.type=type;
            return this;
        }
        public WriterConfigurationBuilder<T> withSheetNames(List<String> sheetsNames){
            this.sheetsNames=sheetsNames;
            return this;
        }
        public WriterConfigurationBuilder<T> withRows(List<T> rows){
            this.rows=rows;
            return this;
        }
        public WriterConfigurationBuilder<T> withWorkbookFormat(EWorkbookFormat workbookFormat){
            this.eWorkbookFormat=workbookFormat;
            return this;
        }

        public WriterConfiguration<T> build(){
            return new WriterConfiguration<>(this.sheetsNames, this.rows, this.eWorkbookFormat, this.type);
        }

    }
}
