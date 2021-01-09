var nomEmetteur =  document.getElementById("courrierForm:nomEmetteur");
var prenomEmetteur = document.getElementById("courrierForm:prenomEmetteur");
var nomRaisonSocialDestinataire = document.getElementById("courrierForm:nomRaisonSocialDestinataire");
var directionEntrepriseDestinataire = document.getElementById("courrierForm:directionEntrepriseDestinataire");
var nomParticulierDestinataire =  document.getElementById("courrierForm:nomParticulierDestinataire");
var prenomParticulierDestinataire =  document.getElementById("courrierForm:prenomParticulierDestinataire");
var telephoneEntrepriseDestinataire = document.getElementById("courrierForm:telephoneEntrepriseDestinataire");
var fonctionEntrepriseDestinataire = document.getElementById("courrierForm:fonctionEntrepriseDestinataire");
var telephoneEntrepriseEmetteur = document.getElementById("courrierForm:telephoneEntrepriseEmetteur");
var telephoneParticulierDestinataire = document.getElementById("courrierForm:telephoneParticulierDestinataire");
var telephoneParticulierEmetteur = document.getElementById("courrierForm:telephoneParticulierEmetteur");
var emailEntrepriseDestinataire = document.getElementById("courrierForm:emailEntrepriseDestinataire");
var emailParticulierDestinataire = document.getElementById("courrierForm:emailParticulierDestinataire");
var directionAutreMinistereDestinataire = document.getElementById("courrierForm:directionAutreMinistereDestinataire");
var fonctionAutreMinistereDestinataire= document.getElementById("courrierForm:fonctionAutreMinistereDestinataire");
var emailEntrepriseEmetteur = document.getElementById("courrierForm:emailEntrepriseEmetteur");
var emailParticulierEmetteur = document.getElementById("courrierForm:emailParticulierEmetteur");
var referenceCourrier  = document.getElementById("courrierForm:referenceCourrier");
var objetCourrier  = document.getElementById("courrierForm:objetCourrier");
var directionAutreMinistereEmetteur  = document.getElementById("courrierForm:directionAutreMinistereEmetteur");
var fonctionAutreMinistereEmetteur = document.getElementById("courrierForm:fonctionAutreMinistereEmetteur");
var nomRaisonSocialEmetteur = document.getElementById("courrierForm:nomRaisonSocialEmetteur");
var directionEntrepriseEmetteur = document.getElementById("courrierForm:directionEntrepriseEmetteur");
var fonctionEntrepriseEmetteur = document.getElementById("courrierForm:fonctionEntrepriseEmetteur");



var divDestinataireAgentDuMinistere = document.getElementById('divDestinataireAgentDuMinistere');
var divDestinataireAgentAutreMinistere = document.getElementById('divDestinataireAgentAutreMinistere');
var divDestinataireParticulier = document.getElementById('divDestinataireParticulier');
var divDestinataireEntrepriseOuAssociation = document.getElementById('divDestinataireEntrepriseOuAssociation');

var divEmetteurAgentDuMinistere = document.getElementById('divEmetteurAgentDuMinistere');
var divEmetteurAgentAutreMinistere = document.getElementById('divEmetteurAgentAutreMinistere');
var divEmetteurParticulier = document.getElementById('divEmetteurParticulier');
var divEmetteurEntrepriseOuAssociation = document.getElementById('divEmetteurEntrepriseOuAssociation');

var checkBoxConfidentiel = document.getElementById('courrierForm:checkBoxConfidentiel');
var motsCles = document.getElementById('courrierForm:motsCles');
var commentaires = document.getElementById('courrierForm:commentaires');
var divFichierEtAnnnexe = document.getElementById('divFichierEtAnnnexe');
var titreSectionFichier = document.getElementById('titreSectionFichier');

var objetCourrierBean  ="#{nouveauCourrier.courrier.objetCourrier}";
var motClesCourrierBean  ="#{nouveauCourrier.courrier.motsclesCourrier";
var commentairesCourrierBean  ="#{nouveauCourrier.courrier.commentairesCourrier}";


