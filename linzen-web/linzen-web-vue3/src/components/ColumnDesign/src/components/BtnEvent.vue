<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    title="按钮事件配置"
    helpMessage="小程序不支持在线JS脚本"
    :width="800"
    @ok="handleSubmit"
    destroyOnClose
    class="form-script-modal btn-event-modal">
    <div class="form-script-modal-body">
      <div class="main-board">
        <a-form :colon="false" labelAlign="left" :labelCol="{ style: { width: '90px' } }" :model="dataForm" :rules="formRules" ref="formElRef" hideRequiredMark>
          <a-form-item label="类型选择" name="btnType">
            <a-radio-group v-model:value="dataForm.btnType" @change="onBtnTypeChange">
              <a-radio :value="1">弹窗配置</a-radio>
              <a-radio :value="2">JS脚本</a-radio>
              <a-radio :value="3">接口配置</a-radio>
            </a-radio-group>
          </a-form-item>
          <template v-if="dataForm.btnType == 1">
            <a-form-item label="选择表单" name="modelId">
              <linzen-tree-select v-model:value="dataForm.modelId" :options="treeData" placeholder="请选择" lastLevel allowClear @change="onModeIdChange" />
            </a-form-item>
            <a-form-item label="弹窗标题" name="popupTitle">
              <a-input v-model:value="dataForm.popupTitle" placeholder="请输入" allowClear />
            </a-form-item>
            <a-form-item label="弹窗类型" name="popupType" v-if="showType === 'pc'">
              <linzen-select v-model:value="dataForm.popupType" placeholder="请选择" :options="popupTypeOptions" />
            </a-form-item>
            <a-form-item label="弹窗宽度" name="popupWidth" v-if="showType === 'pc'">
              <a-select v-model:value="dataForm.popupWidth">
                <a-select-option v-for="item in popupWidthOptions" :key="item" :value="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="赋值规则" style="margin-bottom: 0"></a-form-item>
            <a-table :data-source="dataForm.formOptions" :columns="formOptionsColumns" size="small" :pagination="false">
              <template #bodyCell="{ column, record, index }">
                <template v-if="column.key === 'currentField'">
                  <linzen-select
                    v-model:value="record.currentField"
                    placeholder="请选择表单字段"
                    :options="formFieldsOptions"
                    :fieldNames="{ options: 'options1' }"
                    allowClear
                    showSearch />
                </template>
                <template v-if="column.key === 'type'">赋值给</template>
                <template v-if="column.key === 'field'">
                  <linzen-select
                    v-model:value="record.field"
                    placeholder="请选择表单字段"
                    :options="fieldOptions"
                    showSearch
                    @dropdownVisibleChange="visibleChange" />
                </template>
                <template v-if="column.key === 'action'">
                  <a-button class="action-btn" type="link" color="error" @click="handleDelItem(index)" size="small">删除</a-button>
                </template>
              </template>
              <template #emptyText>
                <p class="leading-60px">暂无数据</p>
              </template>
            </a-table>
            <div class="table-add-action mb-20px" @click="handleAddFormOptions()">
              <a-button type="link" preIcon="icon-linzen icon-linzen-btn-add">新增</a-button>
            </div>
            <a-form-item label="自定义按钮事件" :labelCol="{ style: { width: '110px' } }">
              <a-switch v-model:checked="dataForm.customBtn" />
              <span class="tip">（开启后，弹窗中按钮事件失效，调用接口事件。）</span>
            </a-form-item>
            <template v-if="dataForm.customBtn">
              <a-form-item label="数据接口" name="interfaceId">
                <interface-modal :value="dataForm.interfaceId" :title="dataForm.interfaceName" dataType="1,3" @change="onInterfaceChange" />
              </a-form-item>
              <a-form-item label="参数设置" style="margin-bottom: 0"></a-form-item>
              <a-table :data-source="dataForm.templateJson" :columns="templateJsonColumns" size="small" :pagination="false" class="mb-20px">
                <template #bodyCell="{ column, record }">
                  <template v-if="column.key === 'field'">
                    <span class="required-sign">{{ record.required ? '*' : '' }}</span>
                    {{ record.field }}{{ record.fieldName ? '(' + record.fieldName + ')' : '' }}
                  </template>
                  <template v-if="column.key === 'sourceType'">
                    <linzen-select
                      v-model:value="record.sourceType"
                      placeholder="请选择参数来源"
                      :options="sourceTypeOptions"
                      class="!w-84px"
                      @change="record.relationField = ''" />
                  </template>
                  <template v-if="column.key === 'relationField'">
                    <linzen-select
                      v-model:value="record.relationField"
                      placeholder="请选择弹窗表单字段"
                      :options="fieldOptions"
                      allowClear
                      showSearch
                      @dropdownVisibleChange="visibleChange"
                      v-if="record.sourceType === 1" />
                    <template v-else>
                      <linzen-input-number
                        v-if="['int', 'decimal'].includes(record.dataType)"
                        v-model:value="record.relationField"
                        placeholder="请输入参数值"
                        :disabled="record.sourceType == 3"
                        clearable />
                      <linzen-date-picker
                        v-else-if="record.dataType == 'datetime'"
                        class="!w-full"
                        v-model:value="record.relationField"
                        placeholder="请选择参数值"
                        format="YYYY-MM-DD HH:mm:ss"
                        :disabled="record.sourceType == 3"
                        clearable />
                      <a-input v-else v-model:value="record.relationField" placeholder="请输入参数值" :disabled="record.sourceType == 3" clearable />
                    </template>
                  </template>
                </template>
                <template #emptyText>
                  <p class="leading-60px">暂无数据</p>
                </template>
              </a-table>
            </template>
          </template>
          <template v-if="dataForm.btnType == 3">
            <a-form-item label="数据接口" name="interfaceId">
              <interface-modal :value="dataForm.interfaceId" :title="dataForm.interfaceName" dataType="1,3" @change="onInterfaceChange" />
            </a-form-item>
            <a-form-item label="参数设置" style="margin-bottom: 0"></a-form-item>
            <a-table :data-source="dataForm.templateJson" :columns="templateJsonColumns" size="small" :pagination="false" class="mb-20px">
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'field'">
                  <span class="required-sign">{{ record.required ? '*' : '' }}</span>
                  {{ record.field }}{{ record.fieldName ? '(' + record.fieldName + ')' : '' }}
                </template>
                <template v-if="column.key === 'sourceType'">
                  <linzen-select
                    v-model:value="record.sourceType"
                    placeholder="请选择参数来源"
                    :options="sourceTypeOptions"
                    class="!w-84px"
                    @change="record.relationField = ''" />
                </template>
                <template v-if="column.key === 'relationField'">
                  <linzen-select
                    v-model:value="record.relationField"
                    placeholder="请选择当前表单字段"
                    :options="formFieldsOptions"
                    :fieldNames="{ options: 'options1' }"
                    allowClear
                    showSearch
                    v-if="record.sourceType === 1" />
                  <template v-else>
                    <linzen-input-number
                      v-if="['int', 'decimal'].includes(record.dataType)"
                      v-model:value="record.relationField"
                      placeholder="请输入参数值"
                      :disabled="record.sourceType == 3"
                      clearable />
                    <linzen-date-picker
                      v-else-if="record.dataType == 'datetime'"
                      class="!w-full"
                      v-model:value="record.relationField"
                      placeholder="请选择参数值"
                      format="YYYY-MM-DD HH:mm:ss"
                      :disabled="record.sourceType == 3"
                      clearable />
                    <a-input v-else v-model:value="record.relationField" placeholder="请输入参数值" :disabled="record.sourceType == 3" clearable />
                  </template>
                </template>
              </template>
              <template #emptyText>
                <p class="leading-60px">暂无数据</p>
              </template>
            </a-table>
            <a-form-item label="启用确认框">
              <a-switch v-model:checked="dataForm.useConfirm" />
            </a-form-item>
            <a-form-item name="confirmTitle" v-if="dataForm.useConfirm">
              <a-input v-model:value="dataForm.confirmTitle" placeholder="请输入确认框提示语" allowClear />
            </a-form-item>
          </template>
        </a-form>
        <template v-if="dataForm.btnType == 2">
          <div class="main-board-editor">
            <MonacoEditor v-model="dataForm.func" />
          </div>
          <div class="main-board-tips">
            <p>支持JavaScript的脚本，<ScriptDemo type="btnEvent" /></p>
          </div>
        </template>
      </div>
    </div>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { reactive, ref, toRefs } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { MonacoEditor } from '/@/components/CodeEditor';
  import { getVisualDevSelector, getFormDataFields } from '/@/api/onlineDev/visualDev';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { InterfaceModal } from '/@/components/CommonModal';
  import type { FormInstance } from 'ant-design-vue';
  import { cloneDeep } from 'lodash-es';
  import ScriptDemo from '/@/components/FormGenerator/src/components/ScriptDemo.vue';
  import { noAllowSelectList } from '/@/components/FormGenerator/src/helper/config';

  const emit = defineEmits(['register', 'confirm']);
  const { createMessage } = useMessage();
  const [registerModal, { closeModal }] = useModalInner(init);
  const popupTypeOptions = [
    { id: 'dialog', fullName: '居中弹窗' },
    { id: 'drawer', fullName: '右侧弹窗' },
  ];
  const sourceTypeOptions = [
    { id: 1, fullName: '字段' },
    { id: 2, fullName: '自定义' },
    { id: 3, fullName: '为空' },
  ];
  const popupWidthOptions = ['600px', '800px', '1000px', '40%', '50%', '60%', '70%', '80%'];
  const formOptionsColumns = [
    { width: 50, title: '序号', align: 'center', customRender: ({ index }) => index + 1 },
    { title: '当前表单值', dataIndex: 'currentField', key: 'currentField', width: 250 },
    { title: '赋值给', dataIndex: 'type', key: 'type', align: 'center' },
    { title: '弹窗表单值', dataIndex: 'field', key: 'field', width: 250 },
    { title: '操作', dataIndex: 'action', key: 'action', width: 50 },
  ];
  const templateJsonColumns = [
    { width: 50, title: '序号', align: 'center', customRender: ({ index }) => index + 1 },
    { title: '参数名称', dataIndex: 'field', key: 'field', width: 200 },
    { title: '参数来源', dataIndex: 'sourceType', key: 'sourceType', width: 100 },
    { title: '参数值', dataIndex: 'relationField', key: 'relationField' },
  ];
  const defaultData = {
    btnType: 1,
    modelId: '',
    popupTitle: '自定义操作',
    popupType: 'dialog',
    popupWidth: '800px',
    formOptions: [],
    customBtn: false,
    func: '({ data, index, refresh, onlineUtils }) => {\r\n   \r\n}',
    interfaceId: '',
    interfaceName: '',
    templateJson: [],
    useConfirm: false,
    confirmTitle: '此操作将通过接口处理',
  };
  const showType = ref('pc');
  const state = reactive({
    dataForm: cloneDeep(defaultData),
    formRules: {
      btnType: [{ required: true, message: '类型不能为空', trigger: 'change' }],
      modelId: [{ required: true, message: '表单不能为空', trigger: 'change' }],
      popupTitle: [{ required: true, message: '弹窗标题不能为空', trigger: 'change' }],
      interfaceId: [{ required: true, message: '数据接口不能为空', trigger: 'change' }],
      confirmTitle: [{ required: true, message: '确认框不能为空', trigger: 'change' }],
    },
  });
  const { dataForm, formRules } = toRefs(state);
  const treeData = ref([]);
  const fieldOptions = ref<any[]>([]);
  const formFieldsOptions = ref<any[]>([]);
  const formElRef = ref<FormInstance>();

  function init(data) {
    showType.value = data.showType || 'pc';
    state.dataForm = { ...cloneDeep(defaultData), ...data.dataForm };
    formFieldsOptions.value = cloneDeep(data.formFieldsOptions)
      .filter(o => !noAllowSelectList.includes(o.__config__.projectKey) && o.id.indexOf('-') < 0)
      .map(o => ({ ...o, disabled: false }));
    formFieldsOptions.value.unshift({ fullName: '@表单ID', id: '@formId' });
    getFeatureList();
    getFieldOptions();
  }
  function getFeatureList() {
    getVisualDevSelector({ type: 1, webType: '1,2', enableFlow: 0, isRelease: 1 }).then(res => {
      treeData.value = res.data.list;
    });
  }
  function onBtnTypeChange() {
    const data: any = cloneDeep(defaultData);
    delete data.btnType;
    state.dataForm = { ...state.dataForm, ...data };
    state.dataForm.formOptions = [];
    state.dataForm.templateJson = [];
    fieldOptions.value = [];
    formElRef.value?.clearValidate();
  }
  function onModeIdChange(val) {
    clearField();
    if (!val) {
      fieldOptions.value = [];
      return;
    }
    getFieldOptions();
  }
  function clearField() {
    state.dataForm.formOptions = (state.dataForm.formOptions as any[]).map(o => ({ ...o, field: '' })) as any;
    state.dataForm.templateJson = (state.dataForm.templateJson as any[]).map(o => ({ ...o, relationField: o.sourceType === 1 ? '' : o.relationField })) as any;
  }
  function getFieldOptions() {
    if (!state.dataForm.modelId) return;
    getFormDataFields(state.dataForm.modelId, 1).then(res => {
      fieldOptions.value = res.data.list.map(o => ({
        ...o,
        id: o.vmodel,
        fullName: o.label,
      }));
    });
  }
  function handleAddFormOptions() {
    (state.dataForm.formOptions as any).push({
      currentField: '',
      field: '',
    });
  }
  function handleDelItem(index) {
    state.dataForm.formOptions.splice(index, 1);
  }
  function visibleChange(val) {
    if (!val) return;
    if (!state.dataForm.modelId) createMessage.warning('请先选择关联功能');
  }
  function onInterfaceChange(id, row) {
    if (!id) {
      state.dataForm.interfaceId = '';
      state.dataForm.interfaceName = '';
      state.dataForm.templateJson = [];
      return;
    }
    if (state.dataForm.interfaceId === id) return;
    state.dataForm.interfaceId = id;
    state.dataForm.interfaceName = row.fullName;
    state.dataForm.templateJson = row.templateJson ? row.templateJson.map(o => ({ ...o, relationField: '', sourceType: 1 })) : [];
  }
  async function handleSubmit() {
    try {
      const values = await formElRef.value?.validate();
      if (!values) return;
      if (state.dataForm.btnType == 1) {
        if (!state.dataForm.formOptions.length) return createMessage.warning('赋值规则不能为空');
        if (state.dataForm.formOptions.length) {
          for (let i = 0; i < state.dataForm.formOptions.length; i++) {
            const e: any = state.dataForm.formOptions[i];
            if (!e.currentField) return createMessage.warning(`赋值规则第${i + 1}行当前表单值不能为空`);
            if (!e.field) return createMessage.warning(`赋值规则第${i + 1}行弹窗表单值不能为空`);
          }
        }
      }
      emit('confirm', state.dataForm);
      closeModal();
    } catch (_) {}
  }
</script>
