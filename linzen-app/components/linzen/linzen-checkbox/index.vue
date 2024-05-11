<template>
	<view :class="'linzen-checkbox linzen-checkbox-right'">
		<view v-if="direction === 'horizontal'">
			<u-checkbox-group @change="onChange" :disabled="disabled" style="display: flow-root;">
				<u-checkbox v-model="item.checked" label-size='26' v-for="(item, index) in newOptions" :key="index"
					:name="item[props.value]">
					{{ item[props.label] }}
				</u-checkbox>
			</u-checkbox-group>
		</view>
		<!-- 竖 -->
		<view class="u-select__body u-select__body__multiple" v-else>
			<scroll-view :scroll-y="true" style="height: 100%">
				<u-checkbox-group @change="onChange" wrap :disabled="disabled">
					<u-checkbox v-model="item.checked" :name="item[props.value]" v-for="(item,i) in newOptions"
						:key="i">
						{{ item[props.label] }}
					</u-checkbox>
				</u-checkbox-group>
			</scroll-view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'linzen-checkbox',
		model: {
			prop: 'value',
			event: 'input'
		},
		props: {
			value: {
				type: Array,
				default: () => []
			},
			direction: {
				type: String,
				default: "horizontal"
			},
			options: {
				type: Array,
				default: () => []
			},
			props: {
				type: Object,
				default: () => ({
					label: 'fullName',
					value: 'id'
				})
			},
			disabled: {
				type: Boolean,
				default: false
			}
		},
		data() {
			return {
				newOptions: [],
			}
		},
		watch: {
			value(val) {
				if (val === '' || val.length < 1) return this.setColumnData()
				this.setDefault()
			},
			options(val) {
				this.setColumnData()
			}
		},
		created() {
			this.setColumnData()
		},
		methods: {
			onChange(value) {
				let selectData = []
				for (let i = 0; i < this.newOptions.length; i++) {
					for (let o = 0; o < value.length; o++) {
						if (value[o] === this.newOptions[i][this.props.value]) {
							selectData.push(this.newOptions[i])
						}
					}
				}
				this.$emit('input', value)
				this.$emit('change', value, selectData)
			},
			setColumnData() {
				this.newOptions = this.options.map(o => ({
					...o,
					checked: false
				}))
				this.setDefault()
			},
			setDefault() {
				if (!this.value) return
				outer: for (let i = 0; i < this.value.length; i++) {
					inner: for (let j = 0; j < this.newOptions.length; j++) {
						if (this.value[i] === this.newOptions[j][this.props.value]) {
							this.newOptions[j].checked = true
							break inner
						}
					}
				}
			},
		}
	}
</script>
<style lang="scss" scoped>
	.linzen-checkbox {
		width: 100%;

		/deep/.u-checkbox-group {
			padding: 0;

			.u-checkbox {
				max-width: 100%;
				justify-content: flex-end;

				.u-checkbox__label {
					max-width: 500rpx;
					word-break: break-all;
					overflow: hidden;
					white-space: nowrap;
					text-overflow: ellipsis;
				}
			}
		}

		/deep/.u-select__body__multiple .u-checkbox-group .u-checkbox__label {
			flex: none;
			margin-left: 20px;
		}

		&.linzen-checkbox-right {
			text-align: right;
		}

	}
</style>