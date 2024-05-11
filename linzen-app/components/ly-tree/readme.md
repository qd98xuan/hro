
**pages.json配置（非常重要！！！小程序和APP端只显示一个层级、无法展开就是因为没有配置这个属性！！请使用复制粘贴！不要自己写错单词！）：**

提示：为了集中精力、减少包体积，2020年4月发布的HBuilderX 2.7版本起，及对应的App SDK包，将不再提供“自定义组件模式”。（uni小程序sdk本身也仅支持v3）。
经验证，如果项目只编译到APP端，这里可以不配置，但配置了也不回有影响，小程序端还是需要配置。

综上：这里的配置建议总是加上，避免小程序端无法展开节点
```javascript
"globalStyle": {
		...
		"usingComponents": {
			"ly-tree-node": "/components/ly-tree/ly-tree-node"
		}
	}
```



**关于render-content：**

·因为uni非H5端目前不支持render函数，插件依赖于uni,目前这个需求还无法实现，如果有特殊的需求需要自定义节点样式，可以自行修改ly-tree-node哦~；


**属性说明：**

|属性名			|类型			|默认值	|说明															|
|-				|-				|-		|-																|
|nodeKey	|String			|-|必填，每个树节点用来作为唯一标识的属性，整棵树应该是唯一的										|
|treeData		|Array			|-		|非懒加载时的展示数据									|
|emptyText		|String			|暂无数据		|内容为空的时候展示的文本														|
|renderAfterExpand		|Boolean			|true	|是否在第一次展开某个树节点后才渲染其子节点	|
|checkStrictly		|Boolean			|false	|在显示复选框的情况下，是否严格的遵循父子不互相关联的做法，默认为 false，如果设置了checkOnlyLeaf为true，这里也将变为true	|
|defaultExpandAll		|Boolean			|false	|是否默认展开所有节点	|
|expandOnClickNode		|Boolean			|true	|是否在点击节点的时候展开或者收缩节点， 默认值为 true，如果为 false，则只有点箭头图标的时候才会展开或者收缩节点	|
|checkOnClickNode		|Boolean			|false	|是否在点击节点的时候选中节点，默认值为 false，即只有在点击复选框时才会选中节点	|
|autoExpandParent		|Boolean			|true	|是否在第一次展开某个树节点后才渲染其子节点	|
|defaultCheckedKeys		|Array			|-	|默认勾选的节点的 key 的数组	|
|defaultExpandedKeys		|Array			|-	|默认展开的节点的 key 的数组	|
|currentNodeKey		|String, Number			|-	|当前选中的节点	|
|showCheckbox		|Boolean			|false	|节点是否可被选择	|
|showRadio		|Boolean			|false	|节点是否可被单选	|
|props		|Object			|见下文	|数据中对应的属性名	|
|lazy		|Boolean			|false	|是否懒加载子节点，需与 load 方法结合使用	|
|highlightCurrent		|Boolean	|false		|是否高亮当前选中节点，默认值是 false		|
|load		|Function	|-		|加载子树数据的方法，仅当 lazy 属性为true 时生效		|
|filterNodeMethod		|Function	|-		|对树节点进行筛选时执行的方法，返回 true 表示这个节点可以显示，返回 false 则表示这个节点会被隐藏		|
|accordion		|Boolean	|false		|是否每次只打开一个同级树节点展开		|
|indent		|Number	|18		|相邻级节点间的水平缩进，单位为像素		|
|iconClass		|String	|-		|自定义树节点的展开图标，图标class		|
|ready【新】		|Boolean			|true		|自主控制loading加载，避免数据还没获取到的空档出现“暂无数据”字样	|
|showNodeIcon【新】		|Boolean	|false		|是否显示节点图标，如果配置为true,需要配置props中对应的图标属性名称		|
|expandOnCheckNode【新】		|Boolean			|true	|节点被选中时展开节点	|
|toggleExpendAll【新】		|Boolean			|false	|切换全部展开、全部折叠	|
|childVisibleForFilterNode【新】		|Boolean	|false		|搜索时是否展示匹配项的所有子节点		|
|checkOnlyLeaf【新】   |Boolean         |false   |是否最后一层叶子节点才显示单选/多选框|
|expandCurrentNodeParent【新】   |Boolean         |false   |是否展开当前节点的父节点，设置当前节点时生效|
|isInjectParentInNode【新】   |Boolean         |false   |是否向node示例中挂载parent（父节点实例），如果配置为true，那么在点击或者展开节点等事件中能直接获取到parent;如果数据量较大，建议不要在node节点中添加parent属性，会造成性能损耗|
|defaultNodeIcon【新】   |String         |-   |当节点图标显示出错时，显示的默认图标|


