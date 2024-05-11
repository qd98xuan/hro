<template>
	<view class="dynamicModel-list-v">
		<view class="head-warp com-dropdown">
			<u-dropdown class="u-dropdown" ref="uDropdown">
				<u-dropdown-item title="排序" :options="sortOptions">
					<view class="screen-box">
						<view class="screen-list" v-if="sortOptions.length">
							<view class="u-p-l-20 u-p-r-20 list">
								<scroll-view scroll-y="true" style="height: 100%;">
									<u-cell-group :border="false">
										<u-cell-item @click="cellClick(item)" :arrow="false" :title="item.label"
											v-for="(item, index) in sortOptions" :key="index" :title-style="{
									color: sortValue.includes(item.value) ? '#2979ff' : '#606266' }">
											<u-icon v-if="sortValue.includes(item.value)" name="checkbox-mark"
												color="#2979ff" size="32" />
										</u-cell-item>
									</u-cell-group>
								</scroll-view>
							</view>
						</view>
						<view v-else class="notData-box u-flex-col">
							<view class="u-flex-col notData-inner">
								<image :src="icon" class="iconImg"></image>
								<text class="notData-inner-text">暂无数据</text>
							</view>
						</view>
						<view class="buttom-actions" v-if="sortOptions.length" style="z-index: 1;">
							<u-button class="buttom-btn" @click="sortValue = []">清空</u-button>
							<u-button class="buttom-btn" type="primary" @click="handleSortSearch">确定</u-button>
						</view>
					</view>
				</u-dropdown-item>
				<u-dropdown-item title="筛选">
					<view class="screen-box">
						<view class="screen-list" v-if="showParser && searchFormConf.length">
							<view class="u-p-l-20 u-p-r-20 list">
								<Parser :formConf="searchFormConf" :searchFormData="searchFormData" ref="searchForm"
									@submit="sumbitSearchForm" :webType="config.webType" :key="key" />
							</view>
						</view>
						<view v-else class="notData-box u-flex-col">
							<view class="u-flex-col notData-inner">
								<image :src="icon" class="iconImg"></image>
								<text class="notData-inner-text">暂无数据</text>
							</view>
						</view>
						<view class="buttom-actions" v-if="showParser && searchFormConf.length" style="z-index: 1;">
							<u-button class="buttom-btn" @click="handleReset">重置</u-button>
							<u-button class="buttom-btn" type="primary" @click="closeDropdown">检索</u-button>
						</view>
					</view>
				</u-dropdown-item>
			</u-dropdown>
		</view>
		<view class="list-warp">
			<mescroll-uni ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :up="upOption"
				top="80">
				<view class="list u-p-b-20 u-p-l-20 u-p-r-20" ref="tableRef">
					<view class="list-box">
						<uni-swipe-action ref="swipeAction">
							<uni-swipe-action-item v-for="(item, index) in list" :key="item.id" :threshold="0"
								:right-options="options">
								<view class="item" @click="goDetail(item)">
									<view class="item-compatible-cell" v-for="(column,i) in columnList" :key="i">
										<template v-if="column.projectKey != 'table'">
											<text class="item-cell-label">{{column.label}}:</text>
											<text class="item-cell-content"
												v-if="['calculate','inputNumber'].includes(column.projectKey)">{{toThousands(item[column.prop],column)}}</text>
											<text class="item-cell-content text-primary"
												v-else-if="column.projectKey == 'relationForm'"
												@click.stop="relationFormClick(item,column)">
												{{item[column.prop]}}
											</text>
											<view class="item-cell-content" v-else-if="column.projectKey == 'rate'">
												<linzen-rate v-model="item[column.prop]" :max="column.count" type="list"
													:allowHalf="column.allowHalf" disabled>
												</linzen-rate>
											</view>
											<view class="item-cell-content item-cell-slider"
												v-else-if="column.projectKey == 'slider'">
												<linzen-slider v-model="item[column.prop]" :step="column.step"
													:min="column.min||0" :max="column.max||100" disabled />
											</view>
											<view class="item-cell-content" v-else-if="column.projectKey == 'uploadFile'"
												@click.stop>
												<linzen-file v-model="item[column.prop]"
													v-if="item[column.prop] && item[column.prop].length"
													:limit="column.limit?column.limit:9" :sizeUnit="column.sizeUnit"
													:fileSize="!column.fileSize ? 5 : column.fileSize"
													:pathType="column.pathType" :isAccount="column.isAccount"
													:folder="column.folder" :accept="column.accept"
													:tipText="column.tipText" type="list" detailed simple />
											</view>
											<view class="item-cell-content" v-else-if="column.projectKey == 'uploadImg'"
												@click.stop>
												<linzen-upload v-model="item[column.prop]" disabled simple
													v-if="item[column.prop] && item[column.prop].length"
													:fileSize="column.fileSize" :limit="column.limit"
													:pathType="column.pathType" :isAccount="column.isAccount"
													:folder="column.folder" :tipText="column.tipText"
													:sizeUnit="column.sizeUnit">
												</linzen-upload>
											</view>
											<view class="item-cell-content" v-else-if="column.projectKey == 'sign'">
												<linzen-sign v-model="item[column.prop]" detailed align='left' />
											</view>
											<view class="item-cell-content" v-else-if="column.projectKey == 'input'">
												<linzen-input v-model="item[column.prop]" detailed align='left'
													:useMask='column.useMask' :maskConfig='column.maskConfig' />
											</view>
											<text class=" item-cell-content" v-else>{{item[column.prop] || ''}}</text>
										</template>
										<tableCell v-else class="tableCell" :label="column.label"
											:childList="item[column.prop]||[]" :children="column.children"
											ref="tableCell" :pageLen="3" @cRelationForm="relationFormClick"></tableCell>
									</view>
									<view class="item-compatible-cell" v-if="config.enableFlow==1">
										<text class="item-cell-label">审批状态:</text>
										<text :class="getFlowStatus(item.flowState).statusCss">
											{{getFlowStatus(item.flowState).text}}
										</text>
									</view>
								</view>
								<template v-slot:right>
									<view class="right-option-box">
										<view class="right-option more-option"
											v-if="columnData.customBtnsList&&columnData.customBtnsList.length"
											@click="handleMoreClick(index)">
											<text>更多</text>
											<uni-icons type="arrowdown" color="#fff" size="16" />
										</view>
										<view class="right-option" v-for="(it,i) in options" @click="handleClick(index)"
											:key="i" v-if="config.webType != 4">
											<text>{{it.text}}</text>
										</view>
									</view>
								</template>
							</uni-swipe-action-item>
						</uni-swipe-action>
					</view>
				</view>
			</mescroll-uni>
		</view>
		<view class="" v-if="config.webType !=4">
			<view class="com-addBtn"
				v-if="isPreview||(jurisdictionObj.btnAllow && jurisdictionObj.btnAllow.includes('btn_add'))"
				@click="addPage()">
				<u-icon name="plus" size="60" color="#fff" />
			</view>
		</view>
		<u-select :list="columnData.customBtnsList" v-model="showMoreBtn" @confirm="selectBtnconfirm" />
		<u-picker mode="selector" v-model="show" :default-selector="[0]" title="请选择流程" :range="selector"
			range-key="fullName" @confirm="confirm"></u-picker>
	</view>
