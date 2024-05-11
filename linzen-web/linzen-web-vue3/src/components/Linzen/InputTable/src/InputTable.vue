<template>
  <div :class="prefixCls">
    <LinzenGroupTitle
      :content="config.__config__.label"
      :helpMessage="config.__config__.tipLabel"
      v-if="config.__config__.showTitle && config.__config__.label"
      :bordered="false" />
    <a-table
      :data-source="tableFormData"
      :columns="getColumns"
      size="small"
      rowKey="linzenId"
      :pagination="false"
      :scroll="{ x: 'max-content' }"
      :rowSelection="getRowSelection"
      :bordered="formStyle === 'word-form' || !!config.__config__?.complexHeaderList?.length">
      <template #headerCell="{ column }">
        <span class="required-sign" v-if="column.__config__ && column.__config__.required">*</span>{{ column.title }}
        <BasicHelp v-if="column.title && column.__config__ && column.__config__.tipLabel" :text="column.__config__.tipLabel" />
      </template>
      <template #bodyCell="{ column, index }">
        <template v-if="column.key === 'index'">{{ index + 1 }}</template>
        <template v-for="(item, cIndex) in tableData">
          <template v-if="column.key === item.__vModel__ && column.__config__.formId === item.__config__.formId">
            <div :key="item.__config__.formId">
              <LinzenRelationForm
                v-if="item.__config__.tag === 'LinzenRelationForm'"
                :rowIndex="index"
                :tableVModel="config.__vModel__"
                :componentVModel="item.__vModel__"
                v-model:value="tableFormData[index][cIndex].value"
                v-bind="getConfById(item.__config__.formId, index)"
                :formData="formData"
                @blur="onFormBlur(index, cIndex, $event)"
                @change="(val, data) => onFormDataChange(index, cIndex, item.__config__.tag, val, data)" />
              <LinzenPopupSelect
                v-else-if="item.__config__.tag === 'LinzenPopupSelect'"
                :rowIndex="index"
                :tableVModel="config.__vModel__"
                :componentVModel="item.__vModel__"
                v-model:value="tableFormData[index][cIndex].value"
                v-bind="getConfById(item.__config__.formId, index)"
                :formData="formData"
                @blur="onFormBlur(index, cIndex, $event)"
                @change="(val, data) => onFormDataChange(index, cIndex, item.__config__.tag, val, data)" />
              <component
                v-else
                :is="item.__config__.tag"
                :rowIndex="index"
                :tableVModel="config.__vModel__"
                :componentVModel="item.__vModel__"
                v-model:value="tableFormData[index][cIndex].value"
                v-bind="getConfById(item.__config__.formId, index)"
                :formData="formData"
                @blur="onFormBlur(index, cIndex, $event)"
                @change="(val, data) => onFormDataChange(index, cIndex, item.__config__.tag, val, data)" />
              <div class="error-tip required-sign" v-show="!tableFormData[index][cIndex].valid">{{ column.title }}不能为空</div>
              <div class="error-tip required-sign" v-show="tableFormData[index][cIndex].valid && !tableFormData[index][cIndex].regValid">
                {{ tableFormData[index][cIndex].regErrorText }}
              </div>
            </div>
          </template>
        </template>
        <template v-if="column.key === 'action'">
          <a-space v-if="config?.columnBtnsList?.length">
            <template v-for="(it, i) in config.columnBtnsList">
              <a-button
                v-if="it.show"
                :key="i"
                class="action-btn"
                type="link"
                :color="it.value == 'remove' ? 'error' : ''"
                @click="columnBtnsHandel(it, index)"
                size="small">
                {{ it.label }}
              </a-button>
            </template>
          </a-space>
        </template>
      </template>
      <template #summary v-if="tableFormData.length && config.showSummary">
        <a-table-summary fixed>
          <a-table-summary-row>
            <a-table-summary-cell :index="0">合计</a-table-summary-cell>
            <a-table-summary-cell v-for="(item, index) in getColumnSum" :key="index" :index="index + 1" :align="getSummaryCellAlign(index)">{{
              item
            }}</a-table-summary-cell>
            <a-table-summary-cell :index="getColumnSum.length + 1" v-if="!disabled"></a-table-summary-cell>
          </a-table-summary-row>
        </a-table-summary>
      </template>
    </a-table>
    <a-space class="input-table-footer-btn" v-if="!disabled && config?.footerBtnsList?.length">
      <template v-for="item in config.footerBtnsList">
        <a-button
          v-if="item.show"
          :key="item.value"
          :type="item.btnType == 'text' ? 'link' : item.btnType"
          :preIcon="item.btnIcon"
          @click="footerBtnsHandle(item)">
          {{ item.label }}
        </a-button>
      </template>
    </a-space>
    <SelectModal :config="actionConfig" :formData="formData" ref="selectModal" @select="addForSelect" />
  </div>
