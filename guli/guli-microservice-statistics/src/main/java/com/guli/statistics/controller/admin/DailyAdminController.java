package com.guli.statistics.controller.admin;

import com.guli.common.vo.R;
import com.guli.statistics.service.DailyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/admin/statistics/daily")
public class DailyAdminController {

    @Autowired
    private DailyService dailyService;

    @GetMapping("{day}")
    public R createStatisticsByDate(@PathVariable String day){
        dailyService.createStatisticsByDay(day);
        return R.ok();
    }

    @GetMapping("show-chart/{begin}/{end}/{type}")
    public R showChart(
            @PathVariable String begin,
            @PathVariable String end,
            @PathVariable String type
    ){
        Map<String, Object> map = dailyService.getChartData(begin, end, type);
        return R.ok().data(map);
    }

}
