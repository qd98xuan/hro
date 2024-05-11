/* process.env.NODE_ENV设置生产环境模式 */
// #ifndef MP
const baseURL = process.env.NODE_ENV === "production" ? "https://app.java.linzensoft.com" : "http://localhost:30000"
const webSocketUrl = process.env.NODE_ENV === "production" ? "wss://app.java.linzensoft.com/websocket" :
	"ws://localhost:30000/api/message/websocket"
const report = process.env.NODE_ENV === 'development' ? 'http://localhost:8200' : baseURL + '/Report'
// #endif

// #ifdef MP
const baseURL = "http://localhost:30000"
const webSocketUrl = "ws://localhost:30000/api/message/websocket"
const report = baseURL + '/Report'
// #endif

const define = {
	copyright: "Copyright @ 2024 信息技术有限公司版权所有",
	sysVersion: "V3.5",
	baseURL, // 接口前缀
	report,
	webSocketUrl,
	comUploadUrl: baseURL + '/api/file/Uploader/',
	timeout: 1000000,
	aMapWebKey: '09485f01587712b3c04e5a9abf324237',
	cipherKey: 'EY8WePvjM5GGwQzn', // 加密key
}
export default define