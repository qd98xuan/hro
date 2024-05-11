<template>
  <div>
    <a-table :data-source="fieldList" :columns="getColumns" size="small" :pagination="false" rowKey="index"
             :scroll="{x:'100%', y:'550px'}"
             class="drag-table">
      <template #bodyCell="{ column, record, index }">

        <template v-if="column.key === 'drag'">
          <i class="drag-handler icon-linzen icon-linzen-darg" title="点击拖动" />
        </template>
        <template v-if="column.key === 'fieldName'">
          <a-input v-model:value="record.fieldName" placeholder="请输入字段名称" :maxlength="50"
                   :disabled="record.isDisabled" />
        </template>

        <template v-if="column.key === 'textLength'">
          <a-input-number
              v-model:value="record.textLength"
              placeholder="请输入长度"
              :disabled="record.isDisabled || record.fieldType !== '2' && record.fieldType !== '4' && record.fieldType !== '5'" />
        </template>

        <template v-if="column.key === 'fieldType'">
          <a-input-group compact>
            <linzen-select v-model:value="record.fieldType" placeholder="请选择" :options="options"
                           style="width: 150px;" :disabled="record.isDisabled">
            </linzen-select>
            <a-button preIcon="icon-linzen icon-linzen-edit" style="float:right" v-if="record.fieldType == '1'"
                      @click="onChange(index, record)" :disabled="record.isDisabled"></a-button>
          </a-input-group>
        </template>

        <template v-if="column.key === 'isNecessary'">
          <a-checkbox v-model:checked="record.isNecessary" />
        </template>

        <template v-if="column.key === 'isOpen'">
          <a-checkbox v-model:checked="record.isOpen" />
        </template>
        <template v-if="column.key === 'description'">
          <a-input v-model:value="record.description" placeholder="请输入说明" :maxlength="50"
                   :disabled="record.isDisabled" />
        </template>
        <template v-if="column.key === 'action'">
          <a-button class="action-btn" type="link" @click="handleDelItem(index)" size="small"
                    v-if="!record.isDisabled">删除
          </a-button>
        </template>
      </template>
    </a-table>
    <div class="table-add-action">
      <a-button type="link" v-if="state.moduleCode == 'basic'" preIcon="icon-linzen icon-linzen-btn-add"
                @click="handleAdd()">新增一行
      </a-button>
      <a-button type="link" v-if="state.moduleCode == 'contact'" preIcon="icon-linzen icon-linzen-btn-add"
                @click="handleAdd()">新增一行
      </a-button>
      <a-button type="primary" preIcon="icon-linzen icon-linzen-btn-add" @click="handleSubmit()">保存数据</a-button>
    </div>
    <ExtraConfigModal @register="registerExtraConfigModal" @confirm="updateSearchRow" />
  </div>
</template>
<script lang="ts" setup>
import { ref, unref, computed, reactive, onMounted, toRefs } from "vue";
import { useMessage } from "/@/hooks/web/useMessage";
import Sortablejs from "sortablejs";

import { buildBitUUID } from "/@/utils/uuid";
import ExtraConfigModal from "./ExtraConfigModal.vue";
import { useModal } from "/@/components/Modal";
import { saveOrUpdateSetting } from "/@/views/Staff/Edit/api";

interface State {
  formData: any;
  moduleCode: string;
  moduleItem: any;
}

const state = reactive<State>({
  formData: {},
  moduleCode: "",
  moduleItem: {}
});
const { formData, moduleCode, moduleItem } = toRefs(state);

const props = defineProps({
  formData: {
    type: Object,
    default: {}
  },
  moduleCode: {
    type: String,
    default: ""
  },
  moduleItem: {
    type: Object,
    default: {}
  }
});

state.moduleCode = computed(() => props.moduleCode);

state.formData = computed(() => ({
  ...props.formData
}));

const [registerExtraConfigModal, { openModal: openExtraConfigModal, closeModal: closeExtraConfigModal }] = useModal();

function openExtraConfig(record, index) {
  record.rowNum = index;
  openExtraConfigModal(true, { ...record });
}

function updateSearchRow(data) {
  fieldList.value[data.rowNum] = data;
  console.info("fieldList", fieldList);
}

const emit = defineEmits(["register", "reload"]);
const { createMessage } = useMessage();

const options = [
  { fullName: "下拉选择器", id: "1" },
  { fullName: "单行输入框", id: "2" },
  { fullName: "日期选择器", id: "3" },
  { fullName: "多行输入框", id: "4" },
  { fullName: "数字输入框", id: "5" },
  { fullName: "地区选择器", id: "6" }
];
const dataForm = reactive({
  value: {
    tableName: "",
    newTable: ""
  }
});

const hasTableData = ref(false);
const fieldList = ref<any[]>([]);

const getColumns = computed(() => {
  let list: any[] = [
    { title: "拖动", dataIndex: "drag", key: "drag", align: "center", width: 50, rowDrag: true },
    {
      width: 50,
      title: "序号",
      align: "center",
      customRender: ({ index }) => index + 1
    },
    { title: "字段名称", dataIndex: "fieldName", key: "fieldName", width: 300 },
    { title: "字段类型", dataIndex: "fieldType", key: "fieldType", width: 200 },
    { title: "长度", dataIndex: "textLength", key: "textLength", width: 200 },
    { title: "是否必填", dataIndex: "isNecessary", key: "isNecessary", align: "center", width: 80 },
    { title: "是否开启", dataIndex: "isOpen", key: "isOpen", align: "center", width: 80 },
    { title: "说明", dataIndex: "description", key: "description" },
    { title: "操作", dataIndex: "action", key: "action", width: 50 }
  ];
  return list;
});

