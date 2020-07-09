package ru.toxic.common.enums;

/**
 * Параметры стратегии проверки сертификатов при REST взаимодействии
 */
public enum UsedTrustStrategy {
    NOT_USED,
    ACCEPT_ALL,
    TRUST_SELF_SIGNED
}
