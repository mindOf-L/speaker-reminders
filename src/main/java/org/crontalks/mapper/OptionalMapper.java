package org.crontalks.mapper;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class OptionalMapper {

    public static String mapToString(Object value) {
        return Optional.ofNullable(value).map(Object::toString).orElse(StringUtils.EMPTY);
    }

    public static int mapToInt(Object value) {
        return Optional.ofNullable(value).map(Object::toString).map(Integer::parseInt).orElse(0);
    }
}
