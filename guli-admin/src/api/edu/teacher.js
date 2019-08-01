import request from '@/utils/request'

// 向后端服务器发送ajax请求
const api_name = '/admin/edu/teacher'
export default{

  getList() {
    return request({
      url: api_name,
      method: 'get'
    })
  },

  getPageList(page, limit, searchObj) {
    console.log('api：获取讲师列表')

    return request({
      url: `${api_name}/${page}/${limit}`,
      method: 'get',
      params: searchObj
    })
  },

  save(teacher) {
    return request({
      url: api_name,
      method: 'post',
      data: teacher
    })
  },

  getById(id) {
    return request({
      url: `${api_name}/${id}`,
      method: 'get'
    })
  },

  updateById(teacher) {
    return request({
      url: `${api_name}/${teacher.id}`,
      method: 'put',
      data: teacher
    })
  },

  removeById(id) {
    return request({
      url: `${api_name}/${id}`,
      method: 'delete'
    })
  }
}
