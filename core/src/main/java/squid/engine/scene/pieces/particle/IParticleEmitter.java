package squid.engine.scene.pieces.particle;

import squid.engine.scene.pieces.GamePiece;

import java.util.List;

public interface IParticleEmitter {

    void cleanup();
    Particle getBaseParticle();
    List<GamePiece> getParticles();

}
