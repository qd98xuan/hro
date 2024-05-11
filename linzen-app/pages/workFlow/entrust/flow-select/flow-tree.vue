<template>
	<u-popup :maskCloseAble="maskCloseAble" mode="right" :popup="false" v-model="value" length="auto"
		:safeAreaInsetBottom="safeAreaInsetBottom" @close="close" :z-index="uZIndex" width="100%">
		<!-- 底部按钮 -->
		<view class="user-select u-flex-col ">
			<view class="user-select-hd">
				<view class="user-select-hd-inner u-flex">
					<view class="icon-zen icon-zen-report-icon-preview-pagePre u-font-40 backIcon"
						@tap="getResult('cancel')">
					</view>
					<view class="user-select-hd-title u-font-32">
						流程选择
					</view>
				</view>
			</view>
			<view class="user-select-search">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false"
					@change="search(swiperCurrent)" bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<view class="user-select-content">
				<view class="alreadySelect">
					<view class="alreadySelect__box u-flex-col">
						<view class="alreadySelect_hd u-flex">
							<view>已选</view>
							<view @click="cleanAll" style="color: #2979ff;">清空列表
							</view>
						</view>
						<view class="select__box u-flex-col" id="box">
							<scroll-view scroll-y="true" style="max-height: 240rpx;">
								<view class="u-flex select__list">
									<view class="u-selectTag u-flex" v-for="(list,index) in selectList" :key="index">
										<view class="u-font-24 select__content">
											<view class="nameSty u-flex">
												<view class="nameUp">
													{{list.fullName}}
												</view>
												<u-icon name="close" class="close" @click='delSelect(index)'>
												</u-icon>
											</view>
											<view class="organizeSty">{{list.organize}}</view>
										</view>
									</view>
								</view>
							</scroll-view>
						</view>
					</view>
				</view>
				<view class="content">
					<!-- tabs切换 -->
					<view class="sticky-tabs">
						<meTabs v-model="tabIndex" :tabs="tabsList" ref="tabs" :tabWidth='tabWidth' @change="tabChange"
							:height='100'></meTabs>
					</view>
					<swiper class="swiper-box" :style="{height: `calc(100vh - ${bh+height}rpx)`}">
						<swiper-item>
							<scroll-view :style="{height: `calc(100vh - ${bh+height}rpx)`}" id="scroll-view-h"
								class="scroll-view2" :refresher-enabled="false" :refresher-threshold="100"
								:scroll-with-animation='true' :refresher-triggered="triggered"
								@scrolltolower="handleScrollToLower" :scroll-y="true">
								<view class="lists_box list_top">
									<view class="list-cell-txt" v-for="(list,index) in list" :key="index"
										@click="handleNodeClick(list)">
										<view class="u-font-30 content">
											<view class="nameSty">{{list.fullName}}
											</view>
										</view>
									</view>
									<view v-if="list.length<1" class="nodata u-flex-col">
										<image :src="noDataIcon" mode="widthFix" class="noDataIcon"></image>
										暂无数据
									</view>
								</view>
							</scroll-view>
						</swiper-item>
					</swiper>
				</view>
			</view>
		</view>
		<view class="flowBefore-actions">
			<template>
				<u-button class="buttom-btn" @click="getResult('cancel')">
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
	import meTabs from './me-tabs.vue';
	import {
		FlowEngineAll
	} from '@/api/workFlow/flowEngine.js'
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
			selectedData: {
				type: Array,
				default () {
					return [];
				}
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
				default: 0
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
			}
		},
		components: {
			meTabs
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
					pageSize: 20
				},
				total: 0,
				categoryId: '',
				triggered: false,
				moving: false
			};
		},
		watch: {
			// 在select弹起的时候，重新初始化所有数据
			value: {
				immediate: true,
				handler(val) {
					if (val) setTimeout(() => this.init(), 10);
				}
			},
		},
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
		created() {
			setTimeout(() => {
				this.triggered = true;
			}, 1000)
			this.$store.dispatch('base/getDictionaryData', {
				sort: 'WorkFlowCategory'
			}).then(res => {
				this.tabsList.push({
					id: 0,
					encode: "all",
					fullName: "全部流程",
				})
				this.tabsList.push(...res)
			})
		},
		methods: {
			init() {
				this.upCallback()
				this.selectList = JSON.parse(JSON.stringify(this.selectedData)) || []
			},
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
			delSelect(index) {
				this.rsetHeight();
				this.selectList.splice(index, 1);
				this.rsetHeight()
			},
			cleanAll() {
				this.selectList = [];
				this.rsetHeight()
			},
			handleNodeClick(obj) {
				if (!this.multiple) {
					this.selectList = []
				}
				var isExist = false;
				for (var i = 0; i < this.selectList.length; i++) {
					if (this.selectList[i].id == obj.id) {
						isExist = true;
						break;
					}
				}!isExist && this.selectList.push(obj);
				this.rsetHeight()
			},
			// tabs通知swiper切换
			tabsChange(index) {
				this.swiperCurrent = index;
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
			search(index) {
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.tabChange()
				}, 300)
			},
			resetData() {
				this.list = []
				this.pagination = {
					currentPage: 1,
					pageSize: 20
				}
			},
			// 切换菜单
			tabChange() {
				this.current = this.tabIndex;
				this.pagination.currentPage = 1
				this.fullName = this.tabsList[this.tabIndex].fullName
				this.categoryId = !this.tabsList[this.tabIndex].id ? '' : this.tabsList[this.tabIndex].id
				this.list = [];
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
					category: this.categoryId ? this.categoryId : ""
				}
				this.loading = false
				FlowEngineAll(query).then(res => {
					const list = res.data.list;
					list.map((o) => {
						o.fullName = o.fullName + '/' + o.enCode
					})
					this.list = this.list.concat(list);
					this.pagination = res.data.pagination
					this.total = this.pagination.total
				}).catch(() => {

				})
			},
			// 点击确定或者取消
			getResult(event = null) {
				// #ifdef MP-WEIXIN
				if (this.moving) return;
				// #endif
				this.keyword = '';
				if (event === 'cancel') {
					this.$emit('confirm', this.selectedData);
					this.close();
					return
				}
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