var typeDEmetteur = null;
var istypeDEmetteurOk = false;
var typeDeDestinataire = null;
var istypeDestinataireOk = false;
var checkTypeEmetteur = false;
var droitRecevoirCourrierDepuisLExterieur = false;
var checkTypeDestinataire = false;
var checkPrioriteCourrier = false;
var telephoneEntrepriseDestinataireFormatCheck = false;
var telephoneEntrepriseEmetteurFormatCheck = false;
var telephoneParticulierDestinataireFormatCheck = false;
var telephoneParticulierEmetteurFormatCheck = false;
var emailEntrepriseDestinataireFormatCheck = false;
var emailParticulierDestinataireFormatCheck = false;
var emailEntrepriseEmetteurFormatCheck = false;
var emailParticulierEmetteurFormatCheck = false;

var phoneValide = /^0[1-7][0-9]{7}$/;
var emailValide = /^[a-z0-9._-]+@[a-z0-9._-]+\.[a-z]{2,8}$/;

/*
 * TimePicker Pris sur le site https://github.com/ericjgagnon/wickedpicker et http://ericjgagnon.github.io/wickedpicker/
 */
var options = {
    twentyFour: true,
    upArrow: 'wickedpicker__controls__control-up',
    downArrow: 'wickedpicker__controls__control-down',
    hoverState: 'hover-state',
    minutesInterval: 1,
    showSeconds: true,
    title: 'Heure',
    timeSeparator: ' : ',

};
$('.timepicker').wickedpicker(options);
/***Fin********TimePicker********/


/****checkbox confidentiel****/
var isConfidentiel = false;

function checkIfCourrierIsConfidentiel(){
    isConfidentiel = !isConfidentiel;
    if (isConfidentiel) {
        console.log("oui");

        objetCourrier.disabled = true;
        motsCles.disabled = true;
        commentaires.disabled = true;

        objetCourrier.style.backgroundColor="#989898";
        motsCles.style.backgroundColor="#989898";
        commentaires.style.backgroundColor="#989898";

        objetCourrier.value="Confidentiel";
        motsCles.value="Confidentiel";
        commentaires.value="Confidentiel";

        objetCourrierBean.value="Confidentiel";
        motClesCourrierBean.value="Confidentiel";
        commentairesCourrierBean.value="Confidentiel";

        divFichierEtAnnnexe.style.display='none';
        titreSectionFichier.style.display='none';
    } else {
        console.log("bad");

        objetCourrier.disabled = false;
        motsCles.disabled = false;
        commentaires.disabled = false;

        objetCourrier.style.backgroundColor="#EAF1FB";
        motsCles.style.backgroundColor="#EAF1FB";
        commentaires.style.backgroundColor="#EAF1FB";

        objetCourrierBean.value="";
        motClesCourrierBean.value="";
        commentairesCourrierBean.value="";

        objetCourrier.value="";
        motsCles.value="";
        commentaires.value="";

        divFichierEtAnnnexe.style.display='block';
        titreSectionFichier.style.display='block';
    }
}


/*telephone validator***/
telephoneEntrepriseDestinataire.addEventListener("blur", function(click){
    if (telephoneEntrepriseDestinataire.value){
        if (!telephoneEntrepriseDestinataire.value.match(phoneValide)){
            swal("Erreur!", "Ce numéro de téléphone ne semble pas correct, voici un exemple : 062254123", "error");
            telephoneEntrepriseDestinataire.value="";
        }else{
            telephoneEntrepriseDestinataireFormatCheck = true;
        }
    }
});
telephoneEntrepriseEmetteur.addEventListener("blur", function(click){
    if (telephoneEntrepriseEmetteur.value){
        if (!telephoneEntrepriseEmetteur.value.match(phoneValide)){
            swal("Erreur!", "Ce numéro de téléphone ne semble pas correct, voici un exemple : 062254123", "error");
            telephoneEntrepriseEmetteur.value="";
        }else{
            telephoneEntrepriseEmetteurFormatCheck = true;
        }
    }
});
telephoneParticulierDestinataire.addEventListener("blur", function(click){
    if (telephoneParticulierDestinataire.value){
        if (!telephoneParticulierDestinataire.value.match(phoneValide)){
            swal("Erreur!", "Ce numéro de téléphone ne semble pas correct, voici un exemple : 062254123", "error");
            telephoneParticulierDestinataire.value="";
        }else{
            telephoneParticulierDestinataireFormatCheck = true;
        }
    }
});
telephoneParticulierEmetteur.addEventListener("blur", function(click){
    if (telephoneParticulierEmetteur.value){
        if (!telephoneParticulierEmetteur.value.match(phoneValide)){
            swal("Erreur!", "Ce numéro de téléphone ne semble pas correct, voici un exemple : 062254123", "error");
            telephoneParticulierEmetteur.value="";
        }else{
            telephoneParticulierEmetteurFormatCheck = true;
        }
    }
});

