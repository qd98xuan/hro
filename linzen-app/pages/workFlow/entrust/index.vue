<template>
	<view class="flowLaunch-v">
		<u-tabs :list="entrustList" :current="current" @change="change" :is-scroll='false' name="fullName">
		</u-tabs>
		<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :down="downOption"
			:up="upOption">
			<view class="search-box search_sticky">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
					bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<view class="flow-list" v-if="list.length > 0">
				<view class="flow-list-box">
					<uni-swipe-action ref="swipeAction">
						<uni-swipe-action-item v-for="(item, index) in list" :key="item.id" :threshold="0"
							:right-options="options" @click="handleClick(index)" :disabled="current==2">
							<view class="item" @click="goDetail(item)" :id="'item'+index" ref="mydom" v-if='current==0'>
								<view class="item-left">
									<view class="item-left-top u-m-b-20">
										<view class='common-lable'
											:class="{'urgent-lable':item.flowUrgent==2,'important-lable':item.flowUrgent==3}">
											{{getLableValue(item.flowUrgent)}}
										</view>
										<text class=" u-font-24 u-line-1 u-m-l-16">{{item.fullName}}</text>
									</view>
									<text class="title u-line-1 u-font-24 u-m-b-18">审批节点：<text
											class="titInner">{{item.thisStep ? item.thisStep : ''}}</text></text>
									<text class="time title u-font-24">发起时间：<text
											class="titInner">{{item.creatorTime | date('yyyy-mm-dd hh:MM:ss')}}</text></text>
								</view>
								<view class="item-right">
									<image :src="item.flowStatus" mode="widthFix" class="item-right-img">
									</image>
								</view>
							</view>
							<view class="item" :id="'item'+index" ref="mydom" @click="goDetail(item)" v-else>
								<view class="item-left">
									<text v-if="current==2" class="title u-line-1 u-font-24 u-m-b-18">委托人：<text
											class="titInner">{{item.userName ? item.userName : ''}}</text></text>
									<text class="title u-line-1 u-font-24 u-m-b-18">受委托人：<text
											class="titInner">{{item.toUserName ? item.toUserName : ''}}</text></text>
									<text class="title u-line-1 u-font-24 u-m-b-18">委托流程：<text
											class="titInner">{{item.flowName ? item.flowName : ''}}</text></text>
									<text class="time title u-font-24 u-m-b-18">开始时间：<text
											class="titInner">{{item.startTime | date('yyyy-mm-dd hh:MM:ss')}}</text></text>
									<text class="time title u-font-24">结束时间：<text
											class="titInner">{{item.endTime | date('yyyy-mm-dd hh:MM:ss')}}</text></text>
								</view>
								<view class="item-right">
									<image :src="item.entrustStatus.flowStatus" mode="widthFix" class="item-right-img">
									</image>
								</view>
							</view>
						</uni-swipe-action-item>
					</uni-swipe-action>
				</view>
			</view>
		</mescroll-body>
		<view class="com-addBtn" v-if="current!=2" @click="addPage()">
			<u-icon name="plus" size="60" color="#fff" />
		</view>
	</view>
</template>

