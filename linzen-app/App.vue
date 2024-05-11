<script>
	import chat from '@/libs/chat.js'
	import {
		getJsonData,
		changeData,
		CreateNewDir
	} from '@/libs/file.js';
	import {
		getAppVersion
	} from '@/api/common.js'
	import {
		getMessageDetail,
		checkInfo,
	} from '@/api/message.js'
	const token = uni.getStorageSync("token");
	export default {
		data() {
			return {
				version: 0,
				resVersion: 0,
				modileSystem: 'android',
				Apk: ''
			}
		},
		onLaunch() {
			// #ifdef H5
			if (!token) return
			chat.initSocket()
			// #endif
			// #ifdef APP-PLUS
			/* uniPush */
			// uni.getPushClientId({
			// 	success: (res) => {
			// 		this.$store.commit('user/SET_CID', res.cid)
			// 		this.handlePush()
			// 	},
			// 	fail(err) {}
			// })
			/* H5+ */
			/* 获取设备信息 */
			uni.getSystemInfo({
				success(res) {
					uni.setStorageSync('systemInfo', res.ua)
				}
			})
			this.handlePush()
			this.getAppVersion()
			// #endif
		},
		methods: {
			getAppVersion() {
				getAppVersion().then(res => {
					let data = res.data.sysVersion || ''
					const matches = data.match(/(\d+\.\d+\.\d+)/);
					if (matches && matches.length > 0) this.Apk =
						`https://cdn.linzensoft.com/apk/Android-java-${matches[0]}.apk`
					data.trim();
					this.version = Number(data.replace(/[^0-9]/ig, ""))
					this.$nextTick(() => {
						this.onUpdate()
					})
				}).catch((err) => {})
			},
			onUpdate() {
				plus.runtime.getProperty(plus.runtime.appid, (wgtinfo) => {
					let resVersion = this.define.sysVersion;
					resVersion.trim();
					this.resVersion = Number(resVersion.replace(/[^0-9]/ig, ""))
					if (this.version != this.resVersion) {
						process.env.NODE_ENV === "production" ?
							uni.setStorageSync('isUpdate', 1) : uni.removeStorageSync('isUpdate')
						uni.showModal({ //提醒用户更新
							title: "立即更新版本",
							success: (res) => {
								if (res.confirm) {
									uni.removeStorageSync('isUpdate')
									let system = plus.os.name;
									if (system === 'Android') {
										// let url = devLanguage ? javaApk : dotNetApk;
										if (!this.Apk) return this.$u.toast('下载链接为空')
										plus.runtime.openURL(this.Apk)
										// uni.downloadFile({
										// 	//下载地址
										// 	url: url,
										// 	success: data => {
										// 		console.log(data)
										// 		if (data.statusCode === 200) {
										// 			plus.runtime.install(data
										// 				.tempFilePath, {
										// 					force: false
										// 				},
										// 				function() {
										// 					plus.runtime
										// 						.restart();
										// 				});
										// 		}
										// 	}
										// })
									} else {
										plus.runtime.launchApplication({
											action: `itms-apps://itunes.apple.com/cn/app/id${'appleId自行配置'}`
										}, function(e) {});
									}
								} else if (res.cancel) {
									if (this.modileSystem == 'ios') {
										plus.ios.import("UIApplication")
											.sharedApplication()
											.performSelector("exit")
									} else if (this.modileSystem == 'android') {
										plus.runtime.quit();
									}
								}
							}
						})
					}
				})
			},
			toIm(item) {
				this.$store.commit('chat/REDUCE_BADGE_NUM', 0)
				/* H5+ */
				uni.navigateTo({
					url: '/pages/message/im/index?name=' + item.realName + '/' + item.account + '&formUserId=' +
						item.formUserId +
						'&headIcon=' +
						item
						.headIcon
				})
				/* unipush2.0 */
				// uni.navigateTo({
				// 	url: '/pages/message/im/index?name=' + item.name + '&formUserId=' +
				// 		item.formUserId +
				// 		'&headIcon=' +
				// 		item
				// 		.headIcon
				// })
			},
			toFlow(item) {
				getMessageDetail(item).then(res => {
					this.$store.commit('chat/SET_MSGINFO_NUM')
					let bodyText = res.data.bodyText ? JSON.parse(res.data.bodyText) : {};
					let config = {
						id: bodyText.processId,
						enCode: bodyText.enCode,
						flowId: bodyText.flowId,
						formType: bodyText.formType,
						opType: bodyText.type == 1 ? 0 : bodyText.type == 2 ? 1 : bodyText.type,
						taskNodeId: bodyText.taskNodeId,
						taskId: bodyText.taskOperatorId,
						fullName: res.data.title,
						status: bodyText.status
					}
					if (bodyText.type == 2) {
						checkInfo(bodyText.taskOperatorId).then(res => {
							if (res.data && res.data.isCheck) config.opType = 3
							setTimeout(() => {
								uni.navigateTo({
									url: '/pages/workFlow/flowBefore/index?config=' +
										this
										.base64.encode(JSON.stringify(
											config), "UTF-8")
								});
							}, 300)

						}).catch((err) => {})
					} else {
						uni.navigateTo({
							url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(JSON
								.stringify(
									config), "UTF-8")
						});
					}
				}).catch(err => {})
			},
			// 处理推送消息
			handlePush() {
				// #ifdef APP-PLUS
				/* H5+ */
				plus.push.addEventListener(
					'click',
					msg => {
						if (msg.payload.messageType == 1) {
							uni.navigateTo({
								url: '/pages/message/messageDetail/index?id=' + msg.payload.id
							});
						} else if (msg.payload.messageType == 2) {
							this.toFlow(msg.payload.id)
						} else {
							this.toIm(msg.payload)
						}
					})

				/* uniPush */
				// uni.onPushMessage((res) => {
				// 	// const pages = getCurrentPages();
				// 	// const currentRoute = pages[pages.length - 1].$page.fullPath
				// 	let payload = res.data.payload
				// 	let text = JSON.parse(this.base64.decode(payload.text))
				// 	let content = text.type == 1 ? '公告' : text.type == 2 ? '流程' : '聊天'
				// 	let title = text.type == 3 ? text.name : text.title
				// 	if (res.type === 'receive') {
				// 		uni.createPushMessage({
				// 			title,
				// 			content: `你有一条${content}消息`,
				// 			payload,
				// 			icon: './static/logo.png',
				// 			success: (res) => {},
				// 			fail: (err) => {}
				// 		})
				// 	} else {
				// 		if (text.type == 1) {
				// 			uni.navigateTo({
				// 				url: '/pages/message/messageDetail/index?id=' + text.id
				// 			});
				// 		} else if (text.type == 2) {
				// 			this.toFlow(text)
				// 		} else {
				// 			this.toIm(text)
				// 		}
				// 	}
				// })
				// #endif
			},
		}
	}
</script>

<style lang="scss">
	/*每个页面公共css */
	@import "@/uview-ui/index.scss";
	@import "@/uview-ui/demo.scss";
	@import "@/assets/iconfont/zen/iconfont.css";
	@import "@/assets/iconfont/custom/iconfont.css";
	@import "@/assets/scss/common.scss";
	@import "@/assets/scss/components.scss";
</style>