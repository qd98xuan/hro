<template>
	<u-popup class="linzen-tree-select-popup" :maskCloseAble="maskCloseAble" mode="right" v-model="value"
		:safeAreaInsetBottom="safeAreaInsetBottom" @close="close" :z-index="uZIndex" width="100%">
		<view class="linzen-tree-select-body">
			<view class="linzen-tree-select-title">
				<text class="icon-zen icon-zen-report-icon-preview-pagePre backIcon" @tap="close"></text>
				<view class="title">省市区</view>
			</view>
			<view class="linzen-tree-selected">
				<view class="linzen-tree-selected-head">
					<view>已选</view>
					<view v-if="multiple" @click="setCheckAll" style="color: #2979ff;">清空列表
					</view>
				</view>
				<view class="linzen-tree-selected-box">
					<scroll-view scroll-y="true" class="select-list">
						<u-tag closeable @close="delSelect(index)" v-for="(list,index) in selectList" :key="index"
							:text="list" class="u-selectTag" />
					</scroll-view>
				</view>
			</view>
			<view class="linzen-tree-select-tree">
				<scroll-view :scroll-y="true" style="height: 100%">
					<ly-tree ref="tree" :node-key="realProps.value" :tree-data="options" :show-checkbox="false"
						@node-click="handleNodeClick" :props="realProps" :show-node-icon="true" :show-radio="false"
						:load="loadNode" lazy />
				</scroll-view>
			</view>
			<!-- 底部按钮 -->
			<view class="linzen-tree-select-actions">
				<u-button class="buttom-btn" @click.stop="close">取消</u-button>
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
	var _self;
	import {
		getProvinceSelector
	} from '@/api/common.js'
	export default {
		name: "tree-select",
		props: {
			selectedData: {
				type: Array,
				default () {
					return [];
				}
			},
			selectId: {
				type: Array,
				default () {
					return [];
				}
			},
			oldSelectData: {
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
			level: {
				type: Number,
				default: 0
			}
		},
		data() {
			return {
				moving: false,
				selectList: [],
				newListId: [],
				options: [],
				selectData: [],
				selectIds: []
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
				this.selectList = JSON.parse(JSON.stringify(this.selectedData))
				this.selectIds = this.selectId
				this.selectData = this.oldSelectData
			},
			loadNode(node, resolve) {
				let id = node.key === null ? -1 : node.key
				let level = node.level
				getProvinceSelector(id).then(res => {
					const list = res.data.list.map((value, i) => ({
						id: value.id,
						fullName: value.fullName,
						isLeaf: level >= _self.level ? true : value.isLeaf
					}));
					resolve(list)
				})
			},
			handleNodeClick(obj) {
				if (!obj.isLeaf) return;
				const getNodePath = this.$refs.tree.getNodePath(obj);
				const selectList = getNodePath
				const list = getNodePath.map(node => node.fullName);
				const listId = getNodePath.map(node => node.id);
				if (listId.length !== this.level + 1) return;
				if (!this.multiple) {
					this.selectList = [];
					this.selectData = [];
					this.selectIds = []
				}
				const isExist = this.selectList.includes(list.join('/'));
				if (!isExist) {
					this.selectList.push(list.join('/'));
					this.selectData.push(selectList)
					this.selectIds.push(listId)
				}
			},
			delSelect(index) {
				this.selectList.splice(index, 1);
				this.selectIds.splice(index, 1);
				this.selectData.splice(index, 1);
			},
			setCheckAll() {
				this.selectList = [];
				this.selectIds = [];
				this.selectData = []
				this.$refs.tree.setCheckAll(false);
			},

			// 点击确定或者取消
			getResult(event = null) {
				this.$emit(event, this.selectIds, this.selectData);
				this.close();
			},
			close() {
				this.$emit('input', false);
			}
		}
	};
</script>

<style scoped lang="scss">
	@import "@/uview-ui/libs/css/style.components.scss";

	.u-select {
		height: 100% !important;

		.u-select__body__treeSelect {
			height: 100% !important;
			display: flex;
			flex-direction: column;

			.swiper-box {
				height: 100%;
				padding-bottom: 96rpx;
			}

			.alreadySelect {
				width: 100%;
				padding: 0 44rpx 0;

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
							padding-top: 10rpx;
							justify-content: flex-start;
							flex-wrap: wrap;

							.u-selectTag {
								margin-bottom: 10rpx;
								margin-left: 10rpx;
							}

							.u-size-default {
								padding: 6rpx 12rpx;
							}
						}
					}
				}
			}

			// .alreadySelect {
			// 	width: 100%;

			// 	.alreadySelect__box {

			// 		.alreadySelect_hd {
			// 			display: flex;
			// 			flex-direction: row;
			// 			justify-content: space-between;
			// 			margin: 0 40rpx;
			// 		}

			// 		.select__box {
			// 			margin: 0 40rpx 40rpx;
			// 			border: 1rpx solid #f4f6f8;
			// 			display: flex;
			// 			flex-direction: row;
			// 			flex-wrap: wrap;
			// 			justify-content: flex-start;
			// 			box-shadow: 0px 4px 6px #f4f6f8;

			// 			.alreadySelect__content {
			// 				.u-size-default {
			// 					margin-left: 10rpx;
			// 					padding: 10rpx;
			// 				}
			// 			}
			// 		}
			// 	}
			// }

			.flowBefore-actions {
				background-color: #fff;
				position: fixed;
				bottom: 0;
				display: flex;
				width: 100%;
				height: 88rpx;
				box-shadow: 0 -2rpx 8rpx #e1e5ec;
				z-index: 999999;

				.buttom-btn {
					width: 100%;
					/* #ifndef MP */
					height: 88rpx !important;
					line-height: 88rpx !important;
					border-radius: 0 !important;

					&::after {
						border: none !important;
					}

					/* #endif */
					/* #ifdef MP */
					.u-btn {
						width: 100%;
						height: 88rpx !important;
						line-height: 88rpx !important;
						border-radius: 0 !important;

						&::after {
							border: none !important;
						}
					}

					/* #endif */
				}
			}
		}

		.u-select__header {
			position: fixed;
			bottom: 0;
			width: 100%;
			background-color: #FFFFFF;

			&::after {
				content: '';
				position: absolute;
				border-bottom: 0.5px solid #eaeef1;
				-webkit-transform: scaleY(0.5);
				transform: scaleY(0.5);
				bottom: 0;
				right: 0;
				left: 0;
			}
		}

		&__action {
			position: relative;
			line-height: $u-form-item-height;
			height: $u-form-item-height;

			&__icon {
				position: absolute;
				right: 20rpx;
				top: 50%;
				transition: transform .4s;
				transform: translateY(-50%);
				z-index: 1;

				&--reverse {
					transform: rotate(-180deg) translateY(50%);
				}
			}
		}

		&__hader {
			&__title {
				color: $u-content-color;
			}
		}

		&--border {
			border-radius: 6rpx;
			border-radius: 4px;
			border: 1px solid $u-form-item-border-color;
		}

		&__header {
			@include vue-flex;
			align-items: center;
			justify-content: space-between;
			height: 80rpx;
			padding: 0 40rpx;
		}

		&__body {
			width: 100%;
			height: 500rpx;
			overflow: hidden;
			background-color: #fff;

			&__picker-view {
				height: 100%;
				box-sizing: border-box;

				&__item {
					@include vue-flex;
					align-items: center;
					justify-content: center;
					font-size: 32rpx;
					color: $u-main-color;
					padding: 0 8rpx;
				}
			}
		}
	}
</style>