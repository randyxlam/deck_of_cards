package deckOfCards.quickstart;


import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;

import org.junit.Test;

import io.restassured.response.Response;

public class Deck extends Base_RT {

	/**
	 * Basic happy path response test. This tests that the service returns a valid response.
	 *
	 * Endpoint: https://deckofcardsapi.com/api/deck/new/
	 */
	@Test
	public void deckOfCards() {
		Response response = when().get(
				getdeckOfCardsBaseUrl() + "/new/" );

		assertThat(response.getStatusCode())
		.as("When calling endpoint, "
				+ "do we get the expected http status code?")
		.isEqualTo(200);
	}

	/**
	 * Basic non-happy path response test (Not Found). This tests that an error is returned
	 * with an invalid request.
	 *
	 * Endpoint: https://deckofcardsapi.com/api/deck/new/2
	 */
	@Test
	public void deckOfCardsInvalid() {
		Response response = when().get(
				getdeckOfCardsBaseUrl() + "/new/" + "2");

		assertThat(response.getStatusCode())
		.as("When calling endpoint, "
				+ "do we get the expected http status code?")
		.isEqualTo(404);
	}

	/**
	 * Shuffling a new deck of cards
	 *
	 * Endpoint: https://deckofcardsapi.com/api/deck/new/shuffle/?deck_count=1

	 */
	@Test
	public void deckOfCardsShuffledTest() {
		given().
		when().
		get(getdeckOfCardsBaseUrl() + "/new/shuffle/?deck_count=1" )
		.then()
		.body(containsString("\"shuffled\": true"));
	}

	@Test
	public void reshuffleExistingDeck() {
		String deckId =
				given().
				when().
				get(getdeckOfCardsBaseUrl() + "/new/shuffle/?cards=AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH").
				then().
				extract().
				path("deck_id");

		given().pathParam("deck_id", deckId).
		when().
		get(getdeckOfCardsBaseUrl() + "/{deck_id}/shuffle/")
		.then()
		.body(containsString("\"shuffled\": true"));
	}

	/**
	 * Drawing cards off a new shuffled deck
	 *
	 * Endpoint: https://deckofcardsapi.com/api/deck/new/draw/?count=10
	 */
	@Test
	public void deckOfCardsDraw() {

		given().
		when().
		get(getdeckOfCardsBaseUrl() + "/new/draw/?count=10" )
		.then()
		.body(containsString("\"remaining\": 42"));
	}

	/**
	 * Checking that the deck contains a specific card by code given a specific partial deck
	 *
	 * Endpoint: https://deckofcardsapi.com/api/deck/new/shuffle/?cards=AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH
	 */
	@Test
	public void containsSpecifcCardCode() {
		String deckId =
				given().
				when().
				get(getdeckOfCardsBaseUrl() + "/new/shuffle/?cards=AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH").
				then().
				extract().
				path("deck_id");

		given().pathParam("deck_id", deckId).
		when().
		get(getdeckOfCardsBaseUrl() + "/{deck_id}/draw/?count=12").
		then().
		body(containsString("\"code\": \"KD\""));
	}

	@Test
	public void containsSpecificCardSuit() {
		String deckId =
				given().
				when().
				get(getdeckOfCardsBaseUrl() + "/new/shuffle/?cards=AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH").
				then().
				extract().
				path("deck_id");

		given().pathParam("deck_id", deckId).
		when().
		get(getdeckOfCardsBaseUrl() + "/{deck_id}/draw/?count=12").
		then().
		body(containsString("\"suit\": \"DIAMONDS\""));
	}
	/**
	 * Basic test to add a card to a pile
	 *
	 * Endpoint: https://deckofcardsapi.com/api/deck/<<deck_id>>/pile/<<pile_name>>/add/?cards=AS,2S

	 */
	@Test
	public void addtoPile() {
		String deckId =
				given().
				when().
				get(getdeckOfCardsBaseUrl() + "/new/shuffle/?cards=AS,2S,KS,AD,2D,KD,AC,2C,KC,AH,2H,KH").
				then().
				extract().
				path("deck_id");

		given().pathParam("deck_id", deckId).
		pathParam("pile_name", "discard").
		when().
		get(getdeckOfCardsBaseUrl() + "/{deck_id}/pile/{pile_name}/add/?cards=AS,2S").
		then().
		body(containsString("\"discard\""));
	}
}
