var modalBoxCreerDossier = document.getElementById('modalBoxCreerDossier');
var nomdossier  = document.getElementById('formdossier:nomdossier');
var descriptiondossier  = document.getElementById('formdossier:descriptiondossier');

function fermerModalBoxCreerDossier() {
    modalBoxCreerDossier.style.display = "none";
    nomdossier.value="";
    descriptiondossier.value="";
}

/***Formulaire d'ajout d'un dossier**/
function ouvrirModalBoxCreerDossier(){
    modalBoxCreerDossier.style.display = "block";
}

/***Rechargement simple de la page ***/
function actualiserLaPageDesCourriers(){
    window.location.reload();
}

/***Rechargement simple de la page ***/
function reinitialiserLeFormulaireDeCreationDUnDossier(){
    nomdossier.value="";
    descriptiondossier.value="";
}