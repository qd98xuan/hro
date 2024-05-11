<template>
	<view class="index-v">
		<view class="search-box_sticky">
			<view class="reply-item u-border-bottom u-flex " @click="openPage('/pages/message/message/index?type=')">
				<view class="reply-item-img-sysMsg reply-item-icon u-flex u-row-center reply-item-icon-color2">
					<text class="icon-zen icon-zen-xitong" />
				</view>
				<view class="reply-item-txt u-flex-1">
					<view class="reply-item-cell reply-item-title u-flex u-row-between">
						<text class="title">站内消息</text>
						<text
							class="u-font-24">{{msgInfo.messageDate?$u.timeFormat(msgInfo.messageDate, 'mm-dd hh:MM'):''}}</text>
					</view>
					<view class="reply-item-cell u-flex u-row-between">
						<text class="reply-item-txt-msg u-line-1 againColor">{{msgInfo.messageText}}</text>
						<u-badge type="error" :count="msgInfo.messageCount" :absolute="false"
							v-if="msgInfo.messageCount" />
					</view>
				</view>
			</view>
			<view class="reply-item u-border-bottom u-flex " @click="openPage('./contacts')">
				<view class="reply-item-img-sysMsg reply-item-icon u-flex u-row-center reply-item-icon-color">
					<text class="icon-zen icon-zen-contacts" />
				</view>
				<view class="reply-item-txt u-flex-1">
					<view class="reply-item-cell reply-item-title u-flex u-row-between" style="margin: 0;">
						<text class="title">通讯录</text>
					</view>
				</view>
			</view>
		</view>
		<mescroll-body ref="mescrollRef" @down="downCallback" :sticky="false" :up="upOption" :bottombar="false">
			<view class="replyList">
				<view class="flow-list">
					<view class="flow-list-box">
						<uni-swipe-action ref="swipeAction">
							<uni-swipe-action-item v-for="(item, index) in list" :key="item.id" :right-options="options"
								@click="relocation(item.id)">
								<view class="reply-item u-border-bottom u-flex" @click="toIm(item)" :id="'item'+index"
									ref="mydom">
									<view class="reply-item-img">
										<u-avatar :src="baseURL+item.headIcon" mode="square" size="96" />
									</view>
									<view class="reply-item-txt u-flex-1">
										<view class="reply-item-cell reply-item-title u-flex u-row-between">
											<text class="title">{{item.realName}}/{{item.account}}</text>
											<text class="u-font-24 againColor">{{item.latestDate|toDateText}}</text>
										</view>
										<view class="reply-item-cell u-flex u-row-between">
											<text
												class="reply-item-txt-msg u-line-1 againColor">{{getMsgText(item.latestMessage,item.messageType)}}</text>
											<u-badge type="error" :count="item.unreadMessage" :absolute="false" />
										</view>
									</view>
								</view>
							</uni-swipe-action-item>
						</uni-swipe-action>
					</view>
				</view>
			</view>
		</mescroll-body>
	</view>
</template>

