<template>
	<view class="linzen-input">
		<!-- 数字输入 -->
		<view v-if="!detailed">
			<view class="input-content" :class="{'input-border':addonBefore||addonAfter}">
				<view class="input-left u-line-1" v-if="addonBefore">{{addonBefore}}</view>
				<view class="input-center">
					<u-input input-align='right' :border="false" v-model="innerValue"
						:type="showPassword?'password':'text'" :placeholder="placeholder" :maxlength="maxlength"
						:disabled="disabled" :clearable='clearable' @blur="onBlur" @input="onInput" />
				</view>
				<!-- #ifndef H5 -->
				<text class="icon-zen icon-zen-scanCode1" v-if="useScan" @click="scanCode" />
				<!-- #endif -->
				<view class="input-right u-line-1" v-if="addonAfter">{{addonAfter}}</view>
			</view>
		</view>
		<view class="detail-text" :class="'detail-text-'+align" v-else>
			<text class="detail-text-addon" v-if="addonBefore">{{ addonBefore }}</text>
			{{ maskedValue }}
			<text class="detail-text-addon" v-if="addonAfter">{{ addonAfter }}</text>
		</view>
	</view>
</template>
<script>
	import {
		useTextMask
	} from '@/utils/useTextMask';
	export default {
		name: 'linzen-input',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				type: [String, Number],
				default: ''
			},
			placeholder: {
				type: String,
				default: ''
			},
			maxlength: {
				type: Number,
				default: 140
			},
			showPassword: {
				type: Boolean,
				default: false
			},
			disabled: {
				type: Boolean,
				default: false
			},
			clearable: {
				type: Boolean,
				default: false
			},
			detailed: {
				type: Boolean,
				default: false
			},
			addonBefore: {
				type: String,
				default: ''
			},
			addonAfter: {
				type: String,
				default: ''
			},
			useMask: {
				type: Boolean,
				default: false
			},
			maskConfig: {
				type: Object,
				default: () => {}
			},
			align: {
				default: 'right'
			},
			useScan: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				innerValue: '',
				timer: null,
				maskedValue: ''
			}
		},
		watch: {
			value: {
				handler(val) {
					this.innerValue = val
					if (!this.useMask) return (this.maskedValue = this.innerValue);
					const {
						getMaskedText
					} = useTextMask(this.maskConfig);
					this.maskedValue = getMaskedText(this.innerValue);
				},
				immediate: true,
			}
		},
		methods: {
			onBlur() {
				this.$emit('blur', this.innerValue)
			},
			onInput(val) {
				this.$emit('input', val)
				this.$emit('change', val)
			},
			scanCode() {
				uni.scanCode({
					success: res => {
						if (!res.result || typeof res.result !== 'string') return
						this.onInput(res.result)
					}
				});
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-input {
		width: 100%;

		.input-content {
			display: flex;
			border-radius: 10rpx;
			height: 74rpx;

			&.input-border {
				border: 1rpx solid rgb(220, 223, 230)
			}

			.input-center {
				flex: 1;
				padding: 0 8rpx;
			}

			.input-left,
			.input-right {
				flex-shrink: 0;
				width: 128rpx;
				background-color: #f5f7fa;
				color: #909399;
				padding: 0 10rpx;
				text-align: center;
			}

			.input-left {
				border-right: 1rpx solid #dcdfe6;
				border-radius: 10rpx 0 0 10rpx;
			}

			.input-right {
				border-left: 1rpx solid #dcdfe6;
				border-radius: 0px 10px 10px 0px;
			}

			.icon-zen-scanCode1 {
				margin-right: 8rpx;
				color: #909399;
			}
		}


		.detail-text {
			word-break: break-all;
			text-align: right;

			.detail-text-addon {
				color: #909399;
			}

			&.ellipsis {
				overflow: hidden;
				white-space: nowrap;
				text-overflow: ellipsis;
			}

			&.detail-text-left {
				text-align: left;
			}
		}
	}
</style>