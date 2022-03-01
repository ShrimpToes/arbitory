package squid.engine.graphics.uniforms.settable.physics;

import squid.engine.scene.pieces.GamePiece;

public class PhysicsObject extends GamePiece {

    BoundingBox boundingBox;

    public PhysicsObject(BoundingBox box) {
        super();
        boundingBox = box;
    }
}
