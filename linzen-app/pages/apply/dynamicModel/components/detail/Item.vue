<template>
	<view :class="{'item-card':item.__config__.projectKey==='card'}"
		v-if="!item.__config__.noShow && (!item.__config__.visibility || (Array.isArray(item.__config__.visibility) && item.__config__.visibility.includes('app')))">
		<template v-if="item.__config__.layout==='colFormItem'">
			<template v-if="item.__config__.projectKey==='divider'">
				<u-divider half-width="200" height="80">{{item.content}}</u-divider>
			</template>
			<template v-else-if="item.__config__.projectKey==='text'">
				<view class="u-p-l-20 u-p-r-20 form-item-box">
					<u-form-item>
						<linzen-text :content="item.content" :textStyle="item.textStyle"></linzen-text>
					</u-form-item>
				</view>
			</template>
			<template v-else-if="item.__config__.projectKey==='link'">
				<view class="u-p-l-20 u-p-r-20 form-item-box">
					<u-form-item>
						<linzen-link :content="item.content" :href="item.href" :target='item.target'
							:textStyle="item.textStyle" />
					</u-form-item>
				</view>
			</template>
			<template v-else-if="item.__config__.projectKey==='alert'">
				<view class="u-p-l-20 u-p-r-20 form-item-box">
					<u-form-item>
						<linzen-alert-tips :type="item.type" :title="item.title" :tagIcon='item.tagIcon'
							:showIcon="item.showIcon" :closable="item.closable" :description="item.description"
							:closeText="item.closeText" />
					</u-form-item>
				</view>
			</template>
			<template v-else-if="item.__config__.projectKey==='groupTitle'">
				<linzen-group :content="item.content" :contentPosition="item.contentPosition"
					:helpMessage="item.helpMessage" @groupIcon="clickIcon(item)" />
			</template>
			<template v-else-if="item.__config__.projectKey==='button'">
				<view class="u-p-l-20 u-p-r-20 form-item-box">
					<u-form-item class="">
						<linzen-button :buttonText="item.buttonText" :align="item.align" :type="item.type"
							:disabled="item.disabled" />
					</u-form-item>
				</view>
			</template>
			<template v-else>
				<view class="u-p-l-20 u-p-r-20 form-item-box">
					<u-form-item :prop="item.__vModel__" :label="realLabel" @clickIcon="clickIcon(item)"
						:left-icon='item.__config__.showLabel && item.__config__.tipLabel && item.__config__.label? "question-circle-fill":""'
						:left-icon-style="{'color':'#a0acb7'}">
						<template v-if="item.__config__.projectKey==='uploadFile'">
							<linzen-file v-model="item.__config__.defaultValue" detailed />
						</template>
						<template v-else-if="item.__config__.projectKey==='uploadImg'">
							<view class="preview-image-box">
								<image v-for="(cItem,ci) in item.__config__.defaultValue" :key="ci"
									class="u-preview-image" :src="define.baseURL+cItem.url" mode="aspectFill"
									@tap.stop="doPreviewImage(define.baseURL+cItem.url)"></image>
							</view>
						</template>
						<template v-else-if="item.__config__.projectKey==='colorPicker'">
							<linzen-colorPicker v-model="item.__config__.defaultValue" :colorFormat="item.colorFormat"
								disabled />
						</template>
						<template v-else-if="item.__config__.projectKey==='rate'">
							<linzen-rate v-model="item.__config__.defaultValue" :max="item.count"
								:allowHalf="item['allow-half']" disabled />
						</template>
						<template v-else-if="item.__config__.projectKey==='slider'">
							<linzen-slider v-model="item.__config__.defaultValue" :step="item.step" :min="item.min||0"
								:max="item.max||100" style="width: 100%;" disabled />
						</template>
						<template v-else-if="item.__config__.projectKey==='editor'">
							<mp-html class="editor-box" :content="item.__config__.defaultValue"></mp-html>
						</template>
						<template v-else-if="item.__config__.projectKey==='relationForm'">
							<view class="linzen-detail-text" style="color:rgb(41, 121, 255)"
								@click.native="toDetail(item)">
								{{item.name}}
							</view>
						</template>
						<template v-else-if="item.__config__.projectKey==='popupSelect'">
							<view class="linzen-detail-text">{{ item.name }}</view>
						</template>
						<template v-else-if="item.__config__.projectKey==='barcode'">
							<linzen-barcode :staticText="item.staticText" :width="item.width" :height="item.height"
								:format="item.format" :dataType="item.dataType" :lineColor="item.lineColor"
								:background="item.background" :relationField="item.relationField" :formData="formValue">
							</linzen-barcode>
						</template>
						<template v-else-if="item.__config__.projectKey==='qrcode'">
							<linzen-qrcode :format="item.format" :colorLight="item.colorLight" :colorDark="item.colorDark"
								:width="item.width" :staticText="item.staticText" :dataType="item.dataType"
								:relationField="item.relationField+'_id'" :formData="formValue"></linzen-qrcode>
						</template>
						<template v-else-if="item.__config__.projectKey==='inputNumber'">
							<linzen-number-box v-model="item.__config__.defaultValue" :step='item.step' :max='item.max'
								:min='item.min' :disabled="item.disabled" :isAmountChinese="item.isAmountChinese"
								:thousands="item.thousands" :addonAfter="item.addonAfter"
								:addonBefore="item.addonBefore" :controls="item.controls" :precision="item.precision"
								:detailed="true">
							</linzen-number-box>
						</template>
						<template v-else-if="item.__config__.projectKey==='calculate'&&item.isStorage==0">
							<linzen-calculation :expression='item.expression' :vModel='item.__vModel__'
								:config='item.__config__' :formData='formValue' v-model="item.__config__.defaultValue"
								:precision="item.precision" :isAmountChinese="item.isAmountChinese"
								:thousands="item.thousands" />
						</template>
						<template v-else-if="item.__config__.projectKey==='sign'">
							<linzen-sign v-model="item.__config__.defaultValue" detailed />
						</template>
						<template v-else-if="item.__config__.projectKey==='location'">
							<linzen-location v-model="item.__config__.defaultValue"
								:enableLocationScope='item.enableLocationScope' detailed />
						</template>
						<template v-else-if="item.__config__.projectKey == 'input'">
							<linzen-input v-model="item.__config__.defaultValue" detailed :addonAfter="item.addonAfter"
								:addonBefore="item.addonBefore" :useMask='item.useMask' :maskConfig='item.maskConfig' />
						</template>
						<template v-else>
							<view class="linzen-detail-text" v-if="item.__config__.projectKey==='calculate'">
								<view class="">{{ toThousands(item.__config__.defaultValue, item) }}</view>
								<view class="" v-if="item.isAmountChinese" style="color: #999;">
									{{linzen.getAmountChinese(getValue(item))}}
								</view>
							</view>
							<view class="linzen-detail-text" v-else>{{ getValue(item) }}</view>
						</template>
					</u-form-item>
				</view>
			</template>
		</template>
		<template v-else>
			<template v-if="item.__config__.projectKey==='card'||item.__config__.projectKey==='row'">
				<view class="linzen-card-cap u-line-1" v-if="item.header" :style="{'padding':'20rpx'}"
					@click="clickIcon(item)">
					{{item.header}}
					<u-icon :name="item.__config__.tipLabel && item.__config__.label? 'question-circle-fill':''"
						class="u-m-l-10" color="#a0acb7"></u-icon>
				</view>
				<Item v-for="(child, index) in item.__config__.children" :key="child.__config__.renderKey" :item="child"
					:formConf="formConf" :formValue="formValue" @toDetail="toDetail" @clickIcon='clickIcon' />
			</template>
			<template v-if="item.__config__.projectKey==='table'">
				<view class="linzen-table">
					<view v-for="(column,columnIndex) in item.__config__.defaultValue" :key="columnIndex">
						<view class="linzen-table-item-title u-row-between u-line-1 u-p-l-20 u-p-r-20"
							@click="clickIcon(item)">
							{{item.__config__.label}}({{columnIndex+1}})
							<u-icon :name="item.__config__.tipLabel && item.__config__.label? 'question-circle-fill':''"
								class="u-m-l-10" color="#a0acb7"></u-icon>
						</view>
						<view class="u-p-l-20 u-p-r-20 form-item-box">
							<u-form-item :label="childItem.__config__.showLabel ? childItem.__config__.label : '' "
								:label-width="childItem.__config__.labelWidth ? childItem.__config__.labelWidth * 1.5 : 100*1.5"
								v-for="(childItem,cIndex) in item.__config__.children" :key="cIndex"
								@clickIcon="clickIcon(childItem)"
								:left-icon='childItem.__config__.showLabel && childItem.__config__.tipLabel && childItem.__config__.label? "question-circle-fill":""'
								:left-icon-style="{'color':'#a0acb7'}"
								v-if="!childItem.__config__.noShow&&(!childItem.__config__.visibility|| (Array.isArray(childItem.__config__.visibility) && childItem.__config__.visibility.includes('pc')))">
								<template>
									<template
										v-if="['relationFormAttr','popupAttr'].includes(childItem.__config__.projectKey)">
										<view class="linzen-detail-text" v-if="!childItem.__vModel__">
											{{ column[childItem.relationField.split('_linzenTable_')[0]+'_'+childItem.showField] }}
										</view>
										<view class="linzen-detail-text" v-else>
											{{column[childItem.__vModel__]}}
										</view>
									</template>
									<template v-else-if="childItem.__config__.projectKey==='relationForm'">
										<view class="linzen-detail-text" style="color:rgb(41, 121, 255)"
											@click.native="toTableDetail(childItem,column[childItem.__vModel__+'_id'])">
											{{column[childItem.__vModel__]}}
										</view>
									</template>
									<template v-else-if="childItem.__config__.projectKey==='uploadFile'">
										<linzen-file v-model="column[childItem.__vModel__]" detailed />
									</template>
									<template v-else-if="childItem.__config__.projectKey==='uploadImg'">
										<view class="preview-image-box">
											<image v-for="(cItem,ci) in column[childItem.__vModel__]" :key="ci"
												class="u-preview-image" :src="define.baseURL+cItem.url"
												mode="aspectFill" @tap.stop="doPreviewImage(define.baseURL+cItem.url)">
											</image>
										</view>
									</template>
									<template v-else-if="childItem.__config__.projectKey==='inputNumber'">
										<linzen-number-box v-model="column[childItem.__vModel__]" :step='childItem.step'
											:max='childItem.max' :min='childItem.min' :disabled="childItem.disabled"
											:isAmountChinese="childItem.isAmountChinese"
											:thousands="childItem.thousands" :addonAfter="childItem.addonAfter"
											:addonBefore="childItem.addonBefore" :controls="childItem.controls"
											:precision="childItem.precision" :detailed="true">
										</linzen-number-box>
									</template>
									<template v-else-if="childItem.__config__.projectKey==='rate'">
										<linzen-rate v-model="column[childItem.__vModel__]" :max="childItem.count"
											:allowHalf="childItem['allow-half']" disabled />
									</template>
									<template v-else-if="childItem.__config__.projectKey==='slider'">
										<linzen-slider v-model="column[childItem.__vModel__]" :step="childItem.step"
											:min="childItem.min||0" :max="childItem.max||100" disabled />
									</template>
									<template v-else-if="childItem.__config__.projectKey=='sign'">
										<linzen-sign v-model="column[childItem.__vModel__]" detailed />
									</template>
									<template v-else-if="childItem.__config__.projectKey=='location'">
										<linzen-location v-model="column[childItem.__vModel__]"
											:enableLocationScope='childItem.enableLocationScope' detailed />
									</template>
									<template
										v-else-if="childItem.__config__.projectKey==='calculate'&&childItem.isStorage==0">
										<linzen-calculation :expression='childItem.expression'
											:vModel='childItem.__vModel__' :config='childItem.__config__'
											:formData='formValue' v-model="column[childItem.__vModel__]"
											:precision="childItem.precision"
											:isAmountChinese="childItem.isAmountChinese"
											:thousands="childItem.thousands" :rowIndex="columnIndex" />
									</template>
									<template class="item-cell-content"
										v-else-if="childItem.__config__.projectKey == 'input'">
										<linzen-input v-model="column[childItem.__vModel__]" detailed
											:addonAfter="childItem.addonAfter" :addonBefore="childItem.addonBefore"
											:useMask='childItem.useMask' :maskConfig='childItem.maskConfig' />
									</template>
									<template v-else>
										<view class="linzen-detail-text"
											v-if="childItem.__config__.projectKey==='calculate'">
											<view class="">{{toThousands(column[childItem.__vModel__],childItem)}}
											</view>
											<view class="" v-if="childItem.isAmountChinese" style="color: #999;">
												{{linzen.getAmountChinese(column[childItem.__vModel__])}}
											</view>
										</view>
										<view class="linzen-detail-text" v-else>{{column[childItem.__vModel__]}}
										</view>
									</template>
								</template>
							</u-form-item>
						</view>
					</view>
					<view class="linzen-table-item" v-if="item.showSummary && summaryField.length">
						<view class="linzen-table-item-title u-flex u-row-between">
							<text class="linzen-table-item-title-num">{{item.__config__.label}}合计</text>
						</view>
						<view class=" u-p-l-20 u-p-r-20 form-item-box">
							<u-form-item v-for="(item,index) in summaryField" :label="item.__config__.label"
								:key="item.__vModel__">
								<u-input input-align='right' v-model="item.value" disabled placeholder=""></u-input>
							</u-form-item>
						</view>
					</view>
				</view>
			</template>
			<template v-if="item.__config__.projectKey==='editor'">
				<mp-html class="editor-box" :content="item.__config__.defaultValue"></mp-html>
			</template>
			<template v-if="item.__config__.projectKey==='tab'">
				<u-tabs is-scroll :list="item.__config__.children" name="title" :current="tabCurrent"
					@change="onTabChange">
				</u-tabs>
				<view v-for="(pane,i) in item.__config__.children" :key='i'>
					<view v-show="i == tabCurrent">
						<Item v-for="(childItem, childIndex) in pane.__config__.children" :key="childIndex"
							:item="childItem" :formConf="formConf" :formValue="formValue" @toDetail="toDetail"
							@clickIcon='clickIcon' />
					</view>
				</view>
			</template>
			<template v-if="item.__config__.projectKey==='collapse'">
				<u-collapse :head-style="{'padding-left':'20rpx'}" :accordion="item.accordion" ref="collapseRef">
					<u-collapse-item :title="pane.title" v-for="(pane, i) in item.__config__.children" :key="i"
						@change="onCollapseChange" :open="item.__config__.active.indexOf(pane.name)>-1">
						<Item v-for="(child, j) in pane.__config__.children" :key="child.__config__.renderKey"
							@collapse-change="onCollapseChange" :item="child" :formConf="formConf"
							:formValue="formValue" @toDetail="toDetail" @clickIcon='clickIcon' />
					</u-collapse-item>
				</u-collapse>
			</template>
		</template>
	</view>
