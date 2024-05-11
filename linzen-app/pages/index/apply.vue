<template>
	<view class="apply-v">
		<uni-nav-bar class="nav" :fixed="true" :statusBar="true" :border="false" height="44">
			<block slot="default">
				<view class="nav-left" @click="showSelectBox">
					<view class="nav-left-text">
						{{ selectData.name }}
					</view>
					<uni-icons class="right-icons" type="arrowdown" color="#000000" size="14"
						v-if="userInfo.systemIds.length > 1" :class="{ 'select-right-icons': showSelect }" />
				</view>
			</block>
		</uni-nav-bar>
		<view class="search-box_sticky" :style="{'top':topSearch+'rpx'}">
			<view class="search-box">
				<u-search placeholder="请输入关键词搜索" v-model="keyword" height="72" :show-action="false" @change="search"
					bg-color="#f0f2f6" shape="square">
				</u-search>
			</view>
		</view>
		<mescroll-body ref="mescrollRef" @down="downCallback" :down="downOption" :sticky="true" @up="upCallback"
			:up="upOption" :bottombar="false" style="min-height: 100%" @init="mescrollInit">
			<view class="common-block">
				<view class="caption">常用功能</view>
				<view class="u-flex u-flex-wrap">
					<view class="item u-flex-col u-col-center" v-for="(item, i) in usualList" :key="i"
						@click="handelClick(item)">
						<text class="u-font-40 item-icon" :class="item.icon"
							:style="{ background: item.iconBackground || '#008cff' }" />
						<text class="u-font-24 u-line-1 item-text">{{ item.fullName}}</text>
					</view>
					<view class="item u-flex-col u-col-center" @click="moreApp">
						<text class="u-font-40 item-icon more">+</text>
						<text class="u-font-24 u-line-1 item-text">添加</text>
					</view>
				</view>
			</view>
			<view class="u-m-b-20">
				<u-tabs :list="tabsMenuList" :current="current" @change="change" :is-scroll="true" name="fullName"
					:key="key">
				</u-tabs>
			</view>
			<view class="workFlow-list">
				<view class="part" v-for="(item, i) in menuList" :key="i" v-if="!!current ||(!current && hasChildren)">
					<view v-if="!!item.children && item.children.length > 0">
						<view class="caption u-line-1">
							{{ item.fullName }}
						</view>
						<view class="u-flex u-flex-wrap">
							<view class="item u-flex-col u-col-center" v-for="(child, ii) in item.children" :key="ii"
								@click="handelClick(child)">
								<text class="u-font-40 item-icon" :class="child.icon"
									:style="{ background: child.iconBackground || '#008cff' }" />
								<text class="u-font-24 u-line-1 item-text">{{child.fullName}}</text>
							</view>
						</view>
					</view>
					<NoData v-if="!!current && (!Array.isArray(item.children) || !item.children.length)"></NoData>
				</view>
				<NoData v-else></NoData>
			</view>
		</mescroll-body>
		<u-popup v-model="passwordShow" mode="center" length="auto">
			<view class="linzen-wrap linzen-wrap-workflow">
				<u-form :model="dataForm" :rules="rules" ref="dataForm" label-position="left" label-width="150"
					label-align="left">
					<u-form-item label="旧密码" prop="oldPassword" required>
						<u-input v-model="dataForm.oldPassword" placeholder="旧密码" type="password"></u-input>
					</u-form-item>
					<u-form-item label="新密码" prop="password" required>
						<u-input v-model="dataForm.password" placeholder="新密码" type="password"></u-input>
					</u-form-item>
					<u-form-item label="重复密码" prop="repeatPsd" required>
						<u-input v-model="dataForm.repeatPsd" placeholder="重复密码" type="password"></u-input>
					</u-form-item>
					<u-form-item label="验证码" prop="code" required>
						<view class="u-flex">
							<u-input v-model="dataForm.code" placeholder="验证码"></u-input>
							<view style="flex: 0.1">
								<u-image :showLoading="true" :src="baseURL + imgUrl" width="130px" height="38px"
									@click="changeCode">
								</u-image>
							</view>
						</view>
					</u-form-item>
					<u-button class="buttom-btn" type="primary" @click.stop="dataFormSubmit">
						{{ "保存" }}
					</u-button>
				</u-form>
			</view>
		</u-popup>
		<u-popup v-model="showSelect" mode="top" class="select-box" height="600px">
			<view :style="{ 'margin-top': statusBarHeight + 44 + 'px' }"></view>
			<view v-for="(item, index) in userInfo.systemIds" :key="index" class="select-item"
				@click="selectItem(item, index)">
				<text class="u-m-r-12 u-font-40"
					:class="[item.icon, { currentItem: item.id === userInfo.appSystemId }]" />
				<text class="item-text sysName"
					:class="{ currentItem: item.id === userInfo.appSystemId }">{{ item.name }}</text>
				<u-icon name="checkbox-mark " class="currentItem" v-if="item.id === userInfo.appSystemId"></u-icon>
			</view>
		</u-popup>
	</view>
