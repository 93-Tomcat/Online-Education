package com.guli.ucenter.service;

import com.guli.ucenter.entity.Member;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 会员表 服务类
 * </p>
 *
 * @author 93
 * @since 2019-07-21
 */
public interface MemberService extends IService<Member> {

    Integer countRegisterByDay(String day);

    /**
     * 根据openid返回用户信息
     * @param openid
     * @return
     */
    Member getByOpenid(String openid);
}
