<template>
	<view class="linzen-wrap personalData">
		<u-toast ref="uToast" />
		<view class="u-p-l-20 u-p-r-20" style="background-color: #fff;">
			<u-form :model="dataForm" :errorType="['toast']" label-width="180" label-align="left" ref="dataForm">
				<u-form-item label="受委托人" prop='toUserId' required>
					<linzen-user-select v-model="dataForm.toUserId" @change="toChangeUser" :disabled="disabled">
					</linzen-user-select>
				</u-form-item>
				<u-form-item label="委托类型" prop='type' required>
					<linzen-select v-model="dataForm.type" placeholder="请选择" :options='typeOptions' :props='props'
						:disabled="disabled">
					</linzen-select>
				</u-form-item>
				<u-form-item label="委托流程">
					<flow-select v-model="dataForm.flowId" placeholder="全部流程" multiple @change="onChange"
						:disabled="disabled">
					</flow-select>
				</u-form-item>
				<u-form-item label="开始时间" prop='startTime' required>
					<linzen-date-time type="date" v-model="dataForm.startTime" placeholder="请选择" :disabled="disabled"
						format="yyyy-MM-dd HH:mm:ss">
					</linzen-date-time>
				</u-form-item>
				<u-form-item label="结束时间" prop='endTime' required>
					<linzen-date-time type="date" v-model="dataForm.endTime" placeholder="请选择" @change="change"
						:disabled="disabled" format="yyyy-MM-dd HH:mm:ss">
					</linzen-date-time>
				</u-form-item>
				<u-form-item label="委托说明">
					<u-input input-align='right' v-model="dataForm.description" type="textarea" placeholder="请输入"
						:disabled="disabled" />
				</u-form-item>
			</u-form>
		</view>
		<view class="flowBefore-actions">
			<template>
				<u-button class="buttom-btn" @click="showctionSheet = true" v-if="showBtn">更多
					<u-icon name="arrow-down" size="24">
					</u-icon>
				</u-button>
				<u-button class="buttom-btn" type="primary" @click.stop="getResult('confirm')" v-if="current != 2">
					{{'确定'}}
				</u-button>
				<u-button class="buttom-btn" @click="getResult('cancel')">
					{{'取消'}}
				</u-button>
			</template>
		</view>
		<u-action-sheet v-model="showctionSheet" :list="actionList" @click="handleAction"
			:tips="{ text: '' , color: '#000' , fontSize: 30 }">
		</u-action-sheet>
	</view>
</template>

