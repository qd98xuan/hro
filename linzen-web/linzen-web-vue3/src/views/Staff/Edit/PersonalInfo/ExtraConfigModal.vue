<template>
  <BasicModal v-bind="$attrs" @register="registerModal" title="下拉选择框配置" @ok="handleSubmit" destroyOnClose
              class="extra-config-modal">
    <div class="extra-config-modal-body" :style="{ 'min-height': '250px'}">
      <a-form :colon="false" labelAlign="left" :labelCol="{ style: { width: '90px' } }" class="right-board-form">
        <div class="options-list">
          <draggable v-model="activeData.dropDownArray" :animation="300" group="selectItem" handle=".option-drag"
                     itemKey="uuid">
            <template #item="{ element, index }">
              <div class="select-item">
                <div class="select-line-icon option-drag">
                  <i class="icon-linzen icon-linzen-darg" />
                </div>
                <a-input v-model:value="element[index]" placeholder="选项名" />
                <div class="close-btn select-line-icon" @click="activeData.dropDownArray.splice(index, 1)">
                  <i class="icon-linzen icon-linzen-btn-clearn" />
                </div>
              </div>
            </template>
          </draggable>
        </div>
        <div class="table-add-action" style="margin-left: 29px; margin-right: 27px; margin-top: 15px;"
             @click="addSelectItem">
          <a-button type="link" preIcon="icon-linzen icon-linzen-btn-add" class="!px-0">
            添加选项
          </a-button>
        </div>
      </a-form>
    </div>
  </BasicModal>
</template>
<script lang="ts" setup>
import { reactive, toRefs } from "vue";
import { BasicModal, useModal, useModalInner } from "/@/components/Modal";
import { cloneDeep } from "lodash-es";
import draggable from "vuedraggable";
import { buildBitUUID } from "/@/utils/uuid";
import { useMessage } from "/@/hooks/web/useMessage";

const { createMessage } = useMessage();

interface State {
  activeData: any;
}

const emit = defineEmits(["register", "confirm"]);

const state = reactive<State>({
  activeData: {}
});
const { activeData } = toRefs(state);
const [registerModal, { closeModal }] = useModalInner(init);

const addSelectItem = () => {
  let options = state.activeData.dropDownArray;
  for (var i = 0; i < options.length; i++) {
    console.info(state.activeData.dropDownArray[i]);
    if (!state.activeData.dropDownArray[i]) {
      createMessage.info("选项不能为空，请输入或者删除");
      return;
    }
  }
  state.activeData.dropDownArray.push("");
};

function init(data) {
  state.activeData = cloneDeep(data);
}

function handleSubmit() {
  let options = state.activeData.dropDownArray;
  for (var i = 0; i < options.length; i++) {
    console.info(state.activeData.dropDownArray[i]);
    if (!state.activeData.dropDownArray[i]) {
      createMessage.info("选项不能为空，请输入或者删除");
      return;
    }
  }
  state.activeData.options = options.join(",");
  emit("confirm", state.activeData);
  closeModal();
}
</script>
<style lang="less" scoped>
.extra-config-modal {
  .extra-config-modal-body {
    min-height: 150px;
    padding-bottom: 20px;

    .options-list {
      max-height: 200px;
      overflow-y: auto;
      margin-bottom: -10px;

      .scrollbar__wrap {
        margin-bottom: 0 !important;
      }

      .select-item {
        display: flex;
        border: 1px dashed @component-background;
        box-sizing: border-box;

        & .ant-input + .ant-input {
          margin-left: 4px;
        }

        .ant-select {
          width: 100%;
        }

        & + .select-item {
          margin-top: 4px;
        }

        &.sortable-chosen {
          border: 1px dashed @primary-color;
        }

        .select-line-icon {
          line-height: 31px;
          font-size: 22px;
          padding: 0 4px;
          color: #606266;

          .icon-linzen-darg {
            font-size: 20px;
            line-height: 31px;
            display: inline-block;
          }

          .icon-linzen-btn-clearn {
            font-size: 18px;
          }
        }

        .close-btn {
          cursor: pointer;
          color: @error-color;
          height: 32px;
          display: flex;
          align-items: center;
        }

        .option-drag {
          cursor: move;
        }
      }

      .linzen-tree__name {
        width: calc(100% - 60px);
      }
    }

    .add-btn {
      padding-left: 27px;
      margin-top: 10px;
    }
  }
}
</style>
