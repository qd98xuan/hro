<template>
	<view :class="'linzen-radio linzen-radio-right'">
		<view class="" v-if="direction === 'horizontal'">
			<u-radio-group @change="onChange" :disabled="disabled" v-model="initValue" style="display:flow-root;">
				<u-radio label-size='26' v-for="(item, index) in options" :key="index" :name="item[props.value]">
					{{ item[props.label] }}
				</u-radio>
			</u-radio-group>
		</view>
		<!-- 竖 -->
		<view class="radio-c" v-else>
			<u-radio-group @change="onChange" :disabled="disabled" v-model="initValue">
				<u-radio label-size='26' v-for="(item, index) in options" :key="index" :name="item[props.value]">
					{{ item[props.label] }}
				</u-radio>
			</u-radio-group>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'linzen-radio',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				default: ''
			},
			direction: {
				type: String,
				default: "horizontal"
			},
			options: {
				type: Array,
				default: () => []
			},
			props: {
				type: Object,
				default: () => ({
					label: 'fullName',
					value: 'id'
				})
			},
			disabled: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				initValue: '',
			}
		},
		watch: {
			value: {
				immediate: true,
				handler(val) {
					this.initValue = val
				},
			}
		},
		created() {
			this.initValue = this.value
		},
		methods: {
			onChange(value) {
				let selectData = this.options.filter(o => o[this.props.value] == value)
				this.$emit('input', value)
				this.$emit('change', value, selectData[0])
			},
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-radio {
		width: 100%;

		.radio-c {
			/deep/.u-radio {
				width: 100% !important;
				flex: 0 0 100%;
				height: 36px;
				justify-content: flex-end
			}

			/deep/.u-radio__label {
				flex: none;
				margin-left: 20px;
				font-size: 30rpx !important;
			}

			/deep/.u-radio-group {
				width: 100%;
			}
		}


		&.linzen-radio-right {
			text-align: right;
		}

	}
</style>