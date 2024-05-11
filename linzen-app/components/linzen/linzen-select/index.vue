<template>
	<view class="linzen-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect" v-if="isForm"></u-input>
		<u-select :list="newOptions" v-model="selectShow" @confirm="selectConfirm" :value-name="props.value"
			:label-name="props.label" :default-value="defaultValue" v-if="!multiple" />
		<mult-select :list="newOptions" v-model="selectShow" @confirm="selectConfirm" :value-name="props.value"
			:label-name="props.label" :default-value="defaultValue" v-if="multiple" />
	</view>
</template>

<script>
	import multSelect from './mult-select'
	export default {
		name: 'linzen-select',
		components: {
			multSelect
		},
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				default: ''
			},
			placeholder: {
				type: String,
				default: '请选择'
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
			isForm: {
				type: Boolean,
				default: true
			},
			disabled: {
				type: Boolean,
				default: false
			},
			multiple: {
				type: Boolean,
				default: false
			}
		},
		computed: {
			newOptions() {
				return this.options.map((o, i) => ({
					...o,
					extra: i
				}))
			}
		},
		watch: {
			newOptions() {
				this.setDefault()
			},
			value(val) {
				this.setDefault()
			}
		},
		data() {
			return {
				selectShow: false,
				innerValue: '',
				defaultValue: []
			}
		},
		created() {
			this.setDefault()
		},
		methods: {
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
			selectConfirm(e, label = '') {
				let selectData;
				if (this.multiple) {
					selectData = []
					for (let i = 0; i < this.newOptions.length; i++) {
						for (let o = 0; o < e.length; o++) {
							if (e[o] === this.newOptions[i][this.props.value]) {
								selectData.push(this.newOptions[i])
							}
						}
					}
					this.$emit('input', e)
					this.$emit('change', e, selectData)
					if (this.isForm) {
						this.defaultValue = e
						this.innerValue = label
					}
				} else {
					selectData = this.newOptions.filter(o => o[this.props.value] == e[0].value)
					this.$emit('input', e[0].value || '')
					this.$emit('change', e[0].value, selectData[0])
					this.innerValue = e[0].label || ''
					if (e[0].extra === undefined || e[0].extra === null || e[0].extra === '') return
					this.defaultValue = [e[0].extra]
				}
			},
			setDefault() {
				if (this.multiple) {
					if (!this.value || !this.value.length) return this.innerValue = ''
					this.defaultValue = this.value
					let label = ''
					outer: for (let i = 0; i < this.defaultValue.length; i++) {
						inner: for (let j = 0; j < this.options.length; j++) {
							if (this.defaultValue[i] === this.options[j][this.props.value]) {
								if (!label) {
									label += this.options[j][this.props.label]
								} else {
									label += ',' + this.options[j][this.props.label]
								}
								break inner
							}
						}
					}
					this.innerValue = label
				} else {
					if (!this.value && this.value !== 0) return this.innerValue = ''
					for (let i = 0; i < this.options.length; i++) {
						if (this.options[i][this.props.value] === this.value) {
							this.defaultValue = [i]
							this.innerValue = this.options[i][this.props.label]
							break
						}
					}
					if(!this.innerValue) this.innerValue = this.value
				}
			},
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-select {
		width: 100%;
	}
</style>