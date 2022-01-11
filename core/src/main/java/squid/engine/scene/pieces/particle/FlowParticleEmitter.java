package squid.engine.scene.pieces.particle;

import org.joml.Vector3f;
import squid.engine.scene.pieces.GamePiece;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlowParticleEmitter implements IParticleEmitter {
    private int maxParticles;
    private boolean active;
    private final List<GamePiece> particles;
    private final Particle baseParticle;
    private long creationPeriodMillis, lastCreationTime, animRange;
    private float speedRndRange, positionRndRange, scaleRndRange;

    public FlowParticleEmitter(Particle baseParticle, int maxParticles, long creationPeriodMillis) {
        particles = new ArrayList<>();
        this.baseParticle = baseParticle;
        this.maxParticles = maxParticles;
        this.creationPeriodMillis = creationPeriodMillis;
        this.lastCreationTime = 0;
        this.active = false;
    }

    public long getAnimRange() {
        return animRange;
    }

    public void setAnimRange(long animRange) {
        this.animRange = animRange;
    }

    @Override
    public Particle getBaseParticle() {
        return baseParticle;
    }

    @Override
    public List<GamePiece> getParticles() {
        return particles;
    }

    public float getPositionRndRange() {
        return positionRndRange;
    }

    public float getScaleRndRange() {
        return scaleRndRange;
    }

    public float getSpeedRndRange() {
        return speedRndRange;
    }

    public int getMaxParticles() {
        return maxParticles;
    }

    public long getCreationPeriodMillis() {
        return creationPeriodMillis;
    }

    public long getLastCreationTime() {
        return lastCreationTime;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void setCreationPeriodMillis(long creationPeriodMillis) {
        this.creationPeriodMillis = creationPeriodMillis;
    }

    public void setLastCreationTime(long lastCreationTime) {
        this.lastCreationTime = lastCreationTime;
    }

    public void setMaxParticles(int maxParticles) {
        this.maxParticles = maxParticles;
    }

    public void setPositionRndRange(float positionRndRange) {
        this.positionRndRange = positionRndRange;
    }

    public void setScaleRndRange(float scaleRndRange) {
        this.scaleRndRange = scaleRndRange;
    }

    public void setSpeedRndRange(float speedRndRange) {
        this.speedRndRange = speedRndRange;
    }

    public void update(long elapsedTime) {
        long now = System.currentTimeMillis();
        if (lastCreationTime == 0) {
            lastCreationTime = now;
        }
        Iterator<? extends GamePiece> iterator = particles.iterator();
        while (iterator.hasNext()) {
            Particle particle = (Particle) iterator.next();
            if (particle.updateTtl(elapsedTime) < 0) {
                iterator.remove();
            } else {
                updatePosition(particle, elapsedTime);
            }
        }

        int length = this.getParticles().size();
        if (now - lastCreationTime >= this.creationPeriodMillis && length < maxParticles) {
            createParticle();
            this.lastCreationTime = now;
        }

    }

    private void createParticle() {
        Particle particle = new Particle(this.getBaseParticle());
        float sign = Math.random() > 0.5 ? 1.0f : -1.0f;
        float speedInc = sign * (float) Math.random() * this.speedRndRange;
        float posInc = sign * (float) Math.random() * this.positionRndRange;
        float scaleInc = sign * (float) Math.random() * this.scaleRndRange;
        long animInc = (long) sign * (long)(Math.random() * this.animRange);
        particle.getPosition().add(posInc, posInc, posInc);
        particle.getVelocity().add(speedInc, speedInc, speedInc);
        particle.setScale(particle.getScale() + scaleInc);
        particle.setUpdateTextureMillis(particle.getUpdateTextureMillis() + animInc);
        particles.add(particle);
    }

    public void updatePosition(Particle particle, long elapsedTime) {
        Vector3f speed = particle.getVelocity();
        float delta = elapsedTime / 1000.0f;
        float dx = speed.x * delta;
        float dy = speed.y * delta;
        float dz = speed.z * delta;
        Vector3f pos = particle.getPosition();
        particle.setPosition(pos.x + dx, pos.y + dy, pos.z + dz);
    }

    @Override
    public void cleanup() {
        for (GamePiece particle : particles) {
            particle.cleanup();
        }
    }

}
