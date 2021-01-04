var formListeDesCourriersRecus = document.getElementById('formListeDesCourriersRecus');
var formAjouterUnCourrierRecu = document.getElementById('formAjouterUnCourrierRecu');
var buttonform = document.getElementById('buttonform');
var buttonlist = document.getElementById('buttonlist');
var objetCourrier  = document.getElementById("formAjouterUnCourrierRecu:objetCourrier");
var motsCles = document.getElementById('formAjouterUnCourrierRecu:motsCles');
var commentaires = document.getElementById('formAjouterUnCourrierRecu:commentaires');
var checkTypeEmetteur = false;
var typeDEmetteur = null;


/****checkbox confidentiel****/
var isConfidentiel = false;
function checkIfCourrierIsConfidentiel() {
    isConfidentiel = !isConfidentiel;
    if (isConfidentiel) {

        objetCourrier.disabled = true;
        motsCles.disabled = true;
        commentaires.disabled = true;

        objetCourrier.style.backgroundColor="#989898";
        motsCles.style.backgroundColor="#989898";
        commentaires.style.backgroundColor="#989898";

        objetCourrier.value="Confidentiel";
        motsCles.value="Confidentiel";
        commentaires.value="Confidentiel";

    } else {
        objetCourrier.disabled = false;
        motsCles.disabled = false;
        commentaires.disabled = false;

        objetCourrier.style.backgroundColor="#EAF1FB";
        motsCles.style.backgroundColor="#EAF1FB";
        commentaires.style.backgroundColor="#EAF1FB";

        objetCourrier.value="";
        motsCles.value="";
        commentaires.value="";
    }

}


function afficherFormulaireCourrierRecu(){
    formAjouterUnCourrierRecu.style.display="block";
    formListeDesCourriersRecus.style.display = "none";
    buttonform.style.display = "none";
    buttonlist.style.display = "block";
}
function afficherListeDesCourriersRecus(){
    formAjouterUnCourrierRecu.style.display="none";
    formListeDesCourriersRecus.style.display = "block";
    buttonlist.style.display = "none";
    buttonform.style.display = "block";
}