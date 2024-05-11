<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<group v-model="selectShow" :options="options" :multiple="multiple" :props="props" :selectedData="selectedData"
			:selectId="!multiple ? [value] : value" @confirm="selectConfirm">
		</group>
	</view>
</template>
<script>
	import group from './group.vue';
	import {
		getGroupCondition
	} from '@/api/common.js'
	export default {
		name: 'linzen-group-select',
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			group
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
			selectType: {
				type: String,
				default: 'all'
			},
			ableIds: {
				type: Array,
				default: () => []
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
				this.options = await this.$store.dispatch('base/getGroupTree')
				this.allList = await this.$store.getters.groupList
				if (this.selectType === 'all') this.setDefault()
				if (this.selectType === 'custom') this.getGroupCondition()
			},
			getGroupCondition() {
				let query = {
					ids: this.ableIds
				}
				getGroupCondition(query).then(res => {
					this.options = res.data.list || [];
					if (this.value && this.value.length) {
						this.setDefault()
					}
				})
			},
			setDefault() {
				const values = this.multiple ? this.value : [this.value];
				this.selectedData = [];
				this.innerValue = '';
				for (const val of values) {
					const item = this.allList.find(group => group.id === val);
					if (item) this.selectedData.push(item);
				}
				this.innerValue = this.selectedData.map(item => item.fullName).join(',');
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