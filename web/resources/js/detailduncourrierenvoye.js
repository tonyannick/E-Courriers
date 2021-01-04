/**Gestion de l'accordion Panel***/
var acc = document.getElementsByClassName("accordionDetailCourrier");
var i;

for (i = 0; i < acc.length; i++) {
    acc[i].addEventListener("click", function() {
        this.classList.toggle("active");
        var panel = this.nextElementSibling;
        if (panel.style.maxHeight) {
            panel.style.maxHeight = null;
        } else {
            panel.style.maxHeight = panel.scrollHeight + "px";
        }
    });
}
/***Formulaire d'ajout dans un dossier**/
function ouvrirModalBoxDossier(){
    modalDossier.style.display = "block";
}

var divlistedesdossier = document.getElementById('divlistedesdossier');
var divajouterdansundossier  = document.getElementById('divajouterdansundossier');
var divcreerundossier  = document.getElementById('divcreerundossier');
var bouttonCreerUnDossier = document.getElementById('bouttonCreerUnDossier');
var modalDossier = document.getElementById('modalDossier');

function cacherListeDesDossier(){
    divlistedesdossier.style.display = "none";
    divajouterdansundossier.style.display="block";
}
function afficherListeDesDossier(){
    divlistedesdossier.style.display = "block";
    divajouterdansundossier.style.display="none";
    divcreerundossier.style.display="none";
    bouttonCreerUnDossier.style.display = "block";
}
function creerUnDossier(){
    divlistedesdossier.style.display = "none";
    divcreerundossier.style.display="block";
    bouttonCreerUnDossier.style.display = "none";
}

function fermerModalDossier() {
    modalDossier.style.display = "none";
}

/***Rechargement simple de la page ***/
function rechargerLaPageDetailDUnCourrierEnvoye(){
    window.location.reload();
}

/***Gestion du tab***/
function openTab(evt, cityName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace("active", "");
    }
    document.getElementById(cityName).style.display = "block";
    evt.currentTarget.className += " active";
}

// Get the element with id="defaultOpen" and click on it
document.getElementById("defaultOpen").click();


var reponseCourrierPopUp = document.getElementById("reponseCourrierPopUp");
function fermerReponseCourrierPopUp(){
    reponseCourrierPopUp.style.display="none";
}

/**Formulaire de réponse à un courrier**/
function ouvrirModalBoxRepondreAUnCourrier(){
    reponseCourrierPopUp.style.display = "block";
}

var imageloading = document.getElementById("form:imageloading");
var divbutton = document.getElementById("divbutton");

function afficherLoadingRepondreAUnCourrier() {
    imageloading.style.display="block";
    divbutton.style.display="none";
}
function retirerLoadingRepondreAUnCourrier() {
    imageloading.style.display="none";
    divbutton.style.display="block";
}