</template>

<script lang="ts" setup>
  import { computed, inject, reactive, unref, nextTick, toRefs, ref, toRaw } from 'vue';
  import { useDesign } from '/@/hooks/web/useDesign';
  import { dyOptionsList } from '/@/components/FormGenerator/src/helper/config';
  import { getDataInterfaceRes } from '/@/api/systemData/dataInterface';
  import { Form } from 'ant-design-vue';
  import { getScriptFunc, getDateTimeUnit, thousandsFormat } from '/@/utils/linzen';
  import { getRealProps } from '/@/components/FormGenerator/src/helper/transform';
  import SelectModal from '/@/components/CommonModal/src/SelectModal.vue';
  import { LinzenRelationForm } from '/@/components/Linzen';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useI18n } from '/@/hooks/web/useI18n';
  import dayjs from 'dayjs';
  import { useBaseStore } from '/@/store/modules/base';
  import type { TableProps } from 'ant-design-vue';
  import { cloneDeep } from 'lodash-es';
  import { buildUUID } from '/@/utils/uuid';

  interface State {
    tableFormData: any[];
    tableData: any[];
    activeRowIndex: number;
    isAddRow: boolean;
    dataInterfaceInfo: any[];
    selectedRowKeys: any[];
    actionConfig: any;
  }

  defineOptions({ name: 'LinzenInputTable', inheritAttrs: false });
  const props = defineProps({
    config: {
      type: Object,
      default: () => {},
    },
    value: {
      type: Array,
      default: () => [],
    },
    formData: Object,
    relations: Object,
    vModel: String,
    disabled: {
      type: Boolean,
      default: false,
    },
  });
  const emit = defineEmits(['update:value', 'change']);
  const { createMessage, createConfirm } = useMessage();
  const { t } = useI18n();
  const baseStore = useBaseStore();
  const formItemContext = Form.useInjectFormItemContext();
  const parameter: any = inject('parameter');
  const formStyle: string | undefined = inject('formStyle');
  const { prefixCls } = useDesign('input-table');
  const state = reactive<State>({
    tableFormData: [],
    tableData: [],
    activeRowIndex: 0,
    isAddRow: true,
    dataInterfaceInfo: [],
    selectedRowKeys: [],
    actionConfig: {},
  });
  const { tableFormData, tableData, actionConfig } = toRefs(state);
  const selectModal = ref(null);

  defineExpose({
    handleRelationForParent,
    submit,
    setTableFormData,
    setTableShowOrHide,
    resetTable,
    reset,
  });

  const childRelations = computed(() => {
    let obj = {};
    for (let key in props.relations) {
      if (key.includes('-')) {
        let tableVModel = key.split('-')[0];
        if (tableVModel === props.vModel) {
          let newKey = key.split('-')[1];
          obj[newKey] = props.relations[key];
        }
      }
    }
    return obj;
  });
  const getColumns = computed(() => {
    const noColumn = { width: 50, title: '序号', dataIndex: 'index', key: 'index', align: 'center', customRender: ({ index }) => index + 1, fixed: 'left' };
    const actionColumn = { title: '操作', dataIndex: 'action', key: 'action', width: getActionWidth(), fixed: 'right' };
    let list = state.tableData
      .map(o => ({
        ...o,
        dataIndex: o.__vModel__,
        key: o.__vModel__,
        width: o.__config__.columnWidth,
        title: o.__config__.label,
        align: o.__config__.tableAlign || 'left',
        fixed: o.__config__.tableFixed == 'left' || o.__config__.tableFixed == 'right' ? o.__config__.tableFixed : false,
        customCell: () => ({ class: 'align-top' }),
      }))
      .filter(o => !o.__config__.noShow && (!o.__config__.visibility || (Array.isArray(o.__config__.visibility) && o.__config__.visibility.includes('pc'))));
    let columnList = list;
    let complexHeaderList: any[] = props.config.__config__.complexHeaderList || [];
    if (complexHeaderList.length) {
      let childColumns: any[] = [];
      for (let i = 0; i < complexHeaderList.length; i++) {
        const e = complexHeaderList[i];
        e.title = e.fullName;
        e.align = e.align;
        e.children = [];
        e.projectKey = 'complexHeader';
        if (e.childColumns?.length) {
          childColumns.push(...e.childColumns);
          for (let j = 0; j < list.length; j++) {
            const o = list[j];
            if (e.childColumns.includes(o.__vModel__) && o.__config__.tableFixed !== 'left' && o.__config__.tableFixed !== 'right')
              e.children.push({ ...toRaw(o), align: o.__config__.tableAlign || 'left' });
          }
        }
      }
      complexHeaderList = complexHeaderList.filter(o => o.children.length);
      for (let i = 0; i < list.length; i++) {
        const item = list[i];
        if (!childColumns.includes(item.__vModel__) || item.__config__.tableFixed === 'left' || item.__config__.tableFixed === 'right')
          complexHeaderList.push(item);
      }
      columnList = complexHeaderList;
    }
    let columns = props.disabled || getActionWidth() == 0 ? [noColumn, ...columnList] : [noColumn, ...columnList, actionColumn];
    const leftFixedList = columns.filter(o => o.fixed === 'left');
    const rightFixedList = columns.filter(o => o.fixed === 'right');
    const noFixedList = columns.filter(o => o.fixed !== 'left' && o.fixed !== 'right');
    return [...leftFixedList, ...noFixedList, ...rightFixedList];
  });
  const getSummaryColumn = computed(() => {
    let defaultColumns = unref(getColumns);
    let columns: any[] = [];
    for (let i = 0; i < defaultColumns.length; i++) {
      const e = defaultColumns[i];
      if (e.projectKey === 'table' || e.projectKey === 'complexHeader') {
        if (e.children?.length) columns.push(...e.children);
      } else {
        columns.push(e);
      }
      if (e.fixed && e.children?.length) {
        for (let j = 0; j < e.children.length; j++) {
          e.children[j].fixed = e.fixed;
        }
      }
    }
    return columns.filter(o => o?.key != 'index' && o?.key != 'action');
  });
  const getColumnSum = computed(() => {
    const list = unref(getSummaryColumn);
    const sums: any[] = [];
    const isSummary = key => props.config.summaryField.includes(key);
    const useThousands = key => list.some(o => o.__vModel__ === key && o.thousands);
    const tableData = list.filter(o => !o.__config__.noShow && (!o.__config__.visibility || o.__config__.visibility.includes('pc')));
    tableData.forEach((column, index) => {
      let sumVal = state.tableFormData.reduce((sum, d) => sum + getCmpValOfRow(d, column.__vModel__), 0);
      if (!isSummary(column.__vModel__)) sumVal = '';
      sumVal = Number.isNaN(sumVal) ? '' : sumVal;
      const realVal = sumVal && !Number.isInteger(sumVal) ? Number(sumVal).toFixed(2) : sumVal;
      sums[index] = useThousands(column.__vModel__) ? thousandsFormat(realVal) : realVal;
    });
    if (unref(getHasBatchBtn)) sums.unshift('');
    return sums;
  });
  const getHasBatchBtn = computed(() => props.config?.footerBtnsList?.length && props.config.footerBtnsList.some(o => o.value == 'batchRemove' && !!o.show));
  const getRowSelection = computed(() => {
    if (!unref(getHasBatchBtn)) return undefined;
    const rowSelection: TableProps['rowSelection'] = {
      selectedRowKeys: state.selectedRowKeys,
      onChange: (selectedRowKeys: string[]) => {
        state.selectedRowKeys = (selectedRowKeys || []).sort().reverse();
      },
    };
    return rowSelection;
  });

  state.tableData = props.config.__config__.children.map(o => {
    if (o.__config__ && dyOptionsList.includes(o.__config__.projectKey) && o.__config__.dataType !== 'static') o.options = [];
    return o;
  });
  buildOptions();
  if (props.value && Array.isArray(props.value) && props.value.length) {
    props.value.forEach(t => addRow(t, false));
  }

  function buildOptions() {
    state.tableData.forEach((cur, index) => {
      const config = cur.__config__;
      if (dyOptionsList.indexOf(config.projectKey) > -1) {
        if (config.dataType === 'dictionary' && config.dictionaryType) {
          baseStore.getDicDataSelector(config.dictionaryType).then(res => {
            cur.options = res;
          });
        }
        if (config.dataType === 'dynamic' && config.propsUrl) {
          const query = { paramList: config.templateJson ? getDefaultParamList(config.templateJson, props.formData) : [] };
          const matchInfo = JSON.stringify({ id: config.propsUrl, query });
          const item = { matchInfo, rowIndex: -1, colIndex: index };
          state.dataInterfaceInfo.push(item);
          getDataInterfaceRes(config.propsUrl, query).then(res => {
            cur.options = Array.isArray(res.data) ? res.data : [];
          });
        }
      }
    });
  }
  function handleRelationForParent(e, defaultValue, notSetDefault) {
    if (!state.tableFormData.length) return;
    for (let i = 0; i < state.tableFormData.length; i++) {
      let row: any[] = state.tableFormData[i];
      for (let j = 0; j < row.length; j++) {
        let item = row[j];
        const vModel = item.projectKey === 'popupSelect' ? item.__vModel__.substring(0, item.__vModel__.indexOf('_linzenRelation_')) : item.__vModel__;
        if (e.__vModel__ === vModel) {
          if (!notSetDefault) item.value = defaultValue;
          if (e.opType === 'setOptions') {
            item.config.options = [];
            const query = { paramList: getParamList(e.__config__.templateJson, props.formData, i) };
            getDataInterfaceRes(e.__config__.propsUrl, query).then(res => {
              const realData = res.data;
              item.config.options = Array.isArray(realData) ? realData : [];
            });
          }
          if (e.opType === 'setUserOptions') {
            const value = (props.formData as any)[e.relationField] || [];
            item.config.ableRelationIds = Array.isArray(value) ? value : [value];
          }
          if (e.opType === 'setStartTime') {
            const value = (props.formData as any)[e.__config__.startRelationField] || null;
            item.config.startTime = value;
          }
          if (e.opType === 'setEndTime') {
            const value = (props.formData as any)[e.__config__.endRelationField] || null;
            item.config.endTime = value;
          }
        }
      }
    }
    updateParentData();
  }
  function handleRelation(data, rowIndex) {
    const currRelations = unref(childRelations);
    for (let key in currRelations) {
      if (key === data.__vModel__) {
        for (let i = 0; i < currRelations[key].length; i++) {
          const e = currRelations[key][i];
          const config = e.__config__;
          const projectKey = config.projectKey;
          let defaultValue: any = null;
          if (
            ['checkbox', 'cascader', 'organizeSelect'].includes(projectKey) ||
            (['select', 'treeSelect', 'popupSelect', 'popupTableSelect', 'userSelect'].includes(projectKey) && e.multiple)
          ) {
            defaultValue = [];
          }
          let row: any[] = state.tableFormData[rowIndex];
          for (let j = 0; j < row.length; j++) {
            let item = row[j];
            const vModel = item.projectKey === 'popupSelect' ? item.__vModel__.substring(0, item.__vModel__.indexOf('_linzenRelation_')) : item.__vModel__;
            if (e.__vModel__ === vModel) {
              if (e.opType === 'setOptions') {
                item.config.options = [];
                const query = { paramList: getParamList(e.__config__.templateJson, props.formData, rowIndex) };
                getDataInterfaceRes(e.__config__.propsUrl, query).then(res => {
                  const realData = res.data;
                  item.config.options = Array.isArray(realData) ? realData : [];
                });
              }
              if (e.opType === 'setUserOptions') {
                const value = getFieldVal(e.relationField, rowIndex) || [];
                item.config.ableRelationIds = Array.isArray(value) ? value : [value];
              }
              if (e.opType === 'setStartTime') {
                const value = getFieldVal(e.__config__.startRelationField, rowIndex) || null;
                item.config.startTime = value;
              }
              if (e.opType === 'setEndTime') {
                const value = getFieldVal(e.__config__.endRelationField, rowIndex) || null;
                item.config.endTime = value;
              }
              if (item.value !== defaultValue) {
                item.value = defaultValue;
                nextTick(() => handleRelation(item, rowIndex));
              }
            }
          }
        }
      }
    }
    updateParentData();
  }
  function buildRowAttr(rowIndex) {
    let row: any[] = state.tableFormData[rowIndex];
    for (let i = 0; i < row.length; i++) {
      const cur = row[i].config;
      const config = cur.__config__;
      if (dyOptionsList.indexOf(config.projectKey) > -1) {
        if (config.dataType === 'dictionary' && config.dictionaryType) {
          baseStore.getDicDataSelector(config.dictionaryType).then(res => {
            cur.options = res;
          });
        }
        if (config.dataType === 'dynamic' && config.propsUrl) {
          if (cur.options?.length && (!config.templateJson || !config.templateJson.length || !hasTemplateJsonRelation(config.templateJson))) continue;
          const query = { paramList: config.templateJson ? getParamList(config.templateJson, props.formData, rowIndex) : [] };
          const matchInfo = JSON.stringify({ id: config.propsUrl, query });
          const item = { matchInfo, rowIndex, colIndex: i };
          const infoIndex = state.dataInterfaceInfo.findIndex(item => item.matchInfo === matchInfo);
          let useCacheOptions = false;
          if (infoIndex === -1) {
            state.dataInterfaceInfo.push(item);
          } else {
            const cacheOptions = getCacheOptions(infoIndex);
            if (cacheOptions.length) {
              cur.options = cacheOptions;
              useCacheOptions = true;
            }
          }
          if (!useCacheOptions) {
            getDataInterfaceRes(config.propsUrl, query)
              .then(res => {
                let realData = res.data;
                cur.options = Array.isArray(realData) ? realData : [];
              })
              .catch(() => {
                cur.options = [];
              });
          }
        }
      }
      if (config.projectKey === 'userSelect' && cur.relationField && cur.selectType !== 'all' && cur.selectType !== 'custom') {
        let value = getFieldVal(cur.relationField, rowIndex) || [];
        cur.ableRelationIds = Array.isArray(value) ? value : [value];
      }
      if (config.projectKey === 'datePicker' || config.projectKey === 'timePicker') {
        if (config.startTimeRule && config.startTimeType == 2 && config.startRelationField) {
          cur.startTime = getFieldVal(config.startRelationField, rowIndex) || null;
        }
        if (config.endTimeRule && config.endTimeType == 2 && config.endRelationField) {
          cur.endTime = getFieldVal(config.endRelationField, rowIndex) || null;
        }
      }
    }
  }
  // 获取缓存options数据
  function getCacheOptions(index) {
    const item = state.dataInterfaceInfo[index];
    if (item.rowIndex === -1) {
      return state.tableData[item.colIndex].options || [];
    } else {
      return state.tableFormData[item.rowIndex][item.colIndex].config.options || [];
    }
  }
  // 判断templateJson里是否有关联字段
  function hasTemplateJsonRelation(templateJson) {
    return templateJson.some(o => o.relationField);
  }
  function getParamList(templateJson, formData, index) {
    for (let i = 0; i < templateJson.length; i++) {
      if (templateJson[i].relationField) {
        if (templateJson[i].relationField.includes('-')) {
          let childVModel = templateJson[i].relationField.split('-')[1];
          let list = state.tableFormData[index].filter(o => o.__vModel__ === childVModel);
          if (!list.length) {
            templateJson[i].defaultValue = '';
          } else {
            let item = list[0];
            templateJson[i].defaultValue = item.value;
          }
        } else {
          templateJson[i].defaultValue = formData[templateJson[i].relationField] || '';
        }
      }
    }
    return templateJson;
  }
  function getDefaultParamList(templateJson, formData) {
    for (let i = 0; i < templateJson.length; i++) {
      if (templateJson[i].relationField) {
        if (templateJson[i].relationField.includes('-')) {
          let childVModel = templateJson[i].relationField.split('-')[1];
          let list = state.tableData.filter(o => o.__vModel__ === childVModel);
          templateJson[i].defaultValue = '';
          if (list.length) templateJson[i].defaultValue = list[0].__config__.defaultValue || '';
        } else {
          templateJson[i].defaultValue = formData[templateJson[i].relationField] || '';
        }
      }
    }
    return templateJson;
  }
  function getFieldVal(field, rowIndex) {
    let val = '';
    if (field.includes('-')) {
      let childVModel = field.split('-')[1];
      let list = state.tableFormData[rowIndex].filter(o => o.__vModel__ === childVModel);
      if (!list.length) {
        val = '';
      } else {
        let item = list[0];
        val = item.value;
      }
    } else {
      val = (props.formData as any)[field] || '';
    }
    return val;
  }
  function clearAddRowFlag() {
    nextTick(() => {
      state.isAddRow = false;
    });
  }
  function setTableFormData(prop, value) {
    let activeRow: any[] = state.tableFormData[state.activeRowIndex];
    for (let i = 0; i < activeRow.length; i++) {
      let vModel = activeRow[i].__vModel__;
      if (activeRow[i].__vModel__.indexOf('_linzenRelation_') >= 0) {
        vModel = activeRow[i].__vModel__.substring(0, activeRow[i].__vModel__.indexOf('_linzenRelation_'));
      }
      if (vModel === prop) {
        activeRow[i].value = value;
        break;
      }
    }
  }
  function setTableShowOrHide(prop, value) {
    for (let i = 0; i < state.tableData.length; i++) {
      if (state.tableData[i].__vModel__ === prop) {
        state.tableData[i].__config__.noShow = value;
        break;
      }
    }
  }
  function onFormBlur(rowIndex, colIndex, e) {
    const data: any = state.tableFormData[rowIndex][colIndex];
    if (data && data.on && data.on.blur) {
      const func: any = getScriptFunc(data.on.blur);
      if (!func) return;
      func({ data: e, rowIndex, ...unref(parameter) });
    }
  }
  function onFormDataChange(rowIndex, colIndex, _tag, val, row) {
    if (state.isAddRow) return;
    const data: any = state.tableFormData[rowIndex][colIndex];
    state.activeRowIndex = rowIndex;
    data.required && (data.valid = checkData(data));
    data.regList && data.regList.length && (data.regValid = checkRegData(data));
    updateParentData();
    handleRelation(data, rowIndex);
    if (data && data.on && data.on.change) {
      const func: any = getScriptFunc(data.on.change);
      if (!func) return;
      const value = row ? row : val;
      func({ data: value, rowIndex, ...unref(parameter) });
    }
    if (['popupSelect', 'relationForm'].includes(data.projectKey)) setTransferFormData(row, data.config.__config__);
  }
  function setTransferFormData(data, config) {
    if (!config?.transferList?.length) return;
    let row = state.tableFormData[state.activeRowIndex];
    for (let index = 0; index < config.transferList.length; index++) {
      const element = config.transferList[index];
      if (element.sourceValue.includes('-')) element.sourceValue = element.sourceValue.split('-')[1];
      for (let index = 0; index < row.length; index++) {
        const e = row[index];
        if (e.__vModel__ == element.sourceValue) {
          e.value = data[element.targetField];
          updateParentData();
        }
      }
    }
  }
  /**
   * 校验单个表单数据
   * @param {CmpConfig} 组件配置对象
   */
  function checkData({ value }) {
    if ([null, undefined, ''].includes(value)) return false;
    if (Array.isArray(value)) return value.length > 0;
    return true;
  }
  function checkRegData(col) {
    let res = true;
    for (let i = 0; i < col.regList.length; i++) {
      const item = col.regList[i];
      if (item.pattern) {
        let pattern = eval(item.pattern);
        if (col.value && !pattern.test(col.value)) {
          res = false;
          col.regErrorText = item.message;
          break;
        }
      }
    }
    return res;
  }
  /**
   * 校验表格数据必填项
   */
  function submit() {
    let res = true;
    const checkCol = col => {
      col.required && !checkData(col) && (res = col.valid = false);
      col.regList && col.regList.length && !checkRegData(col) && (res = col.regValid = false);
    };
    state.tableFormData.forEach(row => row.forEach(checkCol));
    return res ? getTableValue() : false;
  }
  /**
   * 根据formId获取完整组件配置
   */
  function getConfById(formId, rowIndex) {
    let item = state.tableFormData[rowIndex].find(t => t.formId === formId).config;
    let itemConfig = item.__config__;
    let newObj = {};
    item = getRealProps(item, itemConfig.projectKey);
    for (const key in item) {
      if (!['__config__', '__vModel__', 'on'].includes(key)) {
        newObj[key] = item[key];
      }
      if (key === 'disabled') {
        newObj[key] = props.disabled || item[key];
      }
    }
    if (['relationForm', 'popupSelect'].includes(itemConfig.projectKey)) {
      newObj['field'] = props.config.__vModel__ + item.__vModel__ + '_linzenRelation_' + rowIndex;
    }
    if (['relationFormAttr', 'popupAttr'].includes(itemConfig.projectKey)) {
      let prop = newObj['relationField'].split('_linzenTable_')[0];
      newObj['relationField'] = props.config.__vModel__ + prop + '_linzenRelation_' + rowIndex;
    }
    return newObj;
  }
  /**
   * 获取默认行数据
   */
  function getEmptyRow(val, rowIndex) {
    const currDate = new Date();
    return state.tableData.map((t: any) => {
      let options = [];
      if (dyOptionsList.indexOf(t.__config__.projectKey) > -1) options = t.options;
      if (t.__config__.defaultCurrent) {
        if (t.__config__.projectKey === 'datePicker') {
          t.__config__.defaultValue = dayjs(currDate).startOf(getDateTimeUnit(t.format)).valueOf();
        }
        if (t.__config__.projectKey === 'timePicker') {
          t.__config__.defaultValue = dayjs(currDate).format(t.format || 'HH:mm:ss');
        }
      }
      let res = {
        tag: t.__config__.tag,
        formId: t.__config__.formId,
        value: !val ? t.__config__.defaultValue : Reflect.has(val, t.__vModel__) ? val[t.__vModel__] : getDefaultEmptyValue(t),
        options,
        valid: true,
        regValid: true,
        regErrorText: '',
        on: t.on || {},
        projectKey: t.__config__.projectKey,
        __vModel__: ['relationForm', 'popupSelect'].includes(t.__config__.projectKey) ? t.__vModel__ + '_linzenRelation_' + rowIndex : t.__vModel__,
        regList: t.__config__.regList || [],
        required: t.__config__.required,
        rowData: val || {},
        config: t,
      };
      return res;
    });
  }
  function getDefaultEmptyValue(item) {
    if (!item.__config__ || !item.__config__.projectKey) return undefined;
    const projectKey = item.__config__.projectKey;
    const list = ['checkbox', 'uploadFile', 'uploadImg', 'cascader', 'organizeSelect', 'areaSelect'];
    return list.includes(projectKey) || item.multiple ? [] : undefined;
  }
  // 获取表格数据
  function getTableValue() {
    return state.tableFormData.map(row =>
      (row as any[]).reduce((p, c) => {
        let str = c.__vModel__;
        if (c.__vModel__ && c.__vModel__.indexOf('_linzenRelation_') >= 0) {
          str = c.__vModel__.substring(0, c.__vModel__.indexOf('_linzenRelation_'));
        }
        p[str] = c.value;
        if (c.rowData) p = { ...c.rowData, ...p };
        return p;
      }, {}),
    );
  }
  // 更新父级数据 触发计算公式更新
  function updateParentData() {
    const newVal = getTableValue();
    emit('update:value', newVal);
    emit('change', newVal);
    formItemContext.onFieldChange();
  }
  function columnBtnsHandel(item, index) {
    if (item.value == 'remove') return removeRow(index, item.showConfirm);
    if (item.value == 'copy') return copyRow(index);
  }
  function removeRow(index, showConfirm = 0) {
    const handleRemove = () => {
      state.tableFormData.splice(index, 1);
      nextTick(() => updateParentData());
    };
    if (!showConfirm) return handleRemove();
    createConfirm({
      iconType: 'warning',
      title: t('common.tipTitle'),
      content: '此操作将永久删除该数据, 是否继续?',
      onOk: handleRemove,
    });
  }
  function copyRow(index) {
    let item = cloneDeep(state.tableFormData[index]);
    item.length && item.map(o => delete o.rowData);
    item.linzenId = buildUUID();
    state.tableFormData.push(item);
    nextTick(() => updateParentData());
  }
  function footerBtnsHandle(item) {
    if (item.value == 'add') return addRow();
    if (item.value == 'batchRemove') return batchRemoveRow(item);
    state.actionConfig = item.actionConfig;
    openSelectDialog();
  }
  function batchRemoveRow(item) {
    if (!state.selectedRowKeys.length) return createMessage.error('请选择一条数据');
    const handleBatchRemove = () => {
      state.tableFormData = state.tableFormData.filter(o => !state.selectedRowKeys.includes(o.linzenId));
      nextTick(() => {
        state.selectedRowKeys = [];
        updateParentData();
      });
    };
    if (!item.showConfirm) return handleBatchRemove();
    createConfirm({
      iconType: 'warning',
      title: t('common.tipTitle'),
      content: '此操作将永久删除该数据, 是否继续?',
      onOk: handleBatchRemove,
    });
  }
  function addRow(val?, isUpdate = true) {
    state.isAddRow = true;
    if (!Array.isArray(state.tableFormData)) state.tableFormData = [];
    const rowIndex = state.tableFormData.length;
    const item: any = cloneDeep(getEmptyRow(val, rowIndex));
    item['linzenId'] = buildUUID();
    state.tableFormData.push(item);
    buildRowAttr(rowIndex);
    clearAddRowFlag();
    nextTick(() => {
      if (isUpdate) updateParentData();
    });
  }
  function openSelectDialog() {
    (unref(selectModal) as any)?.openSelectModal();
  }
  function addForSelect(data) {
    data.forEach(t => addRow(t));
  }
  function getCmpValOfRow(row, key) {
    if (!props.config.summaryField.length) return '';
    const isSummary = key => props.config.summaryField.includes(key);
    const target = row.find(t => t.__vModel__ === key);
    if (!target) return '';
    let data = isNaN(target.value) ? 0 : Number(target.value);
    if (isSummary(key)) return data || 0;
    return '';
  }
  function resetTable() {
    state.tableData = props.config.__config__.children;
    state.tableFormData = [];
    // addRow()
  }
  function reset() {
    state.tableData.map(t => {
      let index = state.tableFormData[0].findIndex(c => c.vModel === t.vModel);
      if (index === -1) return;
      for (let i = 0; i < state.tableFormData.length; i++) {
        state.tableFormData[i][index].value = t.defaultValue;
      }
    });
  }
  function getSummaryCellAlign(index) {
    if (!unref(getSummaryColumn).length) return;
    if (unref(getHasBatchBtn)) index--;
    return unref(getSummaryColumn)[index]?.align || 'left';
  }
  function getActionWidth() {
    let actionWidth = 0;
    if (!props.config?.columnBtnsList?.length) return 0;
    props.config.columnBtnsList.map(o => {
      if (o.show) actionWidth += 50;
    });
    return actionWidth;
  }
</script>
<style lang="less" scoped>
  @prefix-cls: ~'@{namespace}-input-table';

  .@{prefix-cls} {
    .error-tip {
      font-size: 12px;
    }
  }
</style>