/***emailvalidator***/
emailEntrepriseDestinataire.addEventListener("blur", function(click){
    if (emailEntrepriseDestinataire.value){
        if (!emailEntrepriseDestinataire.value.match(emailValide)){
            swal("Erreur!", "Cet email ne semble pas correct, voici un exemple : arthurgerard@gmail.com", "error");
            emailEntrepriseDestinataire.value="";
        }else{
            emailEntrepriseDestinataireFormatCheck = true;
        }
    }
});
emailParticulierDestinataire.addEventListener("blur", function(click){
    if (emailParticulierDestinataire.value){
        if (!emailParticulierDestinataire.value.match(emailValide)){
            swal("Erreur!", "Cet email ne semble pas correct, voici un exemple : arthurgerard@gmail.com", "error");
            emailParticulierDestinataire.value="";
        }else{
            emailParticulierDestinataireFormatCheck = true;
        }
    }
});
emailEntrepriseEmetteur.addEventListener("blur", function(click){
    if (emailEntrepriseEmetteur.value){
        if (!emailEntrepriseEmetteur.value.match(emailValide)){
            swal("Erreur!", "Cet email ne semble pas correct, voici un exemple : arthurgerard@gmail.com", "error");
            emailEntrepriseEmetteur.value="";
        }else{
            emailEntrepriseEmetteurFormatCheck = true;
        }
    }
});
emailParticulierEmetteur.addEventListener("blur", function(click){
    if (emailParticulierEmetteur.value){
        if (!emailParticulierEmetteur.value.match(emailValide)){
            swal("Erreur!", "Cet email ne semble pas correct, voici un exemple : arthurgerard@gmail.com", "error");
            emailParticulierEmetteur.value="";
        }else{
            emailParticulierEmetteurFormatCheck = true;
        }
    }
});


/***Check de la fonction de l'user***/
var fonctionUser = document.getElementById('courrierForm:userFonction');

/*****Click sur selectOneRadio emeteur ****/
function clicSurSelectOneRadioEmmetteur() {
    checkTypeEmetteur = true;

    if(document.forms['courrierForm']['courrierForm:choixEmetteurSelectOneRadio'][0].checked == true ){
        typeDEmetteur ="Agent du Ministere";
        divEmetteurAgentDuMinistere.style.display = 'block';
        divEmetteurAgentAutreMinistere.style.display = 'none';
        divEmetteurParticulier.style.display = 'none';
        divEmetteurEntrepriseOuAssociation.style.display = 'none';
        /*divDestinataireAgentDuMinistere.style.display = 'none';*/
    }else if(document.forms['courrierForm']['courrierForm:choixEmetteurSelectOneRadio'][1].checked == true){
        typeDEmetteur ="Agent d'un autre Ministère";
        divEmetteurAgentDuMinistere.style.display = 'none';
        divEmetteurAgentAutreMinistere.style.display = 'block';
        divEmetteurParticulier.style.display = 'none';
        divEmetteurEntrepriseOuAssociation.style.display = 'none';
    }else if(document.forms['courrierForm']['courrierForm:choixEmetteurSelectOneRadio'][2].checked == true){
        typeDEmetteur ="Entreprise";
        divEmetteurAgentDuMinistere.style.display = 'none';
        divEmetteurAgentAutreMinistere.style.display = 'none';
        divEmetteurParticulier.style.display = 'none';
        divEmetteurEntrepriseOuAssociation.style.display = 'block';

    }else if(document.forms['courrierForm']['courrierForm:choixEmetteurSelectOneRadio'][3].checked == true){
        typeDEmetteur ="Association";
        divEmetteurAgentDuMinistere.style.display = 'none';
        divEmetteurAgentAutreMinistere.style.display = 'none';
        divEmetteurParticulier.style.display = 'none';
        divEmetteurEntrepriseOuAssociation.style.display = 'block';

    }else if(document.forms['courrierForm']['courrierForm:choixEmetteurSelectOneRadio'][4].checked == true){
        typeDEmetteur ="Particulier";
        divEmetteurAgentDuMinistere.style.display = 'none';
        divEmetteurAgentAutreMinistere.style.display = 'none';
        divEmetteurParticulier.style.display = 'block';
        divEmetteurEntrepriseOuAssociation.style.display = 'none';
    }

}

