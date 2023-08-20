(function(){const
/* 等号后的数可供修改
↓ "开始调试"按钮 ↓ */
lenToR = 20, /*到右边距离(px)*/
lenToB = 15, /*到底部距离(px)*/
/* ↓ 主菜单 ↓ 1为是 0为否 */
tMenu = 0, /*是否默认在顶部*/
exitC = 1, /*结束调试是否需要确认*/
/*－－－－以下勿改－－－－*/
key=encodeURIComponent('网页元素标识:执行判断');
if(window[key]){return;}try{window[key]=true;let tmpRule=[],targtEle=null,tIndex=0,preRst=[],needC=false,undo=false;
const borderRule='{border:5px solid red!important}',d=document,open=d.createElement("div"),menu=d.createElement("div");
open.style=`position:fixed!important;right:${lenToR}px;bottom:${lenToB}px;background:black;color:snow;line-height:1;padding:7px;user-select:none;touch-action:none`;
open.className='e_i_sky';open.innerText='打开调试';d.body.appendChild(open);menu.style='width:100%;position:fixed!important;bottom:0;left:0;background:beige;display:none';
menu.className='e_i_sky';menu.innerHTML='<style>.e_i_sky{z-index:999999;font-size:15px;white-space:nowrap}.e_i_sky>div{height:35px;display:flex}.e_i_sky::-webkit-scrollbar{display:none}button.e_i_sky{width:25%;color:#662}button.e_i_sky:disabled{color:#5555}</style><style id="elemnt_idntify_css"></style><div><div class="e_i_sky"id="elemnt_idntify_rule" style="width:90%;line-height:32px;padding:0 5px;border:1px solid gray;overflow:auto"></div><button class="e_i_sky"id="elemnt_idntify_more">增目标</button><button class="e_i_sky"id="elemnt_idntify_less"></button><button class="e_i_sky"id="elemnt_idntify_switchTB"style="width:50px">↑</button></div><div><button class="e_i_sky"id="elemnt_idntify_enlarge">扩大范围</button><button class="e_i_sky"id="elemnt_idntify_shrink">缩小范围</button><button class="e_i_sky"id="elemnt_idntify_outRule">导出规则</button><button class="e_i_sky"id="elemnt_idntify_close">结束调试</button></div>';d.body.appendChild(menu);
const css=d.getElementById('elemnt_idntify_css'),ruleInfo=d.getElementById('elemnt_idntify_rule'),more=d.getElementById('elemnt_idntify_more'),less=d.getElementById('elemnt_idntify_less'),switchTB=d.getElementById('elemnt_idntify_switchTB'),enlarge=d.getElementById('elemnt_idntify_enlarge'),shrink=d.getElementById('elemnt_idntify_shrink'),outRule=d.getElementById('elemnt_idntify_outRule'),close=d.getElementById('elemnt_idntify_close');function findRule(e){tmpRule=[];
for(;e&&e.tagName!='BODY';e=e.parentElement){
if(e.id!=''){tmpRule.push(e.tagName+'#'+e.id);}
else if(e.className!=''){tmpRule.push(e.tagName+'.'+e.className.replace(/ /g,'.'));}}}
function usualRefresh(){shrink.disabled=tIndex<=0;enlarge.disabled=tIndex>=tmpRule.length-1;if(location.hostname!='')
{if(tmpRule.length>0){outRule.disabled=false;ruleInfo.innerText='规则：'+location.hostname+'##'+(preRst.length>0?preRst.join()+',':'')+tmpRule[tIndex];
css.innerText=(preRst.length>0?preRst.join()+',':'')+tmpRule[tIndex]+borderRule;needC=true;}else{typeof(window.via)=="object"&&window.via.toast('提示:先长按目标再点开始调试');
outRule.disabled=true;ruleInfo.innerText='未选目标或无法标识';
css.innerText=preRst.length>0?preRst.join()+borderRule:'';
if(preRst.length>0){needC=true;less.disabled=false;less.innerText='撤销';undo=true;}}}else{outRule.disabled=true;ruleInfo.innerText='当前域名获取失败';needC=false;}}
function copy2clip(t){const a=d.createElement("input");a.style='position:fixed;top:-99px;opacity:0';a.value=t;d.body.appendChild(a);a.select();d.execCommand("copy");
d.body.removeChild(a);alert('成功复制Adblock规则，请自行添加为浏览器广告拦截规则。');}function hideMenu(){open.style.display='block';menu.style.display='none';css.innerText='';
targtEle=null;}open.onclick=function(){less.innerText='减目标';undo=false;menu.style.display='block';open.style.display='none';
findRule(targtEle);tIndex=0;less.disabled=tmpRule.length<1&&preRst.length<1;usualRefresh();};open.addEventListener('touchmove',function(e){open.style.right=open.style.bottom='';open.style.left=(e.touches[0].clientX-25)+'px';open.style.top=(e.touches[0].clientY-10)+'px';},{'passive':true});close.onclick=function(){if(exitC&&needC&&!confirm("退出调试将清空当前规则，是否确认？")){return;}preRst=[];hideMenu();};more.onclick=function(){tmpRule[tIndex]&&preRst.push(tmpRule[tIndex]);hideMenu();};less.onclick=function(){targtEle=null;enlarge.disabled=true;shrink.disabled=true;if(undo){tmpRule=[];undo=false;less.innerText='减目标';}else if(tmpRule.length>0){tmpRule=[];}else{preRst.pop();}if(preRst.length>0){ruleInfo.innerText='规则：'+location.hostname+'##'+preRst.join();css.innerText=preRst.join()+borderRule;needC=true;outRule.disabled=false;}else{ruleInfo.innerText='未选目标或无法标识';css.innerText='';less.disabled=true;needC=false;outRule.disabled=true;}};enlarge.onclick=function(){tIndex++;usualRefresh();};shrink.onclick=function(){tIndex--;usualRefresh();};outRule.onclick=function(){const r=preRst.length>0?preRst.join()+(tmpRule.length>0?','+tmpRule[tIndex]:''):tmpRule[tIndex];if(typeof(window.via)=="object"){window.via.record(location.hostname,r);window.via.toast('导出成功，刷新生效');}else{copy2clip(location.hostname+'##'+r);}};switchTB.onclick=function(){if(menu.style.top==''){menu.style.top='0';menu.style.bottom='';switchTB.innerText='↓';}else{menu.style.top='';menu.style.bottom='0';switchTB.innerText='↑';}};tMenu&&switchTB.onclick();d.addEventListener('touchstart',function(e){e.target.className!='e_i_sky'&&(targtEle=e.target);},{'passive':true,'capture':true});}catch(err){console.log('网页元素标识：',err);}})();