package com.ql.uniqueId.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * @author wanqiuli
 * @date 2022/7/1 22:34
 */
@Data
@ToString
@TableName(value = "bas_info")
public class Info {
    @TableId(type = IdType.INPUT)
    private Long id;
    private String name;

    public static Info create(long id, String name) {
        Info info = new Info();
        info.setId(id);
        info.setName(name);
        return info;
    }
}
