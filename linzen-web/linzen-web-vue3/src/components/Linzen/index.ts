import { Input, DatePicker } from 'ant-design-vue';

// linzen 组件
import { BasicCaption } from '/@/components/Basic';
import { LinzenAlert } from '/@/components/Linzen/Alert';
import { LinzenAreaSelect } from '/@/components/Linzen/AreaSelect';
import { LinzenAutoComplete } from '/@/components/Linzen/AutoComplete';
import { LinzenButton } from '/@/components/Linzen/Button';
import { LinzenCron } from '/@/components/Linzen/Cron';
import { LinzenCascader } from '/@/components/Linzen/Cascader';
import { LinzenCheckbox, LinzenCheckboxSingle } from '/@/components/Linzen/Checkbox';
import { LinzenColorPicker } from '/@/components/Linzen/ColorPicker';
import { LinzenDatePicker, LinzenDateRange, LinzenTimePicker, LinzenTimeRange } from '/@/components/Linzen/DatePicker';
import { LinzenDivider } from '/@/components/Linzen/Divider';
import { LinzenIconPicker } from '/@/components/Linzen/IconPicker';
import { LinzenInput, LinzenTextarea } from '/@/components/Linzen/Input';
import { LinzenInputNumber } from '/@/components/Linzen/InputNumber';
import { LinzenLink } from '/@/components/Linzen/Link';
import { LinzenOpenData } from '/@/components/Linzen/OpenData';
import {
  LinzenOrganizeSelect,
  LinzenDepSelect,
  LinzenPosSelect,
  LinzenGroupSelect,
  LinzenRoleSelect,
  LinzenUserSelect,
  LinzenUsersSelect,
} from '/@/components/Linzen/Organize';
import { LinzenQrcode } from '/@/components/Linzen/Qrcode';
import { LinzenBarcode } from '/@/components/Linzen/Barcode';
import { LinzenRadio } from '/@/components/Linzen/Radio';
import { LinzenSelect } from '/@/components/Linzen/Select';
import { LinzenRate } from '/@/components/Linzen/Rate';
import { LinzenSlider } from '/@/components/Linzen/Slider';
import { LinzenSign } from '/@/components/Linzen/Sign';
import { LinzenSwitch } from '/@/components/Linzen/Switch';
import { LinzenText } from '/@/components/Linzen/Text';
import { LinzenTreeSelect } from '/@/components/Linzen/TreeSelect';
import { LinzenUploadFile, LinzenUploadImg, LinzenUploadImgSingle } from '/@/components/Linzen/Upload';
import { Tinymce } from '/@/components/Tinymce/index';
import { LinzenRelationForm } from '/@/components/Linzen/RelationForm';
import { LinzenRelationFormAttr } from '/@/components/Linzen/RelationFormAttr';
import { LinzenPopupSelect, LinzenPopupTableSelect } from '/@/components/Linzen/PopupSelect';
import { LinzenPopupAttr } from '/@/components/Linzen/PopupAttr';
import { LinzenNumberRange } from '/@/components/Linzen/NumberRange';
import { LinzenCalculate } from '/@/components/Linzen/Calculate';
import { LinzenInputTable } from '/@/components/Linzen/InputTable';
import { LinzenLocation } from '/@/components/Linzen/Location';
import { LinzenIframe } from '/@/components/Linzen/Iframe';
import { LinzenMonacoEditor } from '/@/components/Linzen/MonacoEditor';
import { LinzenProTable } from '/@/components/Linzen/ProTable';
import { LinzenAIcon } from '/@/components/Linzen/AIcon';

const LinzenInputPassword = Input.Password;
LinzenInputPassword.name = 'LinzenInputPassword';
const LinzenInputGroup = Input.Group;
LinzenInputGroup.name = 'LinzenInputGroup';
const LinzenInputSearch = Input.Search;
LinzenInputSearch.name = 'LinzenInputSearch';
const LinzenEditor = Tinymce;
LinzenEditor.name = 'LinzenEditor';
const LinzenGroupTitle = BasicCaption;
LinzenGroupTitle.name = 'LinzenGroupTitle';
const LinzenMonthPicker = DatePicker.MonthPicker;
LinzenMonthPicker.name = 'LinzenMonthPicker';
const LinzenWeekPicker = DatePicker.WeekPicker;
LinzenWeekPicker.name = 'LinzenWeekPicker';

export {
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
  LinzenIframe,
  LinzenMonacoEditor,
  LinzenProTable,
  LinzenAIcon,
};
