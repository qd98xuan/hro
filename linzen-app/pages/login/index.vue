<template>
	<view class="logo-v">
		<view class="login-bg">
			<image src="https://app.cdn.linzensoft.com/image/login-bg.png" mode="widthFix"></image>
			<view class="logoImg">
				<u-image :src="appIcon" mode="widthFix">
					<u-image slot="error" src="/static/logo.png" mode="widthFix">
					</u-image>
				</u-image>
			</view>
			<view class="login-version">
				<view class="login-version-text">{{sysConfigInfo.sysVersion || define.sysVersion}}</view>
			</view>
		</view>
		<view class="logo-hd u-flex-col">
			<view class="loginSwitch u-flex-col">
				<view class="loginInputBox u-flex-col" v-show="!isSso && !ssoLoading">
					<u-form :model="formData" :rules="rules" ref="dataForm" :errorType="['toast']" label-position="left"
						label-width="150" label-align="left">
						<u-form-item prop="account">
							<u-input input-align='left' v-model="formData.account" placeholder="请输入帐号" @focus="onFocus"
								@blur="onBlur">
							</u-input>
						</u-form-item>
						<u-form-item prop="password">
							<u-input input-align='left' v-model="formData.password" type="password" placeholder="请输入密码">
							</u-input>
						</u-form-item>
						<u-form-item prop="code" required v-if="needCode">
							<view class="u-flex code-box">
								<u-input v-model="formData.code" placeholder="验证码" input-align='left'></u-input>
								<view style="flex: 0.1;">
									<u-image :showLoading="true" :src="baseURL+imgUrl" width="130px" height="38px"
										@click="changeCode">
									</u-image>
								</view>
							</view>
						</u-form-item>
					</u-form>
					<view class="loginBtnBox u-m-t-64">
						<u-button @click="login" type="primary" :loading="loading">{{ loading ? "登录中...":"登录"}}
						</u-button>
					</view>
					<template v-if="socialsList.length">
						<u-divider margin-top='40' margin-bottom='40' half-width='100%'>其他登录方式</u-divider>
						<view class="other-list">
							<block v-for="(item,i) in socialsList" :key="i">
								<!--#ifdef MP-WEIXIN -->
								<view class="other-item" v-if="item.enname==='wechat_open'" :title="item.name"
									@click="wechatLogin()"><i :class="item.icon" />
								</view>
								<!-- #endif -->
								<!-- #ifndef MP-WEIXIN -->
								<!--#ifdef APP-PLUS-->
								<view class="other-item" v-if="item.enname==='qq'" :title="item.name"
									@click="qqOtherlogin()"><i :class="item.icon" />
								</view>
								<!-- #endif -->
								<!-- #ifdef H5 || MP-WEIXIN -->
								<view class="other-item" v-if="item.enname==='qq'" :title="item.name"
									@click="otherslogin(item.enname,item.renderUrl)"><i :class="item.icon" />
								</view>
								<!-- #endif -->
								<view class="other-item" v-else :title="item.name"
									@click="otherslogin(item.enname,item.renderUrl)"><i :class="item.icon" />
								</view>
								<!-- #endif -->
							</block>
						</view>
					</template>
				</view>
				<view class="sso-login-btn" v-show="isSso  && !ssoLoading">
					<u-button @click="ssoLogin" type="primary" :loading="loading">{{ loading ? "登录中...":"登录"}}
					</u-button>
				</view>
			</view>
		</view>
		<view class="copyright">{{copyright}}</view>
		<u-popup v-model="show" mode="left" width="90%" height="100%">
			<view class="mian">
				<view class='top'>
					<view class='img'>
						<image style="height:50rpx;width: 50rpx;text-align: center;"
							src="/static/image/tabbar/contactsHL.png" mode="widthFix"></image>
					</view>
					<view class='title'>
						请选择登录账号
					</view>
				</view>
				<view v-for="(item,i) in tenantUserInfo" :key="i">
					<view class='info' @click="socailsLogin(item)">
						<view class='user-name'>
							{{item.socialName}}
						</view>
						<view class='user-tenancy'>
							租户名称： {{item.tenantName}}
						</view>
						<view class='user-tenancy'>
							租户id：{{item.tenantId}}
						</view>
						<view class='user-tenancy'>
							账号：{{item.accountName}}
						</view>
					</view>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import {
		login,
		getCodeConfig,
		getCallback,
		otherlogin,
		getLoginConfig,
		getSocialsUserList,
		socialsLogin,
		getTicket
	} from '@/api/common.js'
	import md5Libs from "uview-ui/libs/function/md5";
	import resources from '@/libs/resources'
	export default {
		data() {
			return {
				imgUrl: '',
				loading: false,
				formData: {
					account: "",
					password: "",
					code: "",
					origin: 'password'
				},
				needCode: false,
				codeLength: 4,
				isCode: false,
				rules: {
					account: [{
						required: true,
						message: '请输入账号',
						trigger: 'blur',
					}],
					password: [{
						required: true,
						message: '请输入密码',
						trigger: 'blur',
					}],
				},
				sysConfigInfo: {},
				appIcon: '',
				sysName: '',
				copyright: '',
				isCopyright: true,
				socialsList: [],
				show: false,
				tenantUserInfo: [],
				ssoLoading: true,
				isSso: false,
				ssoTicket: '',
				ssoUrl: '',
				preUrl: '',
				ticketParams: "",
				loginCode: '',
			}
		},
		computed: {
			baseURL() {
				return this.define.baseURL
			},
			cid() {
				return this.$store.getters.cid
			}
		},
		onReady() {
			this.$refs.dataForm.setRules(this.rules);
		},
		onLoad(options) {
			this.ssoTicket = uni.getStorageSync('ssoTicket')
			this.sysConfigInfo = uni.getStorageSync('sysConfigInfo')
			this.appIcon = !!this.sysConfigInfo.appIcon ? this.baseURL + this.sysConfigInfo.appIcon :
				'/static/logo.png'
			this.sysName = !!this.sysConfigInfo.companyName ? this.sysConfigInfo.sysName :
				'LINZEN快速开发平台'
			this.copyright = !!this.sysConfigInfo.copyright ? this.sysConfigInfo.copyright :
				this.define.copyright
			let needCode = uni.getStorageSync('app_loginNeedCode')
			this.isCode = needCode
			this.changeCode()
			this.getLoginConfig()
			this.formData.password = '';
			if (options.data) {
				this.tenantUserInfo = JSON.parse(options.data)
				if (this.tenantUserInfo) this.show = true
			}
		},
		methods: {
			loginToken(res) {
				if (res.data.status != 2) {
					// 登录成功
					if (res.data.status == 1) {
						uni.showLoading({
							title: '登录中'
						})
						this.$store.commit('user/SET_TOKEN', res.data.value)
						uni.setStorageSync('token', res.data.value)
						this.$store.dispatch('user/getCurrentUser').then((res) => {
							uni.hideLoading()
							uni.reLaunch({
								url: '/pages/index/index'
							});
						}).catch(() => {
							uni.hideLoading()
							uni.reLaunch({
								url: '/pages/login/index'
							});
						})
					} else if (res.data.status == 6) {
						this.tenantUserInfo = JSON.parse(res.data.value)
						if (this.tenantUserInfo.length == 1) {
							uni.showLoading({
								title: '登录中'
							})
							this.$store.commit('user/SET_TOKEN', res.data.value)
							uni.setStorageSync('token', res.data.value)
							this.$store.dispatch('user/getCurrentUser').then((res) => {
								uni.hideLoading()
								uni.reLaunch({
									url: '/pages/index/index'
								});
							}).catch(() => {
								uni.hideLoading()
								uni.reLaunch({
									url: '/pages/login/index'
								})
							})
						} else {
							this.show = true
						}
					} else {
						this.show = false
						this.ssoUrl = ''
						uni.showToast({
							title: res.data.value || '操作超时，请重新点击登录',
							icon: 'none'
						})
					}
				}
			},
			wechatLogin() {
				// #ifdef MP-WEIXIN
				getTicket().then(res => {
					this.ssoTicket = res.data
					uni.login({
						provider: 'weixin',
						success: (loginRes) => {
							this.loginCode = loginRes.code
						}
					})
					uni.getUserProfile({
						desc: '获取你的昵称、头像、地区及性别',
						success: (info) => {
							let qurey = {
								encryptedData: info.encryptedData,
								iv: info.iv,
								signature: info.signature,
								code: this.loginCode,
								linzen_ticket: this.ssoTicket,
								socialName: info.userInfo.nickName,
								source: 'wechat_applets'
							}
							socialsLogin(qurey).then(res => {
								this.loginToken(res)
							})
						}
					})
				})
				//  #endif
				// #ifdef APP-PLUS
				getTicket().then(res => {
					this.ssoTicket = res.data
					uni.login({
						provider: 'weixin',
						success: (loginRes) => {
							// 登录成功
							uni.getUserProfile({
								provider: 'weixin',
								success: (info) => {
									let data = {
										source: 'wechat_open',
										uuid: info.userInfo.unionId,
										socialName: info.userInfo.nickName,
										linzen_ticket: this.ssoTicket
									};
									socialsLogin(data).then(res => {
										this.loginToken(res)
									})
								},
								fail: function(err) {
									// 登录授权失败  
									// err.code是错误码
								}
							})
						},
						fail: function(err) {
							// 登录授权失败  
							// err.code是错误码
						}
					});
				})
				//  #endif

			},
			qqOtherlogin() {
				getTicket().then(res => {
					this.ssoTicket = res.data
					uni.login({
						provider: 'qq',
						success: (loginRes) => {
							// 登录成功
							uni.getUserInfo({
								provider: 'qq',
								success: (info) => {
									let data = {
										source: 'qq',
										linzen_ticket: this.ssoTicket,
										socialName: info.userInfo.nickName,
										uuid: info.userInfonickName.unionid,
									};
									socialsLogin(data).then(res => {
										this.loginToken(res)
									}).catch((err) => {})
									// 获取用户信息成功, info.authResult保存用户信息
								}
							})
						},
						fail: function(err) {
							// 登录授权失败  
							// err.code是错误码
						}
					});
				})
			},
			socailsLogin(item) {
				item.tenantLogin = true
				socialsLogin(item).then(res => {
					if (res.code == 200) {
						uni.showLoading({
							title: '登录中'
						})
						this.$store.commit('user/SET_TOKEN', res.data.token)
						uni.setStorageSync('token', res.data.token)
						this.$store.dispatch('user/getCurrentUser').then((res) => {
							uni.hideLoading()
							uni.switchTab({
								url: '/pages/index/index'
							});
							this.show = false
						}).catch(() => {
							uni.hideLoading()
							uni.switchTab({
								url: '/pages/login/index'
							});
						})
					}
				}).catch(() => {
					uni.hideLoading()
					uni.switchTab({
						url: '/pages/login/index'
					});
				})
			},
			otherslogin(key, url) {
				if (key === 'wechat_open') {
					this.wechatLogin();
				} else {
					getTicket().then(res => {
						this.ssoTicket = res.data
						url = url.replace('LINZEN_TICKET', this.ssoTicket)
						uni.setStorageSync('ssoUrl', url)
						uni.navigateTo({
							url: `/pages/login/otherLogin?ssoTicket=` + this.ssoTicket
						})
					}).catch(() => {})
				}
			},
			onFocus(e) {
				this.getCodeConfig(e)
			},
			onBlur(e) {
				this.getCodeConfig(e)
			},
			// 获取登陆配置
			getLoginConfig() {
				getLoginConfig().then(res => {
					this.isSso = res.data.redirect
					this.preUrl = res.data.url
					this.ticketParams = res.data.ticketParams
					let socialsList = res.data.socialsList || []
					this.socialsList = socialsList.filter(o => o.latest && o.enname !=
						'github' && o
						.enname !=
						'wechat_enterprise')
					this.ssoLoading = false
				}).catch(() => {
					this.isSso = false
					this.ssoLoading = false
				})
			},
			getCodeConfig(val) {
				if (!val) return
				getCodeConfig(val).then(res => {
					this.needCode = !!res.data.enableVerificationCode
					if (this.needCode) {
						this.codeLength = res.data.verificationCodeNumber || 4
						this.changeCode()
					}
				})
			},
			changeCode() {
				let timestamp = Math.random()
				this.timestamp = timestamp
				this.imgUrl = `/api/oauth/ImageCode/${this.codeLength || 4}/${timestamp}`
			},
			login() {
				this.$refs.dataForm.validate(valid => {
					if (valid) {
						this.loading = true
						const password = md5Libs.md5(this.formData.password);
						const encryptPassword = this.linzen.aesEncryption.encrypt(password);
						let query = {
							account: this.formData.account,
							password: encryptPassword,
							timestamp: this.timestamp,
							code: this.formData.code,
							origin: this.formData.origin,
							linzen_ticket: this.ssoTicket,
							grant_type: 'password'
						}
						// #ifdef  APP-PLUS
						const clientId = plus.push.getClientInfo().clientid;
						query.clientId = clientId
						/* unipush2.0 */
						// query.Client_Id = this.cid
						// #endif
						login(query).then(res => {
							let token = res.data.token
							this.$store.commit('user/SET_TOKEN', token)
							uni.setStorageSync('token', token)
							this.$store.dispatch('user/getCurrentUser').then((res) => {
								this.loading = false
								uni.switchTab({
									url: '/pages/index/index'
								});
							}).catch(() => {
								this.loading = false
							})
						}).catch((err) => {
							this.formData.code = ''
							this.changeCode()
							this.loading = false
						})
					}
				});
			},
			ssoLogin() {
				getTicket().then(res => {
					this.ssoTicket = res.data
					this.ssoUrl = this.preUrl + '?' + this.ticketParams + '=' + this.ssoTicket
					uni.setStorageSync('ssoUrl', this.ssoUrl)
					uni.navigateTo({
						url: `/pages/login/otherLogin?ssoTicket=${this.ssoTicket}`
					})
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		width: 100%;
		min-height: 100vh;
	}

	.logo-v {
		height: 100vh;
		display: flex;
		flex-direction: column;

		.login-version {
			position: fixed;
			right: 0px;
			top: 0px;
			width: 120rpx;
			height: 120rpx;
			background: url('../../static/image/login_version.png') no-repeat center;
			background-size: 100%;

			.login-version-text {
				width: 120rpx;
				height: 120rpx;
				line-height: 70rpx;
				text-align: center;
				color: #fff;
				font-size: 28rpx;
				transform: rotate(45deg);
			}
		}

		.login-bg {
			height: 500rpx;
			position: relative;

			image {
				width: 100%;
				height: 100%;
			}

			.logoImg {
				width: 160rpx;
				height: 160rpx;
				margin: 0 auto;
				position: absolute;
				/* #ifdef APP-PLUS */
				bottom: -90rpx;
				/* #endif */
				/* #ifndef APP-PLUS */
				bottom: 0;
				/* #endif */
				left: 0;
				right: 0;

				/deep/uni-image {
					border-radius: 10px !important;
				}

				image {
					width: 100%;
					height: 100%;
					border-radius: 20%;
				}
			}
		}

		.logo-hd {
			width: 100%;

			.introduce {
				justify-content: center;
				align-items: center;

				.text-one {
					height: 70rpx;
					font-weight: 700;
				}

				.text-two {
					color: #999999;
				}
			}

			.loginSwitch {
				margin-top: 40rpx;
				justify-content: center;
				align-items: center;

				.tabs {
					color: #999999;
					position: relative;

					&::after {
						content: "";
						width: 64rpx;
						height: 4rpx;
						background-color: #356efe;
						margin-top: 15rpx;
						position: absolute;
						left: 0;
						bottom: -15rpx;
						display: block;
						border-radius: 50rpx;
					}

					// &.active1 {
					// 	&::after {
					// 		left: 0;
					// 	}
					// }

					&.active2 {
						&::after {
							left: 70%;
						}
					}

					.tab {
						width: 50%;
						height: 80upx;
						text-align: center;
						color: #AEAFB5;
						font-size: 32upx;

						&.active {
							color: #3281ff;
						}
					}

				}

				.loginInputBox {
					width: 100%;
					/* #ifdef APP-PLUS */
					margin-top: 120rpx;
					/* #endif */
					/* #ifndef APP-PLUS */
					margin-top: 80rpx;
					/* #endif */
					padding: 0 64rpx;

					.code-box {
						width: 100%;
					}

					.loginBtnBox {}
				}
			}
		}

		.copyright {
			position: fixed;
			bottom: 40rpx;
			left: 0;
			right: 0;
			text-align: center;
			color: #9A9A9A;
			font-size: 24rpx;
		}

		.sso-login-btn {
			width: 100%;
			padding: 0 64rpx;
			/* #ifdef APP-PLUS */
			margin-top: 404rpx;
			/* #endif */
			/* #ifndef APP-PLUS */
			margin-top: 364rpx;
			/* #endif */
		}
	}

	.other-list {
		display: flex;
		align-items: center;
		justify-content: space-around;

		.other-item {
			width: 30px;
			height: 30px;
			line-height: 30px;
			text-align: center;
			cursor: pointer;
			border-radius: 50%;

			i {
				font-size: 20px;
				color: #a0acb7;
			}

			// &:hover {
			// 	background-color: #1890ff;

			// 	i {
			// 		color: #fff;
			// 	}
			// }
		}
	}

	.mian {
		background: url('/static/image/tenancy.png');
		height: 100%;
	}

	.img {
		margin-top: 20rpx;
		margin-left: 30%;
	}

	.title {
		margin-top: -55rpx;
		margin-left: 260rpx;
		font-size: 32rpx;
	}

	.info {
		margin: auto auto;
		width: 96%;
		height: 300rpx;
		background-color: #fff;
		margin-bottom: 20rpx;
		border-radius: 10rpx;
		border-left: 10rpx solid #9DC8FA;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.user-name {
		font-weight: bold;
		font-size: 32rpx;
		margin-left: 20rpx;
		margin-bottom: 20rpx;
		margin-top: 30rpx;
		width: 100%;
		height: 60rpx;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}

	.user-tenancy {
		font-size: 28rpx;
		margin-left: 20rpx;
		margin-bottom: 20rpx;
		width: 100%;
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
</style>