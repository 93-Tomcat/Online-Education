package com.guli.edu.controller.admin;


import com.guli.common.constants.ResultCodeEnum;
import com.guli.common.exception.GuliException;
import com.guli.common.vo.R;
import com.guli.edu.service.SubjectService;
import com.guli.edu.vo.SubjectNestedVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 课程类别管理
 * </p>
 *
 * @author Helen
 * @since 2019-07-20
 */
@RestController
@RequestMapping("/admin/edu/subject")
@CrossOrigin //跨域
@Api(description = "课程类别管理")
public class SubjectAdminController {

    @Autowired
    private SubjectService subjectService;

    @PostMapping("import")
    public R batchImport(
            @RequestParam("file") MultipartFile file){

        try {

            List<String> errorMessage = subjectService.batchImport(file);

            if (errorMessage.size() == 0){
                return R.ok().message("批量导入成功");
            }else{
                return R.error().message("部分数据导入失败").data("errorMessageList",errorMessage);
            }

            }catch (Exception e){
                throw new GuliException(ResultCodeEnum.EXCEL_DATA_IMPORT_ERROR);
            }
    }


    @ApiOperation(value = "嵌套数据列表")
    @GetMapping("")
    public R nestedList(){

        List<SubjectNestedVo> subjectNestedVoList = subjectService.nestedList();
        return R.ok().data("items",subjectNestedVoList);
    }


}

