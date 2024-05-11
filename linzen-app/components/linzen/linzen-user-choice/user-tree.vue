<template>
	<u-popup class="linzen-tree-select-popup" :maskCloseAble="maskCloseAble" mode="right" v-model="value"
		:safeAreaInsetBottom="safeAreaInsetBottom" @close="close" :z-index="uZIndex" width="100%">
		<view class="linzen-tree-select-body">
			<view class="linzen-tree-select-title">
				<text class="icon-zen icon-zen-report-icon-preview-pagePre u-font-40 backIcon" @tap="close"></text>
				<view class="title">选择用户</view>
			</view>
			<view class="linzen-tree-select-search">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false"
					@change="search(swiperCurrent)" bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<selectedBox :clearable="clearable" :selectList="selectList" ref="selectedBox"
				@setSelectList="setSelectList"></selectedBox>
			<view class="u-p-l-32 u-p-r-32" v-if="selectType !== 'all'">全部数据</view>
			<view class="linzen-user-content" v-if="selectType === 'all'">
				<!-- tabs切换 -->
				<view class="search-box_sticky u-userSelect_sticky">
					<!-- #ifdef MP-WEIXIN -->
					<u-tabs-swiper activeColor="#1890ff" ref="tabs" :list="!multiple?[tabsList[0]]:tabsList"
						:current="current" @change="change" :is-scroll="!multiple" :show-bar="false">
					</u-tabs-swiper>
					<!-- #endif -->
					<!-- #ifndef MP-WEIXIN -->
					<u-tabs-swiper activeColor="#1890ff" ref="tabs" :list="!multiple?[tabsList[0]]:tabsList"
						:current="current" @change="change" :is-scroll="!multiple"></u-tabs-swiper>
					<!-- #endif -->
				</view>
				<swiper :current="swiperCurrent" @transition="transition" @animationfinish="animationfinish"
					class="swiper-box">
					<swiper-item>
						<scroll-view :scroll-y="true" class="scroll-view">
							<ly-tree ref="tree" :node-key="realProps.value" :tree-data="options0"
								:highlight-current="true" @node-click="handleNodeClick" :props="realProps"
								:show-node-icon="true" :load="loadNode" lazy class="ly-tree" v-if="swiperCurrent == 0"
								:expandOnClickNode="multiple?false:true" />
						</scroll-view>
					</swiper-item>
					<swiper-item v-if="multiple">
						<scroll-view :scroll-y="true" class="scroll-view">
							<ly-tree ref="tree" :node-key="realProps.value" :tree-data="roleOption"
								:highlight-current="true" @node-click="handleNodeClick" :props="realProps"
								:ready="!!roleOption.length" :filter-node-method="filterNode" v-if="swiperCurrent == 1"
								:expandOnClickNode="false" :show-node-icon="true" />
						</scroll-view>
					</swiper-item>
					<swiper-item v-if="multiple">
						<scroll-view :scroll-y="true" class="scroll-view">
							<ly-tree ref="tree" :node-key="realProps.value" :tree-data="posOption"
								:highlight-current="true" @node-click="handleNodeClick" :props="realProps"
								:ready="!!posOption.length" :filter-node-method="filterNode" v-if="swiperCurrent == 2"
								:expandOnClickNode="false" :show-node-icon="true" />
						</scroll-view>
					</swiper-item>
					<swiper-item v-if="multiple">
						<scroll-view :scroll-y="true" class="scroll-view">
							<ly-tree ref="tree" :node-key="realProps.value" :tree-data="groupOption"
								:highlight-current="true" @node-click="handleNodeClick" :props="realProps"
								:ready="!!groupOption.length" :filter-node-method="filterNode" v-if="swiperCurrent == 3"
								:expandOnClickNode="false" :show-node-icon="true" />
						</scroll-view>
					</swiper-item>
				</swiper>
			</view>
			<view v-else class="linzen-tree-select-tree">
				<scroll-view id="scroll-view-h" class="scroll-view" :refresher-enabled="false"
					:refresher-threshold="100" :scroll-with-animation='true' :refresher-triggered="triggered"
					@scrolltolower="handleScrollToLower" :scroll-y="true" scroll-anchoring>
					<view class="lists_box">
						<view class="list-cell-txt u-border-bottom" v-for="(list,index) in options1" :key="index"
							@click="onSelect(list)">
							<view class="avatar">
								<u-avatar :src="baseURL+list.headIcon" mode="circle" size="default"></u-avatar>
							</view>
							<view class="u-font-30 content">
								<view>{{list.fullName}}</view>
								<view class="organize">{{list.organize}}</view>
							</view>
						</view>
						<view v-if="!list.length" class="nodata u-flex-col">
							<image :src="noDataIcon" mode="widthFix" class="noDataIcon"></image>
							暂无数据
						</view>
					</view>
				</scroll-view>
			</view>
			<!-- 底部按钮 -->
			<view class="linzen-tree-select-actions">
				<u-button class="buttom-btn" @click="close">取消</u-button>
				<u-button class="buttom-btn" type="primary" @click.stop="getResult('confirm')">确定</u-button>
			</view>
		</view>
	</u-popup>
