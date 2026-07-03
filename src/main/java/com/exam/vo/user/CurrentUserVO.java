package com.exam.vo.user;

import com.exam.vo.classinfo.ClassInfoVO;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * 当前登录用户返回对象。
 */
@Data
@Builder
public class CurrentUserVO {

    private Long userId;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private List<String> roleCodes;

    // 学生专属字段
    private String studentNo;
    private Long classId;
    private String className;
    private String gradeName;

    // 教师专属字段
    private List<ClassInfoVO> teacherClasses;
}
