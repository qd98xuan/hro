<template>
	<view class="">
		<uni-nav-bar class='nav' :fixed="true" :statusBar="true" :border="false" height="44">
			<block slot="default">
				<view class="nav-left">
					<view class="nav-left-text">
						{{config.fullName}}
					</view>
				</view>
			</block>
		</uni-nav-bar>
		<view class="" v-show="!showPsd">
			<template v-if="type==='form' || type==='list' || type === 'detali'">
				<view class="" v-if="type==='form' || type === 'detali'">
					<view class="linzen-wrap linzen-wrap-form" v-if="!loading && (type==='form' || type === 'detali')">
						<linzen-parser :formConf="formConf" ref="dynamicForm" @submit="sumbitForm" :key="newDate" />
						<view class="buttom-actions" v-if="type==='form'">
							<u-button class="buttom-btn" @click.stop="resetForm">重置</u-button>
							<u-button class="buttom-btn" type="primary" @click.stop="submit" :loading="btnLoading">
								{{formConf.confirmButtonText||'确定'}}
							</u-button>
						</view>
						<view class="buttom-actions" v-if="type==='detali'">
							<u-button class="buttom-btn" @click.stop="resetForm">取消</u-button>
						</view>
					</view>
				</view>
				<view v-if="type==='list' && flg">
					<List :config="config" ref="List" :modelId="modelId" :columnText="columnText"
						:columnCondition="columnCondition" :encryption='encryption' :searchFormData="searchFormData" />
				</view>
			</template>
		</view>
		<view class="" v-show="!showPsd"></view>
		<u-modal v-model="showPsd" :title-style="titleStyle" title="密码" @confirm="confirm" v-if="showPsd">
			<view class="slot-content u-p-l-32 u-p-r-22 u-p-t-20 u-p-b-20">
				<u-input type="password" placeholder="请输入密码" :border="true" v-model="password"></u-input>
			</view>
		</u-modal>
	</view>
</template>

