<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<post-tree v-model="selectShow" @confirm="selectConfirm" :multiple="multiple" :props="props"
			:selectedData="selectedData" :options="options" :ids="multiple?value:[value]" :title='title' :type='type'>
		</post-tree>
	</view>
</template>

<script>
	import postTree from './post-tree';
	import {
		getOrgByOrganizeCondition,
		getPositionByPositionCondition
	} from '@/api/common.js'
	export default {
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			postTree
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
			},
			type: {
				type: String,
				default: 'user'
			},
		},

		data() {
			return {
				selectShow: false,
				innerValue: '',
				selectedData: [],
				allList: [],
				options: [],
				title: '选择'
			}
		},
		watch: {
			value: {
				immediate: true,
				handler(val) {
					this.title = this.type === 'position' ? '岗位选择' : '部门选择'
					this.getOptions()
					if (!val || !val.length) {
						this.innerValue = ''
						this.selectedData = [];
					}
				}
			}
		},
		methods: {
			async getOptions() {
				const method = this.type === 'department' ? 'getDepartmentTree' : 'getPositionTree'
				this.options = await this.$store.dispatch(`base/${method}`)
				this.allList = await this.$store.dispatch('base/getPositionList')
				if (this.type === 'department') this.allList = await this.$store.getters.departmentList
				if (this.selectType !== 'all') {
					let query = {
						keyword: "",
						departIds: this.ableIds
					}
					let method = this.type === 'department' ? getOrgByOrganizeCondition :
						getPositionByPositionCondition
					if (this.type !== 'department') {
						delete query.departIds
						query.ids = this.ableIds
					}
					await method(query).then(res => {
						this.options = res.data.list
					})
				}
				if (!this.value || !this.value.length) return
				this.setDefault()
			},
			setDefault() {
				this.innerValue = '';
				this.selectedData = [];
				let val = this.multiple ? this.value : [this.value];
				for (let i = 0; i < val.length; i++) {
					inner: for (let j = 0; j < this.allList.length; j++) {
						if (this.allList[j].id === val[i]) {
							this.selectedData.push(this.allList[j])
							break inner
						}
					}
				}
				let txt = ''
				for (let i = 0; i < this.selectedData.length; i++) {
					if (!!this.selectedData[i].lastFullName) {
						txt += (i ? ',' : '') + this.selectedData[i].lastFullName
					} else {
						txt += (i ? ',' : '') + this.selectedData[i].fullName
					}
				}
				this.innerValue = txt
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
			selectConfirm(e, selectId) {
				this.$emit('input', selectId)
				if (e) this.$emit('change', selectId, e)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>