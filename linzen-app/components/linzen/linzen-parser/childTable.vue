<template>
	<view class="linzen-table">
		<view class="linzen-table-title u-line-1" @click="clickIcon(config)">
			{{config.__config__.label}}
			<u-icon v-if="config.__config__.tipLabel" :name="'question-circle-fill'" class="u-m-l-10"
				color="#a0acb7"></u-icon>
		</view>
		<view class="linzen-table-item" v-for="(item,i) in tableFormData" :key="i">
			<view class="linzen-table-item-title u-flex u-row-between u-p-r-20">
				<view class="linzen-table-item-title-num">({{i+1}})</view>
				<template v-if="!disabled">
					<template v-for="(it, index) in config.columnBtnsList">
						<view v-if="it.show&&!disabled" :key="index"
							:class="it.value=='remove'?'linzen-table-delete-btn':'linzen-table-copy-btn'"
							@click="columnBtnsHandel(it, i)">
							{{it.label}}
						</view>
					</template>
				</template>
				<!-- <view class="linzen-table-item-title-action" v-if="config.showDeleteBtn && !disabled" @click="delItem(i)">
					删除
				</view> -->
			</view>
			<view class="u-p-l-20 u-p-r-20 form-item-box" v-for="(childItem,cIndex) in item" :key="cIndex"
				v-if="!childItem.__config__.noShow && childItem.__config__.isVisibility&& childItem.__config__.projectKey!=='colorPicker'">
				<u-form-item :label="childItem.__config__.showLabel ? childItem.__config__.label : '' "
					:required="childItem.__config__.required"
					:left-icon='childItem.__config__.showLabel && childItem.__config__.tipLabel && childItem.__config__.label? "question-circle-fill":""'
					@clickIcon="clickIcon(childItem)" :left-icon-style="{'color':'#a8aaaf'}"
					:label-width="childItem.__config__.labelWidth ? childItem.__config__.labelWidth * 1.5 : 100*1.5">
					<block v-if="childItem.__config__.projectKey==='input'">
						<linzen-input :showPassword="childItem['show-password']" v-model="tableFormData[i][cIndex].value"
							:placeholder="childItem.placeholder"
							:maxlength="childItem.maxlength?childItem.maxlength:140" :disabled="childItem.disabled"
							@input="onChange($event,childItem,i)" :clearable='childItem.clearable'
							:addonAfter="childItem.addonAfter" :addonBefore="childItem.addonBefore"
							:useScan='childItem.useScan' />
					</block>
					<block v-if="childItem.__config__.projectKey==='calculate'">
						<linzen-calculation :expression='childItem.expression' :vModel='childItem.__vModel__'
							:config='childItem.__config__' :formData='formData' v-model="tableFormData[i][cIndex].value"
							:rowIndex="i" :precision="childItem.precision" :isAmountChinese="childItem.isAmountChinese"
							:thousands="childItem.thousands" />
					</block>
					<block v-if="childItem.__config__.projectKey==='textarea'">
						<u-input input-align='right' v-model="tableFormData[i][cIndex].value" type="textarea"
							:placeholder="childItem.placeholder"
							:maxlength="childItem.maxlength?childItem.maxlength:140"
							:disabled="disabled||childItem.disabled" @input="onChange($event,childItem,i)" />
					</block>
					<block v-if="childItem.__config__.projectKey==='inputNumber'">
						<linzen-number-box v-model="tableFormData[i][cIndex].value" :step='childItem.step'
							:max='childItem.max' :min='childItem.min' :disabled="childItem.disabled"
							@change="change(arguments,childItem,i)" :isAmountChinese="childItem.isAmountChinese"
							:thousands="childItem.thousands" :addonAfter="childItem.addonAfter"
							:addonBefore="childItem.addonBefore" :controls="childItem.controls"
							:precision="childItem.precision">
						</linzen-number-box>
					</block>
					<block v-if="childItem.__config__.projectKey==='switch'">
						<linzen-switch v-model="tableFormData[i][cIndex].value" :disabled="disabled||childItem.disabled"
							@change="onChange($event,childItem,i)">
						</linzen-switch>
					</block>
					<block v-if="childItem.__config__.projectKey==='radio'">
						<linzen-radio v-model="tableFormData[i][cIndex].value" :options="childItem.options"
							:props="childItem.props" @change=" itemChange('radio')" :disabled="childItem.disabled"
							:direction='childItem.direction'>
						</linzen-radio>
					</block>
					<block v-if="childItem.__config__.projectKey==='checkbox'">
						<linzen-checkbox v-model="tableFormData[i][cIndex].value" :options="childItem.options"
							:props="childItem.props" :disabled="disabled||childItem.disabled"
							@change="change(arguments,childItem,i)" :direction='childItem.direction'>
						</linzen-checkbox>
					</block>
					<block v-if="childItem.__config__.projectKey==='select'">
						<linzen-select v-model="tableFormData[i][cIndex].value" :placeholder="childItem.placeholder"
							:options="childItem.options" :props="childItem.props" :multiple="childItem.multiple"
							:disabled="disabled||childItem.disabled" @change="change(arguments,childItem,i)">
						</linzen-select>
					</block>
					<block v-if="childItem.__config__.projectKey==='cascader'">
						<linzen-cascader v-model="tableFormData[i][cIndex].value" :placeholder="childItem.placeholder"
							:options="childItem.options" :props="childItem.props" :multiple="childItem.multiple"
							:disabled="disabled||childItem.disabled" @change="change(arguments,childItem,i)">
						</linzen-cascader>
					</block>
					<block v-if="childItem.__config__.projectKey==='groupSelect'">
						<linzen-group-select :vModel='childItem.__vModel__' v-model="tableFormData[i][cIndex].value"
							:multiple="childItem.multiple" :disabled="childItem.disabled"
							:selectType="childItem.selectType" :ableIds="childItem.ableIds"
							:placeholder="childItem.placeholder" @change="change(arguments,childItem,i)" />
					</block>
					<!-- 下拉补全 -->
					<block v-if="childItem.__config__.projectKey==='autoComplete'">
						<linzen-auto-complete :disabled="childItem.disabled" :interfaceName="childItem.interfaceName"
							:placeholder="childItem.placeholder" :interfaceId="childItem.interfaceId"
							:total="childItem.total" v-model="tableFormData[i][cIndex].value"
							:templateJson="childItem.templateJson" :formData='formData'
							:relationField="childItem.relationField" :clearable='childItem.clearable'
							:propsValue="childItem.propsValue" :rowIndex="i"></linzen-auto-complete>
					</block>
					<block v-if="childItem.__config__.projectKey==='roleSelect'">
						<linzen-role-select :vModel='childItem.__vModel__' v-model="tableFormData[i][cIndex].value"
							:multiple="childItem.multiple" :disabled="childItem.disabled"
							:selectType="childItem.selectType" :ableIds="childItem.ableIds"
							:placeholder="childItem.placeholder" @change="change(arguments,childItem,i)" />
					</block>
					<block v-if="childItem.__config__.projectKey==='timePicker'">
						<linzen-date-time type="time" v-model="tableFormData[i][cIndex].value"
							:placeholder="childItem.placeholder" :disabled="disabled||childItem.disabled"
							@change="change(arguments,childItem,i)" :format="childItem.format"
							:startTime="childItem.startTime" :endTime='childItem.endTime'>
						</linzen-date-time>
					</block>
					<block v-if="childItem.__config__.projectKey==='datePicker'">
						<linzen-date-time :type="childItem.type||'date'" v-model="tableFormData[i][cIndex].value"
							:placeholder="childItem.placeholder" :disabled="disabled||childItem.disabled"
							@change="change(arguments,childItem,i)" :format="childItem.format"
							:startTime="childItem.startTime" :endTime='childItem.endTime'>
						</linzen-date-time>
					</block>
					<block v-if="childItem.__config__.projectKey==='uploadImg'">
						<linzen-upload v-model="tableFormData[i][cIndex].value" :disabled="disabled||childItem.disabled"
							:fileSize="childItem.fileSize" :tipText="childItem.tipText" :folder="childItem.folder"
							:pathType="childItem.pathType" :isAccount="childItem.isAccount"
							@change="change(arguments,childItem,i)">
						</linzen-upload>
					</block>
					<block v-if="childItem.__config__.projectKey==='uploadFile'">
						<linzen-file v-model="tableFormData[i][cIndex].value" :disabled="disabled||childItem.disabled"
							:limit="childItem.limit?childItem.limit:9" :sizeUnit="childItem.sizeUnit"
							:fileSize="childItem.fileSize" :accept="childItem.accept" :folder="childItem.folder"
							:pathType="childItem.pathType" :isAccount="childItem.isAccount" :tipText="childItem.tipText"
							@change="change(arguments,childItem,i)" />
					</block>
					<block v-if="childItem.__config__.projectKey==='rate'">
						<linzen-rate v-model="tableFormData[i][cIndex].value" :max="childItem.count"
							:allowHalf="childItem.allowHalf" :disabled="childItem.disabled"
							@change="change(arguments,childItem,i)"></linzen-rate>
					</block>
					<block v-if="childItem.__config__.projectKey==='slider'">
						<linzen-slider v-model="tableFormData[i][cIndex].value" :step="childItem.step"
							:min="childItem.min||0" :max="childItem.max||100" style="width: 100%;"
							:disabled="disabled||childItem.disabled" @change="change(arguments,childItem,i)" />
					</block>
					<block v-if="childItem.__config__.projectKey==='relationFormAttr'">
						<linzen-relation-attr v-model="tableFormData[i][cIndex].value" :showField="childItem.showField"
							:relationField="childItem.relationField+'_linzenRelation_'+i" type="relationFormAttr"
							:isStorage='childItem.isStorage' @change="change(arguments,childItem,i)">
						</linzen-relation-attr>
					</block>
					<block v-if="childItem.__config__.projectKey==='popupAttr'">
						<linzen-relation-attr v-model="tableFormData[i][cIndex].value" :showField="childItem.showField"
							:relationField="childItem.relationField+'_linzenRelation_'+i" type="popupAttr"
							:isStorage='childItem.isStorage' @change="change(arguments,childItem,i)">
						</linzen-relation-attr>
					</block>
					<block v-if="childItem.__config__.projectKey==='relationForm'">
						<linzen-relation-select type="relation" v-model="tableFormData[i][cIndex].value"
							:placeholder="childItem.placeholder" :disabled="childItem.disabled"
							:modelId="childItem.modelId" :columnOptions="childItem.columnOptions"
							:relationField="childItem.relationField" :hasPage="childItem.hasPage"
							:pageSize="childItem.pageSize"
							:vModel="childItem.__config__.tableName ? childItem.__vModel__ + '_linzenTable_' + childItem.__config__.tableName + (childItem
					                            .__config__.isSubTable ? '0' : '1')+'_linzenRelation_'+i : childItem.__vModel__+'_linzenRelation_'+i"
							@change="change(arguments,childItem,i)">
						</linzen-relation-select>
					</block>
					<block v-if="childItem.__config__.projectKey === 'popupSelect'">
						<linzen-popup-select type="popup" v-model="tableFormData[i][cIndex].value"
							:placeholder="childItem.placeholder" :disabled="childItem.disabled" :formData="formData"
							:templateJson="childItem.templateJson" :rowIndex="i" :interfaceId="childItem.interfaceId"
							:columnOptions="childItem.columnOptions" :relationField="childItem.relationField"
							:propsValue="childItem.propsValue" :hasPage="childItem.hasPage"
							:pageSize="childItem.pageSize"
							:vModel="childItem.__config__.tableName ? childItem.__vModel__ + '_linzenTable_' + childItem.__config__.tableName + (childItem
					                            .__config__.isSubTable ? '0' : '1')+'_linzenRelation_'+i : childItem.__vModel__+'_linzenRelation_'+i"
							@change="change(arguments,childItem,i)">
						</linzen-popup-select>
					</block>
					<block v-if="childItem.__config__.projectKey === 'popupTableSelect'">
						<linzen-table-select v-model="tableFormData[i][cIndex].value" :placeholder="childItem.placeholder"
							:disabled="childItem.disabled" :interfaceId="childItem.interfaceId" :formData="formData"
							:templateJson="childItem.templateJson" :rowIndex="i"
							:columnOptions="childItem.columnOptions" :relationField="childItem.relationField"
							:propsValue="childItem.propsValue" :hasPage="childItem.hasPage"
							:pageSize="childItem.pageSize"
							:vModel="childItem.__config__.tableName ? childItem.__vModel__ + '_linzenTable_' + childItem.__config__.tableName + (childItem.__config__.isSubTable ? '0' : '1')+'_linzenRelation_'+i : childItem.__vModel__+'_linzenRelation_'+i"
							:multiple="childItem.multiple" :filterable="childItem.filterable"
							@change="change(arguments,childItem,i)"></linzen-table-select>
					</block>
					<block v-if="childItem.__config__.projectKey==='organizeSelect'">
						<linzen-com-select v-model="tableFormData[i][cIndex].value" :multiple="childItem.multiple"
							:placeholder="childItem.placeholder" :disabled="disabled||childItem.disabled"
							:selectType="childItem.selectType" :ableIds="childItem.ableIds"
							@change="change(arguments,childItem,i)">
						</linzen-com-select>
					</block>
					<block v-if="childItem.__config__.projectKey==='depSelect'">
						<linzen-postordep-select type="department" v-model="tableFormData[i][cIndex].value"
							:multiple="childItem.multiple" :placeholder="childItem.placeholder"
							:disabled="childItem.disabled" :ableIds="childItem.ableIds"
							:selectType="childItem.selectType" @change="change(arguments,childItem,i)">
						</linzen-postordep-select>
					</block>
					<block v-if="childItem.__config__.projectKey==='posSelect'">
						<linzen-postordep-select type="position" v-model="tableFormData[i][cIndex].value"
							:multiple="childItem.multiple" :placeholder="childItem.placeholder"
							:disabled="childItem.disabled" :ableIds="childItem.ableIds"
							:selectType="childItem.selectType" @change="change(arguments,childItem,i)">
						</linzen-postordep-select>
					</block>
					<block v-if="childItem.__config__.projectKey==='userSelect'">
						<linzen-user-select v-model="tableFormData[i][cIndex].value" :multiple="childItem.multiple"
							:placeholder="childItem.placeholder" :disabled="childItem.disabled"
							:selectType="childItem.selectType" :ableIds="childItem.ableIds"
							:ableRelationIds="childItem.ableRelationIds" :clearable="childItem.clearable"
							@change="change(arguments,childItem,i)">
						</linzen-user-select>
					</block>
					<!-- 用户组件 -->
					<block v-if="childItem.__config__.projectKey==='usersSelect'">
						<linzen-user-choice v-model="tableFormData[i][cIndex].value" :multiple="childItem.multiple"
							:placeholder="childItem.placeholder" :disabled="childItem.disabled"
							:selectType="childItem.selectType" :ableIds="childItem.ableIds"
							:clearable="childItem.clearable" @change="change(arguments,childItem,i)">
						</linzen-user-choice>
					</block>
					<!-- 签名 -->
					<block v-if="childItem.__config__.projectKey==='sign'">
						<linzen-sign v-model="tableFormData[i][cIndex].value" :disabled="childItem.disabled"
							@change="change(arguments,childItem,i)" />
					</block>
					<!-- 定位 -->
					<block v-if="childItem.__config__.projectKey==='location'">
						<linzen-location v-model="tableFormData[i][cIndex].value" :autoLocation="childItem.autoLocation"
							:adjustmentScope="childItem.adjustmentScope"
							:enableLocationScope="childItem.enableLocationScope"
							:enableDesktopLocation="childItem.enableDesktopLocation"
							:locationScope="childItem.locationScope" :disabled="childItem.disabled"
							:clearable='item.clearable' @change="change(arguments,childItem,i)">
						</linzen-location>
					</block>
					<block v-if="childItem.__config__.projectKey==='treeSelect'">
						<linzen-tree-select v-model="tableFormData[i][cIndex].value" :options="childItem.options"
							:props="childItem.props" :multiple="childItem.multiple" :placeholder="childItem.placeholder"
							:disabled="disabled||childItem.disabled" @change="change(arguments,childItem,i)"
							:filterable="childItem.filterable">
						</linzen-tree-select>
					</block>
					<block v-if="childItem.__config__.projectKey==='areaSelect'">
						<linzen-city-select v-model="tableFormData[i][cIndex].value" :placeholder="childItem.placeholder"
							:level="childItem.level" :disabled="disabled||childItem.disabled"
							:multiple="childItem.multiple" @change="change(arguments,childItem,i)">
						</linzen-city-select>
					</block>
					<block v-if="childItem.__config__.projectKey==='billRule'">
						<u-input input-align='right' v-model="tableFormData[i][cIndex].value" placeholder="系统自动生成"
							disabled @input="onChange($event,childItem,i)"></u-input>
					</block>
				</u-form-item>
			</view>

		</view>
		<!-- <view class="linzen-table-addBtn" v-if="config.showAddBtn && !disabled && tableFormData.length>0"
			@click="addItem()">
			<u-icon name="plus" color="#2979ff"></u-icon>{{config.actionText}}
		</view>
		<view class="linzen-table-addBtn" v-if="config.showAddBtn && tableFormData.length<=0 && !disabled"
			@click="addItem()">
			<u-icon name="plus" color="#2979ff"></u-icon>{{config.actionText}}
			{{config.__config__.label}}
		</view> -->
		<view class="linzen-table-footer-btn" v-if="!disabled">
			<template v-for="item in config.footerBtnsList">
				<view v-if="item.show&&item.value!='batchRemove'" class="linzen-table-btn"
					:class="'linzen-table-'+item.btnType+'-btn'" @click="footerBtnsHandle(item)">
					<text class="linzen-table-btn-icon" :class="item.btnIcon" />
					<text class="linzen-table-btn-text">{{item.label}}</text>
				</view>
			</template>
		</view>
		<view class="linzen-table-item" v-if="config.showSummary && summaryField.length">
			<view class="linzen-table-item-title u-flex u-row-between">
				<text class="linzen-table-item-title-num">{{config.__config__.label}}合计</text>
			</view>
			<view class=" u-p-l-20 u-p-r-20 form-item-box">
				<u-form-item v-for="(item,index) in summaryField" :label="item.__config__.label" :key="item.__vModel__">
					<u-input input-align='right' v-model="item.value" disabled placeholder=""></u-input>
				</u-form-item>
			</view>
		</view>
		<u-modal v-model="show" :content="content" width='70%' border-radius="16" :content-style="contentStyle"
			:titleStyle="titleStyle" :confirm-style="confirmStyle" :title="title" confirm-text="确定">
		</u-modal>
	</view>
