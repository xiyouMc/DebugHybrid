$( document ).ready(function() {
    $("#hybridUrl").keypress(function(e){
        if(e.which == 13) {
            openFunction();
        }
    });
});

function openFunction(){
  var url = $('#hybridUrl').val();
  $.ajax(
    {
      url:"open?url="+escape(url),
      success: function(result){
        result = JSON.parse(result);
        console.log(result);
      }
    }
  );
}

/**
 * json美化
 *   jsonFormat2(json)这样为格式化代码。
 *   jsonFormat2(json,true)为开启压缩模式
 * @param txt
 * @param compress
 * @returns {string}
 */
function jsonFormat(txt,compress){
    var indentChar = '    ';
    if(/^\s*$/.test(txt)){
        alert('数据为空,无法格式化! ');
        return;
    }
    try{var data=eval('('+txt+')');}
    catch(e){
        alert('数据源语法错误,格式化失败! 错误信息: '+e.description,'err');
        return;
    };
    var draw=[],last=false,This=this,line=compress?'':'\n',nodeCount=0,maxDepth=0;

    var notify=function(name,value,isLast,indent/*缩进*/,formObj){
        nodeCount++;/*节点计数*/
        for (var i=0,tab='';i<indent;i++ )tab+=indentChar;/* 缩进HTML */
        tab=compress?'':tab;/*压缩模式忽略缩进*/
        maxDepth=++indent;/*缩进递增并记录*/
        if(value&&value.constructor==Array){/*处理数组*/
            draw.push(tab+(formObj?('"'+name+'":'):'')+'['+line);/*缩进'[' 然后换行*/
            for (var i=0;i<value.length;i++)
                notify(i,value[i],i==value.length-1,indent,false);
            draw.push(tab+']'+(isLast?line:(','+line)));/*缩进']'换行,若非尾元素则添加逗号*/
        }else   if(value&&typeof value=='object'){/*处理对象*/
            draw.push(tab+(formObj?('"'+name+'":'):'')+'{'+line);/*缩进'{' 然后换行*/
            var len=0,i=0;
            for(var key in value)len++;
            for(var key in value)notify(key,value[key],++i==len,indent,true);
            draw.push(tab+'}'+(isLast?line:(','+line)));/*缩进'}'换行,若非尾元素则添加逗号*/
        }else{
            if(typeof value=='string')value='"'+value+'"';
            draw.push(tab+(formObj?('"'+name+'":'):'')+value+(isLast?'':',')+line);
        };
    };
    var isLast=true,indent=0;
    notify('',data,isLast,indent,false);
    return draw.join('');
}

function inspect(){
    if(judgeUA()){
        location.href='chrome://inspect/#devices';
    }
}
function judgeUA(){
        var Sys = {};
        var ua = navigator.userAgent.toLowerCase();
        if (window.ActiveXObject)
            Sys.ie = ua.match(/msie ([\d.]+)/)[1]
        else if (document.getBoxObjectFor)
            Sys.firefox = ua.match(/firefox\/([\d.]+)/)[1]
        else if (window.MessageEvent && !document.getBoxObjectFor)
            Sys.chrome = ua.match(/chrome\/([\d.]+)/)[1]
        else if (window.opera)
            Sys.opera = ua.match(/opera.([\d.]+)/)[1]
        else if (window.openDatabase)
            Sys.safari = ua.match(/version\/([\d.]+)/)[1];

        //以下进行测试
//        if(Sys.ie) document.write('IE: '+Sys.ie);
//        if(Sys.firefox) document.write('Firefox: '+Sys.firefox);
        if(!Sys.chrome){
        alert("请在Chrome浏览器打开该页面!");
        return false;
        }
        else return true;
//        if(Sys.opera) document.write('Opera: '+Sys.opera);
//        if(Sys.safari) document.write('Safari: '+Sys.safari);
}
