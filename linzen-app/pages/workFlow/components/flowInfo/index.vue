<template>
	<view class="steps">
		<movable-area class="movableArea">
			<movable-view class="movableView" :x="x" :out-of-bounds="true" direction="all" :inertia="true" :scale="true"
				:animation="false">
				<FlowCard :options="options" ref="FlowCard" @showTabs="getFlowInfo" />
				<section class="end-node">流程结束</section>
			</movable-view>
		</movable-area>
	</view>
</template>
<script>
	import FlowCard from './FlowCard.vue'
	export default {
		components: {
			FlowCard
		},
		name: 'steps',
		props: {
			options: {},
			config: {}
		},
		data() {
			return {
				x: 30
			}
		},
		mounted() {
			let movableView = uni.createSelectorQuery().in(this).select(".movableView");
			movableView.boundingClientRect(res => {
				const allWith = res.width
				const winWidth = uni.getSystemInfoSync().windowWidth
				this.x = -(allWith / 2) + (winWidth / 2)
			}).exec();
		},
		methods: {
			getFlowInfo(options) {
				let data = {
					subId: options.id,
					nodeId: options.nodeId,
					prevId: options.prevId,
					type: options.type,
					...this.config
				}
				uni.navigateTo({
					url: './subFlowForm?config=' + this.base64.encode(
						JSON.stringify(data), "UTF-8")
				})
			}
		}
	}
</script>
<style scoped lang="scss">
	@import "./FlowCard.scss";

	.movableArea {
		width: 100%;
		/* #ifdef H5 */
		height: calc(100vh - 108rpx - 60rpx - 88rpx - 88rpx);
		/* #endif */
		/* #ifndef H5 */
		height: calc(100vh - 108rpx - 60rpx - 88rpx);
		/* #endif */
	}

	.movableView {
		width: unset;
		height: unset;
	}

	.end-node {
		padding-bottom: 15rpx;
	}
</style>
