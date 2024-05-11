import type { Component } from 'vue';
import type { ComponentType } from './types/index';

/**
 * Component list, register here to setting it in the form
 */
import { StrengthMeter } from '/@/components/StrengthMeter';
import { CountdownInput } from '/@/components/CountDown';
// linzen 组件
import {
  LinzenAlert,
  LinzenAreaSelect,
  LinzenAutoComplete,
  LinzenButton,
  LinzenCron,
  LinzenCascader,
  LinzenColorPicker,
  LinzenCheckbox,
  LinzenCheckboxSingle,
  LinzenDatePicker,
  LinzenDateRange,
  LinzenTimePicker,
  LinzenTimeRange,
  LinzenMonthPicker,
  LinzenWeekPicker,
  LinzenDivider,
  LinzenEditor,
  LinzenGroupTitle,
  LinzenIconPicker,
  LinzenInput,
  LinzenInputPassword,
  LinzenInputGroup,
  LinzenInputSearch,
  LinzenTextarea,
  LinzenInputNumber,
  LinzenLink,
  LinzenOpenData,
  LinzenOrganizeSelect,
  LinzenDepSelect,
  LinzenPosSelect,
  LinzenGroupSelect,
  LinzenRoleSelect,
  LinzenUserSelect,
  LinzenUsersSelect,
  LinzenQrcode,
  LinzenBarcode,
  LinzenRadio,
  LinzenRate,
  LinzenSelect,
  LinzenSlider,
  LinzenSign,
  LinzenSwitch,
  LinzenText,
  LinzenTreeSelect,
  LinzenUploadFile,
  LinzenUploadImg,
  LinzenUploadImgSingle,
  LinzenRelationForm,
  LinzenRelationFormAttr,
  LinzenPopupSelect,
  LinzenPopupTableSelect,
  LinzenPopupAttr,
  LinzenNumberRange,
  LinzenCalculate,
  LinzenInputTable,
  LinzenLocation,
  LinzenIframe, LinzenProTable, LinzenAIcon,
} from '/@/components/Linzen';

import LinzenCardBox from "/@/components/CardBox/index.vue";

const componentMap = new Map<ComponentType, Component>();

componentMap.set('StrengthMeter', StrengthMeter);
componentMap.set('InputCountDown', CountdownInput);

componentMap.set('InputGroup', LinzenInputGroup);
componentMap.set('InputSearch', LinzenInputSearch);
componentMap.set('MonthPicker', LinzenMonthPicker);
componentMap.set('WeekPicker', LinzenWeekPicker);

componentMap.set('Alert', LinzenAlert);
componentMap.set('AreaSelect', LinzenAreaSelect);
componentMap.set('AutoComplete', LinzenAutoComplete);
componentMap.set('Button', LinzenButton);
componentMap.set('Cron', LinzenCron);
componentMap.set('Cascader', LinzenCascader);
componentMap.set('ColorPicker', LinzenColorPicker);
componentMap.set('Checkbox', LinzenCheckbox);
componentMap.set('LinzenCheckboxSingle', LinzenCheckboxSingle);
componentMap.set('DatePicker', LinzenDatePicker);
componentMap.set('DateRange', LinzenDateRange);
componentMap.set('TimePicker', LinzenTimePicker);
componentMap.set('TimeRange', LinzenTimeRange);
componentMap.set('Divider', LinzenDivider);
componentMap.set('Editor', LinzenEditor);
componentMap.set('GroupTitle', LinzenGroupTitle);
componentMap.set('Input', LinzenInput);
componentMap.set('InputPassword', LinzenInputPassword);
componentMap.set('Textarea', LinzenTextarea);
componentMap.set('InputNumber', LinzenInputNumber);
componentMap.set('IconPicker', LinzenIconPicker);
componentMap.set('Link', LinzenLink);
componentMap.set('OrganizeSelect', LinzenOrganizeSelect);
componentMap.set('DepSelect', LinzenDepSelect);
componentMap.set('PosSelect', LinzenPosSelect);
componentMap.set('GroupSelect', LinzenGroupSelect);
componentMap.set('RoleSelect', LinzenRoleSelect);
componentMap.set('UserSelect', LinzenUserSelect);
componentMap.set('UsersSelect', LinzenUsersSelect);
componentMap.set('Qrcode', LinzenQrcode);
componentMap.set('Barcode', LinzenBarcode);
componentMap.set('Radio', LinzenRadio);
componentMap.set('Rate', LinzenRate);
componentMap.set('Select', LinzenSelect);
componentMap.set('Slider', LinzenSlider);
componentMap.set('Sign', LinzenSign);
componentMap.set('Switch', LinzenSwitch);
componentMap.set('Text', LinzenText);
componentMap.set('TreeSelect', LinzenTreeSelect);
componentMap.set('UploadFile', LinzenUploadFile);
componentMap.set('UploadImg', LinzenUploadImg);
componentMap.set('UploadImgSingle', LinzenUploadImgSingle);
componentMap.set('BillRule', LinzenInput);
componentMap.set('ModifyUser', LinzenInput);
componentMap.set('ModifyTime', LinzenInput);
componentMap.set('CreateUser', LinzenOpenData);
componentMap.set('CreateTime', LinzenOpenData);
componentMap.set('CurrOrganize', LinzenOpenData);
componentMap.set('CurrPosition', LinzenOpenData);
componentMap.set('RelationForm', LinzenRelationForm);
componentMap.set('RelationFormAttr', LinzenRelationFormAttr);
componentMap.set('PopupSelect', LinzenPopupSelect);
componentMap.set('PopupTableSelect', LinzenPopupTableSelect);
componentMap.set('PopupAttr', LinzenPopupAttr);
componentMap.set('NumberRange', LinzenNumberRange);
componentMap.set('Calculate', LinzenCalculate);
componentMap.set('InputTable', LinzenInputTable);
componentMap.set('Location', LinzenLocation);
componentMap.set('Iframe', LinzenIframe);

componentMap.set('ProTable', LinzenProTable);
componentMap.set('AIcon', LinzenAIcon);
componentMap.set('CardBox', LinzenCardBox);

export function add(compName: ComponentType, component: Component) {
  componentMap.set(compName, component);
}

export function del(compName: ComponentType) {
  componentMap.delete(compName);
}

export { componentMap };
