<template>
  <BasicModal v-bind="$attrs" @register="registerModal" defaultFullscreen :footer="null" :closable="false" :keyboard="false" class="linzen-full-modal full-modal">
    <template #title>
      <div class="linzen-full-modal-header">
        <div class="header-title">
          <img src="../../../assets/images/linzen.png" class="header-logo" />
          <p class="header-txt" v-if="!activeStep"> · 在线开发</p>
          <a-tooltip :title="dataForm.fullName" v-else>
            <p class="header-txt"> · {{ dataForm.fullName }}</p>
          </a-tooltip>
        </div>
        <a-steps v-model:current="activeStep" type="navigation" size="small">
          <a-step title="基础设置" />
          <a-step title="列表设计" disabled />
        </a-steps>
        <a-space class="options" :size="10">
          <a-button @click="handlePrev" :disabled="activeStep <= 0 || btnLoading">{{ t('common.prev') }}</a-button>
          <a-button @click="handleNext" :disabled="activeStep >= maxStep || loading || btnLoading">{{ t('common.next') }} </a-button>
          <a-button type="primary" @click="handleSubmit()" :disabled="loading" :loading="btnLoading">{{ t('common.saveText') }}</a-button>
          <a-button @click="closeModal()">{{ t('common.cancelText') }}</a-button>
        </a-space>
      </div>
    </template>
    <a-row type="flex" justify="center" align="middle" class="basic-content" v-show="!activeStep">
      <a-col :span="12" :xxl="10" class="basic-form">
        <BasicForm @register="registerForm">
          <template #interfaceId="{ model, field }">
            <interface-modal :value="model[field]" :title="dataForm.interfaceName" :hasPage="0" dataType="1,3" @change="onInterfaceChange" />
          </template>
        </BasicForm>
        <a-table :data-source="interfaceParam" :columns="columns" size="small" :pagination="false" v-if="interfaceParam && interfaceParam.length">
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'field'">
              <span class="required-sign">{{ record.required ? '*' : '' }}</span>
              {{ record.field }}{{ record.fieldName ? '(' + record.fieldName + ')' : '' }}
            </template>
            <template v-if="column.key === 'defaultValue'">
              <linzen-input-number
                v-model:value="record.defaultValue"
                placeholder="请输入"
                allowClear
                :disabled="record.disabled"
                v-if="record.dataType === 'int' || record.dataType === 'decimal'" />
              <linzen-date-picker
                class="!w-full"
                v-model:value="record.defaultValue"
                placeholder="请选择"
                format="yyyy-MM-dd HH:mm:ss"
                allowClear
                :disabled="record.disabled"
                v-else-if="record.dataType === 'datetime'" />
              <a-input v-model:value="record.defaultValue" placeholder="请输入" allowClear :disabled="record.disabled" v-else />
            </template>
            <template v-if="column.key === 'useSearch'">
              <a-checkbox v-model:checked="record.useSearch" @change="onUseSearchChange($event, record)" />
            </template>
          </template>
        </a-table>
      </a-col>
    </a-row>
    <BasicColumnDesign
      ref="columnDesignRef"
      :columnData="columnData"
      :appColumnData="appColumnData"
      :formInfo="dataForm"
      :viewFields="viewFields"
      :interfaceParam="interfaceParam"
      v-if="activeStep == 1" />
  </BasicModal>
