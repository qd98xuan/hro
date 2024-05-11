<template>
	<view class="linzen-wrap linzen-wrap-workflow">
		<linzen-parser :formConf="formConf" ref="dynamicForm" v-if="!loading" @submit="sumbitForm" :key="key" />
		<ErrorForm v-model="showErrorForm" @submitErrorForm="submitErrorForm" ref="ErrorForm" />
	</view>
</template>

<script>
	import ErrorForm from '../../components/ErrorForm'
	import {
		Candidates
	} from '@/api/workFlow/flowBefore'
	import {
		createModel,
		updateModel,
		getModelInfo
	} from '@/api/apply/visualDev'
	import CandidateForm from '../../components/CandidateForm'
	export default {
		components: {
			ErrorForm
		},
		props: {
			config: {
				type: Object,
				default: () => {}
			},
		},
		data() {
			return {
				loading: true,
				key: +new Date(),
				setting: {},
				formConf: {},
				formData: {},
				eventType: '',
				flowUrgent: 1,
				dataForm: {
					id: '',
					// data: '',
					flowId: ''
				},
				candidateList: [],
				candidateType: 1,
				branchList: [],
				props: {
					label: 'nodeName',
					value: 'nodeId'
				},
				properties: {},
				title: '',
				showErrorForm: false,
				isAdd: false,
				userInfo: {}
			}
		},
		methods: {
			init(data) {
				this.userInfo = uni.getStorageSync('userInfo') || {}
				this.setting = data
				this.properties = this.setting.properties || {}
				this.title = this.properties.title ? this.properties.title.replace(/\s+/g, "") : ""
				this.formConf = data.formConf ? JSON.parse(data.formConf) : {}
				this.dataForm.id = data.id || null;
				this.dataForm.flowId = data.flowId;
				this.loading = true;
				this.formData = {};
				this.$nextTick(() => {
					let extra = {}
					if (data.id) {
						this.isAdd = false;
						extra = {
							modelId: data.flowId,
							id: this.dataForm.id,
							type: data.type,
							flowId: data.flowId,
							processId: data.id,
							opType: data.opType,
							taskId: data.taskId
						}
						const formData = data.draftData || data.formData || {}
						this.formData = {
							...formData,
							flowId: data.flowId
						}
					} else {
						this.isAdd = true;
					}
					uni.setStorageSync('dynamicModelExtra', {})
					this.fillFormData(this.formConf, this.formData)
					this.$nextTick(() => {
						this.loading = false
					})
					this.dataForm.flowId = data.flowId
					this.key = +new Date()
				})
			},
			handleMethod(dataForm) {
				const formMethod = dataForm.id ? updateModel : createModel
				formMethod(this.setting.flowId, dataForm).then(res => {
					if (res.data && Array.isArray(res.data) && res.data.length) {
						this.$refs.ErrorForm.init(res.data, this.eventType)
						return
					}
					uni.showToast({
						title: res.msg,
						complete: () => {
							setTimeout(() => {
								uni.$emit('refresh')
								uni.navigateBack()
							}, 1500)
						}
					})
				}).catch(() => {})
			},
			submitErrorForm(data) {
				if (data) this.dataForm = {
					...data,
					...this.dataForm
				}
				this.handleMethod(this.dataForm)
			},
			fillFormData(form, data) {
				form.disabled = this.setting.readonly
				const loop = (list, parent) => {
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
									if (item.__config__.projectKey === 'sign' && this.userInfo.signImg) {
										item.__config__.defaultValue = this.userInfo.signImg
									}
								}
							}

							let noShow = item.__config__.noShow || false,
								isDisabled = item.disabled || false,
								required = item.__config__.required || false,
								isVisibility = false
							if (!item.__config__.visibility || (Array.isArray(item.__config__.visibility) && item
									.__config__.visibility.includes('app'))) isVisibility = true
							if (this.setting.formOperates && this.setting.formOperates.length) {
								let id = item.__config__.isSubTable ? parent.__vModel__ + '-' + item.__vModel__ :
									item
									.__vModel__
								let arr = this.setting.formOperates.filter(o => o.id === id) || []
								if (arr.length) {
									let obj = arr[0]
									noShow = !obj.read
									isDisabled = !obj.write
									required = obj.required ? obj.required : item.__config__.required
								}
							}
							isDisabled = item.readonly ? item.readonly : isDisabled
							if (this.setting.readonly) isDisabled = true
							if (this.setting.origin === 'scan') isDisabled = true
							this.$set(item, 'disabled', isDisabled)
							this.$set(item.__config__, 'noShow', noShow)
							this.$set(item.__config__, 'required', required)
							this.$set(item.__config__, 'isVisibility', isVisibility)
						} else {
							let noShow = item.__config__.noShow ? item.__config__.noShow : false,
								isVisibility = false
							if (!item.__config__.visibility || (Array.isArray(item.__config__.visibility) && item
									.__config__.visibility.includes('app'))) isVisibility = true
							this.$set(item.__config__, 'isVisibility', isVisibility)
							this.$set(item.__config__, 'noShow', noShow)
						}
						if (item.__config__ && item.__config__.children && Array.isArray(item.__config__.children)) {
							loop(item.__config__.children, item)
						}
					}
				}
				loop(form.fields)
				form.formData = data
			},
			sumbitForm(data, callback) {
				if (!data) return
				const formData = {
					...this.formData,
					...data
				}
				this.dataForm.formData = formData
				if (callback && typeof callback === "function") callback()
				this.$emit('eventReceiver', this.dataForm, this.eventType)
			},
			operate() {
				let config = {
					formType: this.setting.formType,
					eventType: this.eventType,
					title: this.title,
					btnTxt: '确认' + this.title,
					hasSign: this.properties.hasSign,
					hasFreeApprover: this.properties.hasFreeApprover,
					isCustomCopy: this.properties.isCustomCopy,
					formData: this.dataForm,
					taskId: 0,
					flowId: this.setting.flowId,
					hasOpinion: this.properties.hasOpinion,
					candidateType: this.candidateType,
					branchList: this.branchList,
					candidateList: this.candidateList,
					isCandidate: this.candidateType == 2 ? true : false,
					props: {
						label: 'nodeName',
						value: 'nodeId'
					},
					type: 1
				}
				uni.navigateTo({
					url: '/pages/workFlow/operate/index?config=' + encodeURIComponent(JSON.stringify(config))
				})
			},
			getCandidates(id, formData) {},
			selfSubmit() {
				this.dataForm.status = this.eventType === 'submit' ? 0 : 1
				this.dataForm.flowId = this.setting.flowId
				this.dataForm.flowUrgent = this.flowUrgent || 1
				if (this.eventType === 'save') return this.selfHandleRequest()
				Candidates(0, {
					formData: this.dataForm
				}).then(res => {
					let data = res.data
					this.branchList = []
					this.candidateType = data.type
					this.branchList = res.data.list
					if (data.type == 1) {
						this.candidateList = res.data.list.filter(o => o.isCandidates)
						this.$nextTick(() => {
							this.operate()
						})
					} else if (data.type == 2) {
						this.candidateList = res.data.list.filter(o => o.isCandidates)
						this.$nextTick(() => {
							this.operate()
						})
					} else {
						uni.showModal({
							title: '提示',
							content: '您确定要提交当前流程吗？',
							success: res => {
								if (res.confirm) {
									this.selfHandleRequest()
								}
							}
						})
					}
				}).catch(() => {})
			},
			selfHandleRequest(candidateList) {
				if (candidateList) this.dataForm = {
					...this.dataForm,
					...candidateList
				}
				this.dataForm.candidateType = this.candidateType
				if (!this.dataForm.id) delete(this.dataForm.id)
				this.handleMethod(this.dataForm)
			},
			submit(eventType, flowUrgent) {
				if (this.setting.isPreview == '1') {
					uni.showToast({
						title: '功能预览不支持数据保存',
						icon: 'none'
					})
					return
				}
				this.eventType = eventType
				this.flowUrgent = flowUrgent
				this.$refs.dynamicForm && this.$refs.dynamicForm.submitForm()
			},
		}
	}
</script>