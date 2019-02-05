package rat.poison.scripts

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import rat.poison.game.*
import rat.poison.game.entity.*
import rat.poison.scripts.aim.bone
import rat.poison.settings.*
import rat.poison.utils.*

private val lastPunch = Vector2()
private val newPunch = Vector2()
private val playerPunch = Vector3()

fun rcs() = every(1) {
	if (me <= 0 || !ENABLE_RCS) return@every
	val shotsFired = me.shotsFired()
	val p = me.punch()

	val forceSet : Boolean
	val finishPunch : Boolean

	if (RCS_RETURNAIM) {
		forceSet = false
		finishPunch = ((p.x in 0.0..0.1) && (p.y in 0.0..0.1))
	} else {
		forceSet = (shotsFired == 0 && !lastPunch.isZero)
		finishPunch = true
	}
	if (forceSet || !finishPunch || shotsFired > 1) { //Fixes aim jumping down
		playerPunch.set(p.x.toFloat(), p.y.toFloat(), p.z.toFloat())
		newPunch.set(playerPunch.x - lastPunch.x, playerPunch.y - lastPunch.y)
		newPunch.scl(1F+RCS_SMOOTHING.toFloat(), 1F+RCS_SMOOTHING.toFloat())


		val angle = clientState.angle()
		angle.apply {
			x -= (newPunch.x)
			y -= (newPunch.y)
			normalize()
		}

		lastPunch.x = playerPunch.x
		lastPunch.y = playerPunch.y

		clientState.setAngle(angle)

		if (forceSet) {
			lastPunch.set(0F, 0F)
		}

		Thread.sleep((.1F/RCS_SMOOTHING).toLong())
	}
	else
	{
		lastPunch.set(0F, 0F)
	}

	bone.set(when {
		shotsFired >= SHIFT_TO_BODY_SHOTS -> BODY_BONE
		shotsFired >= SHIFT_TO_SHOULDER_SHOTS -> SHOULDER_BONE
		else -> AIM_BONE
	})
}