**基本用法：**

·请注意node-key必须配置，指数据中每个节点的唯一标志,打个比方：

	如果数据中以id数据项当做节点的唯一标志，node-key就是"id";
	
	如果数据中以guid数据项当做节点的唯一标志，node-key就是"guid";
	
	如果数据中以personId数据项当做节点的唯一标志，node-key就是"personId"...

```html
<template>
	<ly-tree :tree-data="treeData" 
		:ready="ready"
		node-key="id" 
		@node-expand="handleNodeExpand" 
		@node-click="handleNodeClick">
		</ly-tree>
</template>
```
```javascript
import LyTree from '@/components/ly-tree/ly-tree.vue'
export default {
	components: {
		LyTree
	},
	data() {
		return {
			ready: false, // 这里用于自主控制loading加载状态，避免异步正在加载数据的空档显示“暂无数据”
			treeData: []
		};
	},
	onLoad() {
		// 模拟异步请求
		setTimeout(() => {
			this.treeData = [{
				id: 1,
				label: '一级 1',
				children: [{
					id: 11,
					label: '二级 1-1',
					children: [{
						id: 111,
						label: '三级 1-1-1'
					}]
				}]
			}, {
				id: 2,
				label: '一级 2',
				children: [{
					id: 21,
					label: '二级 2-1',
					children: [{
						id: 211,
						label: '三级 2-1-1'
					}]
				}, {
					id: 22,
					label: '二级 2-2',
					children: [{
						id: 221,
						label: '三级 2-2-1'
					}]
				}]
			}, {
				id: 3,
				label: '一级 3',
				children: [{
					id: 31,
					label: '二级 3-1',
					children: [{
						id: 311,
						label: '三级 3-1-1'
					}]
				}, {
					id: 32,
					label: '二级 3-2',
					children: [{
						id: 321,
						label: '三级 3-2-1'
					}]
				}]
			}];
			
			this.ready = true;
		}, 2000);
	},
	methods: {
		// uni-app中emit触发的方法只能接受一个参数，所以会回传一个对象，打印对象即可见到其中的内容
		handleNodeClick(obj) {
			console.log('handleNodeClick', JSON.stringify(obj));
		},
		handleNodeExpand(obj) {
			console.log('handleNodeExpand', JSON.stringify(obj));
		}
	}
};
```


**这个时候可能有人提出：如果我的数据结构不是{id: 11, label: 'xx', children: []}，而是{personId: 11, name: 'xx', childs: []},该怎么办？？以下props属性配置**

**props 属性说明：**

|属性名			|类型			|默认值	|说明															|
|-				|-				|-		|-																|
|label	|string, function(data, node)			|-|指定节点标签为节点对象的某个属性值										|
|icon[new]		|String			|-	|指定节点图标为节点对象的某个属性值	|
|children		|string			|-		|指定子树为节点对象的某个属性值									|
|disabled		|String			|boolean, function(data, node)		|指定节点选择框是否禁用为节点对象的某个属性值														|
|isLeaf		|boolean, function(data, node)			|true	|指定节点是否为叶子节点，仅在指定了 lazy 属性的情况下生效	|

