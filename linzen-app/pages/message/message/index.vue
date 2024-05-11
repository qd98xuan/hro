<template>
	<view class="message-v">
		<!-- #ifdef H5 -->
		<u-navbar :custom-back="back">
		<!-- #endif -->
			<!-- #ifndef H5 -->
			<u-navbar>
			<!-- #endif -->
				<view class="slot-wrap">
					<view class="title">站内消息</view>
					<view class="nav-icon" @click="readAll">
						<text class="icon-zen icon-zen-clean" />
					</view>
				</view>
			</u-navbar>
			<view class="sticky-box" :style="{'top':statusBarHeight+44+'px'}">
				<view class="search-box search-box_sticky">
					<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
						bg-color="#f0f2f6" shape="square">
					</u-search>
				</view>
				<view class="sticky-box-tabs">
					<view class="tabs-box">
						<u-tabs class="u-tab-box" :list="tablist" :current="current" @change="tabChange"
							:offset="offset">
						</u-tabs>
					</view>
					<view class="status-box">
						<view class="status-icon" @click="showAction = true">
							<uni-icons type="bottom" size="16" color="#3C3C3C"></uni-icons>
						</view>
					</view>
				</view>
			</view>
			<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback"
				:down="downOption" :up="upOption" :bottombar="false">
				<view class="message-list">
					<view class="u-flex message-item u-border-bottom " v-for="(item, i) in list" :key="i"
						@click="detail(item)">
						<view class="message-item-img message-item-icon u-flex u-row-center"
							:class="{'message-item-icon-flow':item.type == 2,'message-notice-icon':item.type == 3,'message-schedule':item.type == 4}">
							<text class="icon-zen icon-zen-xitong" v-if="item.type == 1" />
							<text class="icon-zen icon-zen-generator-notice" v-else-if="item.type == 3" />
							<text class="icon-zen icon-zen-portal-schedule" v-else-if="item.type == 4" />
							<text class="icon-zen icon-zen-generator-flow" v-else />
							<text class="redDot" v-if="!item.isRead"></text>
						</view>
						<view class="message-item-txt">
							<view class="message-item-title u-flex">
								<text class="title u-line-1">{{item.title}}</text>
							</view>
							<view class="u-flex u-row-between message-item-cell">
								<text>{{item.releaseUser}}</text>
								<text class="u-font-24">{{item.releaseTime|date('mm-dd hh:MM')}}</text>
							</view>
						</view>
					</view>
				</view>
			</mescroll-body>
			<u-action-sheet :list="statusOptions" v-model="showAction" @click="handleClick"></u-action-sheet>
	</view>
</template>

