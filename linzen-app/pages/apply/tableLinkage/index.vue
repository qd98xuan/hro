<template>
	<view class="linzen-pop-select">
		<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :sticky="true"
			:down="downOption" :up="upOption">
			<view class="search-box search-box_sticky">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
					bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<view class="u-flex-col tableList">
				<view class="u-flex list-card" v-for="(item,index) in list" :key="index">
					<u-checkbox-group wrap @change="checkboxGroupChange(item,index)">
						<u-checkbox v-model="item.checked">
							<view class="u-flex-col fieldContent">
								<view class="fieldList u-line-1 u-flex" v-for="(column,c) in actionConfig.columnOptions"
									:key="c">
									<view class="key">{{column.label+':'}}</view>
									<view class="val">{{item[column.value]}}</view>
								</view>
							</view>
						</u-checkbox>
					</u-checkbox-group>
				</view>
			</view>
		</mescroll-body>
		<!-- 底部按钮 -->
		<view class="flowBefore-actions">
			<template>
				<u-button class="buttom-btn" @click.stop="eventLauncher('cancel')">
					{{'取消'}}
				</u-button>
				<u-button class="buttom-btn" type="primary" @click.stop="eventLauncher('confirm')">
					{{'确定'}}
				</u-button>
			</template>
		</view>
	</view>
</template>

