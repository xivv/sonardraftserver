package com.sonardraft.resources;

import org.springframework.stereotype.Component;

import com.sonardraft.Tools;
import com.sonardraft.Variables;
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

	public DataFetcher reloadConfiguration() {
		return dataFetchingEnvironment -> {
			return Variables.init();
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
			return getDemoData ();
		};
	}

	public Draft getDemoData() {

		Draft draft = new Draft();

		/**draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Lulu"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Amumu"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Leona"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Cassiopeia"));**/
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Zac"));

		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Darius"));
		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Graves"));

		return Tools.getPriorityDraft(draft);
	}

}
