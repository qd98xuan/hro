<template>
  <section class="common-pane">
    <ScrollContainer class="config-content">
      <a-form :colon="false" layout="vertical" :model="formConf" class="config-form">
        <a-form-item label="获取方式">
          <a-radio-group v-model:value="formConf.formType" class="common-radio formType-radio" @change="onFormTypeChange">
            <a-radio :value="1">从普通表单中获取</a-radio>
            <a-radio :value="2">从流程表单中获取<BasicHelp text="只获取已完成的流程数据" /></a-radio>
            <a-radio :value="3">从数据接口中获取</a-radio>
          </a-radio-group>
          <FormListModal
            :value="formConf.formId"
            :title="formConf.formName"
            :enableFlow="formConf.formType == 1 ? '0' : '1'"
            :allowClear="false"
            @change="onFormIdChange"
            placeholder="请选择表单"
            v-if="formConf.formType == 1 || formConf.formType == 2" />
          <template v-if="formConf.formType == 3">
            <interface-modal :value="formConf.formId" :title="formConf.formName" :allowClear="false" @change="onInterfaceChange" placeholder="请选择接口" />
            <div class="ant-form-item-label mt-12px"><label class="ant-form-item-no-colon">参数设置</label></div>
            <a-table :data-source="formConf.interfaceTemplateJson" :columns="templateJsonColumns" size="small" :pagination="false">
              <template #bodyCell="{ column, record }">
                <template v-if="column.key === 'field'"> {{ record.field }}{{ record.fieldName ? '(' + record.fieldName + ')' : '' }} </template>
                <template v-if="column.key === 'sourceType'">
                  <linzen-select
                    v-model:value="record.sourceType"
                    :options="interfaceSourceTypeOptions"
                    placeholder="请选择参数来源"
                    class="!w-84px"
                    @change="onSourceTypeChange($event, record)" />
                </template>
                <template v-if="column.key === 'relationField'">
                  <template v-if="record.sourceType === 2">
                    <linzen-input-number
                      v-if="['int', 'decimal'].includes(record.dataType)"
                      v-model:value="record.relationField"
                      placeholder="请输入参数值"
                      clearable />
                    <linzen-date-picker
                      v-else-if="record.dataType == 'datetime'"
                      class="!w-full"
                      v-model:value="record.relationField"
                      placeholder="请选择参数值"
                      format="YYYY-MM-DD HH:mm:ss"
                      clearable />
                    <a-input v-else v-model:value="record.relationField" placeholder="请输入参数值" clearable />
                  </template>
                </template>
              </template>
              <template #emptyText>
                <p class="leading-60px">暂无数据</p>
              </template>
            </a-table>
          </template>
        </a-form-item>
        <a-form-item class="!flex-nowrap" v-if="formConf.formType == 1 || formConf.formType == 2">
          <template #label>获取条件<BasicHelp text="满足以下条件，触发执行动作。" /></template>
          <ConditionMain ref="conditionMainRef" />
        </a-form-item>
      </a-form>
    </ScrollContainer>
  </section>
</template>
<script lang="ts" setup>
  import { ref } from 'vue';
  import { ScrollContainer } from '/@/components/Container';
  import FormListModal from './modal/FormListModal.vue';
  import { InterfaceModal } from '/@/components/CommonModal';
  import ConditionMain from '/@/components/ColumnDesign/src/components/ConditionMain.vue';

  defineOptions({ name: 'addDataNode', inheritAttrs: false });
  defineExpose({ initCondition, submitCondition });
  const props = defineProps(['formConf', 'integrateType', 'formFieldsOptions', 'getFormFieldList']);
  const conditionMainRef = ref();
  const interfaceSourceTypeOptions = [
    { id: 2, fullName: '自定义' },
    { id: 3, fullName: '为空' },
  ];
  const templateJsonColumns = [
    { width: 50, title: '序号', align: 'center', customRender: ({ index }) => index + 1 },
    { title: '参数名称', dataIndex: 'field', key: 'field', width: 120 },
    { title: '参数来源', dataIndex: 'sourceType', key: 'sourceType', width: 100 },
    { title: '参数值', dataIndex: 'relationField', key: 'relationField', width: 270 },
  ];

  function onFormTypeChange() {
    handleNull();
    initCondition();
  }
  function onFormIdChange(id, item) {
    if (!id) return onFormTypeChange();
    props.formConf.formName = item.fullName;
    props.formConf.formId = id;
    props.formConf.ruleList = [];
    props.getFormFieldList(id, 'getData');
  }
  function onInterfaceChange(id, item) {
    if (!id) return handleNull();
    if (props.formConf.formId === id) return;
    props.formConf.formName = item.fullName;
    props.formConf.formId = id;
    props.formConf.ruleList = [];
    const formFieldList = item.fieldJson ? JSON.parse(item.fieldJson) : [];
    props.formConf.formFieldList = formFieldList.map(o => ({
      ...o,
      id: o.defaultValue,
      fullName: o.field,
      label: o.field ? o.defaultValue + '(' + o.field + ')' : o.defaultValue,
    }));
    props.formConf.interfaceTemplateJson = (item.templateJson || []).map(o => ({ ...o, sourceType: 2, relationField: '' }));
  }
  function handleNull() {
    props.formConf.formName = '';
    props.formConf.formId = '';
    props.formConf.formFieldList = [];
    props.formConf.ruleList = [];
    props.formConf.interfaceTemplateJson = [];
  }
  function onSourceTypeChange(_val, row) {
    row.relationField = '';
  }
  function initCondition() {
    if (props.formConf.formType != 1 && props.formConf.formType != 2) return;
    conditionMainRef.value?.init({
      conditionList: props.formConf.ruleList || [],
      matchLogic: props.formConf.ruleMatchLogic,
      fieldOptions: props.formConf.formFieldList,
    });
  }
  function submitCondition() {
    const values = conditionMainRef.value?.confirm();
    if (!values) return false;
    return values;
  }
</script>
