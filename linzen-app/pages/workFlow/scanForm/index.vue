<template>
	<view class="dynamicModel-v">
		<template>
			<view class="linzen-wrap linzen-wrap-form" v-if="isShow">
				<childForm ref="child" :config="config" />
			</view>
		</template>
	</view>
</template>

<script>
	import childForm from '@/pages/workFlow/flowBefore/form'
	import {
		flowForm
	} from '@/api/workFlow/flowEngine'
	export default {
		name: 'scanForm',
		components: {
			childForm
		},
		data() {
			return {
				webType: '',
				origin: '',
				config: {},
				formConf: {},
				key: +new Date(),
				flowConfig: {},
				isShow: false,
				dataSource: ''
			}
		},
		onLoad(data) {
			let obj = JSON.parse(data.config)
			this.initData(obj)
		},
		methods: {
			initData(data) {
				flowForm(data.id).then(res => {
					const dataSource = data.ds === "propertyJson" ? "propertyJson" : "draftJson"
					if (!res.data || !res.data[dataSource]) return
					let formConf = JSON.parse(res.data[dataSource])
					let formData = {
						enCode: res.data.enCode,
						flowId: res.data.id,
						formConf: res.data[dataSource],
						formType: res.data.formType,
						fullName: res.data.fullName
					}
					this.config = {
						...formData
					}
					this.isShow = true
					this.$nextTick(() => {
						this.$refs.child.$refs.form.init(this.config)
					})
				})
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.dynamicModel-v {
		height: 100%;
	}
</style>
