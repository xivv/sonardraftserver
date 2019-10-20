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
			Draft draft = Tools.getDraft();
			return Tools.getPriorityDraft(draft);
		};
	}

	private Draft getDemoData() {

		Draft draft = new Draft();
		Character annie = Tools.findByName(Variables.characters, "Annie");
		Character yasou = Tools.findByName(Variables.characters, "Yasuo");
		Character sona = Tools.findByName(Variables.characters, "Sona");

		Character zed = Tools.findByName(Variables.characters, "Zed");
		Character drmundo = Tools.findByName(Variables.characters, "DrMundo");
		Character kalista = Tools.findByName(Variables.characters, "Kalista");

		Character brand = Tools.findByName(Variables.characters, "Brand");
		Character gragas = Tools.findByName(Variables.characters, "Gragas");
		Character none = Tools.findByName(Variables.characters, "None");
		Character picking = Tools.findByName(Variables.characters, "Picking");
		Character nocturne = Tools.findByName(Variables.characters, "Nocturne");

		Character jarvan = Tools.findByName(Variables.characters, "JarvanIV");
		Character masteryi = Tools.findByName(Variables.characters, "MasterYi");
		Character diana = Tools.findByName(Variables.characters, "Diana");

		Character irelia = Tools.findByName(Variables.characters, "Irelia");
		Character gnar = Tools.findByName(Variables.characters, "Gnar");
		Character kassadin = Tools.findByName(Variables.characters, "Kassadin");
		Character kled = Tools.findByName(Variables.characters, "Kled");

		Character corki = Tools.findByName(Variables.characters, "Corki");
		Character aatrox = Tools.findByName(Variables.characters, "Aatrox");

		Character camille = Tools.findByName(Variables.characters, "Camille");

		draft.getBlue().getPicks().add(annie);
		draft.getBlue().getPicks().add(yasou);
		draft.getBlue().getPicks().add(sona);
		draft.getBlue().getPicks().add(aatrox);
		draft.getBlue().getPicks().add(camille);

		draft.getBlue().getBanns().add(zed);
		draft.getBlue().getBanns().add(drmundo);
		draft.getBlue().getBanns().add(kalista);
		draft.getBlue().getBanns().add(irelia);
		draft.getBlue().getBanns().add(gnar);

		draft.getRed().getPicks().add(brand);
		draft.getRed().getPicks().add(gragas);
		draft.getRed().getPicks().add(nocturne);
		draft.getRed().getPicks().add(corki);
		draft.getRed().getPicks().add(none);

		draft.getRed().getBanns().add(jarvan);
		draft.getRed().getBanns().add(masteryi);
		draft.getRed().getBanns().add(diana);
		draft.getRed().getBanns().add(kassadin);
		draft.getRed().getBanns().add(kled);

		return draft;
	}

}
