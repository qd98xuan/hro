<template>
	<view class="linzen-text" :style="_style">{{content}}</view>
</template>
<script>
	export default {
		name: 'linzen-text',
		props: {
			textStyle: {
				type: Object,
				default: () => ({
					// 'font-size': ' 24rpx',
					// "color": '#00000',
					// 'text-align': 'center',
					// 'line-height': '60rpx',
					// 'font-weight': 'normal',
					// 'font-style': 'normal',
					// 'text-decoration': 'none',
				})
			},
			content: {
				default: ''
			}
		},
		computed: {
			_style() {
				return {
					...this.textStyle,
					'line-height': this.textStyle['line-height'] * 2 + 'rpx',
					'font-size': this.textStyle['font-size'] * 2 + 'rpx'
				}
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-text {
		width: 100%;
		padding: 20rpx 32rpx;
		margin: 0;
	}
</style>