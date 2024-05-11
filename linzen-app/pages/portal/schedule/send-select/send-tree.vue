<template>
	<u-popup :maskCloseAble="maskCloseAble" mode="right" v-model="value" :safeAreaInsetBottom="safeAreaInsetBottom"
		@close="close" :z-index="uZIndex" width="100%">
		<view class="user-select u-flex-col ">
			<view class="user-select-hd">
				<view class="user-select-hd-inner u-flex">
					<view class="icon-zen icon-zen-report-icon-preview-pagePre u-font-40 backIcon"
						@tap="getResult('confirm')">
					</view>
					<view class="user-select-hd-title u-font-32">
						选择提醒
					</view>
				</view>
			</view>
			<view class="user-select-search">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false"
					@change="search(swiperCurrent)" bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<view class="user-select-content">
				<view class="content">
					<scroll-view :style="{height: `calc(100vh - ${bh+height}rpx)`}" id="scroll-view-h"
						class="scroll-view2" :refresher-enabled="false" :refresher-threshold="100"
						:scroll-with-animation='true' :refresher-triggered="triggered"
						@scrolltolower="handleScrollToLower" :scroll-y="true">
						<view class="lists_box list_top">
							<view class="u-pop__countent__selectBtn u-flex-col">
								<radio-group class="u-radio-group" v-model="innerValue" v-for="(item,index) in list"
									:key="index" @change="radioChange(item)">
									<label class="u-radio-label u-flex">
										<view class="u-radio">
											<radio :value="item.id" :checked="item.id === selectId" />
										</view>
										<view class="u-pop__countent__txt u-line-1">
											{{item.fullName}}
										</view>
									</label>
								</radio-group>
							</view>
							<view v-if="list.length<1" class="nodata u-flex-col">
								<image :src="noDataIcon" mode="widthFix" class="noDataIcon"></image>
								暂无数据
							</view>
						</view>
					</scroll-view>
				</view>
			</view>
		</view>
		<!-- 底部按钮 -->
		<view class="flowBefore-actions">
			<template>
				<u-button class="buttom-btn" @click="getResult('confirm')">
					{{'取消'}}
				</u-button>
				<u-button class="buttom-btn" type="primary" @click.stop="getResult('confirm')">
					{{'确定'}}
				</u-button>
			</template>
		</view>
	</u-popup>
</template>

