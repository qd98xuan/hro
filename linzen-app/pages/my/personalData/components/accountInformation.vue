<template>
	<view class="personalData-v">
		<u-cell-group class="" style="padding: 0 20rpx;" :border-bottom="false" :border="false">
			<u-cell-item title="账户" :value="data.account" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="所属组织" :value="data.organize" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="直属主管" :value="data.manager" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="岗位" :value="data.position" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="职级" :value="data.ranks" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="角色" :value="data.roleId" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="注册时间" :value="creatorTime" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="上次登录" :value="prevLogTime" :arrow="false" :title-style="titleStyle"></u-cell-item>
			<u-cell-item title="入职时间" :value="entryDate" :arrow="false" :title-style="titleStyle"
				:border-bottom="false"></u-cell-item>
		</u-cell-group>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				gender: '',
				birthday: '',
				prevLogTime: '',
				creatorTime: '',
				entryDate: '',
				titleStyle: {
					color: '#303133'
				},
				data: {}
			}
		},
		computed: {
			baseURL() {
				return this.define.baseURL
			},
		},
		onLoad() {

		},
		methods: {
			init(data) {
				this.data = data || {}
				this.creatorTime = this.data.creatorTime ? this.$u.timeFormat(this.data.creatorTime,
					'yyyy-mm-dd hh:MM') : ''
				this.prevLogTime = this.data.prevLogTime ? this.$u.timeFormat(this.data.prevLogTime,
					'yyyy-mm-dd hh:MM') : ''
				this.entryDate = this.data.entryDate ? this.$u.timeFormat(this.data.entryDate,
					'yyyy-mm-dd hh:MM') : ''
			},
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.personalData-v {
		background-color: #fff;
	}

	/deep/.u-cell {
		height: 112rpx;
		padding: 20rpx 0;
	}
</style>