<template>
	<view class="dynamicModel-v">
		<template v-if="showPage">
			<view class="linzen-wrap linzen-wrap-form" v-if="config.mt == 2">
				<linzen-parser :formConf="formConf" ref="dynamicForm" @submit="sumbitForm" :key="key" />
			</view>
			<template v-else>
				<FlowForm ref="flowForm" :config="flowConfig" />
			</template>
		</template>
	</view>
</template>

<script>
	import FlowForm from '@/pages/workFlow/flowBefore/flowForm'
	import {
		getConfigData,
		getModelInfo
	} from '@/api/apply/visualDev'
	export default {
		name: 'scanForm',
		components: {
			FlowForm
		},
		data() {
			return {
				webType: '',
				showPage: false,
				origin: '',
				id: '',
				config: {},
				formConf: {},
				key: +new Date(),
				flowConfig: {},
				isAdd: false,
				userInfo: {}
			}
		},
		onLoad(option) {
			this.userInfo = uni.getStorageSync('userInfo') || {}
			this.config = JSON.parse(option.config)
			this.initData()
		},
		methods: {
			initData() {
				this.showPage = false
				if (this.config.mt == 2) {
					this.getConfigData()
				} else {
					this.isAdd = true
					let data = {
						flowId: this.config.fid,
						id: this.config.pid,
						formType: 2,
						opType: this.config.opt,
						taskId: this.config.ftid
					}
					this.showPage = true
					this.$nextTick(() => {
						this.$refs.flowForm.init(data)
					})
				}
			},
			getConfigData() {
				getConfigData(this.config.mid).then(res => {
					if (res.code !== 200 || !res.data) {
						uni.showToast({
							title: '暂无此页面',
							icon: 'none',
							complete: () => {
								setTimeout(() => {
									uni.navigateBack()
								}, 1500)
							}
						})
						return
					}
					this.formConf = JSON.parse(res.data.formData)
					uni.setNavigationBarTitle({
						title: res.data.fullName
					})
					let extra = {
						modelId: this.config.mid,
						id: this.config.id,
						type: this.config.mt
					}
					uni.setStorageSync('dynamicModelExtra', extra)
					getModelInfo(this.config.mid, this.config.id).then(res => {
						if (!res.data.data) return
						let formData = JSON.parse(res.data.data)
						this.fillFormData(this.formConf, formData)
						this.$nextTick(() => {
							this.showPage = true
							this.key = +new Date()
						})
					})
				})
			},
			fillFormData(form, data) {
				const loop = list => {
					for (let i = 0; i < list.length; i++) {
						let item = list[i]
						if (item.__vModel__) {
							let val = data.hasOwnProperty(item.__vModel__) ? data[item.__vModel__] : item.__config__
								.defaultValue
							if (!item.__config__.isSubTable) item.__config__.defaultValue = val
							if (this.isAdd || item.__config__.isSubTable) { //新增时候，默认当前
								if (item.__config__.defaultCurrent) {
									if (item.__config__.projectKey === 'datePicker') {
										if (!data.hasOwnProperty(item.__vModel__)) {
											let format = item.format
											let dateStr = this.linzen.toDate(new Date().getTime(), format)
											let time = format === 'yyyy' ? '-01-01 00:00:00' : format === 'yyyy-MM' ?
												'-01 00:00:00' : format === 'yyyy-MM-dd' ?
												' 00:00:00' : ''
											val = new Date(dateStr + time).getTime()
											item.__config__.defaultValue = val
										}
									}
									if (item.__config__.projectKey === 'timePicker') {
										if (!data.hasOwnProperty(item.__vModel__)) {
											val = this.linzen.toDate(new Date(), item.format)
											item.__config__.defaultValue = val
										}
									}
									if (item.__config__.projectKey === 'organizeSelect' && (this.userInfo
											.organizeIdList instanceof Array && this.userInfo.organizeIdList.length > 0
										)) {
										item.__config__.defaultValue = item.multiple ? [this.userInfo.organizeIdList] :
											this.userInfo.organizeIdList
									}
									if (item.__config__.projectKey === 'depSelect' && this.userInfo.departmentId) {
										item.__config__.defaultValue = item.multiple ? [this.userInfo.departmentId] :
											this.userInfo.departmentId;
									}
									if (item.__config__.projectKey === 'posSelect' && (this.userInfo
											.positionIds instanceof Array && this.userInfo.positionIds.length > 0)) {
										item.__config__.defaultValue = item.multiple ? this.userInfo.positionIds.map(
											o => o.id) : this.userInfo.positionIds[0].id
									}
									if (item.__config__.projectKey === 'roleSelect' && (this.userInfo
											.roleIds instanceof Array && this.userInfo.roleIds.length > 0)) {
										item.__config__.defaultValue = item.multiple ? this.userInfo.roleIds : this
											.userInfo.roleIds[0];
									}
									if (item.__config__.projectKey === 'groupSelect' && (this.userInfo
											.groupIds instanceof Array && this.userInfo.groupIds.length > 0)) {
										item.__config__.defaultValue = item.multiple ? this.userInfo.groupIds : this
											.userInfo.groupIds[0];
									}
									if (['userSelect'].includes(item.__config__.projectKey) && this.userInfo.userId) {
										item.__config__.defaultValue = item.multiple ? [this.userInfo.userId] : this
											.userInfo.userId;
									}
									if (item.__config__.projectKey === 'usersSelect' && this.userInfo.userId) {
										item.__config__.defaultValue = item.multiple ? [this.userInfo.userId +
											'--user'] : this.userInfo.userId + '--user';
									}
								}
							}
							this.$set(item, 'disabled', true)
							let noShow = !item.__config__.noShow ? false : item.__config__.noShow
							let isVisibility = false
							if (!item.__config__.visibility || (Array.isArray(item.__config__.visibility) && item
									.__config__.visibility.includes('app'))) isVisibility = true
							this.$set(item.__config__, 'isVisibility', isVisibility)
							this.$set(item.__config__, 'noShow', noShow)
						} else {
							let noShow = false,
								isVisibility = false
							if (!item.__config__.visibility || (Array.isArray(item.__config__.visibility) && item
									.__config__.visibility.includes('app'))) isVisibility = true
							this.$set(item.__config__, 'isVisibility', isVisibility)
							this.$set(item.__config__, 'noShow', noShow)
						}
						if (item.__config__ && item.__config__.projectKey !== 'table' && item.__config__.children && Array
							.isArray(item.__config__.children)) {
							loop(item.__config__.children)
						}
					}
				}
				loop(form.fields)
			},
			sumbitForm() {}
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