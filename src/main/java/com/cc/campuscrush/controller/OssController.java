package com.cc.campuscrush.controller;

import com.cc.campuscrush.common.Result;
import com.cc.campuscrush.utils.AliyunOSSOperator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * OssController控制器
 * &lt;p&gt;核心功能：阿里云OSS文件上传&lt;/p&gt;
 * &lt;p&gt;使用场景：通用文件上传服务，支持JPG/PNG图片上传至阿里云OSS并返回访问URL，校验文件类型和大小限制，被前端各模块的文件上传功能调用&lt;/p&gt;
 *
 * @author zcongcong
 * @date 2026-05-27
 */
@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
public class OssController {

    @Autowired
    private AliyunOSSOperator aliyunOSSOperator;

    /**
     * 上传图片文件至阿里云OSS并返回访问URL（无需登录，公开接口）
     * 业务逻辑：校验文件非空 → 校验文件类型为jpg/jpeg/png → 校验文件大小不超过10MB → 读取文件字节内容 → 委托aliyunOSSOperator上传到OSS → 返回图片访问URL
     * 异常场景：文件为空返回"请选择要上传的文件"；文件类型不匹配返回"仅支持jpg、jpeg和png格式的图片"；文件超过10MB返回"文件大小不能超过10MB"；IO异常返回"文件上传失败"；其他异常返回"图片上传失败: 错误信息"
     *
     * @param file 图片文件（请求参数，必填，表单字段名"file"，仅支持jpg/jpeg/png格式，最大10MB）
     * @return Result.data 为OSS上图片的访问URL字符串
     */
    @PostMapping("/upload")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("请选择要上传的文件");
            }

            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (!originalFilename.endsWith(".jpg") && !originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpeg")) {
                return Result.error("仅支持jpg、jpeg和png格式的图片");
            }

            // 检查文件大小（10MB）
            if (file.getSize() > 10 * 1024 * 1024) {
                return Result.error("文件大小不能超过10MB");
            }

            // 使用阿里云OSS上传文件
            byte[] fileContent = file.getBytes();
            String imageUrl = aliyunOSSOperator.upload(fileContent, originalFilename);

            return Result.success(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}
