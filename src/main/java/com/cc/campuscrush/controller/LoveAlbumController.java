package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.entity.LoveAlbum;
import com.cc.campuscrush.service.LoveAlbumService;
import com.cc.campuscrush.utils.AliyunOSSOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * LoveAlbumController控制器
 * &lt;p&gt;核心功能：情侣相册照片的上传和管理&lt;/p&gt;
 * &lt;p&gt;使用场景：情侣空间相册模块，支持照片列表查询、单张照片详情、上传新照片至阿里云OSS、更新照片和删除照片，被前端情侣相册页面调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/album")
@CrossOrigin
public class LoveAlbumController {

    @Autowired
    private LoveAlbumService albumService;

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    private Long getCurrentUserId(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            throw new RuntimeException("未登录");
        }
        return userId;
    }

    /**
     * 查询当前用户的所有相册照片列表
     * 业务逻辑：从请求头获取userId → 委托albumService查询该用户的全部照片 → 返回照片列表
     * 异常场景：未登录时getCurrentUserId抛出RuntimeException；无照片时返回空列表
     *
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为LoveAlbum列表，无照片时为空数组
     */
    @GetMapping
    public Result<List<LoveAlbum>> list(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        return Result.success(albumService.findAllByUserId(currentUserId));
    }

    /**
     * 根据ID查询单张相册照片详情（含权限校验）
     * 业务逻辑：从请求头获取userId → 委托albumService按id和userId查询 → 判断是否有权访问 → 返回照片详情
     * 异常场景：未登录抛出RuntimeException；照片不存在或不属于当前用户返回"数据不存在或无权限访问"错误
     *
     * @param id 相册照片ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为LoveAlbum对象；无权限时返回error
     */
    @GetMapping("/{id}")
    public Result<LoveAlbum> getById(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        LoveAlbum album = albumService.findByIdAndUserId(id, currentUserId);
        if (album == null) {
            return Result.error("数据不存在或无权限访问");
        }
        return Result.success(album);
    }

    /**
     * 上传一张新照片到情侣相册（文件上传至OSS并写入数据库）
     * 业务逻辑：从请求头获取userId → 校验文件非空 → 将文件上传至阿里云OSS获取URL → 创建LoveAlbum对象 → 委托albumService写入数据库 → 返回创建后的相册对象
     * 异常场景：未登录抛出RuntimeException；文件为空返回"请选择图片文件"错误；上传失败时返回"上传失败：错误信息"
     *
     * @param file 图片文件（请求参数，必填，表单字段名"file"）
     * @param photoName 照片名称（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选）
     * @return Result.data 为新创建的LoveAlbum对象，包含id、photoUrl等字段；失败时返回error
     */
    @PostMapping
    public Result<LoveAlbum> add(@RequestParam("file") MultipartFile file,
                           @RequestParam("photoName") String photoName,
                           @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择图片文件");
            }

            Long currentUserId = getCurrentUserId(userId);

            // 上传文件到OSS
            byte[] content = file.getBytes();
            String ossUrl = aliyunOSSOperator.upload(content, file.getOriginalFilename());

            // 创建相册对象
            LoveAlbum album = new LoveAlbum();
            album.setUserId(currentUserId);
            album.setPhotoName(photoName);
            album.setPhotoUrl(ossUrl);

            albumService.insert(album);
            return Result.success(album);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 更新指定相册照片（重新上传文件并更新数据库记录）
     * 业务逻辑：从请求头获取userId → 校验文件非空 → 上传新文件至OSS获取URL → 构建更新的LoveAlbum对象 → 委托albumService更新 → 校验update返回值
     * 异常场景：未登录抛出RuntimeException；文件为空返回"请选择图片文件"错误；记录不存在或无权限时返回"数据不存在或无权限修改"错误；上传失败时返回"上传失败：错误信息"
     *
     * @param id 相册照片ID（路径参数，必填）
     * @param file 新的图片文件（请求参数，必填，表单字段名"file"）
     * @param photoName 新的照片名称（请求参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为null，无返回数据；失败时返回error
     */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              @RequestParam("photoName") String photoName,
                              @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择图片文件");
            }

            Long currentUserId = getCurrentUserId(userId);

            // 上传文件到OSS
            byte[] content = file.getBytes();
            String ossUrl = aliyunOSSOperator.upload(content, file.getOriginalFilename());

            // 创建相册对象
            LoveAlbum album = new LoveAlbum();
            album.setId(id);
            album.setUserId(currentUserId);
            album.setPhotoName(photoName);
            album.setPhotoUrl(ossUrl);

            int result = albumService.update(album);
            if (result == 0) {
                return Result.error("数据不存在或无权限修改");
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定相册照片（含权限校验）
     * 业务逻辑：从请求头获取userId → 委托albumService按id和userId删除 → 校验delete返回值 → 返回成功
     * 异常场景：未登录抛出RuntimeException；记录不存在或无权限时返回"数据不存在或无权限删除"错误
     *
     * @param id 相册照片ID（路径参数，必填）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于权限校验）
     * @return Result.data 为null，无返回数据；无权限时返回error
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long currentUserId = getCurrentUserId(userId);
        int result = albumService.deleteByIdAndUserId(id, currentUserId);
        if (result == 0) {
            return Result.error("数据不存在或无权限删除");
        }
        return Result.success();
    }
}
