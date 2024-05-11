<template>
	<view class="operate-v">
		<view class="linzen-wrap">
			<u-form :model="dataForm" label-position="left" label-width="150" ref="dataForm">
				<view class="u-p-l-20 u-p-r-20 form-item-box">
					<u-form-item label="退回节点" v-if="config.eventType === 'reject' && isLastAppro"
						:class="{ 'form-item': rejectType == 3 }">
						<linzen-select v-model="dataForm.rejectStep" :options="config.rejectList.list" :props="props"
							:disabled="config.rejectStep != 2" />
					</u-form-item>
				</view>
				<view class="reject" v-if="rejectType == 3 && config.eventType === 'reject'">
					<view class="">
						<u-radio-group v-model="dataForm.rejectType">
							<u-radio @change="radioChange(item)" v-for="(item, index) in list" :key="index"
								:name="item.name" :disabled="item.disabled">
								{{ item.fullName }}
							</u-radio>
						</u-radio-group>
					</view>
				</view>
				<view class="u-p-l-20 u-p-r-20 form-item-box">
					<u-form-item label="分支选择" prop="branch" v-if="isBranch" required>
						<linzen-select v-model="dataForm.branchList" @change="branchChange" placeholder="请选择审批分支"
							:options="branchList" multiple :props="config.props" />
					</u-form-item>
					<u-form-item :label="seletUserLabel" prop="freeApproverUserId"
						:required="config.eventType === 'transfer' || config.hasFreeApprover ? true: false"
						v-if="isFreeApprover">
						<linzen-user-select v-model="dataForm.freeApproverUserId" @open="open" />
					</u-form-item>
					<u-form-item label="加签类型" v-if="config.hasFreeApprover">
						<linzen-select :options="typeList" v-model="dataForm.freeApproverType"
							@change="freeApproverChange" />
					</u-form-item>
					<u-form-item prop="comInput" v-for="(item, index) in candidateList" :key="index"
						:label="item.nodeName" required
						v-if=" config.eventType !== 'transfer' &&config.eventType !== 'revoke' && config.eventType !== 'recall' && isCandidate">
						<u-input v-if="item.hasCandidates" type="select" :select-open="item.selectShow"
							v-model="candidateValue[item.nodeId]" @click="openSelect(item)" placeholder="请选择审批候选人">
						</u-input>
						<linzen-user-select v-model="candidateValue[item.nodeId]" v-else multiple
							@change="change($event, item.nodeId)" @open="open" />
					</u-form-item>
					<u-form-item :label="opinionName" prop="handleOpinion"
						v-if="config.eventType !== 'submit' && config.hasOpinion">
						<u-input v-model="dataForm.handleOpinion" type="textarea" :placeholder="placeholder" />
						<view class="u-p-10"
							v-if="config.eventType != 'transfer' &&config.eventType != 'recall' &&config.eventType !== 'revoke' && config.eventType !== 'approvalCancel'">
							<u-button type="info" size="mini" @click="commonWords">常用语</u-button>
						</view>
					</u-form-item>
					<u-form-item :label="config.title + '附件'" prop="fileList"
						v-if="config.eventType !== 'submit' && config.hasOpinion">
						<linzen-file v-model="dataForm.fileList" :limit="3" />
					</u-form-item>
					<u-form-item label="抄送人员" prop="copyIds" v-if="isCustomCopy">
						<linzen-user-select v-model="dataForm.copyIds" multiple @open="open" />
					</u-form-item>
					<u-form-item label="签名" prop="signImg" v-if="isSign" required>
						<linzen-sign ref="sig" v-model="dataForm.signImg" @input="initSignImg" />
					</u-form-item>
				</view>

			</u-form>
		</view>
		<view v-if="isShow">
			<view class="flowBefore-actions" v-if="config.eventType !== 'submit'">
				<u-button class="buttom-btn" type="primary" @click="handleClick(config.eventType)">
					{{ btnTxt }}
				</u-button>
			</view>
		</view>
		<view v-if="isShow">
			<view class="flowBefore-actions" v-if="config.eventType === 'submit'">
				<u-button class="buttom-btn" @click="handleClick('cancel')">取消</u-button>
				<u-button class="buttom-btn" type="primary" @click="handleClick(config.eventType)">确定
				</u-button>
			</view>
		</view>
		<u-popup v-model="show" mode="bottom" border-radius="14" height="700rpx" closeable :mask-close-able="false">
			<view class="u-p-28 hd" style="text-align: center">常用审批语</view>
			<view class="add">
				<view class="add-inner" @click="editCommonWord">
					<u-icon name="plus-circle-fill" color="#81d3f8" size="28"></u-icon>
					<text class="u-m-l-8">添加常用语</text>
				</view>
			</view>
			<view class="content">
				<scroll-view scroll-y="true" style="height: 480rpx">
					<view class="commonList u-flex-col">
						<view class="u-flex item" v-if="commonWordsList.length > 0"
							v-for="(item, index) in commonWordsList" :key="index">
							<view class="txt" @click="selectCommonWord(item)">
								{{ item.commonWordsText }}
							</view>
							<view class="icon" @click.stop="editCommonWord(item)" v-if="item.commonWordsType == 1">
								<i class="icon-zen icon-zen-btn-edit" style="color: #81d3f8;font-size: 36rpx;"></i>
							</view>
							<view class="icon" @click.stop="delCommonWord(item)" v-if="item.commonWordsType == 1">
								<i class="icon-zen icon-zen-extend-trash" style="color: #e35d4b;font-size: 36rpx;"></i>
							</view>
						</view>
						<view class="" v-if="commonWordsList.length <= 0">
							<view class="notData-box u-flex-col">
								<view class="u-flex-col notData-inner">
									<image :src="icon" mode="" class="iconImg"></image>
									<text class="notData-inner-text">暂无数据</text>
								</view>
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
		</u-popup>
		<u-popup v-model="show2" mode="center" width="500rpx" border-radius="24" :key="key">
			<view class="u-flex-col innerPopup-box">
				<view class="innerPopup-hd"> 审批常用语 </view>
				<view class="innerPopup-content">
					<u-input v-model="commonWordsText" type="textarea" placeholder="请输入内容" :auto-height="false"
						maxlength="99999" height="150" />
				</view>
				<view class="u-flex btn-box">
					<button class="btn" @click="cancel">取消</button>
					<button class="btn btn2" @click="confirm">确定</button>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import {
		createSignImg
	} from "@/api/common.js";
	import {
		commonWords,
		getSelector,
		Create,
		getCommonWordsInfo,
		Update,
		deleteCommonWordsInfo
	} from "@/api/commonWords.js";
	import resources from "@/libs/resources.js";
	export default {
		data() {
			return {
				placeholder: "请输入意见",
				icon: resources.message.nodata,
				customStyle: {
					// 注意驼峰命名，并且值必须用引号包括，因为这是对象
					color: "red",
					width: "100%",
					height: "100%",
					borderRadius: "0",
					border: "0",
				},
				commonWordsText: "",
				show2: false,
				show: false,
				btnTxt: '确认',
				typeList: [{
						fullName: "审批前",
						id: 1,
					},
					{
						fullName: "审批后",
						id: 2,
					},
				],
				props: {
					label: "nodeName",
					value: "nodeCode",
				},
				list: [{
						fullName: "重新审批",
						disabled: false,
						name: 1,
					},
					{
						fullName: "直接提交给我",
						disabled: false,
						name: 2,
					},
				],
				config: {},
				dataForm: {
					fileList: [],
					handleOpinion: "",
					signImg: "",
					copyIds: "",
					freeApproverUserId: "",
					branchList: [],
					candidateList: {},
					rejectStep: "",
					freeApproverType: 1,
					rejectType: 1,
				},
				candidateValue: {},
				selectList: [],
				selectVal: {},
				isCandidate: false,
				branchList: [],
				candidateList: [],
				candidateType: "",
				seletUserLabel: "选择人员",
				opinionName: "审批意见",
				isShow: true,
				isBranch: false,
				isSign: false,
				rejectList: [],
				isLastAppro: true,
				rejectType: 1,
				commonWordsList: [],
				commonWordsData: {},
				key: +new Date()
			};
		},
		onLoad(option) {
			try {
				this.config = JSON.parse(decodeURIComponent(option.config));
			} catch {
				this.config = JSON.parse(option.config);
			}
			this.userInfo = uni.getStorageSync("userInfo") || {};
			this.dataForm.signImg = this.userInfo.signImg;
			this.rejectList = this.config.rejectList.list || [];
			this.rejectType = this.config.rejectType;
			this.isLastAppro = this.config.rejectList.isLastAppro;
			uni.setNavigationBarTitle({
				title: this.config.title,
			});
			this.btnTxt = this.btnTxt + this.config.title.replace(/\s+/g, "")
			this.isCandidate = true;
			this.candidateType = this.config.candidateType; /* 1==分支 2==候选人 3==直接通过*/
			this.isSign = this.config.eventType !== "submit" && this.config.hasSign;
			//分支
			if (this.config.hasFreeApprover) {
				this.opinionName = "加签原因";
				this.seletUserLabel = "加签人员";
				this.isBranch = false;
			} else {
				this.isBranch =
					this.candidateType == 1 &&
					this.config.eventType !== "transfer" &&
					this.config.eventType !== "reject";
			}
			if (this.candidateType === 3) this.isCandidate = false;
			this.candidateList = this.config.candidateList;
			this.branchList = this.config.branchList || [];
			if (this.branchList.length) {
				this.branchList = this.branchList.filter((o) => o.isBranchFlow);
				this.candidateList = this.config.branchList.filter(
					(o) => !o.isBranchFlow && o.isCandidates
				);
				this.candidateList = this.candidateList.map((o) => ({
					...o,
					label: o.nodeName + "审批人",
				}));
			}
			if (
				this.config.eventType === "reject" ||
				this.config.eventType === "revoke" ||
				this.config.eventType === "recall" ||
				this.config.eventType === "submit" ||
				this.config.eventType === "transfer"
			) {
				if (this.config.eventType === "transfer") {
					this.seletUserLabel = "转给谁";
					this.opinionName = "转审原因";
					this.placeholder = "请输入原因";
				}
				if (this.config.eventType === "reject") {
					this.opinionName = "退回意见";
					this.dataForm.rejectStep = this.rejectList[0][this.props.value];
					this.dataForm.rejectType = this.rejectType != 3 ? this.rejectType : 1;
				}
				if (this.config.eventType === "recall") {
					this.opinionName = "撤回原因";
					this.placeholder = "请输入原因";
				}
				if (this.config.eventType === "revoke") {
					this.opinionName = "撤回原因";
					this.placeholder = "请输入原因";
				}
			}
			if (this.config.eventType === "approvalCancel") {
				this.opinionName = "驳回原因";
				this.placeholder = "请输入原因";
			}
			uni.$on("confirm", (data, id) => {
				this.selectConfirm(data, id);
			});
			this.init();
			if (this.config.eventType !== "submit" && this.config.hasOpinion) {
				this.getSelector();
			}
		},
		onUnload() {
			uni.$off("confirm");
		},
		computed: {
			isCustomCopy() {
				return this.config.isCustomCopy && (
					this.config.eventType === 'reject' || !['freeapprover', 'transfer', 'revoke', 'recall', 'reject']
					.includes(this.config.eventType)
				);
			},
			isFreeApprover() {
				return (this.config.hasFreeApprover || this.config.eventType === 'transfer') && this.config.eventType !==
					'revoke' && this.config.eventType !== 'recall' && this.config.eventType !== 'reject'
			}
		},
		methods: {
			init() {
				if (this.candidateType == 1) {
					let list = [];
					this.isCandidate = false;
					const defaultList = this.candidateList;
					for (let i = 0; i < this.dataForm.branchList.length; i++) {
						inner: for (let j = 0; j < this.branchList.length; j++) {
							let o = this.branchList[j];
							if (this.dataForm.branchList[i] === o.nodeId && o.isCandidates) {
								this.isCandidate = true;
								list.push({
									...o,
									label: o.nodeName + "审批人",
								});
								break inner;
							}
						}
						this.candidateList = [...defaultList, ...list];
					}
				} else if (this.candidateType == 2) {
					if (Array.isArray(this.candidateList) && this.candidateList.length) {
						this.isCandidate =
							this.config.eventType === "freeapprover" ? false : true;
						this.candidateList = this.candidateList.map((o) => ({
							...o,
							label: o.nodeName + "审批人",
						}));
					}
				}
				this.key = +new Date()
			},
			/* 常用语 */
			getSelector() {
				getSelector().then((res) => {
					this.commonWordsList = res.data.list || [];
					this.key = +new Date()
				});
			},
			confirm(e) {
				this.commonWordsData.commonWordsText = this.commonWordsText;
				this.commonWordsData.commonWordsType = 1
				if (!this.commonWordsText) return this.$u.toast(`审批常用语不能为空`);
				let funs = this.commonWordsData.id === 0 ? Create : Update;
				funs(this.commonWordsData)
					.then((res) => {
						this.show2 = false;
						this.commonWordsText = "";
						uni.showToast({
							title: res.msg,
							icon: "none",
							complete: () => {
								this.getSelector();
							},
						});
					})
					.catch((err) => {
						this.show2 = false;
						this.getSelector();
					});
			},
			cancel() {
				this.show2 = false;
				this.commonWordsText = "";
			},
			commonWords() {
				this.show = true;
				this.key = +new Date()
			},
			selectCommonWord(item) {
				this.dataForm.handleOpinion =
					this.dataForm.handleOpinion + item.commonWordsText;
				this.show = false;
			},
			delCommonWord(item) {
				deleteCommonWordsInfo(item.id).then(res => {
					this.$u.toast(res.msg)
					this.getSelector();
				})
			},
			editCommonWord(item) {
				this.show2 = true;
				let data = {
					commonWordsText: "",
					enabledMark: 1,
					id: 0,
					sortCode: 0,
					systemIds: [],
					systemNames: [],
				};
				if (item.id) {
					this.commonWordsText = item.commonWordsText;
					this.commonWordsData = {
						...item,
						systemIds: [],
						systemNames: []
					};
				} else {
					this.commonWordsText = "";
					this.commonWordsData = data;
				}
			},
			/* 常用语 end */

			// 选中某个单选框时，由radio时触发
			radioChange(e) {
				this.dataForm.rejectType = e.name;
			},
			open(e) {
				this.isShow = !e;
			},
			change(val, nodeId) {
				if (val.length < 1) return
				let vals = [];
				for (let i = 0; i < val.length; i++) {
					vals.push(val[i]);
				}
				this.$set(this.dataForm.candidateList, nodeId, vals);
			},
			branchChange(e) {
				this.dataForm.branchList = e;
				this.init();
			},
			freeApproverChange(e) {
				this.isBranch = false;
				this.isCandidate = false;
				if (this.config.hasFreeApprover && e == 2 && this.candidateList.length) {
					this.isCandidate = true;
					this.candidateType = this.config.candidateType;
					if (this.candidateType == 1 && this.branchList.length > 0) {
						this.isBranch = true;
					}
				}
			},
			initSignImg(e) {
				let data = {
					signImg: e,
					isDefault: 1,
				};
				createSignImg(data).then((res) => {
					this.userInfo.signImg = e;
					uni.setStorageSync("userInfo", this.userInfo);
				});
			},

			//选择审批候选人
			openSelect(item) {
				this.selectList = [];
				for (let o in this.selectVal) {
					if (o === item.nodeId) this.selectList = this.selectVal[o];
				}
				item.formData = this.config.formData;
				item.taskId = this.config.taskId;
				item.selectList = this.selectList;
				item.candidateList = JSON.stringify(this.candidateList);
				uni.navigateTo({
					url: "/pages/workFlow/candiDateUserSelect/index?data=" +
						encodeURIComponent(JSON.stringify(item)),
				});
			},
			selectConfirm(e, nodeId) {
				let data = e;
				let users = [];
				let val = [];
				let selectVal = [];
				for (let i = 0; i < this.candidateList.length; i++) {
					for (let j = 0; j < data.length; j++) {
						if (data[j].nodeId === this.candidateList[i].nodeId) {
							val.push(data[j].fullName);
							selectVal.push(data[j]);
							this.$set(this.candidateValue, nodeId, val.join(","));
							users.push(data[j].id);
						}
					}
				}
				this.$set(this.selectVal, nodeId, selectVal);
				this.$set(this.dataForm.candidateList, nodeId, users);
			},
			handleClick(type) {
				if (type === "cancel") return uni.navigateBack();
				if (!this.isCandidate) delete this.dataForm["candidateList"];
				if (!this.config.hasSign) delete this.dataForm.signImg;
				if (!this.config.hasFreeApprover) delete this.dataForm.freeApproverType;
				this.dataForm.copyIds = !!this.dataForm.copyIds ?
					this.dataForm.copyIds.join() :
					"";
				let query = {
					...this.dataForm,
					eventType: this.config.eventType,
					candidateType: this.candidateType,
				};
				if (type === "transfer") {
					if (this.dataForm.freeApproverUserId.length <= 0)
						return this.$u.toast(`转审人员不能为空`);
				}
				if (this.config.hasFreeApprover) {
					if (this.dataForm.freeApproverUserId.length <= 0)
						return this.$u.toast(`加签人员不能为空`);
				}
				if (this.isBranch && this.branchList.length > 0) {
					if (this.dataForm.branchList.length <= 0)
						return this.$u.toast(`分支选择不能为空`);
				}
				if (this.isCandidate) {
					query.candidateList = this.dataForm.candidateList;
					if (Object.keys(this.dataForm.candidateList).length == 0)
						return this.$u.toast(`候选人不能为空`);
				}
				if (this.config.eventType !== "submit" && this.isSign) {
					if (this.dataForm.signImg.length <= 0)
						return this.$u.toast(`签名不能为空`);
				}
				if (this.config.type == 1) {
					query = {
						...query,
						...this.config.formData,
					};
				}
				query.fileList = query.fileList;
				uni.$emit("operate", query);
				uni.navigateBack();
			},
		},
	};
