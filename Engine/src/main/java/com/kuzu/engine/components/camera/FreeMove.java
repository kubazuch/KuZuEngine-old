package com.kuzu.engine.components.camera;

import com.kuzu.engine.components.GameComponent;
import com.kuzu.engine.core.Input;
import com.kuzu.engine.core.KeyCode;
import com.kuzu.engine.math.MathUtils;
import org.joml.Quaternionf;

public class FreeMove extends GameComponent {
	private float speed;

	public FreeMove(float speed) {
		this.speed = speed;
	}

	@Override
	public void input(float delta, Input input) {
		float movAmt = speed * delta;

		if (input.isKeyPressed(KeyCode.W)) {
			getTransform().getPos().fma(movAmt, MathUtils.getForward(getTransform().getRot().conjugate(new Quaternionf())));
		}
		if (input.isKeyPressed(KeyCode.S)) {
			getTransform().getPos().fma(-movAmt, MathUtils.getForward(getTransform().getRot().conjugate(new Quaternionf())));
		}
		if (input.isKeyPressed(KeyCode.A)) {
			getTransform().getPos().fma(movAmt, MathUtils.getRight(getTransform().getRot().conjugate(new Quaternionf())));
		}
		if (input.isKeyPressed(KeyCode.D)) {
			getTransform().getPos().fma(-movAmt, MathUtils.getRight(getTransform().getRot().conjugate(new Quaternionf())));
		}
		if (input.isKeyPressed(KeyCode.SPACE)) {
			getTransform().getPos().fma(movAmt, MathUtils.UP);
		}
		if (input.isKeyPressed(KeyCode.LEFT_SHIFT)) {
			getTransform().getPos().fma(-movAmt, MathUtils.UP);
		}
	}
}
