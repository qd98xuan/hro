const state = {
	socket: null,
	badgeNum: 0,
	msgInfo: {},
	formUserId: ''
};
const mutations = {
	SET_BADGE_NUM(state, badgeNum) {
		state.badgeNum = badgeNum
	},
	ADD_BADGE_NUM(state, num) {
		state.badgeNum += num
	},
	REDUCE_BADGE_NUM(state, num) {
		let badgeNum = state.badgeNum - num
		if (badgeNum < 0) badgeNum = 0
		state.badgeNum = badgeNum
	},
	SET_MSGINFO(state, msgInfo) {
		state.msgInfo = msgInfo
	},
	SET_MSGINFO_NUM(state, num) {
		if (num || num === 0) {
			state.msgInfo.messageCount = num
			state.msgInfo.count = num
			state.badgeNum = num
			return
		}
		state.msgInfo.messageCount -= 1
		state.msgInfo.count = state.msgInfo.messageCount
		let badgeNum = state.badgeNum - 1
		if (badgeNum < 0) badgeNum = 0
		state.badgeNum = badgeNum
	},
	SET_FORMUSERID(state, formUserId) {
		state.formUserId = formUserId
	},
};
const actions = {
	sendMessage({
		state,
		commit
	}, data) {
		const item = {
			account: data.toAccount,
			headIcon: data.toHeadIcon,
			id: data.toUserId,
			latestDate: data.latestDate,
			latestMessage: data.toMessage,
			messageType: data.messageType,
			realName: data.toRealName,
			unreadMessage: 0
		}
		const addItem = {
			sendUserId: data.UserId,
			contentType: data.messageType,
			content: data.toMessage,
			sendTime: data.dateTime,
			method: data.method
		}
		uni.$emit('addMsg', addItem)
		uni.$emit('updateList', item)
	},
	receiveMessage({
		state,
		commit
	}, data) {
		if (state.formUserId === data.formUserId) {
			data.unreadMessage = 0
			const item = {
				sendUserId: data.formUserId,
				contentType: data.messageType,
				content: data.formMessage,
				sendTime: data.dateTime,
				method: data.method
			}
			uni.$emit('addMsg', item)
		} else {
			data.unreadMessage = 1
			commit('ADD_BADGE_NUM', 1)
		}
		data.id = data.formUserId
		data.latestMessage = data.formMessage
		uni.$emit('updateList', data)
	},
	getMessageList({
		state,
		commit
	}, data) {
		uni.$emit('getMessageList', data)
	},
	messagePush({
		state,
		commit
	}, data) {
		state.msgInfo.messageText = data.title;
		state.msgInfo.messageCount += data.unreadNoticeCount;
		state.msgInfo.messageDate = data.messageDefaultTime
		commit('ADD_BADGE_NUM', data.unreadNoticeCount || 1)
	}
}
export default {
	namespaced: true,
	state,
	mutations,
	actions
}
