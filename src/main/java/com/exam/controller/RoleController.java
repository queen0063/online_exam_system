package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.Result;
import com.exam.dto.role.RoleSaveDTO;
import com.exam.service.RoleService;
import com.exam.vo.user.RoleVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色接口。
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<RoleVO>> list() {
        return Result.success(roleService.listAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<RoleVO> detail(@PathVariable Long id) {
        return Result.success(roleService.detail(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "角色管理", description = "保存角色", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody RoleSaveDTO roleSaveDTO) {
        roleService.save(roleSaveDTO);
        return Result.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "角色管理", description = "删除角色", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        roleService.remove(id);
        return Result.success("删除成功", null);
    }
}
