import define from '@/utils/define'
import store from '@/store'

const Socket = {
	contime: 0,
	ws: null,
	initSocket() {
		try {
			const token = uni.getStorageSync('token') || ''
			const sys = uni.getStorageSync('systemInfo') || ''
			const userInfo = uni.getStorageSync('userInfo') || {}
			Socket.ws = uni.connectSocket({
				url: define.webSocketUrl + '/' + encodeURIComponent(token),
				// #ifdef APP-PLUS
				header: {
					'User-Agent': sys
				},
				// #endif
				success() {
					// console.log("websocket连接成功")
				},
			});
			store.state.chat.socket = Socket.ws

			uni.onSocketOpen(res => {
				Socket.contime = 0
				const msg = JSON.stringify({
					method: "OnConnection",
					token,
					mobileDevice: true,
					systemId: userInfo.appSystemId
				});
				Socket.sendMsg(msg)
			});

			uni.onSocketError(res => {
				store.state.chat.socket = null
				setTimeout(() => {
					Socket.contime += 1
					if (Socket.contime <= 10) {
						if (Socket.contime >= 3) {
							uni.showToast({
								title: 'IM通讯正在连接:' + '连接第' + Socket.contime + '次！稍后...',
								icon: 'none'
							})
						}
						Socket.reConnect();
					} else {
						uni.showToast({
							title: 'IM通讯连接失败，联系服务器管理员',
							icon: 'none'
						})
					}
				}, 10)
			});
			uni.onSocketClose(res => {
				store.state.chat.socket = null
				if (token) Socket.reConnect()
			});
			uni.onSocketMessage(res => {
				let dataStr = res.data;
				const data = JSON.parse(dataStr)
				let options = {
					cover: false,
					sound: 'system',
					title: data.title
				};
				switch (data.method) {
					case "initMessage": //初始化
						const msgInfo = {
							messageText: data.messageDefaultText || '暂无数据',
							messageCount: data.unreadTotalCount || 0,
						}
						let badgeNum = data.unreadTotalCount || 0
						for (let i = 0; i < data.unreadNums.length; i++) {
							badgeNum = badgeNum + data.unreadNums[i].unreadNum
						}
						store.commit('chat/SET_BADGE_NUM', badgeNum)
						store.commit('chat/SET_MSGINFO', msgInfo)
						break;
					case "Online": //在线用户

						break;
					case "Offline": //离线用户

						break;
					case "sendMessage": //发送消息
						store.dispatch('chat/sendMessage', data)
						break;
					case "receiveMessage": //接收消息
						// #ifdef APP-PLUS
						plus.push.createMessage('你有一条聊天消息', data, options);
						// #endif
						store.dispatch('chat/receiveMessage', data)
						break;
					case "messageList": //消息列表
						store.dispatch('chat/getMessageList', data)
						break;
					case "messagePush": //消息推送
						// #ifdef APP-PLUS
						let content = data.messageType == 2 ? '流程' : '公告'
						plus.push.createMessage(`你有一条${content}消息`, data, options);
						// #endif
						store.dispatch('chat/messagePush', data)
						break;
					case "closeSocket": //断开websocket连接
						uni.closeSocket()
						break;
					case "logout":
						uni.showToast({
							title: data.msg || '登录已过期',
							icon: 'none',
							complete: () => {
								setTimeout(() => {
									store.dispatch('user/resetToken').then(() => {
										uni.closeSocket()
										uni.reLaunch({
											url: '/pages/login/index'
										})
									})
								}, 1500)
							}
						})
						break;
					default:
						break;
				}
			});
		} catch (e) {}
	},
	sendMsg(data) {
		let content = data;
		uni.sendSocketMessage({
			data: content,
			fail: (e) => {
				Socket.reConnect()
			}
		})
	},
	//重连
	reConnect() {
		Socket.initSocket()
	},
};

export default Socket