</template>
<script>
	/**
	 * tree-select 树形选择器
	 * @property {Boolean} v-model 布尔值变量，用于控制选择器的弹出与收起
	 * @property {Boolean} safe-area-inset-bottom 是否开启底部安全区适配(默认false)
	 * @property {String} cancel-color 取消按钮的颜色（默认#606266）
	 * @property {String} confirm-color 确认按钮的颜色(默认#2979ff)
	 * @property {String} confirm-text 确认按钮的文字
	 * @property {String} cancel-text 取消按钮的文字
	 * @property {Boolean} mask-close-able 是否允许通过点击遮罩关闭Picker(默认true)
	 * @property {String Number} z-index 弹出时的z-index值(默认10075)
	 * @event {Function} confirm 点击确定按钮，返回当前选择的值
	 */
	const defaultProps = {
		label: 'fullName',
		value: 'id',
		icon: 'icon',
		children: 'children'
	}
	import {
		getUserSelectorNew,
		getSelectedUserList
	} from '@/api/common.js'
	import resources from '@/libs/resources.js'
	import selectedBox from './selected-box.vue'
	import LyTree from '@/components/ly-tree/ly-tree.vue'
	export default {
		name: "tree-select",
		components: {
			LyTree,
			selectedBox
		},
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
			// 通过双向绑定控制组件的弹出与收起
			value: {
				type: Boolean,
				default: false
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
			props: {
				type: Object,
				default: () => ({
					label: 'fullName',
					value: 'id',
					icon: 'icon',
					children: 'children',
					isLeaf: 'isLeaf'
				})
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
			roleOption: {
				type: Array,
				default: []
			},
			groupOption: {
				type: Array,
				default: []
			},
			posOption: {
				type: Array,
				default: []
			},
			list: {
				type: Array,
				default: []
			},
			zIndex: {
				default: 9999
			}
		},
		data() {
			return {
				noDataIcon: resources.message.nodata,
				triggered: false,
				moving: false,
				selectList: [],
				keyword: '',
				tabsList: [{
						name: '部门'
					},
					{
						name: '角色'
					},
					{
						name: '岗位'
					},
					{
						name: '分组'
					}
				],
				current: 0,
				swiperCurrent: 0,
				options: [],
				options0: [],
				options1: [],
				pagination: {
					currentPage: 1,
					pageSize: 20
				},
				total: 0,
				height: 0
			};
		},
		watch: {
			// 在select弹起的时候，重新初始化所有数据
			value: {
				immediate: true,
				handler(val) {
					if (val) setTimeout(() => this.init(), 10);
				}
			}
		},
		computed: {
			baseURL() {
				return this.define.baseURL
			},
			uZIndex() {
				// 如果用户有传递z-index值，优先使用
				return this.zIndex ? this.zIndex : this.$u.zIndex.popup;
			},
			realProps() {
				return {
					...defaultProps,
					...this.props
				}
			}
		},
		created() {
			this._freshing = false;
			setTimeout(() => {
				this.triggered = true;
			}, 1000)
		},
		methods: {
			init() {
				this.options1 = this.list
				this.selectList = JSON.parse(JSON.stringify(this.selectedData))
			},
			filterNode(value, data) {
				if (!value) return true;
				return data[this.props.label].indexOf(value) !== -1;
			},
			setSelectList(e) {
				this.selectList = e
				this.rsetHeight()
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
			handleScrollToLower() {
				this.getSelectedUserList()
				uni.showLoading({
					title: '加载更多'
				})
			},
			getSelectedUserList() {
				let query = this.query;
				this.pagination.keyword = this.keyword
				query.pagination = this.pagination
				getSelectedUserList(query).then(res => {
					const list = res.data.list || [];
					uni.hideLoading()
					if (list.length < 1) return uni.showToast({
						title: '没有更多信息啦！',
						icon: 'none'
					});
					this.options1 = this.options1.concat(list);
					this.pagination.currentPage++
				}).catch(() => {
					uni.hideLoading()
				})
			},
			onSelect(list) {
				if (!this.multiple) {
					this.selectList = []
				}
				let flag = false;
				for (let i = 0; i < this.selectList.length; i++) {
					if (this.selectList[i].id === list.id) {
						flag = true;
						return
					}
				}!flag && this.selectList.push(list)
				this.rsetHeight()
			},
			loadNode(node, resolve) {
				if (node.level === 0) {
					getUserSelectorNew(node.level).then(res => {
						resolve(res.data.list)
					})
				} else {
					getUserSelectorNew(node.data.id).then(res => {
						const data = res.data.list
						resolve(data)
					})
				}
			},
			// tab栏切换
			change(index) {
				this.swiperCurrent = index;
				this.keyword = ''
			},
			search(index) {
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.pagination = {
						currentPage: 1,
						pageSize: 20
					}
					if (this.selectType === 'all') {
						if (this.swiperCurrent == 0 && this.current == 0) {
							getUserSelectorNew(0, this.keyword).then(res => {
								this.options0 = res.data.list
							})
						} else {
							this.$nextTick(() => {
								this.$refs.tree.filter(this.keyword);
							})
						}
					} else {
						let query = this.query;
						this.pagination.keyword = this.keyword
						query.pagination = this.pagination
						getSelectedUserList(query).then(res => {
							const list = res.data.list;
							this.options1 = list
							this.pagination = res.data.pagination
							this.total = this.pagination.total
						})
					}
				}, 300)
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
			},
			handleNodeClick(obj) {
				if ((!this.multiple && this.swiperCurrent == 0 && obj.data.type !== 'user') || (this
						.swiperCurrent == 3 && obj.data.type == 0) || !obj.data.type) return;
				if (!this.multiple) {
					this.selectList = []
				}
				var isExist = false;
				for (var i = 0; i < this.selectList.length; i++) {
					if (this.selectList[i].id == obj.data.id) {
						isExist = true;
						break;
					}
				}!isExist && this.selectList.push(obj.data);
				this.rsetHeight()
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

			// 点击确定或者取消
			getResult(event = null) {
				// #ifdef MP-WEIXIN
				if (this.moving) return;
				// #endif
				this.keyword = '';
				let id = '';
				let userId = []
				for (let i = 0; i < this.selectList.length; i++) {
					id = this.selectList[i].id + '--' + this.selectList[i].type;
					userId.push(id)
				}
				this.$emit(event, userId, this.selectList);
				this.close();
			},
			close() {
				this.current = 0
				this.swiperCurrent = 0
				this.$emit('input', false);
			}
		}
	};
</script>
<style scoped lang="scss">
	.linzen-user-content {
		flex: 1;
		display: flex;
		flex-direction: column;

		.swiper-box {
			flex: 1;
		}
	}

	.scroll-view {
		height: 100%;
	}

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

				.organize {
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
</style>