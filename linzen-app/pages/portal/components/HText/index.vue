<template>
	<view class="text-v">
		<view class="" v-if="option.styleType==1">
			<view v-if="option.textAutoplay" @click="jump"
				:style="{'font-weight':option.textFontWeight?700:400,'font-style':option.textFontStyle?'italic':'normal','text-decoration':option.textUnderLine,'color':option.textFontColor}">
				<u-notice-bar :volume-icon="false" :list="option.defaultValue" :bg-color="option.textBgColor || '#fff'"
					:color="option.textFontColor" :font-size="option.textFontSize*2" :speed="option.textAutoplaySpeed"
					:autoplay="option.textAutoplay">
				</u-notice-bar>
			</view>
			<view class="txtSty" v-if="!option.textAutoplay" @click="jump"
				:style="{'color':option.textFontColor,'font-size':option.textFontSize*2+'rpx','background-color':option.textBgColor,'font-weight':option.textFontWeight?700:400,'text-decoration':option.textUnderLine,'font-style':option.textFontStyle?'italic':'normal','text-align':option.textLeft}">
				<text>{{option.defaultValue}}</text>
			</view>
		</view>
		<view class="u-p-20 parse" v-if="option.styleType==2">
			<mp-html :content="option.defaultValue" :key="key"></mp-html>
		</view>
	</view>
</template>

<script>
	import {
		getDataInterfaceRes
	} from '@/api/common'
	let timer;
	export default {
		props: {
			config: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				option: {},
				key: +new Date()
			}
		},
		created() {
			this.init()
			uni.$off('proRefresh')
			uni.$on('proRefresh', () => {
				this.initData()
			})
		},
		methods: {
			init() {
				this.option = JSON.parse(JSON.stringify(this.config.option))
				if (timer) clearInterval(timer)
				this.initData()
				if (!this.config.allRefresh.autoRefresh && this.config.refresh.autoRefresh) {
					timer = setInterval(this.initData, this.config.refresh.autoRefreshTime * 60000)
				}
			},
			jump() {
				if (this.config.platform === 'mp') return
				let url;
				if (!this.option.appLinkType) return
				if (this.option.appLinkType == 1) {
					let data = {
						id: this.option.appModuleId,
						moduleId: this.option.appModuleId,
						urlAddress: this.option.appUrlAddress,
						...JSON.parse(this.option.appPropertyJson)
					}
					if (this.option.appType == 3) {
						url = '/pages/apply/dynamicModel/index?config=' + this.base64.encode(JSON
							.stringify(data), "UTF-8")
					} else if (this.option.appType == 2) {
						url = this.option.appUrlAddress + '?menuId=' + this.option.appModuleId
					} else {
						url = '/pages/apply/externalLink/index?url=' + encodeURIComponent(this.option.appUrlAddress)
					}
				} else {
					url = '/pages/apply/externalLink/index?url=' + encodeURIComponent(this.option.appUrlAddress)
				}
				uni.navigateTo({
					url: url,
					fail: (err) => {
						this.$u.toast("暂无此页面")
					}
				})
			},
			initData() {
				if (this.option.textAutoplay) this.option.defaultValue = [this.option.defaultValue]
				if (this.config.dataType === "dynamic") {
					getDataInterfaceRes(this.config.propsApi, {}).then(res => {
						this.option.defaultValue = res.data
						if (this.option.styleType == 2 && typeof(res) != 'string') this.option.defaultValue =
							JSON.stringify(res)
						if (this.option.textAutoplay) this.option.defaultValue = [this.option
							.defaultValue
						]
					})
				}
				this.key = +new Date()
			}
		}
	}
</script>


<style lang="scss">
	.text-v {

		// padding: 20rpx 0;
		/deep/.u-notice-box {
			margin-bottom: 0 !important;
		}

		.parse {
			word-break: break-all;
		}

		.txtSty {
			width: 100wh;
			height: 100%;
			padding: 20rpx;
			word-wrap: break-word;
			color: blue;
			font-size: 36rpx;
		}
	}
</style>