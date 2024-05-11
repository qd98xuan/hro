<template>
	<view class="linzen-wrap linzen-wrap-form">
		<linzen-parser :formConf="formConf" ref="dynamicForm" v-if="!loading" @submit="sumbitForm" :key="key" />
		<view class="buttom-actions">
			<u-button class="buttom-btn" @click.stop="cancel">取消</u-button>
			<u-button class="buttom-btn" type="primary" @click.stop="submit" :loading="btnLoading">
				{{config.confirmButtonText||'确定'}}
			</u-button>
		</view>
	</view>
</template>

<script>
	import {
		getConfigData,
		getModelInfo,
		createModel
	} from '@/api/apply/visualDev'
	import {
		getDataInterfaceRes
	} from '@/api/common'
	export default {
		data() {
			return {
				config: {},
				id: "",
				modelId: "",
				formConf: {},
				dataForm: {},
				key: +new Date(),
				loading: false,
				btnLoading: false,
				isPreview: true,
				formData: {},
				isAdd: false,
				userInfo: {}
			}
		},
		onLoad(e) {
			this.userInfo = uni.getStorageSync('userInfo') || {}
			this.loading = true
			let data = e.data ? JSON.parse(e.data) : {}
			this.config = data.config
			this.id = data.id
			this.modelId = data.modelId
			this.isPreview = data.isPreview
			if (this.id != null && this.id != undefined && this.id != '') {
				this.isAdd = false
			} else {
				this.isAdd = true
			}
			uni.setNavigationBarTitle({
				title: this.config.popupTitle
			})
			if (this.config.modelId) this.getConfigData(data.row)
		},
		methods: {
			getConfigData(row) {
				getConfigData(this.config.modelId).then(res => {
					if (res.code !== 200 || !res.data) {
						uni.showToast({
							title: res.msg || '请求出错，请重试',
							icon: 'none'
						})
						return
					}
					this.formConf = JSON.parse(res.data.formData)
					const setDataFun = (formData) => {
						if (this.config.formOptions.length) {
							for (let k in formData) {
								for (let i = 0; i < this.config.formOptions.length; i++) {
									const ele = this.config.formOptions[i]
									if (ele.currentField == '@formId') this.formData[ele.field] = formData.id;
									if (ele.currentField == k) this.formData[ele.field] = formData[k]
								}
							}
						}
						this.fillFormData(this.formConf, this.formData)
						this.key = +new Date()
						this.loading = false
					}
					if (this.id) {
						getModelInfo(this.modelId, this.id).then(res => {
							let dataForm = res.data
							if (!dataForm.data) return
							const formData = JSON.parse(dataForm.data)
							this.formData = {}
							setDataFun(formData)
						})
					} else {
						const formData = row
						setDataFun(formData)
					}
				}).catch(() => {})
			},
			fillFormData(form, data) {
				const loop = list => {
					for (let i = 0; i < list.length; i++) {
						let item = list[i]
						if (item.__vModel__) {
							let val = data.hasOwnProperty(item.__vModel__) ? data[item.__vModel__] : item.__config__
								.defaultValue
							if (!item.__config__.isSubTable) item.__config__.defaultValue = val
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
									item.__config__.defaultValue = item.multiple ? [this.userInfo.userId + '--user'] :
										this.userInfo.userId + '--user';
								}
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
			cancel() {
				uni.navigateBack();
			},
			sumbitForm(data, callback) {
				if (!data) return
				this.btnLoading = true
				const successFun = (res, callback) => {
					if (callback && typeof callback === "function") callback()
					uni.showToast({
						title: res.msg,
						complete: () => {
							setTimeout(() => {
								this.btnLoading = false
								uni.navigateBack()
							}, 1500)
						}
					})
				}
				if (this.config.customBtn) {
					if (this.config.templateJson && this.config.templateJson.length) {
						this.config.templateJson.forEach((e) => {
							const value = data[e.relationField] || data[e.relationField] == 0 || data[e
								.relationField] == false ? data[e.relationField] : ''
							e.defaultValue = e.sourceType == 1 ? value : e.relationField
						})
					}
					const query = {
						paramList: this.config.templateJson || [],
					}
					getDataInterfaceRes(this.config.interfaceId, query).then(res => {
						successFun(res, callback)
					}).catch(() => {
						this.btnLoading = false
					})
				} else {
					this.dataForm.data = JSON.stringify(data)
					createModel(this.config.modelId, this.dataForm).then(res => {
						successFun(res, callback)
					}).catch(() => {
						this.btnLoading = false
					})
				}
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
		}
	}
</script>

<style scoped lang="scss">
	page {
		background-color: #f0f2f6;
	}
</style>