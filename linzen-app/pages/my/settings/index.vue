<template>
	<view class="settings-v">
		<u-cell-group class="u-p-l-20 u-p-r-20" :border="false">
			<!-- #ifdef APP-PLUS -->
			<u-cell-item title="用户协议" @click='openPage(agreement)' :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="隐私政策" @click='openPage(policy)' :title-style="titleStyle"></u-cell-item>
			<!-- #endif -->
			<u-cell-item title="修改密码" @click="modifyPsd('/pages/my/modifyPsd/index')" :title-style="titleStyle">
			</u-cell-item>
			<u-cell-item title="联系我们" @click="modifyPsd('/pages/my/contactUs/index')" :title-style="titleStyle">
			</u-cell-item>
			<u-cell-item title="关于平台" @click="modifyPsd('/pages/my/abouts/index')" :title-style="titleStyle"
				:border-bottom="false">
			</u-cell-item>
		</u-cell-group>
	</view>
</template>

<script>
	import resources from '@/libs/resources.js'
	export default {
		data() {
			return {
				// #ifdef APP-PLUS
				agreement: resources.userAgreement,
				policy: resources.privacyPolicy,
				// #endif
				titleStyle: {
					color: '#303133'
				},
			};
		},
		methods: {
			modifyPsd(path) {
				if (!path) return
				uni.navigateTo({
					url: path
				})
			},
			// #ifdef APP-PLUS
			openPage(url) {
				plus.runtime.openURL(url);
			}
			// #endif
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	/deep/.u-cell {
		height: 112rpx;
		padding: 20rpx 0;
	}

	.settings-v {
		background-color: #fff;
	}
</style>