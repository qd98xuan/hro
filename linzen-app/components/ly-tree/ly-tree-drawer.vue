<template>
	<ly-drawer :visible="show" mode="right" width="80%" @close="close">
		<scroll-view :scroll-y="true" :style="{
				height:appWrapperHeight
			}">
			<ly-tree :tree-data="treeData" show-radio node-key="id" @check="handleCheck" />
		</scroll-view>

		<view class="footer">
			<view class="button confirm" @tap="handleConfirm">确定</view>
			<view class="button cancle" @tap="handleClose">取消</view>
		</view>
	</ly-drawer>
</template>

<script>
	import LyDrawer from './ly-drawer.vue'
	import LyTree from '@/components/ly-tree/ly-tree.vue'
	export default {
		name: 'lyTreeDrawer',

		components: {
			LyDrawer,
			LyTree
		},

		data() {
			const sysInfo = uni.getSystemInfoSync();

			return {
				showTree: false,
				// #ifdef H5
				appWrapperHeight: (sysInfo.screenHeight - uni.upx2px(100)) + 'px',
				// #endif
				// #ifndef H5
				appWrapperHeight: (sysInfo.windowHeight - uni.upx2px(100)) + 'px',
				// #endif
				treeData: [{
					id: 1,
					label: '一级 1',
					children: [{
						id: 3,
						label: '二级 2-1',
						children: [{
							id: 4,
							label: '三级 3-1-1'
						}, {
							id: 5,
							label: '三级 3-1-2'
						}]
					}, {
						id: 2,
						label: '二级 2-2',
						children: [{
							id: 6,
							label: '三级 3-2-1'
						}, {
							id: 7,
							label: '三级 3-2-2'
						}]
					}],
				}, {
					id: 21,
					label: '一级 21',
					children: [{
						id: 23,
						label: '二级 2-2-1',
						children: [{
							id: 24,
							label: '三级 2-3-1-1'
						}, {
							id: 25,
							label: '三级 2-3-1-2'
						}]
					}, {
						id: 22,
						label: '二级 2-2-2',
						children: [{
							id: 26,
							label: '三级 2-3-2-1'
						}, {
							id: 27,
							label: '三级 2-3-2-2'
						}]
					}],
				}, {
					id: 31,
					label: '一级 31',
					children: [{
						id: 33,
						label: '二级 3-2-1',
						children: [{
							id: 34,
							label: '三级 3-3-1-1'
						}, {
							id: 35,
							label: '三级 3-3-1-2'
						}]
					}, {
						id: 32,
						label: '二级 3-2-2',
						children: [{
							id: 36,
							label: '三级 3-3-2-1'
						}, {
							id: 37,
							label: '三级 2-3-2-2'
						}]
					}],
				}],
				selectedData: {}
			}
		},

		props: {
			show: Boolean
		},

		methods: {
			handleCheck(obj) {
				if (obj.node.checked) {
					this.selectedData = obj.data;
				} else {
					this.selectedData = void 0;
				}
			},

			handleClose() {
				this.$emit('close');
			},

			handleConfirm() {
				this.$emit('confirm', this.selectedData);
			}
		}
	}
</script>

<style lang="scss">
	.footer {
		position: fixed;
		z-index: 998;
		left: 0;
		bottom: 0;
		background: #FFFFFF;
		width: 100%;
		height: 100rpx;
		overflow: hidden;
		box-shadow: 1rpx 1rpx 8px #c7c7c7;
	}

	.footer .button {
		width: 50%;
		border: none;
		text-align: center;
		float: right;
		font-size: 30rpx;
		border-radius: 0;
		height: 100rpx;
		line-height: 100rpx;
	}

	.footer .button:after {
		width: 0;
		height: 0;
	}

	.footer .button.confirm {
		background: #409EFF;
		color: #fff;
	}

	.footer .button.cancle {
		background: #f5f5f5;
	}
</style>