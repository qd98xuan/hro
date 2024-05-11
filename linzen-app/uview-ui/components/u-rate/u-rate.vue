<template>
	<view class="u-rate" :id="elId" ref="u-rate">
		<view class="u-rate__content" @touchmove.stop="touchMove">
			<view class="u-rate__content__item" v-for="(item, index) in count" :key="index" :class="[elClass]">
				<view class="u-rate__content__item__icon-wrap" ref="u-rate__content__item__icon-wrap"
					@tap.stop="clickHandler(index + 1, $event)">
					<u-icon :name="Math.floor(activeIndex) > index? activeIcon: inactiveIcon"
						:color="disabled? '#c8c9cc': Math.floor(activeIndex) > index? activeColor: inactiveColor"
						:custom-style="{padding:`0 ${gutter / 2 + 'rpx'}`}" :size="size"></u-icon>
				</view>
				<view v-if="allowHalf" @tap.stop="clickHandler(index + 1, $event)"
					class="u-rate__content__item__icon-wrap u-rate__content__item__icon-wrap--half"
					:style="[{width: starWidth / 2 + 'px',}]" ref="u-rate__content__item__icon-wrap">
					<u-icon :name="Math.ceil(activeIndex) > index? activeIcon: inactiveIcon"
						:color="disabled? '#c8c9cc': Math.ceil(activeIndex) > index? activeColor: inactiveColor"
						:custom-style="{padding: `0 ${gutter / 2 + 'rpx'}`}" :size="size"></u-icon>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	/**
	 * rate 评分
	 * @description 该组件一般用于满意度调查，星型评分的场景
	 * @tutorial https://www.uviewui.com/components/rate.html
	 * @property {String Number} count 最多可选的星星数量（默认5）
	 * @property {String Number} current 默认选中的星星数量（默认0）
	 * @property {Boolean} disabled 是否禁止用户操作（默认false）
	 * @property {String Number} size 星星的大小，单位rpx（默认32）
	 * @property {String} inactive-color 未选中星星的颜色（默认#b2b2b2）
	 * @property {String} active-color 选中的星星颜色（默认#FA3534）
	 * @property {String} active-icon 选中时的图标名，只能为uView的内置图标（默认star-fill）
	 * @property {String} inactive-icon 未选中时的图标名，只能为uView的内置图标（默认star）
	 * @property {String} gutter 星星之间的距离（默认10）
	 * @property {String Number} min-count 最少选中星星的个数（默认0）
	 * @property {Boolean} allow-half 是否允许半星选择（默认false）
	 * @event {Function} change 选中的星星发生变化时触发
	 * @example <u-rate :count="count" :current="2"></u-rate>
	 */

	export default {
		name: 'u-rate',
		props: {
			// 用于v-model双向绑定选中的星星数量
			// 1.4.5版新增
			value: {
				type: [Number, String],
				default: -1
			},
			// 要显示的星星数量
			count: {
				type: [Number, String],
				default: 5
			},
			// 当前需要默认选中的星星(选中的个数)
			// 1.4.5后通过value双向绑定，不再建议使用此参数
			current: {
				type: [Number, String],
				default: 0
			},
			// 是否不可选中
			disabled: {
				type: Boolean,
				default: false
			},
			// 星星的大小，单位rpx
			size: {
				type: [Number, String],
				default: 32
			},
			// 未选中时的颜色
			inactiveColor: {
				type: String,
				default: '#b2b2b2'
			},
			// 选中的颜色
			activeColor: {
				type: String,
				default: '#FA3534'
			},
			// 星星之间的间距，单位rpx
			gutter: {
				type: [Number, String],
				default: 10
			},
			// 最少能选择的星星个数
			minCount: {
				type: [Number, String],
				default: 0
			},
			// 是否允许半星(功能尚未实现)
			allowHalf: {
				type: Boolean,
				default: false
			},
			// 选中时的图标(星星)
			activeIcon: {
				type: String,
				default: 'star-fill'
			},
			// 未选中时的图标(星星)
			inactiveIcon: {
				type: String,
				default: 'star'
			},
			// 自定义扩展前缀，方便用户扩展自己的图标库
			customPrefix: {
				type: String,
				default: 'uicon'
			},
			colors: {
				type: Array,
				default () {
					return []
				}
			},
			icons: {
				type: Array,
				default () {
					return []
				}
			}
		},
		data() {
			return {
				// 生成一个唯一id，否则一个页面多个评分组件，会造成冲突
				elId: this.$u.guid(),
				elClass: this.$u.guid(),
				starBoxLeft: 0, // 评分盒子左边到屏幕左边的距离，用于滑动选择时计算距离
				// 当前激活的星星的index，如果存在value，优先使用value，因为它可以双向绑定(1.4.5新增)
				activeIndex: this.value,
				starWidth: 0, // 每个星星的宽度
				moving: false,
			}
		},
		watch: {
			current(val) {
				this.activeIndex = val
			},
			value(val) {
				this.activeIndex = val;
			},
		},
		mounted() {
			this.$nextTick(() => {
				this.getElRectById()
				this.getElRectByClass()
			})
		},
		computed: {
			decimal() {
				if (this.disabled) {
					return this.activeIndex * 100 % 100
				} else if (this.allowHalf) {
					return 50
				}
			},
			elActiveColor() {
				const len = this.colors.length
				// 如果有设置colors参数(此参数用于将图标分段，比如一共5颗星，colors传3个颜色值，那么根据一定的规则，2颗星可能为第一个颜色
				// 4颗星为第二个颜色值，5颗星为第三个颜色值)
				if (len && len <= this.count) {
					const step = Math.round(this.activeIndex / Math.round(this.count / len))
					if (step < 1) return this.colors[0]
					if (step > len) return this.colors[len - 1]
					return this.colors[step - 1]
				}
				return this.activeColor
			}
		},
		methods: {
			// 获取评分组件盒子的布局信息
			getElRectById() {
				// uView封装的获取节点的方法，详见文档
				this.$uGetRect('#' + this.elId).then(res => {
					this.starBoxLeft = res.left
				})
			},
			// 获取单个星星的尺寸
			getElRectByClass() {
				// uView封装的获取节点的方法，详见文档
				this.$uGetRect('.' + this.elClass).then(res => {
					this.starWidth = res.width
				})
			},
			// 手指滑动
			touchMove(e) {
				if (!e.changedTouches[0]) return
				this.preventEvent(e);
				const x = e.changedTouches[0].pageX;
				this.getActiveIndex(x);
			},
			// 停止滑动
			touchEnd(e) {
				if (!e.changedTouches[0]) return
				this.preventEvent(e);
				const x = e.changedTouches[0].pageX;
				this.getActiveIndex(x);
			},
			common() {
				// uView封装的获取节点的方法，详见文档
				this.$uGetRect('#' + this.elId).then(res => {
					this.starBoxLeft = res.left
				})
				// uView封装的获取节点的方法，详见文档
				this.$uGetRect('.' + this.elClass).then(res => {
					this.starWidth = res.width
				})
			},
			// 通过点击，直接选中
			async clickHandler(i, e) {
				if (this.starWidth == 0 || this.starBoxLeft == 0) await this.common()
				if (uni.$u.os() === "ios" && this.moving) return
				this.preventEvent(e);
				let x = 0;
				// 点击时，在nvue上，无法获得点击的坐标，所以无法实现点击半星选择
				// #ifndef APP-NVUE
				x = e.changedTouches[0].pageX;
				// #endif
				// #ifdef APP-NVUE
				// nvue下，无法通过点击获得坐标信息，这里通过元素的位置尺寸值模拟坐标
				x = i * this.starWidth + this.starBoxLeft;
				// #endif
				this.getActiveIndex(x, true)
			},
			getActiveIndex(x, isClick = false) {
				if (this.disabled) return
				const allRateWidth = this.starWidth * this.count + this.starBoxLeft;
				// 滑动点相对于评分盒子左边的距离
				x = this.rangeFun(this.starBoxLeft, allRateWidth, x) - this.starBoxLeft
				const distance = x;
				let index;
				if (this.allowHalf) {
					index = Math.floor(distance / this.starWidth);
					// 取余，判断小数的区间范围
					const decimal = distance % this.starWidth;
					if (decimal <= this.starWidth / 2 && decimal > 0) {
						index += 0.5;
					} else if (decimal > this.starWidth / 2) {
						index++;
					}
				} else {
					index = Math.floor(distance / this.starWidth);
					// 取余，判断小数的区间范围
					const decimal = distance % this.starWidth;
					// 非半星时，只有超过了图标的一半距离，才认为是选择了这颗星
					if (isClick) {
						if (decimal > 0) index++;
					} else {
						if (decimal > this.starWidth / 2) index++;
					}
				}
				this.activeIndex = Math.min(index, this.count);
				// 对最少颗星星的限制
				if (this.activeIndex < this.minCount) this.activeIndex = this.minCount;
				// 设置延时为了让click事件在touchmove之前触发
				setTimeout(() => {
					this.moving = true;
				}, 10);
				// 一定时间后，取消标识为移动中状态，是为了让click事件无效
				setTimeout(() => {
					this.moving = false;
				}, 10);
				this.emitEvent()
			},
			// 发出事件
			emitEvent() {
				// 发出change事件
				this.$emit('change', this.activeIndex)
				// 同时修改双向绑定的value的值
				if (this.value != -1) {
					this.$emit('input', this.activeIndex)
				}
			},
			showDecimalIcon(index) {
				return this.disabled && parseInt(this.activeIndex) === index
			},
			rangeFun(min = 0, max = 0, value = 0) {
				return Math.max(min, Math.min(max, Number(value)))
			}
		},
	}
</script>

<style scoped lang="scss">
	@import "../../libs/css/style.components.scss";
	$u-rate-margin: 0 !default;
	$u-rate-padding: 0 !default;
	$u-rate-item-icon-wrap-half-top: 0 !default;
	$u-rate-item-icon-wrap-half-left: 0 !default;

	.u-rate {
		display: flex;
		align-items: center;
		margin: $u-rate-margin;
		padding: $u-rate-padding;
		/* #ifndef APP-NVUE */
		touch-action: none;
		/* #endif */

		&__content {
			display: flex;

			&__item {
				position: relative;

				&__icon-wrap {
					&--half {
						position: absolute;
						overflow: hidden;
						top: $u-rate-item-icon-wrap-half-top;
						left: $u-rate-item-icon-wrap-half-left;
					}
				}
			}
		}
	}

	.u-icon {
		/* #ifndef APP-NVUE */
		box-sizing: border-box;
		/* #endif */
	}
</style>