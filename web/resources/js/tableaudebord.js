var courrierTraitesParDirection = document.getElementById('courrierTraitesParDirection');
var courrierTraitesParTypeDEmetteur = document.getElementById('courrierTraitesParTypeDEmetteur');
var courrierEntrantEtSortant = document.getElementById('courrierEntrantEtSortant');

var myChart = new Chart(courrierTraitesParDirection, {
    type: 'bar',
    data: {
        labels: ['DCSI', 'DCAF', 'DCRH', 'AJE', 'SG','IGS'],
        datasets: [{
            label: '',
            data: [12, 19, 13, 5,15,7],
            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)',
                'rgba(153, 102, 255, 0.2)',
                'rgba(255, 159, 64, 0.2)'
            ],
            borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)',
                'rgba(153, 102, 255, 1)',
                'rgba(255, 159, 64, 1)'
            ],
            borderWidth:2,
            hoverBorderWidth : 3,
            hoverBorderColor : '#4CAF50'
        }]
    },
    options: {
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        },
        legend : {
            display:false
        },
        tooltip: {
            enable:true
        }
    }
});

var myChart2 = new Chart(courrierTraitesParTypeDEmetteur, {
    type: 'pie',
    data: {
        labels: ['Particulier', 'Entreprise', 'Ministere', 'Autre Ministère'],
        datasets: [{
            label: '',
            data: ['#{tableauDeBord.nombreCourrierParticulier}', '#{tableauDeBord.nombreCourrierEntreprise}','#{tableauDeBord.nombreCourrierAgentMinistere}', '#{tableauDeBord.nombreCourrierAgentAutreMinistere}'],
            backgroundColor: [
                'rgba(255, 99, 132, 0.2)',
                'rgba(54, 162, 235, 0.2)',
                'rgba(255, 206, 86, 0.2)',
                'rgba(75, 192, 192, 0.2)'
            ],
            borderColor: [
                'rgba(255, 99, 132, 1)',
                'rgba(54, 162, 235, 1)',
                'rgba(255, 206, 86, 1)',
                'rgba(75, 192, 192, 1)'
            ],
            borderWidth:2,
            hoverBorderWidth : 3,
            hoverBorderColor : '#4CAF50'
        }]
    },
    options: {
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        }
    }
});

var myChart3 = new Chart(courrierEntrantEtSortant, {
    type: 'doughnut',
    data: {
        labels: ['Courriers reçus', 'Courriers envoyés'],
        datasets: [{
            label: '',
            data: ['#{tableauDeBord.statistiques.nombreDeCourrierEnvoyesDuJour}', '#{tableauDeBord.statistiques.nombreDeCourrierEnvoyesDuMois}'],
            backgroundColor: [
                'rgba(250, 162, 72, 0.5)',
                'rgba(113, 199, 170, 0.5)'
            ],
            borderColor: [
                'rgba(250, 162, 72, 0.5)',
                'rgba(113, 199, 170, 0.5)'
            ],
            borderWidth:2,
            hoverBorderWidth : 3,
            hoverBorderColor : '#4CAF50'
        }]
    },
    options: {
        scales: {
            yAxes: [{
                ticks: {
                    beginAtZero: true
                }
            }]
        }
    }
});
