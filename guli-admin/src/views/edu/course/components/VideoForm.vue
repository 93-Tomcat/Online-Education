<template>
  <!-- 添加和修改课时表单 -->
  <el-dialog :visible.sync="dialogVisible" title="添加课时" @close="close">
    <el-form :model="video" label-width="120px">
      <el-form-item label="课时标题">
        <el-input v-model="video.title"/>
      </el-form-item>
      <el-form-item label="课时排序">
        <el-input-number v-model="video.sort" :min="0" controls-position="right"/>
      </el-form-item>
      <el-form-item label="是否免费">
        <el-radio-group v-model="video.free">
          <el-radio :label="true">免费</el-radio>
          <el-radio :label="false">默认</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="上传视频" label-width="120px" >

        <el-upload
          ref="upload"
          :on-change="fileChange"
          :on-exceed="handleUploadExceed"
          :before-remove="beforeVodRemove"
          :on-remove="handleVodRemove"
          :file-list="fileList"
          :limit="1"
          :auto-upload="false"
          class="upload-demo"
          action="">
          <el-button slot="trigger" size="small" type="primary">选取文件</el-button>
          <el-tooltip placement="right-end">
            <div slot="content">最大支持1G，<br>
              支持3GP、ASF、AVI、DAT、DV、FLV、F4V、<br>
              GIF、M2T、M4V、MJ2、MJPEG、MKV、MOV、MP4、<br>
              MPE、MPG、MPEG、MTS、OGG、QT、RM、RMVB、<br>
              SWF、TS、VOB、WMV、WEBM 等视频格式上传</div>
            <i class="el-icon-question"/>
          </el-tooltip>
          <el-button
            :disabled="uploadDisabled"
            style="margin-left: 10px;"
            size="small"
            type="success"
            @click="authUpload">开始上传</el-button>
          <label class="status"><span>{{ statusText }}</span></label>
          <span class="progress"><i id="auth-progress">{{ authProgress }}</i></span>
        </el-upload>

      </el-form-item>

    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="close">取 消</el-button>
      <el-button type="primary" @click="saveOrUpdate">确 定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import video from '@/api/edu/video'
