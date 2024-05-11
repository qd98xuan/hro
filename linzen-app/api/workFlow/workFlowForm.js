import request from '@/utils/request'
// 新建表单
export function Create(key, data) {
	return request({
		url: `/api/workflow/Engine/FlowTask`,
		method: 'post',
		data,
		options: {
			load: true
		}
	})
}
// 修改表单
export function Update(key, data) {
	return request({
		url: `/api/workflow/Engine/FlowTask/${data.id}`,
		method: 'put',
		data
	})
}
//通过表单id获取流程id
export function getFormById(id) {
	return request({
		url: `/api/flowForm/Form/getFormById/${id}`,
		method: 'get'
	})
}