/*****Click sur selectOneRadio destinataire *****/
function clicSurSelectOneRadioDestinataire() {
    checkTypeDestinataire = true;

    if(document.forms['courrierForm']['courrierForm:choixDestinataireSelectOneRadio'][0].checked == true ){
        typeDeDestinataire ="Agent du Ministere";
        divDestinataireAgentDuMinistere.style.display = 'block';
        divDestinataireAgentAutreMinistere.style.display = 'none';
        divDestinataireParticulier.style.display = 'none';
        divDestinataireEntrepriseOuAssociation.style.display = 'none';

    }else if(document.forms['courrierForm']['courrierForm:choixDestinataireSelectOneRadio'][1].checked == true){
        typeDeDestinataire ="Agent d'un autre Ministère";
        divDestinataireAgentDuMinistere.style.display = 'none';
        divDestinataireAgentAutreMinistere.style.display = 'block';
        divDestinataireParticulier.style.display = 'none';
        divDestinataireEntrepriseOuAssociation.style.display = 'none';

    }else if(document.forms['courrierForm']['courrierForm:choixDestinataireSelectOneRadio'][2].checked == true){
        typeDeDestinataire ="Entreprise";
        divDestinataireAgentDuMinistere.style.display = 'none';
        divDestinataireAgentAutreMinistere.style.display = 'none';
        divDestinataireParticulier.style.display = 'none';
        divDestinataireEntrepriseOuAssociation.style.display = 'block';
    }else if(document.forms['courrierForm']['courrierForm:choixDestinataireSelectOneRadio'][3].checked == true){
        typeDeDestinataire ="Association";
        divDestinataireAgentDuMinistere.style.display = 'none';
        divDestinataireAgentAutreMinistere.style.display = 'none';
        divDestinataireParticulier.style.display = 'none';
        divDestinataireEntrepriseOuAssociation.style.display = 'block';
    }else if(document.forms['courrierForm']['courrierForm:choixDestinataireSelectOneRadio'][4].checked == true){
        typeDeDestinataire ="Particulier";
        divDestinataireAgentDuMinistere.style.display = 'none';
        divDestinataireAgentAutreMinistere.style.display = 'none';
        divDestinataireParticulier.style.display = 'block';
        divDestinataireEntrepriseOuAssociation.style.display = 'none';
    }

}

/***Mettre nom en majuscule***/
function mettreTexteEnMajusculeAuClick(element){
    var start = element.target.selectionStart;
    var end = element.target.selectionEnd;
    element.target.value = element.target.value.toUpperCase();
    element.target.setSelectionRange(start, end);
}

    nomEmetteur.addEventListener("keyup", mettreTexteEnMajusculeAuClick, false);
    nomParticulierDestinataire.addEventListener("keyup",mettreTexteEnMajusculeAuClick,false);
    nomRaisonSocialEmetteur.addEventListener("keyup", mettreTexteEnMajusculeAuClick, false);
    directionEntrepriseEmetteur.addEventListener("keyup",mettreTexteEnMajusculeAuClick,false);
    nomRaisonSocialDestinataire.addEventListener("keyup", mettreTexteEnMajusculeAuClick, false);
    directionEntrepriseDestinataire.addEventListener("keyup",mettreTexteEnMajusculeAuClick,false);
    fonctionEntrepriseEmetteur.addEventListener("keyup",mettreTexteEnMajusculeAuClick,false);


