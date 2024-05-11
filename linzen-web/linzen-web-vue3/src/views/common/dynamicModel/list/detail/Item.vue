<template>
  <a-col
    :class="[...(getConfig.className || []), getConfig.layout === 'colFormItem' ? 'ant-col-item' : '']"
    :span="getConfig.span"
    v-if="!getConfig.noShow && (!getConfig.visibility || (Array.isArray(getConfig.visibility) && getConfig.visibility.includes('pc')))">
    <template v-if="getConfig.layout === 'colFormItem'">
      <template v-if="getConfig.projectKey === 'divider'">
        <linzen-divider :contentPosition="item.contentPosition" :content="item.content" />
      </template>
      <template v-else>
        <a-form-item :name="item.__vModel__" :labelCol="getLabelCol">
          <template #label v-if="getConfig.showLabel">
            {{ getConfig.label ? getConfig.label + (formConf.labelSuffix || '') : '' }}
            <BasicHelp :text="getConfig.tipLabel" v-if="getConfig.label && getConfig.tipLabel" />
          </template>
          <template v-if="getConfig.projectKey === 'text'">
            <linzen-text :content="item.content" :textStyle="item.textStyle" />
          </template>
          <template v-else-if="getConfig.projectKey === 'link'">
            <linzen-link :content="item.content" :href="item.href" :target="item.target" :textStyle="item.textStyle" />
          </template>
          <template v-else-if="getConfig.projectKey === 'alert'">
            <linzen-alert
              :title="item.title"
              :type="item.type"
              :closable="item.closable"
              :showIcon="item.showIcon"
              :description="item.description"
              :closeText="item.closeText" />
          </template>
          <template v-else-if="getConfig.projectKey === 'groupTitle'">
            <linzen-group-title :content="item.content" :contentPosition="item.contentPosition" :helpMessage="item.helpMessage" />
          </template>
          <template v-else-if="getConfig.projectKey === 'button'">
            <linzen-button :align="item.align" :buttonText="item.buttonText" :type="item.type" :disabled="item.disabled" />
          </template>
          <template v-else-if="getConfig.projectKey === 'uploadFile'">
            <linzen-upload-file v-model:value="getConfig.defaultValue" detailed disabled />
          </template>
          <template v-else-if="getConfig.projectKey === 'uploadImg'">
            <linzen-upload-img v-model:value="getConfig.defaultValue" detailed disabled />
          </template>
          <template v-else-if="getConfig.projectKey === 'colorPicker'">
            <linzen-color-picker v-model:value="getConfig.defaultValue" :showAlpha="item.showAlpha" :colorFormat="item.colorFormat" disabled />
          </template>
          <template v-else-if="getConfig.projectKey === 'rate'">
            <linzen-rate v-model:value="getConfig.defaultValue" :count="item.count" :allowHalf="item.allowHalf" disabled />
          </template>
          <template v-else-if="getConfig.projectKey === 'slider'">
            <linzen-slider v-model:value="getConfig.defaultValue" :min="item.min" :max="item.max" :step="item.step" disabled />
          </template>
          <template v-else-if="getConfig.projectKey === 'editor'">
            <div v-html="getConfig.defaultValue"></div>
          </template>
          <template v-else-if="getConfig.projectKey === 'relationForm'">
            <p class="link-text" @click="toDetail(item)">{{ item.name }}</p>
          </template>
          <template v-else-if="getConfig.projectKey === 'popupSelect'">
            <p>{{ item.name }}</p>
          </template>
          <template v-else-if="getConfig.projectKey === 'barcode'">
            <linzen-barcode
              :format="item.format"
              :lineColor="item.lineColor"
              :background="item.background"
              :width="item.width"
              :height="item.height"
              :staticText="item.staticText"
              :dataType="item.dataType"
              :relationField="item.relationField + '_id'"
              :formData="formData" />
          </template>
          <template v-else-if="getConfig.projectKey === 'qrcode'">
            <linzen-qrcode
              :format="item.format"
              :colorLight="item.colorLight"
              :colorDark="item.colorDark"
              :width="item.width"
              :staticText="item.staticText"
              :dataType="item.dataType"
              :relationField="item.relationField + '_id'"
              :formData="formData" />
          </template>
          <template v-else-if="getConfig.projectKey === 'inputNumber'">
            <linzen-input-number
              v-model:value="getConfig.defaultValue"
              :precision="item.precision"
              :addonBefore="item.addonBefore"
              :addonAfter="item.addonAfter"
              :thousands="item.thousands"
              :isAmountChinese="item.isAmountChinese"
              disabled
              detailed />
          </template>
          <template v-else-if="getConfig.projectKey === 'calculate'">
            <linzen-calculate
              :expression="item.expression"
              :isStorage="item.isStorage"
              :formData="formData"
              :precision="item.precision"
              :thousands="item.thousands"
              :isAmountChinese="item.isAmountChinese"
              detailed />
          </template>
          <template v-else-if="getConfig.projectKey === 'location'">
            <linzen-location v-model:value="getConfig.defaultValue" :enableLocationScope="item.enableLocationScope" detailed />
          </template>
          <template v-else-if="getConfig.projectKey === 'sign'">
            <linzen-sign v-model:value="getConfig.defaultValue" detailed />
          </template>
          <template v-else-if="getConfig.projectKey === 'iframe'">
            <linzen-iframe
              :href="item.href"
              :height="item.height"
              :borderType="item.borderType"
              :borderColor="item.borderColor"
              :borderWidth="item.borderWidth" />
          </template>
          <template v-else-if="getConfig.projectKey === 'input'">
            <linzen-input
              v-model:value="getConfig.defaultValue"
              :addonBefore="item.addonBefore"
              :addonAfter="item.addonAfter"
              :useMask="item.useMask"
              :maskConfig="item.maskConfig"
              detailed />
          </template>
          <template v-else>
            <p>{{ getValue(item.__config__?.defaultValue || '') }}</p>
          </template>
        </a-form-item>
      </template>
    </template>
    <template v-else>
      <template v-if="getConfig.projectKey === 'card'">
        <a-card :hoverable="item.shadow === 'hover'" :size="formConf.size">
          <template #title v-if="item.header">{{ item.header }}<BasicHelp :text="getConfig.tipLabel" v-if="getConfig.tipLabel" /></template>
          <a-row>
            <Item v-for="(childItem, childIndex) in getConfig.children" v-bind="getBindValue" :key="childIndex" :item="childItem" @toDetail="toDetail" />
          </a-row>
        </a-card>
      </template>
      <a-row v-if="getConfig.projectKey === 'row'">
        <Item v-for="(childItem, childIndex) in getConfig.children" v-bind="getBindValue" :key="childIndex" :item="childItem" @toDetail="toDetail" />
      </a-row>
      <template v-if="getConfig.projectKey === 'tab'">
        <a-tabs :type="item.type" :tabPosition="item.tabPosition" :size="formConf.size" v-model:activeKey="getConfig.active">
          <a-tab-pane v-for="pane in getConfig.children" :key="pane.name" :tab="pane.title">
            <a-row>
              <Item
                v-for="(childItem, childIndex) in pane.__config__.children"
                v-bind="getBindValue"
                :key="childIndex"
                :item="childItem"
                @toDetail="toDetail" />
            </a-row>
          </a-tab-pane>
        </a-tabs>
      </template>
      <template v-if="getConfig.projectKey === 'collapse'">
        <a-collapse :ghost="item.ghost" :expandIconPosition="item.expandIconPosition" :accordion="item.accordion" v-model:activeKey="getConfig.active">
          <a-collapse-panel v-for="pane in getConfig.children" :key="pane.name" :header="pane.title">
            <a-row>
              <Item
                v-for="(childItem, childIndex) in pane.__config__.children"
                v-bind="getBindValue"
                :key="childIndex"
                :item="childItem"
                @toDetail="toDetail" />
            </a-row>
          </a-collapse-panel>
        </a-collapse>
      </template>
      <template v-if="getConfig.projectKey === 'table'">
        <a-form-item v-if="!getConfig.noShow">
          <LinzenGroupTitle :content="getConfig.label" v-if="getConfig.showTitle && getConfig.label" :helpMessage="getConfig.tipLabel" :bordered="false" />
          <a-table
            :data-source="item.__config__.defaultValue"
            :columns="getColumns"
            size="small"
            :pagination="false"
            :scroll="{ x: 'max-content' }"
            :bordered="formConf.formStyle === 'word-form' || !!getConfig?.complexHeaderList?.length">
            <template #headerCell="{ column }">
              {{ column.title }}
              <BasicHelp v-if="column.title && column.__config__?.tipLabel" :text="column.__config__.tipLabel" />
            </template>
            <template #bodyCell="{ column, record, index }">
              <template v-if="column.key === 'index'">{{ index + 1 }}</template>
              <template v-else-if="column.__config__?.projectKey === 'uploadFile'">
                <linzen-upload-file v-model:value="record[column.dataIndex]" detailed disabled />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'uploadImg'">
                <linzen-upload-img v-model:value="record[column.dataIndex]" detailed disabled />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'colorPicker'">
                <linzen-color-picker v-model:value="record[column.dataIndex]" :showAlpha="column.showAlpha" :colorFormat="column.colorFormat" disabled />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'rate'">
                <linzen-rate v-model:value="record[column.dataIndex]" :count="column.count" :allowHalf="column.allowHalf" disabled />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'slider'">
                <linzen-slider v-model:value="record[column.dataIndex]" :min="column.min" :max="column.max" :step="column.step" disabled />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'relationForm'">
                <p class="link-text" @click="toTableDetail(column, record[column.dataIndex + '_id'])">{{ record[column.dataIndex] }}</p>
              </template>
              <template v-else-if="['relationFormAttr', 'popupAttr'].includes(column.__config__?.projectKey)">
                <p v-if="!record[column.dataIndex]">{{ record[column.relationField.split('_linzenTable_')[0] + '_' + column.showField] }}</p>
                <p v-else>{{ record[column.dataIndex] }}</p>
              </template>
              <template v-else-if="column.__config__?.projectKey === 'inputNumber'">
                <linzen-input-number
                  v-model:value="record[column.dataIndex]"
                  :precision="column.precision"
                  :addonBefore="column.addonBefore"
                  :addonAfter="column.addonAfter"
                  :thousands="column.thousands"
                  :isAmountChinese="column.isAmountChinese"
                  disabled
                  detailed />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'calculate'">
                <linzen-calculate
                  :rowIndex="index"
                  :expression="column.expression"
                  :isStorage="column.isStorage"
                  :formData="formData"
                  :precision="column.precision"
                  :thousands="column.thousands"
                  :isAmountChinese="column.isAmountChinese"
                  detailed />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'location'">
                <linzen-location v-model:value="record[column.dataIndex]" :enableLocationScope="column.enableLocationScope" detailed />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'sign'">
                <linzen-sign v-model:value="record[column.dataIndex]" detailed />
              </template>
              <template v-else-if="column.__config__?.projectKey === 'input'">
                <linzen-input
                  v-model:value="record[column.dataIndex]"
                  :addonBefore="column.addonBefore"
                  :addonAfter="column.addonAfter"
                  :useMask="column.useMask"
                  :maskConfig="column.maskConfig"
                  detailed />
              </template>
              <template v-else>
                <p>{{ getValue(record[column.dataIndex]) }}</p>
              </template>
            </template>
            <template #summary v-if="item.__config__.defaultValue.length && item.showSummary">
              <a-table-summary fixed>
                <a-table-summary-row>
                  <a-table-summary-cell :index="0">合计</a-table-summary-cell>
                  <a-table-summary-cell v-for="(item, index) in getColumnSum" :key="index" :index="index + 1" :align="getSummaryCellAlign(index)">
                    {{ item }}
                  </a-table-summary-cell>
                </a-table-summary-row>
              </a-table-summary>
            </template>
          </a-table>
        </a-form-item>
      </template>
      <template v-if="getConfig.projectKey === 'tableGrid'">
        <table
          class="table-grid-box"
          :style="{
            '--borderType': item.__config__.borderType,
            '--borderColor': item.__config__.borderColor,
            '--borderWidth': item.__config__.borderWidth + 'px',
          }">
          <tbody>
            <tr v-for="(tr, index) in getConfig.children" :key="index">
              <td
                v-for="(td, i) in tr.__config__.children"
                :key="i"
                :colspan="td.__config__.colspan"
                :rowspan="td.__config__.rowspan"
                v-show="!td.__config__.merged"
                :style="{
                  '--backgroundColor': td.__config__.backgroundColor,
                }">
                <Item
                  v-for="(childItem, childIndex) in td.__config__.children"
                  v-bind="getBindValue"
                  :key="childIndex"
                  :item="childItem"
                  @toDetail="toDetail" />
              </td>
            </tr>
          </tbody>
        </table>
      </template>
    </template>
  </a-col>
