<template>
  <BasicPopup v-bind="$attrs" @register="TemplateIndexRegister" title="待入职模板" class="full-popup" destroyOnClose>
    <div style="margin: 10px 10px 10px 10px">
      <div @click="openTemplate"> 啊啊啊啊啊啊啊啊啊啊111</div>
    </div>
  </BasicPopup>
  <Template @register="TemplatePopup"></Template>
</template>
<script lang="ts" setup>
import { ref } from "vue";
import Template from "./Template.vue";

import { BasicPopup, usePopup, usePopupInner } from "/@/components/Popup";

const [TemplateIndexRegister, { closePopup, changeLoading, changeOkLoading }] = usePopupInner(init);


const [TemplatePopup, { openPopup: openTemplatePopup }] = usePopup();

function init(data) {

}

function openTemplate(data) {
  openTemplatePopup(true, { ...data });
}


</script>
