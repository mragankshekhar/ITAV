$(function () {
       // Lets be professional, shall we?
    "use strict";

    // Some variables for later
    var dictionary, set_lang;
    //German

    dictionary={
            "LOGIN_FORM": "Logon-Formular",
            "LOGIN": "Logon",
            "SIGNUP": "Verpflichtung",
            "FORGOT": "Vergaß",
            "BACK": "Rückseite",
            "REMEMBER": "Erinnern Sie sich",
            "LOGIN_WITH": "Logon mit",
            "FACEBOOK": "Facebook",
            "GOOGLE": "Google",

            "SEND": "Senden Sie",
            "CHANGE_LANGUAGE": "Ändern Sie Sprache",
            "OR": "Oder",
            "CREATE_NEW_PROFILE": "Erstellen Sie ein neues Profil",

            //register
            "ALREADY_HAVE_LOGINID":"Alrady haben Logon Identifikation",
            "USERNAME": "Benutzername",
            "PASSWORD": "Kennwort",
            "RE_PASSWORD": "Tippen Sie Kennwort neu",
            "FULLNAME": "Vollständiger Name",
            "EMAIL": "eMail",
            "REGISTER_NOW": "Verpflichtung jetzt",

            //Side menu
            "SERVICE": "Kundendienst",
            "ABOUT": "Über",
            "FAQ": "FAQ",
            "SETTING": "Einstellung",
            "FEEDBACK": "Feed-back",
            "PRIVACY_POLICY": "Privacy policy",
            "TERMS_OF_USES": "Ausdrücke des Gebrauches",
            "SHARE": "Anteil",
            "EXIT": "Ausgang",

            //Profile
            "GENERAL_INFORMATION": "General Information",
            "PHONE": "Telefon",
            "GENDER": "Geschlecht",
            "MALE": "Mann",
            "FEMALE": "Frau",
            "DOB": "Geburtsdatum",
            "PROFILE": "Profil",
            "CHANGE_LANGUAGE": "Ändern Sie Sprache",
            "DELETE_ACCOUNT": "Löschung-Konto",
            "LOGOUT": "Logout",

            //heading
            "MY_PROFILE":"Mein Profil",
            "SERVICE": "Service",
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