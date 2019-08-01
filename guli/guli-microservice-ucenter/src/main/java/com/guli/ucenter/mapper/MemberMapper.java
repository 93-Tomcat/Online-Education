package com.guli.ucenter.mapper;

import com.guli.ucenter.entity.Member;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author 93
 * @since 2019-07-21
 */
public interface MemberMapper extends BaseMapper<Member> {

    Integer selectRegisterCount(String day);

}
