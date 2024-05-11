<template>
  <BasicPopup v-bind="$attrs" @register="registerPopup" title="个人信息设置" class="full-popup" destroyOnClose>
    <div style="margin: 10px 10px 10px 10px; height: calc(100% - 50px); overflow-y: auto;">
      <a-collapse v-model:activeKey="activeKey" class="bg-white">
        <a-collapse-panel v-for="(item, index)  in collapseList" :key="index + ''">
          <template #header> {{ item.moduleName }} {{ index }}</template>
          <BaseInfo :formData="item" moduleCode="item.moduleCode"></BaseInfo>
        </a-collapse-panel>
      </a-collapse>
    </div>
  </BasicPopup>
</template>
<script lang="ts" setup>
import { ref } from "vue";
import BaseInfo from "./BaseInfo.vue";
import { BasicPopup, usePopupInner } from "/@/components/Popup";
import { getBaseDetail } from "/@/views/Staff/Edit/api";

const emit = defineEmits(["register", "select"]);
const [registerPopup, { closePopup, changeLoading, changeOkLoading }] = usePopupInner(init);
const collapseList = ref<any>();
const activeKey = ref(["0"]);

function init(data) {
  changeLoading(true);
  getBaseDetail({ moduleEntity: { moduleType: "1" } }).then(res => {
    console.info(res);
    if (res.code == 200) {
      collapseList.value = res.data;
    }
    changeLoading(false);
  })
      .catch(() => {
        changeLoading(false);
      });
}
</script>
