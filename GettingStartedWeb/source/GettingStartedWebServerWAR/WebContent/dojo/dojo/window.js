/*
	Copyright (c) 2004-2011, The Dojo Foundation All Rights Reserved.
	Available via Academic Free License >= 2.1 OR the modified BSD license.
	see: http://dojotoolkit.org/license for details
*/

//>>built
define("dojo/window",["./_base/lang","./_base/sniff","./_base/window","./dom","./dom-geometry","./dom-style"],function(_1,_2,_3,_4,_5,_6){
var _7=_1.getObject("dojo.window",true);
_7.getBox=function(){
var _8=(_3.doc.compatMode=="BackCompat")?_3.body():_3.doc.documentElement,_9=_5.docScroll(),w,h;
if(_2("touch")){
var _a=_3.doc.parentWindow||_3.doc.defaultView;
w=_a.innerWidth||_8.clientWidth;
h=_a.innerHeight||_8.clientHeight;
}else{
w=_8.clientWidth;
h=_8.clientHeight;
}
return {l:_9.x,t:_9.y,w:w,h:h};
};
_7.get=function(_b){
if(_2("ie")&&_7!==document.parentWindow){
_b.parentWindow.execScript("document._parentWindow = window;","Javascript");
var _c=_b._parentWindow;
_b._parentWindow=null;
return _c;
}
return _b.parentWindow||_b.defaultView;
};
_7.scrollIntoView=function(_d,_e){
try{
_d=_4.byId(_d);
var _f=_d.ownerDocument||_3.doc,_10=_f.body||_3.body(),_11=_f.documentElement||_10.parentNode,_12=_2("ie"),_13=_2("webkit");
if((!(_2("mozilla")||_12||_13||_2("opera"))||_d==_10||_d==_11)&&(typeof _d.scrollIntoView!="undefined")){
_d.scrollIntoView(false);
return;
}
var _14=_f.compatMode=="BackCompat",_15=(_12>=9&&_d.ownerDocument.parentWindow.frameElement)?((_11.clientHeight>0&&_11.clientWidth>0&&(_10.clientHeight==0||_10.clientWidth==0||_10.clientHeight>_11.clientHeight||_10.clientWidth>_11.clientWidth))?_11:_10):(_14?_10:_11),_16=_13?_10:_15,_17=_15.clientWidth,_18=_15.clientHeight,rtl=!_5.isBodyLtr(),_19=_e||_5.position(_d),el=_d.parentNode,_1a=function(el){
return ((_12<=6||(_12&&_14))?false:(_6.get(el,"position").toLowerCase()=="fixed"));
};
if(_1a(_d)){
return;
}
while(el){
if(el==_10){
el=_16;
}
var _1b=_5.position(el),_1c=_1a(el);
if(el==_16){
_1b.w=_17;
_1b.h=_18;
if(_16==_11&&_12&&rtl){
_1b.x+=_16.offsetWidth-_1b.w;
}
if(_1b.x<0||!_12){
_1b.x=0;
}
if(_1b.y<0||!_12){
_1b.y=0;
}
}else{
var pb=_5.getPadBorderExtents(el);
_1b.w-=pb.w;
_1b.h-=pb.h;
_1b.x+=pb.l;
_1b.y+=pb.t;
var _1d=el.clientWidth,_1e=_1b.w-_1d;
if(_1d>0&&_1e>0){
_1b.w=_1d;
_1b.x+=(rtl&&(_12||el.clientLeft>pb.l))?_1e:0;
}
_1d=el.clientHeight;
_1e=_1b.h-_1d;
if(_1d>0&&_1e>0){
_1b.h=_1d;
}
}
if(_1c){
if(_1b.y<0){
_1b.h+=_1b.y;
_1b.y=0;
}
if(_1b.x<0){
_1b.w+=_1b.x;
_1b.x=0;
}
if(_1b.y+_1b.h>_18){
_1b.h=_18-_1b.y;
}
if(_1b.x+_1b.w>_17){
_1b.w=_17-_1b.x;
}
}
var l=_19.x-_1b.x,t=_19.y-Math.max(_1b.y,0),r=l+_19.w-_1b.w,bot=t+_19.h-_1b.h;
if(r*l>0){
var s=Math[l<0?"max":"min"](l,r);
if(rtl&&((_12==8&&!_14)||_12>=9)){
s=-s;
}
_19.x+=el.scrollLeft;
el.scrollLeft+=s;
_19.x-=el.scrollLeft;
}
if(bot*t>0){
_19.y+=el.scrollTop;
el.scrollTop+=Math[t<0?"max":"min"](t,bot);
_19.y-=el.scrollTop;
}
el=(el!=_16)&&!_1c&&el.parentNode;
}
}
catch(error){
console.error("scrollIntoView: "+error);
_d.scrollIntoView(false);
}
};
return _7;
});
