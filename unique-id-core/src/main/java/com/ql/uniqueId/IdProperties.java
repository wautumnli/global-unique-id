package com.ql.uniqueId;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * @author wanqiuli
 * @date 2022/7/1 14:51
 */
@ConfigurationProperties(prefix = IdProperties.STEP)
public class IdProperties {

    public static final String STEP = "unique.id";

    private int step;

    private Map<String, Integer> tableStep;

    public Map<String, Integer> getTableStep() {
        return tableStep;
    }

    public void setTableStep(Map<String, Integer> tableStep) {
        this.tableStep = tableStep;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}
