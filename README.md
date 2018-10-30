# Assistant
功能设计:

	主要功能模块有成绩查询模块,绩点查询模块,失物招领模块,常用网站模块等。
	
	App前端UI设计: UI采用Material Design风格设计(Navigation Bar,Tool Bar无边框按钮，空间层级，fab动画)等

	服务器端:采用LeanCloud后端云服务做后台,实现数据的存储和调用
	
	成绩绩点查询模块设计:使用burp suite抓包获取cookie ,请求方式,请求头部,主体，通过okhttp框架，请求成功后获取h5数据,用jsoup框架进行数据筛选,得到的数据封装在List<JavaBean>里面,通过RecyclerView进行展示。
	
	失物招领模块设计:使用LeanCloud第三方平台存数SmartRefresh+RecyclerView进行数据的展示和及时更新新
	
	常用网站模块设计:收集学校的常用网站,把数据分装到RecyclerView中,点击之后用Intent.ACTION_VIEW跳转到浏览器
	
		
效果图：

绩点查询:
<div align="left">
<img src="https://github.com/huangaa/Assistant/blob/master/images/GPA.gif" width="30%" hight ="50%" alt="绩点查询"/>
</div>

失物招领:
<div align="left">
<img src="https://github.com/huangaa/Assistant/blob/master/images/takePhoto.gif" width="30%" hight ="50%" alt="失物招领"/>
</div>

常用网站:
<div align="left">
<img src="https://github.com/huangaa/Assistant/blob/master/images/TIM图片20181022203209.jpg" width="30%" hight ="30%" alt="常用网站"/>
</div>

