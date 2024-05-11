<template>
	<view class="flowBefore-v">
		<div class="flow-urgent-value" :style="{'background-color':flowUrgentList[selectflowUrgent.extra].bgColor}"
			@click="handleShowSelect">
			<span :style="{'color':flowUrgentList[selectflowUrgent.extra].color}">{{selectflowUrgent.label}}</span>
		</div>
		<view class="flowBefore-box" v-if="config.opType ==='-1'">
			<view class="scroll-v" scroll-y>
				<childForm ref="child" :config="config" @eventReceiver="eventReceiver" v-if="!loading" />
				<ErrorForm v-model="showErrorForm" @submitErrorForm="submitErrorForm" ref="ErrorForm" />
			</view>
		</view>
		<view class="flowBefore-box" v-else>
			<view class="u-border-bottom sticky-box">
				<u-sticky>
					<u-tabs :list="tabBars" :is-scroll="true" :current="tabIndex" @change="tabChange" height="100">
					</u-tabs>
				</u-sticky>
			</view>
			<view>
				<view v-show="tabsName === '表单信息'">
					<view>
						<view class="flowStatus" v-if="config.opType==0 || config.opType==4">
							<image :src="flowStatus" mode="widthFix"></image>
						</view>
					</view>
					<childForm ref="child" :config="config" @eventReceiver="eventReceiver" @setBtnLoad="setBtnLoad"
						v-if="!loading" />
				</view>
				<view class="nodeList-v" v-if="tabsName === '流程信息'">
					<flowInfo :options="flowTemplateJson" :config="config"></flowInfo>
				</view>
				<view v-if="tabsName === '流转记录'">
					<records :options="recordList" :endTime="endTime" :flowId="config.flowId" />
				</view>
				<view v-if="tabsName === '审批汇总'">
					<RecordSummary :processId='processId' ref="recordSummary" :summaryType="summaryType">
					</RecordSummary>
				</view>
				<view v-if="tabsName === '流程评论'">
					<view class="record-v">
						<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback"
							:sticky="false" :down="downOption" :up="upOption" :bottombar="false">
							<view class="discuss_box">
								<scroll-view scroll-y="true" style="height: 100%;">
									<view class="u-flex-col discuss_list" v-for="(item,index) in commentList"
										:key="index">
										<view class="u-flex discuss_txt">
											<view class="discuss_txt_left u-flex">
												<u-avatar :src="baseURL+item.creatorUserHeadIcon"></u-avatar>
												<text class="uName">{{item.creatorUser}}</text>
											</view>
											<text v-if="item.isDel" class="del"
												@click.stop="delComment(item.id,index)">删除</text>
										</view>
										<view class="u-flex-col discuss_content">
											<text class="txt">{{item.text}}</text>
											<linzen-upload v-model="item.image" disabled align='left' detailed />
											<view v-for='(file,f) in item.file' :key="f"
												class="linzen-file-item u-type-primary u-flex u-line-1"
												@click="openFile(file)">
												<view class="u-line-1" style="margin-bottom: 10rpx;">
													{{file.name}}
												</view>
											</view>
										</view>
										<view style="padding-left: 110rpx;margin-top: 20rpx;">
											<text>{{$u.timeFormat(item.creatorTime,'yyyy-mm-dd hh:MM:ss')}}</text>
										</view>
									</view>
								</scroll-view>
							</view>
						</mescroll-body>
					</view>
					<view class="discuss_btn" v-if="tabsName === '流程评论'">
						<button @click.stop="jumpComment" class="custom-style">评论</button>
					</view>
				</view>
				<ErrorForm v-model="showErrorForm" @submitErrorForm="submitErrorForm" ref="ErrorForm" />
			</view>
		</view>

		<view class="flowBefore-actions" v-if="tabIndex != 4 && tabIndex != 3">
			<u-button class="buttom-btn" :loading="btnLoading" @click="showAction = true"
				v-if="actionListLength && !pureForm">
				更多<u-icon name="arrow-down" size="24"></u-icon>
			</u-button>
			<template
				v-if="(config.type === 1 && (config.jurisdictionType === 'btn_edit' || config.jurisdictionType === 'btn_add') && config.opType=='-1')|| (config.type !== 1 && config.opType=='-1')">
				<u-button class="buttom-btn" type="primary" @click.stop="eventLauncher('submit')">
					{{properties.submitBtnText||'提交'}}
				</u-button>
			</template>
			<template v-if="config.opType == 0">
				<u-button class="buttom-btn" type="primary" @click.stop="handlePress()"
					v-if="config.status == 1&&(properties.hasPressBtn || properties.hasPressBtn===undefined)">
					{{properties.pressBtnText||'催办'}}
				</u-button>
				<u-button v-else-if="config.status == 3" class="buttom-btn" type="primary"
					@click.stop="eventLauncher('submit')">
					{{properties.submitBtnText||'提交'}}
				</u-button>
			</template>
			<u-button class="buttom-btn" type="primary" v-if="config.opType == 1&&properties.hasAuditBtn"
				@click.stop="eventLauncher('audit')">
				{{properties.auditBtnText||'通过'}}
			</u-button>
			<u-button class="buttom-btn" type="error" @click.stop="eventReceiver({},'recall')"
				v-if="config.opType == 2 && properties.hasRevokeBtn">
				{{properties.revokeBtnText||'撤回'}}
			</u-button>
			<u-button class="buttom-btn" @click.stop="linzen.goBack()">取消</u-button>
		</view>
		<u-select :list="flowUrgentList" v-model="showFlowUrgent" @confirm="seltConfirm"
			:default-value="defaultValue" />
		<u-action-sheet @click="handleAction" :list="actionList" :tips="{ text: '' , color: '#000' , fontSize: 30 }"
			v-model="showAction">
		</u-action-sheet>
	</view>
