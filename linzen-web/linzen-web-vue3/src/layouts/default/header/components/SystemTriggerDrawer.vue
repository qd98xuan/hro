<template>
  <BasicDrawer v-bind="$attrs" @register="registerDrawer" width="280px" class="full-drawer portal-toggle-drawer" title="切换应用">
    <a-input-search :placeholder="t('common.drawerSearchText')" allowClear v-model:value="keyword" />
    <div :class="classNames">
      <div v-for="(item, i) in getSysList" :key="i" :class="['trigger-way-item', item.currentSystem ? 'active' : '']" @click="selectItem(item)">
        <div class="way-item-title">
          <p style="font-size: 18px; font-weight: bolder; color: rgb(48, 49, 51);">{{ item.name }}</p>
          <span>{{ item.description }}</span>
        </div>
        <div class="way-item-image">
          <img width="40" v-if="item.workLogoIcon" :src="apiUrl + item.workLogoIcon"/>
          <img width="40" v-if="!item.workLogoIcon" :src="getSystemImage('/defaultSystem.png')"/>
        </div>
      </div>
    </div>
  </BasicDrawer>
</template>
<script lang="ts" setup>
  import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { reactive, toRefs, computed } from 'vue';
  import { useI18n } from '/@/hooks/web/useI18n';
  import { setMajor } from '/@/api/permission/userSetting';
  import { useRouter } from 'vue-router';
  import { getSystemImage } from '/@/utils/comm';
  import { useGlobSetting } from '/@/hooks/setting';
  const globSetting = useGlobSetting();
  const apiUrl = globSetting.apiUrl;

  interface State {
    list: any[];
    keyword: string;
  }

  const state = reactive<State>({
    list: [],
    keyword: '',
  });
  const { keyword } = toRefs(state);
  defineEmits(['register']);
  const { createMessage } = useMessage();
  const { t } = useI18n();
  const [registerDrawer, { changeLoading, closeDrawer }] = useDrawerInner(init);
  const router = useRouter();

  const getSysList = computed(() => (state.keyword ? state.list.filter(o => o.name.indexOf(state.keyword) !== -1) : state.list));

  function init(data) {
    state.keyword = '';
    state.list = data.list || [];
  }
  function selectItem(item) {
    if (item.currentSystem) return;
    changeLoading(true);
    let query = { majorId: item.id, majorType: 'System' };
    setMajor(query)
      .then(res => {
        createMessage.success(res.msg).then(() => {
          router.replace('/');
          setTimeout(() => {
            changeLoading(false);
            closeDrawer();
            location.reload();
          }, 50);
        });
      })
      .catch(() => {
        changeLoading(false);
      });
  }

  const classNames = computed(() => {
    return {
      'scene-trigger-way-warp': true,
      disabled: false,
    };
  });
</script>

<style scoped lang="less">
  .scene-trigger-way-warp {
    display: flex;
    flex-wrap: wrap;
    gap: 5px 5px;
    width: 100%;
    padding-top: 8px;

    .trigger-way-item {
      display: flex;
      justify-content: space-between;
      padding: 16px;
      border: 2px solid #e0e4e8;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.3s;
      width: 270px;
      margin: 0px 8px 0px 8px;

      &:hover {
        border: 2px solid rgba(0, 140, 255, 0.651);
      }

      .way-item-title {
        width: 200px;

        p {
          margin-bottom: 8px;
          font-weight: bold;
          font-size: 14px;
        }

        span {
          color: rgba(#000, 0.35);
          font-size: 12px;
        }
      }

      .way-item-image {
        display: flex;
        align-items: center;
        height: 100%;
        margin: 0 !important;
        opacity: 0.5;
      }

      &:hover {
        color: @primary-color-hover;
        .way-item-image {
          opacity: 0.8;
        }
      }

      &.active {
        border-color: @primary-color-active;
        .way-item-image {
          opacity: 1;
        }
      }
    }

    &.disabled {
      color: rgba(#000, 0.8);
      .way-item-image {
        opacity: 0.6;
      }

      .trigger-way-item {
        cursor: not-allowed;
      }
    }
  }
</style>