</template>

<script>
	import {
		getDataInterfaceRes
	} from '@/api/common'
	const dyOptionsList = ['radio', 'checkbox', 'select', 'cascader', 'treeSelect']
	export default {
		name: 'linzen-table',
		inject: ["parameter", "relations"],
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			config: {
				type: Object,
				default: () => {}
			},
			formData: {
				type: Object,
				required: true
			},
			value: {
				type: [Array, String],
				default: () => ([])
			}
		},
		data() {
			return {
				dataInterfaceInfo: [],
				activeRowIndex: 0,
				tableData: [],
				tableFormData: [],
				summaryField: [],
				isIgnore: false,
				show: false,
				addType: 0,
				addTableConf: {},
				tableVmodel: '',
				childRelations: {},
				userInfo: {},
				content: '',
				contentStyle: {
					fontSize: '28rpx',
					padding: '20rpx',
					lineHeight: '44rpx',
					textAlign: 'left'
				},
				titleStyle: {
					padding: '20rpx'
				},
				confirmStyle: {
					height: '80rpx',
					lineHeight: '80rpx',
				},
				title: '提示',
			}
		},
		watch: {
			tableFormData: {
				handler(val, oldVal) {
					const data = this.submit(true)
					this.$emit('input', data)
					this.getTableSummaries()
				},
				deep: true
			},

		},
		computed: {
			disabled() {
				return this.config.disabled
			}
		},
		created() {
			this.init()
		},
		methods: {
			init() {
				this.userInfo = uni.getStorageSync('userInfo') || {}
				this.tableData = this.config.__config__.children || []
				this.handleSummary()
				this.buildOptions()
				this.handleListen()
				this.buildRelation()
			},
			handleSummary() {
				this.summaryField = []
				let summaryField = this.config.summaryField || []
				for (let i = 0; i < summaryField.length; i++) {
					for (let o = 0; o < this.tableData.length; o++) {
						const item = this.tableData[o]
						if (item.__vModel__ === summaryField[i] && !item.__config__.noShow) {
							this.summaryField.push({
								value: '0.00',
								...item
							})
						}
					}
				}
			},
			handleListen() {
				uni.$on('linkPageConfirm', (subVal, Vmodel) => {
					if (this.config.__vModel__ === Vmodel) {
						subVal.forEach(t => this.tableFormData.push(this.getEmptyItem(t)))
						setTimeout(() => {
							uni.$emit('initCollapse')
						}, 50)
					}

				})
				uni.$on('handleRelation', this.handleRelationForParent)
			},
			buildOptions() {
				for (let i = 0; i < this.tableData.length; i++) {
					const config = this.tableData[i].__config__
					if (dyOptionsList.indexOf(config.projectKey) > -1) {
						if (config.dataType === 'dictionary' && config.dictionaryType) {
							this.$store.dispatch('base/getDicDataSelector', config.dictionaryType).then(res => {
								this.tableData[i].options = res || []
								uni.$emit('initCollapse')
							})
						}
						if (config.dataType === 'dynamic' && config.propsUrl) {
							let query = {
								paramList: this.getDefaultParamList(config.templateJson, this.formData)
							}
							const matchInfo = JSON.stringify({
								id: config.propsUrl,
								query
							});
							const item = {
								matchInfo,
								rowIndex: -1,
								colIndex: i
							};
							this.dataInterfaceInfo.push(item);
							getDataInterfaceRes(config.propsUrl, query).then(res => {
								this.tableData[i].options = Array.isArray(res.data) ? res.data : []
							})
						}
					}
				}
				this.initData()
			},
			initData() {
				if (Array.isArray(this.value) && this.value.length) {
					this.value.forEach((t, index) => {
						this.tableFormData.push(this.getEmptyItem(t))
						this.buildAttr(index, t)
					})
					this.initRelationData()
					this.$nextTick(() => {
						uni.$emit('initCollapse')
					})
				}
			},
			buildAttr(rowIndex, val) {
				let row = this.tableFormData[rowIndex];
				for (let i = 0; i < row.length; i++) {
					let item = row[i];
					const config = item.__config__
					if (dyOptionsList.indexOf(config.projectKey) > -1) {
						if (config.dataType === 'dictionary' && config.dictionaryType) {
							this.$store.dispatch('base/getDicDataSelector', config.dictionaryType).then(res => {
								item.options = res || []
								uni.$emit('initCollapse')
							})
						}
						if (config.dataType === 'dynamic' && config.propsUrl) {
							this.handleRelation(item, rowIndex)
							if (item.options && item.options.length && (!config.templateJson || !config.templateJson
									.length || !this.hasTemplateJsonRelation(config.templateJson))) continue
							let query = {
								paramList: this.getParamList(config.templateJson, this.formData, rowIndex)
							}
							const matchInfo = JSON.stringify({
								id: config.propsUrl,
								query
							});
							const itemInfo = {
								matchInfo,
								rowIndex,
								colIndex: i
							};
							const infoIndex = this.dataInterfaceInfo.findIndex(o => o.matchInfo === matchInfo);
							let useCacheOptions = false;
							if (infoIndex === -1) {
								this.dataInterfaceInfo.push(itemInfo);
							} else {
								const cacheOptions = this.getCacheOptions(infoIndex);
								if (cacheOptions.length) {
									item.options = cacheOptions;
									useCacheOptions = true;
									uni.$emit('initCollapse')
								}
							}
							if (!useCacheOptions) {
								getDataInterfaceRes(config.propsUrl, query).then(res => {
									item.options = Array.isArray(res.data) ? res.data : []
									uni.$emit('initCollapse')
								})
							}
						}
					}

				}
			},
			buildRelation() {
				for (let key in this.relations) {
					if (key.includes('-')) {
						let tableVModel = key.split('-')[0]
						if (tableVModel === this.config.__vModel__) {
							let newKey = key.split('-')[1]
							this.childRelations[newKey] = this.relations[key]
						}
					}
				}
			},
			getTableSummaries() {
				if (!this.config.showSummary) return
				if (!this.tableFormData.length) return this.handleSummary()
				const list = this.tableFormData.map((row, i) => {
					return row.reduce((p, c) => {
						p[c.__vModel__] = c.value
						return p
					}, {})
				})
				for (let i = 0; i < this.summaryField.length; i++) {
					let val = 0
					for (let j = 0; j < list.length; j++) {
						const value = list[j][this.summaryField[i].__vModel__]
						if (value) {
							let data = isNaN(value) ? 0 : Number(value)
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
			handleRelationForParent(e, defaultValue, st) {
				if (!this.tableFormData.length) return
				for (let i = 0; i < this.tableFormData.length; i++) {
					let row = this.tableFormData[i];
					for (let j = 0; j < row.length; j++) {
						let item = row[j];
						const vModel = item.projectKey === 'popupSelect' ? item.__vModel__.substring(0, item.__vModel__
							.indexOf('_linzenRelation_')) : item.__vModel__
						if (e.__vModel__ === vModel) {
							if (e.opType === 'setOptions') {
								item.options = []
								let query = {
									paramList: this.getParamList(e.__config__.templateJson, this.formData, i)
								}
								getDataInterfaceRes(e.__config__.propsUrl, query).then(res => {
									item.options = Array.isArray(res.data) ? res.data : []
									uni.$emit('initCollapse')
								})
							}
							if (e.opType === 'setUserOptions') {
								if (e.relationField.includes('-')) {
									const [attr1, attr2] = e.relationField.split('-')
									this.$nextTick(() => {
										let value = this.formData[attr1][i][attr2] || []
										this.$set(this.tableFormData[i][j], 'ableRelationIds', Array.isArray(
											value) ? value : [value])
									})
								} else {
									let value = this.formData[e.relationField] || []
									this.$set(this.tableFormData[i][j], 'ableRelationIds', Array.isArray(value) ? value : [
										value
									])
								}
							}
							this.$nextTick(() => {
								if (e.opType === 'setDate') {
									let startTime = 0
									let endTime = 0
									if (e.__config__.startRelationField && e.__config__.startTimeType == 2) {
										if (e.__config__.startRelationField.includes('-')) {
											const [attr0, attr5] = e.__config__.startRelationField.split('-')
											startTime = this.formData[attr0][i][attr5] || 0
										} else {
											startTime = this.formData[e.__config__.startRelationField] || 0
										}
									} else {
										startTime = e.startTime
									}
									if (e.__config__.endRelationField && e.__config__.endTimeType == 2) {
										if (e.__config__.endRelationField.includes('-')) {
											const [attr3, attr4] = e.__config__.endRelationField.split('-')
											endTime = this.formData[attr3][i][attr4] || 0
										} else {
											endTime = this.formData[e.__config__.endRelationField] || 0
										}
									} else {
										endTime = e.endTime
									}
									item.startTime = startTime
									item.endTime = endTime
								}
								if (e.opType === 'setTime') {
									let format = e.format
									let startTime = ''
									let endTime = ''
									if (e.__config__.startRelationField && e.__config__.startTimeType == 2) {
										if (e.__config__.startRelationField.includes('-')) {
											const [attr0, attr5] = e.__config__.startRelationField.split('-')
											startTime = this.formData[attr0][i][attr5] || '00:00:00'
										} else {
											startTime = this.formData[e.__config__.startRelationField] ||
												'00:00:00'
										}
										startTime = startTime && startTime.split(':').length == 3 ? startTime :
											startTime + ':00'
									} else {
										startTime = e.startTime
									}
									if (e.__config__.endRelationField && e.__config__.endTimeType == 2) {
										if (e.__config__.endRelationField.includes('-')) {
											const [attr3, attr4] = e.__config__.endRelationField.split('-')
											endTime = this.formData[attr3][i][attr4] || '23:59:59'
										} else {
											endTime = this.formData[e.__config__.endRelationField] || '23:59:59'
										}
										endTime = endTime && endTime.split(':').length == 3 ? endTime : endTime +
											':00'
									} else {
										endTime = e.endTime
									}
									item.startTime = startTime
									item.endTime = endTime
								}
							})
							if (item.value != defaultValue) {
								if (st || !item.value) item.value = defaultValue
							}
						}
					}
				}
			},
			handleRelation(data, rowIndex) {
				const currRelations = this.childRelations
				for (let key in currRelations) {
					if (key === data.__vModel__) {
						for (let i = 0; i < currRelations[key].length; i++) {
							const e = currRelations[key][i];
							const config = e.__config__
							const projectKey = config.projectKey
							let defaultValue = ''
							if (['checkbox', 'cascader'].includes(projectKey) || (['select', 'treeSelect',
									'popupSelect',
									'popupTableSelect', 'userSelect'
								].includes(projectKey) && e.multiple)) {
								defaultValue = []
							}
							let row = this.tableFormData[rowIndex];
							for (let j = 0; j < row.length; j++) {
								let item = row[j];
								const vModel = item.projectKey === 'popupSelect' ? item.__vModel__.substring(0, item
									.__vModel__.indexOf('_linzenRelation_')) : item.__vModel__
								if (e.__vModel__ === vModel) {
									if (e.opType === 'setOptions') {
										item.options = []
										let query = {
											paramList: this.getParamList(config.templateJson, this.formData, rowIndex)
										}
										getDataInterfaceRes(config.propsUrl, query).then(res => {
											item.options = Array.isArray(res.data) ? res.data : []
											uni.$emit('initCollapse')
										})
									}
									if (e.opType === 'setUserOptions') {
										let value = this.getFieldVal(e.relationField, rowIndex) || []
										item.ableRelationIds = Array.isArray(value) ? value : [value]
									}
									if (e.opType === 'setDate') {
										let startTime = 0
										let endTime = 0
										if (config.startRelationField && config.startTimeType == 2) {
											startTime = this.getFieldVal(config.startRelationField, rowIndex) || 0
										} else {
											startTime = e.startTime
										}
										if (config.endRelationField && config.endTimeType == 2) {
											endTime = this.getFieldVal(config.endRelationField, rowIndex) || 0
										} else {
											endTime = e.endTime
										}
										item.startTime = startTime
										item.endTime = endTime
									}
									if (e.opType === 'setTime') {
										let startTime = 0
										let endTime = 0
										if (config.startRelationField && config.startTimeType == 2) {
											startTime = this.getFieldVal(config.startRelationField, rowIndex) || '00:00:00'
											startTime = startTime.split(':').length == 3 ? startTime : startTime + ':00'
										} else {
											startTime = e.startTime
										}
										if (config.endRelationField && config.endTimeType == 2) {
											endTime = this.getFieldVal(config.endRelationField, rowIndex) || '23:59:59'
											endTime = endTime.split(':').length == 3 ? endTime : endTime + ':00'
										} else {
											endTime = e.endTime
										}
										item.startTime = startTime
										item.endTime = endTime
									}
									if (item.value != defaultValue) {
										item.value = defaultValue
										this.$nextTick(() => this.handleRelation(item, rowIndex));
									}
								}
							}
						}
					}
				}
			},
			handleDefaultRelation(data, rowIndex = 0) {
				const currRelations = this.childRelations
				for (let key in currRelations) {
					if (key === data) {
						for (let i = 0; i < currRelations[key].length; i++) {
							const e = currRelations[key][i];
							const config = e.__config__
							let defaultValue = ''
							let row = this.tableFormData[rowIndex];
							for (let j = 0; j < row.length; j++) {
								let item = row[j];
								const vModel = item.projectKey === 'popupSelect' ? item.__vModel__.substring(0, item
									.__vModel__.indexOf('_linzenRelation_')) : item.__vModel__
								if (e.__vModel__ === vModel) {
									if (e.opType === 'setUserOptions') {
										let value = this.getFieldVal(e.relationField, rowIndex) || []
										item.ableRelationIds = Array.isArray(value) ? value : [value]
									}
									if (e.opType === 'setDate') {
										let startTime = 0
										let endTime = 0
										if (config.startRelationField && config.startTimeType == 2) {
											startTime = this.getFieldVal(config.startRelationField, rowIndex) || 0
										} else {
											startTime = e.startTime
										}
										if (config.endRelationField && config.endTimeType == 2) {
											endTime = this.getFieldVal(config.endRelationField, rowIndex) || 0
										} else {
											endTime = e.endTime
										}
										item.startTime = startTime
										item.endTime = endTime
									}
									if (e.opType === 'setTime') {
										let startTime = 0
										let endTime = 0
										if (config.startRelationField && config.startTimeType == 2) {
											startTime = this.getFieldVal(config.startRelationField, rowIndex) || '00:00:00'
											if (startTime.split(':').length == 3) {
												startTime = startTime
											} else {
												startTime = startTime + ':00'
											}
										} else {
											startTime = e.startTime
										}
										if (config.endRelationField && config.endTimeType == 2) {
											endTime = this.getFieldVal(config.endRelationField, rowIndex) ||
												'23:59:59'
											if (endTime.split(':').length == 3) {
												endTime = endTime
											} else {
												endTime = endTime + ':00'
											}
										} else {
											endTime = e.endTime
										}
										item.startTime = startTime
										item.endTime = endTime
									}
								}
							}
						}
					}
				}
			},
			getFieldVal(field, rowIndex) {
				let val = ''
				if (field.includes('-')) {
					let childVModel = field.split('-')[1]
					let list = this.tableFormData[rowIndex].filter(o => o.__vModel__ === childVModel)
					val = list.length ? list[0].value : ''
				} else {
					val = this.formData[field] || ''
				}
				return val
			},
			buildRowAttr(rowIndex, val) {
				let row = this.tableFormData[rowIndex];
				for (let i = 0; i < row.length; i++) {
					let item = row[i];
					const config = item.__config__
					for (let key in this.value[rowIndex]) {
						if (key === item.__vModel__) item.value = this.value[rowIndex][key]
					}
					if (dyOptionsList.indexOf(config.projectKey) > -1) {
						if (config.dataType === 'dictionary' && config.dictionaryType) {
							this.$store.dispatch('base/getDicDataSelector', config.dictionaryType).then(res => {
								item.options = res || []
								uni.$emit('initCollapse')
							})
						}
						if (config.dataType === 'dynamic' && config.propsUrl) {
							this.handleRelation(item, rowIndex)
							if (item.options && item.options.length && (!config.templateJson || !config.templateJson
									.length || !this.hasTemplateJsonRelation(config.templateJson))) continue
							let query = {
								paramList: this.getParamList(config.templateJson, this.formData, rowIndex)
							}
							const matchInfo = JSON.stringify({
								id: config.propsUrl,
								query
							});
							const itemInfo = {
								matchInfo,
								rowIndex,
								colIndex: i
							};
							const infoIndex = this.dataInterfaceInfo.findIndex(o => o.matchInfo === matchInfo);
							let useCacheOptions = false;
							if (infoIndex === -1) {
								this.dataInterfaceInfo.push(itemInfo);
							} else {
								const cacheOptions = this.getCacheOptions(infoIndex);
								if (cacheOptions.length) {
									item.options = cacheOptions;
									uni.$emit('initCollapse')
									useCacheOptions = true;
								}
							}
							if (!useCacheOptions) {
								getDataInterfaceRes(config.propsUrl, query).then(res => {
									item.options = Array.isArray(res.data) ? res.data : []
									uni.$emit('initCollapse')
								})
							}
						}
					}
					if (config.projectKey === 'userSelect' && item.relationField && item.selectType !== 'all' && item
						.selectType !== 'custom') {
						let value = this.getFieldVal(item.relationField, rowIndex) || []
						item.ableRelationIds = Array.isArray(value) ? value : [value]
					}
					if (config.projectKey === 'datePicker') {
						let startTime = 0
						let endTime = 0
						if (config.startRelationField && config.startTimeType == 2) {
							startTime = this.getFieldVal(config.startRelationField, rowIndex) || 0
						} else {
							startTime = item.startTime
						}
						if (config.endRelationField && config.endTimeType == 2) {
							endTime = this.getFieldVal(config.endRelationField, rowIndex) || 0
						} else {
							endTime = item.endTime
						}
						item.startTime = startTime
						item.endTime = endTime
					}
					if (config.projectKey === 'timePicker') {
						let startTime = 0
						let endTime = 0
						if (config.startRelationField && config.startTimeType == 2) {
							startTime = this.getFieldVal(config.startRelationField, rowIndex) || '00:00:00'
							startTime = startTime && (startTime.split(':').length == 3) ? startTime : startTime + ':00'
						} else {
							startTime = item.startTime
						}
						if (config.endRelationField && config.endTimeType == 2) {
							endTime = this.getFieldVal(config.endRelationField, rowIndex) || '23:59:59'
							endTime = endTime.split(':').length == 3 ? endTime : endTime + ':00'
						} else {
							endTime = item.endTime
						}
						item.startTime = startTime
						item.endTime = endTime
					}
				}
			},
			// 获取缓存options数据
			getCacheOptions(index) {
				const item = this.dataInterfaceInfo[index];
				if (item.rowIndex === -1) return this.tableData[item.colIndex].options || [];
				return this.tableFormData[item.rowIndex][item.colIndex].options || [];
			},
			// 判断templateJson里是否有关联字段
			hasTemplateJsonRelation(templateJson) {
				return templateJson.some(o => o.relationField);
			},
			getParamList(templateJson, formData, index) {
				if (!templateJson) return []
				for (let i = 0; i < templateJson.length; i++) {
					if (templateJson[i].relationField) {
						if (templateJson[i].relationField.includes('-')) {
							let childVModel = templateJson[i].relationField.split('-')[1]
							let list = this.tableFormData[index].filter(o => o.__vModel__ === childVModel)
							templateJson[i].defaultValue = list.length ? list[0].value : ''
						} else {
							templateJson[i].defaultValue = formData[templateJson[i].relationField] || ''
						}
					}
				}
				return templateJson
			},
			getDefaultParamList(templateJson, formData) {
				if (!templateJson) return []
				for (let i = 0; i < templateJson.length; i++) {
					if (templateJson[i].relationField) {
						if (templateJson[i].relationField.includes('-')) {
							let childVModel = templateJson[i].relationField.split('-')[1]
							let list = this.tableData.filter(o => o.__vModel__ === childVModel)
							templateJson[i].defaultValue = ''
							if (list.length) templateJson[i].defaultValue = list[0].__config__.defaultValue || ''
						} else {
							templateJson[i].defaultValue = formData[templateJson[i].relationField] || ''
						}
					}
				}
				return templateJson
			},
			initRelationData() {
				const handleRelationFun = (list) => {
					list.forEach(cur => {
						this.handleDefaultRelation(cur.__vModel__)
						if (cur.__config__.children) handleRelationFun(cur.__config__.children)
					})
				}
				handleRelationFun(this.config.__config__.children)
			},
			getEmptyItem(val) {
				return this.tableData.map(o => {
					const config = o.__config__
					if (config.projectKey === 'datePicker' && config.defaultCurrent) {
						let format = this.linzen.handelFormat(o.format)
						let dateStr = this.linzen.toDate(new Date().getTime(), format)
						let time = format === 'yyyy' ? '-01-01 00:00:00' : format === 'yyyy-MM' ? '-01 00:00:00' :
							format === 'yyyy-MM-dd' ? ' 00:00:00' : ''
						config.defaultValue = new Date(dateStr + time).getTime()
					}
					if (config.projectKey === 'timePicker' && config.defaultCurrent) {
						config.defaultValue = this.linzen.toDate(new Date(), o.format)
					}
					const res = {
						...o,
						value: val ? val[o.__vModel__] : config.defaultValue,
						options: config.dataType == "dynamic" ? [] : o.options,
						rowData: val || {},
					}

					return res
				})
			},
			formatData() {
				const organizeIdList = this.userInfo.organizeIdList
				for (let i = 0; i < this.tableFormData.length; i++) {
					const item = this.tableFormData[i]
					for (let j = 0; j < item.length; j++) {
						const it = item[j]
						const config = item[j].__config__
						if (config.projectKey === 'datePicker' && config.defaultCurrent &&
							i === this.tableFormData.length - 1) {
							let format = this.linzen.handelFormat(it.format)
							let dateStr = this.linzen.toDate(new Date().getTime(), format)
							let time = format === 'yyyy' ? '-01-01 00:00:00' : format === 'yyyy-MM' ?
								'-01 00:00:00' : format === 'yyyy-MM-dd' ?
								' 00:00:00' : ''
							it.value = new Date(dateStr + time).getTime()
						}
						if (config.projectKey === 'organizeSelect' && config.defaultCurrent && Array.isArray(
								organizeIdList) && organizeIdList.length && i === this.tableFormData.length - 1) {
							it.value = it.multiple ? [organizeIdList] : organizeIdList
						}
					}
				}
			},
			checkData(item) {
				if ([null, undefined, ''].includes(item.value)) return false
				if (Array.isArray(item.value)) return item.value.length > 0
				return true
			},
			submit(noShowToast) {
				let res = true
				outer: for (let i = 0; i < this.tableFormData.length; i++) {
					const row = this.tableFormData[i]
					for (let j = 0; j < row.length; j++) {
						const cur = row[j]
						const config = cur.__config__
						if (config.required && !this.checkData(cur) && config.isVisibility && !config.noShow) {
							res = false
							if (!noShowToast) this.$u.toast(
								`${this.config.__config__.label}(${i+1})${config.label}不能为空`)
							break outer
						}
						if (config.regList && config.regList.length && config.isVisibility) {
							let regList = config.regList
							for (let ii = 0; ii < regList.length; ii++) {
								const item = regList[ii];
								if (item.pattern) {
									item.pattern = item.pattern.toString()
									let start = item.pattern.indexOf('/')
									let stop = item.pattern.lastIndexOf('/')
									let str = item.pattern.substring(start + 1, stop)
									let reg = new RegExp(str)
									item.pattern = reg
								}
								if (cur.value && item.pattern && !item.pattern.test(cur.value)) {
									if (!noShowToast) this.$u.toast(
										`${this.config.__config__.label}(${i+1})${config.label}${item.message}`
									)
									res = false
									break outer
								}
							}
						}
					}
				}
				// const data = this.tableFormData.map(row => row.reduce((p, c) => (p[c.__vModel__] = c.value, p), {})) || []
				const data = this.getTableValue() || []
				return noShowToast ? data : res ? data : false
			},
			// 获取表格数据
			getTableValue() {
				return this.tableFormData.map(row => row.reduce((p, c) => {
					let str = c.__vModel__
					if (c.__vModel__ && c.__vModel__.indexOf('_linzenRelation_') >= 0) {
						str = c.__vModel__.substring(0, c.__vModel__.indexOf('_linzenRelation_'))
					}
					p[str] = c.value
					if (c.rowData) p = {
						...c.rowData,
						...p
					}
					return p
				}, {}))
			},
			setTableFormData(prop, value) {
				let activeRow = this.tableFormData[this.activeRowIndex] || []
				for (let i = 0; i < activeRow.length; i++) {
					if (activeRow[i].__vModel__ === prop) {
						activeRow[i].value = value
						break
					}
				}
			},
			getTableFieldOptions(prop) {
				let res = []
				for (let i = 0; i < this.tableData.length; i++) {
					if (this.tableData[i].__vModel__ === prop) {
						res = this.tableData[i].options || []
						break
					}
				}
				return res
			},
			onChange(val, data, rowIndex) {
				let res = val.value ? val.value : val
				this.activeRowIndex = rowIndex
				var projectKey = data.__config__.projectKey
				if (projectKey == "inputNumber" || projectKey == "switch" || projectKey == "input" || projectKey == "textarea") {
					this.setScriptFunc(res, data, rowIndex)
				}
				this.handleRelation(data, rowIndex)
			},
			change(val, data, rowIndex) {
				this.activeRowIndex = rowIndex
				this.handleRelation(data, rowIndex)
				let res = ''
				if (val.length > 1) {
					res = val[1]
				} else {
					if (data.__config__.projectKey == "uploadImg" || data.__config__.projectKey == "uploadFile") {
						res = val[0]
					} else {
						res = this.getDefalutData(data)
					}
				}
				if (['popupSelect', 'relationForm'].includes(data.__config__.projectKey)) {
					this.setTransferFormData(res, data.__config__, data.__config__.projectKey)
				}
				this.setScriptFunc(res, data, rowIndex)
			},
			getDefalutData(item) {
				let config = item.__config__
				let data = ''
				if (['select', 'radio', 'checkbox'].includes(config.projectKey)) {
					let options = item.options
					let props = item.props
					if (config.projectKey === 'checkbox' || (config.projectKey === 'select' && item.multiple)) {
						let _data = []
						outer: for (let i = 0; i < item.value.length; i++) {
							inner: for (let j = 0; j < options.length; j++) {
								if (item.value[i] === options[j][props.value]) {
									_data.push(options[j])
									break inner
								}
							}
						}
						data = _data
					} else {
						let _data = {}
						for (let i = 0; i < options.length; i++) {
							if (item.value == options[i][props.value]) {
								_data = options[i]
								break
							}
						}
						data = _data
					}
				}
				return data ? data : item.value ? item.value : config.defaultValue
			},
			setScriptFunc(val, data, rowIndex) {
				if (data && data.on && data.on.change) {
					const func = this.linzen.getScriptFunc(data.on.change);
					if (!func) return
					func.call(this, {
						data: val,
						rowIndex,
						...this.parameter
					})
				}
			},
			setTransferFormData(data, config, projectKey) {
				if (!config.transferList.length) return;
				let row = this.tableFormData[this.activeRowIndex];
				for (let index = 0; index < config.transferList.length; index++) {
					const element = config.transferList[index];
					if (element.sourceValue.includes('-')) element.sourceValue = element.sourceValue.split('-')[1];
					for (let index = 0; index < row.length; index++) {
						const e = row[index];
						// if (e.__vModel__ == element.sourceValue) e.value = data[projectKey == 'popupSelect' ? element
						// 	.targetField + '_linzenId' : element.targetField];
						if (e.__vModel__ == element.sourceValue) e.value = data[element.targetField]
					}
				}
			},
			clickIcon(e) {
				if (!e.__config__.tipLabel) return
				this.tipsContent = e.__config__.tipLabel
				this.tipsTitle = e.__config__.label
				this.showTipsModal = true
			},
			onBlur(val, data, rowIndex) {
				this.activeRowIndex = rowIndex
				this.setScriptFunc(val, data, 'blur', rowIndex)
			},
			columnBtnsHandel(item, index) {
				if (item.value == 'remove') return this.removeRow(index, item.showConfirm);
				if (item.value == 'copy') return this.copyRow(index);
			},
			removeRow(index, showConfirm = 0) {
				const handleRemove = () => {
					this.tableFormData.splice(index, 1);
					this.$nextTick(() => uni.$emit('initCollapse'))
				};
				if (!showConfirm) return handleRemove();
				uni.showModal({
					title: '提示',
					content: '确认删除该条信息吗？',
					success: (res) => {
						if (res.confirm) handleRemove()
					}
				})
			},
			copyRow(index) {
				let item = JSON.parse(JSON.stringify(this.tableFormData[index]));
				item.length && item.map(o => delete o.rowData);
				this.tableFormData.push(item);
			},
			footerBtnsHandle(item) {
				item.value == 'add' ? this.addRow() : this.openSelectDialog(item.actionConfig)
			},
			addRow(val) {
				this.tableFormData.push(this.getEmptyItem(val))
				if (this.tableFormData.length) this.formatData()
				const rowIndex = this.tableFormData.length - 1
				this.buildRowAttr(rowIndex, val)
				this.$nextTick(() => uni.$emit('initCollapse'))
			},
			openSelectDialog(actionConfig) {
				const data = {
					actionConfig,
					formData: this.formData,
					tableVmodel: this.config.__vModel__
				}
				uni.navigateTo({
					url: '/pages/apply/tableLinkage/index?data=' + JSON.stringify(data)
				})
			}
		}
	}
</script>