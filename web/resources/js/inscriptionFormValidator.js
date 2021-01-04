var phoneFormatCheck = false;
var emailFormatCheck = false;
var dateNaissanceFormatCheck = false;
var dateDebutBourseFormatCheck = false;
var dateFinBourseFormatCheck = false;
var dateInscriptionDernierEtablissementCheck= false;
var dateDepartDernierEtablissementCheck= false;
var dateBacCheck =  false;

var nom = document.getElementById('inscriptionForm:nom');
var prenom = document.getElementById('inscriptionForm:prenom');
var datedenaissance = document.getElementById('inscriptionForm:datedenaissance');
var lieudenaissance = document.getElementById('inscriptionForm:lieudenaissance');
var adresse = document.getElementById('inscriptionForm:adresse');
var telephone = document.getElementById('inscriptionForm:telephone');
var email = document.getElementById('inscriptionForm:email');

var datedebutbourse = document.getElementById('inscriptionForm:datedebutbourse');
var datefinbourse = document.getElementById('inscriptionForm:datefinbourse');

var nomPere = document.getElementById('inscriptionForm:nomPere');
var adressePere = document.getElementById('inscriptionForm:adressePere');
var telephonePere = document.getElementById('inscriptionForm:telephonePere');
var nomMere = document.getElementById('inscriptionForm:nomMere');
var adresseMere = document.getElementById('inscriptionForm:adresseMere');
var telephoneMere = document.getElementById('inscriptionForm:telephoneMere');

var titreEquivalentBac = document.getElementById('inscriptionForm:titreEquivalentBac');
var dateDObtention = document.getElementById('inscriptionForm:dateDObtention');
var dateDObtentionBac = document.getElementById('inscriptionForm:dateDObtentionBac');
var serieBac = document.getElementById('inscriptionForm:serieBac');
var centreBac = document.getElementById('inscriptionForm:centreBac');

var dateDepartDernierEtablissement = document.getElementById('inscriptionForm:dateDepartDernierEtablissement');
var dateInscriptionDernierEtablissement = document.getElementById('inscriptionForm:dateInscriptionDernierEtablissement');
var nomDernierEtablissement = document.getElementById('inscriptionForm:nomDernierEtablissement');
var filiere = document.getElementById('inscriptionForm:filiere');
var choixEquivalentBac = document.getElementById('inscriptionForm:choixEquivalentBac');
var panelGridFiliere = document.getElementById('inscriptionForm:panelGridFiliere');

var diplomeApresLeBac = document.getElementById('inscriptionForm:diplomeApresLeBac');
var dateDiplomeApresLeBac = document.getElementById('inscriptionForm:dateDiplomeApresLeBac');
var niveauActuel = document.getElementById('inscriptionForm:niveauActuel');
var panelGrid = document.getElementById('inscriptionForm:panelGrid');

var checkSexe = false;
var checkEtatCivil = false;
var checkBourse = false;
var checkEquivalentBac = false;
var checkBac = false;
var checkTypeDePersonne = false;
var checkCycle = false;

var phoneValide = /^0[1-7][0-9]{7}$/;
var emailValide = /^[a-z0-9._-]+@[a-z0-9._-]+\.[a-z]{2,8}$/;
var dateValide =/^\d{1,2}\/\d{1,2}\/\d{4}$/;

telephone.addEventListener("blur", function(click){
    if (telephone.value){
        if (!telephone.value.match(phoneValide)){
            swal("Erreur!", "Ce numéro de téléphone ne semble pas correct, voici un exemple : 062254123", "error");
            //telephone.style.borderColor='red';
            telephone.value="";
        }else{
            phoneFormatCheck = true;
        }
    }
});

email.addEventListener("blur", function(click){
    if(email.value){
        if (!email.value.match(emailValide)) {
            swal("Erreur!", "Cette adresse email ne semble pas correct, voici un exemple : arthurgerard@gmail.com", "error");
            email.value="";
        }else{
            emailFormatCheck = true;
        }
    }
});

