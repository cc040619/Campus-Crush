package com.cc.campuscrush.service;

import com.cc.campuscrush.entity.LoveCoupleProfile;

/**
 * 【LoveCoupleProfileService】服务层接口
 * &lt;p&gt;核心功能：提供情侣档案的查询、创建和更新功能&lt;/p&gt;
 * &lt;p&gt;使用场景：适用于情侣空间档案资料管理场景，被LoveCoupleProfileController等类调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public interface LoveCoupleProfileService {

    /**
     * 根据情侣空间ID查询情侣档案
     * 业务逻辑：查询情侣档案表 → 返回包含双方信息、恋爱天数、纪念日等完整档案数据
     * 异常场景：情侣空间不存在或档案未创建时返回null
     *
     * @param coupleId 情侣空间ID（必填）
     * @return 情侣档案实体，不存在时返回null
     */
    LoveCoupleProfile getByCoupleId(Long coupleId);

    /**
     * 保存或更新情侣档案
     * 业务逻辑：判断档案记录是否存在 → 存在则更新档案信息，不存在则新增档案记录
     * 异常场景：coupleId无效时操作失败
     *
     * @param coupleId 情侣空间ID（必填）
     * @param profile  情侣档案实体（必填，包含双方用户信息、恋爱日期等）
     */
    void saveOrUpdate(Long coupleId, LoveCoupleProfile profile);
}