<script>
	import {
		getPopSelect,
		getRelationSelect
	} from '@/api/common.js'
	import resources from '@/libs/resources.js'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	export default {
		mixins: [MescrollMixin],
		data() {
			return {
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
				list: [],
				type: '',
				onLoadData: {},
				keyword: '',
				innerValue: '',
				listQuery: {
					keyword: '',
					pageSize: 20
				},
				interfaceId: '',
				cur: null,
				firstVal: '',
				firstId: 0,
				selectId: "",
				publicField: '',
				selectItem: [],
				actionConfig: {},
				formData: {},
				userInfo: {},
				subVal: [],
				keyword: '',
				columnOptions: [],
				isDynamic: true,
			}
		},
		onLoad(e) {
			this.userInfo = uni.getStorageSync('userInfo') || {}
			this.onLoadData = JSON.parse(e.data);
			this.actionConfig = this.onLoadData.actionConfig
			this.isDynamic = this.actionConfig.dataSource == 'dynamic'
			this.actionConfig.columnOptions.filter(o => {
				this.columnOptions.push(o.value)
			})
			uni.setNavigationBarTitle({
				title: this.actionConfig.popupTitle || '选择数据'
			})
			this.formData = this.onLoadData.formData
			this.interfaceId = this.actionConfig.interfaceId
			this.listQuery.pageSize = this.actionConfig.hasPage && this.isDynamic ? this.actionConfig.pageSize : 10000
			uni.$on('refresh', () => {
				this.list = [];
				this.mescroll.resetUpScroll();
			})
		},
		computed: {
			paramList() {
				return this.getParamList
			}
		},
		methods: {
			getParamList() {
				let templateJson = this.actionConfig.templateJson
				for (let i = 0; i < templateJson.length; i++) {
					templateJson[i].defaultValue = this.formData[templateJson[i].relationField] || ''
					if (templateJson[i].projectKey === 'createUser') {
						templateJson[i].defaultValue = this.userInfo.userId
					}
					if (templateJson[i].projectKey === 'createTime') {
						templateJson[i].defaultValue = new Date().getTime()
					}
					if (templateJson[i].projectKey === 'currOrganize') {
						templateJson[i].defaultValue = this.userInfo.organizeId
					}
					if (templateJson[i].projectKey === 'currPosition') {
						templateJson[i].defaultValue = this.userInfo.positionIds && this.userInfo.positionIds.length ? this
							.userInfo.positionIds[0] : ''
					}
				}
				return templateJson
			},
			upCallback(page) {
				const interfaceId = this.actionConfig.interfaceId
				const modelId = this.actionConfig.modelId
				if (this.isDynamic && !interfaceId) return this.handleEmpty()
				if (!this.isDynamic && !modelId) return this.handleEmpty()
				let query = {
					...this.listQuery,
					currentPage: page.num,
					keyword: this.keyword,
					columnOptions: this.columnOptions.join(',')
				}
				if (this.isDynamic) {
					query.interfaceId = interfaceId
					query.paramList = this.paramList()
				} else {
					query.modelId = this.actionConfig.modelId
					query.relationField = this.actionConfig.relationField
				}
				const id = this.isDynamic ? interfaceId : modelId
				const method = this.isDynamic ? getPopSelect : getRelationSelect
				method(id, query, {
					load: page.num == 1
				}).then(res => {
					this.mescroll.endSuccess(res.data.list.length);
					if (page.num == 1) this.list = [];
					this.list = this.list.concat(res.data.list);
					this.list = this.list.map((o, i) => ({
						...o,
						checked: false
					}))
				}).catch(() => {
					this.mescroll.endErr();
				})
			},
			handleEmpty() {
				this.mescroll.endSuccess(0);
				this.mescroll.endErr()
			},
			search() {
				// 节流,避免输入过快多次请求
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300)
			},
			checkboxGroupChange(e, index) {
				this.selectItem = this.list.filter(o => o.checked)
				let subVal = []
				for (let i = 0; i < this.selectItem.length; i++) {
					const e = this.selectItem[i]
					let item = {}
					for (let j = 0; j < this.actionConfig.relationOptions.length; j++) {
						let row = this.actionConfig.relationOptions[j]
						item[row.field] = row.type == 1 ? e[!this.isDynamic ? row.value + '_linzenId' : row.value] : row
							.value
					}
					subVal.push(item)
				}
				this.subVal = subVal
			},
			interfaceDataHandler(data) {
				if (!data.dataProcessing) return data.list
				const dataHandler = this.linzen.getScriptFunc(data.dataProcessing)
				if (!dataHandler) return data.list
				return dataHandler(data.list)
			},

			radioChange(item) {
				this.selectId = item[this.publicField];
				this.innerValue = item[this.onLoadData.relationField];
			},
			eventLauncher(type) {
				if (type === 'cancel') return uni.navigateBack();
				uni.$emit('linkPageConfirm', this.subVal, this.onLoadData.tableVmodel)
				uni.navigateBack();
			},
			search() {
				// 节流,避免输入过快多次请求
				this.searchTimer && clearTimeout(this.searchTimer)
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.listQuery.keyword = this.keyword
					this.listQuery.currentPage = 1
					this.listQuery.pageSize = this.hasPage ? this.pageSize : 10000
					this.mescroll.resetUpScroll();
				}, 300)
			},
		}
	}
</script>

<style scoped lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.linzen-pop-select {
		width: 100%;
		// height: 100%;
		// margin-top: 20rpx;
		padding-bottom: 90rpx;

		.tableList {
			overflow: hidden auto;
			padding: 0 20rpx;

			.list-card {
				background-color: #fff;
				width: 100%;
				border-radius: 8rpx;
				margin-top: 20rpx;
				padding: 20rpx 20rpx;
				align-items: flex-start;

				.u-checkbox-group {
					width: 100%;

					.u-checkbox {
						align-items: flex-start;
					}

					.u-checkbox__label {
						.fieldContent {
							width: 100%;
							margin-top: -14rpx;

							.fieldList {
								width: 752rpx;

								.key {
									width: 136rpx;
									margin-right: 10rpx;
									text-align: right;
									overflow: hidden;
									white-space: nowrap;
									text-overflow: ellipsis;
								}

								.val {
									flex: 0.8;
									overflow: hidden;
									white-space: nowrap;
									text-overflow: ellipsis;
								}
							}
						}
					}
				}



			}
		}
	}
</style>