defineExpose({ init });

function init() {

  console.info("init", state.formData);
  fieldList.value = [];

  state.moduleItem = state.formData;

  console.info("moduleItem", state.moduleItem);
  if (state.moduleItem) {
    if (state.moduleItem.fieldList) {
      state.moduleItem.fieldList = state.moduleItem.fieldList.map(item => ({
        ...item,
        isDisabled: true,
        entityFlag: "fieldList",
        isNecessary: "true" == item.isNecessary,
        isOpen: "true" == item.isOpen,
        fieldType: item.fieldType ? item.fieldType + "" : item.fieldType
      }));
      fieldList.value.push(...state.moduleItem.fieldList);
    }

    if (state.moduleItem.additionalFieldList) {
      state.moduleItem.additionalFieldList = state.moduleItem.additionalFieldList.map(item => ({
        ...item,
        isDisabled: false,
        entityFlag: "additionalFieldList",
        isNecessary: "true" == item.isNecessary,
        isOpen: "true" == item.isOpen,
        fieldType: item.fieldType ? item.fieldType + "" : item.fieldType
      }));
      fieldList.value.push(...state.moduleItem.additionalFieldList);
    }
  }

  initSort();
}


const setNodeSort = (data: any, oldIndex: any, newIndex: any) => {
  const currRow = data.splice(oldIndex, 1)[0];
  currRow && data.splice(newIndex, 0, currRow);
};

function handleDelItem(index) {
  fieldList.value.splice(index, 1);
}

function handleAdd(row: undefined | Recordable = undefined) {
  const index = buildBitUUID();
  let item = {
    canSee: null,
    companyId: null,
    description: null,
    dropDownArray: null,
    empAdditionalFieldsVO: null,
    fieldCode: null,
    fieldName: null,
    fieldType: "2",
    gmtCreate: null,
    gmtModified: null,
    id: null,
    ignore: null,
    isDisabled: null,
    isEdit: null,
    isNecessary: null,
    isOpen: null,
    moduleCode: state.moduleItem.moduleCode,
    moduleName: state.moduleItem.moduleName,
    moduleType: null,
    options: null,
    sort: null,
    sourceId: null,
    templateId: null,
    textLength: null,
    entityFlag: "additionalFieldList",
    index
  };

  let options = fieldList.value;
  for (var i = 0; i < options.length; i++) {
    console.info(options[i]);
    if (!options[i].fieldName) {
      createMessage.info("字段名称不能为空，请输入或者删除");
      return;
    }
  }
  fieldList.value.push(item);
}


function exist() {
  let isOk = true;
  for (let i = 0; i < fieldList.value.length; i++) {
    const e = fieldList.value[i];
    if (!e.fieldName) {
      createMessage.error(`第${i + 1}行列 字段名称 不能为空`);
      isOk = false;
      break;
    }
    let num = fieldList.value.filter(o => o.fieldName == e.fieldName);
    if (num.length > 1) {
      createMessage.error(`第${i + 1}行 字段名称 '${e.fieldName}'已重复`);
      isOk = false;
      break;
    }
    if (!e.textLength) {
      createMessage.error(`第${i + 1}行列 字段长度 不能为空`);
      isOk = false;
      break;
    }
  }
  return isOk;
}

async function handleSubmit() {

  if (!fieldList.value.length) return createMessage.error("请至少添加一个字段");
  if (!exist()) return;

  // 设置最后的排序号
  let options = fieldList.value;
  for (var i = 0; i < options.length; i++) {
    console.info(options[i]);
    options[i].sort = i + 1;
  }

  saveOrUpdateSetting({ content: JSON.stringify(fieldList.value) }).then(res => {
    console.info(res);
    createMessage.success(res.msg);
  }).catch(() => {
  });
}

function initSort() {
  const table: any = document.querySelector(`.drag-table .ant-table-tbody`);
  console.info("initSort", document.querySelector(`.drag-table`), document.querySelector(`.ant-table-tbody`));
  Sortablejs.create(table, {
    handle: ".drag-handler",
    animation: 150,
    easing: "cubic-bezier(1, 0, 0, 1)",
    onStart: () => {
      console.info("onStart", fieldList.value);
    },
    // 结束拖动事件
    onEnd: ({ newIndex, oldIndex }: any) => {
      console.info({ newIndex, oldIndex });
      // 这里是行号
      setNodeSort(fieldList.value, oldIndex - 1, newIndex - 1);
    }
  });
}

function onChange(index, record) {
  console.info(index, record);
  if ("1" == record.fieldType) {
    openExtraConfig(record, index);
  }
}

onMounted(() => {
  init();
});

</script>
<style lang="less" scoped>
.caption {
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 10px;

  .title {
    line-height: 24px;
    font-size: 16px;
  }
}


:deep(.ant-table-wrapper .ant-table-header) {
  position: sticky;
  top: 0;
  z-index: 3;
}

:deep(.ant-table-wrapper .ant-table-sticky-scroll) {
  position: sticky;
  bottom: 0;
  z-index: 3;
}

</style>
