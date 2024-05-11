<template>
	<view class="steps">
		<view>
			<view class="node-wrap" v-if="!options.isBranchFlow&&!options.isInterflow&&options.type!='condition'">
				<view class="node-wrap-box" :class="options.type" v-if="options.type">
					<view class="flow-path-card" :class="[options.state,options.type,options.type?'start-node':'']">
						<view class="header">
							<view class="title-box" style="height: 100%; width: 190px;"
								v-if="options.type != 'subFlow' || !options.state">
								<span class="title-text">{{options.properties.title}}</span>
							</view>
							<view v-else class="title-box" style="height: 100%; width: 190px;"
								@click.stop="clickFlowCard(options)">
								<span class="title-text">{{options.properties.title}}</span>
							</view>
							<u-tag class="async-state" v-if="options.type == 'subFlow'" size="mini"
								:text="options.properties.isAsync ? '异步' : '同步'" plain style="right:10px"></u-tag>
						</view>
						<view class="body"><span class="text">{{options.content}}</span></view>
					</view>
					<div class="add-node-btn-box flex justify-center"></div>
				</view>
			</view>
			<div class="branch-wrap" v-if="options.conditionNodes&&options.conditionNodes.length">
				<div class="branch-box-wrap ">
					<div class="branch-box flex justify-center relative">
						<span class="line"></span>
						<div class="col-box" v-for="item,index in options.conditionNodes" :key="index">
							<div class="center-line"></div>
							<div class="top-cover-line"></div>
							<div class="bottom-cover-line"></div>
							<view class="node-wrap ">
								<view class="node-wrap-box branchFlow" v-if="item.type">
									<view class="flow-path-card" :class="[item.state,item.type]">
										<view class="header">
											<view class="title-box" style="height: 100%; width: 190px;">
												<span class="title-text">{{item.properties.title}}</span>
											</view>
											<u-tag class="async-state" v-if="item.type == 'subFlow'" size="mini"
												:text="item.properties.isAsync ? '异步' : '同步'" plain style="right:10px">
											</u-tag>
										</view>
										<view class="body"><span class="text">{{item.content}}</span></view>
									</view>
									<div class="add-node-btn-box flex justify-center"></div>
								</view>
							</view>
							<FlowCard ref="FlowCard" v-if="item.conditionNodes" :options="item" />
							<FlowCard ref="FlowCard"
								v-if="item.childNode&&(item.type =='condition'|| (item.type === 'approver' && (item.isInterflow || item.isBranchFlow)))"
								:options="item.childNode" />
						</div>
					</div>
					<div class="add-node-btn-box flex justify-center"></div>
				</div>
			</div>
			<FlowCard ref="FlowCard" v-if="options.childNode" :options="options.childNode" @showTabs="clickFlowCard">
			</FlowCard>
		</view>
	</view>
</template>

<script>
	import FlowCard from './FlowCard.vue'
	export default {
		components: {
			FlowCard
		},
		name: 'FlowCard',
		props: {
			options: {},
		},
		methods: {
			clickFlowCard(options) {
				this.$emit('showTabs', options)
			}
		}
	}
</script>
<style scoped lang="scss">
	@import "./FlowCard.scss";
	$bg-color: #fff;

	.branch-wrap {
		display: flex;
		justify-content: center;

		.branch-box {
			background: $bg-color;

			>.col-box {
				&:first-of-type {

					&::before,
					&::after {
						background: $bg-color !important;
					}
				}

				&:last-of-type {

					&::before,
					&::after {
						background: $bg-color;
					}
				}
			}
		}
	}

	.node-wrap-box.approver::before {
		background: #fff;
	}

	.flow-path-card {

		&.start-node,
		&.approver,
		&.subFlow {
			.header {
				background-color: #b6b6b6;
			}
		}

		&.state-past {
			.header {
				background-color: #67c23a;
			}
		}

		&.state-curr {
			.header {
				background-color: #1890ff;
			}
		}
	}

	.node-wrap-box .start {
		margin-top: 15rpx;
	}
</style>