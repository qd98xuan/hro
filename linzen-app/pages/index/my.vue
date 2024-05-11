<template>
	<view class="my-v" v-if="loading">
		<view class="u-flex user-box u-p-l-20 u-p-r-10 u-p-b-20">
			<view class="u-m-r-10">
				<u-avatar size="140" @click='chooseAvatar' :src='avatarSrc'></u-avatar>
			</view>
			<view class="u-flex-1 f-right" @click="personalPage('/pages/my/personalData/index')">
				<view class="u-font-18 u-m-l-16">{{baseInfo.realName}}</view>
				<view class="u-m-l-10 u-p-10">
					<u-icon name="arrow-right" color="#969799" size="28"></u-icon>
				</view>
			</view>
		</view>
		<view class="u-m-t-20">
			<view class="" style="background-color: #fff;">
				<u-cell-group style="padding: 0 20rpx;" :border="false">
					<u-cell-item title="我的组织" @click="openPage('/pages/my/business/index','Organize')"
						:title-style="titleStyle">
						<text class="icon-zen icon-zen-zuzhi u-m-r-16 u-font-36" slot="icon" style="color: #303133;" />
					</u-cell-item>
					<u-cell-item title=" 我的岗位" @click="openPage('/pages/my/business/index','Position')"
						:title-style="titleStyle">
						<text class="icon-zen icon-zen-position1 u-m-r-16 u-font-36" slot="icon"
							style="color: #303133;" />
					</u-cell-item>
					<u-cell-item title="我的下属" @click="openPage('/pages/my/subordinate/index')"
						:title-style="titleStyle">
						<text class="icon-zen icon-zen-generator-section u-m-r-16 u-font-36" slot="icon"
							style="color: #303133;" />
					</u-cell-item>
					<!-- #ifndef H5 -->
					<u-cell-item title="扫一扫" @click="scanCode()" :title-style="titleStyle">
						<text class="icon-zen icon-zen-scanCode1 u-m-r-16 u-font-36"
							style="font-weight: bold;color: #303133;" slot="icon" />
					</u-cell-item>
					<!-- #endif -->
					<!-- #ifdef APP-PLUS -->
					<u-cell-item title="账号安全" @click="openPage('/pages/my/accountSecurity/index')"
						:title-style="titleStyle">
						<text class="icon-zen icon-zen-zhanghao u-m-r-16 u-font-36" slot="icon" style="color: #303133;" />
					</u-cell-item>
					<!-- #endif -->
					<u-cell-item title="设置" @click="openPage('/pages/my/settings/index')" :title-style="titleStyle"
						:border-bottom="false">
						<text class="icon-zen icon-zen-shezhi u-m-r-16 u-font-36" slot="icon" style="color: #303133;" />
					</u-cell-item>
				</u-cell-group>
			</view>
		</view>
		<view class="u-p-t-20">
			<view class="logout-cell" hover-class="u-cell-hover" @click="logout">退出登录</view>
		</view>
	</view>
