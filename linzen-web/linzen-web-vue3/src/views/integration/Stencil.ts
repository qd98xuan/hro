import { Stencil } from '@antv/x6-plugin-stencil';

export function getStencil(graphTarget) {
  let stencil = new Stencil({
    title: '流程图',
    target: graphTarget,
    stencilGraphWidth: 200,
    stencilGraphHeight: 180,
    collapsable: true,
    groups: [
      {
        title: '基础流程图',
        name: 'group1',
      },
      {
        title: '系统设计图',
        name: 'group2',
        graphHeight: 250,
        layoutOptions: {
          rowHeight: 70,
        },
      },
    ],
    layoutOptions: {
      columns: 2,
      columnWidth: 80,
      rowHeight: 55,
    },
  });
  return stencil;
}
