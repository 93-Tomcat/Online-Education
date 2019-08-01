package com.guli.aliyunoss.controller;

import com.aliyuncs.utils.StringUtils;
import com.guli.aliyunoss.service.FileService;
import com.guli.aliyunoss.util.ConstantPropertiesUtil;
import com.guli.common.vo.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;

@Api(description = "阿里云oss文件管理")
@CrossOrigin
@RestController
@RequestMapping("/admin/oss/file")
public class FileController {

    @Autowired
    private FileService fileService;



    /**
     * 文件上传
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("upload")
    @CrossOrigin
    public R upload(

            @ApiParam(name = "file" , value = "文件",required = true)
            @RequestParam(value = "file")MultipartFile file,

            @ApiParam(name = "host",value = "文件上传路径",required = false)
            @RequestParam(value = "host",required = false)String host) throws IOException {

        if (!StringUtils.isEmpty(host)){
            ConstantPropertiesUtil.FILE_HOST = host;
        }

        String uploadUrl = fileService.upload(file);
        //返回r对象
        return R.ok().message("文件上传成功").data("url", uploadUrl);

    }
}
