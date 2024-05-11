<template>
	<div class="portal-todoList-box-body">
		<template v-if="list.length">
			<a class="item com-hover" @click="goDetail(item)" v-for="(item, i) in list" :key="i">
				<span class="name">{{item.fullName}}</span>
				<span class="time">{{item.creatorTime | date('yyyy-mm-dd')}}</span>
			</a>
		</template>
		<view v-else class="notData-box u-flex-col">
			<view class="u-flex-col notData-inner">
				<image :src="icon" mode="" class="iconImg"></image>
				<text class="notData-inner-text">暂无数据</text>
			</view>
		</view>
	</div>
</template>
<script>
	import {
		getFlowTodo
	} from '@/api/home'
	import resources from '@/libs/resources.js'
	export default {
		components: {},
		props: {
			config: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				list: [],
				icon: resources.message.nodata,
			}
		},
		created() {
			this.getData()
		},
		methods: {
			getData() {
				getFlowTodo().then(res => {
					this.list = res.data.list.slice(0, 7)
				})
			},
			goDetail(item) {
				if (this.config.platform === 'mp') return
				let config = {
					creatorTime: item.creatorTime,
					enCode: item.enCode,
					flowId: item.flowId,
					formType: item.formType,
					fullName: item.fullName,
					id: item.processId,
					processId: item.processId,
					status: item.status,
					taskNodeId: item.taskNodeId,
					taskId: item.taskOperatorId,
					type: item.type,
					opType: 1,
				}
				uni.navigateTo({
					url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(
						JSON.stringify(config), "UTF-8")
				});
			}
		}
	}
</script>
<style lang="scss">
	.portal-todoList-box-body {
		padding: 42rpx 10rpx 10rpx;
		max-height: 472rpx;
		overflow-y: scroll;

		.item {
			display: block;
			line-height: 40rpx;
			font-size: 0;
			margin-bottom: 24rpx;
			cursor: pointer;


			.name {
				font-size: 28rpx;
				display: inline-block;
				width: calc(100% - 180rpx);
				white-space: nowrap;
				text-overflow: ellipsis;
				overflow: hidden;
				word-break: break-all;
				vertical-align: top;
			}

			.time {
				font-size: 28rpx;
				display: inline-block;
				color: #999;
				width: 180rpx;
				text-align: right;
			}
		}
	}

	.notData-box {
		width: 100%;
		height: 100%;
		justify-content: center;
		align-items: center;

		.notData-inner {
			width: 280rpx;
			height: 308rpx;
			align-items: center;

			.iconImg {
				width: 100%;
				height: 100%;
			}

			.notData-inner-text {
				padding: 30rpx 0;
				color: #909399;
			}
		}
	}
</style>