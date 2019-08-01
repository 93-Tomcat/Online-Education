package com.guli.vod.controller;

import com.guli.common.vo.R;
import com.guli.vod.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(description = "阿里云视频点播微服务")
@CrossOrigin //跨域
@RestController
@RequestMapping("/vod/video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("get-play-auth/{videoId}")
    public R getVideoPlayAuth(

            @ApiParam(name = "videoId",value = "视频ID",required = true)
            @PathVariable String videoId){

        //得到视频凭证
        String playAuth = videoService.getVideoPlayAuth(videoId);

        //返回结果
        return R.ok().message("获取凭证成功").data("playAuth",playAuth);
    }
}
