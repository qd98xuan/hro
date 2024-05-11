<template>
	<view class="u-flex-col slider-box">
		<u-slider v-model="innerValue" :step="step" :min="min" :max="max" :disabled="disabled" @end="end">
			<view class="slider-badge-button">{{innerValue}}</view>
		</u-slider>
	</view>
</template>

<script>
	export default {
		model: {
			prop: 'value',
			event: 'change'
		},
		props: {
			min: {
				type: Number,
				default: 0
			},
			max: {
				type: Number,
				default: 0
			},
			step: {
				default: ''
			},
			value: {
				default: 0
			},
			disabled: {
				type: Boolean,
				default: false
			},
		},
		data() {
			return {
				innerValue: 0
			}
		},
		watch: {
			value: {
				handler(val, oldVal) {
					this.innerValue = val
				},
				deep: true,
				immediate: true
			}
		},
		methods: {
			end(e) {
				this.$emit('change', this.innerValue)
			}
		}
	}
</script>

<style lang="scss">
	.slider-box {
		padding-left: 20rpx;
		width: 100%;
	}
</style>