package com.exam.dto.common;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页查询参数。
 */
@Data
public class PageQueryDTO {

    @Min(value = 1, message = "页码必须大于等于 1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页数量必须大于等于 1")
    private Integer pageSize = 10;
}
