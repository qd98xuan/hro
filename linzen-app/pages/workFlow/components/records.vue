<template>
	<view class="records">
		<view class="records-item" v-for="(item,i) in options" :key="i">
			<!-- <view class="record-cell">
				<text class="title">节点名称：</text>
				<text class="text-link" @click="goDetail(item)">{{item.nodeName}}</text>
			</view> -->
			<view class="record-cell">
				<text class="title">节点名称：</text>
				<text class="content">{{item.nodeName}}</text>
			</view>
			<view class="record-cell">
				<text class="title">操作人员：</text>
				<text class="content">{{item.userName}}</text>
			</view>
			<view class="record-cell">
				<text class="title">接收时间：</text>
				<text class="content">{{item.creatorTime|date('yyyy-mm-dd hh:MM:ss')}}</text>
			</view>
			<view class="record-cell">
				<text class="title">操作时间：</text>
				<text class="content">{{item.handleTime|date('yyyy-mm-dd hh:MM:ss')}}</text>
			</view>
			<view class="record-cell">
				<text class="title">执行动作：</text>
				<text class="content">
					<text class="circle" :style="{background:colorList[item.handleStatus||0]}"></text>
					{{statusList[item.handleStatus||0]}}
					<text
						v-if="item.handleStatus==5||item.handleStatus==6||item.handleStatus==7||item.handleStatus==10">
						：{{item.operatorId}}
					</text>
				</text>
			</view>
			<view class="record-cell" v-if="item.fileList&&item.fileList.length">
				<text class="title">附件：</text>
				<linzen-file v-model="item.fileList" detailed />
			</view>
			<view class="record-cell" v-if="item.handleOpinion">
				<text class="title">备注：</text>
				<text class="content">{{item.handleOpinion}}</text>
			</view>
			<image class="record-cell-img" :src="item.signImg" mode="widthFix" @click="previewImage(item.signImg)"
				v-if="item.signImg">
		</view>
	</view>
</template>

<script>
	export default {
		name: 'Records',
		props: {
			options: {
				type: Array,
				default () {
					return []
				}
			},
			endTime: {
				type: Number,
				default: 0
			},
			flowId: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				colorList: ['rgba(242,68,68,0.39)', 'rgba(35,162,5,0.39)', 'rgba(21,157,120,0.39)', 'rgba(21,21,157,0.39)',
					'rgba(186,33,33,0.39)', 'rgba(25,185,185,0.39)', 'rgba(50,191,61,0.39)', 'rgba(49,151,214,0.39)',
					'rgba(185,123,6,0.39)', 'rgba(45,94,186,0.39)', 'rgba(50,191,61,0.39)', 'rgba(255, 0, 0, 0.39)',
					'rgba(0, 128, 0, 0.39)', 'rgba(172,214,58,0.39)'
				],
				statusList: ['退回', '同意', '发起', '撤回', '终止', '指派', '后加签', '转办', '变更', '复活', '前加签', '挂起', '恢复', '转向'],
			}
		},
		methods: {
			goDetail(item) {
				let opType = '-1'
				if ([1, 2, 4, 5].includes(item.status)) opType = 0
				const config = {
					id: item.taskId,
					opType: opType,
					status: item.status,
					readonly: 1,
					formRecords: 1,
					title: item.nodeName,
					flowId: this.flowId,
					taskNodeId: item.taskNodeId,
					formType: item.formType,
					enCode: item.enCode
				}
				uni.navigateTo({
					url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(JSON.stringify(config),
						"UTF-8")
				})
			},
			previewImage(url) {
				uni.previewImage({
					urls: [url],
					current: url,
					success: () => {},
					fail: () => {
						uni.showToast({
							title: '预览图片失败',
							icon: 'none'
						});
					}
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	.records {
		.records-item {
			position: relative;
			background-color: #fff;
			margin-bottom: 20rpx;
			padding: 28rpx 32rpx 10rpx;

			.record-cell {
				color: #303133;
				line-height: 42rpx;
				font-size: 28rpx;
				padding-bottom: 20rpx;
				display: flex;

				.title {
					text-align: right;
					width: 140rpx;
					display: inline-block;
					flex-shrink: 0;
				}

				.content {
					color: #909399;

					.circle {
						width: 14rpx;
						height: 14rpx;
						border-radius: 50%;
						margin-right: 6rpx;
						display: inline-block;
						margin-bottom: 2rpx;
					}
				}

				.text-link {
					color: #2979FF;
				}
			}

			.record-cell-img {
				position: absolute;
				width: 200rpx;
				top: 28rpx;
				right: 32rpx;
			}
		}
	}
</style>