import { ProductItem } from '/@/views/iot/iotproduct/typings';
import { defineStore } from "pinia";
import { detail, getDeviceNumber } from '/@/api/device/product';

export const useProductStore = defineStore({
  id: "product",
  state: () => ({
    current: {} as ProductItem,
    detail: {} as ProductItem,
    tabActiveKey: "Info"
  }),
  actions: {
    setCurrent(current: ProductItem) {
      this.current = current;
      this.detail = current;
    },
    async getDetail(id: string) {
      const resp = await detail(id);
      if (resp.code === 200) {
        this.current = {
          ...this.current, ...resp.data
        };
        this.detail = resp.data;
      }
    },
    async refresh(id: string) {
      await this.getDetail(id);
      const res = await getDeviceNumber({ productId: id });
      if (res.code === 200) {
        this.current.count = res.data;
      }
    },
    setTabActiveKey(key: string) {
      this.tabActiveKey = key;
    },
    reSet() {
      this.current = {} as ProductItem;
      this.detail = {} as ProductItem;
    }
  }
});
