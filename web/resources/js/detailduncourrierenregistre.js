


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

/***Rechargement simple de la page ***/
function rechargerLaPage(){
    window.location.reload();
}

function closeFormDiscussion() {
    document.getElementById("discussionForm").style.display = "none";
}

var modalModalFileReplacement = document.getElementById("myModalFileReplacement");
var loadingChangementDeFichier = document.getElementById("loadingChangementDeFichier");
var fileUpload = document.getElementById("fileUpload");
var loadingDiscussion = document.getElementById("loadingDiscussion");
var fileUploadDiscussion = document.getElementById("fileUploadDiscussion");


/***Formulaire de changement de fichier**/
function ouvrirModalBoxChangerFichierDuCourrier(){
    modalModalFileReplacement.style.display = "block";
}

/***Fermer le formulaire de changement de fichier**/
function closeModalChangerFichierCourrier(){
    modalModalFileReplacement.style.display = "none";
}

function afficherLoadingAuRemplacementDuFichier() {
    loadingChangementDeFichier.style.display = "block";
    fileUpload.style.display = "none";
}

function afficherLoadingCreerUneDiscussion() {
    loadingDiscussion.style.display = "block";
    fileUploadDiscussion.style.display = "none";
}

function retirerLoadingCreerUneDiscussion() {
    loadingDiscussion.style.display = "none";
    fileUploadDiscussion.style.display = "block";
}

function retirerLoading(){
    loadingChangementDeFichier.style.display = "none";
    fileUpload.style.display = "block";
}


