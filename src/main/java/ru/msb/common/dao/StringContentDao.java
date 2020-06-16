package ru.msb.common.dao;

import io.vavr.Tuple;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public interface StringContentDao {

    ConcurrentMap<Tuple, String> STRING_CONCURRENT_MAP = new ConcurrentHashMap<>();

    String getString(Tuple name);
    Collection<String> getAllStrings();
    Tuple save(String valueType, String value);
}
