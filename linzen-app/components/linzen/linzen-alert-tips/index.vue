<template>
	<view class="alertTips">
		<u-alert-tips :type="type" :title="title" :show-icon="showIcon" :description="description"
			:close-able="closable" :close-text="closeText" @close="show=false" :show="show" :title-style="titleStyle"
			:desc-style="descStyle"></u-alert-tips>
	</view>
</template>
<script>
	export default {
		props: {
			type: {
				type: String,
				default: 'success'
			},
			title: {
				type: String,
				default: '这是一个提示'
			},
			tagIcon: {
				type: String,
				default: 'icon-zen icon-zen-generator-alert'
			},
			showIcon: {
				type: Boolean,
				default: false
			},
			closable: {
				type: Boolean,
				default: false
			},
			description: {
				type: String,
				default: ''
			},
			closeText: {
				type: String,
				default: ''
			}
		},
		data() {
			return {
				show: true,
				titleStyle: {
					'word-break': 'break-all',
					'line-height': '34rpx'
				},
				descStyle: {
					'word-break': 'break-all',
					'line-height': '40rpx',
				},
				key: +new Date()
			}
		}
	}
</script>

<style lang="scss">
	.alertTips {
		width: 100%;
		background-color: #fff;

		.u-alert-tips {
			min-height: 70rpx;
		}
	}
</style>