</script>

<style lang="scss">
	page {
		height: 100%;
		padding-bottom: 90rpx;
	}

	.form-item {
		&::after {
			border-bottom-width: 0px !important;
		}
	}

	.reject {
		text-align: center;
		background-color: #fff;
		padding: 20rpx 32rpx;
		border-bottom: 2rpx solid #f4f6f8;

		.active {
			color: #1890ff;
		}
	}

	.operate-v {
		height: 100vh;
		overflow-y: scroll;
		display: flex;
		flex-direction: column;

		.linzen-wrap {
			.u-form {
				.form-item-box {
					.u-form-item {
						z-index: 0;
					}
				}
			}
		}

		.flowBefore-actions {
			z-index: 0;
		}

		.operate-area {
			flex: 1;
		}

		.hd {
			border-bottom: 2rpx solid #f2f2f2;
		}

		.add {
			width: 100%;
			border-bottom: 2rpx solid #f2f2f2;
			display: flex;
			flex-direction: row;
			justify-content: flex-start;
			padding: 20rpx;

			.add-inner {
				width: 30%;
			}
		}

		.commonList {
			width: 100%;

			.item {
				width: 100%;
				padding: 20rpx;
				border-bottom: 2rpx solid #f2f2f2;

				.txt {
					width: 90%;
					word-wrap: break-word;
				}

				.icon {
					width: 60rpx;
					text-align: right;
				}
			}

			.notData-box {
				width: 100%;
				height: 100%;
				justify-content: center;
				align-items: center;
				padding-top: 100rpx;

				.notData-inner {
					width: 280rpx;
					height: 308rpx;
					align-items: center;

					.iconImg {
						width: 100%;
						height: 100%;
					}

					.notData-inner-text {
						padding: 30rpx 0;
						color: #909399;
					}
				}
			}
		}

		.innerPopup-box {
			justify-content: space-between;
			box-sizing: border-box;
			overflow: hidden;

			.innerPopup-hd {
				height: 80rpx;
				background-color: #f2f2f2;
				border-bottom: 1px solid #f0f2f6;
				padding: 0 20rpx;
				line-height: 80rpx;
			}

			.innerPopup-content {
				padding: 0 20rpx;
			}

			.btn-box {
				width: 100%;

				.btn {
					width: 100%;
					height: 72rpx;
					border-radius: 0 0 0 24rpx !important;
					font-size: 28rpx;

					&::after {
						border-radius: 0px !important;
					}
				}

				.btn2 {
					background-color: #409eff;
					color: #fff;
					border-radius: 0 0 24rpx 0 !important;
				}
			}
		}
	}
</style>