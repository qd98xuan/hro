<template>
	<view class="linzen-org-select">
		<linzen-tree-select v-model="innerValue" :placeholder="placeholder" :options="options" :multiple="multiple"
			:lastLevel="type!=='organize'" lastLevelKey="type" :lastLevelValue="type" :disabled="disabled"
			@change="change">
		</linzen-tree-select>
	</view>
</template>

<script>
	import linzenTreeSelect from '../linzen-tree-select/index.vue';
	import {
		getOrgByOrganizeCondition,
		getPositionByPositionCondition
	} from '@/api/common.js'
	export default {
		name: 'linzen-org-select',
		model: {
			prop: 'value',
			event: 'input'
		},
		components: {
			linzenTreeSelect
		},
		props: {
			value: {
				default: ''
			},
			// organize/department/position/user
			type: {
				type: String,
				default: 'user'
			},
			placeholder: {
				type: String,
				default: '请选择'
			},
			disabled: {
				type: Boolean,
				default: false
			},
			multiple: {
				type: Boolean,
				default: false
			},
			ableDepIds: {
				type: Array,
				default: () => []
			},
			ablePosIds: {
				type: Array,
				default: () => []
			},
			selectType: {
				type: String,
				default: 'all'
			}
		},
		data() {
			return {
				options: [],
				innerValue: '',
				defaultValue: []
			}
		},
		watch: {
			innerValue(val) {
				this.$emit('input', val)
			},
			value(val) {
				this.innerValue = val
			}
		},
		created() {
			this.getOptions()
		},
		methods: {
			getMethod() {
				let method = ''
				switch (this.type) {
					case 'department':
						method = 'getDepartmentTree'
						break;
					case 'position':
						method = 'getPositionTree'
						break;
					default:
						method = 'getDepartmentTree'
						break;
				}
				return method
			},
			change(e, data) {
				this.$emit('change', e, data)
			},
			async getOptions() {
				if (this.selectType === 'all') {
					const method = this.getMethod()
					this.options = await this.$store.dispatch(`base/${method}`)
				} else {
					let method = this.type === 'department' ? getOrgByOrganizeCondition :
						getPositionByPositionCondition
					let query = {
						keyword: "",
						departIds: this.ableDepIds
					}
					if (this.type !== 'department') {
						query.positionIds = this.ablePosIds
					}
					method(query).then(res => {
						this.options = res.data.list
					})
				}
				this.innerValue = this.value
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-org-select {
		width: 100%;
	}
</style>
