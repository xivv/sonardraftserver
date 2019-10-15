package com.sonardraft.resources;

import org.springframework.stereotype.Component;

import com.sonardraft.Tools;
import com.sonardraft.Variables;
import com.sonardraft.db.Character;
import com.sonardraft.db.Draft;

import graphql.schema.DataFetcher;

@Component
public class GraphQLDataFetchers {

	public DataFetcher toggleClientRunning() {
		return dataFetchingEnvironment -> {
			Tools.clientRunning = !Tools.clientRunning;
			return Tools.clientRunning;
		};
	}

	public DataFetcher isClientRunning() {
		return dataFetchingEnvironment -> {
			return Tools.clientRunning;
		};
	}

	public DataFetcher isAlive() {
		return dataFetchingEnvironment -> {
			return Tools.programmRunning;
		};
	}

	public DataFetcher getDraft() {
		return dataFetchingEnvironment -> {

			Draft draft = new Draft();

			Character annie = Tools.findByName(Variables.characters, "Annie");
			Character yasou = Tools.findByName(Variables.characters, "Yasuo");
			Character sona = Tools.findByName(Variables.characters, "Sona");

			Character brand = Tools.findByName(Variables.characters, "Brand");

			draft.getBlue().getPicks().add(annie);
			draft.getBlue().getPicks().add(yasou);
			draft.getBlue().getPicks().add(sona);

			draft.getRed().getPicks().add(brand);

			return Tools.getPriorityDraft(draft);
		};
	}

}