```html
<template>
	<ly-tree :tree-data="treeData" 
		:props="props"
		node-key="personId" 
		@node-expand="handleNodeExpand" 
		@node-click="handleNodeClick">
		</ly-tree>
</template>
```
```javascript
import LyTree from '@/components/ly-tree/ly-tree.vue'
export default {
	components: {
		LyTree
	},
	data() {
		return {
			ready: false,
			// 如果想自定义函数渲染节点名称等，需要将props设置成返回配置对象的函数，否则如果props是一个对象（uni会将对象中的函数过滤掉），对象中的函数属性会被过滤掉
			props: function() {
				return {
					// 这里的label就可以使用函数进行自定义的渲染了
					label(data, node) {
						return '节点' + node.data.personName;
					},
					// label: 'personName', // 指把数据中的‘personName’当做label也就是节点名称
					children: 'childs' // 指把数据中的‘childs’当做children当做子节点数据
				}
			},
			// 如果只是单纯的从数据中取某一个字段作为对应属性，props可以直接设置为对象
			// props: {
			// 	label: 'personName', // 指把数据中的‘personName’当做label也就是节点名称
			// 	children: 'childs' // 指把数据中的‘childs’当做children当做子节点数据
			// },
			treeData: [{
				personId: 1,
				personName: '一级 1',
				childs: [{
					personId: 11,
					personName: '二级 1-1',
					childs: [{
						personId: 111,
						personName: '三级 1-1-1'
					}]
				}]
			}, {
				personId: 2,
				personName: '一级 2',
				childs: [{
					personId: 21,
					personName: '二级 2-1',
					childs: [{
						personId: 211,
						personName: '三级 2-1-1'
					}]
				}, {
					personId: 22,
					personName: '二级 2-2',
					childs: [{
						personId: 221,
						personName: '三级 2-2-1'
					}]
				}]
			}, {
				personId: 3,
				personName: '一级 3',
				childs: [{
					personId: 31,
					personName: '二级 3-1',
					childs: [{
						personId: 311,
						personName: '三级 3-1-1'
					}]
				}, {
					personId: 32,
					personName: '二级 3-2',
					childs: [{
						personId: 321,
						personName: '三级 3-2-1'
					}]
				}]
			}]
		};
	},
	methods: {
		// uni-app中emit触发的方法只能接受一个参数，所以会回传一个对象，打印对象即可见到其中的内容
		handleNodeClick(obj) {
			console.log('handleNodeClick', JSON.stringify(obj));
		},
		handleNodeExpand(obj) {
			console.log('handleNodeExpand', JSON.stringify(obj));
		}
	}
};
```



**懒加载：**

tips:为了确保页面加载完成后才去调用load方法，this指向正确,使用v-if懒加载树组件，在页面的onLoad之后调用

```html
<template>
	<ly-tree v-if="isReady" :props="props" node-key="name" :load="loadNode" lazy>
	</ly-tree>
</template>
```
```javascript
import LyTree from '@/components/ly-tree/ly-tree.vue'
	
var _self;
export default {
	components: {
		LyTree
	},
	data() {
		return {
			//为了确保页面加载完成后才去调用load方法，this指向正确
			isReady: false,
			props: {
				label: 'name',
				children: 'zones',
				isLeaf: 'leaf'
			}
		};
	},
	
	onLoad() {
		_self = this;
		this.isReady = true;
	},
	
	methods: {
		// 因为这个函数是在Vue实例以外的地方调用，如果函数内部需要用到this，需要改成_self
		loadNode(node, resolve) {
			// _self.xxx; 这里用_self而不是this
			
			if (node.level === 0) {
				setTimeout(() => {
					resolve([{
						name: 'region'
					}]);
				}, 1000);
			} else {
				if (node.level > 1) return resolve([]);
				
				setTimeout(() => {
					const data = [{
						name: 'leaf',
						leaf: true
					}, {
						name: 'zone'
					}];
				
					resolve(data);
				}, 500);
			}
		}
	}
};
```




**方法及事件：**

以下是目前树支持的一些方法，更多详情请参见elementUI说明：https://element.eleme.cn/#/zh-CN/component/tree