datedenaissance.addEventListener("blur", function(click){
    if(datedenaissance.value){
        if (!datedenaissance.value.match(dateValide)) {
            swal("Erreur!", "Date invalide, respecté le format indiqué svp, voici un exemple : 24/05/1985", "error");
            datedenaissance.value="";
        }else{
            dateNaissanceFormatCheck = true;
        }
    }
});
datedebutbourse.addEventListener("blur", function(click){
    if(datedebutbourse.value){
        if (!datedebutbourse.value.match(dateValide)) {
            swal("Erreur!", "Date invalide, respecté le format indiqué svp, voici un exemple : 24/05/1985", "error");
            datedebutbourse.value="";
        }else{
            dateDebutBourseFormatCheck= true;
        }
    }
});
datefinbourse.addEventListener("blur", function(click){
    if(datefinbourse.value){
        if (!datefinbourse.value.match(dateValide)) {
            swal("Erreur!", "Date invalide, respecté le format indiqué svp, voici un exemple : 24/05/1985", "error");
            datefinbourse.value="";
        }else{
            dateFinBourseFormatCheck = true;
        }
    }
});
dateInscriptionDernierEtablissement.addEventListener("blur", function(click){
    if(dateInscriptionDernierEtablissement.value){
        if (!dateInscriptionDernierEtablissement.value.match(dateValide)) {
            swal("Erreur!", "Date invalide, respecté le format indiqué svp, voici un exemple : 24/05/2020", "error");
            dateInscriptionDernierEtablissement.value="";
        }else{
            dateInscriptionDernierEtablissementCheck = true;
        }
    }
});
dateDepartDernierEtablissement.addEventListener("blur", function(click){
    if(dateDepartDernierEtablissement.value){
        if (!dateDepartDernierEtablissement.value.match(dateValide)) {
            swal("Erreur!", "Date invalide, respecté le format indiqué svp, voici un exemple : 24/05/2015", "error");
            dateDepartDernierEtablissement.value="";
        }else{
            dateDepartDernierEtablissementCheck = true;
        }
    }
});

dateDObtentionBac.addEventListener("blur", function(click){
    if(dateDObtentionBac.value){
        if (!dateDObtentionBac.value.match(dateValide)) {
            swal("Erreur!", "Date invalide, respecté le format indiqué svp, voici un exemple : 24/05/2020", "error");
            dateDObtentionBac.value="";
        }else{
            dateBacCheck = true;
        }
    }
});

/***Mettre le nom en Majuscule***/
function mettreLeNomEnMajuscule(element){
    var start = element.target.selectionStart;
    var end = element.target.selectionEnd;
    element.target.value = element.target.value.toUpperCase();
    element.target.setSelectionRange(start, end);
}
function mettreLeNomDuPereEnMajuscule(element){
    var start = element.target.selectionStart;
    var end = element.target.selectionEnd;
    element.target.value = element.target.value.toUpperCase();
    element.target.setSelectionRange(start, end);
}
function mettreLeNomDeLaMereEnMajuscule(element){
    var start = element.target.selectionStart;
    var end = element.target.selectionEnd;
    element.target.value = element.target.value.toUpperCase();
    element.target.setSelectionRange(start, end);
}

nom.addEventListener("keyup", mettreLeNomEnMajuscule, false);
nomPere.addEventListener("keyup",mettreLeNomDuPereEnMajuscule, false);
nomMere.addEventListener("keyup",mettreLeNomDeLaMereEnMajuscule, false);

function clickRadioSexe() {
    if(document.forms['inscriptionForm']['inscriptionForm:choixSexe'][0].checked == true || document.forms['inscriptionForm']['inscriptionForm:choixSexe'][1].checked){
        checkSexe = true;
    }
}

function clickRadioEtatCivil() {
    if(document.forms['inscriptionForm']['inscriptionForm:choixEtatCivil'][0].checked == true || document.forms['inscriptionForm']['inscriptionForm:choixEtatCivil'][1].checked){
        checkEtatCivil = true;
    }
}

function clickRadioBourse() {
    if(document.forms['inscriptionForm']['inscriptionForm:choixBourse'][0].checked == true){
        PF('categorieBourse').enable();
        datedebutbourse.style.display='block';
        datefinbourse.style.display='block';
        checkBourse = true;
    }else if( document.forms['inscriptionForm']['inscriptionForm:choixBourse'][1].checked == true){
        PF('categorieBourse').disable();
        datedebutbourse.style.display='none';
        datefinbourse.style.display='none';
        checkBourse = false;
    }
}

function clickRadioEquivalentDuBac() {
    if(document.forms['inscriptionForm']['inscriptionForm:choixEquivalentBac'][0].checked == true ){
        checkEquivalentBac = true;
        PF('choixBac').disable();
    }else if(document.forms['inscriptionForm']['inscriptionForm:choixEquivalentBac'][1].checked){
        PF('choixBac').enable();
        titreEquivalentBac.style.display='none';
        dateDObtention.style.display='none';
        dateDObtentionBac.style.display='block';
        serieBac.style.display='block';
        centreBac.style.display='block';
        choixEquivalentBac.style.display = 'none';
        document.getElementsByTagName("h4")[25].style.display='none';
        document.getElementsByTagName("h4")[26].style.display='none';
        document.getElementsByTagName("h4")[27].style.display='none';
        document.getElementsByTagName("h4")[28].style.display='block';
        document.getElementsByTagName("h4")[29].style.display='block';
        document.getElementsByTagName("h4")[30].style.display='block';
    }
}

