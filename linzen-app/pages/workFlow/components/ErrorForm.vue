<template>
	<view class="dataForm-v">
		<u-popup mode="left" :popup="false" v-model="show" length="auto" @close="close" width="100%">
			<view class="diyTitle u-flex">
				<uni-icons type="back" size="27" class="uni-btn-icon" @click="black"></uni-icons>
				<view class="txt">
					异常处理
				</view>
			</view>
			<view class="linzen-wrap linzen-wrap-form">
				<u-form :model="errorDataForm" ref="errorDataForm" :errorType="['toast']" label-position="left"
					label-width="150" label-align="left">
					<u-form-item prop="errorDataForm" v-for="(item,index) in list" :key="index" :label="item.nodeName"
						required>
						<linzen-user-select v-model="errorDataForm.errorRuleUserList[item.nodeId]" :multiple="true"
							placeholder="异常处理人员不能为空">
						</linzen-user-select>
					</u-form-item>
				</u-form>
				<view class="buttom-actions">
					<u-button class="buttom-btn" @click="cancel">取消</u-button>
					<u-button class="buttom-btn" type="primary" @click="submit">确定</u-button>
				</view>
			</view>
		</u-popup>
	</view>
</template>
<script>
	export default {
		props: {
			// 通过双向绑定控制组件的弹出与收起
		},
		data() {
			return {
				errorDataForm: {
					errorRuleUserList: {},
				},
				list: [],
				show: false,
				query: {}
			};
		},
		methods: {
			init(list, eventType, query) {
				this.query = query
				this.show = true
				this.list = list
				this.eventType = eventType
				this.list.map(o => {
					this.$set(this.errorDataForm.errorRuleUserList, o.nodeId, [])
				})
			},
			submit() {
				const query = {
					...this.query,
					errorRuleUserList: this.errorDataForm.errorRuleUserList,
					eventType: this.eventType
				}
				for (let rules in this.errorDataForm.errorRuleUserList) {
					if (this.errorDataForm.errorRuleUserList[rules].length <= 0) {
						return this.$u.toast(
							`异常处理人员不能为空`
						)
					}
				}
				this.$emit('submitErrorForm', query);
			},
			cancel() {
				this.close()
			},
			black() {
				this.close()
			},
			close() {
				this.show = false
			}
		}
	};
</script>
<style lang="scss" scoped>
	.dataForm-v {
		.diyTitle {
			height: 80rpx;
			padding: 14rpx 6rpx;
			text-align: center;
			justify-content: flex-start;

			.uniui-back {
				font-size: 27px;
				font-weight: lighter;
			}

			.txt {
				flex: 0.95;
			}
		}

		.linzen-wrap,
		.linzen-wrap-form {
			padding: 0;
		}
	}
</style>