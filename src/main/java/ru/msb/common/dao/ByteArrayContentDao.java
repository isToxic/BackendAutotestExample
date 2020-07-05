package ru.msb.common.dao;

import io.vavr.Tuple;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public interface ByteArrayContentDao {
    ConcurrentMap<Tuple, byte[]> BYTE_ARRAY_CONCURRENT_MAP = new ConcurrentHashMap<>();

    byte[] getByteArray(Tuple name);
    Collection<byte[]> getAllByteArrays();
    void save(String requestName, String threadName, byte[] value);
}
