<template>
	<view class="calendar-v" :key="key">
		<calendar :lunar="config.showLunarCalendar" :showMonth="true" @change="change" @monthSwitch="monthSwitch"
			@initdate="initdate" />
		<view class="calendar-b">
			<view class="lunar linzen-card u-m-t-20">
				{{dateDay}}
				<div>{{changedate}}</div>
			</view>
			<u-line></u-line>
			<view class="">
				<scroll-view scroll-y="true" :style="scheduleList.length<3?'' :'height: 390rpx;'">
					<view v-for="(item,index) in scheduleList" :key="index" class="schedule-item">
						<u-swipe-action :index="index" :show="item.show" @click="removeList" @open="open"
							@content-click="goDetail(item.id,item.creatorUserId)">
							<view class="calendar-listBox startTime u-m-b-15 u-font-24 u-flex"
								:style="{background: item.color}">
								<text class="time-left">{{item.allDay?'全天':item.startTime}}</text>
								<view class="time-right u-flex-col">
									<view :class="item.content?'time-top':'time-title'">{{item.title}}</view>
									<view class="time-middle">{{item.content}}</view>
								</view>
							</view>
							<u-line></u-line>
						</u-swipe-action>
					</view>
				</scroll-view>
				<view :class="scheduleList.length<3?'lunar1':'addlunar'">
					<view @click="goDetail()" class="add-title">+添加日程内容</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import calendar from './calendar/uni-calendar.vue'
	import {
		List,
		delSchedule
	} from '@/api/portal/schedule.js'
	import {
		data
	} from '../../../../uview-ui/libs/mixin/mixin'
	export default {

		components: {
			direction: 'col',
			calendar,
		},
		props: {
			config: {
				type: Object,
				default: () => {}
			}
		},
		data() {
			return {
				showForm: false,
				horizontal: 'right',
				vertical: 'bottom',
				direction: 'horizontal',
				pattern: {
					color: '#7A7E83',
					backgroundColor: '#fff',
					selectedColor: '#007AFF',
					buttonColor: "#007AFF"
				},
				changedate: '',
				scheduleList: [],
				exhibitionList: [],
				startDate: '',
				endDate: '',
				dateDay: '',
				query: {},
				options: [{
					text: '删除',
					style: {
						backgroundColor: '#dd524d'
					}
				}],
				startTime: "",
				formVisible: false,
				userInfo: {},
				key: +new Date(),
				toDay: ''
			}
		},
		created() {
			this.userInfo = uni.getStorageSync('userInfo') || {}
		},
		methods: {
			/* 初始化请求 */
			initdate(cale, nowDate) {
				let canlender = cale.canlender;
				let weeks = cale.weeks;
				if (!this.toDay) {
					for (let i = 0; i < canlender.length; i++) {
						if (canlender[i].fullDate === nowDate.fullDate) {
							let day = this.toChinaDay(nowDate.day)
							this.toDay = nowDate.fullDate
							this.dateTime = nowDate.fullDate
							this.dateDay = nowDate.month + '月' + nowDate.date + '日' + "  周" + day +
								" (今天)"
							this.changedate = ''
							if (this.config.showLunarCalendar) this.changedate = '农历  ' + canlender[i].lunar.IMonthCn +
								canlender[i].lunar.IDayCn;
							break;
						}
					}
				}
				let data = {
					weeks: weeks,
					canlender: canlender
				}
				this.handleScheduleList(data)
				if (this.config.refresh.autoRefresh) {
					setInterval(async () => {
						this.key = +new Date()
					}, this.config.refresh.autoRefreshTime * 60000)
				}
			},
			handleScheduleList(data) {
				let canlender = data.canlender
				let startTime = this.startDate = canlender[0].lunar.cYear + '-' + canlender[0].lunar.cMonth + '-' +
					canlender[0].lunar
					.cDay;
				let endTime = this.endDate = canlender[canlender.length - 1].lunar.cYear + '-' + canlender[canlender
						.length - 1].lunar
					.cMonth + '-' + canlender[canlender.length - 1].lunar.cDay;
				let query = {
					startTime: startTime,
					endTime: endTime,
					dateTime: data.fulldate || this.toDay
				}
				List(query).then(res => {
					let signList = res.data.signList;
					if (res.data.todayList) {
						this.scheduleList = res.data.todayList.map(o => ({
							...o,
							show: false
						}));
					}
					for (let i = 0; i < 6; i++) {
						for (let j = 0; j < data.weeks[i].length; j++) {
							let cYear = data.weeks[i][j].lunar.cYear < 10 ? '0' + Number(data.weeks[i][j].lunar
									.cYear) :
								data.weeks[i][j].lunar.cYear;
							let cMonth = data.weeks[i][j].lunar.cMonth < 10 ? '0' + Number(data.weeks[i][j].lunar
									.cMonth) :
								data.weeks[i][j].lunar.cMonth;
							let cDay = data.weeks[i][j].lunar.cDay < 10 ? '0' + Number(data.weeks[i][j].lunar
									.cDay) :
								data.weeks[i][j].lunar.cDay;
							let date = parseInt(cYear + '' + cMonth + '' + cDay)
							data.weeks[i][j].isSign = signList[date] == 0 ? false : true;
						}
					}
				})
			},
			change(e) {
				let weeks = e.cale.weeks;
				let canlender = e.cale.canlender;
				let lunar = e.lunar;
				lunar.cMonth = lunar.cMonth < 10 ? '0' + Number(lunar.cMonth) : lunar.cMonth
				lunar.cDay = lunar.cDay < 10 ? '0' + Number(lunar.cDay) : lunar.cDay
				let allDay = lunar.lYear + '-' + lunar.cMonth + '-' + lunar.cDay
				this.toDay = allDay
				let srt = this.time(e.fulldate)
				let day = this.toChinaDay(lunar.nWeek)
				this.startTime = new Date(e.fulldate).getTime()
				this.dateDay = lunar.cMonth + '月' + lunar.cDay + '日' + "  周" + day +
					srt
				this.changedate = ''
				if (this.config.showLunarCalendar) this.changedate = '农历  ' + lunar.IMonthCn + lunar.IDayCn;
				let data = {
					weeks: weeks,
					canlender: canlender,
					lunar: lunar,
					fulldate: e.fulldate
				}
				this.handleScheduleList(data)
			},
			monthSwitch(e) {},
			goDetail(id, creatorUserId) {
				if (this.config.platform === 'mp') return
				let type = false
				if (this.userInfo.userId == creatorUserId) {
					type = true
				} else {
					type = false
				}
				let idx = id ? id : ''
				if (idx) {
					uni.navigateTo({
						url: '/pages/portal/schedule/detail?id=' + idx + '&type=' + type
					})
				} else {
					uni.navigateTo({
						url: '/pages/portal/schedule/index?id=' + idx + '&startTime=' + this.startTime +
							'&duration=' +
							this.config.duration
					})
				}

			},
			open(index) {
				this.scheduleList[index].show = true;
				this.scheduleList.map((val, idx) => {
					if (index != idx) this.scheduleList[idx].show = false;
				})
			},
			removeList(index, index1) {
				const item = this.scheduleList[index];
				delSchedule(item.id).then(res => {
					this.scheduleList[index].show = false;
					this.$u.toast('删除成功')
					this.scheduleList.splice(index, 1);
					this.$router.go(0)
				})
			},
			toChinaDay(d) { // 日 => \u65e5
				var s
				switch (d) {
					case 1:
						s = '一';
						break
					case 2:
						s = '二';
						break
						break
					case 3:
						s = '三';
						break
					case 4:
						s = '四';
						break
					case 5:
						s = '五';
						break
					case 6:
						s = '六';
						break
					default:
						s = '日'
				}
				return (s)
			},
			time(date) {
				let time = new Date()
				if (new Date(date).getFullYear() == time.getFullYear() && new Date(date).getMonth() == time.getMonth()) {
					let time_str = "";
					if (new Date(date).getDate() === new Date().getDate()) {
						time_str = "  (今天)";
					} else if (new Date(date).getDate() === (new Date().getDate() - 1)) {
						time_str = "  (昨天)";
					} else if (new Date(date).getDate() === (new Date().getDate() + 1)) {
						time_str = "  (明天)";
					} else if (new Date(date).getDate() < new Date().getDate()) {
						time_str = "";
					}
					return time_str;
				}
				return ''

			}
		}
	}
