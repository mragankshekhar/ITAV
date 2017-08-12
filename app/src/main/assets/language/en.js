$(function () {
    // Lets be professional, shall we?
    "use strict";

    // Some variables for later
    var dictionary, set_lang;
    //English

    var dictionary={
            "LOGIN_FORM": "Login Form",
            "LOGIN": "Signin",
            "SIGNUP": "Signup",
            "FORGOT": "Forgot",
            "BACK": "Back",
            "REMEMBER": "Remember",
            "LOGIN_WITH": "Login with",
            "FACEBOOK": "Facebook",
            "GOOGLE": "Google",

            "SEND": "Send",
            "CHANGE_LANGUAGE": "Change Language",
            "OR": "Or",
            "CREATE_NEW_PROFILE": "Create a new profile",

            //Register
            "ALREADY_HAVE_LOGINID":"Alrady have Login ID",
            "RE_PASSWORD": "Re-type Password",
            "USERNAME": "Username",
            "PASSWORD": "Password",
            "FULLNAME": "Fullname",
            "REGISTER_NOW": "Register Now",
            "EMAIL": "Email",

            //Side menu
            "SERVICE": "Customer Service",
            "ABOUT": "About",
            "FAQ": "FAQ",
            "SETTING": "Setting",
            "FEEDBACK": "Feedback",
            "PRIVACY_POLICY": "Privacy policy",
            "TERMS_OF_USES": "Terms of Uses",
            "SHARE": "Share",
            "EXIT": "Exit",

            //Profile

            "GENERAL_INFORMATION": "General Information",
            "PHONE": "Phone",
            "GENDER": "Gender",
            "MALE": "Male",
            "FEMALE": "Female",
            "DOB": "Date of Birth",
            "PROFILE": "Profile",
            "CHANGE_LANGUAGE": "Change Language",
            "DELETE_ACCOUNT": "Delete Account",
            "LOGOUT": "Logout",

            //heading
            "MY_PROFILE":"My Profile",
            "HEADING_SERVICE": "Service",
            "HEADING_FAQ": "FAQ",
            "HEADING_SETTING": "Setting",
            "HEADING_FEEDBACK": "Feedback",
            "HEADING_PRIVACY_POLICY": "Privacy policy",
            "HEADING_TERMS_OF_USES": "Terms of Uses",
     }
     set_lang = function (dictionary) {
         $("[data-lang]").text(function () {
             var key = $(this).data("lang");
             if (dictionary.hasOwnProperty(key)) {
                 return dictionary[key];
             }
         });
         $("[data-placeholder_lang]").attr("placeholder",function(){
             var key = $(this).data("placeholder_lang");
             if (dictionary.hasOwnProperty(key)) {
                 return dictionary[key];
             }
         })
     };
     set_lang(dictionary);
});