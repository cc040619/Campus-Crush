package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.utils.AliyunOSSOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * UploadController控制器
 * &lt;p&gt;核心功能：通用图片上传至阿里云OSS并返回访问URL&lt;/p&gt;
 * &lt;p&gt;使用场景：通用文件上传服务，直接将文件上传到OSS并返回URL，不写入任何业务表，被前端各业务模块的上传功能调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class UploadController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    /**
     * 上传图片到阿里云OSS并返回访问URL（需登录，不写入业务表）
     * 业务逻辑：校验登录状态 → 校验文件非空 → 读取文件字节内容 → 委托aliyunOSSOperator上传到OSS → 返回包含URL的Map
     * 异常场景：未登录返回"未登录"错误；文件为空返回"请选择图片文件"错误；上传异常返回"上传失败：错误信息"
     *
     * @param file 图片文件（请求参数，必填，表单字段名"file"）
     * @param userId 当前用户ID（从X-User-Id请求头获取，可选，用于校验登录状态）
     * @return Result.data 包含url字段（OSS上图片的访问URL）
     */
    @PostMapping("/image")
    public Result<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {

        if (userId == null) {
            return Result.error("未登录");
        }
        if (file == null || file.isEmpty()) {
            return Result.error("请选择图片文件");
        }

        try {
            byte[] content = file.getBytes();
            String ossUrl = aliyunOSSOperator.upload(content, file.getOriginalFilename());

            Map<String, String> data = new HashMap<>();
            data.put("url", ossUrl);
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败：" + e.getMessage());
        }
    }
}
