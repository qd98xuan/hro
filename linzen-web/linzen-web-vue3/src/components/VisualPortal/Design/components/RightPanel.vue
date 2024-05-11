<template>
  <div class="right-box">
    <a-tabs v-model:activeKey="activeKey" :tabBarGutter="10" class="average-tabs">
      <a-tab-pane :key="1" tab="控件属性"></a-tab-pane>
      <a-tab-pane :key="2" tab="门户属性"></a-tab-pane>
    </a-tabs>
    <div class="filed-box">
      <ScrollContainer class="p-15px !pt-0px">
        <a-form :colon="false" labelAlign="left" :labelCol="{ style: { width: '86px' } }" v-if="activeKey === 1 && activeData && activeData.projectKey">
          <a-collapse v-model:activeKey="activeName" accordion ghost expandIconPosition="right">
            <Card
              :key="1"
              :activeData="activeData"
              :showType="showType"
              :menuList="menuList"
              :appMenuList="appMenuList"
              v-if="activeData.projectKey != 'tab' && activeData.projectKey != 'collapse'" />
            <Common
              :key="2"
              :activeData="activeData"
              :showType="showType"
              :menuList="menuList"
              :appMenuList="appMenuList"
              v-if="!noNeedTypeSetList.includes(activeData.projectKey)" />
            <template v-if="chartList.includes(activeData.projectKey) || activeData.projectKey == 'mapChart'">
              <MainTitle :key="3" :activeData="activeData" :showType="showType" />
              <SubTitle :key="4" :activeData="activeData" :showType="showType" />
              <BarStyle :key="5" :activeData="activeData" :showType="showType" v-if="activeData.projectKey == 'barChart'" />
            </template>
            <XAxis :key="6" :activeData="activeData" :showType="showType" v-if="activeData.projectKey == 'barChart' || activeData.projectKey == 'lineChart'" />
            <YAxis :key="7" :activeData="activeData" :showType="showType" v-if="activeData.projectKey == 'barChart' || activeData.projectKey == 'lineChart'" />
            <Label :key="8" :activeData="activeData" :showType="showType" v-if="chartList.includes(activeData.projectKey)" />
            <template v-if="chartList.includes(activeData.projectKey) || activeData.projectKey == 'mapChart'">
              <Tooltip :key="9" :activeData="activeData" :showType="showType" />
              <Margin :key="10" :activeData="activeData" :showType="showType" v-if="showType == 'pc'" />
            </template>
            <template v-if="chartList.includes(activeData.projectKey)">
              <Legend :key="11" :activeData="activeData" :showType="showType" />
              <Color :key="12" :activeData="activeData" :showType="showType" />
            </template>
            <Data :key="13" :activeData="activeData" :showType="showType" v-if="needDataSetList.includes(activeData.projectKey)" />
            <TableSet :key="14" :activeData="activeData" :showType="showType" v-if="activeData.projectKey == 'tableList'" />
            <TabSet :key="15" :activeData="activeData" :showType="showType" v-if="activeData.projectKey == 'tab'" />
            <ScheduleSet :key="16" :activeData="activeData" :showType="showType" v-if="activeData.projectKey == 'schedule'" />
            <NoticeSet :key="17" :activeData="activeData" :showType="showType" v-if="activeData.projectKey == 'notice'" />
          </a-collapse>
          <a-form-item label="多端显示" class="!mt-10px">
            <linzen-checkbox v-model:value="activeData.visibility" :options="getVisibilityOptions" />
          </a-form-item>
        </a-form>
        <a-form :colon="false" labelAlign="left" :labelCol="{ style: { width: '86px' } }" v-if="activeKey === 2">
          <Refresh class="pt-15px" :refresh="refresh" />
        </a-form>
      </ScrollContainer>
    </div>
  </div>
</template>
<script lang="ts" setup>
  import { reactive, toRefs, onMounted, computed } from 'vue';
  import { ScrollContainer } from '/@/components/Container';
  import { getSelectorAll } from '/@/api/system/menu';
  import { chartList, needDataSetList, systemList, layoutList } from '../helper/dataMap';
  import Refresh from '../rightComponents/RRefresh.vue';
  import Card from '../rightComponents/RCard.vue';
  import Common from '../rightComponents/RCommon.vue';
  import Data from '../rightComponents/RData.vue';
  import MainTitle from '../rightComponents/RMainTitle.vue';
  import SubTitle from '../rightComponents/RSubTitle.vue';
  import BarStyle from '../rightComponents/RBarStyle.vue';
  import XAxis from '../rightComponents/RXAxis.vue';
  import YAxis from '../rightComponents/RYAxis.vue';
  import Label from '../rightComponents/RLabel.vue';
  import Tooltip from '../rightComponents/RTooltip.vue';
  import Margin from '../rightComponents/RMargin.vue';
  import Legend from '../rightComponents/RLegend.vue';
  import Color from '../rightComponents/RColor.vue';
  import TableSet from '../rightComponents/RTableSet.vue';
  import TabSet from '../rightComponents/RTabSet.vue';
  import ScheduleSet from '../rightComponents/RScheduleSet.vue';
  import NoticeSet from '../rightComponents/RNoticeSet.vue';

  interface State {
    activeKey: number;
    activeName: number;
    menuList: any[];
    appMenuList: any[];
  }

  const state = reactive<State>({
    activeKey: 1,
    activeName: 0,
    menuList: [],
    appMenuList: [],
  });
  const { activeKey, activeName, menuList, appMenuList } = toRefs(state);
  const props = defineProps(['activeData', 'showType', 'refresh']);
  const noNeedTypeSetList = [...layoutList, ...systemList, 'schedule', 'tableList'];

  const getVisibilityOptions = computed(() => {
    return [
      { id: 'pc', fullName: 'Web' },
      { id: 'app', fullName: 'App', disabled: props.activeData?.projectKey === 'iframe' },
    ];
  });

  function init() {
    getSelectorAll({ category: 'Web' }).then(res => {
      state.menuList = (res.data.list || []).filter(o => o.children && o.children.length);
    });
    getSelectorAll({ category: 'App' }).then(res => {
      state.appMenuList = (res.data.list || []).filter(o => o.children && o.children.length);
    });
  }

  onMounted(() => init());
</script>
