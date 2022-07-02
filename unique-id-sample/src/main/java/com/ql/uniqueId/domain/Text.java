package com.ql.uniqueId.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author wanqiuli
 * @date 2022/7/2 12:14
 */
@Data
@TableName(value = "bas_text")
public class Text {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String text;
}
