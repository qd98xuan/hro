<template>
	<u-form :model="formData" ref="dataForm" :errorType="['toast']" label-position="left" label-width="150">
		<u-form-item :label="item.label" :prop="item.id" v-for="(item, i) in formConfCopy" :key="i">
			<block v-if="useInputList.indexOf(item.__config__.projectKey) > -1 || item.isKeyword">
				<u-input input-align='right' v-model="formData[item.id]" :placeholder="'请输入'+item.label" />
			</block>
			<block v-if="item.__config__.projectKey==='inputNumber'|| item.__config__.projectKey==='calculate'">
				<linzen-number-box v-if="item.__config__.isFromParam" v-model="formData[item.id]" controls />
				<linzen-num-range v-model="formData[item.id]"
					:precision="!item.precision && item.__config__.projectKey=='calculate'?0:item.precision " v-else />
			</block>
			<block v-if="item.__config__.projectKey==='slider' || item.__config__.projectKey==='rate'">
				<linzen-num-range v-model="formData[item.id]"
					:precision="!item.precision && item.__config__.projectKey=='calculate'?0:item.precision " />
			</block>
			<view v-if="item.__config__.projectKey==='switch'" class="u-flex u-form-item-switch">
				<linzen-switch v-model="formData[item.id]"></linzen-switch>
			</view>
			<block v-if="['select','radio','checkbox'].indexOf(item.__config__.projectKey) > -1">
				<linzen-select v-model="formData[item.id]" :placeholder="'请选择'+item.label" :options="item.options"
					:props="item.props" :multiple="item.searchMultiple">
				</linzen-select>
			</block>
			<block v-if="item.__config__.projectKey==='cascader'">
				<linzen-cascader v-model="formData[item.id]" :placeholder="'请选择'+item.label" :options="item.options"
					:props="item.props" :filterable='item.filterable' :showAllLevels="item.showAllLevels"
					:multiple="item.searchMultiple">
				</linzen-cascader>
			</block>
			<!-- 下拉补全 -->
			<block v-if="item.__config__.projectKey==='autoComplete'">
				<linzen-auto-complete :interfaceName="item.interfaceName" :placeholder="item.placeholder"
					:interfaceId="item.interfaceId" :total="item.total" v-model="formData[item.id]"
					:templateJson="item.templateJson" :formData='formData' :relationField="item.relationField"
					:propsValue="item.propsValue" :clearable='item.clearable'></linzen-auto-complete>
			</block>
			<block v-if="useDateList.indexOf(item.__config__.projectKey) > -1 || item.__config__.projectKey==='timePicker'">
				<linzen-date-time v-if="item.__config__.isFromParam" :type="item.type||'date'" v-model="formData[item.id]"
					:placeholder="item.placeholder" :disabled="item.disabled" :startTime="item.startTime"
					:endTime='item.endTime' :format="item.format">
				</linzen-date-time>
				<linzen-date-range v-else v-model="formData[item.id]" :format='item.format'
					:projectKey="item.projectKey==='timePicker'?'time':'date'" />
			</block>
			<block v-if="item.__config__.projectKey==='groupSelect'">
				<linzen-group-select :vModel='item.id' v-model="formData[item.id]" :multiple="item.searchMultiple"
					:disabled="item.disabled" :placeholder="item.placeholder" :ableIds="item.ableIds"
					:selectType="item.selectType" />
			</block>
			<block v-if="item.__config__.projectKey==='roleSelect'">
				<linzen-role-select :vModel='item.id' v-model="formData[item.id]" :multiple="item.searchMultiple"
					:disabled="item.disabled" :placeholder="item.placeholder" :ableIds="item.ableIds"
					:selectType="item.selectType" />
			</block>
			<block v-if="item.__config__.projectKey==='organizeSelect'||item.__config__.projectKey==='currOrganize'">
				<linzen-com-select v-model="formData[item.id]" :placeholder="'请选择'+item.label"
					:multiple="item.searchMultiple" :ableIds="item.ableIds" :selectType="item.selectType">
				</linzen-com-select>
			</block>
			<block v-if="item.__config__.projectKey==='depSelect'||item.__config__.projectKey==='currDept'">
				<linzen-postordep-select type="department" v-model="formData[item.id]" :placeholder="'请选择'+item.label"
					:ableIds="item.ableIds" :selectType="item.selectType" :multiple="item.searchMultiple">
				</linzen-postordep-select>
			</block>
			<block v-if="item.__config__.projectKey==='posSelect'||item.__config__.projectKey==='currPosition'">
				<linzen-postordep-select type="position" v-model="formData[item.id]" :placeholder="'请选择'+item.label"
					:ableIds="item.ableIds" :selectType="item.selectType" :multiple="item.searchMultiple">
				</linzen-postordep-select>
			</block>
			<block v-if="['userSelect','createUser', 'modifyUser'].indexOf(item.__config__.projectKey) > -1">
				<linzen-user-select v-model="formData[item.id]" :placeholder="'请选择'+item.label" :ableIds="item.ableIds"
					:selectType="item.selectType!='custom'?'all':'custom'" :bh="650" :multiple="item.searchMultiple">
				</linzen-user-select>
			</block>
			<!-- 用户组件 -->
			<block v-if="item.__config__.projectKey==='usersSelect'">
				<linzen-user-choice v-model="formData[item.id]" :multiple="item.searchMultiple"
					:placeholder="'请选择'+item.label" :selectType="item.selectType" :ableIds="item.ableIds"
					:clearable="item.clearable">
				</linzen-user-choice>
			</block>
			<block v-if="item.__config__.projectKey==='treeSelect'">
				<linzen-tree-select v-model="formData[item.id]" :options="item.options" :props="item.props"
					:placeholder="'请选择'+item.label" :filterable='item.filterable' :multiple="item.searchMultiple">
				</linzen-tree-select>
			</block>
			<block v-if="item.__config__.projectKey==='areaSelect'">
				<linzen-city-select v-model="formData[item.id]" :placeholder="'请选择'+item.label" :level="item.level"
					:multiple="item.searchMultiple">
				</linzen-city-select>
			</block>
		</u-form-item>
	</u-form>
