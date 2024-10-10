package com.kynsoft.finamer.audit.infrastructure.repository;

import com.kynsoft.finamer.audit.domain.elastic.Audit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuditElasticsearchRepository extends CrudRepository<Audit, String>,
        PagingAndSortingRepository<Audit,String> {
}
