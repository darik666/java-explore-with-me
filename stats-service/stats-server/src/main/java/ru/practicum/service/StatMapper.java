package ru.practicum.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.Stat;

/**
 * Маппер модели статистики
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatMapper {

    public static Stat toStat(EndpointHitDto endpointHitDto) {
        Stat stat = new Stat();
        stat.setApp(endpointHitDto.getApp());
        stat.setUri(endpointHitDto.getUri());
        stat.setIp(endpointHitDto.getIp());
        stat.setTimestamp(endpointHitDto.getTimestamp());
        return stat;
    }
}
