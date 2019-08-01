import request from '@/utils/request'
const api_name = '/api/ucenter/wx'
export default {

  getJwt() {
    return request({
      url: `${api_name}/get-jwt`,
      method: 'get'
    })
  },

  parseJwt(jwtToken) {
    return request({
      url: `${api_name}/parse-jwt`,
      method: 'post',
      data: jwtToken
    })
  }
}