</template>
<script>
	import tableCell from '../tableCell.vue'
	import resources from '@/libs/resources.js'
	import handleJurisdiction from '@/libs/permission.js'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import Parser from '../parser/index.vue'
	import {
		getModelList,
		deteleModel,
		getModelInfo
	} from '@/api/apply/visualDev'
	import {
		FlowJsonList
	} from '@/api/workFlow/flowEngine'
	import {
		getDataInterfaceRes
	} from '@/api/common'
	const useDateList = ['datePicker', 'createTime', 'modifyTime']
	const useArrList = ['cascader', 'areaSelect', 'inputNumber', 'calculate', ...useDateList]
	export default {
		mixins: [MescrollMixin],
		props: ['config', 'modelId', 'isPreview', 'title', 'menuId'],
		components: {
			Parser,
			tableCell
		},
		data() {
			return {
				key: +new Date(),
				selector: [],
				show: false,
				activeFlow: {},
				templateList: [],
				sortValue: [],
				icon: resources.message.nodata,
				upOption: {
					page: {
						num: 0,
						size: 10,
						time: null
					},
					empty: {
						icon: resources.message.nodata,
						tip: "暂无数据",
						top: "300rpx"
					},
					textNoMore: '没有更多数据',
					toTop: {
						bottom: 250
					}
				},
				list: [],
				listQuery: {
					sort: 'desc',
					sidx: '',
					keyword: '',
					queryJson: ''
				},
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				showParser: false,
				columnData: {},
				columnList: [],
				sortOptions: [],
				sortList: [],
				searchList: [],
				searchFormConf: [],
				jurisdictionObj: {},
				selectListIndex: 0,
				showMoreBtn: false,
				properties: {},
				flowId: '',
				key: +new Date(),
				userInfo: {},
				searchFormData: {},
			}
		},
		watch: {
			jurisdictionObj(val) {
				this.options[0].text = val.labelS['btn_remove']
			}
		},
		created() {
			this.init()

		},
		updated() {
			uni.$once('refresh', () => {
				this.list = [];
				this.mescroll.resetUpScroll();
			})
		},
		watch: {
			jurisdictionObj(val) {
				this.options[0].text = val.labelS['btn_remove']
			}
		},
		methods: {
			toThousands(val, column) {
				if (val || val == 0) {
					let valList = val.toString().split('.')
					let num = Number(valList[0])
					let newVal = column.thousands ? num.toLocaleString() : num
					return valList[1] ? newVal + '.' + valList[1] : newVal
				} else {
					return val === 0 ? 0 : ''
				}
			},
			getJsonList() {
				FlowJsonList(this.config.flowId, '1').then(res => {
					this.templateList = res.data;
					this.selector = this.templateList
				})
			},
			relationFormClick(item, column) {
				let vModel = column.vModel ? column.vModel : column.__vModel__
				let model_id = column.modelId
				const idText = '&id=' + item[vModel + '_id']
				let config = {
					modelId: model_id,
					isPreview: true,
					id: item[vModel + '_id'],
					isRelationForm: 1
				}
				const url =
					'/pages/apply/dynamicModel/detail?config=' + this.base64.encode(JSON.stringify(
							config),
						"UTF-8")
				uni.navigateTo({
					url: url
				})
			},
			init() {
				this.permissionList = uni.getStorageSync('permissionList')
				this.userInfo = uni.getStorageSync('userInfo') || {}
				this.properties = this.config.flowTemplateJson ? JSON.parse(this.config.flowTemplateJson).properties : {}
				let columnData = this.config.appColumnData ? this.config.appColumnData : this.config.columnData ? this
					.config.columnData : []
				this.columnData = JSON.parse(columnData)
				this.jurisdictionObj = handleJurisdiction.inserted(this.columnData, this.permissionList, this.menuId)
				this.upOption.page.size = this.columnData.hasPage ? this.columnData.pageSize : 1000000
				this.setDefaultQuery()
				this.columnList = this.jurisdictionObj.columnAllow || []
				this.columnData.customBtnsList = this.jurisdictionObj.customBtnsList || []
				this.columnList = this.transformColumnList(this.columnList)
				this.columnList.map((o) => {
					if (o.projectKey != 'table' && o.label.length > 4) o.label = o.label.substring(0, 4)
				})
				this.sortList = this.columnList.filter(o => o.sortable)
				this.handleSearchList()
				this.handleSortList()
				if (this.config.enableFlow == 1) this.getJsonList()
				this.key = +new Date()
			},
			handleSortList() {
				this.sortOptions = [];
				const sortList = this.sortList
				for (let i = 0; i < sortList.length; i++) {
					let ascItem = {
						label: sortList[i].label + '升序',
						value: sortList[i].prop,
						sidx: sortList[i].prop,
						sort: 'asc'
					}
					let descItem = {
						label: sortList[i].label + '降序',
						value: '-' + sortList[i].prop,
						sidx: sortList[i].prop,
						sort: 'desc'
					}
					this.sortOptions.push(ascItem, descItem)
				}
			},
			handleSearchList() {
				this.searchList = this.columnData.searchList || []
				for (let i = 0; i < this.searchList.length; i++) {
					const item = this.searchList[i]
					const config = item.__config__
					const hasDefaultKeyList = ['depSelect', 'organizeSelect', 'userSelect']
					if (item.value != null && item.value != '' && item.value != []) this.searchFormData[item.id] = item
						.value;
					if (this.config.webType == 4) config.label = item.label
				}
				if (Object.keys(this.searchFormData).length) this.listQuery.queryJson = JSON.stringify(this.searchFormData)
				if (this.searchList.some(o => o.isKeyword)) {
					const keywordItem = {
						id: 'projectKeyword',
						fullName: '关键词',
						prop: 'projectKeyword',
						label: '关键词',
						projectKey: 'input',
						clearable: true,
						placeholder: '请输入',
						value: undefined,
						__config__: {
							projectKey: 'input'
						},
					};
					this.searchList.unshift(keywordItem);
				}
				this.searchFormConf = this.$u.deepClone(this.searchList)
			},
			transformColumnList(columnList) {
				let list = []
				for (let i = 0; i < columnList.length; i++) {
					const e = columnList[i];
					if (!e.prop.includes('-')) {
						e.option = null
						list.push(e)
					} else {
						let prop = e.prop.split('-')[0]
						let vModel = e.prop.split('-')[1]
						let label = e.label.split('-')[0]
						let childLabel = e.label.replace(label + '-', '');
						let newItem = {
							align: "center",
							projectKey: "table",
							prop,
							label,
							children: []
						}
						e.vModel = vModel
						e.childLabel = childLabel
						if (!list.some(o => o.prop === prop)) list.push(newItem)
						for (let i = 0; i < list.length; i++) {
							if (list[i].prop === prop) {
								e.option = null
								list[i].children.push(e)
								break
							}
						}
					}
				}
				return list
			},
			upCallback(page) {
				if (this.isPreview == '1') return this.mescroll.endSuccess(0, false);
				const query = {
					currentPage: page.num,
					pageSize: page.size,
					menuId: this.menuId,
					...this.listQuery
				}
				getModelList(this.modelId, query, {
					load: page.num == 1
				}).then(res => {
					this.showParser = true
					if (page.num == 1) this.list = [];
					this.mescroll.endSuccess(res.data.list.length);
					const list = res.data.list.map((o, i) => ({
						show: false,
						index: i,
						...o
					}));
					this.list = this.list.concat(list);
					this.$nextTick(() => {
						if (this.columnData.funcs && this.columnData.funcs.afterOnload) this
							.setTableLoadFunc()
					})
					uni.$off('refresh')
				}).catch((err) => {
					this.mescroll.endByPage(0, 0);
					this.mescroll.endErr();
					uni.$off('refresh')
				})
			},
			setTableLoadFunc() {
				const LINZENTable = this.$refs.tableRef
				const parameter = {
					data: this.list,
					tableRef: LINZENTable,
					onlineUtils: this.linzen.onlineUtils,
				}
				const func = this.linzen.getScriptFunc.call(this, this.columnData.funcs.afterOnload)
				if (!func) return
				func.call(this, parameter)
			},
			handleClick(index) {
				const item = this.list[index]
				let txt = item.flowState == 2 ? '通过' : item.flowState == 3 ? '退回' : item.flowState == 5 ? '终止' : '提交'
				if (this.config.enableFlow == 1 && [1, 2, 3, 5].includes(item.flowState)) {
					this.$u.toast(`该流程已${txt},无法删除`)
					this.list[index].show = false
					return
				}
				if (!this.jurisdictionObj.btnAllow.includes('btn_remove')) return this.$u.toast("未开启删除权限")
				deteleModel(this.modelId, item.id).then(res => {
					this.$u.toast(res.msg)
					this.list.splice(index, 1)
					this.mescroll.resetUpScroll()
				})
			},
			handleMoreClick(index) {
				this.selectListIndex = index
				this.showMoreBtn = true
			},
			selectBtnconfirm(e) {
				var i = this.columnData.customBtnsList.findIndex((item) => {
					return item.value == e[0].value
				})
				const item = this.columnData.customBtnsList[i]
				const row = this.list[this.selectListIndex]
				const index = this.selectListIndex
				if (item.event.btnType == 1) this.handlePopup(item.event, row, index)
				if (item.event.btnType == 2) this.handleScriptFunc(item.event, row, index)
				if (item.event.btnType == 3) this.handleInterface(item.event, row, index)
			},
			handlePopup(item, row, index) {
				let data = {
					config: item,
					modelId: this.modelId,
					id: this.config.webType == 4 ? '' : row.id,
					isPreview: this.isPreview,
					row: this.config.webType == 4 ? row : '',
				}
				data = encodeURIComponent(JSON.stringify(data))
				uni.navigateTo({
					url: '/pages/apply/LINZENCustom/index?data=' + data
				})
			},
			handleScriptFunc(item, row, index) {
				const parameter = {
					data: row,
					index,
					refresh: this.initData,
					onlineUtils: this.linzen.onlineUtils,
				}
				const func = this.linzen.getScriptFunc.call(this, item.func)
				if (!func) return
				func.call(this, parameter)
			},
			handleInterface(item, row) {
				const handlerData = () => {
					getModelInfo(this.modelId, row.id).then(res => {
						const dataForm = res.data || {};
						if (!dataForm.data) return;
						const data = {
							...JSON.parse(dataForm.data),
							id: row.id
						};
						handlerInterface(data);
					})
				}
				const handlerInterface = data => {
					if (item.templateJson && item.templateJson.length) {
						item.templateJson.forEach(e => {
							const value = data[e.relationField] || data[e.relationField] == 0 || data[e
								.relationField] == false ? data[e.relationField] : ''
							e.defaultValue = (e.sourceType == 1 && e.relationField === '@formId') ? data.id :
								e.sourceType == 1 ? value : e.relationField
						});
					}
					const query = {
						paramList: item.templateJson || []
					};
					getDataInterfaceRes(item.interfaceId, query).then(res => {
						uni.showToast({
							title: res.msg,
							icon: 'none'
						})
					});
				};
				const handleFun = () => {
					this.config.webType == '4' ? handlerInterface(row) : handlerData();
				};
				if (!item.useConfirm) return handleFun();
				uni.showModal({
					title: '提示',
					content: item.confirmTitle || '确认执行此操作',
					success: (res) => {
						if (res.cancel) return
						handleFun()
					}
				})
			},
			initData() {
				this.list = [];
				this.mescroll.resetUpScroll();
			},
			open(index) {
				this.list[index].show = true;
				this.list.map((val, idx) => {
					if (index != idx) this.list[idx].show = false;
				})
			},
			search() {
				if (this.isPreview == '1') return
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300)
			},
			addPage() {
				if (this.config.enableFlow != 1) return this.jumPage({}, '')
				if (!this.templateList.length) return this.$u.toast('流程不存在')
				if (this.templateList.length > 1) {
					this.show = true
				} else {
					this.activeFlow = this.templateList[0]
					this.jumPage({}, '')
				}
			},
			confirm(e) {
				this.activeFlow = this.templateList[e[0]]
				this.jumPage({}, '')
			},
			jumPage(item, btnType) {
				if (!item.id && !item.flowState) btnType = 'btn_add'
				let formTitle = ''
				if (this.config.enableFlow == 1) {
					let flowBtnType = btnType === 'btn_detail' ? 'btn_add' : btnType
					let opType = '-1'
					if ([1, 2, 4, 5].includes(item.flowState)) opType = 0
					const rowFlowId = item.flowId || this.templateList[0].id
					const config = {
						id: item.id || '',
						enCode: this.config.flowEnCode,
						flowId: item.id ? rowFlowId : this.activeFlow.id,
						formType: 2,
						type: 1,
						opType,
						status: item.flowState || '',
						isPreview: this.isPreview,
						fullName: item.id ? '编辑' : '新增',
						jurisdictionType: flowBtnType || '',
						properties: this.properties,
						enableFlow: this.config.enableFlow
					}
					uni.navigateTo({
						url: '/pages/workFlow/flowBefore/index?config=' + this.base64.encode(
							JSON.stringify(config), "UTF-8")
					})
				} else {
					const type = btnType == 'btn_detail' ? 'detail' : 'form'
					const currentMenu = encodeURIComponent(JSON.stringify(this.jurisdictionObj.formAllow))
					let hasEdit = this.jurisdictionObj.btnAllow.includes('btn_edit') ? 'btn_edit' : 'btn_add'
					const config = {
						currentMenu,
						jurisdictionType: hasEdit,
						list: this.list,
						modelId: this.modelId,
						isPreview: this.isPreview,
						id: item.id ? item.id : '',
						index: item.index,
						labelS: this.jurisdictionObj.labelS
					}
					const url = '/pages/apply/dynamicModel/' + type + '?config=' + this.base64.encode(JSON.stringify(
						config), "UTF-8")
					uni.navigateTo({
						url: url
					})
				}
			},
			goDetail(item) {
				if (this.config.webType == 4) return
				let hasDetail = this.jurisdictionObj.btnAllow.includes('btn_detail')
				let hasEdit = this.jurisdictionObj.btnAllow.includes('btn_edit')
				if (!hasDetail && !hasEdit) return
				let btnType = hasDetail ? 'btn_detail' : 'btn_edit'
				this.jumPage(item, btnType)
			},
			getFlowStatus(val) {
				let status
				switch (val) {
					case 0:
						status = {
							text: '等待提交',
							statusCss: 'u-type-info'
						}
						break;
					case 1:
						status = {
							text: '等待审核',
							statusCss: 'u-type-primary'
						}
						break;
					case 2:
						status = {
							text: '审核通过',
							statusCss: 'u-type-success'
						}
						break;
					case 3:
						status = {
							text: '审核退回',
							statusCss: 'u-type-error'
						}
						break;
					case 4:
						status = {
							text: '流程撤回',
							statusCss: 'u-type-warning'
						}
						break;
					case 5:
						status = {
							text: '审核终止',
							statusCss: 'u-type-info'
						}
						break;
					default:
						status = {
							text: '等待提交',
							statusCss: 'u-type-info'
						}
						break;
				}
				return status
			},
			cellClick(item) {
				if (this.isPreview == '1') return this.$u.toast('功能预览不支持排序')
				const findIndex = this.sortValue.findIndex(o => o === item.value);
				if (findIndex < 0) {
					const findLikeIndex = this.sortValue.findIndex(o => o.indexOf(item.sidx) > -1);
					if (findLikeIndex > -1) this.sortValue.splice(findLikeIndex, 1)
					this.sortValue.push(item.value)
				} else {
					this.sortValue.splice(findIndex, 1)
				}
			},
			handleReset() {
				this.searchFormData = {}
				const list = ['datePicker', 'timePicker', 'inputNumber', 'calculate', 'cascader', 'organizeSelect']
				for (let i = 0; i < this.searchList.length; i++) {
					const item = this.searchList[i]
					const config = item.__config__
					let defaultValue = item.searchMultiple || list.includes(config.projectKey) ? [] : undefined
					if (config.isFromParam) defaultValue = undefined
					config.defaultValue = defaultValue
					this.searchFormData[item.id] = item.value || defaultValue
				}
				this.searchFormConf = JSON.parse(JSON.stringify(this.searchList))
				this.key = +new Date()
			},
			setDefaultQuery() {
				const defaultSortConfig = (this.columnData.defaultSortConfig || []).map(o =>
					(o.sort === 'desc' ? '-' : '') + o.field);
				this.listQuery.sidx = defaultSortConfig.join(',')
			},
			handleSortSearch() {
				if (this.sortValue.length) {
					this.listQuery.sidx = this.sortValue.join(',')
				} else {
					this.setDefaultQuery()
				}
				this.$refs.uDropdown.close();
				this.$nextTick(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				})
			},
			closeDropdown() {
				if (this.isPreview == '1') {
					uni.showToast({
						title: '功能预览不支持检索',
						icon: 'none'
					})
					return
				}
				this.$refs.searchForm && this.$refs.searchForm.submitForm()
			},
			fillFormData(list, data) {
				for (let i = 0; i < list.length; i++) {
					let item = list[i]
				}
			},
			sumbitSearchForm(data) {
				const queryJson = data || {}
				this.searchFormData = data
				this.fillFormData(this.searchFormConf, data)
				this.listQuery.queryJson = JSON.stringify(queryJson) !== '{}' ? JSON.stringify(queryJson) : ''
				this.$refs.uDropdown.close();
				this.$nextTick(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
		height: 100%;
		/* #ifdef MP-ALIPAY */
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		/* #endif */
	}

	/deep/.u-cell {
		padding: 0rpx;
		height: 112rpx;
	}

	.screen-box {
		background-color: #fff;
		height: 100%;

		.screen-list {
			width: 100%;
			height: 100%;

			.list {
				height: calc(100% - 88rpx);
				overflow-y: scroll;
			}
		}
	}

	.item {
		padding: 0 !important;
	}

	.notData-box {
		width: 100%;
		height: 100%;
		justify-content: center;
		align-items: center;
		padding-bottom: 200rpx;

		.notData-inner {
			width: 280rpx;
			height: 308rpx;
			align-items: center;

			.iconImg {
				width: 100%;
				height: 100%;
			}

			.notData-inner-text {
				padding: 30rpx 0;
				color: #909399;
			}
		}
	}

	.right-option-box {
		display: flex;
		width: max-content;

		.right-option {
			width: 144rpx;
			height: 100%;
			font-size: 16px;
			color: #fff;
			background-color: #dd524d;
			display: flex;
			align-items: center;
			justify-content: center;
		}

		.more-option {
			background-color: #1890ff;
		}
	}
</style>