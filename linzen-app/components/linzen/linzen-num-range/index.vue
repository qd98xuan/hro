<template>
	<view class="linzen-num-range">
		<u-input input-align='right' v-model="min" placeholder="最小值" type="number"
			@blur="onblur($event,'min')"></u-input>
		<text class="separator">-</text>
		<u-input input-align='right' v-model="max" placeholder="最大值" type="number"
			@blur="onblur($event,'max')"></u-input>
	</view>
</template>

<script>
	export default {
		name: 'linzen-num-range',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				type: [Array, String],
				default: () => []
			},
			disabled: {
				type: Boolean,
				default: false
			},
			precision: {
				type: Number,
				default: undefined
			},
		},
		data() {
			return {
				min: '',
				max: ''
			}
		},
		watch: {
			value: {
				handler(val) {
					if (Array.isArray(val) && val.length === 2) {
						this.min = val[0]
						this.max = val[1]
					} else {
						this.min = ''
						this.max = ''
					}
				},
				immediate: true,
			},
			min(val) {
				this.onChange()
			},
			max(val) {
				this.onChange()
			}
		},
		methods: {
			onblur(e, type) {
				if (type === 'min') {
					this.min = !isNaN(this.precision) ? Number(e).toFixed(this.precision) : e
				} else {
					this.max = !isNaN(this.precision) ? Number(e).toFixed(this.precision) : e
				}
			},
			onChange() {
				if ((!this.min && this.min !== 0) && (!this.max && this.max !== 0)) return this.$emit('input', [])
				this.$emit('input', [this.min, this.max])
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-num-range {
		width: 100%;
		display: flex;
		justify-content: space-between;
		align-items: center;

		.separator {
			margin: 0 20rpx;
			flex-shrink: 0;
		}
	}
</style>