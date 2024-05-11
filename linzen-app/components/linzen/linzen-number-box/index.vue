<template>
	<view class="linzen-input-number">
		<!-- 数字输入 -->
		<view v-if="!detailed">
			<u-number-box v-if="controls" v-model="innerValue" :step="step" :min="min" :max="max" :key="key"
				:disabled="disabled" :positive-integer="false" :input-height="60" @blur="onNumberBlur"
				@change="onChange" />
			<view v-else class="input-content" :class="{'input-border':addonBefore||addonAfter}">
				<view class="input-left u-line-1" v-if="addonBefore">{{addonBefore}}</view>
				<view class="input-center">
					<u-input v-model="innerValue" :placeholder="placeholder"
						:input-align='addonBefore || addonAfter? "center":"right"' :disabled="disabled"
						:clearable="false" @focus="onFocus" @blur="onBlur">
					</u-input>
				</view>
				<view class="input-right u-line-1" v-if="addonAfter">{{addonAfter}}</view>
			</view>
		</view>
		<!-- 详情 -->
		<view v-else>
			<view class="detail-content u-flex">
				<view class="detail-left u-line-1" v-if="addonBefore&&!controls">{{addonBefore}}
				</view>
				<view class="detail-center">{{thousands?thousandsFormat(innerValue) :innerValue}}</view>
				<view class="detail-right u-line-1" v-if="addonAfter&&!controls">{{addonAfter}}
				</view>
			</view>
		</view>
		<!-- 大写金额 -->
		<view class="amount-chinese-name" v-if="isAmountChinese&&getChineseName">{{getChineseName}}</view>
	</view>

</template>
<script>
	export default {
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				type: [Number, String],
				default: ''
			},
			min: {
				type: Number,
				default: -999999999999999
			},
			max: {
				type: Number,
				default: 999999999999999
			},
			step: {
				type: Number,
				default: 1
			},
			disabled: {
				type: Boolean,
				default: false
			},
			addonBefore: {
				default: ''
			},
			addonAfter: {
				default: ''
			},
			precision: {
				type: Number
			},
			controls: {
				type: Boolean,
				default: false
			},
			thousands: {
				type: Boolean,
				default: false
			},
			isAmountChinese: {
				type: Boolean,
				default: false
			},
			detailed: {
				type: Boolean,
				default: false
			},
			type: {
				default: ''
			},
			placeholder: {
				default: '请输入'
			},
		},
		data() {
			return {
				innerValue: null,
				key: +new Date()
			}
		},
		watch: {
			value: {
				handler(val) {
					this.setValue(val)
				},
				immediate: true
			}
		},
		computed: {
			getChineseName() {
				if (!this.isAmountChinese || (!this.getNumberValue && this.getNumberValue !== 0)) return ""
				return this.linzen.getAmountChinese(this.getNumberValue)
			},
			getNumberValue() {
				return this.handleConvertNum(this.innerValue)
			},
		},
		methods: {
			setValue(val) {
				// #ifdef MP
				this.innerValue = (!val && val !== 0) || isNaN(val) ? '' : Number(val);
				// #endif
				// #ifndef MP
				this.innerValue = (!val && val !== 0) || isNaN(val) ? null : Number(val);
				// #endif
				if (!this.innerValue && this.innerValue !== 0) return
				if (this.innerValue < this.min) this.innerValue = this.min
				if (this.innerValue > this.max) this.innerValue = this.max
				if (!isNaN(this.precision)) {
					const value = Number(this.getNumberValue).toFixed(this.precision)
					this.innerValue = this.controls ? Number(value) : value
				}
				if (this.thousands) this.innerValue = this.thousandsFormat(this.innerValue)
			},
			thousandsFormat(num) {
				if (num === 0) return '0';
				if (!num) return '';
				const numArr = num.toString().split('.');
				numArr[0] = numArr[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
				return numArr.join('.');
			},
			onChange() {
				this.setValue(this.innerValue)
				this.$emit('input', this.innerValue)
			},
			onNumberBlur() {
				this.setValue(this.innerValue)
				this.$emit('blur', this.innerValue)
			},
			onBlur(val) {
				this.$emit('blur', this.getNumberValue)
				this.$emit('input', this.innerValue)
			},
			onFocus() {
				if (!this.innerValue) return
				if (this.innerValue.toString().indexOf('e+') > -1) return
				this.innerValue = !isNaN(this.precision) ? Number(this.getNumberValue).toFixed(this.precision) : this
					.getNumberValue
			},
			handleConvertNum(val) {
				if (!val && val !== 0) return null
				let num = this.$u.deepClone(val.toString().split("."))
				const arr2 = num.length > 1 ? num[1].split("").filter(o => (!isNaN(o))).join('') : []
				let arr = num[0].split("").filter(o => (!isNaN(o))).join('');
				let res = num[1] ? arr + '.' + arr2 : Number(arr)
				return val.toString().indexOf('-') != -1 ? Number('-' + res) : res
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-input-number {
		width: 100%;
		display: flex;
		flex-direction: column;
		align-items: flex-end;

		:deep(.u-number-input) {
			width: 150rpx !important;
		}

		.input-content {
			display: flex;
			border-radius: 10rpx;
			height: 74rpx;

			&.input-border {
				border: 1px solid rgb(220, 223, 230)
			}

			.input-left,
			.input-right {
				width: 64px;
				background-color: #f5f7fa;
				color: #909399;
				padding: 0 5px;
				text-align: center;
			}

			.input-left {
				border-right: 1px solid #dcdfe6;
				border-radius: 5px 0 0 5px;
			}

			.input-right {
				border-left: 1px solid #dcdfe6;
				border-radius: 0px 5px 5px 0px;
			}
		}

		.detail-content {
			.detail-left {
				max-width: 128rpx;
				padding-right: 16rpx;
			}

			.detail-right {
				max-width: 128rpx;
				padding-left: 16rpx;
			}
		}

		.amount-chinese-name {
			color: #999999;
			line-height: 40rpx;
			padding: 10rpx 10rpx 0 0;
		}
	}
</style>