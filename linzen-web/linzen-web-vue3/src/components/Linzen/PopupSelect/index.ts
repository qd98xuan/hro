import { withInstall } from '/@/utils';
import type { ExtractPropTypes } from 'vue';
import PopupSelect from './src/PopupSelect.vue';
import PopupTableSelect from './src/PopupTableSelect.vue';
import { popupSelectProps } from './src/props';

export const LinzenPopupSelect = withInstall(PopupSelect);
export const LinzenPopupTableSelect = withInstall(PopupTableSelect);
export declare type PopupSelectProps = Partial<ExtractPropTypes<typeof popupSelectProps>>;
