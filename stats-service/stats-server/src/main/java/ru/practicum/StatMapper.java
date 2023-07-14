package ru.practicum;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
