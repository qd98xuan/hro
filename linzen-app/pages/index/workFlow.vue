<template>
	<view class="workFlow-v" v-show="!pageLoading">
		<view class="workFlow-nodata" v-show="!workflowEnabled">
			<view class="u-flex-col" style="align-items: center;">
				<u-image width="280rpx" height="280rpx" :src="emptyImg"></u-image>
				<text class="u-m-t-20" style="color: #909399;">该应用协同办公未开启</text>
			</view>
		</view>
		<view class="search-box_sticky" v-show="workflowEnabled">
			<view class="head-tabs u-flex">
				<view class="head-tabs-item" @click="openPage('/pages/workFlow/flowLaunch/index')">
					<text class="icon-zen icon-zen-flowLaunch-app u-m-r-4 icon-style" />
					<text>我发起的</text>
				</view>
				<view class="head-tabs-item" @click="openPage('/pages/workFlow/flowTodo/index')">
					<text class="icon-zen icon-zen-flowTodo-app u-m-r-4 icon-style" />
					<text>待办事宜</text>
					<u-badge type="error" class="badge" :count="count" :absolute="true" :offset="offset">
					</u-badge>
				</view>
				<view class="head-tabs-item" @click="openPage('/pages/workFlow/flowDone/index')">
					<text class="icon-zen icon-zen-flowDone-app u-m-r-4 icon-style" />
					<text>已办事宜</text>
				</view>
				<view class="head-tabs-item" @click="openPage('/pages/workFlow/flowCopy/index')">
					<text class="icon-zen icon-zen-flowCopy-app u-m-r-4 icon-style" />
					<text>抄送我的</text>
				</view>
				<view class="head-tabs-item" @click="openPage('/pages/workFlow/entrust/index')">
					<text class="icon-zen icon-zen-flowEntrust-app u-m-r-4 icon-style" />
					<text>流程委托</text>
				</view>
			</view>
			<view class="search-box">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
					bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
		</view>
		<mescroll-body ref="mescrollRef" @down="downCallback" :down="downOption" :sticky="false" @up="upCallback"
			:up="upOption" :bottombar="false" style="min-height: 100%" @init="mescrollInit" v-show="workflowEnabled">
			<view class="common-block">
				<view class="caption">常用表单</view>
				<view class="u-flex u-flex-wrap">
					<view class="item u-flex-col u-col-center" v-for="(item, i) in usualList" :key="i"
						@click="handelClick(item, 1)">
						<text class="u-font-40 item-icon" :class="item.icon"
							:style="{ background: item.iconBackground || '#008cff' }" />
						<text class="u-font-24 u-line-1 item-text">{{ item.fullName }}</text>
					</view>
					<view class="item u-flex-col u-col-center" @click="moreApp">
						<text class="u-font-40 item-icon more">+</text>
						<text class="u-font-24 u-line-1 item-text">添加</text>
					</view>
				</view>
			</view>
			<u-tabs :list="categoryList" :current="current" @change="change" :is-scroll="true" name="fullName">
			</u-tabs>
			<view class="workFlow-list">
				<view class="part">
					<view class="caption u-line-1" v-if="list.length >= 1">{{
            current === 0 ? "全部流程" : fullName
          }}</view>
					<view class="u-flex u-flex-wrap">
						<view class="item u-flex-col u-col-center" v-for="(child, ii) in list" :key="ii"
							@click="handelClick(child)">
							<text class="u-font-40 item-icon" :class="child.icon"
								:style="{ background: child.iconBackground || '#008cff' }" />
							<text class="u-font-24 u-line-1 item-text">{{
                child.fullName
              }}</text>
						</view>
					</view>
				</view>
			</view>
		</mescroll-body>
		<u-picker mode="selector" v-model="show" :default-selector="[0]" title="请选择流程" :range="selector"
			range-key="fullName" @confirm="confirm"></u-picker>
	</view>
