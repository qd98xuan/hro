<template>
  <BasicModal v-bind="$attrs" @register="registerModal" defaultFullscreen :footer="null" :closable="false" :keyboard="false" class="linzen-full-modal full-modal">
    <template #title>
      <div class="linzen-full-modal-header">
        <div class="header-title">
          <img src="../../../assets/images/linzen.png" class="header-logo" />
          <p class="header-txt" v-if="!activeStep"> · 代码生成</p>
          <a-tooltip :title="dataForm.fullName" v-else>
            <p class="header-txt"> · {{ dataForm.fullName }}</p>
          </a-tooltip>
        </div>
        <a-steps v-model:current="activeStep" type="navigation" size="small" @change="onStepChange">
          <a-step title="基础设置" />
          <a-step title="表单设计" :disabled="activeStep <= 1" />
          <a-step title="列表设计" disabled v-if="maxStep >= 2" />
        </a-steps>
        <a-space class="options" :size="10">
          <a-button type="warning" @click="toggleWebType(1)" v-show="activeStep == 2 && dataForm.webType == 2">{{ t('common.closeList') }}</a-button>
          <a-button @click="handlePrev" :disabled="activeStep <= 0 || btnLoading">{{ t('common.prev') }}</a-button>
          <a-button @click="handleNext" :disabled="activeStep >= maxStep || loading || btnLoading">{{ t('common.next') }} </a-button>
          <a-button type="primary" @click="handleSubmit()" :disabled="loading" :loading="btnLoading">{{ t('common.saveText') }}</a-button>
          <a-button @click="closeModal()">{{ t('common.cancelText') }}</a-button>
        </a-space>
      </div>
    </template>
    <a-row type="flex" justify="center" align="middle" class="basic-content" v-show="!activeStep">
      <a-col :span="12" :xxl="10" class="basic-form">
        <BasicForm @register="registerForm" />
        <a-table :data-source="tables" :columns="columns" size="small" :pagination="false" :scroll="{ x: 'max-content' }">
          <template #bodyCell="{ column, record, index }">
            <template v-if="column.key === 'typeId'">
              <a-tag color="processing" v-if="record.typeId == '1'">主表</a-tag>
              <a-tag color="warning" @click="changeTable(record)" v-else style="cursor: pointer" title="点击设置成主表">从表</a-tag>
            </template>
            <template v-if="column.key === 'table'">
              <span :title="record.tableName || record.table">{{ record.table }}</span>
            </template>
            <template v-if="column.key === 'tableField' && record.typeId !== '1'">
              <linzen-select
                v-model:value="record.tableField"
                placeholder="请选择"
                :options="record.fields"
                :field-names="{ value: 'field', label: 'field' }"
                showSearch
                class="!w-144px" />
            </template>
            <template v-if="column.key === 'relationField' && record.typeId !== '1'">
              <linzen-select
                v-model:value="record.relationField"
                placeholder="请选择"
                :options="mainTableFields"
                :field-names="{ value: 'field', label: 'field' }"
                showSearch
                class="!w-144px" />
            </template>
            <template v-if="column.key === 'action'">
              <a-button class="action-btn" type="link" color="error" @click="handleDelItem(record, index)" size="small">移除</a-button>
            </template>
          </template>
          <template #emptyText>
            <p class="ant-table__empty-text">点击“新增”可选择1条(单表)或2条以上(多表)</p>
          </template>
        </a-table>
        <div class="table-add-action" @click="openTableBox">
          <a-button type="link" preIcon="icon-linzen icon-linzen-btn-add">新增一行</a-button>
        </div>
      </a-col>
    </a-row>
    <FormGenerator ref="generatorRef" :conf="formData" :formInfo="dataForm" :dbType="dbType" v-if="activeStep == 1" />
    <BasicColumnDesign
      ref="columnDesignRef"
      :columnData="columnData"
      :appColumnData="appColumnData"
      :formInfo="dataForm"
      @toggleWebType="toggleWebType"
      v-if="activeStep == 2" />
    <TableModal @register="registerTableModal" @select="onTableSelect" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { getInfo, create, update } from '/@/api/onlineDev/visualDev';
  import { getDataSourceSelector } from '/@/api/systemData/dataSource';
  import { getDataModelFieldList } from '/@/api/systemData/dataModel';
  import { ref, reactive, toRefs, unref, nextTick } from 'vue';
  import { BasicModal, useModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { useGeneratorStore } from '/@/store/modules/generator';
  import formValidate from '/@/utils/formValidate';
  import TableModal from '/@/views/onlineDev/webDesign/components/TableModal.vue';
  import { FormGenerator } from '/@/components/FormGenerator';
  import { BasicColumnDesign } from '/@/components/ColumnDesign';

  interface State {
    activeStep: number;
    maxStep: number;
    loading: boolean;
    btnLoading: boolean;
    relationTable: boolean;
    mainTableFields: any[];
    dbOptions: any[];
    tables: any[];
    defaultTable: any[];
    dataForm: Recordable;
    [prop: string]: any;
  }
  interface ComType {
    getData: () => any;
  }

  const emit = defineEmits(['register', 'reload']);
  const [registerForm, { setFieldsValue, getFieldsValue, resetFields, validate, updateSchema }] = useForm({
    schemas: [
      {
        field: 'fullName',
        label: '模板名称',
        component: 'Input',
        componentProps: { placeholder: '请输入', maxlength: 100 },
        rules: [{ required: true, trigger: 'blur', message: '必填' }],
      },
      {
        field: 'enCode',
        label: '模板编码',
        component: 'Input',
        componentProps: { placeholder: '请输入', maxlength: 50 },
        rules: [
          { required: true, trigger: 'blur', message: '必填' },
          { validator: formValidate('enCode'), trigger: 'blur' },
        ],
      },
      {
        field: 'category',
        label: '模板分类',
        component: 'Select',
        componentProps: { placeholder: '请选择', showSearch: true },
        rules: [{ required: true, trigger: 'change', message: '必填' }],
      },
      {
        field: 'enableFlow',
        label: '模板类型',
        defaultValue: 0,
        component: 'Radio',
        componentProps: {
          options: [
            { id: 0, fullName: '普通表单' },
            { id: 1, fullName: '流程表单' },
          ],
          optionType: 'button',
          buttonStyle: 'solid',
        },
        rules: [{ required: true, trigger: 'change', message: '必填', type: 'number' }],
      },
      {
        field: 'description',
        label: '模板说明',
        component: 'Textarea',
        componentProps: { placeholder: '请输入' },
      },
      {
        field: 'dbLinkId',
        label: '数据连接',
        defaultValue: '0',
        component: 'Select',
        componentProps: { placeholder: '请选择', allowClear: false, showSearch: true, fieldNames: { options: 'children' }, onChange: onDbChange },
      },
    ],
  });
  const [registerTableModal, { openModal: openTableModal }] = useModal();
  const [registerModal, { closeModal, changeLoading }] = useModalInner(init);
  const { createMessage, createConfirm } = useMessage();
  const generatorStore = useGeneratorStore();
  const { t } = useI18n();
  const state = reactive<State>({
    activeStep: 0,
    maxStep: 2,
    loading: false,
    btnLoading: false,
    relationTable: false,
    mainTableFields: [],
    dbOptions: [],
    tables: [],
    defaultTable: [],
    dataForm: {
      id: '',
      fullName: '',
      enCode: '',
      type: 1,
      webType: 2,
      dbLinkId: '0',
      sortCode: 0,
      enableFlow: 0,
      state: 1,
      category: '',
      description: '',
      tables: '',
      interfaceId: '',
      interfaceName: '',
      interfaceParam: '',
    },
    formData: null,
    columnData: null,
    appColumnData: null,
    dbType: 'MySQL',
  });
  const generatorRef = ref<Nullable<ComType>>(null);
  const columnDesignRef = ref<Nullable<ComType>>(null);
  const { activeStep, maxStep, loading, btnLoading, tables, mainTableFields, dbType, formData, columnData, appColumnData, dataForm } = toRefs(state);
  const columns = [
    { title: '类别', dataIndex: 'typeId', key: 'typeId', width: 65 },
    { title: '表名', dataIndex: 'table', key: 'table' },
    { title: '外键字段', dataIndex: 'tableField', key: 'tableField', width: 160 },
    { title: '关联主键', dataIndex: 'relationField', key: 'relationField', width: 160 },
    { title: '操作', dataIndex: 'action', key: 'action', width: 50, fixed: 'right' },
  ];
  function handleDelItem(record, index) {
    createConfirm({
      iconType: 'warning',
      title: t('common.tipTitle'),
      content: '确定要移除当前行?',
      onOk: () => {
        state.tables.splice(index, 1);
        if (record.typeId == '1' && state.tables.length) {
          state.tables[0].typeId = '1';
          state.tables[0].relationTable = '';
          state.tables[0].tableField = '';
          state.tables[0].relationField = '';
          state.tables[0].relationTable = '';
          state.mainTableFields = state.tables[0].fields;
          state.relationTable = state.tables[0].table;
        }
      },
    });
  }
  function init(data) {
    state.activeStep = 0;
    state.loading = true;
    state.tables = [];
    state.defaultTable = [];
    state.formData = null;
    state.columnData = null;
    state.appColumnData = null;
    updateSchema([{ field: 'category', componentProps: { options: data.categoryList } }]);
    getDbOptions();
    changeLoading(true);
    resetFields();
    state.dataForm.id = data.id;
    if (state.dataForm.id) {
      getInfo(state.dataForm.id).then(res => {
        state.dataForm = res.data;
        state.maxStep = state.dataForm.webType == 4 ? 1 : 2;
        setFieldsValue(state.dataForm);
        state.formData = state.dataForm.formData && JSON.parse(state.dataForm.formData);
        state.columnData = state.dataForm.columnData && JSON.parse(state.dataForm.columnData);
        state.appColumnData = state.dataForm.appColumnData && JSON.parse(state.dataForm.appColumnData);
        state.tables = (state.dataForm.tables && JSON.parse(state.dataForm.tables)) || [];
        state.defaultTable = (state.dataForm.tables && JSON.parse(state.dataForm.tables)) || [];
        updateFields();
        changeLoading(false);
      });
    } else {
      state.dataForm.type = data.type;
      state.dataForm.webType = data.webType || 2;
      state.maxStep = state.dataForm.webType == 4 ? 1 : 2;
      state.loading = false;
      changeLoading(false);
    }
  }
  function toggleWebType(type) {
    createConfirm({
      iconType: 'warning',
      title: t('common.tipTitle'),
      content: type == '1' ? '关闭后，将切换为纯表单模式' : '开启后，将切换为表单+列表模式',
      onOk: () => {
        state.dataForm.webType = type;
      },
    });
  }
  async function updateFields() {
    if (!state.tables.length) {
      state.loading = false;
      nextTick(() => handleNext());
      return;
    }
    state.dataForm.dbLinkId = state.dataForm.dbLinkId || '0';
    const type = state.dataForm.type;
    const queryType = type == 3 || type == 4 || type == 5 ? '1' : '0';
    for (let i = 0; i < state.tables.length; i++) {
      const res = await getDataModelFieldList(state.dataForm.dbLinkId, state.tables[i].table, queryType);
      const fields = res.data.list;
      state.tables[i].fields = fields;
      if (state.tables[i].typeId == '1') {
        state.mainTableFields = state.tables[i].fields;
        state.relationTable = state.tables[i].table;
      }
    }
    state.loading = false;
    nextTick(() => handleNext());
  }
  function onDbChange() {
    state.tables = [];
  }
  function getDbOptions() {
    getDataSourceSelector().then(res => {
      let list = res.data.list || [];
      list = list.filter(o => o.children && o.children.length);
      if (list[0] && list[0].children && list[0].children.length) list[0] = list[0].children[0];
      delete list[0].children;
      state.dbOptions = list;
      updateSchema([{ field: 'dbLinkId', componentProps: { options: state.dbOptions } }]);
    });
  }
  function getDbType() {
    for (let i = 0; i < state.dbOptions.length; i++) {
      const item = state.dbOptions[i];
      if (state.dataForm.dbLinkId === item.id) {
        state.dbType = item.dbType;
        break;
      }
      const e = state.dbOptions[i].children || [];
      for (let j = 0; j < e.length; j++) {
        if (state.dataForm.dbLinkId === e[j].id) {
          state.dbType = e[j].dbType;
          break;
        }
      }
    }
  }
  function openTableBox() {
    const values = getFieldsValue();
    if (!values.dbLinkId) return createMessage.error('请先选择数据库');
    openTableModal(true, { dbLinkId: values.dbLinkId });
  }
  async function onTableSelect(data) {
    const values = getFieldsValue();
    const type = state.dataForm.type;
    const queryType = type == 3 || type == 4 || type == 5 ? '1' : '0';
    const checkList: any[] = [];
    if (!state.tables.length) {
      for (let i = 0; i < data.length; i++) {
        const e = data[i];
        const relationTable = data[0].table;
        const typeId = i == 0 ? '1' : '0';
        const res = await getDataModelFieldList(values.dbLinkId, e.table, queryType);
        const fields = res.data.list;
        const item = {
          relationField: '',
          relationTable: i == 0 ? '' : relationTable,
          table: e.table,
          tableName: e.tableName,
          tableField: '',
          typeId,
          fields,
        };
        checkList.push(item);
      }
      state.relationTable = checkList[0].table;
      state.mainTableFields = checkList[0].fields;
      state.tables = checkList;
    } else {
      for (let i = 0; i < data.length; i++) {
        const e = data[i];
        let boo = state.tables.some(o => o.table == e.table);
        if (!boo) {
          const res = await getDataModelFieldList(values.dbLinkId, e.table, queryType);
          const fields = res.data.list;
          const item = {
            relationField: '',
            relationTable: state.relationTable,
            table: e.table,
            tableName: e.tableName,
            tableField: '',
            typeId: '0',
            fields,
          };
          checkList.push(item);
        }
      }
      state.tables = [...state.tables, ...checkList];
    }
  }
  function changeTable(record) {
    state.relationTable = record.table;
    state.mainTableFields = record.fields;
    for (let i = 0; i < state.tables.length; i++) {
      state.tables[i].typeId = state.tables[i].table === record.table ? '1' : '0';
      state.tables[i].relationTable = state.tables[i].table === record.table ? '' : state.relationTable;
      state.tables[i].relationField = '';
      state.tables[i].tableField = '';
    }
  }
  function handlePrev() {
    state.activeStep -= 1;
    if (state.activeStep == 0) updateTables();
  }
  async function handleNext() {
    if (state.activeStep < 1) {
      const values = await validate();
      if (!values) return;
      state.dataForm = { ...state.dataForm, ...values };
      getDbType();
      const type = state.dataForm.type;
      if (!state.tables.length) {
        if (state.defaultTable.length || type == 3 || type == 4) {
          createMessage.warning('请至少选择一个数据表');
          return;
        }
        generatorStore.setHasTable(false);
        generatorStore.setAllTable([]);
        generatorStore.setFormItemList([]);
        state.activeStep += 1;
      } else {
        if (!exist()) return;
        const subTable = state.tables.filter(o => o.typeId == '0');
        generatorStore.setHasTable(true);
        generatorStore.setAllTable(state.tables);
        generatorStore.setSubTable(subTable);
        generatorStore.setFormItemList(state.mainTableFields);
        state.activeStep += 1;
      }
    } else if (state.activeStep === 1) {
      (unref(generatorRef) as ComType)
        .getData()
        .then(res => {
          state.formData = res.formData;
          state.dataForm.formData = state.formData ? JSON.stringify(state.formData) : null;
          state.activeStep += 1;
        })
        .catch(err => {
          err.msg && createMessage.warning(err.msg);
        });
    } else {
      (unref(columnDesignRef) as ComType)
        .getData()
        .then(res => {
          state.columnData = res.columnData;
          state.appColumnData = res.appColumnData;
          state.activeStep += 1;
        })
        .catch(err => {
          err.msg && createMessage.warning(err.msg);
        });
    }
  }
  function onStepChange(current) {
    if (current == 0) updateTables();
  }
  function updateTables() {
    state.tables = generatorStore.getAllTable;
    state.mainTableFields = generatorStore.getFormItemList;
  }
  function exist() {
    let isOk = true;
    for (let i = 0; i < state.tables.length; i++) {
      const e = state.tables[i];
      if (e.typeId == '0') {
        if (!e.tableField) {
          createMessage.warning(`表${e.table}外键字段不能为空`);
          isOk = false;
          break;
        }
        if (!e.relationField) {
          createMessage.warning(`表${e.table}关联主键不能为空`);
          isOk = false;
          break;
        }
      }
    }
    return isOk;
  }
  async function handleSubmit() {
    if (state.activeStep < 1) {
      const type = state.dataForm.type;
      if (!state.tables.length && (state.defaultTable.length || type == 3 || type == 4)) return createMessage.warning('请至少选择一个数据表');
      const values = await validate();
      if (!values) return;
      state.dataForm = { ...state.dataForm, ...values };
      handleRequest();
    } else if (state.activeStep === 1) {
      (unref(generatorRef) as ComType)
        .getData()
        .then(res => {
          state.formData = res.formData;
          state.dataForm.formData = state.formData ? JSON.stringify(state.formData) : null;
          handleRequest();
        })
        .catch(err => {
          err.msg && createMessage.warning(err.msg);
        });
    } else {
      if (state.dataForm.webType == 1) return handleRequest();
      (unref(columnDesignRef) as ComType)
        .getData()
        .then(res => {
          state.columnData = res.columnData;
          state.appColumnData = res.appColumnData;
          handleRequest();
        })
        .catch(err => {
          err.msg && createMessage.warning(err.msg);
        });
    }
  }
  function handleRequest() {
    state.btnLoading = true;
    const query = {
      ...state.dataForm,
      tables: JSON.stringify(state.tables),
      formData: state.formData ? JSON.stringify(state.formData) : null,
      columnData: state.columnData ? JSON.stringify(state.columnData) : null,
      appColumnData: state.appColumnData ? JSON.stringify(state.appColumnData) : null,
    };
    const formMethod = state.dataForm.id ? update : create;
    formMethod(query)
      .then(res => {
        createMessage.success(res.msg);
        state.btnLoading = false;
        setTimeout(() => {
          closeModal();
          emit('reload');
        }, 200);
      })
      .catch(() => {
        state.btnLoading = false;
      });
  }
</script>
