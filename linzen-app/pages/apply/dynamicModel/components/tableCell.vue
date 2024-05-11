<template>
	<uni-collapse class='collapse' accordion ref="collapse" @change="collapseChange">
		<uni-collapse-item :key="key">
			<template v-slot:title>
				<view class="u-font-24 u-flex">
					<view class="" style="width: 124rpx;text-align: right;">
						<text>{{label+':'}}</text>
					</view>
					<text style="color: #606266;" class="u-m-l-28">展开数据</text>
				</view>
			</template>
			<view class="collapse-item" v-for="(item,d) in dataList" :key="d">
				<view v-if="d<allPageLen" class="item-cell-c">
					<view class="item-compatible-cell" v-for="(cld,c) in children" :key="c">
						<text class="item-cell-label">{{cld.childLabel}}:</text>
						<text v-if="['calculate','inputNumber'].includes(cld.__config__.projectKey)"
							class="item-cell-content">
							{{toThousands(item[cld.vModel],cld) }}
						</text>
						<text class="item-cell-content text-primary"
							v-else-if="cld.__config__.projectKey === 'relationForm'"
							@click.stop="relationFormClick(item,cld)">
							{{item[cld.vModel] | dynamicTreeText(cld.option)}}
						</text>
						<view class="item-cell-content" v-else-if="cld.__config__.projectKey == 'rate'">
							<linzen-rate v-model="item[cld.vModel]" :max="cld.count" :allowHalf="cld.allowHalf" disabled>
							</linzen-rate>
						</view>
						<view class="item-cell-content item-cell-slider" v-else-if="cld.__config__.projectKey == 'slider'">
							<linzen-slider v-model="item[cld.vModel]" :step="cld.step" :min="cld.min||0"
								:max="cld.max||100" disabled />
						</view>
						<view class="item-cell-content" v-else-if="cld.__config__.projectKey == 'uploadFile'" @click.stop>
							<linzen-file v-model="item[cld.vModel]" :limit="cld.limit?cld.limit:9"
								:sizeUnit="cld.sizeUnit" :fileSize="!cld.fileSize ? 5 : cld.fileSize"
								:pathType="cld.pathType" :isAccount="cld.isAccount" :folder="cld.folder"
								:accept="cld.accept" :tipText="cld.tipText" detailed simple
								v-if="item[cld.vModel] && item[cld.vModel].length" />
						</view>
						<view class="item-cell-content" v-else-if="cld.__config__.projectKey == 'uploadImg'" @click.stop>
							<linzen-upload v-model="item[cld.vModel]" disabled simple :fileSize="cld.fileSize"
								:limit="cld.limit" :pathType="cld.pathType" :isAccount="cld.isAccount"
								:folder="cld.folder" :tipText="cld.tipText" :sizeUnit="cld.sizeUnit"
								v-if="item[cld.vModel] && item[cld.vModel].length">
							</linzen-upload>
						</view>
						<view class="item-cell-content" v-else-if="cld.__config__.projectKey == 'sign'">
							<linzen-sign v-model="item[cld.vModel]" detailed align='left' />
						</view>
						<view class="item-cell-content" v-else-if="cld.__config__.projectKey == 'input'">
							<linzen-input v-model="item[cld.vModel]" detailed align='left' :useMask='cld.useMask'
								:maskConfig='cld.maskConfig' />
						</view>
						<text class="item-cell-content" v-else>{{item[cld.vModel]}}</text>
					</view>
				</view>
			</view>
			<view class="loadMore" @click.stop="loadMore" v-if="!isAllData&&this.dataList.length>allPageLen">
				加载更多
			</view>
		</uni-collapse-item>
	</uni-collapse>
</template>
<script>
	export default {
		props: ['childList', 'label', 'children', 'pageLen', 'thousands', 'thousandsField'],
		data() {
			return {
				dataList: [],
				isAllData: false,
				key: +new Date(),
				allPageLen: 3
			}
		},

		watch: {
			childList: {
				immediate: true,
				handler(val) {
					this.dataList = val
					this.allPageLen = this.pageLen
					this.children.map(o => {
						if (o.childLabel.length > 4) {
							o.childLabel = o.childLabel.substring(0, 4)
						}
					})
				}
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
					return val
				}
			},
			relationFormClick(item, cld) {
				this.$emit('cRelationForm', item, cld)
			},
			loadMore() {
				this.allPageLen = this.childList.length
				this.isAllData = true
			},
			collapseChange(e) {
				if (!e) {
					this.isAllData = false
					setTimeout(() => {
						this.allPageLen = this.pageLen
					}, 500)
				}
			}
		}
	}
</script>