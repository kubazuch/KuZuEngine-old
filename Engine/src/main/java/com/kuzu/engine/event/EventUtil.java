package com.kuzu.engine.event;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;

public class EventUtil {
	/**
	 * Bypass {@link EventBus#register(Object)} throwing {@link EventBusException} when no
	 * methods with {@link org.greenrobot.eventbus.Subscribe} annotation found
	 *
	 * @param bus bus to register to
	 * @param obj object to register
	 */
	public static void register(EventBus bus, Object obj) {
		try {
			bus.register(obj);
		} catch (EventBusException ignored) {
		}
	}
}
