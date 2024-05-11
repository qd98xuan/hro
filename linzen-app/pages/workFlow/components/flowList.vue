<template>
	<view class="flow-list flowBefore">
		<view class="flow-list-box">
			<view class="item" v-for="(item, index) in list" :key="item.id" @click="handleClick(item)">
				<view class="item-left">
					<view class="item-left-top">
						<view class="u-m-r-8" v-if="item.delegateUser">
							<u-tag text="委托" type="success" size="mini" />
						</view>
						<view class='common-lable'
							:class="{'urgent-lable':item.flowUrgent==2,'important-lable':item.flowUrgent==3}">
							{{getLableValue(item.flowUrgent)}}
						</view>
						<text class="title u-font-28 u-line-1">{{item.fullName}}</text>
					</view>
					<text class="title u-line-1 u-font-24">审批节点：<text
							class="titInner">{{item.thisStep ? item.thisStep : '暂无'}}</text></text>
					<text class="time title u-font-24">{{title}}：<text
							class="titInner">{{item.creatorTime | date('yyyy-mm-dd hh:MM:ss')}}</text></text>
				</view>
				<view class="item-right">
					<image :src="item.flowStatus" mode="widthFix" class="item-right-img"></image>
				</view>
			</view>
		</view>
	</view>
</template>
<script>
	import resources from '@/libs/resources.js'
	export default {
		name: "FlowList",
		props: {
			list: {
				type: Array,
				default: () => []
			},
			opType: {
				type: Number,
				default: 1
			}
		},
		data() {
			return {
				title: ''
			};
		},
		created() {
			this.title = this.opType == 1 ? '接收时间' : this.opType == 2 ? '办理时间' : '抄送时间'
		},
		methods: {
			handleClick(item) {
				const config = {
					id: item.processId,
					enCode: item.flowCode,
					flowId: item.flowId,
					formType: item.formType,
					opType: this.opType,
					status: item.status,
					taskNodeId: item.thisStepId,
					fullName: item.fullName,
					taskId: item.id
				}
				uni.navigateTo({
					url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(JSON.stringify(config),
						"UTF-8")
				})
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
			}
		}
	};
</script>
<style scoped lang="scss">
	.item-left-top {
		display: flex;
		width: 100%;

		.common-lable {
			font-size: 24rpx;
			padding: 2rpx 8rpx;
			margin-right: 8rpx;
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

		.title {
			width: unset;
			flex: 1;
			min-width: 0;
		}
	}
</style>