package io.micronaut.data.jdbc.postgres;

import io.micronaut.data.jdbc.BasicTypes;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

@JdbcRepository(dialect = Dialect.POSTGRES)
public interface PostgresBasicTypesRepository extends CrudRepository<BasicTypes, Long> {
}
