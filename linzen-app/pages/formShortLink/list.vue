<template>
	<view class="dynamicModel-list-v">
		<view class="head-warp com-dropdown">
			<u-dropdown class="u-dropdown" ref="uDropdown">
				<u-dropdown-item title="筛选">
					<view class="dropdown-slot-content">
						<view class="dropdown-slot-content-main search-main">
							<scroll-view scroll-y="true" style="height: 1000rpx;">
								<view class="u-p-l-32 u-p-r-32" v-if="showParser && columnCondition.length">
									<Parser :formConf="columnCondition" ref="searchForm" @submit="sumbitSearchForm"
										:webType="config.webType" :searchFormData="searchFormData" />
								</view>
								<view v-else class="notData-box u-flex-col">
									<view class="u-flex-col notData-inner">
										<image :src="icon" class="iconImg"></image>
										<text class="notData-inner-text">暂无数据</text>
									</view>
								</view>
							</scroll-view>
							<view class="buttom-actions" v-if="showParser && columnCondition.length">
								<u-button class="buttom-btn" @click="reset">重置</u-button>
								<u-button class="buttom-btn" type="primary" @click="closeDropdown">检索</u-button>
							</view>
						</view>
						<view class="dropdown-slot-bg" @click="$refs.uDropdown.close()"></view>
					</view>
				</u-dropdown-item>
			</u-dropdown>
		</view>
		<view class="list-warp">
			<mescroll-uni ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :up="upOption"
				top="200">
				<view class="list" ref="tableRef">
					<view class="list-box">
						<uni-swipe-action ref="swipeAction">
							<uni-swipe-action-item v-for="(item, index) in list" :key="item.id" :threshold="0"
								:disabled="true">
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
												<linzen-file v-model="item[column.prop]" disabled
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
											<text class="item-cell-content" v-else>{{item[column.prop]}}</text>
										</template>
										<tableCell v-else class="tableCell" :label="column.label"
											:childList="item[column.prop]||[]" :children="column.children"
											ref="tableCell" :pageLen="3" @cRelationForm="relationFormClick"></tableCell>
									</view>
								</view>
							</uni-swipe-action-item>
						</uni-swipe-action>
					</view>
				</view>
			</mescroll-uni>
		</view>
	</view>
</template>
<script>
	import tableCell from '@/pages/apply/dynamicModel/components/tableCell.vue'
	import resources from '@/libs/resources.js'
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import Parser from '@/pages/apply/dynamicModel/components/parser/index.vue'
	import {
		listLink
	} from '@/api/apply/webDesign'
	export default {
		mixins: [MescrollMixin],
		props: ['config', 'modelId', 'columnCondition', 'columnText', 'encryption', 'searchFormData'],
		components: {
			Parser,
			tableCell
		},
		data() {
			return {
				show: false,
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
				columnList: {},
				searchList: [],
				searchFormConf: [],
				key: +new Date()
			}
		},
		created() {
			this.init()
		},
		methods: {
			init() {
				this.listQuery.sidx = this.columnText[0] && this.columnText[0].prop
				this.columnList = this.transformColumnList(this.columnText)
				this.columnList.map((o) => {
					if (o.projectKey != 'table' && o.label.length > 4) {
						o.label = o.label.substring(0, 4)
					}
				})
				this.$nextTick(() => {
					this.key = +new Date()
				})
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
					menuId: this.modelId,
					...this.listQuery
				}
				listLink(this.modelId, query, this.encryption, {
					load: page.num == 1
				}, this.encryption).then(res => {
					this.showParser = true
					if (page.num == 1) this.list = [];
					this.mescroll.endSuccess(res.data.list.length);
					const list = res.data.list.map((o, i) => ({
						show: false,
						...o
					}));
					this.list = this.list.concat(list);
					uni.$off('refresh')
				}).catch((err) => {
					this.mescroll.endByPage(0, 0);
					this.mescroll.endErr();
					uni.$off('refresh')
				})
			},
			goDetail(item) {
				if (!item.id) return
				let config = {
					modelId: this.modelId,
					id: item.id,
					formTitle: '详情',
					noShowBtn: 1,
					encryption: this.encryption
				}
				this.$nextTick(() => {
					const url =
						'./detail?config=' + this.base64.encode(JSON.stringify(config),
							"UTF-8")
					uni.navigateTo({
						url: url
					})
				})

			},
			reset() {
				this.showParser = false
				this.searchFormConf = JSON.parse(JSON.stringify(this.searchList))
				this.$nextTick(() => {
					this.showParser = true
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
					const val = data.hasOwnProperty(item.__vModel__) ? data[item.__vModel__] : item.__config__
						.defaultValue
					if (!item.__config__.custom && item.__config__.defaultCurrent && item.__config__
						.projectKey === 'timePicker') val = this.linzen.toDate(new Date(), item.format)
					if (!item.__config__.custom && item.__config__.defaultCurrent && item.__config__
						.projectKey === 'datePicker') val = new Date().getTime()
					item.__config__.defaultValue = val
				}
			},
			sumbitSearchForm(data) {
				const queryJson = data || {}
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
</style>