<script>
	const defaultProps = {
		label: 'fullName',
		value: 'id',
	}
	import resources from '@/libs/resources.js'
	import {
		getMsgTemplate
	} from '@/api/portal/portal.js'
	export default {
		props: {

			selectType: {
				type: String,
				default: 'all'
			},
			clearable: {
				type: Boolean,
				default: false
			},
			query: {
				type: Object,
				default: () => ({})
			},
			// 是否显示边框
			border: {
				type: Boolean,
				default: true
			},
			// 通过双向绑定控制组件的弹出与收起
			value: {
				type: Boolean,
				default: false
			},
			// "取消"按钮的颜色
			cancelColor: {
				type: String,
				default: '#606266'
			},
			// "确定"按钮的颜色
			confirmColor: {
				type: String,
				default: '#2979ff'
			},
			// 弹出的z-index值
			zIndex: {
				type: [String, Number],
				default: 99999
			},
			safeAreaInsetBottom: {
				type: Boolean,
				default: false
			},
			// 是否允许通过点击遮罩关闭Picker
			maskCloseAble: {
				type: Boolean,
				default: true
			},
			bh: {
				default: 450
			},
			//多选
			multiple: {
				type: Boolean,
				default: false
			},
			// 顶部标题
			title: {
				type: String,
				default: ''
			},
			// 取消按钮的文字
			cancelText: {
				type: String,
				default: '取消'
			},
			// 确认按钮的文字
			confirmText: {
				type: String,
				default: '确认'
			},
			selectedId: {
				type: String,
				default: ''
			},
		},
		data() {
			return {
				noDataIcon: resources.message.nodata,
				tabWidth: 150,
				tabIndex: 0,
				tabsList: [],
				keyword: '',
				selectList: [],
				height: 0,
				list: [],
				// 因为内部的滑动机制限制，请将tabs组件和swiper组件的current用不同变量赋值
				current: 0, // tabs组件的current值，表示当前活动的tab选项
				swiperCurrent: 0, // swiper组件的current值，表示当前那个swiper-item是活动的
				pagination: {
					currentPage: 1,
					pageSize: 20,
					messageSource: 4
				},
				total: 0,
				categoryId: '',
				triggered: false,
				moving: false,
				innerValue: '',
				selectId: this.selectedId,
			};
		},
		watch: {},
		computed: {
			uZIndex() {
				// 如果用户有传递z-index值，优先使用
				return this.zIndex ? this.zIndex : this.$u.zIndex.popup;
			},
			realProps() {
				return {
					...defaultProps,
				}
			}
		},
		created() {},
		methods: {
			// init() {
			// 	this.upCallback()
			// 	// this.selectList = JSON.parse(JSON.stringify(selectedData)) || []
			// },
			transition({
				detail: {
					dx
				}
			}) {
				this.$refs.tabs.setDx(dx);
			},
			rsetHeight() {
				this.$nextTick(() => {
					this.$uGetRect('#box').then(res => {
						let h = 0
						if (this.selectList.length < 1) return this.height = 0
						h = (Number(res.height.toFixed(0)) * 2)
						this.height = h
					})
				})
			},
			cleanAll() {
				this.selectList = [];
				this.rsetHeight()
			},
			radioChange(item) {
				this.selectId = item.id;
				this.innerValue = item.id;
				this.selectList = item
			},
			// swiper-item左右移动，通知tabs的滑块跟随移动
			transition(e) {
				let dx = e.detail.dx;
				this.$refs.uTabs.setDx(dx);
			},
			// 由于swiper的内部机制问题，快速切换swiper不会触发dx的连续变化，需要在结束时重置状态
			// swiper滑动结束，分别设置tabs和swiper的状态
			animationfinish(e) {
				let current = e.detail.current;
				this.$refs.uTabs.setFinishCurrent(current);
				this.swiperCurrent = current;
				this.current = current;
			},
			// scroll-view到底部加载更多
			onreachBottom() {

			},
			search(index) {
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.resetData()
				}, 300)
			},
			resetData() {
				this.list = []
				this.pagination = {
					currentPage: 1,
					pageSize: 20,
					messageSource: 4
				}
				this.upCallback()
			},
			handleScrollToLower() {
				if (this.pagination.pageSize * this.pagination.currentPage < this.total) {
					this.pagination.currentPage = this.pagination.currentPage + 1;
					this.upCallback()
				} else {
					uni.showToast({
						title: '没有更多信息啦！',
						icon: 'none'
					});
				}
			},
			upCallback() {
				let query = {
					currentPage: this.pagination.currentPage,
					pageSize: this.pagination.pageSize,
					keyword: this.keyword,
					messageSource: this.pagination.messageSource
				}
				this.loading = false
				getMsgTemplate(query).then(res => {
					const list = res.data.list;
					this.list = this.list.concat(list);
					this.pagination = res.data.pagination
					this.total = this.pagination.total
					let item = this.list.filter(o => o.id == this.selectId)[0]
					if (item) this.selectList = item
				}).catch(() => {})
			},
			// 点击确定或者取消
			getResult(event = null) {
				// #ifdef MP-WEIXIN
				if (this.moving) return;
				// #endif
				this.keyword = '';
				if (event === 'cancel') return this.close();
				// if(this.selectList.length === 0) return this.$u.toast('还未选择人员！')
				this.$emit(event, this.selectList);
				this.close();
			},
			// 标识滑动开始，只有微信小程序才有这样的事件
			pickstart() {
				// #ifdef MP-WEIXIN
				this.moving = true;
				// #endif
			},
			// 标识滑动结束
			pickend() {
				// #ifdef MP-WEIXIN
				this.moving = false;
				// #endif
			},
			rsetHeight() {
				this.$nextTick(() => {
					this.$uGetRect('#box').then(res => {
						let h = 0
						if (this.selectList.length < 1) return this.height = 0
						h = (Number(res.height.toFixed(0)) * 2)
						this.height = h
					})
				})
			},
			close() {
				this.$emit('input', false);
			},
			transition({
				detail: {
					dx
				}
			}) {
				this.$refs.tabs.setDx(dx);
			},
			animationfinish({
				detail: {
					current
				}
			}) {
				this.$refs.tabs.setFinishCurrent(current);
				this.swiperCurrent = current;
				this.current = current;
				if (this.swiperCurrent !== 0) this.handOff(this.swiperCurrent)
			},
		}
	};
