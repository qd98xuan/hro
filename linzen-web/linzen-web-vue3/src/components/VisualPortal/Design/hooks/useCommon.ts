import CardHeader from '../../Portal/CardHeader/index.vue';
import webLink from '../../Portal/Link/index.vue';
import { getDataInterfaceRes } from '/@/api/systemData/dataInterface';
import { computed, onMounted, reactive, toRefs, unref, watch } from 'vue';
import { useGlobSetting } from '/@/hooks/setting';

export function useCommon(activeData) {
  interface State {
    firstData: any;
    secondData: any;
    thirdData: any;
    otherDataList: any[];
    value: string;
    list: any[];
    timeList: any[];
  }

  const state = reactive<State>({
    firstData: {},
    secondData: {},
    thirdData: {},
    otherDataList: [],
    value: '',
    list: [],
    timeList: [],
  });
  const { firstData, secondData, thirdData, otherDataList, value, list, timeList } = toRefs(state);
  const arrayList: any[] = ['carousel', 'rankList', 'timeAxis'];
  const globSetting = useGlobSetting();

  const getOption = computed(() => activeData.option);
  const getLinzenKey = computed(() => activeData.projectKey);

  watch(
    () => unref(getOption).styleType,
    val => {
      if (unref(getLinzenKey) == 'rankList' && (val == 3 || val == 4)) handleRankList();
    },
  );
  watch(
    () => activeData.dataType,
    () => {
      if (arrayList.includes(unref(getLinzenKey))) {
        getOption.value.defaultValue = [];
        state.list = [];
      }
    },
  );
  watch(
    () => unref(getOption).defaultValue,
    val => {
      if (arrayList.includes(unref(getLinzenKey))) {
        state.list = JSON.parse(JSON.stringify(val));
      } else {
        state.value = JSON.parse(JSON.stringify(val));
      }
      if (unref(getLinzenKey) == 'rankList' && (getOption.value.styleType == 3 || getOption.value.styleType == 4)) handleRankList();
      if (unref(getLinzenKey) == 'timeAxis') handleTimeAxis();
      if (unref(getLinzenKey) == 'video' && getOption.value.styleType == 1 && val.url) state.value = globSetting.apiUrl + val.url;
    },
    { immediate: true, deep: true },
  );
  watch(
    () => unref(getOption).sortable,
    () => handleTimeAxis(),
  );
  watch(
    () => unref(getOption).showNumber,
    () => handleTimeAxis(),
  );
  watch(
    () => activeData.propsApi,
    val => {
      if (activeData.dataType === 'static' || !val) return;
      state.list = [];
      state.value = '';
      getDataInterfaceRes(val).then(res => {
        if (arrayList.includes(unref(getLinzenKey))) {
          if (Array.isArray(res.data)) state.list = res.data;
          if (unref(getLinzenKey) == 'rankList' && (unref(getOption).styleType == 3 || unref(getOption).styleType == 4)) handleRankList();
          if (unref(getLinzenKey) == 'timeAxis') handleTimeAxis();
        } else {
          state.value = res.data;
        }
      });
    },
  );

  function initData() {
    if (activeData.dataType === 'dynamic') {
      const propsApi = activeData.propsApi;
      if (!propsApi) return;
      getDataInterfaceRes(propsApi).then(res => {
        handleData(res.data);
      });
    } else {
      handleData(activeData.option.defaultValue);
    }
  }
  function handleData(val) {
    const option = activeData.option;
    if (arrayList.includes(unref(getLinzenKey))) {
      state.list = JSON.parse(JSON.stringify(val));
    } else {
      state.value = JSON.parse(JSON.stringify(val));
    }
    if (unref(getLinzenKey) == 'rankList' && (option.styleType == 3 || option.styleType == 4)) handleRankList();
    if (unref(getLinzenKey) == 'timeAxis') handleTimeAxis();
    if (unref(getLinzenKey) == 'video' && option.styleType == 1 && val.url) state.value = globSetting.apiUrl + val.url;
  }
  function handleRankList() {
    state.firstData = {};
    state.secondData = {};
    state.thirdData = {};
    state.otherDataList = [];
    if (state.list && state.list.length) state.firstData = state.list[0];
    if (state.list && state.list.length && state.list.length >= 2) state.secondData = state.list[1];
    if (state.list && state.list.length && state.list.length >= 3) state.thirdData = state.list[2];
    state.otherDataList = JSON.parse(JSON.stringify(state.list)).slice(3, state.list.length);
  }
  function handleTimeAxis() {
    const showNumber = unref(getOption).showNumber || 50;
    const list = state.list.slice(0, showNumber);
    state.timeList = JSON.parse(JSON.stringify(list));
    if (activeData.option.sortable == 2) state.timeList.reverse();
  }

  onMounted(() => initData());

  return {
    CardHeader,
    webLink,
    getOption,
    value,
    list,
    firstData,
    secondData,
    thirdData,
    otherDataList,
    timeList,
  };
}
