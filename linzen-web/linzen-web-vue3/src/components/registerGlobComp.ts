import type {App} from 'vue';
import {Button} from './Button';
import {
    Input,
    InputNumber,
    Layout,
    Form,
    Switch,
    Dropdown,
    Menu,
    Select,
    Table,
    Checkbox,
    Tabs,
    Collapse,
    Card,
    Tooltip,
    Row,
    Col,
    Popconfirm,
    Divider,
    Alert,
    AutoComplete,
    Cascader,
    Rate,
    Slider,
    Avatar,
    Tag,
    Space,
    Steps,
    Popover,
    Radio,
    Progress,
    Image,
    Upload,
} from 'ant-design-vue';

import {BasicHelp, BasicCaption} from '/@/components/Basic';
import {TitleComponent} from '/@/components/TitleComponent';
import {LinzenAlert} from '/@/components/Linzen/Alert';
import {LinzenAreaSelect} from '/@/components/Linzen/AreaSelect';
import {LinzenAutoComplete} from '/@/components/Linzen/AutoComplete';
import {LinzenButton} from '/@/components/Linzen/Button';
import {LinzenCron} from '/@/components/Linzen/Cron';
import {LinzenCascader} from '/@/components/Linzen/Cascader';
import {LinzenCheckbox, LinzenCheckboxSingle} from '/@/components/Linzen/Checkbox';
import {LinzenColorPicker} from '/@/components/Linzen/ColorPicker';
import {LinzenDatePicker, LinzenDateRange, LinzenTimePicker, LinzenTimeRange} from '/@/components/Linzen/DatePicker';
import {LinzenDivider} from '/@/components/Linzen/Divider';
import {LinzenEmpty} from '/@/components/Linzen/Empty';
import {LinzenIconPicker} from '/@/components/Linzen/IconPicker';
import {LinzenInput, LinzenTextarea} from '/@/components/Linzen/Input';
import {LinzenInputNumber} from '/@/components/Linzen/InputNumber';
import {LinzenLink} from '/@/components/Linzen/Link';
import {LinzenOpenData} from '/@/components/Linzen/OpenData';
import {
    LinzenOrganizeSelect,
    LinzenDepSelect,
    LinzenPosSelect,
    LinzenGroupSelect,
    LinzenRoleSelect,
    LinzenUserSelect,
    LinzenUsersSelect,
} from '/@/components/Linzen/Organize';
import {LinzenQrcode} from '/@/components/Linzen/Qrcode';
import {LinzenBarcode} from '/@/components/Linzen/Barcode';
import {LinzenRadio} from '/@/components/Linzen/Radio';
import {LinzenSelect} from '/@/components/Linzen/Select';
import {LinzenRate} from '/@/components/Linzen/Rate';
import {LinzenSlider} from '/@/components/Linzen/Slider';
import {LinzenSign} from '/@/components/Linzen/Sign';
import {LinzenSwitch} from '/@/components/Linzen/Switch';
import {LinzenText} from '/@/components/Linzen/Text';
import {LinzenTreeSelect} from '/@/components/Linzen/TreeSelect';
import {LinzenUploadFile, LinzenUploadImg, LinzenUploadImgSingle, LinzenUploadBtn} from '/@/components/Linzen/Upload';
import {Tinymce} from '/@/components/Tinymce/index';
import {LinzenNumberRange} from '/@/components/Linzen/NumberRange';
import {LinzenRelationFormAttr} from '/@/components/Linzen/RelationFormAttr';
import {LinzenPopupSelect, LinzenPopupTableSelect} from '/@/components/Linzen/PopupSelect';
import {LinzenPopupAttr} from '/@/components/Linzen/PopupAttr';
import {LinzenCalculate} from '/@/components/Linzen/Calculate';
import {LinzenLocation} from '/@/components/Linzen/Location';
import {LinzenIframe} from '/@/components/Linzen/Iframe';

const LinzenEditor = Tinymce;
LinzenEditor.name = 'LinzenEditor';
const LinzenGroupTitle = BasicCaption;
LinzenGroupTitle.name = 'LinzenGroupTitle';



import {LinzenMonacoEditor} from '/@/components/Linzen/MonacoEditor';
import {LinzenProTable} from '/@/components/Linzen/ProTable';
import {LinzenAIcon} from '/@/components/Linzen/AIcon';

// import LinzenCardBox from './CardBox/index.vue';

export function registerGlobComp(app: App) {
    app
        .use(Input)
        .use(InputNumber)
        .use(Button)
        .use(Layout)
        .use(Form)
        .use(Switch)
        .use(Dropdown)
        .use(Menu)
        .use(Select)
        .use(Table)
        .use(Checkbox)
        .use(Tabs)
        .use(Card)
        .use(Collapse)
        .use(Tooltip)
        .use(Row)
        .use(Col)
        .use(Popconfirm)
        .use(Popover)
        .use(Divider)
        .use(Slider)
        .use(Rate)
        .use(Alert)
        .use(AutoComplete)
        .use(Cascader)
        .use(Avatar)
        .use(Tag)
        .use(Space)
        .use(Steps)
        .use(Radio)
        .use(Progress)
        .use(Image)
        .use(Upload)
        .use(BasicHelp)

        .use(TitleComponent)

        .use(LinzenAlert)
        .use(LinzenRate)
        .use(LinzenSlider)
        .use(LinzenAreaSelect)
        .use(LinzenAutoComplete)
        .use(LinzenButton)
        .use(LinzenCron)
        .use(LinzenCascader)
        .use(LinzenCheckbox)
        .use(LinzenCheckboxSingle)
        .use(LinzenColorPicker)
        .use(LinzenDatePicker)
        .use(LinzenDateRange)
        .use(LinzenTimePicker)
        .use(LinzenTimeRange)
        .use(LinzenDivider)
        .use(LinzenEmpty)
        .use(LinzenGroupTitle)
        .use(LinzenIconPicker)
        .use(LinzenInput)
        .use(LinzenTextarea)
        .use(LinzenInputNumber)
        .use(LinzenLink)
        .use(LinzenOrganizeSelect)
        .use(LinzenDepSelect)
        .use(LinzenPosSelect)
        .use(LinzenGroupSelect)
        .use(LinzenRoleSelect)
        .use(LinzenUserSelect)
        .use(LinzenUsersSelect)
        .use(LinzenOpenData)
        .use(LinzenQrcode)
        .use(LinzenBarcode)
        .use(LinzenRadio)
        .use(LinzenSelect)
        .use(LinzenSign)
        .use(LinzenSwitch)
        .use(LinzenText)
        .use(LinzenTreeSelect)
        .use(LinzenEditor)
        .use(LinzenRelationFormAttr)
        .use(LinzenPopupSelect)
        .use(LinzenPopupTableSelect)
        .use(LinzenPopupAttr)
        .use(LinzenNumberRange)
        .use(LinzenCalculate)
        .use(LinzenUploadFile)
        .use(LinzenUploadImg)
        .use(LinzenUploadImgSingle)
        .use(LinzenUploadBtn)
        .use(LinzenLocation)
        .use(LinzenIframe)
        .use(LinzenMonacoEditor)
        .use(LinzenProTable)
        .use(LinzenAIcon);
}
