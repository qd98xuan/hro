import {
	getDictionaryDataAll,
	getOrganizeSelector,
	getDepartmentSelector,
	getPositionSelector,
	getPositionListAll,
	getUserSelector,
	getUserAll,
	getRoleSelector,
	getGroupSelector
} from '@/api/common.js'
import linzen from '@/utils/linzen';
const state = {
	dictionaryList: [],
	organizeTree: [],
	departmentTree: [],
	positionTree: [],
	departmentList: [],
	userTree: [],
	userList: [],
	positionList: [],
	relationData: {},
	roleList: [],
	roleTree: [],
	groupTree: [],
	groupList: [],
}

const mutations = {
	SET_DICTIONARY_LIST: (state, dictionaryList) => {
		state.dictionaryList = dictionaryList
	},
	SET_ORGANIZE_TREE: (state, organizeTree) => {
		state.organizeTree = organizeTree
	},
	SET_DEPARTMENT_LIST: (state, departmentTree) => {
		state.departmentTree = departmentTree
	},
	SET_DEP_LIST: (state, data) => {
		state.departmentList = data;
	},
	SET_POSITION_LIST: (state, positionList) => {
		state.positionList = positionList
	},
	SET_POSITION_TREE: (state, positionTree) => {
		state.positionTree = positionTree
	},
	SET_ROLE_LIST: (state, roleList) => {
		state.roleList = roleList
	},
	SET_ROLE_TREE: (state, roleTree) => {
		state.roleTree = roleTree
	},
	SET_GROUP_TREE: (state, groupTree) => {
		state.groupTree = groupTree;
	},
	SET_GROUP_LIST: (state, data) => {
		state.groupList = data;
	},
	SET_USER_TREE: (state, userTree) => {
		state.userTree = userTree
	},
	SET_USER_LIST: (state, userList) => {
		state.userList = userList
	},
	UPDATE_RELATION_DATA(state, val) {
		state.relationData = val
	},
}

const actions = {
	getDictionaryDataAll({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (state.dictionaryList.length) {
				resolve(state.dictionaryList)
			} else {
				getDictionaryDataAll().then(res => {
					commit('SET_DICTIONARY_LIST', res.data.list)
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			}
		})
	},
	getDictionaryData({
		state,
		dispatch
	}, info) {
		return new Promise(async resolve => {
			let list = [],
				data = [],
				json = []
			if (!state.dictionaryList.length) {
				list = await dispatch('getDictionaryDataAll')
			} else {
				list = state.dictionaryList
			}
			if (info.sort) {
				data = list.filter(o => o.enCode === info.sort)[0]
				if (!info.id) {
					json = data.dictionaryList
				} else {
					let rowData = [];
					if (!data.isTree) {
						rowData = data.dictionaryList.fliter(o => o.id == info.id)
					} else {
						const findData = list => {
							for (let i = 0; i < list.length; i++) {
								const e = list[i];
								if (e.id == info.id) {
									rowData[0] = e
									break
								}
								if (e.children && e.children.length) {
									findData(e.children)
								}
							}
						}
						findData(data.dictionaryList)
					}
					if (rowData.length) {
						json = rowData[0]
					} else {
						json = {
							id: "",
							fullName: ""
						};
					}
				}
			}
			resolve(json)
		})
	},
	getDicDataSelector({
		state,
		dispatch
	}, value, key = 'id') {
		return new Promise(async resolve => {
			let list = [],
				data = {},
				json = [];
			if (!state.dictionaryList.length) {
				list = await dispatch('getDictionaryDataAll')
			} else {
				list = state.dictionaryList
			}
			if (!value) return resolve([])
			let arr = list.filter(o => o[key] === value);
			if (!arr.length) return resolve([])
			data = arr[0];
			json = data.dictionaryList;
			resolve(json)
		})
	},
	getOrganizeTree({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.organizeTree.length) {
				getOrganizeSelector().then(res => {
					commit('SET_ORGANIZE_TREE', res.data.list)
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.organizeTree)
			}
		})
	},
	getGroupTree({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.groupTree.length) {
				getGroupSelector().then(res => {
					commit("SET_GROUP_TREE", res.data);
					let data = linzen.treeToArray(res.data, 'group')
					commit("SET_GROUP_LIST", data);
					resolve(res.data);
				}).catch(error => {
					reject(error);
				});
			} else {
				resolve(state.groupTree);
			}
		});
	},
	getRoleList({
		state,
		commit,
		dispatch
	}) {
		return new Promise((resolve, reject) => {
			if (!state.roleList.length) {
				dispatch('getRoleTree').then(res => {
					let data = linzen.treeToArray(res, 'role')
					commit('SET_ROLE_LIST', data)
					resolve(data)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.roleList)
			}
		})
	},
	getRoleTree({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.roleTree.length) {
				getRoleSelector().then(res => {
					commit('SET_ROLE_TREE', res.data.list)
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.roleTree)
			}
		})
	},
	getDepartmentTree({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.departmentTree.length) {
				getDepartmentSelector().then(res => {
					commit('SET_DEPARTMENT_LIST', res.data.list)
					let data = linzen.treeToArray(res.data.list)
					commit("SET_DEP_LIST", data);
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.departmentTree)
			}
		})
	},
	getPositionList({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.positionList.length) {
				getPositionListAll().then(res => {
					commit('SET_POSITION_LIST', res.data.list)
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.positionList)
			}
		})
	},
	getPositionTree({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.positionTree.length) {
				getPositionSelector().then(res => {
					commit('SET_POSITION_TREE', res.data.list)
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.positionTree)
			}
		})
	},
	getUserTree({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.userTree.length) {
				getUserSelector().then(res => {
					commit('SET_USER_TREE', res.data.list)
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.userTree)
			}
		})
	},
	getUserList({
		state,
		commit
	}) {
		return new Promise((resolve, reject) => {
			if (!state.userList.length) {
				getUserAll().then(res => {
					commit('SET_USER_LIST', res.data.list)
					resolve(res.data.list)
				}).catch(error => {
					reject(error)
				})
			} else {
				resolve(state.userList)
			}
		})
	},
	getUserInfo({
		state,
		dispatch
	}, id) {
		return new Promise(async resolve => {
			let list = []
			if (!state.userList.length) {
				list = await dispatch('getUserList')
			} else {
				list = state.userList
			}
			let item = list.filter(o => o.id === id)[0]
			resolve(item || {})
		})
	},
}

export default {
	namespaced: true,
	state,
	mutations,
	actions
}