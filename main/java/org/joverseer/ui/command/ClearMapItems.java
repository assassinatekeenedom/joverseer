package org.joverseer.ui.command;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

/**
 * Clear the map items
 * 
 * @author Marios Skounakis
 */
public class ClearMapItems extends ActionCommand {

	public ClearMapItems() {
		super("clearMapItemsCommand");
	}

	@Override
	protected void doExecuteCommand() {
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (g == null || !Game.isInitialized(g))
			return;
		Turn t = g.getTurn();
		if (t == null)
			return;
		Container<AbstractMapItem> mapItems = t.getMapItems();
		mapItems.removeAll(mapItems.getItems());
		Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));

	}
}
