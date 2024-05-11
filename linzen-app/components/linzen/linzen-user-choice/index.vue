<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<user-tree v-model="selectShow" @confirm="selectConfirm" :options="options" :multiple="multiple" :props="props"
			:selectedData="selectedData" :selectType="selectType" :query='query' :clearable="clearable"
			:roleOption='roleOption' :groupOption="groupOption" :posOption='posOption' :list="list"
			@scrollToLower="getSelectedUserList">
		</user-tree>
	</view>
</template>
<script>
	import userTree from './user-tree.vue';
	import {
		getSelectedList,
		getGroupSelector,
		getPositionSelector,
		getRoleSelector,
		getSelectedUserList
	} from '@/api/common.js'
	export default {
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			userTree
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
			ableIds: {
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
				query: {
					ids: this.ableIds
				},
				roleOption: [],
				posOption: [],
				groupOption: [],
				list: [],
				pagination: {
					currentPage: 1,
					pageSize: 20,
					keyword: ''
				},
			}
		},
		watch: {
			value: {
				handler(val) {
					this.getSelectedList(val)
				},
				immediate: true
			}
		},
		created() {
			if (this.multiple && this.selectType === 'all') this.init()
			if (this.selectType !== 'all') this.getSelectedUserList()
		},

		methods: {
			init() {
				getGroupSelector().then(res => {
					this.groupOption = res.data
				})
				getPositionSelector().then(res => {
					this.posOption = res.data.list
				})
				getRoleSelector().then(res => {
					this.roleOption = res.data.list
				})
			},
			getSelectedUserList() {
				let query = this.query;
				query.pagination = this.pagination
				getSelectedUserList(query).then(res => {
					const list = res.data.list;
					this.list = this.list.concat(list);
				})
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
			getSelectedList(id) {
				if (id == null) return
				let ids = this.multiple ? id : [id]
				getSelectedList(ids).then(res => {
					let subVal = ''
					let resList = res.data.list || [];
					let txt = ''
					let arrSubVal = []
					for (let i = 0; i < resList.length; i++) {
						txt += (i ? ',' : '') + resList[i].fullName
						subVal = resList[i].id + '--' + resList[i].type
						if (this.multiple) arrSubVal.push(subVal)
					}
					this.innerValue = txt
					this.selectedData = resList
				})
			},
			selectConfirm(id, selectList) {
				this.$emit('input', this.multiple ? id : id[0])
				this.$emit('change', this.multiple ? id : id[0], selectList)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>