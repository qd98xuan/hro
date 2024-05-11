<template>
	<view class="order-v">
		<view class="head-warp com-dropdown">
			<u-dropdown class="u-dropdown" ref="uDropdown">
				<u-dropdown-item title="排序" :options="sortOptions">
					<view class="dropdown-slot-content">
						<view class="dropdown-slot-content-main">
							<u-cell-group>
								<u-cell-item @click="cellClick(item.value)" :arrow="false" :title="item.label"
									v-for="(item, index) in sortOptions" :key="index" :title-style="{
									color: sortValue == item.value ? '#2979ff' : '#606266' }">
									<u-icon v-if="sortValue == item.value" name="checkbox-mark" color="#2979ff"
										size="32"></u-icon>
								</u-cell-item>
							</u-cell-group>
						</view>
						<view class="dropdown-slot-bg" @click="$refs.uDropdown.close()"></view>
					</view>
				</u-dropdown-item>
				<u-dropdown-item title="筛选">
					<view class="dropdown-slot-content">
						<view class="dropdown-slot-content-main">
							<view class="u-p-l-32 u-p-r-32">
								<u-form label-position="left" label-width="150" label-align="left">
									<u-form-item label="起始日期" prop="startTime">
										<linzen-date-time type="date" v-model="listQuery.startTime"></linzen-date-time>
									</u-form-item>
									<u-form-item label="结束日期" prop="endTime">
										<linzen-date-time type="date" v-model="listQuery.endTime"></linzen-date-time>
									</u-form-item>
								</u-form>
							</view>
							<view class="buttom-actions">
								<u-button class="buttom-btn" @click="reset">重置</u-button>
								<u-button class="buttom-btn" type="primary" @click="closeDropdown">检索</u-button>
							</view>
						</view>
						<view class="dropdown-slot-bg" @click="$refs.uDropdown.close()"></view>
					</view>
				</u-dropdown-item>
			</u-dropdown>
		</view>
		<view class="list-warp">
			<mescroll-uni ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" top="100"
				:up="upOption">
				<view class="flow-list">
					<uni-swipe-action ref="swipeAction">
						<uni-swipe-action-item v-for="(item, index) in list" :key="item.id" :right-options="options"
							@click="handleClick(index)">
							<view class="order-item"
								@click="goDetail(item.id,item.currentState,item.customerName,item.flowId)"
								:id="'item'+index">
								<view class="order-item-title u-border-bottom">
									<text class="order-title u-line-1">{{item.customerName}}</text>
								</view>
								<view class="order-item-down">
									<view class="order-item-cell u-flex">
										<text class="time">{{item.orderCode}}</text>
										<text :class="'status '+getFlowStatus(item.currentState).statusCss">
											{{getFlowStatus(item.currentState).text}}
										</text>
									</view>
									<view class="order-item-cell u-flex">
										<text class="time">{{item.salesmanName}}</text>
										<text class="time">{{item.orderDate | date('yyyy-mm-dd')}}</text>
									</view>
								</view>
							</view>
						</uni-swipe-action-item>
					</uni-swipe-action>
				</view>
			</mescroll-uni>
		</view>
		<view class="com-addBtn" @click="addPage()" v-if="isBtn">
			<u-icon name="plus" size="60" color="#fff" />
		</view>
		<u-picker mode="selector" v-model="show" :default-selector="[0]" title="请选择流程" :range="selector"
			range-key="fullName" @confirm="confirm"></u-picker>
	</view>
</template>

