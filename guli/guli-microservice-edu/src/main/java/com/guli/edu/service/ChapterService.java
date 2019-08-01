package com.guli.edu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guli.edu.entity.Chapter;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guli.edu.entity.Course;
import com.guli.edu.vo.ChapterVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程 服务类
 * </p>
 *
 * @author Helen
 * @since 2019-07-12
 */
public interface ChapterService extends IService<Chapter> {

    void removeChapterById(String id);

    List<ChapterVo> nestedList(String courseId);


}
