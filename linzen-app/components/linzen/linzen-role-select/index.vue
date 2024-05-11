<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<role v-model="selectShow" :options="options" :multiple="multiple" :props="props" :selectedData="selectedData"
			:selectId="!multiple ? [value] : value" @confirm="selectConfirm">
		</role>
	</view>
</template>
<script>
	import role from './role.vue';
	import {
		getRoleCondition
	} from '@/api/common.js'
	export default {
		name: 'linzen-role-select',
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			role
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
				options: [],
				selectedData: [],
				allList: [],
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
				this.$store.dispatch('base/getRoleTree').then(res => {
					this.options = res || []
				})
				this.allList = await this.$store.dispatch('base/getRoleList')
				if (this.selectType === 'all') this.setDefault()
				if (this.selectType === 'custom') this.getRoleCondition()
			},
			getRoleCondition() {
				let query = {
					ids: this.ableIds
				}
				getRoleCondition(query).then(res => {
					this.options = res.data.list || [];
					if (this.value && this.value.length > 0) {
						this.setDefault()
					}
				})
			},
			setDefault(value) {
				let val = this.multiple ? this.value : [this.value];
				this.innerValue = ''
				this.selectedData = []
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
					txt += (i ? ',' : '') + this.selectedData[i].fullName
				}
				this.innerValue = txt
			},
			selectConfirm(e, selectId) {
				if (!this.multiple) {
					this.$emit('input', selectId[0])
					this.$emit('change', selectId[0], e[0])
					return
				}
				this.$emit('input', selectId)
				this.$emit('change', selectId, e)
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>