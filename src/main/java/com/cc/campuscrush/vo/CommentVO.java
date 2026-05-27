package com.cc.campuscrush.vo;

import com.cc.campuscrush.entity.Comment;

import java.util.List;

/**
 * 【CommentVO】视图对象
 * &lt;p&gt;核心功能：评论视图对象，继承Comment实体并扩展用户昵称userName、头像avatar和子评论列表children三个展示字段&lt;/p&gt;
 * &lt;p&gt;使用场景：用于前端评论列表展示，支持树形评论结构（通过children字段递归嵌套子评论），在评论查询Service中组装后返回给前端渲染评论楼层&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
public class CommentVO extends Comment {
    private String userName;
    private String avatar;
    private List<CommentVO> children;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<CommentVO> getChildren() {
        return children;
    }

    public void setChildren(List<CommentVO> children) {
        this.children = children;
    }
}
