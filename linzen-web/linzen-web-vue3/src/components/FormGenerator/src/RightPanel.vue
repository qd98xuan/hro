<template>
  <div class="right-board common-board">
    <a-tabs v-model:activeKey="activeKey" :tabBarGutter="10" class="average-tabs">
      <a-tab-pane key="field" tab="组件属性"></a-tab-pane>
      <a-tab-pane key="style" tab="组件样式"></a-tab-pane>
      <a-tab-pane key="form" tab="表单属性"></a-tab-pane>
    </a-tabs>
    <div class="field-box" v-if="activeData?.__config__ && activeData.__config__.projectKey">
      <ScrollContainer>
        <a-form :colon="false" labelAlign="left" :labelCol="{ style: { width: '90px' } }" v-show="activeKey === 'field'" class="right-board-form">
          <a-form-item label="控件类型" v-if="!['tableGridTr', 'tableGridTd'].includes(activeData.__config__.projectKey)">
            <a-input v-model:value="getCompName" disabled />
          </a-form-item>
          <a-form-item label="控件用途" v-if="projectKey == 'calculate'">
            <linzen-select v-model:value="activeData.isStorage" :options="storageTypeOptions" @change="onStorageChange" />
          </a-form-item>
          <div v-if="(activeData.__vModel__ !== undefined && !noVModelList.includes(projectKey)) || activeData.isStorage">
            <div v-if="getHasTable">
              <div v-if="!config.isSubTable">
                <a-form-item label="数据库表" v-if="projectKey !== 'table'">
                  <linzen-select
                    v-model:value="activeData.__config__.tableName"
                    placeholder="请选择"
                    showSearch
                    :dropdownMatchSelectWidth="false"
                    :options="getAllTable.map(o => ({ ...o, fullName: o.tableName ? o.table + '(' + o.tableName + ')' : o.table }))"
                    :fieldNames="{ value: 'table' }"
                    @change="onTableChange">
                    <template #option="{ fullName, typeId }">
                      <span class="custom-option-left"> {{ fullName }}</span>
                      <span class="custom-option-right">{{ typeId == '1' ? '主表' : '从表' }}</span>
                    </template>
                  </linzen-select>
                </a-form-item>
                <a-form-item label="控件字段">
                  <linzen-select
                    v-model:value="activeData.__vModel__"
                    ref="fieldSelectRef"
                    placeholder="请输入数据库字段"
                    :options="fieldOptions"
                    :dropdownMatchSelectWidth="false"
                    showSearch
                    allowClear
                    :disabled="projectKey === 'table'"
                    :showArrow="projectKey !== 'table'"
                    :fieldNames="{ value: 'realField' }"
                    @change="onFieldChange($event, config.formId)">
                    <template #dropdownRender="{ menuNode: menu }">
                      <v-nodes :vnodes="menu" />
                      <a-divider style="margin: 4px 0" />
                      <div class="select-item-add" @mousedown="e => e.preventDefault()" @click="handelAddField">
                        <a-button type="link" preIcon="icon-linzen icon-linzen-btn-add">添加字段</a-button>
                      </div>
                    </template>
                  </linzen-select>
                </a-form-item>
              </div>
              <div v-else>
                <a-form-item label="数据库表">
                  <linzen-select
                    v-model:value="activeData.__config__.relationTable"
                    placeholder="请选择"
                    :options="getAllTable.map(o => ({ ...o, fullName: o.tableName ? o.table + '(' + o.tableName + ')' : o.table }))"
                    :fieldNames="{ value: 'table' }"
                    showSearch
                    disabled>
                  </linzen-select>
                </a-form-item>
                <a-form-item label="控件字段">
                  <linzen-select
                    v-model:value="activeData.__vModel__"
                    ref="childFieldSelectRef"
                    placeholder="请输入数据库字段"
                    :options="getSubTableField(config.relationTable)"
                    :dropdownMatchSelectWidth="false"
                    showSearch
                    allowClear
                    :fieldNames="{ value: 'field' }"
                    @change="onChildTableFieldChange($event, config.formId)">
                    <template #dropdownRender="{ menuNode: menu }">
                      <v-nodes :vnodes="menu" />
                      <a-divider style="margin: 4px 0" />
                      <div class="select-item-add" @mousedown="e => e.preventDefault()" @click="handelAddField">
                        <a-button type="link" preIcon="icon-linzen icon-linzen-btn-add">添加字段</a-button>
                      </div>
                    </template>
                  </linzen-select>
                </a-form-item>
              </div>
            </div>
            <div v-else>
              <a-form-item label="控件字段">
                <linzen-input
                  v-model:value="activeData.__vModel__"
                  placeholder="请输入数据库字段"
                  :disabled="projectKey === 'table'"
                  @change="onInputFieldChange($event, config.formId, config.parentVModel)" />
              </a-form-item>
            </div>
          </div>
          <a-form-item label="控件标题" v-if="!layoutList.includes(projectKey) || projectKey === 'table'">
            <a-input v-model:value="activeData.__config__.label" placeholder="请输入控件标题" />
          </a-form-item>
          <a-form-item label="显示标题" v-if="!layoutList.includes(projectKey)">
            <a-switch v-model:checked="activeData.__config__.showLabel" />
          </a-form-item>
          <a-form-item label="显示标题" v-if="projectKey === 'table'">
            <a-switch v-model:checked="activeData.__config__.showTitle" />
          </a-form-item>
          <a-form-item label="卡片标题" v-if="projectKey === 'card'">
            <a-input v-model:value="activeData.header" placeholder="请输入" />
          </a-form-item>
          <a-form-item label="标题提示" v-if="hasTipLabel(projectKey)">
            <linzen-textarea v-model:value="activeData.__config__.tipLabel" placeholder="请输入" :rows="2" />
          </a-form-item>
          <a-form-item label="占位提示" v-if="activeData.placeholder !== undefined && !systemList.includes(projectKey)">
            <a-input v-model:value="activeData.placeholder" placeholder="请输入" />
          </a-form-item>
          <div v-if="projectKey === 'table'">
            <a-form-item label="关联子表" v-if="getHasTable">
              <linzen-select
                v-model:value="activeData.__config__.tableName"
                placeholder="请选择"
                showSearch
                :dropdownMatchSelectWidth="false"
                :options="getSubTable.map(o => ({ ...o, fullName: o.tableName ? o.table + '(' + o.tableName + ')' : o.table }))"
                :fieldNames="{ value: 'table' }"
                @change="onChildTableNameChange($event, config.formId)">
                <template #option="{ fullName, typeId }">
                  <span class="custom-option-left"> {{ fullName }}</span>
                  <span class="custom-option-right">{{ typeId == '1' ? '主表' : '从表' }}</span>
                </template>
              </linzen-select>
            </a-form-item>
          </div>
          <component :is="getRightComp" v-bind="getRightCompBind" :key="activeData.__config__.renderKey" />
          <a-form-item label="单据模板" v-if="projectKey === 'billRule'">
            <BillRuleModal :value="activeData.__config__.rule" :title="activeData.__config__.ruleName" @change="onRuleChange" />
          </a-form-item>
          <a-form-item label="显示内容" v-if="projectKey === 'currOrganize'">
            <a-select v-model:value="activeData.showLevel" placeholder="请选择">
              <a-select-option value="all">显示组织</a-select-option>
              <a-select-option value="last">显示部门</a-select-option>
            </a-select>
          </a-form-item>
          <div v-if="![...layoutList, ...noVModelList].includes(projectKey) && projectKey !== 'input'">
            <a-form-item label="是否禁用" v-if="!systemList.includes(projectKey)">
              <a-switch v-model:checked="activeData.disabled" />
            </a-form-item>
            <a-form-item label="是否必填" v-if="!systemList.includes(projectKey)">
              <a-switch v-model:checked="activeData.__config__.required" />
            </a-form-item>
            <a-form-item label="是否隐藏">
              <a-switch v-model:checked="activeData.__config__.noShow" />
            </a-form-item>
          </div>
          <a-form-item label="是否隐藏" v-if="['relationFormAttr', 'popupAttr', 'calculate'].includes(projectKey) && activeData.isStorage">
            <a-switch v-model:checked="activeData.__config__.noShow" />
          </a-form-item>
          <div v-if="activeData.on && (modelType == 1 || modelType == 6)">
            <a-divider>脚本事件</a-divider>
            <a-form-item :label="key" v-for="(_value, key) in activeData.on" :key="key">
              <a-button block @click="editFunc(key)">{{ getFuncText(key) }}</a-button>
            </a-form-item>
          </div>
        </a-form>
        <StylePane v-bind="getBindValue" v-show="activeKey === 'style'" />
        <FormAttrPane v-bind="getBindValue" :printTplOptions="printTplOptions" v-show="activeKey === 'form'" />
      </ScrollContainer>
    </div>
    <FieldModal @register="registerFieldModal" @updateOptions="updateFieldOptions" />
    <FormScript @register="registerScriptModal" :treeTitle="formInfo.fullName" :drawingList="drawingList" @confirm="updateScript" />
  </div>