function clicSelectOneRadioPrioriteCourrier() {
    if(document.forms['courrierForm']['courrierForm:choixPrioriteSelectOneRadio'][0].checked == true || document.forms['courrierForm']['courrierForm:choixPrioriteSelectOneRadio'][1].checked == true){
        checkPrioriteCourrier = true;
    }
}

function checkDesValeursDuTypeDEmetteur(){
    switch (typeDEmetteur) {
        case "Agent du Ministere":
            if(!PF('selectOneMenuDirectionEmetteur').value) {
                istypeDEmetteurOk =  false;
            } else{
                 if(!PF('selectOneMenuFonctionEmetteur').value){
                     istypeDEmetteurOk =  false;
                 }else{
                     istypeDEmetteurOk =  true;
                 }
            }
            break;
        case "Agent d'un autre Ministère":
            if(!PF('selectOneMenuAutreMinistereEmetteur').value) {
                istypeDEmetteurOk =  false;
            } else{
                if(!directionAutreMinistereEmetteur.value){
                    istypeDEmetteurOk =  false;
                }else{
                    if(!fonctionAutreMinistereEmetteur.value){
                        istypeDEmetteurOk =  false;
                    }else{
                         istypeDEmetteurOk =  true;
                    }
                }
            }
            break;
        case "Entreprise":
            if(!nomRaisonSocialEmetteur.value) {
                istypeDEmetteurOk =  false;
            } else{
                if(!directionEntrepriseEmetteur.value){
                    istypeDEmetteurOk =  false;
                }else{
                    if(!fonctionEntrepriseEmetteur.value){
                        istypeDEmetteurOk =  false;
                    }else{
                        if(!telephoneEntrepriseEmetteur.value){
                            istypeDEmetteurOk =  false;
                        }else{
                             istypeDEmetteurOk =  true;
                        }

                    }
                }
            }
            break;
        case "Association":
            if(!nomRaisonSocialEmetteur.value) {
                istypeDEmetteurOk =  false;
            } else{
                if(!directionEntrepriseEmetteur.value){
                    istypeDEmetteurOk =  false;
                }else{
                    if(!fonctionEntrepriseEmetteur.value){
                        istypeDEmetteurOk =  false;
                    }else{
                        if(!telephoneEntrepriseEmetteur.value){
                            istypeDEmetteurOk =  false;
                        }else{
                             istypeDEmetteurOk =  true;
                        }

                    }
                }
            }
            break;
        case "Particulier":

            if(!nomEmetteur.value) {
                istypeDEmetteurOk =  false;
            } else{
                if(!prenomEmetteur.value){
                    istypeDEmetteurOk =  false;
                }else{
                    if(!telephoneParticulierEmetteur.value){
                        istypeDEmetteurOk =  false;
                    }else{
                         istypeDEmetteurOk =  true;
                    }
                }
            }
            break;
    }
    return istypeDEmetteurOk;
}

function checkDesValeursDuTypeDeDestinataire(){
    switch (typeDeDestinataire) {
        case "Agent du Ministere":

            if(!PF('selectOneMenuDirectionDestinataire').value) {
                istypeDestinataireOk =  false;
            } else{
                if(!PF('selectOneMenuFonctionDestinataire').value){
                    istypeDestinataireOk =  false;
                }else{
                    istypeDestinataireOk =  true;
                }
            }
            break;
        case "Agent d'un autre Ministère":

            if(!PF('selectOneMenuAutreMinistereDestinataire').value ) {
                istypeDestinataireOk =  false;
            } else{
                if(!directionAutreMinistereDestinataire.value){
                    istypeDestinataireOk =  false;
                }else{
                    if(!fonctionAutreMinistereDestinataire.value){
                        istypeDestinataireOk =  false;
                    }else{
                        istypeDestinataireOk =  true;
                    }
                }
            }
            break;
        case "Entreprise":
            if(!nomRaisonSocialDestinataire.value) {
                istypeDestinataireOk =  false;
            } else{
                if(!directionEntrepriseDestinataire.value){
                    istypeDestinataireOk =  false;
                }else{
                    if(!telephoneEntrepriseDestinataire.value){
                        istypeDestinataireOk =  false;
                    }else{
                        if(!fonctionEntrepriseDestinataire.value){
                            istypeDestinataireOk =  false;
                        }else{
                            istypeDestinataireOk =  true;
                        }

                    }
                }
            }
            break;
        case "Association":
            if(!nomRaisonSocialDestinataire.value) {
                istypeDestinataireOk =  false;
            } else{
                if(!directionEntrepriseDestinataire.value){
                    istypeDestinataireOk =  false;
                }else{
                    if(!telephoneEntrepriseDestinataire.value){
                        istypeDestinataireOk =  false;
                    }else{
                        if(!fonctionEntrepriseDestinataire.value){
                            istypeDestinataireOk =  false;
                        }else{
                            istypeDestinataireOk =  true;
                        }

                    }
                }
            }
            break;
        case "Particulier":

            if(!nomParticulierDestinataire.value) {
                istypeDestinataireOk =  false;
            } else{
                if(!prenomParticulierDestinataire.value){
                    istypeDestinataireOk =  false;
                }else{
                    if(!telephoneParticulierDestinataire.value){
                        istypeDestinataireOk =  false;
                    }else{
                        istypeDestinataireOk =  true;
                    }
                }
            }
            break;
    }

    return istypeDestinataireOk;
}

