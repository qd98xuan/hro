import linzen from '@/utils/linzen'
// 代码生成器数据匹配
export function dynamicText(value, options) {
	if (!value) return ''
	if (Array.isArray(value)) {
		if (!options || !Array.isArray(options)) return value.join()
		let textList = []
		for (let i = 0; i < value.length; i++) {
			let item = options.filter(o => o.id == value[i])[0]
			if (!item || !item.fullName) {
				textList.push(value[i])
			} else {
				textList.push(item.fullName)
			}
		}
		return textList.join()
	}
	if (!options || !Array.isArray(options)) return value
	let item = options.filter(o => o.id == value)[0]
	if (!item || !item.fullName) return value
	return item.fullName
}
export function treeToArray(treeData, type) {
	type = type || ''
	let list = []
	const loop = (treeData) => {
		for (let i = 0; i < treeData.length; i++) {
			const item = treeData[i]
			if (!type || item.type === type) list.push(item)
			if (item.children && Array.isArray(item.children)) {
				loop(item.children)
			}
		}
	}
	loop(treeData)
	return list
}
export function dynamicTreeText(value, options) {
	if (!value) return ''

	function transfer(data, partition) {
		let textList = []

		function loop(data, id) {
			for (let i = 0; i < data.length; i++) {
				if (data[i].id === id) {
					textList.push(data[i].fullName)
					break
				}
				if (data[i].children) loop(data[i].children, id)
			}
		}
		for (let i = 0; i < data.length; i++) {
			if (Array.isArray(data[i])) {
				textList.push(transfer(data[i], "/"))
			} else {
				loop(options, data[i])
			}
		}
		return textList.join(partition)
	}
	if (!options || !Array.isArray(options)) {
		if (Array.isArray(value)) {
			return value.join()
		} else {
			return value
		}
	}
	if (Array.isArray(value)) {
		let text = transfer(value)
		return text
	} else {
		if (!options || !Array.isArray(options)) return value
		let list = value.split()
		let text = transfer(list)
		return text
	}
}
export function toDateText(dateTime) {
	return linzen.toDateText(dateTime)
}