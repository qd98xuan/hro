<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="同步门户" @ok="handleSubmit" class="linzen-release-modal">
    <a-form :colon="false" :model="dataForm" :rules="rules" :labelCol="{ style: { width: '45px' } }" ref="formElRef">
      <div class="release-title">生成主页门户</div>
      <div class="release-main">
        <div class="item" :class="{ active: dataForm.pcPortal === 1 }" @click="selectToggle('pcPortal')">
          <i class="item-icon icon-linzen icon-linzen-pc"></i>
          <p class="item-title">同步Web端门户</p>
          <div class="icon-checked">
            <check-outlined />
          </div>
        </div>
        <div class="item" :class="{ active: dataForm.appPortal === 1 }" @click="selectToggle('appPortal')">
          <i class="item-icon icon-linzen icon-linzen-mobile"></i>
          <p class="item-title">同步APP端门户</p>
          <div class="icon-checked">
            <check-outlined />
          </div>
        </div>
      </div>
      <div class="release-form-main">
        <template v-if="!record.pcPortalIsRelease">
          <a-form-item label="应用" name="pcPortalSystemId" v-if="dataForm.pcPortal">
            <LinzenSelect v-model:value="dataForm.pcPortalSystemId" :options="treeData" multiple placeholder="选择应用" :allowClear="false" />
          </a-form-item>
        </template>
        <template v-if="!record.appPortalIsRelease">
          <a-form-item v-if="(!dataForm.pcPortal || record.pcPortalIsRelease) && dataForm.appPortal"></a-form-item>
          <a-form-item label="应用" name="appPortalSystemId" v-if="dataForm.appPortal">
            <LinzenSelect v-model:value="dataForm.appPortalSystemId" :options="treeData" multiple placeholder="选择应用" :allowClear="false" />
          </a-form-item>
        </template>
      </div>
      <div class="release-title">生成应用菜单</div>
      <div class="release-main">
        <div class="item" :class="{ active: dataForm.pc === 1 }" @click="selectToggle('pc')">
          <i class="item-icon icon-linzen icon-linzen-pc"></i>
          <p class="item-title">同步Web端菜单</p>
          <div class="icon-checked">
            <check-outlined />
          </div>
        </div>
        <div class="item" :class="{ active: dataForm.app === 1 }" @click="selectToggle('app')">
          <i class="item-icon icon-linzen icon-linzen-mobile"></i>
          <p class="item-title">同步APP端菜单</p>
          <div class="icon-checked">
            <check-outlined />
          </div>
        </div>
      </div>
      <div class="release-form-main">
        <template v-if="!record.pcIsRelease">
          <a-form-item label="上级" name="pcModuleParentId" v-if="dataForm.pc">
            <LinzenTreeSelect
              v-model:value="dataForm.pcModuleParentId"
              :options="menuTreeData"
              placeholder="选择上级菜单"
              :allowClear="false"
              :dropdownMatchSelectWidth="false"
              @change="onPcChange" />
          </a-form-item>
        </template>
        <template v-if="!record.appIsRelease">
          <a-form-item v-if="(!dataForm.pc || record.pcIsRelease) && dataForm.app"></a-form-item>
          <a-form-item label="上级" name="appModuleParentId" v-if="dataForm.app">
            <LinzenTreeSelect
              v-model:value="dataForm.appModuleParentId"
              :options="appMenuTreeData"
              placeholder="选择上级菜单"
              :allowClear="false"
              :dropdownMatchSelectWidth="false"
              @change="onAppChange" />
          </a-form-item>
        </template>
      </div>
    </a-form>
  </BasicModal>
