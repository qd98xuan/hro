<template>
	<view class="linzen-switch">
		<u-switch v-model="innerValue" :active-value="activeValue" :inactive-value="inactiveValue" :disabled="disabled"
			@change="onChange" :size="size"></u-switch>
	</view>
</template>

<script>
	export default {
		name: 'linzen-switch',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				default: 0
			},
			activeValue: {
				default: 1
			},
			inactiveValue: {
				default: 0
			},
			size: {
				default: 40
			},
			disabled: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				innerValue: false
			}
		},
		watch: {
			value(val) {
				this.innerValue = !!val
			}
		},
		created() {
			this.innerValue = !!this.value
		},
		methods: {
			onChange(value) {
				this.$emit('input', value)
				this.$emit('change', value)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-switch {
		width: 100%;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: flex-end;
		margin-top: 14rpx;
	}
</style>