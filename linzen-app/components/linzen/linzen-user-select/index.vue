<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<user-tree v-model="selectShow" @confirm="selectConfirm" :multiple="multiple" :props="props" :list="list"
			:selectedData="selectedData" :selectType="selectType" :query='ableQuery' :clearable="clearable"
			ref="userTree">
		</user-tree>
	</view>
</template>

<script>
	import userTree from './user-tree.vue';
	import {
		getUserInfoList,
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
			ableIds: {
				type: Array,
				default: () => []
			},
			ableRelationIds: {
				type: [Array, String],
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
				ableQuery: {
					ids: this.ableIds,
				},
				pagination: {
					currentPage: 1,
					pageSize: 20,
					keyword: ''
				},
				list: []
			}
		},
		watch: {
			value: {
				handler(val) {
					this.setDefault(val)
				},
				immediate: true
			},
			ableRelationIds: {
				handler(val) {
					this.initData()
				},
				immediate: true
			},
		},
		created() {
			this.initData()
		},
		methods: {
			initData() {
				if (this.selectType === 'all') {
					this.setDefault()
				} else {
					if (this.selectType === 'custom') {
						this.ableQuery = {
							ids: this.ableIds,
						}
					} else {
						const suffix = '--' + this.getAbleKey(this.selectType);
						let ableIds = !this.ableRelationIds ? [] : Array.isArray(this.ableRelationIds) ? this
							.ableRelationIds : [this.ableRelationIds];
						this.ableQuery.ids = ableIds.map(o => o + suffix);
					}
					if (Array.isArray(this.ableQuery.ids) && this.ableQuery.ids.length) this.getInfoList()
					this.setDefault()
				}
			},
			setDefault(id) {
				this.selectedData = []
				if (!id) return this.innerValue = ''
				const arr = typeof(id) === 'string' ? id.split(',') : id
				getUserInfoList(arr).then(res => {
					const list = res.data.list
					this.selectedData = list
					let txt = ''
					for (let i = 0; i < list.length; i++) {
						txt += (i ? ',' : '') + list[i].fullName
					}
					this.innerValue = txt
				})
			},
			getInfoList() {
				let query = this.ableQuery;
				query.pagination = this.pagination
				getSelectedUserList(query).then(res => {
					const list = res.data.list || [];
					this.list = list;
				})
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
			},
			getAbleKey(selectType) {
				if (selectType === 'dep') return 'department';
				if (selectType === 'pos') return 'position';
				if (selectType === 'role') return 'role';
				if (selectType === 'group') return 'group';
			},
			selectConfirm(e) {
				this.selectedData = e;
				let label = ''
				let value = []
				for (let i = 0; i < e.length; i++) {
					label += (!label ? '' : ',') + e[i][this.props.label]
					value.push(e[i][this.props.value])
				}
				this.defaultValue = value
				this.innerValue = label
				if (!this.multiple) {
					this.$emit('input', value.join())
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