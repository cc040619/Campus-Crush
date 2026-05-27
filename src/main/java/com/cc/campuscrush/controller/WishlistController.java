package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveCoupleProfile;
import com.cc.campuscrush.mapper.LoveCoupleProfileMapper;
import com.cc.campuscrush.service.LoveWishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * WishlistController控制器
 * &lt;p&gt;核心功能：情侣心愿清单的查看和管理&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间心愿清单模块，查询心愿列表详情，支持新增、编辑、完成心愿等多种操作，需绑定情侣关系后使用，被前端心愿清单页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin
public class WishlistController {

    @Autowired
    private LoveWishlistService wishlistService;

    @Autowired
    private LoveCoupleProfileMapper profileMapper;

    private Long getCoupleId(Long userId) {
        if (userId == null) return null;
        LoveCoupleProfile profile = profileMapper.findByUserId(userId);
        return profile != null ? profile.getCoupleId() : null;
    }

    /**
     * 查询当前用户所在情侣的心愿清单信息
     * 业务逻辑：从请求头获取userId → 查询用户绑定的情侣ID → 委托wishlistService查询该情侣对的心愿数据 → 返回心愿清单
     * 异常场景：未绑定情侣关系返回"请先在设置中绑定情侣关系"错误
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为Map，包含心愿清单列表及完成状态等详细信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getInfo(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");
        return Result.success(wishlistService.getInfo(coupleId));
    }

    /**
     * 更新心愿清单（支持新增、编辑、完成等多种操作，由action参数区分）
     * 业务逻辑：从请求头获取userId → 查询用户绑定的情侣ID → 提取action、wishId、title、description等参数 → 委托wishlistService根据action执行对应操作 → 返回操作结果
     * 异常场景：未绑定情侣关系返回"请先在设置中绑定情侣关系"错误
     *
     * @param body 请求体，包含action（操作类型，必填，如"add"/"edit"/"complete"/"delete"）、wishId（心愿ID，编辑/完成时必填）、title（心愿标题，新增时必填）、description（心愿描述，可选）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为Map，包含更新后的心愿清单数据
     */
    @PostMapping("/update")
    public Result<Map<String, Object>> update(@RequestBody Map<String, Object> body,
                                               @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long coupleId = getCoupleId(userId);
        if (coupleId == null) return Result.error("请先在设置中绑定情侣关系");
        String action = (String) body.get("action");
        Long wishId = body.get("wishId") != null ? Long.valueOf(body.get("wishId").toString()) : null;
        String title = (String) body.get("title");
        String description = (String) body.get("description");
        Map<String, Object> result = wishlistService.update(coupleId, userId, action, wishId, title, description);
        return Result.success(result);
    }
}
