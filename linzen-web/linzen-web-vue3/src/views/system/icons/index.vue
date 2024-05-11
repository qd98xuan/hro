<template>
  <div class="linzen-content-wrapper icons-wrapper">
    <div class="linzen-content-wrapper-center">
      <div class="linzen-content-wrapper-search-box">
        <BasicForm class="search-form" @register="registerForm" @submit="handleSubmit" @reset="handleReset"></BasicForm>
      </div>
      <div class="linzen-content-wrapper-content bg-white">
        <a-tabs v-model:activeKey="activeKey" type="card" class="linzen-content-wrapper-tabs">
          <a-tab-pane key="1" tab="常用图标">
            <ScrollContainer>
              <a-row>
                <a-col :span="6" v-for="item in linzenIconList" :key="item" @click="handleCopy('icon-linzen ' + item)" :title="item" class="icon-item">
                  <i :class="'icon-linzen ' + item" />
                  <span>{{ item }}</span>
                </a-col>
              </a-row>
            </ScrollContainer>
          </a-tab-pane>
          <a-tab-pane key="2" tab="更多图标">
            <ScrollContainer>
              <a-row>
                <a-col :span="6" v-for="item in linzenCustomList" :key="item" @click="handleCopy('linzen-custom ' + item)" :title="item" class="icon-item">
                  <i :class="'linzen-custom ' + item" />
                  <span>{{ item }}</span>
                </a-col>
              </a-row>
            </ScrollContainer>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
  import { reactive, toRefs, watch, unref, onMounted } from 'vue';
  import { BasicForm, useForm } from '/@/components/Form';
  import { linzenIconJson } from '/@/components/Linzen/IconPicker/data/linzenIcon';
  import { linzenCustomJson } from '/@/components/Linzen/IconPicker/data/linzenCustom';
  import { ScrollContainer } from '/@/components/Container';

  import { useMessage } from '/@/hooks/web/useMessage';
  import { useCopyToClipboard } from '/@/hooks/web/useCopyToClipboard';
  import { cloneDeep } from 'lodash-es';

  const linzenIcon = linzenIconJson.glyphs.map(o => `icon-linzen-${o.font_class}`);
  const linzenCustom = linzenCustomJson.glyphs.map(o => `linzen-custom-${o.font_class}`);

  const { createMessage } = useMessage();
  const [registerForm, { resetFields }] = useForm({
    baseColProps: { span: 6 },
    showActionButtonGroup: true,
    showAdvancedButton: true,
    compact: true,
    schemas: [
      {
        field: 'keyword',
        label: '关键词',
        component: 'Input',
        componentProps: {
          placeholder: '请输入关键词',
          submitOnPressEnter: true,
        },
      },
    ],
  });
  const state = reactive({
    activeKey: '1',
    keyword: '',
    linzenIcon,
    linzenCustom,
    linzenIconList: [],
    linzenCustomList: [],
  });
  const { activeKey, linzenIconList, linzenCustomList } = toRefs(state);

  watch(
    () => state.activeKey,
    () => {
      resetFields();
    },
  );

  function handleSubmit(values) {

    state.keyword = values?.keyword || '';
    handleSearch();
  }

  function handleReset() {
    state.keyword = '';
    handleSearch();
  }
  function handleSearch() {
    const key = state.activeKey === '1' ? 'linzenIcon' : 'linzenCustom';
    if (state.keyword) {
      state[key + 'List'] = state[key].filter(o => o.indexOf(state.keyword) > -1);
    } else {
      state[key + 'List'] = cloneDeep(state[key]);
    }
  }
  function handleCopy(item) {
    const { isSuccessRef } = useCopyToClipboard(item);
    unref(isSuccessRef) && createMessage.success('复制成功');
  }
  onMounted(() => {
    handleReset();
  });
</script>
<style lang="less" scoped>
  .icons-wrapper {
    .icon-item {
      padding: 0 10px;
      height: 40px;
      line-height: 38px;
      border: 1px dashed transparent;
      color: #6b7a99;
      cursor: pointer;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      i {
        font-size: 16px;
        line-height: 40px;
        margin-right: 14px;
        vertical-align: top;
      }

      span {
        line-height: 40px;
        vertical-align: top;
      }

      &:hover {
        border-color: @primary-color;

        i {
          font-size: 30px;
        }
      }
    }
  }
</style>
