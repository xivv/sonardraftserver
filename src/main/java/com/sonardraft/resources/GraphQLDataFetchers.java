package com.sonardraft.resources;

import com.sonardraft.Tools;
import com.sonardraft.Variables;
import com.sonardraft.db.Draft;
import graphql.schema.DataFetcher;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

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
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Xayah"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Braum"));
		draft.getBlue().getPicks().add(Tools.findByName(Variables.characters, "Cassiopeia"));

		draft.getBlue().getBanns ().add(Tools.findByName(Variables.characters, "Corki"));
		draft.getBlue().getBanns ().add(Tools.findByName(Variables.characters, "Riven"));
		draft.getBlue().getBanns ().add(Tools.findByName(Variables.characters, "Kled"));
		draft.getBlue().getBanns ().add(Tools.findByName(Variables.characters, "Nautilus"));
		draft.getBlue().getBanns ().add(Tools.findByName(Variables.characters, "Lulu"));


		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Darius"));
		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Graves"));
		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Kaisa"));
		draft.getRed().getPicks().add(Tools.findByName(Variables.characters, "Alistar"));

		draft.getRed().getBanns ().add(Tools.findByName(Variables.characters, "Rakan"));
		draft.getRed().getBanns ().add(Tools.findByName(Variables.characters, "Ezreal"));
		draft.getRed().getBanns ().add(Tools.findByName(Variables.characters, "Kassadin"));
		draft.getRed().getBanns ().add(Tools.findByName(Variables.characters, "Kennen"));
		draft.getRed().getBanns ().add(Tools.findByName(Variables.characters, "Ahri"));

		return Tools.getPriorityDraft(draft);
	}

}
