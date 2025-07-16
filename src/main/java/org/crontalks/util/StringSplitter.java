package org.crontalks.util;

import org.apache.commons.lang3.StringUtils;

public class StringSplitter {

    public static String splitName(String name) {
        if(StringUtils.isBlank(name))
            return StringUtils.EMPTY;

        return name.split(" ")[0];
    }
}
