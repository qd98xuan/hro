<template>
	<uni-rate class="linzen-rate" v-model="innerValue" :size="20" :max="max" :allowHalf="allowHalf" :disabled="disabled"
		@change="onChange" />
</template>
<script>
	export default {
		name: 'linzen-rate',
		inheritAttrs: false,
		props: {
			value: {
				type: [Number, String],
				default: 0
			},
			allowHalf: {
				type: Boolean,
				default: false
			},
			max: {
				type: Number,
				default: 5
			},

			disabled: {
				type: Boolean,
				default: false
			}
		},
		watch: {
			value: {
				handler(val) {
					this.innerValue = Number(val)
				},
				immediate: true
			}
		},
		data() {
			return {
				innerValue: 0
			}
		},
		methods: {
			onChange(data) {
				this.$emit('input', data.value)
				this.$emit('change', data.value)
			},
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-rate {
		width: 100%;
		display: flex;
		justify-content: flex-end
	}
</style>