</template>
<script>
	import {
		FlowEngineListAll,
		FlowEnginePageList,
		getFlowTodoCount,
		FlowJsonList,
	} from "@/api/workFlow/flowEngine";
	import {
		getUsualList
	} from "@/api/apply/apply.js";
	import resources from "@/libs/resources.js";
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import IndexMixin from "./mixin.js";
	import {
		mapGetters
	} from "vuex";
	export default {
		mixins: [MescrollMixin, IndexMixin],
		data() {
			return {
				selector: [],
				show: false,
				activeFlow: {},
				templateList: [],
				count: 0,
				offset: [-12, 55],
				usualList: [],
				downOption: {
					use: true,
					auto: true,
				},
				className: "",
				upOption: {
					page: {
						num: 0,
						size: 50,
						time: null,
					},
					empty: {
						use: true,
						icon: resources.message.nodata,
						tip: "暂无数据",
						fixed: false,
						top: "560rpx",
					},
					textNoMore: "没有更多数据",
				},
				keyword: "",
				category: "",
				current: 0,
				categoryList: [],
				list: [],
				fullName: "",
				loading: false,
				selectFlowValue: 0,
				enCode: "",
				emptyImg: resources.message.nodata,
				workflowEnabled: false,
				pageLoading: false,
			};
		},
		onLoad() {
			uni.showLoading()
			this.pageLoading = true
			this.$store.dispatch('user/getCurrentUser').then((res) => {
				const userInfo = uni.getStorageSync('userInfo') || {}
				uni.hideLoading()
				this.pageLoading = false
				this.workflowEnabled = !!userInfo.workflowEnabled
				if (!this.workflowEnabled) return
				uni.$on("updateUsualList", (data) => {
					this.getUsualList();
				});
				this.getPaymentMethodOptions();
				uni.$on("refresh", () => {
					this.list = [];
					this.current = 0;
					this.mescroll.resetUpScroll();
				});
			})

		},
		onUnload() {
			uni.$off("updateUsualList");
		},
		onShow() {
			this.keyword = ""
			this.pageLoading = true
			const userInfo = uni.getStorageSync('userInfo') || {}
			this.workflowEnabled = !!userInfo.workflowEnabled
			this.$nextTick(() => {
				this.pageLoading = false
				if (!this.workflowEnabled) return
				this.setFlowTodoCount()
			})
		},
		methods: {
			setFlowTodoCount() {
				const query = {
					flowCirculateType: [],
					flowDoneType: [],
					toBeReviewedType: [],
				}
				getFlowTodoCount(query).then((res) => {
						this.count = res.data.toBeReviewed || 0;
					})
					.catch(() => {});
			},
			openPage(path) {
				if (!path) return;
				uni.navigateTo({
					url: path,
				});
			},
			upCallback(page) {
				this.$nextTick(() => {
					this.getUsualList();
				});
				let query = {
					currentPage: page.num,
					pageSize: page.size,
					keyword: this.keyword,
					category: this.category == 0 ? "" : this.category,
					flowType: 0,
				};
				this.loading = false;
				FlowEnginePageList(query, {
						load: page.num == 1,
					})
					.then((res) => {
						let resData = res.data.list || [];
						this.mescroll.endSuccess(resData.length);
						if (page.num == 1) this.list = [];
						const list = resData.map((o) => ({
							show: false,
							...o,
						}));
						this.list = this.list.concat(list);
						this.loading = true;
					})
					.catch(() => {
						this.mescroll.endErr();
					});
			},
			search() {
				this.searchTimer && clearTimeout(this.searchTimer);
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300);
			},

			change(index) {
				this.current = index;
				this.fullName = this.categoryList[index].fullName;
				this.category = this.categoryList[index].id || "";
				this.list = [];
				this.mescroll.resetUpScroll();
			},
			//获取常用
			getUsualList() {
				getUsualList(1).then((res) => {
					this.usualList = res.data.list.map((o) => {
						const objectData = o.objectData ? JSON.parse(o.objectData) : {};
						return {
							...o,
							...objectData,
						};
					});
				});
			},
			getPaymentMethodOptions() {
				this.$store
					.dispatch("base/getDictionaryData", {
						sort: "WorkFlowCategory",
					})
					.then((res) => {
						const firstItem = {
							fullName: "全部流程",
							id: ''
						}
						this.categoryList = [firstItem, ...(res || [])]
					});
			},
			moreApp() {
				uni.navigateTo({
					url: "/pages/workFlow/allApp/index?categoryList=" +
						encodeURIComponent(JSON.stringify(this.categoryList)),
				});
			},
			confirm(e) {
				this.activeFlow = this.templateList[e[0]];
				this.Jump();
			},
			Jump() {
				const config = {
					id: "",
					flowId: this.activeFlow.id,
					opType: "-1",
					fullName: this.activeFlow.fullName,
					enCode: this.enCode,
				};
				uni.navigateTo({
					url: "/pages/workFlow/flowBefore/index?config=" +
						this.base64.encode(JSON.stringify(config), "UTF-8"),
				});
			},
			handelClick(item, type) {
				this.enCode = item.enCode;
				FlowJsonList(item.id, "1").then((res) => {
					this.templateList = res.data;
					if (!this.templateList.length) {
						this.$u.toast("流程不存在");
					} else {
						if (this.templateList.length > 1) {
							this.show = true;
							this.selector = this.templateList;
						} else {
							this.activeFlow = this.templateList[0];
							this.Jump();
						}
					}
				});
			},
		},
	};
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.workFlow-v {
		.common-block {
			background: #fff;
			margin-bottom: 20rpx;

			.caption {
				padding-left: 32rpx;
				font-size: 36rpx;
				line-height: 100rpx;
				font-weight: bold;
			}

			.item {
				margin-bottom: 32rpx;
				width: 25%;

				.item-icon {
					width: 88rpx;
					height: 88rpx;
					margin-bottom: 8rpx;
					line-height: 88rpx;
					text-align: center;
					border-radius: 20rpx;
					color: #fff;
					font-size: 56rpx;

					&.more {
						background: #ececec;
						color: #666666;
						font-size: 50rpx;
					}
				}

				.item-text {
					width: 100%;
					text-align: center;
					padding: 0 16rpx;
				}
			}
		}

		.search-box_sticky {
			margin-bottom: 20rpx;

			.search-box {
				padding: 20rpx;
			}
		}

		.head-tabs {
			width: 100%;
			padding: 0 32rpx;
			height: 132rpx;
			overflow-x: scroll;

			.head-tabs-item {
				width: 25%;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				font-size: 28rpx;
				color: #303133;
				line-height: 40rpx;
				flex-shrink: 0;
				position: relative;

				.icon-style {
					font-size: 48rpx;
					color: #303133;
					margin-bottom: 24rpx;
				}
			}
		}

		.workFlow-list {
			margin-top: 20rpx;

			.part {
				background: #fff;
				margin-bottom: 20rpx;

				.caption {
					padding-left: 32rpx;
					font-size: 36rpx;
					line-height: 100rpx;
					font-weight: bold;
				}

				.item {
					margin-bottom: 32rpx;
					width: 25%;

					.item-icon {
						width: 88rpx;
						height: 88rpx;
						margin-bottom: 8rpx;
						line-height: 88rpx;
						text-align: center;
						border-radius: 20rpx;
						color: #fff;
						font-size: 56rpx;

						&.more {
							background: #ececec;
							color: #666666;
							font-size: 50rpx;
						}
					}

					.item-text {
						width: 100%;
						text-align: center;
						padding: 0 16rpx;
					}
				}
			}
		}

		.workFlow-nodata {
			position: absolute;
			top: 450rpx;
			width: 100%;
			text-align: center;
			z-index: 100;
			background-color: #f0f2f6;
		}
	}
</style>