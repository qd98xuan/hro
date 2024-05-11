<template>
  <BasicPopup v-bind="$attrs" @register="TemplateRegister" title="待入职模板编辑" class="full-popup" showOkBtn @ok="handleSubmit" destroyOnClose>
    <div style="margin: 10px 10px 10px 10px;" class="center">
      <a-row style="height: 100%">
        <a-col :span="4">
          <a-divider>录入信息选择</a-divider>
          <div class="nthClid" style="border-right: 1px solid rgba(0, 0, 0, 0.06);">
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
                <div class="wid45">
                  <a-button type="primary" class="leftBoxCon move" :key="index">
                    <template #icon>
                      <i class="icon-linzen icon-linzen-darg" title="点击拖动" />
                    </template>
                    {{ element.name }}
                  </a-button>
                </div>
              </template>
            </draggable>
          </div>
        </a-col>
        <a-col :span="18" :offset="1">
          <a-divider>表单组成</a-divider>
          <div style="height: 100%">
            <a-form layout="inline"  style="height: calc(100% - 100px)" ref="formRef">
              <draggable
                  :list="centerData"
                  ghost-class=""
                  itemKey="id"
                  :force-fallback="true"
                  group="list"
                  :fallback-class="true"
                  :fallback-on-body="true"
                  dragClass="dragClass"
                  class="draggable"

              >
                <template #item="{element, index}">
                  <div class="inputItem move" @click="focusInput(element)">
                    <div>
                      <a-form-item v-if="element.type === 'Input'" name="input" :rules="[{ required: true, message: 'Please input your username!' }]">
                        <template #label>
                          {{ element.name }}
                        </template>
                        <a-input v-model:value="element.input" :placeholder="element.placeholder" :key="index"
                                 style="width: 350px;" />
                      </a-form-item>
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
            </a-form>
          </div>
        </a-col>
      </a-row>
    </div>
    <div class="page">
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

function handleSubmit() {
  console.info(formRef);
}
// 获取中间内容
defineExpose({
  checkForm
});
</script>

<style scoped lang="scss">

.center {
  background-color: #ffffff;
  width: 100%;
  height: 100%;
  border-radius: 8px;

  &-title {
    font-size: 16px;
    font-weight: 700;
  }

  .dragClass {
    padding: 10px 20px 10px 20px;
    background-color: #deebff;
    border-radius: 5px;
    margin: 15px;
    cursor: move;
    position: relative;

    &:hover {
      background: #fff;
      -webkit-box-shadow: 0 4px 12px 0 rgba(106, 102, 246, .1);
      box-shadow: 0 4px 12px 0 rgba(106, 102, 246, .1);
      border: 1px dashed #6a66f6;
    }
  }

  .inputItem {
    padding: 10px 20px 10px 20px;
    background-color: #deebff7d;
    border-radius: 5px;
    margin: 15px;
    cursor: move;
    position: relative;

    &:hover {
      background: #fff;
      -webkit-box-shadow: 0 4px 12px 0 rgba(106, 102, 246, .1);
      box-shadow: 0 4px 12px 0 rgba(106, 102, 246, .1);
      border: 1px dashed #6a66f6;
    }
  }
}

.draggable {
  height: 100%;
  overflow-y: auto;
  padding: 10px;
  width: 100%;
  background-color: #f8f8fdfc;
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
  bottom: 30px;
  right: 5px;
}

.delItem:hover {
  cursor: pointer;
  background-color: #4169E1;
  color: #ffffff;
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

.title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}

.wid45 {
  width: 48%;
  display: inline-block
}

.nthClid {
  padding-left: 15px;

  .wid45:nth-child(2n) .leftBoxCon {
    margin-left: 8px;
    margin-bottom: 8px;
  }
}

</style>
