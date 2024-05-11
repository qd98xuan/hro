<template>
	<view class="notData-box u-flex-col">
		<view class="u-flex-col notData-inner">
			<image :src="icon" mode="" class="notData-inner-img"></image>
			<text class="notData-inner-text">暂无数据</text>
		</view>
	</view>
</template>

<script>
	import resources from '@/libs/resources.js'
	export default {
		data() {
			return {
				icon: resources.message.nodata,
			}
		},
	}
</script>

<style lang="scss">
	.notData-box {
		width: 100%;
		height: 100%;
		justify-content: center;
		align-items: center;
		padding-top: 200px;
		z-index: 999;
		background-color: #f0f2f6;

		.notData-inner {
			width: 154px;
			height: 170px;
			align-items: center;

			.notData-inner-text {
				padding: 30rpx 0;
				color: #909399;
			}

			.notData-inner-img {
				width: 100%;
				height: 100%;
			}
		}
	}
</style>