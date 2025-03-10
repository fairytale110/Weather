# Weather
CodingTest

# 预览
<img src="https://github.com/fairytale110/Weather/blob/main/apk/Screenshot_20250310_101220.png?raw=true" alt="drawing" width="200"/>

# 开发环境

<img src="https://github.com/user-attachments/assets/85e6a968-836e-4cd2-8984-013ae18b04f1" alt="drawing" width="200"/>

# 功能拆分
- 线性图表显示温度曲线
- list显示小时温度
- 横屏时布局不一样

# 业务流程
- 启动检查GPS是否开启，未开启弹窗提示需要申请GPS权限，然后根据操作进行权限处理
无权限，界面显示遮罩，提示需要定位权限，点击可以重复请求权限
- 检查是否有网，无网络，显示遮罩，提示检查网络是否畅通，点击可重新请求
- 加载数据loadding动画，加载结束关闭loading
加载失败显示遮罩，提示失败，点击可重新请求

# 列表
- 顶部显示title 时间  温度，上滑动，title可悬浮，点击title可滚动到顶部
- 支持全局下拉刷新
- 支持全局上拉加载更多（OPT）

# 曲线图
- 自定义View绘制
- 点击曲线可以根据点位弹窗查看对应的时间和温度
- 滑动可以刷新弹窗位置和数据
- 双指可以缩放，同时更新坐标刻度缩放比例

# TODO
- 缩放优化
