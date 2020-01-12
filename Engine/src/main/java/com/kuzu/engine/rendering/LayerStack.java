package com.kuzu.engine.rendering;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

public class LayerStack implements Iterable<Layer> {
	private ArrayList<Layer> layers = new ArrayList<>();
	private int layerInsertIndex = 0;

	public void pushLayer(Layer layer) {
		layers.add(layerInsertIndex, layer);
		layerInsertIndex++;
	}

	public void pushOverlay(Layer overlay) {
		layers.add(overlay);
	}

	public void popLayer(Layer layer) {
		int it = layers.indexOf(layer);
		if (it != layerInsertIndex && it != -1) {
			layer.onDetach();
			layers.remove(it);
			layerInsertIndex--;
		}
	}

	public void popOverlay(Layer overlay) {
		int it = layers.indexOf(overlay);
		if (it != -1) {
			layers.remove(it);
		}
	}

	public ListIterator<Layer> reverseIterator() {
		return layers.listIterator(layers.size());
	}

	@NotNull
	@Override
	public Iterator<Layer> iterator() {
		return layers.iterator();
	}
}
