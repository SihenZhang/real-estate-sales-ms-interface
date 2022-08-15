package com.sihenzhang.realestate.controller;

import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.sihenzhang.realestate.result.Result;
import com.sihenzhang.realestate.result.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/images")
public class ImageController {
    @Value("${image.path}")
    private String imagePath;

    @PostMapping
    public Result uploadImage(@RequestParam MultipartFile image) {
        if (image.isEmpty()) {
            return Result.buildFail("上传图片失败，图片不能为空");
        }
        // MIME类型判断（MIME类型根据请求头来获取，可以伪造且可能为空，因此并不可靠）
        if (image.getContentType() != null && !StrUtil.equalsAny(image.getContentType(), MimeTypeUtils.IMAGE_JPEG_VALUE, MimeTypeUtils.IMAGE_PNG_VALUE)) {
            return Result.buildFail(StrUtil.format("上传失败，图片MIME类型错误，只支持jpg({})和png({})", MimeTypeUtils.IMAGE_JPEG_VALUE, MimeTypeUtils.IMAGE_PNG_VALUE));
        }
        try {
            // 获取图片输入流
            InputStream imageInputStream = image.getInputStream();
            // 复制一个可复用的输入流
            FastByteArrayOutputStream tmpOutputStream = IoUtil.read(imageInputStream);
            ByteArrayInputStream markSupportedImageInputStream = new ByteArrayInputStream(tmpOutputStream.toByteArray());
            // 获取文件类型（通过读取输入流的前28个字节进行类型判断，对于图片类型而言较为可靠）
            String imageType = FileTypeUtil.getType(markSupportedImageInputStream);
            if (imageType == null || !StrUtil.equalsAny(imageType, "jpg", "png")) {
                return Result.buildFail("上传图片失败，图片类型错误，只支持jpg和png");
            }
            // 由于获取文件类型读取了输入流的前28个字节，因此需要重置输入流
            markSupportedImageInputStream.reset();
            // 获取图片保存路径
            Path imageDirectoryPath = Paths.get(imagePath);
            // 图片使用UUID作为文件名，避免文件重复
            Path imageFilePath = imageDirectoryPath.resolve(IdUtil.fastSimpleUUID() + StrUtil.DOT + imageType);
            // 将图片保存到本地
            File imageFile = imageFilePath.toFile();
            FileWriter.create(imageFile).writeFromStream(markSupportedImageInputStream);
            String path = imageFile.getPath().replace(StrUtil.BACKSLASH, StrUtil.SLASH);
            return Result.build(ResultCode.CREATED, "上传图片成功", Dict.of("path", path));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }
}
