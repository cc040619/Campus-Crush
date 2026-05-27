package com.cc.campuscrush.service.impl;

import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.mapper.LoveCoupleProfileMapper;
import com.cc.campuscrush.service.LoveCoupleProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 【LoveCoupleProfileServiceImpl】情侣档案服务层实现
 * &lt;p&gt;核心功能：情侣个人档案信息的查询与保存更新&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间中个人信息和恋爱起始日期的设置与展示，被 LoveCoupleProfileController 调用，支持按情侣ID查询档案、按用户ID去重保存（存在则更新否则插入），存储恋爱起始日期用于计算相恋天数&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Service
public class LoveCoupleProfileServiceImpl implements LoveCoupleProfileService {

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    /**
     * 根据情侣ID查询情侣档案（被LoveCoupleProfileController调用）
     * 业务逻辑：按coupleId查love_couple_profile表
     * 异常场景：无记录时返回null
     *
     * @param coupleId 情侣关系ID（必填）
     * @return 情侣档案实体，无记录返回null
     */
    @Override
    public LoveCoupleProfile getByCoupleId(Long coupleId) {
        return profileMapper.findByCoupleId(coupleId);
    }

    /**
     * 保存或更新情侣档案（按userId去重，被LoveCoupleProfileController调用）
     * 业务逻辑：设置profile的coupleId → 按userId查是否已有档案 → 存在则用已有ID执行update → 不存在则insert
     * 异常场景：数据库操作失败由MyBatis抛出异常
     *
     * @param coupleId 情侣关系ID（必填）
     * @param profile  情侣档案实体（必填，需含userId）
     */
    @Override
    public void saveOrUpdate(Long coupleId, LoveCoupleProfile profile) {
        profile.setCoupleId(coupleId);
        // 按 user_id 查重（双方共享couple_id时，按user_id区分记录）
        LoveCoupleProfile existing = profileMapper.findByUserId(profile.getUserId());
        if (existing != null) {
            profile.setId(existing.getId());
            profileMapper.update(profile);
        } else {
            profileMapper.insert(profile);
        }
    }
}
