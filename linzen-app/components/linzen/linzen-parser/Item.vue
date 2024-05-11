<template>
	<view class="">
		<block v-if="itemCopy.__config__.projectKey==='text'">
			<linzen-text :content="itemCopy.content" :textStyle="itemCopy.textStyle"></linzen-text>
		</block>
		<block v-else-if="itemCopy.__config__.projectKey==='groupTitle'">
			<linzen-group :content="itemCopy.content" :content-position="itemCopy.contentPosition"
				:helpMessage="itemCopy.helpMessage" @groupIcon="clickIcon(itemCopy)"></linzen-group>
		</block>
		<view class="linzen-divider" v-else-if="itemCopy.__config__.projectKey==='divider'">
			<u-divider half-width="200" height="80">{{itemCopy.content}}</u-divider>
		</view>
		<view v-else-if="isIgnore"></view>
		<!-- 富文本 -->
		<view class="u-p-l-20 u-p-r-20 form-item-box" v-else-if="itemCopy.__config__.projectKey==='editor'">
			<u-form-item :prop="itemCopy.__vModel__" label-width="0">
				<linzen-editor v-model="value" :disabled="itemCopy.disabled"></linzen-editor>
			</u-form-item>
		</view>
		<view class="linzen-card" v-else-if="itemCopy.__config__.projectKey==='card'||itemCopy.__config__.projectKey==='row'">
			<view class="linzen-card-cap u-line-1 u-flex" v-if="itemCopy.header" @click="clickIcon(itemCopy)">
				<u-icon :name="itemCopy.__config__.tipLabel? 'question-circle-fill':''" class="u-m-l-10"
					color="#a0acb7"></u-icon>
			</view>
			<item v-for="(child, index) in itemCopy.__config__.children" :key="child.__config__.renderKey" :item="child"
				:formConf="formConf" :formData="formData" @input="setValue" @click="arrangementInClick"
				@clickIcon='clickIcon' v-if="!child.__config__.noShow&& child.__config__.isVisibility" />
		</view>
		<view v-else-if="itemCopy.__config__.isVisibility && itemCopy.__config__.projectKey==='table'">
			<child-table v-model="value" :config="itemCopy" :ref="itemCopy.__vModel__" @input="setValue"
				:formData='formData'></child-table>
		</view>
		<view class="linzen-card" v-else-if="itemCopy.__config__.projectKey==='tab'">
			<u-tabs is-scroll :list="itemCopy.__config__.children" name="title" :current="tabCurrent"
				@change="onTabChange">
			</u-tabs>
			<view v-for="(item,i) in itemCopy.__config__.children" :key='i'>
				<view v-show="i == tabCurrent">
					<item v-for="(child, index) in item.__config__.children" :key="child.__config__.renderKey"
						:item="child" :formConf="formConf" :formData="formData" @input="setValue" @clickIcon='clickIcon'
						v-if="!child.__config__.noShow&& child.__config__.isVisibility" @click="arrangementInClick" />
				</view>
			</view>
		</view>
		<!-- 折叠面板 -->
		<view v-else-if="itemCopy.__config__.projectKey==='collapse'">
			<u-collapse ref="collapseRef" :head-style="{'padding-left':'20rpx'}" :accordion="itemCopy.accordion">
				<u-collapse-item :title="item.title" v-for="(item, i) in itemCopy.__config__.children" :key="i"
					:open="itemCopy.__config__.active.indexOf(item.name)>-1" @change="onCollapseChange">
					<item v-for="(child, index) in item.__config__.children" :key="child.__config__.renderKey"
						:item="child" :formConf="formConf" :formData="formData" @input="setValue" @clickIcon='clickIcon'
						@click="arrangementInClick" v-if="!child.__config__.noShow&& child.__config__.isVisibility" />
				</u-collapse-item>
			</u-collapse>
		</view>
		<block v-else-if="itemCopy.__config__.projectKey==='link'">
			<linzen-link :content="itemCopy.content" :href="itemCopy.href" :target='itemCopy.target'
				:textStyle="itemCopy.textStyle" @click="onButtonClick" />
		</block>
		<view class="u-p-l-20 u-p-r-20 form-item-box" v-else-if="itemCopy.__config__.projectKey==='alert'">
			<u-form-item>
				<linzen-alert-tips :type="itemCopy.type" :title="itemCopy.title" :tagIcon='itemCopy.tagIcon'
					:showIcon="itemCopy.showIcon" :closable="itemCopy.closable" :description="itemCopy.description"
					:closeText="itemCopy.closeText" />
			</u-form-item>
		</view>
		<!-- 按钮 -->
		<view class="u-p-l-20 u-p-r-20 form-item-box" v-else-if="itemCopy.__config__.projectKey==='button'">
			<u-form-item label-width="0">
				<linzen-button :buttonText="itemCopy.buttonText" :align="itemCopy.align" :type="itemCopy.type"
					:disabled="itemCopy.disabled" @click="onButtonClick($event)"></linzen-button>
			</u-form-item>
		</view>
		<view class="u-p-l-20 u-p-r-20 form-item-box" v-else>
			<u-form-item :label="realLabel" :prop="itemCopy.__vModel__" :required="itemCopy.__config__.required"
				:left-icon='itemCopy.__config__.showLabel && itemCopy.__config__.tipLabel && label ? "question-circle-fill":""'
				@clickIcon="clickIcon(itemCopy)" :left-icon-style="{'color':'#a8aaaf'}" :label-width="labelWidth">
				<!-- 单行输入 -->
				<block v-if="itemCopy.__config__.projectKey==='input'">
					<linzen-input :showPassword="itemCopy['show-password']" v-model="value"
						:placeholder="itemCopy.placeholder" :maxlength="itemCopy.maxlength?itemCopy.maxlength:140"
						:disabled="itemCopy.disabled" @blur="onBlur" @change="itemChange('input')"
						:clearable='itemCopy.clearable' :addonAfter="itemCopy.addonAfter"
						:addonBefore="itemCopy.addonBefore" :useScan='itemCopy.useScan' />
				</block>
				<!-- 下拉补全 -->
				<block v-if="itemCopy.__config__.projectKey==='autoComplete'">
					<linzen-auto-complete :disabled="itemCopy.disabled" :interfaceName="itemCopy.interfaceName"
						:placeholder="itemCopy.placeholder" :interfaceId="itemCopy.interfaceId" :total="itemCopy.total"
						v-model="value" :templateJson="itemCopy.templateJson" :formData='formData'
						:relationField="itemCopy.relationField" :propsValue="itemCopy.propsValue"
						:clearable='itemCopy.clearable' @change="change"></linzen-auto-complete>
				</block>
				<!-- 计算公式 -->
				<block v-if="itemCopy.__config__.projectKey==='calculate'">
					<linzen-calculation :expression='itemCopy.expression' :vModel='itemCopy.__vModel__'
						:config='itemCopy.__config__' :formData='formData' v-model="value"
						:precision="itemCopy.precision" :isAmountChinese="itemCopy.isAmountChinese"
						:thousands="itemCopy.thousands" />
				</block>
				<!-- 颜色选择器 -->
				<block v-if="itemCopy.__config__.projectKey==='colorPicker'">
					<linzen-colorPicker v-model="value" :colorFormat="itemCopy.colorFormat"
						:disabled="itemCopy.disabled"></linzen-colorPicker>
				</block>
				<!-- 文本域 -->
				<block v-if="itemCopy.__config__.projectKey==='textarea'">
					<u-input input-align='right' v-model="value" type="textarea" :placeholder="itemCopy.placeholder"
						:maxlength="itemCopy.maxlength?itemCopy.maxlength:140" :disabled="itemCopy.disabled"
						@blur="onBlur" @change="itemChange()" />
				</block>
				<!-- 数字输入步进器 -->
				<block v-if="itemCopy.__config__.projectKey==='inputNumber'">
					<linzen-number-box v-model="value" :step='itemCopy.step' :max='itemCopy.max' :min='itemCopy.min'
						:disabled="itemCopy.disabled" @blur="onBlur" @change="change"
						:isAmountChinese="itemCopy.isAmountChinese" :thousands="itemCopy.thousands"
						:addonAfter="itemCopy.addonAfter" :addonBefore="itemCopy.addonBefore"
						:controls="itemCopy.controls" :precision="itemCopy.precision" :detailed="false">
					</linzen-number-box>
				</block>
				<!-- 开关 -->
				<block v-if="itemCopy.__config__.projectKey==='switch'" class="u-flex u-form-item-switch">
					<linzen-switch v-model="value" :disabled="itemCopy.disabled" @change="change"></linzen-switch>
				</block>
				<!-- 单选框组 -->
				<block v-if="itemCopy.__config__.projectKey==='radio'">
					<linzen-radio v-model="value" :options="itemCopy.options" :props="itemCopy.props" @change="change"
						:disabled="itemCopy.disabled" :direction='itemCopy.direction'>
					</linzen-radio>
				</block>
				<!-- 多选框组 -->
				<block v-if="itemCopy.__config__.projectKey==='checkbox'">
					<linzen-checkbox v-model="value" :options="itemCopy.options" :props="itemCopy.props" @change="change"
						:disabled="itemCopy.disabled" :direction='itemCopy.direction'>
					</linzen-checkbox>
				</block>
				<!-- 下拉选择 -->
				<block v-if="itemCopy.__config__.projectKey==='select'">
					<linzen-select v-model="value" :placeholder="itemCopy.placeholder" :options="itemCopy.options"
						:props="itemCopy.props" :multiple="itemCopy.multiple" :disabled="itemCopy.disabled"
						@change="change">
					</linzen-select>
				</block>
				<!-- 级联选择 -->
				<block v-if="itemCopy.__config__.projectKey==='cascader'">
					<linzen-cascader v-model="value" :placeholder="itemCopy.placeholder" :options="itemCopy.options"
						:props="itemCopy.props" :disabled="itemCopy.disabled" :multiple="itemCopy.multiple"
						@change="change" :filterable='itemCopy.filterable' :clearable='itemCopy.clearable'
						:showAllLevels="itemCopy.showAllLevels">
					</linzen-cascader>
				</block>
				<!-- 分组选择 -->
				<block v-if="itemCopy.__config__.projectKey==='groupSelect'">
					<linzen-group-select :vModel='itemCopy.__vModel__' v-model="value" :multiple="itemCopy.multiple"
						:disabled="itemCopy.disabled" :placeholder="itemCopy.placeholder" @change="change"
						:ableIds="itemCopy.ableIds" :selectType="itemCopy.selectType" />
				</block>
				<!-- 角色选择 -->
				<block v-if="itemCopy.__config__.projectKey==='roleSelect'">
					<linzen-role-select :vModel='itemCopy.__vModel__' v-model="value" :multiple="itemCopy.multiple"
						:disabled="itemCopy.disabled" :placeholder="itemCopy.placeholder" @change="change"
						:ableIds="itemCopy.ableIds" :selectType="itemCopy.selectType" />
				</block>
				<!-- 时间选择 -->
				<block v-if="itemCopy.__config__.projectKey==='timePicker'">
					<linzen-date-time type="time" v-model="value" :placeholder="itemCopy.placeholder"
						:disabled="itemCopy.disabled" @change="change" :format="itemCopy.format"
						:startTime="itemCopy.startTime" :endTime='itemCopy.endTime'>
					</linzen-date-time>
				</block>
				<!-- 日期选择 -->
				<block v-if="itemCopy.__config__.projectKey==='datePicker'">
					<linzen-date-time :type="itemCopy.type||'date'" v-model="value" :placeholder="itemCopy.placeholder"
						:disabled="itemCopy.disabled" @change="change" :startTime="itemCopy.startTime"
						:endTime='itemCopy.endTime' :format="itemCopy.format">
					</linzen-date-time>
				</block>
				<!-- 图片上传 -->
				<block v-if="itemCopy.__config__.projectKey==='uploadImg'">
					<linzen-upload v-model="value" :disabled="itemCopy.disabled" :fileSize="itemCopy.fileSize"
						:limit="itemCopy.limit" :pathType="itemCopy.pathType" :isAccount="itemCopy.isAccount"
						:folder="itemCopy.folder" @change="change" :tipText="itemCopy.tipText"
						:sizeUnit="itemCopy.sizeUnit">
					</linzen-upload>
				</block>
				<!-- 文件上传 -->
				<block v-if="itemCopy.__config__.projectKey==='uploadFile'">
					<linzen-file v-model="value" :disabled="itemCopy.disabled" :limit="itemCopy.limit?itemCopy.limit:9"
						:sizeUnit="itemCopy.sizeUnit" :fileSize="!itemCopy.fileSize ? 5 : itemCopy.fileSize"
						:pathType="itemCopy.pathType" :isAccount="itemCopy.isAccount" :folder="itemCopy.folder"
						:accept="itemCopy.accept" @change="change" :tipText="itemCopy.tipText" />
				</block>
				<!-- 评分 -->
				<block v-if="itemCopy.__config__.projectKey==='rate'">
					<linzen-rate v-model="value" :max="itemCopy.count" :allowHalf="itemCopy.allowHalf"
						:disabled="itemCopy.disabled" @change="itemChange('rate')">
					</linzen-rate>
				</block>
				<!-- 滑块 -->
				<block v-if="itemCopy.__config__.projectKey==='slider'">
					<linzen-slider v-model="value" :step="itemCopy.step" :min="itemCopy.min||0" :max="itemCopy.max||100"
						:disabled="itemCopy.disabled" @change="change" />
				</block>
				<block v-if="itemCopy.__config__.projectKey==='relationFormAttr'">
					<linzen-relation-attr v-model="value" :showField="itemCopy.showField"
						:relationField="itemCopy.relationField" :isStorage='itemCopy.isStorage' type='relationFormAttr'
						@change="change">
					</linzen-relation-attr>
				</block>
				<block v-if="itemCopy.__config__.projectKey==='popupAttr'">
					<linzen-relation-attr v-model="value" :showField="itemCopy.showField"
						:relationField="itemCopy.relationField" :isStorage='itemCopy.isStorage' type='popupAttr'
						@change="change">
					</linzen-relation-attr>
				</block>
				<!-- 关联表单 -->
				<block v-if="itemCopy.__config__.projectKey==='relationForm'">
					<linzen-relation-select type="relation" v-model="value" :placeholder="itemCopy.placeholder"
						:disabled="itemCopy.disabled" :modelId="itemCopy.modelId"
						:columnOptions="itemCopy.columnOptions" :relationField="itemCopy.relationField"
						:hasPage="itemCopy.hasPage" :pageSize="itemCopy.pageSize" :vModel="itemCopy.__config__.tableName ? itemCopy.__vModel__ + '_linzenTable_' + itemCopy.__config__.tableName + (itemCopy
							.__config__.isSubTable ? '0' : '1') : itemCopy.__vModel__" :popupTitle="itemCopy.popupTitle" @change="change">
					</linzen-relation-select>
				</block>
				<!-- 弹窗选择 -->
				<block v-if="itemCopy.__config__.projectKey === 'popupSelect'">
					<linzen-popup-select type="popup" v-model="value" :placeholder="itemCopy.placeholder"
						:disabled="itemCopy.disabled" :interfaceId="itemCopy.interfaceId" :formData="formData"
						:templateJson="itemCopy.templateJson" :columnOptions="itemCopy.columnOptions"
						:relationField="itemCopy.relationField" :propsValue="itemCopy.propsValue"
						:hasPage="itemCopy.hasPage" :pageSize="itemCopy.pageSize" :vModel="itemCopy.__config__.tableName ? itemCopy.__vModel__ + '_linzenTable_' + itemCopy.__config__.tableName + (itemCopy
							.__config__.isSubTable ? '0' : '1') : itemCopy.__vModel__" :popupTitle="itemCopy.popupTitle" @change="change">
					</linzen-popup-select>
				</block>
				<!-- 下拉表格 -->
				<block v-if="itemCopy.__config__.projectKey === 'popupTableSelect'">
					<linzen-table-select v-model="value" :placeholder="itemCopy.placeholder" :disabled="itemCopy.disabled"
						:interfaceId="itemCopy.interfaceId" :columnOptions="itemCopy.columnOptions" :formData="formData"
						:templateJson="itemCopy.templateJson" :relationField="itemCopy.relationField"
						:propsValue="itemCopy.propsValue" :hasPage="itemCopy.hasPage" :pageSize="itemCopy.pageSize"
						:vModel="itemCopy.__config__.tableName ? itemCopy.__vModel__ + '_linzenTable_' + itemCopy.__config__.tableName + (itemCopy
							.__config__.isSubTable ? '0' : '1') : itemCopy.__vModel__" :popupTitle="itemCopy.popupTitle"
						:multiple="itemCopy.multiple" :filterable="itemCopy.filterable" @change="change">
					</linzen-table-select>
				</block>
				<!-- 组织选择 -->
				<block v-if="itemCopy.__config__.projectKey==='organizeSelect'">
					<linzen-com-select v-model="value" :multiple="itemCopy.multiple" :placeholder="itemCopy.placeholder"
						:disabled="itemCopy.disabled" @change="change" :ableIds="itemCopy.ableIds"
						:selectType="itemCopy.selectType">
					</linzen-com-select>
				</block>
				<!-- 部门选择 -->
				<block v-if="itemCopy.__config__.projectKey==='depSelect'">
					<linzen-postordep-select type="department" v-model="value" :multiple="itemCopy.multiple"
						:placeholder="itemCopy.placeholder" :disabled="itemCopy.disabled" :ableIds="itemCopy.ableIds"
						:selectType="itemCopy.selectType" @change="change">
					</linzen-postordep-select>
				</block>
				<!-- 岗位选择 -->
				<block v-if="itemCopy.__config__.projectKey==='posSelect'">
					<linzen-postordep-select type="position" v-model="value" :multiple="itemCopy.multiple"
						:placeholder="itemCopy.placeholder" :disabled="itemCopy.disabled" :ableIds="itemCopy.ableIds"
						:selectType="itemCopy.selectType" @change="change">
					</linzen-postordep-select>
				</block>
				<!-- 用户选择 -->
				<block v-if="itemCopy.__config__.projectKey==='userSelect'">
					<linzen-user-select v-model="value" :multiple="itemCopy.multiple" :placeholder="itemCopy.placeholder"
						:disabled="itemCopy.disabled" :selectType="itemCopy.selectType" :ableIds="itemCopy.ableIds"
						:clearable="itemCopy.clearable" :ableRelationIds="itemCopy.ableRelationIds" @change="change">
					</linzen-user-select>
				</block>
				<!-- 用户组件 -->
				<block v-if="itemCopy.__config__.projectKey==='usersSelect'">
					<linzen-user-choice v-model="value" :multiple="itemCopy.multiple" :placeholder="itemCopy.placeholder"
						:disabled="itemCopy.disabled" :selectType="itemCopy.selectType" :ableIds="itemCopy.ableIds"
						:clearable="itemCopy.clearable" @change="change">
					</linzen-user-choice>
				</block>
				<!-- 下拉树形 -->
				<block v-if="itemCopy.__config__.projectKey==='treeSelect'">
					<linzen-tree-select v-model="value" :options="itemCopy.options" :props="itemCopy.props"
						:multiple="itemCopy.multiple" :placeholder="itemCopy.placeholder" :disabled="itemCopy.disabled"
						@change="change" :filterable="itemCopy.filterable">
					</linzen-tree-select>
				</block>
				<!-- 地区选择 -->
				<block v-if="itemCopy.__config__.projectKey==='areaSelect'">
					<linzen-city-select v-model="value" :placeholder="itemCopy.placeholder" :level="itemCopy.level"
						:disabled="itemCopy.disabled" :multiple="itemCopy.multiple" @change="change">
					</linzen-city-select>
				</block>
				<!-- 条形码 -->
				<block v-if="itemCopy.__config__.projectKey==='barcode'">
					<linzen-barcode :staticText="itemCopy.staticText" :width="itemCopy.width" :height="itemCopy.height"
						:format="itemCopy.format" :dataType="itemCopy.dataType" :lineColor="itemCopy.lineColor"
						:background="itemCopy.background" :relationField="itemCopy.relationField" :formData="formData">
					</linzen-barcode>
				</block>
				<!-- 二维码 -->
				<block v-if="itemCopy.__config__.projectKey==='qrcode'">
					<linzen-qrcode :staticText="itemCopy.staticText" :width="itemCopy.width" :dataType="itemCopy.dataType"
						:colorDark="itemCopy.colorDark" :colorLight="itemCopy.colorLight"
						:relationField="itemCopy.relationField" :formData="formData">
					</linzen-qrcode>
				</block>
				<!-- 签名 -->
				<block v-if="itemCopy.__config__.projectKey==='sign'">
					<linzen-sign v-model="value" :disabled="itemCopy.disabled" @change="change" />
				</block>
				<!-- 定位 -->
				<block v-if="itemCopy.__config__.projectKey==='location'">
					<linzen-location v-model="value" :autoLocation="itemCopy.autoLocation"
						:adjustmentScope="itemCopy.adjustmentScope" :enableLocationScope="itemCopy.enableLocationScope"
						:enableDesktopLocation="itemCopy.enableDesktopLocation" :locationScope="itemCopy.locationScope"
						:disabled="itemCopy.disabled" :clearable='item.clearable' @change="change">
					</linzen-location>
				</block>
				<block v-if="isSystem">
					<linzen-open-data v-model="value" :type="itemCopy.type"
						:showLevel="itemCopy.showLevel"></linzen-open-data>
				</block>
				<block v-if="itemCopy.__config__.projectKey==='modifyUser'||itemCopy.__config__.projectKey==='modifyTime'">
					<u-input input-align='right' v-model="value" placeholder="系统自动生成" disabled></u-input>
				</block>
			</u-form-item>
		</view>

	</view>