<script>
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import IndexMixin from './mixin.js'
	import {
		mapGetters
	} from "vuex"
	import {
		getIMReply,
		relocation
	} from '@/api/message.js'
	export default {
		mixins: [MescrollMixin, IndexMixin],
		data() {
			return {
				keyword: '',
				list: [],
				options: [{
					text: '移除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				upOption: {
					use: false
				}
			}
		},
		watch: {
			badgeNum(val) {
				this.setTabBarBadge()
			},
		},
		computed: {
			...mapGetters(['msgInfo']),
			baseURL() {
				return this.define.baseURL
			},
			report() {
				return this.define.report
			},
			token() {
				return uni.getStorageSync('token')
			},
		},
		onLoad() {
			uni.$on('updateList', data => {
				this.updateReply(data)
			})
			uni.$on('updateMsgNum', id => {
				this.updateMsgNum(id)
			})
		},
		onUnload() {
			uni.$off('updateList')
			uni.$off('updateMsgNum')
		},
		methods: {
			relocation(id) {
				relocation(id).then(res => {
					getIMReply().then(res => {
						this.list = res.data.list || [];
						this.mescroll.endSuccess(res.data.list.length, false);
						uni.hideLoading()
					}).catch(() => {
						this.mescroll.endErr();
					})
				}).catch(() => {})
			},
			isJSON(str) {
				try {
					var obj = JSON.parse(str);
					if (typeof obj == 'object' && obj) {
						return true;
					} else {
						return false;
					}
				} catch (e) {
					return false;
				}
			},
			downCallback(page) {
				getIMReply().then(res => {
					this.list = res.data.list || [];
					this.mescroll.endSuccess(res.data.list.length, false);
					uni.hideLoading()
				}).catch(() => {
					this.mescroll.endErr();
				})
			},
			search() {
				return
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300)
			},
			updateReply(data) {
				let boo = false
				const len = this.list.length
				for (let i = 0; i < len; i++) {
					if (data.id === this.list[i].id) {
						this.list[i].unreadMessage += data.unreadMessage
						this.list[i].latestMessage = data.latestMessage
						this.list[i].messageType = data.messageType
						this.list[i].latestDate = data.latestDate
						boo = true
						break
					}
				}
				if (boo) return
				data.unreadMessage = data.unreadMessage
				this.list.unshift(data)
			},
			updateMsgNum(id) {
				const len = this.list.length
				for (let i = 0; i < len; i++) {
					if (id === this.list[i].id) {
						const num = this.list[i].unreadMessage
						this.$store.commit('chat/REDUCE_BADGE_NUM', num)
						this.list[i].unreadMessage = 0
						break
					}
				}
			},
			getMsgText(text, type) {
				if (!text) return ""
				let message = ''
				switch (type) {
					case 'voice':
						message = '[语音]'
						break;
					case 'image':
						message = '[图片]'
						break;
					default:
						message = text
						break;
				}
				return message
			},
			openPage(path) {
				if (!path) return
				uni.navigateTo({
					url: path
				})
			},
			toIm(item) {
				const name = item.realName + '/' + item.account
				if (item.unreadMessage) {
					this.$store.commit('chat/REDUCE_BADGE_NUM', item.unreadMessage)
					item.unreadMessage = 0
				}
				uni.navigateTo({
					url: '/pages/message/im/index?name=' + name + '&formUserId=' + item.id + '&headIcon=' + item
						.headIcon
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.index-v {
		.search-box_sticky {
			margin-bottom: 20rpx;
			padding: 0 20rpx;
		}

		.replyList {
			padding: 0 20rpx;
			background-color: #fff;

			.againColor {
				color: #909399;
			}
		}

		.reply-item {
			height: 132rpx;
			background-color: #fff;

			.reply-item-img-sysMsg {
				width: 96rpx;
				height: 96rpx;
				border-radius: 16rpx;
				overflow: hidden;
				margin-right: 16rpx;
				flex-shrink: 0;
			}

			.reply-item-img {
				width: 96rpx;
				height: 96rpx;
				border-radius: 50%;
				overflow: hidden;
				margin-right: 16rpx;
				flex-shrink: 0;
			}

			.reply-item-icon-color {
				background-color: #2bd34f;
			}

			.reply-item-icon-color2 {
				background-color: #3B87F7;
			}

			.reply-item-icon {

				.icon-zen {
					color: #fff;
					font-size: 50rpx;
				}
			}

			.reply-item-txt {
				.reply-item-cell {
					height: 40rpx;
					color: #C6C6C6;
					font-size: 24rpx;

					&.reply-item-title {
						height: 44rpx;
						margin-bottom: 4px;

						.title {
							font-size: 28rpx;
							color: #303133;
						}
					}

					.reply-item-txt-msg {
						width: 480rpx;
					}
				}
			}
		}
	}

	.search-box_sticky {
		padding: 0 32rpx;
	}

	.flow-list .uni-swipe {
		margin-bottom: 0px !important;
		border-radius: 0px !important;
	}

	.flow-list .flow-list-box {
		margin: 0px !important;
	}
</style>