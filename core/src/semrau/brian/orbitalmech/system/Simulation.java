package semrau.brian.orbitalmech.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * @author Brian Semrau
 * @version 5/20/2016
 */
public class Simulation {

    public static final float G = 1f;
//    private static final float K_e = 10;

    private ArrayList<Particle> particles;
    private ArrayList<Particle> toAdd;
    private ArrayList<Particle> toRemove;

    private ShapeRenderer shapeRenderer;

    public Simulation() {
        particles = new ArrayList<>();
        toAdd = new ArrayList<>();
        toRemove = new ArrayList<>();

        shapeRenderer = new ShapeRenderer();
    }

    public void step(float delta) {

        // Iterate interactions between every particle
        for (int i = 0; i < particles.size() - 1; i++) {
            Particle p1 = particles.get(i);
            for (int j = i + 1; j < particles.size(); j++) {
                Particle p2 = particles.get(j);

                float dst2 = p1.pos().dst2(p2.pos());

                Vector2 vdir = p2.pos().cpy().sub(p1.pos());

                // Apply strong nuclear force (fusion)
                // - Combines mass and momentum of two particles
//                if (dst2 < 4) {
//                    p1.setMass(p1.mass() + p2.mass());
//                    p1.setVelocity(p1.velocity().cpy().scl(p1.mass()).add(p2.velocity().cpy().scl(p2.mass())).scl(1.0f / (p1.mass() + p2.mass())));
//                    removeParticle(p2);
//                    continue;
//                }

                // Collision
//                if (dst2 <= (p1.radius() + p2.radius()) * (p1.radius() + p2.radius())) {
//                    Vector2 x1 = p1.pos();
//                    Vector2 x2 = p2.pos();
//                    Vector2 v1 = p1.velocity();
//                    Vector2 v2 = p2.velocity();
//                    float m1 = p1.mass();
//                    float m2 = p2.mass();
//
//                    Vector2 x1x2 = x1.cpy().sub(x2);
//                    Vector2 x2x1 = x2.cpy().sub(x1);
//                    Vector2 v1v2 = v1.cpy().sub(v2);
//                    Vector2 v2v1 = v2.cpy().sub(v1);
//
//                    p1.velocity().sub(x1x2.cpy().scl(
//                            (v1v2).dot(x1x2) * (2 * m2 / (m1 + m2)) / (x1x2.len2())
//                    ));
//                    p2.velocity().sub(x2x1.scl(
//                            (v2v1.dot(x2x1)) / x2x1.len2() * (2 * m1 / (m1 + m2))
//                    ));
//                }

                // Apply gravity
                // GMm/r^2
                float gravMag = G * p1.mass() * p2.mass() / dst2;
                Vector2 grav = vdir.cpy().setLength(gravMag);
                p1.applyForce(grav);
                p2.applyForce(new Vector2(-grav.x, -grav.y));

                // Apply electrostatic repulsion (electron shells)
                // kQq/r^2
//                float elecMag = -K_e / dst2;
//                Vector2 elec = vdir.cpy().setLength(elecMag);
//                p1.applyForce(elec);
//                p2.applyForce(new Vector2(-elec.x, -elec.y));

            }
        }

        // Add and remove particles
        particles.addAll(toAdd);
        toAdd.clear();
        particles.removeAll(toRemove);
        toRemove.clear();

        // Timestep
        for (Particle p : particles) {
            p.timestep(delta);
        }
    }

    public void drawParticles(OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Particle p : particles) {
            shapeRenderer.setColor(p.color());
            shapeRenderer.circle(p.pos().x, p.pos().y, p.radius());
            for (int i = 0; i < p.trail().size() - 1 - 3; i++) {
                shapeRenderer.rectLine(p.trail().get(i), p.trail().get(i + 1), 5 * camera.zoom);
            }
//            for (Vector2 v : p.trail()) {
//                shapeRenderer.box(v.x, v.y, 0, 3, 3, 0);
//            }
        }
        shapeRenderer.end();
    }

    public ArrayList<Particle> particles() {
        return particles;
    }

    public void addParticle(Particle p) {
        toAdd.add(p);
    }

    public void removeParticle(Particle p) {
        toRemove.add(p);
    }

}
