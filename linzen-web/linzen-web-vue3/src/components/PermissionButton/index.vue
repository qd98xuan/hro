<template>
  <template v-if="isPermission">
    <template v-if="popConfirm">
      <a-popconfirm :disabled="!isPermission || props.disabled" :overlayStyle='{width: "220px", zIndex: 1075 }' v-bind="popConfirm">
        <a-tooltip v-if="tooltip" v-bind="tooltip">
          <slot v-if="noButton"></slot>
          <a-button v-else v-bind="props" :disabled="_isPermission" :style="props.style">
            <slot></slot>
            <template #icon>
              <slot name="icon"></slot>
            </template>
          </a-button>
        </a-tooltip>
        <a-button v-else v-bind="props" :disabled="_isPermission" >
          <slot></slot>
          <template #icon>
            <slot name="icon"></slot>
          </template>
        </a-button>
      </a-popconfirm>
    </template>
    <template v-else-if="tooltip">
      <a-tooltip v-bind="tooltip">
        <slot v-if="noButton"></slot>
        <a-button v-else v-bind="props" :disabled="_isPermission" :style="props.style">
          <slot></slot>
          <template #icon>
            <slot name="icon"></slot>
          </template>
        </a-button>
      </a-tooltip>
    </template>
    <template v-else>
      <slot v-if="noButton"></slot>
      <a-button v-else v-bind="props" :disabled="_isPermission" :style="props.style">
        <slot></slot>
        <template #icon>
          <slot name="icon"></slot>
        </template>
      </a-button>
    </template>
  </template>
  <a-tooltip v-else title="暂无权限，请联系管理员" :placement="placement">
    <slot v-if="noButton"></slot>
    <a-button v-else v-bind="props" :disabled="_isPermission" :style="props.style">
      <slot></slot>
      <template #icon>
        <slot name="icon"></slot>
      </template>
    </a-button>
  </a-tooltip>
</template>
<script setup lang="ts" name="PermissionButton">
import {computed, CSSProperties, PropType} from 'vue'
import { TooltipProps, PopconfirmProps } from 'ant-design-vue/es'
import { buttonProps } from 'ant-design-vue/es/button/button'
import { omit } from 'lodash-es';

const props = defineProps({
  noButton: {
    type: Boolean,
    default: () => false
  },
  tooltip: {
    type: Object as PropType<TooltipProps>,
  },
  popConfirm: {
    type: Object as PropType<PopconfirmProps>,
  },
  hasPermission: {
    type: [String , Array, Boolean],
  },
  style: {
    type: Object as PropType<CSSProperties>
  },
  placement:{
    type: String,
    default: 'top'
  },
  ...omit(buttonProps(), 'icon')
})

const isPermission = computed(() => {
  if (!props.hasPermission || props.hasPermission === true) {
    return true;
  }
  return false;
})
const _isPermission = computed(() =>
  'hasPermission' in props && isPermission.value ? 'disabled' in props ? props.disabled as boolean : false : true
)

// const conform = (e: MouseEvent) => {
//   props.popConfirm?.onConfirm?.(e)
// }
</script>
<style scoped lang="less">

</style>
