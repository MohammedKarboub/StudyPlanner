

# Smart Study Planner with Pomodoro Focus Mode

Smart Study Planner is een moderne JavaFX-desktopapplicatie die studenten helpt hun studie te plannen, voortgang bij te houden en gefocust te blijven met een aanpasbare Pomodoro-timer.

---

## Inhoudsopgave

- Features
- Technologieën
- Installatie
- Gebruik
- Toekomstige Uitbreidingen

---

## Features

**Studieplanner**
- Taken toevoegen met titel, beschrijving, deadline en prioriteit
- Schatting van benodigde studietijd per taak
- Overzicht van geplande en voltooide taken
- Slimme sortering op urgentie (deadline + prioriteit)
- Filteren op status en tijdsperiode

**Pomodoro Timer**
- Volledig aanpasbare werk- en pauzeperiodes
- Start, pauzeer, reset functionaliteit
- Timer gekoppeld aan specifieke taken
- Automatische sessie-tracking

**Notificaties**
- Visuele popups
- Optionele systeemnotificaties
- Geluidsnotificaties bij focus/pauze
- Instelbaar via instellingen

**Gegevensopslag**
- Opslag in JSON-formaat
- Data opgeslagen in `~/.studyplanner/`
- Persistente instellingen tussen sessies
- Volledige sessiegeschiedenis

**Statistieken**
- Overzicht van voltooide taken en Pomodoro-sessies
- Grafieken: sessies per dag (laatste 7 dagen)
- Taakstatus verdeling (actief/voltooid/over deadline)
- Tijdsbesteding per categorie

---

## Technologieën

- Java 17+
- JavaFX 21
- Gson 2.10.1
- Maven 3.8+
- Architectuur: MVC
- Timer: JavaFX Timeline API
- Styling: JavaFX CSS

---

## Installatie

**Vereisten**
- Java Development Kit (JDK) 17 of hoger
- Maven 3.8 of hoger
- JavaFX SDK 21 (automatisch via Maven)

**Stappen**

```bash
# Clone de repository
git clone https://github.com/jouwnaam/smart-study-planner.git
cd smart-study-planner

# Build het project
mvn clean install

# Run de applicatie
mvn javafx:run

# Alternatief: Executable JAR
mvn clean package
java -jar target/smart-study-planner-1.0.0.jar
````

---

## Gebruik

1. **Taak toevoegen**

   * Klik op "Nieuwe Taak"
   * Vul titel, beschrijving, deadline en prioriteit in
   * Schat de benodigde studietijd
   * Klik op "Opslaan"

2. **Pomodoro timer starten**

   * Selecteer een taak of "Algemeen"
   * Klik op "Start Focustijd"
   * Werk gefocust tot de timer afgaat
   * Neem pauzes zoals aangegeven

3. **Instellingen aanpassen**

   * Pas werk- en pauzeduur aan
   * Schakel notificaties en geluid aan/uit
   * Klik op "Opslaan"

4. **Statistieken bekijken**

   * Bekijk voortgang en analyse van studiepatronen

---


---

## Toekomstige Uitbreidingen

**Prioriteit 1**

* Cloud sync tussen apparaten
* Herinneringen voor deadlines
* Donkere modus

**Prioriteit 2**

* Google Calendar integratie
* Slimme planningssuggesties
* Groepssessies
* Gamification

**Prioriteit 3**

* Mobile app (iOS/Android)
* Browser extensie / website blocker
* Spotify integratie voor focus playlists

---