</template>
<script lang="ts" setup>
  import { getInfo, create, update } from '/@/api/onlineDev/visualDev';
  import { ref, reactive, toRefs, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { BasicForm, useForm } from '/@/components/Form';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useI18n } from '/@/hooks/web/useI18n';
  import formValidate from '/@/utils/formValidate';
  import { BasicColumnDesign } from '/@/components/ColumnDesign';
  import { InterfaceModal } from '/@/components/CommonModal';
  import { getDataInterfaceInfo } from '/@/api/systemData/dataInterface';

  interface State {
    activeStep: number;
    maxStep: number;
    loading: boolean;
    btnLoading: boolean;
    tables: any[];
    defaultTable: any[];
    dataForm: Recordable;
    viewFields: any[];
    interfaceParam: any[];
    [prop: string]: any;
  }
  interface ComType {
    getData: () => any;
  }

  const emit = defineEmits(['register', 'reload']);
  const [registerForm, { setFieldsValue, resetFields, validate, updateSchema, clearValidate }] = useForm({
    schemas: [
      {
        field: 'fullName',
        label: '视图名称',
        component: 'Input',
        componentProps: { placeholder: '请输入', maxlength: 100 },
        rules: [{ required: true, trigger: 'blur', message: '必填' }],
      },
      {
        field: 'enCode',
        label: '视图编码',
        component: 'Input',
        componentProps: { placeholder: '请输入', maxlength: 50 },
        rules: [
          { required: true, trigger: 'blur', message: '必填' },
          { validator: formValidate('enCode'), trigger: 'blur' },
        ],
      },
      {
        field: 'category',
        label: '视图分类',
        component: 'Select',
        componentProps: { placeholder: '请选择', showSearch: true },
        rules: [{ required: true, trigger: 'change', message: '必填' }],
      },
      {
        field: 'sortCode',
        label: '视图排序',
        defaultValue: 0,
        component: 'InputNumber',
        componentProps: { min: 0, max: 999999 },
      },
      {
        field: 'description',
        label: '视图说明',
        component: 'Textarea',
        componentProps: { placeholder: '请输入' },
      },
      {
        field: 'interfaceId',
        label: '数据接口',
        slot: 'interfaceId',
        component: 'Select',
        rules: [{ required: true, trigger: 'change', message: '必填' }],
      },
    ],
  });
  const [registerModal, { closeModal, changeLoading }] = useModalInner(init);
  const { createMessage } = useMessage();
  const { t } = useI18n();
  const state = reactive<State>({
    activeStep: 0,
    maxStep: 1,
    loading: false,
    btnLoading: false,
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
    interfaceParam: [],
    viewFields: [],
  });
  const columnDesignRef = ref<Nullable<ComType>>(null);
  const { activeStep, maxStep, loading, btnLoading, columnData, appColumnData, dataForm, interfaceParam, viewFields } = toRefs(state);
  const columns = [
    { title: '序号', width: 50, align: 'center', customRender: ({ index }) => index + 1 },
    { title: '参数名称', dataIndex: 'field', key: 'field', width: 200 },
    { title: '值', dataIndex: 'defaultValue', key: 'defaultValue' },
    { title: '作为查询字段', dataIndex: 'useSearch', key: 'useSearch', width: 100, align: 'center' },
  ];
  function init(data) {
    state.activeStep = 0;
    state.loading = true;
    state.tables = [];
    state.defaultTable = [];
    state.interfaceParam = [];
    state.formData = null;
    state.columnData = null;
    state.appColumnData = null;
    state.dataForm.interfaceId = '';
    state.dataForm.interfaceName = '';
    state.dataForm.interfaceParam = '';
    updateSchema([{ field: 'category', componentProps: { options: data.categoryList } }]);
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
        state.interfaceParam = state.dataForm.interfaceParam ? JSON.parse(state.dataForm.interfaceParam) : [];
        if (state.dataForm.interfaceId) initInterface();
        state.loading = false;
        changeLoading(false);
      });
    } else {
      state.dataForm.type = data.type;
      state.dataForm.webType = 4;
      state.maxStep = state.dataForm.webType == 4 ? 1 : 2;
      state.loading = false;
      changeLoading(false);
    }
  }
  function initInterface() {
    changeLoading(true);
    getDataInterfaceInfo(state.dataForm.interfaceId).then(res => {
      const data = res.data;
      state.dataForm.interfaceName = data.fullName;
      state.viewFields = data.fieldJson ? JSON.parse(data.fieldJson) : [];
      const parameterJson = (data.parameterJson ? JSON.parse(data.parameterJson) : []).map(o => ({ ...o, useSearch: false, disabled: false }));
      for (let i = 0; i < parameterJson.length; i++) {
        const findIndex = state.interfaceParam.findIndex(o => o.field === parameterJson[i].field);
        if (findIndex > -1) parameterJson[i] = state.interfaceParam[findIndex];
      }
      state.interfaceParam = parameterJson;
      changeLoading(false);
    });
  }
  function onInterfaceChange(val, row) {
    state.viewFields = [];
    if (!val) {
      state.dataForm.interfaceId = '';
      state.dataForm.interfaceName = '';
      state.interfaceParam = [];
      state.dataForm.interfaceParam = '';
      setFieldsValue({
        interfaceId: state.dataForm.interfaceId,
        interfaceName: state.dataForm.interfaceName,
        interfaceParam: state.dataForm.interfaceParam,
      });
      return;
    }
    state.viewFields = row.fieldJson ? JSON.parse(row.fieldJson) : [];
    if (state.dataForm.interfaceId === val) return;
    state.dataForm.interfaceId = val;
    state.dataForm.interfaceName = row.fullName;
    state.interfaceParam = (row.parameterJson ? JSON.parse(row.parameterJson) : []).map(o => ({ ...o, useSearch: false, disabled: false }));
    state.dataForm.interfaceParam = JSON.stringify(state.interfaceParam);
    setFieldsValue({
      interfaceId: state.dataForm.interfaceId,
      interfaceName: state.dataForm.interfaceName,
      interfaceParam: state.dataForm.interfaceParam,
    });
    setTimeout(() => {
      clearValidate('interfaceId');
    }, 0);
  }
  function handlePrev() {
    state.activeStep -= 1;
  }
  async function handleNext() {
    if (state.activeStep < 1) {
      const values = await validate();
      if (!values) return;
      if (!state.viewFields.length) return createMessage.error('请先设置数据接口的字段列表！');
      state.dataForm = { ...state.dataForm, ...values };
      state.activeStep += 1;
    }
  }
  async function handleSubmit() {
    if (state.activeStep < 1) {
      const values = await validate();
      if (!values) return;
      if (!state.viewFields.length) return createMessage.error('请先设置数据接口的字段列表！');
      state.dataForm = { ...state.dataForm, ...values };
      handleRequest();
    } else if (state.activeStep === 1) {
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
      interfaceParam: JSON.stringify(state.interfaceParam),
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
  function onUseSearchChange(e, record) {
    record.defaultValue = undefined;
    record.disabled = e.target.checked;
  }
</script>
