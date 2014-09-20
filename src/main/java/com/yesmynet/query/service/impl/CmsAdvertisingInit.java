package com.yesmynet.query.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.query.core.dto.DataSourceConfig;
import com.yesmynet.query.core.dto.Environment;
import com.yesmynet.query.core.dto.InfoDTO;
import com.yesmynet.query.core.dto.Parameter;
import com.yesmynet.query.core.dto.ParameterHtmlType;
import com.yesmynet.query.core.dto.ParameterInput;
import com.yesmynet.query.core.dto.ParameterLayoutDTO;
import com.yesmynet.query.core.dto.QueryDefinition;
import com.yesmynet.query.core.dto.QueryResult;
import com.yesmynet.query.core.dto.SelectOption;
import com.yesmynet.query.core.service.QueryDefinitionGetter;
import com.yesmynet.query.core.service.QueryService;
import com.yesmynet.query.core.service.ResourceHolder;
import com.yesmynet.query.core.service.ResourceHolder.ResourceType;
import com.yesmynet.query.utils.QueryUtils;
/**
 * 商城页面用到的广告的初始化
 * @author liuqingzhi
 *
 */
public class CmsAdvertisingInit extends AbstractMainQueryService implements QueryDefinitionGetter{
	private final String Parameter_dbId="dbId";
	private final String Parameter_command="command";
	private final String Command_Value_Delete="deleteAds";
	private final String Command_Value_Init="initAds";
	/**
	 * 所有定义的广告
	 */
	private Set<AdCodeAndType> allAds;
    private QueryDefinition queryDefinition;
	@Override
	protected void afterConstructor() {
		super.afterConstructor();
		commandQueryMap=new HashMap<String,QueryService>();
		
		commandQueryMap.put("deleteAds", new DeleteAdQueryService());
		commandQueryMap.put("initAds", new InitAdQueryService());
		
		
		allAds=new HashSet<AdCodeAndType>();
		allAds.add(new AdCodeAndType("aaa","商城页面顶部条",new AdContent.StringAdContent("这是一个测试广告，没有用处")));
		
		allAds.add(new AdCodeAndType("V3_MALL_HEAD_SITEBAR","商城页面顶部条",new AdContent.StringAdContent("<div class=\"site-bar\">\n"+
				"  <ul class=\"userinfos\">\n"+
				"    <li class=\"bold\"><a href=\"/portal/toLogin.do\" class=\"txt-login\" rel=\"nofollow\">登录</a></li>\n"+
				"    <li class=\"bldr\"><a href=\"/portal/toRegister.do\" class=\"txt-register\" rel=\"nofollow\">注册</a></li>\n"+
				"  </ul>\n"+
				"  <ul class=\"sitelinks\">\n"+
				"    <li class=\"dropdown myaccount\">\n"+
				"      <a href=\"/personalCenter/showIndex.do?page=1\" class=\"txt-down\" rel=\"nofollow\">我的账户</a>\n"+
				"      <ul>\n"+
				"        <li><a href=\"/personalCenter/showMyOrder.do\" rel=\"nofollow\">我的订单<em></em></a></li>\n"+
				"				<li><a href=\"/personalCenter/showMyCollect.do\" rel=\"nofollow\">我的收藏夹<em></em></a></li>\n"+
				"			</ul>\n"+
				"			<b class=\"arrow-top\"></b>\n"+
				"		</li>\n"+
				"	</ul>\n"+
				"</div>\n")));
		
		allAds.add(new AdCodeAndType("V3_MALL_HEAD_TOPLINE","商城页面顶部小广告",new AdContent.StringAdContent("<div class=\"head-topline e-topline\" btime=\"2014-05-05 00:00:00\" etime=\"2023-06-30 23:59:59\" style=\";background:#8b23ae;width:100%;margin-bottom:0; padding-bottom:0;position:relative;\" >\n"+ 
				"<div style=\"background:url(http://121.40.175.204/img/cmsad/20140909960_130.jpg) no-repeat center 0;height:130px;position:relative;width:auto;\" class=\"topline-main\">\n"+ 
				"<a style=\"display:block;height:130px;position:absolute;width:100%;left:0;\" target=\"_blank\" href=\"\"></a>\n"+ 
				"</div>\n"+ 
				"</div>")));
		
		allAds.add(new AdCodeAndType("V3_MALL_HEAD_LOGO","商城页面顶部条",new AdContent.StringAdContent("<div class=\"head-logo\">\n"+ 
				"<a group2=\"1443\" group1=\"ad\" class=\"logo-site\" href=\"/mall/\">商城</a>\n"+ 
				"</div>")));
		
		//allAds.add(new AdCodeAndType("V3_MALL_HEAD_CT_SEARCHTEXT",1,"搜索的热词",""));
		//allAds.add(new AdCodeAndType("V3_MALL_HEAD_AD_SEARCHLINK",1,"搜索的链接",""));
		allAds.add(new AdCodeAndType("V3_MALL_HEAD_SERVICES","页面头上的服务介绍",new AdContent.StringAdContent("<div class=\"head-pledge\">\n"+
				"	<ul>\n"+
				"		<li class=\"jxsp\">\n"+
				"			<h5>精选食品</h5>\n"+
				"            <div class=\"hp-txt\">\n"+
				"              <p>精选全球酒水美食 品质商家合作</p>\n"+
				"              <p>打造的“体验一流、专业服务、精品荟萃”的线上食品精品平台</p>\n"+
				"            </div>\n"+
				"		</li>\n"+
				"		<li class=\"jybs\">\n"+
				"			<h5>假一赔十</h5>\n"+
				"	        <div class=\"hp-txt\">\n"+
				"	            <p>无忧购物保障  正品“假一罚十”</p>\n"+
				"	            <p>本商城承诺所售物品为正品。</p>\n"+
				"	        </div>\n"+
				"		</li>\n"+
				"        <li class=\"qtbh\">\n"+
				"			<h5>七天包换</h5>\n"+
				"	        <div class=\"hp-txt\">\n"+
				"	            <p>7天无理由退换货</p>\n"+
				"	            <p>客户付款购买商品，自客户签收商品之日起7日内，在未使用商品、未破坏原包装并不影响二次销售的情况下，可无理由申请换退换货。</p>\n"+
				"	        </div>\n"+
				"		</li>\n"+
				"	</ul>\n"+
				"</div>\n")));
		
		allAds.add(new AdCodeAndType("V3_MAll_HEAD_NAVILINK","页面顶上一些链接",
				new AdContent.StringAdContent("<ul class=\"navlinks\" style=\"overflow:hidden\">\n"+
						"    <li><a href=\"/\" target=\"_blank\">商城</a></li>\n"+
						"	<li folder=\"/marketing/sales/\"><a href=\"\" target=\"_blank\">抢购</a></li>\n"+
						"	<li folder=\"/groupbuy/\"><a href=\"/\" target=\"_blank\">热门团购</a></li>\n"+
						"	<li folder=\"\"><a href=\"/\" target=\"_blank\">秒杀</a></li>\n"+
						"	<li folder=\"\"><a href=\"\" target=\"_blank\">进口新鲜美食</a></li>\n"+
						"</ul>\n")));
		
		//这是最长的一个广告
		allAds.add(new AdCodeAndType("V3_MAll_HEAD_CATEGORY","商品分类导航",
				new AdContent.StringAdContent("<div class=\"categorys\">\n"+
				"	<h3>\n"+
				"	<a target=\"_blank\" href=\"/\">商品分类</a><b class=\"arrow\"></b>\n"+
				"	</h3>\n"+
				"	<div class=\"categroup\">\n"+
				"		<dl>\n"+
				"			<dt>\n"+
				"			<h4>\n"+
				"			<a href=\"/\" class=\"topcate\" target=\"_blank\" group1=\"ad\" group2=\"1308\">零食特产</a>\n"+
				"			</h4>\n"+
				"			<p>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">生鲜食品</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">休闲零食</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">坚果/蜜饯</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">饼干/糕点</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">厨房调料</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">米面粮油</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\" class=\"bg\">团购精选</a>\n"+
				"			</p>\n"+
				"			</dt>\n"+
				"			<dd class=\"h400\">\n"+
				"			<ul class=\"subcates\">\n"+
				"				<li>\n"+
				"				<h5>进口食品</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口牛奶乳品</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口酒</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口粮油</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口水/饮料</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口饼干/糕点</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口罐头调料</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口冲饮谷物</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口坚果/蜜饯</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口生鲜</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口咖啡/茶叶</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口营养品</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">更多品牌</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>价格段</h5>\n"+
				"				<p class=\"col3\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">1 - 49元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">50 - 99元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">100-199元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">200-299元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">300-499元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">500-999元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">1000元以上</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>种类</h5>\n"+
				"				<p class=\"col3\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">方便面</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">康师傅</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">统一</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">农心</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">日清</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">干脆面</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">火腿肠</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">双汇</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">罐头</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			<ul class=\"relcates\">\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p class=\"col2\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">商城网</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">淘宝</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">网上商城</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">网店</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\" class=\"w80\">限时抢购</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			</dd>\n"+
				"		</dl>\n"+
				"		<dl>\n"+
				"			<dt>\n"+
				"			<h4><a href=\"/\" class=\"topcate\" target=\"_blank\" group1=\"ad\" group2=\"1308\">手机、数码、配件</a></h4>\n"+
				"			<p>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">三星</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">苹果</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">小米</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">华为</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">索尼</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\" class=\"bg\">酷派</a>\n"+
				"			</p>\n"+
				"			</dt>\n"+
				"			<dd class=\"h320\">\n"+
				"			<ul class=\"subcates\">\n"+
				"				<li>\n"+
				"				<h5>产地</h5>\n"+
				"				<p class=\"lh22\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">瑞典</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">法国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">意大利</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">西班牙</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">德国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">墨西哥</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">俄罗斯</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">英国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">其他国家</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>品牌</h5>\n"+
				"				<p class=\"lh22\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">诺基亚</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">联想</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">HTC</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">魅族</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">飞利浦</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">中兴</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">努比亚</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">佳能</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">卡西欧</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">尼康</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">aigo</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">更多品牌</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>价格</h5>\n"+
				"				<p class=\"lh22 col3\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">1 - 49元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">50 - 99元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">100 - 199元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">200 - 299元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">300 - 499元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">500 - 799元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">1000元以上</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			<ul class=\"relcates\">\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p class=\"col2\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">对讲机</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">手机饰品 </a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">内存卡</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">蓝牙耳机</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\" class=\"w80\">名品大放价</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			</dd>\n"+
				"		</dl>\n"+
				"		<dl>\n"+
				"			<dt>\n"+
				"			<h4>\n"+
				"			<a href=\"/\" class=\"topcate\" target=\"_blank\" group1=\"ad\" group2=\"1308\">智能设备</a>\n"+
				"			</h4>\n"+
				"			<p>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">JAWBONE</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">iLING</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">智能穿戴</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">高清播放器</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">智能家居系统</a>\n"+
				"			</p>\n"+
				"			</dt>\n"+
				"			<dd class=\"h320\">\n"+
				"			<ul class=\"subcates\">\n"+
				"				<li>\n"+
				"				<h5>产地</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">浙江</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">上海</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">江苏</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">广州</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">安徽</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">其他地区</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>价格段</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">0-30</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">30-60</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">60-100</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">100-200</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">200-300</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">300以上</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>品牌</h5>\n"+
				"				<p class=\"col3\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">石库门</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">同里红</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">古越龙山</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">会稽山</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">女儿红</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">稽山见水</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">即墨</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">坊巷青红</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			<ul class=\"relcates\">\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">热门店铺1</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">新店2</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>热门活动</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">秋冬应季品</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			</dd>\n"+
				"		</dl>\n"+
				"		<dl>\n"+
				"			<dt>\n"+
				"			<h4>\n"+
				"			<a href=\"/\" class=\"topcate\" target=\"_blank\" group1=\"ad\" group2=\"1308\">收纳洗晒</a>\n"+
				"			</h4>\n"+
				"			<p>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">压缩袋</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">脏衣篮</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">衣物收纳</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">杂物收纳</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">收纳箱</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">收纳层</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\" class=\"bg\">买一赠二</a>\n"+
				"			</p>\n"+
				"			</dt>\n"+
				"			<dd class=\"h320\">\n"+
				"			<ul class=\"subcates\">\n"+
				"				<li>\n"+
				"				<h5>产地</h5>\n"+
				"				<p class=\"lh22\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">德国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">比利时</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">韩国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">中国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">荷兰</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">其他国家</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>价格</h5>\n"+
				"				<p class=\"lh22 col3\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">1-99元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">100-199元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">200-299元</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">300元以上</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>品牌</h5>\n"+
				"				<p class=\"lh22\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">蓝月亮</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">青岛</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">百威</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">科罗娜</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">宝龙</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">卡力特</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">威斯路</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">猛士</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">科隆巴赫</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">慕尼黑</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">柏龙</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">皇家</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">艾斯特</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">威麦</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">教士</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">督威</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">布雷帝国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">更多品牌</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			<ul class=\"relcates\">\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p class=\"col2\">\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">快点网</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">朗邦商贸</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">雄狮</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">欧巴好啤 </a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">尚订网</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\" class=\"w80\">指定商品买1送6 </a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			</dd>\n"+
				"		</dl>\n"+
				"		<dl>\n"+
				"			<dt>\n"+
				"			<h4>\n"+
				"			<a href=\"/\" class=\"topcate\" target=\"_blank\" group1=\"ad\" group2=\"1308\">食品零食</a>\n"+
				"			</h4>\n"+
				"			<p>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口食品</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口鲜果</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">牛奶</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">养生茶饮</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">海鲜水产</a>\n"+
				"				<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">咖喱皇</a>\n"+
				"			</p>\n"+
				"			</dt>\n"+
				"			<dd style=\"height:400px;\">\n"+
				"			<ul class=\"subcates\">\n"+
				"				<li>\n"+
				"				<h5>进口食品</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">进口零食</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">牛奶</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">海鲜制品</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>生鲜蔬果</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">本来生活</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">国产精选</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">干凡</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>休闲零食</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">红枣</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>茶冲乳品</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">茶冲乳品</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">蜂蜜柚子茶</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>特产美食</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">泰国</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">台湾</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>粮油干货调味</h5>\n"+
				"				<p>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">咖喱皇</a>\n"+
				"					<a href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">橄榄油</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			<ul class=\"relcates\">\n"+
				"				<li>\n"+
				"				<h5>热销品牌</h5>\n"+
				"				<p>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">本来生活</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">大地原生</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">咖喱皇</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">龚氏</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">有机厨坊</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">顶好</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">榴的华</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"				<li>\n"+
				"				<h5>热销店铺</h5>\n"+
				"				<p>\n"+
				"					<a style=\"width:100px;\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">本来生活</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">邮康进口食品</a>\n"+
				"					<a class=\"w80\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">乡礼香专营店</a>\n"+
				"					<a style=\"width:100px;\" href=\"/\" target=\"_blank\" group1=\"ad\" group2=\"1308\">大地原生大闸蟹</a>\n"+
				"				</p>\n"+
				"				</li>\n"+
				"			</ul>\n"+
				"			</dd>\n"+
				"		</dl>\n"+
				"	</div>\n"+
				"</div>\n")));
		
		//焦点图
		AdContent.ImagListAdContent bigFocuseImags=new AdContent.ImagListAdContent();
		AdCodeAndType bigFocus= new AdCodeAndType("V3_MALL_INDEX_AD_BIGFOCUS","商城页面轮播大图",bigFocuseImags);
		List<AdContent.ImagListAdContent.AdItem> items=new ArrayList<AdContent.ImagListAdContent.AdItem>();
		bigFocuseImags.setItems(items);
		bigFocuseImags.setViewMax(6);
		items.add(new AdContent.ImagListAdContent.AdItem("天禄手工酒","http://121.40.175.204/img/cmsad/1408523913621.jpg","/"));
		items.add(new AdContent.ImagListAdContent.AdItem("世博金奖纪念酒","http://121.40.175.204/img/cmsad/1409540344498.jpg","/"));
		allAds.add(bigFocus);
		
		//新店商品列表
		AdContent.GoodsListAdContent newStoreGoods=new AdContent.GoodsListAdContent();
		AdCodeAndType newStore= new AdCodeAndType("V3_MALL_INDEX_NEWSHOP_LIST","商城首页-新店商品",newStoreGoods);
		newStoreGoods.setViewMax(5);
		newStoreGoods.setGoodsIds(Arrays.asList(5185624L,5185623L,5184952L,5168881L,1L));
		allAds.add(newStore);
		
		AdContent.GoodsListAdContent groupGoods=new AdContent.GoodsListAdContent();
		AdCodeAndType group= new AdCodeAndType("V3_MALL_INDEX_AD_TODAYGROUP","商城首页-今日团购",groupGoods);
		groupGoods.setViewMax(5);
		groupGoods.setGoodsIds(Arrays.asList(5168881L));
		allAds.add(group);
		
		//促销精选
		AdContent.ImagListAdContent promotionImags=new AdContent.ImagListAdContent();
		AdCodeAndType promotion= new AdCodeAndType("V3_MALL_INDEX_AD_PROMOTION","商城首页页面-促销精选",promotionImags);
		List<AdContent.ImagListAdContent.AdItem> promotionImagesItems=new ArrayList<AdContent.ImagListAdContent.AdItem>();
		promotionImags.setItems(promotionImagesItems);
		promotionImags.setViewMax(3);
		promotionImagesItems.add(new AdContent.ImagListAdContent.AdItem("商城促销","http://121.40.175.204/img/cmsad/108s.jpg","/"));
		allAds.add(promotion);
		
		
		

	}
	@Override
	protected String getCommand(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		queryDefinition=getQueryDefinition();
		List<Parameter> parameters = queryDefinition.getParameters();
		return QueryUtils.getParameterValue(parameters, Parameter_command);
	}
	@Override
	public QueryDefinition getQueryDefinition() {
		if(queryDefinition==null)
		{
			queryDefinition=new QueryDefinition();
			List<Parameter> parameters=new ArrayList<Parameter>();
			
			queryDefinition.setParameters(parameters);
			
			parameters.add(QueryUtils.createParameter("要执行的操作",Parameter_command,"",ParameterHtmlType.Select,null,null,null,true,10,1,1));
			parameters.add(QueryUtils.createParameter("要操作的数据库",Parameter_dbId,"",ParameterHtmlType.Select,null,null,null,true,10,1,1));
			parameters.add(QueryUtils.createParameter("确定","executeButton","",ParameterHtmlType.Button,"","","onclick='$(\"#queryForm\").submit();'",true,30,1,1));
		}

        return queryDefinition;
	}
	@Override
	protected void settingParameterOptions(QueryDefinition queryDefinition,
			ResourceHolder resourceHolder, Environment environment) {
		settingParameterOptions(queryDefinition.getParameters(),resourceHolder);
	}
	/**
	 * 设置参数的选项
	 * @param parameters
	 * @param resourceHolder 
	 */
	private void settingParameterOptions(List<Parameter> parameters,ResourceHolder resourceHolder)
	{
		Parameter parameterByName = QueryUtils.getParameterByName(parameters, Parameter_dbId);
		if(parameterByName!=null)
		{
			List<SelectOption> dbOptions = QueryUtils.getResourceOptions(resourceHolder,true,ResourceType.Database);
			parameterByName.getParameterInput().setOptionValues(dbOptions);
		}
		
		Parameter parameterByName2 = QueryUtils.getParameterByName(parameters, Parameter_command);
		if(parameterByName2!=null)
		{
			List<SelectOption> dbOptions=new ArrayList<SelectOption>(); 
			
			SelectOption delete=new SelectOption();
			delete.setText("删除所有广告");
			delete.setValue(Command_Value_Delete);
			dbOptions.add(delete);
			
			SelectOption init=new SelectOption();
			init.setText("插入所有广告");
			init.setValue(Command_Value_Init);
			dbOptions.add(init);
			
			parameterByName2.getParameterInput().setOptionValues(dbOptions);
		}
	}
    /**
     * 表示广告和对应的类型
     * @author liuqingzhi
     *
     */
    private class AdCodeAndType
    {
    	/**
    	 * 广告的代码
    	 */
    	private String adCode;
    	/**
    	 * 广告的中文名称
    	 */
    	private String name;
    	/**
    	 * 广告的内容
    	 */
    	private AdContent adContent;
		public AdCodeAndType(String adCode, String name,
				AdContent adContent) {
			super();
			this.adCode = adCode;
			this.name = name;
			this.adContent = adContent;
		}
		public String getAdCode() {
			return adCode;
		}
		public void setAdCode(String adCode) {
			this.adCode = adCode;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public AdContent getAdContent() {
			return adContent;
		}
		public void setAdContent(AdContent adContent) {
			this.adContent = adContent;
		}
		
    }
    /**
     * 表示广告内容的对象，不同的广告内容，需要的内容不一样
     * 目前有3种：html内容、轮播图、推荐商品
     * @author liuqingzhi
     *
     */
    private interface AdContent
    {
    	public static class StringAdContent implements AdContent
    	{
    		/**
        	 * 广告的内容
        	 */
        	private String content;
			public StringAdContent(String content) {
				super();
				this.content = content;
			}
			public String getContent() {
				return content;
			}

