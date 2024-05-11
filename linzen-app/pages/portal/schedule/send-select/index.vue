<template>
	<view class="linzen-tree-select">
		<u-input input-align='right' type="select" :select-open="selectShow" v-model="innerValue"
			:placeholder="placeholder" @click="openSelect"></u-input>
		<flow-tree v-model="selectShow" @confirm="selectConfirm" :selectedId="selectedId" :bh="bh" ref="userTree">
		</flow-tree>
	</view>
</template>

<script>
	import flowTree from './send-tree.vue';
	import {
		getMsgTemplate
	} from '@/api/portal/portal.js'
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
			send: {
				type: String,
				default: ''
			},
			placeholder: {
				type: String,
				default: '请选择'
			},
			disabled: {
				type: Boolean,
				default: false
			},
			sendName: {
				type: String,
				default: '请选择'
			},
		},
		data() {
			return {
				selectShow: false,
				innerValue: '',
				selectedId: '',
			}
		},
		watch: {
			send: {
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
				if (id) {
					this.innerValue = this.value
					this.selectedId = id
				}
			},
			openSelect() {
				if (this.disabled) return
				this.selectShow = true
				this.$refs.userTree.resetData()
				this.setDefault()
			},
			selectConfirm(e) {
				this.selectedId = e.id;
				this.defaultValue = e.id
				this.innerValue = e.fullName
				this.$emit('input', e.id)
				this.$emit('change', e.id, e.fullName)
				return
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-tree-select {
		width: 100%;
	}
</style>