<script>
	import resources from '@/libs/resources.js'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import {
		getOrderList,
		Delete
	} from '@/api/apply/order'
	import {
		getFlowIdByCode,
		FlowJsonList
	} from '@/api/workFlow/flowEngine'
	export default {
		mixins: [MescrollMixin],
		data() {
			return {
				selector: [],
				show: false,
				activeFlow: {},
				templateList: [],
				sortValue: 0,
				sortOptions: [{
						label: '单据升序',
						value: 1,
					},
					{
						label: '单据降序',
						value: 2,
					},
					{
						label: '日期升序',
						value: 3,
					},
					{
						label: '日期降序',
						value: 4,
					}
				],
				upOption: {
					page: {
						num: 0,
						size: 20,
						time: null
					},
					empty: {
						icon: resources.message.nodata,
						tip: "暂无数据",
						top: "300rpx"
					},
					textNoMore: '没有更多数据',
					toTop: {
						bottom: 250
					}
				},
				list: [],
				listQuery: {
					sort: 'desc',
					sidx: '',
					keyword: '',
					startTime: '',
					endTime: ''
				},
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				menuId: '',
				flowId: '',
				key: +new Date()
			}
		},
		computed: {
			isBtn() {
				return this.$setPermission.hasBtnP('btn_add', this.menuId)
			}
		},
		onLoad(e) {
			this.menuId = e.menuId
			this.getFlowIdByCode()
		},
		onShow() {
			this.$nextTick(() => {
				this.list = [];
				this.mescroll.resetUpScroll();
			})
		},
		onUnload() {
			uni.$off('refresh')
		},
		methods: {
			confirm(e) {
				this.activeFlow = this.templateList[e[0]]
				this.jumPage()
			},
			getFlowIdByCode() {
				getFlowIdByCode('crmOrder').then(res => {
					this.flowId = res.data
					this.getFlowJsonList()
				})
			},
			getFlowJsonList() {
				FlowJsonList(this.flowId, '1').then(res => {
					this.templateList = res.data
				})
			},
			upCallback(page) {
				let query = {
					currentPage: page.num,
					pageSize: page.size,
					...this.listQuery
				}
				getOrderList(query, {
					load: page.num == 1
				}).then(res => {
					if (page.num == 1) this.list = [];
					this.mescroll.endSuccess(res.data.list.length);
					const list = res.data.list.map(o => ({
						show: false,
						...o
					}));
					this.list = this.list.concat(list);
					this.$nextTick(() => {
						this.key = +new Date()
					})
				}).catch(() => {
					this.mescroll.endErr();
				})
			},
			handleClick(index, index1) {
				const item = this.list[index]
				if ([1, 2, 3, 5].includes(item.currentState)) {
					this.$u.toast("流程正在审核,请勿删除")
					this.list[index].show = false
					return
				}
				if (!this.$setPermission.hasBtnP('btn_remove', this.menuId)) return this.$u.toast("未开启删除权限")
				Delete(item.id).then(res => {
					this.$u.toast(res.msg)
					this.list.splice(index, 1)
					if (!this.list.length) this.mescroll.resetUpScroll()
				})
				this.$nextTick(() => {
					this.key = +new Date()
				})
			},
			open(index) {
				this.list[index].show = true;
				this.list.map((val, idx) => {
					if (index != idx) this.list[idx].show = false;
				})
			},
			search() {
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300)
			},
			addPage() {
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
						this.jumPage()
					}
				}
				this.$nextTick(() => {
					this.key = +new Date()
				})
			},
			jumPage(id, status, fullName, flowId, btnType) {
				if (!id && !status && !fullName) btnType = 'btn_edit'
				let opType = '-1'
				if ([1, 2, 4, 5].includes(status)) opType = 0
				const config = {
					id: id,
					enCode: 'crmOrder',
					flowId: flowId || this.activeFlow.id,
					formType: 1,
					opType: opType,
					status: status,
					type: 1,
					fullName: fullName || '新增订单',
					jurisdictionType: btnType || ''
				}
				uni.navigateTo({
					url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(JSON.stringify(config),
						"UTF-8")
				})
			},
			goDetail(id, status, fullName, flowId) {
				if (!this.$setPermission.hasBtnP('btn_edit', this.menuId) && !this.$setPermission.hasBtnP('btn_detail',
						this.menuId)) return
				let btnType = '';
				if (this.$setPermission.hasBtnP('btn_edit', this.menuId)) btnType = 'btn_edit'
				this.jumPage(id, status, fullName, flowId, btnType)
			},
			getFlowStatus(val) {
				let status
				switch (val) {
					case 0:
						status = {
							text: '等待提交',
							statusCss: 'u-type-info'
						}
						break;
					case 1:
						status = {
							text: '等待审核',
							statusCss: 'u-type-primary'
						}
						break;
					case 2:
						status = {
							text: '审核通过',
							statusCss: 'u-type-success'
						}
						break;
					case 3:
						status = {
							text: '审核退回',
							statusCss: 'u-type-error'
						}
						break;
					case 4:
						status = {
							text: '流程撤回',
							statusCss: 'u-type-warning'
						}
						break;
					case 5:
						status = {
							text: '审核终止',
							statusCss: 'u-type-info'
						}
						break;
					default:
						status = {
							text: '等待提交',
							statusCss: 'u-type-info'
						}
						break;
				}
				return status
			},
			cellClick(val) {
				this.listQuery.sort = val == 1 || val == 3 ? 'asc' : 'desc'
				this.listQuery.sidx = val == 1 || val == 2 ? 'orderCode' : 'orderDate'
				this.sortValue = val
				this.$refs.uDropdown.close();
				this.$nextTick(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				})
			},
			reset() {
				this.listQuery.startTime = ''
				this.listQuery.endTime = ''
			},
			closeDropdown() {
				this.$refs.uDropdown.close();
				this.$nextTick(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
		height: 100%;
		/* #ifdef MP-ALIPAY */
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		/* #endif */
	}



	.order-v {
		height: 100%;
		display: flex;
		flex-direction: column;

		.mescroll-empty {
			padding: 211px 27px
		}

		.head-warp {
			background-color: #fff;
		}

		.list-warp {
			flex: 1;
			min-width: 0;
			min-height: 0;

			.flow-list {
				margin: 0 20rpx;

				/deep/.uni-swipe {
					border-radius: 10rpx;
				}

				.order-item {
					background-color: #fff;
					padding: 0 32rpx;

					.order-item-down {
						.order-item-cell {
							font-size: 28rpx;
							color: #333333;
							justify-content: space-between;
							padding: 10rpx 0;
						}
					}

					.order-item-title {
						height: 90rpx;
						line-height: 90rpx;
						width: 100%;
						font-size: 30rpx;

						.order-title {
							display: block;
						}
					}
				}
			}
		}
	}
</style>