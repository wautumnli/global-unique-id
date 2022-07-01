package com.ql.uniqueId;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wanqiuli
 * @date 2022/7/1 14:51
 */
@ConfigurationProperties(prefix = IdProperties.STEP)
public class IdProperties {

    public static final String STEP = "unique.id.step";

    private int step;

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