/***Toast d'ajout de fichier du courrier appelé depuis le bean***/
function validerAjouterFichier(){
    new Toast({
        message: 'Le fichier du courrier à bien été ajouté....',
        type: 'success'
    })
}

/***Block UI JS HoldOn.js***/
var options1 = {
    theme:"custom",
    content:'<h:graphicImage value="./resources/images/loading.gif" id="loadingcourrier"/>',
    message:'Enregistrement en cours, veuillez patienter.... ',
    backgroundColor:"#d7d4d71",
    textColor:"#1847b1"
};

/***Rechargement simple de la page ***/
function rechargerLaPageDuFormulaire(){
    setTimeout(function () {
        objetCourrierBean.value="";
        motClesCourrierBean.value="";
        commentairesCourrierBean.value="";
        window.location.reload();
    }, 500);
}


/***bloquage de l'UI lors de l'enregistrement du courrier
 *  méthode appelée directement dans le backing bean de la vue
 * **/
function debutBlocageUI(){
    HoldOn.open(options1);
}

function finBlocageUI() {
    HoldOn.close();
}


/***Controle du formulaire avant la validation***/
function verifierLeFormulaireAvantValidation(){
    checkDesValeursDuTypeDEmetteur();
    checkDesValeursDuTypeDeDestinataire();
    //checkDesValeursDuTypeDeDestinataire();
    /*console.log("istypeDestinataireOk " +istypeDestinataireOk);
    console.log("istypeDEmetteurOk "+istypeDEmetteurOk);
    console.log(" 1 : "+PF('selectOneMenuDirectionDestinataire').value);
    console.log(" 2 : "+PF('selectOneMenuFonctionDestinataire').value);*/

        if(!checkTypeEmetteur){
            swal("Attention!", "Vous devez renseigner le type d'émetteur !!", "warning");
        }else{
            if(!checkTypeDestinataire){
                swal("Attention!", "Vous devez renseigner le type de destinataire !!", "warning");
            }else{
                if(!checkPrioriteCourrier){
                    swal("Attention!", "Vous devez renseigner la priorité du courrier !!", "warning");
                }else{
                    if(!objetCourrier.value){
                        swal("Attention!", "Vous devez renseigner l'objet du courrier !!", "warning");
                    }else {
                        if (!referenceCourrier.value) {
                            swal("Attention!", "Vous devez renseigner la référence du courrier !!", "warning");
                        } else {
                            if (!PF('selectOneMenuTypeDeCourrier').value) {
                                swal("Attention!", "Vous devez renseigner le type de courrier !!", "warning");
                            } else {
                                if(!istypeDEmetteurOk){
                                    swal("Attention!", "Vous devez renseigner toutes les infomations obligatoires de l'émetteur !!", "warning");
                                } else{
                                   if(!istypeDestinataireOk){
                                       swal("Attention!", "Vous devez renseigner toutes les infomations obligatoires du destinataire !!", "warning");
                                   }else{
                                       PF('alerteBoxValiderFormulaire').show();
                                   }
                                }
                            }
                        }
                    }
                }
            }
        }

}
