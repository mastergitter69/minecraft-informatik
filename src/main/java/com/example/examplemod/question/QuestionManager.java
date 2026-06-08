package com.example.examplemod.question;

import com.example.examplemod.data.DailyAskData;

import java.util.List;
import java.util.stream.Collectors;

public class QuestionManager {

    // einfache Frage + Antwort Struktur
    public record Question(String question, String answer) {}

    // alle verfügbaren Fragen
    public static final List<Question> QUESTIONS = List.of(
            new Question("Wie viel ist ein normaler Stack?", "64"),
            new Question("Wie viel Herzen hat ein Spieler?", "10"),
            new Question("Wie viele Layer braucht ein Full Beacon?", "4"),
            new Question("Wie viele Blöcke braucht ein Full Beacon?", "164"),
            new Question("Was ist das Buildlimit dieser Version?", "320"),
            new Question("Wie viel Ressourcen braucht eine komplette Rüstung?", "24"),
            new Question("Wie viele Logs brauchst du um eine Holzpickaxe zu craften (mit Table)?", "3"),
            new Question("Wie viele Stöcke braucht man für eine komplette Schwertserie (Holz bis Diamant)?", "10"),
            new Question("Wie viele Zuckerrohr braucht man für eine komplette Bücherregal-Umrandung eines Enchantment Tables?", "105"), new Question("Wie viele Eisenbarren braucht man für Eisenrüstung + Schwert + Schaufel + Spitzhacke + Axt?", "32"), new Question("Wie viele Planken bekommt man aus einem einzigen Baumstamm?", "4"),
            new Question("Wie viele Leder braucht man für alle Bücher in einem vollständigen Bücherregal-Setup?", "45"),
            // Spielmechanik & Werte
            new Question("Wie viel Schaden macht ein kritischer Treffer mit einem Diamantschwert?", "9"),
            new Question("Wie viele Erfahrungspunkte braucht man für Level 30?", "1395"),
            new Question("Wie viele Sekunden dauert es, einen Diamantblock mit bloßer Hand abzubauen?", "125"),
            new Question("Wie viel Hunger füllt ein gekochtes Steak?", "8"),
            new Question("Wie tief muss man fallen, um genau einen Herzschaden zu nehmen?", "4"),
            new Question("Wie groß ist ein Chunk in Blöcken?", "16"),
            new Question("In welcher Y-Ebene spawnt Diamant am häufigsten?", "-58"),
            new Question("Wie weit muss ein Nether-Portal vom nächsten entfernt sein, damit es ein eigenes Portal spawnt?", "128"),
            new Question("Wie viele Blöcke entspricht 1 Block im Nether in der Oberwelt?", "8"),
            new Question("Wie viele Blaze Rods braucht man, um zum Enderdrachen zu gelangen?", "6"),
            new Question("Wie viel HP hat der Wither?", "300"),
            new Question("Wie viele Enderpearls droppt ein Enderman durchschnittlich?", "1"),
            new Question("Wie hoch ist der Nether von Bedrock zu Bedrock?", "128"),
            // Crafting-Outputs
             new Question("Wie viele Fackeln bekommt man aus einem Stack Kohle?", "256"),
            new Question("Wie viele Glasscheiben bekommt man aus einem Crafting mit 6 Glasblöcken?", "16"),
            new Question("Wie viele Knochenmehl bekommt man aus einem Knochen?", "3"),
            new Question("Wie viele Brote kann man aus einem Stack Weizen backen?", "21"),
            new Question("Wie viele Treppenblöcke bekommt man aus 6 Blöcken?", "4"),
            new Question("Wie viele Planken braucht man für eine Truhe?", "8"),
            new Question("Wie viele Blöcke weit reicht eine Water Source fürs Farming?", "4"),
            new Question("Wie viele Bögen kann man mit einem Stack Faden craften?", "21"),
            // Enchanting
            new Question("Wie viele Bücherregale braucht man für Level-30-Enchants?", "15"),
            new Question("Wie viele Lapislazuli braucht man für einen Level-30-Enchant?", "3"),
            new Question("Wie viele Glasflaschen bekommt man aus 3 Glasblöcken?", "3"),
            // Redstone
            new Question("Wie weit überträgt ein Redstone-Signal ohne Verstärker?", "15"),
            new Question("Wie viele Ticks hat eine Redstone-Verzögerung auf Stufe 1?", "2"),
            new Question("Wie viele Redstone-Staub braucht man für einen Komparator?", "3"),
            new Question("Wie viele Pistons kann ein einzelner Redstone-Block aktivieren?", "6"),
            // Mobs & Strukturen
            new Question("Wie viel HP hat ein Creeper?", "20"),
            new Question("Wie groß ist die Explosionsreichweite eines Creepers?", "3"),
            new Question("Wie viele Herzen regeneriert man mit einem goldenen Apfel?", "2"),
            new Question("Wie viel HP hat ein Eisengolem?", "100"),
            new Question("Wie viele Strongholds gibt es pro Welt?", "128"),
            new Question("Wie weit vom Spawn entfernt spawnen Strongholds .?", "1408"),
            new Question("Wie viele End-Portale gibt es pro Stronghold?", "1"),
            new Question("Wie tief ist Bedrock-Level in der Oberwelt?", "-64"),
            new Question("Wie viele Biome gibt es in der aktuellen Java-Version?", "61")
    );

    // wählt eine neue Frage, die noch nicht gestellt wurde
    public static Question getQuestion(DailyAskData data) {

        List<Question> remaining = QUESTIONS.stream()
                .filter(q -> !data.wasAsked(q.question()))
                .collect(Collectors.toList());

        // wenn alles einmal dran war → Reset
        if (remaining.isEmpty()) {
            data.clearAskedQuestions();
            remaining = List.copyOf(QUESTIONS);
        }

        // zufällige Auswahl
        Question q = remaining.get((int)(Math.random() * remaining.size()));

        // merken dass diese Frage gestellt wurde
        data.addAskedQuestion(q.question());

        return q;
    }

    // prüft Antwort vom Spieler
    public static boolean isCorrect(Question q, String a) {
        return q.answer().trim().equalsIgnoreCase(a.trim());
    }
}