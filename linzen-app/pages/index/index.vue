<template>
	<view class="menhu-v">
		<!-- #ifndef MP -->
		<uni-nav-bar class='nav' :fixed="true" :statusBar="true" :border="false" :right-icon="rightIcon"
			@clickRight="scan">
			<block slot="default">
				<view class="nav-left" @click="showSelectBox">
					<view class="nav-left-text">
						{{portalTitle}}
					</view>
					<uni-icons class='right-icons' type="arrowdown" color="#000000" size="14"
						v-if="portalList.length>0 &&userInfo.appPortalId" :class="{'select-right-icons':showSelect}" />
				</view>
			</block>
		</uni-nav-bar>
		<template>
			<template v-if="userInfo.appPortalId">
				<mescroll-body ref="mescrollRef" @down="downCallback" :down="downOption" :sticky="true" @up="upCallback"
					:up="upOption" :bottombar="false" style="min-height: 100%" @init="mescrollInit">
					<view class="portal-v" v-if="authConfig.type==0">
						<template v-if="formData.length">
							<view class="portal-box" v-for="(item,index) in formData" :key="index">
								<portalItem :item='item' ref="portalItem" :key="key" :protalData="formData"
									v-if="item.show" />
							</view>
						</template>
						<view v-else class="portal-v portal-nodata">
							<view class="u-flex-col" style="align-items: center;">
								<u-image width="280rpx" height="280rpx" :src="emptyImg1"></u-image>
								<text class="u-m-t-20" style="color: #909399;">暂无数据</text>
							</view>
						</view>
					</view>
					<template v-if="authConfig.type==1">
						<!-- #ifdef APP-PLUS -->
						<view v-if="authConfig.linkType==1 && showWebView">
							<web-view :src="authConfig.customUrl"></web-view>
						</view>
						<!-- #endif -->
						<!-- #ifdef H5 -->
						<view v-if="authConfig.linkType==1 && showWebView" style="height:calc(100vh - 100px)">
							<web-view :src="authConfig.customUrl"></web-view>
						</view>
						<!-- #endif -->
						<view v-else class="portal-v portal-nodata">
							<view class="u-flex-col" style="align-items: center;">
								<u-image width="280rpx" height="280rpx" :src="emptyImg"></u-image>
								<text class="u-m-t-20" style="color: #909399;">当前内容无法在APP上显示，请前往PC门户查看～～</text>
							</view>
						</view>
					</template>
				</mescroll-body>
			</template>
			<view class="portal-v" v-else>
				<view class="portal-box">
					<defaultPortal></defaultPortal>
				</view>
			</view>
		</template>
		<!-- #endif -->
		<!-- #ifdef MP -->
		<view>
			<web-view :src="mpPortalUrl"></web-view>
		</view>
		<!-- #endif -->
		<!-- #ifndef MP -->
		<u-popup v-model="showSelect" mode="top" class="select-box" height="600rpx" @close="closePopup">
			<!-- #ifdef APP-PLUS -->
			<view :style="{'margin-top':statusBarHeight+88+'px'}"></view>
			<!-- #endif -->
			<!-- #ifdef H5 -->
			<view :style="{'margin-top':statusBarHeight+88+'rpx'}"></view>
			<!-- #endif -->
			<view v-for="(item,index) in portalList" :key="index" class="select-item" @click="selectItem(item,index)">
				<text class="u-m-r-12 u-font-40"
					:class="[item.icon,{'currentItem':item.id === userInfo.appPortalId}]" />
				<text class="item-text sysName">{{item.fullName}}</text>
				<u-icon name="checkbox-mark " class="currentItem" v-if="item.id === userInfo.appPortalId"></u-icon>
			</view>
		</u-popup>
		<!-- #endif -->
	</view>
