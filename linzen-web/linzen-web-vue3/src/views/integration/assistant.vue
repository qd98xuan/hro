<template>
  <div class="linzen-content-wrapper bg-white">
    <div class="linzen-content-wrapper-center">
      <div class="linzen-content-wrapper-search-box">
        <a-row>
          <!-- 具体表单 -->
          <a-col style="width: 250px; padding-right: 10px" class="ant-col-item">
            <div class="app-stencil" ref="dndContainerRef">
              <a-collapse>
                <a-collapse-panel key="1">
                  <template #header> </template>

                  <div class="data-processing-dag-node">
                    <div class="main-area" @mousedown="handleMouseDown($event)">
                      <div class="main-info">
                        <i class="add-icon icon-linzen icon-linzen-launchFlow"></i>
                        <a-tooltip placement="top" :open-delay="800">
                          <div class="ellipsis-row node-name">数据流转</div>
                        </a-tooltip>
                      </div>

                      <div class="status-action">
                        <a-tooltip>
                          <i class="add-icon icon-linzen icon-linzen-launchFlow"></i>
                        </a-tooltip>
                      </div>
                    </div>
                  </div>
                </a-collapse-panel>
                <a-collapse-panel key="2">
                  <template #header> </template>
                  <p>2</p>
                </a-collapse-panel>
                <a-collapse-panel key="3">
                  <template #header> </template>
                  <p>3</p>
                </a-collapse-panel>
              </a-collapse>
            </div>
          </a-col>
          <!-- 具体表单 -->
          <a-col :span="20" class="ant-col-item">
            <div id="container" ref="containerRef"></div>
            <TeleportContainer />
          </a-col>
        </a-row>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import { ref, nextTick, defineExpose, reactive, onMounted } from 'vue';
  import { Graph, Shape, Node, Path, Cell } from '@antv/x6';
  import { Dnd } from '@antv/x6-plugin-dnd';
  import { Snapline } from '@antv/x6-plugin-snapline';
  import { buildBitUUID } from '/@/utils/uuid';

  import NodeItem from './components/dataflow/index.vue';
  import { register, getTeleport } from '@antv/x6-vue-shape';
  import { Stencil } from '@antv/x6-plugin-stencil';

  register({
    shape: 'node-item',
    width: 100,
    height: 100,
    component: NodeItem,
  });
  const TeleportContainer = getTeleport(); // 自定义节点优化

  const dndContainerRef = ref<any>(null);
  const containerRef = ref<any>(null);
  const stencilContainerRef = ref<any>(null);
  const dndContainer1Ref = ref<any>(null);
    

  let graph: Graph;
  let dnd: Dnd;

  // 注册插件
  function registerGraphPlugins(graph) {
    graph.use(
      new Snapline({
        enabled: true,
      }),
    );
  }

  function init() {
    const container = containerRef.value; // 绑定画布
    graph = new Graph({
      grid: {
        size: 5,// 
        visible: true,
        type: 'mesh',
        args: {
          color: '#ddd', // 网格线颜色
          thickness: 1, // 网格线宽度
        },
      },
      container: container,
      width: 1000, // container.offsetWidth, // 画布宽
      height: 1200, // container.offsetHeight, // 画布高
      scaling: { min: 0.01, max: 16 },
      background: {
        color: '#F2F7FA',
      },
      autoResize: true,
    });

    registerGraphPlugins(graph);

    initDnd(graph);
  }


  function initDnd(graph) {
    const dndContainer = dndContainerRef.value;
    // 指定画布拖拽区
    dnd = new Dnd({
      target: graph,// 目标画布
      scaled: false,
      dndContainer: dndContainer,// 如果设置 dndContainer，在 dndContainer 上放开鼠标不会放置节点，常用于 dnd 容器处于画布上面的场景
      // 拖拽开始时，获取被拖拽的节点，默认克隆 dnd.start 传入的节点
      getDragNode: node => node.clone({ keepId: true }),
      // 拖拽结束时，获取放置到目标画布的节点，默认克隆被拖拽的节点。
      getDropNode: node => {
        console.info('拖拽生成节点', node, node.getData());
        return graph.createNode(formatData(node.getData()));
      },
      // 拖拽结束时，验证节点是否可以放置到目标画布中。
      validateNode: node => {
        console.info('拖拽结束时', node, node.getData());
        return true;
      },
      // 自定义拖拽画布容器（不知道干什么用的）。
      draggingContainer: document.body
    });
  }

  // #region 拖拽生成节点
  function handleMouseDown(event) {
    console.info('拖拽生成节点', event);
    const uuid = buildBitUUID();
    const nodeVO = {
      id: 'node_id_' + uuid,
      tools: 'button-remove'
    };
    const node = graph.createNode(formatData(nodeVO));
    dnd.start(node, event);
  }

  const COMMON_GROUP_OPTION = {
    port: {
      markup: [
        {
          tagName: 'rect', //矩形
          selector: 'portBody',
        },
      ],
      position: {
        name: 'absolute',
        args: { x: 0, y: 0 }, //相对节点绝对定位，在formatData有重置位置
      },
      attrs: {
        //样式
        portBody: {
          width: 6,
          height: 6,
          strokeWidth: 2,
          stroke: '#6A93FF',
          fill: '#fff',
          magnet: true,
        },
      },
      zIndex: 3,
    },
  };

  function formatData(params: any) {
    console.info("params", params.x);
    const portLength = params?.ports?.length || 1;
    const uuid = buildBitUUID();
    const portItems = [
      {
        id: 'port_id_' + uuid, // 连接桩唯一 ID，默认自动生成。
        group: 'port', // 分组名称，指定分组后将继承分组中的连接桩选项。
        name: 'item.name',
        args: {
          x: 170,
          y: 22,
          angle: 45,
        }, // 为群组中指定的连接桩布局算法提供参数, 我们不能为单个连接桩指定布局算法，但可以为群组中指定的布局算法提供不同的参数。
      },
    ];
    return {
      id: params.id,
      shape: 'node-item',
      x: params.x, // 放置到 画布上的节点 X轴位置
      y: params.y, // 放置到 画布上的节点 Y轴位置
      width: 200, // 放置到 画布上的节点宽度
      height: 50, //节点高度
      data: params, //用来自定义节点展示节点信息，及节点连接桩信息
      ports: {
        groups: COMMON_GROUP_OPTION, //连接桩样式
        items: [...portItems],
      },
    };
  }

  onMounted(() => {
    console.info('init');
    init();
  });

    // 注册自定义节点 图标+标题+描述
    Shape.HTML.register({
    shape: 'cu-data-node',
    width: 'auto',
    height: 104,
    effect: ['data'],
    html(cell) {
      // 获取节点传递过来的数据
      console.info("获取节点传递过来的数据", cell.getData());
      const { label, img, desc } = cell.getData();
      // 创建自定义的节点容器
      const container = document.createElement('div');
      return container;
    }
  });
  

  // "@antv/x6-plugin-clipboard": "^2.0.0", // 如果使用剪切板功能，需要安装此包
  // "@antv/x6-plugin-history": "^2.0.0", // 如果使用撤销重做功能，需要安装此包
  // "@antv/x6-plugin-keyboard": "^2.0.0", // 如果使用快捷键功能，需要安装此包
  // "@antv/x6-plugin-minimap": "^2.0.0", // 如果使用小地图功能，需要安装此包
  // "@antv/x6-plugin-scroller": "^2.0.0", // 如果使用滚动画布功能，需要安装此包
  // "@antv/x6-plugin-selection": "^2.0.0", // 如果使用框选功能，需要安装此包
  // "@antv/x6-plugin-snapline": "^2.0.0", // 如果使用对齐线功能，需要安装此包
  // "@antv/x6-plugin-dnd": "^2.0.0", // 如果使用 dnd 功能，需要安装此包
  // "@antv/x6-plugin-stencil": "^2.0.0", // 如果使用 stencil 功能，需要安装此包
  // "@antv/x6-plugin-transform": "^2.0.0", // 如果使用图形变换功能，需要安装此包
  // "@antv/x6-plugin-export": "^2.0.0", // 如果使用图片导出功能，需要安装此包
  // "@antv/x6-react-components": "^2.0.0", // 如果使用配套 UI 组件，需要安装此包
  // "@antv/x6-react-shape": "^2.0.0", // 如果使用 react 渲染功能，需要安装此包
  // "@antv/x6-vue-shape": "^2.0.0" // 如果使用 vue 渲染功能，需要安装此包
</script>

<style lang="less" scoped>
  @import './base.css';
</style>
