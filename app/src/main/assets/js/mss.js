
myApp.onPageInit('*', function (page) {
    var progressbar     = $('#progressbar');
    	var statustxt       = $('#progresstext');
    	var submitbutton    = $("#submit");
    	var myform          = $(".form_ajax");
    	var title			= document.title;
    	var progressDiv     = $("#progressDiv");
    	var completed       = '0%';
    	$(myform).ajaxForm({
    		beforeSend: function() { //brfore sending form
    			//submitbutton.attr('disabled', ''); // disable upload button
    			//progressDiv.show();
    			//document.title = " Processing Please wait...";
    			//progressbar.width(completed);
    			Android.showToast(" Processing Please wait...")
    		},
    		uploadProgress: function(event, position, total, percentComplete) { //on progress
    			//$("#progress").width(percentComplete + '%');//update progressbar percent complete
    			//$(".statustxt").html(percentComplete + '% completed'); //update status text
    			//addNotice(percentComplete+ "% Completed...", "fa fa-refresh", "");
    			//document.title = percentComplete+ "% Completed..."
    			//submitbutton.html(percentComplete+' % Processing...');
    		},
    		complete: function(response) { // on complete
    			//x$.gritter.removeAll();
    			//document.title = title;
    			//submitbutton.removeAttr('disabled'); //enable submit button
    			//progressDiv.slideUp(); // hide progressbar
    			Android.log(response.responseText);
    			var data=$.parseJSON(response.responseText);
    			if(data.status=="success"){
    				if(data.type=="alert"){
    					Android.showToast(data.message)
    				}
    				else if(data.type=="image"){
    					Android.showToast(data.message);
    					$("#"+data.imgid).attr("src",data.imgurl);
    				}
    				else if(data.type=="div"){
    					$("#"+data.divid).html(data.message);
    				}
    				else if(data.type=="url"){
    					Android.showToast(data.message);
    					if(data.myfunction=="saveUser"){
                            Android.saveUser(data.id,data.uname,data.pass,data.avatar,data.fullname);
                            Android.reload("");
    					}else{
    					    setInterval(function(){ window.location=data.url; }, 2000);
    					}
    				}else if(data.type=="popup"){
    					Android.showToast(data.message);
    				}else{
    					Android.showToast(data.message);
    				}
    				//resetbutton.click();
    			}
    			else if(data.status=="error"){
    				Android.showToast(data.message);
    			}
    		}
    	});
        $$(".ms-lang").on('click',function(){
           mainView.router.reloadPage();
       })
       $$('img.lazy').trigger('lazy');
       ImportJs("language/"+mylang+".js");
       updateReg(regID);setLanguage(mylang);setLatLang(lat,long);
       setMyDetail(fullname,username,avatar,password,id)
       fetchUserDetail();
       Android.log("current page id :"+page.name+"\n"+listCookies());
       if(page.name=="profile"){
            ImportJs("js/profile.js");
       }else if(page.name=="about"){
            getPageDetail('Aboutus');
       }else if(page.name=="service"){
            getPageDetail('Services');
       }else if(page.name=="faq"){
            getPageDetail('faq');
       }else if(page.name=="setting"){

       }else if(page.name=="feedback"){

       }else if(page.name=="privacy_policy"){
            getPageDetail('Privacypolicy');
       }else if(page.name=="terms_of_uses"){
            getPageDetail('TermsofUses');
       }
 });

var fullname="",username="",avatar="",password="",id="";
function setMyDetail(fullname_u,username_u,avatar_u,password_u,id_u){
    fullname=fullname_u,username=username_u,avatar=avatar_u,password=password_u,id=id_u;
    var finalAvatar="http://mssinfotech.in/itav/uploads/avatar/"+avatar
    Android.log(finalAvatar)
    $$(".avatar").attr('src',finalAvatar);
    $$(".fullname").html(fullname);
    $$(".username").html(username);
    //$$(".email").html(email);
}
var mylang="",regID="",lat="",long="";
function setLatLang(lat_p,long_p){
   setCookie("lat",lat_p,"365")
   setCookie("long",long_p,"365")
   $$(".lat").val(lat_p);
   $$(".long").val(long_p);
   lat=lat_p;long=long_p;
}

function setLanguage(language){
   setCookie("language",language,"365")
   $$(".lang").val(language);
   mylang=language;
}
function ImportJs(jsfile){
   var imported = document.createElement('script');
   imported.src = jsfile;
   document.head.appendChild(imported);
   Android.log("update language js=>"+jsfile)
}
function updateReg(reg){
    $$('.reg_id').val(reg);
    regID=reg;
}
function getPageDetail(id){
    var dataDetails=getCookie(id);
    if(dataDetails!=""){
        $$("#page-body").html(dataDetails);
    }
    var url="http://www.mssinfotech.in/itav/api/cms.php?type=fetch-page&id="+id+"&lang="+mylang;
    $$.post(url,function(data){
        setCookie(id, data, "365");
        $$("#page-body").html(data);

    })
}
function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires="+d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
function listCookies() {
    var theCookies = document.cookie.split(';');
    var aString = '';
    for (var i = 1 ; i <= theCookies.length; i++) {
        aString += i + ' ' + theCookies[i-1] + "\n";
    }
    return aString;
}
function fetchUserDetail(){
    if(username!="" && password!=""){
        $.getJSON("http://www.mssinfotech.in/itav/api/profile.php",{"type":"fetch-profile","uid":username,"pass":password},function(data){
            if(data.status=="success"){
                setCookie("uname",data.fullname,"365");
                setCookie("username",data.username,"365");
                setCookie("email",data.email,"365");
                setCookie("phone",data.mobile,"365");
                setCookie("gender",data.gender,"365");
                setCookie("dob",data.dob,"365");
                setCookie("is_public",data.is_public,"365")
                setCookie("avatar","http://www.mssinfotech.in/itav/uploads/"+data.avatar,"365");
            }else{
                Android.showToast("please check your network ")
            }
        })
    }
}
$('.page-content').on('scroll',function() {
    var scroll = $('.page-content').scrollTop();
    console.log(scroll)
    if (scroll >= 100) {
        $('.navbar').removeClass('mss-navbar', 1000, "easeInBack");
        $('.navbar-inner').removeClass('mss-navbar-inner', 1000, "easeInBack");
    } else {
        $('.navbar').addClass('mss-navbar', 1000, "easeInBack");
        $('.navbar-inner').addClass('mss-navbar-inner', 1000, "easeInBack");
    }
});