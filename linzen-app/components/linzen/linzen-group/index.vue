<template>
	<view class="u-flex linzen-group-box">
		<view class="linzen-group" :style="{'text-align':contentPosition}" @click="cIcon()">{{content}}
			<u-icon :name="helpMessage? 'question-circle-fill':''" class="u-m-l-10" color="#a0acb7"></u-icon>
		</view>
	</view>

</template>
<script>
	export default {
		name: 'linzen-group',
		props: {
			content: {
				type: String,
				default: ''
			},
			helpMessage: {
				type: String,
				default: ''
			},
			contentPosition: {
				type: String,
				default: 'left'
			},
		},
		methods: {
			cIcon() {
				if (!this.helpMessage) return
				this.$emit('groupIcon')
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-group-box {
		width: 100%;

		.linzen-group {
			width: 100%;
			color: #333333;
			font-size: 32rpx;
			line-height: 70rpx;
			margin: 0;
			padding: 0 20rpx;
		}
	}
</style>