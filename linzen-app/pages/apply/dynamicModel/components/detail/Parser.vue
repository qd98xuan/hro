<template>
	<u-form :model="formValue" ref="dataForm" :errorType="['toast']"
		:label-position="formConf.labelPosition==='top'?'top':'left'"
		:label-width="formConf.labelWidth<=120 ? 120*1.5:formConf.labelWidth*1.5"
		:label-align="formConf.labelPosition==='right'?'right':'left'" :class='formConf.className'>
		<template v-for="(item, index) in formConf.fields">
			<Item :key="item.__config__.renderKey" :item="item" :formConf="formConf" :class="item.__config__.className"
				:formValue="formValue" :ref="item.__vModel__?item.__vModel__: undefined" @toDetail="toDetail"
				@clickIcon='clickIcon' />
		</template>
		<u-modal v-model="show" :content="content" width='70%' border-radius="16" :content-style="contentStyle"
			:titleStyle="titleStyle" :confirm-style="confirmStyle" :title="title">
		</u-modal>
	</u-form>
</template>
<script>
	import Item from './Item'
	export default {
		components: {
			Item
		},
		props: {
			formConf: {
				type: Object,
				required: true
			},
			formValue: {
				type: Object,
			},
			loading: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				show: false,
				content: '',
				contentStyle: {
					fontSize: '28rpx',
					padding: '20rpx',
					lineHeight: '44rpx',
					textAlign: 'left'
				},
				titleStyle: {
					padding: '20rpx'
				},
				confirmStyle: {
					height: '80rpx',
					lineHeight: '80rpx',
				},
				title: '提示',
			}
		},
		methods: {
			clickIcon(e) {
				this.content = e.tipLabel || e.__config__.tipLabel || e.helpMessage
				this.title = e.__config__.label
				this.show = true
			},
			toDetail(item) {
				this.$emit('toDetail', item)
			}
		}
	}
</script>