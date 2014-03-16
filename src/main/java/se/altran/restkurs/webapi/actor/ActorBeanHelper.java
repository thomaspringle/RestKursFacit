package se.altran.restkurs.webapi.actor;

import java.util.ArrayList;
import java.util.List;

import se.altran.restkurs.actor.Actor;

public class ActorBeanHelper {

	public static List<ActorBean> asActorBeans(List<Actor> actors) {
		List<ActorBean> actorBeans = new ArrayList<>(actors.size());
		for (Actor actor: actors) {
			actorBeans.add(actor.asActorBean());
		}
		return actorBeans;
	}
}