			public void setContent(String content) {
				this.content = content;
			}
			@Override
			public void delete(AdCodeAndType ad, JdbcTemplate jdbcTemplate) {
				deleteHtmlAd(ad.getAdCode(),jdbcTemplate);
			}
			@Override
			public void insert(AdCodeAndType ad, JdbcTemplate jdbcTemplate) {
				initHtmlAd(ad.getAdCode(),ad.getName(),this.getContent(),jdbcTemplate);
			}
			private void initHtmlAd(String locateCode,String adName,String adContent,JdbcTemplate jdbcTemplate)
			{
				long locationId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_L_ID.Nextval  from dual");
				long adId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_I_ID.Nextval  from dual");
				long adLocMapId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_ILM_ID.Nextval  from dual");
				long publicId = jdbcTemplate.queryForLong("select SEQ_M_CMS_AD_ILP_ID.Nextval  from dual");
				
				jdbcTemplate.update("insert into M_CMS_AD_LOCATION (ID, L_NAME, L_CODE, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION)\n"+
				"values (?, ?, ?, 0, null, null, sysdate, 1, 0)", locationId,adName,locateCode);
				
				jdbcTemplate.update("insert into M_CMS_AD_INFO (ID, I_NAME, I_BEGIN_DATE, I_END_DATE, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION, I_CONTENT, CHANNEL_NO)\n"+
				"values (?, ?, sysdate-200, sysdate+500, 0, null, null, sysdate, 1, 0, ?, '01')",adId,adName,adContent);
				
				jdbcTemplate.update("insert into M_CMS_AD_IL_MAP (ID, I_ID, L_ID, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION)\n"+
				"values (?, ?, ?, 0, null, null, sysdate, 1, 0)",adLocMapId,adId,locationId);

				jdbcTemplate.update("insert into m_cms_ad_il_publish (ID, M_ID, P_ONLINE_FLAG, P_BEGIN_DATE, P_END_DATE, CANCEL_FLAG, UPDATE_DATE, UPDATE_BY, CREATED_DATE, CREATED_BY, VERSION)\n"+
				"values (?, ?, 1, sysdate-1, sysdate+1000, 0, null, null, sysdate, 1, 0)",publicId,adLocMapId);
			}
			/**
			 * 删除指定
			 * @param locateCode
			 * @param jdbcTemplate
			 * @return
			 */
			private int deleteHtmlAd(String locateCode,JdbcTemplate jdbcTemplate)
			{
				int deleted=0;
				
				jdbcTemplate.update("delete  from m_cms_ad_il_publish where M_ID in (select id from M_CMS_AD_IL_MAP where L_ID in (select id from M_CMS_AD_LOCATION where L_CODE=?))",locateCode);
				jdbcTemplate.update("delete  from M_CMS_AD_IL_MAP where L_ID in (select id from M_CMS_AD_LOCATION where L_CODE=?)",locateCode);
				jdbcTemplate.update("delete  from M_CMS_AD_LOCATION where L_CODE=?",locateCode);
				
				return deleted;
			}
    	}
    	public static class ImagListAdContent implements AdContent
    	{
    		/**
        	 * 多个内容的广告最多显示多少个内容
        	 */
        	private Integer viewMax;
        	/**
        	 * 包含的多个图片
        	 */
        	private List<AdItem> items;
			public Integer getViewMax() {
				return viewMax;
			}
			public void setViewMax(Integer viewMax) {
				this.viewMax = viewMax;
			}
			public List<AdItem> getItems() {
				return items;
			}
			public void setItems(List<AdItem> items) {
				this.items = items;
			}
			@Override
			public void delete(AdCodeAndType ad, JdbcTemplate jdbcTemplate) {
				deleteImageAd(ad.getAdCode(),jdbcTemplate);
			}
			@Override
			public void insert(AdCodeAndType ad, JdbcTemplate jdbcTemplate) {
				initItemAd(ad.getAdCode(),ad.getName(),this.getViewMax(),this.getItems(),jdbcTemplate);
			}
			private void initItemAd(String locateCode,String adName,Integer viewMax,List<AdContent.ImagListAdContent.AdItem> items,JdbcTemplate jdbcTemplate)
			{
				long adSiteId = jdbcTemplate.queryForLong("select SEQ_M_CMS_ADVERTISING_SITE_ID.Nextval from dual");
				
				jdbcTemplate.update("insert into M_CMS_ADVERTISING_SITE (ADS_ID, ADS_NAME, ADS_CODE, DISPLAY_ORDER, VIEW_MAXNUM, ADS_TYPE, ADS_LENGTH, ADS_WIDTH, ADS_DESC, CREATE_TIME, CANCEL_FLAG, UPDATE_BY, UPDATE_TIME)\n"+
						"values (?,?,?,270, ?, 1, null, null, null, sysdate, 0, 1, sysdate)",adSiteId,adName,locateCode,viewMax);
				
				if(!CollectionUtils.isEmpty(items))
				{
					int i=1;
					for(AdContent.ImagListAdContent.AdItem it:items)
					{
						long adId = jdbcTemplate.queryForLong("select SEQ_M_CMS_ADVERTISING_ID.Nextval from dual");
						jdbcTemplate.update("insert into M_CMS_ADVERTISING (AT_ID, ADS_ID, AT_NAME, DISPLAY_ORDER, START_TIME, END_TIME, ONLINE_STATUS, AT_TYPE, MEDIUM_ADDRESS, LINK_ADDRESS, AT_TEXT, JUMP_TYPE, CREATE_TIME, CANCEL_FLAG, UPDATE_BY, UPDATE_TIME, IMG_ADDRESS)\n"+
								"values (?, ?, ?, ?, sysdate-10, sysdate+100, 0, 2, '', ?, null, 0, sysdate, 0, 1, sysdate, ?)",adId,adSiteId,it.getTitle(),i*10,it.getLink(),it.getImageSrc());
						i++;
					}
				}
			}
			private void deleteImageAd(String adCode, JdbcTemplate jdbcTemplate) {
				jdbcTemplate.update("delete From M_CMS_ADVERTISING where ADS_ID in (select ADS_ID from M_CMS_ADVERTISING_SITE where ADS_CODE=?)",adCode);
				jdbcTemplate.update("delete from M_CMS_ADVERTISING_SITE where ADS_CODE=?",adCode);
			}
			/**
		     * 包含多个内容的广告的内容
		     * @author liuqingzhi
		     *
		     */
		    public static class AdItem
		    {
		    	/**
		    	 * 显示的标题
		    	 */
		    	private String title;
		    	/**
		    	 * 图片地址
		    	 */
		    	private String imageSrc;
		    	/**
		    	 * 链接地址
		    	 */
		    	private String link;
				public AdItem(String title, String imageSrc, String link) {
					super();
					this.title = title;
					this.imageSrc = imageSrc;
					this.link = link;
				}
				public String getTitle() {
					return title;
				}
				public void setTitle(String title) {
					this.title = title;
				}
				public String getImageSrc() {
					return imageSrc;
				}
				public void setImageSrc(String imageSrc) {
					this.imageSrc = imageSrc;
				}
				public String getLink() {
					return link;
				}
				public void setLink(String link) {
					this.link = link;
				}
		    }
    	}
    	public class GoodsListAdContent implements AdContent
    	{
    		/**
        	 * 多个内容的广告最多显示多少个内容
        	 */
        	private Integer viewMax;
        	/**
        	 * 商品ID集合
        	 */
        	private List<Long> goodsIds;
			public Integer getViewMax() {
				return viewMax;
			}
			public void setViewMax(Integer viewMax) {
				this.viewMax = viewMax;
			}
			public List<Long> getGoodsIds() {
				return goodsIds;
			}
			public void setGoodsIds(List<Long> goodsIds) {
				this.goodsIds = goodsIds;
			}
			@Override
			public void delete(AdCodeAndType ad, JdbcTemplate jdbcTemplate) {
				deleteGoodsAd(ad.getAdCode(), jdbcTemplate);
			}
			@Override
			public void insert(AdCodeAndType ad, JdbcTemplate jdbcTemplate) {
				initGoodsListAd(ad.getAdCode(),ad.getName(),this.getViewMax(),this.getGoodsIds(),jdbcTemplate);
			}
			private void initGoodsListAd(String adCode, String name,Integer viewMax, List<Long> goodsIds, JdbcTemplate jdbcTemplate) {
				long adId = jdbcTemplate.queryForLong("select seq_m_cms_gr_site_id.nextval from dual");
				
				jdbcTemplate.update("insert into m_cms_gr_site (grsite_id,page_id,grsite_name,grsite_code,order_by,view_maxnum,grsite_desc,cancel_flag)\n"+
				"values (?,1,?,?,20,?,null,0)",adId,name,adCode,viewMax);
				
				int i=1;
				if(!CollectionUtils.isEmpty(goodsIds))
				{
					for(Long goodsId:goodsIds)
					{
						long itemId = jdbcTemplate.queryForLong("select seq_m_cms_gr_site_goods_id.nextval from dual");
						jdbcTemplate.update("insert into m_cms_gr_site_goods(grsg_id,grsite_id,goods_id,display_order,cancel_flag)\n"+
								"values (?,?,?,?,0)",itemId,adId,goodsId,i*10);
						i++;
					}
				}
				
			}
			private void deleteGoodsAd(String adCode, JdbcTemplate jdbcTemplate) {
				jdbcTemplate.update("delete From m_cms_gr_site_goods where grsite_id in (select grsite_id from m_cms_gr_site where grsite_code=?)",adCode);
				jdbcTemplate.update("delete from m_cms_gr_site where grsite_code=?",adCode);
			}
    	}
    	/**
    	 * 删除广告的操作
    	 * @param ad
    	 * @param jdbcTemplate
    	 */
    	public void delete(AdCodeAndType ad,JdbcTemplate jdbcTemplate);
    	/**
    	 * 插入广告的操作
    	 * @param ad
    	 * @param jdbcTemplate
    	 */
	    public void insert(AdCodeAndType ad,JdbcTemplate jdbcTemplate);
    }
    
