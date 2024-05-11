<template>
	<view class="dynamicModel-form-v linzen-wrap linzen-wrap-form" v-if="showPage">
		<Parser :formConf="formConf" :formValue="formData" ref="dynamicForm" v-if="!loading" :key="key"
			@toDetail="toDetail" />
		<view class="buttom-actions">
			<u-button class="buttom-btn" @click.stop="linzen.goBack">取消</u-button>
			<u-button class="buttom-btn" type="primary" @click.stop="handleEdit"
				v-if="btnType === 'btn_edit' && !this.setting.noShowBtn">{{labelS.btn_edit}}
			</u-button>
		</view>
	</view>
</template>

<script>
	import {
		getConfigData,
		createModel,
		updateModel
	} from "@/api/apply/visualDev";
	import {
		getDataChange,
	} from "@/api/common";

	import Parser from "./components/detail/Parser";
	export default {
		components: {
			Parser,
		},
		data() {
			return {
				showPage: false,
				loading: true,
				isPreview: "0",
				modelId: "",
				formConf: {},
				formData: {},
				dataForm: {
					id: "",
					data: "",
				},
				btnType: "",
				formPermissionList: {},
				formList: [],
				labelS: {}
			};
		},
		onLoad(option) {
			let config = JSON.parse(this.base64.decode(option.config));
			this.formPermissionList = !config.currentMenu ? [] :
				JSON.parse(decodeURIComponent(config.currentMenu));
			this.formList = this.formPermissionList.formList;
			this.btnType = config.jurisdictionType || "";
			this.labelS = config.labelS || {
				btn_edit: '编辑'
			}
			this.modelId = config.modelId;
			this.isPreview = config.isPreview || "0";
			this.dataForm.id = config.id || "";
			this.setting = config;
			this.getConfigData();
			uni.$on("refresh", () => {
				this.getConfigData();
			});
		},
		beforeDestroy() {
			uni.$off("refresh");
		},
		methods: {
			getConfigData() {
				this.loading = true;
				getConfigData(this.modelId).then((res) => {
					if (res.code !== 200 || !res.data) {
						uni.showToast({
							title: "暂无此页面",
							icon: "none",
							complete: () => {
								setTimeout(() => {
									uni.navigateBack();
								}, 1500);
							},
						});
						return;
					}
					this.formConf = res.data.formData ? JSON.parse(res.data.formData) : {};
					this.beforeInit(this.formConf.fields || []);
					this.showPage = true;
					this.key = +new Date();
					this.initData();
				});
			},
			beforeInit(fields) {
				const loop = (list) => {
					for (var index = 0; index < list.length; index++) {
						const config = list[index].__config__;
						if (config.children && config.children.length) loop(config.children);
						if (config.projectKey == "tableGrid") {
							let newList = [];
							for (var i = 0; i < config.children.length; i++) {
								let element = config.children[i];
								for (var j = 0; j < element.__config__.children.length; j++) {
									let item = element.__config__.children[j];
									newList.push(...item.__config__.children);
								}
							}
							list.splice(index, 1, ...newList);
						}
					}
				};
				loop(fields);
			},
			initData() {
				this.$nextTick(() => {
					if (this.dataForm.id) {
						let extra = {
							modelId: this.modelId,
							id: this.dataForm.id,
							type: 2,
						};
						getDataChange(this.modelId, this.dataForm.id).then((res) => {
							this.dataForm = res.data;
							this.loading = false;
							if (!this.dataForm.data) return;
							this.formData = {
								...JSON.parse(this.dataForm.data),
								id: this.dataForm.id,
							};
							this.fillFormData(this.formConf, this.formData);
							this.initRelationForm(this.formConf.fields);
						});
					} else {
						this.loading = false;
					}
					this.key = +new Date();
				});
			},
			fillFormData(form, data) {
				const loop = (list, parent) => {
					for (let i = 0; i < list.length; i++) {
						let item = list[i];
						let isVisibility = !item.__config__.visibility || (Array.isArray(
								item.__config__.visibility) && item
							.__config__.visibility.includes('app'))
						this.$set(item.__config__, 'isVisibility', isVisibility)
						if (item.__vModel__) {
							if (
								item.__config__.projectKey === "relationForm" ||
								item.__config__.projectKey === "popupSelect"
							) {
								item.__config__.defaultValue = data[item.__vModel__ + "_id"];
								this.$set(item, "name", data[item.__vModel__] || "");
							} else {
								let val = data.hasOwnProperty(item.__vModel__) ?
									data[item.__vModel__] :
									item.__config__.defaultValue;
								item.__config__.defaultValue = val;
							}
							if (this.formPermissionList.useFormPermission) {
								let id = item.__config__.isSubTable ?
									parent.__vModel__ + "-" + item.__vModel__ :
									item.__vModel__;
								let noShow = true;
								if (this.formList && this.formList.length) {
									noShow = !this.formList.some((o) => o.enCode === id);
								}
								noShow = item.__config__.noShow ? item.__config__.noShow : noShow;
								this.$set(item.__config__, "noShow", noShow);
							}
						} else {
							if (['relationFormAttr', 'popupAttr'].includes(item.__config__.projectKey)) {
								item.__config__.defaultValue =
									data[item.relationField.split('_linzenTable_')[0] + '_' + item.showField];
							}
						}
						if (
							item.__config__ &&
							item.__config__.children &&
							Array.isArray(item.__config__.children)
						) {
							loop(item.__config__.children, item);
						}
					}
				};
				loop(form.fields);
				this.loading = false;
			},
			initRelationForm(componentList) {
				componentList.forEach((cur) => {
					const config = cur.__config__;
					if (
						config.projectKey == "relationFormAttr" ||
						config.projectKey == "popupAttr"
					) {
						const relationKey = cur.relationField.split("_linzenTable_")[0];
						componentList.forEach((item) => {
							const noVisibility =
								Array.isArray(item.__config__.visibility) &&
								!item.__config__.visibility.includes("app");
							if (
								relationKey == item.__vModel__ &&
								(noVisibility || !!item.__config__.noShow) && !cur.__vModel__
							) {
								cur.__config__.noShow = true;
							}
						});
					}
					if (cur.__config__.children && cur.__config__.children.length)
						this.initRelationForm(cur.__config__.children);
				});
			},
			toDetail(item) {
				const id = item.__config__.defaultValue;
				if (!id) return;
				let config = {
					modelId: item.modelId,
					id: id,
					formTitle: "详情",
					noShowBtn: 1,
				};
				this.$nextTick(() => {
					const url =
						"/pages/apply/dynamicModel/detail?config=" +
						this.base64.encode(JSON.stringify(config), "UTF-8");
					uni.navigateTo({
						url: url,
					});
				});
			},
			handleEdit() {
				const currentMenu = encodeURIComponent(
					JSON.stringify(this.formPermissionList)
				);
				let config = {
					modelId: this.modelId,
					isPreview: this.isPreview,
					id: this.setting.id,
					jurisdictionType: "btn_edit",
					currentMenu,
					list: this.setting.list,
					index: this.setting.index,
				};
				const url =
					"/pages/apply/dynamicModel/form?config=" +
					this.base64.encode(JSON.stringify(config), "UTF-8");
				uni.navigateTo({
					url: url,
				});
			},
		},
	};
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.dynamicModel-form-v {
		/deep/.u-form-item {
			min-height: 112rpx;
			background-color: #fff;
		}
	}
</style>