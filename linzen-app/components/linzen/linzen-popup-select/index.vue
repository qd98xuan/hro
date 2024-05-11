<template>
	<view class="linzen-popup-select">
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
		name: 'linzen-popup-select',
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
			disabled: {
				type: Boolean,
				default: false
			},
			columnOptions: {
				type: Array,
				default: () => []
			},
			relationField: {
				type: String,
				default: ''
			},
			type: {
				type: String,
				default: 'popup'
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
			pageSize: {
				type: Number,
				default: 100000
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
			}
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
			}
		},
		watch: {
			value(val) {
				this.setDefault()
			},
		},
		created() {
			uni.$on('confirm', (subVal, innerValue, list, selectData) => {
				this.confirm(subVal, innerValue, list, selectData)
			})
			this.setDefault()
		},
		methods: {
			setDefault() {
				if (this.value) {
					if (!this.interfaceId) return
					const paramList = this.getParamList()
					let query = {
						ids: [this.value],
						interfaceId: this.interfaceId,
						propsValue: this.propsValue,
						relationField: this.relationField,
						paramList
					}
					getDataInterfaceDataInfoByIds(this.interfaceId, query).then(res => {
						const data = res.data && res.data.length ? res.data[0] : {};
						if (data[this.relationField]) this.innerValue = data[this.relationField]
						if (!this.vModel) return
						let relationData = this.$store.getters.relationData
						this.$set(relationData, this.vModel, data)
						this.$store.commit('base/UPDATE_RELATION_DATA', relationData)
					})
				} else {
					this.innerValue = ''
					if (!this.vModel) return
					let relationData = this.$store.getters.relationData
					this.$set(relationData, this.vModel, {})
					this.$store.commit('base/UPDATE_RELATION_DATA', relationData)
				}
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
					type: this.type,
					propsValue: this.propsValue,
					modelId: this.interfaceId,
					hasPage: this.hasPage,
					pageSize,
					id: this.value,
					vModel: this.vModel,
					popupTitle: this.popupTitle || '选择数据',
					innerValue: this.innerValue,
					paramList: this.getParamList()
				}
				uni.navigateTo({
					url: '/pages/apply/popSelect/index?data=' + encodeURIComponent(JSON.stringify(data))
				})
			},
			confirm(subVal, innerValue, vModel, selectData) {
				if (vModel === this.vModel) {
					this.firstVal = innerValue;
					this.firstId = subVal;
					this.innerValue = innerValue;
					this.$emit('input', subVal)
					this.$emit('change', subVal, selectData)
				}
			},
		}
	}
</script>

<style>
	.linzen-popup-select {
		width: 100%;
		height: 100%;
	}
</style>