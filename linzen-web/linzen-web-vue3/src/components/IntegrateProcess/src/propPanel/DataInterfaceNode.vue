<template>
  <section class="common-pane">
    <ScrollContainer class="config-content">
      <a-form :colon="false" layout="vertical" :model="formConf" class="config-form">
        <a-form-item label="执行数据接口">
          <interface-modal
            :value="formConf.formId"
            :title="formConf.formName"
            :flowType="1"
            :allowClear="false"
            @change="onFormIdChange"
            placeholder="请选择" />
          <div class="ant-form-item-label mt-12px"><label class="ant-form-item-no-colon">参数设置</label></div>
          <a-table :data-source="formConf.templateJson" :columns="templateJsonColumns" size="small" :pagination="false">
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'field'">
                <span class="required-sign">{{ record.required ? '*' : '' }}</span>
                {{ record.field }}{{ record.fieldName ? '(' + record.fieldName + ')' : '' }}
              </template>
              <template v-if="column.key === 'sourceType'">
                <linzen-select
                  v-model:value="record.sourceType"
                  :options="record.required ? noNullOptions : interfaceSourceTypeOptions"
                  placeholder="请选择参数来源"
                  class="!w-84px"
                  @change="onSourceTypeChange($event, record)" />
              </template>
              <template v-if="column.key === 'relationField'">
                <linzen-select
                  v-model:value="record.relationField"
                  placeholder="请选择表单字段"
                  :options="formFieldsOptions"
                  allowClear
                  showSearch
                  :fieldNames="{ label: 'label', options: 'options1' }"
                  optionLabelProp="label"
                  class="!w-254px"
                  @change="onRelationFieldChange($event, record)"
                  v-if="record.sourceType === 1" />
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
        </a-form-item>
      </a-form>
    </ScrollContainer>
  </section>
</template>
<script lang="ts" setup>
  import { ScrollContainer } from '/@/components/Container';
  import { InterfaceModal } from '/@/components/CommonModal';
  import { interfaceSourceTypeOptions } from '../helper/define';

  defineOptions({ name: 'dataInterfaceNode', inheritAttrs: false });
  const props = defineProps(['formConf', 'integrateType', 'formFieldsOptions', 'getFormFieldList']);

  const templateJsonColumns = [
    { width: 50, title: '序号', align: 'center', customRender: ({ index }) => index + 1 },
    { title: '参数名称', dataIndex: 'field', key: 'field', width: 120 },
    { title: '参数来源', dataIndex: 'sourceType', key: 'sourceType', width: 100 },
    { title: '参数值', dataIndex: 'relationField', key: 'relationField', width: 270 },
  ];
  const noNullOptions = interfaceSourceTypeOptions.filter(o => o.id != 3);

  function onFormIdChange(id, item) {
    if (!id) return handleNull();
    if (props.formConf.formId === id) return;
    props.formConf.formName = item.fullName;
    props.formConf.formId = id;
    props.formConf.templateJson = (item.templateJson || []).map(o => ({ ...o, sourceType: 1, relationField: '' }));
  }
  function handleNull() {
    props.formConf.formName = '';
    props.formConf.formId = '';
    props.formConf.templateJson = [];
  }
  function onRelationFieldChange(val, row) {
    if (!val) return;
    let list = props.formFieldsOptions.filter(o => o.id === val);
    if (!list.length) return;
    let item = list[0];
    row.isSubTable = item.__config__ && item.__config__.isSubTable ? item.__config__.isSubTable : false;
  }
  function onSourceTypeChange(_val, row) {
    row.relationField = '';
  }
</script>
