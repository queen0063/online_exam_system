package com.exam.vo.auth;

import lombok.Builder;
import lombok.Data;

/**
 * 学生专属注册链接信息。
 */
@Data
@Builder
public class RegisterInviteVO {

    private String inviteCode;
    private Long classId;
    private String className;
    private String gradeName;
    private Long teacherId;
    private String teacherName;
    private String registerUrl;
}
