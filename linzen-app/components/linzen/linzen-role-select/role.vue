<template>
	<u-popup class="linzen-tree-select-popup" v-model="value" width="100%" mode="right" :z-index="uZIndex" @close="close">
		<view class="linzen-tree-select-body">
			<view class="linzen-tree-select-title">
				<text class="icon-zen icon-zen-report-icon-preview-pagePre backIcon" @tap="close"></text>
				<view class="title">角色选择</view>
			</view>
			<view class="linzen-tree-select-search">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
					bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<view class="linzen-tree-selected">
				<view class="linzen-tree-selected-head">
					<view>已选</view>
					<view v-if="multiple" class="clear-btn" @click="setCheckAll">清空列表</view>
				</view>
				<view class="linzen-tree-selected-box">
					<scroll-view scroll-y="true" class="select-list">
						<u-tag closeable @close="delSelect(index)" v-for="(list,index) in selectList" :key="index"
							:text="list.fullName" class="u-selectTag" />
					</scroll-view>
				</view>
			</view>
			<view class="linzen-tree-select-tree">
				<scroll-view :scroll-y="true" style="height: 100%">
					<ly-tree ref="tree" :node-key="realProps.value" :tree-data="options" :show-checkbox="false"
						@node-click="handleNodeClick" :props="realProps" :show-node-icon="true" :show-radio="false"
						:filter-node-method="filterNode" />
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
		getDepartmentSelector
	} from '@/api/common.js'
	let _self;
	export default {
		props: {
			options: {
				type: Array,
				default () {
					return [];
				}
			},
			selectedData: {
				type: Array,
				default () {
					return [];
				}
			},
			selectId: {
				default: ''
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
			multiple: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				moving: false,
				selectList: [],
				selectListId: [],
				newListId: [],
				keyword: ''
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
		created() {
			_self = this
			this.init()
		},
		computed: {
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
		methods: {
			init() {
				this.selectListId = JSON.parse(JSON.stringify(this.selectId))
				if (!Array.isArray(this.selectId)) {
					this.selectListId = []
				}
				this.selectList = JSON.parse(JSON.stringify(this.selectedData))
			},
			handleNodeClick(obj) {
				if (obj.data.type !== "role") return
				if (!this.multiple) {
					this.selectList = [];
					this.selectListId = [];
				}
				const isExist = this.selectList.some(item => item.id === obj.data.id);
				if (!isExist) {
					this.selectList.push(obj.data);
					this.selectListId.push(obj.data.id);
				}
			},
			delSelect(index) {
				this.selectList.splice(index, 1);
				this.selectListId.splice(index, 1);
			},
			setCheckAll() {
				this.selectList = [];
				this.selectListId = [];
				this.$refs.tree.setCheckAll(false);
			},

			// 点击确定或者取消
			getResult(event = null) {
				this.keyword = ""
				this.$emit(event, this.selectList, this.selectListId);
				this.close();
			},
			close() {
				this.$emit('input', false);
			},
			filterNode(value, data) {
				if (!value) return true;
				// #ifndef MP
				return data[this.realProps.label].indexOf(value) !== -1;
				// #endif
				// #ifdef MP
				return this.label.indexOf(value) !== -1;
				// #endif
			},
			search(val) {
				this.$refs.tree.filter(val);
				this.$refs.tree && this.$refs.tree.filter(this.keyword)
			},
		}
	};
</script>