</template>
<script>
	import {
		getDictionaryDataSelector,
		getDataInterfaceRes
	} from '@/api/common'
	const dyOptionsList = ['radio', 'checkbox', 'select', 'cascader', 'treeSelect']
	const useInputList = ['input', 'textarea', 'text', 'billRule', 'location']
	const useDateList = ['datePicker', 'createTime', 'modifyTime']
	const useArrList = ['cascader', 'areaSelect', 'inputNumber', 'calculate', ...useDateList]
	const isDef = ['userSelect', 'posSelect', 'depSelect', 'organizeSelect']
	export default {
		props: ['formConf', 'webType', 'searchFormData'],
		data() {
			const data = {
				useInputList,
				useDateList,
				formConfCopy: this.$u.deepClone(this.formConf),
				formData: this.$u.deepClone(this.searchFormData),
			}
			this.initRelationForm(data.formConfCopy)
			this.initFormData(data.formConfCopy, data.formData)
			return data
		},
		methods: {
			initFormData(componentList, formData) {
				componentList.forEach(cur => {
					const config = cur.__config__
					if (cur.id && !formData[cur.id]) {
						formData[cur.id] = cur.value;
					}
					if (dyOptionsList.indexOf(config.projectKey) > -1) {
						let isTreeSelect = config.projectKey === 'treeSelect' || config.projectKey === 'cascader'
						if (config.dataType === 'dictionary' && config.dictionaryType) {
							cur.options = []
							getDictionaryDataSelector(config.dictionaryType).then(res => {
								cur.options = res.data.list
							})
						}
						if (config.dataType === 'dynamic' && config.propsUrl) {
							cur.options = []
							getDataInterfaceRes(config.propsUrl).then(res => {
								cur.options = Array.isArray(res.data) ? res.data : []
							})
						}
					}
				})
			},
			initRelationForm(componentList) {
				componentList.forEach(cur => {
					const config = cur.__config__
					if (config.projectKey == 'relationFormAttr' || config.projectKey == 'popupAttr') {
						const relationKey = cur.relationField.split("_linzenTable_")[0]
						componentList.forEach(item => {
							const noVisibility = Array.isArray(item.__config__.visibility) && !item
								.__config__.visibility.includes('app')
							if ((relationKey == item.id) && (noVisibility || !!item.__config__
									.noShow)) {
								cur.__config__.noShow = true
							}
						})
					}
					if (cur.__config__.children && cur.__config__.children.length) this.initRelationForm(cur
						.__config__.children)
				})
			},
			submitForm() {
				this.$refs.dataForm.validate(valid => {
					if (!valid) return
					for (let key in this.formData) {
						if (!this.formData[key]) this.formData[key] = undefined
						if (this.formData[key] && Array.isArray(this.formData[key]) && !this.formData[key]
							.length) {
							this.formData[key] = undefined
						}
					}
					this.$emit('submit', this.formData)
				})
			}
		}
	}
</script>
<style lang="scss">
	/deep/.u-form-item {
		min-height: 112rpx;
	}
</style>