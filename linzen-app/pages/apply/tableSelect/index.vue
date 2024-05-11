<template>
	<view class="linzen-pop-select">
		<mescroll-body ref="mescrollRef" @init="mescrollInit" @down="downCallback" @up="upCallback" :sticky="true"
			:down="downOption" :up="upOption">
			<view class="search-box search-box_sticky" v-if="filterable">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
					bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
			<view class="u-pop-selectScroll u-flex-col">
				<view class="u-pop__countent__selectBtn u-flex-col" v-if="!onLoadData.multiple">
					<radio-group class="u-radio-group" v-model="innerValue" v-for="(item,index) in list" :key="index"
						@change="radioChange(item)">
						<label class="u-radio-label u-flex">
							<view class="u-radio">
								<radio :value="item[publicField]" :checked="item[publicField] === selectId[0]" />
							</view>
							<view class="u-pop__countent__txt u-line-1">
								{{item[onLoadData.relationField]}}
							</view>
						</label>
					</radio-group>
				</view>
				<view class="u-select__body u-select__body__multiple" v-if="onLoadData.multiple">
					<u-checkbox-group wrap v-for="(item,i) in list" :key="i">
						<u-checkbox v-model="item.checked" :name="item[publicField]"
							@change="checkboxChange($event,item)">
							{{item[onLoadData.relationField]}}
						</u-checkbox>
					</u-checkbox-group>
				</view>
				<!-- 表格版 -->
				<!-- <u-table align="center" class="relation__table">
					<u-tr class="relation__table__tr">
						<u-th class="relation__table__th">
							<view style="height: 70rpx;"></view>
						</u-th>
						<u-th class="relation__table__th">序号</u-th>
						<u-th class="relation__table__th" v-for="(columns,i) in onLoadData.columnOptions.slice(0,1)" :key="i">
							{{columns.label}}
						</u-th>
					</u-tr>
					<u-tr class="relation__table__tr" v-for="(item,index) in list" :key="index">
						<view class="td__box" @click="radioChange(index,item)">
							<u-td class="relation__table__td">
								<radio-group @change="radioChange(index,item)" v-model="innerValue">
									<radio :value="item.id" :checked="index === cur" />
								</radio-group>
							</u-td>
							<u-td class="relation__table__td">{{index+1}}</u-td>

							<u-td class="relation__table__td">
								{{item[onLoadData.relationField]}}
							</u-td>
						</view>

					</u-tr>
				</u-table> -->
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
		getRelationSelect,
		getPopSelect
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
				onLoadData: {},
				keyword: '',
				innerValue: '',
				listQuery: {
					keyword: '',
					pageSize: 20
				},
				modelId: '',
				cur: null,
				firstVal: '',
				firstId: 0,
				selectId: [],
				publicField: '',
				txt: [],
				filterable: false,
				selectData: [],
				newSelctData: []
			}
		},
		onLoad(e) {
			this.onLoadData = JSON.parse(decodeURIComponent(e.data));
			this.innerValue = this.onLoadData.innerValue
			this.publicField = this.onLoadData.propsValue
			this.modelId = this.onLoadData.modelId
			this.filterable = this.onLoadData.filterable
			this.listQuery.pageSize = this.onLoadData.hasPage ? this.onLoadData.pageSize : 10000
			this.selectId = this.onLoadData.id || []
			this.selectData = this.onLoadData.selectData || []
			uni.setNavigationBarTitle({
				title: this.onLoadData.popupTitle
			})
			uni.$on('refresh', () => {
				this.list = [];
				this.mescroll.resetUpScroll();
			})
		},
		methods: {
			upCallback(page) {
				const paramList = this.onLoadData.paramList
				let query = {
					...this.listQuery,
					currentPage: page.num,
					interfaceId: this.onLoadData.modelId,
					propsValue: this.onLoadData.propsValue,
					relationField: this.onLoadData.relationField,
					paramList
				}
				getPopSelect(this.modelId, query, {
					load: page.num == 1
				}).then(res => {
					this.mescroll.endSuccess(res.data.list.length);
					if (page.num == 1) this.list = [];
					this.list = this.list.concat(res.data.list);
					if (this.onLoadData.multiple) {
						this.list = this.list.map((o, i) => ({
							...o,
							checked: false
						}))
						if (this.selectId.length > 0) {
							this.setSelectValue()
						}
					}
				}).catch(() => {
					this.mescroll.endErr();
				})
			},
			// 获取默认选中的值
			setSelectValue() {
				outer: for (let i = 0; i < this.selectId.length; i++) {
					inner: for (let j = 0; j < this.list.length; j++) {
						if (this.selectId[i] === this.list[j][this.publicField]) {
							this.list[j].checked = true
							break inner
						}
					}
				}
			},
			radioChange(item) {
				this.selectId = []
				this.selectData = []
				this.selectId.push(item[this.publicField]);
				this.selectData.push(item)
			},
			checkboxChange(e, item) {
				if (e.value) {
					this.selectId.push(e.name)
					this.newSelctData.push(item)
				} else {
					this.newSelctData = this.newSelctData.filter(o => o[this.publicField] != e.name && !e.value)
					this.selectId = this.selectId.filter(o => o != e.name)
					this.selectData = this.selectData.filter(o => o[this.publicField] != e.name)
				}
			},
			handelData() {
				let txt = [];
				if (this.onLoadData.multiple) {
					this.selectData = this.selectData.concat(this.newSelctData)
				}
				this.selectData.forEach(o => {
					txt.push(o[this.onLoadData.relationField])
				})
				this.innerValue = txt.length == 1 ? txt[0] : txt.toString()
			},
			eventLauncher(type) {
				if (type === 'confirm') {
					this.handelData()
					this.$nextTick(() => {
						uni.$emit('tabConfirm', this.selectId, this.innerValue, this.onLoadData.vModel, this
							.selectData)
						uni.navigateBack();
					})
				} else {
					this.list = []
					uni.navigateBack();
				}
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
	.linzen-pop-select {
		width: 100%;
		height: 100%;
	}

	.u-pop-selectScroll {
		.u-pop__countent__selectBtn {
			width: 100%;

			.u-radio-group {
				.u-radio-label {
					border-bottom: 1rpx solid #ebeef5;
					padding: 20rpx 20rpx;

					.u-radio {
						flex: 0.3;
						text-align: center;
					}

					.u-pop__countent__txt {
						overflow: hidden;
						white-space: nowrap;
						text-overflow: ellipsis;
						flex: 2;
						padding: 0 10rpx;
					}
				}
			}
		}

		/* 表格版 */
		.relation__table {
			.relation__table__tr {
				.relation__table__th {
					&:first-child {
						flex: 0.15;
					}

					&:nth-child(2) {
						flex: 0.15;
					}

					//#ifndef MP-WEIXIN
					height: 70rpx;
					//#endif
				}

				.td__box {
					display: flex;
					flex-direction: row;
					flex: 1;

					.relation__table__td {
						&:first-child {
							flex: 0.15;
						}

						&:nth-child(2) {
							flex: 0.15;
						}

						height: 70rpx;
					}
				}
			}

			/* 表格版 End*/
		}




		.nodata {
			margin-top: 258rpx;
			justify-content: center;
			align-items: center;

			image {
				width: 280rpx;
				height: 215rpx;
			}
		}
	}
</style>