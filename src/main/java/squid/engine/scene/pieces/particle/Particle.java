package squid.engine.scene.pieces.particle;

import org.joml.Vector3f;
import squid.engine.graphics.Mesh;
import squid.engine.graphics.textures.Texture;
import squid.engine.scene.pieces.GamePiece;

public class Particle extends GamePiece {
    private Vector3f velocity;
    private long ttl, updateTextureMillis, currentAnimTimeMillis;
    private int animFrames;

    public Particle(Mesh mesh, Vector3f velocity, long ttl, long updateTexturemillis) {
        super(mesh);
        this.velocity = velocity;
        this.ttl = ttl;
        this.updateTextureMillis = updateTexturemillis;
        this.currentAnimTimeMillis = 0;
        Texture texture = this.getMesh().getMaterial().getTexture();
        this.animFrames = texture.getRows() * texture.getCols();
    }

    public Particle(Particle particle) {
        super(particle.getMesh());
        this.ttl = particle.getTtl();
        this.velocity = particle.getVelocity();
        this.setScale(particle.getScale());
        this.setPosition(particle.getPosition());
        this.setRotation(particle.getRotation());
        this.currentAnimTimeMillis = particle.currentAnimTimeMillis;
        this.animFrames = particle.animFrames;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3f velocity) {
        this.velocity = velocity;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long updateTtl(long elapsedTime) {
        this.ttl -= elapsedTime;
        currentAnimTimeMillis += elapsedTime;
        if (currentAnimTimeMillis >= updateTextureMillis && animFrames > 0) {
            currentAnimTimeMillis = 0;
            int pos = getTextPos();
            pos++;
            if (pos < animFrames) {
                setTextPos(pos);
            } else {
                setTextPos(0);
            }
        }
        return this.ttl;
    }

    public long getUpdateTextureMillis() {
        return updateTextureMillis;
    }

    public void setUpdateTextureMillis(long updateTextureMillis) {
        this.updateTextureMillis = updateTextureMillis;
    }

    public int getAnimFrames() {
        return animFrames;
    }

}
