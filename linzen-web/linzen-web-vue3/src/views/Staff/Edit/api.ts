import { defHttp } from "/@/utils/http/axios";

// 保存数据
export function saveOrUpdateSetting(data) {
    return defHttp.post({ url: "/api/hro/EmployeeSetting/saveOrUpdateSetting", data });
}


// 保存数据
export function getBaseDetail(data) {
    return defHttp.post({ url: "/api/hro/EmployeeSetting/getBaseDetail", data });
}
