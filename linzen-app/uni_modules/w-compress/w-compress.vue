<template>
	<view class="compress__canvas">
		<!-- #ifndef H5 -->
		<canvas canvas-id="compress_canvas" :style="{width: width + 'px', height: height + 'px'}"></canvas>
		<!-- #endif -->
	</view>
</template>

<script>
	import compress from './compress.js'
	export default {
		name: 'wCompress',
		data() {
			return {
				width: 0,
				height: 0
			}
		},
		methods: {
			start(imgUrl, options = {}) {
				return new Promise(async (resolve, reject) => {
					if (imgUrl instanceof Array) {
						try {
							let arr = []
							for (let i = 0; i < imgUrl.length; i++) {
								let url = await compress(imgUrl[i], this, options)
								arr.push(url)
							}

							resolve(arr)
						} catch (e) {
							reject(e)
						}
					} else {
						compress(imgUrl, this, options)
							.then(resolve)
							.catch(reject)
					}
				})
			}
		}
	}
</script>

<style scoped>
	.compress__canvas {
		position: absolute;
		left: 10000px;
		visibility: hidden;
		height: 0;
		overflow: hidden;
	}
</style>
