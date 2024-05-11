<template>
	<view class="linzen-relation-select">
		<u-input :class="{'link-style':disabled&&innerValue}" input-align='right' type="select" v-model="innerValue"
			disabled @click="openSelect" :placeholder="placeholder"></u-input>
	</view>
</template>

<script>
	import {
		getDataChange
	} from '@/api/common.js'
	export default {
		name: 'linzen-relation-select',
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
				default: []
			},
			relationField: {
				type: String,
				default: ''
			},
			type: {
				type: String,
				default: 'relation'
			},
			propsValue: {
				type: String,
				default: ''
			},
			modelId: {
				type: String,
				default: ''
			},
			hasPage: {
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
			popupTitle: {
				type: String,
				default: ''
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
			}
		},
		watch: {
			value(val) {
				this.setDefault()
			},
		},

		created() {
			uni.$on('confirm1', (subVal, innerValue, list, selectData) => {
				this.confirm(subVal, innerValue, list, selectData)
			})
			this.setDefault()
		},
		methods: {
			setDefault() {
				if (this.value) {
					if (!this.modelId) return
					getDataChange(this.modelId, this.value).then(res => {
						if (!res.data || !res.data.data) return
						let data = JSON.parse(res.data.data)
						this.innerValue = data[this.relationField]
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
			openSelect() {
				if (this.disabled) {
					if (!this.value) return
					let config = {
						modelId: this.modelId,
						id: this.value,
						formTitle: '详情',
						noShowBtn: 1
					}
					this.$nextTick(() => {
						const url =
							'/pages/apply/dynamicModel/detail?config=' + this.base64.encode(JSON.stringify(config),
								"UTF-8")
						uni.navigateTo({
							url: url
						})
					})
					return
				}
				let data = {
					columnOptions: this.columnOptions,
					relationField: this.relationField,
					type: this.type,
					propsValue: this.propsValue,
					modelId: this.modelId,
					hasPage: this.hasPage,
					pageSize: this.pageSize,
					id: this.value,
					vModel: this.vModel,
					popupTitle: this.popupTitle || '选择数据',
					innerValue: this.innerValue
				}
				uni.navigateTo({
					url: '/pages/apply/popSelect/index?data=' + encodeURIComponent(JSON.stringify(data))
				})
			},
			confirm(subVal, innerValue, vModel, selectData) {
				if (vModel === this.vModel) {
					this.firstVal = innerValue || '';
					this.firstId = subVal;
					this.innerValue = innerValue || '';
					this.$emit('input', subVal)
					this.$emit('change', subVal, selectData)
				}
			},
		}
	}
</script>

<style lang="scss" scoped>
	.linzen-relation-select {
		width: 100%;
		height: 100%;
	}

	.link-style {
		/deep/.uni-input-wrapper {
			color: #1890ff !important;
			text-decoration: underline;
			cursor: pointer !important;
		}
	}
</style>