</template>
<script>
	import {
		getMenuList,
		getUsualList
	} from "@/api/apply/apply.js";
	import NoData from '@/components/noData'
	import {
		setMajor,
		updatePassword,
		getSystemConfig,
		updatePasswordMessage,
	} from "@/api/common";
	import resources from "@/libs/resources.js";
	import MescrollMixin from "@/uni_modules/mescroll-uni/components/mescroll-uni/mescroll-mixins.js";
	import IndexMixin from "./mixin.js";
	import md5Libs from "uview-ui/libs/function/md5";
	export default {
		mixins: [MescrollMixin, IndexMixin],
		components: {
			NoData
		},
		data() {
			var validatePass = (rule, value, callback) => {
				// const passwordreg = /(?=.*\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Z0-9]).{6,16}/
				//是否包含数字
				const containsNumbers = /[0-9]+/;
				//是否包含小写字符
				const includeLowercaseLetters = /[a-z]+/;
				//是否包含大写字符
				const includeUppercaseLetters = /[A-Z]+/;
				//是否包含字符
				const containsCharacters = /\W/;
				if (value === "") {
					callback(new Error("新密码不能为空"));
				} else if (this.baseForm.passwordStrengthLimit == 1) {
					if (this.baseForm.passwordLengthMin) {
						if (value.length < this.baseForm.passwordLengthMinNumber) {
							callback(
								new Error(
									"新密码长度不能小于" +
									this.baseForm.passwordLengthMinNumber +
									"位"
								)
							);
						}
					}
					if (this.baseForm.containsNumbers) {
						if (!containsNumbers.test(value)) {
							callback(new Error("新密码必须包含数字"));
						}
					}
					if (this.baseForm.includeLowercaseLetters) {
						if (!includeLowercaseLetters.test(value)) {
							callback(new Error("新密码必须包含小写字母"));
						}
					}
					if (this.baseForm.includeUppercaseLetters) {
						if (!includeUppercaseLetters.test(value)) {
							callback(new Error("新密码必须包含大写字字母"));
						}
					}
					if (this.baseForm.containsCharacters) {
						if (!containsCharacters.test(value)) {
							callback(new Error("新密码必须包含字符"));
						}
					}
					callback();
				} else {
					callback();
				}
			};
			var validatePass2 = (rule, value, callback) => {
				if (value === "") {
					callback(new Error("重复密码不能为空"));
				} else if (value !== this.dataForm.password) {
					callback(new Error("两次密码输入不一致"));
				} else {
					callback();
				}
			};
			return {
				topSearch: 80,
				passwordShow: false,
				current: 0,
				bannerList: [{
						image: resources.banner.home1Url,
					},
					{
						image: resources.banner.home2Url,
					},
					{
						image: resources.banner.home3Url,
					},
				],
				usualList: [],
				tabsMenuList: [{
					fullName: "全部功能",
				}],
				menuList: [],
				downOption: {
					use: true,
					auto: true,
				},
				upOption: {
					page: {
						num: 0,
						size: 50,
						time: null,
					},
					empty: {
						use: true,
						icon: resources.message.nodata,
						tip: "暂无数据",
						fixed: false,
						top: "560rpx",
					},
					textNoMore: "",
				},
				keyword: "",
				statusBarHeight: "",
				userInfo: {
					systemIds: [],
				}, //CurrentUser接口中的userInfo数据
				showSelect: false,
				selectData: {
					name: "",
					id: "",
				},
				modelId: "",
				config: {},
				fullName: "",
				key: +new Date(),
				imgUrl: "",
				timestamp: "",
				dataForm: {
					oldPassword: "",
					password: "",
					repeatPsd: "",
					code: "",
					timestamp: "",
				},
				baseForm: {
					passwordStrengthLimit: 0,
					passwordLengthMin: false,
					passwordLengthMinNumber: 0,
					containsNumbers: false,
					includeLowercaseLetters: false,
					includeUppercaseLetters: false,
					containsCharacters: false,
					mandatoryModificationOfInitialPassword: 0,
				},
				rules: {
					oldPassword: [{
						required: true,
						message: "旧密码不能为空",
						trigger: "blur",
					}, ],
					password: [{
						required: true,
						validator: validatePass,
						trigger: "blur",
					}, ],
					repeatPsd: [{
						required: true,
						validator: validatePass2,
						trigger: "blur",
					}, ],
					code: [{
						required: true,
						message: "验证码不能为空",
						trigger: "blur",
					}, ],
				},
			};
		},
		computed: {
			baseURL() {
				return this.define.baseURL;
			},
			token() {
				return uni.getStorageSync('token');
			},
			report() {
				return this.define.report;
			},
			hasChildren() {
				let hasChildren = false
				for (let i = 0; i < this.menuList.length; i++) {
					if (this.menuList[i].children && this.menuList[i].children.length) {
						hasChildren = true
						break
					}
				}
				return hasChildren
			}
		},
		watch: {
			passwordShow(val) {
				if (val) {
					this.$nextTick(() => {
						this.$refs.dataForm.setRules(this.rules);
					});
				}
			},
		},
		onShow() {
			this.keyword = ""
		},
		onLoad() {
			uni.$on('updateUsualList', data => {
				this.getUsualList()
			})
			uni.$on('refresh', () => {
				this.menuList = [];
				this.current = 0;
				this.mescroll.resetUpScroll();
			});
			this.getStatusBarHeight();
			this.changeCode()
		},
		onUnload() {
			uni.$off("updateUsualList");
		},
		methods: {
			getStatusBarHeight() {
				let that = this
				wx.getSystemInfo({
					success(res) {
						that.statusBarHeight = res.statusBarHeight;
					},
				});
				// #ifdef APP-PLUS
				uni.getSystemInfo({
					success(res) {
						that.statusBarHeight = res.statusBarHeight;
						let topSearch = 75 + that.statusBarHeight * 2
						that.topSearch = topSearch
					}
				})
				// #endif
			},
			changeCode() {
				let timestamp = Math.random();
				this.timestamp = timestamp;
				this.imgUrl = `/api/file/ImageCode/${timestamp}`;
			},
			dataFormSubmit() {
				this.$refs["dataForm"].validate((valid) => {
					if (valid) {
						let query = {
							oldPassword: md5Libs.md5(this.dataForm.oldPassword),
							password: md5Libs.md5(this.dataForm.password),
							code: this.dataForm.code,
							timestamp: this.timestamp,
						};
						updatePassword(query)
							.then((res) => {
								this.$store.dispatch("user/logout").then(() => {
									uni.reLaunch({
										url: "/pages/login/index",
									});
								});
							})
							.catch(() => {
								this.changeImg();
							});
					}
				});
			},
			initSysList(res) {
				this.userInfo = res;
				if (this.userInfo.systemIds && this.userInfo.systemIds.length) {
					this.userInfo.systemIds.forEach((item) => {
						if (item.id == this.userInfo.appSystemId) this.selectData = item
					})
				}
				updatePasswordMessage();
				getSystemConfig().then((res) => {
					if (
						this.userInfo.changePasswordDate == null &&
						res.data.mandatoryModificationOfInitialPassword == 1
					) {
						this.passwordShow = true;
					}
					this.baseForm = res.data;
				});
			},
			search() {
				this.searchTimer && clearTimeout(this.searchTimer);
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.mescroll.resetUpScroll();
				}, 300);
			},
			getUsualList() {
				getUsualList(2).then((res) => {
					this.usualList = res.data.list.map((o) => {
						const objectData = o.objectData ? JSON.parse(o.objectData) : {};
						return {
							...o,
							...objectData,
						};
					});
				});
			},
			upCallback(keyword) {
				let query = {
					keyword: this.keyword,
				};
				uni.showLoading({
					title: '正在加载',
					mask: true
				})
				this.$store.dispatch("user/getCurrentUser").then((res) => {
					this.initSysList(res);
					this.getUsualList();
					getMenuList(query)
						.then((res) => {
							this.current = 0;
							let list = res.data.list || [];
							this.tabsMenuList = [{
								fullName: "全部功能",
							}];
							this.mescroll.endSuccess(list.length);
							for (let i = 0; i < list.length; i++) {
								let children = list[i].children;
								let tabsMenuList = {
									fullName: list[i].fullName,
								};
								this.tabsMenuList.push(tabsMenuList);
								if (Array.isArray(children) && children.length) {
									for (let j = 0; j < children.length; j++) {
										let iconBackground = "",
											moduleId = "";
										if (children[j].propertyJson) {
											let propertyJson = JSON.parse(children[j].propertyJson);
											iconBackground = propertyJson.iconBackgroundColor || "";
											moduleId = propertyJson.moduleId || "";
										}
										this.$set(children[j], "iconBackground", iconBackground);
										this.$set(children[j], "moduleId", moduleId);
									}
								}
							}
							this.list = list;
							this.menuList = list;
							uni.hideLoading()
							this.key = +new Date();
							this.mescroll.endSuccess(this.menuList.length, false);
						})
						.catch(() => {
							this.mescroll.endSuccess(0);
							this.mescroll.endErr();
						});
				});
			},
			change(index) {
				this.current = index;
				this.fullName = this.tabsMenuList[index].fullName;
				this.menuList = this.list;
				this.keyword = ""
				if (this.current > 0) this.menuList = [this.list[index - 1]];
			},
			search() {
				this.searchTimer && clearTimeout(this.searchTimer);
				this.searchTimer = setTimeout(() => {
					this.list = [];
					this.menuList = [];
					this.mescroll.resetUpScroll();
				}, 300);
			},
			moreApp() {
				uni.navigateTo({
					url: "/pages/workFlow/allApp/index?type=2",
				});
			},
			handelClick(item) {
				if (item.type == 2) {
					uni.navigateTo({
						url: item.urlAddress +
							"?menuId=" +
							item.id +
							"&fullName=" +
							item.fullName,
						fail: (err) => {
							this.$u.toast("暂无此页面");
						},
					});
					return;
				}
				if (item.type == 3) {
					this.modelId = item.moduleId;
					if (!item.moduleId) {
						this.$u.toast("暂无此页面");
						return;
					}
					uni.navigateTo({
						url: "/pages/apply/dynamicModel/index?config=" +
							this.base64.encode(JSON.stringify(item), "UTF-8"),
						fail: (err) => {
							this.$u.toast("暂无此页面");
						},
					});
				}
				if (item.type == 7 || item.type == 5) {
					let url =
						encodeURIComponent(item.urlAddress) + "&fullName=" + item.fullName;
					if (item.type == 5) {
						url = encodeURIComponent(
							`${this.report}/preview.html?id=${item.moduleId}&token=${this.token}&page=1&from=menu`
						);
					}
					if (!item.urlAddress && item.type == 7) {
						this.$u.toast("暂无此页面");
						return;
					}
					uni.navigateTo({
						url: "/pages/apply/externalLink/index?url=" +
							url +
							"&fullName=" +
							item.fullName +
							"&type=" +
							item.type,
						fail: (err) => {
							this.$u.toast("暂无此页面");
						},
					});
					// // #ifdef APP-PLUS
					// plus.runtime.openURL(item.urlAddress);
					// // #endif
					// // #ifndef APP-PLUS
					// uni.navigateTo({
					// 	url: '/pages/apply/externalLink/index?url=' + url,
					// 	fail: (err) => {
					// 		this.$u.toast("暂无此页面")
					// 	}
					// })
					// // #endif
					return;
				}
				if (item.type == 8) {
					if (!item.urlAddress) {
						this.$u.toast("暂无此页面");
						return;
					}
					uni.navigateTo({
						url: "/pages/portal/scanProtal/index?id=" + item.moduleId + "&protalType=1" +
							"&fullName=" + item
							.fullName,
						fail: (err) => {
							this.$u.toast("暂无此页面");
						},
					});
					return
				}
			},
			showSelectBox() {
				if (this.userInfo.systemIds.length <= 1) return;
				this.showSelect = !this.showSelect;
			},
			selectItem(item, index) {
				if (item.id === this.userInfo.appSystemId) return
				let query = {
					majorId: item.id,
					majorType: "System",
					menuType: 1,
				};
				setMajor(query).then((res) => {
					if (res.code == 200) {
						this.changeSelData(item, index);
						this.keyword = "";
						this.$u.toast(res.msg);
						this.mescroll.resetUpScroll();
					}
				});
			},
			changeSelData(item, index) {
				this.selectData = item
				this.userInfo.appSystemId = item.id
				this.showSelect = false
			},
		},
	};
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.apply-v {
		.search-box_sticky {
			margin-bottom: 20rpx;

			.search-box {
				padding: 20rpx;
			}
		}

		.common-block {
			background-color: #fff;
			margin: 20rpx 0;

			.caption {
				padding: 0 32rpx;
				font-size: 36rpx;
				line-height: 100rpx;
				font-weight: bold;
			}

			.item {
				margin-bottom: 32rpx;
				width: 25%;

				.item-icon {
					width: 88rpx;
					height: 88rpx;
					margin-bottom: 8rpx;
					line-height: 88rpx;
					text-align: center;
					border-radius: 20rpx;
					color: #fff;
					font-size: 56rpx;

					&.more {
						background: #ececec;
						color: #666666;
						font-size: 50rpx;
					}
				}

				.item-text {
					width: 100%;
					text-align: center;
					padding: 0 16rpx;
				}
			}
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

		.select-box {
			.currentItem {
				color: #2979ff;
			}

			.select-item {
				height: 100rpx;
				display: flex;
				align-items: center;
				padding: 0 20rpx;
				font-size: 30rpx;
				color: #303133;
				text-align: left;
				position: relative;

				&::after {
					content: " ";
					position: absolute;
					left: 2%;
					top: 0;
					box-sizing: border-box;
					width: 96%;
					height: 1px;
					transform: scale(1, 0.3);
					border: 0 solid #e4e7ed;
					z-index: 2;
					border-bottom-width: 1px;
				}

				.sysName {
					flex: 1;
					overflow: auto;
					min-width: 0;
				}
			}
		}

		.search-box {
			overflow-y: overlay;
			height: 112rpx;
			width: 100%;
			padding: 20rpx 20rpx;
			z-index: 10000;
			background: #fff;
		}

		.banner {
			padding: 0rpx 20rpx 20rpx;
			background-color: #fff;

			.u-indicator-item-round.u-indicator-item-round-active {
				background-color: $u-type-primary;
			}
		}

		.notice-bar {
			margin: 20rpx 20rpx 0;
			background: #fff;
			border-radius: 8rpx;
			z-index: 99;
			color: #303133 !important;
		}

		.workFlow-list {
			// padding: 20rpx 20rpx 0;

			.part {
				background: #fff;
				margin-bottom: 20rpx;

				.caption {
					padding: 0 32rpx;
					font-size: 36rpx;
					line-height: 100rpx;
					font-weight: bold;
				}

				.item {
					margin-bottom: 32rpx;
					width: 25%;

					.item-icon {
						width: 88rpx;
						height: 88rpx;
						margin-bottom: 8rpx;
						line-height: 88rpx;
						text-align: center;
						border-radius: 20rpx;
						color: #fff;
						font-size: 56rpx;

						&.more {
							background: #ececec;
							color: #666666;
							font-size: 50rpx;
						}
					}

					.item-text {
						width: 100%;
						text-align: center;
						padding: 0 16rpx;
					}
				}
			}
		}
	}
</style>