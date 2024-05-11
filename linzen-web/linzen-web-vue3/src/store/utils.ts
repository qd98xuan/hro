/**
 * 生成随机数
 * @param length
 * @returns
 */
export const randomString = (length?: number) => {
  const tempLength = length || 32;
  const chars = 'ABCDEFGHJKMNPQRSTWXYZabcdefhijkmnprstwxyz2345678';
  const maxPos = chars.length;
  let pwd = '';
  for (let i = 0; i < tempLength; i += 1) {
    pwd += chars.charAt(Math.floor(Math.random() * maxPos));
  }
  return pwd;
};

/**
 * 时间戳转时分秒文本
 * @param time
 * @returns
 */
export const timestampFormat = (time: number) => {
  let hour = 0;
  let minute = 0;
  let second = 0;
  const timeStr = 'hh小时mm分钟ss秒';

  if (time) {
    if (time >= 60 * 60 * 1000) {
      hour = Math.trunc(time / (60 * 60 * 1000));
    }

    if (time >= 60 * 1000) {
      minute = Math.trunc((time - hour * 60 * 60 * 1000) / (60 * 1000));
    }

    second = Math.trunc(
      (time - hour * (60 * 60 * 1000) - minute * 60 * 1000) / 1000,
    );
  }

  return timeStr
    .replace('hh', hour.toString())
    .replace('mm', minute.toString())
    .replace('ss', second.toString());
};

export const ArrayToTree = (list: any[]): any[] => {
  const treeList: any[] = [];
  // 所有项都使用对象存储起来
  const map = {};

  // 建立一个映射关系：通过id快速找到对应的元素
  list.forEach((item) => {
    if (!item.children) {
      item.children = [];
    }
    map[item.id] = item;
  });

  list.forEach((item) => {
    // 对于每一个元素来说，先找它的上级
    //    如果能找到，说明它有上级，则要把它添加到上级的children中去
    //    如果找不到，说明它没有上级，直接添加到 treeList
    const parent = map[item.parentId];
    // 如果存在则表示item不是最顶层的数据
    if (parent) {
      parent.children.push(item);
    } else {
      // 如果不存在 则是顶层数据
      treeList.push(item);
    }
  });
  // 返回出去
  return treeList;
};

export const EventEmitter = {
  list: {},
  subscribe: function(events: string[], fn: Function) {
    const list = this.list
    events.forEach(event => {
      (list[event] || (list[event] = [])).push(fn)
    })
    return this
  },
  emit: function(events:string, data?: any) {
    const list = this.list
    const fns: Function[] = list[events] ? [...list[events]] : []

    if (!fns.length) return false;

    fns.forEach(fn => {
      fn(data)
    })

    return this
  },
  unSubscribe: function(events:string[], fn: Function) {
    const list = this.list
    events.forEach(key => {
      if (key in list) {
        const fns = list[key]
        for (let i = 0; i < fns.length; i++) {
          if (fns[i] === fn) {
            fns.splice(i, 1)
            break;
          }
        }
      }
    })
    return this
  }
}
