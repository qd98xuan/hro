<template>
	<view class="dynamicModel-v">
		<template v-if="showPage">
			<template v-if="webType == 1">
				<Form :config="config" :modelId="modelId" :isPreview="isPreview" />
			</template>
			<template v-if="webType == 2 || webType == 4">
				<List :config="config" :modelId="modelId" :isPreview="isPreview" :title="title" :menuId="menuId"
					ref="List" />
			</template>
		</template>
		<u-picker mode="selector" v-model="show" :default-selector="[0]" title="请选择流程" :range="selector"
			range-key="fullName" @confirm="confirm" cancel-text="" :mask-close-able="false"></u-picker>
	</view>
</template>
<script>
	import Form from "./components/form/index.vue";
	import List from "./components/list/index.vue";
	import {
		FlowJsonList
	} from "@/api/workFlow/flowEngine";
	import {
		getConfigData
	} from "@/api/apply/visualDev.js";
	export default {
		name: "dynamicModel",
		components: {
			Form,
			List,
		},
		data() {
			return {
				selector: [],
				show: false,
				enableFlow: 0,
				webType: "",
				showPage: false,
				isPreview: false,
				modelId: "",
				menuId: "",
				title: "",
				config: {},
				preview: false,
				previewType: "1",
				activeFlow: {},
				templateList: [],
			};
		},
		onLoad(obj) {
			this.$store.dispatch('base/getDictionaryDataAll')
			const config = JSON.parse(this.base64.decode(obj.config)) || {};
			this.config = config;
			this.isPreview = this.config.isPreview || "0";
			this.title = this.config.fullName || "";
			uni.setNavigationBarTitle({
				title: this.config.fullName || "",
			});
			this.menuId = this.config.id || "";
			this.getConfigData();
		},
		methods: {
			getConfigData() {
				getConfigData(this.config.moduleId, undefined).then((res) => {
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
					let data = res.data;
					let config = {
						...this.config,
						...data,
					};
					this.config = config;
					if (this.config.enableFlow == 1 && this.config.webType == "1") {
						FlowJsonList(this.config.moduleId).then((res) => {
							this.templateList = res.data;
							if (!this.templateList.length) {
								this.$u.toast("流程不存在");
								setTimeout(() => {
									uni.navigateBack();
								}, 1500);
							} else {
								if (this.templateList.length > 1) {
									this.show = true;
									this.selector = this.templateList;
								} else {
									this.activeFlow = this.templateList[0];
									this.Jump();
								}
							}
						});
					} else {
						this.isPreview = this.config.isPreview ? true : false;
						this.modelId = this.config.moduleId;
						this.previewType = this.config.previewType;
						this.menuId = this.config.id || "";
						this.enableFlow = this.config.enableFlow || 0;
						this.webType = this.config.webType || "2";
						this.showPage = true;
					}
				});
			},
			confirm(e) {
				this.activeFlow = this.templateList[e[0]];
				this.Jump();
			},
			Jump() {
				const config = {
					id: "",
					enCode: this.config.flowEnCode,
					flowId: this.activeFlow.id,
					formType: 2,
					type: 1,
					opType: "-1",
					status: "",
					isPreview: null,
					fullName: this.activeFlow.fullName,
					jurisdictionType: "",
					enableFlow: this.config.enableFlow,
					pureForm: true,
				};
				uni.redirectTo({
					url: "/pages/workFlow/flowBefore/index?config=" +
						this.base64.encode(JSON.stringify(config), "UTF-8"),
					fail: (err) => {
						this.$u.toast("暂无此页面");
					},
				});
			},
		},
	};
</script>
<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.dynamicModel-v {
		height: 100%;
	}
</style>