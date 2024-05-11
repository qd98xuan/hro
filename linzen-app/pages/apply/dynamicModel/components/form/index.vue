<template>
	<view class="linzen-wrap linzen-wrap-form">
		<linzen-parser :formConf="formConf" ref="dynamicForm" v-if="!loading" @submit="sumbitForm" :key="key" />
		<view class="buttom-actions" v-if="origin !='scan'">
			<u-button class="buttom-btn" @click.stop="resetForm">重置</u-button>
			<u-button class="buttom-btn" type="primary" @click.stop="submit" :loading="btnLoading">
				{{formConf.confirmButtonText||'确定'}}
			</u-button>
		</view>
	</view>
</template>

<script>
	import {
		createModel,
		getModelInfo
	} from '@/api/apply/visualDev'
	export default {
		props: ['config', 'modelId', 'isPreview', 'origin', 'id'],
		data() {
			return {
				dataForm: {
					data: ''
				},
				formConf: {},
				key: +new Date(),
				btnLoading: false,
				loading: true,
				isAdd: false,
				userInfo: {}
			}
		},
		created() {
			this.init()
		},
		methods: {
			init() {
				this.userInfo = uni.getStorageSync('userInfo') || {}
				this.formConf = JSON.parse(this.config.formData)
				this.loading = true
				this.initData()
			},
			initData() {
				this.$nextTick(() => {
					if (this.origin === 'scan') {
						let extra = {
							modelId: this.modelId,
							id: this.id,
							type: 2
						}
						uni.setStorageSync('dynamicModelExtra', extra)
						getModelInfo(this.modelId, this.id).then(res => {
							this.dataForm = res.data
							if (!this.dataForm.data) return
							this.formData = JSON.parse(this.dataForm.data)
							this.fillFormData(this.formConf, this.formData)
							this.$nextTick(() => {
								this.loading = false
							})
						})
					} else {
						uni.setStorageSync('dynamicModelExtra', {})
						this.formData = {}
						this.loading = false
						this.isAdd = true
						this.fillFormData(this.formConf, this.formData)
					}
					this.key = +new Date()
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
											let format = this.linzen.handelFormat(item.format)
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
											'--user'
										] : this.userInfo.userId + '--user';
									}
									if (item.__config__.projectKey === 'sign' && this.userInfo.signImg) item.__config__
										.defaultValue = this.userInfo.signImg
								}
							}
							if (this.origin === 'scan') {
								this.$set(item, 'disabled', true)
							}
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
						if (item.__config__ && item.__config__.children && Array
							.isArray(item.__config__.children)) {
							loop(item.__config__.children)
						}

					}
				}
				loop(form.fields)
			},
			sumbitForm(data, callback) {
				if (!data) return
				this.btnLoading = true
				this.dataForm.data = JSON.stringify(data)
				if (callback && typeof callback === "function") callback()
				createModel(this.modelId, this.dataForm).then(res => {
					uni.showToast({
						title: res.msg,
						complete: () => {
							setTimeout(() => {
								this.btnLoading = false
								uni.navigateBack()
							}, 1500)
						}
					})
				}).catch(() => {
					this.btnLoading = false
				})
			},
			submit() {
				if (this.isPreview) {
					uni.showToast({
						title: '功能预览不支持数据保存',
						icon: 'none'
					})
					return
				}
				this.$refs.dynamicForm && this.$refs.dynamicForm.submitForm()
			},
			resetForm() {
				this.loading = true
				this.$nextTick(() => {
					this.loading = false
					this.$refs.dynamicForm && this.$refs.dynamicForm.resetForm()
					this.init()
					this.key = +new Date()
				})
			}
		}
	}
</script>