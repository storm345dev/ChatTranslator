package org.stormdev.chattranslator.api;

import java.util.UUID;

public interface DataStorage<T> {
	public T getValue(UUID uuid);
	public void setValue(UUID uuid, T value);
	public boolean hasValueSet(UUID uuid);
}
