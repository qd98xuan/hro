<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<city-tree v-if="selectShow" v-model="selectShow" @confirm="selectConfirm" :multiple="multiple" :props="props"
			:selectedData="selectedData" :level='level' :selectId="selectId" :oldSelectData="selectData">
		</city-tree>
	</view>
</template>

<script>
	import cityTree from './city-tree';
	import {
		getProvinceSelectorInfoList
	} from '@/api/common.js'
	export default {
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			cityTree
		},
		props: {
			value: {
				default: ''
			},
			placeholder: {
				type: String,
				default: '请选择'
			},
			props: {
				type: Object,
				default: () => ({
					label: 'fullName',
					value: 'id',
					children: 'children',
					isLeaf: 'isLeaf'
				})
			},
			disabled: {
				type: Boolean,
				default: false
			},
			multiple: {
				type: Boolean,
				default: false
			},
			level: {
				type: Number,
				default: 0
			}
		},
		watch: {
			value: {
				handler(val) {
					this.setDefault()
				},
				immediate: true
			}
		},
		data() {
			return {
				selectShow: false,
				innerValue: '',
				selectedData: [],
				selectId: [],
				selectData: []
			}
		},
		methods: {
			setDefault() {
				if (!Array.isArray(this.value) || this.value.length < 1) {
					this.selectedData = []
					this.innerValue = ''
					return
				}
				const values = this.multiple ? this.value : [this.value];
				this.selectId = values
				getProvinceSelectorInfoList(values).then(res => {
					const list = res.data
					let txt = ''
					for (let i = 0; i < list.length; i++) {
						txt += (i ? ',' : '') + list[i].join('/')
						this.selectedData.push(list[i].join('/'))
					}
					this.innerValue = txt
				})
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
			selectConfirm(selectId, selectData) {
				this.selectData = selectData
				if (!this.multiple) {
					this.$emit('input', selectId[0])
					this.$emit('change', selectId[0], selectData)
					return
				}
				this.$emit('input', selectId)
				this.$emit('change', selectId, selectData)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>