<template>
	<view :class="'linzen-button linzen-button-'+align">
		<u-button :custom-style="customStyle" :type="realType" :disabled="disabled" @click="onClick">{{buttonText}}
		</u-button>
	</view>
</template>
<script>
	export default {
		props: {
			align: {
				default: 'left'
			},
			buttonText: {
				default: ''
			},
			disabled: {
				type: Boolean,
				default: false
			},
			type: {
				default: ''
			}
		},
		computed: {
			realType() {
				return !this.type ? 'default' : this.type === 'danger' ? 'error' : this.type
			}
		},
		data() {
			return {
				customStyle: {
					display: 'inline-block'
				}
			}
		},
		methods: {
			onClick(event) {
				this.$emit('click', event)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-button {
		width: 100%;

		&.linzen-button-left {
			text-align: left;
		}

		&.linzen-button-center {
			text-align: center;
		}

		&.linzen-button-right {
			text-align: right;
		}
	}
</style>