</template>

<script>
	import childTable from './childTable.vue'
	import Item from './Item'
	const systemList = ['createUser', 'createTime', 'currOrganize', 'currDept', 'currPosition', 'billRule']
	const ignoreList = []
	const specialList = ['link', 'editor', 'button', 'alert']
	export default {
		name: 'Item',
		model: {
			event: 'input'
		},
		components: {
			childTable,
			Item
		},
		props: {
			item: {
				type: Object,
				required: true
			},
			formConf: {
				type: Object,
				required: true
			},
			formData: {
				type: Object,
				required: true
			},
		},
		data() {
			return {
				tabCurrent: 0,
				value: ''
			}
		},
		watch: {
			value(val) {
				this.itemCopy.__config__.defaultValue = this.value
				this.$emit('input', this.itemCopy)
			}
		},
		inject: ["parameter"],
		computed: {
			itemCopy() {
				return this.$u.deepClone(this.item)
			},
			isSystem() {
				return systemList.indexOf(this.itemCopy.__config__.projectKey) > -1
			},
			isIgnore() {
				return ignoreList.indexOf(this.itemCopy.__config__.projectKey) > -1
			},
			labelWidth() {
				return this.itemCopy.__config__.labelWidth ? this.itemCopy.__config__.labelWidth * 1.5 : 100 * 1.5
			},
			label() {
				return this.item.__config__.showLabel && specialList.indexOf(this.item.__config__.projectKey) < 0 ? this.item
					.__config__.label : ''
			},
			realLabel() {
				return this.label ? (this.label + (this.formConf.labelSuffix || '')) : ''
			}
		},
		created() {
			const projectKey = this.itemCopy.__config__.projectKey
			if (projectKey === 'switch') {
				this.value = this.itemCopy.__config__.defaultValue ? 1 : 0
			} else if (projectKey === 'cascader') {
				this.value = this.itemCopy.__config__.defaultValue || []
			} else if (projectKey === 'rate') {
				this.value = this.itemCopy.__config__.defaultValue || 0
			} else if (projectKey === 'tab') {
				const list = this.itemCopy.__config__.children
				for (var i = 0; i < list.length; i++) {
					if (this.itemCopy.__config__.active == list[i].name) {
						this.tabCurrent = i
						break
					}
				}
			} else {
				this.value = this.itemCopy.__config__.defaultValue
			}
		},
		mounted() {
			if (this.itemCopy.__config__.projectKey === 'collapse') {
				uni.$on('initCollapse', () => {
					//初始化折叠面板高度高度
					this.$refs.collapseRef && this.$refs.collapseRef.init()
				})
			}
		},
		methods: {
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
			clickIcon(e) {
				if (!e.__config__.tipLabel && !e.helpMessage) return
				this.$emit('clickIcon', e)
			},
			onTabChange(index) {
				const hasBarcode = this.hasBarcodeField(this.itemCopy.__config__.children[index]);
				if (hasBarcode) uni.$emit('upDateCode')
				if (this.tabCurrent === index) return
				this.tabCurrent = index;
				this.$emit('tab-change', this.itemCopy, index)
				this.onCollapseChange()
			},
			onCollapseChange(data) {
				this.$emit('collapse-change', this.itemCopy, data)
				uni.$emit('initCollapse')
			},
			setValue(item, data) {
				this.$emit('input', item, data)
			},
			setDefaultValue(val) {
				this.value = val
			},
			arrangementInClick(item, event) {
				this.$emit('click', item, event)
			},
			onButtonClick(event) {
				this.$emit('click', this.itemCopy, event)
			},
			onBlur(data) {
				this.$emit('blur', this.itemCopy, data)
			},
			itemChange(type) {
				this.$nextTick(() => {
					this.change()
				})
			},
			change(item, data) {
				this.$nextTick(() => {
					if (!data) data = this.getDefalutData(this.itemCopy)
					this.$emit('change', this.itemCopy, data)
					this.onChange(this.itemCopy, data)
				})
			},
			onChange(item, data) {
				if (['popupSelect', 'relationForm'].includes(item.__config__.projectKey)) {
					this.setTransferFormData(data, item.__config__, item.__config__.projectKey)
				}
				if (item && item.on && item.on.change) {
					const func = this.linzen.getScriptFunc(item.on.change);
					if (!func) return
					func.call(this, {
						data: data ? data : item.__config__.defaultValue,
						...this.parameter
					})
				}
				uni.$emit('subChange', this.itemCopy, data)
			},
			setTransferFormData(data, config, projectKey) {
				if (!config.transferList.length) return;
				for (let index = 0; index < config.transferList.length; index++) {
					const element = config.transferList[index];
					// this.parameter.setFormData(element.sourceValue, data[projectKey == 'popupSelect' ? element
					// 	.targetField + '_linzenId' : element.targetField]);
					this.parameter.setFormData(element.sourceValue, data[element.targetField])
				}
			},
			getDefalutData(item) {
				let config = item.__config__
				let data = ''
				if (['select', 'radio', 'checkbox'].includes(config.projectKey)) {
					let options = item.options
					let props = item.props
					if (config.projectKey === 'checkbox' || (config.projectKey === 'select' && item.multiple)) {
						let _data = []
						outer: for (let i = 0; i < config.defaultValue.length; i++) {
							inner: for (let j = 0; j < options.length; j++) {
								if (config.defaultValue[i] === options[j][props.value]) {
									_data.push(options[j])
									break inner
								}
							}
						}
						data = _data
					} else {
						let _data = {}
						for (let i = 0; i < options.length; i++) {
							if (config.defaultValue === options[i][props.value]) {
								_data = options[i]
								break
							}
						}
						data = _data
					}
				}
				return data
			},
			toDetail(item) {
				this.$emit('toDetail', item)
			},
			toTableDetail(item, value) {
				item.__config__.defaultValue = value
				this.$emit('toDetail', item)
			},
		}
	}
</script>
<style>
	.close-btn {
		height: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
	}
</style>