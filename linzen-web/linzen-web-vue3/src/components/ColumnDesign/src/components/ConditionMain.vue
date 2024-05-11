<template>
  <div class="condition-main">
    <div class="mb-10px" v-if="conditionList.length">
      <span class="mr-10px">分组匹配逻辑</span>
      <linzen-select v-model:value="matchLogic" placeholder="请选择" :options="logicOptions" class="!w-68px" />
    </div>
    <div class="condition-list" v-for="(item, index) in conditionList" :key="index">
      <a-row :gutter="8">
        <a-col :span="6" class="!flex align-middle flex-nowrap">
          <span class="leading-32px mr-10px">条件逻辑</span>
          <linzen-select v-model:value="item.logic" placeholder="请选择" :options="logicOptions" class="!flex-1" />
        </a-col>
        <a-col :span="5">
          <a-button preIcon="icon-linzen icon-linzen-btn-add" @click="addItem(index)" class="!w-full">添加条件</a-button>
        </a-col>
        <a-col :span="5">
          <a-button preIcon="icon-linzen icon-linzen-nav-close" @click="delGroup(index)">删除分组</a-button>
        </a-col>
      </a-row>
      <a-row :gutter="8" v-for="(child, childIndex) in item.groups" :key="index + childIndex" class="mt-10px">
        <a-col :span="6">
          <linzen-select
            v-model:value="child.field"
            :options="fieldOptions"
            placeholder="请选择字段"
            showSearch
            allowClear
            :fieldNames="{ options: 'options1' }"
            @change="(val, data) => onFieldChange(val, data, child, index, childIndex)" />
        </a-col>
        <a-col :span="5">
          <linzen-select
            v-model:value="child.symbol"
            placeholder="运算符号"
            :options="getSymbolOptions(child.projectKey)"
            :dropdownMatchSelectWidth="false"
            @change="(val, data) => onSymbolChange(val, data, child)" />
        </a-col>
        <a-col :span="4" v-if="showFieldValueType">
          <linzen-select
            v-model:value="child.fieldValueType"
            :options="sourceTypeOptions"
            placeholder="请选择字段"
            :disabled="child.disabled"
            @change="child.fieldValue = undefined" />
        </a-col>
        <a-col :span="8" v-if="child.fieldValueType === 1">
          <linzen-select
            v-model:value="child.fieldValue"
            :options="valueFieldOptions"
            placeholder="请选择字段"
            showSearch
            allowClear
            :fieldNames="{ options: 'options1' }"
            :disabled="child.disabled" />
        </a-col>
        <a-col :span="showFieldValueType ? 8 : 12" v-if="child.fieldValueType !== 1">
          <template v-if="child.projectKey === 'inputNumber'">
            <linzen-number-range v-model:value="child.fieldValue" :precision="child.precision" :disabled="child.disabled" v-if="child.symbol == 'between'" />
            <linzen-input-number v-model:value="child.fieldValue" :precision="child.precision" :disabled="child.disabled" placeholder="请输入" v-else />
          </template>
          <template v-else-if="child.projectKey === 'calculate'">
            <linzen-number-range v-model:value="child.fieldValue" :precision="child.precision || 0" :disabled="child.disabled" v-if="child.symbol == 'between'" />
            <linzen-input-number v-model:value="child.fieldValue" :precision="child.precision || 0" :disabled="child.disabled" placeholder="请输入" v-else />
          </template>
          <template v-else-if="['rate', 'slider'].includes(child.projectKey)">
            <linzen-number-range
              v-model:value="child.fieldValue"
              :precision="child.projectKey == 'rate' && child.allowHalf ? 1 : 0"
              :disabled="child.disabled"
              v-if="child.symbol == 'between'" />
            <linzen-input-number
              v-model:value="child.fieldValue"
              :precision="child.projectKey == 'rate' && child.allowHalf ? 1 : 0"
              :disabled="child.disabled"
              placeholder="请输入"
              v-else />
          </template>
          <div class="pt-3px" v-else-if="child.projectKey === 'switch'">
            <linzen-switch v-model:value="child.fieldValue" :disabled="child.disabled" />
          </div>
          <template v-else-if="child.projectKey === 'colorPicker'">
            <linzen-color-picker v-model:value="child.fieldValue" size="small" :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'timePicker'">
            <linzen-time-range v-model:value="child.fieldValue" :format="child.format" allowClear :disabled="child.disabled" v-if="child.symbol == 'between'" />
            <linzen-time-picker v-model:value="child.fieldValue" :format="child.format" allowClear :disabled="child.disabled" v-else />
          </template>
          <template v-else-if="['datePicker', 'createTime', 'updateTime'].includes(child.projectKey)">
            <linzen-date-range
              v-model:value="child.fieldValue"
              :format="child.format || 'YYYY-MM-DD HH:mm:ss'"
              allowClear
              :disabled="child.disabled"
              v-if="child.symbol == 'between'" />
            <linzen-date-picker v-model:value="child.fieldValue" :format="child.format || 'YYYY-MM-DD HH:mm:ss'" allowClear :disabled="child.disabled" v-else />
          </template>
          <template v-else-if="['organizeSelect', 'currOrganize'].includes(child.projectKey)">
            <linzen-organize-select
              v-model:value="child.fieldValue"
              allowClear
              :selectType="child.selectType"
              :ableIds="child.ableIds"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="['depSelect'].includes(child.projectKey)">
            <linzen-dep-select
              v-model:value="child.fieldValue"
              allowClear
              :selectType="child.selectType"
              :ableIds="child.ableIds"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'roleSelect'">
            <linzen-role-select
              v-model:value="child.fieldValue"
              allowClear
              :selectType="child.selectType"
              :ableIds="child.ableIds"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'groupSelect'">
            <linzen-group-select
              v-model:value="child.fieldValue"
              allowClear
              :selectType="child.selectType"
              :ableIds="child.ableIds"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'posSelect'">
            <linzen-pos-select
              v-model:value="child.fieldValue"
              allowClear
              :selectType="child.selectType"
              :ableIds="child.ableIds"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'currPosition'">
            <linzen-pos-select v-model:value="child.fieldValue" allowClear :multiple="child.multiple" :disabled="child.disabled" />
          </template>
          <template v-else-if="['createUser', 'updateUser'].includes(child.projectKey)">
            <linzen-user-select v-model:value="child.fieldValue" allowClear :multiple="child.multiple" :disabled="child.disabled" />
          </template>
          <template v-else-if="['userSelect'].includes(child.projectKey)">
            <linzen-user-select
              v-model:value="child.fieldValue"
              allowClear
              :selectType="child.selectType != 'all' && child.selectType != 'custom' ? 'all' : child.selectType"
              :ableIds="child.ableIds"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="['usersSelect'].includes(child.projectKey)">
            <linzen-users-select
              v-model:value="child.fieldValue"
              allowClear
              :selectType="child.selectType"
              :ableIds="child.ableIds"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'areaSelect'">
            <linzen-area-select
              v-model:value="child.fieldValue"
              :level="child.level"
              allowClear
              :multiple="child.multiple"
              :disabled="child.disabled"
              :key="item.cellKey" />
          </template>
          <template v-else-if="['select', 'radio', 'checkbox'].includes(child.projectKey)">
            <linzen-select
              v-model:value="child.fieldValue"
              placeholder="请选择"
              showSearch
              allowClear
              :options="child.options"
              :fieldNames="child.props"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'cascader'">
            <linzen-cascader
              v-model:value="child.fieldValue"
              :options="child.options"
              :fieldNames="child.props"
              :showAllLevels="child.showAllLevels"
              showSearch
              allowClear
              placeholder="请选择"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'treeSelect'">
            <linzen-tree-select
              v-model:value="child.fieldValue"
              :options="child.options"
              :fieldNames="child.props"
              showSearch
              allowClear
              placeholder="请选择"
              :multiple="child.multiple"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'relationForm'">
            <linzen-relation-form
              v-model:value="child.fieldValue"
              placeholder="请选择"
              :modelId="child.modelId"
              allowClear
              :columnOptions="child.columnOptions"
              :relationField="child.relationField"
              :hasPage="child.hasPage"
              :pageSize="child.pageSize"
              :popupType="child.popupType"
              :popupTitle="child.popupTitle"
              :popupWidth="child.popupWidth"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'popupSelect' || child.projectKey === 'popupTableSelect'">
            <linzen-popup-select
              v-model:value="child.fieldValue"
              placeholder="请选择"
              :interfaceId="child.interfaceId"
              allowClear
              :multiple="child.multiple"
              :columnOptions="child.columnOptions"
              :propsValue="child.propsValue"
              :templateJson="child.templateJson"
              :relationField="child.relationField"
              :hasPage="child.hasPage"
              :pageSize="child.pageSize"
              :popupType="child.popupType"
              :popupTitle="child.popupTitle"
              :popupWidth="child.popupWidth"
              :disabled="child.disabled" />
          </template>
          <template v-else-if="child.projectKey === 'autoComplete'">
            <linzen-auto-complete
              v-model:value="child.fieldValue"
              placeholder="请输入"
              allowClear
              :interfaceId="child.interfaceId"
              :relationField="child.relationField"
              :templateJson="child.templateJson"
              :total="child.total"
              :disabled="child.disabled" />
          </template>
          <template v-else>
            <a-input v-model:value="child.fieldValue" placeholder="请输入" allowClear :disabled="child.disabled" />
          </template>
        </a-col>
        <a-col :span="1" class="text-center">
          <i class="icon-linzen icon-linzen-btn-clearn" @click="delItem(index, childIndex)" />
        </a-col>
      </a-row>
    </div>
    <div class="query-noData" v-show="!conditionList.length && isSuperQuery">
      <img src="../../../../assets/images/query-noData.png" class="noData-img" />
      <div class="noData-txt">
        <span>没有任何查询条件</span>
        <a-divider type="vertical"></a-divider>
        <span class="link-text" @click="addGroup">点击新增</span>
      </div>
    </div>
    <span class="link-text mt-10px inline-block" @click="addGroup()" v-show="conditionList.length || !isSuperQuery">
      <i class="icon-linzen icon-linzen-btn-add text-14px mr-4px"></i>添加分组
    </span>
  </div>