<script>
	import {
		UpdateUser
	} from '@/api/common'
	import {
		Create,
		getListByAuthorize,
		Update,
		FlowDelegateInfo,
		entrustStop
	} from '@/api/workFlow/entrust.js'
	import {
		FlowEngineListAll
	} from '@/api/workFlow/flowEngine.js'
	import flowSelect from './flow-select/index.vue'
	export default {
		components: {
			flowSelect
		},
		data() {
			const data = {
				showBtn: false,
				showctionSheet: false,
				show: false,
				avatar: 'https://cdn.uviewui.com/uview/common/logo.png',
				props: {
					label: 'fullName',
					value: 'enCode'
				},
				dataForm: {
					id: '',
					userId: '',
					toUserId: '',
					flowId: [],
					description: '',
					startTime: '',
					endTime: '',
					flowName: '',
					toUserName: '',
					type: '',
				},
				typeOptions: [{
					enCode: "0",
					fullName: '发起委托'
				}, {
					enCode: "1",
					fullName: '审批委托'
				}],
				userInfo: {},
				rules: {
					userId: [{
						required: true,
						message: '委托人不能为空',
						trigger: ['change', 'blur'],
					}],
					toUserId: [{
						required: true,
						message: '受委托人不能为空',
						trigger: ['change', 'blur'],
					}],
					type: [{
						required: true,
						message: '委托类型不能为空',
						trigger: ['change', 'blur'],
					}],
					endTime: [{
						required: true,
						message: '结束时间不能为空',
						trigger: 'blur',
						type: 'number'
					}],
					startTime: [{
						required: true,
						message: '开始时间不能为空',
						trigger: 'blur',
						type: 'number'
					}]
				},
				isGradeUser: 2,
				myNameAccount: '',
				actionList: [],
				current: '1',
				disabled: false
			}
			this.getOptions()
			return data
		},
		computed: {
			baseURL() {
				return this.define.baseURL
			},
		},
		onLoad(option) {
			this.userInfo = uni.getStorageSync('userInfo') || {}
			if (option) {
				this.current = option.current
				this.disabled = this.current == 2 ? true : false
				this.dataForm.id = option.id || ''
				this.showBtn = option.current == 1 ? true : false
				if (this.showBtn && option.status == 1 || option.status == 0) {
					this.actionList.push({
						text: '结束委托',
						fontSize: 30,
					})
				}
			}
			uni.setNavigationBarTitle({
				title: this.dataForm.id ? '编辑' : '新建'
			})
			this.myNameAccount = this.userInfo.userName + '/' + this.userInfo.userAccount
			this.dataForm.userId = this.userInfo.userId
			this.dataForm.userName = this.myNameAccount
			FlowEngineListAll().then((res) => {
				this.flowEngineList = res.data.list
				//初始化数据
				if (this.dataForm.id) {
					FlowDelegateInfo(this.dataForm.id).then(res => {
						this.dataForm = res.data
						this.dataForm.flowId = this.dataForm.flowId ? this.dataForm.flowId.split(",") : []
						this.myNameAccount = this.dataForm.userName
					})
				}
			})
		},
		mounted() {
			this.$refs.dataForm.setRules(this.rules);
		},
		methods: {
			handleAction(index) {
				let currTime = Math.round(new Date())
				uni.showModal({
					title: '提示',
					content: '结束后,流程不再进行委托!',
					success: (res) => {
						if (res.confirm) {
							entrustStop(this.dataForm.id).then(res => {
								this.dataForm.endTime = currTime
								uni.$emit('refresh')
								uni.navigateBack()
							})
						}
					}
				})
			},
			onChange(id, listData) {
				if (listData && listData.length) {
					let arr = []
					listData.forEach(item => {
						arr.push(item.fullName)
					})
					this.dataForm.flowName = arr.join(",")
				} else {
					this.dataForm.flowName = "全部流程"
				}
			},
			change(val, list) {
				this.$nextTick(() => {
					this.$emit('change', this.dataForm)
				})
			},
			toChangeUser(id, selectedData) {
				return this.dataForm.toUserName = selectedData.fullName
			},
			onChangeUser(id, selectedData) {
				this.dataForm.userName = selectedData.fullName
			},
			getOptions() {
				this.show = true
			},
			// 点击确定或者取消
			getResult(event = null) {
				// #ifdef MP-WEIXIN
				if (this.moving) return;
				// #endif
				this.keyword = '';
				if (event === 'cancel') return this.close();
				this.submit()
			},
			close() {
				uni.navigateBack();
			},
			submit() {
				let startTime = this.dataForm.startTime;
				let endTime = this.dataForm.endTime;
				this.$refs.dataForm.validate(valid => {
					if (valid) {
						if (startTime > endTime) {
							this.$refs.uToast.show({
								title: '开始时间不能大于等于结束时间',
								type: 'error'
							})
							this.dataForm.startTime = '';
							this.dataForm.endTime = '';
							return
						}
						const formMethod = this.dataForm.id ? Update : Create
						let params = {
							...this.dataForm
						}
						params.flowId = this.dataForm.flowId ? this.dataForm.flowId.join(",") : ""
						if (this.isGradeUser == 2) {
							params.userId = this.userInfo.userId
							params.userName = this.myNameAccount
						}
						if (!params.flowId) {
							params.flowName = "全部流程"
						}
						formMethod(params).then(res => {
							uni.showToast({
								title: res.msg,
								complete: () => {
									setTimeout(() => {
										uni.$emit('refresh')
										uni.navigateBack()
									}, 1500)
								}
							});
						}).catch()
					}
				});
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	/deep/.u-form-item {
		min-height: 112rpx;
	}

	.u-form {
		padding: 0;
	}

	.slot-btn {
		width: 329rpx;
		height: 140rpx;
		display: flex;
		justify-content: center;
		align-items: center;
		background: rgb(244, 245, 246);
		border-radius: 10rpx;
	}

	.slot-btn__hover {
		background-color: rgb(235, 236, 238);
	}
</style>