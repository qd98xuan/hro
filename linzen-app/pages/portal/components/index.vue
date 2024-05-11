<template>
	<view class="">
		<!-- 卡片 -->
		<block v-if="item.show && item.projectKey === 'card'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<view class="card-inner u-p-l-8 u-p-r-8 u-p-t-8">
							<Item v-for="(child, index) in item.children" :item="child" :key="index" />
						</view>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 排行榜 -->
		<block v-if="item.show && item.projectKey === 'rankList'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HRankList :config="item"></HRankList>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 文本 -->
		<block v-if="item.show && item.projectKey === 'text'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HText :config="item"></HText>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 图片 -->
		<block v-if="item.show && item.projectKey === 'image'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HImage :config="item"></HImage>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 轮播图 -->
		<block v-if="item.show && item.projectKey === 'carousel'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HCarousel :config="item"></HCarousel>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 视频 -->
		<block v-if="item.show && item.projectKey === 'video'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HVideo :config="item"></HVideo>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 图表 -->
		<block v-if="item.show && (item.projectKey === 'barChart' || item.projectKey === 'lineChart' || item.projectKey === 'pieChart'||
			item.projectKey=='radarChart' || item.projectKey === 'mapChart')">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HCharts :config="item" :key="key">
						</HCharts>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 我的待办 -->
		<block v-if="item.show && item.projectKey === 'todo'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HTodo :config="item" :key="key">
						</HTodo>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 常用功能 -->
		<block v-if="item.show && item.projectKey === 'dataBoard'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HDataBoard :config="item" :key="key">
						</HDataBoard>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 数据面板 -->
		<block v-if="item.show && item.projectKey === 'commonFunc'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HCommonFunc :config="item" :key="key">
						</HCommonFunc>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 时间轴 -->
		<block v-if="item.show && item.projectKey === 'timeAxis'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HTimeAxis :config="item"></HTimeAxis>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 表格 -->
		<block v-if="item.show && item.projectKey === 'tableList'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HTable :config="item">
						</HTable>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 日程 -->
		<block v-if="item.show && item.projectKey === 'schedule'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HSchedule :config="item" />
					</template>
				</HCard>
			</view>
		</block>
		<!-- 待办事项 -->
		<block v-if="item.show && item.projectKey === 'todoList'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HTodoList :config="item">
						</HTodoList>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 未读邮件 -->
		<block v-if="item.show && item.projectKey === 'email'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HEmail :config="item">
						</HEmail>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 公告通知 -->
		<block v-if="item.show && item.projectKey === 'notice'">
			<view class="u-m-b-20">
				<HCard :cardData="item">
					<template slot='content'>
						<HNotice :config="item">
						</HNotice>
					</template>
				</HCard>
			</view>
		</block>
		<!-- 标签 -->
		<block v-if="item.show && item.projectKey === 'tab'">
			<view class="u-m-b-20" style="background-color: #ffffff;">
				<u-tabs :list="item.children" name="title" :is-scroll="item.children.length>3?true:false"
					:current="tabCurrent" @change="onTabChange" :show-bar="item.type?false:true" :class="tabsClass"
					:inactive-color="item.type==='border-card'?' #9ea1a6':'#303133'"
					:active-item-style='activeItemStyle' :bg-color="item.type==='border-card'?'#f5f7fa':'#fff'">
				</u-tabs>
				<view v-for="(item,i) in item.children" :key='i' class="tab-inner u-p-l-8 u-p-r-8 u-p-b-8 u-p-t-8">
					<view v-show="i == tabCurrent">
						<Item v-for="(child, index) in item.children" :item="child" :key="key" />
					</view>
				</view>
			</view>
		</block>
	</view>
</template>
<script>
	import Item from './index'
	import HCard from './HCard'
	import HDataBoard from './HDataBoard'
	import HTable from './HTable'
	import HNotice from './HNotice'
	import HEmail from './HEmail'
	import HTodoList from './HTodoList'
	import HCharts from './HCharts'
	import HRankList from './HRankList'
	import HSchedule from './HSchedule'
	import HImage from './HImage'
	import HCarousel from './HCarousel'
	import HText from './HText'
	import HVideo from './HVideo'
	import HTodo from './HTodo'
	import HCommonFunc from './HCommonFunc'
	import HTimeAxis from './HTimeAxis'
	export default {
		name: 'Item',
		props: {
			item: {
				type: Object,
				default: () => ({})
			}
		},
		components: {
			Item,
			HCard,
			HDataBoard,
			HTable,
			HNotice,
			HEmail,
			HTodoList,
			HCharts,
			HRankList,
			HSchedule,
			HImage,
			HCarousel,
			HText,
			HVideo,
			HTodo,
			HCommonFunc,
			HTimeAxis
		},
		data() {
			return {
				cardData: {},
				current: 0,
				tabCurrent: 0,
				key: +new Date(),
				tabsClass: '',
				activeItemStyle: {
					'background-color': '#fff',
				}
			}
		},
		created() {
			if (this.item.projectKey === 'tab') {
				const list = this.item.children
				for (var i = 0; i < list.length; i++) {
					if (this.item.active == list[i].name) {
						this.tabCurrent = i
						break
					}
				}
				if (this.item.type === "border-card" || this.item.type === "card") {
					this.tabsClass = 'htabs'
				}
			}
		},
		methods: {
			change(index) {
				this.current = index;
			},
			onTabChange(index) {
				if (this.tabCurrent === index) return
				this.tabCurrent = index;
				this.key = +new Date()
			},
		}
	}
</script>

<style lang="scss">
</style>