import request from '@/utils/request'
// 获取流程引擎列表
export function FlowEngineList(data) {
	return request({
		url: `/api/workflow/Engine/flowTemplate`,
		method: 'get',
		data
	})
}
// 获取流程引擎信息
export function FlowEngineInfo(id) {
	return request({
		url: `/api/workflow/Engine/flowTemplate/${id}`,
		method: 'get'
	})
}
// 获取多流程流程列表
export function FlowJsonList(id, type) {
	return request({
		url: `/api/workflow/Engine/flowTemplate/FlowJsonList/${id}`,
		method: 'get',
		data: {
			type
		}
	})
}
//获取流程引擎分页
export function FlowEnginePageList(data) {
	return request({
		url: `/api/workflow/Engine/flowTemplate/PageListAll`,
		method: 'get',
		data,
		options: {
			load: false
		}
	})
}

//表单预览
export function flowForm(id) {
	return request({
		url: `/api/flowForm/Form/${id}`,
		method: 'get'
	})
}

// 列表ListAll
export function FlowEngineListAll() {
	return request({
		url: `/api/workflow/Engine/flowTemplate/ListAll`,
		method: 'get',
		options: {
			load: false
		}
	})
}
// 流程引擎下拉框
export function FlowEngineSelector(type) {
	return request({
		url: `/api/workflow/Engine/flowTemplate/Selector`,
		method: 'get',
		data: {
			type
		}
	})
}
// 获取流程评论列表
export function getCommentList(data) {
	return request({
		url: `/api/workflow/Engine/FlowComment`,
		method: 'get',
		data
	})
}
// 新建流程评论
export function createComment(data) {
	return request({
		url: `/api/workflow/Engine/FlowComment`,
		method: 'post',
		data
	})
}
// 删除流程评论
export function delComment(id) {
	return request({
		url: `/api/workflow/Engine/FlowComment/${id}`,
		method: 'delete'
	})
}
// 委托可选全部流程
export function FlowEngineAll(data) {
	return request({
		url: `/api/workflow/Engine/flowTemplate/getflowAll`,
		method: 'get',
		data
	})
}
// 委托设置流程
export function getFlowList(data) {
	return request({
		url: `/api/workflow/Engine/flowTemplate/getflowList`,
		method: 'get',
		data
	})
}
// 获取引擎id
export function getFlowIdByCode(enCode) {
	return request({
		url: `/api/workflow/Engine/flowTemplate/getFlowIdByCode/${enCode}`,
		method: 'get'
	})
}
// 获取待办未读
export function getFlowTodoCount(data) {
	return request({
		url: `/api/visualdev/Dashboard/FlowTodoCount`,
		method: 'post',
		data
	})
}