<script>
	import resources from '@/libs/resources.js'
	import {
		FlowDelegateList,
		delegateGetflow,
		DeleteDelagate
	} from '@/api/workFlow/entrust.js'
	import {
		FlowLaunchList,
		Delete
	} from '@/api/workFlow/flowLaunch.js'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import flowlist from '../components/flowList.vue'
	export default {
		components: {
			flowlist
		},
		mixins: [MescrollMixin],
		data() {
			return {
				keyword: '',
				opType: 3,
				list: [],
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
				entrustList: [{
						fullName: '委托发起'
					},
					{
						fullName: '委托设置'
					},
					{
						fullName: '委托给我的'
					}
				],
				current: 0,
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				key: +new Date()
			}
		},
		onShow() {
			uni.$on('refresh', () => {
				this.list = [];
				this.mescroll.resetUpScroll();
			})
		},
		onLoad(e) {
			if (e.index) {
				this.current = e.index
			}
		},
		methods: {
			addPage(id) {
				if (this.current == 0) {
					uni.navigateTo({
						url: '/pages/workFlow/entrust/flow'
					})
				} else {
					uni.navigateTo({
						url: '/pages/workFlow/entrust/form?id='
					})
				}
			},
			handleClick(index) {
				const item = this.list[index]
				if (this.current == 0) {
					if ([1, 2, 3, 5].includes(item.status)) {
						this.$u.toast("流程正在审核,请勿删除")
						this.list[index].show = false
						return
					}
					Delete(item.id).then(res => {
						this.$u.toast(res.msg)
						this.mescroll.resetUpScroll()
					})
				}
				if (this.current == 1) {
					DeleteDelagate(item.id).then(res => {
						this.$u.toast(res.msg)
						this.mescroll.resetUpScroll()
					})
				}
			},
			upCallback(page) {
				let query = {
					currentPage: page.num,
					pageSize: page.size,
					keyword: this.keyword,
				}
				if (this.current == 1) query.myOrDelagateToMe = 1
				if (this.current == 2) query.myOrDelagateToMe = 2
				if (this.current == 0) query.delegateType = true
				if (this.current != 0) {
					FlowDelegateList(query, {
						load: page.num == 1
					}).then(res => {
						this.mescroll.endSuccess(res.data.list.length);
						if (page.num == 1) this.list = [];
						const list = res.data.list.map(o => ({
							'entrustStatus': this.getEntrustStatus(o),
							...o
						}))
						this.list = this.list.concat(list);
						this.key = +new Date()
					}).catch(() => {
						this.mescroll.endErr();
					})
				} else {
					FlowLaunchList(query, {
						load: page.num == 1
					}).then(res => {
						this.mescroll.endSuccess(res.data.list.length);
						if (page.num == 1) this.list = [];
						const list = res.data.list.map(o => ({
							'flowStatus': this.getFlowStatus(o.status),
							...o
						}))
						this.list = this.list.concat(list);
					}).catch(() => {
						this.mescroll.endErr();
					})
				}
			},
			getFlowStatus(status) {
				let flowStatus;
				switch (status) {
					case 0: //等待提交
						flowStatus = resources.status.submit
						break;
					case 1: //等待审核
						flowStatus = resources.status.review
						break;
					case 2: //审核通过
						flowStatus = resources.status.reviewAdopt
						break;
					case 3: //审核驳回
						flowStatus = resources.status.reviewRefuse
						break;
					case 4: //流程撤回
						flowStatus = resources.status.reviewUndo
						break;
					case 5: //审核终止
						flowStatus = resources.status.reviewStop
						break;
					default: //等待审核
						flowStatus = resources.status.review
						break;
				}
				return flowStatus
			},
			getEntrustStatus(o) {
				let startTime = o.startTime
				let endTime = o.endTime
				let currTime = Math.round(new Date())
				// 0-委托中 1-未开始 2-已失效
				let status;
				if (startTime > currTime) {
					status = 1
				} else if (endTime <= currTime) {
					status = 2
				} else {
					status = 0
				}
				let flowStatus;
				switch (status) {
					case 0: //委托中
						flowStatus = resources.status.entrusting
						break;
					case 1: //未开始
						flowStatus = resources.status.notStarted
						break;
					default: //已失效
						flowStatus = resources.status.expired
						break;
				}
				return {
					flowStatus,
					status
				}
			},
			search() {
				// 节流,避免输入过快多次请求
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300)
			},
			change(index) {
				this.keyword = ''
				this.current = index;
				this.list = [];
				this.search()
			},
			getLableValue(value) {
				var lableValue = ''
				switch (value) {
					case 1:
						lableValue = '普通'
						break;
					case 2:
						lableValue = '重要'
						break;
					case 3:
						lableValue = '紧急'
						break;
					default:
						lableValue = '普通'
						break;
				}
				return lableValue
			},
			goDetail(item) {
				let url = '/pages/workFlow/'
				if (this.current == 0) {
					let opType = '-1'
					if ([1, 2, 4, 5].includes(item.status)) opType = 0
					const config = {
						id: item.id,
						enCode: item.flowCode,
						flowId: item.flowId,
						formType: item.formType,
						opType: opType,
						status: item.status,
						taskNodeId: '',
						fullName: item.fullName,
						jurisdictionType: 'btn_edit'
					}
					url = url + 'flowBefore/index?config=' + this.base64.encode(JSON.stringify(config), "UTF-8")
				} else {
					if (item.entrustStatus.status == 2) return
					url = url + 'entrust/form?id=' + item.id + '&status=' + item.entrustStatus.status + '&current=' + this
						.current
				}
				uni.navigateTo({
					url: url
				})
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.search_sticky {
		z-index: 990;
		position: sticky;
		background-color: #fff;
	}

	.flowLaunch-v {
		width: 100%;

		.flow-list-box {
			width: 95%;

			.item {
				display: flex;
				width: 100%;
				height: 100%;

				.common-lable {
					font-size: 24rpx;
					border-radius: 8rpx;
					color: #409EFF;
					border: 1px solid #409EFF;
					background-color: #e5f3fe;

				}

				.urgent-lable {
					color: #E6A23C;
					border: 1px solid #E6A23C;
					background-color: #fef6e5;
				}

				.important-lable {
					color: #F56C6C;
					border: 1px solid #F56C6C;
					background-color: #fee5e5;
				}


				.item-left {
					.title {
						width: unset;
						flex: 1;
						min-width: 0;
						// margin-bottom: 16rpx;
						// border: 1px solid red;

						// &:last-child() {
						// 	margin-bottom: 0;
						// 	border: 1px solid blue;
						// }
					}


				}

				.item-right {
					display: flex;
					justify-content: flex-end;
					height: 88rpx;

					.item-right-img {
						width: 102rpx;

					}
				}
			}


		}

		.item-left-top {
			display: flex;
			width: 100%;
			align-items: baseline;

			.common-lable {
				font-size: 24rpx;
				padding: 2rpx 8rpx;

				border-radius: 8rpx;
				color: #409EFF;
				border: 1px solid #409EFF;
				background-color: #e5f3fe;
				// margin-bottom: 16rpx;
			}

			.urgent-lable {
				color: #E6A23C;
				border: 1px solid #E6A23C;
				background-color: #fef6e5;
			}

			.important-lable {
				color: #F56C6C;
				border: 1px solid #F56C6C;
				background-color: #fee5e5;
			}

			.title {
				width: unset;
				flex: 1;
				min-width: 0;
			}
		}
	}
</style>