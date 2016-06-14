package semrau.brian.orbitalmech.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * @author Brian Semrau
 * @version 5/20/2016
 */
public class Particle {

    private Vector2 lastPos;
    private Vector2 pos;

    private Vector2 lastVel;
    private Vector2 velocity;

    private Vector2 lastAccel;
    private Vector2 accel;

    private float mass;
    private float invMass;
    private float radius;

    private Color color;
    private ArrayList<Vector2> trail;
    private int stepSinceLastTrail;

    private Particle(Vector2 lastPos, Vector2 pos, Vector2 lastVel, Vector2 velocity, Vector2 lastAccel, Vector2 accel, float mass) {
        this.lastPos = lastPos.cpy();
        this.pos = pos.cpy();
        this.lastVel = lastVel.cpy();
        this.velocity = velocity.cpy();
        this.lastAccel = lastAccel.cpy();
        this.accel = accel.cpy();
        setMass(mass);

        color = new Color();
        Color.rgba8888ToColor(color, ((int) (Math.random() * Integer.MAX_VALUE)) | (0xff));
        trail = new ArrayList<>();
    }

    public Particle(Vector2 pos, float mass) {
        this(new Vector2(), pos, new Vector2(), new Vector2(), new Vector2(), new Vector2(), mass);
    }

    public void timestep(float delta) {
        if (stepSinceLastTrail > 8) {
            trail.add(pos.cpy());
            if (trail.size() > 1200) {
                trail.remove(0);
            }
            stepSinceLastTrail = 0;
        }
        stepSinceLastTrail++;

        // Integrate applied force over velocity and position using trapezoidal sums

        velocity.add(accel.cpy().add(lastAccel).scl(delta * 2));

        pos.add(velocity.cpy().add(lastVel).scl(delta * 2));

        accel.set(0, 0);
    }

    public float mass() {
        return mass;
    }

    public float radius() {
        return radius;
    }

    public Vector2 pos() {
        return pos;
    }

    public Vector2 velocity() {
        return velocity;
    }

    public Color color() {
        return color;
    }

    public ArrayList<Vector2> trail() {
        return trail;
    }

    public void setMass(float mass) {
        this.mass = mass;
        this.invMass = 1.0f / mass;
        this.radius = (float) Math.sqrt(mass / 3.14159f);
    }

    public void setPos(Vector2 pos) {
        this.pos.set(pos);
        lastPos = this.pos.cpy();
    }

    public void setVelocity(Vector2 vel) {
        this.velocity.set(vel);
        lastVel = this.velocity.cpy();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void applyForce(Vector2 force) {
        accel.add(force.cpy().scl(invMass));
    }

    public Particle copy() {
        return new Particle(lastPos, pos, lastVel, velocity, lastAccel, accel, mass);
    }

}
