package com.cc.campuscrush.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * LoveAlbum实体类
 * &lt;p&gt;核心功能：情侣相册照片存储，记录照片名称和访问地址&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣用户在恋爱空间上传和浏览共享相册照片，被LoveAlbumController、LoveAlbumService调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@Data
public class LoveAlbum {
    private Long id;
    private Long userId;
    private String photoName;
    private String photoUrl;
    private LocalDateTime uploadTime;
}
