package com.exam.controller;

import com.exam.aspect.OperationLog;
import com.exam.common.enums.OperationTypeEnum;
import com.exam.common.result.PageResult;
import com.exam.common.result.Result;
import com.exam.dto.user.ChangePasswordDTO;
import com.exam.dto.user.ResetPasswordDTO;
import com.exam.dto.user.UserQueryDTO;
import com.exam.dto.user.UserSaveDTO;
import com.exam.service.UserService;
import com.exam.vo.user.UserVO;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口。
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<UserVO>> page(UserQueryDTO queryDTO) {
        return Result.success(userService.page(queryDTO));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<UserVO> detail(@PathVariable Long id) {
        return Result.success(userService.detail(id));
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public Result<List<UserVO>> students(UserQueryDTO queryDTO) {
        return Result.success(userService.students(queryDTO));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "用户管理", description = "保存用户", operationType = OperationTypeEnum.CREATE)
    public Result<Void> save(@Valid @RequestBody UserSaveDTO userSaveDTO) {
        userService.save(userSaveDTO);
        return Result.success("保存成功", null);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "用户管理", description = "删除用户", operationType = OperationTypeEnum.DELETE)
    public Result<Void> remove(@PathVariable Long id) {
        userService.remove(id);
        return Result.success("删除成功", null);
    }

    @PutMapping("/change-password")
    @OperationLog(module = "用户管理", description = "修改密码", operationType = OperationTypeEnum.UPDATE)
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(changePasswordDTO);
        return Result.success("密码修改成功", null);
    }

    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @OperationLog(module = "用户管理", description = "重置密码", operationType = OperationTypeEnum.UPDATE)
    public Result<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        userService.resetPassword(id, resetPasswordDTO);
        return Result.success("密码重置成功", null);
    }
}
