const getters = {
	token: state => state.user.token,
	userInfo: state => state.user.userInfo,
	dictionaryList: state => state.base.dictionaryList,
	departmentList: state => state.base.departmentList,
	positionList: state => state.base.positionList,
	positionTree: state => state.base.positionTree,
	relationData: state => state.base.relationData,
	roleList: state => state.base.roleList,
	badgeNum: state => state.chat.badgeNum,
	msgInfo: state => state.chat.msgInfo,
	groupList: state => state.base.groupList,
	cid: state => state.user.cid
}
export default getters