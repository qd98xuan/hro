<template>
	<view class="approve-v u-flex-col">
		<u-tabs :list="approve_list" :current="approveIndex" @change="approveChange" height="90" :show-bar="false"
			:active-item-style='activeItemStyle' font-size="28">
		</u-tabs>
		<view class="approve_list">
			<view v-if="list.length">
				<view class="u-flex approve_box" v-for="(item,index) in list" :key="index">
					<view class="left-box">{{item.fullName}}</view>
					<view class="right-box">
						<view v-for="(child,i) in item.list" :key="i" class="right-box-item">
							<view class='head-avatar'>
								<u-avatar size='mini' :src="baseURL+child.headIcon" />
							</view>
							<view class="item-box">
								<view class="item-top">
									<view class="userName">{{child.userName}}</view>
									<u-tag :text="child.txt" mode="light" :type="child.tagType" />
								</view>
								<view class="item-content" v-if="child.handleOpinion && child.handleStatus!=2">
									{{child.handleOpinion}}
								</view>
								<linzen-file v-if="child.fileList.length && child.handleStatus!=2"
									v-model="child.fileList" detailed />
								<view class="dateTime">
									{{$u.timeFormat(child.handleTime,'yyyy-mm-dd hh:MM:ss')}}
								</view>
							</view>
						</view>
					</view>
				</view>
			</view>
			<view class="noContent" v-else>
				<img :src="nodataIcon" class="img">
				<view>暂无数据</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getRecordList
	} from '@/api/workFlow/flowBefore'
	import resources from '@/libs/resources.js'
	export default {
		name: "RecordSummary",
		props: {
			processId: {
				type: String,
				default: ''
			},
			summaryType: {
				default: "0"
			},
		},
		data() {
			return {
				approve_list: [{
					name: '按部门汇总',
					id: '1'
				}, {
					name: '按岗位汇总',
					id: '3'
				}],
				list: [],
				approveIndex: 0,
				tabI: 1,
				nodataIcon: resources.message.nodata,
				activeItemStyle: {
					'background-color': '#f0f2f6',
					'border-radius': '18rpx 18rpx 0 0',
				}
			}
		},
		created() {
			this.init()
		},
		computed: {
			baseURL() {
				return this.define.baseURL
			},
		},
		watch: {
			approveIndex(val) {
				this.init()
			}
		},
		methods: {
			init() {
				const query = {
					category: this.tabI,
					type: this.summaryType
				}
				getRecordList(this.processId, query).then(res => {
					this.list = res.data || []
					if (this.list.length) {
						this.list.forEach((o, i) => {
							o.list.forEach(j => {
								j.fileList = j.fileList ? JSON.parse(j.fileList) : []
							})
							o.list = o.list.map(i => ({
								txt: i.handleStatus == 0 ? '退回' : i.handleStatus == 1 ? '同意' :
									i.handleStatus == 2 ? '发起' : i.handleStatus == 3 ? '撤回' : i
									.handleStatus == 4 ? '终止' : i.handleStatus == 5 ? '指派' : i
									.handleStatus == 6 ? '后加签' : i.handleStatus == 10 ? '前加签' :
									i.handleStatus == 8 ? '变更' : i.handleStatus == 13 ? '转向' :
									'转审',
								tagType: i.handleStatus == 0 ? 'error' : i.handleStatus == 1 ?
									'success' : i.handleStatus == 3 || i.handleStatus == 4 || i
									.handleStatus == 8 || i.handleStatus == 13 ?
									'warning' : "primary",
								...i
							}))
						})
					}
				}).catch(() => {})
			},
			approveChange(index) {
				this.approveIndex = index;
				this.tabI = this.approve_list[index].id
			},
		}
	}
</script>
<style lang="scss" scoped>
	.approve-v {
		.u-tabs {
			height: 90rpx !important;

			:deep(.u-tab-item) {
				margin-top: 20rpx;
				height: 74rpx !important;
				line-height: 73rpx !important;
			}
		}

		.approve_list {
			width: 100%;
			margin-top: 20rpx;
			padding: 0 20rpx;

			.u-tag {
				padding: 10rpx !important;
			}

			.approve_box {
				width: 100%;
				box-shadow: 0 2px 12px 0 rgba(0, 0, 0, .1);
				border-radius: 20rpx;
				margin-bottom: 20rpx;
				background-color: #fff;
				position: relative;

				.left-box {
					padding: 0 12rpx;
					text-align: center;
					writing-mode: vertical-rl;
					letter-spacing: 4rpx;

				}

				.right-box {
					height: 100%;
					flex: 1;
					min-width: 0;
					padding: 20rpx 0rpx;
					border-left: 1px solid #e4e7ed;

					.right-box-item {
						display: flex;

						.head-avatar {
							padding: 0 16rpx 0 20rpx;
						}

						&:nth-child(2n) {
							border-top: 1rpx solid #e4e7ed;
							padding-top: 20rpx;
						}

						&:first-child {
							padding-bottom: 20rpx;
						}

						.item-box {
							flex: 1;
							min-width: 0;
							font-size: 26rpx;

							.item-top {
								display: flex;
								align-items: center;
								padding-right: 20rpx;

								.userName {
									flex: 1;
									min-width: 0;
									overflow: hidden;
									text-overflow: ellipsis;
									white-space: nowrap;
									padding-right: 20rpx;
								}
							}


							.item-content {
								text-align: justify;
								text-justify: newspaper;
								word-break: break-all;
								padding: 20rpx 0;
								color: #636569;
							}

							.dateTime {
								font-weight: 400;
								padding-top: 20rpx;
							}
						}
					}
				}
			}
		}

		.noContent {
			text-align: center;
			padding: 58% 0;

			.img {
				width: 154px;
				height: 118px;
			}
		}
	}
</style>