package com.guli.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.guli.common.until.ExcelImportUtil;
import com.guli.edu.entity.Subject;
import com.guli.edu.mapper.SubjectMapper;
import com.guli.edu.service.SubjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guli.edu.vo.SubjectNestedVo;
import com.guli.edu.vo.SubjectVo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author Helen
 * @since 2019-07-12
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {

    @Transactional
    @Override
    public List<String> batchImport(MultipartFile file) throws Exception {
        //创建错误提示列表
        List<String> errorMsg = new ArrayList<>();
        //读取客户端上传的Excel文件
        ExcelImportUtil excelHSSFUtil = new ExcelImportUtil(file.getInputStream());

        Sheet sheet = excelHSSFUtil.getSheet();

        int rowCount = sheet.getPhysicalNumberOfRows();
        //判断数据是否存在
        if(rowCount <= 1){
            errorMsg.add("Excel中数据不存在,请填写数据!");
            return errorMsg;
        }
        //循环遍历读取数据
        for (int rowNum = 1; rowNum < rowCount ; rowNum++) {

            Row rowData = sheet.getRow(rowNum);

            if (rowData != null){
                //读取一级分类(index=0)
                Cell levelOneCell = rowData.getCell(0);
                String levelOneCellValue = "";
                if (levelOneCell != null){
                    levelOneCellValue = excelHSSFUtil.getCellValue(levelOneCell).trim();
                    if (StringUtils.isEmpty(levelOneCellValue)){
                        errorMsg.add("第"+rowNum+"行的记录一级类别为空,请填写类别");
                        //return errorMsg;
                        continue;
                    }

                }

                //判断一级分类是否重复
                Subject subject = this.getbyTitle(levelOneCellValue);
                String parentId = null;

                if (subject == null){//不重复的情况
                    //将一级分类存储到数据库
                    Subject subjectLevelOne = new Subject();
                    subjectLevelOne.setParentId("0");
                    subjectLevelOne.setTitle(levelOneCellValue);
                    subjectLevelOne.setSort(rowNum);
                    baseMapper.insert(subjectLevelOne);
                    parentId = subjectLevelOne.getId();
                }else {//重复的情况
                    parentId = subject.getId();
                }




                //获取二级分类
                String levelTwoValue = "";
                Cell levelTwoCell = rowData.getCell(1);
                if (levelTwoCell != null){
                    levelTwoValue = excelHSSFUtil.getCellValue(levelTwoCell).trim();
                    if (StringUtils.isEmpty(levelTwoValue)){
                        errorMsg.add("第" + rowNum + "行二级分类为空");
                        continue;
                    }
                }

                //判断二级分类是否重复
                Subject subjectSub = this.getSubByTitle(levelTwoValue, parentId);
                Subject subjectLevelTwo = null;
                if (subjectSub == null){
                    //  将二级分类插入到数据库
                    subjectLevelTwo = new Subject();
                    subjectLevelTwo.setTitle(levelTwoValue);
                    subjectLevelTwo.setParentId(parentId);
                    subjectLevelTwo.setSort(rowNum);
                    baseMapper.insert(subjectLevelTwo);//添加到数据库
                }




            }


        }

        return errorMsg;
    }

    @Override
    public List<SubjectNestedVo> nestedList() {

        //最终要返回的数据列表 
        ArrayList<SubjectNestedVo> subjectNestedVoArrayList = new ArrayList<>();

        //获取一级类别
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id",0);
        queryWrapper.orderByAsc("sort","id");
        //获取一级分类
        List<Subject> subjects = baseMapper.selectList(queryWrapper);

        //获取二级类别
        QueryWrapper<Subject> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.ne("parent_id",0);
        queryWrapper2.orderByAsc("sort","id");
        //获取二级分类
        List<Subject> SubSubjects = baseMapper.selectList(queryWrapper2);



        for (int i = 0; i < subjects.size(); i++) {
            Subject subject = subjects.get(i);

            //创建一级分类vo
            SubjectNestedVo subjectNestedVo = new SubjectNestedVo();
 //           subjectNestedVo.setId(subject.getId());
 //           subjectNestedVo.setTitle(subject.getTitle());
            BeanUtils.copyProperties(subject,subjectNestedVo);
            subjectNestedVoArrayList.add(subjectNestedVo);

            //处理二级分类
            ArrayList<SubjectVo> subjectVoArrayList = new ArrayList<>();
            String id = subject.getId();
            //根据一级类别的id查询二级类别的列表
            for (int j = 0; j < SubSubjects.size(); j++) {
                Subject subSubject = SubSubjects.get(j);
                if (subSubject.getParentId().equals(id)){
                    SubjectVo subjectVo = new SubjectVo();
                    BeanUtils.copyProperties(subSubject,subjectVo);
                    subjectVoArrayList.add(subjectVo);
                }
            }

            subjectNestedVo.setChildren(subjectVoArrayList);
        }

        return subjectNestedVoArrayList;
    }


    /**
     * 根据分类的名称查询这个一级分类中是否存在
     */
    private  Subject getbyTitle(String title){

        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id","0"); //一级分类
        return baseMapper.selectOne(queryWrapper);
    }

    /**
     * 根据分类名称和父类id查询这个二级分类中是否存在
     */
    private Subject getSubByTitle(String title,String parentId){

        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title",title);
        queryWrapper.eq("parent_id",parentId);
        return baseMapper.selectOne(queryWrapper);
    }
}
