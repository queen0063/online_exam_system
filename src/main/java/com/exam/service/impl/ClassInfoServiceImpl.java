package com.exam.service.impl;

import com.exam.entity.ClassInfo;
import com.exam.mapper.ClassInfoMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.ClassInfoService;
import com.exam.vo.classinfo.ClassInfoVO;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 班级服务实现。
 */
@Service
public class ClassInfoServiceImpl implements ClassInfoService {

    private final ClassInfoMapper classInfoMapper;

    public ClassInfoServiceImpl(ClassInfoMapper classInfoMapper) {
        this.classInfoMapper = classInfoMapper;
    }

    @Override
    public List<ClassInfoVO> listVisible() {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        return classInfoMapper.selectVisible(SecurityContextUtils.getUserId(), admin).stream()
                .map(this::toVO)
                .toList();
    }

    private ClassInfoVO toVO(ClassInfo classInfo) {
        return ClassInfoVO.builder()
                .id(classInfo.getId())
                .classCode(classInfo.getClassCode())
                .className(classInfo.getClassName())
                .gradeName(classInfo.getGradeName())
                .teacherId(classInfo.getTeacherId())
                .status(classInfo.getStatus())
                .createTime(classInfo.getCreateTime())
                .build();
    }
}