</template>

<script lang="ts" setup>
  import { reactive, toRefs } from 'vue';
  import { getDictionaryDataSelector } from '/@/api/systemData/dictionary';
  import { getDataInterfaceRes } from '/@/api/systemData/dataInterface';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { cloneDeep } from 'lodash-es';
  import { dyOptionsList } from '/@/components/FormGenerator/src/helper/config';
  import { LinzenRelationForm } from '/@/components/Linzen';
  import { isEmpty } from '/@/utils/is';

  interface State {
    conditionList: any[];
    fieldOptions: any[];
    matchLogic: string;
  }

  const props = defineProps({
    isSuperQuery: { type: Boolean, default: false },
    defaultAddEmpty: { type: Boolean, default: false },
    showFieldValueType: { type: Boolean, default: false },
    valueFieldOptions: { type: Array, default: () => [] },
  });
  defineExpose({
    init,
    confirm,
    updateConditionList,
  });

  const { createMessage } = useMessage();
  const notSupportList = [
    'relationFormAttr',
    'popupAttr',
    'uploadFile',
    'uploadImg',
    'colorPicker',
    'editor',
    'link',
    'button',
    'text',
    'alert',
    'table',
    'sign',
  ];
  const emptyChildItem = {
    field: '',
    symbol: '',
    projectKey: '',
    fieldValueType: props.showFieldValueType ? 1 : 2,
    fieldValue: undefined,
    fieldValueLinzenKey: '',
    cellKey: +new Date(),
  };
  const emptyItem = { logic: 'and', groups: [emptyChildItem] };
  const sourceTypeOptions = [
    { id: 1, fullName: '字段' },
    { id: 2, fullName: '自定义' },
  ];
  const logicOptions = [
    { id: 'and', fullName: 'and' },
    { id: 'or', fullName: 'or' },
  ];
  const baseSymbolOptions = [
    { id: '==', fullName: '等于' },
    { id: '<>', fullName: '不等于' },
    { id: 'like', fullName: '包含' },
    { id: 'notLike', fullName: '不包含' },
    { id: 'null', fullName: '为空' },
    { id: 'notNull', fullName: '不为空' },
  ];
  const rangeSymbolOptions = [
    { id: '>=', fullName: '大于等于' },
    { id: '>', fullName: '大于' },
    { id: '==', fullName: '等于' },
    { id: '<=', fullName: '小于等于' },
    { id: '<', fullName: '小于' },
    { id: '<>', fullName: '不等于' },
    { id: 'between', fullName: '介于' },
    { id: 'null', fullName: '为空' },
    { id: 'notNull', fullName: '不为空' },
  ];
  const selectSymbolOptions = [
    { id: '==', fullName: '等于' },
    { id: '<>', fullName: '不等于' },
    { id: 'in', fullName: '包含任意一个' },
    { id: 'notIn', fullName: '不包含任意一个' },
    { id: 'null', fullName: '为空' },
    { id: 'notNull', fullName: '不为空' },
  ];
  const switchSymbolOptions = [
    { id: '==', fullName: '等于' },
    { id: '<>', fullName: '不等于' },
  ];
  const locationSymbolOptions = [
    { id: 'like', fullName: '包含' },
    { id: 'notLike', fullName: '不包含' },
    { id: 'null', fullName: '为空' },
    { id: 'notNull', fullName: '不为空' },
  ];
  const relationFormSymbolOptions = [...switchSymbolOptions, { id: 'null', fullName: '为空' }, { id: 'notNull', fullName: '不为空' }];
  const useRangeSymbolList = ['calculate', 'inputNumber', 'rate', 'slider', 'datePicker', 'timePicker', 'createTime', 'updateTime'];
  const useSelectSymbolList = [
    'radio',
    'checkbox',
    'select',
    'treeSelect',
    'cascader',
    'areaSelect',
    'organizeSelect',
    'depSelect',
    'posSelect',
    'userSelect',
    'usersSelect',
    'roleSelect',
    'groupSelect',
    'createUser',
    'updateUser',
    'currOrganize',
    'currPosition',
    'popupTableSelect',
  ];
  const useSwitchSymbolList = ['switch'];
  const useRelationFormSymbolList = ['relationForm', 'popupSelect'];
  const state = reactive<State>({
    conditionList: [],
    fieldOptions: [],
    matchLogic: 'and',
  });
  const { conditionList, fieldOptions, matchLogic } = toRefs(state);

  function init(data) {
    updateConditionList(data);
    const fieldOptions = data.fieldOptions.filter(o => !notSupportList.includes(o.__config__.projectKey));
    state.fieldOptions = buildOptions(fieldOptions);
    if (!state.conditionList.length && props.defaultAddEmpty) addGroup();
  }
  function updateConditionList(data) {
    state.conditionList = cloneDeep(data.conditionList || []);
    state.matchLogic = data.matchLogic || 'and';
  }
  function buildOptions(componentList) {
    componentList.forEach(cur => {
      cur.disabled = false;
      const config = cur.__config__;
      if (dyOptionsList.includes(config.projectKey)) {
        if (config.dataType === 'dictionary' && config.dictionaryType) {
          cur.options = [];
          getDictionaryDataSelector(config.dictionaryType).then(res => {
            cur.options = res.data.list;
          });
        }
        if (config.dataType === 'dynamic' && config.propsUrl) {
          cur.options = [];
          const query = { paramList: config.templateJson ? config.templateJson : [] };
          getDataInterfaceRes(config.propsUrl, query).then(res => {
            cur.options = Array.isArray(res.data) ? res.data : [];
          });
        }
      }
    });
    return componentList;
  }
  function onFieldChange(val, data, item, index, childIndex) {
    item.cellKey = +new Date();
    if (item.fieldValueType != 1) {
      item.fieldValue = undefined;
      item.fieldValueLinzenKey = '';
    }
    const newItem = cloneDeep(emptyChildItem);
    for (let key of Object.keys(newItem)) {
      newItem[key] = item[key];
    }
    if (!val) {
      item.projectKey = '';
      item.symbol = undefined;
      item.disabled = false;
      return;
    }
    item = { ...newItem, ...data };
    const config = data.__config__;
    if (item.projectKey != config.projectKey) item.symbol = undefined;
    item.projectKey = data.__config__?.projectKey || '';
    item.disabled = ['null', 'notNull'].includes(item.symbol);
    item.multiple = ['in', 'notIn'].includes(item.symbol);
    state.conditionList[index].groups[childIndex] = item;
  }
  function onSymbolChange(val, _data, item) {
    item.fieldValue = undefined;
    item.disabled = ['null', 'notNull'].includes(val);
    item.multiple = ['in', 'notIn'].includes(val);
    if (props.showFieldValueType && ['null', 'notNull'].includes(val)) {
      item.fieldValueType = 1;
      item.fieldValueLinzenKey = '';
    }
  }
  function addItem(index) {
    state.conditionList[index].groups.push(cloneDeep(emptyChildItem));
  }
  function delItem(index, childIndex) {
    state.conditionList[index].groups.splice(childIndex, 1);
  }
  function addGroup() {
    state.conditionList.push(cloneDeep(emptyItem));
  }
  function delGroup(index) {
    state.conditionList.splice(index, 1);
  }
  function getSymbolOptions(projectKey) {
    if (useSwitchSymbolList.includes(projectKey)) return switchSymbolOptions;
    if (useRelationFormSymbolList.includes(projectKey)) return relationFormSymbolOptions;
    if (useRangeSymbolList.includes(projectKey)) return rangeSymbolOptions;
    if (useSelectSymbolList.includes(projectKey)) return selectSymbolOptions;
    if (projectKey == 'location') return locationSymbolOptions;
    return baseSymbolOptions;
  }
  function exist() {
    let isOk = true;
    for (let i = 0; i < state.conditionList.length; i++) {
      const e = state.conditionList[i];
      for (let j = 0; j < e.groups.length; j++) {
        const child = e.groups[j];
        if (!child.field) {
          createMessage.warning('条件字段不能为空');
          isOk = false;
          return;
        }
        if (!child.symbol) {
          createMessage.warning('条件符号不能为空');
          isOk = false;
          return;
        }
        if (!['null', 'notNull'].includes(child.symbol) && ((!child.fieldValue && child.fieldValue !== 0) || isEmpty(child.fieldValue))) {
          createMessage.warning('数据值不能为空');
          isOk = false;
          return;
        }
      }
    }
    return isOk;
  }
  function confirm() {
    if (!exist()) return false;
    return {
      matchLogic: state.matchLogic,
      conditionList: cloneDeep(state.conditionList),
    };
  }
</script>
