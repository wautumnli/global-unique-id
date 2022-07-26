package com.ql.uniqueId.domain;

import lombok.Data;

/**
 * @author wanqiuli
 * @date 2022/7/26 21:47
 */
@Data
public class SequencePo {
    private Long currentMaxId;
    private Integer step;
}
