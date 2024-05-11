<template>
  <BasicPopup v-bind="$attrs" @register="TemplateRegister" title="待入职模板编辑" class="full-popup" destroyOnClose>
    <div class="page">
      <div>
        <div class="title">字段库</div>
        <draggable
            :list="leftMenu"
            :forceFallback="true"
            chosen-class="chosenClass"
            ghostClass="ghostClass"
            dragClass="dragClass"
            fallbackClass="fallbackClass"
            :group="{ name: 'list', pull: 'clone' }"
            :clone="clone"
            :sort="true"
            itemKey="id"
        >
          <template #item="{ element, index }">
            <a-button type="primary" class="move" :key="index">
              <template #icon v-if="element.type === 'Input'">
                <edit-outlined />
              </template>
              <template #icon v-if="element.type === 'Textarea'">
                <ordered-list-outlined />
              </template>
              {{ element.name }}
            </a-button>
          </template>
        </draggable>
      </div>
      <div>
        <div class="title">字段库</div>
        <draggable
            :list="leftMenu"
            chosenClass="chosenClass"
            ghostClass="ghost"
            dragClass="dragClass"
            forceFallback="true"
            fallbackClass="true"
            fallbackOnBody="true"
            :group="{ name: 'list', pull: 'clone' }"
            :clone="clone"
            :sort="true"
            itemKey="id"
            animation="300"
            touchStartThreshold="0px"
        >
          <template #item="{ element, index }">
            <a-button class="move" :key="index">
              <template #icon v-if="element.type === 'Input'">
                <edit-outlined />
              </template>
              <template #icon v-if="element.type === 'Textarea'">
                <ordered-list-outlined />
              </template>
              {{ element.name }}
            </a-button>
          </template>
        </draggable>
      </div>
      <div class="center">
        <div class="center-title">编辑字段</div>
        <draggable
            :list="centerData"
            ghost-class="ghost"
            itemKey="id"
            :force-fallback="true"
            group="list"
            :fallback-class="true"
            :fallback-on-body="true"
            class="draggable"
            animation="300"
        >
          <template #item="{element, index}">
            <div class="inputItem move" @click="focusInput(element)">
              <div class="form-title move">{{ element.name }}</div>
              <div>
                <a-input v-model:value="input" @focus="focusInput(element)" v-if="element.type === 'Input'"
                         :placeholder="element.placeholder" :key="index" />
              </div>
              <div>
                <a-textarea
                    :key="index"
                    v-model:value="textarea"
                    show-count :maxlength="200"
                    @focus="focusInput(element)"
                    :placeholder="element.placeholder"
                    v-if="element.type === 'Textarea'"
                />
              </div>
              <div class="delItem" @click="deleteComponent(index)">
                <delete-outlined style="font-size: 14px; opacity: 0.8" />
              </div>
            </div>
          </template>
        </draggable>
      </div>
      <div class="right" v-if="rightData">
        <div class="title">{{ rightData.name }}</div>
        <div class="rightContent">
          <a-form ref="formRef" :model="rightData" labelAlign="left">
            <a-form-item name="name" label="标识名" :rules="[{ required: true, message: '标识名不能为空' }]">
              <a-input v-model:value="rightData.name" :placeholder="input" />
            </a-form-item>
            <a-form-item name="placeholder" label="提示语">
              <a-input v-model:value="rightData.placeholder" />
            </a-form-item>
            <a-form-item>
              <a-checkbox v-model:checked="rightData.isChecked">是否必填</a-checkbox>
            </a-form-item>
          </a-form>
        </div>
      </div>
    </div>
  </BasicPopup>
</template>
<script lang="ts" setup>
import draggable from "vuedraggable";
import {
  EditOutlined,
  OrderedListOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined
} from "@ant-design/icons-vue";
import { message, Modal } from "ant-design-vue";
import { ref, reactive, createVNode } from "vue";

import { BasicPopup, usePopupInner } from "/@/components/Popup";

const [TemplateRegister, { closePopup, changeLoading, changeOkLoading }] = usePopupInner(init);

function init(data) {

}

function moveToRight() {

}

const formRef = ref();
const input = ref("");
const textarea = ref("");
// 左侧组件菜单
const leftMenu = [
  {
    name: "单行文本",
    type: "Input",
    isChecked: true,
    placeholder: "请输入"
  },
  {
    name: "多行文本",
    type: "Textarea",
    isChecked: false,
    placeholder: "请输入"
  }
];

// clone一个新的拖动组件的值
const clone = (obj: any) => {
  console.info("深拷贝一个对象，否则三个数据指向的都是一个地址");
  // 深拷贝一个对象，否则三个数据指向的都是一个地址，
  const newObj = JSON.parse(JSON.stringify(obj));
  return newObj;
};
// 中间数据
const centerData = reactive([]);
// 右侧
let rightData = ref();

// 鼠标聚焦输入框时，显示右侧内容
const focusInput = (item: any) => {
  rightData.value = item;
};
// 删除
const deleteComponent = (index: number) => {
  // Modal.confirm({
  //   title: "确定要删除该组件吗?",
  //   icon: createVNode(ExclamationCircleOutlined),
  //   onOk() {
  //     centerData.splice(index, 1);
  //   }
  // });
  centerData.splice(index, 1);
};

// 表单校验
const checkForm = () => {
  formRef.value.validateFields().then(async () => {
    // console.log(centerData)
  });
};
// 获取中间内容
defineExpose({
  checkForm
});
</script>

<style scoped lang="scss">
.ant-form-item {
  display: block;
}

.page {
  position: relative;
  display: flex;
  justify-content: center;

  .left {
    width: 20%;
    position: absolute;
    left: 10px;

    .btns {
      margin: 0 10px;
    }
  }

  .center {
    background-color: #ffffff;
    width: 35%;
    height: 100%;
    border-radius: 8px;
    padding: 20px 30px;

    &-title {
      font-size: 16px;
      font-weight: 700;
    }

    .inputItem {
      padding: 10px 20px 30px 20px;
      background-color: #deebff;
      border-radius: 5px;
      margin: 15px;
      cursor: move;
      position: relative;

      .form-title {
        margin-bottom: 10px;
        margin-left: 5px;
      }

      .delItem {
        width: 30px;
        height: 30px;
        display: flex;
        justify-content: center;
        align-items: center;
        border-radius: 50%;
        box-shadow: 0 2px 4px 0 hsla(0, 0%, 63.9%, .5);
        background-color: #ffffff;
        color: #001529;
        text-align: center;
        position: absolute;
        bottom: -10px;
        right: 10px;
      }

      .delItem:hover {
        cursor: pointer;
        background-color: #4169E1;
        color: #ffffff;
      }
    }

    .draggable {
      height: 600px;
      background-color: red;
    }
  }

  .right {
    width: 15%;
    height: 100%;
    position: absolute;
    right: 0;

    .rightContent {
      background-color: #ffffff;
      padding: 10px 20px;
    }
  }
}

.title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}

/*.dragClass {
  border: 1px solid #01AE97;
  border-radius: 0px;
}*/
</style>
