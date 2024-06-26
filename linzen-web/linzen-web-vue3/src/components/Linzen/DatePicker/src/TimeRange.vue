<template>
  <TimeRangePicker v-bind="getBindValue" v-model:value="innerValue" @change="onChange" />
</template>

<script lang="ts" setup>
  import { TimePicker } from 'ant-design-vue';
  import { computed, ref, unref, watch } from 'vue';
  import { timeRangeProps } from './props';
  import { useAttrs } from '/@/hooks/core/useAttrs';

  const TimeRangePicker = TimePicker.TimeRangePicker;

  defineOptions({ name: 'LinzenTimeRange', inheritAttrs: false });
  const props = defineProps(timeRangeProps);
  const emit = defineEmits(['update:value', 'change']);
  const attrs = useAttrs({ excludeDefaultKeys: false });
  const innerValue = ref<[string, string] | undefined>(undefined);

  const getStartTimeArr = computed(() => {
    if (!props.startTime) return [];
    return props.startTime.split(':');
  });
  const getEndTimeArr = computed(() => {
    if (!props.endTime) return [];
    return props.endTime.split(':');
  });
  const getBindValue = computed(() => ({
    ...unref(attrs),
    placeholder: props.placeholder,
    format: props.format,
    valueFormat: props.format,
    showNow: !props.startTime && !props.endTime,
    disabledTime: () => ({
      disabledHours: () => {
        const { startTime, endTime } = props;
        if (!startTime && !endTime) return [];
        const hours: number[] = [];
        if (startTime) {
          const startHour = Number(unref(getStartTimeArr)[0]);
          for (let i = 0; i < startHour; i++) {
            hours.push(i);
          }
        }
        if (endTime) {
          const endHour = Number(unref(getEndTimeArr)[0]);
          for (let i = 0; i < 24; i++) {
            if (i > endHour) hours.push(i);
          }
        }
        return hours;
      },
      disabledMinutes: selectedHour => {
        const { startTime, endTime } = props;
        if (!startTime && !endTime) return [];
        const minutes: number[] = [];
        if (startTime) {
          const startHour = Number(unref(getStartTimeArr)[0]);
          const startMinute = Number(unref(getStartTimeArr)[1]);
          if (selectedHour < startHour) {
            for (let i = 0; i < 60; i++) {
              minutes.push(i);
            }
            return minutes;
          }
          if (selectedHour === startHour) {
            for (let i = 0; i < startMinute; i++) {
              minutes.push(i);
            }
          }
        }
        if (endTime) {
          const endHour = Number(unref(getEndTimeArr)[0]);
          const endMinute = Number(unref(getEndTimeArr)[1]);
          if (selectedHour === endHour) {
            for (let i = 0; i < 60; i++) {
              if (i > endMinute) minutes.push(i);
            }
            return minutes;
          }
          if (selectedHour > endHour) {
            for (let i = 0; i < 60; i++) {
              minutes.push(i);
            }
            return minutes;
          }
        }
        return minutes;
      },
      disabledSeconds: (selectedHour, selectedMinute) => {
        const { startTime, endTime } = props;
        if ((!startTime && !endTime) || props.format === 'HH:mm') return [];
        const seconds: number[] = [];
        const selectedHourMinute = padZero(selectedHour) + padZero(selectedMinute);
        if (startTime) {
          const startHourMinute = unref(getStartTimeArr)[0] + unref(getStartTimeArr)[1];
          const startSecond = unref(getStartTimeArr)[2] ? Number(unref(getStartTimeArr)[2]) : 0;
          if (selectedHourMinute < startHourMinute) {
            for (let i = 0; i < 60; i++) {
              seconds.push(i);
            }
            return seconds;
          }
          if (selectedHourMinute === startHourMinute) {
            for (let i = 0; i < startSecond; i++) {
              seconds.push(i);
            }
          }
        }
        if (endTime) {
          const endHourMinute = unref(getEndTimeArr)[0] + unref(getEndTimeArr)[1];
          const endSecond = unref(getEndTimeArr)[2] ? Number(unref(getEndTimeArr)[2]) : 0;
          if (selectedHourMinute === endHourMinute) {
            for (let i = 0; i < 60; i++) {
              if (i > endSecond) seconds.push(i);
            }
            return seconds;
          }
          if (selectedHourMinute > endHourMinute) {
            for (let i = 0; i < 60; i++) {
              seconds.push(i);
            }
            return seconds;
          }
        }
        return seconds;
      },
    }),
  }));

  watch(
    () => props.value,
    val => {
      setValue(val);
    },
    { immediate: true },
  );

  function setValue(value) {
    innerValue.value = value;
  }
  function onChange(date) {
    emit('update:value', date);
    emit('change', date);
  }
  function padZero(str) {
    return new RegExp(/^\d$/g).test(str) ? `0${str}` : str.toString();
  }
</script>
