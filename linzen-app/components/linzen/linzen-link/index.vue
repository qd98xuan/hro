<template>
	<view class="txtLink" :style="textStyle" @click="jump">
		<text>{{content}}</text>
	</view>
</template>

<script>
	export default {
		props: {
			content: {
				type: String,
				default: '文本链接'
			},
			href: {
				type: String,
				default: ''
			},
			target: {
				type: String,
				default: ''
			},
			textStyle: {
				type: Object,
				default: {}
			}
		},
		data() {
			return {

			}
		},
		created() {

		},
		methods: {
			jump(event) {
				this.$emit('click', event)
				if (this.href == '') return this.$u.toast("未配置跳转链接")
				if (this.target === '_self') {
					uni.navigateTo({
						url: '/pages/apply/externalLink/index?url=' + this.href,
						fail: (err) => {
							this.$u.toast("暂无此页面")
						}
					})
				} else {
					// #ifdef APP-PLUS
					plus.runtime.getProperty(plus.runtime.appid, (wgtinfo) => {
						plus.runtime.openURL(this.href)
					})
					// #endif
					// #ifndef APP-PLUS
					uni.navigateTo({
						url: '/pages/apply/externalLink/index?url=' + this.href,
						fail: (err) => {
							this.$u.toast("暂无此页面")
						}
					})
					// #endif
				}
			}
		}
	}
</script>

<style lang="scss">
	.txtLink {
		width: 100%;
		height: 110rpx;
		background-color: #fff;
		color: #1890ff;
		line-height: 110rpx;
		text-align: center;
		// padding: 20rpx;
		box-sizing: border-box;
		padding: 0 20rpx;
		margin-bottom: 20rpx;
	}
</style>
