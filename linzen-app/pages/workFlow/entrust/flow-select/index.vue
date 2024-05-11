<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<flow-tree v-model="selectShow" @confirm="selectConfirm" :options="options" :multiple="multiple" :props="props"
			:selectedData="selectedData" :bh="bh" :clearable="clearable" ref="userTree">
		</flow-tree>
	</view>
</template>

<script>
	import flowTree from './flow-tree.vue';
	import {
		FlowEngineAll
	} from '@/api/workFlow/flowEngine.js'
	import {
		login
	} from '@/api/common';
	export default {
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			flowTree
		},
		props: {
			bh: {
				default: 450
			},
			value: {
				default: ''
			},
			options: {
				type: Array,
				default: () => []
			},
			ableDepIds: {
				type: Array,
				default: () => []
			},
			ableRoleIds: {
				type: Array,
				default: () => []
			},
			ablePosIds: {
				type: Array,
				default: () => []
			},
			ableGroupIds: {
				type: Array,
				default: () => []
			},
			ableUserIds: {
				type: Array,
				default: () => []
			},
			ableRelationIds: {
				type: Array,
				default: () => []
			},
			selectType: {
				type: String,
				default: 'all'
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
			clearable: {
				type: Boolean,
				default: false
			},
			multiple: {
				type: Boolean,
				default: false
			},
		},
		data() {
			return {
				selectShow: false,
				innerValue: '',
				selectedData: [],
				selectedIds: []
			}
		},
		watch: {
			value: {
				handler(val) {
					if (val.length == 0) return this.innerValue = ''
					this.setDefault(val)
				},
				immediate: true
			},
			selectShow(val) {
				this.$emit('userTree', val)
			}
		},
		methods: {
			setDefault(id) {
				if (!id) return this.innerValue = ''
				this.selectedData = []
				const arr = id
				if (arr) {
					FlowEngineAll(arr).then(res => {
						const list = res.data.list
						let txt = ''
						for (let i = 0; i < list.length; i++) {
							inner: for (let j = 0; j < arr.length; j++) {
								if (list[i].id == arr[j]) {
									this.selectedData.push(list[i])
									this.$refs.userTree.init(this.selectedData)
									txt += (j ? ',' : '') + list[i].fullName
									break inner
								}
							}
						}
						this.innerValue = txt
					})
				}
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
				this.$refs.userTree.resetData()
				this.setDefault()
			},
			selectConfirm(e) {
				this.selectedData = e;
				let label = ''
				let value = []
				this.defaultValue = []
				for (let i = 0; i < e.length; i++) {
					label += (i ? ',' : '') + e[i][this.props.label]
					value.push(e[i][this.props.value])
				}
				this.defaultValue = value
				this.innerValue = label
				if (!this.multiple) {
					this.$emit('input', value)
					this.$emit('change', value.join(), e[0])
					return
				}
				this.$emit('input', value)
				this.$emit('change', value, e)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>