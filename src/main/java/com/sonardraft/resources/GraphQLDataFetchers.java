package com.sonardraft.resources;

import org.springframework.stereotype.Component;

import com.sonardraft.db.Character;
import com.sonardraft.db.Draft;

import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetchers {

	public DataFetcher getDraft() {
		return dataFetchingEnvironment -> {

			Draft draft = new Draft();

			Character annie = new Character("Annie");
			Character bard = new Character("Bard");
			Character jinx = new Character("Jinx");
			Character aatrox = new Character("Aatrox");
			Character nocturne = new Character("Nocturne");

			Character malphite = new Character("Malphite");
			Character yasou = new Character("Yasou");
			Character drmundo = new Character("DrMundo");
			Character kaisa = new Character("Kaisa");
			Character braum = new Character("Braum");

			draft.getBlue().getPicks().add(annie);
			draft.getBlue().getPicks().add(bard);
			draft.getBlue().getPicks().add(jinx);
			draft.getBlue().getPicks().add(aatrox);
			draft.getBlue().getPicks().add(nocturne);

			draft.getRed().getPicks().add(yasou);
			draft.getRed().getPicks().add(malphite);
			draft.getRed().getPicks().add(drmundo);
			draft.getRed().getPicks().add(kaisa);
			draft.getRed().getPicks().add(braum);

			return draft;
		};
	}

}
