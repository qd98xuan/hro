<template>
	<view class="t-page">
		<view class="color-box" :class="" @click="open">
			<view class="colorVal" :style="{backgroundColor:bgColor}">
				<uni-icons type="bottom" size="10" color='#c7c7c7'></uni-icons>
			</view>
		</view>
		<!-- 需要声明 ref  -->
		<t-color-picker ref="colorPicker" :color="value" @confirm="confirm" :colorFormat='colorFormat'>
		</t-color-picker>
	</view>
</template>
<script>
	import tColorPicker from './t-color-picker.vue'
	import conversion from '@/libs/color-typeConversion.js'
	export default {
		components: {
			tColorPicker
		},
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				default: ''
			},
			colorFormat: {
				type: String,
				default: 'hex'
			},
			disabled: {
				type: Boolean,
				default: false
			},
		},
		data() {
			return {
				bgColor: "#fff",
				hsvObj: {},
				hsvList: ['h', 's', 'v'],
			};
		},
		watch: {
			value(val) {
				if (!val) return this.bgColor = '#fff'
				if (this.colorFormat === 'hsv') {
					let color = ""
					var result = val.match(/\(([^)]*)\)/)
					result[1].split(',').forEach((o, i) => {
						this.$set(this[this.colorFormat + 'Obj'], this[this.colorFormat + 'List'][i], o)
					})
					color = conversion.hsv2rgb(this[this.colorFormat + 'Obj'].h, this[this.colorFormat + 'Obj'].s, this[
						this.colorFormat + 'Obj'].v)
					this.bgColor = `rgb(${color.r},${color.g},${color.b})`
				} else {
					this.bgColor = val
				}
			}
		},
		created() {
			if (!this.value) return this.bgColor = '#fff'
			this.$nextTick(() => {
				if (this.colorFormat === 'hsv') {
					let color = ""
					var result = this.value.match(/\(([^)]*)\)/)
					result[1].split(',').forEach((o, i) => {
						this.$set(this[this.colorFormat + 'Obj'], this[this.colorFormat + 'List'][i], o)
					})
					color = conversion.hsv2rgb(this[this.colorFormat + 'Obj'].h, this[this.colorFormat + 'Obj'].s,
						this[this.colorFormat + 'Obj'].v)
					this.bgColor = `rgb(${color.r},${color.g},${color.b})`
				} else {
					this.bgColor = this.value
				}
			})
		},
		methods: {
			open(item) {
				if (this.disabled) return
				// 打开颜色选择器
				this.$refs.colorPicker.open();
			},
			confirm(e) {
				this.bgColor = e.colorVal
				this.$emit('input', e.colorVal)
			}
		}
	};
</script>
<style lang="scss">
	.t-page {
		flex: 1;
		display: flex;
		flex-direction: row;
		align-items: center;
		justify-content: flex-end;

		.color-box {
			width: 70rpx;
			height: 70rpx;
			border: 1px solid #e6e6e6;
			background-color: #fff;
			border-radius: 10rpx;
			display: flex;
			flex-direction: column;
			align-items: center;
			justify-content: center;

			.colorVal {
				width: 48rpx;
				height: 48rpx;
				border: 1px solid #999;
				border-radius: 6rpx;
				display: flex;
				flex-direction: column;
				align-items: center;
				justify-content: center;
				background-color: #fff;

				.colorVal-inner {
					width: 100%;
					height: 100%;
					color: #c7c7c7;
					display: flex;
					flex-direction: column;
					justify-content: center;
					align-items: center;
					text-align: center;
				}
			}
		}
	}
</style>
