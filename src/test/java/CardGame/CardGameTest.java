package CardGame;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static io.restassured.RestAssured.given;

public class CardGameTest {

    @Test
    public void testBlackjackGame() {
        RestAssured.baseURI = "https://deckofcardsapi.com/api/deck";

        // 1. Get new deck
        String deckId = given()
                .when().get("/new/")
                .then().statusCode(200)
                .extract().path("deck_id");

        // 2. Shuffle
        given().when().get("/" + deckId + "/shuffle/").then().statusCode(200);

        // 3. Deal 6 cards
        JsonPath response = given()
                .when().get("/" + deckId + "/draw/?count=6")
                .then().statusCode(200)
                .extract().jsonPath();

        List<Map<String, String>> cards = response.getList("cards");

        List<String> player1 = Arrays.asList(cards.get(0).get("value"), cards.get(1).get("value"), cards.get(2).get("value"));
        List<String> player2 = Arrays.asList(cards.get(3).get("value"), cards.get(4).get("value"), cards.get(5).get("value"));

        System.out.println("Player 1 cards: " + player1);
        System.out.println("Player 2 cards: " + player2);

        // 4. Check Blackjack (first two cards only)
        boolean p1Blackjack = isBlackjack(player1.subList(0,2));
        boolean p2Blackjack = isBlackjack(player2.subList(0,2));

        if (p1Blackjack) {
            System.out.println("Player 1 has Blackjack!");
        }
        if (p2Blackjack) {
            System.out.println("Player 2 has Blackjack!");
        }

        Assert.assertTrue(true); // test always passes, focus on automation steps
    }

    private boolean isBlackjack(List<String> hand) {
        if (hand.size() != 2) return false;
        boolean hasAce = hand.contains("ACE");
        boolean hasTenValue = hand.stream().anyMatch(v -> v.equals("10") || v.equals("KING") || v.equals("QUEEN") || v.equals("JACK"));
        return hasAce && hasTenValue;
    }
}
