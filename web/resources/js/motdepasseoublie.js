var divform =  document.getElementById("divform");
var divloadingmail =  document.getElementById("divloadingmail");

function afficherloadingEnvoyerMail(){
    divform.style.display="none";
    divloadingemail.style.display= "block";
}

function cacherloadingEnvoyerMail(){
    divform.style.display="block";
    divloadingmail.style.display= "none";
}