<template>
	<u-input input-align='right' :value="value" placeholder="系统自动生成" disabled></u-input>
</template>

<script>
	export default {
		name: 'linzen-open-data',
		props: {
			value: {
				type: String,
				default: ''
			},
			/**
			 * createUser - 当前用户
			 * createTime - 当前时间
			 * currOrganize - 所属组织
			 * currPosition - 所属岗位
			 * currDept - 所属部门
			 * billRule - 单据规则
			 */
			type: {
				type: String,
				default: ''
			},
			showLevel: {
				type: String,
				default: 'last'
			},
		},
		data() {
			return {
				innerValue: '',
				userInfo: '',
				placeholder: ''
			}
		},
		watch: {
			showLevel() {
				this.setDefault()
			}
		},
		created() {
			this.userInfo = uni.getStorageSync('userInfo') || {}
			this.setDefault()
		},
		methods: {
			setDefault() {
				if (this.type === 'createUser') {
					this.innerValue = this.userInfo.userName + '/' + this.userInfo.userAccount
					if (!this.userInfo.userName && !this.userInfo.userAccount) this.innerValue = ""
				}
				if (this.type === 'createTime') {
					this.innerValue = this.$u.timeFormat(new Date(), 'yyyy-mm-dd hh:MM:ss')
				}
				if (this.type === 'currOrganize') {
					this.innerValue = this.showLevel === 'last' ? this.userInfo.departmentName : this.userInfo.organizeName
				}
				if (this.type === 'currPosition') {
					this.innerValue = this.userInfo.positionName || ""
				}
				if (this.type === 'billRule') {
					this.placeholder = "系统自动生成"
				}
			}
		}
	}
</script>