</template>
<script lang="ts" setup>
  import { release } from '/@/api/onlineDev/portal';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { ref, reactive, toRefs, nextTick } from 'vue';
  import type { FormInstance } from 'ant-design-vue';
  import { CheckOutlined } from '@ant-design/icons-vue';
  import { getSystemList } from '/@/api/system/system';
  import { getMenuSelector } from '/@/api/system/menu';
  import { useMessage } from '/@/hooks/web/useMessage';

  interface State {
    dataForm: any;
    record: any;
    rules: any;
    treeData: any[];
    menuTreeData: any[];
    appMenuTreeData: any[];
  }

  const emit = defineEmits(['register', 'reload']);
  const { createMessage, createConfirm } = useMessage();
  const [registerModal, { changeOkLoading, closeModal }] = useModalInner(init);
  const formElRef = ref<FormInstance>();
  const state = reactive<State>({
    dataForm: {
      pc: 1,
      app: 1,
      pcModuleParentId: '',
      appModuleParentId: '',
      pcSystemId: '',
      appSystemId: '',
      pcPortal: 1,
      appPortal: 1,
      pcPortalSystemId: [],
      appPortalSystemId: [],
    },
    record: {},
    rules: {
      pcPortalSystemId: [{ required: true, message: '应用不能为空', trigger: 'change' }],
      appPortalSystemId: [{ required: true, message: '应用不能为空', trigger: 'change' }],
      pcModuleParentId: [{ required: true, message: '上级菜单不能为空', trigger: 'change' }],
      appModuleParentId: [{ required: true, message: '上级菜单不能为空', trigger: 'change' }],
    },
    treeData: [],
    menuTreeData: [],
    appMenuTreeData: [],
  });
  const { dataForm, record, rules, treeData, menuTreeData, appMenuTreeData } = toRefs(state);

  function init(data) {
    state.record = data;
    state.dataForm = {
      pc: !data.pcIsRelease && !data.appIsRelease && !data.pcPortalIsRelease && !data.appPortalIsRelease ? 1 : data.pcIsRelease,
      app: !data.pcIsRelease && !data.appIsRelease && !data.pcPortalIsRelease && !data.appPortalIsRelease ? 1 : data.appIsRelease,
      pcModuleParentId: '',
      appModuleParentId: '',
      pcSystemId: '',
      appSystemId: '',
      pcPortal: !data.pcIsRelease && !data.appIsRelease && !data.pcPortalIsRelease && !data.appPortalIsRelease ? 1 : data.pcPortalIsRelease,
      appPortal: !data.pcIsRelease && !data.appIsRelease && !data.pcPortalIsRelease && !data.appPortalIsRelease ? 1 : data.appPortalIsRelease,
      pcPortalSystemId: [],
      appPortalSystemId: [],
    };
    getSystemOptions();
    getMenuOptions();
    getAppMenuOptions();
    nextTick(() => {
      formElRef.value?.clearValidate();
    });
  }
  function getSystemOptions() {
    getSystemList({ enableMark: 1, selector: true }).then(res => {
      state.treeData = res.data.list || [];
    });
  }
  function selectToggle(key) {
    state.dataForm[key] = state.dataForm[key] === 1 ? 0 : 1;
  }
  async function handleSubmit() {
    try {
      if (!state.dataForm.pc && !state.dataForm.app && !state.dataForm.pcPortal && !state.dataForm.appPortal)
        return createMessage.error('请至少选择一个同步方式');
      const values = await formElRef.value?.validate();
      if (!values) return;
      const pcPortalSystemId = state.dataForm.pcPortalSystemId.toString();
      const appPortalSystemId = state.dataForm.appPortalSystemId.toString();
      const query = {
        ...state.dataForm,
        pcPortalSystemId,
        appPortalSystemId,
      };
      createConfirm({
        iconType: 'warning',
        title: '提示',
        content: '发布确定后会覆盖当前线上版本且进行门户同步，是否继续?',
        onOk: () => {
          changeOkLoading(true);
          release(state.record.id, query)
            .then(res => {
              changeOkLoading(false);
              createMessage.success(res.msg);
              emit('reload');
              closeModal();
            })
            .catch(() => {
              changeOkLoading(false);
            });
        },
      });
    } catch (_) {}
  }
  function getMenuOptions() {
    getMenuSelector({ category: 'Web' }, 0).then(res => {
      state.menuTreeData = res.data.list || [];
    });
  }
  function getAppMenuOptions() {
    getMenuSelector({ category: 'App' }, 0).then(res => {
      state.appMenuTreeData = res.data.list || [];
      for (let index = 0; index < state.appMenuTreeData.length; index++) {
        const item = state.appMenuTreeData[index];
        if (item.type == 0) item.disabled = true;
      }
    });
  }
  function onPcChange(_id, data) {
    state.dataForm.pcSystemId = data.systemId;
  }
  function onAppChange(_id, data) {
    state.dataForm.appSystemId = data.systemId;
  }
</script>
<style lang="less" scoped>
  .release-title {
    font-size: 16px;
    padding-top: 10px;
    font-weight: bold;
  }

  .item {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 80px !important;
    padding-top: 0 !important;

    .item-icon {
      width: 28px !important;
      height: 28px !important;
      font-size: 16px !important;
      margin: 0 8px 0 0 !important;
      line-height: 26px !important;
      border-width: 1px !important;
    }
  }
</style>
