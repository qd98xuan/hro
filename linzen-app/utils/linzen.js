import request from '@/utils/request'
import define from '@/utils/define'
import CryptoJS from 'crypto-js'

const linzen = {
	goBack() {
		uni.navigateBack()
	},
	handelFormat(format) {
		let formatObj = {
			'yyyy': 'yyyy',
			'yyyy-MM': 'yyyy-MM',
			'yyyy-MM-dd': 'yyyy-MM-dd',
			'yyyy-MM-dd HH:mm': 'yyyy-MM-dd HH:mm',
			'yyyy-MM-dd HH:mm:ss': 'yyyy-MM-dd HH:mm:ss',
			'HH:mm:ss': 'HH:mm:ss',
			"HH:mm": "HH:mm",
			'YYYY': 'yyyy',
			'YYYY-MM': 'yyyy-MM',
			'YYYY-MM-DD': 'yyyy-MM-dd',
			'YYYY-MM-DD HH:mm': 'yyyy-MM-dd HH:mm',
			'YYYY-MM-DD HH:mm:ss': 'yyyy-MM-dd HH:mm:ss',
		}
		return formatObj[format]
	},
	treeToArray(treeData, type) {
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
	},
	toDate(v, format) {
		format = format ? format : "yyyy-MM-dd HH:mm"
		if (!v) return "";
		var d = v;
		if (typeof v === 'string') {
			if (v.indexOf("/Date(") > -1)
				d = new Date(parseInt(v.replace("/Date(", "").replace(")/", ""), 10));
			else
				d = new Date(Date.parse(v.replace(/-/g, "/").replace("T", " ").split(".")[0]));
		} else {
			d = new Date(v)
		}
		var o = {
			"M+": d.getMonth() + 1,
			"d+": d.getDate(),
			"h+": d.getHours(),
			"H+": d.getHours(),
			"m+": d.getMinutes(),
			"s+": d.getSeconds(),
			"q+": Math.floor((d.getMonth() + 3) / 3),
			"S": d.getMilliseconds()
		};
		if (/(y+)/.test(format)) {
			format = format.replace(RegExp.$1, (d.getFullYear() + "").substr(4 - RegExp.$1.length));
		}
		for (var k in o) {
			if (new RegExp("(" + k + ")").test(format)) {
				format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k])
					.length));
			}
		}
		return format;
	},
	toFileSize(size) {
		if (size == null || size == "") {
			return "";
		}
		if (size < 1024.00)
			return linzen.toDecimal(size) + " 字节";
		else if (size >= 1024.00 && size < 1048576)
			return linzen.toDecimal(size / 1024.00) + " KB";
		else if (size >= 1048576 && size < 1073741824)
			return linzen.toDecimal(size / 1024.00 / 1024.00) + " MB";
		else if (size >= 1073741824)
			return linzen.toDecimal(size / 1024.00 / 1024.00 / 1024.00) + " GB";
	},
	toDecimal(num) {
		if (num == null) {
			num = "0";
		}
		num = num.toString().replace(/\$|\,/g, '');
		if (isNaN(num))
			num = "0";
		var sign = (num == (num = Math.abs(num)));
		num = Math.floor(num * 100 + 0.50000000001);
		var cents = num % 100;
		num = Math.floor(num / 100).toString();
		if (cents < 10)
			cents = "0" + cents;
		for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
			num = num.substring(0, num.length - (4 * i + 3)) + '' +
			num.substring(num.length - (4 * i + 3));
		return (((sign) ? '' : '-') + num + '.' + cents);
	},
	getScriptFunc(str) {
		// #ifndef MP
		let func = null
		try {
			func = eval(str)
			if (Object.prototype.toString.call(func) !== '[object Function]') return false;
			return func
		} catch (error) {
			console.log(error);
			return false
		}
		// #endif
		// #ifdef MP
		return false
		// #endif
	},
	interfaceDataHandler(data) {
		if (!data.dataProcessing) return data.data
		const dataHandler = this.getScriptFunc(data.dataProcessing)
		if (!dataHandler) return data.data
		return dataHandler(data.data)
	},
	toDateText(dateTimeStamp) {
		if (!dateTimeStamp) return ''
		let result = ''
		let minute = 1000 * 60; //把分，时，天，周，半个月，一个月用毫秒表示
		let hour = minute * 60;
		let day = hour * 24;
		let week = day * 7;
		let halfamonth = day * 15;
		let month = day * 30;
		let now = new Date().getTime(); //获取当前时间毫秒
		let diffValue = now - dateTimeStamp; //时间差
		if (diffValue < 0) return "刚刚"
		let minC = diffValue / minute; //计算时间差的分，时，天，周，月
		let hourC = diffValue / hour;
		let dayC = diffValue / day;
		let weekC = diffValue / week;
		let monthC = diffValue / month;
		if (monthC >= 1 && monthC <= 3) {
			result = " " + parseInt(monthC) + "月前"
		} else if (weekC >= 1 && weekC <= 3) {
			result = " " + parseInt(weekC) + "周前"
		} else if (dayC >= 1 && dayC <= 6) {
			result = " " + parseInt(dayC) + "天前"
		} else if (hourC >= 1 && hourC <= 23) {
			result = " " + parseInt(hourC) + "小时前"
		} else if (minC >= 1 && minC <= 59) {
			result = " " + parseInt(minC) + "分钟前"
		} else if (diffValue >= 0 && diffValue <= minute) {
			result = "刚刚"
		} else {
			let datetime = new Date();
			datetime.setTime(dateTimeStamp);
			let Nyear = datetime.getFullYear();
			let Nmonth = datetime.getMonth() + 1 < 10 ? "0" + (datetime.getMonth() + 1) : datetime.getMonth() + 1;
			let Ndate = datetime.getDate() < 10 ? "0" + datetime.getDate() : datetime.getDate();
			let Nhour = datetime.getHours() < 10 ? "0" + datetime.getHours() : datetime.getHours();
			let Nminute = datetime.getMinutes() < 10 ? "0" + datetime.getMinutes() : datetime.getMinutes();
			let Nsecond = datetime.getSeconds() < 10 ? "0" + datetime.getSeconds() : datetime.getSeconds();
			result = Nyear + "-" + Nmonth + "-" + Ndate
		}
		return result;
	},
	//取整
	toRound(number) {
		var bite = 0;
		if (number < 10) {
			return 10;
		}
		if (number > 10 && number < 50) {
			return 50;
		}
		while (number >= 10) {
			number /= 10;
			bite += 1;
		}
		return Math.ceil(number) * Math.pow(10, bite);
	},
	//金额大写
	getAmountChinese(val) {
		if (!val && val != 0) return '';
		if (val == 0) return '零元整';
		const regExp = /[a-zA-Z]/;
		if (regExp.test(val)) return '数字较大溢出';
		let amount = +val;
		if (isNaN(amount)) return '';
		if (amount < 0) amount = Number(amount.toString().split('-')[1]);
		const NUMBER = ['零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖'];
		const N_UNIT1 = ['', '拾', '佰', '仟'];
		const N_UNIT2 = ['', '万', '亿', '兆'];
		const D_UNIT = ['角', '分', '厘', '毫'];
		let [integer, decimal] = amount.toString().split('.');
		if (integer && (integer.length > 15 || integer.indexOf('e') > -1)) return '数字较大溢出';
		let res = '';
		// 整数部分
		if (integer) {
			let zeroCount = 0;
			for (let i = 0, len = integer.length; i < len; i++) {
				const num = integer.charAt(i);
				const pos = len - i - 1; // 排除个位后 所处的索引位置
				const q = pos / 4;
				const m = pos % 4;
				if (num == '0') {
					zeroCount++;
				} else {
					if (zeroCount > 0 && m != 3) res += NUMBER[0];
					zeroCount = 0;
					res += NUMBER[parseInt(num)] + N_UNIT1[m];
				}
				if (m == 0 && zeroCount < 4) res += N_UNIT2[Math.floor(q)];
			}
		}
		if (Number(integer) != 0) res += '元';
		// 小数部分
		if (parseInt(decimal)) {
			for (let i = 0; i < 4; i++) {
				const num = decimal.charAt(i);
				if (parseInt(num)) res += NUMBER[num] + D_UNIT[i];
			}
		} else {
			res += '整';
		}
		if (val < 0) res = '负数' + res;
		return res;
	},
	// 转千位分隔
	thousandsFormat(num) {
		if (num == 0) return '0';
		if (!num) return '';
		const numArr = num.toString().split('.');
		numArr[0] = numArr[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
		return numArr.join('.');
	},
	idGenerator() {
		let quotient = (new Date() - new Date('2020-08-01'))
		quotient += Math.ceil(Math.random() * 1000)
		const chars = '0123456789ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz';
		const charArr = chars.split("")
		const radix = chars.length;
		const res = []
		do {
			let mod = quotient % radix;
			quotient = (quotient - mod) / radix;
			res.push(charArr[mod])
		} while (quotient);
		return res.join('')
	},
	onlineUtils: {
		// 获取用户信息
		getUserInfo() {
			const userInfo = uni.getStorageSync('userInfo') || {};
			userInfo.token = uni.getStorageSync('token') || '';
			return userInfo;
		},
		// 获取设备信息
		getDeviceInfo() {
			const deviceInfo = {
				vueVersion: '2',
				origin: 'app'
			};
			return deviceInfo;
		},
		// 请求
		request(url, method, data, headers) {
			return request({
				url: url,
				method: method || 'GET',
				data: data || {},
				header: headers || {},
				options: {
					load: false
				}
			})
		},
		// 路由跳转
		route(url, type = 'navigateTo') {
			if (!url) return;
			uni.$u.route({
				url,
				type
			})
		},
		// 消息提示
		toast(message, type = 'info', duration = 3000) {
			uni.$u.toast(message, duration)
		},
	},
	aesEncryption: {
		decrypt(str, cipherKey = '') {
			if (!cipherKey) cipherKey = define.cipherKey
			const hexStr = CryptoJS.enc.Base64.stringify(CryptoJS.enc.Hex.parse(str))
			const decryptedData = CryptoJS.AES.decrypt(hexStr, CryptoJS.enc.Utf8.parse(cipherKey), {
				mode: CryptoJS.mode.ECB,
				padding: CryptoJS.pad.Pkcs7
			}).toString(CryptoJS.enc.Utf8);
			return decryptedData
		},
		encrypt(str, cipherKey = '') {
			if (!cipherKey) cipherKey = define.cipherKey
			const encryptedData = CryptoJS.AES.encrypt(str, CryptoJS.enc.Utf8.parse(cipherKey), {
				mode: CryptoJS.mode.ECB,
				padding: CryptoJS.pad.Pkcs7
			}).toString();
			const result = CryptoJS.enc.Hex.stringify(CryptoJS.enc.Base64.parse(encryptedData))
			return result
		}
	},
	getDistance(lat1, lon1, lat2, lon2) {
		const toRadians = (degrees) => {
			return degrees * (Math.PI / 180);
		}
		const R = 6371;
		const dLat = toRadians(lat2 - lat1);
		const dLon = toRadians(lon2 - lon1);
		const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
			Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
			Math.sin(dLon / 2) * Math.sin(dLon / 2);
		const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		const distance = R * c;
		return distance * 1000;
	},
}
export default linzen