tips:为树组件绑定ref属性，如ref="tree"，则调用方式为this.$refs.tree.getCheckedNodes(),详情见工程示例“树节点的选择”

```javascript
/*
 * @description 对树节点进行筛选操作
 * @method filter
 * @param {all} value 在 filter-node-method 中作为第一个参数
 * @param {Object} data 搜索指定节点的节点数据，不传代表搜索所有节点，假如要搜索A节点下面的数据，那么nodeData代表treeData中A节点的数据
*/
filter(value, data) {
	if (!this.filterNodeMethod) throw new Error('[Tree] filterNodeMethod is required when filter');
	this.store.filter(value, data);
},

/*
 * @description 获取节点的唯一标识符
 * @method getNodeKey
 * @param {String, Number} nodeId
 * @return {String, Number} 匹配到的数据中的某一项数据
*/
getNodeKey(nodeId) {
	let node = this.store.root.getChildNodes([nodeId])[0];
	return getNodeKey(this.nodeKey, node.data);
},

/*
* @description 获取节点路径
* @method getNodePath
* @param {Object} data 节点数据
* @return {Array} 路径数组
*/
getNodePath(data) {
	return this.store.getNodePath(data);
},

/*
 * @description 若节点可被选择（即 show-checkbox 为 true），则返回目前被选中的节点所组成的数组
 * @method getCheckedNodes
 * @param {Boolean} leafOnly 是否只是叶子节点，默认false
 * @param {Boolean} includeHalfChecked 是否包含半选节点，默认false
 * @return {Array} 目前被选中的节点所组成的数组
*/
getCheckedNodes(leafOnly, includeHalfChecked) {
	return this.store.getCheckedNodes(leafOnly, includeHalfChecked);
},

/*
 * @description 若节点可被选择（即 show-checkbox 为 true），则返回目前被选中的节点的 key 所组成的数组
 * @method getCheckedKeys
 * @param {Boolean} leafOnly 是否只是叶子节点，默认false,若为 true 则仅返回被选中的叶子节点的 keys
 * @param {Boolean} includeHalfChecked 是否返回indeterminate为true的节点，默认false
 * @return {Array} 目前被选中的节点所组成的数组
*/
getCheckedKeys(leafOnly, includeHalfChecked) {
	return this.store.getCheckedKeys(leafOnly, includeHalfChecked);
},

/*
 * @description 获取当前被选中节点的 data，若没有节点被选中则返回 null
 * @method getCurrentNode
 * @return {Object} 当前被选中节点的 data，若没有节点被选中则返回 null
*/
getCurrentNode() {
	const currentNode = this.store.getCurrentNode();
	return currentNode ? currentNode.data : null;
},

/*
 * @description 获取当前被选中节点的 key，若没有节点被选中则返回 null
 * @method getCurrentKey
 * @return {all} 当前被选中节点的 key， 若没有节点被选中则返回 null
*/
getCurrentKey() {
	const currentNode = this.getCurrentNode();
	return currentNode ? currentNode[this.nodeKey] : null;
},

/*
 * @description 设置全选/取消全选
 * @method setCheckAll
 * @param {Boolean} isCheckAll 选中状态,默认为true
*/
setCheckAll(isCheckAll = true) {
	if (this.showRadio) throw new Error('You set the "show-radio" property, so you cannot select all nodes');
	
	if (!this.showCheckbox) console.warn('You have not set the property "show-checkbox". Please check your settings');
	
	this.store.setCheckAll(isCheckAll);
},

/*
 * @description 设置目前勾选的节点
 * @method setCheckedNodes
 * @param {Array} nodes 接收勾选节点数据的数组
 * @param {Boolean} leafOnly 是否只是叶子节点, 若为 true 则仅设置叶子节点的选中状态，默认值为 false
*/
setCheckedNodes(nodes, leafOnly) {
	this.store.setCheckedNodes(nodes, leafOnly);
},

/*
 * @description 通过 keys 设置目前勾选的节点
 * @method setCheckedKeys
 * @param {Array} keys 勾选节点的 key 的数组 
 * @param {Boolean} leafOnly 是否只是叶子节点, 若为 true 则仅设置叶子节点的选中状态，默认值为 false
*/
setCheckedKeys(keys, leafOnly) {
	if (!this.nodeKey) throw new Error('[Tree] nodeKey is required in setCheckedKeys');
	this.store.setCheckedKeys(keys, leafOnly);
},

/*
 * @description 通过 key / data 设置某个节点的勾选状态
 * @method setChecked
 * @param {all} data 勾选节点的 key 或者 data 
 * @param {Boolean} checked 节点是否选中
 * @param {Boolean} deep 是否设置子节点 ，默认为 false
*/
setChecked(data, checked, deep) {
	this.store.setChecked(data, checked, deep);
},

/*
 * @description 若节点可被选择（即 show-checkbox 为 true），则返回目前半选中的节点所组成的数组
 * @method getHalfCheckedNodes
 * @return {Array} 目前半选中的节点所组成的数组
*/
getHalfCheckedNodes() {
	return this.store.getHalfCheckedNodes();
},

/*
 * @description 若节点可被选择（即 show-checkbox 为 true），则返回目前半选中的节点的 key 所组成的数组
 * @method getHalfCheckedKeys
 * @return {Array} 目前半选中的节点的 key 所组成的数组
*/
getHalfCheckedKeys() {
	return this.store.getHalfCheckedKeys();
},

/*
 * @description 通过 node 设置某个节点的当前选中状态
 * @method setCurrentNode
 * @param {Object} node 待被选节点的 node
*/
setCurrentNode(node) {
	if (!this.nodeKey) throw new Error('[Tree] nodeKey is required in setCurrentNode');
	this.store.setUserCurrentNode(node);
},

/*
 * @description 通过 key 设置某个节点的当前选中状态
 * @method setCurrentKey
 * @param {all} key 待被选节点的 key，若为 null 则取消当前高亮的节点
*/
setCurrentKey(key) {
	if (!this.nodeKey) throw new Error('[Tree] nodeKey is required in setCurrentKey');
	this.store.setCurrentNodeKey(key);
},

/*
 * @description 根据 data 或者 key 拿到 Tree 组件中的 node
 * @method getNode
 * @param {all} data 要获得 node 的 key 或者 data
*/
getNode(data) {
	return this.store.getNode(data);
},

/*
 * @description 删除 Tree 中的一个节点
 * @method remove
 * @param {all} data 要删除的节点的 data 或者 node
*/
remove(data) {
	this.store.remove(data);
},

/*
 * @description 为 Tree 中的一个节点追加一个子节点
 * @method append
 * @param {Object} data 要追加的子节点的 data 
 * @param {Object} parentNode 子节点的 parent 的 data、key 或者 node
*/
append(data, parentNode) {
	this.store.append(data, parentNode);
},

/*
 * @description 为 Tree 的一个节点的前面增加一个节点
 * @method insertBefore
 * @param {Object} data 要增加的节点的 data 
 * @param {all} refNode 要增加的节点的后一个节点的 data、key 或者 node
*/
insertBefore(data, refNode) {
	this.store.insertBefore(data, refNode);
},

/*
 * @description 为 Tree 的一个节点的后面增加一个节点
 * @method insertAfter
 * @param {Object} data 要增加的节点的 data 
 * @param {all} refNode 要增加的节点的前一个节点的 data、key 或者 node
*/
insertAfter(data, refNode) {
	this.store.insertAfter(data, refNode);
},

/*
 * @description 通过 keys 设置节点子元素
 * @method updateKeyChildren
 * @param {String, Number} key 节点 key 
 * @param {Object} data 节点数据的数组
*/
updateKeyChildren(key, data) {
	if (!this.nodeKey) throw new Error('[Tree] nodeKey is required in updateKeyChild');
	this.store.updateChildren(key, data);
}
```