</script>

<style scoped lang="scss">
	.u-popup {}

	.user-select {
		.user-select-hd {
			height: 80rpx;

			.user-select-hd-inner {
				width: calc(100% - 56rpx);
				box-sizing: border-box;
				align-items: center;
				font-weight: bh;
				letter-spacing: 2rpx;

				.backIcon {
					width: 56rpx;
					height: 70rpx;
					text-align: center;
					font-weight: 400;
					margin-left: 16rpx;
				}

				.user-select-hd-title {
					width: 100%;
					height: 80rpx;
					text-align: center;
				}
			}
		}

		.user-select-search {
			padding: 20rpx 24rpx;
		}

		.list_top {
			margin-top: 40rpx;
		}

		.user-select-content {
			width: 100%;
			padding: 0 24rpx 38rpx;

			.alreadySelect {
				width: 100%;

				.alreadySelect__box {
					.alreadySelect_hd {
						width: 100%;
						height: 60rpx;
						justify-content: space-between;
					}

					.select__box {
						width: 100%;
						justify-content: center;
						border-bottom: 1rpx solid #c0c4cc;

						.select__list {
							justify-content: flex-start;
							flex-wrap: wrap;
							padding-top: 10rpx;

							.u-selectTag {
								// width: 310rpx;
								border: 1px solid #2194fa;
								background-color: #e8f4fe;
								line-height: 40rpx;
								margin: 10rpx;
								padding-left: 10rpx;
								align-items: center;
								border-radius: 8rpx;

								.select__content {
									width: 82%;
									margin-left: 10rpx;

									.nameSty {

										color: #353535;

										.nameUp {
											white-space: nowrap;
											//overflow: hidden; //超出的文本隐藏
											// text-overflow: ellipsis;
										}

										.close {
											width: 26px;
											justify-content: flex-end;
											color: #2194fa;
										}
									}

									.organizeSty {
										color: #a0a1a1;
										white-space: nowrap;
										overflow: hidden; //超出的文本隐藏
										text-overflow: ellipsis
									}
								}
							}

							.u-size-default {
								padding: 6rpx 12rpx;
							}
						}
					}
				}
			}

			.content {

				.scroll-view2 {
					height: calc(100vh - 430rpx);

					.lists_box {
						height: 100%;

						.nodata {
							height: 100%;
							margin: auto;
							align-items: center;
							justify-content: center;
							color: #909399;

							.noDataIcon {
								width: 300rpx;
								height: 210rpx;
							}
						}

						.list-cell-txt {
							display: flex;
							box-sizing: border-box;
							width: 100%;
							padding: 20rpx 32rpx;
							overflow: hidden;
							color: $u-content-color;
							font-size: 28rpx;
							line-height: 24px;
							background-color: #fff;

							.content {
								width: 85%;
								margin-left: 15rpx;

								.nameSty {}

								.organizeSty {
									white-space: nowrap;
									overflow: hidden; //超出的文本隐藏
									text-overflow: ellipsis
								}
							}

							.department {
								color: #9A9A9A;
							}
						}
					}
				}
			}
		}
	}
</style>