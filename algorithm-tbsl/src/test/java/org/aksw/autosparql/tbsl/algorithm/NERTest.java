package org.aksw.autosparql.tbsl.algorithm;

import java.util.List;

import org.aksw.autosparql.commons.nlp.ner.DBpediaSpotlightNER;
import org.aksw.autosparql.commons.nlp.ner.LingPipeNER;
import org.aksw.autosparql.commons.nlp.ner.NER;

public class NERTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		List<String> namedEntities;

		NER dbpner = new DBpediaSpotlightNER();
		NER lpner = new LingPipeNER();

		String sentence1 = "Which software company produced World of Warcraft?";

		long startTime = System.currentTimeMillis();
		namedEntities = dbpner.getNamedEntitites(sentence1);
		System.out.format("Using DBpedia Spotlight WebService (%d ms):\n", System.currentTimeMillis()-startTime);
		System.out.println(namedEntities + "\n");

		startTime = System.currentTimeMillis();
		namedEntities = lpner.getNamedEntitites(sentence1);
		System.out.format("Using Lingpipe API with local DBpedia dictionary (%d ms):\n", System.currentTimeMillis()-startTime);
		System.out.println(namedEntities);

		String sentence2 = "Give me all actors of the television series Charmed and did Nirvana record Nevermind?";

		startTime = System.currentTimeMillis();
		namedEntities = dbpner.getNamedEntitites(sentence2);
		System.out.format("Using DBpedia Spotlight WebService (%d ms):\n", System.currentTimeMillis()-startTime);
		System.out.println(namedEntities + "\n");

		startTime = System.currentTimeMillis();
		namedEntities = lpner.getNamedEntitites(sentence2);
		System.out.format("Using Lingpipe API with local DBpedia dictionary (%d ms):\n", System.currentTimeMillis()-startTime);
		System.out.println(namedEntities);
	}

}
