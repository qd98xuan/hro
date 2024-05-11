<template>
	<view class="linzen-date-range">
		<view class="u-flex">
			<linzen-date-time :type="projectKey" v-model="startValue" :placeholder="placeholder" :disabled="disabled"
				inputType="text" scene="searchList" :format="format" @change="change" :defaultTime="startDefaultTime"
				selectType='start' :key="key" ref="dateTime">
			</linzen-date-time>
			<view class="u-p-l-10 u-p-r-10">
				至
			</view>
			<linzen-date-time :type="projectKey" v-model="endValue" :placeholder="placeholder" :disabled="disabled"
				inputType="text" scene="searchList" :format="format" @change="change" :defaultTime="endDefaultTime"
				selectType='end' :key="key+1" ref="dateTime">
			</linzen-date-time>
		</view>
	</view>
</template>
<script>
	export default {
		name: 'linzen-date-range',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				type: Array,
				default: () => []
			},
			placeholder: {
				type: String,
				default: '请选择日期范围'
			},
			disabled: {
				type: Boolean,
				default: false
			},
			format: {
				type: String,
				default: 'yyyy-MM-dd HH:mm:ss'
			},
			projectKey: {
				type: String,
				default: 'date'
			}
		},
		data() {
			return {
				startDefaultTime: '',
				endDefaultTime: '',
				startValue: '',
				endValue: '',
				datetimerange: [],
				datetimerangeObj: {},
				key: +new Date()
			}
		},
		watch: {
			value: {
				immediate: true,
				handler(val) {
					if (Array.isArray(val) && val.length > 0) {
						this.startValue = val[0]
						this.endValue = val[1]
					} else {
						this.startValue = ''
						this.endValue = ''
					}
				}
			},
		},
		methods: {
			change(e, type) {
				this.datetimerange = []
				if (type == 'start') {
					const format = 'yyyy-mm-dd hh:MM:ss'
					this.$set(this.datetimerangeObj, type, e)
					this.$refs.dateTime.defaultTime = this.projectKey == 'time' ? this.datetimerangeObj['start'] : this.$u
						.timeFormat(this.datetimerangeObj['start'], format)
					this.handelVal()
				} else {
					this.$set(this.datetimerangeObj, type, e)
					this.handelVal()
				}
			},
			handelVal() {
				this.datetimerange.unshift(this.datetimerangeObj['start'])
				this.datetimerange.push(this.datetimerangeObj['end'])
				if (this.datetimerange[0] > this.datetimerange[1]) {
					this.$u.toast('开始不能大于结束')
					setTimeout(() => {
						this.startValue = ""
						this.endValue = ""
						this.datetimerangeObj = {}
						this.datetimerange = []
						this.key = +new Date()
					}, 500)
					return
				}
				this.$emit('input', this.datetimerange)
			}
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-date-range {
		width: 100%;

		/deep/.u-input__input {
			text-align: center !important;
			font-size: 24rpx;
		}

		/deep/.uni-date__x-input {
			height: 100% !important;
		}

		/deep/.uni-datetime-picker--btn {
			border-radius: 10rpx;
		}
	}
</style>