</template>
<script>
	var wv; //计划创建的webview
	import {
		PortalList,
		SetPortal
	} from '@/api/portal/portal.js'
	// #ifndef MP
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js"
	import IndexMixin from './mixin.js'
	import chat from '@/libs/chat.js'
	import portalItem from '@/pages/portal/components/index.vue'
	import defaultPortal from '@/pages/portal/components/defaultPortal.vue'
	import {
		auth
	} from '@/api/portal/portal.js'
	// #endif
	import resources from '@/libs/resources.js'
	import emptyImg from '@/static/image/defPortal.png'
	export default {
		// #ifndef MP
		mixins: [MescrollMixin, IndexMixin],
		// #endif
		components: {
			// #ifndef MP
			portalItem,
			defaultPortal
			// #endif
		},
		data() {
			return {
				showWebView: true,
				emptyImg: emptyImg,
				emptyImg1: resources.message.nodata,
				rightIcon: '',
				key: +new Date(),
				formData: [],
				portalTitle: '门户',
				statusBarHeight: '',
				showSelect: false,
				selectData: {
					name: '',
					id: ''
				},
				portalList: [],
				id: '',
				userInfo: {},
				downOption: {
					use: true,
					auto: true
				},
				upOption: {
					page: {
						num: 0,
						size: 50,
						time: null
					},
					empty: {
						use: false,
					},
					textNoMore: '没有更多数据',
				},
				authConfig: {},
				token: '',
				mpPortalUrl: ''
			};
		},
		onShow() {
			this.$forceUpdate()
			this.token = uni.getStorageSync('token')
			this.mpPortalUrl = this.define.baseURL + '/pages/portal/mpPortal/index?token=' + this.token
			this.userInfo = uni.getStorageSync('userInfo') || {}
			if (!this.userInfo.appPortalId) return
			// #ifndef MP
			this.getPortalList()
			this.$nextTick(() => {
				this.mescroll.resetUpScroll();
				this.portalList = []
			})
			// #endif
			// #ifdef APP-PLUS
			this.rightIcon = 'scan'
			// #endif
		},
		onReady() {
			// #ifdef APP-PLUS
			this.setWebview()
			// #endif
		},
		onLoad(e) {
			// #ifndef MP
			this.token = uni.getStorageSync('token')
			if (!this.$store.state.chat.socket && this.token) chat.initSocket()
			// #endif
		},
		methods: {
			setWebview() {
				if (this.authConfig.linkType == 1) {
					var currentWebview = this.$scope
						.$getAppWebview() //此对象相当于html5plus里的plus.webview.currentWebview()。在uni-app里vue页面直接使用plus.webview.currentWebview()无效
					let height = 0;
					uni.getSystemInfo({
						//成功获取的回调函数，返回值为系统信息
						success: (sysinfo) => {
							height = sysinfo.windowHeight - 50; //自行修改，自己需要的高度 此处如底部有其他内容，可以直接---(-50)这种
						},
						complete: () => {}
					});
					this.$nextTick(() => {
						setTimeout(() => {
							wv = currentWebview.children()[0]
							wv.setStyle({
								top: 80,
								height,
								scalable: true
							})
						}, 500); //如果是页面初始化调用时，需要延时一下
					})
				}
			},
			upCallback(keyword) {
				auth(this.userInfo.appPortalId).then(res => {
					this.authConfig = res.data || {}
					let data = JSON.parse(res.data.formData) || {};
					this.formData = data.layout ? JSON.parse(JSON.stringify(data.layout)) : []
					this.handelFormData(data)
					if (data.refresh.autoRefresh) {
						this.timer && clearInterval(this.timer);
						this.timer = setInterval(() => {
							uni.$emit('proRefresh')
						}, data.refresh.autoRefreshTime * 60000)
					}
					this.mescroll.endSuccess(this.formData.length);
					this.key = +new Date()
					// #ifdef APP-PLUS
					this.setWebview()
					// #endif
				}).catch(() => {
					this.formData = []
					this.mescroll.endSuccess(0);
					this.mescroll.endErr();
					this.key = +new Date()
				})
			},
			handelFormData(data) {
				const loop = (list) => {
					list.forEach(o => {
						o.allRefresh = data.refresh
						o.show = false
						if (o.visibility && o.visibility.length && o.visibility.includes('app')) o.show =
							true
						if (o.children && o.children.length) loop(o.children)
					})
					this.key = +new Date()
				}
				loop(this.formData)
				this.dataList = this.formData.filter(o => o.show)
				if (this.dataList.length < 1) {
					this.formData = this.dataList
					this.mescroll.endSuccess(this.dataList.length);
				}
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
			scan() {
				uni.scanCode({
					success: res => {
						if (this.isJSON(res.result.trim())) {
							const result = JSON.parse(res.result.trim())
							if (result.t === 'ADP') {
								let config = {
									isPreview: 1,
									id: result.id,
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
								let config = JSON.stringify(result)
								uni.navigateTo({
									url: '/pages/apply/dynamicModel/scanForm?config=' + config,
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
									`${this.report}/preview.html?id=${result.id}&token=${this.token}&page=1&from=menu&fullName=${result.fullName}`
								uni.navigateTo({
									url: '/pages/apply/externalLink/index?url=' + encodeURIComponent(
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
			},

			getPortalList() {
				PortalList().then(res => {
					let list = res.data.list || [];
					list.map(o => {
						this.portalList.push(...o.children)
						this.portalList.forEach(o => {
							if (o.id === this.userInfo.appPortalId) {
								this.portalTitle = o.fullName
							}
						})
					})
				})
			},
			closePopup() {
				// #ifdef APP-PLUS
				this.setWebview()
				uni.$emit('showVideo', true)
				this.showWebView = true
				// #endif
			},
			showSelectBox() {
				if (Array.isArray(this.portalList) && this.portalList.length) this.showSelect = !this.showSelect
				// #ifdef APP-PLUS
				uni.$emit('showVideo', false)
				this.showWebView = false
				this.setWebview()
				// #endif
			},
			getStatusBarHeight() {
				let that = this;
				wx.getSystemInfo({
					success: function(res) {
						that.statusBarHeight = res.statusBarHeight;
					},
				});
			},
			selectItem(item, index) {
				SetPortal(item.id).then(res => {
					this.portalTitle = this.portalList[index].fullName
					this.userInfo.appPortalId = item.id
					// #ifndef MP
					this.mescroll.resetUpScroll();
					// #endif
					this.showSelectBox()
					uni.setStorageSync('userInfo', this.userInfo)
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.menhu-v {
		.nav {
			z-index: 99999;

			/deep/.uni-navbar__content {
				z-index: 99999;
			}

			/deep/.uni-navbar__header-container {
				justify-content: center;
			}

			.nav-left {
				max-width: 100%;
				display: flex;
				align-items: center;

				.nav-left-text {
					font-weight: 700;
					font-size: 32rpx;
					flex: 1;
					min-width: 0;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}

				.right-icons {
					font-weight: 700;
					margin-top: 2px;
					margin-left: 4px;
					transition-duration: 0.3s;
				}

				.select-right-icons {
					transform: rotate(-180deg);
				}
			}
		}

		.select-box {
			overflow-y: scroll;

			.currentItem {
				color: #2979FF;
			}

			.select-item {
				height: 100rpx;
				display: flex;
				align-items: center;
				padding: 0 20rpx;
				font-size: 30rpx;
				color: #303133;
				text-align: left;
				position: relative;

				&::after {
					content: " ";
					position: absolute;
					left: 2%;
					top: 0;
					box-sizing: border-box;
					width: 96%;
					height: 1px;
					transform: scale(1, .3);
					border: 0 solid #e4e7ed;
					z-index: 2;
					border-bottom-width: 1px;
				}

				.sysName {
					flex: 1;
					overflow: auto;
					min-width: 0;
				}
			}
		}
	}

	/deep/.portal-nodata {
		position: absolute;
		top: 450rpx;
		width: 100%;
		text-align: center;
		z-index: 100;
		background-color: #f0f2f6;
	}
</style>