    private abstract class AbstractQueryService implements QueryService
    {
    	@Override
		public QueryResult doInQuery(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment) {
			QueryResult re=new QueryResult();
			
			List<Parameter> parameters = queryDefinition.getParameters();
			String dbId = QueryUtils.getParameterValue(parameters,Parameter_dbId);
	        
			InfoDTO<DataSourceConfig> dataSourceConfigInfoDTO = QueryUtils.getDataSourceConfig(dbId,resourceHolder);
	        
	        if(!dataSourceConfigInfoDTO.isSuccess())
	        {
	        	 re.setContent(dataSourceConfigInfoDTO.getMsg());
	             return re;
	        }
	        DataSourceConfig dataSourceConfig = dataSourceConfigInfoDTO.getData();
	        JdbcTemplate jdbcTemplate=new JdbcTemplate(dataSourceConfig.getDatasource());
	        re=doInDb(queryDefinition, resourceHolder, environment, jdbcTemplate);
	        
			return re;
		}
    	public abstract QueryResult doInDb(QueryDefinition queryDefinition,ResourceHolder resourceHolder, Environment environment,JdbcTemplate jdbcTemplate);
    }
    private class InitAdQueryService extends AbstractQueryService
    {
		@Override
		public QueryResult doInDb(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment,JdbcTemplate jdbcTemplate) {
			QueryResult re=new QueryResult();
			
			for(AdCodeAndType ad:allAds)
			{
				AdContent adContent = ad.getAdContent();
				if(adContent!=null)
				{
					adContent.insert(ad, jdbcTemplate);
				}
			}
			re.setContent("初始化广告内容成功");
			return re;
		}
    }
    private class DeleteAdQueryService extends AbstractQueryService
    {
		@Override
		public QueryResult doInDb(QueryDefinition queryDefinition,
				ResourceHolder resourceHolder, Environment environment,
				JdbcTemplate jdbcTemplate) {
			QueryResult re=new QueryResult();
			
				for(AdCodeAndType ad:allAds)
				{
					AdContent adContent = ad.getAdContent();
					if(adContent!=null)
					{
						adContent.delete(ad, jdbcTemplate);
					}
				}
			re.setContent("删除广告成功");
			return re;
		}
    }
}
