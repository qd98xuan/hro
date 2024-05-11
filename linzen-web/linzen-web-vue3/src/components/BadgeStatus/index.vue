<template>
    <a-badge
        :color="_color"
        :text="text"
    ></a-badge>
</template>

<script setup lang="ts">
import { Badge as ABadge } from 'ant-design-vue';
import { getHexColor } from './color'
import {computed} from "vue";
const props = defineProps({
    text: {
        type: String,
    },
    status: {
        type: [String, Number],
        default: 'default',
    },
    /**
     * 自定义status值颜色
     * @example {
     *   1: 'success',
     *   0: 'error'
     * }
     */
    statusNames: {
      type: Object,
      default: () => ({
        'processing': '9, 46, 231',
        'error': '229, 0, 18',
        'success': '36, 178, 118',
        'warning': '255, 144, 0',
        'default': '102, 102, 102',
        //告警颜色
        'level1': '229, 0,  18',
        'level2': '255, 148,  87',
        'level3': '250, 189,  71',
        'level4': '153, 153, 153',
        'level5': '196, 196,  196'
      })
    },
});

const _color = computed(() => {
  return getHexColor(props.statusNames[props.status], 1)
})
</script>
