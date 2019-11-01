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
			// Draft draft = Tools.getDraft();
			return getDemoData();
		};
	}

	public Draft getDemoData() {

		Draft draft = new Draft();

		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Draven"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Evelynn"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Ezreal"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Kled"));

		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Gragas"));
		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Graves"));

		return Tools.getPriorityDraft(draft);
	}

}
