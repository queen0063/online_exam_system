package com.exam.service;

import com.exam.common.result.PageResult;
import com.exam.dto.user.ChangePasswordDTO;
import com.exam.dto.user.ResetPasswordDTO;
import com.exam.dto.user.UserQueryDTO;
import com.exam.dto.user.UserSaveDTO;
import com.exam.vo.user.UserVO;
import java.util.List;

public interface UserService {

    PageResult<UserVO> page(UserQueryDTO queryDTO);

    UserVO detail(Long id);

    List<UserVO> students(UserQueryDTO queryDTO);

    void save(UserSaveDTO userSaveDTO);

    void remove(Long id);

    void changePassword(ChangePasswordDTO changePasswordDTO);

    void resetPassword(Long userId, ResetPasswordDTO resetPasswordDTO);
}
