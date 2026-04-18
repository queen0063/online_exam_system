package com.exam.service;

import com.exam.dto.role.RoleSaveDTO;
import com.exam.vo.user.RoleVO;
import java.util.List;

public interface RoleService {

    List<RoleVO> listAll();

    RoleVO detail(Long id);

    void save(RoleSaveDTO roleSaveDTO);

    void remove(Long id);
}