</script>

<style lang="scss">
	page {
		background-color: #f0f2f6;
	}

	.calendar-v {
		.calendar-b {
			.lunar {
				background-color: #FFFFFF;
				padding: 40rpx 32rpx 20rpx;
				color: #303133;
				font-size: 24rpx;
			}

			.addlunar {
				height: 124rpx;
				line-height: 124rpx;
				border-top: 1rpx solid rgb(228, 231, 237);
			}

			.add-title {
				margin-left: 20rpx;
				font-size: 24rpx;
				color: #C0C0C0;
			}

			.lunar1 {
				height: 124rpx;
				line-height: 124rpx;
			}

			.calendar-listBox {
				padding: 16rpx 32rpx;
				background-color: #FFFFFF;
				height: 65px;

				.startTime {
					color: #9a9a9a;
				}
			}
		}

		.schedule-item {
			width: 100%;
			margin: 20rpx 0;
			padding: 0rpx 20rpx;
		}

	}

	.time-left {
		width: 15%;
		display: flex;
		justify-content: left;
		align-items: center;
		height: 100%;
		font-size: 28rpx;
		color: #303133;
	}

	.time-title {
		margin-top: 10rpx;
		margin-left: 20rpx;
		width: 100%;
		font-size: 28rpx;
		color: #303133;
		display: flex;
		justify-content: left;
		align-items: center;
		height: 100%;
	}

	.time-right {
		width: 80%;
		height: 100%;
		margin-left: 20rpx;
		border-left: 4rpx solid #2A7DFD;
		color: #606266;
	}

	.time-top {
		margin-top: 10rpx;
		margin-left: 20rpx;
		width: 100%;
		font-size: 28rpx;
		color: #303233;
	}

	.time-middle {
		width: 100%;
		margin-left: 20rpx;
		font-size: 24rpx;
		color: #606266;
	}
</style>