</template>

<script>
	import {
		Create,
		Update
	} from '@/api/workFlow/workFlowForm'
	import {
		FlowBeforeInfo,
		Audit,
		Reject,
		Transfer,
		Recall,
		Cancel,
		SaveAudit,
		Candidates,
		RejectList,
		FreeApprover
	} from '@/api/workFlow/flowBefore'
	import {
		Revoke,
		Press
	} from '@/api/workFlow/flowLaunch'
	import {
		createModel,
		updateModel
	} from '@/api/apply/visualDev'
	import {
		getDownloadUrl
	} from '@/api/common'
	import {
		getCommentList,
		createComment,
		delComment,
		FlowEngineInfo
	} from '@/api/workFlow/flowEngine'
	import {
		checkInfo
	} from '@/api/message.js'
	import resources from '@/libs/resources.js'
	import childForm from './form.vue'
	import flowInfo from '../components/flowInfo'
	import records from '../components/records.vue'
	import CandidateForm from '../components/CandidateForm'
	import ErrorForm from '../components/ErrorForm'
	import RecordSummary from '../components/RecordSummary'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	export default {
		mixins: [MescrollMixin],
		components: {
			CandidateForm,
			childForm,
			flowInfo,
			records,
			RecordSummary,
			ErrorForm
		},
		data() {
			return {
				actionListLength: false,
				downOption: {
					use: true,
					auto: true
				},
				upOption: {
					page: {
						num: 0,
						size: 20,
						time: null
					},
					empty: {
						use: true,
						icon: resources.message.nodata,
						tip: "暂无数据",
						fixed: true,
						top: "300rpx",
					},
					textNoMore: '没有更多数据',
				},
				isComment: false,
				isSummary: false,
				show: false,
				config: {},
				currentView: '',
				formData: {},
				flowTaskInfo: {},
				flowFormInfo: {},
				flowTemplateInfo: {},
				flowTaskNodeList: [],
				flowTemplateJson: [],
				recordList: [],
				properties: {},
				endTime: 0,
				tabIndex: 1,
				tabBars: [{
						name: '表单信息',
						id: 0
					},
					{
						name: '流程信息',
						id: 1
					},
					{
						name: '流转记录',
						id: 2
					}
				],
				flowStatus: '',
				stepIndex: 0,
				userOptions: [],
				btnLoading: false,
				eventType: '',
				commentList: [],
				processId: "",
				candidateList: [],
				summaryType: 0,
				tabsName: '表单信息',
				title: '',
				branchList: [],
				candidateType: 3,
				countersignOver: false,
				selectflowUrgent: {
					extra: '0',
					label: '普通',
					value: 1,
				},
				showFlowUrgent: false,
				defaultValue: [0],
				flowUrgent: 1,
				flowUrgentList: [{
						label: '普通',
						color: '#409EFF',
						bgColor: '#e5f3fe',
						value: 1,
						extra: '0'
					},
					{
						label: '重要',
						color: '#E6A23C',
						bgColor: '#fef6e5',
						value: 2,
						extra: '1'
					},
					{
						label: '紧急',
						color: '#F56C6C',
						bgColor: '#fee5e5',
						value: 3,
						extra: '2'
					},
				],
				showErrorForm: false,
				showAction: false,
				actionList: [],
				rejectList: [],
				rejectStep: '',
				pureForm: false,
				key: +new Date()
			};
		},
		computed: {
			baseURL() {
				return this.define.baseURL
			},
		},
		watch: {
			isSummary(val) {
				if (val) return this.tabBars.push({
					name: '审批汇总',
					id: 3
				})
			},
			isComment(val) {
				if (val) return this.tabBars.push({
					name: '流程评论',
					id: 4
				})
			},
			actionList(val) {
				if (val.length) this.actionListLength = true
			}
		},
		onLoad(option) {
			const config = JSON.parse(this.base64.decode(option.config))
			this.config = config
			this.pureForm = this.config.pureForm ? this.config.pureForm : false
			if (option.token) {
				this.$store.commit('user/SET_TOKEN', '')
				this.$store.commit('user/SET_TOKEN', option.token)
				uni.setStorageSync('token', option.token)
				let dataConfig = {
					id: this.config.processId,
					enCode: this.config.enCode,
					flowId: this.config.flowId,
					formType: this.config.formType,
					opType: this.config.type == 1 ? 0 : this.config.type == 2 ? 1 : this.config.type,
					taskNodeId: this.config.taskNodeId,
					taskId: this.config.taskOperatorId,
					hideCancelBtn: true,
					fullName: '',
					status: this.config.status
				}
				if (this.config.type == 2) {
					checkInfo(this.config.taskOperatorId).then(res => {
						if (res.data && res.data.isCheck) dataConfig.opType = 3
					}).catch((err) => {})
				}
				this.config = dataConfig
			}
			if (!config) return this.linzen.goBack()
			this.init()
			uni.$on('operate', (data) => {
				this[data.eventType + 'Handle'](data)
			})
			this.processId = this.config.id
			if (this.config.opType !== '-1') {
				setTimeout(() => {
					this.tabIndex = 0
				}, 0)
			}
		},
		onShow() {
			uni.$off('comment')
			uni.$on('comment', data => {
				this.commentList = [];
				this.current = 0;
				this.addComment(data)
			})
		},
		onUnload() {
			uni.$off('operate')
			uni.$off('refresh')
			uni.$off('comment')
		},
		methods: {
			handleCodeGeneration(config) {
				this.config = config
				this.init(this.config)
				uni.$on('operate', (data) => {
					this[data.eventType + 'Handle'](data)
				})
				uni.$on('comment', data => {
					this.commentList = [];
					this.current = 0;
					this.addComment(data)
				})
				if (this.config.opType !== '-1') {
					setTimeout(() => {
						this.tabIndex = 0
					}, 0)
				}
				this.processId = this.config.id
				uni.setNavigationBarTitle({
					title: this.config.fullName
				})
				this.pureForm = true
				this.key = +new Date()
			},

			// 流程评论
			doPreviewImage(url) {
				let images;
				this.commentList.forEach(o => {
					if (o.image.length > 0) {
						images = o.image.map(item => this.baseURL + item.url)
					}
				})
				uni.previewImage({
					urls: images,
					current: url,
					success: () => {},
					fail: () => {
						uni.showToast({
							title: '预览图片失败',
							icon: 'none'
						});
					}
				});
			},
			openFile(item) {
				// #ifdef MP
				this.previewFile(item)
				// #endif
				// #ifndef MP
				getDownloadUrl('annex', item.fileId).then(res => {
					// #ifdef H5
					window.location.href = this.baseURL + res.data.url + '&name=' + item.name;
					// #endif
					// #ifndef H5
					uni.downloadFile({
						url: this.baseURL + res.data.url + '&name=' + item.name,
						success: function(res) {
							var filePath = res.tempFilePath;
							uni.openDocument({
								filePath: encodeURI(filePath),
								showMenu: true,
								success: function(res) {

								}
							});
						}
					});
					// #endif
				})
				// #endif
			},
			previewFile(item) {
				let url = item.url
				uni.downloadFile({
					url: this.baseURL + url,
					success: (res) => {
						var filePath = res.tempFilePath;
						uni.openDocument({
							filePath: encodeURI(filePath),
							success: (res) => {}
						});
					}
				});
			},
			// 流程评论end

			// 轮播菜单
			swiperChange(e) {
				this.tabIndex = e.detail.current;
				this.tabsName = this.tabBars[this.tabIndex].name;
			},
			upCallback(page) {
				let query = {
					currentPage: page.num,
					pageSize: page.size,
					sort: 'desc',
					sidx: '',
					taskId: this.processId
				}
				getCommentList(query, {
					load: page.num == 1
				}).then(res => {
					this.mescroll.endSuccess(res.data.list.length);
					if (page.num == 1) this.commentList = [];
					const list = res.data.list.map((o) => {
						o.image = JSON.parse(o.image)
						o.file = JSON.parse(o.file)
						return o
					})
					this.commentList = this.commentList.concat(list);
				}).catch(() => {
					this.mescroll.endErr();
				})

			},
			tabChange(index) {
				this.tabIndex = index;
				this.tabsName = this.tabBars[index].name;
			},
			jumpComment() {
				uni.navigateTo({
					url: '/pages/workFlow/comment/index'
				})
			},
			addComment(query) {
				query.taskId = this.processId
				createComment(query).then(res => {
					this.mescroll.resetUpScroll()
				})
			},
			delComment(id, i) {
				uni.showModal({
					title: '提示',
					content: '确定删除?',
					success: (res) => {
						if (res.confirm) {
							delComment(id).then(res => {
								this.commentList.splice(i, 1)
								this.mescroll.resetUpScroll()
							})
						}
					}
				})
			},
			init() {
				this.tabIndex = 1
				if (this.config.formRecords) this.tabIndex = 4
				/**
				 * opType
				 * -1 - 我发起的新建/编辑 
				 * 0 - 我发起的详情
				 * 1 - 待办事宜
				 * 2 - 已办事宜
				 * 3 - 抄送事宜
				 */
				this.getBeforeInfo(this.config)
			},
			getBeforeInfo(data) {
				this.formData.flowId = data.flowId;
				this.loading = true
				FlowBeforeInfo(data.id || 0, {
					taskNodeId: data.taskNodeId,
					taskOperatorId: data.taskId,
					flowId: data.flowId
				}).then(res => {
					data.formData = res.data.formData || {}
					this.flowTaskInfo = res.data.flowTaskInfo || {};
					this.flowFormInfo = res.data.flowFormInfo || {};
					data.formType = this.flowFormInfo.formType
					this.config.formType = this.flowFormInfo.formType
					this.config.formEnCode = this.flowFormInfo.enCode
					this.loading = false
					this.flowTemplateInfo = res.data.flowTemplateInfo || {}
					const flowTemplateJson = this.flowTemplateInfo.flowTemplateJson ? JSON.parse(this
						.flowTemplateInfo.flowTemplateJson) : null;
					data.flowTemplateJson = flowTemplateJson;
					this.flowTemplateJson = flowTemplateJson;
					this.isComment = this.flowTemplateJson.properties.isComment;
					this.isSummary = this.flowTemplateJson.properties.isSummary;
					this.summaryType = this.flowTemplateJson.properties.summaryType;
					this.flowTaskNodeList = res.data.flowTaskNodeList || [];
					const recordList = res.data.flowTaskOperatorRecordList || [];
					this.recordList = recordList.reverse();
					for (let i = 0; i < this.recordList.length; i++) {
						const item = this.recordList[i]
						item.formType = this.flowFormInfo.formType;
						item.enCode = this.flowFormInfo.enCode;
						if (item.fileList) item.fileList = JSON.parse(item.fileList)
					}
					this.properties = res.data.approversProperties || {};
					data.draftData = res.data.draftData || null;
					data.formConf = this.flowFormInfo.propertyJson;
					data.type = this.flowTaskInfo.type;
					this.endTime = this.flowTaskInfo.completion == 100 ? this.flowTaskInfo.endTime : 0;
					this.config.status = this.flowTaskInfo.status;
					this.config.fullName = data.opType == '-1' ? this.flowTemplateInfo.fullName : this.flowTaskInfo
						.fullName
					this.title = this.flowTaskInfo.fullName
					if (this.config.status !== 0 && this.config.status !== 3) {
						this.title = this.flowTaskInfo.thisStep ? this.config.fullName + '/' + this.flowTaskInfo
							.thisStep : this.config.fullName
					}
					uni.setNavigationBarTitle({
						title: this.config.fullName
					})
					if (this.config.formRecords && this.config.title) {
						uni.setNavigationBarTitle({
							title: this.config.title
						})
					}
					this.flowUrgent = this.flowTaskInfo.flowUrgent || 1
					const getSelectInfo = () => {
						var obj = {
							value: this.flowUrgent,
							extra: '0',
							label: '普通'
						}
						this.flowUrgentList.forEach((e, i) => {
							if (e.value == this.flowUrgent) {
								obj.extra = i
								obj.label = e.label
							}
						})
						return obj
					}
					this.selectflowUrgent = getSelectInfo()
					this.handleMoreBtnList()
					if (this.flowTaskNodeList.length) {
						for (let i = 0; i < this.flowTaskNodeList.length; i++) {
							const nodeItem = this.flowTaskNodeList[i]
							const loop = data => {
								if (Array.isArray(data)) data.forEach(d => loop(d))
								if (data.nodeId === nodeItem.nodeCode) {
									data.id = nodeItem.id
									if (nodeItem.type == 0) data.state = 'state-past'
									if (nodeItem.type == 1) data.state = 'state-curr'
									if (nodeItem.nodeType === 'approver' || nodeItem.nodeType === 'start' ||
										nodeItem.nodeType === 'subFlow') data.content = nodeItem.userName
									return
								}
								if (data.conditionNodes && Array.isArray(data.conditionNodes)) loop(data
									.conditionNodes)
								if (data.childNode) loop(data.childNode)
							}
							loop(flowTemplateJson)
						}
						this.flowTemplateJson = flowTemplateJson
					}
					if (data.opType != 1 && data.opType != '-1') data.readonly = true
					data.formOperates = []
					if (data.opType == 0) {
						if (this.flowTemplateJson && this.flowTemplateJson.properties && this.flowTemplateJson
							.properties
							.formOperates) {
							data.formOperates = this.flowTemplateJson.properties.formOperates || []
						}
					} else {
						data.formOperates = res.data.formOperates || []
					}
					switch (this.config.status) {
						case 0: //等待提交
							this.flowStatus = resources.status.submit
							break;
						case 1: //等待审核
							this.flowStatus = resources.status.review
							break;
						case 2: //审核通过
							this.flowStatus = resources.status.reviewAdopt
							break;
						case 3: //审核退回
							this.flowStatus = resources.status.reviewRefuse
							break;
						case 4: //流程撤回
							this.flowStatus = resources.status.reviewUndo
							break;
						case 5: //审核终止
							this.flowStatus = resources.status.reviewStop
							break;
						default: //等待审核
							this.flowStatus = resources.status.review
							break;
					}
					setTimeout(() => {
						this.$nextTick(() => {
							if (!this.$refs.child || !this.$refs.child.$refs.form || !this.$refs
								.child.$refs.form.init) {
								uni.showToast({
									title: '暂无此流程表单',
									icon: 'none',
									complete: () => {
										setTimeout(() => {
											uni.navigateBack()
										}, 1500)
									}
								})
								return
							}
							this.$refs.child.$refs.form.init(data)
						})
					}, 100)
				})
			},
			handleAction(index) {
				switch (this.actionList[index].id) {
					case 'save':
						this.eventLauncher('save')
						break;
					case 'transfer':
						this.eventReceiver({}, 'transfer')
						break;
					case 'reject':
						this.eventReceiver({}, 'reject')
						break;
					case 'saveAudit':
						this.eventLauncher('saveAudit')
						break;
					case 'revoke':
						this.eventReceiver({}, 'revoke')
						break;
					case 'freeapprover':
						this.eventLauncher('freeapprover')
					case 'approvalCancel':
						this.eventReceiver({}, 'approvalCancel')
					default:
						break;
				}
			},
			handleMoreBtnList() {
				const config = this.config
				const type = config.type
				const opType = config.opType
				const properties = this.properties
				if ((type === 1 && (config.jurisdictionType === 'btn_edit' || config.jurisdictionType ===
						'btn_add') && config.opType == '-1') || ((type !== 1 && opType == '-1'))) {
					this.actionList.push({
						text: properties.saveBtnText || '暂存',
						id: 'save'
					})
				} else if (opType == 0) {
					if (config.status == 1 && (properties.hasRevokeBtn || properties.hasRevokeBtn === undefined)) {
						this.actionList.push({
							text: properties.revokeBtnText || '撤回',
							id: 'revoke'
						})
					} else if (config.status == 3) {
						this.actionList.push({
							text: properties.saveBtnText || '暂存',
							id: 'save'
						})
					}
				} else if (opType == 1) {
					if (properties.hasTransferBtn) {
						this.actionList.push({
							text: properties.transferBtnText || '转审',
							id: 'transfer'
						})
					}
					if (properties.hasRejectBtn) {
						this.actionList.push({
							text: properties.rejectBtnText || '退回',
							id: 'reject'
						})
					}
					if (properties.hasSaveBtn) {
						this.actionList.push({
							text: properties.saveBtnText || '暂存',
							id: 'saveAudit'
						})
					}
					if (properties.hasFreeApproverBtn) {
						this.actionList.push({
							text: properties.hasFreeApproverBtnText || '加签',
							id: 'freeapprover'
						})
					}
					if (properties.hasCancelBtn) {
						this.actionList.push({
							text: properties.cancelBtnText || '驳回',
							id: 'approvalCancel'
						})
					}
				}
			},
			eventLauncher(eventType) {
				this.$refs.child && this.$refs.child.$refs.form && this.$refs.child
					.$refs.form.submit(eventType, this.selectflowUrgent.value)
			},
			eventReceiver(formData, eventType) {
				this.formData = {
					...formData
				};
				this.eventType = eventType
				this.formData.flowUrgent = this.selectflowUrgent.value || 1
				if (eventType === 'save' || eventType === 'submit') {
					return this.submitOrSave(eventType)
				}
				if (eventType === 'saveAudit') {
					return this.saveAudit()
				}
				if (eventType === 'audit') {
					this.getCandidates(this.config.taskId, this.formData)
				}
				if (eventType === 'reject' || eventType === 'revoke' || eventType === 'recall') {
					let txt = eventType === 'reject' ? '退回' : '撤回'
					let data = {
						formData: this.formData,
						eventType: this.eventType
					}
					if (eventType === 'recall') return this.operate('recall', this.properties.revokeBtnText)
					if (eventType === 'reject') {
						RejectList(this.config.taskId).then(res => {
							this.rejectList = res.data || []
							this.operate('reject', this.properties.rejectBtnText)
						}).catch({})
						return
					}
					if (eventType === 'revoke') return this.operate('revoke', this.properties.revokeBtnText)
					if (!this.properties.hasOpinion && !this.properties.hasSign) return uni.showModal({
						title: '提示',
						content: `此操作将${txt}该审批单,是否继续?`,
						success: res => {
							if (res.confirm) {
								if (eventType === 'recall') return this.recallHandle(data)
								if (eventType === 'reject') return this.rejectHandle(data)
								if (eventType === 'revoke') return this.revokeHandle(data)
							}
						}
					})
				}
				if (eventType === 'transfer') {
					return this.operate('transfer', this.properties.transferBtnText)
				}
				if (eventType === 'freeapprover') {
					this.getCandidates(this.config.taskId, this.formData)
				}
				if (eventType === 'approvalCancel') {
					let data = {
						formData: this.formData,
						eventType: this.eventType
					}
					if (!this.properties.hasOpinion && !this.properties.hasSign) {
						return uni.showModal({
							title: '提示',
							content: `此操作将审批驳回终止流程，是否继续?`,
							success: res => {
								if (res.confirm) {
									this.approvalCancelHandle(data)
								}
							}
						})
					}
					return this.operate('approvalCancel', this.properties.cancelBtnText || '驳回')
				}
			},
			/* 保存草稿 */
			saveAudit() {
				this.btnLoading = true
				let query = {
					...this.formData
				}
				SaveAudit(this.config.taskId, query).then(res => {
					uni.showToast({
						title: res.msg,
						icon: 'none',
						complete: () => {
							setTimeout(() => {
								this.btnLoading = false
								uni.navigateBack()
							}, 1500)
						}
					});
				}).catch(() => {
					this.btnLoading = false
				})
			},
			//异常处理
			submitErrorForm(data) {
				if (data.eventType === "submit") {
					this.handleRequest(data)
				} else {
					this.handleApproval(data)
				}
			},
			getCandidates(id, formData) {
				formData.flowId = this.config.flowId
				Candidates(id, {
					flowId: formData.flowId,
					flowUrgent: this.flowUrgent,
					...formData
				}).then(res => {
					const data = res.data || {}
					this.candidateType = data.type || 3
					this.countersignOver = !!data.countersignOver
					this.branchList = data.list || []
					if (this.eventType === 'save' || this.eventType === 'submit') {
						if (data.type == 1) {
							this.operate('submit', '提交审核')
						} else if (data.type == 2) {
							this.branchList = []
							this.candidateList = res.data.list.filter(o => o.isCandidates)
							this.operate('submit', '提交审核')
						} else {
							if (this.properties.isCustomCopy) {
								return this.operate('submit', '提交审核')
							}
							this.branchList = []
							uni.showModal({
								title: '提示',
								content: '您确定要提交当前流程吗?',
								success: res => {
									if (res.confirm) {
										this.handleRequest()
									}
								}
							})
						}
					} else {
						this.candidateList = res.data.list ? res.data.list.filter(o => o.isCandidates) : []
						if (!this.properties.hasOpinion && !this.properties.hasFreeApprover && !this.properties
							.hasSign && !this.properties.isCustomCopy && this.candidateType == 3) {
							let data = {
								formData: this.formData,
								eventType: this.eventType
							}
							uni.showModal({
								title: '提示',
								content: '此操作将通过该审批单,是否继续?',
								success: res => {
									if (res.confirm) {
										this.auditHandle(data)
									}
								}
							})
							return
						}
						if (this.eventType === 'freeapprover') return this.operate(this.eventType, this.properties
							.hasFreeApproverBtnText)
						this.operate(this.eventType, this.properties.auditBtnText)
					}
				}).catch(() => {

				})
			},
			submitOrSave(eventType) {
				this.formData.status = eventType === 'submit' ? 0 : 1
				if (eventType === 'save') return this.handleRequest()
				this.getCandidates(0, this.formData)
			},
			submitHandle(data) {
				this.handleRequest(data)
			},
			selfHandleRequest() {
				const formMethod = this.formData.id ? updateModel : createModel
				formMethod(this.config.flowId, this.formData).then(res => {
					uni.showToast({
						title: res.msg,
						icon: 'none',
						complete: () => {
							setTimeout(() => {
								uni.navigateBack()
							}, 1500)
						}
					})
				}).catch(() => {})
			},
			handleRequest(data) {
				this.formData = {
					...data,
					...this.formData,
					flowId: this.config.flowId,
					candidateType: this.candidateType,
					countersignOver: this.countersignOver,
					status: this.eventType === 'save' ? 1 : 0,
					delegateUserList: this.config.delegateUserList || [],
					id: this.config.id
				}
				if (this.eventType === 'save') this.btnLoading = true
				let formMethod = this.formData.id ? Update : Create
				// 流程
				formMethod(this.config.enCode, this.formData).then(res => {
					if (res.data && Array.isArray(res.data) && res.data.length) {
						this.$refs.ErrorForm.init(res.data, this.eventType)
						return
					}
					uni.showToast({
						title: res.msg,
						icon: 'none',
						complete: () => {
							setTimeout(() => {
								uni.$emit('refresh')
								this.btnLoading = false
								uni.navigateBack()
							}, 1500)
						}
					})
				}).catch(() => {
					this.btnLoading = false
				})
			},
			handlePress() {
				uni.showModal({
					title: '提示',
					content: '此操作将提示该节点尽快处理',
					success: res => {
						if (res.confirm) {
							Press(this.config.id).then(res => {
								this.$u.toast(res.msg)
							})
						}
					}
				})
			},
			operate(eventType, title) {
				let config = {
					eventType,
					title: title.replace(/\s+/g, ""),
					hasSign: this.properties.hasSign,
					hasFreeApprover: eventType === 'freeapprover' ? this.properties.hasFreeApproverBtn : false,
					isCustomCopy: this.properties.isCustomCopy,
					taskId: eventType === 'submit' ? 0 : this.config.taskId,
					formData: this.formData,
					hasOpinion: this.properties.hasOpinion,
					candidateType: this.candidateType,
					branchList: this.branchList,
					candidateList: this.candidateList,
					rejectStep: this.properties.rejectStep,
					rejectList: this.rejectList,
					rejectType: this.properties.rejectType,
					props: {
						label: 'nodeName',
						value: 'nodeId'
					}
				}
				uni.navigateTo({
					url: '/pages/workFlow/operate/index?config=' + encodeURIComponent(JSON.stringify(config))
				})
			},
			revokeHandle(data) {
				Revoke(this.config.id, {
					handleOpinion: data.handleOpinion,
					signImg: data.signImg,
					fileList: data.fileList
				}).then(res => {
					this.toastAndBack(res.msg, true)
				})
			},
			recallHandle(data) {
				Recall(this.config.taskId, {
					handleOpinion: data.handleOpinion,
					signImg: data.signImg,
					fileList: data.fileList
				}).then(res => {
					this.toastAndBack(res.msg, true)
				})
			},
			auditHandle(data) {
				this.handleApproval(data)
			},
			freeapproverHandle(data) {
				this.freeApprover(data)
			},
			transferHandle(data) {
				Transfer(this.config.taskId, data).then(res => {
					this.toastAndBack(res.msg, true)
				})
			},
			// 驳回审核
			approvalCancelHandle(data) {
				Cancel(this.config.id, {
					handleOpinion: data.handleOpinion,
					signImg: data.signImg,
					fileList: data.fileList,
					eventType: 'approvalCancel'
				}).then(res => {
					this.toastAndBack(res.msg, true)
				})
			},
			rejectHandle(data) {
				this.handleApproval(data)
			},
			freeApprover(data) {
				const query = {
					...data,
					...this.formData
				}
				FreeApprover(this.config.taskId, query).then(res => {
					this.toastAndBack(res.msg, true)
				})
			},
			handleApproval(data) {
				const query = {
					...data,
					countersignOver: this.countersignOver,
					...this.formData,
					enCode: this.config.enCode
				}
				const approvalMethod = data.eventType === 'audit' ? Audit : Reject
				if (this.config.taskId != undefined) {
					approvalMethod(this.config.taskId, query).then(res => {
						if (res.data && Array.isArray(res.data) && res.data.length) {
							this.$refs.ErrorForm.init(res.data, data.eventType, query)
							return
						}
						this.toastAndBack(res.msg, true)
					})
				}
			},
			setBtnLoad(val) {
				this.btnLoading = !!val
			},
			toastAndBack(title, refresh) {
				uni.showToast({
					title: title,
					icon: 'none',
					mask: true,
					complete: () => {
						setTimeout(() => {
							uni.$emit('refresh')
							uni.navigateBack()
						}, 1500)
					}
				})
			},
			handleShowSelect() {
				if (this.config.opType == '-1') this.showFlowUrgent = true
			},
			seltConfirm(e) {
				this.flowUrgent = e[0].value
				this.selectflowUrgent = e[0]
				this.defaultValue = [this.flowUrgentList.findIndex(item => item.value === e[0].value)] || [0]
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
		height: 100%;
	}

	.flow-urgent-value {
		position: sticky;
		top: var(--window-top);
		z-index: 200;
		display: flex;
		align-items: center;
		justify-content: center;
		height: 60rpx;
		font-size: 28rpx;
	}

	.flowBefore-v {
		display: flex;
		flex-direction: column;



		.workFlowTitle {
			width: 100%;
			padding: 0 32rpx 32rpx 32rpx;
			background-color: #FFFFFF;
			font-size: 32rpx;
			font-weight: 700;
			white-space: pre-wrap;
			text-align: left;
		}

		.flowBefore-box {
			height: 100%;
			flex: 1;
			display: flex;
			flex-direction: column;
			overflow: hidden;
			padding-bottom: 88rpx;

			.sticky-box {
				z-index: 500;
			}

			.discuss_box {
				.discuss_list {
					margin-bottom: 40rpx;

					.discuss_txt {
						width: 100%;
						justify-content: space-between;

						.discuss_txt_left {
							.uName {
								margin-left: 20rpx;
							}
						}

						.del {
							color: red;
						}
					}

					.discuss_content {
						padding-left: 110rpx;

						.txt {
							color: #666666
						}

						.img_box {
							margin: 40rpx 0;
						}
					}
				}
			}
		}

		.swiper-box {
			height: 100vh;
		}

		.swiper-item {
			flex: 1;
			flex-direction: row;
		}

		.scroll-v {
			flex: 1;
			/* #ifndef MP-ALIPAY */
			flex-direction: column;
			/* #endif */
			width: 100%;
			height: 100%;
		}

		.flowStatus {
			position: absolute;
			top: 90rpx;
			right: 0;
			border: 0;
			margin: 20rpx;
			opacity: 0.7;
			z-index: 999;

			image {
				width: 200rpx;
			}
		}

		.discuss_btn {
			background-color: #fff;
			position: fixed;
			bottom: 0;
			display: flex;
			width: 100%;
			// height: 88rpx;
			// box-shadow: 0 -2rpx 8rpx #e1e5ec;
			z-index: 20;

			.custom-style {
				background-color: #2979ff;
				color: #FFFFFF;
				width: 100%;
				border-radius: 0 !important;

				&::after {
					border: none !important;
				}
			}

			.content {
				padding: 24rpx;
				text-align: center;

				.confrim-btn {
					display: flex;
					flex-direction: row;

					.send {
						flex: 1;
						background-color: #2979ff;
						color: #FFFFFF;
					}

					.cancel {
						flex: 1;
					}
				}
			}
		}
	}

	.nodeList-v {
		background-color: #fff;
	}

	.record-v {
		padding: 32rpx 32rpx 10rpx;
		background-color: #fff;
	}
</style>