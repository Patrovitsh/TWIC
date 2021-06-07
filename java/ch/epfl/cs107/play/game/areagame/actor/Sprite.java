package ch.epfl.cs107.play.game.areagame.actor;

import ch.epfl.cs107.play.math.Positionable;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;


public class Sprite extends ImageGraphics {

    /**
     * Creates a new Sprite.
     * @param name (String): image name, may be null
     * @param width (int): actual image width, before transformation
     * @param height (int): actual image height, before transformation
     * @param parent (Positionable): parent of this, not null
     * @param roi (RegionOfInterest): region of interest into the image as a rectangle in the image. May be null
     * @param anchor (Vector): image anchor, not null
     * @param alpha (float): transparency, between 0 (invisible) and 1 (opaque)
     * @param depth (float): render priority, lower-values drawn first
     */
    public Sprite(String name, float width, float height, Positionable parent, RegionOfInterest roi, Vector anchor, float alpha, float depth) {
        super(ResourcePath.getSprite(name), width, height, roi, anchor, alpha, depth);
        setParent(parent);
    }

    /**
     * Creates a new image graphics.
     * @param name (String): image name, not null
     * @param width (int): actual image width, before transformation
     * @param height (int): actual image height, before transformation
     * @param parent (Positionable): parent of this, not null
     * @param roi (RegionOfInterest): region of interest into the image as a rectangle in the image. May be null
     * @param anchor (Vector): image anchor, not null
     */
    public Sprite(String name, float width, float height, Positionable parent, RegionOfInterest roi, Vector anchor) {
        super(ResourcePath.getSprite(name), width, height, roi, anchor, 1.0f, 0);
        setParent(parent);
    }

    /**
     * Creates a new image graphics.
     * @param name (String): image name, not null
     * @param width (int): actual image width, before transformation
     * @param height (int): actual image height, before transformation
     * @param parent (Positionable): parent of this, not null
     * @param roi (RegionOfInterest): region of interest into the image as a rectangle in the image. May be null
     */
    public Sprite(String name, float width, float height, Positionable parent, RegionOfInterest roi) {
        super(ResourcePath.getSprite(name), width, height, roi, Vector.ZERO, 1.0f, 0);
        setParent(parent);
    }

    /**
     * Creates a new image graphics.
     * @param name (String): image name, not null
     * @param width (int): actual image width, before transformation
     * @param height (int): actual image height, before transformation
     * @param parent (Positionable): parent of this, not null
     */
    public Sprite(String name, float width, float height, Positionable parent) {
        super(ResourcePath.getSprite(name), width, height, null, Vector.ZERO, 1.0f, 0);
        setParent(parent);
    }
}
