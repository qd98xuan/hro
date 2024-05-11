<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<com-tree v-model="selectShow" @confirm="selectConfirm" :multiple="multiple" :props="props"
			:selectedData="selectedData" :options="options" :selectId='multiple?value:[value]'>
		</com-tree>
	</view>
</template>

<script>
	import comTree from './com-tree';
	import {
		getOrgByOrganizeCondition
	} from '@/api/common.js'
	export default {
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			comTree
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
			selectType: {
				type: String,
				default: 'all'
			},
			ableIds: {
				type: Array,
				default: () => []
			}
		},

		data() {
			return {
				selectShow: false,
				innerValue: '',
				selectedData: [],
				allList: [],
				options: []
			}
		},
		watch: {
			value: {
				immediate: true,
				handler(val) {
					this.getOptions()
				}
			},
		},
		methods: {
			async getOptions() {
				this.options = await this.$store.dispatch(`base/getDepartmentTree`)
				this.allList = this.$store.getters.departmentList
				if (this.selectType !== 'all') {
					const departIds = this.ableIds ? this.ableIds.map(o => o[o.length - 1]) : [];
					const query = {
						departIds
					};
					await getOrgByOrganizeCondition(query).then(res => {
						this.options = res.data.list || []
					})
				}
				if (!this.value || !this.value.length) {
					this.innerValue = ''
					this.selectedData = [];
					return
				}
				this.setDefault()
			},
			setDefault() {
				let val = this.multiple ? this.value : [this.value];
				let textList = []
				this.selectedData = [];
				this.innerValue = ''
				for (let i = 0; i < val.length; i++) {
					let item = val[i];
					inner: for (let j = 0; j < this.allList.length; j++) {
						if (item.toString() === this.allList[j].organizeIds.toString()) {
							item = this.allList[j].organize
							break inner
						}
					};
					textList.push(item)
				}
				this.selectedData = textList
				this.innerValue = this.selectedData.join(',')
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
			selectConfirm(e, selectId) {
				this.$emit('input', selectId)
				this.$emit('change', selectId)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>