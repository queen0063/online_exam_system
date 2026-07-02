package com.exam.service.impl;

import com.exam.common.api.ResultCode;
import com.exam.common.enums.RoleCode;
import com.exam.common.exception.BusinessException;
import com.exam.common.util.CodeGenerateUtils;
import com.exam.dto.classinfo.ClassInfoSaveDTO;
import com.exam.entity.ClassInfo;
import com.exam.entity.SysRole;
import com.exam.mapper.ClassInfoMapper;
import com.exam.mapper.SysRoleMapper;
import com.exam.security.context.SecurityContextUtils;
import com.exam.service.ClassInfoService;
import com.exam.vo.classinfo.ClassInfoVO;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 班级服务实现。
 */
@Service
public class ClassInfoServiceImpl implements ClassInfoService {

    private final ClassInfoMapper classInfoMapper;
    private final SysRoleMapper sysRoleMapper;

    public ClassInfoServiceImpl(ClassInfoMapper classInfoMapper, SysRoleMapper sysRoleMapper) {
        this.classInfoMapper = classInfoMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    @Override
    public List<ClassInfoVO> listAll() {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        List<ClassInfo> classes = admin
                ? classInfoMapper.selectAll()
                : classInfoMapper.selectAll().stream()
                        .filter(classInfo -> SecurityContextUtils.getUserId().equals(classInfo.getTeacherId()))
                        .toList();
        return classes.stream()
                .map(this::toVO)
                .toList();
    }

    @Override
    public List<ClassInfoVO> listVisible() {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        return classInfoMapper.selectVisible(SecurityContextUtils.getUserId(), admin).stream()
                .map(this::toVO)
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ClassInfoSaveDTO classInfoSaveDTO) {
        boolean admin = SecurityContextUtils.hasAnyRole("ADMIN");
        Long teacherId = admin ? classInfoSaveDTO.getTeacherId() : SecurityContextUtils.getUserId();
        validateTeacher(teacherId);
        ClassInfo classInfo = classInfoSaveDTO.getId() == null ? new ClassInfo() : getClassInfo(classInfoSaveDTO.getId());
        if (!admin && classInfo.getId() != null && !SecurityContextUtils.getUserId().equals(classInfo.getTeacherId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能编辑自己负责的班级");
        }
        if (classInfoSaveDTO.getId() == null) {
            classInfo.setClassCode(CodeGenerateUtils.generate("CLS", code -> classInfoMapper.selectByCode(code) != null));
        }
        classInfo.setClassName(classInfoSaveDTO.getClassName());
        classInfo.setGradeName(classInfoSaveDTO.getGradeName());
        classInfo.setTeacherId(teacherId);
        classInfo.setStatus(classInfoSaveDTO.getStatus());
        classInfo.setUpdateTime(LocalDateTime.now());
        if (classInfoSaveDTO.getId() == null) {
            classInfo.setCreateTime(LocalDateTime.now());
            classInfo.setDeleted(0);
            classInfoMapper.insert(classInfo);
        } else {
            classInfoMapper.updateById(classInfo);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void remove(Long id) {
        ClassInfo classInfo = getClassInfo(id);
        if (!SecurityContextUtils.hasAnyRole("ADMIN")
                && !SecurityContextUtils.getUserId().equals(classInfo.getTeacherId())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "教师只能删除自己负责的班级");
        }
        classInfoMapper.logicDeleteById(id);
    }

    private ClassInfo getClassInfo(Long id) {
        ClassInfo classInfo = classInfoMapper.selectById(id);
        if (classInfo == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "班级不存在");
        }
        return classInfo;
    }

    private void validateTeacher(Long teacherId) {
        if (teacherId == null) {
            return;
        }
        boolean isTeacher = sysRoleMapper.selectRolesByUserId(teacherId).stream()
                .map(SysRole::getRoleCode)
                .anyMatch(RoleCode.TEACHER.name()::equals);
        if (!isTeacher) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "负责教师不存在或不是教师角色");
        }
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
