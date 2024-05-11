<template>
  <div class="linzen-content-wrapper">
    <div class="linzen-content-wrapper-center">
      <div class="linzen-content-wrapper-content bg-white">
        <a-row>
          <a-col :span="4">
            <a-divider>员工字段设置</a-divider>
            <div class="scene_trigger_way_warp" @click="viewPersonalInfo">
              <div class="trigger-way-item">
                <div class="way-item-title">
                  <p style="color: rgb(48, 49, 51);">个人信息设置</p>
                  <span>设置个人基本信息、联系信息等</span>
                </div>
                <div class="way-item-image">
                  <i style="font-size: 36px" class="add-icon icon-linzen icon-linzen-pc"></i>
                </div>
              </div>
            </div>
            <div class="scene_trigger_way_warp">
              <div class="trigger-way-item">
                <div class="way-item-title">
                  <p style="color: rgb(48, 49, 51);">个人信息设置</p>
                  <span>设置个人基本信息、联系信息等</span>
                </div>
                <div class="way-item-image">
                  <i style="font-size: 36px" class="add-icon icon-linzen icon-linzen-pc"></i>
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="1">
          </a-col>
          <a-col :span="4">
            <a-divider>入职模板设置</a-divider>
            <div class="scene_trigger_way_warp" @click="entryTemplateSet">
              <div class="trigger-way-item">
                <div class="way-item-title">
                  <p style="color: rgb(48, 49, 51);">入职模板设置</p>
                  <span>设置入职模板</span>
                </div>
                <div class="way-item-image">
                  <i style="font-size: 36px" class="add-icon icon-linzen icon-linzen-pc"></i>
                </div>
              </div>
            </div>
            <div class="scene_trigger_way_warp">
              <div class="trigger-way-item">
                <div class="way-item-title">
                  <p style="color: rgb(48, 49, 51);">待入职模板设置</p>
                  <span>设置待入职模板</span>
                </div>
                <div class="way-item-image">
                  <i style="font-size: 36px" class="add-icon icon-linzen icon-linzen-pc"></i>
                </div>
              </div>
            </div>
          </a-col>
          <a-col :span="4">
          </a-col>
        </a-row>
      </div>
    </div>
    <PersonalInfo @register="PersonalInfoPopup"></PersonalInfo>
    <TemplateIndex @register="TemplateIndexPopup"></TemplateIndex>
  </div>
</template>
<script lang="ts" setup>
import { usePopup } from "/@/components/Popup";

import PersonalInfo from "./PersonalInfo/index.vue";
import TemplateIndex from "./Template/index.vue";

const [PersonalInfoPopup, { openPopup: openPersonalInfoPopup }] = usePopup();
const [TemplateIndexPopup, { openPopup: openTemplateIndexPopup }] = usePopup();

// 流程监控查看各节点表单详情
function viewPersonalInfo(data) {
  openPersonalInfoPopup(true, { ...data });
}
function entryTemplateSet(data) {
  openTemplateIndexPopup(true, { ...data });
}

</script>

<style scoped lang="less">
.scene_trigger_way_warp {
  display: flex;
  flex-wrap: wrap;
  gap: 5px 5px;
  width: 100%;
  padding-top: 8px;

  .trigger-way-item {
    display: flex;
    justify-content: space-between;
    padding: 16px;
    border: 1px solid #e0e4e8;
    border-radius: 5px;
    cursor: pointer;
    transition: all 0.3s;
    width: 270px;
    margin: 0px 8px 0px 8px;

    &:hover {
      border: 1px solid rgba(0, 140, 255, 0.651);
    }

    .way-item-title {
      width: 200px;

      p {
        margin-bottom: 8px;
        font-weight: bold;
        font-size: 14px;
      }

      span {
        color: rgba(#000, 0.35);
        font-size: 12px;
      }
    }

    .way-item-image {
      display: flex;
      align-items: center;
      height: 100%;
      margin: 0 !important;
      opacity: 0.5;
    }

    &:hover {
      color: @primary-color-hover;

      .way-item-image {
        opacity: 0.8;
      }
    }

    &.active {
      border-color: @primary-color-active;

      .way-item-image {
        opacity: 1;
      }
    }
  }

  &.disabled {
    color: rgba(#000, 0.8);

    .way-item-image {
      opacity: 0.6;
    }

    .trigger-way-item {
      cursor: not-allowed;
    }
  }
}

.gutter-example :deep(.ant-row > div) {
  background: transparent;
  border: 0;
}

.gutter-box {
  background: #00a0e9;
  padding: 5px 0;
}
</style>