</template>

<script lang="ts" setup>
  import { computed, unref, toRaw } from 'vue';
  import { omit } from 'lodash-es';
  import { thousandsFormat } from '/@/utils/linzen';

  defineOptions({ name: 'Item' });
  const props = defineProps({
    item: { type: Object, required: true },
    formConf: { type: Object, required: true },
    formData: { type: Object },
    loading: { type: Boolean, default: false },
  });
  const emit = defineEmits(['toDetail']);

  const getBindValue = computed(() => ({ ...omit(props, ['item']) }));
  const getConfig = computed(() => props.item.__config__);
  const getLabelCol = computed(() => {
    const globalLabelWidth = props.formConf.labelWidth;
    let labelCol = {};
    if (props.formConf.labelPosition !== 'top' && unref(getConfig).showLabel) {
      let labelWidth = (unref(getConfig).labelWidth || globalLabelWidth) + 'px';
      if (!unref(getConfig).showLabel) labelWidth = '0px';
      labelCol = { style: { width: labelWidth } };
    }
    return labelCol;
  });
  const getColumns = computed(() => {
    if (unref(getConfig).projectKey !== 'table') return [];
    const noColumn = { width: 50, title: '序号', dataIndex: 'index', key: 'index', align: 'center', customRender: ({ index }) => index + 1, fixed: 'left' };
    const list = unref(getConfig)
      .children.filter(
        o => !o.__config__.noShow && (!o.__config__.visibility || (Array.isArray(o.__config__.visibility) && o.__config__.visibility.includes('pc'))),
      )
      .map(o => ({
        ...o,
        title: o.__config__?.label,
        dataIndex: o.__vModel__,
        width: o.__config__?.columnWidth || undefined,
        align: o.__config__.tableAlign || 'left',
        fixed: o.__config__.tableFixed == 'left' || o.__config__.tableFixed == 'right' ? o.__config__.tableFixed : false,
      }));
    let columnList = list;
    let complexHeaderList: any[] = props.item.__config__.complexHeaderList || [];
    if (complexHeaderList.length) {
      let childColumns: any[] = [];
      for (let i = 0; i < complexHeaderList.length; i++) {
        const e = complexHeaderList[i];
        e.title = e.fullName;
        e.align = e.align;
        e.children = [];
        e.projectKey = 'complexHeader';
        if (e.childColumns?.length) {
          childColumns.push(...e.childColumns);
          for (let j = 0; j < list.length; j++) {
            const o = list[j];
            if (e.childColumns.includes(o.__vModel__) && o.__config__.tableFixed !== 'left' && o.__config__.tableFixed !== 'right')
              e.children.push({ ...toRaw(o), align: o.__config__.tableAlign || 'left' });
          }
        }
      }
      complexHeaderList = complexHeaderList.filter(o => o.children.length);
      for (let i = 0; i < list.length; i++) {
        const item = list[i];
        if (!childColumns.includes(item.__vModel__) || item.__config__.tableFixed === 'left' || item.__config__.tableFixed === 'right')
          complexHeaderList.push(item);
      }
      columnList = complexHeaderList;
    }
    let columns = [noColumn, ...columnList];
    const leftFixedList = columns.filter(o => o.fixed === 'left');
    const rightFixedList = columns.filter(o => o.fixed === 'right');
    const noFixedList = columns.filter(o => o.fixed !== 'left' && o.fixed !== 'right');
    return [...leftFixedList, ...noFixedList, ...rightFixedList];
  });
  const getSummaryColumn = computed(() => {
    let defaultColumns = unref(getColumns);
    let columns: any[] = [];
    for (let i = 0; i < defaultColumns.length; i++) {
      const e = defaultColumns[i];
      if (e.projectKey === 'table' || e.projectKey === 'complexHeader') {
        if (e.children?.length) columns.push(...e.children);
      } else {
        columns.push(e);
      }
      if (e.fixed && e.children?.length) {
        for (let j = 0; j < e.children.length; j++) {
          e.children[j].fixed = e.fixed;
        }
      }
    }
    return columns.filter(o => o?.key != 'index' && o?.key != 'action');
  });
  const getColumnSum = computed(() => {
    if (unref(getConfig).projectKey !== 'table') return [];
    const list = unref(getSummaryColumn);
    const sums: any[] = [];
    const isSummary = key => props.item.summaryField.includes(key);
    const useThousands = key => list.some(o => o.__vModel__ === key && o.thousands);
    const tableData = list.filter(o => !o.__config__.noShow && (!o.__config__.visibility || o.__config__.visibility.includes('pc')));
    tableData.forEach((column, index) => {
      let sumVal = unref(getConfig).defaultValue.reduce((sum, d) => sum + getCmpValOfRow(d, column.__vModel__), 0);
      if (!isSummary(column.__vModel__)) sumVal = '';
      sumVal = Number.isNaN(sumVal) ? '' : sumVal;
      const realVal = sumVal && !Number.isInteger(sumVal) ? Number(sumVal).toFixed(2) : sumVal;
      sums[index] = useThousands(column.__vModel__) ? thousandsFormat(realVal) : realVal;
    });
    return sums;
  });

  function toDetail(item) {
    emit('toDetail', item);
  }
  function toTableDetail(item, value) {
    item.__config__.defaultValue = value;
    emit('toDetail', item);
  }
  function getValue(value) {
    return Array.isArray(value) ? value.join() : value;
  }
  function getCmpValOfRow(row, key) {
    const isSummary = key => props.item.summaryField.includes(key);
    if (!props.item.summaryField.length || !isSummary(key)) return 0;
    const target = row[key];
    if (!target) return 0;
    const data = isNaN(target) ? 0 : Number(target);
    return data;
  }
  function getSummaryCellAlign(index) {
    if (!unref(getSummaryColumn).length) return;
    return unref(getSummaryColumn)[index]?.align || 'left';
  }
</script>