<script>
	import {
		getMessageList,
		getMessageDetail,
		checkInfo,
		getUnReadMsgNum,
		MessageAllRead
	} from '@/api/message.js'
	import resources from '@/libs/resources.js'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	export default {
		mixins: [MescrollMixin],
		data() {
			return {
				offset: [5, 8],
				downOption: {
					use: true,
					auto: true
				},
				upOption: {
					page: {
						num: 0,
						size: 20,
						time: null
					},
					empty: {
						use: true,
						icon: resources.message.nodata,
						tip: "暂无数据",
						fixed: true,
						top: "300rpx",
					},
					textNoMore: '没有更多数据',
				},
				keyword: '',
				type: "",
				list: [],
				current: 0,
				tablist: [{
					name: '全部',
					count: 0
				}, {
					name: '系统',
					count: 0
				}, {
					name: '流程',
					count: 0
				}, {
					name: '公告',
					count: 0
				}, {
					name: '日程',
					count: 0
				}],
				status: '未读',
				isRead: 0,
				statusOptions: [{
					text: '全部'
				}, {
					text: '未读'
				}, {
					text: '已读'
				}],
				showAction: false,
				statusBarHeight: ""
			}
		},
		onLoad(option) {
			this.getUnReadMsgNum()
			this.getStatusBarHeight()
		},
		methods: {
			back() {
				history.back();
			},
			upCallback(page) {
				let query = {
					currentPage: page.num,
					pageSize: page.size,
					keyword: this.keyword,
					type: this.type,
					isRead: this.isRead
				}
				getMessageList(query, {
					load: page.num == 1
				}).then(res => {
					this.mescroll.endSuccess(res.data.list.length);
					if (page.num == 1) this.list = [];
					const list = res.data.list;
					this.list = this.list.concat(list);
				}).catch(() => {
					this.mescroll.endErr();
				})
			},
			getUnReadMsgNum() {
				getUnReadMsgNum().then(res => {
					const data = res.data
					for (var i = 0; i < this.tablist.length; i++) {
						const item = this.tablist[i]
						if (item.name == '全部') item.count = data.unReadNum
						if (item.name == '系统') item.count = data.unReadSystemMsg
						if (item.name == '流程') item.count = data.unReadMsg
						if (item.name == '公告') item.count = data.unReadNotice
						if (item.name == '日程') item.count = data.unReadSchedule
					}
					this.$store.commit('chat/SET_MSGINFO_NUM', Number(data.unReadNum))
				})
			},
			search() {
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300)
			},
			tabChange(e) {
				this.current = e
				if (e == 0) this.type = ''
				if (e == 1) this.type = 3
				if (e == 2) this.type = 2
				if (e == 3) this.type = 1
				if (e == 4) this.type = 4
				this.list = [];
				this.mescroll.resetUpScroll();
			},
			handleClick(index) {
				if (index == 0) {
					this.status = '全部'
					this.isRead = ''
				} else if (index == 1) {
					this.status = '未读'
					this.isRead = 0
				} else {
					this.status = '已读'
					this.isRead = 1
				}
				this.list = [];
				this.mescroll.resetUpScroll();
			},
			getStatusBarHeight() {
				let that = this;
				wx.getSystemInfo({
					success: function(res) {
						that.statusBarHeight = res.statusBarHeight;
					},
				});
			},
			readAll() {
				const query = {
					keyword: this.keyword,
					type: this.type,
					isRead: this.isRead
				}
				MessageAllRead(query).then(res => {
					if (this.isRead === 0) {
						this.list = [];
						this.mescroll.resetUpScroll();
					} else {
						for (let i = 0; i < this.list.length; i++) {
							this.$set(this.list[i], 'isRead', '1')
						}
					}
					this.getUnReadMsgNum()
					uni.showToast({
						title: res.msg,
						icon: 'none'
					});
				})
			},
			detail(item) {
				if (item.type == '1' || item.type == '3') {
					if (!item.isRead) {
						item.isRead = 1
						this.$store.commit('chat/SET_MSGINFO_NUM')
						uni.$on('initUnReadMsgNum', () => {
							this.getUnReadMsgNum()
						})
					}
					uni.navigateTo({
						url: '/pages/message/messageDetail/index?id=' + item.id
					});
				} else {
					getMessageDetail(item.id).then(res => {
						if (!item.isRead) {
							item.isRead = 1
							this.$store.commit('chat/SET_MSGINFO_NUM')
							this.$nextTick(() => {
								this.getUnReadMsgNum()
							})
						}

						let bodyText = res.data.bodyText ? JSON.parse(res.data.bodyText) : {};
						if (item.type == 4) {
							if (bodyText.type == 3) return
							let groupId = bodyText.groupId || ''
							uni.navigateTo({
								url: '/pages/portal/schedule/detail?groupId=' + groupId +
									'&id=' + bodyText.id
							});
							return
						}
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
						if (item.flowType == 1) {
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
									url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(
										JSON
										.stringify(
											config), "UTF-8")
								});
							}
						} else {
							let url = '/pages/workFlow/entrust/index'
							url = bodyText.type == 1 ? url + '?index=1' : url + '?index=2'
							uni.navigateTo({
								url: url
							});
						}
					})
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.message-v {
		background: #eef0f4;

		/deep/.u-border-bottom:after {
			border-bottom-width: 0px
		}

		.slot-wrap {
			display: flex;
			align-items: center;
			justify-content: center;
			position: absolute;
			left: 41px;
			right: 41px;
			/* #ifdef MP */
			width: 100%;
			/* #endif */

			.title {
				font-size: 32rpx;
				font-weight: bold;
			}

			.nav-icon {
				width: 48rpx;
				height: 48rpx;
				margin-top: 4rpx;
				margin-left: 22rpx;
				background: rgb(240, 242, 246);
				border-radius: 50%;
				display: flex;
				align-items: center;
				justify-content: center;

				text {
					font-size: 32rpx;
				}
			}
		}

		.message-schedule {
			background-color: #77f !important;
		}

		.sticky-box {
			height: 100%;
			position: sticky;
			z-index: 100;
		}

		.search-box_sticky {
			// top: calc(var(--status-bar-height) + 88rpx);
		}

		.sticky-box-tabs {
			width: 100%;
			display: flex;
			flex-direction: row;
			margin-bottom: 20rpx;
			background-color: #fff;
			border-bottom: 1rpx solid #eef0f4;
			height: 106rpx;
			align-items: center;

			.tabs-box {
				width: 90%;
			}

			.status-box {
				width: 10%;
				text-align: center;
				padding: 28rpx 18rpx;

				.status-title {
					flex-shrink: 0;
					color: #999;
					font-size: 28rpx;
				}

				.status-icon {
					width: 100%;
					align-items: center;
					font-size: 24rpx;
				}

				.status-input {
					flex: 1;
				}
			}
		}

		.u-tab-box {}

		.message-list {
			padding: 0 20rpx;
			background-color: #fff;

			.message-item {
				height: 132rpx;

				.message-item-icon-flow {
					background-color: #33CC51 !important;
				}

				.message-notice-icon {
					background-color: #e09f0c !important;
				}

				.message-item-img {
					width: 96rpx;
					height: 96rpx;
					margin-right: 16rpx;
					flex-shrink: 0;
					border-radius: 50%;
					background-color: #3B87F7;
					position: relative;

					.icon-zen {
						color: #fff;
						font-size: 50rpx;
					}

					.redDot {
						height: 16rpx;
						width: 16rpx;
						border-radius: 50%;
						background: #FE5146;
						display: inline-block;
						// margin-right: 6rpx;
						flex-shrink: 0;
						position: absolute;
						right: 2rpx;
						top: 2rpx;
					}
				}

				.message-item-txt {
					width: calc(100% - 112rpx);

					.message-item-title {
						line-height: 46rpx;
						margin-bottom: 6rpx;

						.title {
							font-size: 28rpx;
						}
					}

					.message-item-cell {
						color: #909399;
						font-size: 24rpx;
					}
				}
			}
		}
	}
</style>