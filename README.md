# Hogeschool Leiden Collegekaart App Android

![Laravel Tests](https://github.com/larsniet/HSL-Collegekaart/actions/workflows/laravel.yml/badge.svg)

## :construction_worker: Technieken
<img alt="Java" src="https://img.shields.io/badge/java-%23ED8B00.svg?&style=for-the-badge&logo=java&logoColor=white"/><img alt="Kotlin" src="https://img.shields.io/badge/kotlin-%230095D5.svg?&style=for-the-badge&logo=kotlin&logoColor=white"/><img alt="PHP" src="https://img.shields.io/badge/php-%23777BB4.svg?&style=for-the-badge&logo=php&logoColor=white"/><img alt="Laravel" src="https://img.shields.io/badge/laravel-%23FF2D20.svg?&style=for-the-badge&logo=laravel&logoColor=white"/><img alt="MySQL" src="https://img.shields.io/badge/mysql-%2300f.svg?&style=for-the-badge&logo=mysql&logoColor=white"/>

## :iphone: Beschrijving
Elke student heeft er wel eens last van (gehad), je hebt op juist dat ene moment dat je het niet verwacht je collegekaart nodig. Tja, je zou naar huis kunnen fietsen maar dan is het waarschijnlijk al te laat. Deze app brengt daar verandering in. Je logt in met je eigen Hogeschool Leiden account en je gegevens worden automatisch opgehaald. Deze gegevens gebruikt de app om vervolgens jouw collegekaart na te maken in de app. Nadat je bent ingelogd kan je met 1 klik op de app meteen je collegekaart tevoorschijn halen en je barcode laten scannen.

## :information_source: Uitleg
Deze repository bestaat uit 2 onderdelen. De backend (gebouwd in Laravel) en de frontend (gebouwd in Android). Voor het ontwikkelen van de Laravel applicatie heb ik gebruik gemaakt van een Digital Ocean server waar Ubuntu op staat. Voor het ontwikkelen van de Android applicatie heb ik gebruikt gemaakt van Android Studio en zowel fysieke apparaten als emulators om te testen. 

## :calling: Installatie

Gebruik [git](https://github.com/git/git) om deze repository te clonen.

```bash
git clone https://github.com/larsniet/HSL-Collegekaart.git
```

### Opzetten van Laravel omgeving

Bij het opzetten van de Laravel omgeving ga ik ervan uit dat de benodigde software geïnstalleerd is op de host. Denk hierbij bijvoorbeeld aan Composer, PHP en MySQL.

Begin met het aanpassen van de database gegevens in het .env bestand naar jouw eigen gegevens (in mijn geval MySQL). Vervolgens kan je de volgende commands uitvoeren:

```bash
cd ./HSL-Collegekaart/laravel
composer install # Installeerd alle dependencies
php artisan key:generate # Maakt unieke key aan in het .env bestand
php aritsan migrate:fresh --seed # Haalt de database die is ingevoerd in het .env bestand leeg en vult deze met de benodigde data
php artisan serve --host={host ip-adres} # Start de laravel applicatie lokaal
php artisan jwt:secret # Maakt JWT secret aan
```

### Opzetten van de Android applicatie

- Open de **android** map in Android Studio. 
- Wacht tot het downloaden van de dependencies en het builden van de gradle files compleet is. 
- Navigeer naar **HSL-Collegekaart\android\app\src\main\res\raw**, hier staat een MSAL-config bestand in als voorbeeld. Om te voorkomen dat iedereen zomaar toegang heeft tot de inlogmethode van de Hogeschool Leiden wordt het werkende bestand met gevoelige gegevens niet meegestuurd. Vul alle gegevens in zoals ze in de example MSAL-config (maar dan met je eigen gegevens) staan en vernoem het bestand naar **msal_config.json**. 
- Sla vervolgens het stukje vanaf de laatste '/' van de redirect_uri op, deze heb je nodig voor het bestand **AndroidManifest.xml**. 
- Navigeer naar **HSL-Collegekaart\android\app\src\main\AndroidManifest.xml** en ga op zoek naar de data reference (er is er maar 1). 
- Vul bij **android:path** het laatste stukje van de redirect_uri in.
- In verband met problemen door lokale hosting moet het bestand **HSL-Collegekaart\android\app\src\main\res\xml\network_security_config.xml** gevuld worden met het ip-adres van de host, anders kan de Android applicatie geen verbinding maken met de (lokale) Laravel applicatie.
- Vervolgens kies je een emulator/fysiek apparaat en kan je de app runnen. 

Voor meer uitleg, zie de documentatie van [MSAL](https://docs.microsoft.com/nl-nl/azure/active-directory/develop/msal-configuration). 

## :hearts: Bijdragen
Pull requests zijn welkom. Voor grote verandering, open eerst een issue om de gewenste veranderingen te bespreken.

## :copyright: License
[GNU GPLv3](https://choosealicense.com/licenses/gpl-3.0/)
