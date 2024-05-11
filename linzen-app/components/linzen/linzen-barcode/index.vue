<template>
	<tki-barcode class="linzen-barcode" v-if="barcode&&showBarCode" ref="barcode" :format="format" :cid="cid"
		:val="barcode" :opations="opations" loadMake :key="key" />
</template>
<script>
	import tkiBarcode from "./tki-barcode/tki-barcode.vue"
	let unique = 0

	export default {
		props: {
			dataType: {
				type: String,
				default: 'static'
			},
			format: {
				type: String,
				default: 'code128'
			},
			lineColor: {
				type: String,
				default: '#000'
			},
			background: {
				type: String,
				default: '#fff'
			},
			relationField: {
				type: String,
				default: ''
			},
			formData: {
				type: Object
			},
			width: {
				type: Number,
				default: 4
			},
			height: {
				type: Number,
				default: 40
			},
			staticText: {
				type: String,
				default: ''
			}
		},
		components: {
			tkiBarcode
		},
		data() {
			return {
				cid: '',
				relationText: "",
				key: +new Date(),
				showBarCode: false
			}
		},
		computed: {
			barcode() {
				return this.dataType === 'static' ? this.staticText : this.relationText?.toString()
			},
			opations() {
				return {
					format: this.format,
					width: this.width,
					height: this.height,
					displayValue: false,
					lineColor: this.lineColor,
					background: this.background
				}
			}
		},
		created() {
			this.cid = this.uuid()
			this.showBarCode = true
			uni.$on('upDateCode', (subVal, Vmodel) => {
				this.showBarCode = false
				this.$nextTick(() => {
					this.key = +new Date()
					this.showBarCode = true
				})
			})
		},
		watch: {
			formData: {
				handler: function(val) {
					if (val && this.dataType === 'relation' && this.relationField) {
						this.relationText = val[this.relationField]
						this.key = +new Date()
					}
				},
				deep: true,
				immediate: true
			},
		},
		methods: {
			uuid() {
				const time = Date.now()
				const random = Math.floor(Math.random() * 1000000000)
				unique++
				return 'barcode_' + random + unique + String(time)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-barcode {
		width: 100%;
		overflow: hidden;
		margin-bottom: -20rpx;
	}
</style>