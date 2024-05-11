<!-- 参数类型输入组件 -->
<template>
    <div class="value-item-warp">
        <Select
            v-if="typeMap.get(itemType) === 'select'"
            :mode="mode"
            v-model:value="myValue"
            :options="options"
            allowClear
            style="width: 100%"
            :getPopupContainer="getPopupContainer"
            @change='selectChange'
        />
        <TimePicker
          v-else-if="typeMap.get(itemType) === 'time'"
          v-model:value="myValue"
          allowClear
          valueFormat="HH:mm:ss"
          style="width: 100%"
          :getPopupContainer="getPopupContainer"
          @change='timeChange'
        />
        <DatePicker
            v-else-if="typeMap.get(itemType) === 'date'"
            v-model:value="myValue"
            allowClear
            showTime
            valueFormat="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
            :getPopupContainer="getPopupContainer"
            @change='dateChange'
        />
<!--        <InputNumber-->
<!--            v-else-if="typeMap.get(itemType) === 'inputNumber'"-->
<!--            v-model:value="myValue"-->
<!--            allowClear-->
<!--            style="width: 100%"-->
<!--            @change='inputChange'-->
<!--        />-->
        <Input
            allowClear
            v-else-if="typeMap.get(itemType) === 'object'"
            v-model:value="myValue"
            @change='inputChange'
        >
            <template #addonAfter>
                <FormOutlined @click="modalVis = true" />
            </template>
        </Input>
        <GeoComponent
            v-else-if="typeMap.get(itemType) === 'geoPoint'"
            v-model:point="myValue"
            @change='inputChange'
        />
        <Input
            v-else-if="typeMap.get(itemType) === 'file'"
            v-model:value="myValue"
            placeholder="请输入链接"
            allowClear
            @change='inputChange'
        >
            <template #addonAfter>
                <a-upload
                    name="file"
                    :action="getAction"
                    :headers="headers"
                    :showUploadList="false"
                    @change="handleFileChange"
                >
                    <UploadOutlined />
                </a-upload>
            </template>
        </Input>
        <InputPassword
            v-else-if="typeMap.get(itemType) === 'password'"
            allowClear
            type="password"
            v-model:value="myValue"
            style="width: 100%"
            @change='inputChange'
        />
        <Input
            v-else
            :placeholder="placeholder"
            allowClear
            type="text"
            v-model:value="myValue"
            style="width: 100%"
            @change='inputChange'
        />

        <!-- 代码编辑器弹窗 -->
        <AModal
            title="编辑"
            ok-text="确认"
            cancel-text="取消"
            v-model:visible="modalVis"
            width="700px"
            :getPopupContainer="getPopupContainer"
            @cancel="modalVis = false"
            @ok="handleItemModalSubmit"
            :zIndex='1100'
        >
            <div style="width: 100%; height: 300px">
<!--                <JMonacoEditor v-model:modelValue="objectValue" />-->
            </div>
        </AModal>
    </div>
</template>

<script setup lang="ts" name='ValueItem'>
import { PropType, ref, watch, computed } from "vue";
import { UploadChangeParam, UploadFile } from 'ant-design-vue';
import { DefaultOptionType } from 'ant-design-vue/lib/select';
import { ItemData, ITypes } from './types';
import { FormOutlined, UploadOutlined } from '@ant-design/icons-vue';
import { TimePicker, DatePicker, Modal as AModal, InputNumber, Input, InputPassword, Select } from 'ant-design-vue';
import GeoComponent from '../GeoComponent/index.vue';
import { useGlobSetting } from "/@/hooks/setting";
import { getToken } from "/@/utils/auth";
const globSetting = useGlobSetting();
const getAction = computed(() => globSetting.uploadUrl);

type Emits = {
    (e: 'update:modelValue', data: string | number | boolean): void;
    (e: 'change', data: any, item?: any): void;
};
const emit = defineEmits<Emits>();

const props = defineProps({
    itemData: {
        type: Object as PropType<ItemData>,
        default: () => ({}),
    },
    // 组件双向绑定的值
    modelValue: {
        type: [Number, String],
        default: '',
    },
    // 组件类型
    itemType: {
        type: String,
        default: () => 'string',
    },
    // 下拉选择框下拉数据
    options: {
        type: Array as PropType<DefaultOptionType[]>,
        default: () => [],
    },
    // 多选框
    mode: {
        type: String as PropType<'multiple' | 'tags' | 'combobox' | ''>,
        default: ''
    },
    placeholder: {
        type: String,
        default: () => '',
    },
    getPopupContainer: {
        type: Function,
        default: undefined
    }
});
// type Props = {
//     itemData?: Object;
//     modelValue?: string | number | boolean;
// };
// const props = withDefaults(defineProps<Props>(), {
//     itemData: () => ({ type: 'object' }),
//     modelValue: '',
// });

const componentsType = ref<ITypes>({
    int: 'inputNumber',
    long: 'inputNumber',
    float: 'inputNumber',
    double: 'inputNumber',
    string: 'input',
    array: 'input',
    password: 'password',
    enum: 'select',
    boolean: 'select',
    date: 'date',
    object: 'object',
    geoPoint: 'geoPoint',
    file: 'file',
});
const typeMap = new Map(Object.entries(componentsType.value));

// const myValue = computed({
//     get: () => {
//         return props.modelValue;
//     },
//     set: (val: any) => {
//         objectValue.value = val;
//         emit('update:modelValue', val);
//     },
// });

const myValue = ref(props.modelValue)

// 代码编辑器弹窗
const modalVis = ref<boolean>(false);
const objectValue = ref<string>('');
const handleItemModalSubmit = () => {
    myValue.value = objectValue.value.replace(/[\r\n]\s*/g, '');
    modalVis.value = false;
    emit('update:modelValue', objectValue.value);
    emit('change', objectValue.value)
};

// 文件上传
const headers = computed(() => ({ Authorization: getToken() as string }));
const handleFileChange = (info: UploadChangeParam<UploadFile<any>>) => {
    if (info.file.status === 'done') {
        const url = info.file.response?.result;
        myValue.value = url;
        emit('update:modelValue', url);
        emit('change', url);
    }
};

const selectChange = (e: string, option: any) => {
  emit('update:modelValue', myValue.value);
  emit('change', e, option)
}

const timeChange = (e: any) => {
  emit('update:modelValue', myValue.value);
  emit('change', e)
}

const inputChange = (e: any) => {
  emit('update:modelValue', myValue.value);
  emit('change', e && e.target ? e.target.value : e)
}

const dateChange = (e: any) => {
  emit('update:modelValue', myValue.value);
  emit('change', e)
}

watch(() => props.modelValue, () => {
  myValue.value = props.modelValue
}, { immediate: true })

if (props.itemType === 'object') {
  objectValue.value = props.modelValue as string
}

</script>

<style lang="less" scoped></style>
