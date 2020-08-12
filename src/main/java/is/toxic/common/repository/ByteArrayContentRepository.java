package is.toxic.common.repository;

import io.vavr.Tuple;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Описание хранилища ByteArray
 */
@Repository
public interface ByteArrayContentRepository {
    ConcurrentMap<Tuple, byte[]> BYTE_ARRAY_CONCURRENT_MAP = new ConcurrentHashMap<>();

    byte[] getByteArray(Tuple name);

    Collection<byte[]> getAllByteArrays();

    void save(String requestName, String threadName, byte[] value);
}
