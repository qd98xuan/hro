<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<cascader-tree v-if="selectShow" v-model="selectShow" @confirm="selectConfirm" :multiple="multiple"
			:props="props" :selectedData="selectedData" :options="options" :selectedId="!multiple ? [value] : value"
			:filterable='filterable' :clearable="clearable" :showAllLevels="showAllLevels">
		</cascader-tree>
	</view>
</template>

<script>
	import cascaderTree from './cascader-tree';
	import {
		getProvinceSelectorInfoList
	} from '@/api/common.js'
	export default {
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			cascaderTree
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
					value: 'id',
					children: 'children'
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
			showAllLevels: {
				type: Boolean,
				default: true
			},
			filterable: {
				type: Boolean,
				default: false
			},
			clearable: {
				type: Boolean,
				default: false
			},
		},
		watch: {
			value(val) {
				if (val.length <= 0) return this.innerValue = ''
				this.setDefault(val)
			},
			options: {
				handler(val, oldVal) {
					if (this.value.length > 0) {
						this.setDefault(this.value)
					}
				},
				deep: true
			}
		},
		data() {
			return {
				selectShow: false,
				innerValue: '',
				selectedData: [],
				allList: []
			}
		},
		created() {
			if (this.value && this.value.length) this.setDefault(this.value);
		},
		methods: {
			async setDefault(value) {
				this.allList = await this.treeToArray(value)
				if (!this.multiple) value = [value]
				let txt = []
				for (let i = 0; i < value.length; i++) {
					let val = JSON.parse(JSON.stringify(value[i]))
					for (let j = 0; j < val.length; j++) {
						inner: for (let k = 0; k < this.allList.length; k++) {
							if (val[j] === this.allList[k][this.props.value]) {
								val[j] = this.allList[k][this.props.label];
								break;
							}
						}
					}
					txt.push(val)
				}
				this.selectedData = txt.map(o => this.showAllLevels ? o.join('/') : o[o.length - 1])
				this.innerValue = this.selectedData.join()
			},
			async treeToArray() {
				let options = JSON.parse(JSON.stringify(this.options))
				let list = []
				const loop = (options) => {
					for (let i = 0; i < options.length; i++) {
						const item = options[i]
						list.push(item)
						if (item[this.props.children] && Array.isArray(item[this.props
								.children])) {
							loop(item[this.props.children])
						}
					}
				}
				loop(options)
				return list
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
			selectConfirm(e, selectId) {
				this.selectedData = e;
				this.innerValue = e.join()
				if (this.multiple) {
					this.$emit('input', selectId)
					this.$emit('change', selectId)
					return
				}
				this.$emit('input', selectId[0])
				this.$emit('change', selectId[0])
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>