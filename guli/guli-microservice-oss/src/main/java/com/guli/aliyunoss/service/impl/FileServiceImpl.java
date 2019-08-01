package com.guli.aliyunoss.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.guli.aliyunoss.service.FileService;
import com.guli.aliyunoss.util.ConstantPropertiesUtil;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {



    @Override
    public String upload(MultipartFile file) throws IOException {

        //获取阿里云存储相关常量
        String endPoint = ConstantPropertiesUtil.END_POINT;
        String accessKeyId = ConstantPropertiesUtil.ACCESS_KEY_ID;
        String accessKeySecret = ConstantPropertiesUtil.ACCESS_KEY_SECRET;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;
        String fileHost = ConstantPropertiesUtil.FILE_HOST;

        InputStream inputStream = file.getInputStream();

        //判断oss实例是否存在：如果不存在则创建，如果存在则获取
        OSS ossClient = new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
        if (!ossClient.doesBucketExist(bucketName)) {
            //创建bucket
            ossClient.createBucket(bucketName);
            //设置oss实例的访问权限：公共读
            ossClient.setBucketAcl(bucketName, CannedAccessControlList.PublicRead);
        }

        String filePath = new DateTime().toString("yyyy/MM/dd");

        //文件原始名
        String originalFilename = file.getOriginalFilename();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileNanme = UUID.randomUUID().toString() + fileType;

        String fileUrl =fileHost +"/"+ filePath +"/"+ fileNanme;

        ossClient.putObject(bucketName, fileUrl, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();

        String uploadUrl = "https://"+ bucketName + "." + endPoint + "/" + fileUrl;

        return uploadUrl;
    }



}