</template>
<script>
	import {
		getDownloadUrl
	} from '@/api/common'
	import Item from './Item'
	const specialList = ['link', 'editor', 'button', 'alert']
	export default {
		name: 'Item',
		props: {
			item: {
				type: Object,
				required: true
			},
			formConf: {
				type: Object,
				required: true
			},
			formValue: {
				type: Object,
			},
		},
		components: {
			Item
		},
		data() {
			return {
				tabCurrent: 0,
				tableData: [],
				summaryField: []
			}
		},
		computed: {
			label() {
				return this.item.__config__.showLabel && specialList.indexOf(this.item.__config__.projectKey) < 0 ? this.item
					.__config__.label : ''
			},
			realLabel() {
				return this.label ? (this.label + (this.formConf.labelSuffix || '')) : ''
			},
		},
		created() {
			if (this.item.__config__.projectKey === 'table') {
				const newVal = this.item.__config__.defaultValue
				let summaryField = this.item.summaryField || []
				this.summaryField = []
				this.tableData = this.item.__config__.children || []
				for (let i = 0; i < summaryField.length; i++) {
					for (let o = 0; o < this.tableData.length; o++) {
						if (this.tableData[o].__vModel__ === summaryField[i] && !this.tableData[o].__config__
							.noShow) {
							this.summaryField.push({
								value: '0.00',
								...this.tableData[o]
							})
						}
					}
				}
				this.$nextTick(() => {
					this.getTableSummaries(newVal, this.item)
				})
				//if (!newVal.length) this.item.__config__.defaultValue.push({})
			}
			if (this.item.__config__.projectKey === 'tab') {
				const list = this.item.__config__.children
				for (var i = 0; i < list.length; i++) {
					if (this.item.__config__.active == list[i].name) {
						this.tabCurrent = i
						break
					}
				}
			}
		},
		mounted() {
			uni.$on('initCollapse', () => {
				//初始化折叠面板高度高度
				this.$refs.collapseRef && this.$refs.collapseRef.init()
			})
			//初始化折叠面板高度高度
			this.$refs.collapseRef && this.$refs.collapseRef.init()
		},
		methods: {
			toThousands(val, column) {
				if (val) {
					let valList = val.toString().split('.')
					let num = Number(valList[0])
					let newVal = column.thousands ? num.toLocaleString() : num
					return valList[1] ? newVal + '.' + valList[1] : newVal
				} else {
					return val
				}
			},
			getTableSummaries(newVal, config) {
				for (let i = 0; i < this.summaryField.length; i++) {
					let val = 0
					for (let j = 0; j < newVal.length; j++) {
						if (newVal[j][this.summaryField[i].__vModel__]) {
							let data = isNaN(newVal[j][this.summaryField[i].__vModel__]) ? 0 :
								Number(newVal[j][this.summaryField[i].__vModel__])
							val += data
							this.summaryField[i].value = this.summaryField[i].thousands ? Number(val).toLocaleString(
								'zh', {
									maximumFractionDigits: '2',
									minimumFractionDigits: '2'
								}) : val.toFixed(2)
						}
					}
				}
			},
			clickIcon(e) {
				if (!e.__config__.tipLabel && !e.helpMessage && !e.tipLabel) return
				this.$emit('clickIcon', e)
			},
			hasBarcodeField(data) {
				const {
					__config__: config
				} = data;
				if (config.projectKey === 'barcode' || config.projectKey === 'qrcode') {
					return true;
				}
				if (config.children) {
					return config.children.some(child => this.hasBarcodeField(child));
				}
				return false;
			},
			onTabChange(index) {
				const hasBarcode = this.hasBarcodeField(this.item.__config__.children[index]);
				if (hasBarcode) uni.$emit('upDateCode')
				if (this.tabCurrent === index) return
				this.tabCurrent = index;
				this.$emit('tab-change', this.item, index)
				//初始化折叠面板高度高度
				uni.$emit('initCollapse')
			},
			onCollapseChange() {
				this.$emit('collapse-change')
				uni.$emit('initCollapse')
			},
			doPreviewImage(url) {
				const images = this.item.__config__.defaultValue.map(item => this.define.baseURL + item.url);
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
			toDetail(item) {
				this.$emit('toDetail', item)
			},
			toTableDetail(item, value) {
				item.__config__.defaultValue = value
				this.$emit('toDetail', item)
			},
			getValue(item) {
				if (Array.isArray(item.__config__.defaultValue)) {
					if (['timeRange', 'dateRange'].includes(item.__config__.projectKey)) {
						return item.__config__.defaultValue.join('')
					}
					return item.__config__.defaultValue.join()
				}
				if (item.__config__.projectKey === 'switch') return item.__config__.defaultValue
				if (item.__config__.projectKey === 'calculate') return Number(item.__config__.defaultValue)
				return item.__config__.defaultValue === 0 ? 0 : ''
			},
		}
	}
</script>