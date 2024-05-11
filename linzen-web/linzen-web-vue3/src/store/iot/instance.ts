import { DeviceInstance } from "/@/views/iot/iotdevice/typings"
import { defineStore } from "pinia"
import { deviceDetail } from '/@/api/device/instance'

export const useInstanceStore = defineStore({
  id: 'device',
  state: () => ({
    current: {} as DeviceInstance,
    detail: {} as DeviceInstance,
    tabActiveKey: 'Info'
  }),
  actions: {
    setCurrent(current: DeviceInstance) {
      this.current = current
      this.detail = current
    },
    async refresh(id: string) {
      const resp: any = await deviceDetail(id)
      console.info("获取设备的详细信息:", resp);
      if(resp.code === 200){
        this.current = resp.data;
        this.detail = resp.data;
        console.info(this.detail);
      }
    },
    setTabActiveKey(key: string) {
      this.tabActiveKey = key
    },
  }
})