// import chapter from '@/api/edu/chapter'
import vod from '@/api/edu/vod'
export default {

  props: {
    courseId: {
      type: String,
      default: null
    }
  },

  data() {
    return {
      dialogVisible: false,
      video: {// 课时对象
        chapterId: '',
        title: '',
        sort: 0,
        free: false,
        videoSourceId: '', // 阿里云视频id
        videoOriginalName: '' // 视频名称

        // 其余data,
      },
      fileList: [], // 上传文件列表
      BASE_API: process.env.BASE_API, // 接口API地址
      timeout: '',
      partSize: '',
      parallel: '',
      retryCount: '',
      retryDuration: '',
      region: 'cn-shanghai',
      userId: '1800361165107185',
      file: null,
      authProgress: null,
      uploadDisabled: true,
      uploader: null,
      statusText: ''

    }
  },

  methods: {
    open(chapterId, videoId) {
      this.dialogVisible = true
      this.video.chapterId = chapterId
      if (videoId) {
        video.getById(videoId).then(response => {
          this.video = response.data.item
          if (this.video.videoSourceId && this.video.videoOriginalName) {
            this.fileList = [{ 'name': this.video.videoOriginalName }]
          }
        })
      }
    },

    saveOrUpdate() {
      if (!this.video.id) {
        this.save()
      } else {
        this.update()
      }
    },

    save() {
      this.video.courseId = this.courseId
      console.log(this.video)
      video.save(this.video).then(response => {
        this.$message({
          type: 'success',
          message: '保存成功!'
        })
        // 关闭组件
        this.close()
        // 调用父组件监听函数
        this.$emit('onSaveSuccess')
      })
    },
    close() {
      this.dialogVisible = false
      // 重置表单
      this.resetForm()
    },

    resetForm() {
      this.video.id = null
      this.video.title = ''
      this.video.sort = 0
      this.video.chapterId = ''
      this.video.free = false
      this.video.videoSourceId = ''

      this.fileList = []

      this.file = null
      this.authProgress = null
      this.uploadDisabled = true
      this.uploader = null
      this.statusText = ''
    },

    update() {
      video.updateById(this.video).then(response => {
        this.$message({
          type: 'success',
          message: '修改成功!'
        })
        // 关闭组件
        this.close()
        // 调用父组件监听函数
        this.$emit('onSaveSuccess')
      })
    },

    // 视频上传成功的回调
    handleVodUploadSuccess(response, file, fileList) {
      this.video.videoSourceId = response.data.videoId
      this.video.videoOriginalName = file.name
    },
    // 上传多于一个视频
    handleUploadExceed(files, fileList) {
      this.$message.warning('想要重新上传视频，请先删除已上传的视频')
    },

    beforeVodRemove(file, fileList) {
      return this.$confirm(`确定移除 ${file.name}？`)
    },

    // 删除时回调
    handleVodRemove() {
      vod.removeById(this.video.videoSourceId).then(response => {
        this.video.videoSourceId = ''
        this.video.videoOriginalName = ''
        this.fileList = []
        video.updateById(this.video)

        this.$message({
          type: 'success',
          message: response.message
        })
      })
    },

    /**
     * 选择文件
     */
    fileChange(file, fileList) {
      console.log('fileChange')
      this.file = file.raw
      if (!this.file) {
        alert('请先选择需要上传的文件!')
        return
      }
      var userData = '{"Vod":{}}'
      if (this.uploader) {
        this.uploader.stopUpload()
        this.authProgress = 0
        this.statusText = ''
      }
      this.uploader = this.createUploader()
      console.log(userData)
      this.uploader.addFile(this.file, null, null, null, userData)
      this.uploadDisabled = false
    },

    /**
     * 开始上传
     */
    authUpload() {
    // 然后调用 startUpload 方法, 开始上传
      if (this.uploader !== null) {
        this.uploader.startUpload()
        this.uploadDisabled = true
      }
    },

    /**
     * 创建AliyunUpload播放器组件
     */
    createUploader(type) {
      console.log('createUploader')
      const self = this

      /* eslint-disable no-undef */
      const uploader = new AliyunUpload.Vod({
        userId: self.userId, // 阿里账号ID ，值的来源https://help.aliyun.com/knowledge_detail/37196.html
        region: self.region, // 上传到点播的地域， 默认值为'cn-shanghai',//eu-central-1,ap-southeast-1
        timeout: self.timeout || 60000, // 超时时间
        partSize: self.partSize || 1048576, // 分片大小默认1M，不能小于100K
        parallel: self.parallel || 5, // 并行上传分片个数，默认5
        retryCount: self.retryCount || 3, // 网络原因失败时，重新上传次数，默认为3
        retryDuration: self.retryDuration || 2, // 网络原因失败时，重新上传间隔时间，默认为2秒
        // 添加文件成功
        addFileSuccess: function(uploadInfo) {
          console.log('添加文件成功')
          console.log(uploadInfo)
          self.uploadDisabled = false
          self.statusText = '添加文件成功, 等待上传...'
          console.log('addFileSuccess: ' + uploadInfo.file.name)
        },
        // 开始上传
        onUploadstarted: function(uploadInfo) {
          console.log('开始上传')
          console.log(uploadInfo)
          console.log('获取上传地址和凭证')
          const fileName = uploadInfo.file.name
          const title = fileName.substring(0, fileName.lastIndexOf('.'))
          // 调用 获取视频上传地址和凭证接口
          // 调用 uploader.setUploadAuthAndAddress 方法
          vod.getUploadAuthAndAddress(title, fileName).then(response => {
            console.log(response)
            const uploadAuth = response.data.response.uploadAuth
            const uploadAddress = response.data.response.uploadAddress
            const videoId = response.data.response.videoId
            uploader.setUploadAuthAndAddress(uploadInfo, uploadAuth, uploadAddress, videoId)
          })
          self.statusText = '文件开始上传...'
        },
        // 文件上传成功
        onUploadSucceed: function(uploadInfo) {
          console.log('onUploadSucceed')
          console.log(uploadInfo.file)
          self.statusText = '文件上传成功!'
          const fileName = uploadInfo.file.name
          const title = fileName.substring(0, fileName.lastIndexOf('.'))
          self.video.videoOriginalName = title
          self.video.videoSourceId = uploadInfo.videoId
          console.log(self.video)
        },
        // 文件上传失败
        onUploadFailed: function(uploadInfo, code, message) {
          console.log('onUploadFailed: file:' + uploadInfo.file.name + ',code:' + code + ', message:' + message)
          self.statusText = '文件上传失败!'
        },
        // 文件上传进度，单位：字节, 可以在这个函数中拿到上传进度并显示在页面上
        onUploadProgress: function(uploadInfo, totalSize, progress) {
          console.log('onUploadProgress:file:' + uploadInfo.file.name + ', fileSize:' + totalSize + ', percent:' + Math.ceil(progress * 100) + '%')
          const progressPercent = Math.ceil(progress * 100)
          self.authProgress = progressPercent + '%'
          self.statusText = '文件上传中...'
        },
        // 上传凭证超时
        onUploadTokenExpired: function(uploadInfo) {
          // 上传大文件超时, 如果是上传方式一即根据 UploadAuth 上传时
          // 需要根据 uploadInfo.videoId 调用刷新视频上传凭证接口(https://help.aliyun.com/document_detail/55408.html)重新获取 UploadAuth
          // 然后调用 resumeUploadWithAuth 方法
          vod.refreshUploadAuthAndAddress(uploadInfo.videoId).then(response => {
            console.log(response)
            const uploadAuth = response.data.response.uploadAuth
            // 上传凭证失效后恢复上传
            uploader.resumeUploadWithAuth(uploadAuth)
            console.log('upload expired and resume upload with uploadauth ' + uploadAuth)
          })
          self.statusText = '文件超时...'
        },
        // 全部文件上传结束
        onUploadEnd: function(uploadInfo) {
          console.log('onUploadEnd: uploaded all the files')
          self.statusText = '文件上传完毕'
        }
      })
      return uploader
    }

  }
}
</script>
