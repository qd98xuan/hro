<template>
	<!-- 请注意弹框的显隐用v-show，不要用v-if -->
	<view v-show="visibleSync" :class="{
			'drawer--visible':showDrawer,
			'drawer--right':rightMode
		}" class="drawer" @touchmove.stop.prevent="handleMove">
		<view class="drawer__mask" @click="handleClose" />
		<view class="drawer__content" :style="{width: width}">
			<slot />
		</view>
	</view>
</template>

<script>
	export default {
		name: 'drawer',

		props: {
			// 显示状态
			visible: {
				type: Boolean,
				default: false
			},

			//显示模式（左、右），只在初始化生效
			mode: {
				type: String,
				default: ''
			},

			// 蒙层显示状态
			mask: {
				type: Boolean,
				default: true
			},

			// 抽屉宽度，百分比
			width: {
				type: String,
				default: '61.8%'
			}
		},

		data() {
			return {
				visibleSync: false,
				showDrawer: false,
				rightMode: false
			}
		},

		watch: {
			visible(val) {
				console.log(val);
				clearTimeout(this.watchTimer)
				setTimeout(() => {
					this.showDrawer = val
				}, 100)

				if (this.visibleSync) {
					clearTimeout(this.closeTimer)
				}

				if (val) {
					this.visibleSync = val
				} else {
					this.watchTimer = setTimeout(() => {
						this.visibleSync = val
					}, 300)
				}
			}
		},

		created() {
			this.visibleSync = this.visible
			setTimeout(() => {
				this.showDrawer = this.visible
			}, 100)
			this.rightMode = this.mode === 'right'
		},

		methods: {
			handleClose() {
				this.showDrawer = false
				this.closeTimer = setTimeout(() => {
					this.visibleSync = false
					this.$emit('close')
				}, 200)
			},

			handleMove() {}
		}
	}
</script>

<style>
	.drawer {
		display: block;
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		overflow: hidden;
		visibility: hidden;
		height: 100%;
		z-index: 999;
	}

	.drawer.drawer--right .drawer__content {
		left: auto;
		right: 0;
		transform: translatex(100%);
	}

	.drawer.drawer--visible {
		visibility: visible;
	}

	.drawer.drawer--visible .drawer__content {
		transform: translatex(0);
	}

	.drawer.drawer--visible .drawer__mask {
		display: block;
		opacity: 1;
	}

	.drawer__mask {
		display: block;
		opacity: 0;
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background: rgba(0, 0, 0, 0.4);
		transition: opacity 0.3s;
	}

	.drawer__content {
		display: block;
		position: absolute;
		top: 0;
		left: 0;
		height: 100%;
		background: #fff;
		transition: all 0.3s ease-out;
		transform: translatex(-100%);
	}
</style>