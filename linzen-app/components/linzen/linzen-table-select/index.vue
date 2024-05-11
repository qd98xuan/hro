<template>
	<view class="linzen-table-select">
		<u-input input-align='right' type="select" v-model="innerValue" disabled @click="openSelect"
			:placeholder="placeholder">
		</u-input>
	</view>
</template>

<script>
	import {
		getDataInterfaceDataInfoByIds
	} from '@/api/common.js'
	export default {
		name: 'linzen-table-select',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				type: [String, Array],
				default: ''
			},
			placeholder: {
				type: String,
				default: '请选择'
			},
			disabled: {
				type: Boolean,
				default: false
			},
			filterable: {
				type: Boolean,
				default: false
			},
			columnOptions: {
				type: Array,
				default: []
			},
			relationField: {
				type: String,
				default: ''
			},
			propsValue: {
				type: String,
				default: ''
			},
			popupTitle: {
				type: String,
				default: ''
			},
			interfaceId: {
				type: String,
				default: ''
			},
			hasPage: {
				type: Boolean,
				default: false
			},
			multiple: {
				type: Boolean,
				default: false
			},
			pageSize: {
				type: Number,
				default: 10000
			},
			vModel: {
				type: String,
				default: ''
			},
			rowIndex: {
				default: null
			},
			formData: {
				type: Object
			},
			templateJson: {
				type: Array,
				default: () => []
			},
		},
		data() {
			return {
				selectShow: false,
				innerValue: '',
				defaultValue: '',
				current: null,
				defaultOptions: [],
				firstVal: '',
				firstId: 0,
				list: [],
				selectData: []
			}
		},
		watch: {
			value(val) {
				this.$nextTick(() => {
					if (val != '' || val.length > 0) {
						this.setDefault()
					} else {
						this.innerValue = ''
					}
				})
			},
		},
		created() {
			this.setDefault()
			uni.$on('tabConfirm', (subVal, txt, vModel, list) => {
				this.tabConfirm(subVal, txt, vModel, list)
			})
		},
		methods: {
			setDefault() {
				if (!this.value || !this.value.length) return this.innerValue = ''
				let query = {
					ids: !this.multiple ? [this.value] : this.value,
					interfaceId: this.interfaceId,
					propsValue: this.propsValue,
					relationField: this.relationField,
					paramList: this.getParamList()
				}
				if (!this.interfaceId) return
				let value = !this.multiple ? [this.value] : this.value
				getDataInterfaceDataInfoByIds(this.interfaceId, query).then(res => {
					this.list = res.data || []
					this.selectData = this.list
					let label = []
					this.list.forEach((o, i) => {
						for (let j = 0; j < value.length; j++) {
							if (value[j] == o[this.propsValue]) {
								if (!!o[this.relationField]) {
									label.push(o[this.relationField])
								}
							}
						}
					})
					this.innerValue = label.length == 1 ? label[0] : label.join(',')
					let relationData = this.$store.getters.relationData
					this.$set(relationData, this.vModel, this.list)
					this.$store.commit('base/UPDATE_RELATION_DATA', relationData)
				})
			},
			getParamList() {
				let templateJson = this.templateJson
				if (!this.formData) return templateJson
				for (let i = 0; i < templateJson.length; i++) {
					if (templateJson[i].relationField) {
						if (templateJson[i].relationField.includes('-')) {
							let tableVModel = templateJson[i].relationField.split('-')[0]
							let childVModel = templateJson[i].relationField.split('-')[1]
							templateJson[i].defaultValue = this.formData[tableVModel] && this.formData[tableVModel][this
								.rowIndex
							] && this.formData[tableVModel][this.rowIndex][childVModel] || ''
						} else {
							templateJson[i].defaultValue = this.formData[templateJson[i].relationField] || ''
						}
					}
				}
				return templateJson
			},
			openSelect() {
				if (this.disabled) return
				const pageSize = this.hasPage ? this.pageSize : 100000
				let data = {
					columnOptions: this.columnOptions,
					relationField: this.relationField,
					propsValue: this.propsValue,
					modelId: this.interfaceId,
					hasPage: this.hasPage,
					pageSize,
					id: !this.multiple ? [this.value] : this.value,
					vModel: this.vModel,
					popupTitle: this.popupTitle || '选择数据',
					innerValue: this.innerValue,
					multiple: this.multiple,
					filterable: this.filterable,
					paramList: this.getParamList(),
					selectData: this.selectData
				}
				uni.navigateTo({
					url: '/pages/apply/tableSelect/index?data=' + encodeURIComponent(JSON.stringify(data))
				})
			},
			tabConfirm(subVal, txt, vModel, list) {
				if (vModel === this.vModel) {
					this.innerValue = ''
					this.innerValue = txt
					if (!this.multiple) {
						this.$emit('input', subVal[0])
						this.$emit('change', subVal[0], list[0])
					} else {
						this.$emit('input', subVal)
						this.$emit('change', subVal, list)
					}
				}
			},
		}
	}
</script>

<style>
	.linzen-table-select {
		width: 100%;
		height: 100%;
	}
</style>