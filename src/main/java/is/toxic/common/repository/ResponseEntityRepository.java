package is.toxic.common.repository;

import io.vavr.Tuple;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Описание хранилища ResponseEntity
 */
@Repository
public interface ResponseEntityRepository {
    ConcurrentMap<Tuple, ResponseEntity<String>> RESPONSE_ENTITY_CONCURRENT_MAP = new ConcurrentHashMap<>();

    ResponseEntity<String> getResponseEntity(Tuple key);

    Collection<ResponseEntity<String>> getAllResponseEntities();

    void save(ResponseEntity<String> responseEntity, String requestName);

}
