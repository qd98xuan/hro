import type { GenItem } from '../types/genItem';
// 动态options
const dyOptionsList = ['radio', 'checkbox', 'select', 'cascader', 'treeSelect'];
// 展示组件
const vModelIgnoreList = ['divider', 'text', 'link', 'alert', 'groupTitle', 'button', 'barcode', 'qrcode', 'iframe'];
// 动态存储
const dyStorageList = ['relationFormAttr', 'popupAttr', 'calculate'];
// 不添加vModel
const noVModelList = [...vModelIgnoreList, ...dyStorageList];
// 不可以添加到子表组件
const noTableAllowList = [
  ...vModelIgnoreList,
  'editor',
  'radio',
  'checkbox',
  'createUser',
  'createTime',
  'updateUser',
  'updateTime',
  'currOrganize',
  'currDept',
  'currPosition',
  'colorPicker',
  'table',
];
// 不可以添加到列表展示
const noColumnShowList = [...vModelIgnoreList, 'colorPicker', 'editor', 'relationFormAttr', 'popupAttr'];
// 不可以添加到搜索
const noSearchList = [
  ...noColumnShowList,
  // 'switch',
  'timeRange',
  'dateRange',
  'relationForm',
  'popupSelect',
  'popupTableSelect',
  'uploadImg',
  'uploadFile',
  'sign',
];
// 搜索时控件为input
const useInputList = ['input', 'textarea', 'billRule', 'location'];
// 搜索时控件为日期选择器
const useDateList = ['createTime', 'updateTime'];
// 搜索时控件为下拉选择器
const useSelectList = ['radio', 'checkbox', 'select'];
// 系统控件
const systemComponentsList = ['createUser', 'createTime', 'updateUser', 'updateTime', 'currOrganize', 'currPosition', 'billRule'];
// 不允许关联到联动里面的控件
const noAllowRelationList = ['table', 'uploadImg', 'uploadFile', 'updateUser', 'updateTime'];
// 不允许关联表单选择的控件
const noAllowSelectList = [...noAllowRelationList, ...systemComponentsList, 'relationForm', 'popupSelect'];
// 不允许分组和排序
const noGroupList = ['sign', 'location', 'uploadImg', 'uploadFile', 'editor'];
const calculateItem: GenItem = {
  __config__: {
    projectKey: 'calculate',
    label: '计算公式',
    tipLabel: '',
    labelWidth: undefined,
    showLabel: true,
    required: false,
    tag: 'LinzenCalculate',
    tagIcon: 'icon-linzen icon-linzen-generator-count',
    className: [],
    defaultValue: null,
    layout: 'colFormItem',
    span: 24,
    dragDisabled: false,
    visibility: ['pc', 'app'],
    tableName: '',
    noShow: false,
    regList: [],
  },
  style: { width: '100%' },
  expression: [],
  isStorage: 0,
  thousands: false,
  isAmountChinese: false,
  precision: 2,
};
// 在线开发-功能设计/流程设计/移动设计独有组件
const onlinePeculiarList: GenItem[] = [
  {
    __config__: {
      projectKey: 'qrcode',
      label: '二维码',
      tipLabel: '',
      labelWidth: undefined,
      showLabel: true,
      tag: 'LinzenQrcode',
      tagIcon: 'icon-linzen icon-linzen-generator-qrcode',
      className: [],
      defaultValue: '',
      layout: 'colFormItem',
      span: 24,
      dragDisabled: false,
      visibility: ['pc', 'app'],
      tableName: '',
      noShow: false,
      regList: [],
    },
    colorDark: '#000',
    colorLight: '#fff',
    width: 100,
    dataType: 'static',
    staticText: '二维码',
    relationField: '',
  },
  {
    __config__: {
      projectKey: 'barcode',
      label: '条形码',
      tipLabel: '',
      labelWidth: undefined,
      showLabel: true,
      tag: 'LinzenBarcode',
      tagIcon: 'icon-linzen icon-linzen-generator-barcode',
      className: [],
      defaultValue: '',
      layout: 'colFormItem',
      span: 24,
      dragDisabled: false,
      visibility: ['pc', 'app'],
      tableName: '',
      noShow: false,
      regList: [],
    },
    format: 'code128',
    lineColor: '#000',
    background: '#fff',
    width: 4,
    height: 40,
    dataType: 'static',
    staticText: '10241024',
    relationField: '',
  },
];
export {
  dyOptionsList,
  noVModelList,
  noTableAllowList,
  noColumnShowList,
  noSearchList,
  calculateItem,
  onlinePeculiarList,
  useInputList,
  useDateList,
  useSelectList,
  systemComponentsList,
  noAllowRelationList,
  noAllowSelectList,
  noGroupList,
};
