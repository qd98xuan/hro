<template>
  <BasicModal v-bind="$attrs" width="1000px" class="LINZEN-complex-header-Modal" @register="registerModal" title="复杂表头配置" @ok="handleSubmit" destroyOnClose>
    <a-table size="small" rowKey="id" class="complex-header-table" :data-source="list" :columns="columns" :pagination="false">
      <template #bodyCell="{ column, record, index }">
        <template v-if="column.key === 'drag'">
          <i class="drag-handler icon-linzen icon-linzen-darg" title="点击拖动" />
        </template>
        <template v-if="column.key === 'fullName'">
          <a-input v-model:value="record.fullName" placeholder="请输入" allowClear :maxlength="50" />
        </template>
        <template v-if="column.key === 'childColumns'">
          <LinzenSelect
            v-model:value="record.childColumns"
            :fieldNames="{ options: 'options1' }"
            placeholder="请选择"
            multiple
            showSearch
            allowClear
            :options="getChildColumnsList(index)" />
        </template>
        <template v-if="column.key === 'align'">
          <LinzenSelect v-model:value="record.align" placeholder="请选择" :options="alignList" />
        </template>
        <template v-if="column.key === 'action'">
          <a-button class="action-btn" type="link" color="error" @click="handleDel(index)" size="small">删除</a-button>
        </template>
      </template>
    </a-table>
    <div class="table-add-action" @click="handleAdd()">
      <a-button type="link" preIcon="icon-linzen icon-linzen-btn-add">添加</a-button>
    </div>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { nextTick, reactive, toRefs } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import Sortablejs from 'sortablejs';
  import { buildBitUUID } from '/@/utils/uuid';
  import { cloneDeep } from 'lodash-es';

  interface State {
    list: any[];
    childColumnsList: any[];
  }

  const state = reactive<State>({
    list: [],
    childColumnsList: [],
  });
  const { list } = toRefs(state);
  const alignList = [
    { fullName: '左对齐', id: 'left' },
    { fullName: '居中对齐', id: 'center' },
    { fullName: '右对齐', id: 'right' },
  ];
  const emit = defineEmits(['register', 'confirm']);
  const [registerModal, { closeModal }] = useModalInner(init);
  const columns = [
    { title: '拖动', dataIndex: 'drag', key: 'drag', align: 'center', width: 50 },
    { title: '表头列名', dataIndex: 'fullName', key: 'fullName', width: 200 },
    { title: '子列', dataIndex: 'childColumns', key: 'childColumns', width: 280 },
    { title: '对齐方式', dataIndex: 'align', key: 'align', width: 150 },
    { title: '操作', dataIndex: 'action', key: 'action', width: 50 },
  ];

  function init(data) {
    state.list = cloneDeep(data.list);
    state.childColumnsList = cloneDeep(data.columnOptions).map(o => ({ ...o, disabled: false }));
    nextTick(() => initSort());
  }
  function initSort() {
    const searchTable: any = document.querySelector(`.complex-header-table .ant-table-tbody`);
    Sortablejs.create(searchTable, {
      handle: '.drag-handler',
      animation: 150,
      easing: 'cubic-bezier(1, 0, 0, 1)',
      onStart: () => {},
      onEnd: ({ newIndex, oldIndex }: any) => {
        const currRow = state.list.splice(oldIndex, 1)[0];
        state.list.splice(newIndex, 0, currRow);
      },
    });
  }
  function handleAdd() {
    const id = 'complex' + buildBitUUID();
    const item = { fullName: '表头列名' + id, childColumns: [], align: 'center', id };
    state.list.push(item);
  }
  function handleDel(index) {
    state.list.splice(index, 1);
  }
  function getChildColumnsList(index) {
    let options: any[] = [];
    for (let i = 0; i < state.list.length; i++) {
      const e = state.list[i];
      if (e.childColumns?.length && index !== i) options.push(...e.childColumns);
    }
    let list: any[] = state.childColumnsList.filter(o => !options.includes(o.id));
    if (list.length) list = list.map(o => ({ ...o, align: 'left' }));
    return list;
  }
  function handleSubmit() {
    emit('confirm', state.list);
    nextTick(() => closeModal());
  }
</script>
<style lang="less">
  .LINZEN-complex-header-Modal {
    .ant-modal-body {
      height: 60vh;

      & > .scrollbar {
        padding: 0;

        .scrollbar__view {
          .table-add-action {
            margin-bottom: 10px;
          }
        }
      }
    }
  }
</style>