</template>
<script>
	import IndexMixin from './mixin.js'
	import {
		UpdateAvatar
	} from '@/api/common'
	import {
		UserSettingInfo
	} from '@/api/common'
	export default {
		mixins: [IndexMixin],
		data() {
			return {
				titleStyle: {
					color: '#303133'
				},
				avatarSrc: '',
				baseInfo: {},
				loading: false
			}
		},
		computed: {
			baseURL() {
				return this.define.comUploadUrl
			},
			baseURL2() {
				return this.define.baseURL
			},
			token() {
				return uni.getStorageSync('token')
			},
			report() {
				return this.define.report
			}
		},
		onShow() {
			UserSettingInfo().then(res => {
				this.baseInfo = res.data || {}
				this.avatarSrc = this.baseURL2 + this.baseInfo.avatar
				this.loading = true
			})
		},
		methods: {
			chooseAvatar() {
				uni.chooseImage({
					count: 1,
					sizeType: ['original', 'compressed'],
					success: (res) => {
						let tempFilePaths = res.tempFilePaths[0]
						uni.uploadFile({
							url: this.baseURL + 'userAvatar',
							filePath: tempFilePaths,
							name: 'file',
							header: {
								'Authorization': this.token
							},
							success: (uploadFileRes) => {
								let data = JSON.parse(uploadFileRes.data)
								UpdateAvatar(data.data.name).then(res => {
									this.$u.toast('头像更换成功')
									this.avatarSrc = this.baseURL2 + data.data.url
								})
							},
							fail: (err) => {
								this.$u.toast('头像更换失败')
							}
						});
					}
				});
			},
			openPage(path, type) {
				if (!path) return
				let url = !!type ? path + '?majorType=' + type : path
				uni.navigateTo({
					url: url
				})
			},
			personalPage(path) {
				if (!path) return
				uni.navigateTo({
					url: path + '?baseInfo=' + encodeURIComponent(JSON
						.stringify(this.baseInfo))
				})
			},
			isJSON(str) {
				try {
					var obj = JSON.parse(str);
					if (typeof obj == 'object' && obj) {
						return true;
					} else {
						return false;
					}
				} catch (e) {
					return false;
				}
			},
			logout() {
				uni.showModal({
					title: '提示',
					content: '确定退出当前账号吗？',
					success: res => {
						if (res.confirm) {
							this.$store.dispatch('user/logout').then(() => {
								uni.closeSocket()
								uni.reLaunch({
									url: '/pages/login/index'
								})
							})
						}
					}
				})
			},
			scanCode() {
				uni.scanCode({
					success: res => {
						if (this.isJSON(res.result.trim())) {
							const result = JSON.parse(res.result.trim())
							if (result.t === 'ADP') {
								let config = {
									isPreview: 1,
									moduleId: result.id,
									previewType: result.previewType
								}
								uni.navigateTo({
									url: '/pages/apply/dynamicModel/index?config=' + this.base64
										.encode(JSON.stringify(config), "UTF-8"),
									fail: (err) => {
										this.$u.toast("暂无此页面")
									}
								})
							}
							if (result.t === 'DFD') {
								uni.navigateTo({
									url: '/pages/apply/dynamicModel/scanForm?config=' + JSON.stringify(
										result),
									fail: (err) => {
										this.$u.toast("暂无此页面")
									}
								})
							}
							if (result.t === 'WFP') {
								uni.navigateTo({
									url: '/pages/workFlow/scanForm/index?config=' + JSON.stringify(
										result),
									fail: (err) => {
										this.$u.toast("暂无")
									}
								})
							}
							if (result.t === 'report') {
								let url =
									`${this.report}/preview.html?id=${result.id}&token=${this.token}&page=1&from=menu`
								uni.navigateTo({
									url: '/pages/apply/externalLink/index?url=' +
										encodeURIComponent(
											url) + '&fullName= ' + result.fullName,
									fail: (err) => {
										this.$u.toast("暂无")
									}
								})
							}
							if (result.t === 'portal') {
								uni.navigateTo({
									url: '/pages/portal/scanProtal/index?id=' + result.id,
									fail: (err) => {
										this.$u.toast("暂无")
									}
								})
							}
						} else {
							uni.navigateTo({
								url: '/pages/my/scanResult/index?result=' + res.result,
								fail: (err) => {
									this.$u.toast("暂无此页面")
								}
							})
						}
					}
				});
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.my-v {
		/deep/ .u-cell {
			height: 112rpx;
			padding: 0;
		}

		.user-box {
			background-color: #fff;
		}

		.logout-cell {
			text-align: center;
			font-size: 28rpx;
			height: 112rpx;
			background-color: #fff;
			color: #d9001b;
			line-height: 112rpx;
		}

		.f-right {
			display: flex;
			flex-direction: row;
			justify-content: space-between;
			align-items: center;
			text-align: center;
		}
	}
</style>