<script>
	import {
		getConfig,
		createModel,
		getShortLink,
		checkPwd
	} from '@/api/apply/webDesign'
	import md5Libs from "uview-ui/libs/function/md5";
	import List from './list.vue'
	const getFormDataFields = item => {
		if (!item.__config__ || !item.__config__.projectKey) return true
		const projectKey = item.__config__.projectKey
		const list = ["input", "textarea", "inputNumber", "switch", "datePicker", "timePicker", "colorPicker", "rate",
			"slider", "editor", "link", "text", "alert", 'table', "collapse", 'collapseItem', 'tabItem',
			"tab", "row", "card", "groupTitle", "divider", 'sign', 'location'
		]
		const fieldsSelectList = ["radio", "checkbox", "select", "cascader", "treeSelect"]
		if (list.includes(projectKey) || (fieldsSelectList.includes(projectKey) && item.__config__.dataType ===
				'static')) return true
		return false
	}
	export default {
		components: {
			List
		},
		data() {
			return {
				columnCondition: [],
				columnText: [],
				flg: false,
				password: '',
				titleStyle: {
					paddingTop: '24rpx'
				},
				showPsd: false,
				customStyle: {
					backgroundColor: '#fff'
				},
				dataForm: {
					data: ''
				},
				formConf: {},
				newDate: +new Date(),
				btnLoading: false,
				loading: true,
				modelId: '',
				config: {},
				type: 'form',
				listConfig: {},
				shortLinkData: {},
				formData: {},
				encryption: '',
				searchFormData: {}
			}
		},
		computed: {
			token() {
				return uni.getStorageSync('token')
			}
		},
		onLoad(e) {
			this.formData = e.formData ? JSON.parse(e.formData) : {};
			const decryptedData = this.linzen.aesEncryption.decrypt(e.encryption)
			if (!decryptedData) return
			const decrypt = JSON.parse(decryptedData)
			this.encryption = e.encryption
			this.modelId = decrypt.modelId
			this.type = decrypt.type
			this.getShortLink()
			this.getConfig()
		},
		methods: {
			// 递归过滤
			recursivefilter(arr, value) {
				let newColumn = arr.filter(item => getFormDataFields(item))
				newColumn.forEach(x =>
					x.__config__ && x.__config__.children && Array.isArray(x.__config__.children) && (x
						.__config__.children = this.recursivefilter(x.__config__.children))
				)
				return newColumn
			},
			getConfig() {
				getConfig(this.modelId, this.encryption).then(res => {
					this.config = res.data || {}
					this.formConf = JSON.parse(this.config.formData) || {}
					this.beforeInit(this.formConf.fields)
					let fields = this.recursivefilter(this.formConf.fields)
					this.formConf.fields = fields
					this.fillFormData(fields, this.formData)
					this.$nextTick(() => {
						this.newDate = +new Date()
						this.loading = false
					})
				})
			},
			beforeInit(fields) {
				const loop = (list) => {
					for (var index = 0; index < list.length; index++) {
						const config = list[index].__config__
						if (config.children && config.children.length) loop(config.children)
						if (config.projectKey == 'tableGrid') {
							let newList = []
							for (var i = 0; i < config.children.length; i++) {
								let element = config.children[i]
								for (var j = 0; j < element.__config__.children.length; j++) {
									let item = element.__config__.children[j]
									newList.push(...item.__config__.children)
								}
							}
							list.splice(index, 1, ...newList)
						}
					}
				}
				loop(fields)
			},
			getShortLink() {
				getShortLink(this.modelId, this.encryption).then(res => {
					this.shortLinkData = res.data || {}
					this.addSearchList()
					for (const item of this.columnCondition) {
						const {
							__config__,
							id,
							value,
							label
						} = item;
						if (value != null && value != '' && value != []) {
							this.searchFormData[id] = value;
						}
					}
					this.columnText = JSON.parse(this.shortLinkData.columnText)
					if (this.type == 'list' && this.shortLinkData.columnPassUse == 1) this.showPsd = true
					if (this.type == 'form' && this.shortLinkData.formPassUse == 1) this.showPsd = true
					this.flg = true
					this.newDate = +new Date()
				})
			},
			addSearchList() {
				this.columnCondition = JSON.parse(this.shortLinkData.columnCondition)
				const obj = {
					label: '关键词',
					__config__: {
						projectKey: 'input'
					},
					value: undefined,
					id: 'projectKeyword'
				};
				for (let i = 0; i < this.columnCondition.length; i++) {
					const item = this.columnCondition[i];
					if (item.isKeyword) return this.columnCondition.unshift(obj);
				}
			},
			confirm() {
				let data = {
					id: this.modelId,
					password: md5Libs.md5(this.password),
					type: this.type == 'form' ? 0 : 1,
					encryption: this.encryption
				}
				checkPwd(data).then(res => {
					this.showPsd = false
					this.newDate = +new Date()
				}).catch(err => {
					this.showPsd = true
					this.password = ''
					this.newDate = +new Date()
				})
			},
			fillFormData(form, data) {
				const loop = list => {
					for (let i = 0; i < list.length; i++) {
						let item = list[i]
						if (item.__vModel__) {
							let val = data.hasOwnProperty(item.__vModel__) ? data[item.__vModel__] : item.__config__
								.defaultValue
							if (!item.__config__.custom && item.__config__.defaultCurrent) {
								if (item.__config__.projectKey === 'timePicker') {
									item.__config__.defaultValue = this.linzen.toDate(new Date(), this.linzen.handelFormat(
										item.format))
								}
								if (item.__config__.projectKey === 'datePicker') {
									item.__config__.defaultValue = new Date().getTime()
								}
								if (item.__config__.projectKey === 'organizeSelect' && (this.userInfo
										.organizeIdList instanceof Array && this.userInfo.organizeIdList.length > 0
									)) {
									item.__config__.defaultValue = item.multiple ? [this.userInfo.organizeIdList] :
										this.userInfo.organizeIdList
								}
								if (item.__config__.projectKey === 'depSelect' && this.userInfo.departmentId) {
									item.__config__.defaultValue = item.multiple ? [this.userInfo.departmentId] :
										this.userInfo.departmentId;
								}
								if (item.__config__.projectKey === 'posSelect' && (this.userInfo
										.positionIds instanceof Array && this.userInfo.positionIds.length > 0)) {
									item.__config__.defaultValue = item.multiple ? this.userInfo.positionIds.map(
										o => o.id) : this.userInfo.positionIds[0].id
								}
								if (item.__config__.projectKey === 'roleSelect' && (this.userInfo
										.roleIds instanceof Array && this.userInfo.roleIds.length > 0)) {
									item.__config__.defaultValue = item.multiple ? this.userInfo.roleIds : this
										.userInfo.roleIds[0];
								}
								if (item.__config__.projectKey === 'groupSelect' && (this.userInfo
										.groupIds instanceof Array && this.userInfo.groupIds.length > 0)) {
									item.__config__.defaultValue = item.multiple ? this.userInfo.groupIds : this
										.userInfo.groupIds[0];
								}
								if (['userSelect'].includes(item.__config__.projectKey) && this.userInfo.userId) {
									item.__config__.defaultValue = item.multiple ? [this.userInfo.userId] : this
										.userInfo.userId;
								}
								if (item.__config__.projectKey === 'usersSelect' && this.userInfo.userId) {
									item.__config__.defaultValue = item.multiple ? [this.userInfo.userId + '--user'] :
										this.userInfo.userId + '--user';
								}
							}
							if (this.origin === 'scan') {
								this.$set(item, 'disabled', true)
							}
							let noShow = !item.__config__.noShow ? false : item.__config__.noShow
							let isVisibility = false
							if (!item.__config__.visibility || (Array.isArray(item.__config__.visibility) && item
									.__config__.visibility.includes('app'))) isVisibility = true
							this.$set(item.__config__, 'isVisibility', isVisibility)
							this.$set(item.__config__, 'noShow', noShow)
						} else {
							let noShow = false,
								isVisibility = false
							if (!item.__config__.visibility || (Array.isArray(item.__config__.visibility) && item
									.__config__.visibility.includes('app'))) isVisibility = true
							this.$set(item.__config__, 'isVisibility', isVisibility)
							this.$set(item.__config__, 'noShow', noShow)
						}
						if (item.__config__ && item.__config__.children && Array
							.isArray(item.__config__.children)) {
							loop(item.__config__.children)
						}
					}
				}
				loop(form)
			},
			sumbitForm(data, callback) {
				if (!data) return
				this.btnLoading = true
				this.dataForm.data = JSON.stringify(data)
				if (callback && typeof callback === "function") callback()
				createModel(this.modelId, this.dataForm, this.encryption).then(res => {
					uni.showToast({
						title: res.msg,
						complete: () => {
							setTimeout(() => {
								this.btnLoading = false
								this.resetForm()
							}, 1500)
						}
					})
				}).catch(() => {
					this.btnLoading = false
				})
			},
			submit() {
				if (this.isPreview) {
					uni.showToast({
						title: '功能预览不支持数据保存',
						icon: 'none'
					})
					return
				}
				this.$refs.dynamicForm && this.$refs.dynamicForm.submitForm()
			},
			resetForm() {
				this.loading = true
				this.newDate = +new Date()
				this.$nextTick(() => {
					this.loading = false
					this.$refs.dynamicForm && this.$refs.dynamicForm.resetForm()
				})
			}
		}
	}
</script>
<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.nav {
		z-index: 99999;

		/deep/.uni-navbar__content {
			z-index: 99999;
		}

		/deep/.uni-navbar__header-container {
			justify-content: center;
		}
	}

	.nav-left {
		max-width: 100%;
		display: flex;
		align-items: center;

		.nav-left-text {
			font-weight: 700;
			font-size: 32rpx;
			flex: 1;
			min-width: 0;
			white-space: nowrap;
			overflow: hidden;
			text-overflow: ellipsis;
		}

		.right-icons {
			font-weight: 700;
			margin-top: 2px;
			margin-left: 4px;
			transition-duration: 0.3s;
		}

		.select-right-icons {
			transform: rotate(-180deg);
		}
	}

	.pasd_box {
		width: 100%;
		// height: calc(100vh - 100rpx);
		// border: 1px solid blue;
		padding: 60% 0;
		display: flex;
		flex-direction: row;
		justify-content: center;
		align-items: center;
		box-sizing: border-box;

		.pasd_box_input {

			// width: 80%;
			.ipt {
				border-radius: 8rpx 0 0 8rpx;
				border: 1px solid red;
			}

			box-sizing: border-box;

		}
	}
</style>