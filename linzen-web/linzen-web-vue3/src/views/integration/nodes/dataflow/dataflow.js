export default {
  // 节点类型
  NodeType: {
    INPUT: 'INPUT', // 数据输入
    FILTER: 'FILTER', // 数据过滤
    JOIN: 'JOIN', // 数据连接
    UNION: 'UNION', // 数据合并
    AGG: 'AGG', // 数据聚合
    OUTPUT: 'OUTPUT', // 数据输出
  },

  // 元素校验状态
  CellStatus: {
    DEFAULT: 'default',
    SUCCESS: 'success',
    ERROR: 'error',
  },

  // 不同节点类型的icon
  NODE_TYPE_LOGO: {
    INPUT:
      'https://mdn.alipayobjects.com/huamei_f4t1bn/afts/img/A*RXnuTpQ22xkAAAAAAAAAAAAADtOHAQ/original', // 数据输入
    FILTER:
      'https://mdn.alipayobjects.com/huamei_f4t1bn/afts/img/A*ZJ6qToit8P4AAAAAAAAAAAAADtOHAQ/original', // 数据筛选
    JOIN: 'https://mdn.alipayobjects.com/huamei_f4t1bn/afts/img/A*EHqyQoDeBvIAAAAAAAAAAAAADtOHAQ/original', // 数据连接
    UNION:
      'https://mdn.alipayobjects.com/huamei_f4t1bn/afts/img/A*k4eyRaXv8gsAAAAAAAAAAAAADtOHAQ/original', // 数据合并
    AGG: 'https://mdn.alipayobjects.com/huamei_f4t1bn/afts/img/A*TKG8R6nfYiAAAAAAAAAAAAAADtOHAQ/original', // 数据聚合
    OUTPUT:
      'https://mdn.alipayobjects.com/huamei_f4t1bn/afts/img/A*zUgORbGg1HIAAAAAAAAAAAAADtOHAQ/original', // 数据输出
  },

  // 加工类型列表
  PROCESSING_TYPE_LIST: [
    {
      type: 'FILTER',
      name: '数据筛选',
    },
    {
      type: 'JOIN',
      name: '数据连接',
    },
    {
      type: 'UNION',
      name: '数据合并',
    },
    {
      type: 'AGG',
      name: '数据聚合',
    },

    {
      type: 'OUTPUT',
      name: '数据输出',
    },
  ],
};