</template>
<script lang="ts" setup>
  import { getDataModelFieldList } from '/@/api/systemData/dataModel';
  import { getDictionaryTypeSelector } from '/@/api/systemData/dictionary';
  import { getPrintDevSelector } from '/@/api/system/printDev';
  import { reactive, toRefs, computed, ref, unref, watch, onMounted, nextTick } from 'vue';
  import { ScrollContainer } from '/@/components/Container';
  import FieldModal from './components/FieldModal.vue';
  import FormScript from './components/FormScript.vue';
  import StylePane from './components/StylePane.vue';
  import FormAttrPane from './components/FormAttrPane.vue';
  import { BillRuleModal } from '/@/components/CommonModal';
  import * as RightComp from './rightComponents';
  import { useModal } from '/@/components/Modal';
  import { useGeneratorStore } from '/@/store/modules/generator';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useAttrs } from '/@/hooks/core/useAttrs';
  import type { ItemCfg } from './types/genItem';
  import { inputComponents, selectComponents, systemComponents, layoutComponents } from './helper/componentMap';
  import { layoutList, systemList, orgList } from './helper/rightPanel';
  import { noVModelList, calculateItem, onlinePeculiarList } from './helper/config';
  import { upperFirst } from 'lodash-es';

  defineOptions({ inheritAttrs: false });
  const props = defineProps(['activeData', 'formConf', 'drawingList', 'formInfo', 'dbType']);
  const attrs = useAttrs({ excludeDefaultKeys: false });
  const storageTypeOptions = [
    { id: 0, fullName: '展示数据' },
    { id: 1, fullName: '存储数据' },
  ];
  const VNodes = (_, { attrs }) => attrs.vnodes;
  const generatorStore = useGeneratorStore();
  const { createMessage } = useMessage();
  const [registerFieldModal, { openModal: openFieldModal }] = useModal();
  const [registerScriptModal, { openModal: openScriptModal }] = useModal();
  const fieldSelectRef = ref(null);
  const childFieldSelectRef = ref(null);
  const state = reactive({
    activeKey: 'field',
    fieldOptions: [],
    dictionaryOptions: [],
    printTplOptions: [],
    activeFunc: '',
  });
  const { activeKey, fieldOptions, printTplOptions } = toRefs(state);
  const getBindValue = computed(() => ({ ...props }));
  const config = computed<ItemCfg>(() => unref(props.activeData).__config__);
  const projectKey = computed(() => unref(props.activeData).__config__?.projectKey);
  const modelType = computed(() => unref(props.formInfo).type);
  const getCompName = computed(() => {
    const allComps = [...inputComponents, ...selectComponents, ...systemComponents, ...layoutComponents, calculateItem, ...onlinePeculiarList];
    const comp = allComps.filter(o => o.__config__.projectKey === unref(projectKey));
    if (!comp.length) return '';
    return comp[0].__config__.label;
  });
  const getRightComp = computed(() => {
    if (!unref(projectKey)) return null;
    if (orgList.includes(unref(projectKey))) return RightComp.ROrgRight;
    if (unref(projectKey) === 'popupTableSelect') return RightComp.RPopupSelect;
    return RightComp['R' + upperFirst(unref(projectKey))];
  });
  const getRightCompBind = computed(() => ({
    activeData: props.activeData,
    dicOptions: state.dictionaryOptions,
    ...unref(attrs),
    drawingOptions: unref(drawingOptions),
    drawingList: props.drawingList,
    formInfo: unref(props.formInfo),
    modelType: unref(modelType),
  }));
  const drawingOptions = computed(() => {
    let list: any[] = [];
    const loop = (data, parent?) => {
      if (!data) return;
      if (data.__config__ && data.__config__.projectKey !== 'table' && data.__config__.children && Array.isArray(data.__config__.children)) {
        loop(data.__config__.children, data);
      }
      if (Array.isArray(data)) data.forEach(d => loop(d, parent));
      if (data.__vModel__ && data.__config__.projectKey !== 'table') {
        list.push({ id: data.__vModel__, fullName: data.__config__.label });
      }
    };
    loop(unref(props.drawingList));
    return list;
  });
  const getHasTable = computed(() => generatorStore.getHasTable);
  const getAllTable = computed({
    get() {
      return generatorStore.getAllTable;
    },
    set(val) {
      generatorStore.setAllTable(val);
    },
  });
  const getSubTable = computed({
    get() {
      return generatorStore.getSubTable;
    },
    set(val) {
      generatorStore.setSubTable(val);
    },
  });
  const getFormItemList = computed({
    get() {
      return generatorStore.getFormItemList;
    },
    set(val) {
      generatorStore.setFormItemList(val);
    },
  });
  const getMainTable = computed(() => {
    const allTable = unref(getAllTable);
    let item = allTable.filter(o => o.typeId == '1')[0];
    if (!item || !item.table) return '';
    return item.table;
  });

  watch(
    () => unref(props.formConf),
    val => {
      if (val.formStyle === 'word-form' && val.labelPosition === 'top') {
        val.labelPosition = 'left';
      }
    },
    { deep: true },
  );
  watch(
    () => unref(props.activeData),
    val => {
      if (!val || !val.__config__) return;
      if (!val.__config__.tableName && val.__config__.projectKey !== 'table') {
        val.__config__.tableName = unref(getMainTable);
      }
      setDefaultOptions();
    },
  );
  function getSubTableField(key) {
    let item: Recordable = {};
    let list = unref(getSubTable).filter(o => o.table === key);
    if (list.length) {
      item = list[0];
    }
    let arr = [];
    if (item && item.fields) {
      arr = item.fields.filter(o => o.primaryKey != 1).map(o => ({ ...o, fullName: o.fieldName ? o.field + '(' + o.fieldName + ')' : o.field }));
    }
    return arr;
  }
  function onChildTableFieldChange(val, formId) {
    if (!val) return (props.activeData.__vModel__ = '');
    let boo = false;
    const loop = list => {
      for (let i = 0; i < list.length; i++) {
        const e = list[i];
        const config = e.__config__;
        if (config.projectKey === 'table' && config.tableName === props.activeData.__config__.relationTable) {
          for (let j = 0; j < config.children.length; j++) {
            const child = config.children[j];
            if (child.__vModel__ === val && child.__config__.formId !== formId) {
              boo = true;
              break;
            }
          }
        }
        if (config && config.projectKey != 'table' && config.children && Array.isArray(config.children)) {
          loop(config.children);
        }
      }
    };
    loop(unref(props.drawingList));
    if (boo) {
      createMessage.warning(`子表字段【${val}】已存在,请重新选择!`);
      props.activeData.__vModel__ = undefined;
      nextTick(() => {
        props.activeData.__vModel__ = '';
      });
      return;
    }
    const options: any[] = getSubTableField(props.activeData.__config__.relationTable);
    const item = options.filter(o => o.field == val)[0];
    if (!item || !item.fieldName) return;
    props.activeData.__config__.label = item.fieldName;
  }
  function onFieldChange(val, formId) {
    if (!val) return (props.activeData.__vModel__ = '');
    let boo = false;
    const loop = list => {
      for (let i = 0; i < list.length; i++) {
        const e = list[i];
        const config = e.__config__;
        if (e.__vModel__ === val && e.__config__.formId !== formId) {
          boo = true;
          break;
        }
        if (config && config.projectKey != 'table' && config.children && Array.isArray(config.children)) {
          loop(config.children);
        }
      }
    };
    loop(unref(props.drawingList));
    if (boo) {
      createMessage.warning(`字段【${val}】已存在,请重新选择!`);
      props.activeData.__vModel__ = undefined;
      nextTick(() => {
        props.activeData.__vModel__ = '';
      });
      return;
    }
    const item = (state.fieldOptions as any[]).filter(o => o.realField == val)[0];
    if (!item || !item.fieldName) return;
    props.activeData.__config__.label = item.fieldName;
  }
  function onInputFieldChange(val, formId, parentVModel) {
    if (!val) return;
    const RegExp = /(^_([a-zA-Z0-9]_?)*$)|(^[a-zA-Z](_?[a-zA-Z0-9])*_?$)/;
    if (!RegExp.test(val)) {
      createMessage.warning(`请输入正确的字段`);
      return;
    }
    let boo = false;
    let childBoo = false;
    if (parentVModel) {
      const loop = (data, parent?) => {
        if (!data) return;
        if (data.__config__ && data.__config__.children && Array.isArray(data.__config__.children)) {
          loop(data.__config__.children, data);
        }
        if (Array.isArray(data)) data.forEach(d => loop(d, parent));
        if (parent?.__config__?.projectKey == 'table') {
          if (
            data.__vModel__ &&
            data.__vModel__.toLowerCase() === val.toLowerCase() &&
            data.__config__.formId !== formId &&
            data.__config__.parentVModel === parentVModel
          ) {
            childBoo = true;
            return;
          }
        }
      };
      loop(unref(props.drawingList));
    } else {
      const loop = (data, parent?) => {
        if (!data) return;
        if (data.__config__?.projectKey !== 'table' && data.__config__?.children && Array.isArray(data.__config__.children)) {
          loop(data.__config__.children, data);
        }
        if (Array.isArray(data)) data.forEach(d => loop(d, parent));
        if (data.__vModel__ && data.__vModel__.toLowerCase() === val.toLowerCase() && data.__config__.formId !== formId) {
          boo = true;
          return;
        }
      };
      loop(unref(props.drawingList));
    }
    if (boo) {
      createMessage.warning(`字段【${val}】已存在,请重新输入!`);
      props.activeData.__vModel__ = '';
      return;
    }
    if (childBoo) {
      createMessage.warning(`子表字段【${val}】已存在,请重新输入!`);
      props.activeData.__vModel__ = '';
      return;
    }
    props.activeData.__vModel__ = toggleVmodelCase(val);
  }
  function toggleVmodelCase(str) {
    const dbType = props.dbType || '';
    if (dbType.toLowerCase() === 'Oracle'.toLowerCase() || dbType.toLowerCase() === 'DM'.toLowerCase()) {
      return str.toUpperCase();
    }
    if (dbType.toLowerCase() === 'PostgreSQL'.toLowerCase() || dbType.toLowerCase() === 'KingBaseES'.toLowerCase()) {
      return str.toLowerCase();
    }
    return str;
  }
  function onStorageChange(val) {
    if (val == 0) {
      props.activeData.__vModel__ = '';
      props.activeData.__config__.noShow = false;
    } else {
      if (!unref(getHasTable)) {
        props.activeData.__vModel__ = toggleVmodelCase(`${props.activeData.__config__.projectKey}Field${props.activeData.__config__.formId}`);
      } else {
        if (props.activeData.__config__.isSubTable && !props.activeData.__config__.relationTable) {
          const loop = (data, parent?) => {
            if (!data) return;
            if (data.__config__ && data.__config__.projectKey !== 'table' && data.__config__.children && Array.isArray(data.__config__.children)) {
              loop(data.__config__.children, data);
            }
            if (Array.isArray(data)) data.forEach(d => loop(d, parent));
            if (data.__vModel__ === props.activeData.__config__.parentVModel) {
              props.activeData.__config__.relationTable = data.__config__.tableName;
            }
          };
          loop(unref(props.drawingList));
        }
        setDefaultOptions();
      }
    }
  }
  function onTableChange() {
    props.activeData.__vModel__ = '';
    setDefaultOptions();
  }
  // 设计子表关联子表变化
  function onChildTableNameChange(tableName, formId) {
    if (!tableName) return;
    const drawingList = unref(props.drawingList) || [];
    let boo = false;
    const loop = list => {
      for (let i = 0; i < list.length; i++) {
        const e = list[i];
        const config = e.__config__;
        if (config.projectKey === 'table' && config.tableName === tableName && config.formId !== formId) {
          boo = true;
          break;
        }
        if (config && config.projectKey != 'table' && config.children && Array.isArray(config.children)) {
          loop(config.children);
        }
      }
    };
    loop(drawingList);
    if (boo) {
      createMessage.warning(`子表【${tableName}】已存在,请重新选择!`);
      props.activeData.__config__.tableName = undefined;
      nextTick(() => {
        props.activeData.__config__.tableName = '';
      });
      return;
    }
    for (let i = 0; i < props.activeData.__config__.children.length; i++) {
      props.activeData.__config__.children[i].__config__.relationTable = tableName;
      props.activeData.__config__.children[i].__vModel__ = '';
    }
  }
  function setDefaultOptions() {
    if (!unref(getHasTable)) return;
    let fieldOptions: any[] = [];
    if ((props.activeData.__vModel__ === undefined && props.activeData.isStorage != 1) || props.activeData.__config__.projectKey === 'table') return;
    if (!props.activeData.__config__.tableName || props.activeData.__config__.tableName === unref(getMainTable)) {
      fieldOptions = getFormItemList.value.map(o => ({ ...o, realField: o.field }));
    } else {
      let list = unref(getAllTable).filter(o => o.table === props.activeData.__config__.tableName);
      if (!list.length) {
        props.activeData.__config__.tableName = unref(getMainTable);
        fieldOptions = getFormItemList.value.map(o => ({ ...o, realField: o.field }));
        props.activeData.__vModel__ = '';
      } else {
        let item = list[0];
        let options = item.fields.map(o => ({
          ...o,
          realField: 'linzen_' + props.activeData.__config__.tableName + '_linzen_' + o.field,
        }));
        fieldOptions = options;
      }
    }
    (state.fieldOptions as any[]) = fieldOptions
      .filter(o => o.primaryKey != 1)
      .map(o => ({ ...o, fullName: o.fieldName ? o.field + '(' + o.fieldName + ')' : o.field }));
  }
  function handelAddField() {
    let tableName = '';
    if (!props.activeData.__config__.isSubTable) {
      tableName = props.activeData.__config__.tableName;
      (unref(fieldSelectRef) as any)?.getSelectRef().blur();
    } else {
      tableName = props.activeData.__config__.relationTable;
      (unref(childFieldSelectRef) as any)?.getSelectRef().blur();
    }
    const linkId = props.formInfo.dbLinkId || '0';
    setTimeout(() => {
      openFieldModal(true, { linkId, tableName });
    }, 200);
  }
  async function updateFieldOptions() {
    let tableName = '';
    if (!props.activeData.__config__.isSubTable) {
      tableName = props.activeData.__config__.tableName;
    } else {
      tableName = props.activeData.__config__.relationTable;
    }
    const type = props.formInfo.type;
    const queryType = type == 3 || type == 4 || type == 5 ? '1' : '0';
    const res = await getDataModelFieldList(props.formInfo.dbLinkId, tableName, queryType);
    let fields = res.data.list;
    for (let i = 0; i < getAllTable.value.length; i++) {
      if (getAllTable.value[i].table === tableName) {
        getAllTable.value[i].fields = fields;
        break;
      }
    }
    if (!props.activeData.__config__.isSubTable) {
      if (props.activeData.__config__.tableName === unref(getMainTable)) getFormItemList.value = fields;
      setDefaultOptions();
    } else {
      getSubTable.value = getAllTable.value.filter(o => o.typeId == '0');
    }
  }
  function getFuncText(key) {
    let text = '';
    switch (key) {
      case 'change':
        text = '发生变化时触发';
        break;
      case 'blur':
        text = '失去焦点时触发';
        break;
      case 'click':
        text = '点击时触发';
        break;
      case 'tabClick':
        text = '面板点击时触发';
        break;
      default:
        text = '';
        break;
    }
    return text;
  }
  function editFunc(funcName) {
    state.activeFunc = funcName;
    openScriptModal(true, { text: props.activeData.on[state.activeFunc] });
  }
  function updateScript(data) {
    openScriptModal();
    props.activeData.on[state.activeFunc] = data;
  }
  function onRuleChange(val, row) {
    if (!val) {
      props.activeData.__config__.rule = '';
      props.activeData.__config__.ruleName = '';
      return;
    }
    if (props.activeData.__config__.rule === val) return;
    props.activeData.__config__.rule = val;
    props.activeData.__config__.ruleName = row.fullName;
  }

  function getDictionaryType() {
    getDictionaryTypeSelector().then(res => {
      state.dictionaryOptions = res.data.list.filter(o => o.children && o.children.length);
    });
  }
  function getPrintTplList() {
    getPrintDevSelector('2').then(res => {
      state.printTplOptions = res.data.list.filter(o => o.children && o.children.length).map(o => ({ ...o, hasChildren: true }));
    });
  }
  function hasTipLabel(projectKey) {
    return ![...layoutList, ...systemList].includes(projectKey) || ['table', 'card', 'billRule'].includes(projectKey);
  }

  onMounted(() => {
    getDictionaryType();
    getPrintTplList();
    if (!props.activeData || !props.activeData.__config__) return;
    if (!props.activeData.__config__.tableName && props.activeData.__config__.projectKey !== 'table') {
      props.activeData.__config__.tableName = unref(getMainTable);
    }
    setDefaultOptions();
  });
</script>
