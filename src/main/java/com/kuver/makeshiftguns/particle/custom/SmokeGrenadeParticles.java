package com.kuver.makeshiftguns.particle.custom;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class SmokeGrenadeParticles extends TextureSheetParticle {

    public float size = (float) (Math.random() * 1 + 1);

    protected SmokeGrenadeParticles(ClientLevel level,
                                    double pX, double pY, double pZ,
                                    SpriteSet spriteset,
                                    double pXSpeed, double pYSpeed, double pZSpeed) {
        super(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.friction = 0.9F;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize = size;
        this.lifetime = 600;
        this.setSpriteFromAge(spriteset);

        this.rCol = 1F;
        this.gCol = 1F;
        this.bCol = 1F;
    }

    @Override
    public void tick() {
        super.tick();
        fadeOut();
    }

    private void fadeOut() {

        //change this to be smoother
        if (this.age >= (this.lifetime - 50)) {
            float fadeage = (this.age - 550);
            float fadelifetime = (this.lifetime - 550);
            this.alpha = (-(1/fadelifetime) * fadeage + 1F);
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public float color = (float) (Math.random() * 0.3 + 0.6);

        public Provider(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        public Particle createParticle(@NotNull SimpleParticleType particleType, @NotNull ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
            SmokeGrenadeParticles smokeParticle = new SmokeGrenadeParticles(level, x, y, z, this.sprites, dx, dy, dz);
            smokeParticle.pickSprite(this.sprites);
            smokeParticle.setColor(
                    color,
                    color,
                    color
            );
            return smokeParticle;
        }
    }
}