function clickRadioBac() {
    if(document.forms['inscriptionForm']['inscriptionForm:choixBac'][0].checked == true ){
        checkBac = true;
        checkEquivalentBac = false;
        document.getElementsByTagName("h4")[25].style.display='none';
        document.getElementsByTagName("h4")[26].style.display='none';
        document.getElementsByTagName("h4")[27].style.display='none';
        document.getElementsByTagName("h4")[28].style.display='block';
        document.getElementsByTagName("h4")[29].style.display='block';
        document.getElementsByTagName("h4")[30].style.display='block';
        titreEquivalentBac.style.display='none';
        dateDObtention.style.display='none';
        dateDObtentionBac.style.display='block';
        serieBac.style.display='block';
        centreBac.style.display='block';
        choixEquivalentBac.style.display = 'none';
    }else if(document.forms['inscriptionForm']['inscriptionForm:choixBac'][1].checked == true){
        checkEquivalentBac = true;
        checkBac = false;
        document.getElementsByTagName("h4")[24].style.display='block';
        document.getElementsByTagName("h4")[25].style.display='block';
        document.getElementsByTagName("h4")[26].style.display='block';
        document.getElementsByTagName("h4")[27].style.display='block';
        choixEquivalentBac.style.display = 'block';
        document.getElementsByTagName("h4")[28].style.display='none';
        document.getElementsByTagName("h4")[29].style.display='none';
        document.getElementsByTagName("h4")[30].style.display='none';
        PF('choixBac').disable();
        titreEquivalentBac.style.display='block';
        dateDObtention.style.display='block';
        dateDObtentionBac.style.display='none';
        serieBac.style.display='none';
        centreBac.style.display='none';

    }
}

function clickRadioTypeDePersonne() {
    if(document.forms['inscriptionForm']['inscriptionForm:choixEtatTypeDemandeur'][0].checked == true ){
        checkTypeDePersonne = true;
        document.getElementsByTagName("h4")[36].style.display='block';
        document.getElementsByTagName("h4")[37].style.display='block';

        document.getElementsByTagName("h4")[35].style.display='block';
        diplomeApresLeBac.style.display='block';
        dateDiplomeApresLeBac.style.display='block';
        niveauActuel.style.display='block';

    }else if(document.forms['inscriptionForm']['inscriptionForm:choixEtatTypeDemandeur'][1].checked == true){
        checkTypeDePersonne = false;
        document.getElementsByTagName("h4")[35].style.display='none';
        document.getElementsByTagName("h4")[36].style.display='none';
        document.getElementsByTagName("h4")[37].style.display='none';
        diplomeApresLeBac.style.display='none';
        dateDiplomeApresLeBac.style.display='none';
        niveauActuel.style.display='none';


    }
}


/***Block UI JS HoldOn.js***/
var options1 = {
    theme:"custom",
    content:'<h:graphicImage value="./resources/images/loading.gif" id="loadingcourrier"/>',
    message:'Enregistrement en cours, veuillez patienter.... ',
    backgroundColor:"#d7d4d71",
    textColor:"#1847b1"
};

function debutBlocageUI(){
    HoldOn.open(options1);
}
function finBlocageUI() {
    HoldOn.close();
}

function verifierFormulaire(){
   /* if(checkSexe == false) {
        swal("Attention!", "Vous devez renseigner le sexe!", "warning");
    }else{
        if(checkEtatCivil== false) {
            swal("Attention!", "Vous devez renseigner l'état civil ", "warning");
        }else{
            if(!nom.value && !prenom.value){
                swal("Attention!", "Vous devez renseigner le nom et le prénom ", "warning");
            }else{
                if(!datedenaissance.value && !lieudenaissance.value){
                    swal("Attention!", "Vous devez renseigner la date et le lieu de naissance", "warning");
                }else{
                    if(!telephone.value && !email.value){
                        swal("Attention!", "Vous devez renseigner le téléphone et l'émail", "warning");
                    }else{
                        if(!PF('listeNationalite').value){
                            swal("Attention!", "Vous devez renseigner la nationalité", "warning");
                        }else{
                            if(!nomPere.value && !adressePere.value && !telephonePere.value) {
                                swal("Attention!", "Vous devez renseigner les informations du pére", "warning");
                            } else{
                                if(!nomPere.value && !adressePere.value && !telephonePere.value) {
                                    swal("Attention!", "Vous devez renseigner les informations du pére", "warning");
                                } else{
                                    if(!nomMere.value && !adresseMere.value && !telephoneMere.value) {
                                        swal("Attention!", "Vous devez renseigner les informations de la mère", "warning");
                                    }else{
                                        if(checkBourse == false) {
                                            swal("Attention!", "Vous devez indiquer si vous etes boursier ou pas!", "warning");
                                        }else{
                                            if(!nomDernierEtablissement.value && !dateDepartDernierEtablissement.value && !dateInscriptionDernierEtablissement.value) {
                                                swal("Attention!", "Vous devez renseigner les informations concernant votre dernier établissement", "warning");
                                            }else{
                                                if(!PF('listCycle').value){
                                                    swal("Attention!", "Cliquez sur le cycle pour lequel vous postulé", "warning");
                                                }else{
                                                     if(checkBac == false) {
                                                        swal("Attention!", "Vous devez indiquer si vous etes bachelier ou pas!", "warning");
                                                    }else{*/
                                                         PF('validerFormulaire').show();
                                                    /* }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }*/
}
