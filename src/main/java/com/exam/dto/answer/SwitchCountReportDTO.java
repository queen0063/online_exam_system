package com.exam.dto.answer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 切屏次数上报参数。
 */
@Data
public class SwitchCountReportDTO {

    @NotNull(message = "切屏次数不能为空")
    @Min(value = 0, message = "切屏次数不能小于0")
    private Integer switchCount;
}
