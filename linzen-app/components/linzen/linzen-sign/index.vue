<template>
	<view class="linzen-sign" :class="align=='right'?'flex-end':'flex-start'">
		<view class="linzen-sign-box">
			<template v-if="showBtn">
				<image class="linzen-sign-img" :src="innerValue" mode="scaleToFill" v-show="innerValue"
					@tap.stop="handlePreviewImage(innerValue)" />
				<view class="linzen-sign-btn" v-if="!detailed" @click="addSign()">
					<i class="icon-zen icon-zen-signature" />
					<view class="title" v-if="!innerValue">{{signTip}}</view>
				</view>
			</template>
			<sign ref="sign" @input="signDialog" />
		</view>
	</view>
</template>
<script>
	import sign from './Sign.vue'

	export default {
		name: 'linzen-sign',
		components: {
			sign
		},
		props: {
			value: {
				type: String,
				default: ''
			},
			signTip: {
				type: String,
				default: '手写签名'
			},
			disabled: {
				type: Boolean,
				default: false
			},
			detailed: {
				type: Boolean,
				default: false
			},
			align: {
				type: String,
				default: 'right'
			},
			showBtn: {
				type: Boolean,
				default: true
			},
		},
		data() {
			return {
				signVisible: false,
				innerValue: '',
			}
		},
		watch: {
			value: {
				handler(val) {
					this.innerValue = val || ''
					this.$emit('input', this.innerValue)
				},
				deep: true,
				immediate: true
			},
		},
		methods: {
			addSign() {
				if (this.disabled) return
				this.signVisible = true
				this.$nextTick(() => {
					this.$refs.sign.showSignature();
				})
			},
			signDialog(val) {
				this.signVisible = false
				if (val) {
					this.innerValue = val
					this.$emit('input', this.innerValue)
					this.$emit('change', this.innerValue)
				}
			},
			handlePreviewImage(url) {
				// #ifdef H5
				uni.previewImage({
					urls: [url],
					current: url,
					success: () => {},
					fail: () => {
						uni.showToast({
							title: '预览图片失败',
							icon: 'none'
						});
					}
				});
				// #endif
			}
		}
	}
</script>

<style lang="scss">
	.linzen-sign {
		width: 100%;
		display: flex;
		align-items: center;

		&.flex-end {
			justify-content: flex-end;
		}

		&.flex-start {
			justify-content: flex-start;
		}

		.linzen-sign-box {
			display: flex;
		}

		.linzen-sign-img {
			width: 160rpx;
			height: 80rpx;
			flex-shrink: 0;
		}

		.linzen-sign-btn {
			color: #2188ff;
			width: 100%;
			display: flex;
			flex-shrink: 0;

			.icon-zen-signature {
				font-size: 52rpx;
			}

			.title {
				font-size: 28rpx;
			}
		}
	}
</style>