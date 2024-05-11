import request from '@/utils/request'
// 获取审批常用语列表
export function commonWords(data) {
	return request({
		url: `/api/system/CommonWords`,
		method: 'get',
		data
	})
}
// 获取审批常用语详情
export function getCommonWordsInfo(id) {
	return request({
		url: `/api/system/CommonWords/${id}`,
		method: 'get'
	})
}
// 删除审批常用语详情
export function deleteCommonWordsInfo(id) {
	return request({
		url: `/api/system/CommonWords/${id}`,
		method: 'DELETE'
	})
}
//获取所属应用
export function getSelector() {
	return request({
		url: `/api/system/CommonWords/Selector?type=App`,
		method: 'get'
	})
}
// 审批常用语新建
export function Create(data) {
	return request({
		url: '/api/system/CommonWords',
		method: 'post',
		data
	})
}
// 审批常用语编辑
export function Update(data) {
	return request({
		url: `/api/system/CommonWords/${data.id}`,
		method: 'put',
		data
	})
}
