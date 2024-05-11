<template>
	<view class="linzen-relation linzen-relation-right">
		<u-input input-align='right' disabled :placeholder="placeholder" v-model="value"></u-input>
	</view>
</template>

<script>
	export default {
		name: 'linzen-relation-attr',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: ["showField", "relationField", 'type', 'isStorage'],
		data() {
			return {
				value: ''
			}
		},
		computed: {
			relationData() {
				return this.$store.getters.relationData
			},
			placeholder() {
				const typeMap = {
					relationFormAttr: {
						0: '用于展示关联表单的属性，且数据不会保存',
						1: '用于展示关联表单的属性，且数据同时会保存入库'
					},
					popupAttr: {
						0: '用于展示弹窗选择的属性，且数据不会保存',
						1: '用于展示关联弹窗的属性，且数据同时会保存入库'
					}
				};
				if (this.type && this.isStorage !== undefined) {
					return typeMap[this.type][this.isStorage] || '';
				}
			}
		},
		watch: {
			relationData: {
				handler(val) {
					if (!this.showField || !this.relationField) return
					let obj = val[this.relationField] || {}
					this.value = obj[this.showField] ? obj[this.showField] : ''
					this.$emit('input', this.value)
					this.$emit('change', this.value)
				},
				deep: true
			}
		}
	}
</script>

<style lang="scss" scoped>
	.linzen-relation {
		width: 100%;

		&.linzen-relation-right {
			text-align: right;
		}
	}
</style>