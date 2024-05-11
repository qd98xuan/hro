<template>
	<view>
		<view class="page_v u-flex-col">
			<view>
				<view v-if="show" v-for="(item,index) in signImg" :key="index" :class="item.isDefault ? 'active' : '' "
					class="lists_box" @longpress="handleTouchStart(item,index)">
					<view class="signImgBox">
						<image :src="item.signImg" mode="scaleToFill" class="signImg"></image>
					</view>
					<view class="icon-checked-box" v-if="item.isDefault">
						<view class="icon-checked">
							<u-icon name="checkbox-mark" color="#fff" size="28"></u-icon>
						</view>
					</view>
					<view class="sign-mask" v-if="!item.isDefault && item.isSet" :id="index">
						<view class="sign-mask-btn">
							<u-button@click.prevent="del(item.id,index)">删除</u-button>
								<u-button type="primary" @click.prevent="setDefault(item.id,index)">设为默认</u-button>
						</view>
					</view>
				</view>
			</view>
			<linzen-sign ref="signRef" @input="signData" :showBtn="false" />
			<NoData v-if="!show"></NoData>
		</view>
		<view class="flowBefore-actions">
			<template>
				<u-button class="buttom-btn" type="primary" @click='addSign'>添加签名</u-button>
			</template>
		</view>

	</view>
</template>
<script>
	import NoData from '@/components/noData.vue'
	import {
		createSignImg,
		setDefSignImg,
		delSignImg
	} from '@/api/common.js'
	export default {
		components: {
			NoData
		},
		data() {
			return {
				value: '',
				show: true,
				signImg: [],
				isSet: false
			}
		},
		methods: {
			addSign() {
				this.$refs.signRef.addSign();
			},
			init(data) {
				let signImg = JSON.parse(JSON.stringify(data))
				this.show = signImg.length > 0 ? true : false
				this.signImg = signImg.map(o => ({
					isSet: false,
					...o
				}))
			},
			signData(e) {
				if (e) {
					let data = {
						'signImg': e,
						'isDefault': 0
					}
					createSignImg(data).then((res) => {
						this.$emit('pagination')
					})
				}
			},
			handleTouchStart(item, index) {
				this.signImg.map((o, i) => {
					o.isSet = false
				})
				item.isSet = true
			},
			del(id, index) {
				delSignImg(id, index).then((res) => {
					this.signImg.splice(index, 1)
				})
			},
			setDefault(id, index) {
				let userInfo = uni.getStorageSync('userInfo')
				setDefSignImg(id).then((res) => {
					this.signImg.map((o, i) => {
						o.isDefault = false;
						if (index == i) {
							o.isDefault = true
							o.isSet = false
							userInfo.signImg = o.signImg
							uni.setStorageSync('userInfo', userInfo)
						}
					})

				})
			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.page_v {
		height: 100%;
		padding: 0 20rpx;

		.active {
			border: 1rpx solid #2979FF;
			color: #2979FF;

			.icon-zen-organization {
				&::before {
					color: #2979FF !important;
				}
			}
		}

		.sign-mask {
			width: 100%;
			height: 220rpx;
			background: rgba(0, 0, 0, .3);
			position: absolute;
			top: 0;
			border-radius: 12rpx;
			display: flex;
			align-items: center;
			flex-direction: column;
			justify-content: center;

			.sign-mask-btn {
				width: 60%;
				display: flex;
			}
		}

		.lists_box {
			width: 100%;
			height: 200rpx;
			border-radius: 8rpx;
			position: relative;
			display: flex;
			flex-direction: column;
			justify-content: center;
			background-color: #FFFFFF;
			margin-top: 20rpx;

			.signImgBox {
				width: 100%;
				height: 100%;
				padding: 10rpx 10rpx;
				text-align: center;

				.signImg {
					width: 100%;
					height: 100%;
				}
			}

			.icon-checked-box {
				display: flex;
				width: 140rpx;
				height: 80rpx;
				position: absolute;
				transform: scale(0.9);
				right: -4rpx;
				bottom: -2rpx;
				flex-direction: row;
				align-items: center;


				.icon-checked {
					width: 44rpx;
					height: 44rpx;
					border: 40rpx solid #1890ff;
					border-left: 40rpx solid transparent;
					border-top: 40rpx solid transparent;
					border-bottom-right-radius: 12rpx;
					position: absolute;
					transform: scale(0.95);
					right: -8rpx;
					bottom: -6rpx;
				}
			}
		}
	}
</style>