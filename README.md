# Hogeschool Leiden Collegekaart App Android

https://img.shields.io/appveyor/tests/larsniet/HSL-Collegekaart?compact_message

## Technieken
<img alt="Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?&style=for-the-badge&logo=java&logoColor=white"/><img alt="Kotlin" src="https://img.shields.io/badge/kotlin-%230095D5.svg?&style=for-the-badge&logo=kotlin&logoColor=white"/><img alt="PHP" src="https://img.shields.io/badge/php-%23777BB4.svg?&style=for-the-badge&logo=php&logoColor=white"/><img alt="Laravel" src="https://img.shields.io/badge/laravel-%23FF2D20.svg?&style=for-the-badge&logo=laravel&logoColor=white"/><img alt="MySQL" src="https://img.shields.io/badge/mysql-%2300f.svg?&style=for-the-badge&logo=mysql&logoColor=white"/>

## Beschrijving
Elke student heeft er wel eens last van (gehad), je hebt op juist dat ene moment dat je het niet verwacht je collegekaart nodig. Tja, je zou naar huis kunnen fietsen maar dan is het waarschijnlijk al te laat. Deze app brengt daar verandering in. Je logt in met je eigen Hogeschool Leiden account en je gegevens worden automatisch opgehaald. Deze gegevens gebruikt de app om vervolgens jouw collegekaart na te maken in de app. Nadat je bent ingelogd kan je met 1 klik op de app meteen je collegekaart tevoorschijn halen en je barcode laten scannen.

## Uitleg
Deze repository bestaat uit 2 onderdelen. De backend (gebouwd in Laravel) en de frontend (gebouwd in Android). Voor het ontwikkelen van de Laravel applicatie heb ik gebruik gemaakt van een Digital Ocean server waar Ubuntu op staat. Voor het ontwikkelen van de Android applicatie heb ik gebruikt gemaakt van Android Studio en zowel fysieke apparaten als emulators om te testen. 

## Installatie

Gebruik [git](https://github.com/git/git) om deze applicaties te downloaden.

```bash
git clone https://github.com/larsniet/HSL-Collegekaart.git
```

## Opzetten van Laravel omgeving

Bij het opzetten van de Laravel omgeving ga ik ervan uit dat de benodigde software geïnstalleerd is op de host. Denk hierbij bijvoorbeeld aan Composer, PHP en MySQL.

Begin met het aanpassen van de database gegevens in het .env bestand naar jouw eigen gegevens (in mijn geval MySQL). Vervolgens kan je de volgende commands uitvoeren:

```bash
cd ./HSL-Collegekaart/laravel
composer install # Installeerd alle dependencies
php artisan key:generate # Maakt unieke key aan in het .env bestand
php aritsan migrate:fresh --seed # Haalt de database die is ingevoerd in het .env bestand leeg en vult deze met de benodigde data
php artisan serve # Start de laravel applicatie lokaal
```

## Opzetten van de Android applicatie

Open de **android** map in Android Studio. Wacht tot het downloaden van de dependencies en het builden van de gradle files compleet is. Vervolgens kies je een emulator/fysiek apparaat en kan je de app runnen. 

## Bijdragen
Pull requests zijn welkom. Voor grote verandering, open eerst een issue om de gewenste veranderingen te bespreken.

## License
[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)
