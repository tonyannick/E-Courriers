var motdepasseUser =  document.getElementById("formMonCompte:motDePasseUser");
var nomUser =  document.getElementById("formMonCompte:nomUser");
/***revélé le mot de passe***/
function voirMotDePasseUser(){
    if (motdepasseUser.type === "password") {
        motdepasseUser.type = "text";
    } else {
        motdepasseUser.type = "password";
    }
}

/***Mettre nom en majuscule***/
function mettreTexteEnMajusculeAuClick(element){
    var start = element.target.selectionStart;
    var end = element.target.selectionEnd;
    element.target.value = element.target.value.toUpperCase();
    element.target.setSelectionRange(start, end);
}

nomUser.addEventListener("keyup", mettreTexteEnMajusculeAuClick, false);