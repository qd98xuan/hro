<template>
	<view class="workFlow-v">
		<u-tabs :list="entrustList" :current="flowCurrent" @change="flowChange" :is-scroll='false' name="fullName">
		</u-tabs>
		<view class="search-box_sticky">
			<view class="search-box">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
					bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<u-tabs :list="categoryList" :current="current" @change="change" :is-scroll='true' name="fullName">
			</u-tabs>
		</view>
		<mescroll-body ref="mescrollRef" @down="downCallback" :down="downOption" :sticky="false" @up="upCallback"
			:up="upOption" :bottombar="false" style="min-height: 100%;" @init="mescrollInit">
			<view class="workFlow-list">
				<view class="part">
					<view class="caption u-line-1" v-if="list.length >= 1">{{current === 0 ? "全部流程" : fullName}}</view>
					<view class="u-flex u-flex-wrap">
						<view class="item u-flex-col u-col-center" v-for="(child,ii) in list" :key="ii"
							@click="handelClick(child)">
							<text class="u-font-40 item-icon" :class="child.icon"
								:style="{'background':child.iconBackground||'#008cff'}" />
							<text class="u-font-24 u-line-1 item-text">{{child.fullName}}</text>
						</view>
					</view>
				</view>
			</view>
		</mescroll-body>
		<linzen-select :multiple="true" ref="select" :options="options" @change="selectChange" :isForm='false'>
		</linzen-select>
		<u-picker mode="selector" v-model="show" :default-selector="[0]" title="请选择流程" :range="selector"
			range-key="fullName" @confirm="confirm"></u-picker>
	</view>
</template>
<script>
	import {
		delegateGetflow,
		getUserListByFlowId
	} from '@/api/workFlow/entrust.js'
	import {
		FlowJsonList
	} from '@/api/workFlow/flowEngine'
	import {
		getUsualList
	} from '@/api/apply/apply.js'
	import resources from '@/libs/resources.js'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	export default {
		mixins: [MescrollMixin],
		data() {
			return {
				selector: [],
				show: false,
				activeFlow: {},
				templateList: [],
				downOption: {
					use: true,
					auto: true
				},
				upOption: {
					page: {
						num: 0,
						size: 50,
						time: null
					},
					empty: {
						use: true,
						icon: resources.message.nodata,
						tip: "暂无数据",
						fixed: false,
						top: "560rpx",
					},
					textNoMore: '没有更多数据',
				},
				keyword: '',
				category: '',
				current: 0,
				categoryList: [{
					fullName: '全部流程'
				}],
				list: [],
				fullName: '',
				loading: false,
				entrustList: [{
						fullName: '发起流程'
					},
					{
						fullName: '功能流程'
					}
				],
				flowCurrent: 0,
				options: [],
				config: {}
			}
		},
		onLoad() {
			this.getPaymentMethodOptions()
			uni.$on('refresh', () => {
				this.list = [];
				this.current = 0
				this.mescroll.resetUpScroll();
			})
		},
		methods: {
			openPage(path) {
				if (!path) return
				uni.navigateTo({
					url: path
				})
			},
			upCallback(page) {
				let query = {
					currentPage: page.num,
					pageSize: page.size,
					keyword: this.keyword,
					category: this.category == 0 ? '' : this.category,
					flowType: this.flowCurrent
				}
				this.loading = false
				delegateGetflow(query, {
					load: page.num == 1
				}).then(res => {
					let resData = res.data.list || [];
					this.mescroll.endSuccess(resData.length);
					if (page.num == 1) this.list = [];
					const list = resData.map(o => ({
						show: false,
						...o
					}));
					this.list = this.list.concat(list);
					this.loading = true
				}).catch(() => {
					this.mescroll.endErr();
				})
			},
			search() {
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300)
			},
			flowChange(index) {
				this.flowCurrent = index;
				this.current = 0;
				this.list = [];
				this.mescroll.resetUpScroll()
			},
			change(index) {
				this.current = index;
				this.fullName = this.categoryList[index].fullName
				this.category = !this.categoryList[index].id ? '' : this.categoryList[index].id
				this.list = [];
				this.keyword = ""
				this.mescroll.resetUpScroll()
			},
			getPaymentMethodOptions() {
				this.$store.dispatch('base/getDictionaryData', {
					sort: 'WorkFlowCategory'
				}).then(res => {
					res.forEach(i => {
						this.categoryList.push(i)
					})
				})
			},
			confirm(e) {
				this.activeFlow = this.templateList[e[0]]
				this.Jump()
			},
			Jump() {
				this.config = {
					id: '',
					flowId: this.activeFlow.id,
					opType: '-1',
					taskNodeId: '',
					fullName: this.activeFlow.fullName
				}
				getUserListByFlowId({
					flowId: this.activeFlow.id
				}).then(res => {
					this.options = res.data.list || []
					if (this.options.length > 1) return this.$refs.select.selectShow = true
					this.config.delegateUserList = [this.options[0].id]
					uni.navigateTo({
						url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(JSON
							.stringify(this
								.config),
							"UTF-8")
					})
				})
			},
			handelClick(item) {
				FlowJsonList(item.id, '1').then(res => {
					this.templateList = res.data
					if (!this.templateList.length) {
						this.$u.toast(
							'流程不存在'
						)
					} else {
						if (this.templateList.length > 1) {
							this.show = true;
							this.selector = this.templateList
						} else {
							this.activeFlow = this.templateList[0]
							this.Jump()
						}
					}
				})
			},
			selectChange(e) {
				if (!e.length) return this.$u.toast(
					'请选择人员'
				)
				this.config.delegateUserList = e
				uni.navigateTo({
					url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(JSON.stringify(this
							.config),
						"UTF-8")
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.workFlow-v {
		.search-box_sticky {
			margin-bottom: 20rpx;

			.search-box {
				padding: 20rpx
			}
		}

		.head-tabs {
			width: 100%;
			padding: 0 32rpx;
			height: 132rpx;

			.head-tabs-item {
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				font-size: 28rpx;
				color: #303133;
				line-height: 40rpx;

				.icon-style {
					font-size: 48rpx;
					color: #303133;
					margin-bottom: 24rpx;
				}
			}
		}

		.workFlow-list {
			padding: 0rpx 20rpx 0;

			.part {
				background: #fff;
				border-radius: 8rpx;
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
							background: #ECECEC;
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
	}
</style>